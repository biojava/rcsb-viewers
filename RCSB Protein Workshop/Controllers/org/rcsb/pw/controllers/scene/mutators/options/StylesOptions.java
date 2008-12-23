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
package org.rcsb.pw.controllers.scene.mutators.options;

import org.rcsb.mbt.model.attributes.AtomRadiusByScaledCpk;
import org.rcsb.mbt.model.attributes.IAtomRadius;
import org.rcsb.vf.glscene.jogl.ChainGeometry;
import org.rcsb.vf.glscene.jogl.Geometry;
import org.rcsb.vf.glscene.jogl.ChainGeometry.RibbonForm;


public class StylesOptions {
	
	public static final IAtomRadius DEFAULT_ATOM_RADIUS = AtomRadiusByScaledCpk.create();
	public static final int DEFAULT_ATOM_FORM = Geometry.FORM_THICK;
	public static final boolean DEFAULT_IS_BOND_ORDER_SHOWN = true;
	public static final boolean DEFAULT_RIBBON_SMOOTHING = true;
	public static final RibbonForm DEFAULT_RIBBON_FORM = RibbonForm.RIBBON_TRADITIONAL;
	
	private RibbonForm currentRibbonForm = StylesOptions.DEFAULT_RIBBON_FORM;
	private IAtomRadius currentAtomRadius = StylesOptions.DEFAULT_ATOM_RADIUS;
	private int currentAtomForm = StylesOptions.DEFAULT_ATOM_FORM;
	private boolean isBondOrderShown = StylesOptions.DEFAULT_IS_BOND_ORDER_SHOWN;
	
	private boolean areRibbonsSmoothed = StylesOptions.DEFAULT_RIBBON_SMOOTHING;
	


	public int getCurrentAtomForm() {
		return this.currentAtomForm;
	}

	public void setCurrentAtomForm(final int currentAtomForm) {
		if(currentAtomForm != Geometry.FORM_POINTS && currentAtomForm != Geometry.FORM_THICK) {
			(new Exception(currentAtomForm + " not a valid atom form")).printStackTrace();
			return;
		}
		
		this.currentAtomForm = currentAtomForm;
	}


	public IAtomRadius getCurrentAtomRadius() {
		return this.currentAtomRadius;
	}


	public void setCurrentAtomRadius(final IAtomRadius atomRadius) {
		this.currentAtomRadius = atomRadius;
	}

	public boolean isBondOrderShown() {
		return this.isBondOrderShown;
	}

	public void setShowBondOrder(final boolean isBondOrderShown) {
		this.isBondOrderShown = isBondOrderShown;
	}


	public boolean areRibbonsSmoothed() {
		return this.areRibbonsSmoothed;
	}


	public void setAreRibbonsSmoothed(final boolean areRibbonsSmoothed) {
		this.areRibbonsSmoothed = areRibbonsSmoothed;
	}


	public RibbonForm getCurrentRibbonForm() {
		return this.currentRibbonForm;
	}


	public void setCurrentRibbonForm(final RibbonForm currentRibbonForm)
	{
		this.currentRibbonForm = currentRibbonForm;
	}	
}
