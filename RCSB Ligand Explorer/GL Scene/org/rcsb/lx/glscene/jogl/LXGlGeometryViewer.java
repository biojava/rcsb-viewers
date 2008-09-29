package org.rcsb.lx.glscene.jogl;

import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.rcsb.lx.controllers.app.LXState;
import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.controllers.update.LXUpdateController;
import org.rcsb.lx.controllers.update.LXUpdateEvent;
import org.rcsb.lx.model.Interaction;
import org.rcsb.lx.model.InteractionConstants;
import org.rcsb.lx.model.LXModel;
import org.rcsb.lx.ui.LXDocumentFrame;
import org.rcsb.lx.ui.LigandSideBar;
import org.rcsb.lx.ui.dialogs.IPickInfoReceiver;
import org.rcsb.mbt.controllers.scene.PdbToNdbConverter;
import org.rcsb.mbt.controllers.update.IUpdateListener;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.mbt.glscene.jogl.AtomGeometry;
import org.rcsb.mbt.glscene.jogl.BondGeometry;
import org.rcsb.mbt.glscene.jogl.ChainGeometry;
import org.rcsb.mbt.glscene.jogl.Constants;
import org.rcsb.mbt.glscene.jogl.DisplayListRenderable;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.rcsb.mbt.glscene.jogl.JoglSceneNode;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponentRegistry;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.BondStyle;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.attributes.LineStyle;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.attributes.StructureStylesEvent;
import org.rcsb.mbt.model.util.Algebra;
import org.rcsb.vf.glscene.jogl.VFGlGeometryViewer;


public class LXGlGeometryViewer extends VFGlGeometryViewer implements IUpdateListener
{
	private static final long serialVersionUID = -2533795368689609713L;

	public Residue currentLigand = null;
	
	private int numberTimesDisplayed = -1;
		
	public LXGlGeometryViewer()
	{
		this.do_glFinishInShaders = true;
		bondGeometry.setShowOrder(false);
					// This seems odd -
					// The drawn representation in the original appears to have this unset, yet it
					// is set in the code.
					//
					// I've turned it off, for now, since the bond orders don't appear to be correct
					// if they're shown.
					//
					// 	rickb - 23-May-08
	}

	/**
	 * Initial ligand has been reset - request it to be redrawn
	 * 
	 * We have to do this because of the staged way displays take place -
	 * once numberTimesDisplay gets to 3, displayInitialLigand is locked out.
	 */
	public void requestRedrawInitialLigand()
	{
		this.numberTimesDisplayed = 0;
		requestRepaint();
	}
	
	private void displayInitialLigand()
	{
		LXDocumentFrame activeFrame = LigandExplorer.sgetActiveFrame();
		LXModel model = LigandExplorer.sgetModel();
		LigandSideBar sidebar = activeFrame.getLigandSideBar();
		Vector ligandList = sidebar.getLigandList();
		if (ligandList != null)
		{
			final int ligSize = ligandList.size();
			final String initialLigand = model.getInitialLigand();
			
			if(initialLigand != null)
			{
				for(int i = 0; i < ligSize; i++)
				{
					Residue r = (Residue)ligandList.get(i);
					if(r.toString().toLowerCase().indexOf(initialLigand.toLowerCase()) >= 0)
					{
						sidebar.getLigandJList().setSelectedIndex(i);
						activeFrame.getLigandSideBar().applyButton.doClick();
					}
				}
			}
		}
	}

	@Override
	public void display(final GLAutoDrawable drawable)
	{
		final LXModel model = LigandExplorer.sgetModel();
		
		final GL gl = drawable.getGL();

		// clear and bail if there's nothing to render.
		if (model.getStructures() == null) {
			gl.glClearColor(this.backgroundColor[0],
					this.backgroundColor[1], this.backgroundColor[2],
					this.backgroundColor[3]);

			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
			drawable.swapBuffers();
			return;
		}


		super.display(drawable);
		try
		{
			if(this.numberTimesDisplayed < 3) {
				this.numberTimesDisplayed++;
			}
			if(this.numberTimesDisplayed == 1) {
				displayInitialLigand();
			} else if(this.numberTimesDisplayed == 0) {
				this.requestRepaint();	// make sure it reaches the above logic
			}
		}
		
		catch (final Exception e)
		{
			e.printStackTrace();
			System.err.flush();
	
			this.screenshotFailed = true;
		}
	}
	
	@Override
	public void mouseClicked(final MouseEvent e)
	{
		super.mouseClicked(e);
		this.reportClickToDialog();
	}
	
