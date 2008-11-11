//  $Id: Relation_Conformation_Atom.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Relation_Conformation_Atom.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.3  2004/04/09 00:17:00  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.2  2004/01/29 17:08:16  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.1  2002/10/24 17:54:01  moreland
//  Provides implementation for the improved Structure API/implementation.
//
//  Revision 1.0  2002/09/24 23:38:39  moreland
//

package org.rcsb.mbt.model;

import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;

/**
 *  This class represents a relationship between the
 *  Conformation and the Atom StructureComponent class.
 *  It links the Conformation's start_residue and end_residue
 *  fields with the Atom's residue_id field.
 *  <P>
 *  This class is used by the StructureComponentRegistry
 *  to define the relationship model for StructureComponent objects.
 *  <P>
 *  @author John L. Moreland
 */
public class Relation_Conformation_Atom
	extends StructureComponentRelation
{
	/**
	 * Constructs a relation between Conformation and Atom objects.
	 * <P>
	 */
	public Relation_Conformation_Atom( )
	{
		super( "Relation_Conformation_Atom",
			ComponentType.RELATION_CONFORMATION_ATOMS, ComponentType.ATOM );
	}

	/**
	 * Defines how a Conformation object is linked to an Atom object.
	 * <P>
	 */
	
	protected boolean isLinked( final StructureComponent subject,
		final StructureComponent object )
	{
		// Cast the StructureComponent objects to the correct types.
		final Conformation conformation = (Conformation) subject;
		final Atom atom = (Atom) object;
	
		// Does the Atom's chain_id fall between the
		// Conformation's start_chain and end_chain values?
		if ( atom.chain_id.compareTo( conformation.start_chain) < 0 ) {
			return false;
		}
		if ( atom.chain_id.compareTo( conformation.end_chain ) > 0 ) {
			return false;
		}
		 
		// Does the Atom's residue_id fall between the
		// Conformation's start_residue and end_residue values?
		if ( atom.residue_id < conformation.start_residue ) {
			return false;
		}
		if ( atom.residue_id > conformation.end_residue ) {
			return false;
		}

		return true;
	}
}

