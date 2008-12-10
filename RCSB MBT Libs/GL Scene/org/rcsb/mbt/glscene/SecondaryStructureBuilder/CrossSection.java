//  $Id: CrossSection.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: CrossSection.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.2  2006/10/04 17:21:06  jbeaver
//  Lots of changes from surfaces to improved picking
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
//  Revision 1.3  2004/01/29 18:05:21  agramada
//  Removed General Atomics from copyright
//
//  Revision 1.2  2003/12/09 00:39:16  agramada
//  Removed reference to the debug class.
//
//  Revision 1.1  2003/06/25 00:20:26  agramada
//  CrossSection moved in this package. It will be removed in the future.
//
//  Revision 1.1  2003/01/10 19:43:48  agramada
//  First check in of the 3d graphics package.
//
//  Revision 1.0  2002/06/10 23:38:39  agramada
//

/**
 * CrossSection represents, geometrically, a closed poligon. It 
 * is used in conjunction with extrusion to generate secondary structure representations.
 * A CrossSection is an intrinsic property, i.e. it has a meaninig only when defined with respect to a LOCAL
 * refrence frame (FrenetTrihedron). For instance the vertices defining the shape have to be interpreted
 * relative to a FrenetTrihedron.
 *
 * @author      Apostol Gramada
 * @version     $Revision: 1.1 $
 * @since       JDK1.2.2
 */

package org.rcsb.mbt.glscene.SecondaryStructureBuilder;

import org.rcsb.mbt.glscene.SecondaryStructureBuilder.vec.Vec3f;

import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import org.rcsb.mbt.glscene.jogl.Color3f;



public class CrossSection {
    private int vertexCount = 0;      // Number of vertices
    private Vec3f[] vertex = null;    // The set of vertices. Last identical with the first.
    private Vector3f[] vectorVertex = null;
    private Vec3f[] normal = null;    // Normals assigned to vertices ( unit vectors )
    private Vector3f[] vectorNormal = null;
    private Color3f[] color = null;   // We would probably like to pass colors to Extrusion in this way
    private Color3f sideColor = null;

    // Empty constructor
    public CrossSection( ) { 
    }
  
    // Construct a CrossSection with a specified number of vertices only.
    public CrossSection( final int vertexCount ) { 
	this.vertexCount = vertexCount;
	this.vertex = new Vec3f[vertexCount];
	this.normal = new Vec3f[vertexCount];
	this.vectorVertex = new Vector3f[vertexCount];
	this.vectorNormal = new Vector3f[vertexCount];
    }

    // Construct a CrossSection with a specified number of vertices, specified set of vertices.
    // The length of vertices should be equal to vertexCount.
    public CrossSection( final int vertexCount, final Vec3f[] vertices ) { 
	this.vertexCount = vertexCount;
	this.vertex = new Vec3f[vertexCount];
	this.normal = new Vec3f[vertexCount];
	this.vectorVertex = new Vector3f[vertexCount];
	this.vectorNormal = new Vector3f[vertexCount];
	for( int i=0; i < vertexCount; i++ ) {
	    this.vertex[i] = new Vec3f();
	    this.vertex[i].value[0] = vertices[i].value[0];
	    this.vertex[i].value[1] = vertices[i].value[1];
	    this.vertex[i].value[2] = vertices[i].value[2];
	    this.vectorVertex[i] = new Vector3f( this.vertex[i].value[0], this.vertex[i].value[1], this.vertex[i].value[2] );
	}
    }

