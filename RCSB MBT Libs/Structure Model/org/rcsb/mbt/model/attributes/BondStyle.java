//  $Id: BondStyle.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.//
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
//  $Log: BondStyle.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:54  jbeaver
//  Initial commit
//
//  Revision 1.1  2005/11/10 23:19:47  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.0 2005/04/04 00:12:54  moreland
//


package org.rcsb.mbt.model.attributes;


import org.rcsb.mbt.model.*;


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
	public static int PROPERTY_COLOR = 1;
	private IBondForm   bondForm   = BondFormRegistry.getDefault( );
	public static int PROPERTY_FORM = 2;
	private IBondLabel  bondLabel  = BondLabelRegistry.getDefault( );
	public static int PROPERTY_LABEL = 3;
	private IBondRadius bondRadius = BondRadiusRegistry.getDefault( );
	public static int PROPERTY_RADIUS = 4;


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
	
	public final boolean isTypeSafe( final String scType )
	{
		return (scType == StructureComponentRegistry.TYPE_BOND);
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

