//  $Id: FrenetTrihedron.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: FrenetTrihedron.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.4  2004/04/09 00:04:28  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.3  2004/01/29 18:05:25  agramada
//  Removed General Atomics from copyright
//
//  Revision 1.2  2003/12/09 00:42:34  agramada
//  Removed reference to the debug class.
//
//  Revision 1.1  2003/06/24 22:19:45  agramada
//  Reorganized the geometry package. Old classes removed, new classes added.
//
//  Revision 1.1  2003/01/10 19:43:48  agramada
//  First check in of the 3d graphics package.
//
//  Revision 1.0  2002/06/10 23:38:39  agramada
//


package  org.rcsb.vf.glscene.SecondaryStructureBuilder;

import javax.vecmath.Vector3f;


/**
 * FrenetTrihedron encapsulates a LOCAL reference frame on a curve defined by the LOCAL tangent, normal and 
 * binormal vectors. Unlike a real Frenet trihedron though, this IS NOT a rectangular trihedron in general 
 * inspite the name of the class and the name of the three (unit) vectors that may suggest so. 
 * Meant to play the role of a Frenet trihedron but with the additional
 * flexibility of NO IMPOSED orthogonality. Certainly, the Frenet equations DO NOT hold either, in general.
 * <P>
 * @author      Apostol Gramada
 * @version     $Revision: 1.1 $
 * @since       JDK1.2.2
 */
public class FrenetTrihedron {
    private Vector3f origin = null;
    private Vector3f tangent = null;
    private Vector3f normal = null;
    private Vector3f binormal= null;
    static Vector3f previousNormal = null;

    public FrenetTrihedron() {
	/*
	  this.origin = new Vector3d();
	  this.tangent = new Vector3d();
	  this.normal = new Vector3d();
	  binormal = new Vector3d();
	*/
    }

    public FrenetTrihedron( final Vector3f origin, final Vector3f tangent, final Vector3f normal ) {
	// It is assumed that the tangent and normal vectors are already normalized
	this.origin = new Vector3f( origin );
	this.tangent = new Vector3f( tangent );
	this.normal = new Vector3f( normal );
	this.binormal = new Vector3f();
	this.binormal.cross( this.tangent, this.normal ); 
	this.binormal.normalize();
    }

    public FrenetTrihedron( final Vector3f origin, final Vector3f tangent, final Vector3f binormal, final int flag ) {
	// It is assumed that the tangent and binormal vectors are already normalized
	this.origin = new Vector3f( origin );
	this.tangent = new Vector3f( tangent );
	this.binormal = new Vector3f( binormal );
	this.normal = new Vector3f( );
	this.normal.cross( binormal, tangent ); 
	// We include the case of a nonorthogonal reference system in general, and in that case
	// the cross product above may not render an unit vector. Therefore, we need to normalize.
	this.normal.normalize();
    }

    public FrenetTrihedron( final Vector3f origin, final Vector3f tangent, final Vector3f normal, final Vector3f binormal ) {
	// It is assumed that the tangent, normal and binormal vectors are already normalized
	this.origin = new Vector3f( origin );
	this.tangent = new Vector3f( tangent );
	this.normal = new Vector3f( normal );
	this.binormal = binormal; 
    }

    public FrenetTrihedron( final FrenetTrihedron trihedron ) {
	this.origin = new Vector3f( trihedron.getOrigin() );
	this.tangent = new Vector3f( trihedron.getTangent() );
	this.normal = new Vector3f( trihedron.getNormal() );
	this.binormal = new Vector3f( trihedron.getBinormal() );
    }

    public void setOrigin( final Vector3f v ) {
	this.origin = new Vector3f( v );
	if( ( this.tangent != null ) && ( this.normal != null ) ) {
	    this.binormal = new Vector3f();
	    this.binormal.cross( this.tangent, this.normal );
	    // We include the case of a nonorthogonal reference system in general, and in that case
	    // the cross product above may not render an unit vector. Therefore, we need to normalize.
	    this.binormal.normalize(); 
	}
    }

    public void setOriginOnly( final Vector3f v ) {
	this.origin = new Vector3f( v );
    }

