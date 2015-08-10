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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.TreeMap;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import javax.vecmath.Color4b;
import javax.vecmath.Color4f;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.LineSegment;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.Surface;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.BondStyle;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.attributes.LineStyle;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.attributes.SurfaceStyle;
import org.rcsb.mbt.model.util.DebugState;
import org.rcsb.vf.controllers.app.VFAppBase;


import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.gl2.GLUT;


public class DisplayLists
{
	public class UniqueColorMapValue
	{
		public UniqueColorMapValue(final DisplayLists lists, final int index)
		{
			this.lists = lists;
			this.index = index;
		}

		public DisplayLists lists = null;

		public int index = -1;
	}

	// public FloatBuffer colors = null;
	// public FloatBuffer normals = null;
	// public FloatBuffer vertices = null;
	// public IntBuffer indices = null;
	public int[] videoMemoryReferences = null; // Used for display lists,
												// vertex buffer objects, etc.
												// These mostly encapsulate
												// vertex and normal data for
												// this object.

	public int normalReference = -1; // used only for vertex buffer objects.

	public int vertexReference = -1; // used only for vertex buffer objects.

	public IntBuffer[] indexArrays = null; // used only for vertex buffer
											// objects.

	public int[][] ranges = null;

	public static boolean useVertexBufferObjects = false; // DEBUG - switch
															// between display
															// lists and VBOs.

	public boolean memoryReferencesAreToDisplayLists = true; // else, vertex
																// buffer
																// objects.

	// public int primitiveType = -1; // the type of primitive drawn with this
	// array list.
	public float[] ambientColor = null;

	public float[] specularColor = null;

	public float[] emissiveColor = null;

	public float[] diffuseColor = null;

	public float[] shininess = null;

	public float[] translation = null; // translate the drawn object to this
										// coordinate.

	public float[] rotation = null; // rotate the object by this amount.

	public float[] scale = null; // scale the drawn object by this amount.
	
	public boolean doPreRot = false;
	public double preTranslateX;  // pre-transform by this amount (required by multiple order bonds)
	public double preRotYcos = 1.0, preRotYsin = 0.0;
	
	public int mutableColorType = -1; // the type of color which is modified
										// by this.colors.
	// public int vertexSize = -1;

	// bonds only
	public boolean isLeftSideOfBond = false; // else, is right. Left is
												// bond.getAtom(0) side. Right
												// is bond.getAtom(1) side.

	public boolean disableLigting = false;

	private Color3b[] uniqueColors = null; // one name used for each display
											// list

	// private HashedMap rangeMap = null;

	// public static boolean areNormalsEnabled = false;
	// public static boolean areColorsEnabled = false;

	public static final byte uniqueColorIncrement = (byte) 1;

	public static Color3b firstColor = new Color3b(DisplayLists.uniqueColorIncrement,
			(byte) 0, (byte) 0, (byte) 0);

	public static Color3b nextColor = DisplayLists.firstColor;

	public static TreeMap<Color3b,UniqueColorMapValue> uniqueColorMap = new TreeMap<Color3b,UniqueColorMapValue>(); // maps names to
															// arrayLists.

	// public static int COLOR_DIMENSIONS = 3;
	// public static int NORMAL_DIMENSIONS = 3;
	// public static int VERTEX_DIMENSIONS = 3;
	// public static int INDEX_DIMENSIONS = 1;

	public StructureComponent structureComponent = null;

	public void disableLighting() {
		this.disableLigting = true;
	}

	public DisplayLists(final StructureComponent sc) {
		this.structureComponent = sc;
	}

	/**
	 * Used for picking. The scene is drawn such that each display list uses a
	 * unique color.
	 * 
	 * @param index
	 * @return
	 */
	public Color3b getRepresentativeColor(final int index) {
		return this.uniqueColors[index];
	}

	public void setupLists(final int size) {
		// if(this.displayLists == null) {
		this.videoMemoryReferences = new int[size];
		for (int i = 0; i < this.videoMemoryReferences.length; i++) {
			this.videoMemoryReferences[i] = -1;
		}

		this.generateUniqueColors();
		// }
	}

