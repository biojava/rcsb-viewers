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


import java.util.*;


/**
 *  A class used to register ResidueColor implementation objects.
 *  The names of the ResidueColor objects can be retrieved and
 *  used in a GUI menu in order for a user to pick the desired
 *  ResidueColor algorithm. The retrieved ResidueColor object may then be
 *  handed to a StructureStyle instance in order to assocate one
 *  or more Residue representations to a given coloring scheme.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IResidueColor
 *  @see	org.rcsb.mbt.model.attributes.StructureStyles
 */
public class ResidueColorRegistry
{
	// The registered ResidueColor objects
	private static final Hashtable residueColorObjects = new Hashtable( );

	// The default ResidueColor object name
	private static String defaultName = null;

	// Add the well-known ResidueColor implementation names.
	static
	{
		ResidueColorRegistry.add( ResidueColorByHydrophobicity.NAME, ResidueColorByHydrophobicity.create() );
		ResidueColorRegistry.add( ResidueColorByResidueIndex.NAME, ResidueColorByResidueIndex.create() );
		ResidueColorRegistry.add( ResidueColorByFragmentType.NAME, ResidueColorByFragmentType.create() );
		ResidueColorRegistry.add( "Red", new ResidueColorByRgb( 1.0f, 0.0f, 0.0f ) );
		ResidueColorRegistry.add( "Green", new ResidueColorByRgb( 0.0f, 1.0f, 0.0f ) );
		ResidueColorRegistry.add( "Blue", new ResidueColorByRgb( 0.0f, 0.0f, 1.0f ) );
		ResidueColorRegistry.add( "Cyan", new ResidueColorByRgb( 0.0f, 1.0f, 1.0f ) );
		ResidueColorRegistry.add( "Magenta", new ResidueColorByRgb( 1.0f, 0.0f, 1.0f ) );
		ResidueColorRegistry.add( "Yellow", new ResidueColorByRgb( 1.0f, 1.0f, 0.0f ) );
		ResidueColorRegistry.add( "Gray", new ResidueColorByRgb( 0.5f, 0.5f, 0.5f ) );
		ResidueColorRegistry.add( "White", new ResidueColorByRgb( 1.0f, 1.0f, 1.0f ) );
		ResidueColorRegistry.add( "Black", new ResidueColorByRgb( 0.0f, 0.0f, 0.0f ) );

		ResidueColorRegistry.defaultName = ResidueColorByResidueIndex.NAME;
	}

	//
	// Registration methods
	//

	/**
	 *  Return the number of registered ResidueColor impelementations.
	 */
	public static int count( )
	{
		return ResidueColorRegistry.residueColorObjects.size( );
	}

	/**
	 *  Return the name of the default ResidueColor impelementation.
	 */
	public static String getDefaultName( )
	{
		return ResidueColorRegistry.defaultName;
	}

	/**
	 *  Add a new ResidueColor implementation.
	 */
	public static void add( final String name, final IResidueColor residueColor )
	{
		ResidueColorRegistry.residueColorObjects.put( name, residueColor );
	}

	/**
	 *  Remove an existing ResidueColor implementation.
	 */
	public static void remove( final String name )
	{
		ResidueColorRegistry.residueColorObjects.remove( name );
	}

	/**
	 *  Get an ResidueColor implementation by name.
	 */
	public static IResidueColor get( final String name )
	{
		return (IResidueColor) ResidueColorRegistry.residueColorObjects.get( name );
	}

	/**
	 *  Get the default ResidueColor implementation.
	 */
	public static IResidueColor getDefault( )
	{
		return (IResidueColor) ResidueColorRegistry.residueColorObjects.get( ResidueColorRegistry.defaultName );
	}

	/**
	 *  Return an Enumeration of String values for all registered
	 *  ResidueColor objects.
	 */
	public static Enumeration names( )
	{
		return ResidueColorRegistry.residueColorObjects.keys( );
	}
}

