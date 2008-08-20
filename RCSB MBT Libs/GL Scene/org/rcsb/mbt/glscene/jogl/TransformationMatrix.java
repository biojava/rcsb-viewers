package org.rcsb.mbt.glscene.jogl;

import java.nio.FloatBuffer;
import java.util.regex.Pattern;

import org.rcsb.mbt.glscene.geometry.Matrix3f;
import org.rcsb.mbt.glscene.geometry.UnitCell;
import org.rcsb.mbt.model.cppConversion.IntReference;


import com.sun.opengl.util.BufferUtil;


public class TransformationMatrix {
	public void setTransformationMatrix(final Matrix3f matrix, final Vector3f vector) {
		this.init();
		
		synchronized(this.transformationMatrix) {
			// column-major order for OpenGl
			this.transformationMatrix.put(matrix.m00);
			this.transformationMatrix.put(matrix.m10);
			this.transformationMatrix.put(matrix.m20);
			this.transformationMatrix.put(0);
			this.transformationMatrix.put(matrix.m01);
			this.transformationMatrix.put(matrix.m11);
			this.transformationMatrix.put(matrix.m21);
			this.transformationMatrix.put(0);
			this.transformationMatrix.put(matrix.m02);
			this.transformationMatrix.put(matrix.m12);
			this.transformationMatrix.put(matrix.m22);
			this.transformationMatrix.put(0);
			this.transformationMatrix.put(vector.coordinates[0]);
			this.transformationMatrix.put(vector.coordinates[1]);
			this.transformationMatrix.put(vector.coordinates[2]);
			this.transformationMatrix.put(1);
		}
	}
	
	/**
	 * This function will change the contents of result, but will not change point.
	 */
	public void transformPoint(final double[] point, final double[] result) {
		result[0] = this.transformationMatrix.get(0) * point[0] + this.transformationMatrix.get(4) * point[1] + this.transformationMatrix.get(8) * point[2] + this.transformationMatrix.get(12);
		result[1] = this.transformationMatrix.get(1) * point[0] + this.transformationMatrix.get(5) * point[1] + this.transformationMatrix.get(9) * point[2] + this.transformationMatrix.get(13);
		result[2] = this.transformationMatrix.get(2) * point[0] + this.transformationMatrix.get(6) * point[1] + this.transformationMatrix.get(10) * point[2] + this.transformationMatrix.get(14);
	}
	
	/**
	 * The provided rotation matrix is:
	 * m00 m01 m02
	 * m10 m11 m12
	 * m20 m21 m22
	 * 
	 * And the provided translation vector is <v0 v1 v2>
	 */
	public void setTransformationMatrix(final float m00, final float m01, final float m02, final float m10, final float m11, final float m12, final float m20, final float m21, final float m22, final float v0, final float v1, final float v2) {
		this.init();
		
		synchronized(this.transformationMatrix) {
			// column-major order for OpenGl
			this.transformationMatrix.put(m00);
			this.transformationMatrix.put(m10);
			this.transformationMatrix.put(m20);
			this.transformationMatrix.put(0);
			this.transformationMatrix.put(m01);
			this.transformationMatrix.put(m11);
			this.transformationMatrix.put(m21);
			this.transformationMatrix.put(0);
			this.transformationMatrix.put(m02);
			this.transformationMatrix.put(m12);
			this.transformationMatrix.put(m22);
			this.transformationMatrix.put(0);
			this.transformationMatrix.put(v0);
			this.transformationMatrix.put(v1);
			this.transformationMatrix.put(v2);
			this.transformationMatrix.put(1);
		}
	}
	
	public void setIdentity() {
//		 column-major order for OpenGl
		this.transformationMatrix.put(1);
		this.transformationMatrix.put(0);
		this.transformationMatrix.put(0);
		this.transformationMatrix.put(0);
		this.transformationMatrix.put(0);
		this.transformationMatrix.put(1);
		this.transformationMatrix.put(0);
		this.transformationMatrix.put(0);
		this.transformationMatrix.put(0);
		this.transformationMatrix.put(0);
		this.transformationMatrix.put(1);
		this.transformationMatrix.put(0);
		this.transformationMatrix.put(0);
		this.transformationMatrix.put(0);
		this.transformationMatrix.put(0);
		this.transformationMatrix.put(1);
	}

	// make sure to call this once you have a UnitCell object if you set the
	// transformation with setFullSymmetryOperation().
	public void updateFullSymmetryDataWithUnitCell(final UnitCell cell) {
		// the translation vector in the matrix needs to be multiplied by the
		// unit cell.
//		if (cell != null) {
//			this.transformationMatrix.put(12,
//					(float) (this.transformationMatrix.get(12) * cell.lengthA));
//			this.transformationMatrix.put(13,
//					(float) (this.transformationMatrix.get(13) * cell.lengthB));
//			this.transformationMatrix.put(14,
//					(float) (this.transformationMatrix.get(14) * cell.lengthC));
//		}

		this.printMatrix("**regenerated with cell**");
	}

	// used for inverse. Copied from
	// http://www.flipcode.com/documents/matrfaq.html#Q24.
	private void m4_submat(final TransformationMatrix mr, final TransformationMatrix mb,
			final int i, final int j) {
		final TransformationMatrix unit = new TransformationMatrix(), tmp = new TransformationMatrix();
		unit.init();
		tmp.init();
		int ti, tj, idst = 0, jdst = 0;

		for (ti = 0; ti < 4; ti++) {
			if (ti < i) {
				idst = ti;
			} else if (ti > i) {
				idst = ti - 1;
			}

			for (tj = 0; tj < 4; tj++) {
				if (tj < j) {
					jdst = tj;
				} else if (tj > j) {
					jdst = tj - 1;
				}

				if (ti != i && tj != j) {
					mb.transformationMatrix.put(idst * 3 + jdst,
							mr.transformationMatrix.get(ti * 4 + tj));
				}
			}
		}
	}

