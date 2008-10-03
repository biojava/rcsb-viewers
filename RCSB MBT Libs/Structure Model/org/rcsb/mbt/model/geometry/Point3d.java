package org.rcsb.mbt.model.geometry;


public class Point3d extends Pointd {
	public Point3d() {
		super(3);
	}
	
	public Point3d(final double[] pt) {
		super.vector = pt;
	}
	
	public Point3d(final Vectord copy) {
		super(copy);
	}
	
	public Point3d(final double x, final double y, final double z) {
		super(3);
		this.set(x,y,z);
	}

	public void set(final double x, final double y, final double z) {
		super.vector[0] = x;
		super.vector[1] = y;
		super.vector[2] = z;
	}
	
	/**
	 * Sets this vector to be the first three components of (v,1) * m where v is treated as a row vector.
	 * @param pt 
	 * @param m a 4x4 matrix
	 */
	public void xformPt(final Point3d pt, final Matrixd m) {
		final Vectord v = new Vectord(4);
		v.vector[0] = super.vector[0]; 
		v.vector[1] = super.vector[1];
		v.vector[2] = super.vector[2];
		v.vector[3] = 1;
		m.multLeft(v, this);
	}
}
