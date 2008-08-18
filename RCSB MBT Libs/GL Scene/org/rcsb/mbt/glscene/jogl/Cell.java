package org.rcsb.mbt.glscene.jogl;

/**
 * Represents an orthogonal cell that encompasses something. Usually used for transformation matrices.
 * @author John Beaver
 *
 */
public class Cell {
	public float[] upperLeftCorner = new float[3];
	public float[] lowerLeftConer = new float[3];
}
