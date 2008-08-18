//  $Id: Strand.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Strand.java,v $
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
//  Revision 1.6  2004/04/09 00:17:00  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.5  2004/01/31 19:11:48  moreland
//  Removed outdated programming note.
//
//  Revision 1.4  2004/01/29 17:08:16  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.3  2003/04/23 17:27:59  moreland
//  Removed StructureComponentID/scid and replaced it with a "structure" field
//  in the StructureComponent base class.
//  Changed "getType" method to "getStructureComponentType" to return dynamic
//  SC type (ie: class name).
//
//  Revision 1.2  2003/02/03 22:33:31  moreland
//  Updated comments to reflect the toolkit's treatment of "strand" VS "sheet".
//
//  Revision 1.1  2002/12/16 06:24:41  moreland
//  Changed code to support differentiation of Conformation into Coil, Helix,
//  Strand, and Turn sub-class types (at Eliot Clingman's suggestion).
//
//  Revision 1.1  2002/10/24 17:54:01  moreland
//  Provides implementation for the improved Structure API/implementation.
//
//  Revision 1.1.1.1  2002/07/16 18:00:18  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//


package org.rcsb.mbt.model;


/**
 *  Implements a StructureComponent container for Strand conformation
 *  (secondary structure) data.
 *  <P>
 *  IMPORTANT!: Each individual SHEET record in most data sources
 *  (eg: PDB and mmCIF files) really represents a single STRAND. And, it
 *  takes multiple SHEET records in order to fully specify a sheet structure.
 *  So, in this toolkit, we treat each individual sheet record as a Strand.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Structure
 *  @see	org.rcsb.mbt.model.Conformation
 *  @see	org.rcsb.mbt.model.StructureComponent
 */
public class Strand
	extends Conformation
	implements java.lang.Cloneable
{
	//
	// Constructor
	//

	/**
	 *  Creates a new Strand object.
	 */
	public Strand( )
	{
	}

	/**
	 *  This method returns the fully qualified name of this class as a String
	 *  object. The String object is guaranteed to be a reference to the
	 *  same String object for all instances of a given sub-class.
	 *  This is used in a number of places by the toolkit:
	 */
	private static String className = null;
	public static String getClassName()
	{
		if ( Strand.className == null ) {
			Strand.className = Strand.class.getName();
		}
		return Strand.className;
	}

	/**
	 *  This method returns the fully qualified name of this class.
	 */
	
	public String getStructureComponentType( )
	{
		return Strand.className;
	}

	//
	// StructureComponent methods
	//

	/**
	 *  Copy all of the field values from the parameter object into "this".
	 */
	
	public void copy( final StructureComponent structureComponent )
	{
		super.copy( structureComponent );
		// Strand strand = (Strand) structureComponent;
		// localField       = strand.localField;
	}

	/**
	 *  Clone this object.
	 */
	
	public Object clone( )
		throws CloneNotSupportedException
	{
		return super.clone( );
	}

	//
	// Strand fields
	//

	// None.
}

