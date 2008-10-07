package org.rcsb.mbt.glscene.geometry;

import java.io.PrintWriter;


public class MaxDimVectord extends Vectord {
	public int maxDim;

	/**
	 * @memo this constant MUST be defined by all vectors, indicating the
	 *       maximal dimension that the point can have
	 */
	public int MAXDIM/* =maxDim */;

	/** @memo create an uninitialized vector */
	public MaxDimVectord(final int maxDim) {
		this.maxDim = maxDim;
		this.init(0);
	}

	/** @memo create an uninitialized vector */
	public MaxDimVectord(final int d, final int maxDim) {
		this.maxDim = maxDim;
		this.init(d);
	}

	/** @memo create a vector of dim numbers (NOT yet implemented) */
	// HvMaxDimVector (long d, HvDouble v0 ...)
	// {
	// init(d);
	// va_list ap;
	// ( ap = (va_list)&v0 + ( (sizeof(v0) + sizeof(int) - 1) & ~(sizeof(int) -
	// 1) ) );
	// vector[0] = (static_cast<double>(v0));
	// int i=1;
	// while (i<dimension)
	// vector[i++] =(static_cast<double>(( *(HvDouble *)((ap += ( (sizeof(HvDouble) +
	// sizeof(int) - 1) & ~(sizeof(int) - 1) )) - ( (sizeof(HvDouble) +
	// sizeof(int) - 1) & ~(sizeof(int) - 1) )) )));
	// ( ap = (va_list)0 );
	// }
	/** @memo create a vector whose i_th component is x[i] */
	public MaxDimVectord(final int d, final double x[], final int maxDim) {
		this.maxDim = maxDim;
		this.init(d);
		this.copy(x);
	}

	/** @memo a copy conststructor */
	public MaxDimVectord(final MaxDimVectord v, final int maxDim) {
		this.maxDim = maxDim;
		this.init(v.getDimension());
		this.copy(v.getVector());
	}

	/** @memo Access component i (note: no bounds checks are performed!) */
	public double get(final int i) {
		return this.vector[i];
	}

	/** @memo Access component i (note: no bounds checks are performed!) */
	public double parens(final int i) {
		return this.vector[i];
	}

	/** @memo Access component i (note: no bounds checks are performed!) */
	public double component(final int i) {
		return this.vector[i];
	}

	/** @memo copy vector v into this vector */
	public void copy ( final MaxDimVectord v)
{ this.setDimension(v.getDimension()); Vectord/*<double>*/.copy(this.dim,v.vector,this.vector); }

	/** @memo copy the elements of x such that the i_th component is x[i] */
	public void copy(final double x[]) {
		Vectord.copy(this.dim, x, this.vector);
	}

	/** @memo set the vector to the ZERO-vector, so that x+(*this) = x */
	public void zero() {
		Vectord.zero(this.dim, this.vector);
	}

	/** @memo compute the inner product of this with v2 */
	public double innerProductWith(final MaxDimVectord v2) {
		return Vectord.innerProduct(this.dim, this.vector, v2.vector);
	}

	/** @memo compute the inner product of this with v2 */
	public double mult(final MaxDimVectord v2) {
		return this.innerProductWith(v2);
	}

	/** @memo scale this uniformly by the scalar s */
//	public MaxDimVectord multInPlace(double s) {
//		Vectord.mult(dim, vector, s, vector);
//		return this;
//	}

	/** @memo scale this uniformly by the scalar 1/s */
//	public MaxDimVectord divideInPlace(double s) {
//		return multInPlace(1.0 / s);
//	}

	/** @memo scale v uniformly by the scalar s */
//	public MaxDimVectord mult(double s, MaxDimVectord v) {
//		return v.mult(s);
//	}

	/** @memo compute the vector that is scaled uniformly by s */
//	public MaxDimVectord mult(double s) {
//		MaxDimVectord v = new MaxDimVectord(getDimension());
//		Vectord.mult(dim, vector, s, v.vector);
//		return v;
//	}

	/** @memo compute the vector that is scaled uniformly by 1/s */
//	public MaxDimVectord divide(double s) {
//		return mult(1.0 / s);
//	}

	/** @memo normalize the vector */
	public void normalize() {
		Vectord.normalize(this.dim, this.vector, this.vector);
	}

	/** @memo return this vector normalized */
//	public MaxDimVectord getNormalized() {
//		MaxDimVectord v = new MaxDimVectord(getDimension());
//		Vectord.normalize(dim, vector, v.vector);
//		return v;
//	}

	/** @memo set this to the difference vector v-w */
	public void sub(final MaxDimVectord v, final MaxDimVectord w) {
		this.setDimension(v.getDimension());
		Vectord.sub(this.dim, v.vector, w.vector, this.vector);
	}

	/** @memo set this to the composite vector v+w */
	public void add(final MaxDimVectord v, final MaxDimVectord w) {
		this.setDimension(v.getDimension());
		Vectord.add(this.dim, v.vector, w.vector, this.vector);
	}

