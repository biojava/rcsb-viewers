//  $Id: Algebra.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
//
//  Copyright 2000-2004 The Regents of the University of California.
//  All Rights Reserved.
//
//  Permission to use, copy, modify and distribute any part of this
//  Molecular Biology Toolkit (MBT)
//  for educational, research and non-profit purposes, without fee, and without
//  a written agreement is hereby granted, provided that the above copyright
//  notice, this paragraph and the following three paragraphs appear in all
//  copies.
//
//  Those desiring to incorporate this MBT into commercial products
//  or use for commercial purposes should contact the Technology Transfer &
//  Intellectual Property Services, University of California, San Diego, 9500
//  Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815,
//  FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
//
//  IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
//  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//  LOST PROFITS, ARISING OUT OF THE USE OF THIS MBT, EVEN IF THE
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//  THE MBT PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
//  UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
//  UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
//  NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
//  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
//  MBT WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
//
//  For further information, please see:  http://mbt.sdsc.edu
//


package org.rcsb.mbt.model.geometry;


/**
 *  This class provides a number of methods for performing linear algebra
 *  calculations on cartesian coordinate arrays.  Note it is not tied to any
 *  particular vector implementation.
 *  
 *  TODO: This should be removed - it duplicates functionality already present in the
 *  javax.vecmath.  Points and vectors should be carried consistently.  All of the
 *  replicate vector operations packages should be consolidated.
 *  24-Nov-2008 - rickb
 *  
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Bond
 *  @see	org.rcsb.mbt.model.util.BondFactory
 */
public class ArrayLinearAlgebra
{
	/**
	 *  Compute the distance between two virtex coordinates.
	 *  <P>
	 *  <PRE>
	 *  v1--v2
	 *  </PRE>
	 *  @param	v1	First virtex coordinate as float[3].
	 *  @param	v2	Second virtex coordinate as float[3].
	 *  @return	the computed distence.
	 */
	public static float distance( final float v1[], final float v2[] )
	{
		return (float)Math.sqrt(
			(v2[0] - v1[0]) * (v2[0] - v1[0]) +
			(v2[1] - v1[1]) * (v2[1] - v1[1]) +
			(v2[2] - v1[2]) * (v2[2] - v1[2])
		);
	}
	
	public static double distance( final double v1[], final float v2[] )
	{
		return Math.sqrt(
			(v2[0] - v1[0]) * (v2[0] - v1[0]) +
			(v2[1] - v1[1]) * (v2[1] - v1[1]) +
			(v2[2] - v1[2]) * (v2[2] - v1[2])
		);
	}

	public static double distance( final double v1[], final double v2[] )
	{
		return Math.sqrt(
			(v2[0] - v1[0]) * (v2[0] - v1[0]) +
			(v2[1] - v1[1]) * (v2[1] - v1[1]) +
			(v2[2] - v1[2]) * (v2[2] - v1[2])
		);
	}

	/**
	 *  Calculate the angle formed by three virtex coordinates.
	 *  <P>
	 *  <PRE>
	 *  v1  v3
	 *   \  /
	 *    v2
	 *  </PRE>
	 *  <P>
	 *  @param	v1	An end virtex coordinate as float[3].
	 *  @param	v2	The middle virtex coordinate as float[3].
	 *  @param	v3	An end virtex coordinate as float[3].
	 *  @return	angle in degrees.
	 */
	public static float angle( final double _v1[] , final double _v2[], final double _v3[])
	{
		float v1[] = { (float)_v1[0], (float)_v1[1], (float)_v1[2] };
		float v2[] = { (float)_v2[0], (float)_v2[1], (float)_v2[2] };
		float v3[] = { (float)_v3[0], (float)_v3[1], (float)_v3[2] };
		      
		return angle(v1, v2, v3);
	}
	
