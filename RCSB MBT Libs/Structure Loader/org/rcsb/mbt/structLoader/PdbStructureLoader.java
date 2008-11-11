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


package org.rcsb.mbt.structLoader;



import java.util.*;
import java.io.*;
import java.net.*;
import java.util.zip.*;

import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.geometry.ModelTransformationList;
import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.util.*;



/**
 *  Implements the StructureLoader interface to enable reading of PDB files
 *  either from local disk or from a URL.
 *  <P>
 *  This loader follows (somewhat) the PDB file format as documented by:
 *  http://www.rcsb.org/pdb/docs/format/pdbguide2.2/guide2.2_frame.html
 *  <P>
 *  <h3>Rules for non-protein/ligand classification: ('breakoutByResId' defined)</h3>
 *  <p>
 *  In the XML reader, there are secondary identifiers that can be used to break out
 *  non-protein chains more clearly.  Those aren't available in the PDB file.</p>
 *  <p>
 *  However, by examining the HETATM records and looking at residue and compound id, it is
 *  possible to break out chains like the XML reader.  The structure is essentially the same,
 *  however the name specifics aren't, because we don't have those in the pdb file.</p>
 *  <p>
 *  In general, it is probably better and more accurate to bring up the .xml file, if
 *  possible.  If not, the reconstructed pdb reader with the 'breakoutByResId' flag
 *  (currently set explicitly in the doc controller) will do a reasonable job of
 *  mimic-ing the .xml reader behavior.</p>
 *  <ul>
 *  <li>
 *  HETATM records are considered to be ligands, no matter what their chain affiliation.</li>
 *  <li>
 *  Waters are broken out into their own chain.</li>
 *  <li>
 *  HETATM records that have no chain id are assigned a pseudo-id, beginning with an underscore
 *  and having a sequential numeric component for the rest of the id.</li>
 *  <li>
 *  HETATM records that have a chain id corresponding to a previously defined chain id in the
 *  ATOM record set (protein atoms) are broken out into their own chain with an id of
 *  &lt;original id&gt;'</li>
 *  </ul>
 *  <p>
 *  21-Oct-08 - rickb</p>
 *  <p style="red">
 *  Note: The loader currently only responds to ATOM, HETATM records and the first MODEL encountered.
 *  In particular, CONECT and all the secondary structure stuff are completely ignored.<br/>
 *  The rest of the system is relying on either the dictionaries or
 *  internal calculations/determinations for bond information.
 *  10-Oct-08 - rickb</p>
 *  
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
	private boolean breakoutByResId = false;
	private Set<String> nonProteinChainIds = new TreeSet<String>();
	
	/**
	 * Set this if the models are part of a greater whole.
	 * This is normally set from a flag in the scene manager.
	 * 
	 * @param flag
	 */
	public void setTreatModelsAsSubunits(boolean flag) { treatModelsAsSubunits = flag; }
	
	/**
	 * Set this if you want to break out residues not belonging to a chain into
	 * their own pseudo-chains.  Psuedo-chains are identified by '_' prefix
	 * ('_A', '_B', etc.)
	 * 
	 * Waters are broken out into their own chain, as well.
	 * 
	 * Default is no.
	 * @param flag
	 */
	public void setBreakoutEmptyChainsByResId(boolean flag) { breakoutByResId = flag; }
	
	// A hashtable of vectors where
	// each hash KEY is the StructureComponent type String.
	// each hash VALUE is a Vector of StructureComponent objects.
	// NOTE: This protected variable is used only to enable each new
	// Structure object to access the newly loaded data. The Structure
	// constructor stores a copy of the object so this variable can
	// be re-used for the next load request. This is not thread-safe
	// but there doesn't seem to be any other way to enable an
	// anonymous class to be handed state (asside from parameters).
	protected Hashtable<ComponentType, Vector<StructureComponent>> passComponents = null;

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
		
		Vector<String> pdbChainIds = new Vector<String>();
		Vector<String> pdbResidueIds = new Vector<String>();
		Vector<String> ndbChainIds = new Vector<String>();
		Vector<Object> ndbResidueIds = new Vector<Object>();
		String previousResidueIdRaw = "";	// the untouched residue id.
		int previousResidueIdInt = Integer.MIN_VALUE;	// the residue id which was assigned to the Atom.residue_id field
		int previousResidueIdIntSimple = Integer.MIN_VALUE;	// the simple int conversion of the file's residue id, minus any letters.  

		SharedObjects sharedStrings = new SharedObjects( );
		Integer currentPseudoChainId = 0;

		this.passComponents = new Hashtable<ComponentType, Vector<StructureComponent>>( );
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
			
			boolean isHetAtom = line.startsWith("HETATM");

			// Parse the line buffer
			if (line.startsWith("ATOM") || isHetAtom)
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
									// see below to see how this gets modified

				if(treatModelsAsSubunits)
				{
					atom.chain_id = atom.chain_id + "$$$" + modelCount;
				}

				atom.chain_id = sharedStrings.share( atom.chain_id );
				//							System.out.println(modelCount);
				
				String newResidueIdRaw = line.substring(22, 28 ).trim();
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
				if ((increment == 0 && !previousResidueIdRaw.equals(newResidueIdRaw)))
					increment = 1;
						// if this isn't a simple number, need to check the string as well.

				if (previousResidueIdInt == Integer.MIN_VALUE)
				{
					newResidueIdInt = newResidueIdIntSimple;
					increment = 1;	// flag to make sure this chain/residue id pair is recorded.
				}
				
				else
					newResidueIdInt = previousResidueIdInt + increment;

				if (isHetAtom)
									// het atoms are explicitly non-protein chains, in the
									// current vernacular.  Break them out into pseudo chains.
				{
					if (atom.chain_id == null || atom.chain_id.equals(""))
					{						
						if (breakoutByResId)
						{
							if (atom.compound.equals("HOH"))
								atom.chain_id = "HOH";
							
							else
							{
								if (increment > 0) currentPseudoChainId++;
								atom.chain_id = "_" + currentPseudoChainId;
							}
						}
						
						else atom.chain_id = "_";
					}

					else if (pdbChainIds.contains(atom.chain_id))
								// uh-oh - 'embedded' (hetatoms defined same chain id as a protein.)
								// break out embedded chains, regardless
					{
						atom.chain_id += '\'';
								// signify new chain with a prime

						if (!pdbChainIds.contains(atom.chain_id))
							increment = 1;						
							// make sure the new id is added to the pdbChainIds
							// (just use increment as a flag, not an operation...)
					}
					
					nonProteinChainIds.add(atom.chain_id);
				}

				if (increment > 0)
				{			
					if (atom.chain_id.equals("HOH"))
						pdbChainIds.add("");
					else
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

				Vector<StructureComponent> records = this.passComponents.get(ComponentType.ATOM);
				
				if ( records == null )
				{
					records = new Vector<StructureComponent>( );
					this.passComponents.put(
							ComponentType.ATOM, records );
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
			this.passComponents.put( ComponentType.BOND, bonds );

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
			protected Hashtable<ComponentType, Vector<StructureComponent>> structureComponents = null;

			// To free up the global state for another load call.
			private String localUrlString;

			// public Structure()  Anonymous inner class constructor.
			{
				this.structureComponents = passComponents;
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

			@Override
			public int getStructureComponentCount( ComponentType scType )
			{
				Vector<StructureComponent> records = this.structureComponents.get( scType );
				if ( records == null ) {
					return 0;
				} else {
					return records.size( );
				}
			}

			@Override
			public StructureComponent getStructureComponentByIndex( ComponentType type,
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
	 * Not implemented. NPRIDS are chain ids comprised of HETATM records
	 */
	public Set<String> getNonProteinChainIds()
	{
		return nonProteinChainIds;
	}
	


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

