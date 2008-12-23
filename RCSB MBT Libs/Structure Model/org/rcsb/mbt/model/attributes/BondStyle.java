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
package org.rcsb.mbt.model.attributes;


import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;


/**
 *  Provides a container for all viewable styles related to a Bond.
 *  <P>
 *  @author John L. Moreland
 *  @copyright UCSD
 *  @see Style
 */
public class BondStyle
	extends Style
	implements Cloneable
{
	// Style properties
	private IBondColor  bondColor  = BondColorRegistry.getDefault( );
	private IBondForm   bondForm   = BondFormRegistry.getDefault( );
	private IBondLabel  bondLabel  = BondLabelRegistry.getDefault( );
	private IBondRadius bondRadius = BondRadiusRegistry.getDefault( );



	//
	// Constructors
	//


	/**
	 *  Construct a bond style object using default style properties.
	 *  <P>
	 *  @see BondColorRegistry
	 *  @see BondFormRegistry
	 *  @see BondLabelRegistry
	 *  @see BondRadiusRegistry
	 */
	public BondStyle( )
	{
	}


	/**
	 * Construct a bond style object with the specified properties.
	 * <P>
	 * @param IBondColor defines the color applied to each end of a bond.
	 * @param IBondForm defines the geometric form used for a bond shape.
	 * @param IBondLabel defines the String value used to label the bond.
	 * @param IBondRadius defines the radius used to draw a bond shape.
	 * @throws NullPointerException
	 */
	public BondStyle(
		final IBondColor bondColor,
		final IBondForm bondForm,
		final IBondLabel bondLabel,
		final IBondRadius bondRadius
	)
	{
		if ( bondColor == null ) {
			throw new NullPointerException( "bondColor is null" );
		}
		if ( bondForm == null ) {
			throw new NullPointerException( "bondForm is null" );
		}
		if ( bondLabel == null ) {
			throw new NullPointerException( "bondLabel is null" );
		}
		if ( bondRadius == null ) {
			throw new NullPointerException( "bondRadius is null" );
		}

		this.bondColor = bondColor;
		this.bondForm = bondForm;
		this.bondLabel = bondLabel;
		this.bondRadius = bondRadius;
	}


	//
	// Cloneable interface
	//


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param	NA
	 * @return	NA
	 * @throws	NA
	 */
	public void copy( final BondStyle bondStyle )
	{
		this.bondColor  = bondStyle.bondColor;
		this.bondForm   = bondStyle.bondForm;
		this.bondLabel  = bondStyle.bondLabel;
		this.bondRadius = bondStyle.bondRadius;
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param	NA
	 * @return	NA
	 * @throws	NA
	 */
	
	public Object clone( )
		throws CloneNotSupportedException
	{
		return super.clone( );
	}


	//
	// BondStyle Methods
	//


	/**
	 * Return true if the Style implementation is type safe for
	 * the given StructureComponent type. That is, can the style
	 * be applied to the type?
	 * <P>
	 * @return
	 * @throws
	 */
	
	public final boolean isTypeSafe( final ComponentType scType )
	{
		return (scType == ComponentType.BOND);
	}


	/**
	 *  Set the BondColor object that defines what color is applied to each
	 *  end of a bond.
	 *  <P>
	 *  @param IBondColor defines the color applied to each end of a bond.
	 *  @throws NullPointerException if bondColor argument is null.
	 */
	public void setBondColor( final IBondColor bondColor )
	{
		if ( bondColor == null ) {
			throw new NullPointerException( "null BondColor argument" );
		}

		this.bondColor = bondColor;
	}


	/**
	 *  Get the BondColor object that defines what color is applied to each
	 *  end of a bond.
	 *  <P>
	 */
	public IBondColor getBondColor( )
	{
		return this.bondColor;
	}


	/**
	 *  Get the value that defines the primary color for the bond.
	 *  <P>
	 *  @param Bond specifies the bond for which a color is to be applied.
	 *  @param color is the array in which the bond color is to be stored.
	 *  @throws NullPointerException if bond or color arguments are null.
	 *  @throws IllegalArgumentException if color length < 3.
	 */
	public void getBondColor( final Bond bond, final float color[] )
	{
		if ( bond == null ) {
			throw new NullPointerException( "null bond argument" );
		}
		if ( color == null ) {
			throw new NullPointerException( "null color argument" );
		}
		if ( color.length < 3 ) {
			throw new IllegalArgumentException( "color length must be >= 3" );
		}

		this.bondColor.getBondColor( bond, color );
	}


	/**
	 *  Get the value that defines the secondary/split color for the bond.
	 *  <P>
	 *  @param Bond specifies the bond for which a color is to be applied.
	 *  @param color is the array in which the bond color is to be stored.
	 *  @throws NullPointerException if bond or color arguments are null.
	 *  @throws IllegalArgumentException if color length < 3.
	 */
	public void getSplitBondColor( final Bond bond, final float color[] )
	{
		if ( bond == null ) {
			throw new NullPointerException( "null bond argument" );
		}
		if ( color == null ) {
			throw new NullPointerException( "null color argument" );
		}
		if ( color.length < 3 ) {
			throw new IllegalArgumentException( "color length must be >= 3" );
		}

		this.bondColor.getSplitBondColor( bond, color );
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public void setBondRadius( final IBondRadius bondRadius )
	{
		if ( bondRadius == null ) {
			throw new NullPointerException( "null BondRadius argument" );
		}

		this.bondRadius = bondRadius;
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public IBondRadius getBondRadius( )
	{
		return this.bondRadius;
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public float getBondRadius( final Bond bond )
	{
		return this.bondRadius.getBondRadius( bond );
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public void setBondLabel( final IBondLabel bondLabel )
	{
		if ( bondLabel == null ) {
			throw new NullPointerException( "null BondLabel argument" );
		}

		this.bondLabel = bondLabel;
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public IBondLabel getBondLabel( )
	{
		return this.bondLabel;
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public String getBondLabel( final Bond bond )
	{
		return this.bondLabel.getBondLabel( bond );
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public void setBondForm( final IBondForm bondForm )
	{
		if ( bondForm == null ) {
			throw new NullPointerException( "null BondForm argument" );
		}

		this.bondForm = bondForm;
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public IBondForm getBondForm( )
	{
		return this.bondForm;
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public int getBondForm( final Bond bond )
	{
		return this.bondForm.getBondForm( bond );
	}
}

