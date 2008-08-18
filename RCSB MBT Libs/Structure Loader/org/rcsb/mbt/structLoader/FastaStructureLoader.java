//  $Id: FastaStructureLoader.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
//
//  Copyright 2000-2004 The Regents of the University of California.
//  All Rights Reserved.
//
//  Permission to use, copy, modify and distribute any part of this
//  Molecular Biology Toolkit (MBT)
//  for educational, research and non-profit purposes, without fee, and without
//  a written agreement is hereby granted, provided that the above copyright
//  notice, this paragraph and the following three paragraphs appear in all
//  copies.
//
//  Those desiring to incorporate this MBT into commercial products
//  or use for commercial purposes should contact the Technology Transfer &
//  Intellectual Property Services, University of California, San Diego, 9500
//  Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815,
//  FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
//
//  IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
//  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//  LOST PROFITS, ARISING OUT OF THE USE OF THIS MBT, EVEN IF THE
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//  THE MBT PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
//  UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
//  UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
//  NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
//  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
//  MBT WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
//
//  For further information, please see:  http://mbt.sdsc.edu
//
//  History:
//  $Log: FastaStructureLoader.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
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
//  Revision 1.7  2005/11/08 20:58:16  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.6  2004/04/09 00:06:40  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.5  2004/01/29 17:23:33  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.4  2003/04/23 18:06:48  moreland
//  emoved StructureComponentID/scid and replaced it with a "structure" field
//  in the StructureComponent base class.
//  Changed "getType" method to "getStructureComponentType" to return dynamic
//  SC type (ie: class name).
//
//  Revision 1.3  2003/04/03 18:24:56  moreland
//  Residue is now a container of Atom objects (or empty for sequence data).
//
//  Revision 1.2  2002/10/24 18:00:50  moreland
//  Added support to use the StructureComponentRegistry class.
//
//  Revision 1.1.1.1  2002/07/16 18:00:19  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//


package org.rcsb.mbt.structLoader;



import java.io.*;
import java.net.*;
import java.util.*;

import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.util.*;


/**
 *  Implements the StructureLoader interface to enable reading of Fasta files
 *  either from local disk or from a URL.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.structLoader.IStructureLoader
 *  @see	org.rcsb.mbt.structLoader.StructureFactory
 */
