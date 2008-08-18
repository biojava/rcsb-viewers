// $Id: UncompressInputStream.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
//
// This file is part of the HTTPClient package
// Copyright (C) 1996-2001 Ronald Tschal?r
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free
// Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
// MA 02111-1307, USA
//
// For questions, suggestions, bug-reports, enhancement-requests etc.
// I may be contacted at:
//
// ronald@innovation.ch
//
// The HTTPClient's home page is located at:
//
// http://www.innovation.ch/java/HTTPClient/
//
// This version has been modified from the original 0.3-3 version by the
// Unidata Program Center (support@unidata.ucar.edu) to make the constructor
// public and to fix a couple of bugs.
//
// The PDB version has been modified to clean up the formatting, to add
// CVS header and history lines, and to change the package statement.
//
// History:
//  $Log: UncompressInputStream.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.1  2004/02/10 21:28:47  moreland
//  Added support for UNIX compress format.
//
//  Revision 1.0  2004/02/10 17:23:33  moreland
//


package org.rcsb.mbt.structLoader.openmms.cifparse;


import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * This class decompresses an input stream containing data compressed with
 * the unix "compress" utility (LZC, a LZW variant). This code is based
 * heavily on the <var>unlzw.c</var> code in <var>gzip-1.2.4</var> (written
 * by Peter Jannesen) and the original compress code.
 *
 * @version 0.3-3 06/05/2001
 * @author Ronald Tschal?r
 * @author Unidata Program Center
 */
