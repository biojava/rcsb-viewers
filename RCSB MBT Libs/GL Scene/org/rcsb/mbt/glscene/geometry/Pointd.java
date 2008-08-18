package org.rcsb.mbt.glscene.geometry;

import java.util.Random;

import org.rcsb.mbt.glscene.jogl.Vector3f;
import org.rcsb.mbt.model.util.Algebra;


public class Pointd extends Vectord {
	protected Pointd() {}
	
	public Pointd(final int dimension) {
		super(dimension);
	}
	
	public Pointd(final Vectord copy) {
		super(copy.vector.length);
		for(int i = 0; i < super.vector.length; i++) {
			super.vector[i] = copy.vector[i];
		}
	}

	public void set(final Vectord v) {
		for(int i = 0; i < v.length(); i++) {
			this.vector[i] = v.vector[i];
		}
	}
	
	public void set(final Vector3f v) {
		for(int i = 0; i < v.coordinates.length; i++) {
			this.vector[i] = v.coordinates[i];
		}
	}
	
	public void set(final double[] vec) {
		this.vector = vec;
	}
	
	public void set(final Pointd p) {
		for(int i = 0; i < super.vector.length; i++) {
			super.vector[i] = p.vector[i];
		}
	}
	
	public Pointd addInPlace(final Pointd p) {
		for(int i = 0; i < super.vector.length; i++) {
			super.vector[i] += p.vector[i];
		}
		
		return this;
	}
	
	public double length() {
		return Vectord.length(super.vector.length, super.vector);
	}
	
	public double distance (final Pointd p)
	  { return Algebra.distance(super.vector, p.vector); }
	
	public double getNormSquared() {
		return Math.pow(this.getNormal(), 2);
	}
	
	public double getNormal() {
		return Vectord.innerProduct(super.vector.length,super.vector,super.vector);
	}

	public void scaleAndAdd(final Pointd point2, final double t, final Vectord direction) {
		this.setDimension(direction.vector.length); Vectord.addscale(super.vector.length,point2.vector,t,direction.vector,super.vector);
	}
	
	public void setDimension(final int dim) {
		if(super.vector != null && super.vector.length != dim) {
			super.vector = new double[dim];
		}
	}

	public void scaleAndSub(final Pointd pt, final double d, final Vectord normal) {
		this.setDimension(normal.getDimension()); Vectord.subscale(super.vector.length,pt.vector,d,normal.vector,super.vector);
	}
	
	public double innerProductWith(final Vectord v2) {
		return Vectord.innerProduct(super.vector.length,super.vector,v2.vector);
	}
	
	public void zero() {
		for(int i = 0; i < super.vector.length; i++) {
			super.vector[i] = 0;
		}
	}

	// static function getDimension
	// Result:
	// returns the dimension of the space
	// in which this type of point lies
	// Note:
	// the concept of dimensionality is
	// not very well defined at the moment.
	// The use of this method is strongly
	// discouraged!
	public int getDimension() {
		return super.vector.length;
	}

	// function distance2
	// Arguments:
	// p another point
	// Result:
	// this function returns the square of the Euclidean
	// distance from self to p.
	public double distance2(final Pointd p) {
		return Math.pow(this.distance(p), 2);
	}

	// procedure perturbe
	// Arguments:
	// r a "small" value
	//
	// this procedure perturbes each coordinate
	// of the point by at most 0.5*r in in the positive
	// or negative direction. The perturbed point
	// lies thus within a box with sides of length r
	// and center self; this implies that the the
	// perturbed point will be at most a Euclidean
	// distance of r*sqrt(3) away from the original point.
	public void perturbe( /* HvVector_Double. */final double r) {
		final Random rnd = new Random();
		for (int i = 0; i < this.getDimension(); ++i) {
			/* HvVector_Double. */double v = (rnd.nextInt() / 0x7fff * r);
			if (rnd.nextInt() > 0x7fff / 2) {
				v = -v;
			}
			this.vector[i] += v;
		}
	}
	
	public void add(final double v) {
		for(int i = 0; i < this.vector.length; i++) {
			this.vector[i] += v;
		}
	}

	// function powerDistance
	// Arguments:
	// p another point
	// Result:
	// this function returns what is called the power
	// distance between to points; in the case of
	// unweighted points, this distance is just the
	// square of the Euclidean distance
	public double powerDistance(final Pointd p) {
		return this.distance2(p);
	}

	// procedure computeEqPoint
	// Arguments:
	// p another point
	// q a place for the result
	//
	// this function computes the point q that lies
	// halfway between self and p.
	public void computeEqPoint(final Pointd p, final Pointd q) {
		q.set(p.add(this));
		q.multInPlace(0.5);
	}

	// function computeEqPoint
	// Arguments:
	// p another point
	// Result:
	// this function returns the point that
	// lies halfway betwen self and p.
	public Pointd computeEqPoint(final Pointd p) {
		final Pointd q = new Pointd(p.vector.length);
		this.computeEqPoint(p, q);
		return q;
	}
	
	public double getWeight() {
		return 0d;
	}
}
