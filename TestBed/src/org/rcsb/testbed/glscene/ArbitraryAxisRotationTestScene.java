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

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.rcsb.mbt.model.geometry.ArrayLinearAlgebra;
import org.rcsb.testbed.app.TestBed;
import org.rcsb.testbed.util.PrimitiveDisplayListsFactory;

import com.sun.opengl.util.GLUT;

public class ArbitraryAxisRotationTestScene extends SceneBase implements IScene
{	
	private int cylListId, rotVectorId, ballId;
	private ArrayList<float[]> vectors = new ArrayList<float[]>();
	private int currentVectorIX;
	private final float r2d = 180.0f / (float)Math.PI,  rotInc = 10.0f;
	private float rotAngle = 0.0f;
	private final float ballPoint[] = new float[] {0.25f, 0.0f, 0.0f};
	private boolean useGLRot = true;
	
	public void incRotAngle() { rotAngle += rotInc; if (rotAngle >= 360.0f) rotAngle -= 360.0f;}
	public void decRotAngle() { rotAngle -= rotInc; if (rotAngle <= -360.0f) rotAngle -= 360.0f; }
	public void setUseGLRot(boolean flag) { useGLRot = flag; reset(); }
	public void nextRotVector() { currentVectorIX++; if (currentVectorIX >= vectors.size()) currentVectorIX = 0; reset(); }
	
	public void init(GLAutoDrawable drawable, GLUT glut, GLU glu)
	{
			super.init(drawable, glut, glu);
			final double diameter = 0.01;
			
			currentVectorIX = TestBed.sgetCaseNum();
			
			GL gl = drawable.getGL();
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			gl.glLoadIdentity();
			
			cylListId = PrimitiveDisplayListsFactory.genCylinder(diameter, true, gl, glut, glu);
			rotVectorId = gl.glGenLists(1);
			gl.glNewList(rotVectorId, GL.GL_COMPILE);
			gl.glPushMatrix();
			gl.glCallList(cylListId);
			gl.glTranslatef(0.125f, 0.0f, -0.5f);
			gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
			gl.glScalef(1.0f, 0.25f, 1.0f);
			gl.glCallList(cylListId);
			gl.glPopMatrix();
			gl.glEndList();
			
			ballId = PrimitiveDisplayListsFactory.genSphere(diameter * 2.0, gl, glut, glu);
			loadArrayList();
	}
	
	private void loadArrayList()
	{
		vectors.add(new float[] { 1.0f, 1.0f, 1.0f });
		vectors.add(new float[] { 0.5f, 1.0f, 1.0f });
		vectors.add(new float[] { -0.3f, -1.0f, 1.0f});
		vectors.add(new float[] { 0.0f, 1.0f, 0.0f });
		vectors.add(new float[] { 1.0f, 0.0f, 0.0f });
		vectors.add(new float[] { 0.0f, 0.0f, 1.0f });
	}

	public void display(GLAutoDrawable drawable, GLUT glut, GLU glu)
	{
		super.display(drawable, glut, glu);
		GL gl = drawable.getGL();
		
		final float rotVec[] = vectors.get(currentVectorIX);
		ArrayLinearAlgebra.normalizeVector(rotVec);
		
		final float ortho[] = { rotVec[2], 0.0f, -rotVec[0] };
		final float orientation = (float)Math.acos( rotVec[1] );
		
		gl.glPushMatrix();
		gl.glRotatef(rotAngle, rotVec[0], rotVec[1], rotVec[2]);
		gl.glRotatef(orientation * r2d, ortho[0], ortho[1], ortho[2]);
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		gl.glCallList(rotVectorId);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		if (useGLRot)
		{
			gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
			gl.glRotatef(rotAngle, rotVec[0], rotVec[1], rotVec[2]);
			gl.glRotatef(orientation * r2d, ortho[0], ortho[1], ortho[2]);
			gl.glTranslatef(0.25f, 0.0f, 0.0f);
		}
		else
		{
			float rotator[] = new float[4];
			float point[] = new float[] { ballPoint[0], ballPoint[1], ballPoint[2] };

			gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
			if (ArrayLinearAlgebra.vectorLength(ortho) > 1.0e-9)
			{
				rotator[0] = orientation;
				rotator[1] = ortho[0];
				rotator[2] = ortho[1];
				rotator[3] = ortho[2];
				ArrayLinearAlgebra.angleAxisRotate(rotator, point);
								// rotate to the orientation, first.
			}
			
			rotator[0] = rotAngle / r2d;  // radians, please
			rotator[1] = rotVec[0];
			rotator[2] = rotVec[1];
			rotator[3] = rotVec[2];
			ArrayLinearAlgebra.angleAxisRotate(rotator, point);
								// rotate about the oriented axis.
			
			gl.glTranslatef(point[0], point[1], point[2]);
		}
		gl.glCallList(ballId);
		gl.glPopMatrix();
	}

	public void reset()
	{
		rotAngle = 0.0f;
	}
}
