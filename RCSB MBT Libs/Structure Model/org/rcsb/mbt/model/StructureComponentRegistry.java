//  $Id: StructureComponentRegistry.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: StructureComponentRegistry.java,v $
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
//  Revision 1.9  2004/05/04 23:45:12  moreland
//  Moved StructureInfo to an adornment object of the Structure base class.
//
//  Revision 1.8  2004/05/04 23:14:11  moreland
//  Added StructureInfo class to registry list.
//
//  Revision 1.7  2004/04/09 00:17:01  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.6  2004/01/29 17:08:16  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.5  2003/07/21 20:48:48  moreland
//  Added Fragment StructureComponent object.
//
//  Revision 1.4  2003/04/03 22:37:09  moreland
//  Added Chain and Bond classes.
//
//  Revision 1.3  2003/01/13 23:41:24  moreland
//  Commented out debug/print statements.
//
//  Revision 1.2  2002/12/16 06:25:53  moreland
//  Changed code to support differentiation of Conformation into Coil, Helix,
//  Strand, and Turn sub-class types (at Eliot Clingman's suggestion).
//
//  Revision 1.1  2002/10/24 17:54:01  moreland
//  Provides implementation for the improved Structure API/implementation.
//
//  Revision 1.1.1.1  2002/07/16 18:00:15  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//

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

