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
 * Created on 2011/11/08
 *
 */ 
package org.rcsb.vf.glscene.surfaces;

import java.awt.Color;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.attributes.*;
import org.rcsb.mbt.surface.BindingSiteSurfaceOrienter;
import org.rcsb.mbt.surface.datastructure.LineInfo;
import org.rcsb.mbt.surface.datastructure.TriangulatedSurface;
import org.rcsb.mbt.surface.datastructure.FaceInfo;
import org.rcsb.mbt.surface.datastructure.VertInfo;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.vf.glscene.jogl.Constants;
import org.rcsb.vf.glscene.jogl.DisplayListGeometry;
import org.rcsb.vf.glscene.jogl.DisplayLists;

import com.sun.opengl.util.GLUT;


/**
 * Creates a display list for surfaces.
 * 
 * @author Peter Rose
 */
public class SurfaceGeometry extends DisplayListGeometry {
	private TriangulatedSurface triangulatedSurface = null;
	private Color4f[] colors = null;
	private Vector3f alignment = null;

	public DisplayLists[] getDisplayLists(final StructureComponent structureComponent, final Style style, final GL gl, final GLU glu, final GLUT glut) {
		final Surface surface = (Surface)structureComponent;
		colors = surface.getColors();
		alignment = surface.getAlignment();
		triangulatedSurface = surface.getTriangulatedSurface();

		final DisplayLists[] lists = new DisplayLists[1];
		lists[0] = new DisplayLists(surface);
		lists[0].setupLists(1);
		lists[0].startDefine(0, gl, glu, glut);

		// enable color tracking with glColor
		gl.glPushAttrib(GL.GL_COLOR_MATERIAL);
		gl.glEnable(GL.GL_COLOR_MATERIAL);

		// draw with transparency
		gl.glPushAttrib(GL.GL_BLEND);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		if(surface.isMeshSurface()) {
			gl.glPushAttrib(GL.GL_LINE_WIDTH);
			gl.glLineWidth(1.0f);	
			drawLines(gl, GL.GL_LINES);	
			gl.glPopAttrib();
		} else if (surface.isDotSurface()) {
			gl.glPushAttrib(GL.GL_POINT_SIZE);
    	  	gl.glPointSize(3.0f);
		    drawDots(gl, GL.GL_POINTS);
		    gl.glPopAttrib();
		} else {
			// for open surfaces color render both sides
			if (surface.isBackfaceRendered()) {
				gl.glDisable(GL.GL_CULL_FACE);
			}

			// draw faces
			gl.glFrontFace(GL.GL_CCW);
			drawFaces(gl, GL.GL_TRIANGLES);
			gl.glDisable(GL.GL_BLEND);

			// reset to previous value
			if (surface.isBackfaceRendered()) {
				gl.glEnable(GL.GL_CULL_FACE);
			}
		}
		
		gl.glPopAttrib();
		gl.glPopAttrib();

		lists[0].endDefine(gl, glu, glut);
		lists[0].structureComponent = surface;

		lists[0].mutableColorType = GL.GL_AMBIENT_AND_DIFFUSE;	
		lists[0].specularColor = Constants.chainSpecularColor.color;
		lists[0].emissiveColor = Constants.chainEmissiveColor.color;
		lists[0].shininess = Constants.chainHighShininess;

		return lists;
	}

