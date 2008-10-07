package org.rcsb.mbt.glscene.geometry;

import java.io.PrintWriter;

import org.rcsb.mbt.model.cppConversion.IntArrayReference;
import org.rcsb.mbt.model.cppConversion.IntReference;

public class MaxDimMatrixd extends Matrixd {
	public int maxDim;

	/**
	 * @memo create a matrix, where the all elements but the diagonal ones are
	 *       set to 0. The element (i,i) are set to v
	 */
	public MaxDimMatrixd(final int maxDim) {
		this.maxDim = maxDim;
		this.init();
	}

	/** @memo create an uninitialized matrix */
	public MaxDimMatrixd(final int dim, final int maxdim) {
		this.maxDim = this.maxDim;
		this.init(dim, dim);
	}

	public MaxDimMatrixd(final int r, final int c, final int maxDim) {
		this.maxDim = maxDim;
		this.init(r, c);
	}

	/** @memo create a matrix from the supplied 2-dimensional array */
	public MaxDimMatrixd(final int r, final int c, final double matrix[][], final int maxDim) {
		this.maxDim = maxDim;
		this.init(r, c);
		Matrixd.copy(this.cols, this.rows, matrix, this.getArray());
	}

	/** @memo need a copy constructor */
	public MaxDimMatrixd(final MaxDimMatrixd m) {
		this.maxDim = this.maxDim;
		this.init();
		this.set(m);
	}

	/**
	 * @memo create a matrix as the outer product matrix of two vectors; the
	 *       outer product matrix is a matrix such that m(i,j) = v1(i)*v2(j)
	 */
	public MaxDimMatrixd(final MaxDimVectord v1, final MaxDimVectord v2) {
		this.maxDim = this.maxDim;
		this.init(v1.getDimension(), v1.getDimension());
		Vectord
				.outerProduct(this.cols, v1.getVector(), v2.getVector(), this.getArray());
	}

	/** @memo return the identity matrix */
	public static MaxDimMatrixd identity(final int r, final int c) {
		final MaxDimMatrixd m = new MaxDimMatrixd(r, c);
		Matrixd.identity(c, r, m.getArray());
		return m;
	}

	/** @memo set the matrix to the identity matrix */
	public void setIdentity() {
		Matrixd.identity(this.cols, this.rows, this.getArray());
	}

	public void setOuterProduct(final MaxDimVectord v1, final MaxDimVectord v2) {
		Vectord
				.outerProduct(this.cols, v1.getVector(), v2.getVector(), this.getArray());
	}

	/** @memo set row vector i to v */
	public void setRowVector(final int i, final MaxDimVectord v) {
		Matrixd.setRowVector(this.cols, this.rows, i, v.getVector(), this.getArray());
	}

	/** @memo get row vector i and place it in v */
	public void getRowVector(final int i, final MaxDimVectord v) {
		v.setDimension(this.getCols());
		Matrixd.getRowVector(this.cols, this.rows, i, this.getArray(), v.getVector());
	}

	/** @memo return row vector i */
//	public MaxDimVectord getRowVector(int i) {
//		MaxDimVectord v = new MaxDimVectord(this.maxDim);
//		getRowVector(i, v);
//		return v;
//	}

	/** @memo return a pointer to the elements of row i */
	public double[] getRow(final int i) {
		return this.getArray()[i * this.getCols()];
	}

	/** @memo set column vector j to vector v */
	public void setColumnVector(final int j, final MaxDimVectord v) {
		Matrixd.setColumnVector(this.cols, this.rows, j, v.getVector(), this.getArray());
	}

	/** @memo get column vector j in vector v */
	public void getColumnVector(final int j, final MaxDimVectord v) {
		v.setDimension(this.getRows());
		Matrixd.getColumnVector(this.cols, this.rows, j, this.getArray(), v.getVector());
	}

	/** @memo return column vector j */
//	public MaxDimVectord getColumnVector(int i) {
//		MaxDimVectord v = new MaxDimVectord(this.maxDim);
//		getColumnVector(i, v);
//		return v;
//	}

	public boolean isSquare() {
		return this.cols == this.rows;
	}

	/** @memo transpose THIS matrix */
	public void transpose() {
		Matrixd.transpose(this.cols, this.rows, this.getArray(), this.getArray());
		final int tmp = this.cols;
		this.cols = this.rows;
		this.rows = tmp;
	}

