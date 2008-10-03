//  $Id: Fragment.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Fragment.java,v $
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
//  Revision 1.15  2005/11/08 20:58:12  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.14  2005/02/15 01:03:12  moreland
//  Fixed or removed hashCode/equals methods that were causing hash collisions.
//
//  Revision 1.13  2004/04/09 00:17:00  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.12  2004/01/31 19:11:48  moreland
//  Removed outdated programming note.
//
//  Revision 1.11  2004/01/29 17:08:16  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.10  2004/01/16 18:14:09  moreland
//  Improved exception handling for more index out of bounds conditions.
//
//  Revision 1.9  2004/01/15 19:12:00  moreland
//  Added more code for IndexOutOfBoundsException handling.
//
//  Revision 1.8  2004/01/15 00:50:22  moreland
//  Fixed hashCode method to prevent value from equalling parent chain value.
//  Fixed the getResidue method to correctly add startResidueIndex to index.
//
//  Revision 1.7  2003/12/20 01:00:14  moreland
//  Constructors are now protected because only Chain can manage Fragments.
//
//  Revision 1.6  2003/11/22 00:47:10  moreland
//  Added check to equals method for Fragment type.
//
//  Revision 1.5  2003/11/21 22:36:23  moreland
//  Simplified the hasCode method's hash function.
//
//  Revision 1.4  2003/11/21 22:33:59  moreland
//  Fixed hashCode and equals methods to include chain as part of the hash function.
//
//  Revision 1.3  2003/11/20 21:31:39  moreland
//  Added setChain, getChain, getResidueCount and getResidue methods to Fragment.
//  Added call to fragment.setChain method to Chain class.
//  Both changes above enable the Fragment class to access parent and child objects.
//
//  Revision 1.2  2003/10/17 18:19:56  moreland
//  Fixed a javadoc comment.
//
//  Revision 1.1  2003/07/21 20:47:40  moreland
//  First implementation of a secondary structure fragment object.
//
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//  First version.
//


package org.rcsb.mbt.model;


import java.util.*;


/**
 *  Implements a StructureComponent container for secondary structure fragment
 *  information. This consists of Residue range and the secondary structure type.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.StructureComponent
 *  @see	org.rcsb.mbt.model.Structure
 */
