//  $Id: RelationFilter.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: RelationFilter.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.3  2004/04/09 00:07:08  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.2  2004/01/29 17:10:14  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.1  2002/10/26 00:17:41  moreland
//  Oops, I forgot to add this to CVS.
//
//


package org.rcsb.mbt.model.filters;


import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureComponentRelation;


/**
 *  Defines a StructureComponentFilter sub-class which handles the filtering
 *  of StructureComponent objects that are related to the StructureComponent
 *  subject parameter. This is used by the Structure class to process
 *  getStructureComponentRelations method calls.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.filters.IStructureComponentFilter
 *  @see	org.rcsb.mbt.model.StructureComponent
 *  @see	org.rcsb.mbt.model.StructureComponentIterator
 *  @see	org.rcsb.mbt.model.Structure
 */
public class RelationFilter
	implements IStructureComponentFilter
{
	// The relationship implementation object
	private StructureComponentRelation relation = null;

	// The relationship subject to be compared to objects
	private StructureComponent subject = null;

	/**
	 * Constructs a RelationFilter for the given StructureComponentRelation
	 * instance and StructureComponent subject parameters.
	 * <P>
	 */
	public RelationFilter( final StructureComponentRelation relation,
		final StructureComponent subject )
			throws IllegalArgumentException
	{
		this.relation = relation;
		this.subject = subject;
	}

	/**
	 *  Return the StructureComponentRegistry type for returned object types.
	 */
	public String type( )
	{
		return this.relation.getObjectType();
	}

	/**
	 *  Ask the relation object if the object should be accepted or not.
	 *  <P>
	 */
	public boolean accept( final StructureComponent structureComponent )
	{
		return this.relation.isRelated( this.subject, structureComponent );
	}
}