public class UncompressInputStream
	extends FilterInputStream
{
	byte[] one = new byte[1];

	// string table stuff
	private static final int TBL_CLEAR = 0x100;
	private static final int TBL_FIRST = UncompressInputStream.TBL_CLEAR + 1;

	private int[] tab_prefix;
	private byte[] tab_suffix;
	private final int[] zeros = new int[256];
	private byte[] stack;

	// various state
	private boolean block_mode;
	private int n_bits;
	private int maxbits;
	private int maxmaxcode;
	private int maxcode;
	private int bitmask;
	private int oldcode;
	private byte finchar;
	private int stackp;
	private int free_ent;

	// input buffer
	private final byte[] data = new byte[10000];
	private int bit_pos = 0, end = 0, got = 0;
	private boolean eof = false;
	private static final int EXTRA = 64;

	private static final int LZW_MAGIC = 0x1f9d;
	private static final int MAX_BITS = 16;
	private static final int INIT_BITS = 9;
	private static final int HDR_MAXBITS = 0x1f;
	private static final int HDR_EXTENDED = 0x20;
	private static final int HDR_FREE = 0x40;
	private static final int HDR_BLOCK_MODE = 0x80;

	private static final boolean debug = false;


	/**
	 *  Construct an instance of this class from an InputStream.
	 *  <P>
	 *  @param is the input stream to decompress
	 *  @exception IOException if the header is malformed
	 */
	public UncompressInputStream( final InputStream is )
		throws IOException
	{
		super( is );
		this.parse_header( );
	}


	/**
	 *  Read a single byte returning the result in the lower byte of an int.
	 */
	
	public synchronized int read( )
		throws IOException
	{
		final int b = this.read( this.one, 0, 1 );
		if ( b == 1 ) {
			return (this.one[0] & 0xff);
		} else {
			return -1;
		}
	}


	/**
	 *  Read the specified number of bytes into the buffer.
	 */
	
	public synchronized int read( final byte[] buf, final int off_, final int len_ )
		throws IOException
	{
		int off = off_, len = len_;
		if ( this.eof ) {
			return -1;
		}
		final int start = off;

		// Using local copies of various variables speeds things up by as
		// much as 30% !

		final int[] l_tab_prefix = this.tab_prefix;
		final byte[] l_tab_suffix = this.tab_suffix;
		final byte[] l_stack = this.stack;
		int l_n_bits = this.n_bits;
		int l_maxcode = this.maxcode;
		final int l_maxmaxcode = this.maxmaxcode;
		int l_bitmask = this.bitmask;
		int l_oldcode = this.oldcode;
		byte l_finchar = this.finchar;
		int l_stackp = this.stackp;
		int l_free_ent = this.free_ent;
		final byte[] l_data = this.data;
		int l_bit_pos = this.bit_pos;


		// empty stack if stuff still left

		int s_size = l_stack.length - l_stackp;
		if ( s_size > 0 )
		{
			final int num = (s_size >= len) ? len : s_size ;
			System.arraycopy( l_stack, l_stackp, buf, off, num );
			off += num;
			len -= num;
			l_stackp += num;
		}

		if ( len == 0 )
		{
			this.stackp = l_stackp;
			return off-start;
		}


		// loop, filling local buffer until enough data has been decompressed

		main_loop: do
		{
			if ( this.end < UncompressInputStream.EXTRA ) {
				this.fill();
			}

			final int bit_in = (this.got > 0) ? (this.end - this.end%l_n_bits)<<3 :
			(this.end<<3)-(l_n_bits-1);

			while ( l_bit_pos < bit_in )
			{
				// handle 1-byte reads correctly
				if ( len == 0 )
				{
					this.n_bits = l_n_bits;
					this.maxcode = l_maxcode;
					this.maxmaxcode = l_maxmaxcode;
					this.bitmask = l_bitmask;
					this.oldcode = l_oldcode;
					this.finchar = l_finchar;
					this.stackp = l_stackp;
					this.free_ent = l_free_ent;
					this.bit_pos = l_bit_pos;

					return off-start;
				}

				// check for code-width expansion

				if ( l_free_ent > l_maxcode )
				{
					final int n_bytes = l_n_bits << 3;
					l_bit_pos = (l_bit_pos-1) +
					n_bytes - (l_bit_pos-1+n_bytes) % n_bytes;

					l_n_bits++;
					l_maxcode = (l_n_bits==this.maxbits) ? l_maxmaxcode :
					(1<<l_n_bits) - 1;

					if ( UncompressInputStream.debug ) {
						System.err.println(
							"Code-width expanded to " + l_n_bits );
					}

					l_bitmask = (1<<l_n_bits)-1;
					l_bit_pos = this.resetbuf( l_bit_pos );
					continue main_loop;
				}


				// read next code

				final int pos = l_bit_pos>>3;
				int code = (((l_data[pos]&0xFF) | ((l_data[pos+1]&0xFF)<<8) |
					((l_data[pos+2]&0xFF)<<16)) >> (l_bit_pos & 0x7)) &
					l_bitmask;
				l_bit_pos += l_n_bits;


				// handle first iteration

				if ( l_oldcode == -1 )
				{
					if ( code >= 256 ) {
						throw new IOException(
							"corrupt input: " + code + " > 255" );
					}
					l_finchar = (byte) (l_oldcode = code);
					buf[off++] = l_finchar;
					len--;
					continue;
				}


				// handle CLEAR code

				if (code == UncompressInputStream.TBL_CLEAR && this.block_mode)
				{
					System.arraycopy( this.zeros, 0, l_tab_prefix, 0, this.zeros.length );
					l_free_ent = UncompressInputStream.TBL_FIRST - 1;

					final int n_bytes = l_n_bits << 3;
					l_bit_pos = (l_bit_pos-1) +
					n_bytes - (l_bit_pos-1+n_bytes) % n_bytes;
					l_n_bits = UncompressInputStream.INIT_BITS;
					l_maxcode = (1 << l_n_bits) - 1;
					l_bitmask = l_maxcode;

					if ( UncompressInputStream.debug ) {
						System.err.println( "Code tables reset" );
					}

					l_bit_pos = this.resetbuf(l_bit_pos);
					continue main_loop;
				}


				// setup

				final int incode = code;
				l_stackp = l_stack.length;


				// Handle KwK case

				if ( code >= l_free_ent )
				{
					if ( code > l_free_ent ) {
						throw new IOException( "corrupt input: code=" + code +
						", free_ent=" + l_free_ent );
					}
				
					l_stack[--l_stackp] = l_finchar;
					code = l_oldcode;
				}


				// Generate output characters in reverse order

				while ( code >= 256 )
				{
					l_stack[--l_stackp] = l_tab_suffix[code];
					code = l_tab_prefix[code];
				}
				l_finchar = l_tab_suffix[code];
				buf[off++] = l_finchar;
				len--;


				// And put them out in forward order

				s_size = l_stack.length - l_stackp;
				final int num = (s_size >= len) ? len : s_size ;
				System.arraycopy( l_stack, l_stackp, buf, off, num );
				off += num;
				len -= num;
				l_stackp += num;


				// generate new entry in table

				if ( l_free_ent < l_maxmaxcode )
				{
					l_tab_prefix[l_free_ent] = l_oldcode;
					l_tab_suffix[l_free_ent] = l_finchar;
					l_free_ent++;
				}


				// Remember previous code

				l_oldcode = incode;


				// if output buffer full, then return

				if ( len == 0 )
				{
					this.n_bits = l_n_bits;
					this.maxcode = l_maxcode;
					this.bitmask = l_bitmask;
					this.oldcode = l_oldcode;
					this.finchar = l_finchar;
					this.stackp = l_stackp;
					this.free_ent = l_free_ent;
					this.bit_pos = l_bit_pos;

					return off-start;
				}
			}

			l_bit_pos = this.resetbuf( l_bit_pos );
		}
		while ( this.got > 0 );

		this.n_bits = l_n_bits;
		this.maxcode = l_maxcode;
		this.bitmask = l_bitmask;
		this.oldcode = l_oldcode;
		this.finchar = l_finchar;
		this.stackp = l_stackp;
		this.free_ent = l_free_ent;
		this.bit_pos = l_bit_pos;

		this.eof = true;
		return off-start;
	}


	/**
	 *  Moves the unread data in the buffer to the beginning and resets
	 *  the pointers.
	 */
	private final int resetbuf( final int bit_pos )
	{
		final int pos = bit_pos >> 3;
		System.arraycopy( this.data, pos, this.data, 0, this.end-pos );
		this.end -= pos;
		return 0;
	}


	/**
	 *  Read the filler bytes.
	 */
	private final void fill( )
		throws IOException
	{
		this.got = this.in.read( this.data, this.end, this.data.length-1-this.end );
		if ( this.got > 0 ) {
			this.end += this.got;
		}
	}


	/**
	 *  Skip the specified number of bytes.
	 */
	
	public synchronized long skip( final long num )
		throws IOException
	{
		final byte[] tmp = new byte[(int) num];
		final int got = this.read( tmp, 0, (int) num );

		if ( got > 0 ) {
			return got;
		} else {
			return 0L;
		}
	}


	/**
	 *  Return the number of available bytes.
	 */
	
	public synchronized int available( )
		throws IOException
	{
		if ( this.eof ) {
			return 0;
		}

		return this.in.available();
	}


	/**
	 *  Parse the input header.
	 */
	private void parse_header( )
		throws IOException
	{
		// read in and check magic number

		int t = this.in.read( );
		if ( t < 0 ) {
			throw new EOFException( "Failed to read magic number" );
		}
		int magic = (t & 0xff) << 8;
		t = this.in.read( );
		if ( t < 0 ) {
			throw new EOFException( "Failed to read magic number" );
		}
		magic += t & 0xff;
		if ( magic != UncompressInputStream.LZW_MAGIC ) {
			throw new IOException( "Input not in compress format (read " +
			"magic number 0x" + Integer.toHexString(magic) + ")" );
		}


		// read in header byte

		final int header = this.in.read( );
		if ( header < 0 ) {
			throw new EOFException( "Failed to read header" );
		}

		this.block_mode = (header & UncompressInputStream.HDR_BLOCK_MODE) > 0;
		this.maxbits = header & UncompressInputStream.HDR_MAXBITS;

		if ( this.maxbits > UncompressInputStream.MAX_BITS ) {
			throw new IOException( "Stream compressed with " + this.maxbits +
			" bits, but can only handle " + UncompressInputStream.MAX_BITS + " bits" );
		}

		if ( (header & UncompressInputStream.HDR_EXTENDED) > 0 ) {
			throw new IOException( "Header extension bit set" );
		}

		if ( (header & UncompressInputStream.HDR_FREE) > 0 ) {
			throw new IOException( "Header bit 6 set" );
		}

		if ( UncompressInputStream.debug )
		{
			System.err.println( "block mode: " + this.block_mode );
			System.err.println( "max bits: " + this.maxbits );
		}


		// initialize stuff

		this.maxmaxcode = 1 << this.maxbits;
		this.n_bits = UncompressInputStream.INIT_BITS;
		this.maxcode = (1 << this.n_bits) - 1;
		this.bitmask = this.maxcode;
		this.oldcode = -1;
		this.finchar = 0;
		this.free_ent = this.block_mode ? UncompressInputStream.TBL_FIRST : 256;

		this.tab_prefix = new int[1 << this.maxbits];
		this.tab_suffix = new byte[1 << this.maxbits];
		this.stack = new byte[1 << this.maxbits];
		this.stackp = this.stack.length;

		for ( int idx=255; idx>=0; idx-- ) {
			this.tab_suffix[idx] = (byte) idx;
		}
	} 


	/**
	 *  Decompress the file specified on the command line and write the
	 *  uncompressed data to standard output.
	 */
	public static void main( final String args[] )
		throws Exception
	{
		if ( args.length != 1 )
		{
			System.err.println( "Usage: UncompressInputStream <file>" );
			System.exit( 1 );
		}

		final InputStream in =
			new UncompressInputStream( new FileInputStream( args[0] ) );

		final byte[] buf = new byte[100000];
		int tot = 0;
		final long beg = System.currentTimeMillis( );

		while ( true )
		{
			final int got = in.read( buf );
			if ( got < 0 ) {
				break;
			}
			System.out.write( buf, 0, got );
			tot += got;
		}

		final long end = System.currentTimeMillis( );
		System.err.println( "Decompressed " + tot + " bytes" );
		System.err.println( "Time: " + (end-beg)/1000. + " seconds" );
	}
}