	/** @memo set this to the vector u+s*w */
	public void scaleAndAdd(final MaxDimVectord u, final double s, final MaxDimVectord v) {
		this.setDimension(v.getDimension());
		Vectord.addscale(this.dim, u.vector, s, v.vector, this.vector);
	}

	/** @memo set this to the vector u-s*w */
	public void scaleAndSub(final MaxDimVectord u, final double s, final MaxDimVectord v) {
		this.setDimension(v.getDimension());
		Vectord.subscale(this.dim, u.vector, s, v.vector, this.vector);
	}

	/** @memo add the vector v to this */
	public MaxDimVectord addInPlace(final MaxDimVectord v) {
		Vectord.add(this.dim, this.vector, v.vector, this.vector);
		return this;
	}

	/** @memo subtract the vector v from this */
	public MaxDimVectord subtractInPlace(final MaxDimVectord v) {
		Vectord.sub(this.dim, this.vector, v.vector, this.vector);
		return this;
	}

	/** @memo return the vector that results from adding this and v */
	public MaxDimVectord add(final MaxDimVectord v) {
		final MaxDimVectord a = new MaxDimVectord(this.maxDim);
		a.add(this, v);
		return a;
	}

	/** @memo return the vector that results from subtracting v from this */
	public MaxDimVectord negate(final MaxDimVectord v) {
		final MaxDimVectord a = new MaxDimVectord(this.maxDim);
		a.sub(this, v);
		return a;
	}

	/** @memo return the inverted vector, i.e. component(i) becomes -component(i) */
//	public MaxDimVectord negate() {
//		MaxDimVectord a = new MaxDimVectord(getDimension(), this.maxDim);
//		Vectord.invert(dim, vector, a.vector);
//		return a;
//	}

	/**
	 * @memo compare this vector with v
	 * @return FALSE, if there exists an i, such that component(i) !=
	 *         v.component(i)
	 */
	public boolean equals(final MaxDimVectord v) {
		return Vectord.equal(this.dim, this.vector, v.vector);
	}

	/** @memo a custom copy operator */
	public MaxDimVectord set(final MaxDimVectord v) {
		this.copy(v);
		return this;
	}
	
	public void set(final int i, final double val) {
		this.vector[i] = val;
	}

	/** @memo return a pointer to an array double[dim] */
	public double[] getVector() {
		return this.vector;
	}

	/** @memo compute the length of the vector, aka 2-norm */
	public double length() {
		return Vectord.length(this.dim, this.vector);
	}

	/**
	 * @memo return the distance between this vector and v. This function is
	 *       equivalent to computing (operator-(v).length())
	 */
	public double distance(final MaxDimVectord v) {
		return Vectord.length(this.dim, this.vector, v.vector);
	}

	/** @memo return the the square of the 2-norm */
	public double getNormSquared() {
		return Vectord.innerProduct(this.dim, this.vector, this.vector);
	}

	/** @memo compute the angle between this and v */
	public double getAngle(final MaxDimVectord v) {
		return Vectord.getAngle(this.dim, this.vector, v.vector);
	}

	public void setDimension(final int d) {
		this.dim = d;
	}

	/** @memo return the dimension of this vector */
	public int getDimension() {
		return this.dim;
	}

	/** @memo invert this vector, i.e. component(i) becomes -component(i) */
	public void invert() {
		Vectord.invert(this.dim, this.vector, this.vector);
	}

	/** @memo a predefined output operator */
	public PrintWriter print(final PrintWriter out, final MaxDimVectord v) {
		return Vectord.print(v.getDimension(), out, v.vector);
	}

	/**
	 * @memo set this vector to the cross product of dim-1 vectors;
	 * @param v
	 *            is an array of vectors; this function calls setCrossProduct(
	 *            HvMaxDimVector* v[dim-1])!
	 * @return returns a reference to this vector
	 */
	public MaxDimVectord setCrossProduct(final MaxDimVectord v[]) {
		this.setDimension(v[0].getDimension());
		final MaxDimVectord[] vecs = new MaxDimVectord[this.dim - 1];
		for (int i = 0; i < this.dim - 1; ++i) {
			vecs[i] = v[i];
		}
		return this.setCrossProduct(vecs, -1);
	}

	public int dim;

	public void init(final int d) {
		this.setDimension(d);
		super.vector = new double[this.maxDim];
	}

	public MaxDimVectord/*<maxDim>*/ setCrossProduct(final MaxDimVectord v[], final int unused) {
		final double m[][] = new double[this.maxDim][this.maxDim];
		final int pivots[] = new int[this.maxDim];
		this.setDimension(v[0].getDimension());
		for (int i = 0; i < this.dim; ++i) {
			// fill the matrix with the submatrix
			for (int col = 0; col < this.dim - 1; ++col) {
				int r = 0;
				for (int row = 0; row < this.dim; ++row) {
					if (row != i) {
						m[col][r++] = v[col].get(row);
					}
				}
			}
			// compute the determinant m
			super.vector[i] = Matrixd.factorize(this.dim - 1, pivots, m);
			if (i % 2 > 0) {
				super.vector[i] = -super.vector[i];
			}
		}
		return this;
	}
}
