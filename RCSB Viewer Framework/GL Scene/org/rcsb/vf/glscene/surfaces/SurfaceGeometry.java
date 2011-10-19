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

// package org.rcsb.mbt.viewers.GlStructureViewer;


import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.attributes.*;
import org.rcsb.mbt.surface.datastructure.TriangulatedSurface;
import org.rcsb.mbt.surface.datastructure.FaceInfo;
import org.rcsb.mbt.surface.datastructure.VertInfo;
import org.rcsb.vf.glscene.jogl.Constants;
import org.rcsb.vf.glscene.jogl.DisplayListGeometry;
import org.rcsb.vf.glscene.jogl.DisplayLists;

import com.sun.opengl.util.GLUT;



public class SurfaceGeometry
extends DisplayListGeometry
{
	TriangulatedSurface triangulatedSurface = null;
	Color4f[] colors = null;

	/**
	 *  Construct a new SurfaceGeometry object.
	 */
	public SurfaceGeometry( )
	{
		//		System.out.println("Creating new SurfaceGeometry");
	}

	public DisplayLists[] getDisplayLists(final StructureComponent structureComponent, final Style style, final GL gl, final GLU glu, final GLUT glut) {

		final Surface surface = (Surface)structureComponent;
		colors = surface.getColors();
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

		// draw faces
		gl.glFrontFace(GL.GL_CCW);
	//	gl.glPushAttrib(GL.GL_LIGHT_MODEL_TWO_SIDE);
	//	gl.glLightModeli(GL.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_FALSE);
		
		gl.glPointSize(1.5f);
		drawFaces(gl, GL.GL_TRIANGLES);
	//	       drawFaces(gl, GL.GL_POINTS);

		gl.glDisable(GL.GL_BLEND);
		
		gl.glPopAttrib();
		gl.glPopAttrib();
	//	gl.glPopAttrib();

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
			gl.glNormal3f(n0.getX(), n0.getY(), n0.getZ());
			gl.glColor4f(c0.getX(), c0.getY(), c0.getZ(), c0.getW());
			gl.glVertex3f(p0.getX(), p0.getY(), p0.getZ());

			gl.glNormal3f(n1.getX(), n1.getY(), n1.getZ());
			gl.glColor4f(c1.getX(), c1.getY(), c1.getZ(), c1.getW());
			gl.glVertex3f(p1.getX(), p1.getY(), p1.getZ());

			gl.glNormal3f(n2.getX(), n2.getY(), n2.getZ());
			gl.glColor4f(c2.getX(), c2.getY(), c2.getZ(), c2.getW());
			gl.glVertex3f(p2.getX(), p2.getY(), p2.getZ());
		}
		gl.glEnd();
		gl.glFlush();
	}

}