	private void drawFaces(GL gl, int shadeType) {
		gl.glBegin(shadeType);

		List<FaceInfo> faces = triangulatedSurface.getFaces();
		List<VertInfo> vertices = triangulatedSurface.getVertices();

		for (FaceInfo face: faces) {
			Point3f p0 = vertices.get(face.a).p;
			Point3f p1 = vertices.get(face.b).p;
			Point3f p2 = vertices.get(face.c).p;

			Vector3f n0 = vertices.get(face.a).normal;
			Vector3f n1 = vertices.get(face.b).normal;
			Vector3f n2 = vertices.get(face.c).normal;

			Color4f c0 = colors[face.a];
			Color4f c1 = colors[face.b];
			Color4f c2 = colors[face.c];

			// draw triangle       
			gl.glNormal3f(n0.x, n0.y, n0.z);
			gl.glColor4f(c0.x, c0.y, c0.z, c0.w);
			gl.glVertex3f(p0.x, p0.y, p0.z);

			gl.glNormal3f(n1.x, n1.y, n1.z);
			gl.glColor4f(c1.x, c1.y, c1.z, c1.w);
			gl.glVertex3f(p1.x, p1.y, p1.z);

			gl.glNormal3f(n2.x, n2.y, n2.z);
			gl.glColor4f(c2.x, c2.y, c2.z, c2.w);
			gl.glVertex3f(p2.x, p2.y, p2.z);
		}
		gl.glEnd();
		gl.glFlush();
	}
	
