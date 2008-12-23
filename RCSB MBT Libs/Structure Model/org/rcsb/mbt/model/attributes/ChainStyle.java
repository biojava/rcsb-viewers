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


import java.util.HashMap;

import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;



/**
 * ChainStyle.java<BR>
 * <P>
 * Please complete these missing tags
 * @author	John L. Moreland
 * @copyright	UCSD
 * @see
 */
public class ChainStyle
	extends Style
	implements Cloneable
{
	// Style properties
	private IResidueColor residueColor = ResidueColorRegistry.getDefault( );

	private IResidueLabel residueLabel = ResidueLabelCustom.getSingleton();

	private boolean residueBinding = false;
	private final HashMap rBinding = new HashMap();
	//
	// Constructors
	//


	/**
	 * Construct an object which stores styles for a Chain.
	 * <P>
	 */
	public ChainStyle( )
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
	public void copy( final ResidueStyle residueStyle )
	{
		this.residueColor = residueStyle.getResidueColor( );
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
	// ChainStyle methods
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
		return (scType == ComponentType.CHAIN);
	}


	/**
	 * xxx.
	 * <P>
	 */
	public void setResidueColor( final IResidueColor residueColor )
	{
		if ( residueColor == null ) {
			throw new NullPointerException( "null residueColor argument" );
		}

		this.residueColor = residueColor;
	}


	/**
	 * xxx.
	 * <P>
	 */
	public IResidueColor getResidueColor( )
	{
		return this.residueColor;
	}


	/**
	 * xxx.
	 * <P>
	 */
	public void getResidueColor( final Residue residue, final float[] color )
	{
		if ( residue == null ) {
			throw new NullPointerException( "null residue argument" );
		}
		if ( color == null ) {
			throw new NullPointerException( "null color argument" );
		}

		this.residueColor.getResidueColor( residue, color );
	}


	public IResidueLabel getResidueLabel() {
		return this.residueLabel;
	}


	public void setResidueLabel(final IResidueLabel residueLabel) {
		this.residueLabel = residueLabel;
	}
	
	
	// Scratch event that is re-used for all fired events.
	private final StructureStylesEvent structureStylesEvent =
		new StructureStylesEvent();
	
	public void resetBinding(final Structure struc) {
		final StructureMap structureMap = struc.getStructureMap();
		for (int i=0; i<structureMap.getResidueCount(); i++)
		{
			final Residue r=structureMap.getResidue(i);
			if (this.rBinding.containsKey(r))
			{
				this.rBinding.remove(r);
				this.structureStylesEvent.structureComponent=r;
				this.structureStylesEvent.property=StyleProperty.PROPERTY_BINDING;
				this.fireStructureStylesEvent(this.structureStylesEvent);
			}
		}
	}


}

