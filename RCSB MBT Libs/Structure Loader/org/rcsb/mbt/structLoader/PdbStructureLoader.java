//  $Id: PdbStructureLoader.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: PdbStructureLoader.java,v $
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
//  Revision 1.5  2006/07/18 21:06:38  jbeaver
//  *** empty log message ***
//
//  Revision 1.4  2006/05/17 16:47:29  jbeaver
//  fixed bug with biological units flag
//
//  Revision 1.3  2006/05/17 06:18:07  jbeaver
//  added some biological unit processing
//
//  Revision 1.2  2006/05/16 17:57:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.20  2005/11/08 20:58:16  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.19  2004/11/08 22:54:27  moreland
//  If the element field is not an element, try the first letter of the name field.
//
//  Revision 1.18  2004/08/16 16:30:19  moreland
//  Now uses SharedObjects class to reduce memory usage.
//
//  Revision 1.17  2004/06/29 18:11:05  moreland
//  Added minimal support for MODEL records - now only loads the first model.
//
//  Revision 1.16  2004/05/20 21:58:25  moreland
//  Improved atom.element determination from atom.name when element is missing.
//  Added support for the CONECT record.
//
//  Revision 1.15  2004/05/12 23:49:30  moreland
//  Added support to populate the StructureInfo meta-data object.
//
//  Revision 1.14  2004/05/10 17:57:24  moreland
//  Added support for alternate location identifier (atom occupancy < 1.0).
//
//  Revision 1.13  2004/04/09 00:06:40  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.12  2004/01/29 17:23:33  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.11  2003/09/19 16:11:51  moreland
//  Changed "println" to "Status.output" calls.
//
//  Revision 1.10  2003/05/16 21:28:19  moreland
//  Provide a default 1.0 for atom.occupancy and 0.0 for atom.bfactor if data field is empty.
//
//  Revision 1.9  2003/05/14 01:09:37  moreland
//  Fixed shared-state problem to enable multiple structure load operations.
//
//  Revision 1.8  2003/04/30 17:55:21  moreland
//  Added Atom serial number field support.
//  Corrected progress calculation for expectedBytes for compressed data.
//
//  Revision 1.7  2003/04/29 16:08:20  moreland
//  Changed progress message from "Processing" to "Loading".
//  Changed default when atom.chain_id is empty or null from "A" to "-" (bourne).
//
//  Revision 1.6  2003/04/23 18:08:23  moreland
//  Removed StructureComponentID/scid and replaced it with a "structure" field
//  in the StructureComponent base class.
//  Changed "getType" method to "getStructureComponentType" to return dynamic
//  SC type (ie: class name).
//  StructureComponent records are now retrieved from a fast cache.
//
//  Revision 1.5  2003/04/05 01:07:10  moreland
//  Added URL loading support.
//  Enabled GZIP reading (for both File and URL loading).
//  Rewrote buffering to use BufferedInputStream and two small read buffers.
//  Rewrote parser code to instantiate StructureComponent objects.
//  Tossed "getColumns" method and instead use faster direct buffer access.
//  Changed import lines to use * (it was too painful to keep adding individual classes).
//  Moved almost all parameters from the class to the primary load method.
//  Added Status.progress support.
//  StructureComponent objects now stored and retrieved from a Hashtable of Vectors.
//
//  Revision 1.4  2003/04/02 18:27:34  moreland
//  Fixed PDB field column error.
//  Added code to support gzip compressed PDB files.
//
//  Revision 1.3  2003/02/25 01:13:04  moreland
//  Removed unused getToken method.
//
//  Revision 1.2  2003/02/03 22:51:08  moreland
//  Added Strand/sheet support.
//
//  Revision 1.1.1.1  2002/07/16 18:00:19  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//


package org.rcsb.mbt.structLoader;



import java.util.*;
import java.io.*;
import java.net.*;
import java.util.zip.*;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.scene.PdbToNdbConverter;
import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.util.*;



