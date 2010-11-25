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
 * Created on 2008/12/22
 *
 */ 
package org.rcsb.vf.glscene.jogl;

import java.awt.Color;

public class Color3f implements Comparable {
	public final float[] color = {0,0,0,1};
	
	private static final float[] scratchArray = {0,0,0,1};	// red, green, blue, alpha
	
	public Color3f() {
		
	}
	
	public Color3f(final Color color) {
		this.set(color);
	}
	
	public Color3f(final Color3f copy) {
		this.set(copy.color[0], copy.color[1], copy.color[2], copy.color[3]);
	}
	
	public Color3f(final float red, final float green, final float blue) {
		this.set(red, green, blue, 1f);
	}
	
	public Color3f(final float red, final float green, final float blue, final float alpha) {
		this.set(red, green, blue, alpha);
	}
	
	public void set(final Color color) {
		color.getComponents(Color3f.scratchArray);
		this.set(Color3f.scratchArray[0], Color3f.scratchArray[1], Color3f.scratchArray[2], Color3f.scratchArray[3]);
	}
	
	/**
	 * Clamped to [(0f,0f,0f,0f),(1f,1f,1f,1f)]. 
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	public void set(final float red, final float green, final float blue, final float alpha) {
		this.color[0] = red;
		this.color[1] = green;
		this.color[2] = blue;
		this.color[3] = alpha;
		
		for(int i = 0; i < this.color.length; i++) {
			if(this.color[i] < 0f) {
				this.color[i] = 0f;
			} else if(this.color[i] > 1f) {
				this.color[i] = 1f;
			}
		}
	}
	
	public String toString() {
		return this.color[0] + "," + this.color[1] + "," + this.color[2] + "," + this.color[3];
	}

	public int compareTo(final Object o) {
		return this.toString().compareTo(((Color3f)o).toString());
	}
}
