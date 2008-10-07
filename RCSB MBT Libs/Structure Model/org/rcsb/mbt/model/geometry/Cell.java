package org.rcsb.mbt.model.geometry;

/**
 * Represents an orthogonal cell that encompasses something. Usually used for transformation matrices.
 * @author John Beaver
 *
 */
public class Cell {
	public float[] upperLeftCorner = new float[3];
	public float[] lowerLeftCorner = new float[3];
}