	// used for inverse. Copied from
	// http://www.flipcode.com/documents/matrfaq.html#Q24.
	private float m3_det(final TransformationMatrix mat) {
		float det;

		det = mat.transformationMatrix.get(0)
				* (mat.transformationMatrix.get(4)
						* mat.transformationMatrix.get(8) - mat.transformationMatrix
						.get(7)
						* mat.transformationMatrix.get(5))
				- mat.transformationMatrix.get(1)
				* (mat.transformationMatrix.get(3)
						* mat.transformationMatrix.get(8) - mat.transformationMatrix
						.get(6)
						* mat.transformationMatrix.get(5))
				+ mat.transformationMatrix.get(2)
				* (mat.transformationMatrix.get(3)
						* mat.transformationMatrix.get(7) - mat.transformationMatrix
						.get(6)
						* mat.transformationMatrix.get(4));

		return (det);
	}

	// used for inverse. Copied from
	// http://www.flipcode.com/documents/matrfaq.html#Q24.
	private float m4_det(final TransformationMatrix mr) {
		float det, result = 0, i = 1;
		final TransformationMatrix msub3 = new TransformationMatrix();
		msub3.init();
		int n;

		for (n = 0; n < 4; n++, i *= -1) {
			this.m4_submat(mr, msub3, 0, n);

			det = this.m3_det(msub3);
			result += mr.transformationMatrix.get(n) * det * i;
		}

		return (result);
	}

	// used for inverse. Copied from
	// http://www.flipcode.com/documents/matrfaq.html#Q24.
	private int m4_inverse(final TransformationMatrix mr, final TransformationMatrix ma) {
		final float mdet = this.m4_det(ma);
		final TransformationMatrix mtemp = new TransformationMatrix();
		mtemp.init();
		int i, j, sign;

		if (Math.abs(mdet) < 0.0005) {
			return (0);
		}

		for (i = 0; i < 4; i++) {
			for (j = 0; j < 4; j++) {
				sign = 1 - ((i + j) % 2) * 2;

				this.m4_submat(ma, mtemp, i, j);

				mr.transformationMatrix.put(i + j * 4, (this.m3_det(mtemp) * sign)
						/ mdet);
			}
		}

		return (1);
	}

	/**
	 * Return the inverse of this matrix.
	 * 
	 * @return the inverse.
	 */
//	public TransformationMatrix inverse() {
//		final TransformationMatrix returned = new TransformationMatrix();
//		returned.init();
//
//		this.m4_inverse(returned, this);
//
//		return returned;
//	}

	/**
	 * Given a 4x4 array "matrix0", this function replaces it with the LU
	 * decomposition of a row-wise permutation of itself. The input parameters
	 * are "matrix0" and "dimen". The array "matrix0" is also an output
	 * parameter. The vector "row_perm[4]" is an output parameter that contains
	 * the row permutations resulting from partial pivoting. The output
	 * parameter "even_row_xchg" is 1 when the number of row exchanges is even,
	 * or -1 otherwise. Assumes data type is always double.
	 * 
	 * This function is similar to luDecomposition, except that it is tuned
	 * specifically for 4x4 matrices.
	 * 
	 * @return true if the matrix is nonsingular, or false otherwise.
	 */
	//
	// Reference: Press, Flannery, Teukolsky, Vetterling,
	// _Numerical_Recipes_in_C_, Cambridge University Press,
	// 1988, pp 40-45.
	//
	static boolean luDecomposition(final double[] matrix0, final int[] row_perm) {

		final double row_scale[] = new double[4];

		// Determine implicit scaling information by looping over rows
		{
			int i, j;
			int ptr, rs;
			double big, temp;

			ptr = 0;
			rs = 0;

			// For each row ...
			i = 4;
			while (i-- != 0) {
				big = 0.0;

				// For each column, find the largest element in the row
				j = 4;
				while (j-- != 0) {
					temp = matrix0[ptr++];
					temp = Math.abs(temp);
					if (temp > big) {
						big = temp;
					}
				}

				// Is the matrix singular?
				if (big == 0.0) {
					return false;
				}
				row_scale[rs++] = 1.0 / big;
			}
		}

		{
			int j;
			int mtx;

			mtx = 0;

			// For all columns, execute Crout's method
			for (j = 0; j < 4; j++) {
				int i, imax, k;
				int target, p1, p2;
				double sum, big, temp;

				// Determine elements of upper diagonal matrix U
				for (i = 0; i < j; i++) {
					target = mtx + (4 * i) + j;
					sum = matrix0[target];
					k = i;
					p1 = mtx + (4 * i);
					p2 = mtx + j;
					while (k-- != 0) {
						sum -= matrix0[p1] * matrix0[p2];
						p1++;
						p2 += 4;
					}
					matrix0[target] = sum;
				}

				// Search for largest pivot element and calculate
				// intermediate elements of lower diagonal matrix L.
				big = 0.0;
				imax = -1;
				for (i = j; i < 4; i++) {
					target = mtx + (4 * i) + j;
					sum = matrix0[target];
					k = j;
					p1 = mtx + (4 * i);
					p2 = mtx + j;
					while (k-- != 0) {
						sum -= matrix0[p1] * matrix0[p2];
						p1++;
						p2 += 4;
					}
					matrix0[target] = sum;

					// Is this the best pivot so far?
					if ((temp = row_scale[i] * Math.abs(sum)) >= big) {
						big = temp;
						imax = i;
					}
				}

				if (imax < 0) {
					new Exception().printStackTrace();
				}

				// Is a row exchange necessary?
				if (j != imax) {
					// Yes: exchange rows
					k = 4;
					p1 = mtx + (4 * imax);
					p2 = mtx + (4 * j);
					while (k-- != 0) {
						temp = matrix0[p1];
						matrix0[p1++] = matrix0[p2];
						matrix0[p2++] = temp;
					}

					// Record change in scale factor
					row_scale[imax] = row_scale[j];
				}

				// Record row permutation
				row_perm[j] = imax;

				// Is the matrix singular
				if (matrix0[(mtx + (4 * j) + j)] == 0.0) {
					return false;
				}

				// Divide elements of lower diagonal matrix L by pivot
				if (j != (4 - 1)) {
					temp = 1.0 / (matrix0[(mtx + (4 * j) + j)]);
					target = mtx + (4 * (j + 1)) + j;
					i = 3 - j;
					while (i-- != 0) {
						matrix0[target] *= temp;
						target += 4;
					}
				}
			}
		}

		return true;
	}