	private void reportClickToDialog()
	{
		final IPickInfoReceiver dialog = LigandExplorer.sgetActiveFrame().getDisplayDialog();
		if (dialog != null) {
			double[] point = null;
			String description = null;

			if (this.lastComponentMouseWasOver != null) {
				if (this.lastComponentMouseWasOver.getStructureComponentType() == StructureComponentRegistry.TYPE_ATOM) {
					final Atom a = (Atom) this.lastComponentMouseWasOver;

					final PdbToNdbConverter converter = a.structure.getStructureMap()
							.getPdbToNdbConverter();

					String chainId;
					String resId;
					final Object[] pdbIds = converter.getPdbIds(a.chain_id,
							new Integer(a.residue_id));
					if (pdbIds == null) {
						chainId = a.chain_id;
						resId = a.residue_id + "";
					} else {
						chainId = (String) pdbIds[0];
						resId = (String) pdbIds[1];
					}

					point = a.coordinate;
					description = "Atom: "
							+ (chainId == null ? "" : chainId + "/") + resId
							+ "/" + a.number;
				} else if (this.lastComponentMouseWasOver
						.getStructureComponentType() == StructureComponentRegistry.TYPE_BOND) {
					final Bond b = (Bond) this.lastComponentMouseWasOver;
					final Atom a1 = b.getAtom(0);
					final Atom a2 = b.getAtom(1);

					final PdbToNdbConverter converter = a1.structure
							.getStructureMap().getPdbToNdbConverter();

					String chainId1;
					String resId1;
					Object[] pdbIds = converter.getPdbIds(a1.chain_id,
							new Integer(a1.residue_id));
					if (pdbIds == null) {
						chainId1 = a1.chain_id;
						resId1 = a1.residue_id + "";
					} else {
						chainId1 = (String) pdbIds[0];
						resId1 = (String) pdbIds[1];
					}

					String chainId2;
					String resId2;
					pdbIds = converter.getPdbIds(a2.chain_id, new Integer(
							a2.residue_id));
					if (pdbIds == null) {
						chainId2 = a2.chain_id;
						resId2 = a2.residue_id + "";
					} else {
						chainId2 = (String) pdbIds[0];
						resId2 = (String) pdbIds[1];
					}

					point = new double[] {
							(a1.coordinate[0] + a2.coordinate[0]) / 2,
							(a1.coordinate[1] + a2.coordinate[1]) / 2,
							(a1.coordinate[2] + a2.coordinate[2]) / 2 };
					description = "Bond: between atom "
							+ (chainId1 == null ? "" : chainId1 + "/") + resId1
							+ "/" + a1.number + " and atom "
							+ (chainId2 == null ? "" : chainId2 + "/") + resId2
							+ "/" + a2.number;
				} 
				
				else if (this.lastComponentMouseWasOver
						.getStructureComponentType() == StructureComponentRegistry.TYPE_RESIDUE)
				{
					final Residue r = (Residue) this.lastComponentMouseWasOver;
					Atom a = r.getAlphaAtom();

					final PdbToNdbConverter converter = a.structure.getStructureMap()
							.getPdbToNdbConverter();

					String chainId;
					String resId;
					final Object[] pdbIds = converter.getPdbIds(a.chain_id,
							new Integer(a.residue_id));
					if (pdbIds == null)
					{
						chainId = a.chain_id;
						resId = a.residue_id + "";
					}
					
					else
					{
						chainId = (String) pdbIds[0];
						resId = (String) pdbIds[1];
					}

					if (a == null)
						a = r.getAtom(r.getAtomCount() / 2);

					point = a.coordinate;
					description = "Residue: "
							+ (chainId == null ? "" : chainId + "/") + resId
							+ "/" + r.getCompoundCode();
				}
			}

			dialog.processPick(point, description);
		}
	}

	/**
	 * Control the camera motion.
	 * 
	 * Sigh.  Ok, this does essentially the same thing as the base class, but it does it on
	 * the SceneNode view vectors, rather than the built-in carried vectors.
	 * Very annoying.
	 * This should be refactorable, but not now.
	 * 
	 * Deliberately hides base version.
	 * 
	 * 20-May-08 - rickb
	 */
	@Override
	public void mouseDragged(final MouseEvent e) {
		final int x = e.getX();
		final int y = e.getY();
		final Dimension size = e.getComponent().getSize();

		final double v3d[] = { 0.0f, 0.0f, 0.0f };
		final double v3d2[] = { 0.0f, 0.0f, 0.0f };
		// final double v3d3[] = { 0.0f, 0.0f, 0.0f };
		// final double r4d[] = { 0.0f, 0.0f, 0.0f, 0.0f };
		// final double r4d2[] = { 0.0f, 0.0f, 0.0f, 0.0f };

		// if (this.lastComponentMouseWasOver != null) {
		Structure struc = null;
		LXModel model = LigandExplorer.sgetModel();
		if (this.lastComponentMouseWasOver == null)
		{
			if (model.hasStructures())
				struc = model.getStructures().get(0);

		} else {
			struc = this.lastComponentMouseWasOver.structure;
		}
		if (struc == null) {
			return;
		}

		// disable the movement animation if it's active.
		if (LXState.movementThread != null) {
			LXState.movementThread.terminate();
		}

		final StructureMap sm = struc.getStructureMap();
		final LXSceneNode sn = (LXSceneNode)sm.getSceneNode();

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
			final double viewDirection[] = { sn.viewCenter[0] - sn.viewEye[0],
					sn.viewCenter[1] - sn.viewEye[1],
					sn.viewCenter[2] - sn.viewEye[2] };
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
			Algebra.crossProduct(viewDirection, sn.viewUp, viewRight);
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
			final double viewMatrix[] = { viewRight[0], sn.viewUp[0],
					viewDirection[0], 0.0f, viewRight[1], sn.viewUp[1],
					viewDirection[1], 0.0f, viewRight[2], sn.viewUp[2],
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
			sn.viewEye[0] -= sn.rotationCenter[0];
			sn.viewEye[1] -= sn.rotationCenter[1];
			sn.viewEye[2] -= sn.rotationCenter[2];
			Algebra.angleAxisRotate(rotDelta, sn.viewEye);
			// Translate back.
			sn.viewEye[0] += sn.rotationCenter[0];
			sn.viewEye[1] += sn.rotationCenter[1];
			sn.viewEye[2] += sn.rotationCenter[2];

			/*
			 * for(int i = 0; i < viewEye.length; i++) {
			 * if(Double.isNaN(viewEye[i])) { System.err.flush(); } }
			 */

			// Compute the new viewCenter.
			// Translate to the rotationCenter.
			sn.viewCenter[0] -= sn.rotationCenter[0];
			sn.viewCenter[1] -= sn.rotationCenter[1];
			sn.viewCenter[2] -= sn.rotationCenter[2];
			Algebra.angleAxisRotate(rotDelta, sn.viewCenter);
			// Translate back.
			sn.viewCenter[0] += sn.rotationCenter[0];
			sn.viewCenter[1] += sn.rotationCenter[1];
			sn.viewCenter[2] += sn.rotationCenter[2];

			/*
			 * for(int i = 0; i < viewCenter.length; i++) {
			 * if(Double.isNaN(viewCenter[i])) { System.err.flush(); } }
			 */

			// Compute the new viewUp.
			// (note that we do not translate to the rotation center first
			// because viewUp is a direction vector not an absolute vector!)
			Algebra.angleAxisRotate(rotDelta, sn.viewUp);

			/*
			 * for(int i = 0; i < viewUp.length; i++) {
			 * if(Double.isNaN(viewUp[i])) { System.err.flush(); } }
			 */

			Algebra.normalizeVector(sn.viewUp);

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

			v3d[0] = sn.viewCenter[0] - sn.viewEye[0];
			v3d[1] = sn.viewCenter[1] - sn.viewEye[1];
			v3d[2] = sn.viewCenter[2] - sn.viewEye[2];

//			final double length = Algebra.vectorLength(v3d); // Get
			// length
			// before
			// normalize!

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

			sn.viewEye[0] += v3d[0];
			sn.viewEye[1] += v3d[1];
			sn.viewEye[2] += v3d[2];
			sn.viewCenter[0] += v3d[0];
			sn.viewCenter[1] += v3d[1];
			sn.viewCenter[2] += v3d[2];
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
			v3d2[0] = sn.viewCenter[0] - sn.viewEye[0];
			v3d2[1] = sn.viewCenter[1] - sn.viewEye[1];
			v3d2[2] = sn.viewCenter[2] - sn.viewEye[2];
//			final double length = Algebra.vectorLength(v3d2); // Get
			// length
			// before
			// norm!
			Algebra.normalizeVector(v3d2);

			// Compute left-right direction vector (v3d2 x viewUp).
			Algebra.crossProduct(v3d2, sn.viewUp, v3d);
			Algebra.normalizeVector(v3d);

			// Compute a deltaX and deltaY that provide a nice motion speed,
			// then multiply the direction vector by the deltas.

			final double deltaX = 30.0 * ((double) (this.prevMouseX - x) / (double) size.width);
			final double deltaY = 30.0 * ((double) (y - this.prevMouseY) / (double) size.height);

			// Add the deltaX portion of the left-right vector
			// to the deltaY portion of the up-down vector
			// to get our relative offset vector.
			final double shiftX = (v3d[0] * deltaX) + (sn.viewUp[0] * deltaY);
			final double shiftY = (v3d[1] * deltaX) + (sn.viewUp[1] * deltaY);
			final double shiftZ = (v3d[2] * deltaX) + (sn.viewUp[2] * deltaY);

			// Add the resulting offsets to viewEye and viewCenter.

			sn.viewEye[0] += shiftX;
			sn.viewEye[1] += shiftY;
			sn.viewEye[2] += shiftZ;
			sn.viewCenter[0] += shiftX;
			sn.viewCenter[1] += shiftY;
			sn.viewCenter[2] += shiftZ;
		}

		this.prevMouseX = x;
		this.prevMouseY = y;

		this.requestRepaint();
		// }
	}
	