public class Fragment
	extends StructureComponent
	implements java.lang.Cloneable
{
	// The Chain to which this Fragment belongs.
	private Chain chain = null;

	// The start Residue index for this Fragment.
	// The Residue index is a Chain-level index (not a Structure-level index).
	private int startResidueIndex = -1;

	// The end Residue index for this Fragment.
	// The Residue index is a Chain-level index (not a Structure-level index).
	private int endResidueIndex = -1;

	// The secondary structure conformation type assigned to this Fragment.
	private String conformationType = Conformation.TYPE_UNDEFINED;

	//
	// Constructors
	//

	/**
	 * Construct a Fragment for the given start Residue
	 * index, end Residue index, and conformation type.
	 * The Residue index is a Chain-level index (not a Structure-level index).
	 * This is protected because only Chain can manage Fragments.
	 */
	protected Fragment( final int startResidueIndex, final int endResidueIndex, final String conformationType )
	{
		this.startResidueIndex = startResidueIndex;
		this.endResidueIndex = endResidueIndex;
		this.conformationType = conformationType;
	}

	/**
	 * Constructor variant used when there will likely be an atom list.
	 * This is protected because only Chain can manage Fragments.
	 */
	protected Fragment( )
	{
		this.startResidueIndex = -1;
		this.endResidueIndex = -1;
		this.conformationType = Conformation.TYPE_UNDEFINED;
	}

	/**
	 * Set the chain to which this Fragment belongs (called by chain.addFragment).
	 */
	protected void setChain( final Chain chain )
	{
		this.chain = chain;
	}

	/**
	 * Get the chain to which this Fragment belongs.
	 */
	public Chain getChain( )
	{
		return this.chain;
	}

	//
	// StructureComponent methods.
	//

	/**
	 *  Copy all of the field values from the parameter object into "this".
	 */
	
	public void copy( final StructureComponent structureComponent )
	{
		this.setStructure( structureComponent.getStructure() );
		final Fragment fragment = (Fragment) structureComponent;

		this.startResidueIndex = fragment.startResidueIndex;
		this.endResidueIndex = fragment.endResidueIndex;
		this.conformationType = fragment.conformationType;
	}

	/**
	 *  Clone this object.
	 */
	
	public Object clone( )
		throws CloneNotSupportedException
	{
		return super.clone( );
	}

	private static String className = null;
	/**
	 *  This method returns the fully qualified name of this class.
	 *  <P>
	 *  This name is used by the StructureComponentRegistry class to enable
	 *  dynamic registration and discovery of new StructureComponent
	 *  sub-classes/types. The name is also used to create a unique integer
	 *  indentifier for each type in order to make run-time type comparisons
	 *  fast.
	 */
	public static String getClassName()
	{
		if ( Fragment.className == null ) {
			Fragment.className = Fragment.class.getName();
		}
		return Fragment.className;
	}

	/**
	 *  This method returns the fully qualified name of this class.
	 */
	
	public String getStructureComponentType( )
	{
		return Fragment.className;
	}


	//
	// Fragment methods.
	//

	/**
	 * Set the start Reisude index for this Fragment.
	 * The Residue index is a Chain-level index (not a Structure-level index).
	 */
	public void setStartResidueIndex( final int index )
	{
		if ( this.chain == null ) {
			throw new NullPointerException( "Fragment has no chain" );
		}
		if ( index < 0 ) {
			throw new IndexOutOfBoundsException( "index < 0" );
		}
		if ( index >= this.chain.getResidueCount() ) {
			throw new IndexOutOfBoundsException( "index >= count" );
		}

		this.startResidueIndex = index;
	}

	/**
	 * Get the start Reisude index for this Fragment.
	 * The Residue index is a Chain-level index (not a Structure-level index).
	 */
	public int getStartResidueIndex( )
	{
		return this.startResidueIndex;
	}

	/**
	 * Set the end Reisude index for this Fragment.
	 * The Residue index is a Chain-level index (not a Structure-level index).
	 */
	public void setEndResidueIndex( final int index )
	{
		if ( this.chain == null ) {
			throw new NullPointerException( "Fragment has no chain" );
		}
		if ( index < 0 ) {
			throw new IndexOutOfBoundsException( "index < 0" );
		}
		if ( index >= this.chain.getResidueCount() ) {
			throw new IndexOutOfBoundsException( "index >= count" );
		}

		this.endResidueIndex = index;
	}

	/**
	 * Get the end Reisude index for this Fragment.
	 * The Residue index is a Chain-level index (not a Structure-level index).
	 */
	public int getEndResidueIndex( )
	{
		return this.endResidueIndex;
	}

	/**
	 * Get the residue count for this Fragment.
	 */
	public int getResidueCount( )
	{
		return (this.endResidueIndex - this.startResidueIndex + 1);
	}

	/**
	 * Get the Residue at the given fragment-level residue index.
	 */
	public Residue getResidue( final int index )
	{
		if ( this.chain == null ) {
			throw new NullPointerException( "Fragment has no chain" );
		}
		if ( index < 0 ) {
			throw new IndexOutOfBoundsException( "index < 0" );
		}
		if ( index >= this.getResidueCount() ) {
			throw new IndexOutOfBoundsException( "index " + index +
				" >= " + this.getResidueCount() );
		}

		return this.chain.getResidue( index + this.startResidueIndex );
	}

	/**
	 * Get the residues for this Fragment.
	 */
	public Vector<Residue> getResidues( )
	{
		final Vector<Residue> residues = new Vector<Residue>( );
		final int count = this.getResidueCount( );
		for ( int r=0; r<count; r++ ) {
			residues.add( this.getResidue( r ) );
		}
		return residues;
	}

	/**
	 * Set the Conformation type for this Fragment.
	 */
	public void setConformationType( final String conformationType )
	{
		this.conformationType = conformationType;
	}

	/**
	 * Get the Conformation type for this Fragment.
	 */
	public String getConformationType( )
	{
		return this.conformationType;
	}
}