	public static float angle( final float v1[], final float v2[], final float v3[] )
	{
		// See: http://mathworld.wolfram.com/Line-LineAngle.html

		// Compute the numerator.
		final float a =
			(v1[0] - v2[0]) * (v3[0] - v2[0]) +
			(v1[1] - v2[1]) * (v3[1] - v2[1]) +
			(v1[2] - v2[2]) * (v3[2] - v2[2]);

		// Compute the distance between v1 and v2.
		final float b = (float)Math.sqrt(
			(v1[0] - v2[0]) * (v1[0] - v2[0]) +
			(v1[1] - v2[1]) * (v1[1] - v2[1]) +
			(v1[2] - v2[2]) * (v1[2] - v2[2])
		);

		// Compute the distance between v3 and v2.
		final float c = (float)Math.sqrt(
			(v3[0] - v2[0]) * (v3[0] - v2[0]) +
			(v3[1] - v2[1]) * (v3[1] - v2[1]) +
			(v3[2] - v2[2]) * (v3[2] - v2[2])
		);

		// Calculate the angle between the two vectors.

		final float cos = a / (b * c);
		final float rad = (float)Math.acos( cos );
		final float degrees = (float)Math.toDegrees( rad );

		return degrees;
	 }


	/**
	 *  Calculate the dihedral angle defined by four virtex coordinates.
	 *  <P>
	 *  <PRE>
	 *  v1     v4
	 *   \     /
	 *    v2--v3
	 *  </PRE>
	 *  <P>
	 *  @param	v1	An end virtex coordinate as float[3].
	 *  @param	v2	An interior virtex coordinate as float[3].
	 *  @param	v3	An interior virtex coordinate as float[3].
	 *  @param	v4	An end virtex coordinate as float[3].
	 *  @return	dihedral angle in degrees.
	 */
	public static float dihedralAngle( final double _v1[], final double _v2[],
									   final double _v3[], final double _v4[] )
	{
		float v1[] = { (float)_v1[0], (float)_v1[1], (float)_v1[2] };
		float v2[] = { (float)_v2[0], (float)_v2[1], (float)_v2[2] };
		float v3[] = { (float)_v3[0], (float)_v3[1], (float)_v3[2] };
		float v4[] = { (float)_v4[0], (float)_v4[1], (float)_v4[2] };
		
		return dihedralAngle(v1, v2, v3, v4);
	}

	
	public static float dihedralAngle( final float v1[], final float v2[],
		final float v3[], final float v4[] )
	{
		// See: http://mathworld.wolfram.com/DihedralAngle.html

		// Calculate the normal components for plane 1 (defined by v1-v2-v3).
		final float a1 =
			(v3[1] - v2[1]) * (v1[2] - v2[2]) -
			(v1[1] - v2[1]) * (v3[2] - v2[2]);

		final float b1 =
			(v1[0] - v2[0]) * (v3[2] - v2[2]) -
			(v3[0] - v2[0]) * (v1[2] - v2[2]);

		final float c1 =
			(v3[0] - v2[0]) * (v1[1] - v2[1]) -
			(v1[0] - v2[0]) * (v3[1] - v2[1]);
		
		// Calculate the normal components for plane 2 (defined by v2-v3-v4).
		final float a2 =
			(v3[1] - v2[1]) * (v4[2] - v2[2]) -
			(v4[1] - v2[1]) * (v3[2] - v2[2]);

		final float b2 =
			(v4[0] - v2[0]) * (v3[2] - v2[2]) -
			(v3[0] - v2[0]) * (v4[2] - v2[2]);

		final float c2 =
			(v3[0] - v2[0]) * (v4[1] - v2[1]) -
			(v4[0] - v2[0]) * (v3[1] - v2[1]);

		// Calculate the dihedral angle between the two plane's normals.

		final float a = a1*a2 + b1*b2 + c1*c2;
		final float b = (float)Math.sqrt( a1*a1 + b1*b1 + c1*c1 );
		final float c = (float)Math.sqrt( a2*a2 + b2*b2 + c2*c2 );

		final float cos = a / (b * c);

		final float rad = (float)Math.acos( cos );
		final float degrees = (float)Math.toDegrees( rad );

		return degrees;
	}


