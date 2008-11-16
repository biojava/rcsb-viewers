//  $Id: Conformation.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Conformation.java,v $
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
//  Revision 1.8  2004/04/09 00:17:00  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.7  2004/01/31 19:11:48  moreland
//  Removed outdated programming note.
//
//  Revision 1.6  2004/01/29 17:08:15  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.5  2003/04/25 00:57:02  moreland
//  Changed TYPE_UNDEFINED string value so it is more intuitive.
//
//  Revision 1.4  2003/04/24 17:12:55  moreland
//  Added an "undefined" conformation type string in order to support
//  secondary structure conformation fragment assignments in the Chain class.
//
//  Revision 1.3  2003/04/23 17:22:06  moreland
//  Removed unused constructor.
//
//  Revision 1.2  2002/12/16 06:24:41  moreland
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


package org.rcsb.mbt.model.interim;

import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;


/**
 *  Implements a an abstract StructureComponent container for conformation
 *  (secondary structure) data.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Structure
 *  @see	org.rcsb.mbt.model.StructureComponent
 */
public abstract class Conformation
	extends StructureComponent
	implements java.lang.Cloneable
{
	//
	// StructureComponent methods
	//

	/**
	 *  Copy all of the field values from the parameter object into "this".
	 */
	
	public void copy( final StructureComponent structureComponent )
	{
		final Conformation conformation = (Conformation) structureComponent;
		this.name           = conformation.name;
		this.start_compound = conformation.start_compound;
		this.start_chain    = conformation.start_chain;
		this.start_residue  = conformation.start_residue;
		this.end_compound   = conformation.end_compound;
		this.end_chain      = conformation.end_chain;
		this.end_residue    = conformation.end_residue;
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
	 */
	private static String className = null;
	public static String getClassName()
	{
		if ( Conformation.className == null ) {
			Conformation.className = ((new Throwable()).getStackTrace())[0].getClassName();
		}
		return Conformation.className;
	}

	//
	// Conformation fields
	//

	/**
	 *  For example, "helix_BA", "turn_1A"
	 */
	public String name = null;

	/**
	 *  For example, "ALA", "GLU", "LYS", etc.
	 */
	public String start_compound = null;

	/**
	 *  This value corresponds to the start Atom.chain_id value.
	 *  For example, "A", "B", "D", etc.
	 */
	public String start_chain = null;

	/**
	 *  This value corresponds to the Atom.residue_id value.
	 *  For example, 22, 27, etc.
	 */
	public int start_residue = -1;

	/**
	 *  For example, "ALA", "GLU", "LYS", etc.
	 */
	public String end_compound = null;

	/**
	 *  This value corresponds to the end Atom.chain_id value.
	 *  For example, "A", "B", "D", etc.
	 */
	public String end_chain = null;

	/**
	 *  This value corresponds to the end Atom.residue_id value.
	 *  For example, 27, 137, etc.
	 */
	public int end_residue = -1;
}

