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
package org.rcsb.vf.glscene.jogl;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

public class JoglUtils {

	public static void accFrustum(final GL gl, final GLU glu, final GLUT glut, final double left,
			final double right, final double bottom, final double top, final double zNear, final double zFar,
			final double pixdx, final double pixdy, final double eyedx, final double eyedy, final double focus) {
		
		GL2 gl2 = gl.getGL2();
		
		double xwSize, ywSize;
		double dx, dy;
		final int[] viewport = new int[4];

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		xwSize = right - left;
		ywSize = top - bottom;
		dx = -(pixdx * xwSize / viewport[2] + eyedx * zNear / focus);
		dy = -(pixdy * ywSize / viewport[3] + eyedy * zNear / focus);

		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		gl2.glFrustum(left + dx, right + dx, bottom + dy, top + dy, zNear, zFar);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();
		gl2.glTranslatef(-(float) eyedx, -(float) eyedy, 0f);
	}

	public static void accPerspective(final GL gl, final GLU glu, final GLUT glut, final double fovy,
			final double aspect, final double zNear, final double zFar, final double pixDx,
			final double pixdy, final double eyedx, final double eyedy, final double focus) {
		final double fov2 = ((fovy * Math.PI) / 180) / 2;

		double top = zNear / (Math.cos(fov2) / Math.sin(fov2));
		final double bottom = -top;
		double right = top * aspect;
		final double left = -right;

		JoglUtils.accFrustum(gl, glu, glut, left, right, bottom, top, zNear, zFar, pixDx,
				pixdy, eyedx, eyedy, focus);
//		frustum_jitter(gl, glu, glut, left, right, bottom, top, zNear, zFar, pixDx, pixdy);
		
		// double ymax = zNear * Math.tan( fovy * 3.14159265 / 360.0 );
		// double ymin = -ymax;
		// double xmin = ymin * aspect;
		// double xmax = ymax * aspect;
		// accFrustum(gl, glu, glut, xmin, xmax, ymin, ymax, zNear, zFar, pixDx,
		// pixdy, eyedx, eyedy, focus );

	}
}