	public void transpose(final MaxDimMatrixd m) {
		m.setDimension(this.cols, this.rows);
		Matrixd.transpose(this.cols, this.rows, this.getArray(), m.getArray());
	}

	/** @memo return the transposed matrix */
//	public MaxDimMatrixd transposed() {
//		MaxDimMatrixd m = new MaxDimMatrixd(cols, rows);
//		Matrixd.transpose(cols, rows, getArray(), m.getArray());
//		return m;
//	}

	// compute the square matrix into M
	public void square(final MaxDimMatrixd M) {
		Matrixd.square(this.cols, this.getArray(), M.getArray());
	}

	/** @memo compute the trace of the matrix (sum of diagonal elements) */
	public double trace() {
		double val = this.get(0, 0);
		for (int i = 1; i < this.cols; ++i) {
			val += this.get(i, i);
		}
		return val;
	}

	/**
	 * @memo compute the determinant of the matrix @{ due to its computational
	 *       complexity the use of this method is discouraged, i.e. use only
	 *       very sparingly }
	 */
	public double determinant() {
		if (!this.isSquare()) {
			return 0.0;
		}
		final int _cols[] = new int[this.maxDim + 2];
		final IntArrayReference col = new IntArrayReference(_cols, 1);
		Matrixd.setDeterminantVector(_cols, this.cols);
		return Matrixd.subDeterminant(this.cols, 0, col, this.getArray());
	}

	/** @memo compute the sign of the determinant */
	public int computeDeterminantSign() {
		final double d = this.determinant();
		return d > 0 ? 1 : d < 0 ? -1 : 0;
	}

	/**
	 * @memo compute the subdeterminants separately into the vector EXAMPLE: let
	 *       the matrix be | 1 2 3 | | 4 5 6 | | 7 8 9 | then the vector v will
	 *       be | 5*9-6*8 | | 4*9-6*7 | | 5*5-5*7 |
	 */
	public double computeMinorDeterminants(final MaxDimVectord v) {
		final int _cols[] = new int[this.maxDim + 2];
		final IntArrayReference col = new IntArrayReference(_cols, 1);
		Matrixd.setDeterminantVector(_cols, this.cols);
		double det = 0.0;
		final IntReference c = new IntReference(-1);
		for (int cnt = 0; col.get(c.value) < this.cols; ++cnt) {
			v.set(cnt,Matrixd.minorDeterminant(this.cols, 0, col, c, this.getArray()));
			if (cnt % 2 == 1) {
				v.set(cnt,-v.get(cnt));
			}
			det += v.get(cnt) * this.get(0, c.value);
		}
		return det;
	}

	public MaxDimMatrixd inverse() {
		final MaxDimMatrixd m = new MaxDimMatrixd(this.maxDim);
		this.inverse(m);
		return m;
	}

	/** @memo scale uniformly (along all axis) by the given scalar value */
	public void setScaling(final double s) {
		Matrixd.setScaling(this.cols, s, this.getArray());
	}

	/** @memo set the matrix to scale along axis i by a value of v[i] */
	public void setScaling(final MaxDimVectord v) {
		Matrixd.setScaling(this.cols, v.getVector(), this.getArray());
	}

	/** @memo set the matrix to scale along axis i by a value of s[i] */
	public void setScaling(final double s[]) {
		Matrixd.setScaling(this.cols, s, this.getArray());
	}

	/** @memo set the matrix to translate by a vector v */
	public void setTranslation(final MaxDimVectord v) {
		Matrixd.setTranslation(this.cols, v.getVector(), this.getArray());
	}

	/** @memo set the matrix to translate by a vector HvMaxDimVector<double,maxDim>(v) */
	public void setTranslation(final double v[]) {
		Matrixd.setTranslation(this.cols, v, this.getArray());
	}

	/**
	 * @memo rotate around one of the coordinate axis center of rotation is the
	 *       origin (NOT IMPLEMENTED)
	 */
	public void setRotation(final int axis, final double angle) {
	}

	/**
	 * @memo rotate around the given axis by the given angle center of rotation
	 *       is the origin (NOT IMPLEMENTED)
	 */
	public void setRotation(final MaxDimVectord axis, final double angle) {
	}

