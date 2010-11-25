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
package org.rcsb.vf.glscene.SecondaryStructureBuilder;


import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.rcsb.vf.glscene.SecondaryStructureBuilder.vec.Vec3f;
import org.rcsb.vf.glscene.jogl.Color3f;


/**
 * IntrinsicCrossSection represents, geometrically, a closed poligon. It 
 * is used in conjunction with extrusion to generate secondary structure representations.
 * An IntrinsicCrossSection is an intrinsic property, i.e. it has a meaninig only when defined with respect to a LOCAL
 * refrence frame (FrenetTrihedron). For instance the vertices defining the shape have to be interpreted
 * relative to a FrenetTrihedron.
 *
 * @author      Apostol Gramada
 * @version     $Revision: 1.1 $
 * @since       JDK1.2.2
 */
public class IntrinsicCrossSection {
    private int vertexCount = 0;      // Number of vertices
    private Vec3f[] vertex = null;    // The set of vertices. Last identical with the first.
    private Vector3f[] vectorVertex = null;
    private Vec3f[] normal = null;    // Normals assigned to vertices ( unit vectors )
    private Vector3f[] vectorNormal = null;
    private Color3f[] color = null;   // We would probably like to pass colors to Extrusion in this way
    private Color3f sideColor = null;

    // Empty constructor
    public IntrinsicCrossSection( ) { 
    }
  
    // Construct a CrossSection with a specified number of vertices only.
    public IntrinsicCrossSection( final int vertexCount ) { 
	this.vertexCount = vertexCount;
	this.vertex = new Vec3f[vertexCount];
	this.normal = new Vec3f[vertexCount];
	this.vectorVertex = new Vector3f[vertexCount];
	this.vectorNormal = new Vector3f[vertexCount];
    }

    // Construct a CrossSection with a specified number of vertices, specified set of vertices.
    // The length of vertices should be equal to vertexCount.
    public IntrinsicCrossSection( final int vertexCount, final Vec3f[] vertices ) { 
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
    public IntrinsicCrossSection( final int vertexCount, final Vec3f[] vertices, final Vec3f[] normals ) { 
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
    public IntrinsicCrossSection( final int vertexCount, final Vec3f[] vertices, final Vec3f[] normals, final Color3f[] colors ) { 
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

    // Construct a CrossSection from a given IntrinsicCrossSection.
    public IntrinsicCrossSection( final IntrinsicCrossSection base ) { 
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
    
    
    // Scale the figure defined by the vertices. 
    //
    public void scale( final Vector3f scale ) {	// was Tuple3d
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

    // Rotate the set of vertices (and the associated normals) making the poligon 
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
    // Takes a Vector3d as argument
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
      for( int i = 0; i < this.vertexCount-1; i++ ) {
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
      this.normal[this.vertexCount-1] = this.normal[0];
      this.vectorNormal[this.vertexCount-1] = this.vectorNormal[0];

    }

  // Sets the normals associated with any given vertex equal to
  // the average value of itself and the previous one. Essentially, this
  // allows in effect an easy switch from face normals to vertex normals.
  // Assumes normals already set
  public void averageNormals() {
    final Vector3f workSpace = new Vector3f();
    final Vector3f keepOld = new Vector3f();

    keepOld.set( this.vectorNormal[0] );
    this.vectorNormal[0].set( this.vectorNormal[0] ); 
    this.vectorNormal[0].add( this.vectorNormal[this.vertexCount-1] );
    this.vectorNormal[0].normalize();
    this.normal[0].value[0] = this.vectorNormal[0].x;
    this.normal[0].value[1] = this.vectorNormal[0].y;
    this.normal[0].value[2] = this.vectorNormal[0].z;

    for( int i = 1; i < this.vertexCount-1; i++ ) {
      workSpace.set( this.vectorNormal[i] );
      workSpace.add( keepOld );
      workSpace.normalize();
      keepOld.set( this.vectorNormal[i] ); // Save the old value
      this.vectorNormal[i].set( workSpace );
      this.normal[i].value[0] = this.vectorNormal[i].x;
      this.normal[i].value[1] = this.vectorNormal[i].y;
      this.normal[i].value[2] = this.vectorNormal[i].z;
    }
    
    this.vectorNormal[this.vertexCount-1] = this.vectorNormal[0];
    this.normal[this.vertexCount-1] = this.normal[0];

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

