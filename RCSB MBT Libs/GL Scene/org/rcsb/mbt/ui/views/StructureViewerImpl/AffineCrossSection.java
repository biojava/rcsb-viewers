//  $Id: AffineCrossSection.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: AffineCrossSection.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.2  2006/10/04 17:21:06  jbeaver
//  Lots of changes from surfaces to improved picking
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
//  Revision 1.3  2004/04/09 00:04:27  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.2  2004/01/29 18:05:15  agramada
//  Removed General Atomics from copyright
//
//  Revision 1.1  2003/06/24 22:26:57  agramada
//  Reorganized geometry package. Old classes removed, new classes added.
//
//  Revision 1.1  2003/01/10 19:43:48  agramada
//  First check in of the 3d graphics package.
//
//  Revision 1.0  2002/06/10 23:38:39  agramada
//


package org.rcsb.mbt.ui.views.StructureViewerImpl;

import org.rcsb.mbt.glscene.jogl.Color3f;
import org.rcsb.mbt.model.geometry.Point3d;
import org.rcsb.mbt.model.geometry.Vector3f;
import org.rcsb.mbt.ui.views.StructureViewerImpl.FrenetTrihedron;
import org.rcsb.mbt.ui.views.StructureViewerImpl.IntrinsicCrossSection;


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
	vx.scale( v.coordinates[0] );
	vy.set( trihedron.getNormal() );
	vy.scale( v.coordinates[1] );
	vz.set( trihedron.getBinormal() );
	vz.scale( v.coordinates[2] );

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

