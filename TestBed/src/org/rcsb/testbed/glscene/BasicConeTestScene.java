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

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.vecmath.Matrix4f;

import org.rcsb.testbed.util.PrimitiveDisplayListsFactory;

import com.sun.opengl.util.GLUT;

public class BasicConeTestScene extends SceneBase implements IScene
{
	static private final float angleInc = 10.0f/180.f; // vecmath wants radians (yay)
	
	private float xaxisRot = 0.0f, yaxisRot = 0.0f, zaxisRot = 0.0f;
	private float currentMatrixValues[] = new float[16];
	private int coneListId = -1;
	
	private Matrix4f currentMatrix = new Matrix4f(), rotMatrix = new Matrix4f();
	
	public float getXRot() { return xaxisRot; }
	public float getYRot() { return yaxisRot; }
	public float getZRot() { return zaxisRot; }
	
	private enum RotAxis { X_AXIS, Y_AXIS, Z_AXIS, NONE }

	public void incXRot() { xaxisRot -= angleInc; accumRotation(RotAxis.X_AXIS, -angleInc); }
	public void incYRot() { yaxisRot += angleInc; accumRotation(RotAxis.Y_AXIS, angleInc); }
	public void incZRot() { zaxisRot += angleInc; accumRotation(RotAxis.Z_AXIS, angleInc); }
	public void decXRot() { xaxisRot += angleInc; accumRotation(RotAxis.X_AXIS, angleInc); }
	public void decYRot() { yaxisRot -= angleInc; accumRotation(RotAxis.Y_AXIS, -angleInc); }
	public void decZRot() { zaxisRot -= angleInc; accumRotation(RotAxis.Z_AXIS, -angleInc); }
	
	public BasicConeTestScene()
	{
		reset();
	}

	@Override
	public void display(GLAutoDrawable drawable, GLUT glut, GLU glu)
	{
		super.display(drawable, glut, glu);
		GL gl = drawable.getGL();

		int ix = 0;
		for (int row = 0; row < 4; row++)
			for (int col = 0; col < 4; col++)
				currentMatrixValues[ix++] = currentMatrix.getElement(row, col);
		
 		gl.glPushMatrix();
		gl.glMultMatrixf(currentMatrixValues, 0);
		
		gl.glColor3f(1.0f, 0.0f, 0.0f);

		if (coneListId == -1)
			coneListId = PrimitiveDisplayListsFactory.genCone(0.75f, gl, glut, glu);
		
		gl.glCallList(coneListId);
		gl.glPopMatrix();
	}
	
	@Override
	public void init(GLAutoDrawable drawable, GLUT glut, GLU glu)
	{
		super.init(drawable, glut, glu);
	}
	
	@Override
	public void reset()
	{
		xaxisRot = yaxisRot = zaxisRot = 0.0f;
		currentMatrix.setIdentity();
	}
	
	/**
	 * We want the rotation to act on the *accumulated* rotation, not individually.
	 * So, we have to create a matrix and keep the current rotation value in that, 
	 * then multiply this rotation against that to get what we want.
	 * 
	 * @param axis - the axis about which to rotate
	 * @param angle - the angle to rotate
	 */
	private void accumRotation(RotAxis axis, float angle)
	{
		rotMatrix.setIdentity();
		
		switch (axis)
		{
			case X_AXIS: rotMatrix.rotX(angle); break;
			case Y_AXIS: rotMatrix.rotY(angle); break;
			case Z_AXIS: rotMatrix.rotZ(angle); break;
		}
		
		currentMatrix.mul(rotMatrix);
					// multiply against the current rotation.
	}
}
