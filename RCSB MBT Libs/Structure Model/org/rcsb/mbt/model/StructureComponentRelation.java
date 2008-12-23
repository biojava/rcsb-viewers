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

import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;

/**
 *  A class used to store a single relationship description between two
 *  StructureComponent types (much like a relational link between
 *  tables in a database). This class is used by the StructureComponentRegistry
 *  to define the relationship model for StructureComponent objects.
 *  <P>
 *  For example, the Conformation type relates to Atom type by means
 *  of an implied reference between the Conformation class's start_residue
 *  and end_residue fields, and the Atom class's residue_id field. A
 *  Conformation object therfore "relates" to a set of Atom values.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.StructureComponent
 *  @see	org.rcsb.mbt.model.StructureComponentRegistry
 */
public abstract class StructureComponentRelation
{
	/**
	 * The name of this Relation
	 */
	private String name = null;
	private ComponentType subject_type = null;
	private ComponentType object_type = null;

	/**
	 * Constructor.
	 */
	public StructureComponentRelation( final String name,
			final ComponentType subject_type,
			final ComponentType object_type )
	{
		this.name = name;
		this.subject_type = subject_type;
		this.object_type = object_type;
	}

	/**
	 * Returns the <I>name</I> of this StructureComponentRelation object.
	 */
	public final String getName()
	{
		return this.name;
	}

	/**
	 * Returns the <I>subject type</I> of this StructureComponentRelation
	 * object.
	 */
	public final ComponentType getSubjectType()
	{
		return this.subject_type;
	}

	/**
	 * Returns the <I>object type</I> of this StructureComponentRelation
	 * object.
	 */
	public final ComponentType getObjectType()
	{
		return this.object_type;
	}

	/**
	 * Tests to see if one StructureComponent instance is related to
	 * another. This method first checks to see if the subject and object
	 * types have a defined relation. Then a specific comparision to see
	 * if the instances are directly linked is performed by calling the
	 * abstract "isLinked" method.
	 * <P>
	 */
	public final boolean isRelated( final StructureComponent subject, final StructureComponent object )
	{
		// Are the subject and object related at all?
		if ( subject.getStructureComponentType() != this.subject_type ) {
			return false;
		}
		if ( object.getStructureComponentType() != this.object_type ) {
			return false;
		}

		// Are the subject and object linked?
		return this.isLinked( subject, object );
	}

	/**
	 * Tests to see if one StructureComponent instance is linked to
	 * another. This method is called by the parent class's "isRelated"
	 * method.
	 * <P>
	 * Example:
	 * <P>
	 * <PRE>
	 * StructureComponentRelation residue_relation =
	 *     new StructureComponentRelation( name, subject_type, object_type )
	 * {
	 *     abstract boolean isRelated( StructureComponent subject,
	 *         StructureComponent object )
	 *     {
	 *         // Cast the StructureComponent objects to the correct types.
	 *         Conformation conformation = (Conformation) subject;
	 *         Atom atom = (Atom) object;
	 *         
	 *         // Does the Atom's residue_id fall between the
	 *         // Conformation's start_residue and end_residue values?
	 *         if ( atom.residue_id < conformation.start_residue )
	 *             return false;
	 *         if ( atom.residue_id > conformation.end_residue )
	 *             return false;
	 *         return true;
	 *     }
	 * };
	 * </PRE>
	 * <P>
	 */
	protected abstract boolean isLinked( StructureComponent subject,
		StructureComponent object );
}

