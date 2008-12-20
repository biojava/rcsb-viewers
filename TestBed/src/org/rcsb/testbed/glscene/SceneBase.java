package org.rcsb.testbed.glscene;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;

public class SceneBase implements IScene
{
	static boolean drawAxes = true;
	private int axisId;
	
	public void display(GLAutoDrawable drawable, GLUT glut, GLU glu)
	{
		GL gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		if (drawAxes)
			gl.glCallList(axisId);
	}

	public void init(GLAutoDrawable drawable, GLUT glut, GLU glu)
	{
		GL gl = drawable.getGL();
		
		final float extent = 1.0f;
		
		axisId = gl.glGenLists(1);
		gl.glNewList(axisId, GL.GL_COMPILE);
		
		gl.glDisable(GL.GL_LIGHTING);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex3f(0.0f, extent, 0.0f);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glVertex3f(extent, 0.0f, 0.0f);
		gl.glEnd();
		
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glVertex3f(0.0f, 0.0f, extent);
		gl.glEnd();
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEndList();
	}

	public void reset()
	{

	}

}
