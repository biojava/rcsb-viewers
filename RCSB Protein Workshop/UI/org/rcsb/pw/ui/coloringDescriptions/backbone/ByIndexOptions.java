package org.rcsb.pw.ui.coloringDescriptions.backbone;

import org.rcsb.mbt.model.attributes.InterpolatedColorMap;

public class ByIndexOptions {
	private InterpolatedColorMap currentColorMap = null;
	
	public InterpolatedColorMap getCurrentColorMap() {
		return this.currentColorMap;
	}
	
	public void setCurrentColorMap(final InterpolatedColorMap currentColorMap) {
		this.currentColorMap = currentColorMap;
	}
}
