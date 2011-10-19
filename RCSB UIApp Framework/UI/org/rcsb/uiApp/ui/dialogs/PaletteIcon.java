package org.rcsb.uiApp.ui.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

import org.rcsb.mbt.model.attributes.ColorBrewer;

public class PaletteIcon implements Icon {
	private ColorBrewer brewer;
	private int colorCount;
	private int width;
	private int height;
	private Color[] colors;
	
	public PaletteIcon(ColorBrewer brewer, int colorCount, int width, int height) {
		this.brewer = brewer;
		this.colorCount = colorCount;
		this.width = width;
		this.height= height;
	}
	
	public void paintIcon(Component c, Graphics g, int x, int y) {
		this.colors = brewer.getColorPalette(colorCount);
		
		for (int i = 0; i < colorCount; i++) {
			g.setColor(colors[i]);
			g.fillRect(x, y+i*height, width, height);
			g.setColor(Color.BLACK);
			g.drawRect(x, y+i*height, width, height);
		}
	}
	
	public int getIconWidth() {
		return width;
	}
	
	public int getIconHeight() {
		return height * colorCount;
	}
}
