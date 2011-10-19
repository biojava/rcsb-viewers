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
 * Created on 2008/12/22
 *
 */ 
package org.rcsb.testbed.util;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.sun.opengl.util.GLUT;

/**
 * Static creators for various pieces of geometry.
 * 
 * @author rickb
 *
 */
public class PrimitiveDisplayListsFactory
{

	/** 
	 * Generates a cylinder of the requested radius, two unit lengths long.
	 * 
	 * @param diam
	 * @return
	 */
	public static int genCylinder(double radius, boolean centered, GL gl, GLUT glut, GLU glu)
	{
		int cylListId = gl.glGenLists(1);
		gl.glNewList(cylListId, GL.GL_COMPILE);
		gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
		if (centered)
			gl.glTranslatef(0.0f, 0.0f, -0.5f);
		
		GLUquadric qObj = glu.gluNewQuadric();
		glu.gluQuadricOrientation(qObj, GLU.GLU_OUTSIDE);
		glu.gluQuadricNormals(qObj, GL.GL_FLAT);
		glu.gluCylinder(qObj, radius, radius, 1.0, 40, 1);
		glu.gluDeleteQuadric(qObj);
		
		qObj = glu.gluNewQuadric();
		glu.gluQuadricOrientation(qObj, GLU.GLU_OUTSIDE);
		glu.gluQuadricNormals(qObj, GL.GL_FLAT);
		glu.gluDisk(qObj, 0.0, radius, 40, 1);
		glu.gluDeleteQuadric(qObj);
		
		gl.glTranslatef(0.0f, 0.0f, 1.0f);
		qObj = glu.gluNewQuadric();
		glu.gluDisk(qObj, 0.0, radius, 40, 1);
		glu.gluDeleteQuadric(qObj);
		gl.glEndList();

		return cylListId;
	}
	
	public static int genSphere(double radius, GL gl, GLUT glut, GLU glu)
	{
		int ballId = gl.glGenLists(1);
		GLUquadric qObj = glu.gluNewQuadric();
		glu.gluQuadricOrientation(qObj, GLU.GLU_OUTSIDE);
		glu.gluQuadricNormals(qObj, GL.GL_FLAT);
		gl.glNewList(ballId, GL.GL_COMPILE);
		glu.gluSphere(qObj, radius, 40, 40);
		gl.glEndList();
		
		return ballId;
	}

	public static int genCone(float size, GL gl, GLUT glut, GLU glu)
	{
		int coneId = gl.glGenLists(1);
		gl.glNewList(coneId, GL.GL_COMPILE);
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, -size/3, 0.0f);
		gl.glRotatef(-90, 1.0f, 0.0f, 0.0f);

//		glut.glutSolidCone(size / 2, size, 40, 1);

		GLUquadric qCone = glu.gluNewQuadric();
		glu.gluQuadricOrientation(qCone, GLU.GLU_OUTSIDE);
		glu.gluQuadricNormals(qCone, GL.GL_FLAT);
		glu.gluCylinder(qCone, size/2, 0.0f, size, 40, 1);
		glu.gluDeleteQuadric(qCone);
		
		GLUquadric qDisk = glu.gluNewQuadric();
		glu.gluQuadricOrientation(qDisk, GLU.GLU_INSIDE);
		glu.gluQuadricNormals(qDisk, GL.GL_FLAT);
		glu.gluDisk(qDisk, 0.0, size / 2, 40, 1);
		glu.gluDeleteQuadric(qDisk);
		gl.glPopMatrix();
		gl.glEndList();
		return coneId;
	}
}