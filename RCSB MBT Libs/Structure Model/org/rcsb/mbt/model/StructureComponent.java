//  $Id: StructureComponent.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: StructureComponent.java,v $
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
//  Revision 1.7  2005/11/08 20:58:12  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.6  2004/04/09 00:17:01  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.5  2004/01/31 19:11:48  moreland
//  Removed outdated programming note.
//
//  Revision 1.4  2004/01/29 17:08:16  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.3  2003/04/23 17:34:50  moreland
//  Removed StructureComponentID/scid and replaced it with a "structure" field
//  in the StructureComponent base class.
//  Changed "getType" method to "getStructureComponentType" to return dynamic
//  SC type (ie: class name).
//  Added getStructure method.
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

import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;


/**
 *  Defines the abstract base class for a StructureComponent data container.
 *  Each subclass instance represents a raw "record" from its source Structure.
 *  Using the Structure class directly is useful if an application simply
 *  wishes to walk the raw records of a data set. For a more structured "map"
 *  (hierarchical view) of derived data, see the StructureMap class.
 *  <P>
 *  Here is an example of the Atom sub-class which demonstrates a typical
 *  use case for all StructureComponent sub-classes:
 *  <P>
 *  <PRE>
 *      int atomCount = structure.getStructureComponentCount(
 *         StructureComponentRegistry.TYPE_ATOM );
 *      for ( int i=0; i<atomCount; i++ )
 *      {   
 *         Atom atom = (Atom) getStructureComponentByIndex(
 *            StructureComponentRegistry.TYPE_ATOM, i );
 *         // do something useful here with the current Atom record
 *      }
 *  </PRE>
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Structure
 *  @see	org.rcsb.mbt.model.StructureMap
 */
public abstract class StructureComponent
	implements java.lang.Cloneable
{
	/**
	 *  The Structure to which a StructureComponent instance belongs.
	 */
	public Structure structure = null;

	/**
	 *  Set the Structure to which a StructureComponent instance belongs.
	 */
	public final void setStructure( final Structure structure )
	{
		if ( structure == null ) {
			throw new NullPointerException( "Null structure" );
		}
		this.structure = structure;
	}

	/**
	 *  Get the Structure to which a StructureComponent instance belongs.
	 */
	public final Structure getStructure( )
	{
		return this.structure;
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
	 *  Copy all of the field values from the parameter object into "this".
	 */
	abstract public void copy( StructureComponent structureComponent );

	/**
	 *  This method returns the fully qualified name of this class.
	 *  Sub-classes must override this method (This method should really
	 *  be declared abstract, but unfortunately Java does not allow a
	 *  method to be both static AND abstract). Instead, this base class 
	 *  implementation will through an UnsupportedOperationException.
	 *  <P>
	 *  This name is used by the StructureComponentRegistry class to enable
	 *  dynamic registration and discovery of new StructureComponent
	 *  sub-classes/types. The name is also used to create instances
	 *  of any sublcass dynamically from this name.
	 */
	public static String getClassName( )
		throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException(
			"getClassName method not implemented" );
	}

	/**
	 *  This method returns the fully qualified name of this class.
	 *  Sub-classes must override this method. Unfortunately Java does not
	 *  allow a method to be both static AND abstract). Instead, this base
	 *  class implementation will through an UnsupportedOperationException.
	 *  <P>
	 *  This name is used by the StructureComponentRegistry class to enable
	 *  dynamic registration and discovery of new StructureComponent
	 *  sub-classes/types. The name is also used to create instances
	 *  of any sublcass dynamically from this name.
	 */
	public abstract ComponentType getStructureComponentType( );
}

