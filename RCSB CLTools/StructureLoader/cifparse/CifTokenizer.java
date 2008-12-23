/*
 * BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence. This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors. These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 * http://www.biojava.org/
 *
 * This code was contributed from the Molecular Biology Toolkit
 * (MBT) project at the University of California San Diego.
 *
 * Please reference J.L. Moreland, A.Gramada, O.V. Buzko, Qing
 * Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (MBT):
 * A Modular Platform for Developing Molecular Visualization
 * Applications. BMC Bioinformatics, 6:21.
 *
 * The MBT project was funded as part of the National Institutes
 * of Health PPG grant number 1-P01-GM63208 and its National
 * Institute of General Medical Sciences (NIGMS) division. Ongoing
 * development for the MBT project is managed by the RCSB
 * Protein Data Bank(http://www.pdb.org) and supported by funds
 * from the National Science Foundation (NSF), the National
 * Institute of General Medical Sciences (NIGMS), the Office of
 * Science, Department of Energy (DOE), the National Library of
 * Medicine (NLM), the National Cancer Institute (NCI), the
 * National Center for Research Resources (NCRR), the National
 * Institute of Biomedical Imaging and Bioengineering (NIBIB),
 * the National Institute of Neurological Disorders and Stroke
 * (NINDS), and the National Institute of Diabetes and Digestive
 * and Kidney Diseases (NIDDK).
 *
 * Created on 2007/02/08
 *
 */ 
package org.rcsb.mbt.structLoader.openmms.cifparse;


import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.net.*;


/**
 * Class that reads the cif/STAR formatted file and
 * breaks it up into tokens at "word" boundaries.
 *
 * @author Douglas S. Greer
 * @version 1.3
 * Please complete these missing tags
 * @rref
 * @copyright
 * @concurrency
 * @see
 */
public class CifTokenizer
{
	static final boolean debugTokenizer = false;

	public boolean tokenIsName;

	String filename;
	BufferedReader br;
	StreamTokenizer st;
	StringBuffer sbuf;
	int linenum;

	final static int NULL_STATE = 1;
	final static int TOKEN_STATE = 2;
	final static int REG_QUOTE_STATE = 3;
	final static int SEMI_QUOTE_STATE = 4;

	final static int WHITESPACE_CHAR = 1;
	final static int REG_QUOTE_CHAR = 2;
	final static int NEWLINE_CHAR = 3;
	final static int OTHER_CHAR = 4;


