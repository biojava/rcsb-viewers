//  $Id: Coil.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Coil.java,v $
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
//  Revision 1.5  2004/04/09 00:17:00  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.4  2004/01/31 19:11:48  moreland
//  Removed outdated programming note.
//
//  Revision 1.3  2004/01/29 17:08:15  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.2  2003/04/23 17:20:54  moreland
//  Removed StructureComponentID/scid and replaced it with a "structure" field
//  in the StructureComponent base class.
//  Changed "getType" method to "getStructureComponentType" to return dynamic
//  SC type (ie: class name).
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


package org.rcsb.mbt.model.interim;

import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureComponentRegistry;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;


/**
 *  Implements a StructureComponent container for Coil conformation
 *  (secondary structure) data.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Structure
 *  @see	org.rcsb.mbt.model.interim.Conformation
 *  @see	org.rcsb.mbt.model.StructureComponent
 */
public class Coil
	extends Conformation
	implements java.lang.Cloneable
{
	//
	// Constructor
	//

	/**
	 *  Creates a new Coil object.
	 */
	public Coil( )
	{
	}

	/**
	 *  This method returns the fully qualified name of this class as a String
	 *  object. The String object is guaranteed to be a reference to the
	 *  same String object for all instances of a given sub-class.
	 */
	private static String className = null;
	public static String getClassName()
	{
		if ( Coil.className == null ) {
			Coil.className = Coil.class.getName();
		}
		return Coil.className;
	}

	/**
	 *  This method returns the fully qualified name of this class.
	 */
	public ComponentType getStructureComponentType( )
	{
		return ComponentType.COIL;
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
		// Coil coil = (Coil) structureComponent;
		// localField       = coil.localField;
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
	// Coil fields
	//

	// None.
}

