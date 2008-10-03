package org.rcsb.mbt.model.geometry;

import java.io.PrintWriter;

import org.rcsb.mbt.model.cppConversion.DoubleReference;
import org.rcsb.mbt.model.cppConversion.IntArrayReference;
import org.rcsb.mbt.model.cppConversion.IntReference;

public class Matrixd {
	public static void identity(final int dim, final double[][] v) {
		Matrixd.identity(dim, dim, v);
	}

	public static void setRowVector(final int dim, final int i, final double[] v, final double[][] u) {
		Matrixd.setRowVector(dim, dim, i, v, u);
	}

	public static void getRowVector(final int dim, final int i, final double[][] v, final double[] u) {
		Matrixd.getRowVector(dim, dim, i, v, u);
	}

	public static void setColumnVector(final int dim, final int j, final double[] v, final double[][] u) {
		Matrixd.setColumnVector(dim, dim, j, v, u);
	}

	public static void getColumnVector(final int dim, final int j, final double[][] v, final double[] u) {
		Matrixd.getColumnVector(dim, dim, j, v, u);
	}

	public static void transpose(final int dim, final double M[][], final double N[][]) {
		Matrixd.transpose(dim, dim, M, N);
	}

	public static void square(final int dim, final double M[][], final double N[][]) {
		Matrixd.mult(dim, dim, dim, M, M, N);
	}

	// rotate around one of the coordinate axis
	// center of rotation is the origin
	public static void setRotation(final int unused, final int axis, final double angle,
			final double[] unused2) {
	}

	// rotate around the given axis by the given angle
	// center of rotation is the origin
	public static void setRotation(final int unused, final double[] axis, final double angle,
			final double[] unused2) {
	}

	// rotate around the given axis by the given angle.
	// center is the center of rotation
	public static void setRotation(final int unused, final double[] center, final double[] axis,
			final double angle, final double[] unused2) {
	}

	// rotate around the given axis by the given angle.
	// center is the center of rotation
	public static void setRotation(final int unused, final double[] center, final int axis,
			final double angle, final double[] unused2) {
	}

	public static void add(final int dim, final double A[][], final double B[][], final double C[][]) {
		Matrixd.add(dim, dim, A, B, C);
	}

	public static void sub(final int dim, final double A[][], final double B[][], final double C[][]) {
		Matrixd.sub(dim, dim, A, B, C);
	}

	public static void multVectorLeft(final int dim, final double m[][], final double u[],
			final double v[]) {
		Matrixd.multVectorLeft(dim, dim, m, u, v);
	}

	public static void multVectorRight(final int dim, final double m[][], final double u[],
			final double v[]) {
		Matrixd.multVectorRight(dim, dim, m, u, v);
	}

	public static void mult(final int dim, final double m[][], final double s, final double n[][]) {
		Matrixd.mult(dim, dim, m, s, n);
	}

	public static void mult(final int dim, final double A[][], final double B[][], final double C[][]) {
		Matrixd.mult(dim, dim, dim, A, B, C);
	}

	// copy the contents of an array of types into THIS matrix
	public static void copy(final int dim, final int rows, final double[][] from, final double[][] to) {
		for (int i = 0; i < dim; ++i) {
			for (int j = 0; j < rows; j++) {
				to[i][j] = from[i][j];
			}
		}
	}

	public static void copy(final int dim, final double[][] from, final double[][] to) {
		Matrixd.copy(dim, dim, from, to);
	}

	public static PrintWriter print(final int dim, final PrintWriter out, final double v[][]) {
		return Matrixd.print(dim, dim, out, v);
	}

	// public static int index(int dim, int i, int j) {
	// return i * dim + j;
	// }

	public static void setDeterminantVector(final int[] list, final int n) {
		for (int i = 0; i <= n; ++i) {
			list[i] = i;
		}
	}

	public static int linkUnlink(final IntArrayReference list, final int i) {
		final int p = list.get(i);
		list.set(i, list.get(p));
		list.set(p, i);
		return p;
	}

	public static void setRowVector(final int cols, final int unused, final int i, final double v[],
			final double A[][]) {
		final double[] a = A[i];
		for (int j = 0; j < cols; ++j) {
			a[j] = v[j];
		}
	}

	public static void getRowVector(final int cols, final int rows, final int i, final double A[][],
			final double v[]) {
		final double[] a = A[i];
		for (int j = 0; j < cols; ++j) {
			v[j] = a[j];
		}
	}

	public static void setColumnVector(final int cols, final int rows, final int i, final double v[],
			final double A[][]) {
		for (int j = 0; j < rows; ++j) {
			A[i][j] = v[j];
		}
	}

	public static void getColumnVector(final int cols, final int rows, final int i, final double A[][],
			final double v[]) {
		for (int j = 0; j < rows; ++j) {
			v[j] = A[i][j];
		}
	}

	public static void identity(final int cols, final int rows, final double m[][]) {
		int i, j;
		for (i = 0; i < cols; ++i) {
			for (j = 0; j < rows; j++) {
				if (i == j) {
					m[i][j] = 1.0;
				} else {
					m[i][j] = 0.0;
		// int r = cols < rows ? cols : rows;
		// for (i = 0; i < r; ++i)
		// m[i * (cols + 1)] = 1.0;
				}
			}
		}
	}