	/**
	 * From j3d.org's vecmath library. Solves a set of linear equations. The
	 * input parameters "matrix1", and "row_perm" come from luDecompostionD4x4
	 * and do not change here. The parameter "matrix2" is a set of column
	 * vectors assembled into a 4x4 matrix of floating-point values. The
	 * procedure takes each column of "matrix2" in turn and treats it as the
	 * right-hand side of the matrix equation Ax = LUx = b. The solution vector
	 * replaces the original column of the matrix.
	 * 
	 * If "matrix2" is the identity matrix, the procedure replaces its contents
	 * with the inverse of the matrix from which "matrix1" was originally
	 * derived.
	 */
	//
	// Reference: Press, Flannery, Teukolsky, Vetterling,
	// _Numerical_Recipes_in_C_, Cambridge University Press,
	// 1988, pp 44-45.
	//
	static void luBacksubstitution(final double[] matrix1, final int[] row_perm,
			final double[] matrix2) {

		int i, ii, ip, j, k;
		int rp;
		int cv, rv;

		// rp = row_perm;
		rp = 0;

		// For each column vector of matrix2 ...
		for (k = 0; k < 4; k++) {
			// cv = &(matrix2[0][k]);
			cv = k;
			ii = -1;

			// Forward substitution
			for (i = 0; i < 4; i++) {
				double sum;

				ip = row_perm[rp + i];
				sum = matrix2[cv + 4 * ip];
				matrix2[cv + 4 * ip] = matrix2[cv + 4 * i];
				if (ii >= 0) {
					// rv = &(matrix1[i][0]);
					rv = i * 4;
					for (j = ii; j <= i - 1; j++) {
						sum -= matrix1[rv + j] * matrix2[cv + 4 * j];
					}
				} else if (sum != 0.0) {
					ii = i;
				}
				matrix2[cv + 4 * i] = sum;
			}

			// Backsubstitution
			// rv = &(matrix1[3][0]);
			rv = 3 * 4;
			matrix2[cv + 4 * 3] /= matrix1[rv + 3];

			rv -= 4;
			matrix2[cv + 4 * 2] = (matrix2[cv + 4 * 2] - matrix1[rv + 3]
					* matrix2[cv + 4 * 3])
					/ matrix1[rv + 2];

			rv -= 4;
			matrix2[cv + 4 * 1] = (matrix2[cv + 4 * 1] - matrix1[rv + 2]
					* matrix2[cv + 4 * 2] - matrix1[rv + 3]
					* matrix2[cv + 4 * 3])
					/ matrix1[rv + 1];

			rv -= 4;
			matrix2[cv + 4 * 0] = (matrix2[cv + 4 * 0] - matrix1[rv + 1]
					* matrix2[cv + 4 * 1] - matrix1[rv + 2]
					* matrix2[cv + 4 * 2] - matrix1[rv + 3]
					* matrix2[cv + 4 * 3])
					/ matrix1[rv + 0];
		}
	}

	public TransformationMatrix inverse3() {
		final TransformationMatrix returned = new TransformationMatrix();
		returned.init();

		final double temp[] = new double[16];
		final double result[] = new double[16];
		final int row_perm[] = new int[4];
		int i;
		// Copy source matrix to t1tmp
		temp[0] = this.transformationMatrix.get(0);
		temp[1] = this.transformationMatrix.get(4);
		temp[2] = this.transformationMatrix.get(8);
		temp[3] = this.transformationMatrix.get(12);

		temp[4] = this.transformationMatrix.get(1);
		temp[5] = this.transformationMatrix.get(5);
		temp[6] = this.transformationMatrix.get(9);
		temp[7] = this.transformationMatrix.get(13);

		temp[8] = this.transformationMatrix.get(2);
		temp[9] = this.transformationMatrix.get(6);
		temp[10] = this.transformationMatrix.get(10);
		temp[11] = this.transformationMatrix.get(14);

		temp[12] = this.transformationMatrix.get(3);
		temp[13] = this.transformationMatrix.get(7);
		temp[14] = this.transformationMatrix.get(11);
		temp[15] = this.transformationMatrix.get(15);

		// Calculate LU decomposition: Is the matrix singular?
		if (!TransformationMatrix.luDecomposition(temp, row_perm)) {
			// Matrix has no inverse
			new Exception("Error: matrix has no inverse.").printStackTrace();
		}

		// Perform back substitution on the identity matrix
		for (i = 0; i < 16; i++) {
			result[i] = 0.0;
		}
		result[0] = 1.0;
		result[5] = 1.0;
		result[10] = 1.0;
		result[15] = 1.0;
		TransformationMatrix.luBacksubstitution(temp, row_perm, result);

		returned.transformationMatrix.put(0, (float) result[0]);
		returned.transformationMatrix.put(4, (float) result[1]);
		returned.transformationMatrix.put(8, (float) result[2]);
		returned.transformationMatrix.put(12, (float) result[3]);

		returned.transformationMatrix.put(1, (float) result[4]);
		returned.transformationMatrix.put(5, (float) result[5]);
		returned.transformationMatrix.put(9, (float) result[6]);
		returned.transformationMatrix.put(13, (float) result[7]);

		returned.transformationMatrix.put(2, (float) result[8]);
		returned.transformationMatrix.put(6, (float) result[9]);
		returned.transformationMatrix.put(10, (float) result[10]);
		returned.transformationMatrix.put(14, (float) result[11]);

		returned.transformationMatrix.put(3, (float) result[12]);
		returned.transformationMatrix.put(7, (float) result[13]);
		returned.transformationMatrix.put(11, (float) result[14]);
		returned.transformationMatrix.put(15, (float) result[15]);

		return returned;
	}

