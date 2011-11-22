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
package org.rcsb.mbt.model.attributes;


import java.util.Enumeration;
import java.util.Hashtable;


/**
 *  A class used to register BondColor implementation objects.
 *  The names of the BondColor objects can be retrieved and
 *  used in a GUI menu in order for a user to pick the desired
 *  BondColor algorithm. The retrieved BondColor object may then be
 *  handed to a StructureStyle instance in order to assocate one
 *  or more Bond representations to a given coloring scheme.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IBondColor
 *  @see	org.rcsb.mbt.model.attributes.StructureStyles
 */
public class BondColorRegistry
{
	// The registered BondColor objects
	private static final Hashtable bondColorObjects = new Hashtable( );

	// The default BondColor object name
	private static String defaultName = null;

	// Add the well-known BondColor implementation names.
	static
	{
		BondColorRegistry.add( BondColorByAtomColor.NAME, BondColorByAtomColor.create() );
		BondColorRegistry.add( BondColorByElement.NAME, BondColorByElement.create() );

		BondColorRegistry.defaultName = BondColorByAtomColor.NAME;
	}

	//
	// Registration methods
	//

	/**
	 *  Return the number of registered BondColor impelementations.
	 */
	public static int count( )
	{
		return BondColorRegistry.bondColorObjects.size( );
	}

	/**
	 *  Return the name of the default BondColor impelementation.
	 */
	public static String getDefaultName( )
	{
		return BondColorRegistry.defaultName;
	}

	/**
	 *  Add a new BondColor implementation.
	 */
	public static void add( final String name, final IBondColor bondColor )
	{
		BondColorRegistry.bondColorObjects.put( name, bondColor );
	}

	/**
	 *  Remove an existing BondColor implementation.
	 */
	public static void remove( final String name )
	{
		BondColorRegistry.bondColorObjects.remove( name );
	}

	/**
	 *  Get an BondColor implementation by name.
	 */
	public static IBondColor get( final String name )
	{
		return (IBondColor) BondColorRegistry.bondColorObjects.get( name );
	}

	/**
	 *  Get the default BondColor implementation.
	 */
	public static IBondColor getDefault( )
	{
		return (IBondColor) BondColorRegistry.bondColorObjects.get( BondColorRegistry.defaultName );
	}

	/**
	 *  Return an Enumeration of String values for all registered
	 *  BondColor objects.
	 */
	public static Enumeration names( )
	{
		return BondColorRegistry.bondColorObjects.keys( );
	}
}