	public static void computeAtA(final int cols, final int rows, final double A[][],
			final double C[][]) {
		double tmp;
		int i, j, k;
		for (k = 0; k < cols; ++k) {
			for (i = 0; i < cols; ++i) {
				tmp = 0.0;
				for (j = 0; j < rows; j++) {
					tmp += A[j][i] * A[j][k];
				}
				C[i][k] = tmp;
			}
		}
	}

	public static void computeAtb(final int cols, final int rows, final double A[][], final double u[],
			final double v[]) {
		double tmp;
		int r, c;
		for (r = 0; r < cols; ++r) {
			tmp = A[0][r] * u[0];
			for (c = 1; c < rows; ++c) {
				tmp += A[c][r] * u[c];
			}
			v[r] = tmp;
		}
	}

	public static void mult(final int colsA, final int rowsA, final int colsB, final double A[][],
			final double B[][], final double C[][]) {
		double tmp;
		int i, j, k;
		for (k = 0; k < colsB; ++k) {
			for (i = 0; i < rowsA; ++i) {
				tmp = A[i][0] * B[0][k];
				for (j = 1; j < colsA; j++) {
					tmp += A[i][j] * B[j][k];
				}
				C[i][k] = tmp;
			}
		}
	}

	public static void multVectorRight(final int cols, final int rows, final double A[][],
			final double u[], final double v[]) {
		double tmp;
		int i, j;
		for (i = 0; i < rows; ++i) {
			tmp = A[i][0] * u[0];
			for (j = 1; j < cols; ++j) {
				tmp += A[i][j] * u[j];
			}
			v[i] = tmp;
		}
	}

	public static void multVectorLeft(final int cols, final int rows, final double A[][],
			final double u[], final double v[]) {
		double tmp;
		int i, j;
		for (j = 0; j < cols; ++j) {
			tmp = A[0][j] * u[0];
			for (i = 1; i < rows; ++i) {
				tmp += A[i][j] * u[j];
			}
			v[j] = tmp;
		}
	}