	private void generateUniqueColors() {
		if (this.uniqueColors != null) {
			for (int i = 0; i < this.uniqueColors.length; i++) {
				DisplayLists.uniqueColorMap.remove(this.uniqueColors[i]);
			}
		}

		final GlGeometryViewer viewer = VFAppBase.sgetGlGeometryViewer();

		int colorCount = 0;
		if(this.indexArrays != null) {	// vertex buffer objects
			colorCount = this.indexArrays.length;
		} else {
			colorCount = this.videoMemoryReferences.length;
		}
		this.uniqueColors = new Color3b[colorCount];
		for (int i = 0; i < colorCount; i++) {
			// do names...
			this.uniqueColors[i] = DisplayLists.nextColor;

			DisplayLists.uniqueColorMap.put(DisplayLists.nextColor,
					new UniqueColorMapValue(this, i));

			if (DisplayLists.uniqueColorMap.size() >= viewer.colorCount) {
//				(new Exception(
//						"Warning: all names are taken. Operations like picking which demand unique names may fail to work as expected."))
//						.printStackTrace();
				return;
			}

			// increment the next name.
			while (true) {
				DisplayLists.nextColor = new Color3b(DisplayLists.nextColor);
				if (DisplayLists.nextColor.color[0] == 127) {
					if (DisplayLists.nextColor.color[1] == 127) {
						if (DisplayLists.nextColor.color[2] == 127) {
							if (DisplayLists.nextColor.color[3] == 127) { // wrap
																			// around.
								DisplayLists.nextColor = DisplayLists.firstColor;
							} else {
								DisplayLists.nextColor
										.set(
												(byte) -126,
												(byte) -126,
												(byte) -126,
												(byte) (DisplayLists.nextColor.color[3] + DisplayLists.uniqueColorIncrement));
							}
						} else {
							DisplayLists.nextColor
									.set(
											(byte) -126,
											(byte) -126,
											(byte) (DisplayLists.nextColor.color[2] + DisplayLists.uniqueColorIncrement),
											DisplayLists.nextColor.color[3]);
						}
					} else {
						DisplayLists.nextColor
								.set(
										(byte) -126,
										(byte) (DisplayLists.nextColor.color[1] + DisplayLists.uniqueColorIncrement),
										DisplayLists.nextColor.color[2],
										DisplayLists.nextColor.color[3]);
					}
				} else {
					DisplayLists.nextColor
							.set(
									(byte) (DisplayLists.nextColor.color[0] + DisplayLists.uniqueColorIncrement),
									DisplayLists.nextColor.color[1],
									DisplayLists.nextColor.color[2],
									DisplayLists.nextColor.color[3]);
				}

				if (!DisplayLists.uniqueColorMap
						.containsKey(DisplayLists.nextColor)) {
					break;
				}
			}
		}
	}

	public DisplayLists copy() {
		final DisplayLists clone = new DisplayLists(this.structureComponent);

		clone.ambientColor = this.ambientColor;
		clone.specularColor = this.specularColor;
		clone.emissiveColor = this.emissiveColor;
		clone.diffuseColor = this.diffuseColor;
		clone.shininess = this.shininess;
		clone.translation = this.translation; // translate the drawn object to
												// this coordinate.
		clone.scale = this.scale; // scale the drawn object by this amount.
		clone.rotation = this.rotation;
		clone.mutableColorType = this.mutableColorType;

		clone.videoMemoryReferences = this.videoMemoryReferences;
		clone.memoryReferencesAreToDisplayLists = this.memoryReferencesAreToDisplayLists;
		clone.normalReference = this.normalReference;
		clone.vertexReference = this.vertexReference;
		clone.ranges = this.ranges;

		clone.generateUniqueColors();

		clone.isLeftSideOfBond = this.isLeftSideOfBond;

		clone.disableLigting = this.disableLigting;
		return clone;
	}