	private void drawLines(GL gl, int shadeType) {
		gl.glBegin(shadeType);

		List<LineInfo> lines = triangulatedSurface.getLines();
		List<VertInfo> vertices = triangulatedSurface.getVertices();

		for (LineInfo line: lines) {
			Point3f p0 = vertices.get(line.a).p;
			Point3f p1 = vertices.get(line.b).p;
			
			Vector3f n0 = vertices.get(line.a).normal;
			Vector3f n1 = vertices.get(line.b).normal;

			Color4f c0 = colors[line.a];
			Color4f c1 = colors[line.b];

			// draw lines     
			gl.glNormal3f(n0.x, n0.y, n0.z);
			gl.glColor4f(c0.x, c0.y, c0.z, c0.w);
			gl.glVertex3f(p0.x, p0.y, p0.z);

			gl.glNormal3f(n1.x, n1.y, n1.z);
			gl.glColor4f(c1.x, c1.y, c1.z, c1.w);
			gl.glVertex3f(p1.x, p1.y, p1.z);
		}
		// draw composite normal vector
		Point3f base = triangulatedSurface.getCentroid();
		Point3f tip = new Point3f(base);
		Vector3f normal = triangulatedSurface.getCompositeNormal();
		normal.scale(5.0f);
		tip.add(normal);
		System.out.println("Base: " + base);
		System.out.println("Tip: " + tip);
		
		gl.glLineWidth(15.0f);
		gl.glNormal3f(normal.x, normal.y, normal.z);
//		gl.glNormal3f(1.0f, 1.0f, 1.0f);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glVertex3f(base.x, base.y, base.z);
		
		gl.glNormal3f(normal.x, normal.y, normal.z);
	//	gl.glNormal3f(1.0f, 1.0f, 1.0f);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glVertex3f(tip.x, tip.y, tip.z);
		
		// draw longest distance
		gl.glLineWidth(15.0f);
		gl.glNormal3f(normal.x, normal.y, normal.z);
//		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		// vertex
		gl.glVertex3f(-20f, 0f, 0f);
		
		gl.glNormal3f(normal.x, normal.y, normal.z);
//		gl.glNormal3f(1.0f, 1.0f, 1.0f);
		gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		// base point
		gl.glVertex3f(20, 0, 0);
		
		gl.glLineWidth(15.0f);
		gl.glNormal3f(normal.x, normal.y, normal.z);
//		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		// vertex
		gl.glVertex3f(0f, -20f, 0f);
		
		gl.glNormal3f(normal.x, normal.y, normal.z);
//		gl.glNormal3f(1.0f, 1.0f, 1.0f);
		gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		// base point
		gl.glVertex3f(0, 20, 0);
		
		gl.glLineWidth(15.0f);
		gl.glNormal3f(normal.x, normal.y, normal.z);
//		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
		// vertex
		gl.glVertex3f(0f, 0f, -20f);
		
		gl.glNormal3f(normal.x, normal.y, normal.z);
//		gl.glNormal3f(1.0f, 1.0f, 1.0f);
		gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
		// base point
		gl.glVertex3f(0, 0, 20);
		
		//----------------------------
		// alignment vector
		
		gl.glLineWidth(15.0f);
		gl.glNormal3f(normal.x, normal.y, normal.z);
//		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		// vertex
		gl.glVertex3f(0f, 0f, 0f);
		
		gl.glNormal3f(normal.x, normal.y, normal.z);
//		gl.glNormal3f(1.0f, 1.0f, 1.0f);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		// base point
		// 1stp
		gl.glVertex3f(-2.1638867f, 9.4748944f, -2.3545647f);
		// 2jfz
	//	gl.glVertex3f(6.276257f, 7.4633217f, -2.2152701f);
		//-------------------------------------------------
		// adjusted alignment vector
		gl.glLineWidth(15.0f);
		gl.glNormal3f(normal.x, normal.y, normal.z);
//		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		gl.glColor4f(0.5f, 0.50f, 1.0f, 1.0f);
		// vertex
		gl.glVertex3f(0f, 0f, 0f);
		
		gl.glNormal3f(normal.x, normal.y, normal.z);
//		gl.glNormal3f(1.0f, 1.0f, 1.0f);
		gl.glColor4f(0.50f, 0.50f, 1.0f, 1.0f);
		// base point
		// 1stp
		gl.glVertex3f(-0.930412f, 9.9460393f, -0.4589443f);
		
		
	//	drawNormals(gl, GL.GL_LINES);
		gl.glEnd();
		gl.glFlush();
	}
	
	
//	public void drawNormals(GL gl, int shadeType) {
//		Structure structure = AppBase.sgetModel().getStructures().get(0);
//		StructureMap smap = structure.getStructureMap();
//		List<Surface> surfaces = smap.getSurfaces();
//		Vector3f compositeNormal = new Vector3f();
//		gl.glBegin(shadeType);
//		for (Surface s: surfaces){
//			List<VertInfo> vertices = s.getTriangulatedSurface().getVertices();
//			int count = 0;
//			Color4f white = new Color4f(Color.WHITE);
//			Color4f red = new Color4f(Color.RED);
//			for (VertInfo v: vertices) {
//				Point3f base = v.p;
//				Point3f tip = new Point3f(v.normal);
//				tip.scale(100f);
//				tip.add(base);
//
//				Color4f c0 = null;
//	//			if (intersectsSurface(base, tip)) {
//					c0 = red;
//				//	continue;
//				} else {
//					c0 = white;
//				//	continue;
//				}
//
//				Point3f p0 = base;
//				Point3f p1 = tip;
//
//				Vector3f n0 = v.normal;
//				Vector3f n1 = v.normal;
//
//				// draw lines     
//				gl.glNormal3f(n0.x, n0.y, n0.z);
//				gl.glColor4f(c0.x, c0.y, c0.z, c0.w);
//				gl.glVertex3f(p0.x, p0.y, p0.z);
//
//				gl.glNormal3f(n1.x, n1.y, n1.z);
//				gl.glColor4f(c0.x, c0.y, c0.z, c0.w);
//				gl.glVertex3f(p1.x, p1.y, p1.z);
//
//				//		   compositeNormal.add(v.normal);
//			}
//		}
//	}
//	
	private void drawDots(GL gl, int shadeType) {
		gl.glBegin(shadeType);

		List<VertInfo> vertices = triangulatedSurface.getVertices();

		for (int i = 0; i < vertices.size(); i++) {
			VertInfo vertex = vertices.get(i);
			Point3f p0 = vertex.p;		
			Vector3f n0 = vertex.normal;
			Color4f c0 = colors[i];

			// draw dots    
			gl.glNormal3f(n0.x, n0.y, n0.z);
			gl.glColor4f(c0.x, c0.y, c0.z, c0.w);
			gl.glVertex3f(p0.x, p0.y, p0.z);
		}
		gl.glEnd();
		gl.glFlush();
	}


}
