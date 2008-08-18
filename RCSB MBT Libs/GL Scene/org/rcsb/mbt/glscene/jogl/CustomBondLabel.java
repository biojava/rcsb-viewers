package org.rcsb.mbt.glscene.jogl;

import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.attributes.BondLabel;

public class CustomBondLabel implements BondLabel {
	private String label = null;
	
	public CustomBondLabel(final String label) {
		this.label = label;
	}
	
	public String getBondLabel(final Bond bond) {
		return this.label;
	}

	public void setBondLabel(final String label) {
		this.label = label;
	}
}