	/**
	 *  Compute the vector cross product between v1 and v2 storing the
	 *  output in result.
	 *  <P>
	 *  @param	v1	First vector argument.
	 *  @param	v2	Second vector argument.
	 *  @param	result	The vector in which the result will be stored.
	 */
	public static void crossProduct( final float[] v1, final float[] v2, final float[] result )
	{
		if ( v1.length != 3 ) {
			throw new IllegalArgumentException( "v1.length != 3" );
		}
		if ( v2.length != 3 ) {
			throw new IllegalArgumentException( "v2.length != 3" );
		}
		if ( result.length != 3 ) {
			throw new IllegalArgumentException( "result.length != 3" );
		}
		if ( v1 == v2 ) {
			throw new IllegalArgumentException( "v1 == v2" );
		}
		if ( result == v1 ) {
			throw new IllegalArgumentException( "result == v1" );
		}
		if ( result == v2 ) {
			throw new IllegalArgumentException( "result == v2" );
		}

		result[0] = + ( ( v1[1] * v2[2] ) - ( v2[1] * v1[2] ) );
		result[1] = - ( ( v1[0] * v2[2] ) - ( v2[0] * v1[2] ) );
		result[2] = + ( ( v1[0] * v2[1] ) - ( v2[0] * v1[1] ) );
	}

	public static void crossProduct( final double[] v1, final double[] v2, final double[] result )
	{
		if ( v1.length < 3 ) {
			throw new IllegalArgumentException( "v1.length != 3" );
		}
		if ( v2.length < 3 ) {
			throw new IllegalArgumentException( "v2.length != 3" );
		}
		if ( result.length < 3 ) {
			throw new IllegalArgumentException( "result.length != 3" );
		}
		if ( v1 == v2 ) {
			throw new IllegalArgumentException( "v1 == v2" );
		}
		if ( result == v1 ) {
			throw new IllegalArgumentException( "result == v1" );
		}
		if ( result == v2 ) {
			throw new IllegalArgumentException( "result == v2" );
		}

		result[0] = + ( ( v1[1] * v2[2] ) - ( v2[1] * v1[2] ) );
		result[1] = - ( ( v1[0] * v2[2] ) - ( v2[0] * v1[2] ) );
		result[2] = + ( ( v1[0] * v2[1] ) - ( v2[0] * v1[1] ) );
	}

	/**
	 *  Return the vector dot product between v1 and v2.
	 *  <P>
	 *  @param	v1	First vector argument.
	 *  @param	v2	Second vector argument.
	 *  @return	The dot product value.
	 */
	public static float dotProduct( final float[] v1, final float[] v2 )
	{
		if ( v1.length != v2.length ) {
			throw new IllegalArgumentException( "v1.length != v2.length" );
		}

		float sumOfSquares = 0.0f;
		for ( int i=0; i<v1.length; i++ ) {
			sumOfSquares += v1[i]*v2[i];
		}

		return sumOfSquares;
	}
	
	public static double dotProduct( final double[] v1, final double[] v2 )
	{
		if ( v1.length != v2.length ) {
			throw new IllegalArgumentException( "v1.length != v2.length" );
		}

		double sumOfSquares = 0.0;
		for ( int i=0; i<v1.length; i++ ) {
			sumOfSquares += v1[i]*v2[i];
		}

		return sumOfSquares;
	}


	/**
	 *  Compute the normalized inVector storing the result in outVector.
	 *  Note: it is safe to pass the same object for in and out vectors.
	 *  <P>
	 *  @param	inVector	The input vector argument.
	 *  @param	outVector	The output vector.
	 */
	public static void normalizeVector( final float[] inVector, final float[] outVector )
	{
		if ( inVector == null ) {
			throw new NullPointerException( "null inVector" );
		}
		if ( outVector == null ) {
			throw new NullPointerException( "null outVector" );
		}
		if ( inVector.length != outVector.length ) {
			throw new IllegalArgumentException( "vector length mismatch" );
		}

		float length = ArrayLinearAlgebra.vectorLength( inVector );
		if ( length == 0.0 ) {
			length = 1.0f;
		}
		for ( int i=0; i<inVector.length; i++ ) {
			outVector[i] = inVector[i] / length;
		}
	}

	public static void normalizeVector( final double[] inVector, final double[] outVector )
	{
		if ( inVector == null ) {
			throw new NullPointerException( "null inVector" );
		}
		if ( outVector == null ) {
			throw new NullPointerException( "null outVector" );
		}
		if ( inVector.length != outVector.length ) {
			throw new IllegalArgumentException( "vector length mismatch" );
		}

		double length = ArrayLinearAlgebra.vectorLength( inVector );
		if ( length == 0.0 ) {
			length = 1.0;
		}
		for ( int i=0; i<inVector.length; i++ ) {
			outVector[i] = inVector[i] / length;
		}
	}

