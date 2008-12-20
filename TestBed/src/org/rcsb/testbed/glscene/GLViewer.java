package org.rcsb.testbed.glscene;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;


import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import org.rcsb.testbed.app.TestBed;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;

@SuppressWarnings("serial")
public class GLViewer extends JPanel implements GLEventListener
{	
	public GLCanvas glCanvas = null;

	private GLCapabilities glCapabilities = null;
	private Animator animator;
	private GLUT glut = new GLUT();
	private GLU glu = new GLU();
	
	public GLViewer()
	{
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
//		setBackground(Color.black);
	}
	
	public void initGL()
	{
		
		glCapabilities = new GLCapabilities();
		// glCapabilities.setAccumBlueBits(16);
		// glCapabilities.setAccumRedBits(16);
		// glCapabilities.setAccumGreenBits(16);
		// glCapabilities.setAccumAlphaBits(16);
//		glCapabilities.setNumSamples(6);
//		glCapabilities.setSampleBuffers(true);

		glCanvas = new GLCanvas(glCapabilities)
/* **/
		{
			@Override
			public void addNotify()
			{
				super.setVisible(true);
				super.addNotify();
			}
			
			@Override
			public void removeNotify()
			{
				super.setVisible(false);
			}
		}
/* **/
		;
		
		glCanvas.setSize(getWidth(), getHeight());
		
		glCanvas.addGLEventListener(this);

		add(glCanvas);
		//glCanvas.setFocusable(false);
		glCanvas.setVisible(true);
		glCanvas.requestFocus();
		
//		animator = new Animator(glCanvas);
//		animator.start();
	}

	public void display(GLAutoDrawable drawable)
	{
		TestBed.sgetActiveFrame().getScene().display(drawable, glut, glu);
	}
	
	public void requestRedraw()
	{
		glCanvas.display();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean arg1, boolean arg2)
	{

	}

	public void init(GLAutoDrawable drawable)
	{
		drawable.setGL(new DebugGL(drawable.getGL()));
		
		float lightColor[] = new float[] { 0.8f, 0.8f, 0.8f, 1.0f };

		GL gl = drawable.getGL();
/* **/
		// Lighting
		gl.glEnable(GL.GL_LIGHTING);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, lightColor, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[] {-5.0f, 10.0f, 10.0f, 0.0f}, 0);
		gl.glEnable(GL.GL_LIGHT0);

		// Rendering
		gl.glEnable(GL.GL_NORMALIZE); 
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glShadeModel(GL.GL_SMOOTH);

		gl.glLineWidth(1.0f);
		gl.glPointSize(10f);
		gl.glCullFace(GL.GL_BACK);
		gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);
		
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
		gl.glEnable(GL.GL_COLOR_MATERIAL);

		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		for (IScene scene : TestBed.sgetSceneController().getScenes())
			scene.init(drawable, glut, glu);
		//
		// Add mouse listeners
		//
//		drawable.addMouseListener(this);
//		drawable.addMouseMotionListener(this);

/* **/

//		drawable.setAutoSwapBufferMode(true);		
	}

	public void reshape(GLAutoDrawable drawable, int arg1, int arg2, int arg3,
			int arg4)
	{
	}
}
