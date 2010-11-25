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
package org.rcsb.vf.glscene.surfaces;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;


import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.Surface;
import org.rcsb.mbt.model.attributes.Style;
import org.rcsb.vf.glscene.jogl.Constants;
import org.rcsb.vf.glscene.jogl.DisplayListGeometry;
import org.rcsb.vf.glscene.jogl.DisplayLists;

import com.sun.opengl.util.GLUT;



public class SurfaceGeometry
	extends DisplayListGeometry
{
	// Shared display lists: key="form:quality" value=Integer(displayList)
//	public static Hashtable sharedDisplayLists = new Hashtable( );
	Point3f[] vertices = null;
    Color4f[] colors = null;
    int[][] faces = null;

	/**
	 *  Construct a new SurfaceGeometry object.
	 */
	public SurfaceGeometry( )
	{
//		System.out.println("Creating new SurfaceGeometry");
	}
	
	public DisplayLists[] getDisplayLists(final StructureComponent structureComponent,
			final Style style, final GL gl, final GLU glu, final GLUT glut) {
		//
		// Handle quality, form, and shared display lists.
		//
		
		final Surface surface = (Surface)structureComponent;
		vertices = surface.getVertices();
        colors = surface.getColors();
        faces = surface.getFaces();
        
		final DisplayLists[] lists = new DisplayLists[1];
		
		if ( lists[0] == null ) {
			lists[0] = new DisplayLists(surface);
			lists[0].setupLists(1);
			
			lists[0].startDefine(0, gl, glu, glut);

	        // enable color tracking with glColor
	        gl.glEnable(GL.GL_COLOR_MATERIAL);

			boolean transparent = colors[0].w < 0.95f;
			
			 // draw with transparency
	        if (transparent) {
	            gl.glEnable(GL.GL_BLEND);
	            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	            gl.glDepthMask(false);
	            // draw transparent surface twice with opposite winding, see OpenGL Superbible p. 449
	            // this seems to create some artifacts
	//            gl.glFrontFace(GL.GL_CW); 
	//           drawFaces(gl, GL.GL_TRIANGLES); 
	        }
	        
	        gl.glFrontFace(GL.GL_CCW);
	        drawFaces(gl, GL.GL_TRIANGLES);

	        if (transparent) {
	            gl.glDepthMask(true);
		        gl.glDisable(GL.GL_BLEND);
	        }

			lists[0].endDefine(gl, glu, glut);

			
			// TODO -pr is this the reason why surface doesn't change color -> yes, cached here
//			SurfaceGeometry.sharedDisplayLists.put( dlKey, lists[0] );
		}
//		lists[0] = lists[0].copy();
		lists[0].structureComponent = surface;

		lists[0].mutableColorType = GL.GL_AMBIENT_AND_DIFFUSE;	
		lists[0].specularColor = Constants.chainSpecularColor.color;
		lists[0].emissiveColor = Constants.chainEmissiveColor.color;
		lists[0].shininess = Constants.chainHighShininess;
		
		return lists;
	}
	
    private void drawFaces(GL gl, int shadeType) {
        Vector3f v0 = new Vector3f();
        Vector3f v1 = new Vector3f();
        Vector3f n = new Vector3f();
        
        gl.glBegin(shadeType);
        for (int i = 0; i < faces.length; i++) {
            Point3f p0 = vertices[faces[i][0]];
            Point3f p1 = vertices[faces[i][1]];
            Point3f p2 = vertices[faces[i][2]];
            Color4f c0 = colors[faces[i][0]];
            Color4f c1 = colors[faces[i][1]];
            Color4f c2 = colors[faces[i][2]];

            // calculate normal
            v0.sub(p0, p1);
            v1.sub(p1, p2);
            n.cross(v0, v1);
            n.normalize();

            // draw triangle
            gl.glNormal3f(n.x, n.y, n.z);
            gl.glColor4f(c0.x, c0.y, c0.z, c0.w);
            gl.glVertex3f(p0.x, p0.y, p0.z);
            gl.glColor4f(c1.x, c1.y, c1.z, c1.w);
            gl.glVertex3f(p1.x, p1.y, p1.z);
            gl.glColor4f(c2.x, c2.y, c2.z, c2.w);
            gl.glVertex3f(p2.x, p2.y, p2.z);
        }
        gl.glEnd();
    }

	
}