	/**
	 * Please complete the missing tags for CifTokenizer
	 * @param
	 * @return
	 * @throws
	 * @pre
	 * @post
	 */
	public CifTokenizer( final String fn_ )
		throws IOException
	{
		String fn = fn_;
		this.linenum = 1;
		this.sbuf = new StringBuffer();
		this.filename = fn;

		if( fn.endsWith( ".dic" ) )
		{
			// Its a dictionary, so open it as a Java resource.
			final InputStream inputStream = this.getClass().getResourceAsStream( fn );
			if( inputStream == null ) {
				throw new IOException( "CifTokenizer: no dictionary resource " + fn );
			}
			final InputStreamReader inputStreamReader =
			new InputStreamReader( inputStream );
			this.br = new BufferedReader( inputStreamReader );
		}
		else if( fn.startsWith( "http:" ) || fn.startsWith( "ftp:" ) )
		{
			// URL

			final URL url = new URL( fn );
			final InputStream inputStream = url.openStream();

			// Zip compression?
			if( fn.endsWith( ".zip" ) )
			{
				// URL: Zip
				final ZipInputStream zipInputStream =
				new ZipInputStream( inputStream );
				final ZipEntry zipEntry = zipInputStream.getNextEntry();
				if( zipEntry == null ) {
					throw new IOException( "No ZipEntries in Zip stream" );
				}
				final InputStreamReader inputStreamReader =
				new InputStreamReader( zipInputStream );
				this.br = new BufferedReader( inputStreamReader );
			}
			else if( fn.endsWith( ".gz" ) )
			{
				// URL: GZip
				final GZIPInputStream gzipInputStream =
				new GZIPInputStream( inputStream );
				final InputStreamReader inputStreamReader =
				new InputStreamReader( gzipInputStream );
				this.br = new BufferedReader( inputStreamReader );
			}
			else if( fn.endsWith( ".Z" ) )
			{
				// URL: UNIX compress
				final UncompressInputStream uncompressInputStream =
				new UncompressInputStream( inputStream );
				final InputStreamReader inputStreamReader =
				new InputStreamReader( uncompressInputStream );
				this.br = new BufferedReader( inputStreamReader );
			}
			else if( fn.endsWith( ".cif" ) )
			{
				// URL: No compression
				final InputStreamReader inputStreamReader =
				new InputStreamReader( inputStream );
				this.br = new BufferedReader( inputStreamReader );
			}
			else
			{
				throw new IOException( "Unsupported cif file name extension" );
			}
		}
		else
		{
			// File

			// If it exists, strip the "file:" off
			if( fn.startsWith( "file:" ) ) {
				fn = fn.substring( 5 );
			}

			// Zip Compression?
			if( fn.endsWith( ".zip" ) )
			{
				// File: Zip Compression
				final ZipFile zf = new ZipFile( fn );
				final Enumeration ee = zf.entries();
				final ZipEntry ze = ( ZipEntry ) ee.nextElement();
				if( ze == null )
				{
					throw new IOException( "No ZipEntries in Zip File" );
				}
				this.br = new BufferedReader(
				new InputStreamReader( zf.getInputStream( ze ) ) );
			}
			else if( fn.endsWith( ".gz" ) )
			{
				// File: GZip
				final File file = new File( fn );
				final FileInputStream fileInputStream = new FileInputStream( file );
				final GZIPInputStream gzipInputStream =
				new GZIPInputStream( fileInputStream );
				final InputStreamReader inputStreamReader =
				new InputStreamReader( gzipInputStream );
				this.br = new BufferedReader( inputStreamReader );
			}
			else if( fn.endsWith( ".Z" ) )
			{
				// File: UNIX compress
				final File file = new File( fn );
				final FileInputStream fileInputStream = new FileInputStream( file );
				final UncompressInputStream uncompressInputStream =
				new UncompressInputStream( fileInputStream );
				final InputStreamReader inputStreamReader =
				new InputStreamReader( uncompressInputStream );
				this.br = new BufferedReader( inputStreamReader );
			}
			else
			{
				// File: No compression
				final FileReader fileReader = new FileReader( fn );
				this.br = new BufferedReader( fileReader );
			}
		}
		// We now have a generic BufferedReader

		this.st = new StreamTokenizer( this.br );
		this.st.resetSyntax();

		// numbers are treated as words
		this.st.wordChars( '!', '!' );
		this.st.wordChars( '$', '&' );
		this.st.wordChars( '(', '9' );
		this.st.wordChars( ':', ':' );
		this.st.wordChars( '<', '>' );
		this.st.wordChars( '?', '?' );

		this.st.wordChars( '@', '@' );
		this.st.wordChars( 'A', 'Z' );
		this.st.wordChars( '[', '_' );

		this.st.wordChars( '`', '`' );
		this.st.wordChars( 'a', 'z' );
		this.st.wordChars( '{', '~' );

		// All white space plus ; ' and " chars are defaulted to
		// "ordinary" by the StreamTokenizer

		// st.commentChar('#'); - Doesn't work "#"s may occur in quoted strings
		this.st.eolIsSignificant( true );
	}


	/**
	 * Please complete the missing tags for getLineNumber
	 * @param
	 * @return
	 * @throws
	 * @pre
	 * @post
	 */
	public int getLineNumber()
	{
		return this.linenum;
	}


