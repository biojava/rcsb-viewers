package org.rcsb.testbed.glscene;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;

public interface IScene
{
	public void display(GLAutoDrawable drawable, GLUT glut, GLU glu);
	public void init(GLAutoDrawable drawable, GLUT glut, GLU glu);
	public void reset();
}
