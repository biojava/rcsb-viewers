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

