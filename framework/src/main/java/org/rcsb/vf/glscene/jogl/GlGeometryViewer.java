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
 * Created on 2007/07/02
 *
 */ 
package org.rcsb.vf.glscene.jogl;

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
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;


import javax.media.opengl.DebugGL2;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLProfile;
import javax.media.opengl.TraceGL2;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JPanel;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.Surface;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.StructureModel.StructureList;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.BondStyle;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.attributes.IStructureStylesEventListener;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.attributes.StructureStylesEvent;
import org.rcsb.mbt.model.attributes.SurfaceStyle;
import org.rcsb.mbt.model.geometry.ArrayLinearAlgebra;
import org.rcsb.mbt.model.util.DebugState;
import org.rcsb.mbt.model.util.ExternReferences;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.controllers.scene.SceneController;
import org.rcsb.vf.glscene.jogl.ChainGeometry.RibbonForm;
import org.rcsb.vf.glscene.jogl.tiles.TileRenderer;
import org.rcsb.vf.glscene.surfaces.SurfaceGeometry;


import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.gl2.GLUT;



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
WindowListener, IStructureStylesEventListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 229310376730362252L;

	// OpenGL rendering environment
	public GLCanvas glCanvas = null;

	private GLCapabilities glCapabilities = null;

	protected boolean do_glFinishInShaders = false;

	private final GLUT glut = new GLUT();

	private final GLU glu = new GLU();

	private boolean backBufferContainsPickData = false;

	GLAutoDrawable drawable = null;
	
	GLCanvas drawableViewer = null;

	public List<DisplayListRenderable> renderablesToDestroy = Collections.synchronizedList(new ArrayList<DisplayListRenderable>());

	public List<Integer> simpleDisplayListsToDestroy = Collections.synchronizedList(new ArrayList<Integer>());

	// Default geometry for new Renderables defaultGeometry{scType} = Geometry
	public static Hashtable<ComponentType, DisplayListGeometry> defaultGeometry = new Hashtable<ComponentType, DisplayListGeometry>();

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

	public static final float[] DEFAULT_SPECULAR_COLOR = black;

	public static final float[] DEFAULT_EMISSION_COLOR = black;

	public static final float[] DEFAULT_AMBIENT_COLOR = black;

	public static final float[] DEFAULT_DIFFUSE_COLOR = black;

	/* **
	// Frame rate and other drawing statistics
	private long lastTime = System.currentTimeMillis();

	private int frameCount = 0;

	private double fps = 0.0f;
	 * **/

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
		ArrayLinearAlgebra.normalizeVector(newViewDirection);

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
		ArrayLinearAlgebra.normalizeVector(this.viewUp);
		final double dot = ArrayLinearAlgebra.dotProduct(this.viewUp, newViewDirection);
		final double newViewUp[] = {
				this.viewUp[0] - newViewDirection[0] * dot,
				this.viewUp[1] - newViewDirection[1] * dot,
				this.viewUp[2] - newViewDirection[2] * dot };
		ArrayLinearAlgebra.normalizeVector(newViewUp);

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
		ArrayLinearAlgebra.normalizeVector(newViewDirection);

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
		ArrayLinearAlgebra.normalizeVector(this.viewUp);
		final double dot = ArrayLinearAlgebra.dotProduct(this.viewUp, newViewDirection);
		final double newViewUp[] = {
				this.viewUp[0] - newViewDirection[0] * dot,
				this.viewUp[1] - newViewDirection[1] * dot,
				this.viewUp[2] - newViewDirection[2] * dot };
		ArrayLinearAlgebra.normalizeVector(newViewUp);

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

		atomGeometry = new AtomGeometry();
		// atomGeometry.setForm( Geometry.FORM_LINES ); // JLM DEBUG
		defaultGeometry.put(ComponentType.ATOM, atomGeometry);
		bondGeometry = new BondGeometry();
		bondGeometry.setShowOrder(true);
		// bondGeometry.setForm( Geometry.FORM_LINES ); // JLM DEBUG
		// bondGeometry.setShowOrder( true ); // JLM DEBUG
		defaultGeometry.put(ComponentType.BOND, bondGeometry);
		chainGeometry = new ChainGeometry();
		chainGeometry.setForm(Geometry.FORM_THICK); // JLM
		// DEBUG
		defaultGeometry.put(ComponentType.CHAIN, chainGeometry);

		VFAppBase.sgetSceneController().setDefaultGeometry(defaultGeometry);

		// Set up a GL Canvas
		this.glCapabilities = new GLCapabilities(GLProfile.getDefault());
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
			JoglSceneNode sceneNode = (JoglSceneNode)struct.getStructureMap().getUData();
			JoglSceneNode.RenderablesMap map = sceneNode.renderables;
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

		currentProgram = 0;
		vertexShader = 0;
		fragmentShader = 0;
		shaderProgram = 0;

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

	public IntBuffer tmpIntBufferOne = Buffers.newDirectIntBuffer(1);

	public boolean supportsShaderPrograms = false;

	/**
	 * GLEventListener interface - init method: Initialize the GlGeometryViewer
	 * class.
	 */
	public void init(final GLAutoDrawable drawable)
	{
		
		System.err.println("IN GlGeometryViewer - init " + drawable.getClass().getSimpleName());
		
		this.drawable = drawable;
		
		Class c = drawable.getClass();
		
	

		
		if ( GLCanvas.class.isAssignableFrom(c)) {				
			drawableViewer = (GLCanvas) drawable;
			System.err.println("we got a drawable viewer!");
		} else {
			System.err.println("??? not a GlGeometryViewer, but " + drawable.getClass().getName());
		}
		
		if (DebugState.isDebug())
			
			drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));

		if (DebugState.isTrace())
		    drawable.setGL(new TraceGL2(drawable.getGL().getGL2(), System.err));

		//
		//		 Set up JOGL (OpenGL)
		//

		final GL gl = drawable.getGL();
		final GL2 gl2 = gl.getGL2();
		
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
		gl.glEnable(GL2.GL_LIGHTING);
		gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, new float[] { 0.8f, 0.8f,
				0.8f, 1.0f }, 0);
		gl.glEnable(GL2.GL_LIGHT0);

		// Rendering
		gl.glEnable(GL2.GL_NORMALIZE); 
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl2.glShadeModel(GL2.GL_SMOOTH);

		checkSetShaderSupport();

		gl.glLineWidth(3.0f);
		gl2.glPointSize(10f);
		gl.glCullFace(GL.GL_BACK);
		gl2.glPolygonMode(GL.GL_FRONT, GL2.GL_FILL);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		
		//
		// Add mouse listeners
		//
				
		
		if ( drawableViewer != null){
			drawableViewer.addMouseListener(this);
			drawableViewer.addMouseMotionListener(this);
		}

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
			//this.drawable.;
			//this.repaint();
			
			//this.repaint();
			if ( drawableViewer != null)
				drawableViewer.repaint();
			
		}
	}

	private boolean needsPick = false;

	private static final double fovy = 45.0;
	private static final double zNear = 1.0; // 1.0, 0.1
	private static final double zFar = 10000.0; // 80.0, 10000.0

	public void requestPick() {
		this.needsPick = true;
		if (this.drawable != null && !AppBase.backgroundScreenshotOnly) {
			//this.drawable.display();
			//this.repaint();
			
			if ( drawableViewer != null)
				drawableViewer.repaint();
		}
	}

	public void requestPickAndRepaint() {
		this.needsPick = true;
		this.needsRepaint = true;
		if (this.drawable != null && !AppBase.backgroundScreenshotOnly) {
			//this.drawable.display();
			//this.repaint();
			if ( drawableViewer != null)
				drawableViewer.repaint();
		}
	}

	/**
	 * GLEventListener interface - display method:
	 */
	public void display(final GLAutoDrawable drawable) {
		try {
			final GL gl = drawable.getGL();
			final GL2 gl2 = gl.getGL2();
			final SceneController sceneController = VFAppBase.sgetSceneController();

			if (this.needsPick && !this.isScreenshotRequested)
				this.drawOrPick(gl, this.glu, this.glut, drawable, true, true, true);
			// needs to be called twice in the case of a pick event. (?)


			else
			{ // draw

				if (sceneController.isDebugEnabled()
						&& sceneController.getDebugSettings().isAntialiasingEnabled) {
					gl2.glClearAccum(this.backgroundColor[0],
							this.backgroundColor[1], this.backgroundColor[2],
							this.backgroundColor[3]);
					gl.glClear(GL2.GL_ACCUM_BUFFER_BIT);

					gl2.glMatrixMode(GL2.GL_MODELVIEW);
					gl2.glLoadIdentity();

					for (int jitter = 0; jitter < sceneController.getDebugSettings().JITTER_ARRAY.length; jitter++) {
						JoglUtils.accPerspective(
								gl,
								this.glu,
								this.glut,
								50f,
								1.0 / this.aspect,
								zNear,
								zFar,
								0,
								0,
								sceneController.getDebugSettings().blurFactor
								* sceneController.getDebugSettings().JITTER_ARRAY[jitter][0],
								sceneController.getDebugSettings().blurFactor
								* sceneController.getDebugSettings().JITTER_ARRAY[jitter][1],
								5f);

						this.drawOrPick(gl, this.glu, this.glut, drawable,
								false, false, false);
						// gl.glFlush();
						gl2.glAccum(GL2.GL_ACCUM, 1f / sceneController
								.getDebugSettings().JITTER_ARRAY.length);
						gl.glFlush();
					}
					gl2.glAccum(GL2.GL_RETURN, 1);
				}

				else
				{
					if (this.needsRepaint || (!this.needsPick && !this.needsRepaint))
					{
						this.drawOrPick(gl, this.glu, this.glut, drawable,
								true, true, false);
						drawable.swapBuffers();
					}
				}
			}

			this.needsRepaint = this.needsPick = false;

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
			final boolean canClearModelviewMatrix, final boolean isPick)
	{
		if (this.isScreenshotRequested)
		{
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
			tr.setImageBuffer(GL2.GL_BGR, GL.GL_UNSIGNED_BYTE, imageBuffer);
			tr.trPerspective(fovy, 1.0 / this.aspect,
					zNear, zFar);

			do
			{
				tr.beginTile(gl);

				gl.glFlush();
				this.drawScene(gl, glu, glut, false, canClearModelviewMatrix,
						null, null, false);
				gl.glFlush();
			} while (tr.endTile(gl));

			ImageUtil.flipImageVertically(image);

			this.reshape(drawable, 0, 0, oldViewportWidth, oldViewportHeight);

			this.screenshot = image;
			this.isScreenshotRequested = false;
			this.requestRepaint(); // allow the image to be repainted with
			// the old aspects.

			System.gc();
			System.gc();
			System.gc();
		}

		else
		{
			Point mouseLocationInPanel = null;
			if (isPick && this.mouseLocationInPanel != null)
				mouseLocationInPanel = (Point) this.mouseLocationInPanel.clone();

			MouseEvent mousePickEvent = null;
			if (isPick && this.pickMouseEvent != null)
			{
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

	protected void drawScene(final GL gl, final GLU glu, final GLUT glut,
			final boolean canModifyProjectionMatrix,
			final boolean canClearModelviewMatrix,
			final MouseEvent mousePickEvent, final Point mouseLocationInPanel,
			boolean isPick)
	{

		GL2 gl2 = gl.getGL2();
		
		synchronized (this.renderablesToDestroy)
		{
			for (DisplayListRenderable renderable : renderablesToDestroy)
				renderable.destroy(gl, glu, glut);

			renderablesToDestroy.clear();
		}

		synchronized (this.simpleDisplayListsToDestroy)
		{
			for (Integer list : simpleDisplayListsToDestroy)
				gl2.glDeleteLists(list.intValue(), 1);

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
				gl2.glMatrixMode(GL2.GL_PROJECTION);

				gl2.glLoadIdentity();
			}

			if (isPick)
			{
				gl.glDisable(GL2.GL_LIGHTING);
				gl2.glShadeModel(GL2.GL_FLAT);

				if (this.supportsShaderPrograms && currentProgram != 0
						&& shaderProgram != 0)
				{
					gl2.glUseProgram(0);
					currentProgram = 0;
				}

			}

			else
			{
				this.backBufferContainsPickData = false;

				gl.glEnable(GL2.GL_LIGHTING);
				gl2.glShadeModel(GL2.GL_SMOOTH);

				if (this.supportsShaderPrograms)
				{
					boolean successful = true;
					if (vertexShader == 0)
						successful = this.createVertexShader(gl, glu, glut);

					if (fragmentShader == 0 && successful)
						successful = this.createFragmentShader(gl, glu, glut);

					if (shaderProgram == 0 && successful)
					{
						successful = this.createShaderProgram(gl, glu, glut);
						currentProgram = shaderProgram;
					}

					if (currentProgram == 0 && shaderProgram != 0 && successful)
					{
						gl2.glUseProgram(shaderProgram);
						currentProgram = shaderProgram;
					}

					// if one or more steps for creating the shader failed,
					// disable shaders.
					if (!successful)
					{
						this.supportsShaderPrograms = false;
						shaderProgram = 0;
						vertexShader = 0;
						fragmentShader = 0;
					}
				}
			}

			//
			// Set up the view frustum.
			//

			if (canModifyProjectionMatrix)
				glu.gluPerspective(fovy, 1.0 / this.aspect,
						zNear, zFar);

			//
			// Set up the camera transform.
			//

			// Background

			gl.glClearColor(this.backgroundColor[0], this.backgroundColor[1],
					this.backgroundColor[2], this.backgroundColor[3]);

			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

			gl2.glMatrixMode(GL2.GL_MODELVIEW);
			if (canClearModelviewMatrix)
				gl2.glLoadIdentity();

			// Position the headlight relative to the viewpoint.
			gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, this.lightPosition, 0);


			// Set the viewpoint.
			// This is necessary for the simple viewer and protein workshop viewers' manipulation.
			// -- not the ligand explorer (which handles it differently, somehow.)
			//
			if (!ExternReferences.isLigandExplorer())
				glu.gluLookAt(this.viewEye[0], this.viewEye[1], this.viewEye[2],
						this.viewCenter[0], this.viewCenter[1], this.viewCenter[2],
						this.viewUp[0], this.viewUp[1], this.viewUp[2]);

			StructureList structures = AppBase.sgetModel().getStructures();
			for (Structure structure : structures)
			{
				boolean continuePick = false;
				try {
					gl2.glPushMatrix();
					JoglSceneNode sceneNode = (JoglSceneNode)structure.getStructureMap().getUData();
					continuePick = sceneNode.draw(gl, glu, glut, isPick, structure);
				} catch (Exception e)
				{
					if (DebugState.isDebug())
						e.printStackTrace();
				}

				gl2.glPopMatrix();

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
				boolean picked = false;

				if (this.rgbaBuffer == null)
					this.rgbaBuffer = Buffers.newDirectByteBuffer(4);

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

				picked = tempColor.color[0] + tempColor.color[1] + tempColor.color[2] != 0;

				if (picked)
				{
					final DisplayLists.UniqueColorMapValue value = DisplayLists.getDisplayLists(this.tempColor);

					if (value == null)
						Status.output(Status.LEVEL_REMARK, " "); // clear the
						// status
					else if (value.lists.structureComponent.getStructureComponentType() == ComponentType.FRAGMENT)
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


					this.lastComponentMouseWasOver = newComponent;
					this.backBufferContainsPickData = true;
				}
			}
		}

		catch (final Exception e)
		{
			if (DebugState.isDebug())
			{
				e.printStackTrace();
				System.err.flush();
			}
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
		final ComponentType scType = structureComponent.getStructureComponentType();

		if (scType == ComponentType.ATOM) {
			final Atom atom = (Atom) structureComponent;
			
			String insertionCode = "";
//			if (! atom.insertionCode.isEmpty()) { // this is a Java 1.6 method
			if (atom.insertionCode.length() > 0) {
				insertionCode = " Insertion code: " + atom.insertionCode;
			}
			Status.output(Status.LEVEL_REMARK, "Atom: " + atom.name
					+ " Residue " + atom.compound + " " + atom.authorResidue_id 
					+ insertionCode 
					+ " Chain " + atom.authorChain_id);
		} else if (scType == ComponentType.BOND) {
			final Bond bond = (Bond) structureComponent;

			final Atom a1 = bond.getAtom(0);
			final Atom a2 = bond.getAtom(1);
			
			String insertionCode1 = "";
//			if (! a1.insertionCode.isEmpty()) { // this is a Java 1.6 method
			if (a1.insertionCode.length() > 0) {
				insertionCode1 = " Insertion code: " + a1.insertionCode;
			}
			String insertionCode2 = "";
//			if (! a2.insertionCode.isEmpty()) { // this is a Java 1.6 method
			if (a2.insertionCode.length() > 0) {
				insertionCode2 = " Insertion code: " + a2.insertionCode;
			}

			Status.output(Status.LEVEL_REMARK, "Covalent bond. Atom 1: " + a1.name 
					+ " Residue: " + a1.compound + " " + a1.authorResidue_id 
					+ insertionCode1
					+ " Chain: " + a1.authorChain_id 
					+ " - Atom 2: " + a2.name 
					+ " Residue: " + a2.compound + " " + a2.authorResidue_id 
					+ insertionCode2
					+ " Chain: " + a2.authorChain_id);

		} else if (scType == ComponentType.RESIDUE) {
			final Residue r = (Residue) structureComponent;
			final Fragment f = r.getFragment();
			final ComponentType conformationTypeIdentifier = f.getConformationType();
			String conformationType = "Unknown";
			if (conformationTypeIdentifier == ComponentType.COIL) {
				conformationType = "Coil";
			} else if (conformationTypeIdentifier == ComponentType.HELIX) {
				conformationType = "Helix";
			} else if (conformationTypeIdentifier == ComponentType.STRAND) {
				conformationType = "Strand";
			} else if (conformationTypeIdentifier == ComponentType.TURN) {
				conformationType = "Turn";
			}

			String insertionCode = "";
//			if (! r.getInsertionCode().isEmpty()) { // this is Java 6 code
			if (r.getInsertionCode().length() > 0) {
				insertionCode = " Insertion code: " + r.getInsertionCode();
			}
			
			Status.output(Status.LEVEL_REMARK, "Residue: " + r.getCompoundCode() + " "
						+ r.getAuthorResidueId()
						+ insertionCode
						+ " Chain: " + r.getAuthorChainId()
						+ " Conformation: "
						+ conformationType);

		} else if (scType == ComponentType.FRAGMENT) {
			final Fragment f = (Fragment) structureComponent;

			// remove all but the local name for the secondary structure class.
			ComponentType conformation = f.getConformationType();

			final String startPdbChainId = f.getResidue(0).getAuthorChainId();
			final String startPdbResidueId = String.valueOf(f.getResidue(0).getAuthorResidueId());
			final String endPdbResidueId = String.valueOf(f.getResidue(f.getResidueCount() - 1)
					.getAuthorResidueId());

			Status.output(Status.LEVEL_REMARK, conformation
					+ " Fragment: chain " + startPdbChainId
					+ " from residue " + startPdbResidueId
					+ " to residue " + endPdbResidueId);

		} else if (scType == ComponentType.CHAIN) {
			final Chain c = (Chain) structureComponent;

			Status.output(Status.LEVEL_REMARK, "Chain: " + c.getAuthorChainId()
					+ " backbone");
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

		if ((e.getModifiers() & InputEvent.CTRL_MASK) == 0
				&& (e.getModifiers() & InputEvent.SHIFT_MASK) == 0
				&& (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			// ROTATE

			// Get a rotation delta using a virtual sphere mapping.
			final double rotDelta[] = { 0.0f, 1.0f, 0.0f, 0.0f };
			
			// PR this will create an invalid rotDelta
			if (prevMouseX == x && prevMouseY == y) {
//				System.out.println("GlGeometryViewer: pX:" + prevMouseX + " pY:" + prevMouseY + " x:" + x + " y" + y);
				return;
			}
			this.virtualSphere.compute(this.prevMouseX, this.prevMouseY, x, y,
					rotDelta);
			
			 for(int i = 0; i < rotDelta.length; i++) {
			 if(Double.isNaN(rotDelta[i])) { System.err.println("NaN1");System.err.flush(); } }
			

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
			//
			 if (Double.isNaN(viewDirection[0]) ||
			 Double.isNaN(viewDirection[1]) || Double.isNaN(viewDirection[2])) {
			 System.err.println("NaN2");System.err.flush(); }
			 

			ArrayLinearAlgebra.normalizeVector(viewDirection);
			//
			  if (Double.isNaN(viewDirection[0]) ||
			  Double.isNaN(viewDirection[1]) || Double.isNaN(viewDirection[2])) {
			  System.err.println("NaN3");System.err.flush(); }
			 

			// Construct the viewRight vector (ie: viewDirection x viewUp).
			final double viewRight[] = { 1.0f, 0.0f, 0.0f };
			ArrayLinearAlgebra.crossProduct(viewDirection, this.viewUp, viewRight);
			//
			  if (Double.isNaN(viewRight[0]) || Double.isNaN(viewRight[1]) ||
			  Double.isNaN(viewRight[2])) { System.err.println("NaN4");System.err.flush(); }
			 

			ArrayLinearAlgebra.normalizeVector(viewRight);

			//
			  if (Double.isNaN(viewRight[0]) || Double.isNaN(viewRight[1]) ||
			  Double.isNaN(viewRight[2])) { System.err.println("NaN5");System.err.flush(); }
			

			// Construct the virtual-sphere-to-view rotation matrix
			// (transpose)
			final double viewMatrix[] = { viewRight[0], this.viewUp[0],
					viewDirection[0], 0.0f, viewRight[1], this.viewUp[1],
					viewDirection[1], 0.0f, viewRight[2], this.viewUp[2],
					viewDirection[2], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };

			//
			  for(int i = 0; i < viewMatrix.length; i++) {
			  if(Double.isNaN(viewMatrix[i])) { System.err.println("NaN6");System.err.flush(); } }
			

			// Transform the virtual sphere axis's coordinate system
			final double vsAxis[] = { rotDelta[1], rotDelta[2], rotDelta[3] };
			ArrayLinearAlgebra.matrixRotate(viewMatrix, vsAxis);
			rotDelta[1] = vsAxis[0];
			rotDelta[2] = vsAxis[1];
			rotDelta[3] = vsAxis[2];

			//
			 for(int i = 0; i < rotDelta.length; i++) {
			 if(Double.isNaN(rotDelta[i])) { System.err.println("NaN7");System.err.flush(); } }
			 

			// NOW we can apply the transformed rotation to the view!
			// Compute the new viewEye.
			// Translate to the rotationCenter.
			this.viewEye[0] -= this.rotationCenter[0];
			this.viewEye[1] -= this.rotationCenter[1];
			this.viewEye[2] -= this.rotationCenter[2];
			ArrayLinearAlgebra.angleAxisRotate(rotDelta, this.viewEye);
			// Translate back.
			this.viewEye[0] += this.rotationCenter[0];
			this.viewEye[1] += this.rotationCenter[1];
			this.viewEye[2] += this.rotationCenter[2];

			//
			  for(int i = 0; i < viewEye.length; i++) {
			  if(Double.isNaN(viewEye[i])) { System.err.println("NaN8");System.err.flush(); } }
			 

			// Compute the new viewCenter.
			// Translate to the rotationCenter.
			this.viewCenter[0] -= this.rotationCenter[0];
			this.viewCenter[1] -= this.rotationCenter[1];
			this.viewCenter[2] -= this.rotationCenter[2];
			ArrayLinearAlgebra.angleAxisRotate(rotDelta, this.viewCenter);
			// Translate back.
			this.viewCenter[0] += this.rotationCenter[0];
			this.viewCenter[1] += this.rotationCenter[1];
			this.viewCenter[2] += this.rotationCenter[2];

			//
			  for(int i = 0; i < viewCenter.length; i++) {
			  if(Double.isNaN(viewCenter[i])) { System.err.println("NaN9");System.err.flush(); } }
			 

			// Compute the new viewUp.
			// (note that we do not translate to the rotation center first
			// because viewUp is a direction vector not an absolute vector!)
			ArrayLinearAlgebra.angleAxisRotate(rotDelta, this.viewUp);

			//
			 for(int i = 0; i < viewUp.length; i++) {
			 if(Double.isNaN(viewUp[i])) { System.err.println("NaN10");System.err.flush(); } }
			 

			ArrayLinearAlgebra.normalizeVector(this.viewUp);

			//
			  for(int i = 0; i < viewUp.length; i++) {
			  if(Double.isNaN(viewUp[i])) { System.err.println("NaN11");System.err.flush(); } }
			
		} else if ((e.getModifiers() & InputEvent.CTRL_MASK) == 0
				&& (e.getModifiers() & InputEvent.SHIFT_MASK) != 0
				|| (e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
			// DOLLY IN/OUT

			// Compute normalized direction vector from viewEye to
			// viewCenter.

			v3d[0] = this.viewCenter[0] - this.viewEye[0];
			v3d[1] = this.viewCenter[1] - this.viewEye[1];
			v3d[2] = this.viewCenter[2] - this.viewEye[2];

			ArrayLinearAlgebra.normalizeVector(v3d);

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

		} else if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0
				|| (e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
			// TRANSLATE LEFT/RIGHT AND UP/DOWN

			// Compute a left-right direction vector from the current view
			// vectors (ie: cross product of viewUp and viewCenter-viewEye).

			// Compute view direction vector (v3d2).
			v3d2[0] = this.viewCenter[0] - this.viewEye[0];
			v3d2[1] = this.viewCenter[1] - this.viewEye[1];
			v3d2[2] = this.viewCenter[2] - this.viewEye[2];
			ArrayLinearAlgebra.normalizeVector(v3d2);

			// Compute left-right direction vector (v3d2 x viewUp).
			ArrayLinearAlgebra.crossProduct(v3d2, this.viewUp, v3d);
			ArrayLinearAlgebra.normalizeVector(v3d);

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
		VFAppBase.sgetSceneController().resetView(forceRecalculation);
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
		this.backgroundColor = black;

		if (AppBase.sgetModel().hasStructures())
		{
			for (final Structure s : AppBase.sgetModel().getStructures())
			{
				final StructureMap sm = s.getStructureMap();
				final StructureStyles ss = sm.getStructureStyles();
				final JoglSceneNode sn = (JoglSceneNode)sm.getUData();
				sn.regenerateGlobalList();

				sn.clearRenderables();
				sn.clearLabels();

				for (Chain c : sm.getChains())
				{
						if (c.getResidueCount() > 0
								&& (c.getResidue(0).getClassification() == Residue.Classification.AMINO_ACID ||
										c.getResidue(0).getClassification() == Residue.Classification.NUCLEIC_ACID)) {
						ss.setVisible(c, true);
					} else {
						ss.setVisible(c, false);
					}

					for (Fragment f : c.getFragments())
					{
						for (Residue r : f.getResidues())
						{
							Vector<Atom> atoms = r.getAtoms();
							for (Atom a : atoms)
							{
									if (r.getClassification() != Residue.Classification.AMINO_ACID &&
											r.getClassification() != Residue.Classification.NUCLEIC_ACID
											&& !r.getCompoundCode().equals("HOH")) {
									ss.setVisible(a, true);
								} else {
									ss.setVisible(a, false);
								}
							}

							for (Bond b : sm.getBonds(atoms))
							{
									if (r.getClassification() != Residue.Classification.AMINO_ACID &&
											r.getClassification() != Residue.Classification.NUCLEIC_ACID &&
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
		structureAdded(str, RibbonForm.RIBBON_TRADITIONAL, true);
	}

	/**
	 * Internal response to 'structure added'.  Sets default ribbon form and whether or not to set the
	 * default atom styles.  Set to 'false' if you want to do your own.
	 * 
	 * @param str - the structure added
	 * @param ribbonForm - the default ribbon form
	 * @param doAtoms - whether or no to do the atom styles.
	 */
	protected void structureAdded(final Structure str, RibbonForm ribbonForm, boolean doAtoms)
	{		
		final StructureMap structureMap = str.getStructureMap();
		final StructureStyles structureStyles = structureMap
		.getStructureStyles();
		final JoglSceneNode sn = (JoglSceneNode)structureMap.getUData();

		final ChainGeometry defaultChainGeometry = (ChainGeometry) defaultGeometry.get(ComponentType.CHAIN);
		final AtomGeometry defaultAtomGeometry = (AtomGeometry) defaultGeometry.get(ComponentType.ATOM);
		final BondGeometry defaultBondGeometry = (BondGeometry) defaultGeometry.get(ComponentType.BOND);
		
		

		final ChainStyle defaultChainStyle = (ChainStyle) structureStyles.getDefaultStyle(ComponentType.CHAIN);
		final AtomStyle defaultAtomStyle = (AtomStyle) structureStyles.getDefaultStyle(ComponentType.ATOM);
		final BondStyle defaultBondStyle = (BondStyle) structureStyles.getDefaultStyle(ComponentType.BOND);
		
		

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
			// should this go after the end of the if-statement ??
			this.requestRepaint();
		}
	}
	
	/**
	 * 
	 * @param str
	 */
	public void surfaceAdded(final Structure str)
	{
		final StructureMap structureMap = str.getStructureMap();
		final JoglSceneNode sn = (JoglSceneNode)structureMap.getUData();
		
		final SurfaceStyle defaultSurfaceStyle = new SurfaceStyle();

		final SurfaceGeometry defaultSurfaceGeometry = new SurfaceGeometry();
		
		// add solid surfaces first
		for (Surface s: structureMap.getSurfaces()) {
			if (!s.isTransparent()) {
				synchronized (sn.renderables) {
					sn.renderables.put(s, 
							new DisplayListRenderable(s,defaultSurfaceStyle, defaultSurfaceGeometry));
				}
			}
		}
		
		// add transparent surfaces second
		for (Surface s: structureMap.getSurfaces()) {
			if (s.isTransparent()) {
				synchronized (sn.renderables) {
					sn.renderables.put(s, 
							new DisplayListRenderable(s,defaultSurfaceStyle, defaultSurfaceGeometry));
				}
			}
		}
		// this.requestRepaint(); // doesn't seem to update reliably
		VFAppBase.sgetGlGeometryViewer().requestRepaint();
	}

	public void surfaceRemoved(final Structure str) {
		final StructureMap structureMap = str.getStructureMap();
		JoglSceneNode sn = (JoglSceneNode)structureMap.getUData();
		
		Set<Entry<StructureComponent, DisplayListRenderable>> set = sn.renderables.entrySet();
		boolean needsRepaint = false;
		Iterator<Entry<StructureComponent, DisplayListRenderable>> iter = set.iterator();
	
		synchronized (sn.renderables) {
			while (iter.hasNext()) {
				Entry<StructureComponent, DisplayListRenderable> entry = iter.next();
				if (entry.getKey() instanceof Surface) {
					DisplayListRenderable renderable = entry.getValue();
					iter.remove();
					this.renderablesToDestroy.add(renderable);
					needsRepaint = true;
				}
			}
		}
		
		if (needsRepaint) {
		//	this.requestRepaint(); // doesn't seem to update reliably
			VFAppBase.sgetGlGeometryViewer().requestRepaint();
		}
	}
	
	public void windowActivated(final WindowEvent e)
	{
		requestRepaint();
	}

	public void windowClosed(final WindowEvent e) {
	}

	public void windowClosing(final WindowEvent e) {
	}

	public void windowDeactivated(final WindowEvent e)
	{
	//	requestRepaint();
	}

	public void windowDeiconified(final WindowEvent e) {
		requestRepaint();
	}

	public void windowIconified(final WindowEvent e) {
	//	requestRepaint();
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
			final JoglSceneNode sn = (JoglSceneNode)structure.getStructureMap().getUData();

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
		if (!ExternReferences.isLigandExplorer())
		{
			AppBase.sgetModel().clear();
			this.requestRepaint();
		}
	}

	public void newStructureAdded(final Structure struc) {
		this.structureAdded(struc);
	}

	protected boolean createVertexShader(final GL gl, final GLU glu,
			final GLUT glut)
	{
		GL2 gl2 = gl.getGL2();
		final String[] lines = this.getReaderLines("per_pixel_v.glsl");
		// final String[] lines = this.getReaderLines("test_v.glsl");
		final int[] lengths = this.createLengthArray(lines);

		vertexShader = gl2.glCreateShader(GL2.GL_VERTEX_SHADER);
		gl2.glShaderSource(vertexShader, lines.length, lines, lengths, 0);
		gl2.glCompileShader(vertexShader);

		if (this.do_glFinishInShaders)
			gl.glFinish();
		else
			gl.glFlush();

		final IntBuffer buf = Buffers.newDirectIntBuffer(1);
		gl2.glGetShaderiv(vertexShader, GL2.GL_INFO_LOG_LENGTH, buf);
		int logLength = buf.get(0);
		buf.rewind();
		gl2.glGetShaderiv(vertexShader, GL2.GL_COMPILE_STATUS, buf);
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
		gl2.glGetShaderInfoLog(vertexShader, logLength, length, 0, bufArray, 0);

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
			if (vertexShader > 0) {
				gl2.glDeleteShader(vertexShader);
			}
			return false;
		}

		return true;
	}

	private boolean createFragmentShader(final GL gl, final GLU glu,
			final GLUT glut) {
		final String[] lines = this.getReaderLines("per_pixel_f.glsl");
		final int[] lengths = this.createLengthArray(lines);

		GL2 gl2 =gl.getGL2();
		
		fragmentShader = gl2.glCreateShader(GL2.GL_FRAGMENT_SHADER);
		gl2.glShaderSource(fragmentShader, lines.length, lines, lengths, 0);
		gl2.glCompileShader(fragmentShader);

		if (this.do_glFinishInShaders)
			gl.glFinish();
		else
			gl.glFlush();

		final IntBuffer buf = Buffers.newDirectIntBuffer(1);
		gl2.glGetShaderiv(fragmentShader, GL2.GL_INFO_LOG_LENGTH, buf);
		int logLength = buf.get(0);
		buf.rewind();
		gl2.glGetShaderiv(fragmentShader, GL2.GL_COMPILE_STATUS, buf);
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
		gl2.glGetShaderInfoLog(fragmentShader, logLength, length, 0,
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
			if (fragmentShader > 0) {
				gl2.glDeleteShader(fragmentShader);
			}
			return false;
		}

		return true;
	}

	private boolean createShaderProgram(final GL gl, final GLU glu,
			final GLUT glut) {
		
		GL2 gl2 = gl.getGL2();
		
		shaderProgram = gl2.glCreateProgram();
		gl2.glAttachShader(shaderProgram, vertexShader);
		gl2.glAttachShader(shaderProgram, fragmentShader);
		gl2.glLinkProgram(shaderProgram);
		gl2.glUseProgram(shaderProgram);

		if (this.do_glFinishInShaders)
			gl.glFinish();
		else
			gl.glFlush();

		final IntBuffer buf = Buffers.newDirectIntBuffer(1);
		gl2.glGetProgramiv(shaderProgram, GL2.GL_INFO_LOG_LENGTH, buf);
		int logLength = buf.get(0);
		buf.rewind();
		gl2.glGetProgramiv(shaderProgram, GL2.GL_LINK_STATUS, buf);
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
		gl2.glGetProgramInfoLog(shaderProgram, logLength, length, 0,
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
			if (shaderProgram > 0) {
				gl2.glDeleteProgram(shaderProgram);
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

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}


}
