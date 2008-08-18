package org.rcsb.mbt.model.attributes;

public class LineStyle extends Style {
	
	public static final int SOLID = 0;
	public static final int DOTTED = 1;
	public static final int DASHED = 2;
	public int lineStyle = LineStyle.SOLID;
	private float[] color = {0, 0, 0, 0};
	public String label = null;

	public LineStyle() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public boolean isTypeSafe(final String scType) {
		return true;
	}

	public float[] getColor() {
		return this.color;
	}

	public void setColor(final float[] color) {
		this.color = color;
	}

}
