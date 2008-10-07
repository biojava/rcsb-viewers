//  $Id: BackboneLine.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
//
//  Copyright 2000-2004 The Regents of the University of California.
//  All Rights Reserved.
//
//  Permission to use, copy, modify and distribute any part of this
//  Molecular Biology Toolkit (MBT)
//  for educational, research and non-profit purposes, without fee, and without
//  a written agreement is hereby granted, provided that the above copyright
//  notice, this paragraph and the following three paragraphs appear in all
//  copies.
//
//  Those desiring to incorporate this MBT into commercial products
//  or use for commercial purposes should contact the Technology Transfer &
//  Intellectual Property Services, University of California, San Diego, 9500
//  Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815,
//  FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
//
//  IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
//  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//  LOST PROFITS, ARISING OUT OF THE USE OF THIS MBT, EVEN IF THE
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//  THE MBT PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
//  UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
//  UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
//  NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
//  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
//  MBT WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
//
//  For further information, please see:  http://mbt.sdsc.edu
//
//  History:
//  $Log: BackboneLine.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.4  2004/04/09 00:04:28  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.3  2004/01/29 18:05:17  agramada
//  Removed General Atomics from copyright
//
//  Revision 1.2  2003/12/08 22:02:34  agramada
//  Moved toward an uniform approach to color updating/highlighting.
//
//  Revision 1.1  2003/07/17 20:19:18  agramada
//  First checkin.
//
//
//

package org.rcsb.mbt.ui.views.StructureViewerImpl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.rcsb.mbt.glscene.geometry.Vector3f;
import org.rcsb.mbt.glscene.jogl.DisplayLists;

import com.sun.opengl.util.GLUT;


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
		
		lists.mutableColorType = GL.GL_EMISSION;
//		lists.primitiveType = GL.GL_TRIANGLE_STRIP;
		
		lists.setupLists(ranges.length);
		for(int i = 0; i < ranges.length; i++) {
			final Object[] tmp = (Object[])ranges[i];
			final int[] range = (int[])tmp[1];
			
			lists.startDefine(i, gl, glu, glut);
			gl.glBegin(GL.GL_LINE_STRIP);
			for(int j = range[0]; j <= range[1]; j++) {
				gl.glVertex3fv(this.coords[j].coordinates, 0);
			}
			gl.glEnd();
			lists.endDefine(gl, glu, glut);
		}
	}
	
	/*
	 * 
	 */
	public void setColors(final float[][] colorMap) {
	}
}
