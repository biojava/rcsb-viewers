//  $Id: Bond.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Bond.java,v $
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
//  Revision 1.14  2005/11/08 20:58:12  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.13  2005/03/31 21:49:36  moreland
//  Corrected ClassCastException with addition of instanceof call.
//
//  Revision 1.12  2005/02/15 01:03:12  moreland
//  Fixed or removed hashCode/equals methods that were causing hash collisions.
//
//  Revision 1.11  2005/02/03 18:16:27  moreland
//  Constructor and set methods now prevent bonds from connecting an atom to itself.
//
//  Revision 1.10  2004/10/27 20:03:30  moreland
//  Corrected javadoc SEE references.
//
//  Revision 1.9  2004/06/02 18:23:01  moreland
//  Added support for bond order.
//
//  Revision 1.8  2004/04/29 22:55:10  moreland
//  Added ionic bond type.
//  Improved getBondType method's covalent VS hydrogen bond determination.
//
//  Revision 1.7  2004/04/09 00:17:00  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.6  2004/02/05 18:36:36  moreland
//  Now computes distances using the Algebra class methods.
//
//  Revision 1.5  2004/01/29 17:08:15  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.4  2003/12/20 00:56:38  moreland
//  Implemented getBondType method using atom distances and element types.
//
//  Revision 1.3  2003/07/11 22:54:26  moreland
//  Added a "hashCode" and "equal" methods to enable hashing/uniqueness to be based upon the
//  atoms/children (and regardless of atom order).
//
//  Revision 1.2  2003/04/23 17:16:38  moreland
//  Removed StructureComponentID/scid and replaced it with a "structure" field
//  in the StructureComponent base class.
//  Changed "getType" method to "getStructureComponentType" to return dynamic
//  SC type (ie: class name).
//
//  Revision 1.1  2003/04/03 23:08:11  moreland
//  First version.
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


import org.rcsb.mbt.model.geometry.Algebra;
import org.rcsb.mbt.model.util.*;


/**
 *  Implements a StructureComponent container for bond information.
 *  This may be a covalent or a hydrogen bond.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.StructureComponent
 *  @see	org.rcsb.mbt.model.Atom
 */
