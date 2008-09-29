package org.rcsb.mbt.glscene.jogl;

//  $Id: GlGeometryViewer.java,v 1.6 2007/07/02 04:29:08 jbeaver Exp $
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
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.//
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
//  $Log: GlGeometryViewer.java,v $
//  Revision 1.6  2007/07/02 04:29:08  jbeaver
//  *** empty log message ***
//
//  Revision 1.5  2007/04/09 22:36:41  jbeaver
//  *** empty log message ***
//
//  Revision 1.4  2007/03/01 00:41:50  jbeaver
//  *** empty log message ***
//
//  Revision 1.3  2007/02/15 21:53:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.2  2007/02/09 13:18:37  jbeaver
//  fixed black protein bug and miscolored protein bug
//
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.7  2007/01/06 19:47:53  jbeaver
//  *** empty log message ***
//
//  Revision 1.6  2007/01/03 19:33:49  jbeaver
//  *** empty log message ***
//
//  Revision 1.5  2006/10/08 17:19:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.4  2006/10/04 17:21:06  jbeaver
//  Lots of changes from surfaces to improved picking
//
//  Revision 1.3  2006/09/21 20:56:01  jbeaver
//  *** empty log message ***
//
//  Revision 1.2  2006/09/20 22:52:48  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.3  2006/09/06 04:46:12  jbeaver
//  Added initial capability for labeling ribbons
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.13  2006/07/18 21:06:38  jbeaver
//  *** empty log message ***
//
//  Revision 1.12  2006/06/21 21:00:53  jbeaver
//  fixed fog so it works with states
//
//  Revision 1.11  2006/06/08 23:52:16  jbeaver
//  added partial surface translation (edu.utexas.*)
//
//  Revision 1.10  2006/06/06 23:30:24  psteeger
//  hooked fog to protein
//
//  Revision 1.9  2006/06/06 22:06:34  jbeaver
//  *** empty log message ***
//
//  Revision 1.8  2006/06/02 23:41:24  jbeaver
//  *** empty log message ***
//
//  Revision 1.7  2006/05/30 09:43:44  jbeaver
//  Added lines and fog
//
//  Revision 1.6  2006/05/16 17:57:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.5  2006/04/14 23:37:33  jbeaver
//  Update with some (very broken) surface rendering stuff
//
//  Revision 1.4  2006/03/25 02:11:22  jbeaver
//  *** empty log message ***
//
//  Revision 1.3  2006/03/24 06:25:55  jbeaver
//  Added a state interpolation feature
//
//  Revision 1.2  2006/03/13 15:06:23  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.0 2005/04/04 00:12:54  moreland
//

// CORE JAVA
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JPanel;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.scene.PdbToNdbConverter;
import org.rcsb.mbt.controllers.scene.SceneController;
import org.rcsb.mbt.controllers.update.IUpdateListener;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.mbt.glscene.jogl.tiles.TileRenderer;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureComponentRegistry;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.BondStyle;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.attributes.StructureStylesEvent;
import org.rcsb.mbt.model.attributes.StructureStylesEventListener;
import org.rcsb.mbt.model.util.Algebra;
import org.rcsb.mbt.model.util.Status;



import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.ImageUtil;


/**
 * Define the GlGeometryViewer class.
 * <P>
 * 
 * @author John L. Moreland
 * @copyright SDSC
 * @see
 */
public class GlGeometryViewer extends JPanel implements GLEventListener,
		MouseListener, MouseMotionListener, IUpdateListener,
		WindowListener, StructureStylesEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 229310376730362252L;

	// OpenGL rendering environment
	public GLCanvas glCanvas = null;

	private GLCapabilities glCapabilities = null;
	
	protected boolean do_glFinishInShaders = false;

//	public void setVisible(boolean vis) {
//		System.err.flush();
//	}
	
	// private Animator animator = null;

	private final GLUT glut = new GLUT();

	private final GLU glu = new GLU();

	private boolean backBufferContainsPickData = false;

	GLAutoDrawable drawable = null;

	public List<DisplayListRenderable> renderablesToDestroy = Collections.synchronizedList(new ArrayList<DisplayListRenderable>());

	public List<Integer> simpleDisplayListsToDestroy = Collections.synchronizedList(new ArrayList<Integer>());

	// Default geometry for new Renderables defaultGeometry{scType} = Geometry
	public static Hashtable<String, DisplayListGeometry> defaultGeometry = new Hashtable<String, DisplayListGeometry>();

	// Mouse state
	protected int prevMouseX, prevMouseY;

	private ByteBuffer rgbaBuffer = null;

	private final Color3b tempColor = new Color3b();

	private final int viewport[] = new int[4];

	private double aspect = 1.0f;

	// VirtualSphere2 virtualSphere = new VirtualSphere2(150, 150, 150);

	// Lighting
	protected float lightPosition[] = { 0.0f, 0.0f, 0.0f, 1.0f };

	// Basic reusable colors
	public static final float black[] = { 0.0f, 0.0f, 0.0f, 0.0f };

	public static final float white[] = { 1.0f, 1.0f, 1.0f, 0.0f };

	public static final float yellow[] = { 1.0f, 1.0f, 0.7f, 0.0f };

	public static final float gray[] = { 0.5f, 0.5f, 0.5f, 0.0f };

	public static final float red[] = { 1.0f, 0.0f, 0.0f, 0.0f };

	public static final float green[] = { 0.0f, 1.0f, 0.0f, 0.0f };

	public static final float blue[] = { 0.0f, 0.0f, 1.0f, 0.0f };

	public static final float[] DEFAULT_SPECULAR_COLOR = GlGeometryViewer.black;

	public static final float[] DEFAULT_EMISSION_COLOR = GlGeometryViewer.black;

	public static final float[] DEFAULT_AMBIENT_COLOR = GlGeometryViewer.black;

	public static final float[] DEFAULT_DIFFUSE_COLOR = GlGeometryViewer.black;

	// Frame rate and other drawing statistics
	private long lastTime = System.currentTimeMillis();

	private int frameCount = 0;

	private double fps = 0.0f;

	private boolean isScreenshotRequested = false;

	private BufferedImage screenshot = null;

	private int screenshotWidth = 1000;

	private int screenshotHeight = 2000;

	protected boolean screenshotFailed = false;

	public int tileHeight = -1;

	public int tileWidth = -1;

	public static AtomStyle atomStyle = null;

	public static BondStyle bondStyle = null;

	public static ChainStyle chainStyle = null;

	public static AtomGeometry atomGeometry = null;

	public static BondGeometry bondGeometry = null;

	public static ChainGeometry chainGeometry = null;

	protected VirtualSphere2 virtualSphere = new VirtualSphere2(150, 150, 150);

	// public static Hashtable sharedLabelDisplayLists = new Hashtable( );

	public float backgroundColor[] = { 0.0f, 0.0f, 0.0f, 0.0f };

	// -----------------------------------------------------------------------------

	public static int vertexShader = 0;

	public static int fragmentShader = 0;

	public static int shaderProgram = 0;

	public static int currentProgram = 0;
	
	public double[] viewEye = { Double.NaN, Double.NaN, Double.NaN };

	public double[] viewCenter = { Double.NaN, Double.NaN, Double.NaN };

	public double[] viewUp = { Double.NaN, Double.NaN, Double.NaN };

	public double[] rotationCenter = { Double.NaN, Double.NaN, Double.NaN };
	
	public double[][] bounds = null;
	
