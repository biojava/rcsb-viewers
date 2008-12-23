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
package org.rcsb.mbt.model;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 *  A class used to register StructureComponent types and
 *  their relationships (much like a database schema defines tables and
 *  their relational links).
 *  <P>
 *  Each StructureComponent is like a table in a database.
 *  Each StructureComponent <I>type</I> is like a table ID in a database.
 *  Each StructureComponent <I>relation</I> is like a relational link
 *  between tables in a database.
 *  <P>
 *  A set of <I>public static final String</I> variables whos names begin
 *  with "TYPE_" are defined to enable users (eg: StructureLoader, Structure,
 *  etc) to perform very fast StructureComponent runtime type comparisions by
 *  simply comparing object references (ie: not the actual String values).
 *  <P>
 *  Since each StructureComponent type value is simply the fully qualified
 *  String name of the class, the type values may also be used 
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.StructureComponent
 *  
 * 
 *  TODO:  This whole thing should be reworked to use enums.
 *  
 *  07-Aug-08 - rickb
 */
public class StructureComponentRegistry
{
	// The StructureComponent relations.
	private static final Hashtable<String, StructureComponentRelation> relations =
		new Hashtable<String, StructureComponentRelation>();

	//
	// TYPE_ field values
	//

	public enum ComponentType
	{
		NONE,
    	STRUCTURE,
		CHAIN,
		COIL,
		HELIX,
		STRAND,
		TURN,
		UNDEFINED_CONFORMATION,
		RESIDUE,
		FRAGMENT,
		ATOM,
		BOND,
		MODEL,
		SYMMETRY,
		SURFACE,
		ACTIVE_SITE,
		POLYMER,
		NONPOLYMER,
		DISULPHIDE,
		SALT_BRIDGE,
		RELATION_CONFORMATION_ATOMS,
		EXTENDED;
		
		public boolean isConformationType() { return this.ordinal() >= COIL.ordinal() &&
											  ordinal() < UNDEFINED_CONFORMATION.ordinal(); }
	}

	//
	// RELATION_ field values
	//


	// Since this is a static class, do initialization in a static block:
    static
    {
		//
		// Add the well-known StructureComponent relations.
		//
		StructureComponentRegistry.addRelation( new Relation_Conformation_Atom() );

		// Add other StructureComponent relations as they're implemented...
	}

	//
	// Type registration methods
	//

	/**
	 *  Return the number of registered StructureComponent types.
	 */
	public static int getTypeCount()
	{
		return ComponentType.values().length;
	}

	//
	// Relation registration methods
	//

	/**
	 *  Return the number of registered StructureComponent relations.
	 */
	public static int getRelationCount()
	{
		return StructureComponentRegistry.relations.size();
	}

	/**
	 *  Register a new StructureComponent relation.
	 */
	public static void addRelation( final StructureComponentRelation relation )
	{
		StructureComponentRegistry.relations.put( relation.getName(), relation );
	}

	/**
	 *  Get an existing StructureComponent relation.
	 */
	public static StructureComponentRelation getRelation( final String name )
	{
		return (StructureComponentRelation) StructureComponentRegistry.relations.get( name );
	}


	/**
	 *  Un-register an existing StructureComponent relation.
	 */
	public static void removeRelation( final String name )
	{
		StructureComponentRegistry.relations.remove( name );
	}

	/**
	 *  Return an Enumeration of String values for all registered
	 *  StructureComponent relations.
	 */
	public static Enumeration<String> getRelationNames( )
	{
		return StructureComponentRegistry.relations.keys( );
	}
}

