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
package org.rcsb.testbed.glscene;

import java.nio.FloatBuffer;
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
import com.sun.opengl.util.BufferUtil;
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
		
		float lightColor[] = new float[] {1.0f, 1.0f, 1.0f, 1.0f };

		GL gl = drawable.getGL();
		
		FloatBuffer ambient = BufferUtil.newFloatBuffer(4);
		ambient.put(0, 0.2f);
		ambient.put(1, 0.2f);
		ambient.put(2, 0.2f);
		ambient.put(3, 1.0f);
		ambient.rewind();
		
		gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, ambient);
		
/* **/
		// Lighting
		gl.glEnable(GL.GL_LIGHTING);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, lightColor, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[] {-1.0f, 1.0f, 5.0f, 0.0f}, 0);
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
		
		gl.glColorMaterial(GL.GL_FRONT, GL.GL_AMBIENT);
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
		GL gl = drawable.getGL();
		gl.glViewport(0, 0, getWidth(), getHeight());
		gl.glMatrixMode(gl.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(60.0f, (float)getWidth()/(float)getHeight(), 0.2f, 10.0f);
	}
}
