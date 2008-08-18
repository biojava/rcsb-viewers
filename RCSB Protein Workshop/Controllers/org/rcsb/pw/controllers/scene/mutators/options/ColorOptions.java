package org.rcsb.pw.controllers.scene.mutators.options;

import java.awt.Color;

public class ColorOptions {
	
	private Color currentColor = Color.white;
	

	public Color getCurrentColor() {
		return this.currentColor;
	}

	public void setCurrentColor(final Color currentColor) {
		this.currentColor = currentColor;
	}
}