	/**
	 *  Compute the normalized vector in place.
	 *  <P>
	 *  @param	vector	The vector to be normalized in-place.
	 */
	public static void normalizeVector( final float[] vector )
	{
		ArrayLinearAlgebra.normalizeVector( vector, vector );
	}
	
	public static void normalizeVector( final double[] vector )
	{
		ArrayLinearAlgebra.normalizeVector( vector, vector );
	}


	/**
	 *  Return the cartesian length of the vector.
	 *  <P>
	 *  @param	vector	The input vector.
	 *  @return	The length of the input vector.
	 */
	public static float vectorLength( final float[] vector )
	{
		if ( vector == null ) {
			throw new NullPointerException( "null vector" );
		}

		float sqSum = 0.0f;
		for ( int i=0; i<vector.length; i++ ) {
			sqSum += vector[i] * vector[i];
		}

		return (float)Math.sqrt( sqSum );
	}

	public static double vectorLength( final double[] vector )
	{
		if ( vector == null ) {
			throw new NullPointerException( "null vector" );
		}

		float sqSum = 0.0f;
		for ( int i=0; i<vector.length; i++ ) {
			sqSum += vector[i] * vector[i];
		}

		return Math.sqrt( sqSum );
	}

	/**
	 *  Rotate the point by the given angle-axis {angle,x,y,z} arbitrary rotation vector
	 *  (not axis aligned).
	 *  <P>
	 *  @param	angleAxis	The angle-axis rotation vector.
	 *  @param	point	The point to be rotated in-place.
	 */
	public static void angleAxisRotate( final float[] angleAxis, final float[] point )
	{
		if ( angleAxis == null ) {
			throw new NullPointerException( "null angleAxis" );
		}
		if ( angleAxis.length < 4 ) {
			throw new IllegalArgumentException( "angleAxis.length < 4" );
		}
		if ( point == null ) {
			throw new NullPointerException( "null point" );
		}
		if ( point.length < 3 ) {
			throw new IllegalArgumentException( "point.length < 3" );
		}

		final float matrix[] = new float[16];

		ArrayLinearAlgebra.rotationToMatrix( angleAxis, matrix );
		ArrayLinearAlgebra.matrixRotate( matrix, point );
	}
	
	public static void angleAxisRotate( final double[] angleAxis, final double[] point )
	{
		if ( angleAxis == null ) {
			throw new NullPointerException( "null angleAxis" );
		}
		if ( angleAxis.length < 4 ) {
			throw new IllegalArgumentException( "angleAxis.length < 4" );
		}
		if ( point == null ) {
			throw new NullPointerException( "null point" );
		}
		if ( point.length < 3 ) {
			throw new IllegalArgumentException( "point.length < 3" );
		}

		final double matrix[] = new double[16];

		ArrayLinearAlgebra.rotationToMatrix( angleAxis, matrix );
		ArrayLinearAlgebra.matrixRotate( matrix, point );
	}


	/**
	 *  Rotate the point by the given 16-element rotation matrix.
	 *  <P>
	 *  @param	matrix	The 16-element rotation matrix.
	 *  @param	point	The point to be rotated in-place.
	 */
	public static void matrixRotate( final float[] matrix, final float[] point )
	{
		if ( matrix == null ) {
			throw new NullPointerException( "null matrix" );
		}
		if ( matrix.length < 16 ) {
			throw new IllegalArgumentException( "matrix.length < 16" );
		}
		if ( point == null ) {
			throw new NullPointerException( "null point" );
		}
		if ( point.length < 3 ) {
			throw new IllegalArgumentException( "point.length < 3" );
		}

		final float[] m = matrix;
		final float x = point[0];
		final float y = point[1];
		final float z = point[2];
		final float w = 1.0f;

		point[0] = m[0]*x + m[1]*y + m[2]*z  + m[3]*w;
		point[1] = m[4]*x + m[5]*y + m[6]*z  + m[7]*w;
		point[2] = m[8]*x + m[9]*y + m[10]*z + m[11]*w;
	}