	public static void mult(final int cols, final int rows, final double A[][], final double s,
			final double C[][]) {
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; j++) {
				C[i][j] = A[i][j] * s;
			}
		}
	}

	public static void sub(final int cols, final int rows, final double A[][], final double B[][],
			final double C[][]) {
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; j++) {
				C[i][j] = A[i][j] - B[i][j];
			}
		}
	}

	public static void add(final int cols, final int rows, final double A[][], final double B[][],
			final double C[][]) {
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < cols; j++) {
				C[i][j] = A[i][j] + B[i][j];
			}
		}
	}

	public static void transpose(final int cols, final int rows, final double A[][], final double B[][]) {
		double tmp;
		int i, j;
		if (cols == rows) {
			for (i = 0; i < cols; i++) {
				for (j = i; j < cols; j++) {
					tmp = A[i][j];
					B[i][j] = A[j][i];
					B[j][i] = tmp;
				}
			}
		} else if (A != B) {
			for (i = 0; i < cols; i++) {
				for (j = 0; j < rows; j++) {
					B[i][j] = A[j][i];
				}
			}
		} else {
			System.err.print("transpose failed.\n");
		}
	}

	public static PrintWriter print(final int cols, final int rows, final PrintWriter out,
			final double A[][]) {
		int i, j;
		// int precision = out.precision(4);
		// int flags = out.setf(ios.fixed,ios.floatfield);
		out.print("{\n");
		for (i = 0; i < rows; i++) {
			for (j = 0; j < cols; j++) {
				// out.width(8);
				out.print(A[i][j] + ' ');
			}
			out.print('\n');
		}
		out.print("} ");
		// out.precision(precision);
		// out.setf(ios.fixed,flags);
		out.flush();
		return out;
	}

	public static boolean solveGauss(final int cols, final int p[], final double A[][],
			final double b[], final double x[]) {
		int i, j, k;
		int numSwaps = 0;
		double det = 1.0;
		// initialize p
		for (i = 0; i < cols; ++i) {
			p[i] = i;
		}
		// loop over all the columns k
		for (k = 0; k < cols; ++k) {
			// find the best pivot in column k
			int pivot = k; // the pivot element is at m[k][k]
			double pivot_element = Math.abs(A[p[pivot]][k]);
			for (i = k + 1; i < cols; ++i) {
				if (Math.abs(A[p[i]][k]) > pivot_element) {
					pivot = i;
					pivot_element = Math.abs(A[p[pivot]][k]);
				}
			}
			if (p[pivot] != p[k]) {
				final int tmp = p[pivot];
				p[pivot] = p[k];
				p[k] = tmp;
				// swap(p[pivot], p[k]);
				++numSwaps;
			}
			pivot = p[k];
			// update all rows k <= i < cols
			pivot_element = A[pivot][k];
			det *= pivot_element;
			// if the pivot element is 0.0 then swap it with the last row
			// and move to the next column
			if (pivot_element == 0.0) {
			} else {
				pivot_element = (-1.0) / pivot_element;
				for (i = k + 1; i < cols; ++i) {
					final int p_i = p[i];
					final double r = A[p_i][k] * pivot_element;
					// fill in the U part of m
					for (j = k + 1; j < cols; ++j) {
						A[p_i][j] += r * A[pivot][j];
					}
					b[p_i] += r * b[pivot];
				}
			}
		}
		// now, we can do the back substitution
		{
			i = cols - 1;
			while (true) {
				double tmp = 0;
				for (j = i + 1; j < cols; ++j) {
					tmp += A[p[i]][j] * x[p[j]];
				}
				x[i] = (b[p[i]] - tmp) / A[p[i]][i];
				if (i-- == 0) {
					break;
				}
			}
		}
		return numSwaps % 2 > 0 ? -det != 0 : det != 0;
	}

	public static int nullSpace(final int cols, final double A[], final double[][] nullSpace) {
		final int dim = 0;
		return dim;
	}

	public static double factorize(final int cols, final int p[], final double A[][]) {
		int i, j, k;
		int numSwaps = 0;
		double det = 1.0;
		double pivot_element;
		double tmp;
		// initialize p
		for (i = 0; i < cols; ++i) {
			p[i] = i;
		}
		// loop over all the columns k
		for (k = 0; k < cols; ++k) {
			// find the best pivot in column k
			int pivot = k; // the pivot element is at m[k][k]
			pivot_element = Math.abs(A[p[pivot]][k]);
			for (i = k + 1; i < cols; ++i) {
				tmp = Math.abs(A[p[i]][k]);
				if (tmp > pivot_element) {
					pivot = i;
					pivot_element = tmp;
				}
			}
			if (p[pivot] != p[k]) {
				final int tmp2 = p[pivot];
				p[pivot] = p[k];
				p[k] = tmp2;
				// swap(p[pivot], p[k]);
				++numSwaps;
			}
			pivot = p[k];
			// update all rows k <= i < cols
			pivot_element = A[pivot][k];
			det *= pivot_element;
			// if the pivot element is 0.0 then swap it with the last row
			// and move to the next column
			if (pivot_element == 0.0) {
				return 0.0;
			} else {
				pivot_element = (-1.0) / pivot_element;
				for (i = k + 1; i < cols; ++i) {
					final int p_i = p[i];
					double r = A[p_i][k] * pivot_element;
					// fill in the U part of m
					for (j = k + 1; j < cols; ++j) {
						A[p_i][j] += r * A[pivot][j];
					}
					// fill in the L part of m
					A[p_i][k] = -r;
				}
			}
		}
		return numSwaps % 2 == 1 ? -det : det;
	}

	public static void forwardSubstitution(final int cols, final int p[], final double A[][],
			final double b[], final double y[]) {
		int i, j;
		y[0] = b[p[0]];
		for (i = 1; i < cols; ++i) {
			double tmp = 0;
			for (j = 0; j < i; ++j) {
				tmp += A[p[i]][j] * y[j];
			}
			y[i] = b[p[i]] - tmp;
			// cerr << "Forward step #"<<i<<" Y " << y[i] << " B = " << b[p[i]]
			// << " TMP " << tmp << " B-TMP " << b[p[i]]-tmp <<'\n';
		}
	}

	public static void backwardSubstitution(final int cols, final int p[], final double A[][],
			final double y[], final double x[]) {
		int i, j;
		i = cols - 1;
		while (true) {
			double tmp = 0.0;
			for (j = i + 1; j < cols; ++j) {
				tmp += A[p[i]][j] * x[j];
			}
			x[i] = (y[i] - tmp) / A[p[i]][i];
			// cerr << "Backward step #" << i << " X " << x[i] << " Y " << y[i]
			// << " TMP " << tmp << " Y-TMP : "<< y[i]-tmp << " A " <<
			// A[index(cols,p[i],i)] << '\n';
			if (i-- == 0) {
				break;
			}
		}
	}

	public static void setScaling(final int cols, final double s, final double[][] m) {
		int i;
		for (i = 0; i < cols; ++i) {
			for (int j = 0; j < cols; j++) {
				m[i][j] = 0.0;
			}
		}
		for (i = 0; i < cols; ++i) {
			m[i][1] = s;
		}
	}

	public static void setScaling(final int cols, final double[] s, final double[][] m) {
		int i;
		for (i = 0; i < cols; ++i) {
			for (int j = 0; j < cols; j++) {
				m[i][j] = 0.0;
			}
		}
		for (i = 0; i < cols; ++i) {
			m[i + 1][0] = s[i];
		}
	}

	public static void setTranslation(final int cols, final double[] t, final double[][] m) {
		Matrixd.identity(cols, m);
		for (int i = 0; i < cols; ++i) {
			m[i][cols - 1] = t[i];
		}
	}

	public static double subDeterminant(final int dim, final int row,
			final IntArrayReference cols, final double[][] m) {
		// the basis case
		if (row == dim - 1) {
			return m[row][cols.get(-1)];
		}
		if (row == dim - 2) {
			final int c0 = cols.get(-1);
			final int c1 = cols.get(c0);
			// cerr << "Evaluate: " << m(row,c0)*m(row+1,c1) -
			// m(row,c1)*m(row+1,c0)<<'\n';
			return m[row][c0] * m[row + 1][c1] - m[row][c1] * m[row + 1][c0];
		}
		// go through the list
		int cnt = 0;
		double det = 0;
		int c = -1;
		do {
			c = Matrixd.linkUnlink(cols, c);
			// compute the determinant of the submatrix
			// where the column c and row (row+1) are eliminated
			double val = m[row][c] * Matrixd.subDeterminant(dim, row + 1, cols, m);
			// link e back
			Matrixd.linkUnlink(cols, c);
			det += cnt++ % 2 == 1 ? -val : val;
		} while (cols.get(c) < dim);
		// cerr << "Evaluate: " << det << '\n';
		return det;
	}

	public static double minorDeterminant(final int dim, final int row,
			final IntArrayReference cols, final IntReference c, final double[][] m) {
		c.value = Matrixd.linkUnlink(cols, c.value);
		final double val = Matrixd.subDeterminant(dim, row + 1, cols, m);
		Matrixd.linkUnlink(cols, c.value);
		return val;
	}

	/** @memo create an uninitialized matrix */
	public Matrixd() {
	}

	/** @memo create a matrix from the supplied 2-dimensional array */
	public Matrixd(final double matrix[][]) {
		Matrixd.copy(this.mat.length, matrix, this.getArray());
	}

	/**
	 * @memo create a matrix as the outer product matrix of two vectors; the
	 *       outer product matrix is a matrix such that m(i,j) = v1(i)*v2(j)
	 */
	public Matrixd(final Vectord v1, final Vectord v2) {
		Vectord.outerProduct(this.mat.length, v1.getVector(), v2.getVector(),
				this.getArray());
	}

	/** @memo return the identity matrix */
	public static Matrixd identity(final int dimension) {
		final Matrixd m = new Matrixd();
		Matrixd.identity(dimension, m.getArray());
		return m;
	}

	/** @memo set the matrix to the identity matrix */
	public void setIdentity() {
		Matrixd.identity(this.mat.length, this.getArray());
	}

	/** @memo set row vector i to v */
	public void setRowVector(final int i, final Vectord v) {
		Matrixd.setRowVector(this.mat.length, i, v.getVector(), this.getArray());
	}

	/** @memo get row vector i and place it in v */
	public void getRowVector(final int i, final Vectord v) {
		Matrixd.getRowVector(this.mat.length, i, this.getArray(), v.getVector());
	}

	/** @memo return row vector i */
	public Vectord getRowVector(final int i) {
		final Vectord v = new Vectord();
		this.getRowVector(i, v);
		return v;
	}

	/** @memo return a pointer to the elements of row i */
	public double[] getRow(final int i) {
		return this.mat[i];
	}

	/** @memo set column vector j to vector v */
	public void setColumnVector(final int j, final Vectord v) {
		Matrixd.setColumnVector(this.mat.length, j, v.getVector(), this.getArray());
	}

	/** @memo get column vector j in vector v */
	public void getColumnVector(final int j, final Vectord v) {
		Matrixd.getColumnVector(this.mat.length, j, this.getArray(), v.getVector());
	}

	/** @memo return column vector j */
	public Vectord getColumnVector(final int i) {
		final Vectord v = new Vectord();
		this.getColumnVector(i, v);
		return v;
	}

	/** @memo transpose THIS matrix */
	public void transpose(final Matrixd m) {
		Matrixd.transpose(this.mat.length, this.getArray(), m.getArray());
	}

	/** @memo transpose THIS matrix */
	public void transpose() {
		Matrixd.transpose(this.mat.length, this.getArray(), this.getArray());
	}

	/** @memo return the transposed matrix */
	public Matrixd transposed() {
		final Matrixd m = new Matrixd();
		Matrixd.transpose(this.mat.length, this.getArray(), m.getArray());
		return m;
	}

	public void setOuterProduct(final Vectord v1, final Vectord v2) {
		Vectord.outerProduct(this.mat.length, v1.getVector(), v2.getVector(),
				this.getArray());
	}

	// compute the square matrix into M
	public void square(final Matrixd M) {
		Matrixd.square(this.mat.length, this.getArray(), M.getArray());
	}

	/** @memo compute the trace of the matrix (sum of diagonal elements) */
	public double trace() {
		double val = this.get(0, 0);
		for (int i = 1; i < this.mat.length; ++i) {
			val += this.get(i, i);
		}
		return val;
	}

	/** @memo compute the determinant of the matrix */
	public double determinant() {
		final int cols[] = new int[this.mat.length + 2];
		final IntArrayReference col = new IntArrayReference(cols, 1);
		Matrixd.setDeterminantVector(cols, this.mat.length);
		return Matrixd.subDeterminant(this.mat.length, 0, col, this.getArray());
	}

	/** @memo compute the sign of the determinant */
	public int computeDeterminantSign() {
		final double d = this.determinant();
		return d > 0 ? 1 : d < 0 ? -1 : 0;
	}

	/**
	 * @memo compute the subdeterminants separately into the vector EXAMPLE: let
	 *       the matrix be | 1 2 3 | | 4 5 6 | | 7 8 9 | then the vector v will
	 *       be | 5*9-6*8 | | 6*7-4*9 | | 5*5-5*7 |
	 */
	public double computeMinorDeterminants(final Vectord v) {
		final int _cols[] = new int[this.mat.length + 2];
		final IntArrayReference cols = new IntArrayReference(_cols, 1);
		Matrixd.setDeterminantVector(_cols, this.mat.length);
		final IntReference c = new IntReference(-1);
		double det = 0.0;
		for (int cnt = 0; cols.get(c.value) < this.mat.length; ++cnt) {
			v.set(cnt, Matrixd.minorDeterminant(this.mat.length, 0, cols, c,
					this.getArray()));
			if (cnt % 2 == 1) {
				v.set(cnt, -v.get(cnt));
			}
			det += v.get(cnt) * this.get(0, c.value);
		}
		return det;
	}

	public MaxDimMatrixd inverse(final int dimension) {
		final MaxDimMatrixd m = new MaxDimMatrixd(dimension);
		this.inverse(m);
		return m;
	}

	/** @memo scale uniformly (along all axis) by the given scalar value */
	public void setScaling(final double s) {
		Matrixd.setScaling(this.mat.length, s, this.getArray());
	}

	/** @memo set the matrix to scale along axis i by a value of v[i] */
	public void setScaling(final Vectord v) {
		Matrixd.setScaling(this.mat.length, v.getVector(), this.getArray());
	}

	/** @memo set the matrix to scale along axis i by a value of s[i] */
	public void setScaling(final double s[]) {
		Matrixd.setScaling(this.mat.length, s, this.getArray());
	}

	/** @memo set the matrix to translate by a vector v */
	public void setTranslation(final Vectord v) {
		Matrixd.setTranslation(this.mat.length, v.getVector(), this.getArray());
	}

	/** @memo set the matrix to translate by a vector Vectord(v) */
	public void setTranslation(final double v[]) {
		Matrixd.setTranslation(this.mat.length, v, this.getArray());
	}

	/**
	 * @memo rotate around one of the coordinate axis center of rotation is the
	 *       origin
	 */
	public void setRotation(final int axis, final DoubleReference angle) {
	}

	/**
	 * @memo rotate around the given axis by the given angle center of rotation
	 *       is the origin
	 */
	public void setRotation(final Vectord axis, final DoubleReference angle) {
	}

	/**
	 * @memo rotate around the given axis by the given angle. center is the
	 *       center of rotation
	 */
	public void setRotation(final Vectord center, final Vectord axis, final DoubleReference angle) {
	}

	/**
	 * @memo rotate around the given axis by the given angle. center is the
	 *       center of rotation
	 */
	public void setRotation(final Vectord center, final int axis, final DoubleReference angle) {
	}

	// ///////////////////////////////////////////////////
	// / solving systems of linear equations
	// /////////////////////////////////////////////////////
	/**
	 * @memo lu-factorize the matrix; p is this.mat.length-dimensional array to
	 *       be filled in with pivots It should return an exception, if the
	 *       matrix is singular
	 * @return determinant of matrix
	 */
	public double factorize(final int p[]) {
		return Matrixd.factorize(this.mat.length, p, this.getArray());
	}

	/**
	 * @memo solve a system this*x=b, where this has already been factorized
	 * @param p
	 *            the pivot array that was filled in by the previous factorize
	 *            call
	 */
	public void solve(final int p[], final Vectord b, final Vectord x) {
		final Vectord y = new Vectord();
		this.forwardSubstitution(p, b, y);
		this.backwardSubstitution(p, y, x);
	}

	/**
	 * @memo solve a system A*x=b, where this has not been factorized
	 * @return TRUE, if the system could be solved false, otherwise NOTE: a will
	 *         be factorized afterwards
	 */
	public boolean solve(final Vectord b, final Vectord x) {
		final int p[] = new int[this.mat.length];
		if (this.factorize(p) == 0.0) {
			return false;
		}
		this.solve(p, b, x);
		return true;
	}

	// // not the most efficient implementation due to
	// // copying!!
	// public boolean solve(Vectord b, Vectord x) {
	// return Matrixd(this).solve(b, x);
	// }

	public boolean solveGauss(final Vectord b, final Vectord x) {
		final int p[] = new int[this.mat.length];
		return Matrixd.solveGauss(this.mat.length, p, this.getArray(), b.getVector(), x
				.getVector());
	}

	// not the most efficient implementation due to
	// copying!!
	// public boolean solveGauss(Vectord b, Vectord x) {
	// Vectord _bcopy = new Vectord(b);
	// return Matrixd(this).solveGauss(_bcopy, x);
	// }

	public Matrixd negate() {
		for (int i = 0; i < this.getDimension(); ++i) {
			for (int j = 0; j < this.getDimension(); j++) {
				this.getArray()[i][j] *= -1;
			}
		}
		return this;
	}

	// Matrixd negate ()
	// { Matrixd M = new Matrixd(this); return M.negate(); }
	// /////////////////////////////////////////////////////
	// Arithmetic operations on matrices
	// overloaded operators are grouped together with
	// their corresponding explicit functions
	// /////////////////////////////////////////////////////
	public Matrixd mult(final double d) {
		for (int i = 0; i < this.getDimension(); ++i) {
			for (int j = 0; j < this.getDimension(); j++) {
				this.getArray()[i][j] *= d;
			}
		}
		return this;
	}

	public Matrixd multiplyInPlace(final double d) {
		return this.mult(d);
	}

	public Matrixd multiply(final double d) {
		final Matrixd m = new Matrixd(this);
		return m.multiplyInPlace(d);
	}

	public Matrixd(final Matrixd m) {
		this.set(m.mat);
	}

	public Matrixd multiply(final double d, final Matrixd n) {
		return n.multiply(d);
	}

	/**
	 * @memo v = this * u
	 * @return reference to v
	 */
	public Vectord multRight(final Vectord u, final Vectord v) {
		Matrixd.multVectorRight(this.mat.length, this.getArray(), u.getVector(), v
				.getVector());
		return v;
	}

	/**
	 * @memo c = b * this
	 * @return reference to c
	 */
	public Vectord multLeft(final Vectord b, final Vectord c) {
		Matrixd.multVectorLeft(this.mat.length, this.getArray(), b.getVector(), c
				.getVector());
		return c;
	}

	/** @memo compute the matrix-vector product this * u */
	public Vectord multiply(final Vectord u) {
		final Vectord v = new Vectord();
		this.multRight(u, v);
		return v;
	}

	/**
	 * @memo M = THIS*B Note: this function only works if this != &M && &B != &M
	 * @return reference to M
	 */
	public Matrixd multRight(final Matrixd B, final Matrixd M) {
		Matrixd.mult(this.mat.length, this.getArray(), B.getArray(), M.getArray());
		return M;
	}

	/**
	 * @memo M = B*THIS Note: this function only works if this != &M && &B != &M
	 * @return reference to M
	 */
	public void multLeft(final Matrixd B, final Matrixd M) {
		B.multRight(this, M);
	}

	/** @memo compute the matrix product this * b */
	public Matrixd multiply(final Matrixd B) {
		final Matrixd M = new Matrixd();
		this.multRight(B, M);
		return M;
	}

	/**
	 * @memo right multiply this by M
	 * @return reference to this
	 */
	public Matrixd rightMultiply(final Matrixd M) {
		final Matrixd N = new Matrixd(this);
		N.multRight(M, this);
		return this;
	}

	public Matrixd multiplyInPlace(final Matrixd m) {
		return this.rightMultiply(m);
	}

	/**
	 * @memo left multiply this by M
	 * @return reference to this
	 */
	public Matrixd leftMultiply(final Matrixd m) {
		final Matrixd n = new Matrixd(this);
		n.multLeft(m, this);
		return this;
	}

	/** @memo this = m+n */
	public void add(final Matrixd m, final Matrixd n) {
		Matrixd.add(this.mat.length, m.getArray(), n.getArray(), this.getArray());
	}

	/** @memo compute this + m */
	public Matrixd add(final Matrixd m) {
		final Matrixd n = new Matrixd();
		n.add(this, m);
		return n;
	}

	/** @memo compute this = this + m */
	public Matrixd addInPlace(final Matrixd m) {
		this.add(this, m);
		return this;
	}

	/** @memo compute this = m - n */
	public void sub(final Matrixd m, final Matrixd n) {
		Matrixd.sub(this.mat.length, m.getArray(), n.getArray(), this.getArray());
	}

	/** @memo compute this - m */
	public Matrixd subtract(final Matrixd m) {
		final Matrixd n = new Matrixd();
		n.sub(this, m);
		return n;
	}

	/** @memo compute this = this - m */
	public Matrixd subtractInPlace(final Matrixd m) {
		this.add(this, m);
		return this;
	}

	// /////////////////////////////////////////////////
	// copying matrices and accessing matrix elements
	// /////////////////////////////////////////////////
	/** @memo copy the contents of an array of types into this */
	public Matrixd set(final double m[][]) {
		Matrixd.copy(this.mat.length, m, this.getArray());
		return this;
	}

	/** @memo return a pointer to the first element of row i */
	public double[] get(final int i) {
		return this.mat[i];
	}

	/** @memo return the value of entry in row i, column j */
	public double parens(final int i, final int j) {
		return this.mat[i][j];
	}

	/** @memo return the value of entry in row i, column j */
	public double get(final int i, final int j) {
		return this.mat[i][j];
	}

	/** @memo print the matrix */
	public PrintWriter print(final PrintWriter out) {
		return Matrixd.print(this.mat.length, out, this.getArray());
	}

	/** @memo get the dimension of the matrix */
	public int getDimension() {
		return this.mat.length;
	}

	// //////////////////////////////////////////////////////////
	/** @memo v = v * M */
	public Vectord multiplyInPlace(final Vectord v, final Matrixd M) {
		final Vectord u = new Vectord(v);
		M.multLeft(u, v);
		return v;
	}

	/** @memo return the vector v * M */
	public Vectord multiply(final Vectord v, final Matrixd M) {
		final Vectord u = new Vectord();
		M.multLeft(v, u);
		return u;
	}

	/** @memo print the matrix */
	public PrintWriter print(final PrintWriter out, final Matrixd m) {
		return Matrixd.print(this.mat.length, out, m.getArray());
	}

	// applying backward and forward substitution to a factorized matrix
	public void backwardSubstitution(final int p[], final Vectord y, final Vectord x) {
		Matrixd.backwardSubstitution(this.mat.length, p, this.getArray(),
				y.getVector(), x.getVector());
	}

	public void forwardSubstitution(final int p[], final Vectord y, final Vectord x) {
		Matrixd.forwardSubstitution(this.mat.length, p, this.getArray(), y.getVector(),
				x.getVector());
	}

	public void solve(final int p[], final Vectord b, final Matrixd m, final int i) {
		final Vectord x = new Vectord();
		this.solve(p, b, x);
		m.setColumnVector(i, x);
	}

	public double[][] getArray() {
		return this.mat;
	}

	public double mat[][] = new double[this.mat.length][this.mat.length];

	public Matrixd(final double v) {
		int i, j;
		for (i = 0; i < this.mat.length; i++) {
			this.mat[i][i] = v;
			for (j = i + 1; j < this.mat.length; j++) {
				this.mat[i][j] = this.mat[j][i] = 0;
			}
		}
	}

	public Matrixd(final int rows, final int columns) {
		this.mat = new double[rows][columns];
	}

	public Matrixd(final Vectord[] v) {
		this(v, true);
	}

	public Matrixd(final Vectord[] v, final boolean rowVector/* =true */) {
		int i, j;
		if (rowVector) {
			for (i = 0; i < this.mat.length; ++i) {
				for (j = 0; j < this.mat.length; ++j) {
					this.mat[i][j] = v[i].get(j);
				}
			}
		} else {
			for (i = 0; i < this.mat.length; ++i) {
				for (j = 0; j < this.mat.length; ++j) {
					this.mat[i][j] = v[j].get(i);
				}
			}
		}
	}

	public double inverse(final Matrixd inv) {
		int i, j;
		final Vectord unit = new Vectord(), tmp = new Vectord();
		final int p[] = new int[this.mat.length];
		final Matrixd m = new Matrixd(this.mat);
		final double det = m.factorize(p);
		if (det != 0) {
			for (i = 0; i < this.mat.length; unit.set(i++, 0)) {
				;
			}
			for (i = 0; i < this.mat.length; ++i) {
				unit.set(i, 1.0);
				m.solve(p, unit, tmp);
				for (j = 0; j < this.mat.length; ++j) {
					inv.set(j, i, tmp.get(j));
				}
				unit.set(i, 0.0);
			}
		}
		return det;
	}

	public void set(final int row, final int column, final double value) {
		this.mat[row][column] = value;
	}

	public double invert() {
		int i, j;
		final Vectord unit = new Vectord(), tmp = new Vectord();
		final int p[] = new int[this.mat.length];
		final Matrixd m = new Matrixd(this.mat);
		final double det = m.factorize(p);
		if (det != 0) {
			for (i = 0; i < this.mat.length; unit.set(i++, 0)) {
				;
			}
			for (i = 0; i < this.mat.length; ++i) {
				unit.set(i, 1.0);
				m.solve(p, unit, tmp);
				for (j = 0; j < this.mat[0].length; ++j) {
					this.set(j, i, tmp.get(j));
				}
				unit.set(i, 0.0);
			}
		}
		return det;
	}

	public void swap(final Matrixd m2) {
		final double[][] M1 = this.getArray();
		final double[][] M2 = m2.getArray();
		for (int i = 0; i < M1.length; i++) {
			for (int j = 0; j < M1[0].length; j++) {
				final double tmp = M1[i][j];
				M1[i][j] = M2[i][j];
				M2[i][j] = tmp;
			}
		}
	}

	// compute the coefficients of the
	// the characteristic polynomial
	// the algorithm is the simple Keller-Gehrig algorithm with O(n**3 * log n)
	// Arguments:
	// this.mat.length: the matrix A is a this.mat.length-x-this.mat.length
	// matrix
	// A the matrix whose char. eqn. we want to find
	// T1,T2,T3 three matrices of the same dimensions as A
	// where the coefficients are to be stored (NOTE: only this.mat.length
	// coefficents, the
	// highest coefficent is 1)
	public int computeCharacteristicEquation(final Vectord poly) {
		final Matrixd T1 = new Matrixd();
		final Matrixd TMP[] = new Matrixd[3]; // the temporary matrices
		final Matrixd U = new Matrixd(), Uinv = new Matrixd();
		final Matrixd A = this;
		TMP[0] = T1;
		TMP[1] = Uinv;
		TMP[2] = (this); // only used for access
		// put the column vector [1 0 0 ] into the first column of U
		// and the first column of A into the second column of U
		{
			for (int i = 1; i < this.mat.length; ++i) {
				U.set(i, 0, 0.0);
			}
			U.set(0, 0, 1.0);
		}
		TMP[0] = TMP[2];
		for (int i = 1; i < this.mat.length; i *= 2) {
			for (int j = 0; j < i && j + i < this.mat.length; ++j) {
				// simple matrix vector computation
				for (int k = 0; k < this.mat.length; ++k) {
					double val = (0);
					for (int m = 0; m < this.mat.length; ++m) {
						val += (TMP[0]).get(k, m) * U.get(m, j);
					}
					U.set(k, i + j, val);
				}
			}
			// some book keeping
			(TMP[0]).square(TMP[1]);
			if (TMP[0] == TMP[2]) {
				TMP[0] = T1;
			}
			final Matrixd tmp = TMP[0];
			TMP[0] = TMP[1];
			TMP[1] = tmp;
		}
		// compute the inverse of U
		// #ifndef LEVERRIER
		if (U.inverse(Uinv) == 0.0)
		// #endif
		{
			TMP[0] = T1;
			TMP[0] = Uinv;
			TMP[0] = A;
			poly.set(this.mat.length - 1, A.trace());
			for (int i = 1; i < this.mat.length; ++i) {
				// assertion:
				// TMP[0] points to the matrix A<i-1>
				// compute B_1 in TMP[1] by subtracting the last coefficient
				final double c = poly.get(this.mat.length - i);
				for (int j = 0; j < this.mat.length; ++j) {
					(TMP[0]).set(j, j, (TMP[0]).get(j, j) - c);
				}
				A.multRight(TMP[0], TMP[1]);

				final Matrixd tmp = TMP[0];
				TMP[0] = TMP[1];
				TMP[1] = tmp;

				poly.set(this.mat.length - i - 1, TMP[0].trace() / (i + 1));
			}
			if (this.mat.length % 2 == 1) {
				return -1;
			} else {
				poly.invert();
				return 1;
			}
		}
		// cerr << "The temporary matrices are: \n";
		// cerr << "U : " << U << '\n';
		// cerr << "A : " << A << '\n';
		// cerr << "Uinv : " << Uinv << '\n';
		// cerr << "I : " << Uinv*U<<'\n';
		Uinv.multRight(A, T1);
		T1.multRight(U, Uinv);
		// now, Uinv contains the vector in the last column
		for (int k = 0; k < this.mat.length; ++k) {
			poly.set(k, -Uinv.get(k, this.mat.length - 1));
		}
		// cerr << "A' : " << Uinv << '\n';
		return this.mat.length % 2 == 1 ? -1 : 1;
	}

	public boolean approximate(final Vectord b, final Vectord x) {
		final Matrixd A = new Matrixd(this.mat[0].length, this.mat[0].length);
		final Vectord y = new Vectord(A.mat.length);
		Matrixd.computeAtA(this.mat[0].length, this.mat.length, this.getArray(), A
				.getArray());
		Matrixd.computeAtb(this.mat[0].length, this.mat.length, this.getArray(), b
				.getVector(), y.getVector());
		// cerr << "The approximation matrix A = " << A << '\n';
		// cerr << "The new vector b = " << y << '\n';
		System.err.flush();
		return A.solve(y, x);
	}
	
	public void setDimension(final int dimension) {
		this.mat = new double[dimension][dimension];
	}
	
	public void preTrans(final double x, final double y, final double z, final Matrixd m) {
		Matrixd.setAsScaleMatrix4x4(x,y,z,this.mat);
		this.multiply(m);
	}
	
	public void preScale(final double x, final double y, final double z, final Matrixd m) {
		Matrixd.setAsTranslationMatrix4x4(x,y,z,this.mat);
		this.multiply(m);
	}
	
	public void postMult(final Matrixd m) {
		this.multiply(m);
	}
	
	public void makeVecRotVec(final Point3d v1, final Point3d v2) {
		
	}
	
	public static void setAsScaleMatrix4x4(final double x, final double y, final double z, final double[][] matrix) {
		
		matrix[0][0] = 1;
		matrix[1][0] = 0;
		matrix[2][0] = 0;
		matrix[3][0] = 0;
		matrix[0][1] = 0;
		matrix[1][1] = 1;
		matrix[2][1] = 0;
		matrix[3][1] = 0;
		matrix[0][2] = 0;
		matrix[1][2] = 0;
		matrix[2][2] = 1;
		matrix[3][2] = 0;
		matrix[0][3] = x;
		matrix[1][3] = y;
		matrix[2][3] = z;
		matrix[3][3] = 1;
	}
	
public static void setAsTranslationMatrix4x4(final double x, final double y, final double z, final double[][] matrix) {
		
		matrix[0][0] = x;
		matrix[1][0] = 0;
		matrix[2][0] = 0;
		matrix[3][0] = 0;
		matrix[0][1] = 0;
		matrix[1][1] = y;
		matrix[2][1] = 0;
		matrix[3][1] = 0;
		matrix[0][2] = 0;
		matrix[1][2] = 0;
		matrix[2][2] = z;
		matrix[3][2] = 0;
		matrix[0][3] = 0;
		matrix[1][3] = 0;
		matrix[2][3] = 0;
		matrix[3][3] = 1;
	}
}