public class FastaStructureLoader
	implements IFileStructureLoader
{
	protected String urlString;

	//
	// StructureLoader interface methods
	//

	/**
	 * Returns the common name for the loader implementation.
	 * This is the string that might appear in a user-selectable menu.
	 */
	public String getLoaderName( )
	{
		return new String( "Fasta Structure Loader" );
	}


	/**
	 * Returns a reference to a named structure as a Structure object.
	 * The "name" may be interpreted by the specific implementation
	 * of the StructureLoader class. For example, a file loader would
	 * interpret the "name" as a file or URL path, while a database loader
	 * would interpret the "name" as a structure name. This enables a
	 * common interface for all StructureLoader classes, yet does not
	 * prevent a specific implementation from implementing additional
	 * methods. Also, since each StructureLoader sub-class must
	 * implement the "canLoad" method, an application can always
	 * determine if a given loader is capable of delivering a specific
	 * structure or not.
	 */
	public Structure load( final String name )
	{
		if ( ! this.canLoad( name ) ) {
			return null;
		}

		Structure structure = null;
		try
		{	
			final File file = new File( name );
			structure = this.load( file );
		}
		catch( final NullPointerException e )
		{
			try
			{	
				final URL url = new URL( name );
				structure = this.load( url );
			}
			catch( final MalformedURLException e2 )
			{
			}
		}
		return structure;
	}


	/**
	 * Returns true if the loader is capable of loading the structure,
	 * or false otherwise. This enables higher-level code to be able
	 * to build a context sensative menu of only the loaders that can
	 * load a given structure name.
	 */
	public boolean canLoad( final String name )
	{
		// Cache the urlString for use by the Structure class
		this.urlString = name;

		if ( ! name.endsWith( ".fsa" ) ) {
			return false;
		}

		if ( name.startsWith( "file:" ) ||
			name.startsWith( "http:" ) ||
			name.startsWith( "ftp:" ) ) {
			return true;
		}

		return false;
	}

	//
	// FileStructureLoader interface methods
	//

	/**
	 * Returns a reference to a Structure read from the given File object.
	 */
	public Structure load( final File file )
	{
		if ( ! this.canLoad( file ) ) {
			return null;
		}

		try
		{
			final InputStream inputStream = new FileInputStream( file );
			return this.load( inputStream );
		}
		catch ( final FileNotFoundException e )
		{
			return null;
		}
		catch ( final SecurityException e )
		{
			return null;
		}
	}


	/**
	 * Returns true if the Structure can be read from the given File object.
	 */
	public boolean canLoad( final File file )
	{
		if ( ! file.exists() ) {
			return false;
		}

		try
		{
			String path = file.getCanonicalPath( );
			path = "file:" + path;
			return this.canLoad( path );
		}
		catch( final IOException e )
		{
			return false;
		}
	}


	/**
	 * Returns a reference to a Structure read from the given URL object.
	 */
	public Structure load( final URL url )
	{
		if ( ! this.canLoad( url ) ) {
			return null;
		}

		try
		{
			final InputStream inputStream = url.openStream( );
			return this.load( inputStream );
		}
		catch( final IOException e )
		{
			return null;
		}
	}


	/**
	 * Returns true if the Structure can be read from the given URL object.
	 */
	public boolean canLoad( final URL url )
	{
		return this.canLoad( url.toExternalForm( ) );
	}

	//
	// FastaStructureLoader methods
	//

	/**
	 * Returns a reference to a Structure read from the given InputStream.
	 */
	public Structure load( final InputStream inputStream )
	{
		// InputStreamReader isr = new InputStreamReader( inputStream );
		// BufferedReader br = new BufferedReader( isr );
		// String line = br.readline( );

		// Read the file into a raw byte buffer

		byte bytes[];
		int byteCount = 0;
		int bytesRead = 0;
		try
		{
			byteCount = inputStream.available( );
			bytes = new byte[ byteCount ];
			bytesRead = inputStream.read( bytes, 0, byteCount );
			inputStream.close( );
			if ( bytesRead != byteCount ) {
				return null;
			}
		}
		catch ( final IOException e )
		{
			try
			{
				inputStream.close( );
			}
			catch ( final IOException e3 )
			{
			}
			return null;
		}

		// Figure out the size of the header

		int headerLength = 0;
		if ( bytes[0] == '>' )
		{
			for ( headerLength=0; headerLength<byteCount; headerLength++ ) {
				if ( (bytes[headerLength] == '\n') || (bytes[headerLength] == '\r') ) {
					break;
				}
			}
			headerLength++; // Include the new line characer
		}

		// Figure out the number of amino acids

		int dataCount = 0;
		for ( int i=headerLength; i<byteCount; i++ ) {
			if ( (bytes[i] != '\n') && (bytes[i] != '\r') ) {
				dataCount++;
			}
		}

		// Copy the raw data bytes into an indexable buffer
		// (ie: strip out the header and the new lines)

		final byte rawData[] = new byte[ dataCount ];
		int j=0;
		for ( int i=headerLength; i<byteCount; i++ ) {
			if ( (bytes[i] != '\n') && (bytes[i] != '\r') ) {
				rawData[j++] = bytes[i];
			}
		}

		final Structure structure = new Structure()
		{
			String urlStr = FastaStructureLoader.this.urlString;
			byte data[] = rawData;

			
			public String getUrlString( )
			{
				return this.urlStr;
			}

			
			public int getStructureComponentCount( String scType )
			{
				if ( scType == StructureComponentRegistry.TYPE_RESIDUE ) {
					return this.data.length;
				} else {
					return 0;
				}
			}

			private Vector residues = null;
			
			public StructureComponent getStructureComponentByIndex( String type,
				int index )
				throws IndexOutOfBoundsException, IllegalArgumentException
			{
				if ( type != StructureComponentRegistry.TYPE_RESIDUE ) {
					throw new IllegalArgumentException( "type mismatch" );
				}

				if ( this.residues == null )
				{
					int residueCount = this.getStructureComponentCount(
						StructureComponentRegistry.TYPE_RESIDUE );
					this.residues = new Vector( residueCount );
					// Populate the vector with null so we can do cache checks.
					for ( int i=0; i<residueCount; i++ ) {
						this.residues.add( null );
					}
				}

				// Check the cache.
				Residue residue = (Residue) this.residues.elementAt( index );
				if ( residue != null ) {
					return residue;
				}

				// The residue is not in the cache.
				String compound = AminoAcidInfo.getCodeFromLetter( this.data[index] );
				residue = new Residue( compound );
				residue.setStructure( this );

				// Store the residue into the cache.
				this.residues.setElementAt( residue, index );

				return residue;
			}
		};

		return structure;
	}

}

