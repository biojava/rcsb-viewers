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
package org.rcsb.mbt.structLoader.CifStructureLoaderImpl;

import java.util.*;

import org.rcsb.mbt.structLoader.openmms.cifparse.*;

/**
 *  Provides the builder implementation for parsing cif data into MBT
 *  containers.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.structLoader.CifStructureLoader
 */
public class MbtBuilder
	implements Builder
{
	CifParser cifParser;
	DataItemList dataItemList;

	// A set of all the single key-value pairs
	// (a hash of Strings).
	Hashtable single_values = new Hashtable( );

	// A set of all the multi-value key-value pairs
	// (a hashtable of vectors of hashtables of Strings).
	Hashtable multi_values = new Hashtable( );

	String lastKey = null;
	String lastValue = null;
	Vector lastVector = null;
	Hashtable lastHashtable = null;

	public MbtBuilder( final CifParser cp, final DataItemList dil )
	{
		this.cifParser = cp;
		this.dataItemList = dil;
	}

	public void readDataBlock( final String id, final CifTokenizer ct )
		throws CifParseException
	{
		// System.err.println( "MbtBuilder.readDataBlock" );
		this.cifParser.readDataBlock( ct, this.dataItemList );
	}

	public Hashtable getSingles( )
	{
		return this.single_values;
	}

	private boolean differentiatedConformations = false;
	public Hashtable getMultis( )
	{
		// Lets differentiate the _struct_conf records into
		// specific coil, helix, strand, and turn conformation records
		// eg: "_struct_conf" is differentiated into
		//     "_struct_conf_coil"
		//     "_struct_conf_helix"
		//     "_struct_conf_strand"
		//     "_struct_conf_turn"
		// Note that the original _struct_conf records are left in place
		// for compatibility.
		if ( ! this.differentiatedConformations )
		{
			this.differentiatedConformations = true;
			final Vector conformations = (Vector) this.multi_values.get( "_struct_conf" );
			if ( conformations != null )
			{
				final int count = conformations.size();
				for ( int i=0; i<count; i++ )
				{
					final Hashtable hash = (Hashtable) conformations.get( i );
					String type = (String) hash.get( "_struct_conf.conf_type_id" );
					type = type.toLowerCase();

					String conf_type = null;
					if ( type.startsWith( "coil" ) ) {
						conf_type = "_struct_conf_coil";
					} else if ( type.startsWith( "helx" ) ) {
						conf_type = "_struct_conf_helix";
					} else if ( type.startsWith( "strand" ) ) {
						conf_type = "_struct_conf_strand";
					} else if ( type.startsWith( "turn" ) ) {
						conf_type = "_struct_conf_turn";
					}
					if ( conf_type == null ) {
						continue;
					}

					Vector diffConfVector =
						(Vector) this.multi_values.get( conf_type );
					if ( diffConfVector == null )
					{
						diffConfVector = new Vector( );
						this.multi_values.put( conf_type, diffConfVector );
					}

					diffConfVector.add( hash );
				}
			}
		}

		return this.multi_values;
	}

	//
	// Builder methods
	//

	/******************************  COMPOUNDS  ******************************/

	public void beginCompound()
		throws CifParseException
	{
		// System.err.println( "MbtBuilder.beginCompound" );
	}

	public void endCompound()
		throws CifParseException
	{
		// System.err.println( "MbtBuilder.endCompound" );
	}

	/****************************  SINGLE ITEMS  *****************************/

	public void setSingleItem( final DataItem di )
		throws CifParseException
	{
		// System.err.println( "MbtBuilder.setSingleValue = " + lastKey );
	}

	public void insertSingleValue( final DataItem di, final String val )
		throws CifParseException
	{
		this.lastKey = di.getItemName( );
		this.lastValue = val;
		this.single_values.put( this.lastKey, this.lastValue );
		// System.err.println( "MbtBuilder.insertSingleValue: " + lastKey + " = " + lastValue );
	}

	/********************************  LOOPS  ********************************/

	private final Runtime runtime = Runtime.getRuntime( );

	public void beginLoop()
		throws CifParseException
	{
	/*
		// Check to see if the parser is filling up memory with String tokens.
		// If so, try to tell the garbage collector to clean up.
		//
		// It has been observed (particularly under Windows-XP) that the CifParser
		// generates so many String objects so quickly that the garbage collector
		// does not have sufficient time to run in order to clean up!
		//
		float freeMemory = (float) runtime.freeMemory( );
		// float maxMemory = (float) runtime.maxMemory( );
		// float memRatio = freeMemory / maxMemory;
		// if ( memRatio < 0.2 )
		if ( freeMemory < 100000.0f )
		{
			org.rcsb.mbt.util.Status.output(
				org.rcsb.mbt.util.Status.LEVEL_WARNING,
				"CifStructureLoader.MbtBuilder: garbage collect attempt" );
			// System.err.println( "CifStructureLoader.MbtBuilder: garbage collect free = " + freeMemory + ", max = " + maxMemory + ", ratio = " + memRatio );
			System.gc( );
			try
			{
				// Give the garbage collector some time to run.
				// Observation shows that gc usually takes < 0.01 seconds,
				// but if we're running out of memory, lets be generous!
				Thread.sleep( 100 ); // sleep 0.1 seconds
			}
			catch( java.lang.InterruptedException e )
			{
				// There's nothing we can do about the sleep failing.
			}
		}
	*/

		this.lastVector = new Vector( );
		// System.err.println( "MbtBuilder.beginLoop" );
	}

	public void endLoop()
		throws CifParseException
	{
		String loop_name = null;
		if ( this.lastVector.size() > 0 )
		{
			loop_name = this.lastKey.substring( 0, this.lastKey.indexOf(".") );
			if ( this.keepLoop( loop_name ) ) {
				this.multi_values.put( loop_name, this.lastVector );
			}
		}
		this.lastVector = null;
		// System.err.println( "MbtBuilder.endLoop " + loop_name );
	}

	/**
	 *  Only keep loop records that MBT cares about.
	 *  <P>
	 *  WARNING: Since this limits what the CifStructureLoader class
	 *  can ever access, be mindful when entries added/removed, as well
	 *  as when the CifStructureLoader needs to add/remove support.
	 */
	private boolean keepLoop( final String loopName )
	{
		if ( loopName.equals( "_citation_author" ) ) {
			return true;
		}
		if ( loopName.equals( "_atom_site" ) ) {
			return true;
		}
		if ( loopName.equals( "_struct_conf" ) ) {
			return true;
		}
		if ( loopName.equals( "_struct_sheet_range" ) ) {
			return true;
		}
		if ( loopName.equals( "_struct_asym" ) ) {
			return true;
		}
		return false;
	}

	public void beginRow()
		throws CifParseException
	{
		this.lastHashtable = new Hashtable( );

		// System.err.println( "  MbtBuilder.beginRow" );
	}

	public void endRow()
		throws CifParseException
	{
		if ( this.lastHashtable.size() > 0 ) {
			this.lastVector.add( this.lastHashtable );
		}
		this.lastHashtable = null;
		// System.err.println( "  MbtBuilder.endRow" );
	}

	public void setLoopItem( final int column, final DataItem di )
		throws CifParseException
	{
		// String iname = di.getItemName();
		// lastKey = iname;

		// System.err.println( "MbtBuilder.setLoopItem = " + iname );
	}

	public void insertLoopValue( final int column, final DataItem di, final String val )
		throws CifParseException
	{
		if ( di == null ) {
			return;
		}
		if ( val == null ) {
			return;
		}

		this.lastKey = di.getItemName();
		if ( this.lastKey == null ) {
			return;
		}
		this.lastValue = val;
		this.lastHashtable.put( this.lastKey, this.lastValue );

		// System.err.println( "    MbtBuilder.insertLoopValue (col " + column + ") " + lastKey + " = " + lastValue );
	}
}