	/**
	 * Ripped from
	 * http://www.euclideanspace.com/maths/algebra/matrix/code/#invert
	 * 
	 * @return the inverse of this 4x4 matrix
	 */
//	public TransformationMatrix inverse2() {
//		final TransformationMatrix returned = new TransformationMatrix();
//		returned.init();
//
//		final float m00 = this.transformationMatrix.get(0);
//		final float m10 = this.transformationMatrix.get(1);
//		final float m20 = this.transformationMatrix.get(2);
//		final float m30 = this.transformationMatrix.get(3);
//		final float m01 = this.transformationMatrix.get(4);
//		final float m11 = this.transformationMatrix.get(5);
//		final float m21 = this.transformationMatrix.get(6);
//		final float m31 = this.transformationMatrix.get(7);
//		final float m02 = this.transformationMatrix.get(8);
//		final float m12 = this.transformationMatrix.get(9);
//		final float m22 = this.transformationMatrix.get(10);
//		final float m32 = this.transformationMatrix.get(11);
//		final float m03 = this.transformationMatrix.get(12);
//		final float m13 = this.transformationMatrix.get(13);
//		final float m23 = this.transformationMatrix.get(14);
//		final float m33 = this.transformationMatrix.get(15);
//
//		float r_m00 = m12 * m23 * m31 - m13 * m22 * m31 + m13 * m21 * m32 - m11
//				* m23 * m32 - m12 * m21 * m33 + m11 * m22 * m33;
//		float r_m10 = m13 * m22 * m30 - m12 * m23 * m30 - m13 * m20 * m32 + m10
//				* m23 * m32 + m12 * m20 * m33 - m10 * m22 * m33;
//		float r_m20 = m11 * m23 * m30 - m13 * m21 * m30 + m13 * m20 * m31 - m10
//				* m23 * m31 - m11 * m20 * m33 + m10 * m21 * m33;
//		float r_m30 = m12 * m21 * m30 - m11 * m22 * m30 - m12 * m20 * m31 + m10
//				* m22 * m31 + m11 * m20 * m32 - m10 * m21 * m32;
//		float r_m01 = m03 * m22 * m31 - m02 * m23 * m31 - m03 * m21 * m32 + m01
//				* m23 * m32 + m02 * m21 * m33 - m01 * m22 * m33;
//		float r_m11 = m02 * m23 * m30 - m03 * m22 * m30 + m03 * m20 * m32 - m00
//				* m23 * m32 - m02 * m20 * m33 + m00 * m22 * m33;
//		float r_m21 = m03 * m21 * m30 - m01 * m23 * m30 - m03 * m20 * m31 + m00
//				* m23 * m31 + m01 * m20 * m33 - m00 * m21 * m33;
//		float r_m31 = m01 * m22 * m30 - m02 * m21 * m30 + m02 * m20 * m31 - m00
//				* m22 * m31 - m01 * m20 * m32 + m00 * m21 * m32;
//		float r_m02 = m02 * m13 * m31 - m03 * m12 * m31 + m03 * m11 * m32 - m01
//				* m13 * m32 - m02 * m11 * m33 + m01 * m12 * m33;
//		float r_m12 = m03 * m12 * m30 - m02 * m13 * m30 - m03 * m10 * m32 + m00
//				* m13 * m32 + m02 * m10 * m33 - m00 * m12 * m33;
//		float r_m22 = m01 * m13 * m30 - m03 * m11 * m30 + m03 * m10 * m31 - m00
//				* m13 * m31 - m01 * m10 * m33 + m00 * m11 * m33;
//		float r_m32 = m02 * m11 * m30 - m01 * m12 * m30 - m02 * m10 * m31 + m00
//				* m12 * m31 + m01 * m10 * m32 - m00 * m11 * m32;
//		float r_m03 = m03 * m12 * m21 - m02 * m13 * m21 - m03 * m11 * m22 + m01
//				* m13 * m22 + m02 * m11 * m23 - m01 * m12 * m23;
//		float r_m13 = m02 * m13 * m20 - m03 * m12 * m20 + m03 * m10 * m22 - m00
//				* m13 * m22 - m02 * m10 * m23 + m00 * m12 * m23;
//		float r_m23 = m03 * m11 * m20 - m01 * m13 * m20 - m03 * m10 * m21 + m00
//				* m13 * m21 + m01 * m10 * m23 - m00 * m11 * m23;
//		float r_m33 = m01 * m12 * m20 - m02 * m11 * m20 + m02 * m10 * m21 - m00
//				* m12 * m21 - m01 * m10 * m22 + m00 * m11 * m22;
//
//		// 1 / determinant
//		final float scale = (1 / (r_m03 * r_m12 * r_m21 * r_m30 - r_m02 * r_m13
//				* r_m21 * r_m30 - r_m03 * r_m11 * r_m22 * r_m30 + r_m01 * r_m13
//				* r_m22 * r_m30 + r_m02 * r_m11 * r_m23 * r_m30 - r_m01 * r_m12
//				* r_m23 * r_m30 - r_m03 * r_m12 * r_m20 * r_m31 + r_m02 * r_m13
//				* r_m20 * r_m31 + r_m03 * r_m10 * r_m22 * r_m31 - r_m00 * r_m13
//				* r_m22 * r_m31 - r_m02 * r_m10 * r_m23 * r_m31 + r_m00 * r_m12
//				* r_m23 * r_m31 + r_m03 * r_m11 * r_m20 * r_m32 - r_m01 * r_m13
//				* r_m20 * r_m32 - r_m03 * r_m10 * r_m21 * r_m32 + r_m00 * r_m13
//				* r_m21 * r_m32 + r_m01 * r_m10 * r_m23 * r_m32 - r_m00 * r_m11
//				* r_m23 * r_m32 - r_m02 * r_m11 * r_m20 * r_m33 + r_m01 * r_m12
//				* r_m20 * r_m33 + r_m02 * r_m10 * r_m21 * r_m33 - r_m00 * r_m12
//				* r_m21 * r_m33 - r_m01 * r_m10 * r_m22 * r_m33 + r_m00 * r_m11
//				* r_m22 * r_m33));
//
//		// scale the matrix
//		r_m00 *= scale;
//		r_m01 *= scale;
//		r_m02 *= scale;
//		r_m03 *= scale;
//		r_m10 *= scale;
//		r_m11 *= scale;
//		r_m12 *= scale;
//		r_m13 *= scale;
//		r_m20 *= scale;
//		r_m21 *= scale;
//		r_m22 *= scale;
//		r_m23 *= scale;
//		r_m30 *= scale;
//		r_m31 *= scale;
//		r_m32 *= scale;
//		r_m33 *= scale;
//
//		returned.transformationMatrix.put(0, r_m00);
//		returned.transformationMatrix.put(1, r_m10);
//		returned.transformationMatrix.put(2, r_m20);
//		returned.transformationMatrix.put(3, r_m30);
//		returned.transformationMatrix.put(4, r_m01);
//		returned.transformationMatrix.put(5, r_m11);
//		returned.transformationMatrix.put(6, r_m21);
//		returned.transformationMatrix.put(7, r_m31);
//		returned.transformationMatrix.put(8, r_m02);
//		returned.transformationMatrix.put(9, r_m12);
//		returned.transformationMatrix.put(10, r_m22);
//		returned.transformationMatrix.put(11, r_m32);
//		returned.transformationMatrix.put(12, r_m03);
//		returned.transformationMatrix.put(13, r_m13);
//		returned.transformationMatrix.put(14, r_m23);
//		returned.transformationMatrix.put(15, r_m33);
//
//		return returned;
//	}