    // Construct a CrossSection with a specified number of vertices, specified set of vertices
    // and specified set of normals
    public CrossSection( final int vertexCount, final Vec3f[] vertices, final Vec3f[] normals ) { 
	this.vertexCount = vertexCount;
	this.vertex = new Vec3f[vertexCount];
	this.normal = new Vec3f[vertexCount];
	this.vectorVertex = new Vector3f[vertexCount];
	this.vectorNormal = new Vector3f[vertexCount];
	for( int i=0; i < vertexCount; i++ ) {
	    this.vertex[i] = new Vec3f();
	    this.vertex[i].value[0] = vertices[i].value[0];
	    this.vertex[i].value[1] = vertices[i].value[1];
	    this.vertex[i].value[2] = vertices[i].value[2];
	    this.vectorVertex[i] = new Vector3f( this.vertex[i].value[0], this.vertex[i].value[1], this.vertex[i].value[2] );

	    this.normal[i] = new Vec3f();
	    this.normal[i].value[0] = normals[i].value[0];
	    this.normal[i].value[1] = normals[i].value[1];
	    this.normal[i].value[2] = normals[i].value[2];
	    this.vectorNormal[i] = new Vector3f( this.normal[i].value[0], this.normal[i].value[1], this.normal[i].value[2] );
	}
    }

    // Construct a CrossSection with a specified number of vertices, specified set of vertices,
    // normals, and per vertex colors
    public CrossSection( final int vertexCount, final Vec3f[] vertices, final Vec3f[] normals, final Color3f[] colors ) { 
	this.vertexCount = vertexCount;
	this.vertex = new Vec3f[vertexCount];
	this.normal = new Vec3f[vertexCount];
	this.color = new Color3f[vertexCount];
	this.vectorVertex = new Vector3f[vertexCount];
	this.vectorNormal = new Vector3f[vertexCount];
	for( int i=0; i < vertexCount; i++ ) {
	    this.vertex[i] = new Vec3f();
	    this.vertex[i].value[0] = vertices[i].value[0];
	    this.vertex[i].value[1] = vertices[i].value[1];
	    this.vertex[i].value[2] = vertices[i].value[2];
	    this.vectorVertex[i] = new Vector3f( this.vertex[i].value[0], this.vertex[i].value[1], this.vertex[i].value[2] );

	    this.normal[i] = new Vec3f();
	    this.normal[i].value[0] = normals[i].value[0];
	    this.normal[i].value[1] = normals[i].value[1];
	    this.normal[i].value[2] = normals[i].value[2];
	    this.vectorNormal[i] = new Vector3f( this.normal[i].value[0], this.normal[i].value[1], this.normal[i].value[2] );

	    this.color[i] = new Color3f( colors[i].color[0], colors[i].color[1], colors[i].color[2] );
	}
    }

    // Construct a CrossSection from a given CrossSection.
    public CrossSection( final CrossSection base ) { 
	this.vertexCount = base.getVertexCount();
	this.vertex = new Vec3f[this.vertexCount];
	this.normal = new Vec3f[this.vertexCount];
	this.vectorVertex = new Vector3f[this.vertexCount];
	this.vectorNormal = new Vector3f[this.vertexCount];
	if( base.getColors() != null ) {
	  this.color = new Color3f[this.vertexCount];
	  this.color = (Color3f[])base.getColors().clone();
	}
	for( int i=0; i < this.vertexCount; i++ ) {
	    Vec3f v = new Vec3f();
	    v = base.getVertices()[i]; 
	    this.vertex[i] = new Vec3f();
	    this.vertex[i].value[0] = v.value[0];
	    this.vertex[i].value[1] = v.value[1];
	    this.vertex[i].value[2] = v.value[2];
	    this.vectorVertex[i] = new Vector3f( this.vertex[i].value[0], this.vertex[i].value[1], this.vertex[i].value[2] );

	    Vec3f vn = new Vec3f();
	    vn = base.getNormals()[i];
	    this.normal[i] = new Vec3f();
	    this.normal[i].value[0] = vn.value[0];
	    this.normal[i].value[1] = vn.value[1];
	    this.normal[i].value[2] = vn.value[2];
	    this.vectorNormal[i] = new Vector3f( this.normal[i].value[0], this.normal[i].value[1], this.normal[i].value[2] );
	}
	this.sideColor = base.getSideColor();
    }
    
    
    // Rotate the set of vertices (and the associated normals) making the poligon 
    // Scale the figure defined by the vertices. 
    public void scale( final Vector3f scale ) {
	final Vector3f vv = new Vector3f();
	//

	final Vector3f vvertex = new Vector3f();
	for( int i = 0; i < this.vertex.length; i++ ) {
	    vv.set( this.vertex[i].value[0], this.vertex[i].value[1], this.vertex[i].value[2] );

	    vvertex.set( scale.x*vv.x, scale.y*vv.y, scale.z*vv.z );

	    // Now, we have to convert back to Vec3f format.
	    this.vertex[i].value[0] = vvertex.x;
	    this.vertex[i].value[1] = vvertex.y;
	    this.vertex[i].value[2] = vvertex.z; 
	    this.vectorVertex[i].set( vvertex );
	}
    }

