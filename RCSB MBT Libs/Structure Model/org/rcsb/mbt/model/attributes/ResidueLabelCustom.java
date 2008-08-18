package org.rcsb.mbt.model.attributes;

import java.util.HashMap;

import org.rcsb.mbt.model.Residue;


public class ResidueLabelCustom implements IResidueLabel {
	// key: Residue, value: String label
	private final HashMap<Residue, String> residueLabels = new HashMap<Residue, String>();
	private static ResidueLabelCustom singleton = null;
	
	// make sure this class can only be instantiated as a singleton.
	private ResidueLabelCustom() {
		
	}
	
	public static ResidueLabelCustom getSingleton() {
		if(ResidueLabelCustom.singleton == null) {
			ResidueLabelCustom.singleton = new ResidueLabelCustom();
		}
		return ResidueLabelCustom.singleton;
	}
	
	public String getResidueLabel(final Residue res) {
		return this.residueLabels.get(res);
	}

	public void setResidueLabel(final Residue res, final String label_) {
		String label = label_;
		if(label == null || (label = label.trim()).length() == 0) {
			this.residueLabels.remove(res);
		} else {
			this.residueLabels.put(res, label);
		}
	}
	
}