	public void updateFullSymmetryDataWithInverseFractionalTransform(
			final TransformationMatrix fractional,
			final TransformationMatrix fractionalInverse) {
		TransformationMatrix result = this.multiply4square_x_4square2(fractionalInverse,
				this);
		result.printMatrix("**fractional * symmetry**");
		result = this.multiply4square_x_4square2(result, fractional);
		this.transformationMatrix = result.transformationMatrix;
		
		// quick fix, to remove rounding errors.
		for(int i = 0; i < 16; i++) {
			final float val = this.transformationMatrix.get(i);
			if(val != 0 && val < 0.0000001 && val > -0.0000001) {
				this.transformationMatrix.put(i, 0);
			}
		}

		this.printMatrix("**symmetry * inverse fractional**");
	}

	private TransformationMatrix multiply4square_x_4square2(
			final TransformationMatrix leftMat, final TransformationMatrix rightMat) {
		final TransformationMatrix result = new TransformationMatrix();
		result.init();

		float m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33; // vars
		// for
		// temp
		// result
		// matrix

		m00 = leftMat.transformationMatrix.get(0) * rightMat.transformationMatrix.get(0) + leftMat.transformationMatrix.get(4) * rightMat.transformationMatrix.get(1) + leftMat.transformationMatrix.get(8) * rightMat.transformationMatrix.get(2)
				+ leftMat.transformationMatrix.get(12) * rightMat.transformationMatrix.get(3);
		m01 = leftMat.transformationMatrix.get(0) * rightMat.transformationMatrix.get(4) + leftMat.transformationMatrix.get(4) * rightMat.transformationMatrix.get(5) + leftMat.transformationMatrix.get(8) * rightMat.transformationMatrix.get(6)
				+ leftMat.transformationMatrix.get(12) * rightMat.transformationMatrix.get(7);
		m02 = leftMat.transformationMatrix.get(0) * rightMat.transformationMatrix.get(8) + leftMat.transformationMatrix.get(4) * rightMat.transformationMatrix.get(9) + leftMat.transformationMatrix.get(8) * rightMat.transformationMatrix.get(10)
				+ leftMat.transformationMatrix.get(12) * rightMat.transformationMatrix.get(11);
		m03 = leftMat.transformationMatrix.get(0) * rightMat.transformationMatrix.get(12) + leftMat.transformationMatrix.get(4) * rightMat.transformationMatrix.get(13) + leftMat.transformationMatrix.get(8) * rightMat.transformationMatrix.get(14)
				+ leftMat.transformationMatrix.get(12) * rightMat.transformationMatrix.get(15);

		m10 = leftMat.transformationMatrix.get(1) * rightMat.transformationMatrix.get(0) + leftMat.transformationMatrix.get(5) * rightMat.transformationMatrix.get(1) + leftMat.transformationMatrix.get(9) * rightMat.transformationMatrix.get(2)
				+ leftMat.transformationMatrix.get(13) * rightMat.transformationMatrix.get(3);
		m11 = leftMat.transformationMatrix.get(1) * rightMat.transformationMatrix.get(4) + leftMat.transformationMatrix.get(5) * rightMat.transformationMatrix.get(5) + leftMat.transformationMatrix.get(9) * rightMat.transformationMatrix.get(6)
				+ leftMat.transformationMatrix.get(13) * rightMat.transformationMatrix.get(7);
		m12 = leftMat.transformationMatrix.get(1) * rightMat.transformationMatrix.get(8) + leftMat.transformationMatrix.get(5) * rightMat.transformationMatrix.get(9) + leftMat.transformationMatrix.get(9) * rightMat.transformationMatrix.get(10)
				+ leftMat.transformationMatrix.get(13) * rightMat.transformationMatrix.get(11);
		m13 = leftMat.transformationMatrix.get(1) * rightMat.transformationMatrix.get(12) + leftMat.transformationMatrix.get(5) * rightMat.transformationMatrix.get(13) + leftMat.transformationMatrix.get(9) * rightMat.transformationMatrix.get(14)
				+ leftMat.transformationMatrix.get(13) * rightMat.transformationMatrix.get(15);

		m20 = leftMat.transformationMatrix.get(2) * rightMat.transformationMatrix.get(0) + leftMat.transformationMatrix.get(6) * rightMat.transformationMatrix.get(1) + leftMat.transformationMatrix.get(10) * rightMat.transformationMatrix.get(2)
				+ leftMat.transformationMatrix.get(14) * rightMat.transformationMatrix.get(3);
		m21 = leftMat.transformationMatrix.get(2) * rightMat.transformationMatrix.get(4) + leftMat.transformationMatrix.get(6) * rightMat.transformationMatrix.get(5) + leftMat.transformationMatrix.get(10) * rightMat.transformationMatrix.get(6)
				+ leftMat.transformationMatrix.get(14) * rightMat.transformationMatrix.get(7);
		m22 = leftMat.transformationMatrix.get(2) * rightMat.transformationMatrix.get(8) + leftMat.transformationMatrix.get(6) * rightMat.transformationMatrix.get(9) + leftMat.transformationMatrix.get(10) * rightMat.transformationMatrix.get(10)
				+ leftMat.transformationMatrix.get(14) * rightMat.transformationMatrix.get(11);
		m23 = leftMat.transformationMatrix.get(2) * rightMat.transformationMatrix.get(12) + leftMat.transformationMatrix.get(6) * rightMat.transformationMatrix.get(13) + leftMat.transformationMatrix.get(10) * rightMat.transformationMatrix.get(14)
				+ leftMat.transformationMatrix.get(14) * rightMat.transformationMatrix.get(15);

		m30 = leftMat.transformationMatrix.get(3) * rightMat.transformationMatrix.get(0) + leftMat.transformationMatrix.get(7) * rightMat.transformationMatrix.get(1) + leftMat.transformationMatrix.get(11) * rightMat.transformationMatrix.get(2)
				+ leftMat.transformationMatrix.get(15) * rightMat.transformationMatrix.get(3);
		m31 = leftMat.transformationMatrix.get(3) * rightMat.transformationMatrix.get(4) + leftMat.transformationMatrix.get(7) * rightMat.transformationMatrix.get(5) + leftMat.transformationMatrix.get(11) * rightMat.transformationMatrix.get(6)
				+ leftMat.transformationMatrix.get(15) * rightMat.transformationMatrix.get(7);
		m32 = leftMat.transformationMatrix.get(3) * rightMat.transformationMatrix.get(8) + leftMat.transformationMatrix.get(7) * rightMat.transformationMatrix.get(9) + leftMat.transformationMatrix.get(11) * rightMat.transformationMatrix.get(10)
				+ leftMat.transformationMatrix.get(15) * rightMat.transformationMatrix.get(11);
		m33 = leftMat.transformationMatrix.get(3) * rightMat.transformationMatrix.get(12) + leftMat.transformationMatrix.get(7) * rightMat.transformationMatrix.get(13) + leftMat.transformationMatrix.get(11) * rightMat.transformationMatrix.get(14)
				+ leftMat.transformationMatrix.get(15) * rightMat.transformationMatrix.get(15);

		result.transformationMatrix.put(0, m00);
		result.transformationMatrix.put(4, m01);
		result.transformationMatrix.put(8, m02);
		result.transformationMatrix.put(12, m03);
		result.transformationMatrix.put(1, m10);
		result.transformationMatrix.put(5, m11);
		result.transformationMatrix.put(9, m12);
		result.transformationMatrix.put(13, m13);
		result.transformationMatrix.put(2, m20);
		result.transformationMatrix.put(6, m21);
		result.transformationMatrix.put(10, m22);
		result.transformationMatrix.put(14, m23);
		result.transformationMatrix.put(3, m30);
		result.transformationMatrix.put(7, m31);
		result.transformationMatrix.put(11, m32);
		result.transformationMatrix.put(15, m33);

		return result;
	}

