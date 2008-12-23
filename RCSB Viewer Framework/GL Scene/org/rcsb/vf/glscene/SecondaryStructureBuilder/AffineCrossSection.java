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
import javax.vecmath.Vector3f;

import org.rcsb.vf.glscene.SecondaryStructureBuilder.FrenetTrihedron;
import org.rcsb.vf.glscene.SecondaryStructureBuilder.IntrinsicCrossSection;
import org.rcsb.vf.glscene.jogl.Color3f;


/**
 * AffineCrossSection is the representation of a {@link CrossSection} objects tied to a given point in space. 
 * <P>
 * @author      Apostol Gramada
 */
public class AffineCrossSection {

    private IntrinsicCrossSection intrinsicCS = null;
    private int vertexCount = 0;      // Number of vertices
    private Point3d[] vertices;
    private Vector3f[] normals = null;
    private Color3f[] color = null;   
    private Color3f sideColor = null;
    private FrenetTrihedron frenet = null;

    /**
     * Build an AffineCrossSection from a given intrinsic {@link CrossSection} and a given {@link FrenetTrihedron}. 
     */
    public AffineCrossSection( final IntrinsicCrossSection crossS, final FrenetTrihedron ft ) { 
	this.intrinsicCS = crossS;
	this.vertexCount = crossS.getVertexCount();
	this.frenet = ft;

	this.vertices = this.generatePoints( this.intrinsicCS, ft );
	this.normals = this.generateNormals( this.intrinsicCS, ft );
    }
    
    public Point3d[] generatePoints( final IntrinsicCrossSection cs, final FrenetTrihedron trihedron ) {
	final Vector3f workspace = new Vector3f();
	final Vector3f x0 = new Vector3f( trihedron.getOrigin() );
	final Point3d[] toReturn = new Point3d[this.vertexCount];
	final Vector3f[] vectorVertex = cs.getVectorVertices();
	for( int i = 0; i < this.vertexCount; i++ ) {
	    toReturn[i] = new Point3d();
	    workspace.add( this.generateGlobalVector( vectorVertex[i], trihedron ), x0);
	    toReturn[i].set( workspace );
	}
	return toReturn;
    }

    public Vector3f[] generateNormals( final IntrinsicCrossSection cs, final FrenetTrihedron trihedron ) {
	final Vector3f[] toReturn = new Vector3f[this.vertexCount];
	for( int i = 0; i < this.vertexCount; i++ ) {
	    toReturn[i] = new Vector3f();
	    final Vector3f[] vectorNormal = cs.getVectorNormals();
	    toReturn[i].set( this.generateGlobalVector( vectorNormal[i], trihedron ) );
	    toReturn[i].normalize();
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

    // Scale the figure defined by the vertices. 
    //
    public void scale( final Vector3f scale ) {	// was Tuple3d
	final IntrinsicCrossSection cs = new IntrinsicCrossSection( this.intrinsicCS );
	cs.scale( scale );

	this.vertices = this.generatePoints( cs, this.frenet );
	this.normals = this.generateNormals( cs, this.frenet );
    }

    // TO DO: Set methods
    //
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

    public Point3d[] getPointVertices() {
	return this.vertices;
    }

    public Vector3f[] getNormals() {
	return this.normals;
    }

    public Color3f[] getColors() {
        return this.color;
    }

    public FrenetTrihedron getFrenetTrihedron() {
	return this.frenet;
    }

}

