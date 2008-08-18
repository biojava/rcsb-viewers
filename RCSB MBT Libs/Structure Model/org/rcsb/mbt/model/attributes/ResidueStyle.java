//  $Id: ResidueStyle.java,v 1.1 2007/02/08 02:38:51 jbeaver Exp $
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
//  $Log: ResidueStyle.java,v $
//  Revision 1.1  2007/02/08 02:38:51  jbeaver
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
 *  Provides a container for all viewable styles related to a Residue.
 *  <P>
 *  @author John L. Moreland
 *  @copyright UCSD
 *  @see Style
 */
public class ResidueStyle
	extends Style
	implements Cloneable
{
	//
	// Style properties
	//

	/**
	 *  ResidueColor defines the color applied to each end of a residue.
	 */
	private IResidueColor residueColor = ResidueColorRegistry.getDefault( );
	public static int PROPERTY_COLOR = 1;


	//
	// Constructors
	//


	/**
	 *  Construct a residue style object using default style properties.
	 *  <P>
	 *  @see ResidueColorRegistry
	 */
	public ResidueStyle( )
	{
	}


	/**
	 *  Construct a residue style object with the specified properties.
	 *  <P>
	 *  @param IResidueColor defines the color applied to each residue.
	 *  @throws NullPointerException
	 */
	public ResidueStyle( final IResidueColor residueColor )
	{
		if ( residueColor == null ) {
			throw new NullPointerException( "residueColor is null" );
		}

		this.residueColor = residueColor;
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
	public void copy( final ResidueStyle residueStyle )
	{
		this.residueColor = residueStyle.residueColor;
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
	// Style Methods
	//


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @return
	 * @throws
	 */
	public boolean isSelected( final Residue residue )
	{
		final int atomCount = residue.getAtomCount( );
		final StructureStyles structureStyles =
			residue.getStructure().getStructureMap().getStructureStyles( );
		if ( atomCount <= 0 )
		{
			return structureStyles.isSelected( residue );
		}
		else
		{
			for ( int a=0; a<atomCount; a++ )
			{
				final Atom atom = residue.getAtom( a );
				if ( ! structureStyles.isSelected( atom ) ) {
					return false;
				}
			}
			return true;
		}
	}


	//
	// ResidueStyle Methods
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
		return (scType == StructureComponentRegistry.TYPE_RESIDUE);
	}


	/**
	 *  Set the ResidueColor object that defines what color is applied to each
	 *  end of a residue.
	 *  <P>
	 *  @param IResidueColor defines the color applied to each end of a residue.
	 *  @throws NullPointerException if residueColor argument is null.
	 */
	public void setResidueColor( final IResidueColor residueColor )
	{
		if ( residueColor == null ) {
			throw new NullPointerException( "null ResidueColor argument" );
		}

		this.residueColor = residueColor;
	}


	/**
	 *  Get the ResidueColor object that defines what color is applied to each
	 *  end of a residue.
	 *  <P>
	 */
	public IResidueColor getResidueColor( )
	{
		return this.residueColor;
	}


	/**
	 *  Get the value that defines the primary color for the residue.
	 *  <P>
	 *  @param Residue specifies the residue for which a color is to be applied.
	 *  @param color is the array in which the residue color is to be stored.
	 *  @throws NullPointerException if residue or color arguments are null.
	 *  @throws IllegalArgumentException if color length < 3.
	 */
	public void getResidueColor( final Residue residue, final float color[] )
	{
		if ( residue == null ) {
			throw new NullPointerException( "null residue argument" );
		}
		if ( color == null ) {
			throw new NullPointerException( "null color argument" );
		}
		if ( color.length < 3 ) {
			throw new IllegalArgumentException( "color length must be >= 3" );
		}

		this.residueColor.getResidueColor( residue, color );
	}
}

