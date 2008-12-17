package org.rcsb.vf.glscene.jogl;

//  $Id: VirtualSphere2.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  History:
//  $Log: VirtualSphere2.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.2  2006/03/25 02:11:22  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.0  2004/09/30 23:50:56  moreland
//  First version.
//


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

