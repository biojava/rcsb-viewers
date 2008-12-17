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
