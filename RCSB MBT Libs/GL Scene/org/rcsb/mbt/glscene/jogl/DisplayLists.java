package org.rcsb.mbt.glscene.jogl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.TreeMap;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.glscene.surfaces.Surface;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.LineSegment;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureComponentRegistry;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.BondStyle;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.attributes.LineStyle;
import org.rcsb.mbt.model.attributes.StructureStyles;


import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;


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

	public static TreeMap uniqueColorMap = new TreeMap(); // maps names to
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

		final GlGeometryViewer viewer = AppBase.sgetGlGeometryViewer();

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
		gl.glPushMatrix();

		if (this.translation != null) {
			gl.glTranslatef(this.translation[0], this.translation[1],
					this.translation[2]);
		}
		if (this.rotation != null) {
			gl.glRotatef(this.rotation[0], this.rotation[1], this.rotation[2],
					this.rotation[3]);
		}
		if (this.scale != null) {
			// Radius
			gl.glScalef(this.scale[0], this.scale[1], this.scale[2]);
		}

		final StructureStyles ss = this.structureComponent.structure
				.getStructureMap().getStructureStyles();
		final GlGeometryViewer viewer = AppBase.sgetGlGeometryViewer();

		if (this.structureComponent.getStructureComponentType() == StructureComponentRegistry.TYPE_FRAGMENT) {
			// each display list represents the corresponding residue in the
			// fragment.

			if (!this.memoryReferencesAreToDisplayLists) {
				gl.glBindBuffer(GL.GL_ARRAY_BUFFER, this.normalReference);
				gl.glNormalPointer(GL.GL_FLOAT,0, 0);
				gl.glBindBuffer(GL.GL_ARRAY_BUFFER, this.vertexReference);
				gl.glVertexPointer(3, GL.GL_FLOAT,0, 0);
			}

			final Fragment f = (Fragment) this.structureComponent;
			final int resCount = f.getResidueCount();
			for (int i = 0; i < resCount; i++) {
				final Residue r = f.getResidue(i);

				if (ss.isVisible(r)) {

					if (isPickMode) {
						if (viewer.alphaBits == 0) {
							gl.glColor3ubv(this.uniqueColors[i].color, 0);
						} else {
							gl.glColor4ubv(this.uniqueColors[i].color, 0);
						}
					} else {
						gl.glPushMatrix();
						this.enactMutableColor(r, gl, glu, glut);
					}

					if (this.memoryReferencesAreToDisplayLists) {
						gl.glCallList(this.videoMemoryReferences[i]);
					} else {
						this.indexArrays[i].rewind();
						gl.glDrawRangeElements(GL.GL_TRIANGLE_STRIP, this.ranges[i][0], this.ranges[i][1], this.indexArrays[i].capacity(),
								GL.GL_UNSIGNED_INT, this.indexArrays[i]);
					}
					
					if(!isPickMode) {
						gl.glPopMatrix();
					}
				}
			}
		}
		
		else if (this.structureComponent.getStructureComponentType() == StructureComponentRegistry.TYPE_ATOM
				|| this.structureComponent.getStructureComponentType() == StructureComponentRegistry.TYPE_BOND
				|| this.structureComponent.getStructureComponentType() == Surface.COMPONENT_TYPE
				|| this.structureComponent instanceof LineSegment)
						// Not a good mechanism.  Should be an overrideable query to base class or
						// interface.
	{
			if (isPickMode) {
				// just use the first color.
				if (viewer.alphaBits == 0) {
					gl.glColor3ubv(this.uniqueColors[0].color, 0);
				} else {
					gl.glColor4ubv(this.uniqueColors[0].color, 0);
				}
			} else {
				this.enactMutableColor(this.structureComponent, gl, glu, glut);
			}

			if (this.videoMemoryReferences != null) {
				for (int i = 0; i < this.videoMemoryReferences.length; i++) {
					if (this.videoMemoryReferences[i] >= 0) {
						gl.glCallList(this.videoMemoryReferences[i]);
					}
				}
			}

		}

		gl.glPopMatrix();
	}

	private void enactMutableColor(final StructureComponent sc, final GL gl,
			final GLU glu, final GLUT glut) {
		final StructureStyles ss = sc.structure.getStructureMap()
				.getStructureStyles();

//		if (ss.isSelected(sc)) {
//			StructureStyles.getSelectionColor(DisplayLists.tempColorFloat);
//			// tempColorFloat[3] = .5f;
//			gl.glColor3fv(DisplayLists.tempColorFloat, 0);
//		} else 
		if (sc.getStructureComponentType() == StructureComponentRegistry.TYPE_RESIDUE) {
			final Residue r = (Residue) sc;

			final Fragment f = (Fragment) this.structureComponent;
			final Chain c = f.getChain();
			final ChainStyle style = (ChainStyle) ss.getStyle(c);
			style.getResidueColor(r, DisplayLists.tempColorFloat);
			// tempColorFloat[3] = .5f;
			gl.glMaterialfv(GL.GL_FRONT, this.mutableColorType, DisplayLists.tempColorFloat, 0);
			gl.glColor3fv(DisplayLists.tempColorFloat, 0);
		} else if (sc.getStructureComponentType() == StructureComponentRegistry.TYPE_ATOM) {
			final Atom a = (Atom) sc;

			final AtomStyle style = (AtomStyle) ss.getStyle(a);
			style.getAtomColor(a, DisplayLists.tempColorFloat);
			// tempColorFloat[3] = 1f;
			gl.glMaterialfv(GL.GL_FRONT, this.mutableColorType, DisplayLists.tempColorFloat, 0);
		} else if (sc.getStructureComponentType() == StructureComponentRegistry.TYPE_BOND) {
			final Bond b = (Bond) sc;

			final BondStyle style = (BondStyle) ss.getStyle(b);
			if (this.isLeftSideOfBond) {
				style.getBondColor(b, DisplayLists.tempColorFloat);
			} else {
				style.getSplitBondColor(b, DisplayLists.tempColorFloat);
			}
			DisplayLists.tempColorFloat[3] = 1f;
			gl.glMaterialfv(GL.GL_FRONT, this.mutableColorType, DisplayLists.tempColorFloat, 0);
		} else if(sc.getStructureComponentType() == Surface.COMPONENT_TYPE) {
			gl.glColor3fv(Constants.transparentWhite, 0);
		} else if (sc instanceof LineSegment) {
			final LineStyle style = (LineStyle) ss.getStyle(sc);
			final float[] color = style.getColor();
			gl.glColor3fv(color, 0);
		}
		
//		gl.glFinish();
	}

	private static final float[] tempColorFloat = { 0, 0, 0, 1f };

	public void draw(final GL gl, final GLU glu, final GLUT glut,
			final boolean isPickMode) {

		final GlGeometryViewer viewer = AppBase.sgetGlGeometryViewer();

		if (this.disableLigting && !isPickMode) {
			if (GlGeometryViewer.currentProgram != 0) {
				gl.glUseProgram(0);
			}
			gl.glDisable(GL.GL_LIGHTING);
		}

		if (this.specularColor != null) {
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, this.specularColor, 0);
		}
		if (this.emissiveColor != null) {
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, this.emissiveColor, 0);
		}
		if (this.ambientColor != null) {
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, this.ambientColor, 0);
		}
		if (this.diffuseColor != null) {
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, this.diffuseColor, 0);
		}
		if (this.shininess != null) {
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, this.shininess, 0);
		} 

		if (!this.memoryReferencesAreToDisplayLists) {
			gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
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
			gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
		}

		if (this.disableLigting && !isPickMode) {
			if (GlGeometryViewer.currentProgram != 0) {
				gl.glUseProgram(GlGeometryViewer.currentProgram);
			}
			gl.glEnable(GL.GL_LIGHTING);
		}
	}

	public void deleteVideoMemory(final GL gl, final GLU glu, final GLUT glut) {
		if (this.memoryReferencesAreToDisplayLists) {
			if(this.videoMemoryReferences != null) {
				final int listCount = this.videoMemoryReferences.length;
				for (int i = 0; i < listCount; i++) {
					if (this.videoMemoryReferences[i] >= 0) {
						gl.glDeleteLists(this.videoMemoryReferences[i], 1);
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

	public static UniqueColorMapValue getDisplayLists(final Color3b color) {
		return (UniqueColorMapValue) DisplayLists.uniqueColorMap.get(color);
	}

	public void startDefine(final int index, final GL gl, final GLU glu,
			final GLUT glut) {
		final int list = gl.glGenLists(1);
		this.videoMemoryReferences[index] = list;
		gl.glNewList(list, GL.GL_COMPILE);
	}

	public void endDefine(final GL gl, final GLU glu, final GLUT glut) {
		gl.glEndList();
	}

	public int getUniqueColorsSize() {
		return this.uniqueColors.length;
	}

	public void startDefineVertexBufferObjects(final int countIndexArrays) {

		this.indexArrays = new IntBuffer[countIndexArrays];
		this.ranges = new int[countIndexArrays][];
	}

	private static final IntBuffer tmpIntBuffer = BufferUtil.newIntBuffer(1);

	public void defineVertexBufferObject(final GL gl, final GLU glu, final GLUT glut,
			final FloatBuffer vertices, final FloatBuffer normals, final int vertexCount) {
		this.memoryReferencesAreToDisplayLists = false;

		vertices.rewind();
		DisplayLists.tmpIntBuffer.rewind();
		gl.glGenBuffers(1, DisplayLists.tmpIntBuffer);
		this.vertexReference = DisplayLists.tmpIntBuffer.get(0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, this.vertexReference);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexCount
				* BufferUtil.SIZEOF_FLOAT, vertices, GL.GL_STATIC_DRAW);

		normals.rewind();
		DisplayLists.tmpIntBuffer.rewind();
		gl.glGenBuffers(1, DisplayLists.tmpIntBuffer);
		this.normalReference = DisplayLists.tmpIntBuffer.get(0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, this.normalReference);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, vertexCount
				* BufferUtil.SIZEOF_FLOAT, normals, GL.GL_STATIC_DRAW);
	}
	
	public void setIndexArray(final int index, final IntBuffer array, final int[] range) {
		this.indexArrays[index] = array;
		this.ranges[index] = range;
	}

	public void endDefineVertexBufferObjects() {
		this.generateUniqueColors();
	}
}
