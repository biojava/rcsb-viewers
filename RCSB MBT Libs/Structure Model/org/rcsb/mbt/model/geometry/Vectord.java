package org.rcsb.mbt.model.geometry;

import java.io.BufferedReader;
import java.io.PrintWriter;

import org.rcsb.mbt.model.cppConversion.BooleanReference;


public class Vectord {
	public double[] vector = null;
	
	protected Vectord() {}
	
	/** @memo create an uninitialized vector */
	public Vectord(final int dim) {
		this.vector = new double[dim];
	}
	
	public Vectord(final Vectord copy) {
		this.set(copy);
	}
	
	public void set(final Vectord copy) {
		this.copy(copy);
	}

	public void set(final int index, final double value) {
		this.vector[index] = value;
	}
	
	/** @memo return the dimension of this vector */
	public int getDimension() {
		return this.vector.length;
	}
	
	public Vectord negate() {
		final Vectord neg = new Vectord(this);
		for(int i = 0; i < neg.vector.length; i++) {
			neg.vector[i] = -neg.vector[i];
		}
		return neg;
	}

	/** @memo create a vector whose i_th component is x[i] */
	public Vectord(final double x[]) {
		Vectord.copy(this.vector.length, x, this.vector);
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

	/** @memo is the vector valid (i.e. contains no NAN numbers) */
	public boolean isValid() {
		for (int i = 0; i < this.getDimension(); ++i) {
			if (Double.isNaN(this.component(i))) {
				return false;
			}
		}
		return true;
	}

	/** @memo copy vector v into this vector */
	public void copy(final Vectord v) {
		Vectord.copy(this.getDimension(), v.vector, this.vector);
	}

	/** @memo copy the elements of x such that the i_th component is x[i] */
	public void copy(final double x[]) {
		Vectord.copy(this.getDimension(), x, this.vector);
	}

	/** @memo set the vector to the ZERO-vector, so that x+(*this) = x */
	public void zero() {
		Vectord.zero(this.getDimension(), this.vector);
	}

	/** @memo compute the inner product of this with v2 */
	public double innerProductWith(final Vectord v2) {
		return Vectord.innerProduct(this.getDimension(), this.vector, v2.vector);
	}

	/** @memo compute the inner product of this with v2 */
	public double mult(final Vectord v2) {
		return this.innerProductWith(v2);
	}

	/** @memo scale this uniformly by the scalar s */
	public Vectord multInPlace(final double s) {
		Vectord.mult(this.getDimension(), this.vector, s, this.vector);
		return this;
	}

	/** @memo scale this uniformly by the scalar 1/s */
	public Vectord divideInPlace(final double s) {
		return this.multInPlace(1.0 / s);
	}

	/** @memo scale v uniformly by the scalar s */
	public Vectord mult(final double s, final Vectord v) {
		return v.mult(s);
	}

	/** @memo compute the vector that is scaled uniformly by s */
	public Vectord mult(final double s) {
		final Vectord v = new Vectord(this.vector.length);
		Vectord.mult(this.getDimension(), this.vector, s, v.vector);
		return v;
	}

	/** @memo compute the vector that is scaled uniformly by 1/s */
	public Vectord divide(final double s) {
		return this.mult(1.0 / s);
	}

	/** @memo normalize the vector */
	public void normalize() {
		Vectord.normalize(this.getDimension(), this.vector, this.vector);
	}

	/** @memo return this vector normalized */
	public Vectord getNormalized() {
		final Vectord v = new Vectord(this.vector.length);
		Vectord.normalize(this.getDimension(), this.vector, v.vector);
		return v;
	}

	/** @memo set this to the difference vector v-w */
	public void sub(final Vectord v, final Vectord w) {
		Vectord.sub(this.getDimension(), v.vector, w.vector, this.vector);
	}

	/** @memo set this to the composite vector v+w */
	public void add(final Vectord v, final Vectord w) {
		Vectord.add(this.getDimension(), v.vector, w.vector, this.vector);
	}

	/** @memo set this to the vector u+s*w */
	public void scaleAndAdd(final Vectord u, final double s, final Vectord v) {
		Vectord.addscale(this.getDimension(), u.vector, s, v.vector, this.vector);
	}

	/** @memo set this to the vector u-s*w */
	public void scaleAndSub(final Vectord u, final double s, final Vectord v) {
		Vectord.subscale(this.getDimension(), u.vector, s, v.vector, this.vector);
	}

	/** @memo add the vector v to this */
	public Vectord addInPlace(final Vectord v) {
		Vectord.add(this.getDimension(), this.vector, v.vector, this.vector);
		return this;
	}

	/** @memo subtract the vector v from this */
	public Vectord subInPlace(final Vectord v) {
		Vectord.sub(this.getDimension(), this.vector, v.vector, this.vector);
		return this;
	}

	/** @memo return the vector that results from adding this and v */
	public Vectord add(final Vectord v) {
		final Vectord a = new Vectord(this.vector.length);
		a.add(this, v);
		return a;
	}

	/** @memo return the vector that results from subtracting v from this */
	public Vectord sub(final Vectord v) {
		final Vectord a = new Vectord(this.vector.length);
		a.sub(this, v);
		return a;
	}

	/** @memo return the inverted vector, i.e. component(i) becomes -component(i) */
	public Vectord sub() {
		final Vectord a = new Vectord(this.vector.length);
		Vectord.invert(this.getDimension(), this.vector, a.vector);
		return a;
	}

	/**
	 * @memo compare this vector with v
	 * @return false, if there exists an i, such that component(i) !=
	 *         v.component(i)
	 */
	public boolean equals(final Vectord v) {
		return Vectord.equal(this.getDimension(), this.vector, v.vector);
	}

	/** @memo return a pointer to an array double[dimension] */
	public double[] getVector() {
		return this.vector;
	}

	/** @memo compute the length of the vector, aka 2-norm */
	public double length() {
		return Vectord.length(this.getDimension(), this.vector);
	}

	/**
	 * @memo return the distance between this vector and v. This function is
	 *       equivalent to computing (operator-(v).length())
	 */
	public double distance(final Vectord v) {
		return Vectord.length(this.getDimension(), this.vector, v.vector);
	}

	/** @memo return the the square of the 2-norm */
	public double getNormSquared() {
		return Vectord.innerProduct(this.getDimension(), this.vector, this.vector);
	}

	/** @memo compute the angle between this and v */
	public double getAngle(final Vectord v) {
		return Vectord.getAngle(this.getDimension(), this.vector, v.vector);
	}

	/** @memo invert this vector, i.e. component(i) becomes -component(i) */
	public void invert() {
		Vectord.invert(this.getDimension(), this.vector, this.vector);
	}

	/** @memo a predefined output operator */
	public PrintWriter print(final PrintWriter out, final Vectord v) {
		return Vectord.print(this.getDimension(), out, v.vector);
	}

	public BufferedReader read(final BufferedReader in, final Vectord v) {
		return this.read(this.getDimension(), in, v.vector);
	}
	
	public BufferedReader read(final int dimension, final BufferedReader in, final double[] v) {
		// do something...
		return in;
	}

	/**
	 * @memo set this vector to the cross product of dimension-1 vectors;
	 * @param v
	 *            is an array of pointers to vectors, so that vector copying is
	 *            minimized
	 * @return returns a reference to this vector
	 */
	public Vectord setCrossProduct(final Vectord v[], final int unused) {
		if (this.vector.length - 1 == 0) {
			// HvVector v; // SKVINAY this is reallllllly bad coding :( why ?
			// v.zero();
			// return v;
			return this;
		} else {
			final double[][] m = new double[this.vector.length - 1][this.vector.length - 1];
			final int pivots[] = new int[this.vector.length - 1];
			for (int i = 0; i < this.vector.length; ++i) {
				// fill the matrix with the submatrix
				for (int col = 0; col < this.vector.length - 1; ++col) {
					int r = 0;
					for (int row = 0; row < this.vector.length; ++row) {
						if (row != i) {
							m[col][r++] = v[col].get(row);
						}
					}
				}
				// compute the determinant m
				this.vector[i] = Matrixd.factorize(this.vector.length - 1, pivots, m);
				if (i % 2 > 0) {
					this.vector[i] = -this.vector[i];
				}
			}
			return this;
		}
	}

	/**
	 * @memo set this vector to the cross product of dimension-1 vectors;
	 * @param v
	 *            is an array of vectors; this function calls setCrossProduct(
	 *            HvVector* v[dimension-1])!
	 * @return returns a reference to this vector
	 */
	public Vectord setCrossProduct(final Vectord v[]) {
		final Vectord vecs[] = new Vectord[this.vector.length];
		for (int i = 0; i < this.vector.length - 1; ++i) {
			vecs[i] = v[i];
		}
		return this.setCrossProduct(vecs, -1);
	}

	// compute the vector n from this to the subspace
	// spanned by the given vectors
	// n will be orthogonal to the subspace
	// note: d vectors define a d-1 dimensional subspace!
	// returns the length of n. if the projection point
	// is inside the hull of the subspace, then the length is positive
	// otherwise it is negative
	public double computeProjectionVector(final Vectord v[], final int _dimension, final Vectord n,
			final BooleanReference inside) {
		final Vectord _v[] = new Vectord[this.vector.length + 1];
		for (int i = 0; i < _dimension; ++i) {
			_v[i] = v[i];
		}
		return this.computeProjectionVector(_v, _dimension, n, inside, -1);
	}

	public double computeProjectionVector(final Vectord v[], final int _dimension, final Vectord n) {
		final BooleanReference b = new BooleanReference();
		return this.computeProjectionVector(v, _dimension, n, b);
	}

	public double computeProjectionVector(final Vectord v[], final int dim, final Vectord n,
			final BooleanReference inside, final int unused) {
		final MaxDimMatrixd A = new MaxDimMatrixd(this.vector.length + dim, 2 * this.vector.length);
		final MaxDimVectord b = new MaxDimVectord(this.vector.length + dim, 2 * this.vector.length);
		final MaxDimVectord x = new MaxDimVectord(this.vector.length + dim, 2 * this.vector.length);
		// setup the matrix
		{
			// set the left of the matrix
			{
				for (int j = 0; j < dim; ++j) {
					int i;
					for (i = 0; i < this.vector.length; ++i) {
						A.set(i, j, v[j].get(i));
					}
					A.set(this.vector.length, j,1.0);
					for (i = this.vector.length + 1; i < this.vector.length + dim; ++i) {
						A.set(i, j, 0.0);
					}
				}
			}
			// set the upper right of the matrix
			{
				int j = dim;
				for (int i = 0; i < this.vector.length; ++i) {
					for (int k = dim; k < dim + this.vector.length; ++k) {
						A.set(i, k,0.0);
					}
					A.set(i, j++,-1.0);
					b.set(i,this.vector[i]);
				}
				for (int k = dim; k < dim + this.vector.length; ++k) {
					A.set(this.vector.length, k,0.0);
				}
				b.set(this.vector.length,1.0);
			}
			// set the lower right of the matrix
			{
				for (int j = this.vector.length + 1; j < this.vector.length + dim; ++j) {
					b.set(j,0.0);
					for (int i = 0; i < this.vector.length; ++i) {
						A.set(j, i + dim,v[j - this.vector.length].get(i) - v[0].get(i));
					}
				}
			}
		}
		// (cerr << "computeProjectionVector: " << A << '\n').flush();
		if (!A.solve(b, x)) {
			inside.value = false;
			return 0.0;
		}
		// compute whether the projection point is inside the
		// the bounded subspace (hull)
		// and compute the normal and the distance vectors
		{
			int i;
			double dist = 0.0;
			int j = dim;
			for (i = dim; i < this.vector.length + dim; ++i) {
				n.getVector()[j++] = x.get(i);
				dist += x.get(i) * x.get(i);
			}
			dist = Math.sqrt(dist);
			inside.value = true;
			for (i = 0; i < dim; ++i) {
				if (x.get(i) < 0.0) {
					inside.value = false;
					break;
				}
			}
			return dist;
		}
	}
	
//	 BufferedReader read (long dim, BufferedReader in , double[] v )
	// {
	// for (long i=0;i<dim && !in.eof();++i)
	// in >> v[i];
	// return in;
	// }
	public static PrintWriter print(final int dim, final PrintWriter out, final double[] v) {
		// int precision = out.precision(4);
		// int flags = out.setf(ios.fixed,ios.floatfield);
		// out << '{';
		for (int i = 0; i < dim - 1; ++i) {
			// out.width(8);
			out.print(v[i] + "   ");
		}
		// out.width(8);
		out.print('\t' + v[dim - 1]);
		// out <<" }";
		// out.precision(precision);
		// out.setf(ios.fixed,flags);
		out.flush();
		return out;
	}

	 public static void copy (final int dim, final double[] from, final double[] to)
	 { for (int i=0;i<dim;++i) {
		to[i]=from[i];
	} }
	public static void zero(final long dim, final double[] v) {
		for (int i = 0; i < dim; ++i) {
			v[i] = 0.0;
		}
	}

	public static double innerProduct(final long dim, final double[] v1, final double[] v2) {
		double val = v1[0] * v2[0];
		for (int i = 1; i < dim; ++i) {
			val += v1[i] * v2[i];
		}
		return val;
	}

	public static void outerProduct(final int dim, final double[] v1, final double[] v2, final double[][] out) {
		for (int i = 0; i < dim; ++i) {
			for (int j = 0; j < dim; ++j) {
				out[i][j] = v1[i] * v2[j];
			}
		}
	}

	public static void normalize(final long dim, final double u[], final double v[]) {
		final double len = Vectord.length(dim, u);
		for (int i = 0; i < dim; ++i) {
			v[i] /= len;
		}
	}

	public static void add(final long dim, final double[] u, final double[] v, final double[] w) {
		for (int i = 0; i < dim; ++i) {
			w[i] = u[i] + v[i];
		}
	}

	public static void addscale(final long dim, final double[] u, final double s, final double[] v, final double[] w) {
		for (int i = 0; i < dim; ++i) {
			w[i] = u[i] + s * v[i];
		}
	}

	public static void subscale(final long dim, final double[] u, final double s, final double[] v, final double[] w) {
		for (int i = 0; i < dim; ++i) {
			w[i] = u[i] - s * v[i];
		}
	}

	public static void sub(final long dim, final double[] u, final double[] v, final double[] w) {
		for (int i = 0; i < dim; ++i) {
			w[i] = u[i] - v[i];
		}
	}

	public static void mult(final long dim, final double[] v, final double a, final double[] w) {
		for (int i = 0; i < dim; ++i) {
			w[i] = a * v[i];
		}
	}

	public static boolean equal(final long dim, final double[] u, final double[] v) {
		for (int i = 0; i < dim; ++i) {
			if (u[i] != v[i]) {
				return false;
			}
		}
		return true;
	}

	public static double length(final long dim, final double[] v) {
		return Math.sqrt(Vectord.innerProduct(dim, v, v));
	}

	public static double length(final long dim, final double[] u, final double[] v) {
		double len = u[0] - v[0];
		len *= len;
		for (int i = 1; i < dim; ++i) {
			final double t = u[i] - v[i];
			len += t * t;
		}
		return Math.sqrt(len);
	}

	public static double getAngle(final long dim, final double[] u, final double[] v) {
		final double _u = Vectord.innerProduct(dim, u, u);
		final double _v = Vectord.innerProduct(dim, v, v);
		final double _w = Vectord.innerProduct(dim, u, v);
		return Math.acos(_w / Math.sqrt(_u * _v));
	}

	public static void invert(final long dim, final double u[], final double v[]) {
		for (int i = 0; i < dim; ++i) {
			v[i] = -u[i];
		}
	}
}