/**
 *  Implements the StructureLoader interface to enable reading of PDB files
 *  either from local disk or from a URL.
 *  <P>
 *  This loader folows the PDB file format as documented by:
 *  http://www.rcsb.org/pdb/docs/format/pdbguide2.2/guide2.2_frame.html
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.structLoader.IStructureLoader
 *  @see	org.rcsb.mbt.structLoader.StructureFactory
 */
public class PdbStructureLoader
	implements IFileStructureLoader
{
	protected String urlString = null;
	private long expectedInputBytes = 1;
	private PdbToNdbConverter converter = null;
	
	// A hashtable of vectors where
	// each hash KEY is the StructureComponent type String.
	// each hash VALUE is a Vector of StructureComponent objects.
	// NOTE: This protected variable is used only to enable each new
	// Structure object to access the newly loaded data. The Structure
	// constructor stores a copy of the object so this variable can
	// be re-used for the next load request. This is not thread-safe
	// but there doesn't seem to be any other way to enable an
	// anonymous class to be handed state (asside from parameters).
	protected Hashtable<String, Vector<StructureComponent>> passComponents = null;

	// Container for any general meta-data that describes the structure.
	protected StructureInfo structureInfo = null;
	
	//
	// StructureLoader interface methods
	//

	/**
	 * Returns the common name for the loader implementation.
	 * This is the string that might appear in a user-selectable menu.
	 */
	public String getLoaderName( )
	{
		return new String( "PDB Structure Loader" );
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
		if ( structure != null ) {
			this.urlString = name;
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
		// System.err.println( "PdbStructureLoader.canLoad(String)" );
		if ( name.indexOf( ".pdb" ) < 0 ) {
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
		// System.err.println( "PdbStructureLoader.load(File)" );

		if ( ! this.canLoad( file ) ) {
			return null;
		}
		if ( ! file.exists() ) {
			return null;
		}
		if ( ! file.canRead() ) {
			return null;
		}

		this.expectedInputBytes = file.length( );
		if ( this.expectedInputBytes <= 0 ) {
			return null;
		}

		try
		{
			final FileInputStream fileInputStream = new FileInputStream( file );
			if ( fileInputStream == null ) {
				return null;
			}
			this.urlString = file.toURL().toExternalForm();
			BufferedInputStream bufferedInputStream = null;
			if ( this.urlString.endsWith( ".gz" ) )
			{
				final GZIPInputStream gzipInputStream =
					new GZIPInputStream( fileInputStream );
				bufferedInputStream =
					new BufferedInputStream( gzipInputStream );

				// JLM DEBUG: crude hack for progress, because with
				// a gzip stream we can't tell how much data there will be!
				// A 4:1 compression is typical for PDB files.
				this.expectedInputBytes *= 4;
			}
			else
			{
				bufferedInputStream =
					new BufferedInputStream( fileInputStream );
			}
			return this.load( bufferedInputStream );
		}
		catch ( final FileNotFoundException e )
		{
			return null;
		}
		catch ( final MalformedURLException e )
		{
			return null;
		}
		catch ( final SecurityException e )
		{
			return null;
		}
		catch ( final IOException e )
		{
			return null;
		}
	}


	/**
	 * Returns true if the Structure can be read from the given File object.
	 */
	public boolean canLoad( final File file )
	{
		// System.err.println( "PdbStructureLoader.canLoad(File)" );
		if ( ! file.exists() ) {
			return false;
		}

		try
		{
			// String path = file.getCanonicalPath( );
			final String path = file.toURL().toExternalForm();
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
		// System.err.println( "PdbStructureLoader.load(URL)" );

		if ( ! this.canLoad( url ) ) {
			return null;
		}

		try
		{
			final URLConnection urlConnection = url.openConnection( );
			this.expectedInputBytes = urlConnection.getContentLength( );
//			if ( expectedInputBytes <= 0 ) return null;
			final InputStream inputStream = urlConnection.getInputStream( );

			BufferedInputStream bufferedInputStream = null;
			if ( inputStream != null ) {
				this.urlString = url.toExternalForm( );
			}
			if ( this.urlString.endsWith( ".gz" ) )
			{
				final GZIPInputStream gzipInputStream =
					new GZIPInputStream( inputStream );
				bufferedInputStream =
					new BufferedInputStream( gzipInputStream );
			}
			else
			{
				bufferedInputStream =
					new BufferedInputStream( inputStream );
			}
			return this.load( bufferedInputStream );
		}
		catch( final IOException e )
		{
			Status.output( Status.LEVEL_ERROR, "PdbStructureLoader.load( " + url + " ): " + e );
			return null;
		}
	}


	/**
	 * Returns true if the Structure can be read from the given URL object.
	 */
	public boolean canLoad( final URL url )
	{
		// System.err.println( "PdbStructureLoader.canLoad(URL)" );
		return this.canLoad( url.toExternalForm( ) );
	}

	//
	// PdbStructureLoader methods
	//

	/**
	 * Returns a reference to a Structure read from the given (uncompressed)
	 * InputStream.
	 */
	public Structure load( final BufferedInputStream bufferedInputStream )
	{
		// System.err.println( "PdbStructureLoader.load(BufferedInputStream)" );
		if ( bufferedInputStream == null ) {
			return null;
		}
		
		final Vector<String> pdbChainIds = new Vector<String>();
		final Vector<String> pdbResidueIds = new Vector<String>();
		final Vector<String> ndbChainIds = new Vector<String>();
		final Vector<Object> ndbResidueIds = new Vector<Object>();
		String previousResidueIdRaw = "";	// the untouched residue id.
		int previousResidueIdInt = Integer.MIN_VALUE;	// the residue id which was assigned to the Atom.residue_id field
		int previousResidueIdIntSimple = Integer.MIN_VALUE;	// the simple int conversion of the file's residue id, minus any letters.  

		SharedObjects sharedStrings = new SharedObjects( );

		this.passComponents = new Hashtable<String, Vector<StructureComponent>>( );
		final long expectedBytes = this.expectedInputBytes;
		// System.err.println( "PdbStructureLoader.load: expectedBytes = " + expectedBytes );

		final byte buf[] = new byte[ 2048 ]; // raw read data buffer
		final byte line[] = new byte[ 100 ]; // accumulated line buffer

		// System.err.println( "PdbStructureLoader.load: expectedReads = " + expectedReads );
		int onePercent = (int) (((float) expectedBytes / (float) buf.length) / 100.f);
		if ( onePercent <= 0 ) {
			onePercent = 1;
		}
		// System.err.println( "PdbStructureLoader.load: onePercent = " + onePercent );
		int percentDone = 0;

		Status.progress( percentDone, "Loading " + this.urlString );

		// Create a hash for atom numbers that we can use
		// later if we need to process CONECT records.
		// We'll free memory when we're done with it.
		Hashtable<Integer, Atom> atomNumberHash = new Hashtable<Integer, Atom>( );
		Vector<int[]> conectRecords = new Vector<int[]>( );
		
		int linePos = 0;
		int bytesRead = 0;
		int lastRead = 0;
		int lines = 0;
		int readCount = 0;
		int modelCount = 0; // How many models have we seen?
		try
		{
			while ( bufferedInputStream.available() > 0 )
			{
				// Re-fill the raw data buffer.
				try
				{
					lastRead = bufferedInputStream.read( buf, 0, buf.length );
				}
				catch( final IOException e )
				{
					Status.output( Status.LEVEL_ERROR, "PdbStructureLoader.load( stream ): " + e );
					return null;
				}
				bytesRead += lastRead;

				readCount++;
				if ( readCount % onePercent == 0 )
				{
					percentDone = (int)((bytesRead * 100L)/ expectedBytes);
					Status.progress( percentDone, "Loading " + this.urlString );
				}

/* XXX_DBUG */
				String strLine  = "";
/**/
				// Process the buffer
				for ( int bufPos=0; bufPos<lastRead; bufPos++ )
				{
					// Copy a byte from the input buffer into the line buffer
					line[linePos] = buf[bufPos];
/* XXX DEBUG */
					strLine += (char)buf[bufPos];
/**/
					if ( buf[bufPos] == '\n' )
					{
						// line[0-linePos] now has a complete line.
						lines++;
	
						// Parse the line buffer
	
						//
						// ATOM and HETATM records
						//
						boolean isAtom = false;
						if (
							(line[0] == 'A') &&
							(line[1] == 'T') &&
							(line[2] == 'O') &&
							(line[3] == 'M')
						) {
							isAtom = true;
						} else if (
							(line[0] == 'H') &&
							(line[1] == 'E') &&
							(line[2] == 'T') &&
							(line[3] == 'A') &&
							(line[4] == 'T') &&
							(line[5] == 'M')
						) {
							isAtom = true;
						}
						if ( isAtom )
						{
							// PDB File Atom Record offsets as documented by
							// http://www.rcsb.org/pdb/docs/format/pdbguide2.2/part_62.html
 							// 1 -  6   RecordName
 							// 7 - 11   serial
 							// 12       -
							// 13 - 16  name
							// 17       altLoc
							// 18 - 20  resName
							// 21       -
							// 22       chainID
							// 23 - 26  resSeq
							// 27       iCode
							// 28 - 30  -
							// 31 - 38  x
							// 39 - 46  y
							// 47 - 54  z
							// 55 - 60  occupancy
							// 61 - 66  tempFactor
							// 67 - 72  -
							// 73 - 76  segID
							// 77 - 78  element
							// 79 - 80  charge
							// NOTE: In this application, we need to subtract 1 from
							// each index in order to match the 0-based array offsets.
	
							final Atom atom = new Atom( );
							String str = null;

							atom.number =  Integer.parseInt( (new String( line, 6, 5 )).trim() );

							atom.name = (new String( line, 12, 4 )).trim().replace('*', '\'');	//**JB quick fix: the dictionary expects ' instead of *
							atom.name = sharedStrings.share( atom.name );

							atom.element = (new String( line, 76, 2 )).trim();
							atom.element = atom.element.replaceAll( "[0-9]", "" );
							if ( (atom.element == null) ||
								atom.element.equals("") ||
								(PeriodicTable.getElement( atom.element ) == null)
							)
							{
								// The element field was not an element,
								// so, try the first letter of the name.
								atom.element = atom.name.substring( 0, 1 );
								if ( PeriodicTable.getElement( atom.element ) == null ) {
									throw new IllegalArgumentException( "no atom element symbol around line " + lines );
								}
							}
							atom.element = sharedStrings.share( atom.element );

							atom.altLoc = (new String( line, 16, 1 )).trim();
							atom.altLoc = sharedStrings.share( atom.altLoc );

							atom.compound = (new String( line, 17, 3 )).trim();
							atom.compound = sharedStrings.share( atom.compound );

							atom.chain_id = (new String( line, 21, 1 )).trim();
							if ( atom.chain_id == null ) {
								atom.chain_id = "_";
							} else if ( atom.chain_id.equals( "" ) ) {
								atom.chain_id = "_";
							}

							if(AppBase.sgetSceneController().shouldTreatModelsAsSubunits()) {
								atom.chain_id = atom.chain_id + "$$$" + modelCount;
							}
							
							atom.chain_id = sharedStrings.share( atom.chain_id );
//							System.out.println(modelCount);
							
							final String newResidueIdRaw = new String( line, 22, 6 ).trim();  //**JB expanded to account for non-integer residue ids. Was: ( line, 22, 4 ).
							String temp = newResidueIdRaw;
							while(Character.isLetter(temp.charAt(temp.length() - 1))) {		//**JB don't remove anything but the last letters. If there are any spaces between the letters and the number, etc., I want an exception thrown so I know.
								temp = temp.substring(0, temp.length() - 1);
							}
							final int newResidueIdIntSimple = Integer.parseInt(temp);
							int newResidueIdInt = -1;
							int increment = Math.abs(newResidueIdIntSimple - previousResidueIdIntSimple);
							if(increment == 0 && !previousResidueIdRaw.equals(newResidueIdRaw)) {	// if this isn't a simple number, need to check the string as well.
								increment = 1;
							}
							if(previousResidueIdInt == Integer.MIN_VALUE) {
								newResidueIdInt = newResidueIdIntSimple;
								increment = 1;	// flag to make sure this chain/residue id pair is recorded.
							} else {
								newResidueIdInt = previousResidueIdInt + increment;
							}
							
							if(increment > 0) {
								pdbChainIds.add(atom.chain_id);
								ndbChainIds.add(atom.chain_id);
								pdbResidueIds.add(newResidueIdRaw);
								ndbResidueIds.add(new Integer(newResidueIdInt));
							}
							
							previousResidueIdInt = newResidueIdInt;
							previousResidueIdIntSimple = newResidueIdIntSimple;
							previousResidueIdRaw = newResidueIdRaw;
							
							atom.residue_id = newResidueIdInt;

							atom.coordinate = new double[3];
							atom.coordinate[0] =
								Double.parseDouble( (new String( line, 30, 8 )).trim() );
							atom.coordinate[1] =
								Double.parseDouble( (new String( line, 38, 8 )).trim() );
							atom.coordinate[2] =
								Double.parseDouble( (new String( line, 46, 8 )).trim() );

							str = (new String( line, 54, 6 )).trim();
							if ( str.length() == 0 ) {
								atom.occupancy = 1.0f;
							} else {
								atom.occupancy = Float.parseFloat( str );
							}

							str = (new String( line, 60, 6 )).trim();
							if ( str.length() == 0 ) {
								atom.bfactor = 0.0f;
							} else {
								atom.bfactor = Float.parseFloat( str );
							}
	
							Vector<StructureComponent> records = this.passComponents.get(
								StructureComponentRegistry.TYPE_ATOM );
							if ( records == null )
							{
								records = new Vector<StructureComponent>( );
								this.passComponents.put(
									StructureComponentRegistry.TYPE_ATOM, records );
							}
							records.add( atom );

							// Add atom to cache for conect record processing.
							atomNumberHash.put( new Integer( atom.number ), atom );

							// Reset linePos to the start of the line buffer.
							linePos = 0;
							continue;
						}
	
						//
						// HELIX record
						//
//						if (
//							(line[0] == 'H') &&
//							(line[1] == 'E') &&
//							(line[2] == 'L') &&
//							(line[3] == 'I') &&
//							(line[4] == 'X')
//						)
//						{
//							Helix helix = new Helix( );
//	
//							helix.name = (new String( line, 0, 6 )).trim();
//							helix.name = sharedStrings.share( helix.name );
//							helix.start_compound = (new String( line, 15, 3 )).trim();
//							helix.start_compound = sharedStrings.share( helix.start_compound );
//							helix.start_chain = (new String( line, 19, 1 )).trim();
//							if ( helix.start_chain == null ) helix.start_chain = "A";
//							helix.start_chain = sharedStrings.share( helix.start_chain );
//							helix.start_residue = Integer.parseInt( (new String( line, 21, 4 )).trim() );
//							helix.end_compound = (new String( line, 27, 3 )).trim();
//							helix.end_compound = sharedStrings.share( helix.end_compound );
//							helix.end_chain = (new String( line, 31, 1 )).trim();
//							if ( helix.end_chain == null ) helix.end_chain = "A";
//							helix.end_chain = sharedStrings.share( helix.end_chain );
//							helix.end_residue = Integer.parseInt( (new String( line, 33, 4 )).trim() );
//							helix.setRighthand( true ); // JLM DEBUG
//	
//							Vector records = (Vector) passComponents.get(
//								StructureComponentRegistry.TYPE_HELIX );
//							if ( records == null )
//							{
//								records = new Vector( );
//								passComponents.put(
//									StructureComponentRegistry.TYPE_HELIX, records );
//							}
//							records.add( helix );
//	
//							// Reset linePos to the start of the line buffer.
//							linePos = 0;
//							continue;
//						}
//	
//						//
//						// TURN record
//						//
//						if (
//							(line[0] == 'T') &&
//							(line[1] == 'U') &&
//							(line[2] == 'R') &&
//							(line[3] == 'N')
//						)
//						{
//							Turn turn = new Turn( );
//	
//							turn.name = (new String( line, 0, 6 )).trim();
//							turn.name = sharedStrings.share( turn.name );
//							turn.start_compound = (new String( line, 15, 3 )).trim();
//							turn.start_compound = sharedStrings.share( turn.start_compound );
//							turn.start_chain = (new String( line, 19, 1 )).trim();
//							if ( turn.start_chain == null ) turn.start_chain = "A";
//							turn.start_chain = sharedStrings.share( turn.start_chain );
//							turn.start_residue = Integer.parseInt( (new String( line, 20, 4 )).trim() );
//							turn.end_compound = (new String( line, 26, 3 )).trim();
//							turn.end_compound = sharedStrings.share( turn.end_compound );
//							turn.end_chain = (new String( line, 30, 1 )).trim();
//							if ( turn.end_chain == null ) turn.end_chain = "A";
//							turn.end_chain = sharedStrings.share( turn.end_chain );
//							turn.end_residue = Integer.parseInt( (new String( line, 31, 4 )).trim() );
//	
//							Vector records = (Vector) passComponents.get(
//								StructureComponentRegistry.TYPE_TURN );
//							if ( records == null )
//							{
//								records = new Vector( );
//								passComponents.put(
//									StructureComponentRegistry.TYPE_TURN, records );
//							}
//							records.add( turn );
//	
//							// Reset linePos to the start of the line buffer.
//							linePos = 0;
//							continue;
//						}
//	
//						//
//						// STRAND (SHEET) record
//						//
//						if (
//							(line[0] == 'S') &&
//							(line[1] == 'H') &&
//							(line[2] == 'E') &&
//							(line[3] == 'E') &&
//							(line[4] == 'T')
//						)
//						{
//							// Each SHEET record is really composed of
//							// multiple STRAND records.
//
//							Strand strand = new Strand( );
//
//							strand.name = (new String( line, 0, 6 )).trim();
//							strand.name = sharedStrings.share( strand.name );
//							strand.start_compound = (new String( line, 17, 3 )).trim();
//							strand.start_compound = sharedStrings.share( strand.start_compound );
//							strand.start_chain = (new String( line, 21, 1 )).trim();
//							if ( strand.start_chain == null ) strand.start_chain = "A";
//							strand.start_chain = sharedStrings.share( strand.start_chain );
//							strand.start_residue = Integer.parseInt( (new String( line, 22, 4 )).trim() );
//							strand.end_compound = (new String( line, 28, 3 )).trim();
//							strand.end_compound = sharedStrings.share( strand.end_compound );
//							strand.end_chain = (new String( line, 32, 1 )).trim();
//							if ( strand.end_chain == null ) strand.end_chain = "A";
//							strand.end_chain = sharedStrings.share( strand.end_chain );
//							strand.end_residue = Integer.parseInt( (new String( line, 33, 4 )).trim() );
//
//							// Add the Strand object.
//							Vector records = (Vector) passComponents.get(
//								StructureComponentRegistry.TYPE_STRAND );
//							if ( records == null )
//							{
//								records = new Vector( );
//								passComponents.put(
//									StructureComponentRegistry.TYPE_STRAND, records );
//							}
//							records.add( strand );
//	
//							// Reset linePos to the start of the line buffer.
//							linePos = 0;
//							continue;
//						}
//
//						//
//						// HEADER record
//						//
//						if (
//							(line[0] == 'H') &&
//							(line[1] == 'E') &&
//							(line[2] == 'A') &&
//							(line[3] == 'D') &&
//							(line[4] == 'E') &&
//							(line[5] == 'R')
//						)
//						{
//							if ( structureInfo == null )
//								structureInfo = new StructureInfo( );
//	
//							structureInfo.setReleaseDate(
//								(new String( line, 50, 9 )).trim()
//							);
//
//							structureInfo.setIdCode(
//								(new String( line, 62, 4 )).trim()
//							);
//
//							// Reset linePos to the start of the line buffer.
//							linePos = 0;
//							continue;
//						}
//
//						//
//						// AUTHOR record
//						//
//						if (
//							(line[0] == 'A') &&
//							(line[1] == 'U') &&
//							(line[2] == 'T') &&
//							(line[3] == 'H') &&
//							(line[4] == 'O') &&
//							(line[5] == 'R')
//						)
//						{
//							if ( structureInfo == null )
//								structureInfo = new StructureInfo( );
//	
//							String authors = structureInfo.getAuthors( );
//							String moreAuthors =
//								(new String( line, 10, 60 )).trim();
//
//							if ( authors == null )
//								structureInfo.setAuthors( moreAuthors );
//							else
//								structureInfo.setAuthors( authors + moreAuthors );
//
//							// Reset linePos to the start of the line buffer.
//							linePos = 0;
//							continue;
//						}
//
//						//
//						// COMPND record
//						//
//						if (
//							(line[0] == 'C') &&
//							(line[1] == 'O') &&
//							(line[2] == 'M') &&
//							(line[3] == 'P') &&
//							(line[4] == 'N') &&
//							(line[5] == 'D')
//						)
//						{
//							if ( structureInfo == null )
//								structureInfo = new StructureInfo( );
//	
//							String compound = structureInfo.getLongName( );
//							String moreCompound =
//								(new String( line, 10, 60 )).trim();
//
//							if ( compound == null )
//							{
//								structureInfo.setLongName( moreCompound );
//								if ( moreCompound.length() > 20 )
//									structureInfo.setShortName( moreCompound.substring( 0, 20 ) + "..." );
//								else
//									structureInfo.setShortName( moreCompound );
//							}
//							else
//								structureInfo.setLongName( compound + " : " + moreCompound );
//
//							// Reset linePos to the start of the line buffer.
//							linePos = 0;
//							continue;
//						}
//
//						//
//						// EXPDTA record
//						//
//						if (
//							(line[0] == 'E') &&
//							(line[1] == 'X') &&
//							(line[2] == 'P') &&
//							(line[3] == 'D') &&
//							(line[4] == 'T') &&
//							(line[5] == 'A')
//						)
//						{
//							if ( structureInfo == null )
//								structureInfo = new StructureInfo( );
//	
//							String expData =
//								structureInfo.getDeterminationMethod( );
//							String moreExpData =
//								(new String( line, 10, 60 )).trim();
//
//							if ( expData == null )
//								structureInfo.setDeterminationMethod( moreExpData );
//							else
//								structureInfo.setDeterminationMethod( expData + " : " + moreExpData );
//
//							// Reset linePos to the start of the line buffer.
//							linePos = 0;
//							continue;
//						}

						//
						// CONECT record
						//**JB calculate bonds ignoring this data.
//						if (
//							(line[0] == 'C') &&
//							(line[1] == 'O') &&
//							(line[2] == 'N') &&
//							(line[3] == 'E') &&
//							(line[4] == 'C') &&
//							(line[5] == 'T')
//						)
//						{
//							// Cache the data until we have atoms.
//							int conect[] = new int[11];
//							for ( int i=0; i<=conect.length; i++ )
//							{
//								conect[i] = Integer.parseInt(
//									(new String( line, 6+i*5, 5 )).trim()
//								);
//							}
//							conectRecords.add( conect );
//
//							// Reset linePos to the start of the line buffer.
//							linePos = 0;
//							continue;
//						}

						//
						// MODEL record
						//
						if (
							(line[0] == 'M') &&
							(line[1] == 'O') &&
							(line[2] == 'D') &&
							(line[3] == 'E') &&
							(line[4] == 'L')
						)
						{
							modelCount++; // How many models have we seen?

							if ( !this.shouldRecordMoreModels(modelCount) ) {
								break; // Only load 1st model
							}

							// Reset linePos to the start of the line buffer.
							linePos = 0;
							continue;
						}

						// Reset linePos to the start of the line buffer.
						linePos = 0;
/* XXX_DEBUG */
						strLine = "";
/**/
					}
					else
					{
						// Continue building the line.
						linePos++;
						if ( linePos >= line.length )
						{
							Status.output( Status.LEVEL_ERROR, "PdbStructureLoader.load( stream ): buffer overflow!" );
							return null;
						}
					}
				}

				if ( !this.shouldRecordMoreModels(modelCount) ) {
					break; // Only load 1st model
				}
			}
		}
		catch( final IOException e )
		{
			Status.output( Status.LEVEL_ERROR, "PdbStructureLoader.load( stream ): " + e );
			return null;
		}

		//
		// Post-process cached CONECT records to produce Bond objects.
		//
		final int conectCount = conectRecords.size( );
		if ( conectCount > 0 )
		{
			final Vector<StructureComponent> bonds = new Vector<StructureComponent>( );
			this.passComponents.put( StructureComponentRegistry.TYPE_BOND, bonds );

			for ( int i=0; i<conectCount; i++ )
			{
				final int conect[] = (int[]) conectRecords.elementAt( i );
				final Atom atom0 = atomNumberHash.get( new Integer( conect[0] ) );
				for ( int j=1; j<conect.length; j++ )
				{
					if ( conect[j] < 0 ) {
						continue;
					}
					final Atom atom1 = (Atom) atomNumberHash.get( new Integer( conect[j] ) );
					bonds.add( new Bond( atom0, atom1 ) );
				}
			}
		}
		conectRecords.clear( );
		conectRecords = null;
		atomNumberHash.clear( );
		atomNumberHash = null;

		//
		// Create the Structure object
		//
		final Structure structure = new Structure()
		{
			// A hashtable of vectors where
			// each hash KEY is the StructureComponent type String.
			// each hash VALUE is a Vector of StructureComponent objects.
			protected Hashtable<String, Vector<StructureComponent>> structureComponents = null;

			// To free up the global state for another load call.
			private String localUrlString;

			// public Structure()  Anonymous inner class constructor.
			{
				this.structureComponents = PdbStructureLoader.this.passComponents;
				PdbStructureLoader.this.passComponents = null;

				this.localUrlString = PdbStructureLoader.this.urlString;
				PdbStructureLoader.this.urlString = null;

				if ( PdbStructureLoader.this.structureInfo != null ) {
					this.setStructureInfo( PdbStructureLoader.this.structureInfo );
				}
			}

			
			public String getUrlString( )
			{
				return this.localUrlString;
			}

			
			public int getStructureComponentCount( String scType )
			{
				Vector<StructureComponent> records = this.structureComponents.get( scType );
				if ( records == null ) {
					return 0;
				} else {
					return records.size( );
				}
			}

			public StructureComponent getStructureComponentByIndex( String type,
				int index )
				throws IndexOutOfBoundsException, IllegalArgumentException
			{
				Vector<StructureComponent> records = this.structureComponents.get( type );
				if ( records == null ) {
					throw new IllegalArgumentException( "no records of type " + type );
				}

				StructureComponent structureComponent =
					(StructureComponent) records.elementAt( index );
				structureComponent.setStructure( this );

				return structureComponent;
            }
		};

		sharedStrings.clear( );
		sharedStrings = null;

		this.converter = new PdbToNdbConverter();
		this.converter.append(pdbChainIds, ndbChainIds, pdbResidueIds, ndbResidueIds);
		
		// Progress is done.
		Status.progress( 100, null );

		return structure;
	}


	public PdbToNdbConverter getConverter() {
		return this.converter;
	}
	
	private boolean shouldRecordMoreModels(final int modelCount) {
		return AppBase.sgetSceneController().shouldTreatModelsAsSubunits() || modelCount < 2;
	}
}

