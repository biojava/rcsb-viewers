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
 *  A class used to register AtomLabel implementation objects.
 *  The names of the AtomLabel objects can be retrieved and
 *  used in a GUI menu in order for a user to pick the desired
 *  AtomLabel algorithm. The retrieved AtomLabel object may then be
 *  handed to a StructureStyle instance in order to assocate one
 *  or more Atom representations to a given label scheme.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IAtomLabel
 *  @see	org.rcsb.mbt.model.attributes.StructureStyles
 */
public class AtomLabelRegistry
{
	// The registered AtomLabel objects
	private static final Hashtable atomLabelObjects = new Hashtable( );

	// The default AtomLabel object name
	private static String defaultName = null;

	// Add the well-known AtomLabel implementation names.
	static
	{
		AtomLabelRegistry.add( AtomLabelNone.NAME, AtomLabelNone.create() );
		AtomLabelRegistry.add( AtomLabelByAtomName.NAME, AtomLabelByAtomName.create() );
		AtomLabelRegistry.add( AtomLabelByAtomElement.NAME, AtomLabelByAtomElement.create() );
		AtomLabelRegistry.add( AtomLabelByAtomCompound.NAME, AtomLabelByAtomCompound.create() );
		AtomLabelRegistry.add( AtomLabelByChainId.NAME, AtomLabelByChainId.create() );

		AtomLabelRegistry.defaultName = AtomLabelNone.NAME;
	}

	//
	// Registration methods
	//

	/**
	 *  Return the number of registered AtomLabel impelementations.
	 */
	public static int count( )
	{
		return AtomLabelRegistry.atomLabelObjects.size( );
	}

	/**
	 *  Return the name of the default AtomLabel impelementation.
	 */
	public static String getDefaultName( )
	{
		return AtomLabelRegistry.defaultName;
	}

	/**
	 *  Add a new AtomLabel implementation.
	 */
	public static void add( final String name, final IAtomLabel atomLabel )
	{
		AtomLabelRegistry.atomLabelObjects.put( name, atomLabel );
	}

	/**
	 *  Remove an existing AtomLabel implementation.
	 */
	public static void remove( final String name )
	{
		AtomLabelRegistry.atomLabelObjects.remove( name );
	}

	/**
	 *  Get an AtomLabel implementation by name.
	 */
	public static IAtomLabel get( final String name )
	{
		return (IAtomLabel) AtomLabelRegistry.atomLabelObjects.get( name );
	}

	/**
	 *  Get the default AtomLabel implementation.
	 */
	public static IAtomLabel getDefault( )
	{
		return (IAtomLabel) AtomLabelRegistry.atomLabelObjects.get( AtomLabelRegistry.defaultName );
	}

	/**
	 *  Return an Enumeration of String values for all registered
	 *  AtomLabel objects.
	 */
	public static Enumeration names( )
	{
		return AtomLabelRegistry.atomLabelObjects.keys( );
	}
}