    // around an axis passing through point x0 in the direction "direction".
    public void rotate( final float angle, final Vec3f x0, final Vec3f direction ) {
	final Vector3f vx0 = new Vector3f( x0.value[0], x0.value[1], x0.value[2] ); 
	final Vector3f vn = new Vector3f( direction.value[0], direction.value[1], direction.value[2] ); 
	vn.normalize(); // normal might not already be a unit vector.
	final Vector3f vv = new Vector3f();
	//
	// Construct the rotation quaternion:
	float cosHalfT, sinHalfT; // The set of angles (in radians)
	float x, y, z;
	cosHalfT = (float) Math.cos( angle/2 );
	sinHalfT = (float) Math.sin( angle/2 );
	x = sinHalfT*vn.x;
	y = sinHalfT*vn.y;
	z = sinHalfT*vn.z;
	final Quat4f q1 = new Quat4f( );
	q1.set( x, y, z, cosHalfT );

	// Need some working space
	final Quat4f q = new Quat4f();
	final Quat4f p = new Quat4f();

	// We are now ready to rotate the vertices.
	// Since, the rotation is about an axis that doesn't pass through origin in general,
 	// we need to rewrite each vertex in a refrence frame passing through 
	// a point on the axis of rotation.
	final Vector3f vvertex = new Vector3f();
	for( int i = 0; i < this.vertex.length; i++ ) {
	    vv.set( this.vertex[i].value[0], this.vertex[i].value[1], this.vertex[i].value[2] );
	    vv.sub( vx0 );
	    q.set( vv.x, vv.y, vv.z, 0.0f );
	    p.set( q1 );
	    q.mulInverse( q1 );
	    p.mul( q );

	    vvertex.set( p.x, p.y, p.z );
	    vvertex.add( vx0 );
	    this.vectorVertex[i].set( vvertex.x, vvertex.y, vvertex.z );

	    // Now, we have to convert back to Vec3f format.
	    this.vertex[i].value[0] = vvertex.x;
	    this.vertex[i].value[1] = vvertex.y;
	    this.vertex[i].value[2] = vvertex.z; 
	    
	    // And reset normals
	    // setNormalsByVector( normal );
	}
    }

    // Rotate the set of vertices (and the associated normals) making the poligon 
    // around an axis passing through point x0 in the direction "direction".
    public void rotate( final float angle, final Vec3f direction ) {
      this.rotate( angle, new Vec3f( 0.0f, 0.0f, 0.0f ), direction );
    }

    public Point3d[] generatePoints( final FrenetTrihedron trihedron ) {
	final Vector3f workspace = new Vector3f();
	final Vector3f x0 = new Vector3f( trihedron.getOrigin() );
	final Point3d[] toReturn = new Point3d[this.vertexCount];
	for( int i = 0; i < this.vertexCount; i++ ) {
	    toReturn[i] = new Point3d();
	    workspace.add( this.generateGlobalVector( this.vectorVertex[i], trihedron ), x0);
	    toReturn[i].set( workspace );
	}
	return toReturn;
    }

