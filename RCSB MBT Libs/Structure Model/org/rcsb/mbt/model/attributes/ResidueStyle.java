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
	
	public final boolean isTypeSafe( final ComponentType scType )
	{
		return (scType == ComponentType.RESIDUE);
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

