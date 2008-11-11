package org.rcsb.mbt.model;

import org.rcsb.mbt.glscene.geometry.Point3d;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;

/**
 * Simple line segment, defining two points.
 * @author rickb
 *
 */
/*
 * Not the best of situations, because it relies on OpenGl Point3d, at the moment.
 * 
 * Points are used in calculations all over the place, so whatever solution is
 * used to abstract this from the OpenGl implementation, it shouldn't result in copies
 * when it has to go between model and OGL.
 * 
 * Have to do better than that.
 * 
 * 27-Oct-08 - rickb
 * 
 */
public class LineSegment extends StructureComponent {
	
	private Point3d firstPoint = null;
	private Point3d secondPoint = null;

	public LineSegment(final Point3d fPoint, final Point3d sPoint)
	{
		super();
		this.firstPoint = fPoint;
		this.secondPoint = sPoint;
	}

	
	public void copy(final StructureComponent structureComponent)
	{

	}
	
	public ComponentType getStructureComponentType() {
		return null;
	}
	
	public Point3d getFirstPoint() {
		return this.firstPoint;
	}
	
	public Point3d getSecondPoint() {
		return this.secondPoint;
	}

}
