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



/**
 *  This class implements the ResidueColor interface using a fixed RGB color.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IResidueColor
 *  @see	org.rcsb.mbt.model.Residue
 */
public class ResidueColorByRgb
	implements IResidueColor
{
	private float[] defaultColor = null;

	private final HashMap colorByResidue = new HashMap();
	
	private IResidueColor defaultColorGenerator = null;
	
	public ResidueColorByRgb() {
		
	}
	
	/**
	 *  Create an instance of this class using the specified RGB color.
	 */
	public ResidueColorByRgb( final float[] defaultColor )
	{
		this.setDefaultColor( defaultColor );
	}

	/**
	 *  Create an instance of this class using the specified red, green,
	 *  and blue values.
	 */
	public ResidueColorByRgb( final float red, final float green, final float blue )
	{
		this.defaultColor = new float[3];
		this.defaultColor[0] = red;
		this.defaultColor[1] = green;
		this.defaultColor[2] = blue;
	}
	
	public ResidueColorByRgb(final IResidueColor defaultColorGenerator) {
		this.defaultColorGenerator = defaultColorGenerator;
	}

	/**
	 *  Produce a fixed color for the residue.
	 */
	public void getResidueColor( final Residue residue, final float[] color )
	{
		if ( color == null ) {
			throw new IllegalArgumentException( "null color" );
		}

		final float[] savedColor = (float[])this.colorByResidue.get(residue);
		
		if(savedColor != null) {
			color[0] = savedColor[0];
			color[1] = savedColor[1];
			color[2] = savedColor[2];
		} else if(this.defaultColorGenerator != null) {
			this.defaultColorGenerator.getResidueColor(residue, color);
		} else {
			try {
				color[0] = this.defaultColor[0];
				color[1] = this.defaultColor[1];
				color[2] = this.defaultColor[2];
			} catch(final Exception e) {
				System.err.flush();
			}
		}
	}

	public boolean usesDefaultColorGenerator(final Residue r) {
		return !this.colorByResidue.containsKey(r);
	}
	
	public IResidueColor getDefaultColorGenerator() {
		return this.defaultColorGenerator;
	}
	
	/**
	 *  Set the fixed RGB color used to color a residue.
	 */
	public void setDefaultColor( final float[] color )
		throws IllegalArgumentException
	{
		if ( color == null ) {
			throw new IllegalArgumentException( "null color" );
		}

		if(this.defaultColor == null) {
			this.defaultColor = new float[3];
		}
		this.defaultColor[0] = color[0];
		this.defaultColor[1] = color[1];
		this.defaultColor[2] = color[2];
	}
	
	public void setColor(final Residue residue, final float[] color) {
		final float[] color_ = new float[] {color[0], color[1], color[2]};
		
		this.colorByResidue.put(residue, color_);
	}

	public void setDefaultColorGenerator(final IResidueColor defaultColorGenerator) {
		this.defaultColorGenerator = defaultColorGenerator;
	}
}

