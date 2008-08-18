package org.rcsb.mbt.model.attributes;

import org.rcsb.mbt.model.Residue;

public class ResidueLabelNull implements IResidueLabel {

	public String getResidueLabel(final Residue res) {
		return null;
	}

	public void setResidueLabel(final Residue res, final String label) {
		// do nothing.
	}

}
