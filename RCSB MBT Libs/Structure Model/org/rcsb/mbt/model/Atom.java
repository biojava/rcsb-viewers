//  $Id: Atom.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Atom.java,v $
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
//  Revision 1.13  2005/11/08 20:58:11  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.12  2004/11/08 17:26:45  moreland
//  Made note that B-Factor (temperature) ranges from 0.0 to 100.0.
//
//  Revision 1.11  2004/10/27 20:03:30  moreland
//  Corrected javadoc SEE references.
//
//  Revision 1.10  2004/08/16 16:26:35  moreland
//  Corrected several comment blocks.
//
//  Revision 1.9  2004/05/10 17:56:58  moreland
//  Added support for alternate location identifier (atom occupancy < 1.0).
//
//  Revision 1.8  2004/04/09 00:17:00  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.7  2004/01/31 19:11:48  moreland
//  Removed outdated programming note.
//
//  Revision 1.6  2004/01/29 17:08:15  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.5  2003/04/30 17:48:14  moreland
//  Added atom serial number field.
//
//  Revision 1.4  2003/04/23 17:15:12  moreland
//  Removed StructureComponentID/scid and replaced it with a "structure" field
//  in the StructureComponent base class.
//  Changed "getType" method to "getStructureComponentType" to return dynamic
//  SC type (ie: class name).
//
//  Revision 1.3  2003/04/03 18:11:19  moreland
//  Changed field name "type" to "element" due to naming and meaning conflict.
//
//  Revision 1.2  2002/10/24 17:54:01  moreland
//  Provides implementation for the improved Structure API/implementation.
//
//  Revision 1.1.1.1  2002/07/16 18:00:18  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//


package org.rcsb.mbt.model;


/**
 *  Implements a StructureComponent container for atom data.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.StructureComponent
 *  @see	org.rcsb.mbt.model.Structure
 */
public class Atom
	extends StructureComponent
	implements java.lang.Cloneable
{

	//
	// Constructor
	//

	/**
	 *  Creates a new empty Atom object.
	 */
	public Atom( )
	{
	}

	//
	// StructureComponent methods
	//

	/**
	 *  Copy all of the field values from the parameter object into "this".
	 */
	public void copy( final StructureComponent structureComponent )
	{
		this.setStructure( structureComponent.getStructure() );
		final Atom atom = (Atom) structureComponent;

		this.element       = atom.element;
		this.name          = atom.name;
		this.number        = atom.number;
		this.altLoc        = atom.altLoc;
		this.compound      = atom.compound;
		this.chain_id      = atom.chain_id;
		this.residue_id    = atom.residue_id;
		this.coordinate[0] = atom.coordinate[0];
		this.coordinate[1] = atom.coordinate[1];
		this.coordinate[2] = atom.coordinate[2];
		this.occupancy     = atom.occupancy;
		this.bfactor       = atom.bfactor;
	}

	/**
	 *  Clone this object.
	 */
	public Object clone( )
		throws CloneNotSupportedException
	{
		return super.clone( );
	}

	/**
	 *  This method returns the fully qualified name of this class.
	 *  <P>
	 *  This name is used by the StructureComponentRegistry class to enable
	 *  dynamic registration and discovery of new StructureComponent
	 *  sub-classes/types. The name is also used to create a unique integer
	 *  indentifier for each type in order to make run-time type comparisons
	 *  fast.
	 *  
	 *  Notes:
	 *  Interesting notion - I don't see where it's used to actually create
	 *  an instance from the classname.  Seems storing simpleName and renaming
	 *  this to just getName() would work just as well and relieve a lot of
	 *  complication in StructureStyles.
	 *  
	 *  Leaving it, though.  Revisit later.
	 *  
	 *  rickb - 07-Aug-08
	 */
	public static String getClassName()
	{
		return Atom.class.getName();
	}

	/**
	 *  This method returns the fully qualified name of this class.
	 */
	public String getStructureComponentType( )
	{
		return Atom.getClassName();
	}

	//
	// Atom fields
	//

	/**
	 *  The element symbol.
	 *  For example, "C", "O", "N", "H", etc.
	 */
	public String element = null; // _atom_site.type_symbol

	/**
	 *  The macromolecular ID (element name).
	 *  For example, "CA", "CB", "OG1", "N", etc.
	 */
	public String name = null; // _atom_site.label_atom_id

	/**
	 *  The atom serial number.
	 */
	public int number = -1; // _atom_site.id

	/**
	 *  Compound 3-letter code.
	 *  For example, "VAL", "THR", "ILE", etc.
	 */
	public String compound = null; // _atom_site.label_comp_id


	/**
	 *  Chain/Asymetric unit ID.
	 *  For example, "A", "B", "C", etc.
	 */
	public String chain_id = null; // _atom_site.label_asym_id

	/**
	 *  Residue/Sequence ID.
	 *  Must be a positive integer.
	 *  For example, "10", "11", "13", etc.
	 */
	public int residue_id = -1; // _atom_site.label_seq_id

	/**
	 *  The x,y,z coordinate in angstroms.
	 *  For example, (21.023, 30.128, 12.340).
	 */
	public double coordinate[] = { 0.0f, 0.0f, 0.0f }; // _atom_site.Cartn_x,y,z

	/**
	 *  The fraction (0.0-1.0) of a given atom name that occurs in a residue
	 *  having alternate atom locations.
	 *  For example, "1.0", "0.5", "0.33", etc.
	 *  <P>
	 *  @see #altLoc
	 *  <P>
	 */
	public float occupancy = 0.0f; // _atom_site.occupancy

	/**
	 *  Alternate Location Identifier (single character)
	 *  should be specified when occupancy < 1.
	 *  For example, "", "A", "B", "C", etc.
	 *  <P>
	 *  @see #occupancy
	 *  <P>
	 */
	public String altLoc = null; // _atom_site.label_alt_id

	/**
	 *  Temperature B-Factor (0.0 - 100.0).
	 *  For example, "17.12", "26.28", etc.
	 */
	public float bfactor = 0.0f; // _atom_site.B_iso_or_equiv

	/**
	 *  Partial Charge (-1.0 - 3.0).
	 *  For example, "0.3", "1.1", etc.
	 */
	public float partialCharge = 0.0f; // _atom_site.???
}