	/**
	 * @memo rotate around the given axis by the given angle. center is the
	 *       center of rotation (NOT IMPLEMENTED)
	 */
	public void setRotation(final MaxDimVectord center, final MaxDimVectord axis,
			final double angle) {
	}

	/**
	 * @memo rotate around the given axis by the given angle. center is the
	 *       center of rotation (NOT IMPLEMENTED)
	 */
	public void setRotation(final MaxDimVectord center, final int axis, final double angle) {
	}

	/**
	 * @memo lu-factorize the matrix; p is dim-dimensional array to be filled in
	 *       with pivots It should return an exception, if the matrix is
	 *       singular
	 * @return determinant of matrix
	 */
	public double factorize(final int p[]) {
		return Matrixd.factorize(this.cols, p, this.getArray());
	}

	/**
	 * @memo solve a system this*x=b, where this has already been factorized
	 * @param p
	 *            the pivot array that was filled in by the previous factorize
	 *            call
	 */
	public boolean solve(final int p[], final MaxDimVectord b,
			final MaxDimVectord x) {
		if (!this.isSquare()) {
			return false;
		}
		final MaxDimVectord y = new MaxDimVectord(this.maxDim);
		this.forwardSubstitution(p, b, y);
		this.backwardSubstitution(p, y, x);
		return true;
	}

	/**
	 * @memo solve a system this*x=b, where this has not been factorized
	 * @return true, if the system could be solved false, otherwise
	 */
	public boolean solve(final MaxDimVectord b, final MaxDimVectord x) {
		if (!this.isSquare()) {
			return false;
		}
		final int p[] = new int[this.maxDim];
		if (this.factorize(p) == 0.0) {
			return false;
		}
		this.solve(p, b, x);
		return true;
	}

//	public MaxDimMatrixd negate() {
//		for (int i = 0; i < getCols(); ++i)
//			for(int j = 0; j < getRows(); ++j)
//				getArray()[i][j] *= -1;
//		return this;
//	}

//	public MaxDimMatrixd negateAndCopy() {
//		MaxDimMatrixd M = new MaxDimMatrixd(this);
//		return M.negate();
//	}

	// /////////////////////////////////////////////////////
	// Arithmetic operations on matrices
	// overloaded operators are grouped together with
	// their corresponding explicit functions
	// /////////////////////////////////////////////////////
//	public MaxDimMatrixd mult(double d) {
//		for (int i = 0; i < getCols() * getRows(); ++i)
//			for(int j = 0; j < getRows(); ++j)
//				getArray()[i][j] *= d;
//		return this;
//	}

//	public MaxDimMatrixd multInPlace(double d) {
//		return mult(d);
//	}

	// MaxDimMatrixd operator* ( double& d)
	// { MaxDimMatrixd m(*this); return m *= d; }
//	public MaxDimMatrixd mult(double d, MaxDimMatrixd n) {
//		return n.mult(d);
//	}

	/**
	 * @memo v = this * u
	 * @return reference to v
	 */
	public MaxDimVectord multRight(final MaxDimVectord u, final MaxDimVectord v) {
		v.setDimension(this.getRows());
		Matrixd.multVectorRight(this.cols, this.rows, this.getArray(), u.getVector(), v
				.getVector());
		return v;
	}

	/**
	 * @memo c = b * this
	 * @return reference to c
	 */
	public MaxDimVectord multLeft(final MaxDimVectord b, final MaxDimVectord c) {
		c.setDimension(this.getCols());
		Matrixd.multVectorLeft(this.cols, this.rows, this.getArray(), b.getVector(), c
				.getVector());
		return c;
	}

	/** @memo compute the matrix-vector product this * u */
	public MaxDimVectord mult(final MaxDimVectord u) {
		final MaxDimVectord v = new MaxDimVectord(this.getCols());
		this.multRight(u, v);
		return v;
	}

	/**
	 * @memo M = THIS*B Note: this function only works if this != &M && &B != &M
	 * @return reference to M
	 */
	public MaxDimMatrixd multRight(final MaxDimMatrixd B, final MaxDimMatrixd M) {
		M.setDimension(this.getRows(), B.getCols());
		Matrixd.mult(this.cols, this.rows, B.getCols(), this.getArray(), B.getArray(), M
				.getArray());
		return M;
	}

