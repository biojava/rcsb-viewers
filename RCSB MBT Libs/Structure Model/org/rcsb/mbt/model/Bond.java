/*
 * BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence. This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors. These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 * http://www.biojava.org/
 *
 * This code was contributed from the Molecular Biology Toolkit
 * (MBT) project at the University of California San Diego.
 *
 * Please reference J.L. Moreland, A.Gramada, O.V. Buzko, Qing
 * Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (MBT):
 * A Modular Platform for Developing Molecular Visualization
 * Applications. BMC Bioinformatics, 6:21.
 *
 * The MBT project was funded as part of the National Institutes
 * of Health PPG grant number 1-P01-GM63208 and its National
 * Institute of General Medical Sciences (NIGMS) division. Ongoing
 * development for the MBT project is managed by the RCSB
 * Protein Data Bank(http://www.pdb.org) and supported by funds
 * from the National Science Foundation (NSF), the National
 * Institute of General Medical Sciences (NIGMS), the Office of
 * Science, Department of Energy (DOE), the National Library of
 * Medicine (NLM), the National Cancer Institute (NCI), the
 * National Center for Research Resources (NCRR), the National
 * Institute of Biomedical Imaging and Bioengineering (NIBIB),
 * the National Institute of Neurological Disorders and Stroke
 * (NINDS), and the National Institute of Diabetes and Digestive
 * and Kidney Diseases (NIDDK).
 *
 * Created on 2007/02/08
 *
 */ 
package org.rcsb.mbt.model;


import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.geometry.ArrayLinearAlgebra;


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
	public enum BondType
	{
	/**
	 * Type for a covalent bond (bonding interaction due to electron sharing).
	 */
		COVALENT,

	/**
	 * Type for a hydrogen bond (bonding interaction due to partial charges).
	 */
		HYDROGEN,

	/**
	 * Type for an ionic bond (bonding interaction due to electrostatic
	 * attraction between cations and anions).
	 */
		IONIC
	}


	// The two atoms forming the bond.
	private Atom atoms[];

	// The order of the bond (how many electron pairs are shared).
	// (( turn this into an enum??? ))
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
	@Override
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
	@Override
	public Object clone( )
		throws CloneNotSupportedException
	{
		return super.clone( );
	}


	/**
	 *  One Bond is equal to another Bond if they hashCode values are equal.
	 *  That is if they contain (in either order) the same two Atom objects.
	 */
	@Override
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
	@Override
	public ComponentType getStructureComponentType( )
	{
		return ComponentType.BOND;
	}

	//
	// Bond methods.
	//

	/**
	 * Get the type of bond (ie: covalent or hydrogen)
	 * based upon atom distances and element types.
	 */
	public BondType getBondType( )
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
			return BondType.HYDROGEN;
		}

		// If neither atoms are hydrogen then it can't be a hydrogen bond
		if ( (! a0Hydrogen) && (! a1Hydrogen) )
		{
			// JLM DEBUG: it could be a covalent OR ionic bond (need a check)!
			return BondType.COVALENT;
		}

		// Exactly one of the atoms is a hydrogen.

		// JLM DEBUG: Hmmm, is this rule always true?
		if ( this.atoms[0].residue_id == this.atoms[1].residue_id ) {
			return BondType.COVALENT; // Bond atoms are in the same compound
		} else {
			return BondType.HYDROGEN; // Bond atoms are in different compounds
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

		return ArrayLinearAlgebra.distance( this.atoms[0].coordinate, this.atoms[1].coordinate );
	}

	public static final double getDistance( final Atom atom0, final Atom atom1 )
	{
		return ArrayLinearAlgebra.distance( atom0.coordinate, atom1.coordinate );
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