//	private PrintStream debugOut = null;
	
	/**
	 * Move the camera to the specified eye point and orient the view toward the
	 * specified 3D center point with a given up vector. This provides complete
	 * camera control.
	 */
	public void lookAt(final double[] eye, final double[] center,
			final double[] up) {
		// Set the new eye location.
		this.viewEye[0] = eye[0];
		this.viewEye[1] = eye[1];
		this.viewEye[2] = eye[2];

//		final double zoomDistance = Algebra.distance(this.viewCenter, center);

		// Set the new view center point.
		this.viewCenter[0] = center[0];
		this.viewCenter[1] = center[1];
		this.viewCenter[2] = center[2];

		// Set the new rotation center point.
		this.rotationCenter[0] = center[0];
		this.rotationCenter[1] = center[1];
		this.rotationCenter[2] = center[2];

		// Set the new view up vector.
		this.viewUp[0] = up[0];
		this.viewUp[1] = up[1];
		this.viewUp[2] = up[2];

		// double sign = viewCenter[0] + viewCenter[1] + viewCenter[2] <
		// center[0] + center[1] + center[2] ? -1 : 1;
		// zoomDistance *= sign;
		// fogStart += zoomDistance;
		// fogEnd += zoomDistance;

		requestRepaint();
	}

	public void lookAt(final float[] eye, final float[] center, final float[] up) {
		// Set the new eye location.
		this.viewEye[0] = eye[0];
		this.viewEye[1] = eye[1];
		this.viewEye[2] = eye[2];

		// Set the new view center point.
		this.viewCenter[0] = center[0];
		this.viewCenter[1] = center[1];
		this.viewCenter[2] = center[2];
		// Set the new rotation center point.
		this.rotationCenter[0] = center[0];
		this.rotationCenter[1] = center[1];
		this.rotationCenter[2] = center[2];

		// Set the new view up vector.
		this.viewUp[0] = up[0];
		this.viewUp[1] = up[1];
		this.viewUp[2] = up[2];

		requestRepaint();
	}

	/**
	 * Rotate and refocus the camera at the specified 3D point, but keep the
	 * current eye location and up orientation of the camera. This provides a
	 * "turn your head and eyes toward an object" behavior. The new up-vector is
	 * computed automatically.
	 */
	public void lookAt(final double[] center) {
		this.lookAt(this.viewEye, center);
	}

	public void lookAt(final float[] center) {
		this.lookAt(this.viewEye, center);
	}

	/**
	 * Move the camera to the specified eye point and orient the view toward the
	 * specified 3D center point. This provides a "move your body to a point in
	 * space and look at an object" behavior. The new up-vector is computed
	 * automatically.
	 */
	public void lookAt(final double[] eye, final double[] center) {
		if (eye.length < 3) {
			throw new IllegalArgumentException("eye.length < 3");
		}
		if (center.length < 3) {
			throw new IllegalArgumentException("center.length < 3");
		}

		// We need to compute a new viewUp and but we need to ensure that it
		// is both perpendicular (orthonormal) to the new viewDirection and
		// also oriented in a "similar" direction to the old viewUp.
		// To do so, we project one vector onto the other and subtract
		// (Graham-Schmidt orthogonalization).

		final double newViewDirection[] = { center[0] - eye[0],
				center[1] - eye[1], center[2] - eye[2] };
		Algebra.normalizeVector(newViewDirection);

		// Use Graham-Schmidt orthogonalization: e3 = e2 - e1 * ( e2 DOT e1 )
		// to produce a newViewUp vector that is orthonormal to the
		// new view direction by starting with the old viewUp vector:
		//
		// e1 = newViewDirection
		// e2 = viewUp
		// e3 = newViewUp
		//
		// Note that the dot product of two orthonormal vectors is 0,
		// so it is safe to re-orthogonalize since the orthogonal vector
		// component will be the zero vector and you will be left with the
		// original input vector.
		Algebra.normalizeVector(this.viewUp);
		final double dot = Algebra.dotProduct(this.viewUp, newViewDirection);
		final double newViewUp[] = {
				this.viewUp[0] - newViewDirection[0] * dot,
				this.viewUp[1] - newViewDirection[1] * dot,
				this.viewUp[2] - newViewDirection[2] * dot };
		Algebra.normalizeVector(newViewUp);

		// Set the new eye location.
		this.viewEye[0] = eye[0];
		this.viewEye[1] = eye[1];
		this.viewEye[2] = eye[2];

		// Set the new view center point.
		this.viewCenter[0] = center[0];
		this.viewCenter[1] = center[1];
		this.viewCenter[2] = center[2];
		// Set the new rotation center point.
		this.rotationCenter[0] = center[0];
		this.rotationCenter[1] = center[1];
		this.rotationCenter[2] = center[2];

		// Set the new view up vector.
		this.viewUp[0] = newViewUp[0];
		this.viewUp[1] = newViewUp[1];
		this.viewUp[2] = newViewUp[2];

		requestRepaint();
	}

	/**
	 * Returns the array describing the camera's orientation in the scene.
	 * Changing the contents of this array will have an immediate effect on the
	 * camera.
	 */
	public double[] getEye()
	{
		final double[] eye = new double[this.viewEye.length];
		for (int i = 0; i < eye.length; i++)
			eye[i] = this.viewEye[i];

		return eye;
	}

	/**
	 * Returns the array describing the camera's position in the scene. Changing
	 * the contents of this array will have an immediate effect on the camera.
	 */
	public double[] getCenter() {
		final double[] center = new double[this.viewCenter.length];
		for (int i = 0; i < center.length; i++) {
			center[i] = this.viewCenter[i];
		}

		return center;
	}

	/**
	 * Returns the array describing the camera's up vector in the scene.
	 * Changing the contents of this array will have an immediate effect on the
	 * camera.
	 */
	public double[] getUp() {
		final double[] up = new double[this.viewUp.length];
		for (int i = 0; i < up.length; i++) {
			up[i] = this.viewUp[i];
		}

		return up;
	}

	public void lookAt(final double[] eye, final float[] center) {
		if (eye.length < 3) {
			throw new IllegalArgumentException("eye.length < 3");
		}
		if (center.length < 3) {
			throw new IllegalArgumentException("center.length < 3");
		}

		// We need to compute a new viewUp and but we need to ensure that it
		// is both perpendicular (orthonormal) to the new viewDirection and
		// also oriented in a "similar" direction to the old viewUp.
		// To do so, we project one vector onto the other and subtract
		// (Graham-Schmidt orthogonalization).

		final double newViewDirection[] = { center[0] - eye[0],
				center[1] - eye[1], center[2] - eye[2] };
		Algebra.normalizeVector(newViewDirection);

		// Use Graham-Schmidt orthogonalization: e3 = e2 - e1 * ( e2 DOT e1 )
		// to produce a newViewUp vector that is orthonormal to the
		// new view direction by starting with the old viewUp vector:
		//
		// e1 = newViewDirection
		// e2 = viewUp
		// e3 = newViewUp
		//
		// Note that the dot product of two orthonormal vectors is 0,
		// so it is safe to re-orthogonalize since the orthogonal vector
		// component will be the zero vector and you will be left with the
		// original input vector.
		Algebra.normalizeVector(this.viewUp);
		final double dot = Algebra.dotProduct(this.viewUp, newViewDirection);
		final double newViewUp[] = {
				this.viewUp[0] - newViewDirection[0] * dot,
				this.viewUp[1] - newViewDirection[1] * dot,
				this.viewUp[2] - newViewDirection[2] * dot };
		Algebra.normalizeVector(newViewUp);

		// Set the new eye location.
		this.viewEye[0] = eye[0];
		this.viewEye[1] = eye[1];
		this.viewEye[2] = eye[2];

		// Set the new view center point.
		this.viewCenter[0] = center[0];
		this.viewCenter[1] = center[1];
		this.viewCenter[2] = center[2];
		// Set the new rotation center point.
		this.rotationCenter[0] = center[0];
		this.rotationCenter[2] = center[2];

		// Set the new view up vector.
		this.viewUp[0] = newViewUp[0];
		this.viewUp[1] = newViewUp[1];
		this.viewUp[2] = newViewUp[2];

		requestRepaint();
	}

	//
	// Constructors
	//

	/**
	 * Construct an instance of the class.
	 */
	public GlGeometryViewer() {
		super(new BorderLayout());
		super.setBackground(Color.black);
		
		GlGeometryViewer.atomGeometry = new AtomGeometry();
		// atomGeometry.setForm( Geometry.FORM_LINES ); // JLM DEBUG
		GlGeometryViewer.defaultGeometry.put(Atom.getClassName(),
				GlGeometryViewer.atomGeometry);
		GlGeometryViewer.bondGeometry = new BondGeometry();
		GlGeometryViewer.bondGeometry.setShowOrder(true);
		// bondGeometry.setForm( Geometry.FORM_LINES ); // JLM DEBUG
		// bondGeometry.setShowOrder( true ); // JLM DEBUG
		GlGeometryViewer.defaultGeometry.put(Bond.getClassName(),
				GlGeometryViewer.bondGeometry);
		GlGeometryViewer.chainGeometry = new ChainGeometry();
		GlGeometryViewer.chainGeometry.setForm(Geometry.FORM_THICK); // JLM
		// DEBUG
		GlGeometryViewer.defaultGeometry.put(Chain.getClassName(),
				GlGeometryViewer.chainGeometry);

		AppBase.sgetSceneController().setDefaultGeometry(
				GlGeometryViewer.defaultGeometry);
		
		// Set up a GL Canvas
		this.glCapabilities = new GLCapabilities();
		// glCapabilities.setAccumBlueBits(16);
		// glCapabilities.setAccumRedBits(16);
		// glCapabilities.setAccumGreenBits(16);
		// glCapabilities.setAccumAlphaBits(16);
		this.glCapabilities.setNumSamples(6);
		this.glCapabilities.setSampleBuffers(true);

		this.glCanvas = new GLCanvas(this.glCapabilities) {
			private static final long serialVersionUID = 293707014622741877L;

			@Override
			public void addNotify() {
				super.setVisible(true);
				super.addNotify();
			}
			
			@Override
			public void removeNotify() {
				super.setVisible(false);
			}
		};/*
															 * GLDrawableFactory.getFactory()
															 * .createExternalGLDrawable().createGLCanvas(glCapabilities);
															 */
		// ** this wrapper panel is used to bypass a current Jogl (I think) bug.
		// If not used, the glCanvas causes the JSplitPane to resize strangely.
		// Not sure why the bug occurs or why this fixes it.
		final JPanel wrapperPanel = new JPanel() {
			private static final long serialVersionUID = 7973626073594754645L;

			{
				this.setLayout(new BorderLayout());
				this.setMinimumSize(new Dimension(100, 100));
				super.setBackground(Color.black);
			}
		};
		this.glCanvas.addGLEventListener(this);

		wrapperPanel.add(this.glCanvas);
		super.add(wrapperPanel);
		this.glCanvas.setFocusable(false);

		// Start rendering
		// animator = new Animator( glCanvas );
		// animator = new FPSAnimator(glCanvas, 100);
		// animator.start();
		// animator.stop();

		AppBase.sgetUpdateController().registerListener(this);
	}

	public void invalidateAllGeometry()
	{
		for(Structure struct : AppBase.sgetModel().getStructures())
		{
			JoglSceneNode.RenderablesMap map = struct.getStructureMap().getSceneNode().renderables;
			if(!map.isEmpty()) {
				synchronized(map) {
					Iterator<DisplayListRenderable> valIt = map.values().iterator();
					while(valIt.hasNext()) {
						DisplayListRenderable rend = (DisplayListRenderable)valIt.next();
						rend.setDirty();
					}
				}
			}
		}
		
		GlGeometryViewer.currentProgram = 0;
		GlGeometryViewer.vertexShader = 0;
		GlGeometryViewer.fragmentShader = 0;
		GlGeometryViewer.shaderProgram = 0;
		
		this.backBufferContainsPickData = false;
	}
	
	// -----------------------------------------------------------------------------

	//
	// GLEventListener interface.
	//

	// values queried from opengl
	public int redBits = 0;

	public int greenBits = 0;

	public int blueBits = 0;

	public int alphaBits = 0;

	public int maxRedValue = 0;

	public int maxGreenValue = 0;

	public int maxBlueValue = 0;

	public int maxAlphaValue = 0;

	public int colorCount = 0;

	public IntBuffer tmpIntBufferOne = BufferUtil.newIntBuffer(1);

	public boolean supportsShaderPrograms = false;

	/**
	 * GLEventListener interface - init method: Initialize the GlGeometryViewer
	 * class.
	 */
	public void init(final GLAutoDrawable drawable)
	{
		this.drawable = drawable;

		drawable.setGL(new DebugGL(drawable.getGL()));

//		 drawable.setGL(new TraceGL(drawable.getGL(), debugOut));

		//
//		 Set up JOGL (OpenGL)
		//

		final GL gl = drawable.getGL();

		// gl.glEnable(GL.GL_POLYGON_SMOOTH);
		// gl.glSampleCoverage(.1f, true);

		// check for supported features...
		gl.glGetIntegerv(GL.GL_RED_BITS, this.tmpIntBufferOne);
		this.redBits = this.tmpIntBufferOne.get(0);
		gl.glGetIntegerv(GL.GL_GREEN_BITS, this.tmpIntBufferOne);
		this.greenBits = this.tmpIntBufferOne.get(0);
		gl.glGetIntegerv(GL.GL_BLUE_BITS, this.tmpIntBufferOne);
		this.blueBits = this.tmpIntBufferOne.get(0);
		gl.glGetIntegerv(GL.GL_ALPHA_BITS, this.tmpIntBufferOne);
		this.alphaBits = this.tmpIntBufferOne.get(0);

		// for efficiency...
		this.maxRedValue = (int) Math.pow(2, this.redBits) - 1;
		this.maxGreenValue = (int) Math.pow(2, this.greenBits) - 1;
		this.maxBlueValue = (int) Math.pow(2, this.blueBits) - 1;
		this.maxAlphaValue = (int) Math.pow(2, this.alphaBits) - 1;
		this.colorCount = (int) Math.pow(2, this.redBits + this.greenBits + this.blueBits
				+ this.alphaBits);

		// Lighting
		gl.glEnable(GL.GL_LIGHTING);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, new float[] { 0.8f, 0.8f,
				0.8f, 1.0f }, 0);
		gl.glEnable(GL.GL_LIGHT0);

		// Rendering
		gl.glEnable(GL.GL_NORMALIZE); 
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glShadeModel(GL.GL_SMOOTH);

		checkSetShaderSupport();

		gl.glLineWidth(3.0f);
		gl.glPointSize(10f);
		gl.glCullFace(GL.GL_BACK);
		gl.glPolygonMode(GL.GL_FRONT, GL.GL_FILL);

		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		//
		// Add mouse listeners
		//
		drawable.addMouseListener(this);
		drawable.addMouseMotionListener(this);

		drawable.setAutoSwapBufferMode(false);
	}

	int viewportWidth = 0;

	int viewportHeight = 0;

	public void setBackgroundColor(final float red, final float green,
			final float blue, final float alpha) {
		this.backgroundColor[0] = red;
		this.backgroundColor[1] = green;
		this.backgroundColor[2] = blue;
		this.backgroundColor[3] = alpha;
	}

	public void getBackgroundColor(final float[] copy) {
		copy[0] = this.backgroundColor[0];
		copy[1] = this.backgroundColor[1];
		copy[2] = this.backgroundColor[2];
		copy[3] = this.backgroundColor[3];
	}

	/**
	 * GLEventListener interface - reshape method:
	 */
	public void reshape(final GLAutoDrawable drawable, final int x,
			final int y, final int width, final int height) {
		try {
			this.viewportWidth = width;
			this.viewportHeight = height;

			final double w = width;
			final double h = height;

			final GL gl = drawable.getGL();

			gl.glGetIntegerv(GL.GL_VIEWPORT, this.viewport, 0);
			this.aspect = h / w;

			final int iw = (int) (w / 2.0);
			final int ih = (int) (h / 2.0);
			final int ir = (int) (0.9 * Math.min(iw, ih));
			this.virtualSphere.setCircle(iw, ih, ir);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		this.requestRepaint();
	}

	double rotate = 0;

	public boolean needsRepaint = true;

	public void requestRepaint() {
		this.needsRepaint = true;
		if (this.drawable != null && !AppBase.backgroundScreenshotOnly) {
			this.drawable.repaint();
		}
	}

	private boolean needsPick = false;

	private static final double fovy = 45.0;

	private static final double zNear = 1.0; // 1.0, 0.1

	private static final double zFar = 10000.0; // 80.0, 10000.0

	public void requestPick() {
		this.needsPick = true;
		if (this.drawable != null && !AppBase.backgroundScreenshotOnly) {
			this.drawable.repaint();
		}
	}

	public void requestPickAndRepaint() {
		this.needsPick = true;
		this.needsRepaint = true;
		if (this.drawable != null && !AppBase.backgroundScreenshotOnly) {
			this.drawable.repaint();
		}
	}

	/**
	 * GLEventListener interface - display method:
	 */
	public void display(final GLAutoDrawable drawable) {
		try {
			// if (!this.needsRepaint && !this.needsPick) {
			// System.out.print("N");
			// }
			// if(this.needsRepaint) System.out.print("R");
			// if(this.needsPick) System.out.print("P");

			// if(!this.needsPick) {
			// System.err.flush();
			// }

			final GL gl = drawable.getGL();

			final SceneController sceneController = AppBase.sgetSceneController();

			if (this.needsPick && !this.isScreenshotRequested) { // needs to
				// be called
				// twice in
				// the case
				// of a pick
				// event.
				this.drawOrPick(gl, this.glu, this.glut, drawable, true, true,
						true);
			} else { // draw

				if (sceneController.isDebugEnabled()
						&& sceneController.getDebugSettings().isAntialiasingEnabled) {
					gl.glClearAccum(this.backgroundColor[0],
							this.backgroundColor[1], this.backgroundColor[2],
							this.backgroundColor[3]);
					gl.glClear(GL.GL_ACCUM_BUFFER_BIT);

					gl.glMatrixMode(GL.GL_MODELVIEW);
					gl.glLoadIdentity();

					for (int jitter = 0; jitter < sceneController.getDebugSettings().JITTER_ARRAY.length; jitter++) {
						JoglUtils
								.accPerspective(
										gl,
										this.glu,
										this.glut,
										50f,
										1.0 / this.aspect,
										GlGeometryViewer.zNear,
										GlGeometryViewer.zFar,
										0,
										0,
										sceneController.getDebugSettings().blurFactor
												* sceneController.getDebugSettings().JITTER_ARRAY[jitter][0],
										sceneController.getDebugSettings().blurFactor
												* sceneController.getDebugSettings().JITTER_ARRAY[jitter][1],
										5f);
						// JoglUtils.accPerspective(gl, glu, glut, fovy, 1.0 /
						// aspect, zNear, zFar, 0, 0,
						// Model.getSingleton().getDebugSettings().blurFactor *
						// Model.getSingleton().getDebugSettings().JITTER_ARRAY[jitter][0],
						// Model.getSingleton().getDebugSettings().blurFactor *
						// Model.getSingleton().getDebugSettings().JITTER_ARRAY[jitter][1],
						// Model.getSingleton().getDebugSettings().blurPlane);
						this.drawOrPick(gl, this.glu, this.glut, drawable,
								false, false, false);
						// gl.glFlush();
						gl
								.glAccum(GL.GL_ACCUM, 1f / sceneController
										.getDebugSettings().JITTER_ARRAY.length);
						gl.glFlush();
					}
					gl.glAccum(GL.GL_RETURN, 1);
				} else {
					if (this.needsRepaint
							|| (!this.needsPick && !this.needsRepaint)) {
						this.drawOrPick(gl, this.glu, this.glut, drawable,
								true, true, false);
						drawable.swapBuffers();
					}
				}
			}

			this.needsRepaint = this.needsPick = false;

			/*
			 * int error = gl.glGetError(); if(error != 0) {
			 * System.err.println("OpenGL error code: " + error); }
			 */
			/* **
			 * not using AccessableSurfaceGeometry...
			if (!this.hasRenderedAtLeastOnce) {
				System.err.println("*** vertex count: "
						+ Extrusion.VERTEX_COUNT + "; vertex cache hits: "
						+ Extrusion.VERTEX_CACHE_HITS + " ***");
				System.err.println("*** toroid count: "
						+ AccessableSurfaceGeometry.TOTAL_TOROID_PATCHES + "; toroid cache hits: "
						+ AccessableSurfaceGeometry.TOROID_CACHE_HITS + " ***");
				System.err.println("*** concave count: "
						+ AccessableSurfaceGeometry.TOTAL_CONCAVE_PATCHES + "; concave cache hits: "
						+ AccessableSurfaceGeometry.CONCAVE_CACHE_HITS + " ***");
			}
			*/

			this.hasRenderedAtLeastOnce = true;

		} catch (final Exception e) {
			e.printStackTrace();
			System.err.flush();

			this.screenshotFailed = true;
		}
		
//		debugOut.println("***End of display()...\n\n");
//		debugOut.flush();
	}

	public void drawOrPick(final GL gl, final GLU glu, final GLUT glut,
			final GLAutoDrawable drawable,
			final boolean canModifyProjectionMatrix,
			final boolean canClearModelviewMatrix, final boolean isPick) {
		if (this.isScreenshotRequested) {

			final int oldViewportWidth = this.viewportWidth;
			final int oldViewportHeight = this.viewportHeight;
			this.reshape(drawable, 0, 0, this.screenshotWidth,
					this.screenshotHeight);

			final int bufImgType = BufferedImage.TYPE_3BYTE_BGR;

			final BufferedImage image = new BufferedImage(this.screenshotWidth,
					this.screenshotHeight, bufImgType);
			final ByteBuffer imageBuffer = ByteBuffer
					.wrap(((DataBufferByte) image.getRaster().getDataBuffer())
							.getData());

			final TileRenderer tr = new TileRenderer();
			if (this.tileHeight > 0 && this.tileWidth > 0) {
				tr.setTileSize(this.tileWidth, this.tileHeight, 0);
			} else {
				tr.setTileSize(super.getWidth(), super.getHeight(), 0);
			}
			tr.setImageSize(this.screenshotWidth, this.screenshotHeight);
			tr.setImageBuffer(GL.GL_BGR, GL.GL_UNSIGNED_BYTE, imageBuffer);
			tr.trPerspective(GlGeometryViewer.fovy, 1.0 / this.aspect,
					GlGeometryViewer.zNear, GlGeometryViewer.zFar);

			do {
				tr.beginTile(gl);

				gl.glFlush(); // TODO
				this.drawScene(gl, glu, glut, false, canClearModelviewMatrix,
						null, null, false);
				gl.glFlush(); // TODO
			} while (tr.endTile(gl));

			// this.verifyByteBuffer(imageBuffer);

			ImageUtil.flipImageVertically(image);

			this.reshape(drawable, 0, 0, oldViewportWidth, oldViewportHeight);

			this.screenshot = image;
			this.isScreenshotRequested = false;
			this.requestRepaint(); // allow the image to be repainted with
			// the old aspects.

			System.gc();
			System.gc();
			System.gc();
		} else {
			Point mouseLocationInPanel = null;
			if (isPick && this.mouseLocationInPanel != null) {
				mouseLocationInPanel = (Point) this.mouseLocationInPanel
						.clone();
			}
			MouseEvent mousePickEvent = null;
			if (isPick && this.pickMouseEvent != null) {
				mousePickEvent = new MouseEvent((Component) this.pickMouseEvent
						.getSource(), this.pickMouseEvent.getID(),
						this.pickMouseEvent.getWhen(), this.pickMouseEvent
								.getModifiers(), this.pickMouseEvent.getX(),
						this.pickMouseEvent.getY(), this.pickMouseEvent
								.getClickCount(), this.pickMouseEvent
								.isPopupTrigger(), this.pickMouseEvent
								.getButton());
			}

			this.drawScene(gl, glu, glut, canModifyProjectionMatrix,
					canClearModelviewMatrix, mousePickEvent,
					mouseLocationInPanel, isPick);
			this.mouseLocationInPanel = null;
			this.pickMouseEvent = null;

		}
	}

	public boolean hasRenderedAtLeastOnce = false;

	protected void drawScene(final GL gl, final GLU glu, final GLUT glut,
			final boolean canModifyProjectionMatrix,
			final boolean canClearModelviewMatrix,
			final MouseEvent mousePickEvent, final Point mouseLocationInPanel,
			boolean isPick)
	{
		// if(isPick) {
		// System.err.print("P");
		// } else {
		// System.err.print("D");
		// }
		//		
		// if(this.backBufferContainsPickData) {
		// System.err.print("-");
		// } else {
		// System.err.print("+");
		// }

		synchronized (this.renderablesToDestroy)
		{
			/*
			 * debug info. int renderablesToDestroySize =
			 * this.renderablesToDestroy.size(); if(renderablesToDestroySize >
			 * 0) { synchronized(this.renderables) {
			 * System.err.println("...destroying " + renderablesToDestroySize + "
			 * renderables. " + this.renderables.size() + " renderables still to
			 * be displayed."); } }
			 */

			for (DisplayListRenderable renderable : renderablesToDestroy)
				renderable.destroy(gl, glu, glut);

			renderablesToDestroy.clear();
		}

		synchronized (this.simpleDisplayListsToDestroy)
		{
			/*
			 * debug info. int renderablesToDestroySize =
			 * this.renderablesToDestroy.size(); if(renderablesToDestroySize >
			 * 0) { synchronized(this.renderables) {
			 * System.err.println("...destroying " + renderablesToDestroySize + "
			 * renderables. " + this.renderables.size() + " renderables still to
			 * be displayed."); } }
			 */
			for (Integer list : simpleDisplayListsToDestroy)
				gl.glDeleteLists(list.intValue(), 1);

			this.simpleDisplayListsToDestroy.clear();
		}

		int x2D = -1;
		int y2D = -1;

		if (mousePickEvent != null)
		{
			x2D = mousePickEvent.getX();
			y2D = mousePickEvent.getY();
		}
		
		else if (mouseLocationInPanel != null)
		{
			x2D = mouseLocationInPanel.x;
			y2D = mouseLocationInPanel.y;
		}

		//
		// Set up the projection matrix.
		//

		if (!isPick || !this.backBufferContainsPickData)
		{
			if (canModifyProjectionMatrix)
			{
				gl.glMatrixMode(GL.GL_PROJECTION);

				gl.glLoadIdentity();
			}

			//
			// Is this the start of a pick traversal?
			//

			// gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
			// gl.glEnable(GL.GL_LINE_SMOOTH);

			if (isPick)
			{
				gl.glDisable(GL.GL_LIGHTING);
				// gl.glDisable(GL.GL_FOG);
				gl.glShadeModel(GL.GL_FLAT);

				if (this.supportsShaderPrograms && GlGeometryViewer.currentProgram != 0
						&& GlGeometryViewer.shaderProgram != 0)
				{
					gl.glUseProgram(0);
					GlGeometryViewer.currentProgram = 0;
				}
				
			}
			
			else
			{
				this.backBufferContainsPickData = false;

				gl.glEnable(GL.GL_LIGHTING);
				gl.glShadeModel(GL.GL_SMOOTH);

				// if(isFogEnabled) gl.glEnable(GL.GL_FOG);
				if (this.supportsShaderPrograms)
				{
					boolean successful = true;
					if (GlGeometryViewer.vertexShader == 0)
						successful = this.createVertexShader(gl, glu, glut);

					if (GlGeometryViewer.fragmentShader == 0 && successful)
						successful = this.createFragmentShader(gl, glu, glut);

					if (GlGeometryViewer.shaderProgram == 0 && successful)
					{
						successful = this.createShaderProgram(gl, glu, glut);
						GlGeometryViewer.currentProgram = GlGeometryViewer.shaderProgram;
					}

					if (GlGeometryViewer.currentProgram == 0 && GlGeometryViewer.shaderProgram != 0 && successful)
					{
						gl.glUseProgram(GlGeometryViewer.shaderProgram);
						GlGeometryViewer.currentProgram = GlGeometryViewer.shaderProgram;
					}

					// if one or more steps for creating the shader failed,
					// disable shaders.
					if (!successful)
					{
						this.supportsShaderPrograms = false;
						GlGeometryViewer.shaderProgram = 0;
						GlGeometryViewer.vertexShader = 0;
						GlGeometryViewer.fragmentShader = 0;
					}
				}
			}

			//
			// Set up the view frustum.
			//

			if (canModifyProjectionMatrix)
				glu.gluPerspective(GlGeometryViewer.fovy, 1.0 / this.aspect,
						GlGeometryViewer.zNear, GlGeometryViewer.zFar);

			// gl.glFrustum( left, right, bottom, top, zNear, zFar );

			//
			// Set up the camera transform.
			//

			// Background

			gl.glClearColor(this.backgroundColor[0], this.backgroundColor[1],
					this.backgroundColor[2], this.backgroundColor[3]);

			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

			gl.glMatrixMode(GL.GL_MODELVIEW);
			if (canClearModelviewMatrix)
				gl.glLoadIdentity();
			
			// Position the headlight relative to the viewpoint.
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, this.lightPosition, 0);
			

			// Set the viewpoint.
			// This is necessary for the simple viewer and protein workshop viewers' manipulation.
			// -- not the ligand explorer (which handles it differently, somehow.)
			//
			if (!AppBase.getApp().isLigandExplorer())
				glu.gluLookAt(this.viewEye[0], this.viewEye[1], this.viewEye[2],
					this.viewCenter[0], this.viewCenter[1], this.viewCenter[2],
					this.viewUp[0], this.viewUp[1], this.viewUp[2]);
			
			for (Structure structure : AppBase.sgetModel().getStructures())
			{
				gl.glPushMatrix();
				boolean continuePick = structure.getStructureMap().getSceneNode().draw(gl, glu, glut, isPick, structure);
				gl.glPopMatrix();

				// pick cycles do not need to finish, and paints get
				// priority.
				if (isPick && !continuePick)
					return;

				// gl.glFlush();
			}
		}
				
		try
		{
			if (isPick)
			{
				StructureComponent newComponent = null;

				if (this.rgbaBuffer == null)
					this.rgbaBuffer = BufferUtil.newByteBuffer(4);

				if (this.alphaBits == 0)
				{
					gl.glReadPixels(x2D, this.viewport[3] - y2D, 1, 1, GL.GL_RGB,
							GL.GL_UNSIGNED_BYTE, this.rgbaBuffer);
					this.tempColor.set(this.rgbaBuffer.get(0), this.rgbaBuffer.get(1),
							this.rgbaBuffer.get(2), (byte) 0);
				}
				
				else
				{
					gl.glReadPixels(x2D, this.viewport[3] - y2D, 1, 1, GL.GL_RGBA,
							GL.GL_UNSIGNED_BYTE, this.rgbaBuffer);
					this.tempColor.set(this.rgbaBuffer.get(0), this.rgbaBuffer.get(1),
							this.rgbaBuffer.get(2), this.rgbaBuffer.get(3));
				}
				
				final DisplayLists.UniqueColorMapValue value = DisplayLists.getDisplayLists(this.tempColor);
				
				if (value == null)
					Status.output(Status.LEVEL_REMARK, " "); // clear the
					// status
				else if (value.lists.structureComponent
						.getStructureComponentType() == StructureComponentRegistry.TYPE_FRAGMENT)
				{
					final Fragment f = (Fragment) value.lists.structureComponent;
					final Residue r = f.getResidue(value.index);
					newComponent = r;
					this.reportAtComponent(r);
				}
				
				else
				{
					newComponent = value.lists.structureComponent;
					this.reportAtComponent(value.lists.structureComponent);
				}

//				final Structure[] structures = Model.getSingleton().getModel().getModel().getModel().getStructures();

				boolean needsRepaint = false;
				this.lastComponentMouseWasOver = newComponent;

				if (needsRepaint)
					this.requestRepaint();

				this.backBufferContainsPickData = true;
			}
		}
		
		catch (final Exception e)
		{
			e.printStackTrace();
			System.err.flush();
		}

/* ** DEBUGGING - Compute and output frames per second
		//
		// Compute frames per second.
		//
		// if(mousePickEvent == null && mouseLocationInPanel == null) {
		final long curTime = System.currentTimeMillis();
		this.frameCount++;
		final long totalTime = curTime - this.lastTime;
		if (totalTime >= 1000)
		{
			this.fps = this.frameCount / (totalTime / 1000.0);
			System.err.println("fps = " + this.fps);
			this.lastTime = curTime;
			this.frameCount = 0;
		}
		// }
		// System.out.println("Extrusion vertex count: " +
		// Extrusion.VERTEX_COUNT);
* **/
	}

	// private boolean resizeFinished = false;

	private void reportAtComponent(final StructureComponent structureComponent) {
		final String scType = structureComponent.getStructureComponentType();

		if (scType == StructureComponentRegistry.TYPE_ATOM) {
			final Atom atom = (Atom) structureComponent;

			final Object[] pdbIds = atom.structure.getStructureMap()
					.getPdbToNdbConverter().getPdbIds(atom.chain_id,
							new Integer(atom.residue_id));
			if (pdbIds != null) {
				final String pdbChainId = (String) pdbIds[0];
				final String pdbResidueId = (String) pdbIds[1];

				Status.output(Status.LEVEL_REMARK, "Atom: " + atom.name
						+ ", residue " + pdbResidueId + ", compound "
						+ atom.compound + ", chain " + pdbChainId);
			} else {
				Status
						.output(
								Status.LEVEL_REMARK,
								"Atom: "
										+ atom.name
										+ ", compound "
										+ atom.compound
										+ ". Could not determine the pdb residue or chain ids. Ndb chain id: "
										+ atom.chain_id + ", ndb residue id: "
										+ atom.residue_id);
			}
		} else if (scType == StructureComponentRegistry.TYPE_BOND) {
			final Bond bond = (Bond) structureComponent;

			final Atom a1 = bond.getAtom(0);
			final Atom a2 = bond.getAtom(1);

			final PdbToNdbConverter converter = a1.structure.getStructureMap()
					.getPdbToNdbConverter();
			Object[] tmp = converter.getPdbIds(a1.chain_id, new Integer(
					a1.residue_id));
			final Object[] tmp2 = converter.getPdbIds(a2.chain_id, new Integer(
					a2.residue_id));
			if (tmp != null && tmp2 != null) {
				final String a1ChainId = (String) tmp[0];
				final String a1ResidueId = (String) tmp[1];
				tmp = converter.getPdbIds(a2.chain_id, new Integer(
						a2.residue_id));
				final String a2ChainId = (String) tmp2[0];
				final String a2ResidueId = (String) tmp2[1];

				Status.output(Status.LEVEL_REMARK, "Covalent bond. Atom 1: "
						+ a1.name + ", residue " + a1ResidueId + ", chain "
						+ a1ChainId + "; Atom 2: " + a2.name + ", residue "
						+ a2ResidueId + ", chain " + a2ChainId);
			} else {
				Status
						.output(
								Status.LEVEL_REMARK,
								"Covalent bond. Atom 1: "
										+ a1.name
										+ ", residue* "
										+ a1.residue_id
										+ ", chain* "
										+ a1.chain_id
										+ "; Atom 2: "
										+ a2.name
										+ ", residue* "
										+ a2.residue_id
										+ ", chain* "
										+ a2.chain_id
										+ ". * Could not determine Pdb IDs. Ndb IDs shown instead.");
			}
		} else if (scType == StructureComponentRegistry.TYPE_RESIDUE) {
			final Residue r = (Residue) structureComponent;
			final Fragment f = r.getFragment();
			final String conformationTypeIdentifier = f.getConformationType();
			String conformationType = "Unknown";
			if (conformationTypeIdentifier == StructureComponentRegistry.TYPE_COIL) {
				conformationType = "Coil";
			} else if (conformationTypeIdentifier == StructureComponentRegistry.TYPE_HELIX) {
				conformationType = "Helix";
			} else if (conformationTypeIdentifier == StructureComponentRegistry.TYPE_STRAND) {
				conformationType = "Strand";
			} else if (conformationTypeIdentifier == StructureComponentRegistry.TYPE_TURN) {
				conformationType = "Turn";
			}

			final Object[] tmp = f.structure.getStructureMap()
					.getPdbToNdbConverter().getPdbIds(r.getChainId(),
							new Integer(r.getResidueId()));
			if (tmp != null) {
				final String pdbChainId = (String) tmp[0];
				final String pdbResidueId = (String) tmp[1];

				Status.output(Status.LEVEL_REMARK, "Residue " + pdbResidueId
						+ ", from chain " + pdbChainId + "; "
						+ conformationType + " conformation; "
						+ r.getCompoundCode() + " compound.");
			} else {
				Status
						.output(
								Status.LEVEL_REMARK,
								"Residue* "
										+ r.getResidueId()
										+ ", from chain* "
										+ r.getChainId()
										+ "; "
										+ conformationType
										+ " conformation; "
										+ r.getCompoundCode()
										+ " compound. * Could not determine Pdb IDs. Ndb IDs shown instead.");
			}
		} else if (scType == StructureComponentRegistry.TYPE_FRAGMENT) {
			final Fragment f = (Fragment) structureComponent;

			// remove all but the local name for the secondary structure class.
			String conformation = f.getConformationType();
			final int lastDot = conformation.lastIndexOf('.');
			conformation = conformation.substring(lastDot + 1);

			final String ndbChainId = f.getChain().getChainId();
			final int startNdbResidueId = f.getResidue(0).getResidueId();
			final int endNdbResidueId = f.getResidue(f.getResidueCount() - 1)
					.getResidueId();

			final PdbToNdbConverter converter = f.structure.getStructureMap()
					.getPdbToNdbConverter();
			final Object[] tmp = converter.getPdbIds(ndbChainId, new Integer(
					startNdbResidueId));
			final Object[] tmp2 = converter.getPdbIds(ndbChainId, new Integer(
					endNdbResidueId));

			if (tmp != null && tmp2 != null) {
				final String startPdbChainId = (String) tmp[0];
				final String startPdbResidueId = (String) tmp[1];
				final String endPdbResidueId = (String) tmp2[1];

				Status.output(Status.LEVEL_REMARK, conformation
						+ " fragment: chain " + startPdbChainId
						+ ", from residue " + startPdbResidueId
						+ " to residue " + endPdbResidueId);
			} else {
				Status
						.output(
								Status.LEVEL_REMARK,
								conformation
										+ " fragment: chain* "
										+ f.getChain().getChainId()
										+ ", from residue* "
										+ startNdbResidueId
										+ " to residue* "
										+ endNdbResidueId
										+ ". * Could not determine Pdb IDs. Ndb IDs shown instead.");
			}

			// structureMap.getStructureStyles().setResidueColor(res, yellow);
		} else if (scType == StructureComponentRegistry.TYPE_CHAIN) {
			final Chain c = (Chain) structureComponent;

			// remove all but the local name for the secondary structure class.
			final String pdbChainId = c.structure.getStructureMap()
					.getPdbToNdbConverter().getFirstPdbChainId(c.getChainId());
			if (pdbChainId != null) {
				Status.output(Status.LEVEL_REMARK, "Chain " + pdbChainId
						+ " backbone");
			} else {
				Status
						.output(
								Status.LEVEL_REMARK,
								"Chain* "
										+ c.getChainId()
										+ " backbone. * Could not determine Pdb ID. Ndb ID shown instead.");
			}

			// structureMap.getStructureStyles().setResidueColor(res, yellow);
		}
	}

	/**
	 * GLEventListener interface - displayChanged method:
	 */
	public void displayChanged(final GLAutoDrawable drawable,
			final boolean modeChanged, final boolean deviceChanged)
	{
		// the new display's driver may not support shaders. Not likely, but
		// possible.		
		checkSetShaderSupport();
		this.invalidateAllGeometry();

		// refresh the pick information
		
	}

	/**
	 * Check if a) the app is allowing shader support and b) if the GL implementation supports it.
	 */
	protected void checkSetShaderSupport()
	{
		final GL gl = drawable.getGL();

		this.supportsShaderPrograms = AppBase.getApp().allowShaders() &&
			gl.isFunctionAvailable("glCreateShader") &&
			gl.isFunctionAvailable("glAttachShader") &&
			gl.isFunctionAvailable("glLinkProgram") &&
			gl.isFunctionAvailable("glUseProgram");
	}
	
	// -----------------------------------------------------------------------------

	//
	// MouseListener interface.
	//

	protected final int mouseDownLoc[] = new int[2];

	protected final int mouseUpLoc[] = new int[2];

	protected MouseEvent pickMouseEvent = null;

	protected Point mouseLocationInPanel = null;

	protected StructureComponent lastComponentMouseWasOver = null;

	protected Point lastMouseLocationInPanelForIndicatorBubble = null;

	/**
	 * MouseListener interface - mouseEntered method.
	 */
	public void mouseEntered(final MouseEvent e) {
	}

	/**
	 * mouseExited method.
	 */
	public void mouseExited(final MouseEvent e) {
		this.lastMouseLocationInPanelForIndicatorBubble = this.mouseLocationInPanel = null;
		this.lastComponentMouseWasOver = null;
	}

	/**
	 * mousePressed method.
	 */
	public void mousePressed(final MouseEvent e) {
		this.mouseDownLoc[0] = e.getX();
		this.mouseDownLoc[1] = e.getX();

		this.prevMouseX = e.getX();
		this.prevMouseY = e.getY();
	}

	/**
	 * mouseReleased method.
	 */
	public void mouseReleased(final MouseEvent e) {
		this.mouseUpLoc[0] = e.getX();
		this.mouseUpLoc[1] = e.getX();

		if ((Math.abs(this.mouseDownLoc[0] - this.mouseUpLoc[0]) <= 1)
				&& (Math.abs(this.mouseDownLoc[1] - this.mouseUpLoc[1]) <= 1)) {
			this.pickMouseEvent = e; // Do a pick
		}
	}
	
	public void mouseClicked(final MouseEvent e)
	{
	}

	//
	// MouseMotionListener interface.
	//

	/**
	 * Control the camera motion.
	 */