	// create the transformation matrix using a full symmetry operation string.
	public static final Pattern spaces = Pattern.compile("\\s+");

	public static final Pattern commaSpaces = Pattern.compile("\\s*,\\s*");

	public static final Pattern slash = Pattern.compile("/");

	public void setFullSymmetryOperation(final String fullSymmetryOperation_) {
		// if(this.symmetryShorthand.equals("7_555")) {
		// fullSymmetryOperation_ = "y,x,1/2-z";
		// }

		if (fullSymmetryOperation_ == null) {
			return;
		}

		final String fullSymmetryOperation = TransformationMatrix.spaces.matcher(fullSymmetryOperation_)
				.replaceAll("");
		final String[] xyzRawArray = TransformationMatrix.commaSpaces.split(fullSymmetryOperation);

		if (xyzRawArray == null || xyzRawArray.length != 3) {
			return;
		}

		this.init();

		// swap and/or invert the axes...
		// this sets up the values in the upper left 3x3 matrix.
		for (int i = 0; i < xyzRawArray.length; i++) {
			final String xyzRaw = xyzRawArray[i];
			
			// zero out the values first.
			this.transformationMatrix.put(i, 0f);
			this.transformationMatrix.put(i + 4, 0f);
			this.transformationMatrix.put(i + 8, 0f);
			
			int coordIndexX = xyzRaw.indexOf('x');
			if (coordIndexX >= 0) {
				if (coordIndexX != 0 && xyzRaw.charAt(coordIndexX - 1) == '-') {
					this.transformationMatrix.put(i, -1f);
				} else {
					this.transformationMatrix.put(i, 1f);
				}
			}
			
			int coordIndexY = xyzRaw.indexOf('y');

			if (coordIndexY >= 0) {
				if (coordIndexY != 0 && xyzRaw.charAt(coordIndexY - 1) == '-') {
					this.transformationMatrix.put(i + 4, -1f);
				} else {
					this.transformationMatrix.put(i + 4, 1f);
				}
			}
			
			int coordIndexZ = xyzRaw.indexOf('z');

			if (coordIndexZ >= 0) {
				if (coordIndexZ != 0
						&& xyzRaw.charAt(coordIndexZ - 1) == '-') {
					this.transformationMatrix.put(i + 8, -1f);
				} else {
					this.transformationMatrix.put(i + 8, 1f);
				}
			}
			
			int coordIndex = -1;
			if(coordIndexX >=0) {
				coordIndex = coordIndexX;
			}
			if(coordIndex >= 0) {
				if(coordIndexY >= 0) {
					coordIndex = Math.min(coordIndex, coordIndexY);
				}
			} else {
				coordIndex = coordIndexY;
			}
			if(coordIndex >= 0) {
				if(coordIndexZ >= 0) {
					coordIndex = Math.min(coordIndex, coordIndexZ);
				}
			} else {
				coordIndex = coordIndexZ;
			}
			if(coordIndex < 0) {
				// error
				this.transformationMatrix = null;
				return;
			}
			

			// set the translation coordinate in case the translation is zero.
			final int translationIndex = 12 + i;
			// if(isX) {
			// translationIndex = 12;
			// } else if(isY) {
			// translationIndex = 13;
			// } else { // isZ
			// translationIndex = 14;
			// }
			this.transformationMatrix.put(translationIndex, 0f);

			if (coordIndex != 0) {
				// find out where the fraction (if any) ends...
				final char tmp = xyzRaw.charAt(coordIndex - 1);
				if (tmp == '-' || tmp == '+') {
					coordIndex--;
				}

				if (coordIndex != 0) {
					int fractionStartIndex = 0;
					boolean isNegated = false;
					if (xyzRaw.charAt(0) == '-') {
						isNegated = true;
						fractionStartIndex = 1;
					}

					// get the fraction and convert it to a real
					final String fractionSt = xyzRaw.substring(fractionStartIndex,
							coordIndex);
					final String[] fractionParts = TransformationMatrix.slash.split(fractionSt);
					if (fractionParts == null || fractionParts.length == 0
							|| fractionParts.length > 2) { // error
						return;
					}
					float fraction = Float.parseFloat(fractionParts[0]);
					if (fractionParts.length == 2) {
						fraction /= Float.parseFloat(fractionParts[1]);
					}
					if (isNegated) {
						fraction = -fraction;
					}
					// fraction *= 193.800f;

					// do the translation...
					// this sets up the bottom left 3 element horizontal vector.
					this.transformationMatrix.put(translationIndex, fraction);
				}
			}
		}

		// debug - make an identity matrix
		// transformationMatrix.put(0, 1f);
		// transformationMatrix.put(1, 0f);
		// transformationMatrix.put(2, 0f);
		// transformationMatrix.put(4, 0f);
		// transformationMatrix.put(5, 1f);
		// transformationMatrix.put(6, 0f);
		// transformationMatrix.put(8, 0f);
		// transformationMatrix.put(9, 0f);
		// transformationMatrix.put(10, 1f);
		// transformationMatrix.put(12, 0f);
		// transformationMatrix.put(13, 0f);
		// transformationMatrix.put(14, 0f);

		// finish by setting up the right column of the matrix.
		this.transformationMatrix.put(3, 0f);
		this.transformationMatrix.put(7, 0f);
		this.transformationMatrix.put(11, 0f);
		this.transformationMatrix.put(15, 1f);

		this.printMatrix(fullSymmetryOperation);
	}

