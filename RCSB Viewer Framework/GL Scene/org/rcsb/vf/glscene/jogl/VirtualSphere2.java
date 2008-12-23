/*
 * BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence. This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors. These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 * http://www.biojava.org/
 *
 * This code was contributed from the Molecular Biology Toolkit
 * (MBT) project at the University of California San Diego.
 *
 * Please reference J.L. Moreland, A.Gramada, O.V. Buzko, Qing
 * Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (MBT):
 * A Modular Platform for Developing Molecular Visualization
 * Applications. BMC Bioinformatics, 6:21.
 *
 * The MBT project was funded as part of the National Institutes
 * of Health PPG grant number 1-P01-GM63208 and its National
 * Institute of General Medical Sciences (NIGMS) division. Ongoing
 * development for the MBT project is managed by the RCSB
 * Protein Data Bank(http://www.pdb.org) and supported by funds
 * from the National Science Foundation (NSF), the National
 * Institute of General Medical Sciences (NIGMS), the Office of
 * Science, Department of Energy (DOE), the National Library of
 * Medicine (NLM), the National Cancer Institute (NCI), the
 * National Center for Research Resources (NCRR), the National
 * Institute of Biomedical Imaging and Bioengineering (NIBIB),
 * the National Institute of Neurological Disorders and Stroke
 * (NINDS), and the National Institute of Diabetes and Digestive
 * and Kidney Diseases (NIDDK).
 *
 * Created on 2007/02/08
 *
 */ 
package org.rcsb.vf.glscene.jogl;

// package org.rcsb.vis.viewers;


import java.lang.Math;


/**
 *  Implements a Virtual Sphere algorithm for 3D rotation using
 *  a 2D input device.
 *  This can be used for manipulating objects or (by reversing the rotation)
 *  as part of a camera control/rotation behavior.
 *  <P>
 *  Idea based upon the paper "A Study in Interactive 3-D Rotation Using
 *  2-D Control Devices" by Michael Chen, S. Joy Mountford and
 *  Abigail Sellen published in the ACM Siggraph '88 proceedings
 *  (Volume 22, Number 4, August 1988).
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.vis.viewers.GeometryViewer
 */
public class VirtualSphere2
{
	private int cueCenterX = 320;
	private int cueCenterY = 240;
	private int cueRadius  = 120;

	private double[] u = null;
	private double[] v = null;


	/**
	 *  Primary constructor creates
	 *  a virtual sphere controller using the specified x,y
	 *  screen coordinate as the center and r as the radius of the
	 *  controller's bounding circle.
	 *  <P>
	 *  @param x	Screen x-coordinate for rotation circle.
	 *  @param y	Screen y-coordinate for rotation circle.
	 *  @param r	Screen radius for rotation circle.
	 */
	public VirtualSphere2( final int x, final int y, final int r )
	{
		// Call the pseudo-constructor method.
		this.virtualSphere( x, y, r );
	}


	/**
	 *  Utility constructor creates
	 *  a virtual sphere controller using default x,y
	 *  screen coordinates as the center and default r as the radius of the
	 *  controller's bounding circle.
	 */
	public VirtualSphere2()
	{
		// Call the pseudo-constructor method.
		this.virtualSphere( this.cueCenterX, this.cueCenterY, this.cueRadius );
	}


	/**
	 *  Pseudo-constructor which does all of the work for initialization.
	 *  All of the real constructors call this method.
	 *  <P>
	 *  @param x	Screen x-coordinate for rotation circle.
	 *  @param y	Screen y-coordinate for rotation circle.
	 *  @param r	Screen radius for rotation circle.
	 */
	private void virtualSphere( final int x, final int y, final int r )
	{
		this.u = new double[3];
		this.v = new double[3];

		this.setCircle( x, y, r );
	}


	/**
	 *  Set the parameters for the screen-space constraining 2D circle.
	 *  <P>
	 *  @param x	Screen x-coordinate for rotation circle.
	 *  @param y	Screen y-coordinate for rotation circle.
	 *  @param r	Screen radius for rotation circle.
	 */
	public void setCircle( final int x, final int y, final int r )
	{
		this.cueCenterX = x;
		this.cueCenterY = y;
		this.cueRadius  = r;
	}


	/**
	 *  Set the parameters for the screen-space constraining 2D circle
	 *  as {x,y,r}.
	 *  <P>
	 *  @param circle	Screen {x,y,radius} for rotation circle.
	 */
	public void setCircle( final int[] circle )
	{
		this.setCircle( circle[0], circle[1], circle[2] );
	}


	/**
	 *  Get the parameters for the screen-space constraining 2D circle
	 *  as {x,y,r}.
	 *  <P>
	 *  @param circle	Screen {x,y,radius} for rotation circle.
	 */
	public void getCircle( final int[] circle )
	{
		circle[0] = this.cueCenterX;
		circle[1] = this.cueCenterY;
		circle[2] = this.cueRadius;
	}