	/**
	 * mouseMoved method.
	 */
	@Override
	public void mouseMoved(final MouseEvent e)
	{
		this.mouseLocationInPanel = e.getPoint();
		super.mouseMoved(e);
	}
	
	/**
	 * Get the scenebounds and recalculate the view
	 * 
	 * Deliberately hides base version
	 */
	@Override
	public void resetView(boolean forceRecalculation, boolean repaint)
	{
		LigandExplorer.sgetActiveFrame().getSceneController().resetView(forceRecalculation);
		if (repaint)
			requestRepaint();
	}
	
	@Override
	public void structureAdded(final Structure str)
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

		defaultChainGeometry.setRibbonForm(ChainGeometry.RIBBON_SIMPLE_LINE);
		defaultChainGeometry.setRibbonsAreSmoothed(true);

		// chains
		final int chainCount = structureMap.getChainCount();
		LXSceneNode.RenderablesMap renderables = sn.getRenderablesMap();
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

			synchronized (renderables)
			{
				renderables.put(c, new DisplayListRenderable(c,
						defaultChainStyle, defaultChainGeometry));
			}
		}

		final Vector atoms = new Vector();
		final int ligCount = structureMap.getLigandCount();
		for (int i = 0; i < ligCount; i++) {
			final Residue r = structureMap.getLigandResidue(i);
			atoms.addAll(r.getAtoms());
			structureStyles.setSelected(r, true);
		}
		final Vector bonds = structureMap.getBonds(atoms);

		final int bondCount = bonds.size();
		for (int i = 0; i < bondCount; i++) {
			final Bond b = (Bond) bonds.get(i);

			// set the default style
			structureStyles.setStyle(b, defaultBondStyle);

			// ignore invisible bonds...
			if (!structureStyles.isVisible(b)) {
				continue;
			}

			synchronized (renderables)
			{
				renderables.put(b, new DisplayListRenderable(b,
						defaultBondStyle, defaultBondGeometry));
			}
		}

		final int atomCount = atoms.size();
		for (int i = 0; i < atomCount; i++) {
			final Atom a = (Atom) atoms.get(i);

			// set the default style
			structureStyles.setStyle(a, defaultAtomStyle);

			// ignore invisible atoms...
			if (!structureStyles.isVisible(a)) {
				continue;
			}

			synchronized (renderables)
			{
				renderables.put(a, new DisplayListRenderable(a,
						defaultAtomStyle, defaultAtomGeometry));
			}
		}

		// this.requestRepaint();
	}

	public void setLigand(final Residue res)
	{
		this.currentLigand = res;
	}
	
	public void clearStructure(boolean transitory)
	{
		super.clearStructure();
		if (!transitory)
			currentLigand = null;
	}
	
	public void handleModelChangedEvent(LXUpdateEvent evt)
	{
		switch(evt.action)
		{
		case CLEAR_ALL:
			clearStructure(evt.transitory);
			break;

		default:
			super.handleUpdateEvent(evt);
			break;
		}
	}
	

	// added for protein-ligand interactions
	public void ligandView(final Structure structure) {
		// if (inLigName != null && inLigName.length() > 0) {
		// // GeometryViewer gv = getGeometryViewer();
		// Transform3D trans3D = new Transform3D();
		// trans3D.set(getLigandVector(structure, inLigName));
		// this.setTransform(trans3D);
		// }

		final double[][] ligandBounds = this.getLigandBounds(structure, this.currentLigand);

		if (ligandBounds == null) {
			return;
		}

		final StructureMap sm = structure.getStructureMap();
		final LXSceneNode node = (LXSceneNode)sm.getSceneNode();

		// a water molecule is assumed to be 1.4 angstroms in "diameter". Use a
		// multiple of this to push the display out to show a reasonable amount
		// of interactions.
		final double padding = 1.4 * 9;

		double maxLigandLength = 0;
		for (int i = 0; i < ligandBounds[0].length; i++) {
			maxLigandLength += Math.pow(
					ligandBounds[0][i] - ligandBounds[1][i], 2);
		}
		maxLigandLength = Math.sqrt(maxLigandLength);

		// float[] eye = { 0.0, 0.0, maxDistance * 1.4 };
		final double[] center = {
				(ligandBounds[0][0] + ligandBounds[1][0]) / 2,
				(ligandBounds[0][1] + ligandBounds[1][1]) / 2,
				(ligandBounds[0][2] + ligandBounds[1][2]) / 2 };
		final double[] eye = { center[0], center[1],
				center[2] + maxLigandLength + padding };
		final double[] up = { 0.0f, 1.0f, 0.0f };
		// scene.lookAt(eye, scene.rotationCenter, up);

		final double[] currentOrientation = node.getEye();
		final double[] currentPosition = node.getCenter();
		final double[] currentUp = node.getUp();

		if (LXState.movementThread != null) {
			LXState.movementThread.terminate();
		}

		LXState.movementThread = new LXState.ViewMovementThread(currentOrientation,
				eye, currentPosition, center, currentUp, up, 0, 0, 0, 0);
		LXState.movementThread.start();

		requestRepaint();
	}

	// added for protein-ligand interactions
	// returned:
	// - double[0]: minimum x,y,z values
	// - double[1]: maximum x,y,z values
	// can be null if the ligand name is not found.
	private double[][] getLigandBounds(final Structure s, final Residue curLigand) {
		double maxX, maxY, maxZ;
		double minX, minY, minZ;

		maxX = maxY = maxZ = -(Double.MAX_VALUE - 2);
		minX = minY = minZ = Double.MAX_VALUE;

		final int atomCount = curLigand.getAtomCount();
		for (int j = 0; j < atomCount; j++) {

			final Atom atom_j = curLigand.getAtom(j);
			maxX = Math.max(atom_j.coordinate[0], maxX);
			maxY = Math.max(atom_j.coordinate[1], maxY);
			maxZ = Math.max(atom_j.coordinate[2], maxZ);

			minX = Math.min(atom_j.coordinate[0], minX);
			minY = Math.min(atom_j.coordinate[1], minY);
			minZ = Math.min(atom_j.coordinate[2], minZ);
		}

		return new double[][] { { minX, minY, minZ }, { maxX, maxY, maxZ } };
	}

	/**
	 * Process in incoming StructureStylesEvent.
	 */
	@Override
	public void processStructureStylesEvent(
			final StructureStylesEvent structureStylesEvent) {

	}

	public String getResidueName(final Atom atom) {
		final PdbToNdbConverter converter = atom.structure.getStructureMap()
				.getPdbToNdbConverter();
		final Object[] tmp = converter.getPdbIds(atom.chain_id, new Integer(
				atom.residue_id));
		String name = "";
		if (tmp == null) {
			// use ndb ids
			name = atom.chain_id;
			if (name.length() > 0) {
				name += " " + atom.residue_id;
			} else {
				name = atom.residue_id + "";
			}
		} else {
			// use pdb ids
			if (tmp[0] != null) {
				name = (String) tmp[0];
			}
			if (tmp[1] != null) {
				if (name.length() > 0) {
					name += " " + tmp[1];
				} else {
					name = (String) tmp[1];
				}
			}
		}

		if (name.length() > 0) {
			name = atom.compound + " (" + name + ")";
		} else {
			name = atom.compound;
		}

		return name;
	}

	public String getResidueName(final Residue residue) {
		return this.getResidueName(residue.getAtom(0));
	}

	public void setDisplayLevel(final String level) {
	}

	// public Vector3f getVectorInfo() {
	// // **JB note: hack that only works for the first structure.
	// JoglSceneNode node = Model.getSingleton().getStructures()[0]
	// .getStructureMap().getSceneNode();
	// Vector3f vecInfo = new Vector3f(x, y, zDist);
	// // System.out.println(zDis);
	// return vecInfo;
	// }

	public void processLeftPanelEvent(final Structure structure,
			final float ligwaterbondlower, final float ligwaterbondupper, final boolean ligWaterProOn, final float intLigandBondupper, final float intLigandBondlower, final boolean intLigandOn, final boolean hbondflag,
			final float hbondlower, final float hbondupper, final boolean hydroflag,
			final float hydrolower, final float hydroupper, final boolean otherflag,
			final float otherlower, final float otherupper, final boolean displayDisLabel,
			boolean saveInteractionsToFile) {
		// XXX Status.progress(0.3f, "StructureViewer adding structure...Please wait");

		PrintWriter interactionsOut = null;
		if (saveInteractionsToFile) {
			final JFileChooser chooser = new JFileChooser();
			// chooser.addChoosableFileFilter(new FileFilter() {
			// public boolean accept(File f) {
			// return true;
			// }
			//
			// public String getDescription() {
			// return "Everything";
			// }
			// });

			chooser.addChoosableFileFilter (
				new FileFilter()
				{
					@Override
					public boolean accept(final File f)
					{
						if (f.isDirectory())
							return true;
	
						final String name = f.getName();
						final String lastFour = name.length() > 4 ? name.substring(name
								.length() - 4) : null;
						if (lastFour == null) {
							return false;
						}
	
						return lastFour.equalsIgnoreCase(".txt");
					}
	
					@Override
					public String getDescription()
						{ return ".txt (tab delimited)"; }
				});

			if (chooser.showSaveDialog(LigandExplorer.sgetActiveFrame()) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (file != null) {
					try {
						if (file.getName().indexOf('.') < 0) {
							file = new File(file.getAbsolutePath() + ".txt");
						}

						interactionsOut = new PrintWriter(new java.io.FileWriter(file));

						interactionsOut
								.println("Atom 1\tAtom 2\tDistance\tType");
					} catch (final IOException e) {
						e.printStackTrace();
						return;
					}
				}
			}
		}

		if (!saveInteractionsToFile)
		{
			LXModel model = LigandExplorer.sgetModel();
			model.getInteractionMap().clear();

			final StructureMap structureMap = structure.getStructureMap();
			final StructureStyles structureStyles = structureMap.getStructureStyles();

			structureStyles.clearSelections();
			
			//
			// Notify the viewers that structures have been removed and added.
			// We don't want to remove the model, so we just use the update
			// controller to send a 'remove all' signal and then an 'add all'
			// signal.
			//
			// This is really pretty wanky - should look this over and fix.
			//
			LXDocumentFrame activeFrame = LigandExplorer.sgetActiveFrame();
			LXUpdateController update = LigandExplorer.sgetActiveFrame().getUpdateController();
			update.blockListener(activeFrame);
			update.removeStructure(true);
			update.fireStructureAdded(structure, false, true);
			update.unblockListener(activeFrame);

			final ChainStyle cs = (ChainStyle) structureStyles.getStyle(structureMap
					.getChain(0)); // **JB assume that all chain styles are the
			// same.
			cs.resetBinding(structure);
		}

		final StructureMap structureMap = structure.getStructureMap();

		// If its just sequence data lets bail out!
		if (structureMap.getAtomCount() < 0) {
			return;
		}

		// XXX Status.progress(0.5f, "Continue loading structure... please wait...");
		final int totalResidues = structureMap.getResidueCount();
		int onePercent = (int) (totalResidues / 100.0f);
		if (onePercent <= 0) {
			onePercent = 1;
		}

		// added for protein-ligand interaction. calculate interaction with H2O
		// in the binding site
		if (ligWaterProOn) {
			this.calWaterInteractions(structure, ligwaterbondlower, ligwaterbondupper, displayDisLabel, interactionsOut);
		}

		// added for protein-ligand interactions
		if (intLigandOn) {
			this.calInterLigInteractions(structure, intLigandBondlower, intLigandBondupper, displayDisLabel, interactionsOut);
		}

		// added for protein-ligand interactions
		this.calculateInteractions(structure, hbondflag, hydroflag, otherflag,
				hbondupper, hbondlower, hydroupper, hydrolower, otherupper,
				otherlower, displayDisLabel, interactionsOut);

		// Center the view at the ligand
		// Status.progress(0.95f, "Centering the view at the ligand" +
		// inLigName+"...Please wait");
		if (!saveInteractionsToFile) {
			this.ligandView(structure);
		}

		if (interactionsOut != null) {
			interactionsOut.close();
		}

		LXUpdateController update = LigandExplorer.sgetActiveFrame().getUpdateController();
		update.refreshSequencePanes();

		// XXX Status.progress(1.0f, null);
	}

	// added for protein-ligand interactions
	/**
	 * Convert a double value into a string with two digits after the decimal
	 * 
	 * @param distance
	 *            the input distance
	 * @return dist a distance in string format with two digits after the
	 *         decimal point
	 */
	public static String getDistString(final double distance) {
		final String distString = new Double(distance).toString();
		final int index = distString.indexOf('.');

		String dist = null;
		try {
			dist = distString.substring(0, index + 4);
		} catch (final Exception ex) {
			// only one digit after the decimal to begin with
			dist = distString.substring(0, index + 3);
		}
		return dist;
	}

	public void calculateInteractions(final Structure structure, boolean hbondflag,
			boolean hydroflag, boolean otherflag, final double hbondupper,
			final double hbondlower, final double hydroupper, final double hydrolower,
			final double otherupper, final double otherlower, final boolean displayDisLabel,
			final PrintWriter interactionsOut) {
		int count = 0;
		int count_hydro = 0;
		int count_other = 0;
		final Vector ligandAtoms = this.currentLigand.getAtoms();;
		final Vector proteinAtoms = new Vector();

		String interactionType = null;
		String distString = null;

		if (!hbondflag && !hydroflag && !otherflag) {
			return;
		}

		final StructureMap structureMap = structure.getStructureMap();
		final int chainCt = structureMap.getChainCount();

		for (int n = 0; n < chainCt; n++) {
			final Chain chain = structureMap.getChain(n);
			if (chain.getClassification() == Residue.Classification.AMINO_ACID) {

				for (int k = 0; k < chain.getResidueCount(); k++) {
					final Vector atoms = chain.getResidue(k).getAtoms();
					for (int o = 0; o < atoms.size(); o++) {
						proteinAtoms.add(atoms.get(o));
					}
				}

			}
		}

		final AtomGeometry ag = (AtomGeometry) GlGeometryViewer.defaultGeometry
				.get(StructureComponentRegistry.TYPE_ATOM);
		final AtomStyle as = (AtomStyle) structure.getStructureMap()
				.getStructureStyles().getDefaultStyle(
						StructureComponentRegistry.TYPE_ATOM);
		final BondGeometry bg = (BondGeometry) GlGeometryViewer.defaultGeometry
				.get(StructureComponentRegistry.TYPE_BOND);
		final BondStyle bs = (BondStyle) structure.getStructureMap()
				.getStructureStyles().getDefaultStyle(
						StructureComponentRegistry.TYPE_BOND);

		for (int i = 0; i < ligandAtoms.size(); i++) {
			double distance = 0.0;

			final Atom atom_i = (Atom) ligandAtoms.get(i);

			for (int j = i + 1; j < proteinAtoms.size(); j++) {
				final Atom atom_j = (Atom) proteinAtoms.get(j);

				/*
				 * if (!atom_i.chain_id.equals(atom_j.chain_id)) distance =
				 * distance(atom_i, atom_j);
				 */
				// String label1 = null;
				// String label2 = null;
				// String name_i = getResidueName(atom_i);
				// String name_j = getResidueName(atom_j);
				// hbond
				if (hbondflag) {

					if ((atom_i.element.equals("N") || atom_i.element
							.equals("O"))
							&& (atom_j.element.equals("N") || atom_j.element
									.equals("O"))) {

						distance = Algebra.distance(atom_i.coordinate,
								atom_j.coordinate);

						if (distance <= hbondupper && distance >= hbondlower) {
							interactionType = InteractionConstants.hydrophilicType;
							distString = LXGlGeometryViewer.getDistString(distance);

							// if (!node.isRendered(atom_j)) {
							// DisplayListRenderable renderable = new
							// DisplayListRenderable(atom_j, as, ag);
							// node.addRenderable(renderable);
							// }
							if (interactionsOut == null) {
								this.renderResidue(structureMap
										.getResidue(atom_j), as, ag, bs, bg,
										true);
							}

							count++;

							this.drawInteraction(structure, atom_i, atom_j,
									interactionType, displayDisLabel,
									distString, distance, interactionsOut);
							// label1 =
							// atom_i.chain_id
							// + ":"
							// + atom_i.compound
							// + ":"
							// + atom_i.number
							// + ":"
							// + atom_i.name;
							// label2 =
							// atom_j.chain_id
							// + ":"
							// + atom_j.compound
							// + ":"
							// + atom_j.number
							// + ":"
							// + atom_j.name;

							/*
							 * System.out.println( label1 + "\t " + label2 + "\t " +
							 * distString);
							 */

						}
					}
				}
				if (hydroflag) {

					if (atom_i.element.equals("C")
							&& atom_j.element.equals("C")) {
						distance = Algebra.distance(atom_i.coordinate,
								atom_j.coordinate);
						if (distance <= hydroupper && distance >= hydrolower) {
							interactionType = InteractionConstants.hydrophobicType;
							distString = LXGlGeometryViewer.getDistString(distance);

							count_hydro++;

							// if (!node.isRendered(atom_j)) {
							// DisplayListRenderable renderable = new
							// DisplayListRenderable(atom_j, as, ag);
							// node.addRenderable(renderable);
							// }
							if (interactionsOut == null) {
								this.renderResidue(structureMap
										.getResidue(atom_j), as, ag, bs, bg,
										true);
							}

							// Display distance label

							this.drawInteraction(structure, atom_i, atom_j,
									interactionType, displayDisLabel,
									distString, distance, interactionsOut);
						}
					}
				}
				if (otherflag) {

					if (!(atom_i.element.equals("C") && atom_j.element
							.equals("C"))
							&& !((atom_i.element.equals("N") || atom_i.element
									.equals("O")) && (atom_j.element
									.equals("N") || atom_j.element.equals("O")))) {
						distance = Algebra.distance(atom_i.coordinate,
								atom_j.coordinate);
						if (distance <= otherupper && distance >= otherlower) {
							interactionType = InteractionConstants.otherType;
							distString = LXGlGeometryViewer.getDistString(distance);

							count_other++;

							// if (!node.isRendered(atom_j)) {
							// DisplayListRenderable renderable = new
							// DisplayListRenderable(atom_j, as, ag);
							// node.addRenderable(renderable);
							// }
							if (interactionsOut == null) {
								this.renderResidue(structureMap
										.getResidue(atom_j), as, ag, bs, bg,
										true);
							}

							this.drawInteraction(structure, atom_i, atom_j,
									interactionType, displayDisLabel,
									distString, distance, interactionsOut);

						}

					}
				}

			}
		}
		// System.out.println("countH is " + count);
		// System.out.println("count hydro is " + count_hydro);
		// System.out.println("count other is " + count_other);

	}

	public void renderResidue(final Residue r, final AtomStyle as, final AtomGeometry ag,
			final BondStyle bs, final BondGeometry bg, final boolean showLabel) {
		final StructureMap sm = r.structure.getStructureMap();
		final LXSceneNode node = (LXSceneNode)sm.getSceneNode();

		final Vector atoms = r.getAtoms();
		final Vector bonds = sm.getBonds(atoms);
		for (int i = 0; i < atoms.size(); i++) {
			final Atom a = (Atom) atoms.get(i);
			if (!node.isRendered(a)) {
				final DisplayListRenderable renderable = new DisplayListRenderable(a,
						as, ag);
				node.addRenderable(renderable);
			}
		}
		for (int i = 0; i < bonds.size(); i++) {
			final Bond b = (Bond) bonds.get(i);
			if (!node.isRendered(b))
			{
				final DisplayListRenderable renderable = new DisplayListRenderable(b, bs, bg);
				node.addRenderable(renderable);
			}
		}

		if (showLabel)
		{
			Object[] tmp = sm.getPdbToNdbConverter().getPdbIds(r.getChainId(), new Integer(r.getResidueId()));
			if(tmp == null) {
				node.lxCreateAndAddLabel(r, r.getChainId() + ":"
					+ r.getCompoundCode() + r.getResidueId(), Constants.yellow);
			} else {
				node.lxCreateAndAddLabel(r, (String)tmp[0] + ":"
						+ r.getCompoundCode() + (String)tmp[1], Constants.yellow);
			}
		}

		sm.getStructureStyles().setSelected(r, true);
	}

	public void hideResidue(final Residue r) {
		final StructureMap sm = r.structure.getStructureMap();
		final JoglSceneNode node = sm.getSceneNode();

		final Vector atoms = r.getAtoms();
		final Vector bonds = sm.getBonds(atoms);
		for (int i = 0; i < atoms.size(); i++) {
			final Atom a = (Atom) atoms.get(i);
			node.removeRenderable(a);
		}
		for (int i = 0; i < bonds.size(); i++) {
			final Bond b = (Bond) bonds.get(i);
			node.removeRenderable(b);
		}

		node.removeLabel(r);

		sm.getStructureStyles().setSelected(r, false);
	}

	// added for protein-ligand intreactions
	/**
	 * Creates an interaction monitor
	 * 
	 * @param structure
	 *            Structure whose distance parameter was measured
	 * @param componentHash
	 *            Hashtable
	 * @param a
	 *            first atom
	 * @param b
	 *            second atom
	 * @param interactionType
	 * @param interactionsOut
	 *            if not null, all interactions will be written to file (the
	 *            interactions will still be (re)created visually in the
	 *            viewer).
	 */
	private void drawInteraction(final Structure structure, final Atom a, final Atom b,
			final String interactionType, final boolean displayDisLabel, final String distString, final double distDouble,
			final PrintWriter interactionsOut) {

		if (interactionsOut != null) {
			final PdbToNdbConverter converter = structure.getStructureMap()
					.getPdbToNdbConverter();
			Object[] tmp = converter.getPdbIds(a.chain_id, new Integer(
					a.residue_id));
			String chainId = null, resId = null, chainId2 = null, resId2 = null;
			if (tmp == null) {
				chainId = a.chain_id;
				resId = a.residue_id + "";
			} else {
				chainId = (String) tmp[0];
				resId = (String) tmp[1];
			}
			tmp = converter.getPdbIds(b.chain_id, new Integer(b.residue_id));
			if (tmp == null) {
				chainId2 = b.chain_id;
				resId2 = b.residue_id + "";
			} else {
				chainId2 = (String) tmp[0];
				resId2 = (String) tmp[1];
			}
			interactionsOut.println(chainId + ":" + resId + ":" + a.compound
					+ ":" + a.name + "\t" + chainId2 + ":" + resId2 + ":"
					+ b.compound + ":" + b.name + "\t" + distString + "\t"
					+ interactionType);
			return;
		}

		// create and record the interaction.
		final Interaction ia = new Interaction(a, b, interactionType, distString, distDouble);
		LigandExplorer.sgetModel().getInteractionMap().putInteraction(ia);

		final InteractionGeometry lg = new InteractionGeometry();
		final LineStyle ls = new LineStyle();
		ls.lineStyle = LineStyle.DASHED;
		
		final DisplayListRenderable renderable = new DisplayListRenderable(ia, ls, lg);

		lg.setForm(ls.lineStyle);

		float[] textColor = null;

		if (interactionType == InteractionConstants.hydrophilicType) {
			textColor = InteractionConstants.hydrophilicBondColor;
		} else if (interactionType == InteractionConstants.hydrophobicType) {
			textColor = InteractionConstants.hydrophobicBondColor;
		} else if (interactionType == InteractionConstants.otherType) {
			textColor = InteractionConstants.otherBondColor;
		} else if (interactionType == InteractionConstants.interLigandType) {
			textColor = InteractionConstants.interLigandBondColor;
		} else if (interactionType == InteractionConstants.waterMediatedType) {
			textColor = InteractionConstants.waterBondColor;
		}

		ls.setColor(textColor);
		ia.setStructure(structure);

		final StructureMap sm = structure.getStructureMap();
		final StructureStyles ss = sm.getStructureStyles();

		ss.setStyle(ia, ls);

		final LXSceneNode node = (LXSceneNode)sm.getSceneNode();

		if (displayDisLabel) {
			ls.label = distString;
		}

		node.addRenderable(renderable);
	}

	public void drawInteractionByResidue(final Structure structure, final Atom a, final Atom b,
			final String interactionType, final boolean displayDisLabel, final String distString) {

	}

	// added for protein-ligand interactions
	public void createResLabel(final Structure structure, final Atom atom) {
		final LXSceneNode node = (LXSceneNode)structure.getStructureMap().getSceneNode();

		final Residue r = structure.getStructureMap().getResidue(atom);
		final String label = structure.getStructureMap().getChain(atom).getChainId()
				+ ":" + r.getCompoundCode() + r.getResidueId();
		node.lxCreateAndAddLabel(r, label, InteractionConstants.hydrophilicBondColor);
	}

	// all below are added for protein-ligand interactions
	// public void createDistanceLabel(
	// Structure structure,
	// Atom atom_i,
	// Atom atom_j,
	// String interactionType,
	// String label) {
	//
	// JoglSceneNode node = structure.getStructureMap().getSceneNode();
	//		
	// Residue r = structure.getStructureMap().getResidue(atom);
	// String label =
	// structure.getStructureMap().getChain(atom).getChainId()
	// + ":"
	// + r.getCompoundCode()
	// + r.getResidueId();
	// node.createAndAddLabel(r, label, StructureStylesImpl.hbondColor);
	//		
	// double labelCoord[] = new double[3];
	// labelCoord[0] = (atom_i.coordinate[0] + atom_j.coordinate[0]) / 2;
	// labelCoord[1] = (atom_i.coordinate[1] + atom_j.coordinate[1]) / 2;
	// labelCoord[2] = (atom_i.coordinate[2] + atom_j.coordinate[2]) / 2;
	//
	// //System.out.println("label " + label);
	// LabelJ3D lg = new LabelJ3D(label, labelCoord);
	// lg.setCapability(BranchGroup.ALLOW_DETACH);
	//
	// ((BranchGroup) componentHash.get(structure)).addChild(lg);
	// monitors.put(lg, structure);
	// }

	public void calWaterInteractions(final Structure structure, final float lowerBound, final float upperBound,
			final boolean displayDisLabel, final PrintWriter interactionsOut) {
		final StructureMap structureMap = structure.getStructureMap();
		final int ligCount = structureMap.getLigandCount();
		int intCt = 0;
		final Vector atoms = new Vector();
		double distance = 0.0;
		String distString = null;
		final String interactionType = InteractionConstants.waterMediatedType;
		final Vector waterAtoms = new Vector();
		// HashMap uniqWater = new HashMap();
		for (int i = 0; i < ligCount; i++) {
			final int ligAtomCt = structureMap.getLigandResidue(i).getAtomCount();
			for (int j = 0; j < ligAtomCt; j++) {
				atoms.add(structureMap.getLigandResidue(i).getAtom(j));
			}
		}

		for (int m = 0; m < atoms.size(); m++) {
			final Atom atom_m = (Atom) atoms.get(m);
			final Residue residue_m = structureMap.getResidue(atom_m);
			Atom atom_n = null;
			for (int n = m + 1; n < atoms.size(); n++) {
				atom_n = (Atom) atoms.get(n);
				final Residue residue_n = structureMap.getResidue(atom_n);
				

				if ((residue_m == this.currentLigand || residue_n
						== this.currentLigand)
						&& (atom_m.compound.equals("HOH") || atom_n.compound
								.equals("HOH"))) {

					distance = Algebra.distance(atom_m.coordinate,
							atom_n.coordinate);
					distString = LXGlGeometryViewer.getDistString(distance);

					if (distance < upperBound && distance > lowerBound) {

						this.drawInteraction(structure, atom_m, atom_n,
								interactionType, displayDisLabel, distString, distance,
								interactionsOut);
						intCt++;

						if (atom_m.compound.equals("HOH")) {
							if (!waterAtoms.contains(atom_m)) {
								/*
								 * createResLabel( structure, componentHash,
								 * atom_m);
								 */
								waterAtoms.add(atom_m);
							}
						} else {
							if (!waterAtoms.contains(atom_n)) {
								/*
								 * createResLabel( structure, componentHash,
								 * atom_n);
								 */

								waterAtoms.add(atom_n);
							}
						}
					}
				}
			}
		}
		this.calWaterProInt(structure, waterAtoms, lowerBound, upperBound, displayDisLabel, interactionsOut);

		if (interactionsOut == null) {
			final LXSceneNode node = (LXSceneNode)structure.getStructureMap().getSceneNode();

			final AtomGeometry ag = (AtomGeometry) GlGeometryViewer.defaultGeometry
					.get(StructureComponentRegistry.TYPE_ATOM);
			final AtomStyle as = (AtomStyle) structure.getStructureMap()
					.getStructureStyles().getDefaultStyle(
							StructureComponentRegistry.TYPE_ATOM);
			for (int i = 0; i < waterAtoms.size(); i++) {
				final Atom a = (Atom) waterAtoms.get(i);
				if (!node.isRendered(a)) {
					final DisplayListRenderable renderable = new DisplayListRenderable(
							a, as, ag);
					node.addRenderable(renderable);
				}
			}
		}
	}

	public void calWaterProInt(final Structure structure, final Vector waterAtoms, final float lowerBound, final float upperBound,
			final boolean displayDisLabel, final PrintWriter interactionsOut) {
		final StructureMap structureMap = structure.getStructureMap();
		final int atomCt = structureMap.getAtomCount();
		Vector<Atom> proAtoms = new Vector<Atom>();
		double distance = 0.0;
		String distString = null;
		final String interactionType = InteractionConstants.waterMediatedType;
		HashSet<Residue> uniqRes = new HashSet<Residue>();
		int ct = 0;

		for (int i = 0; i < atomCt; i++) {
			final Atom atom = structureMap.getAtom(i);
			if (structureMap.getChain(atom).getClassification() ==
				Residue.Classification.AMINO_ACID)
					proAtoms.add(atom);
		}

		final LXSceneNode node = (LXSceneNode)structure.getStructureMap().getSceneNode();
		final AtomGeometry ag = (AtomGeometry) GlGeometryViewer.defaultGeometry
				.get(StructureComponentRegistry.TYPE_ATOM);
		final AtomStyle as = (AtomStyle) structure.getStructureMap()
				.getStructureStyles().getDefaultStyle(
						StructureComponentRegistry.TYPE_ATOM);
		final BondGeometry bg = (BondGeometry) GlGeometryViewer.defaultGeometry
				.get(StructureComponentRegistry.TYPE_BOND);
		final BondStyle bs = (BondStyle) structure.getStructureMap()
				.getStructureStyles().getDefaultStyle(
						StructureComponentRegistry.TYPE_BOND);

		for (int j = 0; j < waterAtoms.size(); j++) {
			final Atom atom_j = (Atom) waterAtoms.get(j);
			for (int k = 0; k < proAtoms.size(); k++) {
				final Atom atom_k = (Atom) proAtoms.get(k);
				distance = Algebra.distance(atom_j.coordinate,
						atom_k.coordinate);
				distString = LXGlGeometryViewer.getDistString(distance);
				final Residue res = structureMap.getResidue(atom_k);
				if (!uniqRes.contains(res)) {

					if (distance < upperBound && distance > lowerBound) {
						this.drawInteraction(structure, atom_j, atom_k,
								interactionType, displayDisLabel, distString, distance,
								interactionsOut);

						if (interactionsOut == null) {
							if (!node.isRendered(atom_j)) {
								final DisplayListRenderable renderable = new DisplayListRenderable(
										atom_j, as, ag);
								node.addRenderable(renderable);
							}

							this.renderResidue(res, as, ag, bs, bg, true);
						}

						ct++;
					}
					uniqRes.add(res);
				}
			}
		}
	}

	public void calInterLigInteractions(final Structure structure, final float lowerBound, final float upperBound,
			final boolean displayDisLabel, final PrintWriter interactionsOut) {
		final StructureMap structureMap = structure.getStructureMap();
		final int ligCount = structureMap.getLigandCount();
		// System.out.println("lig count is " + ligCount);
		int intCt = 0;
		final Vector<Atom> atoms = new Vector<Atom>();
		double distance = 0.0;
		String distString = null;
		final String interactionType = InteractionConstants.interLigandType;
		for (int i = 0; i < ligCount; i++) {
			final int ligAtomCt = structureMap.getLigandResidue(i).getAtomCount();
			for (int j = 0; j < ligAtomCt; j++) {
				atoms.add(structureMap.getLigandResidue(i).getAtom(j));
			}
		}

		for (int m = 0; m < atoms.size(); m++) {
			final Atom atom_m = (Atom) atoms.get(m);
			final Residue atomResidueM = structureMap.getResidue(atom_m);
			Atom atom_n = null;
			for (int n = m + 1; n < atoms.size(); n++) {
				atom_n = (Atom) atoms.get(n);
				final Residue atomResidueN = structureMap.getResidue(atom_n);
				
				if (!atom_m.compound.equals("HOH")
						&& !atom_n.compound.equals("HOH")
						&& (atomResidueM != atomResidueN) && (atomResidueM == this.currentLigand || atomResidueN == this.currentLigand)) {
					distance = Algebra.distance(atom_m.coordinate,
							atom_n.coordinate);
					distString = LXGlGeometryViewer.getDistString(distance);

					if (distance < upperBound && distance > lowerBound) {
						this.drawInteraction(structure, atom_m, atom_n,
								interactionType, displayDisLabel, distString, distance,
								interactionsOut);
						intCt++;
					}
				}
			}
		}

	}
}