    public Vector3f[] generateNormals( final FrenetTrihedron trihedron ) {
	final Vector3f[] toReturn = new Vector3f[this.vertexCount];
	for( int i = 0; i < this.vertexCount; i++ ) {
	    toReturn[i] = new Vector3f();
	    toReturn[i].set( this.generateGlobalVector( this.vectorNormal[i], trihedron ) );
	    //toReturn[i].normalize();
	}
	return toReturn;
    }

    private Vector3f generateGlobalVector( final Vector3f v , final FrenetTrihedron trihedron ) {
	final Vector3f vx = new Vector3f();
	final Vector3f vy = new Vector3f();
	final Vector3f vz = new Vector3f();

	vx.set( trihedron.getTangent() );
	vx.scale( v.x );
	vy.set( trihedron.getNormal() );
	vy.scale( v.y );
	vz.set( trihedron.getBinormal() );
	vz.scale( v.z );

	final Vector3f toReturn = new Vector3f();
	toReturn.add( vx, vy );
	toReturn.add( vz );

	return toReturn;
    }

    // Sets the normals such that they are perpendicular to the face generated by vector "direction"
    // and the difference of the pairs of consecutive vectors in the vertex array, i.e. 
    // to the faces of a prism generated by the base in the direction "direction".
    //   Takes a Vec3f as argument
    // NOTE: The normals generated this way will not, in general be accurately perpendicular to the "Real" faces of
    // NOTE: an extrusion. More accurate methods to generate normals may be needed in special cases.
    // 
    public void setNormalsByVector( final Vec3f direction ) {
      // The vertex array has to be already set, otherwise we will get null pointers here.
      final Vector3f vd = new Vector3f();
      vd.x = direction.value[0];
      vd.y = direction.value[1];
      vd.z = direction.value[2];

      // Need some work place.
      final Vector3f v1 = new Vector3f();
      final Vector3f v2 = new Vector3f();
      final Vector3f diff = new Vector3f();
      final Vector3f vNormal = new Vector3f();
      int j = 0;

      int modulo = this.vertexCount-1;
      if( this.vertexCount <= 2 ) {
	  modulo = this.vertexCount; // No polygon, open set of points
      }
      for( int i = 0; i < this.vertex.length; i++ ) {
	v1.x = this.vertex[i].value[0];
	v1.y = this.vertex[i].value[1];
	v1.z = this.vertex[i].value[2];

	j = (i+1)%modulo; // Should cycle when i+1 becomes bigger than vertexCount.
	v2.x = this.vertex[j].value[0];
	v2.y = this.vertex[j].value[1];
	v2.z = this.vertex[j].value[2];
	
	diff.sub( v2, v1 );
	vNormal.cross( vd, diff );
	vNormal.normalize();

	this.normal[i] = new Vec3f();
	this.normal[i].value[0] = vNormal.x;
	this.normal[i].value[1] = vNormal.y;
	this.normal[i].value[2] = vNormal.z;
	this.vectorNormal[i] = new Vector3f( vNormal.x, vNormal.y, vNormal.z );
      }
    }

