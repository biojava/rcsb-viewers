package org.rcsb.mbt.model;

import org.rcsb.mbt.glscene.geometry.Point3d;

public class LineSegment extends StructureComponent {
	
	private Point3d firstPoint = null;
	private Point3d secondPoint = null;

	public LineSegment(final Point3d fPoint, final Point3d sPoint) {
		super();
		this.firstPoint = fPoint;
		this.secondPoint = sPoint;
		// TODO Auto-generated constructor stub
	}

	
	public void copy(final StructureComponent structureComponent) {
		// TODO Auto-generated method stub

	}

	
	public String getStructureComponentType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Point3d getFirstPoint() {
		return this.firstPoint;
	}
	
	public Point3d getSecondPoint() {
		return this.secondPoint;
	}

}
