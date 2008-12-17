//  $Id: Hermite.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Hermite.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.5  2005/06/29 17:02:10  agramada
//  Added another set method with new argument types.
//
//  Revision 1.4  2004/04/09 00:04:29  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.3  2004/01/29 18:05:27  agramada
//  Removed General Atomics from copyright
//
//  Revision 1.2  2003/06/25 21:41:29  agramada
//  Corrected comments to show in the documentation.
//
//  Revision 1.1  2003/01/10 00:28:58  agramada
//  Initial checkin of geometry generation package.
//
//  Revision 1.0  2002/06/10 23:38:39  agramada
//

package org.rcsb.vf.glscene.SecondaryStructureBuilder;

import org.rcsb.vf.glscene.SecondaryStructureBuilder.vec.Vec3f;

import javax.vecmath.Vector3f;


/**
 * The Hermite class contains capabilities for Hermite interpolation of points assumed to lie on 
 * curve.
 * <P>
 * @author      Apostol Gramada
 * @author      John L. Moreland
 * @author      John Tate
 */
public class Hermite {
    // Instance variables

    private Vec3f[] coords = null;
    private float knotWeight = 1.0f;
    private Vec3f p1 = null;
    private Vec3f p2 = null;
    private Vec3f v1 = null;
    private Vec3f v2 = null;
    private Vector3f n1 = null;
    private Vector3f n2 = null;
    private Vector3f vv1 = null;
    private Vector3f vv2 = null;

    // Constructors
	
    public Hermite() {
	}

    public Hermite( final Vec3f[] coords, final float knotWeight ) {
	    this.coords = coords;
	    this.knotWeight = knotWeight;
	}

    // Methods
    //
    public void setCoordinates( final Vec3f[] coords ) {
	    this.coords = coords;
	}
    
    public void setEndNormals( final Vector3f n1, final Vector3f n2 ) {
	this.n1 = new Vector3f( n1 ); // Assumed to be 
	this.n2 = new Vector3f( n2 ); // unit vectors already
    }
    
    public boolean fixTanCoeff() {
	if( (this.v1 != null) && (this.v2 != null) && (this.n1 != null) && (this.n2 != null) ) {
	    this.vv1 = new Vector3f( this.v1.value[0], this.v1.value[1], this.v1.value[2] );
	    this.vv2 = new Vector3f( this.v2.value[0], this.v2.value[1], this.v2.value[2] );
	    this.vv1.normalize();
	    this.vv2.normalize();
	    final Vector3f b1 = new Vector3f();
	    final Vector3f b2 = new Vector3f();
	    b1.cross( this.vv1, this.n1 );
	    b2.cross( this.vv2, this.n2 );
	    final Vector3f d = new Vector3f();
	    d.set( this.p2.value[0]-this.p1.value[0], this.p2.value[1]-this.p1.value[1], this.p2.value[2]-this.p1.value[2] );
	    float r1, r2;

	    System.out.println( );
	    System.out.println( " The distance is " );
	    System.out.println( " " + d.length() );
	    System.out.println( "First origin (" + this.p1.value[0] + " " + this.p1.value[1] + " " + this.p1.value[2] + " )" );
	    System.out.println( "Second origin (" + this.p2.value[0] + " " + this.p2.value[1] + " " + this.p2.value[2] + " )" );
	    System.out.println( "Distance (" + d.x + " " + d.y + " " + d.z + " )" );
	    System.out.println( "First tangent (" + this.vv1.x + " " + this.vv1.y + " " + this.vv1.z + " )" );
	    System.out.println( "Second tangent (" + this.vv2.x + " " + this.vv2.y + " " + this.vv2.z + " )" );
	    System.out.println( "First normal (" + this.n1.x + " " + this.n1.y + " " + this.n1.z + " )" );
	    System.out.println( "Second normal (" + this.n2.x + " " + this.n2.y + " " + this.n2.z + " )" );
	    System.out.println( "First binormal (" + b1.x + " " + b1.y + " " + b1.z + " )" );
	    System.out.println( "Second binormal (" + b2.x + " " + b2.y + " " + b2.z + " )" );
	    System.out.println( );
	    System.out.println( "Dot product d.b1 " + d.dot( b1 ) );
	    System.out.println( "Dot product d.b2 " + d.dot( b2 ) );
	    System.out.println( "Dot product t1.b2 " + this.vv1.dot( b2 ) );
	    System.out.println( "Dot product t2.b1 " + this.vv2.dot( b1 ) );
	    

	    r1 = 3.0f*d.dot( b2 )/this.vv1.dot( b2 );
	    r2 = 3.0f*d.dot( b1 )/this.vv2.dot( b1 );
//	    r1 = 0.43f;
//	    r2 = 0.82f;
	    System.out.println( );
	    System.out.println( " The scale factors are  " );
	    System.out.println( "First " + r1 );
	    System.out.println( "Second " + r2 );
	    System.out.println( );


	    this.vv1.scale( r1 );
	    this.vv2.scale( r2 );
	    this.v1.value[0] = this.vv1.x;
	    this.v1.value[1] = this.vv1.y;
	    this.v1.value[2] = this.vv1.z;
	    this.v2.value[0] = this.vv2.x;
	    this.v2.value[1] = this.vv2.y;
	    this.v2.value[2] = this.vv2.z;	    

	
	    return true;
	}
	else{ 
	    return false;
	}
    }

    public Vec3f[] getCoordinates() {
	    return this.coords;
	}

    public void setKnotWeight( final float knotWeight ) {
	    this.knotWeight = knotWeight;
	}

