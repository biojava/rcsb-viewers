//  $Id: Extrusion.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Extrusion.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.3  2007/01/03 19:33:49  jbeaver
//  *** empty log message ***
//
//  Revision 1.2  2006/10/04 17:21:06  jbeaver
//  Lots of changes from surfaces to improved picking
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.2  2006/05/30 09:43:44  jbeaver
//  Added lines and fog
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.6  2004/04/09 00:04:28  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.5  2004/01/29 18:05:23  agramada
//  Removed General Atomics from copyright
//
//  Revision 1.4  2003/12/09 00:44:57  agramada
//  Removed reference to debug class.
//
//  Revision 1.3  2003/12/08 22:09:04  agramada
//  Changes associated with a more uniform color updating approach.
//
//  Revision 1.2  2003/07/17 18:01:45  agramada
//  Changed the signature of some color highlighting methods.
//
//  Revision 1.1  2003/06/24 22:19:45  agramada
//  Reorganized the geometry package. Old classes removed, new classes added.
//
//  Revision 1.1  2003/01/10 19:43:48  agramada
//  First check in of the 3d graphics package.
//
//  Revision 1.0  2002/06/10 23:38:39  agramada
//

package org.rcsb.mbt.ui.views.StructureViewerImpl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Vector;


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.rcsb.mbt.glscene.geometry.Point3d;
import org.rcsb.mbt.glscene.jogl.Color3f;
import org.rcsb.mbt.glscene.jogl.DisplayLists;
import org.rcsb.mbt.glscene.jogl.Vector3f;
import org.rcsb.mbt.ui.views.StructureViewerImpl.AffineCrossSection;


import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;

/**
 * Extrusion is the main building block for ribbon-like secondary structure
 * representations
 * 
 * @author Apostol Gramada
 * @version $Revision: 1.1 $
 * @since JDK1.2.2
 */
public class Extrusion {

	private AffineCrossSection[] crossSections = null; // All crossSection

	// assumed to have the
	// same number of
	// vertices

	public int vertexCount = 0;

	private int startIndex = 0;

	private int endIndex = 0;

	private boolean PER_FACE_NORMALS = true; // Coils are better off with

	// PER_VERTEX_NORMAL

	private int colorMapStart = 0;

	private float[][] colors = null;

	// arrays for compatability with Java3d so I don't need to change the logic
	// too much when converting from Java3d.
	private Point3d[] coordinates = null;

	private Vector3f[] normals = null;

	private Color3f[] colorsLegacy = null;

	// A number of constructors are needed to handle different cases
	public Extrusion(final int coordinateCount, final int start,
			final int[] stripVertexCounts, final AffineCrossSection[] cs,
			final float[][] colorMap, final int colorMapStart,
			final boolean perVertexNormal) {
		// super( 4*((coordinateCount-1)*(cs[start].getVertexCount()-1) +
		// cs[start].getVertexCount() ),
		// //TriangleStripArray.BY_REFERENCE |
		// TriangleStripArray.COORDINATES | TriangleStripArray.NORMALS |
		// TriangleStripArray.COLOR_3,
		// stripHVertexCounts );

		this.crossSections = cs;
		this.colors = colorMap;
		this.colorMapStart = colorMapStart;
		this.startIndex = start;
		this.endIndex = this.startIndex + coordinateCount - 1;
		this.vertexCount = 4 * ((coordinateCount - 1)
				* (cs[0].getVertexCount() - 1) + cs[0].getVertexCount());

		this.coordinates = new Point3d[this.vertexCount];
		this.normals = new Vector3f[this.vertexCount];
		this.colorsLegacy = new Color3f[this.vertexCount];

		if (perVertexNormal) {
			this.PER_FACE_NORMALS = false;
		}

		this.build();
	}

	 public static int VERTEX_COUNT = 0;
	 public static int VERTEX_CACHE_HITS = 0;
	public static HashMap debugMap = new HashMap();