	/**
	 *  Determine the axis and angle (in radians) of rotation from two
	 *  screen coordinates and relative to the virtual sphere cue circle.
	 *  <P>
	 *  @param px	Screen start point, x-coordinate.
	 *  @param py	Screen start point, y-coordinate.
	 *  @param qx	Screen end point, x-coordinate.
	 *  @param qy	Screen end point, y-coordinate.
	 *  @param rotation	Rotation vector {angle,x,y,z} result.
	 */
	public void compute( final int px, final int py, final int qx, final int qy, final double[] rotation )
	{
		// Project the screen coordinates to 3D points on +z unit hemisphere.
		// Consider the two projected points as vectors from the center of the
		// unit sphere.

		this.pointOnUnitSphere( px, py, this.u );
		/*for(int i = 0; i < u.length; i++) {
			if(Double.isNaN(u[i])) {
				System.err.flush();
			}
		}*/
		
		this.pointOnUnitSphere( qx, qy, this.v );
		/*for(int i = 0; i < v.length; i++) {
			if(Double.isNaN(v[i])) {
				System.err.flush();
			}
		}*/

		// Compute the rotation that transforms vector u onto v.

		// The vector part of the rotation is the cross product (u x v).
		rotation[1] = + ( ( this.u[1] * this.v[2] ) - ( this.v[1] * this.u[2] ) );
		rotation[2] = - ( ( this.u[0] * this.v[2] ) - ( this.v[0] * this.u[2] ) );
		rotation[3] = + ( ( this.u[0] * this.v[1] ) - ( this.v[0] * this.u[1] ) );
		/*for(int i = 0; i < rotation.length; i++) {
			if(Double.isNaN(rotation[i])) {
				System.err.flush();
			}
		}*/
		
		// Normalize.
		double length = Math.sqrt(
			rotation[1] * rotation[1] +
			rotation[2] * rotation[2] +
			rotation[3] * rotation[3]
		);
		/*if(Double.isNaN(length)) {
			System.err.flush();
		}*/
		if ( length == 0.0f ) {
			length = 1.0f;
		}
		rotation[1] /= length;
		rotation[2] /= length;
		rotation[3] /= length;

		// The angle part of the rotation is acos(u DOT v / |u| * |v|).
		rotation[0] = Math.acos(
			(this.u[0]*this.v[0] + this.u[1]*this.v[1] + this.u[2]*this.v[2]) /
			(Math.sqrt( this.u[0]*this.u[0] + this.u[1]*this.u[1] + this.u[2]*this.u[2] ) *
			Math.sqrt( this.v[0]*this.v[0] + this.v[1]*this.v[1] + this.v[2]*this.v[2] ) )
		);

		/*for(int i = 0; i < rotation.length; i++) {
			if(Double.isNaN(rotation[i])) {
				System.err.flush();
			}
		}*/
	/*
		System.err.println( "JLM DEBUG: VirtualSphere2.compute:" );
		System.err.println( "\tm = " + px + "," + py + " - " + qx + "," + qy );
		System.err.println( "\tu = " + u[0] + ", " + u[1] + ", " + u[2] );
		System.err.println( "\tv = " + v[0] + ", " + v[1] + ", " + v[2] );
		System.err.println( "\tr = " + rotation[0] + ", " + rotation[1] + ", " + rotation[2] + ", " + rotation[3] );
	*/
	}


	/**
	 *  Project a 2D point on a circle to a 3D point on the +z hemisphere
	 *  of a unit sphere.  If the 2D point is outside the circle, it is
	 *  first mapped to the nearest point on the circle before projection.
	 *  Orthographic projection is used, though technically the field of
	 *  view of the camera should be taken into account.  But, the
	 *  discrepancy is neglegible.
	 *  <P>
	 *  @param px	Screen x-coordinate.
	 *  @param py	Screen y-coordinate.
	 *  @param v	3D rotation vector {angle,x,y,z} result.
	 */
	public void pointOnUnitSphere( final int px, final int py, final double[] v )
	{
		// Turn screen coordinates into vectors relative to the center of the
		// cue circle and normalize them. Flip the y value since the 3D
		// coordinate has positive y going up.
		v[0] = (px - this.cueCenterX) / (double) this.cueRadius;
		v[1] =  -(py - this.cueCenterY) / (double) this.cueRadius;

		/*for(int i = 0; i < v.length; i++) {
			if(Double.isNaN(v[i])) {
				System.err.flush();
			}
		}*/
		
		final double lengthSqared = v[0]*v[0] + v[1]*v[1];

		/*if(Double.isNaN(lengthSqared)) {
			System.err.flush();
		}*/
		
		// Project the point onto the sphere, assuming orthographic projection.
		// Points beyond the virtual sphere are normalized onto edge of the
		// sphere (where z = 0).
		if ( lengthSqared < 1.0 )
		{
			// We are inside the circle, so use the general solution.
			v[2] = Math.sqrt( 1.0 - lengthSqared );
		}
		else
		{
			// We are outside the circle, so lock to the edge.
			final double length = Math.sqrt( lengthSqared );
			v[0] /= length;
			v[1] /= length;
			v[2] = 0.0f;
		}
		
		/*for(int i = 0; i < v.length; i++) {
			if(Double.isNaN(v[i])) {
				System.err.flush();
			}
		}*/
	}
}