	/**
	 * Please complete the missing tags for getToken
	 * @param
	 * @return
	 * @throws
	 * @pre
	 * @post
	 */
	public String getToken()
		throws IOException, CifParseException
	{
		boolean tokenIsQuoted = false;
		this.tokenIsName = false;

		int state = CifTokenizer.NULL_STATE;
		int lastCharType = CifTokenizer.OTHER_CHAR;

		int lchar;
		int quoteChar = 0;
		String token;
		int ttype = 0;

		// Exit token_found while loop when finished
		token_found:
		while( true )
		{
			lchar = ttype;
			ttype = this.st.nextToken();
			switch( ttype )
			{
				case StreamTokenizer.TT_EOF:
					token = null;
					break token_found;

				case StreamTokenizer.TT_WORD:
					// "ordinary" word
					switch( state )
					{
						case NULL_STATE:
							// Token data found but may be followed by ' " or ;
							state = CifTokenizer.TOKEN_STATE;
							break;
	
						case TOKEN_STATE:
						case REG_QUOTE_STATE:
						case SEMI_QUOTE_STATE:
							// newline *not* followed by ;
							if( lastCharType == CifTokenizer.NEWLINE_CHAR )
							{
								this.addToToken( lchar );
							}
							break;
					}
					this.addToToken( this.st.sval );
					lastCharType = CifTokenizer.OTHER_CHAR;
					break;
					// end "ordinary" word

				case '#' :
					// beginning of comment if not quoted
					switch( state )
					{
						case NULL_STATE:
						case TOKEN_STATE:
							comment:
							while( true )
							{
								// gobble comment
								lchar = ttype;
								ttype = this.st.nextToken();
								switch( ttype )
								{
									case StreamTokenizer.TT_EOF:
										token = null;
										break token_found;
		
									case StreamTokenizer.TT_EOL:
										this.st.pushBack();
										break comment;
		
									default:
										continue;
								}
							}
							break;

						case REG_QUOTE_STATE:
						case SEMI_QUOTE_STATE:
							if( lastCharType == CifTokenizer.NEWLINE_CHAR )
							{
								this.addToToken( lchar );
							}
							this.addToToken( ttype );
							break;
					}
					lastCharType = CifTokenizer.OTHER_CHAR;
					break;

				case ' ' :
				case '\11' :
				case '\13' :
				case '\14' :
					// white space
					switch( state )
					{
						case NULL_STATE:
							// ignore multiple white space
							break;

						case TOKEN_STATE:
							lastCharType = CifTokenizer.WHITESPACE_CHAR;
							token = this.endToken();
							this.st.pushBack();
							break token_found;

						case REG_QUOTE_STATE:
							if( lastCharType == CifTokenizer.REG_QUOTE_CHAR )
							{
								lastCharType = CifTokenizer.WHITESPACE_CHAR;
								token = this.endToken();
								this.st.pushBack();
								break token_found;
							}
							else
							{
								if( lastCharType == CifTokenizer.NEWLINE_CHAR )
								{
									this.addToToken( lchar );
								}
							}
							this.addToToken( ttype );
							break;

						case SEMI_QUOTE_STATE:
							if( lastCharType == CifTokenizer.NEWLINE_CHAR )
							{
								this.addToToken( lchar );
							}
							this.addToToken( ttype );
							break;
					}
					lastCharType = CifTokenizer.WHITESPACE_CHAR;
					break;
					// end white space

				case '\'' :
				case '\"' :
					// quote
					switch( state )
					{
						case NULL_STATE:
							if( lastCharType == CifTokenizer.WHITESPACE_CHAR
							|| lastCharType == CifTokenizer.NEWLINE_CHAR )
							{
								quoteChar = ttype;
								tokenIsQuoted = true;
								state = CifTokenizer.REG_QUOTE_STATE;
							}
							break;

						case TOKEN_STATE:
							this.addToToken( ttype );
							break;

						case REG_QUOTE_STATE:
							// don't match single and double quote chars
							if( ttype == quoteChar )
							{
								if( lastCharType == CifTokenizer.REG_QUOTE_CHAR )
								{
									this.addToToken( ttype );
								}
								// prepare to end quote
								lastCharType = CifTokenizer.REG_QUOTE_CHAR;
							}
							else
							{
								this.addToToken( ttype );
							}
							break;

						case SEMI_QUOTE_STATE:
							this.addToToken( ttype );
							break;
					}
					// prepare to end quote
					if( lastCharType != CifTokenizer.REG_QUOTE_CHAR )
					{
						lastCharType = CifTokenizer.OTHER_CHAR;
					}
					break;
					// end quote

				case ';' :
					// semi-quote
					switch( state )
					{
						case NULL_STATE:
							if( lastCharType == CifTokenizer.NEWLINE_CHAR )
							{
								tokenIsQuoted = true;
								state = CifTokenizer.SEMI_QUOTE_STATE;
							}
							break;

						case TOKEN_STATE:
							this.addToToken( ttype );
							break;

						case REG_QUOTE_STATE:
							this.addToToken( ttype );
							break;

						case SEMI_QUOTE_STATE:
							if( lastCharType == CifTokenizer.NEWLINE_CHAR )
							{
								lastCharType = CifTokenizer.OTHER_CHAR;
								token = this.endToken();
								break token_found;
							}
							else
							{
								this.addToToken( ttype );
							}
							break;
					}
					lastCharType = CifTokenizer.OTHER_CHAR;
					break;

				case StreamTokenizer.TT_EOL:
					// end-of-line
					this.linenum++;
					switch( state )
					{
						case NULL_STATE:
							// ignore multiple white space
							break;

						case TOKEN_STATE:
							lastCharType = CifTokenizer.NEWLINE_CHAR;
							token = this.endToken();
							this.st.pushBack();
							break token_found;

						case REG_QUOTE_STATE:
							if( lastCharType == CifTokenizer.REG_QUOTE_CHAR )
							{
								lastCharType = CifTokenizer.NEWLINE_CHAR;
								token = this.endToken();
								this.st.pushBack();
								break token_found;
							}
							else
							{
								if( lastCharType == CifTokenizer.NEWLINE_CHAR )
								{
									this.addToToken( lchar );
								}
								this.addToToken( this.st.sval );
							}
							break;

						case SEMI_QUOTE_STATE:
							if( lastCharType == CifTokenizer.NEWLINE_CHAR )
							{
								this.addToToken( lchar );
							}
							break;
					}
					lastCharType = CifTokenizer.NEWLINE_CHAR;
					break;
					// end end-of-line

				default:
					switch( state )
					{
						case REG_QUOTE_STATE:
						case SEMI_QUOTE_STATE:
							this.addToToken( ttype );
							// all's fair in quotes
							break;

						case NULL_STATE:
						case TOKEN_STATE:
							throw new CifParseException
							( "CifTokenizer: Unrecognized token: 0x" +
							Integer.toString( ttype, 16 ) + "("
							+ ( ( char ) ttype ) + ")" );
					}
					break;
					// end default
			}
		}
		// end while(true)

		this.tokenIsName = token != null && ! tokenIsQuoted
		&& token.startsWith( "_" );
		if( CifTokenizer.debugTokenizer )
		{
			this.printToken( token );
		}
		// System.err.println(token); // JLM DEBUG
		return token;
	}


