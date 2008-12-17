package org.rcsb.vf.glscene.jogl;

import java.awt.Color;

public class Color3f implements Comparable {
	public final float[] color = {0,0,0,1};
	
	private static final float[] scratchArray = {0,0,0,1};	// red, green, blue, alpha
	
	public Color3f() {
		
	}
	
	public Color3f(final Color color) {
		this.set(color);
	}
	
	public Color3f(final Color3f copy) {
		this.set(copy.color[0], copy.color[1], copy.color[2], copy.color[3]);
	}
	
	public Color3f(final float red, final float green, final float blue) {
		this.set(red, green, blue, 1f);
	}
	
	public Color3f(final float red, final float green, final float blue, final float alpha) {
		this.set(red, green, blue, alpha);
	}
	
	public void set(final Color color) {
		color.getComponents(Color3f.scratchArray);
		this.set(Color3f.scratchArray[0], Color3f.scratchArray[1], Color3f.scratchArray[2], Color3f.scratchArray[3]);
	}
	
	/**
	 * Clamped to [(0f,0f,0f,0f),(1f,1f,1f,1f)]. 
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	public void set(final float red, final float green, final float blue, final float alpha) {
		this.color[0] = red;
		this.color[1] = green;
		this.color[2] = blue;
		this.color[3] = alpha;
		
		for(int i = 0; i < this.color.length; i++) {
			if(this.color[i] < 0f) {
				this.color[i] = 0f;
			} else if(this.color[i] > 1f) {
				this.color[i] = 1f;
			}
		}
	}
	
	public String toString() {
		return this.color[0] + "," + this.color[1] + "," + this.color[2] + "," + this.color[3];
	}

	public int compareTo(final Object o) {
		return this.toString().compareTo(((Color3f)o).toString());
	}
}