	public void drawSimple(final GL gl, final GLU glu, final GLUT glut,
			final boolean isPickMode) {
		
		GL2 gl2 = gl.getGL2();
		try {
			gl2.glPushMatrix();

			if (this.translation != null && Double.isNaN(this.translation[0])) {
				System.out.println("DisplayLists: translation[0] is NaN");
			}
			if (this.translation != null) {
				gl2.glTranslatef(this.translation[0], this.translation[1],
						this.translation[2]);
			}
			if (this.rotation != null) {
				gl2.glRotatef(this.rotation[0], this.rotation[1], this.rotation[2],
						this.rotation[3]);
			}

			if (doPreRot)   // (for multiple order bonds)
			{
/* **/
				gl2.glMultMatrixd(new double[]  {
				           preRotYcos, 0.0, preRotYsin,  0.0,
			 			   0.0, 	   1.0, 	0.0,	 0.0,
			 			   -preRotYsin, 0.0, preRotYcos, 0.0,
			 			   0.0, 	    0.0,    0.0,     1.0}, 0);
						// do pre-rotation.  The pre-rotation is about the 'y' axis.
						//
						// We have to specify a fully qualified rotation matrice for this rotation, otherwise,
						// we can get 'opposite' rotations on one half of the sphere due to the rotation angle
						// being only partially defined by the cosine in the x-z plane.  Sin and Cos fully
					// describe the rotation.
/* **/

				if (Double.isNaN(preTranslateX)) {
					System.err.println("DisplayLists: preTranslateX = NaN!");
				}
				if (preTranslateX != 0.0 && ! Double.isNaN(preTranslateX))
					gl2.glTranslated(preTranslateX, 0.0, 0.0);
						// second, do pretranslation if defined.  Translation is along the x axis,
						// plus or minus some delta
			}

			if (this.scale != null) {
				// Radius
				gl2.glScalef(this.scale[0], this.scale[1], this.scale[2]);
			}

			final StructureStyles ss = this.structureComponent.structure
					.getStructureMap().getStructureStyles();
			final GlGeometryViewer viewer = VFAppBase.sgetGlGeometryViewer();

			if (this.structureComponent.getStructureComponentType() == ComponentType.FRAGMENT) {
				// each display list represents the corresponding residue in the
				// fragment.

				if (!this.memoryReferencesAreToDisplayLists) {
					gl.glBindBuffer(GL.GL_ARRAY_BUFFER, this.normalReference);
					gl2.glNormalPointer(GL.GL_FLOAT,0, 0);
					gl.glBindBuffer(GL.GL_ARRAY_BUFFER, this.vertexReference);
					gl2.glVertexPointer(3, GL.GL_FLOAT,0, 0);
				}

				final Fragment f = (Fragment) this.structureComponent;
				final int resCount = f.getResidueCount();
				for (int i = 0; i < resCount; i++)
				{
					boolean glPush = false;
					final Residue r = f.getResidue(i);

					if (ss.isVisible(r))
					{
						try
						{
							if (isPickMode)
							{
								if (viewer.alphaBits == 0)
									gl2.glColor3ubv(this.uniqueColors[i].color, 0);
								
								else
									gl2.glColor4ubv(this.uniqueColors[i].color, 0);
							}
							
							else
							{
								glPush = true;
								gl2.glPushMatrix();
								enactMutableColor(r, gl, glu, glut);
							}					

							if (memoryReferencesAreToDisplayLists)
								gl2.glCallList(this.videoMemoryReferences[i]);
							
							else
							{
								indexArrays[i].rewind();
								gl2.glDrawRangeElements(GL.GL_TRIANGLE_STRIP, this.ranges[i][0], this.ranges[i][1], this.indexArrays[i].capacity(),
										GL.GL_UNSIGNED_INT, this.indexArrays[i]);
							}
						}
						
						catch (Exception e)
						{
							if (DebugState.isDebug())
								e.printStackTrace();
						}

						if (glPush)
							gl2.glPopMatrix();
					}
				}
			}
			
			else if (this.structureComponent.getStructureComponentType() == ComponentType.ATOM
					|| this.structureComponent.getStructureComponentType() == ComponentType.BOND
					|| this.structureComponent.getStructureComponentType() == ComponentType.SURFACE
					|| this.structureComponent instanceof LineSegment)
							// Not a good mechanism.  Should be an overrideable query to base class or
							// interface.
{
				if (isPickMode) {
					// just use the first color.
					if (viewer.alphaBits == 0) {
						gl2.glColor3ubv(this.uniqueColors[0].color, 0);
					} else {
						gl2.glColor4ubv(this.uniqueColors[0].color, 0);
					}
				} else {
					this.enactMutableColor(this.structureComponent, gl, glu, glut);
				}

				if (this.videoMemoryReferences != null) {
					for (int i = 0; i < this.videoMemoryReferences.length; i++) {
						if (this.videoMemoryReferences[i] >= 0) {
							gl2.glCallList(this.videoMemoryReferences[i]);
						}
					}
				}

			}
		} catch (Exception e)
		{
			if (DebugState.isDebug())
				e.printStackTrace();
		}

		gl2.glPopMatrix();
	}

