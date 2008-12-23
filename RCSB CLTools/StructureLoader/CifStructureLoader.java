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
package org.rcsb.mbt.structLoader;


// MBT

// OpenMMS
import org.rcsb.mbt.controllers.scene.PdbToNdbConverter;
import org.rcsb.mbt.glscene.geometry.UnitCell;
import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.util.*;
import org.rcsb.mbt.structLoader.CifStructureLoaderImpl.MbtBuilder;
import org.rcsb.mbt.structLoader.openmms.cifparse.*;

// Core
import java.io.*;
import java.net.*;
import java.util.*;


/**
 *  Implements the StructureLoader interface to enable reading of Cif files
 *  either from local disk or from a URL.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.structLoader.IStructureLoader
 *  @see	org.rcsb.mbt.structLoader.StructureFactory
 */
public class CifStructureLoader
	implements IFileStructureLoader
{
	protected String urlString = null;
	protected CifDictionary cifDictionary = null;
	protected CifParser cifParser = null;
	protected MbtBuilder mbtBuilder = null;
	protected Structure structure = null;

	// Tells the loader to use "label" fields instead of "auth" fields.
	// The "auth" values are old PDB style mappings for chain IDs,
	// residue IDs, and conformation mappings. The default is (true) to
	// use the improved/cleaned "label" mappings.
	private boolean useLabelFields = true;

	/**
	 * Given an MBT structure component type (as defined in the
	 * StructureComponentRegistry class), return the corresponding CIF
	 * category string.
	 */
	protected Hashtable mbtToCifType = null;

	/**
	 *  Constructs a CifStructureLoader object capable of subsequently
	 *  loading mmCIF files.
	 *  <P>
	 *  This constructor pre-loads the mmCIF dictionary from a file.
	 */
	public CifStructureLoader( )
		throws Exception
	{
		this.mbtToCifType = new Hashtable();

		// mbtToCifType.put( StructureComponentRegistry.TYPE_MODEL, "" );
		// mbtToCifType.put( StructureComponentRegistry.TYPE_ACTIVE_SITE, "" );
		// mbtToCifType.put( StructureComponentRegistry.TYPE_POLYMER, "" );
		// mbtToCifType.put( StructureComponentRegistry.TYPE_NONPOLYMER, "" );
		// mbtToCifType.put( StructureComponentRegistry.TYPE_RESIDUE, "" );
		// mbtToCifType.put( StructureComponentRegistry.TYPE_HBOND, "" );
		// mbtToCifType.put( StructureComponentRegistry.TYPE_DISULPHIDE, "" );
		// mbtToCifType.put( StructureComponentRegistry.TYPE_SALT_BRIGE, "" );

		this.mbtToCifType.put( StructureComponentRegistry.TYPE_ATOM, "_atom_site" );
		this.mbtToCifType.put( StructureComponentRegistry.TYPE_COIL, "_struct_conf_coil" );
		this.mbtToCifType.put( StructureComponentRegistry.TYPE_HELIX, "_struct_conf_helix" );
		this.mbtToCifType.put( StructureComponentRegistry.TYPE_STRAND, "_struct_sheet_range" );
		this.mbtToCifType.put( StructureComponentRegistry.TYPE_TURN, "_struct_conf_turn" );

		//
		// Load the CIF dictionaries.
		//

		final String dictionaries[] = { "cif_mm_V2.0.3.dic", "pdbx_exchange.dic" };
		String dictPath = null;
		this.cifDictionary = new CifDictionary( );
		this.cifParser = new CifParser( );
		try
		{
			// Read the CIF dictionaries.
			for ( int d=0; d<dictionaries.length; d++ )
			{
				dictPath = dictionaries[ d ];
				final CifTokenizer dictionaryTokenizer = new CifTokenizer( dictPath );
				this.cifParser.readDictionary( dictionaryTokenizer, this.cifDictionary, true );
				dictionaryTokenizer.close( );
			}
		}
		catch ( final IOException e )
		{
			throw new Exception( "CifStructureLoader: could not find dictionary " + dictPath );
		}
		catch ( final org.rcsb.mbt.structLoader.openmms.cifparse.CifParseException e )
		{
			throw new Exception( "CifStructureLoader: could not parse dictionary " + dictPath );
		}
	}


	/**
	 * Tells the loader to use "label" fields instead of "auth" fields.
	 * The "auth" values are old PDB style mappings for chain IDs,
	 * residue IDs, and conformation mappings. The default is (true) to
	 * use the improved/cleaned "label" mappings.
	 */
	public void setUseLabelFields( final boolean state )
	{
		this.useLabelFields = state;
	}


	/**
	 * Tells the loader to use "label" fields instead of "auth" fields.
	 * The "auth" values are old PDB style mappings for chain IDs,
	 * residue IDs, and conformation mappings. The default is (true) to
	 * use the improved/cleaned "label" mappings.
	 */
	public boolean getUseLabelFields( )
	{
		return this.useLabelFields;
	}


	//
	// StructureLoader interface methods
	//

	/**
	 * Returns the common name for the loader implementation.
	 * This is the string that might appear in a user-selectable menu.
	 */
	public String getLoaderName( )
	{
		return new String( "Cif Structure Loader" );
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
	public Structure load( final String dataPath )
	{
		if ( ! this.canLoad( dataPath ) ) {
			return null;
		}

		//
		// Load a cif data set.
		//

		try
		{
			final CifTokenizer dataTokenizer = new CifTokenizer( dataPath );
			final DataItemList dataItemList = this.cifDictionary.getDictionaryItemList( );
			this.mbtBuilder = new MbtBuilder( this.cifParser, dataItemList );
			this.cifParser.setBuilder( this.mbtBuilder );
			this.cifParser.readDataBlock( dataTokenizer, dataItemList );
			this.cifParser.setBuilder( null );
			dataTokenizer.close( );
		}
		catch ( final Exception e )
		{
			Status.output( Status.LEVEL_ERROR, "CifStructureLoader.load, exception " + e );
			return null;
		}
		catch ( final OutOfMemoryError oome )
		{
			Status.output( Status.LEVEL_ERROR, "CifStructureLoader.load, error " + oome );
			return null;
		}

		try
		{
			structure = new Structure()
			{
				String urlStr = null;
				Hashtable single_items = null;
				Hashtable multi_items = null;
				SharedObjects sharedStrings = new SharedObjects( );
				Hashtable blankChainIdFlags = new Hashtable( );

				// public Structure( )  // Anonymous inner class constructor.
				{
					this.urlStr = CifStructureLoader.this.urlString;
					CifStructureLoader.this.urlString = null;

					this.single_items = CifStructureLoader.this.mbtBuilder.getSingles( );

					// WARNING: Since the MbtBuilder's "keepLoop" method filters
					// what loop records will end up in the "getMultis" hash,
					// be mindful when loops need to be added/removed. You may
					// also need to add/remove loops in the MbtBuilder class!
					this.multi_items = CifStructureLoader.this.mbtBuilder.getMultis( );

					CifStructureLoader.this.mbtBuilder = null;

					//
					// Some mmCIF files that were derived from old PDB files
					// use a flag to indicate that the asym_id (chain id)
					// should be ignored and set to a default. If this flag
					// is set, then this chain id change should be carried
					// forward through for ALL cif records!
					//

					final Vector struct_asyms = (Vector)
						this.multi_items.get( "_struct_asym" );
					if ( struct_asyms != null )
					{
						for ( int i=0; i<struct_asyms.size(); i++ )
						{
							final Hashtable struct_asym =
								(Hashtable) struct_asyms.get( i );
							final String id = (String) struct_asym.get(
								"_struct_asym.id" );
							final String flag = (String) struct_asym.get(
								"_struct_asym.pdbx_blank_PDB_chainid_flag" );
							if ( flag.equals( "Y" ) ) {
								this.blankChainIdFlags.put( id, flag );
							// System.err.println( "JLM DEBUG, CifStructureLoader.load: " + id + " = " + flag );
							}
						}
					}

					//
					// Create and populate a StructureInfo object from the data.
					//

					final StructureInfo si = new StructureInfo( );
					String str = null;
					Vector records = null;

					str = (String) this.single_items.get( "_struct.entry_id" );
					si.setIdCode( str );

					str = (String) this.single_items.get( "_struct.pdbx_descriptor" );
					if ( (str != null) && (str.length() > 20) ) {
						str = str.substring( 0, 20 ) + "...";
					}
					si.setShortName( str );

					str = (String) this.single_items.get( "_struct.title" );
					si.setLongName( str );

					str = (String) this.single_items.get( "_database_PDB_rev.date" );
					si.setReleaseDate( str );

					str = null;
					records = (Vector) this.multi_items.get( "_citation_author" );
					if ( records != null )
					{
						for ( int i=0; i<records.size(); i++ )
						{
							final Hashtable citationRec = (Hashtable) records.get( i );
							final String author = (String) citationRec.get( "_citation_author.name" );
							if ( author != null )
							{
								if ( str == null ) {
									str = author;
								} else {
									str = str + ", " + author;
								}
							}
						}
					}
					si.setAuthors( str );

					str = (String) this.single_items.get( "_exptl.method" );
					si.setDeterminationMethod( str );

					this.setStructureInfo( si );

					// Since we have finished populating the StructureInfo
					// record, and we don't use the "single_items" hastable
					// for anything else, we can toss that un-used data.
					this.single_items.clear( );

					//
					// Walk through the atom records looking for multiple models.
					// Remove atom records from all but the first model.
					//

					final String scType = StructureComponentRegistry.TYPE_ATOM;
					final String cifName = (String) CifStructureLoader.this.mbtToCifType.get( scType );
					final Vector atoms = (Vector) this.multi_items.get( cifName );
					if ( atoms != null )
					{
						final int atomCount = atoms.size( );
						// Find the boundary between the 1st and 2nd models
						Hashtable atomRec = (Hashtable) atoms.get( 0 );
						if ( atomRec != null )
						{
							final String firstModel = (String)
								atomRec.get( "_atom_site.pdbx_PDB_model_num" );
							if ( firstModel != null )
							{
								for ( int a=atomCount-1; a>0; a-- )
								{
									atomRec = (Hashtable) atoms.get( a );
									if ( atomRec == null ) {
										continue;
									}
									final String model = (String)
										atomRec.get( "_atom_site.pdbx_PDB_model_num" );
									if ( model == null ) {
										continue;
									}
									if ( ! model.equals( firstModel ) )
									{
										atoms.remove( a );
									}
								}
							}
						}
					}
				}

				
				public String getUrlString( )
				{
					return this.urlStr;
				}

				
				public int getStructureComponentCount( final String scType )
				{
					final String cifType = (String) CifStructureLoader.this.mbtToCifType.get( scType );
					if ( cifType == null ) {
						return 0;
					}
					int count = 0;

					final Vector vec = (Vector) this.multi_items.get( cifType );
					if ( vec != null )
					{
						count = vec.size();
						if ( count > 0 ) {
							return count;
						}
					}

					final String singles = (String) this.single_items.get( cifType );
					if ( singles != null ) {
						return 1;
					}

					return 0;
				}

				private void getStructureComponentByIndex( int index,
					final StructureComponent structureComponent )
					throws IndexOutOfBoundsException, IllegalArgumentException
				{
					final String scType = structureComponent.getStructureComponentType( );
					final String cifName = (String) CifStructureLoader.this.mbtToCifType.get( scType );
					structureComponent.setStructure( this );

					try
					{
						if ( scType == StructureComponentRegistry.TYPE_ATOM )
						{
							final Vector atoms = (Vector) this.multi_items.get( cifName );
							final Hashtable hash = (Hashtable) atoms.get( index );
							final Atom atom = (Atom) structureComponent;
							String str;

							str = (String) hash.get( "_atom_site.id" );
							try {
								atom.number = Integer.parseInt( str );
							} catch ( final java.lang.NumberFormatException e ) {
								atom.number = -1;
							}

							atom.element = (String) hash.get( "_atom_site.type_symbol" );
							atom.element = this.sharedStrings.share( atom.element );

							if ( CifStructureLoader.this.useLabelFields ) {
								atom.name = (String) hash.get( "_atom_site.label_atom_id" );
							} else {
								atom.name = (String) hash.get( "_atom_site.auth_atom_id" );
							}
							// atom.name = (String) hash.get( "_atom_site.pdbx_auth_atom_name" );
							atom.name = this.sharedStrings.share( atom.name );

							atom.altLoc =
								(String) hash.get( "_atom_site.label_alt_id" );
							atom.altLoc = this.sharedStrings.share( atom.altLoc );

							atom.compound =
								(String) hash.get( "_atom_site.label_comp_id" );
							atom.compound = this.sharedStrings.share( atom.compound );

							if ( CifStructureLoader.this.useLabelFields ) {
								atom.chain_id =
									(String) hash.get( "_atom_site.label_asym_id" );
							} else {
								atom.chain_id =
									(String) hash.get( "_atom_site.auth_asym_id" );
							}
							// Is the blank chain id flag set for this chain?
							if ( this.blankChainIdFlags.get( atom.chain_id ) != null ) {
								atom.chain_id = StructureMap.defaultChainId;
							}

							atom.chain_id = this.sharedStrings.share( atom.chain_id );
							atom.residue_id = -1;
							try
							{
								if ( CifStructureLoader.this.useLabelFields ) {
									str = (String) hash.get( "_atom_site.label_seq_id" );
								} else {
									str = (String) hash.get( "_atom_site.auth_seq_id" );
								}
								atom.residue_id = Integer.parseInt( str );
							} catch ( final java.lang.NumberFormatException e2 ) {
								if ( CifStructureLoader.this.useLabelFields ) {
									str = (String) hash.get( "_atom_site.auth_seq_id" );
								} else {
									str = (String) hash.get( "_atom_site.label_seq_id" );
								}
								atom.residue_id = Integer.parseInt( str );
							}

							str = (String) hash.get( "_atom_site.Cartn_x" );
							atom.coordinate[0] = Double.parseDouble( str );

							str = (String) hash.get( "_atom_site.Cartn_y" );
							atom.coordinate[1] = Double.parseDouble( str );

							str = (String) hash.get( "_atom_site.Cartn_z" );
							atom.coordinate[2] = Double.parseDouble( str );

							str = (String) hash.get( "_atom_site.occupancy" );
							atom.occupancy = Float.parseFloat( str );

							str = (String) hash.get( "_atom_site.B_iso_or_equiv" );
							atom.bfactor = Float.parseFloat( str );

							// Free up memory for this cif record
							// since it should now be cached in scCache.
							atoms.set( index, null );
						}
						else if (
							(scType == StructureComponentRegistry.TYPE_COIL) ||
							(scType == StructureComponentRegistry.TYPE_HELIX) ||
							(scType == StructureComponentRegistry.TYPE_TURN)
						)
						{
							final Vector conformations = (Vector) this.multi_items.get( cifName );
							final Hashtable hash = (Hashtable) conformations.get( index );
							final Conformation conformation = (Conformation) structureComponent;
							String str;

							str = (String) hash.get( "_struct_conf.id" );
							final String lstr = str.toLowerCase();
							conformation.name = str;

							if ( scType == StructureComponentRegistry.TYPE_HELIX )
							{
								str = (String) hash.get( "_struct_conf.conf_type_id" );
								final Helix helix = (Helix) conformation;
								helix.setRighthand( str.startsWith( "HELX_RH" ) );
							}

							if ( CifStructureLoader.this.useLabelFields ) {
								conformation.start_compound =
									(String) hash.get( "_struct_conf.beg_label_comp_id" );
							} else {
								conformation.start_compound =
									(String) hash.get( "_struct_conf.beg_auth_comp_id" );
							}
							conformation.start_compound = this.sharedStrings.share( conformation.start_compound );

							if ( CifStructureLoader.this.useLabelFields ) {
								conformation.start_chain =
									(String) hash.get( "_struct_conf.beg_label_asym_id" );
							} else {
								conformation.start_chain =
									(String) hash.get( "_struct_conf.beg_auth_asym_id" );
							}
							// Is the blank chain id flag set for this chain?
							if ( this.blankChainIdFlags.get( conformation.start_chain ) != null ) {
								conformation.start_chain = StructureMap.defaultChainId;
							}
							conformation.start_chain = this.sharedStrings.share( conformation.start_chain );

							if ( CifStructureLoader.this.useLabelFields ) {
								str = (String) hash.get( "_struct_conf.beg_label_seq_id" );
							} else {
								str = (String) hash.get( "_struct_conf.beg_auth_seq_id" );
							}

							conformation.start_residue = Integer.parseInt( str );

							if ( CifStructureLoader.this.useLabelFields ) {
								conformation.end_compound =
									(String) hash.get( "_struct_conf.end_label_comp_id" );
							} else {
								conformation.end_compound =
									(String) hash.get( "_struct_conf.end_auth_comp_id" );
							}
							conformation.end_compound = this.sharedStrings.share( conformation.end_compound );

							if ( CifStructureLoader.this.useLabelFields ) {
								conformation.end_chain =
									(String) hash.get( "_struct_conf.end_label_asym_id" );
							} else {
								conformation.end_chain =
									(String) hash.get( "_struct_conf.end_auth_asym_id" );
							}
							// Is the blank chain id flag set for this chain?
							if ( this.blankChainIdFlags.get( conformation.end_chain ) != null ) {
								conformation.end_chain = StructureMap.defaultChainId;
							}
							conformation.end_chain = this.sharedStrings.share( conformation.end_chain );

							if ( CifStructureLoader.this.useLabelFields ) {
								str = (String) hash.get( "_struct_conf.end_label_seq_id" );
							} else {
								str = (String) hash.get( "_struct_conf.end_auth_seq_id" );
							}
							conformation.end_residue = Integer.parseInt( str );

							// Free up memory for this cif record
							// since it should now be cached in scCache.
							conformations.set( index, null );
						}
						else if ( scType == StructureComponentRegistry.TYPE_STRAND )
						{
							final Vector conformations = (Vector) this.multi_items.get( cifName );
							final Hashtable hash = (Hashtable) conformations.get( index );
							final Conformation conformation = (Conformation) structureComponent;
							String str;

							str = (String) hash.get( "_struct_sheet_range.id" );
							final String lstr = str.toLowerCase();
							conformation.name = str;
							conformation.name = this.sharedStrings.share( conformation.name );

							if ( CifStructureLoader.this.useLabelFields ) {
								conformation.start_compound =
									(String) hash.get( "_struct_sheet_range.beg_label_comp_id" );
							} else {
								conformation.start_compound =
									(String) hash.get( "_struct_sheet_range.beg_auth_comp_id" );
							}
							conformation.start_compound = this.sharedStrings.share( conformation.start_compound );

							if ( CifStructureLoader.this.useLabelFields ) {
								conformation.start_chain =
									(String) hash.get( "_struct_sheet_range.beg_label_asym_id" );
							} else {
								conformation.start_chain =
									(String) hash.get( "_struct_sheet_range.beg_auth_asym_id" );
							}
							// Is the blank chain id flag set for this chain?
							if ( this.blankChainIdFlags.get( conformation.start_chain ) != null ) {
								conformation.start_chain = StructureMap.defaultChainId;
							}
							conformation.start_chain = this.sharedStrings.share( conformation.start_chain );

							if ( CifStructureLoader.this.useLabelFields ) {
								str = (String) hash.get( "_struct_sheet_range.beg_label_seq_id" );
							} else {
								str = (String) hash.get( "_struct_sheet_range.beg_auth_seq_id" );
							}
							conformation.start_residue = Integer.parseInt( str );

							if ( CifStructureLoader.this.useLabelFields ) {
								conformation.end_compound =
									(String) hash.get( "_struct_sheet_range.end_label_comp_id" );
							} else {
								conformation.end_compound =
									(String) hash.get( "_struct_sheet_range.end_auth_comp_id" );
							}
							conformation.end_compound = this.sharedStrings.share( conformation.end_compound );

							if ( CifStructureLoader.this.useLabelFields ) {
								conformation.end_chain =
									(String) hash.get( "_struct_sheet_range.end_label_asym_id" );
							} else {
								conformation.end_chain =
									(String) hash.get( "_struct_sheet_range.end_auth_asym_id" );
							}
							// Is the blank chain id flag set for this chain?
							if ( this.blankChainIdFlags.get( conformation.end_chain ) != null ) {
								conformation.end_chain = StructureMap.defaultChainId;
							}
							conformation.end_chain = this.sharedStrings.share( conformation.end_chain );

							if ( CifStructureLoader.this.useLabelFields ) {
								str = (String) hash.get( "_struct_sheet_range.end_label_seq_id" );
							} else {
								str = (String) hash.get( "_struct_sheet_range.end_auth_seq_id" );
							}

							conformation.end_residue = Integer.parseInt( str );

							// Free up memory for this cif record
							// since it should now be cached in scCache.
							conformations.set( index, null );
						}
						else
						{
							throw new IllegalArgumentException( "unsupported type" );
						}
					}
					catch ( final java.lang.NumberFormatException e )
					{
						e.printStackTrace( );
						throw new IllegalArgumentException(
							"Bad number in " + scType + " " +
							+ index + " in " +
							this.urlStr + ": " + e.toString() );
					}
				}

				private final Hashtable scCache = new Hashtable( );
				
				public StructureComponent getStructureComponentByIndex( final String type,
					final int index )
					throws IndexOutOfBoundsException, IllegalArgumentException
				{
					Vector records = (Vector) this.scCache.get( type );
					if ( records == null )
					{
						final int scCount = this.getStructureComponentCount( type );
						records = new Vector( scCount );
						// Init each element to null so we can check cache later.
						for ( int i=0; i<scCount; i++ ) {
							records.add( null );
						}
						this.scCache.put( type, records );
					}

					// Check cache first
					StructureComponent structureComponent =
						(StructureComponent) records.elementAt( index );
					if ( structureComponent != null ) {
						return structureComponent;
					}

					//
					// There's no matching StructureComponent in the cache,
					// so create a new StructureComponent, populate the record,
					// and cache it.
					//

					// Dynamically create a new StructureComponent subclass
					// from the type.
					try
					{
						structureComponent = (StructureComponent)
							Class.forName(type).newInstance( );
					}
					catch ( final Exception e )
					{
						return null;
					}

					// Cache the record.
					records.setElementAt( structureComponent, index );
					// Fill in the StructureComponent fields.
					this.getStructureComponentByIndex( index, structureComponent );
					// JLM DEBUG: We should also toss the raw cif data somehow...

					return structureComponent;
				}
			};
		}
		catch ( final Exception e )
		{
			structure = null;
			Status.output( Status.LEVEL_ERROR, "CifStructureLoader.load, exception " + e );
		}
		catch ( final OutOfMemoryError oome )
		{
			structure = null;
			Status.output( Status.LEVEL_ERROR, "CifStructureLoader.load, error " + oome );
		}

		this.mbtBuilder = null; // Toss the builder.

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

		boolean goodPrefix = false;
		if ( name.startsWith( "file:" ) ) {
			goodPrefix = true;
		}
		if ( name.startsWith( "http:" ) ) {
			goodPrefix = true;
		}
		if ( name.startsWith( "ftp:" ) ) {
			goodPrefix = true;
		}

		boolean goodSuffix = false;
		if ( name.endsWith( ".cif" ) ) {
			goodSuffix = true;
		}
		if ( name.endsWith( ".cif.zip" ) ) {
			goodSuffix = true;
		}
		if ( name.endsWith( ".cif.gz" ) ) {
			goodSuffix = true;
		}
		if ( name.endsWith( ".cif.Z" ) ) {
			goodSuffix = true;
		}

		if ( goodPrefix && goodSuffix ) {
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
		structure = null;
		try
		{
			structure = this.load( file.toURL().toExternalForm() );
		}
		catch ( final Exception e )
		{
			structure = null;
			Status.output( Status.LEVEL_ERROR, "CifStructureLoader.load, exception " + e );
		}
		catch ( final OutOfMemoryError oome )
		{
			structure = null;
			Status.output( Status.LEVEL_ERROR, "CifStructureLoader.load, error " + oome );
		}

		return structure;
	}


	/**
	 * Returns true if the Structure can be read from the given File object.
	 */
	public boolean canLoad( final File file )
	{
		try
		{
			return this.canLoad( file.toURL().toExternalForm() );
		}
		catch ( final Exception e )
		{
			return false;
		}
	}


	/**
	 * Returns a reference to a Structure read from the given URL object.
	 */
	public Structure load( final URL url )
	{
		structure = null;
		try
		{
			structure = this.load( url.toExternalForm() );
		}
		catch ( final Exception e )
		{
			structure = null;
			Status.output( Status.LEVEL_ERROR, "CifStructureLoader.load, exception " + e );
		}
		catch ( final OutOfMemoryError oome )
		{
			structure = null;
			Status.output( Status.LEVEL_ERROR, "CifStructureLoader.load, error " + oome );
		}

		return structure;
	}


	/**
	 * Returns true if the Structure can be read from the given URL object.
	 */
	public boolean canLoad( final URL url )
	{
		return this.canLoad( url.toExternalForm( ) );
	}


	public PdbToNdbConverter getIDConverter() {
		return null;
	}


	public String[] getNonProteinChainIds() {
		return null;
	}


	public Structure getStructure() {
		return structure;
	}


	public UnitCell getUnitCell() {
		return null;
	}
}

