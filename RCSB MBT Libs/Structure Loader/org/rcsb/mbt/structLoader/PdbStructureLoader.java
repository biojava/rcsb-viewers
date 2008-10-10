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

import org.rcsb.mbt.model.geometry.ModelTransformationList;
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
	private Structure structure;
	private boolean treatModelsAsSubunits = false;
	
	/**
	 * Set this if the models are part of a greater whole.
	 * This is normally set from a flag in the scene manager.
	 * 
	 * @param flag
	 */
	public void setTreatModelsAsSubunits(boolean flag) { treatModelsAsSubunits = flag; }
	
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
	public Structure load( final String name) throws IOException
	{
		structure = null;
		
		try
		{	
			final File file = new File( name );
			structure = this.load( file);
		}
		catch( final NullPointerException e )
		{
			final URL url = new URL( name );
			structure = this.load( url );
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
	public boolean canLoad( final String name)
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
	public Structure load( final File file ) throws IOException
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

		final FileInputStream fileInputStream = new FileInputStream( file );
		if ( fileInputStream == null ) {
			return null;
		}
		
		BufferedReader bufferedReader;
		this.urlString = file.toURL().toExternalForm();
		InputStreamReader ir = null;
		if ( this.urlString.endsWith( ".gz" ) )
		{
			final GZIPInputStream gzipInputStream =
				new GZIPInputStream( fileInputStream );
			ir = new InputStreamReader( gzipInputStream );

			// JLM DEBUG: crude hack for progress, because with
			// a gzip stream we can't tell how much data there will be!
			// A 4:1 compression is typical for PDB files.
			this.expectedInputBytes *= 4;
		}
		else
			ir = new InputStreamReader(fileInputStream);
			
		return this.load( new BufferedReader(ir) );
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
	public Structure load( final URL url ) throws IOException
	{
		if ( ! this.canLoad( url ) ) {
			return null;
		}

		final URLConnection urlConnection = url.openConnection( );
		this.expectedInputBytes = urlConnection.getContentLength( );
//			if ( expectedInputBytes <= 0 ) return null;
		final InputStream inputStream = urlConnection.getInputStream( );

		InputStreamReader ir = null;
		
		if ( inputStream != null )
			this.urlString = url.toExternalForm( );

		if ( this.urlString.endsWith( ".gz" ) )
		{
			final GZIPInputStream gzipInputStream =
				new GZIPInputStream( inputStream );
			ir =
				new InputStreamReader( gzipInputStream );
		}
		else
			ir = new InputStreamReader(inputStream);

		return this.load( new BufferedReader(ir) );
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
	public Structure load( final BufferedReader rdr ) throws IOException
	{
		// System.err.println( "PdbStructureLoader.load(BufferedInputStream)" );
		if ( rdr == null ) {
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

		String line;

		// System.err.println( "PdbStructureLoader.load: expectedReads = " + expectedReads );

		int percentDone = 0;

		Status.progress( percentDone, "Loading " + this.urlString );

		// Create a hash for atom numbers that we can use
		// later if we need to process CONECT records.
		// We'll free memory when we're done with it.
		Hashtable<Integer, Atom> atomNumberHash = new Hashtable<Integer, Atom>( );
		Vector<int[]> conectRecords = new Vector<int[]>( );
		
		int bytesRead = 0;
		int lines = 0;
		int modelCount = 0; // How many models have we seen?

		while ( (line = rdr.readLine()) != null )
		{

			bytesRead += line.length() + 1;
			percentDone = (int)((bytesRead * 100L)/ expectedBytes);
			Status.progress( percentDone, "Loading " + this.urlString );

			lines++;

			// Parse the line buffer
			if (line.startsWith("ATOM") || line.startsWith("HETATM"))
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

				Atom atom = new Atom( );
				String str = null;

				atom.number =  Integer.parseInt(line.substring( 6, 11 ).trim());

				atom.name = line.substring(12, 16 ).trim().replace('*', '\'');	//**JB quick fix: the dictionary expects ' instead of *
				atom.name = sharedStrings.share( atom.name );

				atom.element = line.substring(76, 78).trim();
				atom.element = atom.element.replaceAll( "[0-9]", "" );
				if ( (atom.element == null) || atom.element.equals("") ||
						(PeriodicTable.getElement( atom.element ) == null))
				{
					// The element field was not an element,
					// so, try the first letter of the name.
					atom.element = atom.name.substring( 0, 1 ).trim();
					if ( PeriodicTable.getElement( atom.element ) == null ) {
						throw new IllegalArgumentException( "no atom element symbol around line " + lines );
					}
				}
				atom.element = sharedStrings.share( atom.element );

				atom.altLoc = line.substring(16, 17 ).trim();
				atom.altLoc = sharedStrings.share( atom.altLoc );

				atom.compound = line.substring(17, 20 ).trim();
				atom.compound = sharedStrings.share( atom.compound );

				atom.chain_id = line.substring(21, 22 ).trim();
				if ( atom.chain_id == null || atom.chain_id.equals("") )
					atom.chain_id = "_";

				if(treatModelsAsSubunits)
				{
					atom.chain_id = atom.chain_id + "$$$" + modelCount;
				}

				atom.chain_id = sharedStrings.share( atom.chain_id );
				//							System.out.println(modelCount);

				final String newResidueIdRaw = line.substring(22, 28 ).trim();
					//**JB expanded to read 6 chars to account for non-integer residue ids. (Was 4 chars).
				String temp = newResidueIdRaw;
				while(Character.isLetter(temp.charAt(temp.length() - 1)))
					temp = temp.substring(0, temp.length() - 1);
						//**JB don't remove anything but the last letters.
						//  If there are any spaces between the letters and the number, etc.,
						// I want an exception thrown so I know.

				final int newResidueIdIntSimple = Integer.parseInt(temp);
				int newResidueIdInt = -1;
				int increment = Math.abs(newResidueIdIntSimple - previousResidueIdIntSimple);
				if(increment == 0 && !previousResidueIdRaw.equals(newResidueIdRaw))
					increment = 1;
						// if this isn't a simple number, need to check the string as well.

				if(previousResidueIdInt == Integer.MIN_VALUE)
				{
					newResidueIdInt = newResidueIdIntSimple;
					increment = 1;	// flag to make sure this chain/residue id pair is recorded.
				}
				
				else
					newResidueIdInt = previousResidueIdInt + increment;

				if(increment > 0)
				{
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
				atom.coordinate[0] = Double.parseDouble(line.substring(30, 38 ).trim());
				atom.coordinate[1] = Double.parseDouble(line.substring(38, 46 ).trim());
				atom.coordinate[2] = Double.parseDouble(line.substring(46, 54 ).trim());

				str = line.substring(54, 60 ).trim();
				atom.occupancy = ( str.length() == 0 )? 1.0f : Float.parseFloat( str );

				str = line.substring(60, 66 ).trim();
				atom.bfactor = ( str.length() == 0 )? 0.0f : Float.parseFloat( str );

				Vector<StructureComponent> records = this.passComponents.get(StructureComponentRegistry.TYPE_ATOM);
				
				if ( records == null )
				{
					records = new Vector<StructureComponent>( );
					this.passComponents.put(
							StructureComponentRegistry.TYPE_ATOM, records );
				}
				
				records.add( atom );

				// Add atom to cache for conect record processing.
				atomNumberHash.put( new Integer( atom.number ), atom );

				continue;
			}

			//
			// MODEL record
			//
			else if (line.startsWith("MODEL"))
			{
				modelCount++; // How many models have we seen?

				if ( !this.shouldRecordMoreModels(modelCount) ) {
					break; // Only load 1st model
				}

				// Reset linePos to the start of the line buffer.
				continue;
			}

			if ( !this.shouldRecordMoreModels(modelCount) )
				break; // Only load 1st model
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
		structure = new Structure()
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


	public PdbToNdbConverter getIDConverter() {
		return this.converter;
	}
	
	private boolean shouldRecordMoreModels(final int modelCount)
	{
		return treatModelsAsSubunits || modelCount < 2;
	}
	
	/**
	 * get the completed structure.
	 * @return
	 */
    public Structure getStructure() { return structure; }

	/**
	 */
	public String[] getNonProteinChainIds() { return null; }
	


	public boolean hasUnitCell() {
		return false;
	};
	
	/**
	 * get the unit cell for biological units
	 * @return
	 */
	public UnitCell getUnitCell() { return null; }


	public ModelTransformationList getBiologicalUnitTransformationMatrices() {
		return null;
	}


	public ModelTransformationList getNonCrystallographicOperations() {
		return null;
	}


	public boolean hasBiologicUnitTransformationMatrices() {
		return false;
	}


	public boolean hasNonCrystallographicOperations() {
		return false;
	}

}

