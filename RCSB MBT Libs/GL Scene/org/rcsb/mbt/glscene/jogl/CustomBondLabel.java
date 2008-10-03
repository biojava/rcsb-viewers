package org.rcsb.mbt.glscene.jogl;

import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.attributes.IBondLabel;

public class CustomBondLabel implements IBondLabel {
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
