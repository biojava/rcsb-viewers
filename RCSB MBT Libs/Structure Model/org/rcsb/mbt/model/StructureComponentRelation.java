//  $Id: StructureComponentRelation.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: StructureComponentRelation.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.4  2004/04/09 00:17:01  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.3  2004/01/29 17:08:16  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.2  2003/04/23 17:40:15  moreland
//  Removed StructureComponentID/scid and replaced it with a "structure" field
//  in the StructureComponent base class.
//  Changed "getType" method to "getStructureComponentType" to return dynamic
//  SC type (ie: class name).
//
//  Revision 1.1  2002/10/24 17:54:01  moreland
//  Provides implementation for the improved Structure API/implementation.
//
//  Revision 1.0  2002/09/24 23:38:39  moreland
//

package org.rcsb.mbt.model;

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
	private String subject_type = null;
	private String object_type = null;

	/**
	 * Constructor.
	 */
	public StructureComponentRelation( final String name, final String subject_type,
		final String object_type )
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
	public final String getSubjectType()
	{
		return this.subject_type;
	}

	/**
	 * Returns the <I>object type</I> of this StructureComponentRelation
	 * object.
	 */
	public final String getObjectType()
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
	public final boolean isRelated( final StructureComponent subject,
		final StructureComponent object )
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

