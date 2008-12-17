package org.rcsb.vf.glscene.jogl;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.attributes.IAtomLabel;

public class CustomAtomLabel implements IAtomLabel {
	private String label = null;
	
	public CustomAtomLabel(final String label) {
		this.label = label;
	}
	
	public String getAtomLabel(final Atom atom) {
		return this.label;
	}

	public void setAtomLabel(final String label) {
		this.label = label;
	}
}