	public static void matrixRotate( final double[] matrix, final double[] point )
	{
		if ( matrix == null ) {
			throw new NullPointerException( "null matrix" );
		}
		if ( matrix.length < 16 ) {
			throw new IllegalArgumentException( "matrix.length < 16" );
		}
		if ( point == null ) {
			throw new NullPointerException( "null point" );
		}
		if ( point.length < 3 ) {
			throw new IllegalArgumentException( "point.length < 3" );
		}

		final double[] m = matrix;
		final double x = point[0];
		final double y = point[1];
		final double z = point[2];
		final double w = 1.0f;

		point[0] = m[0]*x + m[1]*y + m[2]*z  + m[3]*w;
		point[1] = m[4]*x + m[5]*y + m[6]*z  + m[7]*w;
		point[2] = m[8]*x + m[9]*y + m[10]*z + m[11]*w;
	}

	/**
	 *  Convert the angle-axis {angle,x,y,z} rotation vector to a 16-element
	 *  rotation matrix.
	 *  <P>
	 *  @param	rotation	The angle-axis rotation vector.
	 *  @param	matrix	The resulting 16-element rotation matrix.
	 */
	public static void rotationToMatrix( final float[] rotation, final float[] matrix )
	{
		if ( rotation == null ) {
			throw new NullPointerException( "null rotation" );
		}
		if ( rotation.length < 4 ) {
			throw new IllegalArgumentException( "rotation.length < 4" );
		}
		if ( matrix == null ) {
			throw new NullPointerException( "null matrix" );
		}
		if ( matrix.length < 16 ) {
			throw new IllegalArgumentException( "matrix.length < 16" );
		}

		// Make sure that the rotation vector is unit length.
		final float len = (float)Math.sqrt(
			rotation[1] * rotation[1] +
			rotation[2] * rotation[2] +
			rotation[3] * rotation[3] );
		float norm = 1.0f / len;
		if ( norm == 0.0 ) {
			norm = 1.0f;
		}

		final float a = rotation[0];
		final float x = rotation[1] * norm;
		final float y = rotation[2] * norm;
		final float z = rotation[3] * norm;
		final float c = (float)Math.cos( a );
		final float s = (float)Math.sin( a );
		final float t = 1.0f - c;

		matrix[0]  = t*x*x + c;
		matrix[1]  = t*x*y - s*z;
		matrix[2]  = t*x*z + s*y;
		matrix[3]  = 0.0f;

		matrix[4]  = t*x*y + s*z;
		matrix[5]  = t*y*y + c;
		matrix[6]  = t*y*z - s*x;
		matrix[7]  = 0.0f;

		matrix[8]  = t*x*z - s*y;
		matrix[9]  = t*y*z + s*x;
		matrix[10] = t*z*z + c;
		matrix[11] = 0.0f;

		matrix[12] = 0.0f;
		matrix[13] = 0.0f;
		matrix[14] = 0.0f;
		matrix[15] = 1.0f;
	}
	
	public static void rotationToMatrix( final double[] rotation, final double[] matrix )
	{
		if ( rotation == null ) {
			throw new NullPointerException( "null rotation" );
		}
		if ( rotation.length < 4 ) {
			throw new IllegalArgumentException( "rotation.length < 4" );
		}
		if ( matrix == null ) {
			throw new NullPointerException( "null matrix" );
		}
		if ( matrix.length < 16 ) {
			throw new IllegalArgumentException( "matrix.length < 16" );
		}

		// Make sure that the rotation vector is unit length.
		final double len = Math.sqrt(
			rotation[1] * rotation[1] +
			rotation[2] * rotation[2] +
			rotation[3] * rotation[3] );
		double norm = 1.0 / len;
		if ( norm == 0.0 ) {
			norm = 1.0;
		}

		final double a = rotation[0];
		final double x = rotation[1] * norm;
		final double y = rotation[2] * norm;
		final double z = rotation[3] * norm;
		final double c = Math.cos( a );
		final double s = Math.sin( a );
		final double t = 1.0f - c;

		matrix[0]  = t*x*x + c;
		matrix[1]  = t*x*y - s*z;
		matrix[2]  = t*x*z + s*y;
		matrix[3]  = 0.0f;

		matrix[4]  = t*x*y + s*z;
		matrix[5]  = t*y*y + c;
		matrix[6]  = t*y*z - s*x;
		matrix[7]  = 0.0f;

		matrix[8]  = t*x*z - s*y;
		matrix[9]  = t*y*z + s*x;
		matrix[10] = t*z*z + c;
		matrix[11] = 0.0f;

		matrix[12] = 0.0f;
		matrix[13] = 0.0f;
		matrix[14] = 0.0f;
		matrix[15] = 1.0f;
	}
}