    public float getKnotWeight() {
	    return this.knotWeight;
	}

    public void sample( final float t, final Vec3f coord ) {
	float t2, t3, tp1, tp2, tv1, tv2;
	t2 = t*t;
	t3 = t2*t;
	tp1 = 2.0f*t3 - 3.0f*t2 + 1.0f;
	tp2 = -2.0f*t3 + 3.0f*t2;
	tv1 = t3 - 2.0f*t2 + t;
	tv2 = t3 - t2;
	coord.value[0] =  this.p1.value[0]*tp1 + this.p2.value[0]*tp2 + this.v1.value[0]*tv1 + this.v2.value[0]*tv2;
	coord.value[1] =  this.p1.value[1]*tp1 + this.p2.value[1]*tp2 + this.v1.value[1]*tv1 + this.v2.value[1]*tv2;
	coord.value[2] =  this.p1.value[2]*tp1 + this.p2.value[2]*tp2 + this.v1.value[2]*tv1 + this.v2.value[2]*tv2;
    }


    public Vector3f getTangent( final float t ) {
	final Vector3f toReturn = new Vector3f();
	float t2, tp1, tp2, tv1, tv2;
	t2 = t*t;

	tp1 = 6.0f*(t2 - t);
	tp2 = -tp1;
	tv1 = 3.0f*t2 - 4.0f*t + 1.0f;
	tv2 = 3.0f*t2 - 2.0f*t;
	toReturn.x =  this.p1.value[0]*tp1 + this.p2.value[0]*tp2 + this.v1.value[0]*tv1 + this.v2.value[0]*tv2;
	toReturn.y =  this.p1.value[1]*tp1 + this.p2.value[1]*tp2 + this.v1.value[1]*tv1 + this.v2.value[1]*tv2;
	toReturn.z =  this.p1.value[2]*tp1 + this.p2.value[2]*tp2 + this.v1.value[2]*tv1 + this.v2.value[2]*tv2;

	//    toReturn.normalize();
	return toReturn;
    }
    
    public Vector3f getNormal( final float t ) {
	Vector3f normal = new Vector3f();
	Vector3f tangent = new Vector3f();

	tangent = this.getTangent( t );
	tangent.normalize();
	normal = this.getSecondDerivative( t );
	tangent.scale( tangent.dot( normal ) );
	normal.sub( tangent );
	
	return normal;
    }

    private Vector3f getSecondDerivative( final float t ) {
	final Vector3f derivative2 = new Vector3f();
	float tp, tv1, tv2;

	tp = -12*t+6;
	tv1  = 6*t-4;
	tv2 = 6*t-2;
	derivative2.x = tp*(this.p2.value[0] - this.p1.value[0]) + tv1*this.v1.value[0] + tv2*this.v2.value[0];
	derivative2.y = tp*(this.p2.value[1] - this.p1.value[1]) + tv1*this.v1.value[1] + tv2*this.v2.value[1];
	derivative2.z = tp*(this.p2.value[2] - this.p1.value[2]) + tv1*this.v1.value[2] + tv2*this.v2.value[2];

	return derivative2;
    }

    public void set( final Vec3f start, final Vec3f finish, final Vec3f tangent1, final Vec3f tangent2 ) {
    
	this.p1 = new Vec3f( start.value[0], start.value[1], start.value[2] );
	this.p2 = new Vec3f( finish.value[0], finish.value[1], finish.value[2] );
	this.v1 = new Vec3f( tangent1.value[0], tangent1.value[1], tangent1.value[2] );
	this.v2 = new Vec3f( tangent2.value[0], tangent2.value[1], tangent2.value[2] );
    
	this.v1.value[0] *= this.knotWeight;
	this.v1.value[1] *= this.knotWeight;
	this.v1.value[2] *= this.knotWeight;

	this.v2.value[0] *= this.knotWeight;
	this.v2.value[1] *= this.knotWeight;
	this.v2.value[2] *= this.knotWeight;
    }

    public void set( final Vec3f start, final Vec3f finish, final Vector3f tangent1, final Vector3f tangent2 ) {
    
	this.p1 = new Vec3f( start.value[0], start.value[1], start.value[2] );
	this.p2 = new Vec3f( finish.value[0], finish.value[1], finish.value[2] );
	this.v1 = new Vec3f( tangent1.x, tangent1.y, tangent1.z );
	this.v2 = new Vec3f( tangent2.x, tangent2.y, tangent2.z );
    

	this.v1.value[0] *= this.knotWeight;
	this.v1.value[1] *= this.knotWeight;
	this.v1.value[2] *= this.knotWeight;

	this.v2.value[0] *= this.knotWeight;
	this.v2.value[1] *= this.knotWeight;
	this.v2.value[2] *= this.knotWeight;
    }

    public void set( final Vector3f start, final Vector3f finish, final Vector3f tangent1, final Vector3f tangent2 ) {
    
	this.p1 = new Vec3f( start.x, start.y, start.z );
	this.p2 = new Vec3f( finish.x, finish.y, finish.z );
	this.v1 = new Vec3f( tangent1.x, tangent1.y, tangent1.z );
	this.v2 = new Vec3f( tangent2.x, tangent2.y, tangent2.z );
    

	this.v1.value[0] *= this.knotWeight;
	this.v1.value[1] *= this.knotWeight;
	this.v1.value[2] *= this.knotWeight;

	this.v2.value[0] *= this.knotWeight;
	this.v2.value[1] *= this.knotWeight;
	this.v2.value[2] *= this.knotWeight;
    }
}