	/**
	 * Please complete the missing tags for filename
	 * @param
	 * @return
	 * @throws
	 * @pre
	 * @post
	 */
	public String filename()
	{
		return this.filename;
	}


	/**
	 * Please complete the missing tags for close
	 * @param
	 * @return
	 * @throws
	 * @pre
	 * @post
	 */
	public void close()
		throws IOException
	{
		// BufferedReader br;
		this.br.close();
		this.br = null;
		// StreamTokenizer st;
		this.st = null;
	}


	/**
	 * Please complete the missing tags for addToToken
	 * @param
	 * @return
	 * @throws
	 * @pre
	 * @post
	 */
	private void addToToken( final int c )
	{
		this.sbuf.append( ( char ) c );
	}


	/**
	 * Please complete the missing tags for addToToken
	 * @param
	 * @return
	 * @throws
	 * @pre
	 * @post
	 */
	private void addToToken( final String s )
	{
		this.sbuf.append( s );
	}


	/**
	 * Please complete the missing tags for endToken
	 * @param
	 * @return
	 * @throws
	 * @pre
	 * @post
	 */
	private String endToken()
	{
		final String token = this.sbuf.toString();
		// sbuf.setLength(0);
		this.sbuf = new StringBuffer();
		return token;
	}


	// DEBUG METHOD
	BufferedWriter tof;

	/**
	 * Please complete the missing tags for printToken
	 * @param
	 * @return
	 * @throws
	 * @pre
	 * @post
	 */
	public void printToken( final String token )
		throws IOException
	{
		if( CifTokenizer.debugTokenizer )
		{
			if( this.tof == null )
			{
				int li = this.filename.lastIndexOf( '/' );

				if( li < 0 ) {
					li = 0;
				}
				this.tof = new BufferedWriter( new FileWriter(
				this.filename.substring( li + 1, this.filename.length() )
				+ "_tokens.txt" ) );
			}
			this.tof.newLine();
			if( this.tokenIsName )
			{
				this.tof.write( ":N:" + token );
			}
			else
			{
				this.tof.write( ":::" + token );
			}
		}
		this.tof.flush();
	}
}