	private void enactMutableColor(final StructureComponent sc, final GL gl,
			final GLU glu, final GLUT glut) {
		final StructureStyles ss = sc.structure.getStructureMap()
				.getStructureStyles();

		
		GL2 gl2 =gl.getGL2();
		
//		if (ss.isSelected(sc)) {
//			StructureStyles.getSelectionColor(DisplayLists.tempColorFloat);
//			// tempColorFloat[3] = .5f;
//			gl.glColor3fv(DisplayLists.tempColorFloat, 0);
//		} else 
		if (sc.getStructureComponentType() == ComponentType.RESIDUE) {
			final Residue r = (Residue) sc;

			final Fragment f = (Fragment) this.structureComponent;
			final Chain c = f.getChain();
			final ChainStyle style = (ChainStyle) ss.getStyle(c);
			style.getResidueColor(r, DisplayLists.tempColorFloat);
			// tempColorFloat[3] = .5f;
			gl2.glMaterialfv(GL.GL_FRONT, this.mutableColorType, DisplayLists.tempColorFloat, 0);
			gl2.glColor3fv(DisplayLists.tempColorFloat, 0);
		} else if (sc.getStructureComponentType() == ComponentType.ATOM) {
			final Atom a = (Atom) sc;

			final AtomStyle style = (AtomStyle) ss.getStyle(a);
			style.getAtomColor(a, DisplayLists.tempColorFloat);
			// tempColorFloat[3] = 1f;
			gl2.glMaterialfv(GL.GL_FRONT, this.mutableColorType, DisplayLists.tempColorFloat, 0);
		} else if (sc.getStructureComponentType() == ComponentType.BOND) {
			final Bond b = (Bond) sc;

			final BondStyle style = (BondStyle) ss.getStyle(b);
			if (this.isLeftSideOfBond) {
				style.getBondColor(b, DisplayLists.tempColorFloat);
			} else {
				style.getSplitBondColor(b, DisplayLists.tempColorFloat);
			}
			DisplayLists.tempColorFloat[3] = 1f;
			gl2.glMaterialfv(GL.GL_FRONT, this.mutableColorType, DisplayLists.tempColorFloat, 0);
		} else if(sc.getStructureComponentType() == ComponentType.SURFACE) {
			// TODO -pr 20100501
			final Surface s = (Surface) sc;
			final SurfaceStyle style = (SurfaceStyle) ss.getStyle(sc);
			style.getSurfaceColor(s, DisplayLists.tempColorFloat);
		} else if (sc instanceof LineSegment) {
			final LineStyle style = (LineStyle) ss.getStyle(sc);
			final float[] color = style.getColor();
			gl2.glColor3fv(color, 0);
		}
		
//		gl.glFinish();
	}

	private static final float[] tempColorFloat = { 0, 0, 0, 1f };

	public void draw(final GL gl, final GLU glu, final GLUT glut,
			final boolean isPickMode) {

		GL2 gl2 = gl.getGL2();
		
		if (this.disableLigting && !isPickMode) {
			if (GlGeometryViewer.currentProgram != 0) {
				gl2.glUseProgram(0);
			}
			gl.glDisable(GL2.GL_LIGHTING);
		}

		if (this.specularColor != null) {
			gl2.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, this.specularColor, 0);
		}
		if (this.emissiveColor != null) {
			gl2.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, this.emissiveColor, 0);
		}
		if (this.ambientColor != null) {
			gl2.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, this.ambientColor, 0);
		}
		if (this.diffuseColor != null) {
			gl2.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, this.diffuseColor, 0);
		}
		if (this.shininess != null) {
			gl2.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, this.shininess, 0);
		} 

		if (!this.memoryReferencesAreToDisplayLists) {
			gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
			gl2.glEnableClientState(GL2.GL_NORMAL_ARRAY);
		}