	public void draw(final DisplayLists lists, final GL gl, final GLU glu,
			final GLUT glut, final Object[] ranges) {
		// arrayLists.setupVertices(this.vertexCount);
		// arrayLists.setupColors(this.vertexCount);
		// arrayLists.setupNormals(this.vertexCount);

		// gl.glColorMaterial(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE);
		// gl.glEnable(GL.GL_COLOR_MATERIAL);

		lists.mutableColorType = GL.GL_AMBIENT_AND_DIFFUSE;
		// lists.primitiveType = GL.GL_TRIANGLE_STRIP;

		if (DisplayLists.useVertexBufferObjects) {
			lists.startDefineVertexBufferObjects(ranges.length);
		} else {
			lists.setupLists(ranges.length);
		}
		
		// because of caching, it isn't immediately obvious how many vertices we'll have. Means one additional copy, but it's necessary for this approach.
		Vector vertexVector = null;
		Vector normalVector = null;
		HashMap vertexCache = null;
		if (DisplayLists.useVertexBufferObjects) {
			vertexVector = new Vector();
			normalVector = new Vector();
			vertexCache = new HashMap();
		}
		
		for (int i = 0; i < ranges.length; i++) {
			final Object[] tmp = (Object[]) ranges[i];
			final int[] range = (int[]) tmp[1];
			
			int minIndex = Integer.MAX_VALUE;
			int maxIndex = 0;

			if (DisplayLists.useVertexBufferObjects) {
				final IntBuffer indexBuffer = BufferUtil.newIntBuffer(range[1] - range[0] + 1); 
				for (int j = range[0]; j <= range[1]; j++) {
					final String key = this.coordinates[j].vector[0] + " " + this.coordinates[j].vector[1] + " " + this.coordinates[j].vector[2] + " " + this.normals[j].coordinates[0] + " " + this.normals[j].coordinates[1] + " " + this.normals[j].coordinates[2];
					Integer index = (Integer)vertexCache.get(key);
					if(index == null) {	// not in the cache - create a new vertex index
						final int indexNew = vertexVector.size();
						index = new Integer(indexNew);
						indexBuffer.put(indexNew);
						vertexVector.add(this.coordinates[j]);
						normalVector.add(this.normals[j]);
						vertexCache.put(key, index);
						
						minIndex = Math.min(indexNew, minIndex);
						maxIndex = Math.max(indexNew, maxIndex);
					} else {	// in the cache - reuse the old vertex.
						final int oldIndex = index.intValue();
						indexBuffer.put(oldIndex);
						minIndex = Math.min(oldIndex, minIndex);
						maxIndex = Math.max(oldIndex, maxIndex);
						Extrusion.VERTEX_CACHE_HITS++;
					}
					Extrusion.VERTEX_COUNT++;
				}

				lists.setIndexArray(i, indexBuffer, new int[] {minIndex, maxIndex});
			} else {
				lists.startDefine(i, gl, glu, glut);
				gl.glBegin(GL.GL_TRIANGLE_STRIP);
				for (int j = range[0]; j <= range[1]; j++) {
					gl.glNormal3fv(this.normals[j].coordinates, 0);
					gl.glVertex3dv(this.coordinates[j].vector, 0);
				}
				gl.glEnd();
				lists.endDefine(gl, glu, glut);
			}
		}

		if (DisplayLists.useVertexBufferObjects) {
			final FloatBuffer vertexBuffer = BufferUtil.newFloatBuffer(vertexVector.size() * 3);
			final FloatBuffer normalBuffer = BufferUtil.newFloatBuffer(normalVector.size() * 3);
			for(int i = 0; i < vertexVector.size(); i++) {
				final Point3d vertex = (Point3d)vertexVector.get(i);
				final Vector3f normal = (Vector3f)normalVector.get(i);
				for (int k = 0; k < vertex.vector.length; k++) {
					vertexBuffer.put((float) vertex.vector[k]);
				}
				for (int k = 0; k < normal.coordinates.length; k++) {
					normalBuffer.put(normal.coordinates[k]);
				}
			}
			
			lists.defineVertexBufferObject(gl, glu, glut, vertexBuffer, normalBuffer, vertexBuffer.capacity());
			
			lists.endDefineVertexBufferObjects();
		}
	}