//	private static final Structure[] oneStructure = new Structure[1];
	public void mouseDragged(final MouseEvent e)
	{
		if (!AppBase.sgetModel().hasStructures())
			return;

		final int x = e.getX();
		final int y = e.getY();
		final Dimension size = e.getComponent().getSize();

		final double v3d[] = { 0.0f, 0.0f, 0.0f };
		final double v3d2[] = { 0.0f, 0.0f, 0.0f };
		// final double v3d3[] = { 0.0f, 0.0f, 0.0f };
		// final double r4d[] = { 0.0f, 0.0f, 0.0f, 0.0f };
		// final double r4d2[] = { 0.0f, 0.0f, 0.0f, 0.0f };

		// if (this.lastComponentMouseWasOver != null) {
//		Structure[] strucs = null;
//		if (this.lastComponentMouseWasOver == null) {
//			strucs = structures;
//		} else {
//			strucs = oneStructure;
//			oneStructure[0] = this.lastComponentMouseWasOver.structure;
//		}
//		for(int i = 0; i < strucs.length; i++) {
//			final StructureMap sm = strucs[i].getStructureMap();
	
			if ((e.getModifiers() & InputEvent.CTRL_MASK) == 0
					&& (e.getModifiers() & InputEvent.SHIFT_MASK) == 0
					&& (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
				// ROTATE
	
				// Get a rotation delta using a virtual sphere mapping.
				final double rotDelta[] = { 0.0f, 1.0f, 0.0f, 0.0f };
				this.virtualSphere.compute(this.prevMouseX, this.prevMouseY, x, y,
						rotDelta);
				/*
				 * for(int i = 0; i < rotDelta.length; i++) {
				 * if(Double.isNaN(rotDelta[i])) { System.err.flush(); } }
				 */
	
				// We want to make it look like we're rotating the object
				// instead
				// of the camera, so reverse the camera motion direction.
				rotDelta[0] *= -1.0;
				rotDelta[3] *= -1.0; // The Z-direction needs to be
				// flipped...
	
				// Before we can apply the virtual sphere rotation to the view,
				// we have to transform the virtual sphere's fixed/world
				// coordinate
				// system rotation vector into the camera/view coordinate
				// system.
	
				// Construct the viewDirection vector.
				final double viewDirection[] = { this.viewCenter[0] - this.viewEye[0],
						this.viewCenter[1] - this.viewEye[1],
						this.viewCenter[2] - this.viewEye[2] };
				/*
				 * if (Double.isNaN(viewDirection[0]) ||
				 * Double.isNaN(viewDirection[1]) || Double.isNaN(viewDirection[2])) {
				 * System.err.flush(); }
				 */
	
				Algebra.normalizeVector(viewDirection);
				/*
				 * if (Double.isNaN(viewDirection[0]) ||
				 * Double.isNaN(viewDirection[1]) || Double.isNaN(viewDirection[2])) {
				 * System.err.flush(); }
				 */
	
				// Construct the viewRight vector (ie: viewDirection x viewUp).
				final double viewRight[] = { 1.0f, 0.0f, 0.0f };
				Algebra.crossProduct(viewDirection, this.viewUp, viewRight);
				/*
				 * if (Double.isNaN(viewRight[0]) || Double.isNaN(viewRight[1]) ||
				 * Double.isNaN(viewRight[2])) { System.err.flush(); }
				 */
	
				Algebra.normalizeVector(viewRight);
	
				/*
				 * if (Double.isNaN(viewRight[0]) || Double.isNaN(viewRight[1]) ||
				 * Double.isNaN(viewRight[2])) { System.err.flush(); }
				 */
	
				// Construct the virtual-sphere-to-view rotation matrix
				// (transpose)
				final double viewMatrix[] = { viewRight[0], this.viewUp[0],
						viewDirection[0], 0.0f, viewRight[1], this.viewUp[1],
						viewDirection[1], 0.0f, viewRight[2], this.viewUp[2],
						viewDirection[2], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
	
				/*
				 * for(int i = 0; i < viewMatrix.length; i++) {
				 * if(Double.isNaN(viewMatrix[i])) { System.err.flush(); } }
				 */
	
				// Transform the virtual sphere axis's coordinate system
				final double vsAxis[] = { rotDelta[1], rotDelta[2], rotDelta[3] };
				Algebra.matrixRotate(viewMatrix, vsAxis);
				rotDelta[1] = vsAxis[0];
				rotDelta[2] = vsAxis[1];
				rotDelta[3] = vsAxis[2];
	
				/*
				 * for(int i = 0; i < rotDelta.length; i++) {
				 * if(Double.isNaN(rotDelta[i])) { System.err.flush(); } }
				 */
	
				// NOW we can apply the transformed rotation to the view!
				// Compute the new viewEye.
				// Translate to the rotationCenter.
				this.viewEye[0] -= this.rotationCenter[0];
				this.viewEye[1] -= this.rotationCenter[1];
				this.viewEye[2] -= this.rotationCenter[2];
				Algebra.angleAxisRotate(rotDelta, this.viewEye);
				// Translate back.
				this.viewEye[0] += this.rotationCenter[0];
				this.viewEye[1] += this.rotationCenter[1];
				this.viewEye[2] += this.rotationCenter[2];
	
				/*
				 * for(int i = 0; i < viewEye.length; i++) {
				 * if(Double.isNaN(viewEye[i])) { System.err.flush(); } }
				 */
	
				// Compute the new viewCenter.
				// Translate to the rotationCenter.
				this.viewCenter[0] -= this.rotationCenter[0];
				this.viewCenter[1] -= this.rotationCenter[1];
				this.viewCenter[2] -= this.rotationCenter[2];
				Algebra.angleAxisRotate(rotDelta, this.viewCenter);
				// Translate back.
				this.viewCenter[0] += this.rotationCenter[0];
				this.viewCenter[1] += this.rotationCenter[1];
				this.viewCenter[2] += this.rotationCenter[2];
	
				/*
				 * for(int i = 0; i < viewCenter.length; i++) {
				 * if(Double.isNaN(viewCenter[i])) { System.err.flush(); } }
				 */
	
				// Compute the new viewUp.
				// (note that we do not translate to the rotation center first
				// because viewUp is a direction vector not an absolute vector!)
				Algebra.angleAxisRotate(rotDelta, this.viewUp);
	
				/*
				 * for(int i = 0; i < viewUp.length; i++) {
				 * if(Double.isNaN(viewUp[i])) { System.err.flush(); } }
				 */
	
				Algebra.normalizeVector(this.viewUp);
	
				/*
				 * for(int i = 0; i < viewUp.length; i++) {
				 * if(Double.isNaN(viewUp[i])) { System.err.flush(); } }
				 */
			} else if ((e.getModifiers() & InputEvent.CTRL_MASK) == 0
					&& (e.getModifiers() & InputEvent.SHIFT_MASK) != 0
					|| (e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
				// DOLLY IN/OUT
	
				// Compute normalized direction vector from viewEye to
				// viewCenter.
	
				v3d[0] = this.viewCenter[0] - this.viewEye[0];
				v3d[1] = this.viewCenter[1] - this.viewEye[1];
				v3d[2] = this.viewCenter[2] - this.viewEye[2];
	
				Algebra.normalizeVector(v3d);
	
				// Compute a deltaZ that provides a nice motion speed,
				// then multiply the direction vector by deltaZ.
	
				double deltaZ = 200.0 * ((double) (y - this.prevMouseY) / (double) size.height);
				if (deltaZ < -100.0) {
					deltaZ = -100.0f;
				}
				if (deltaZ > 100.0) {
					deltaZ = 100.0f;
				}
				v3d[0] *= deltaZ;
				v3d[1] *= deltaZ;
				v3d[2] *= deltaZ;
	
				// Add the delta vector to viewEye and viewCenter.
	
				this.viewEye[0] += v3d[0];
				this.viewEye[1] += v3d[1];
				this.viewEye[2] += v3d[2];
				this.viewCenter[0] += v3d[0];
				this.viewCenter[1] += v3d[1];
				this.viewCenter[2] += v3d[2];
				// final double sign = deltaZ < 0 ? 1 : -1;
				// final double vectorLength = Algebra.vectorLength(v3d) * sign;
				// this.fogStart += vectorLength;
				// this.fogEnd += vectorLength;
			} else if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0
					|| (e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
				// TRANSLATE LEFT/RIGHT AND UP/DOWN
	
				// Compute a left-right direction vector from the current view
				// vectors (ie: cross product of viewUp and viewCenter-viewEye).
	
				// Compute view direction vector (v3d2).
				v3d2[0] = this.viewCenter[0] - this.viewEye[0];
				v3d2[1] = this.viewCenter[1] - this.viewEye[1];
				v3d2[2] = this.viewCenter[2] - this.viewEye[2];
				Algebra.normalizeVector(v3d2);
	
				// Compute left-right direction vector (v3d2 x viewUp).
				Algebra.crossProduct(v3d2, this.viewUp, v3d);
				Algebra.normalizeVector(v3d);
	
				// Compute a deltaX and deltaY that provide a nice motion speed,
				// then multiply the direction vector by the deltas.
	
				final double deltaX = 30.0 * ((double) (this.prevMouseX - x) / (double) size.width);
				final double deltaY = 30.0 * ((double) (y - this.prevMouseY) / (double) size.height);
	
				// Add the deltaX portion of the left-right vector
				// to the deltaY portion of the up-down vector
				// to get our relative offset vector.
				final double shiftX = (v3d[0] * deltaX) + (this.viewUp[0] * deltaY);
				final double shiftY = (v3d[1] * deltaX) + (this.viewUp[1] * deltaY);
				final double shiftZ = (v3d[2] * deltaX) + (this.viewUp[2] * deltaY);
	
				// Add the resulting offsets to viewEye and viewCenter.
	
				this.viewEye[0] += shiftX;
				this.viewEye[1] += shiftY;
				this.viewEye[2] += shiftZ;
				this.viewCenter[0] += shiftX;
				this.viewCenter[1] += shiftY;
				this.viewCenter[2] += shiftZ;
			}
//		}

		this.prevMouseX = x;
		this.prevMouseY = y;

		this.requestRepaint();
		// }
	}

	/**
	 * mouseMoved method.
	 */
	public void mouseMoved(final MouseEvent e) {
		this.lastMouseLocationInPanelForIndicatorBubble = this.mouseLocationInPanel = e
				.getPoint();

		this.requestPick();
	}

	// -----------------------------------------------------------------------------

	//
	// Camera view control methods.
	//

	/**
	 * Reset the camera view to point at the origin, with an eye position at Z =
	 * -1, with a Y-up orientation.
	 */
	public void resetView(boolean forceRecalculation, boolean repaint)
	{
		AppBase.sgetSceneController().resetView(forceRecalculation);
		if (repaint)
			this.requestRepaint();
	}

	private final ArrayList<GvPickEventListener> pickListeners = new ArrayList<GvPickEventListener>();

	/**
	 * Add a pick event listener.
	 */
	public void addPickEventListener(final GvPickEventListener pickEventListener) {
		if (pickEventListener == null) {
			throw new NullPointerException("null pickEventListener");
		}

		this.pickListeners.add(pickEventListener);
	}

	/**
	 * Remove a pick event listener.
	 */
	public void removePickEventListener(
			final GvPickEventListener pickEventListener) {
		if (pickEventListener == null) {
			throw new NullPointerException("null pickEventListener");
		}

		this.pickListeners.remove(pickEventListener);
	}

	/**
	 * Fire a pick event to all listeners.
	 */
	public void firePickEvent(final GvPickEvent pickEvent)
	{
		for (GvPickEventListener listener : pickListeners)
			listener.processPickEvent(pickEvent);

		this.requestRepaint();
	}

	public BufferedImage getScreenshotWithRobot() {
		Robot screenRobot = null;
		try {
			screenRobot = new Robot();
		} catch (final AWTException e) {
			e.printStackTrace();
			return null;
		}
		/*
		 * Component c = this; Component c_ = null; while(c != null) { c_ = c; c =
		 * c_.getParent(); } System.out.println(c_.getClass().toString()); if(c_ ==
		 * this.model.getMainFrame()) { System.out.println("This is the main
		 * frame..."); }
		 * 
		 * Toolkit toolkit = Toolkit.getDefaultToolkit(); GraphicsDevice screen =
		 * GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		 * if (toolkit instanceof ComponentFactory) { ComponentFactory peer =
		 * ((ComponentFactory)toolkit); screen. }
		 */

		final Point screenLocation = super.getLocationOnScreen();

		final Rectangle rectangle = new Rectangle(screenLocation.x,
				screenLocation.y, super.getWidth(), super.getHeight());
		final BufferedImage screenshot = screenRobot
				.createScreenCapture(rectangle);

		// return screenshot;
		return screenshot;
	}

	public static final int TARGA_HEADER_SIZE = 18;

	// TODO comment
	public BufferedImage getScreenshot() {
		return this.screenshot;
	}

	public void clearScreenshot() {
		this.screenshot.flush();
		this.screenshot = null;
	}

	public void removeScreenshotReference() {
		this.screenshot = null;
	}

	// TODO comment
	public void requestScreenShot(final int width, final int height) {
		this.screenshot = null;
		this.screenshotFailed = false;
		this.screenshotWidth = width;
		this.screenshotHeight = height;
		this.isScreenshotRequested = true;
		this.requestRepaint();
	}

	public void reset() { reset(false); }
	public void reset(boolean forceRecalculation)
	{
		this.backgroundColor = GlGeometryViewer.black;

		if (AppBase.sgetModel().hasStructures())
		{
			for (final Structure s : AppBase.sgetModel().getStructures())
			{
				final StructureMap sm = s.getStructureMap();
				final StructureStyles ss = sm.getStructureStyles();
				final JoglSceneNode sn = sm.getSceneNode();
				sn.regenerateGlobalList();

				sn.clearRenderables();
				sn.clearLabels();

				// set all non-protein atoms/bonds visible, but all protein
				// atoms/bonds invisible.
				// set all protein chains visible, and all non-protein chains
				// invisible.
				final Iterator chainIt = sm.getChains().iterator();
				while (chainIt.hasNext()) {
					final Chain c = (Chain) chainIt.next();

					if (c.getResidueCount() > 0
							&& c.getResidue(0).getClassification() == Residue.Classification.AMINO_ACID) {

						ss.setVisible(c, true);
					} else {
						ss.setVisible(c, false);
					}

					final Iterator fragIt = c.getFragments().iterator();
					while (fragIt.hasNext()) {
						final Fragment f = (Fragment) fragIt.next();

						final Iterator resIt = f.getResidues().iterator();
						while (resIt.hasNext()) {
							final Residue r = (Residue) resIt.next();

							final Vector atoms = r.getAtoms();
							final Iterator atomsIt = atoms.iterator();
							while (atomsIt.hasNext()) {
								final Atom a = (Atom) atomsIt.next();
								if (r.getClassification() != Residue.Classification.AMINO_ACID
										&& !r.getCompoundCode().equals("HOH")) {
									ss.setVisible(a, true);
								} else {
									ss.setVisible(a, false);
								}
							}

							final Iterator bondsIt = sm.getBonds(atoms)
									.iterator();
							while (bondsIt.hasNext()) {
								final Bond b = (Bond) bondsIt.next();

								if (r.getClassification() != Residue.Classification.AMINO_ACID &&
									r.getClassification() != Residue.Classification.WATER ) {
									ss.setVisible(b, true);
								} else {
									ss.setVisible(b, false);
								}
							}
						}

					}

					this.structureAdded(s);
				}
			}
			
			resetView(forceRecalculation, false);
		}

		if (!forceRecalculation)
			this.requestRepaint();
	}

	/**
	 * Implements the IViewUpdateNotification interface
	 * 
	 * @param str
	 */
	public void structureAdded(final Structure str)
	{
		structureAdded(str, ChainGeometry.RIBBON_TRADITIONAL, true);
	}
	
	/**
	 * Internal response to 'structure added'.  Sets default ribbon form and whether or not to set the
	 * default atom styles.  Set to 'false' if you want to do your own.
	 * 
	 * @param str - the structure added
	 * @param ribbonForm - the default ribbon form
	 * @param doAtoms - whether or no to do the atom styles.
	 */
	protected void structureAdded(final Structure str, int ribbonForm, boolean doAtoms)
	{
		final StructureMap structureMap = str.getStructureMap();
		final StructureStyles structureStyles = structureMap
				.getStructureStyles();
		final JoglSceneNode sn = structureMap.getSceneNode();

		final ChainGeometry defaultChainGeometry = (ChainGeometry) GlGeometryViewer.defaultGeometry
				.get(StructureComponentRegistry.TYPE_CHAIN);
		final AtomGeometry defaultAtomGeometry = (AtomGeometry) GlGeometryViewer.defaultGeometry
				.get(StructureComponentRegistry.TYPE_ATOM);
		final BondGeometry defaultBondGeometry = (BondGeometry) GlGeometryViewer.defaultGeometry
				.get(StructureComponentRegistry.TYPE_BOND);

		final ChainStyle defaultChainStyle = (ChainStyle) structureStyles
				.getDefaultStyle(StructureComponentRegistry.TYPE_CHAIN);
		final AtomStyle defaultAtomStyle = (AtomStyle) structureStyles
				.getDefaultStyle(StructureComponentRegistry.TYPE_ATOM);
		final BondStyle defaultBondStyle = (BondStyle) structureStyles
				.getDefaultStyle(StructureComponentRegistry.TYPE_BOND);

		str.getStructureMap().getStructureStyles()
				.removeStructureStylesEventListener(this);
		str.getStructureMap().getStructureStyles()
				.addStructureStylesEventListener(this);

		defaultChainGeometry.setRibbonForm(ribbonForm);
		defaultChainGeometry.setRibbonsAreSmoothed(true);

		// chains
		final int chainCount = structureMap.getChainCount();
		for (int i = 0; i < chainCount; i++) {
			final Chain c = structureMap.getChain(i);

			// set the default style...
			structureStyles.setStyle(c, defaultChainStyle);

			// ignore invisible chains...
			if (!structureStyles.isVisible(c)) {
				continue;
			}

			final int resCount = c.getResidueCount();
			for (int j = 0; j < resCount; j++) {
				final Residue r = c.getResidue(j);

				// set all residues visible...
				structureStyles.setVisible(r, true);
			}

			synchronized (sn.renderables) {
				sn.renderables.put(c, new DisplayListRenderable(c,
						defaultChainStyle, defaultChainGeometry));
			}
		}

		final int bondCount = structureMap.getBondCount();
		for (int i = 0; i < bondCount; i++) {
			final Bond b = structureMap.getBond(i);

			// set the default style
			structureStyles.setStyle(b, defaultBondStyle);

			// ignore invisible bonds...
			if (!structureStyles.isVisible(b)) {
				continue;
			}

			synchronized (sn.renderables) {
				sn.renderables.put(b, new DisplayListRenderable(b,
						defaultBondStyle, defaultBondGeometry));
			}
		}

		if (doAtoms)
						// atoms will be handled by derived func, if at all.
		{
			final int atomCount = structureMap.getAtomCount();
			for (int i = 0; i < atomCount; i++)
			{
				final Atom a = structureMap.getAtom(i);
	
				// set the default style
				structureStyles.setStyle(a, defaultAtomStyle);
	
				// ignore invisible atoms...
				if (!structureStyles.isVisible(a))
					continue;
	
				synchronized (sn.renderables)
				{
					sn.renderables.put(a, new DisplayListRenderable(a,
							defaultAtomStyle, defaultAtomGeometry));
				}
			}
			this.requestRepaint();
		}
	}

	public void windowActivated(final WindowEvent e) {
	}

	public void windowClosed(final WindowEvent e) {
	}

	public void windowClosing(final WindowEvent e) {
	}

	public void windowDeactivated(final WindowEvent e) {
	}

	public void windowDeiconified(final WindowEvent e) {
	}

	public void windowIconified(final WindowEvent e) {
	}

	public void windowOpened(final WindowEvent e) {
		// this.animator.start();
		this.requestRepaint();
	}

	public boolean hasScreenshotFailed() {
		return this.screenshotFailed;
	}

	public void processStructureStylesEvent(
			final StructureStylesEvent structureStylesEvent) {
		if (structureStylesEvent.attribute == StructureStyles.ATTRIBUTE_SELECTION) {
			this.requestRepaint();
		}
	}

	public void clearStructure()
	{
		for (Structure structure : AppBase.sgetModel().getStructures())
		{
			final JoglSceneNode sn = structure.getStructureMap().getSceneNode();

			final Iterator<DisplayListRenderable> it = sn.renderables.values().iterator();
			while (it.hasNext())
			{
				final DisplayListRenderable renderable = it.next();
				renderable.setDeleteListsOnDeconstruction(true);
				this.renderablesToDestroy.add(renderable);
			}

			sn.clearLabels();

			sn.renderables.clear();
		}
		
		/*
		 * So, this would be better controlled by an invocation flag but, since it's a prescribed
		 * interface, we have to rely on the application hack.
		 */
		if (!AppBase.getApp().isLigandExplorer())
		{
			AppBase.sgetModel().clear();
			this.requestRepaint();
		}
	}

	public void newStructureAdded(final Structure struc) {
		this.structureAdded(struc);
	}

	public void setHasRenderedFlag() {
		this.hasRenderedAtLeastOnce = false;
	}

	/**
	 * Use setHasRenderedFlag() first.
	 */
	public boolean hasRenderedAtLeastOnce() {
		return this.hasRenderedAtLeastOnce;
	}
	
	protected boolean createVertexShader(final GL gl, final GLU glu,
			final GLUT glut)
	{
		final String[] lines = this.getReaderLines("per_pixel_v.glsl");
		// final String[] lines = this.getReaderLines("test_v.glsl");
		final int[] lengths = this.createLengthArray(lines);

		GlGeometryViewer.vertexShader = gl.glCreateShader(GL.GL_VERTEX_SHADER);
		gl.glShaderSource(GlGeometryViewer.vertexShader, lines.length, lines, lengths, 0);
		gl.glCompileShader(GlGeometryViewer.vertexShader);

		if (this.do_glFinishInShaders)
			gl.glFinish();
		else
		    gl.glFlush();
		
		final IntBuffer buf = BufferUtil.newIntBuffer(1);
		gl.glGetShaderiv(GlGeometryViewer.vertexShader, GL.GL_INFO_LOG_LENGTH, buf);
		int logLength = buf.get(0);
		buf.rewind();
		gl.glGetShaderiv(GlGeometryViewer.vertexShader, GL.GL_COMPILE_STATUS, buf);
		final int status = buf.get(0);

/* ** DEBUGGING - output shader information
		System.err.println("Vertex shader creation...");
		System.err.println("\tstatus: " + (status == GL.GL_TRUE));
		System.err.println("\tlog length: " + logLength + "\n");
		System.err.println("Log:");
* **/

		logLength += 10;

		final int[] length = new int[1];
		final byte[] bufArray = new byte[logLength];
		gl.glGetShaderInfoLog(GlGeometryViewer.vertexShader, logLength, length, 0, bufArray, 0);

		final StringBuffer s = new StringBuffer();
		for (int i = 0; i < length[0]; i++) {
			s.append((char) bufArray[i]);
		}
		
		final String log = s.toString();
/* ** DEBUGGING - output shader information
		System.err.println(log + "\n-------------------\n");
* **/

		// if the shader was not created, or if it would be run in software,
		// bail.
		if (status == GL.GL_FALSE) {
			return false;
		}
		if (log.indexOf("software") >= 0) {
			if (GlGeometryViewer.vertexShader > 0) {
				gl.glDeleteShader(GlGeometryViewer.vertexShader);
			}
			return false;
		}

		return true;
	}

	private boolean createFragmentShader(final GL gl, final GLU glu,
			final GLUT glut) {
		final String[] lines = this.getReaderLines("per_pixel_f.glsl");
		// final String[] lines = this.getReaderLines("test_f.glsl");
		final int[] lengths = this.createLengthArray(lines);

		GlGeometryViewer.fragmentShader = gl.glCreateShader(GL.GL_FRAGMENT_SHADER);
		gl.glShaderSource(GlGeometryViewer.fragmentShader, lines.length, lines, lengths, 0);
		gl.glCompileShader(GlGeometryViewer.fragmentShader);

		if (this.do_glFinishInShaders)
			gl.glFinish();
		else
		    gl.glFlush();
		
		final IntBuffer buf = BufferUtil.newIntBuffer(1);
		gl.glGetShaderiv(GlGeometryViewer.fragmentShader, GL.GL_INFO_LOG_LENGTH, buf);
		int logLength = buf.get(0);
		buf.rewind();
		gl.glGetShaderiv(GlGeometryViewer.fragmentShader, GL.GL_COMPILE_STATUS, buf);
		final int status = buf.get(0);
/* ** DEBUGGING - output fragment shader informaiton
		System.err.println("Fragment shader creation...");
		System.err.println("\tstatus: " + (status == GL.GL_TRUE));
		System.err.println("\tlog length: " + logLength + "\n");
		System.err.println("Log:");
* **/

		logLength += 10;

		final int[] length = new int[1];
		final byte[] bufArray = new byte[logLength];
		gl.glGetShaderInfoLog(GlGeometryViewer.fragmentShader, logLength, length, 0,
						bufArray, 0);

		final StringBuffer s = new StringBuffer();
		for (int i = 0; i < length[0]; i++) {
			s.append((char) bufArray[i]);
		}
		final String log = s.toString();
// DEBUGGING		System.err.println(log + "\n-------------------\n");

		// if the shader was not created, or if it would be run in software,
		// bail.
		if (status == GL.GL_FALSE) {
			return false;
		}
		if (log.indexOf("software") >= 0) {
			if (GlGeometryViewer.fragmentShader > 0) {
				gl.glDeleteShader(GlGeometryViewer.fragmentShader);
			}
			return false;
		}

		return true;
	}

	private boolean createShaderProgram(final GL gl, final GLU glu,
			final GLUT glut) {
		GlGeometryViewer.shaderProgram = gl.glCreateProgram();
		gl.glAttachShader(GlGeometryViewer.shaderProgram, GlGeometryViewer.vertexShader);
		gl.glAttachShader(GlGeometryViewer.shaderProgram, GlGeometryViewer.fragmentShader);
		gl.glLinkProgram(GlGeometryViewer.shaderProgram);
		gl.glUseProgram(GlGeometryViewer.shaderProgram);

		if (this.do_glFinishInShaders)
			gl.glFinish();
		else
		    gl.glFlush();
		
		final IntBuffer buf = BufferUtil.newIntBuffer(1);
		gl.glGetProgramiv(GlGeometryViewer.shaderProgram, GL.GL_INFO_LOG_LENGTH, buf);
		int logLength = buf.get(0);
		buf.rewind();
		gl.glGetProgramiv(GlGeometryViewer.shaderProgram, GL.GL_LINK_STATUS, buf);
		final int status = buf.get(0);
/* ** DEBUGGING - output shader program information
		System.err.println("Shader program creation...");
		System.err.println("\tstatus: " + (status == GL.GL_TRUE));
		System.err.println("\tlog length: " + logLength + "\n");
		System.err.println("Log:");
* **/

		logLength += 10;

		final int[] length = new int[1];
		final byte[] bufArray = new byte[logLength];
		gl
				.glGetProgramInfoLog(GlGeometryViewer.shaderProgram, logLength, length, 0,
						bufArray, 0);

		final StringBuffer s = new StringBuffer();
		for (int i = 0; i < length[0]; i++) {
			s.append((char) bufArray[i]);
		}
		final String log = s.toString();
// DEBUGGING		System.err.println(log + "\n-------------------\n");

		// if the shader was not created, or if it would be run in software,
		// bail.
		if (status == GL.GL_FALSE) {
			return false;
		}
		if (log.indexOf("software") >= 0) {
			if (GlGeometryViewer.shaderProgram > 0) {
				gl.glDeleteProgram(GlGeometryViewer.shaderProgram);
			}
			return false;
		}

		return true;
	}

	private String[] getReaderLines(final String filename) {
		final String[] lines = { "" };

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(super.getClass()
					.getResource(filename).openStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines[0] += line + "\n";
			}
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException useless) {
				}
			}
		}

		return lines;
	}

	private int[] createLengthArray(final String[] lines) {
		final int[] lengths = new int[lines.length];

		for (int i = 0; i < lines.length; i++) {
			lengths[i] = lines[i].length();
		}

		return lengths;
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mbt.app_controller.IViewUpdateListener#handleModelChangedEvent(org.rcsb.mbt.app_controller.ViewUpdateEvent)
	 *
	 * If you override this, just intercept the actions you need to intercept - pass the rest through
	 * in your 'default' case (by calling the super and passing the evt on through).
	 */
	public void handleUpdateEvent(UpdateEvent evt)
	{
		switch(evt.action)
		{
		case VIEW_RESET:
			reset();
			break;
			
		case STRUCTURE_ADDED:
			structureAdded(evt.structure);
			break;
			
		case CLEAR_ALL:
			clearStructure();
			break;
		}
	}
}