//		if (this.mutableColorType >= 0) {
//			gl.glColorMaterial(GL.GL_FRONT, this.mutableColorType);
//			gl.glEnable(GL.GL_COLOR_MATERIAL);
//		}

		this.drawSimple(gl, glu, glut, isPickMode);

//		if (this.mutableColorType >= 0) {
//			gl.glDisable(GL.GL_COLOR_MATERIAL);
//		}

		if (!this.memoryReferencesAreToDisplayLists) {
			gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
			gl2.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		}

		if (this.disableLigting && !isPickMode) {
			if (GlGeometryViewer.currentProgram != 0) {
				gl2.glUseProgram(GlGeometryViewer.currentProgram);
			}
			gl.glEnable(GL2.GL_LIGHTING);
		}
	}

	public void deleteVideoMemory(final GL gl, final GLU glu, final GLUT glut) {
		
		GL2 gl2 = gl.getGL2();
		
		if (this.memoryReferencesAreToDisplayLists) {
			if(this.videoMemoryReferences != null) {
				final int listCount = this.videoMemoryReferences.length;
				for (int i = 0; i < listCount; i++) {
					if (this.videoMemoryReferences[i] >= 0) {
						gl2.glDeleteLists(this.videoMemoryReferences[i], 1);
					}
				}
			}
		} else {
			DisplayLists.tmpIntBuffer.put(0, this.vertexReference);
			gl.glDeleteBuffers(1, DisplayLists.tmpIntBuffer);
			DisplayLists.tmpIntBuffer.put(0, this.normalReference);
			gl.glDeleteBuffers(1, DisplayLists.tmpIntBuffer);
		}
	}

	public void deleteUniqueColors() {
		for (int i = 0; i < this.uniqueColors.length; i++) {
			DisplayLists.uniqueColorMap.remove(this.uniqueColors[i]);
		}
	}

	public static UniqueColorMapValue getDisplayLists(final Color3b color)
	{
		return (UniqueColorMapValue) DisplayLists.uniqueColorMap.get(color);
	}

	public void startDefine(final int index, final GL gl, final GLU glu,
			final GLUT glut) {
		
		GL2 gl2 = gl.getGL2();
		
		final int list = gl2.glGenLists(1);
		this.videoMemoryReferences[index] = list;
		gl2.glNewList(list, GL2.GL_COMPILE);
	}

	public void endDefine(final GL gl, final GLU glu, final GLUT glut) {
		GL2 gl2 = gl.getGL2();
		gl2.glEndList();
	}

	public int getUniqueColorsSize() {
		return this.uniqueColors.length;
	}

	public void startDefineVertexBufferObjects(final int countIndexArrays) {

		this.indexArrays = new IntBuffer[countIndexArrays];
		this.ranges = new int[countIndexArrays][];
	}

	private static final IntBuffer tmpIntBuffer = Buffers.newDirectIntBuffer(1);

	public void defineVertexBufferObject(final GL gl, final GLU glu, final GLUT glut,
			final FloatBuffer vertices, final FloatBuffer normals, final int vertexCount) {
		this.memoryReferencesAreToDisplayLists = false;

		vertices.rewind();
		DisplayLists.tmpIntBuffer.rewind();
		gl.glGenBuffers(1, DisplayLists.tmpIntBuffer);
		this.vertexReference = DisplayLists.tmpIntBuffer.get(0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, this.vertexReference);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexCount
				* Buffers.SIZEOF_FLOAT, vertices, GL.GL_STATIC_DRAW);

		normals.rewind();
		DisplayLists.tmpIntBuffer.rewind();
		gl.glGenBuffers(1, DisplayLists.tmpIntBuffer);
		this.normalReference = DisplayLists.tmpIntBuffer.get(0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, this.normalReference);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexCount
				* Buffers.SIZEOF_FLOAT, normals, GL.GL_STATIC_DRAW);
	}
	
	public void setIndexArray(final int index, final IntBuffer array, final int[] range) {
		this.indexArrays[index] = array;
		this.ranges[index] = range;
	}

	public void endDefineVertexBufferObjects() {
		this.generateUniqueColors();
	}
}