	public void build() {
		final int baseVert = this.crossSections[this.startIndex]
				.getVertexCount();
		final int edges = baseVert - 1;
		Color3f[] csColors1;
		Color3f[] csColors2;

		// Each coordinate on the path is the center of the first lid of a
		// TriangleStripArray
		// box. Overall, we have as many strips as number of coordinates-1
		// (coordinateCount-1).
		// Need a consistent enumeration of the vertices composing the two
		// basis.
		//
		int vertexIndex = 0;
		// Start generating the Triangle Strip Array.
		//
		Point3d[] csPoints1 = this.crossSections[this.startIndex]
				.getPointVertices();
		Vector3f[] csNormals1 = this.crossSections[this.startIndex]
				.getNormals();
		Point3d[] csPoints2 = null;
		Vector3f[] csNormals2 = null;

		Point3d v1 = null;
		Point3d v2 = null;
		Point3d v3 = null;
		Point3d v4 = null;
		Vector3f n1 = null;
		Vector3f n2 = null;
		Vector3f n3 = null;
		Vector3f n4 = null;

		Vector3f n11 = null;
		Vector3f n22 = null;
		Vector3f n33 = null;
		Vector3f n44 = null;

		vertexIndex = this.fillSection(this.crossSections[this.startIndex],
				this.colors[this.colorMapStart], true, vertexIndex, true);

		int colorIndex = this.colorMapStart;
		for (int i = this.startIndex; i < this.endIndex; i++) {
			csPoints2 = this.crossSections[i + 1].getPointVertices();
			csNormals2 = this.crossSections[i + 1].getNormals();

			// Now, assign vertices, colors and normals.
			int l = 0;

			v1 = csPoints1[0];
			v2 = csPoints2[0];

			for (int j = 0; j < edges; j++) {
				n1 = csNormals1[j];
				n2 = csNormals2[j];

				// l = (j+1)%edges; // Need to cycle at the end
				l = j + 1;
				v3 = csPoints1[l];
				v4 = csPoints2[l];
				n11 = n1;
				n22 = n2;
				if (this.PER_FACE_NORMALS) {
					n33 = n11;
					n44 = n22;
				} else {
					n3 = csNormals1[l];
					n4 = csNormals2[l];
					n33 = n3;
					n44 = n4;
				}

				this.setCoordinate(vertexIndex, v1);
				this.setNormal(vertexIndex++, n11);
				this.setCoordinate(vertexIndex, v2);
				this.setNormal(vertexIndex++, n22);
				this.setCoordinate(vertexIndex, v3);
				this.setNormal(vertexIndex++, n33);
				this.setCoordinate(vertexIndex, v4);
				this.setNormal(vertexIndex++, n44);

				if (((csColors2 = this.crossSections[i + 1].getColors()) != null)
						&& ((csColors1 = this.crossSections[i].getColors()) != null)) {
					vertexIndex -= 4;
					this.setColor(vertexIndex++, csColors1[j]);
					this.setColor(vertexIndex++, csColors2[j]);
					this.setColor(vertexIndex++, csColors1[j]);
					this.setColor(vertexIndex++, csColors2[j]);
				} else {
					vertexIndex -= 4;
					this.setColor(vertexIndex++, this.colors[colorIndex]);
					this.setColor(vertexIndex++, this.colors[colorIndex]);
					this.setColor(vertexIndex++, this.colors[colorIndex]);
					this.setColor(vertexIndex++, this.colors[colorIndex]);
				}

				v1 = v3;
				v2 = v4;
			}

			// Move to the next strip
			csPoints1 = csPoints2;
			csNormals1 = csNormals2;
			colorIndex++;
		}

		vertexIndex = this.fillSection(this.crossSections[this.endIndex],
				this.colors[colorIndex - 1], false, vertexIndex, false);

	}