	/**
	 * @memo M = B*THIS Note: this function only works if this != &M && &B != &M
	 * @return reference to M
	 */
	public void multLeft(final MaxDimMatrixd B, final MaxDimMatrixd M) {
		B.multRight(this, M);
	}

	/** @memo compute the matrix product this * b */
	public MaxDimMatrixd mult(final MaxDimMatrixd B) {
		final MaxDimMatrixd M = new MaxDimMatrixd(this.maxDim);
		this.multRight(B, M);
		return M;
	}

	/**
	 * @memo right multiply this by M
	 * @return reference to this
	 */
	public MaxDimMatrixd rightMultiply(final MaxDimMatrixd M) {
		final MaxDimMatrixd N = new MaxDimMatrixd(this.maxDim);
		this.multRight(M, N);
		this.swap(N);
		return this;
	}

	public MaxDimMatrixd multInPlace(final MaxDimMatrixd m) {
		return this.rightMultiply(m);
	}

	/**
	 * @memo left multiply this by M
	 * @return reference to this
	 */
	public MaxDimMatrixd leftMultiply(final MaxDimMatrixd m) {
		final MaxDimMatrixd n = new MaxDimMatrixd(this.maxDim);
		this.multLeft(m, n);
		this.swap(n);
		return this;
	}

	/** @memo this = m+n */
	public void add(final MaxDimMatrixd m, final MaxDimMatrixd n) {
		this.setDimension(m.getRows(), m.getCols());
		Matrixd.add(this.cols, this.rows, m.getArray(), n.getArray(), this.getArray());
	}

	/** @memo compute this + m */
	public MaxDimMatrixd add(final MaxDimMatrixd m) {
		final MaxDimMatrixd n = new MaxDimMatrixd(this.maxDim);
		n.add(this, m);
		return n;
	}

	/** @memo compute this = this + m */
	public MaxDimMatrixd addInPlace(final MaxDimMatrixd m) {
		this.add(this, m);
		return this;
	}

	/** @memo compute this = m - n */
	public void sub(final MaxDimMatrixd m, final MaxDimMatrixd n) {
		this.setDimension(m.getRows(), m.getCols());
		Matrixd.sub(this.cols, this.rows, m.getArray(), n.getArray(), this.getArray());
	}

	/** @memo compute this - m */
	public MaxDimMatrixd negate(final MaxDimMatrixd m) {
		final MaxDimMatrixd n = new MaxDimMatrixd(this.maxDim);
		n.sub(this, m);
		return n;
	}

	/** @memo compute this = this - m */
	public MaxDimMatrixd subInPlace(final MaxDimMatrixd m) {
		this.add(this, m);
		return this;
	}

	// /////////////////////////////////////////////////
	// copying matrices and accessing matrix elements
	// /////////////////////////////////////////////////
	/** @memo copy the contents of an array of types into this */
//	public MaxDimMatrixd set(double m[][]) {
//		Matrixd.copy(cols, rows, m, getArray());
//		return this;
//	}
	
	public void set(final int row, final int column, final double value) {
		this.mat[row][column] = value;
	}

	public MaxDimMatrixd set(final MaxDimMatrixd m) {
		final IntReference r = new IntReference(), c = new IntReference();
		m.getDimension(r, c);
		this.setDimension(r.value, c.value);
		Matrixd.copy(this.cols, this.rows, m.getArray(), this.getArray());
		return this;
	}

	/** @memo return a pointer to the first element of row i */
	public double[] get(final int i) {
		return this.mat[i];
	}

	public double get(final int i, final int j) {
		return this.mat[i][j];
	}

	/** @memo return the value of entry in row i, column j */
	public double parens(final int i, final int j) {
		return this.mat[i][j];
	}

	/** @memo print the matrix */
	public PrintWriter print(final PrintWriter out) {
		return Matrixd.print(this.cols, this.rows, out, this.getArray());
	}

	public void setDimension(final int row, final int col) {
		this.rows = row;
		this.cols = col;
		// (void)( (rows <= maxDim && cols <= maxDim) || (_assert("rows <=
		// maxDim &&
		// cols <= maxDim", "c:\\documents and settings\\john
		// beaver\\desktop\\firefox
		// downloads\\molviewer\\molviewer\\hvlib\\hvdatatypes\\hvmaxdimmatrix.h",
		// 477),
		// 0) );
	}

