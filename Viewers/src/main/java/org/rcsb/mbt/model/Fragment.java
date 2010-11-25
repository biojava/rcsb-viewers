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


import java.util.*;

import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;



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
	private ComponentType conformationType = ComponentType.UNDEFINED_CONFORMATION;

	//
	// Constructors
	//

	/**
	 * Construct a Fragment for the given start Residue
	 * index, end Residue index, and conformation type.
	 * The Residue index is a Chain-level index (not a Structure-level index).
	 * This is protected because only Chain can manage Fragments.
	 */
	protected Fragment( final int startResidueIndex, final int endResidueIndex, final ComponentType conformationType )
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
		this.conformationType = ComponentType.UNDEFINED_CONFORMATION;
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
	
	public ComponentType getStructureComponentType( )
	{
		return ComponentType.FRAGMENT;
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
	public void setConformationType( final ComponentType conformationType )
	{
		this.conformationType = conformationType;
	}

	/**
	 * Get the Conformation type for this Fragment.
	 */
	public ComponentType getConformationType( )
	{
		return this.conformationType;
	}
}