public class Bond
	extends StructureComponent
	implements java.lang.Cloneable
{
	/**
	 * Type for a covalent bond (bonding interaction due to electron sharing).
	 */
	public static final String TYPE_COVALENT = "Covalent";

	/**
	 * Type for a hydrogen bond (bonding interaction due to partial charges).
	 */
	public static final String TYPE_HYDROGEN = "Hydrogen";

	/**
	 * Type for an ionic bond (bonding interaction due to electrostatic
	 * attraction between cations and anions).
	 */
	public static final String TYPE_IONIC = "Ionic";


	// The two atoms forming the bond.
	private Atom atoms[];

	// The order of the bond (how many electron pairs are shared).
	private float order = 0.0f;


	//
	// Constructors
	//

	/**
	 * Constructor variant used when there will likely be no atom list
	 * (eg: when only sequence data is loaded).
	 */
	public Bond( final Atom atom0, final Atom atom1 )
	{
		this.setStructure( atom0.getStructure() );

		this.atoms = new Atom[2];
		this.setAtom( 0, atom0 );
		this.setAtom( 1, atom1 );

		// TODO: Is it possible to compute bond order only from the two atom
		// elements? Or, do we need to know about the entire compound?
		// Or, perhaps this needs to be set by the creator of the bond...
		// ((# of electrons in bonding molecular orbitals) - (# of electrons in antibonding molecular orbitals))/2]
		// It is not sufficient to just know the valence count for each atom
		// because it could be bound to other atoms as well...
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
		final Bond bond = (Bond) structureComponent;

		this.atoms[0] = bond.getAtom( 0 );
		this.atoms[1] = bond.getAtom( 1 );
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
	 *  One Bond is equal to another Bond if they hashCode values are equal.
	 *  That is if they contain (in either order) the same two Atom objects.
	 */
	public boolean equals( final Object o )
	{
		if ( ! (o instanceof Bond) ) {
			return false;
		}
		final Bond b = (Bond) o;
		final Atom bAtoms[] = { b.getAtom(0), b.getAtom(1) };
		if ( (bAtoms[0] == this.atoms[0]) && (bAtoms[1] == this.atoms[1]) ) {
			return true;
		} else if ( (bAtoms[0] == this.atoms[1]) && (bAtoms[1] == this.atoms[0]) ) {
			return true;
		} else {
			return false;
		}
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
		if ( Bond.className == null ) {
			Bond.className = Bond.class.getName();
		}
		return Bond.className;
	}

	/**
	 *  This method returns the fully qualified name of this class.
	 */
	public String getStructureComponentType( )
	{
		return Bond.className;
	}

	//
	// Bond methods.
	//

	/**
	 * Get the type of bond (ie: covalent or hydrogen)
	 * based upon atom distances and element types.
	 */
	public String getBondType( )
	{
		if ( this.atoms == null ) {
			return null;
		}
		if ( this.atoms[0] == null ) {
			return null;
		}
		if ( this.atoms[1] == null ) {
			return null;
		}

		boolean a0Hydrogen = this.atoms[0].element.equals( "H" );
		boolean a1Hydrogen = this.atoms[1].element.equals( "H" );

		// If both atoms are hydrogens then it has to be a hydrogen bond.
		if ( a0Hydrogen && a1Hydrogen ) {
			return Bond.TYPE_HYDROGEN;
		}

		// If neither atoms are hydrogen then it can't be a hydrogen bond
		if ( (! a0Hydrogen) && (! a1Hydrogen) )
		{
			// JLM DEBUG: it could be a covalent OR ionic bond (need a check)!
			return Bond.TYPE_COVALENT;
		}

		// Exactly one of the atoms is a hydrogen.

		// JLM DEBUG: Hmmm, is this rule always true?
		if ( this.atoms[0].residue_id == this.atoms[1].residue_id ) {
			return Bond.TYPE_COVALENT; // Bond atoms are in the same compound
		} else {
			return Bond.TYPE_HYDROGEN; // Bond atoms are in different compounds
		}
	}


	/**
	 * Get the bond order (the number of covalently shared electron pairs).
	 * Generally this is a value from 1.0 to 6.0 but may be 1.5 for aromatic
	 * (benzene ring bonds) or 0.0 for unspecified order (the default).
	 * <P>
	 * @see #setOrder( float order )
	 */
	public float getOrder( )
	{
		return this.order;
	}


	/**
	 * Set the bond order (the number of covalently shared electron pairs).
	 * Generally this is a value from 1.0 to 6.0 but may be 1.5 for aromatic
	 * (benzene ring bonds) or 0.0 for unspecified order (the default).
	 * <P>
	 * @see #getOrder( )
	 */
	public void setOrder( final float order )
	{
		if ( (order < 0.0f) || (order > 6.0f) ) {
			throw new IllegalArgumentException( "Invalid order " + order );
		}

		this.order = order;
	}


	/**
	 * Get the distance for the bond.
	 */
	public double getDistance( )
	{
		if ( this.atoms == null ) {
			return 0.0f;
		}
		if ( this.atoms[0] == null ) {
			return 0.0f;
		}
		if ( this.atoms[1] == null ) {
			return 0.0f;
		}

		return Algebra.distance( this.atoms[0].coordinate, this.atoms[1].coordinate );
	}

	public static final double getDistance( final Atom atom0, final Atom atom1 )
	{
		return Algebra.distance( atom0.coordinate, atom1.coordinate );
	}

	/**
	 * Get the specified Atom record that forms this Bond.
	 */
	public Atom getAtom( final int index )
	{
		return this.atoms[index];
	}

	/**
	 * Set the specified Atom record that forms this Bond.
	 */
	public void setAtom( final int index, final Atom atom )
	{
		if ( atom == null ) {
			throw new IllegalArgumentException( "null atom" );
		}

		// Prevent bonds having the same atom twice!
		int otherIndex;
		if ( index == 0 ) {
			otherIndex = 1;
		} else {
			otherIndex = 0;
		}
		if ( this.atoms[otherIndex] == atom ) {
			throw new IllegalArgumentException( "duplicate atom" );
		}

		this.atoms[index] = atom;
	}
}