	public void setDimension(final int d) {
		this.setDimension(d, d);
	}

	/** @memo get the dimension of the matrix */
	public void getDimension(final IntReference row, final IntReference col) {
		row.value = this.rows;
		col.value = this.cols;
	}

	public int getCols() {
		return this.cols;
	}

	public int getRows() {
		return this.rows;
	}

	/** @memo v = v * M */
	public MaxDimVectord mulInPlace(final MaxDimVectord v,
			final MaxDimMatrixd M) {
		final MaxDimVectord u = new MaxDimVectord(v, this.maxDim);
		M.multLeft(u, v);
		return v;
	}

	/** @memo return the vector v * M */
	public MaxDimVectord mul(final MaxDimVectord v, final MaxDimMatrixd M) {
		final MaxDimVectord u = new MaxDimVectord(M.getRows());
		M.multLeft(v, u);
		return u;
	}

	/** @memo print the matrix */
	public PrintWriter print(final PrintWriter out, final MaxDimMatrixd m) {
		return Matrixd.print(m.getCols(), m.getRows(), out, m.getArray());
	}

	// applying backward and forward substitution to a factorized matrix
	public void backwardSubstitution(final int p[], final MaxDimVectord y,
			final MaxDimVectord x) {
		x.setDimension(y.getDimension());
		Matrixd.backwardSubstitution(this.cols, p, this.getArray(), y.getVector(), x
				.getVector());
	}

	public void forwardSubstitution(final int p[], final MaxDimVectord y,
			final MaxDimVectord x) {
		x.setDimension(y.getDimension());
		Matrixd.forwardSubstitution(this.cols, p, this.getArray(), y.getVector(), x
				.getVector());
	}

	public boolean solve(final int p[], final MaxDimVectord b, final MaxDimMatrixd m,
			final int i) {
		final MaxDimVectord x = new MaxDimVectord(this.maxDim);
		if (this.solve(p, b, x)) {
			m.setColumnVector(i, x);
			return true;
		}
		return false;
	}

	public double[][] getArray() {
		return this.mat;
	}

	public void init() {
		this.rows = this.cols = 0;
	}

	public void init(final int r, final int c) {
		this.setDimension(r, c);
	}

	public int rows, cols;

	public double mat[][] = new double[this.maxDim][this.maxDim];

	public MaxDimMatrixd(final int r, final int c, final MaxDimVectord v[], final boolean rowVector, final int maxDim) {
		this.maxDim = maxDim;
		int i, j;
		this.init(r, c);
		if (rowVector) {
			for (i = 0; i < this.rows; ++i) {
				for (j = 0; j < this.cols; ++j) {
					this.set(i, j, v[i].get(j));
				}
			}
		} else {
			for (i = 0; i < this.rows; ++i) {
				for (j = 0; j < this.cols; ++j) {
					this.set(i, j, v[j].get(i));
				}
			}
		}
	}

	public double inverse(final MaxDimMatrixd inv) {
		if (!this.isSquare()) {
			return 0;
		}
		inv.setDimension(this.getRows(), this.getCols());
		int i, j;
		final MaxDimVectord unit = new MaxDimVectord(
				this.getCols(), this.maxDim),tmp = new MaxDimVectord(this.getCols(), this.maxDim);
		final int p[] = new int[this.maxDim];
		final MaxDimMatrixd m = new MaxDimMatrixd(this);
		final double det = m.factorize(p);
		if (det != 0) {
			for (i = 0; i < this.rows; unit.set(i++, 0)) {
				;
			}
			for (i = 0; i < this.rows; ++i) {
				unit.set(i,1.0);
				m.solve(p, unit, tmp);
				for (j = 0; j < this.rows; ++j) {
					inv.set(j,i,tmp.get(j));
				}
				unit.set(i,0.0);
			}
		}
		return det;
	}

