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
 * AtomStyle.java<BR>
 * <P>
 * Please complete these missing tags
 * @author	John L. Moreland
 * @copyright	UCSD
 * @see
 */
public class AtomStyle
	extends Style
	implements Cloneable
{
	// Style properties
	private IAtomColor atomColor = AtomColorRegistry.getDefault( );
	private IAtomRadius atomRadius = AtomRadiusRegistry.getDefault( );
	private IAtomLabel atomLabel = AtomLabelRegistry.getDefault( );


	//
	// Constructors
	//


	/**
	 * Construct an object which stores display styles for an Atom.
	 * <P>
	 * @param	Atom for which to generate a display list.
	 * @return	NA
	 * @throws	NullPointerException
	 */
	public AtomStyle( )
	{
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
	public void copy( final AtomStyle atomStyle )
	{
		this.atomColor  = atomStyle.atomColor;
		this.atomRadius = atomStyle.atomRadius;
		this.atomLabel  = atomStyle.atomLabel;
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
	// AtomStyle methods
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
		return (scType == ComponentType.ATOM);
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public void setAtomColor( final IAtomColor atomColor )
	{
		if ( atomColor == null ) {
			throw new NullPointerException( "null atomColor argument" );
		}

		this.atomColor = atomColor;
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public IAtomColor getAtomColor( )
	{
		return this.atomColor;
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public void getAtomColor( final Atom atom, final float color[] )
	{
		this.atomColor.getAtomColor( atom, color );
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public void setAtomRadius( final IAtomRadius atomRadius )
	{
		if ( atomRadius == null ) {
			throw new NullPointerException( "null atomRadius argument" );
		}

		this.atomRadius = atomRadius;
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public IAtomRadius getAtomRadius( )
	{
		return this.atomRadius;
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public float getAtomRadius( final Atom atom )
	{
		return this.atomRadius.getAtomRadius( atom );
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public void setAtomLabel( final IAtomLabel atomLabel )
	{
		if ( atomLabel == null ) {
			throw new NullPointerException( "null AtomLabel argument" );
		}

		this.atomLabel = atomLabel;
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public IAtomLabel getAtomLabel( )
	{
		return this.atomLabel;
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param
	 * @return
	 * @throws
	 */
	public String getAtomLabel( final Atom atom )
	{
		return this.atomLabel.getAtomLabel( atom );
	}
}