    public void setTangent( final Vector3f v ) {
	this.tangent = new Vector3f( v );
	if( this.normal != null ) {
	    this.binormal = new Vector3f();
	    this.binormal.cross( this.tangent, this.normal );
	    // We include the case of a nonorthogonal reference system in general, and in that case
	    // the cross product above may not render an unit vector. Therefore, we need to normalize.
	    this.binormal.normalize();
	}
    }

    public void setNormal( final Vector3f v ) {
	this.normal = new Vector3f( v );
	if( this.tangent != null ) {
	    this.binormal = new Vector3f();
	    this.binormal.cross( this.tangent, this.normal );
	    this.binormal.normalize();
	}
    }

    
	public String toString() {
	final String toReturn = "Origin: " + this.origin.toString() + "\n Tangent: " + this.tangent.toString() + 
	    "\n Normal: " + this.normal.toString() + "\n Binormal: " + this.binormal.toString();
	return toReturn;
    }

    public float distance( final FrenetTrihedron t ) {
	Vector3f tmp = null;
	tmp = new Vector3f ( this.getOrigin() );
	tmp.sub( t.getOrigin() );
	return tmp.length();
    }

  public void setBinormal( final Vector3f v ) {
    this.binormal = new Vector3f( v );
  }

  public void interpolate( final float t, final FrenetTrihedron trihedron1, final FrenetTrihedron trihedron2 ) {
    Vector3f v1 = null;
    Vector3f v2 = null;
    Vector3f v3 = null;
      
    
    // Just to not let the origin null
    v1 = new Vector3f();
    v1.interpolate( trihedron1.getOrigin(), trihedron2.getOrigin(), t );
    this.origin = v1;

    // By independently interpolating the tangents and normals, they cease to be orthogonal in general,
    // even though they may have been initially. That's OK for some of our needs.
    v1 = new Vector3f();
    v1.interpolate( trihedron1.getTangent(), trihedron2.getTangent(), t );
    v1.normalize();
    this.tangent = v1;
        
    v2 = new Vector3f();
    v2.interpolate( trihedron1.getNormal(), trihedron2.getNormal(), t );
    v2.normalize();
    this.normal = v2;
    
    v3 = new Vector3f();
    v3.cross( this.tangent, this.normal );
    v3.normalize();
    this.binormal = new Vector3f();
    this.binormal.set( v3 );

    //    debug.println(4, "normal . tangent" + "  " + t + "  " + normal.dot( tangent ) );

  }

  public void interpolateByBinormal( final float t, final FrenetTrihedron trihedron1, final FrenetTrihedron trihedron2 ) {
    Vector3f v1 = null;
    Vector3f v2 = null;
    Vector3f v3 = null;
      
    
    // Just to not let the origin null in case it is not going to be explicitely set from outside.
    v1 = new Vector3f();
    v1.interpolate( trihedron1.getOrigin(), trihedron2.getOrigin(), t );
    this.origin = v1;

    /*
    // By independently interpolating the tangents and normals, they cease to be orthogonal in general,
    // even though they may have been initially. That's OK for some of our needs.
    v1 = new Vector3d();
    v1.interpolate( trihedron1.getTangent(), trihedron2.getTangent(), t );
    v1.normalize();
    this.tangent = v1;
    */
        
    v2 = new Vector3f();
    v2.interpolate( trihedron1.getBinormal(), trihedron2.getBinormal(), t );
    v2.normalize();
    this.binormal = v2;
    
    v3 = new Vector3f();
    v3.cross( this.binormal, this.tangent );
    v3.normalize();
    this.normal = v3;

    //    debug.println(4, "normal . tangent" + "  " + t + "  " + normal.dot( tangent ) );

  }

    //***************************************************************************************************
    // Diferent other interpolation schemes may eventually go here
    //
    //***************************************************************************************************

  public Vector3f getOrigin() {
    return new Vector3f( this.origin );
  }

  public Vector3f getTangent() {
    return new Vector3f( this.tangent );
  }

  public Vector3f getNormal() {
    return new Vector3f( this.normal );
  }

  public Vector3f getBinormal() {
    return new Vector3f( this.binormal );
  }

  public static Vector3f getPreviousNormal() {
    return new Vector3f( FrenetTrihedron.previousNormal );
  }

  public static void setPreviousNormal( final Vector3f normal ) {
    FrenetTrihedron.previousNormal = new Vector3f( normal ); 
  }
}