	public double invert() {
		if (!this.isSquare()) {
			return 0;
		}
		int i, j;
		final MaxDimVectord unit = new MaxDimVectord(this.getCols(), this.maxDim),
				tmp = new MaxDimVectord(this.getCols(), this.maxDim); // **JB
		// likely
		// a
		// translation
		// error.
		final int p[] = new int[this.maxDim];
		final MaxDimMatrixd m = new MaxDimMatrixd(this);
		final double det = m.factorize(p);
		if (det != 0) {
			for (i = 0; i < this.rows; unit.set(i++,0)) {
				;
			}
			for (i = 0; i < this.rows; ++i) {
				unit.set(i, 1.0);
				m.solve(p, unit, tmp);
				for (j = 0; j < this.rows; ++j) {
					(this).set(j, i, tmp.get(j));
				}
				unit.set(i,0.0);
			}
		}
		return det;
	}

	public void swap(final MaxDimMatrixd m2) {
		int tmp = this.cols;
		this.cols = m2.cols;
		m2.cols = tmp;
		
		tmp = this.rows;
		this.rows = m2.rows;
		m2.rows = tmp;
		
		for (int i = 0; i < this.maxDim; ++i) {
			for(int j = 0; j < this.maxDim; j++) {
				final double tmpd = this.mat[i][j];
				this.mat[i][j] = m2.mat[i][j];
				m2.mat[i][j] = tmpd;
			}
		}
	}

	// compute the coefficients of the
	// the characteristic polynomial
	// the algorithm is the simple Keller-Gehrig algorithm with O(n**3 * log n)
	// Arguments:
	// dim: the matrix A is a dim-x-dim matrix
	// A the matrix whose char. eqn. we want to find
	// T1,T2,T3 three matrices of the same dimensions as A
	// where the coefficients are to be stored (NOTE: only dim coefficents, the
	// highest coefficent is 1)
	public long computeCharacteristicEquation(final MaxDimVectord poly) {
		final int dim = this.getRows();
		final MaxDimMatrixd T1 = new MaxDimMatrixd(dim);
		final MaxDimMatrixd TMP[] = new MaxDimMatrixd[3]; // the temporary
		// matrices
		final MaxDimMatrixd U = new MaxDimMatrixd(dim), Uinv = new MaxDimMatrixd(
				dim);
		final MaxDimMatrixd A = this;
		poly.setDimension(dim);
		TMP[0] = T1;
		TMP[1] = Uinv;
		TMP[2] = this; // only used for access
		// put the column vector [1 0 0 ] into the first column of U
		// and the first column of A into the second column of U
		{
			for (int i = 0; i < dim; ++i) {
				U.set(i,0,A.get(0,i));
			}
		}
		TMP[0] = TMP[2];
		for (int i = 1; i < dim; i *= 2) {
			for (int j = 0; j < i && j + i < dim; ++j) {
				// simple matrix vector computation
				for (int k = 0; k < dim; ++k) {
					double val = 0;
					for (int m = 0; m < dim; ++m) {
						val += TMP[k].get(m)[0] * U.get(m,j);
					}
					U.set(k,i + j,val);
				}
			}
			// some book keeping
			(TMP[0]).square(TMP[1]);
			if (TMP[0] == TMP[2]) {
				TMP[0] = T1;
			}
			final MaxDimMatrixd tmp = TMP[0];
			TMP[0] = TMP[1];
			TMP[1] = tmp;
		}
		// compute the inverse of U
		// #ifndef LEVERRIER
		if (U.inverse(Uinv) == 0.0)
		// #endif
		{
			TMP[1] = Uinv;
			TMP[0] = T1;
			TMP[0] = A;
			poly.set(dim - 1,A.trace());
			for (int i = 1; i < dim; ++i) {
				// assertion:
				// TMP[0] points to the matrix A<i-1>
				// compute B_1 in TMP[1] by subtracting the last coefficient
				final double c = poly.get(dim - i);
				for (int j = 0; j < dim; ++j) {
					(TMP[0]).get(j)[j] = TMP[0].get(j)[j] - c;
				}
				A.multRight(TMP[0], TMP[1]);
				final MaxDimMatrixd tmp = TMP[0];
				TMP[0] = TMP[1];
				TMP[1] = tmp;
				poly.set(dim - i - 1, TMP[0].trace() / (i + 1));
			}
			if (dim % 2 == 1) {
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
		for (int k = 0; k < dim; ++k) {
			poly.set(k,-Uinv.get(k)[dim - 1]);
		}
		// cerr << "A' : " << Uinv << '\n';
		return dim % 2 == 1 ? -1 : 1;
	}
}
