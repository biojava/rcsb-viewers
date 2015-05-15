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
 * Created on 2007/02/08
 *
 */ 
package org.rcsb.vf.glscene.SecondaryStructureBuilder;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import javax.vecmath.Vector3f;

import org.rcsb.vf.glscene.jogl.DisplayLists;

import com.jogamp.opengl.util.gl2.GLUT;


/**
 * BackboneLine is a LineStripArray class optimized for representing a backbone
 * trace. This class incorporates a simple line representation of the CA atom
 * path (smoothed or not).
 * 
 * @author Apostol Gramada
 */
public class BackboneLine {

	private Vector3f[] coords = null;
	
	public BackboneLine(final int vertexCount, final int[] stripVertexCounts,
			final Vector3f[] coords, final float[][] colorMap) {
		//super(vertexCount, LineStripArray.COORDINATES | LineStripArray.COLOR_3,
		//		stripVertexCounts);

		this.coords = coords;
	}

	// possible problem: Apostol doesn't seem to break his backbones when there is a break in sequence. John Moreland does. I'm using Apostol's logic. Is this a problem?
	public void draw(final DisplayLists lists, final GL gl, final GLU glu, final GLUT glut, final Object[] ranges) {
//		arrayLists.setupColors(this.vertexCount);
		
		lists.mutableColorType = GL2.GL_EMISSION;
//		lists.primitiveType = GL.GL_TRIANGLE_STRIP;
		
		GL2 gl2 = gl.getGL2();
		
		lists.setupLists(ranges.length);
		for(int i = 0; i < ranges.length; i++) {
			final Object[] tmp = (Object[])ranges[i];
			final int[] range = (int[])tmp[1];
			
			lists.startDefine(i, gl, glu, glut);
			gl2.glBegin(GL.GL_LINE_STRIP);
			float coordsArray[] = new float[3];
			for(int j = range[0]; j <= range[1]; j++)
			{
				this.coords[j].get(coordsArray);
				gl2.glVertex3fv(coordsArray, 0);
			}
			gl2.glEnd();
			lists.endDefine(gl, glu, glut);
		}
	}
	
	/*
	 * 
	 */
	public void setColors(final float[][] colorMap) {
	}
}
