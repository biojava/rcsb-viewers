package org.rcsb.pw.controllers.scene.mutators.options;

import java.awt.Color;

import org.rcsb.mbt.model.attributes.LineStyle;
import org.rcsb.mbt.model.geometry.Point3d;



public class LinesOptions {
	private Point3d firstPoint = null;
	private Point3d secondPoint = null;
	private String firstDescription = null;
	private String secondDescription = null;
	private int lineStyle = LineStyle.SOLID;
	private float[] color = {0, 0, 0, 0};
	private boolean displayDistance = false;
	
	public LinesOptions() {
		Color.WHITE.getColorComponents(this.color);
	}
	

	public String getFirstDescription() {
		return this.firstDescription;
	}

	public void setFirstDescription(final String firstDescription) {
		this.firstDescription = firstDescription;
	}

	public Point3d getFirstPoint() {
		return this.firstPoint;
	}

	public void setFirstPoint(final double[] point) {
		this.firstPoint = new Point3d((float)point[0], (float)point[1], (float)point[2]);
	}

	public String getSecondDescription() {
		return this.secondDescription;
	}

	public void setSecondDescription(final String secondDescription) {
		this.secondDescription = secondDescription;
	}

	public Point3d getSecondPoint() {
		return this.secondPoint;
	}

	public void setSecondPoint(final double[] point) {
		this.secondPoint = new Point3d((float)point[0], (float)point[1], (float)point[2]);
	}

	public int getLineStyle() {
		return this.lineStyle;
	}

	public void setLineStyle(final int lineStyle) {
		this.lineStyle = lineStyle;
	}

	public float[] getColor() {
		return this.color;
	}

	public void setColor(final float[] color) {
		this.color = color;
	}


	public boolean isDisplayDistance() {
		return this.displayDistance;
	}


	public void setDisplayDistance(final boolean displayDistance) {
		this.displayDistance = displayDistance;
	}
}
