//  $Id: Symmetry.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Symmetry.java,v $
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
//  Revision 1.6  2005/11/08 20:58:12  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.5  2004/04/09 00:17:01  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.4  2004/01/31 19:11:48  moreland
//  Removed outdated programming note.
//
//  Revision 1.3  2004/01/29 17:08:16  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.2  2003/04/23 17:40:51  moreland
//  Removed StructureComponentID/scid and replaced it with a "structure" field
//  in the StructureComponent base class.
//  Changed "getType" method to "getStructureComponentType" to return dynamic
//  SC type (ie: class name).
//
//  Revision 1.1  2002/12/16 06:29:07  moreland
//  New class to support Structure replication through symmetry operations.
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
 *  Implements a StructureComponent container for Symmetry data.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.StructureComponent
 *  @see	org.rcsb.mbt.model.Structure
 */
public class Symmetry
	extends StructureComponent
	implements java.lang.Cloneable
{
	//
	// Symmetry fields
	//

	/**
	 *  The symmetry space group name.
	 *  For example, "C 2", "I 2"
	 */
	private String name = null; // _symmetry.space_group_name_H-M

	/**
	 *  The symmetry matricies expressed as an array of 4x4 3D transforms.
	 *  For example, 
	 *   [1.0, 0.0, 0.0, 0.0]
	 *   [0.0, 1.0, 0.0, 0.0]
	 *   [0.0, 0.0, 1.0, 0.0]
	 *   [0.0, 0.0, 0.0, 1.0]
	 *  The 0th array index is the symetry index.
	 *  The 1st array index is the matrix number.
	 *  The 2nd array index is the row number.
	 *  The 3rd array index is the column number.
	 */
	private double matrix[][][] = null;

	//
	// Constructor
	//

	/**
	 *  Creates a new Symmetry object.
	 */
	public Symmetry( )
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
		final Symmetry symmetry = (Symmetry) structureComponent;
		for ( int i=0; i<this.matrix.length; i++ ) {
			for ( int j=0; j<this.matrix[i].length; j++ ) {
				for ( int k=0; k<this.matrix[i][j].length; k++ ) {
					this.matrix[i][j][k] = symmetry.matrix[i][j][k];
				}
			}
		}
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
		if ( Symmetry.className == null ) {
			Symmetry.className = ((new Throwable()).getStackTrace())[0].getClassName();
		}
		return Symmetry.className;
	}

	/**
	 *  This method returns the fully qualified name of this class.
	 */
	
	public String getStructureComponentType( )
	{
		return Symmetry.className;
	}

	//
	// Symmetry methods
	//

	/**
	 *  Set the symmetry space group name.
	 *  For example, "C 2", "I 2"
	 */
	public void setName( final String name )
	{
		this.name = name;
	}

	/**
	 *  Get the symmetry space group name.
	 *  For example, "C 2", "I 2"
	 */
	public String getName( )
	{
		return this.name;
	}

	/**
	 *  Set the symmetry matricies expressed as an array of 4x4 3D transforms.
	 *  The upper-left 3x1 sub-matrix is the rotation.
	 *   [1.0, 0.0, 0.0, 0.0]
	 *   [0.0, 1.0, 0.0, 0.0]
	 *   [0.0, 0.0, 1.0, 0.0]
	 *   [0.0, 0.0, 0.0, 1.0]
	 *  The 1st array index is the matrix number.
	 *  The 2nd array index is the row number.
	 *  The 3rd array index is the column number.
	 */
	public void set( final double matrix[][][] )
	{
		this.matrix = matrix;
	}

	/**
	 *  Get the symmetry matricies expressed as an array of 4x4 3D transforms.
	 *  The upper-left 3x1 sub-matrix is the rotation.
	 *   [1.0, 0.0, 0.0, 0.0]
	 *   [0.0, 1.0, 0.0, 0.0]
	 *   [0.0, 0.0, 1.0, 0.0]
	 *   [0.0, 0.0, 0.0, 1.0]
	 *  The 1st array index is the matrix number.
	 *  The 2nd array index is the row number.
	 *  The 3rd array index is the column number.
	 */
	public double[][][] get( )
	{
		return this.matrix;
	}
}