	public void printMatrix(final String fullSymmetryOperation)
	{
/* ** DEBUGGING - printMatrix
		System.err.println("Generated transformation matrix from full symmetry "
						+ fullSymmetryOperation + ", chain id "
						+ this.ndbChainId + ": ");
		
		for (int row = 0; row < 4; row++)
		{
			for (int column = 0; column < 4; column++)
				System.err.print(this.transformationMatrix
						.get(column * 4 + row)
						+ "\t");

			System.err.println();
		}
		System.err.println();
* **/
	}

	public void init() {
		if (this.transformationMatrix == null)
			this.transformationMatrix = BufferUtil.newFloatBuffer(16);
		else
			this.transformationMatrix.rewind();
	}

	private static final IntReference tmpReference = new IntReference();

	public void initFromFullSymmetryString(final String input,
			final int rotationDenominator, final int translationDenominator,
			boolean enable_xyz, boolean enable_hkl, boolean enable_abc) {
		if (input == null || input.length() == 0) {
			return;
		}

		// if(input.equals("y,x,-z")) {
		// input = "y,x,1/2-z";
		// }

		this.init();
		// make sure the bottom right position has a 1
		this.transformationMatrix.put(15, 1f);

		boolean have_xyz = false;
		boolean have_hkl = false;
		boolean have_abc = false;
		int Row = 0;
		int Column = -1;
		int Sign = 1;
		int Mult = 0;
		final double[] ValR = new double[3];
		double ValT = 0.;
		double Value = 0.;
		boolean have_value = false;
		final int P_Add = 0x01;
		final int P_Mult = 0x02;
		final int P_Value = 0x04;
		final int P_XYZ = 0x08;
		final int P_Comma = 0x10;
		int P_mode = P_Add | P_Value | P_XYZ;
		for (int i = 0; i < input.length(); i++) {
			final char curChar = input.charAt(i);
			boolean doCommaOrEndLine = i == input.length() - 1;
			if (!Character.isWhitespace(curChar)) {
				boolean doProcessAdd = false;
				boolean doProcessXYZ = false;
				switch (curChar) {
				case '_':
					break;
				case '+':
					Sign = 1;
					doProcessAdd = true;
					break;
				case '-':
					Sign = -1;
					doProcessAdd = true;
					break;
				case '*':
					if ((P_mode & P_Mult) == 0) {
						new Exception(input).printStackTrace();
					}
					Mult = 1;
					P_mode = P_Value | P_XYZ;
					break;
				case '/':
				case ':':
					if ((P_mode & P_Mult) == 0) {
						new Exception(input).printStackTrace();
					}
					Mult = -1;
					P_mode = P_Value;
					break;
				case '.':
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					if ((P_mode & P_Value) == 0) {
						new Exception(input).printStackTrace();
					}
					{
						// get the full double...
						int k = i + 1;
						for (; k < input.length(); k++) {
							final char nextChar = input.charAt(k);
							if (!Character.isDigit(nextChar) || nextChar != '.') {
								break;
							}
						}
						double V = Double.parseDouble(input.substring(i, k));
						i = k - 1;

						if (Sign == -1) {
							V = -V;
							Sign = 1;
						}
						if (Mult == 1) {
							Value *= V;
						} else if (Mult == -1) {
							if (V != 0.) {
								Value /= V;
							} else if (Value != 0.) {
								new Exception(input).printStackTrace();
							}
						} else {
							Value = V;
						}
					}
					have_value = true;
					P_mode = P_Comma | P_Add | P_Mult | P_XYZ;
					break;
				case 'X':
				case 'x':
					Column = 0;
					have_xyz = true;
					doProcessXYZ = true;
					break;
				case 'Y':
				case 'y':
					Column = 1;
					have_xyz = true;
					doProcessXYZ = true;
					break;
				case 'Z':
				case 'z':
					Column = 2;
					have_xyz = true;
					doProcessXYZ = true;
					break;
				case 'H':
				case 'h':
					Column = 0;
					have_hkl = true;
					doProcessXYZ = true;
					break;
				case 'K':
				case 'k':
					Column = 1;
					have_hkl = true;
					doProcessXYZ = true;
					break;
				case 'L':
				case 'l':
					Column = 2;
					have_hkl = true;
					doProcessXYZ = true;
					break;
				case 'A':
				case 'a':
					Column = 0;
					have_abc = true;
					doProcessXYZ = true;
					break;
				case 'B':
				case 'b':
					Column = 1;
					have_abc = true;
					doProcessXYZ = true;
					break;
				case 'C':
				case 'c':
					Column = 2;
					have_abc = true;
					doProcessXYZ = true;
					break;
				case ',':
				case ';':
					if (Row == 2) {
						new Exception(input + ": too many row expressions")
								.printStackTrace();
					}
					doCommaOrEndLine = true;
					break;
				default:
					new Exception(input).printStackTrace();
				}

				if (doProcessAdd) {
					if ((P_mode & P_Add) == 0) {
						new Exception(input).printStackTrace();
					}
					if (Column >= 0) {
						ValR[Column] += Value;
					} else {
						ValT += Value;
					}
					Value = 0.;
					have_value = false;
					Column = -1;
					Mult = 0;
					P_mode = P_Value | P_XYZ;
				} else if (doProcessXYZ) {
					if (have_xyz && !enable_xyz) {
						new Exception(
								input
										+ ": x,y,z notation not supported in this context")
								.printStackTrace();
					}
					if (have_hkl && !enable_hkl) {
						new Exception(
								input
										+ ": h,k,l notation not supported in this context")
								.printStackTrace();
					}
					if (have_abc && !enable_abc) {
						new Exception(
								input
										+ ": a,b,c notation not supported in this context")
								.printStackTrace();
					}
					if (have_xyz && have_hkl) {
						new Exception(input
								+ ": mix of x,y,z and h,k,l notation")
								.printStackTrace();
					}
					if (have_xyz && have_abc) {
						new Exception(input
								+ ": mix of x,y,z and a,b,c notation")
								.printStackTrace();
					}
					if (have_hkl && have_abc) {
						new Exception(input
								+ ": mix of h,k,l and a,b,c notation")
								.printStackTrace();
					}
					if ((P_mode & P_XYZ) == 0) {
						new Exception(input).printStackTrace();
					}
					if (!have_value) {
						Value = Sign;
						Sign = 1;
					}
					P_mode = P_Comma | P_Add | P_Mult;
				}
			}

			if (doCommaOrEndLine) {
				if ((P_mode & P_Comma) == 0) {
					new Exception(input + ": unexpected end of input")
							.printStackTrace();
				}
				if (Column >= 0) {
					ValR[Column] += Value;
				} else {
					ValT += Value;
				}
				for (int j = 0; j < 3; j++) {
					final int tableIndex = j * 4 + Row;
					TransformationMatrix.tmpReference.value = (int) this.transformationMatrix
							.get(tableIndex);
					if (TransformationMatrix.rationalize(ValR[j], TransformationMatrix.tmpReference, rotationDenominator) != 0) {
						new Exception(input + ": unsuitiblae rotation matrix")
								.printStackTrace();
					}

					this.transformationMatrix.put(tableIndex,
							TransformationMatrix.tmpReference.value);
				}
				final int tableIndex = Row * 4 + 3;
				TransformationMatrix.tmpReference.value = (int) this.transformationMatrix
						.get(tableIndex);
				if (TransformationMatrix.rationalize(ValT, TransformationMatrix.tmpReference, translationDenominator) != 0) {
					new Exception(input + ": unsuitiblae translation vector")
							.printStackTrace();
				}
				this.transformationMatrix.put(tableIndex, TransformationMatrix.tmpReference.value);
				Row++;
				Column = -1;
				Sign = 1;
				Mult = 0;
				for (int k = 0; k < 3; k++) {
					ValR[k] = 0.;
				}
				ValT = 0.;
				Value = 0.;
				have_value = false;
				P_mode = P_Add | P_Value | P_XYZ;
			}
		}

		if (Row != 3) {
			new Exception(input + ": not enough row expressions")
					.printStackTrace();
		}
	}

	private static int rationalize(double fVal, final IntReference iVal, final int den) {
		if (den == 0) {
			return -1;
		}
		fVal *= den;
		if (fVal < 0.) {
			iVal.value = (int) (fVal - .5);
		} else {
			iVal.value = (int) (fVal + .5);
		}
		fVal -= iVal.value;
		fVal /= den;
		if (fVal < 0.) {
			fVal = -fVal;
		}
		if (fVal > .0001) {
			return -1;
		}
		return 0;
	}

	public String id = null;

	public String ndbChainId = null;

	public String symmetryShorthand = null;

	public String code = null;
	
	public Cell cell = new Cell();

	public FloatBuffer transformationMatrix = null;
}