    // Sets the normals such that they are perpendicular to the face generated by vector "direction"
    // and the difference of the pairs of consecutive vectors in the vertex array, i.e. 
    // to the faces of a prism generated by the base in the direction "direction".
    // Takes a Vector3f as argument
    // NOTE: The normals generated this way will not, in general be accurately perpendicular to the "Real" faces of
    // NOTE: an extrusion. More accurate methods to generate normals may be needed in special cases.
    // 
    public void setNormalsByVector( final Vector3f direction ) {

      // The vertex array has to be already set, otherwise we will get null pointers here.
      final Vector3f vd = direction;

      // Need some work place.
      final Vector3f v1 = new Vector3f();
      final Vector3f v2 = new Vector3f();
      final Vector3f diff = new Vector3f();
      final Vector3f vNormal = new Vector3f();
      int j = 0;

      //      debug.println( "VertexLength " + vertex.length );

      int modulo = this.vertexCount-1;
      if( this.vertexCount <= 2 ) {
	  modulo = this.vertexCount; // No polygon, open set of points
      }
      for( int i = 0; i < this.vertexCount; i++ ) {
	v1.x = this.vertex[i].value[0];
	v1.y = this.vertex[i].value[1];
	v1.z = this.vertex[i].value[2];

	j = (i+1)%modulo; // Should cycle when i+1 becomes bigger than vertexCount.
	v2.x = this.vertex[j].value[0];
	v2.y = this.vertex[j].value[1];
	v2.z = this.vertex[j].value[2];
	
	diff.sub( v2, v1 );
	vNormal.cross( vd, diff );
	vNormal.normalize();

	this.normal[i] = new Vec3f();
	this.normal[i].value[0] = vNormal.x;
	this.normal[i].value[1] = vNormal.y;
	this.normal[i].value[2] = vNormal.z;
	this.vectorNormal[i] = new Vector3f( vNormal.x, vNormal.y, vNormal.z );
      }

    }
  // Sets the normals associated with any given vertex equal to
  // the average value of itself and the previous one. Essentially, this
  // allows in effect an easy switch from face normals to vertex normals.
  // Assumes normals already set
  public void averageNormals() {
      /*
    Vector3f workSpace = new Vector3f();
    Vector3f keepOld = new Vector3f();
    int j;
    keepOld.set( vectorNormal[0] );
    for( int i = 0; i < vertexCount; i++ ) {
	//j = (i+1)%(vertexCount-1);
      j = (i+1)%vertexCount;
      workSpace.set( vectorNormal[j] );
      workSpace.add( keepOld );
      workSpace.normalize();
      keepOld.set( vectorNormal[j] ); // Save the old value
      vectorNormal[j].set( workSpace );
      normal[j].value[0] = vectorNormal[j].x;
      normal[j].value[1] = vectorNormal[j].y;
      normal[j].value[2] = vectorNormal[j].z;
    }
      */

      final Vector3f[] workSpace = new Vector3f[this.vertexCount];
      workSpace[0] = new Vector3f();
      workSpace[0].add( this.vectorNormal[this.vertexCount-1], this.vectorNormal[1] );
      workSpace[0].normalize();
      for( int i = 1; i < this.vertexCount-1; i++ ) {
	  workSpace[i] = new Vector3f();
	  workSpace[i].add( this.vectorNormal[i-1], this.vectorNormal[i+1] );
	  workSpace[i].normalize();
      }
      workSpace[this.vertexCount-1] = new Vector3f( workSpace[0] );

      // Now we can copy it back to vectorNormal
      for( int j = 0; j < this.vertexCount; j++ ) {
	  this.vectorNormal[j].set( workSpace[j] );
	  this.normal[j].value[0] = this.vectorNormal[j].x;
	  this.normal[j].value[1] = this.vectorNormal[j].y;
	  this.normal[j].value[2] = this.vectorNormal[j].z;
      }
  }
    
    // TO DO: Set methods
    public void setColors( final Color3f[] colors ) {
	this.color = colors;
    }

  public void setSideColor( final Color3f sideColor ) {
    this.sideColor = sideColor;
  }

    // Get methods
    public int getVertexCount() {
	return this.vertexCount;
    }

  public Color3f getSideColor() {
    return this.sideColor;
  }

    public Vec3f[] getVertices() {
	return this.vertex;
    }

    public Vector3f[] getVectorVertices() {
	return this.vectorVertex;
    }

    public Vec3f[] getNormals() {
	return this.normal;
    }

    public Vector3f[] getVectorNormals() {
	return this.vectorNormal;
    }

    public Color3f[] getColors() {
        return this.color;
    }

}

