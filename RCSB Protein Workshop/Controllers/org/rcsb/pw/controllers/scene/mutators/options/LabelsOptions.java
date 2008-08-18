package org.rcsb.pw.controllers.scene.mutators.options;

public class LabelsOptions {
	public static final int LABEL_BY_RESIDUE = 0;
	public static final int LABEL_BY_ATOM = 1;
	public static final int LABEL_CUSTOM = 2;
	public static final int LABELS_OFF = 3;
	
	public static final int DEFAULT_LABELING_STYLE = LabelsOptions.LABELS_OFF;
	private int currentLabellingStyle = LabelsOptions.DEFAULT_LABELING_STYLE;
	
	private String customLabel = "";
	
	
	public void setCurrentLabellingStyle(final int labelStyle) {
		switch(labelStyle) {
		case LABEL_BY_RESIDUE:
		case LABEL_BY_ATOM:
		case LABEL_CUSTOM:
		case LABELS_OFF:
			this.currentLabellingStyle = labelStyle;
			break;
		default:
			(new IllegalArgumentException("" + labelStyle)).printStackTrace();
		}
	}
	
	public int getCurrentLabellingStyle() {
		return this.currentLabellingStyle;
	}

	public String getCustomLabel() {
		return this.customLabel;
	}

	public void setCustomLabel(final String customLabel_) {
		String customLabel = customLabel_;
		if(customLabel == null) {
			customLabel = "";
		}
		
		this.customLabel = customLabel;
	}
}
