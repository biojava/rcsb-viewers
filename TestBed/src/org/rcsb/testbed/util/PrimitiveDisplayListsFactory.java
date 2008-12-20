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
		glu.gluCylinder(qObj, radius, radius, 1.0, 40, 1);
		glu.gluDeleteQuadric(qObj);
		
		qObj = glu.gluNewQuadric();
		glu.gluQuadricOrientation(qObj, GLU.GLU_INSIDE);
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
		glu.gluQuadricOrientation(qObj, GLU.GLU_INSIDE);
		gl.glNewList(ballId, GL.GL_COMPILE);
		glu.gluSphere(qObj, radius, 40, 40);
		gl.glEndList();
		
		return ballId;
	}

}