	private int fillSection(final AffineCrossSection cs, final float[] color,
			final boolean invertNormal, final int vertex_, final boolean clockWise) {
		int vertex = vertex_;
		final Point3d[] points = cs.getPointVertices();
		final Vector3f[] normals = cs.getNormals();
		final FrenetTrihedron ft = cs.getFrenetTrihedron();

		Vector3f lidNormal = null;
		final Vector3f vdd = ft.getTangent();
		if (invertNormal) {
			vdd.negate();
		}
		vdd.normalize();
		lidNormal = new Vector3f(vdd.coordinates[0], vdd.coordinates[1],
				vdd.coordinates[2]);

		final Vector3f origin = ft.getOrigin();
		final Point3d center = new Point3d(origin.coordinates[0],
				origin.coordinates[1], origin.coordinates[2]);
		Point3d v1 = null;
		final int vertices = cs.getVertexCount();
		final Color3f sideColor = cs.getSideColor();
		if (clockWise) {
			int vert = 0;
			if (sideColor == null) {
				while (vert < vertices) {
					v1 = center;
					this.setColor(vertex, color);
					this.setCoordinate(vertex, v1);
					this.setNormal(vertex, lidNormal);
					vertex++;
					v1 = points[vert];
					this.setColor(vertex, color);
					this.setCoordinate(vertex, v1);
					this.setNormal(vertex, lidNormal);
					vert++;
					vertex++;
				}
			} else {
				while (vert < vertices) {
					v1 = center;
					this.setColor(vertex, new Color3f(sideColor));
					this.setCoordinate(vertex, v1);
					this.setNormal(vertex, lidNormal);
					vertex++;
					v1 = points[vert];
					this.setColor(vertex, new Color3f(sideColor));
					this.setCoordinate(vertex, v1);
					this.setNormal(vertex, lidNormal);
					vert++;
					vertex++;
				}
			}
		} else {
			int vert = vertices - 1;
			if (sideColor == null) {
				while (vert >= 0) {
					v1 = center;
					this.setColor(vertex, color);
					this.setCoordinate(vertex, v1);
					this.setNormal(vertex, lidNormal);
					vertex++;
					v1 = points[vert];
					this.setColor(vertex, color);
					this.setCoordinate(vertex, v1);
					this.setNormal(vertex, lidNormal);
					vert--;
					vertex++;
				}
			} else {
				while (vert >= 0) {
					v1 = center;
					this.setColor(vertex, new Color3f(sideColor));
					this.setCoordinate(vertex, v1);
					this.setNormal(vertex, lidNormal);
					vertex++;
					v1 = points[vert];
					this.setColor(vertex, new Color3f(sideColor));
					this.setCoordinate(vertex, v1);
					this.setNormal(vertex, lidNormal);
					vert--;
					vertex++;
				}
			}
		}

		return vertex;
	}

	public void setColors(final float[][] colors) {
		this.colors = colors;
	}

	public float[][] getColors() {
		return this.colors;
	}

	// Set the torsion angles
	public void setTorsion(final double[] angle) {
	}

	private void setCoordinate(final int vertexIndex, final Point3d point) {
		this.coordinates[vertexIndex] = point;
	}

	private void setNormal(final int vertexIndex, final Vector3f vector) {
		this.normals[vertexIndex] = vector;
	}

	private void setColor(final int vertexIndex, final Color3f color) {
		this.colorsLegacy[vertexIndex] = color;
	}

	private void setColor(final int vertexIndex, final float[] color) {
		this.colorsLegacy[vertexIndex] = new Color3f(color[0], color[1],
				color[2]);
	}
}
