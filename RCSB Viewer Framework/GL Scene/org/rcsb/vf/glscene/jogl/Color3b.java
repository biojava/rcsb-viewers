package org.rcsb.vf.glscene.jogl;

import java.awt.Color;

public class Color3b implements Comparable {
	public final byte[] color = {0,0,0,(byte)255};
	private String colorString = null;
	
	public Color3b() {
		
	}
	
	public Color3b(final Color color) {
		this.set(color);
	}
	
	public Color3b(final Color3b copy) {
		this.set(copy.color[0], copy.color[1], copy.color[2], copy.color[3]);
	}
	
	public Color3b(final byte red, final byte green, final byte blue) {
		this.set(red, green, blue, (byte)255);
	}
	
	public Color3b(final byte red, final byte green, final byte blue, final byte alpha) {
		this.set(red, green, blue, alpha);
	}
	
	public void set(final Color color) {
		this.set((byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue(), (byte)color.getAlpha());
	}
	
	/**
	 * Clamped to [(0f,0f,0f,0f),(1f,1f,1f,1f)]. 
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	public void set(final byte red, final byte green, final byte blue, final byte alpha) {
		this.color[0] = red;
		this.color[1] = green;
		this.color[2] = blue;
		this.color[3] = alpha;
		
		for(int i = 0; i < this.color.length; i++) {
			if(this.color[i] < -126) {
				this.color[i] = -126;
			} else if(this.color[i] > 127) {
				this.color[i] = (byte)127;
			}
		}
		
		this.colorString = this.color[0] + "," + this.color[1] + "," + this.color[2] + "," + this.color[3]; 
	}
	
	public String toString() {
		return this.colorString;
	}

	public int compareTo(final Object o) {
		return this.toString().compareTo(((Color3b)o).toString());
	}
}
