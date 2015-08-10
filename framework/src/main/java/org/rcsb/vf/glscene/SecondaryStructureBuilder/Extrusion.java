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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Vector;


import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;


import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import org.rcsb.vf.glscene.SecondaryStructureBuilder.AffineCrossSection;
import org.rcsb.vf.glscene.jogl.Color3f;
import org.rcsb.vf.glscene.jogl.DisplayLists;



import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.gl2.GLUT;



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
		
		GL2 gl2 = gl.getGL2();
		
		// arrayLists.setupVertices(this.vertexCount);
		// arrayLists.setupColors(this.vertexCount);
		// arrayLists.setupNormals(this.vertexCount);

		// gl.glColorMaterial(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE);
		// gl.glEnable(GL.GL_COLOR_MATERIAL);

		lists.mutableColorType = GL2.GL_AMBIENT_AND_DIFFUSE;
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
				final IntBuffer indexBuffer = Buffers.newDirectIntBuffer(range[1] - range[0] + 1); 
				for (int j = range[0]; j <= range[1]; j++) {
					final String key = this.coordinates[j].x + " " + this.coordinates[j].y + " " + this.coordinates[j].z + " " + this.normals[j].x + " " + this.normals[j].y + " " + this.normals[j].z;
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
				gl2.glBegin(GL.GL_TRIANGLE_STRIP);
				float coordsArray[] = new float[3];
				double vtxArray[] = new double[3];
				for (int j = range[0]; j <= range[1]; j++)
				{
					normals[j].get(coordsArray);
					coordinates[j].get(vtxArray);
					gl2.glNormal3fv(coordsArray, 0);
					gl2.glVertex3dv(vtxArray, 0);
				}
				gl2.glEnd();
				lists.endDefine(gl, glu, glut);
			}
		}

		if (DisplayLists.useVertexBufferObjects) {
			final FloatBuffer vertexBuffer = Buffers.newDirectFloatBuffer(vertexVector.size() * 3);
			final FloatBuffer normalBuffer = Buffers.newDirectFloatBuffer(normalVector.size() * 3);
			for(int i = 0; i < vertexVector.size(); i++)
			{
				final Point3d vertex = (Point3d)vertexVector.get(i);
				final Vector3f normal = (Vector3f)normalVector.get(i);
				
				vertexBuffer.put((float)vertex.x);
				vertexBuffer.put((float)vertex.y);
				vertexBuffer.put((float)vertex.z);
				
				normalBuffer.put(normal.x);
				normalBuffer.put(normal.y);
				normalBuffer.put(normal.z);
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
		lidNormal = new Vector3f(vdd.x, vdd.y,
				vdd.z);

		final Vector3f origin = ft.getOrigin();
		final Point3d center = new Point3d(origin.x,
				origin.y, origin.z);
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
