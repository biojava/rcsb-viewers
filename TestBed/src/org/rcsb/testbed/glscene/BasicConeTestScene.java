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

import com.sun.opengl.util.GLUT;

public class BasicConeTestScene extends SceneBase implements IScene
{
	static private final float angleInc = 10.0f;
	
	private float xaxisRot = 0.0f, yaxisRot = 0.0f, zaxisRot = 0.0f;
	
	public float getXRot() { return xaxisRot; }
	public float getYRot() { return yaxisRot; }
	public float getZRot() { return zaxisRot; }
	
	private enum RotAxis { X_AXIS, Y_AXIS, Z_AXIS, NONE }
	
	private RotAxis rotAxis = RotAxis.NONE;
	
	public void incXRot() { xaxisRot += angleInc; if (xaxisRot >= 360.0f) xaxisRot -= 360.0f; rotAxis = RotAxis.X_AXIS; }
	public void incYRot() { yaxisRot += angleInc; if (yaxisRot >= 360.0f) yaxisRot -= 360.0f; rotAxis = RotAxis.Y_AXIS; }
	public void incZRot() { zaxisRot += angleInc; if (zaxisRot >= 360.0f) zaxisRot -= 360.0f; rotAxis = RotAxis.Z_AXIS; }
	public void decXRot() { xaxisRot -= angleInc; if (xaxisRot <= -360.0f) xaxisRot += 360.0f; rotAxis = RotAxis.X_AXIS; }
	public void decYRot() { yaxisRot -= angleInc; if (yaxisRot <= -360.0f) yaxisRot += 360.0f; rotAxis = RotAxis.Y_AXIS; }
	public void decZRot() { zaxisRot -= angleInc; if (zaxisRot <= -360.0f) zaxisRot += 360.0f; rotAxis = RotAxis.Z_AXIS; }
	
	public void display(GLAutoDrawable drawable, GLUT glut, GLU glu)
	{
		super.display(drawable, glut, glu);
	
		GL gl = drawable.getGL();
		
		gl.glRotatef(xaxisRot, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(yaxisRot, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(zaxisRot, 0.0f, 0.0f, 1.0f);

		gl.glColor3f(1.0f, 0.0f, 0.0f);

		gl.glPushMatrix();
		gl.glTranslatef(0.0f, -.5f, 0.0f);
		gl.glRotatef(-90, 1.0f, 0.0f, 0.0f);

		glut.glutSolidCone(.5, 1.0, 40, 1);
		
		GLUquadric qDisk = glu.gluNewQuadric();
		glu.gluQuadricOrientation(qDisk, GLU.GLU_INSIDE);
		glu.gluDisk(qDisk, 0.0, 0.5f, 40, 1);
		glu.gluDeleteQuadric(qDisk);
		gl.glPopMatrix();
	}
	
	public void init(GLAutoDrawable drawable, GLUT glut, GLU glu)
	{
		super.init(drawable, glut, glu);
	}
	
	public void reset()
	{
		xaxisRot = yaxisRot = zaxisRot = 0.0f;
	}
}
