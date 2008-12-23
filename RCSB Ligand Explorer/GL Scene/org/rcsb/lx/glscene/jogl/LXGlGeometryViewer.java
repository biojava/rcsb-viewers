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
package org.rcsb.lx.glscene.jogl;

import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.controllers.scene.LXViewMovementThread;
import org.rcsb.lx.controllers.update.LXUpdateEvent;
import org.rcsb.lx.model.Interaction;
import org.rcsb.lx.model.InteractionConstants;
import org.rcsb.lx.model.LXModel;
import org.rcsb.lx.ui.dialogs.IPickInfoReceiver;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.BondStyle;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.attributes.LineStyle;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.attributes.StructureStylesEvent;
import org.rcsb.mbt.model.geometry.ArrayLinearAlgebra;
import org.rcsb.mbt.model.util.PdbToNdbConverter;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;
import org.rcsb.vf.controllers.scene.ViewMovementThread;
import org.rcsb.vf.glscene.jogl.AtomGeometry;
import org.rcsb.vf.glscene.jogl.BondGeometry;
import org.rcsb.vf.glscene.jogl.ChainGeometry;
import org.rcsb.vf.glscene.jogl.Constants;
import org.rcsb.vf.glscene.jogl.DisplayListRenderable;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;
import org.rcsb.vf.glscene.jogl.JoglSceneNode;
import org.rcsb.vf.glscene.jogl.ChainGeometry.RibbonForm;


public class LXGlGeometryViewer extends GlGeometryViewer implements IUpdateListener
{
	private static final long serialVersionUID = -2533795368689609713L;

	private int numberTimesDisplayed = -1;
	public int getNumberTimesDisplayed() { return numberTimesDisplayed; }
	
	public LXGlGeometryViewer()
	{
		this.do_glFinishInShaders = true;
	}

	/**
	 * Initial ligand has been reset - request it to be redrawn
	 * 
	 * We have to do this because of the staged way displays take place -
	 * once numberTimesDisplay gets to 3, displayInitialLigand is locked out.
	 */
	public void requestRedrawInitialLigand()
	{
		if (numberTimesDisplayed > 1)
			numberTimesDisplayed = 0;
		requestRepaint();
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
/* **/
		try
		{
			if(this.numberTimesDisplayed < 2)
				this.numberTimesDisplayed++;

			else if(this.numberTimesDisplayed == 0)
				this.requestRepaint();	// make sure it reaches the above logic
		}
		
		catch (final Exception e)
		{
			e.printStackTrace();
			System.err.flush();
	
			this.screenshotFailed = true;
		}
/* **/
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
				if (this.lastComponentMouseWasOver.getStructureComponentType() == ComponentType.ATOM) {
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
						.getStructureComponentType() == ComponentType.BOND) {
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
						.getStructureComponentType() == ComponentType.RESIDUE)
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
	 * 
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

		ViewMovementThread.terminateMovementThread();

		final StructureMap sm = struc.getStructureMap();
		final LXSceneNode sn = (LXSceneNode)sm.getUData();

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

			ArrayLinearAlgebra.normalizeVector(viewDirection);
			/*
			 * if (Double.isNaN(viewDirection[0]) ||
			 * Double.isNaN(viewDirection[1]) || Double.isNaN(viewDirection[2])) {
			 * System.err.flush(); }
			 */

			// Construct the viewRight vector (ie: viewDirection x viewUp).
			final double viewRight[] = { 1.0f, 0.0f, 0.0f };
			ArrayLinearAlgebra.crossProduct(viewDirection, sn.viewUp, viewRight);
			/*
			 * if (Double.isNaN(viewRight[0]) || Double.isNaN(viewRight[1]) ||
			 * Double.isNaN(viewRight[2])) { System.err.flush(); }
			 */

			ArrayLinearAlgebra.normalizeVector(viewRight);

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
			ArrayLinearAlgebra.matrixRotate(viewMatrix, vsAxis);
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
			ArrayLinearAlgebra.angleAxisRotate(rotDelta, sn.viewEye);
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
			ArrayLinearAlgebra.angleAxisRotate(rotDelta, sn.viewCenter);
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
			ArrayLinearAlgebra.angleAxisRotate(rotDelta, sn.viewUp);

			/*
			 * for(int i = 0; i < viewUp.length; i++) {
			 * if(Double.isNaN(viewUp[i])) { System.err.flush(); } }
			 */

			ArrayLinearAlgebra.normalizeVector(sn.viewUp);

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
			ArrayLinearAlgebra.normalizeVector(v3d2);

			// Compute left-right direction vector (v3d2 x viewUp).
			ArrayLinearAlgebra.crossProduct(v3d2, sn.viewUp, v3d);
			ArrayLinearAlgebra.normalizeVector(v3d);

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
		final JoglSceneNode sn = (JoglSceneNode)structureMap.getUData();

		final ChainGeometry defaultChainGeometry = (ChainGeometry) GlGeometryViewer.defaultGeometry
				.get(ComponentType.CHAIN);
		final AtomGeometry defaultAtomGeometry = (AtomGeometry) GlGeometryViewer.defaultGeometry
				.get(ComponentType.ATOM);
		final BondGeometry defaultBondGeometry = (BondGeometry) GlGeometryViewer.defaultGeometry
				.get(ComponentType.BOND);

		final ChainStyle defaultChainStyle = (ChainStyle) structureStyles
				.getDefaultStyle(ComponentType.CHAIN);
		final AtomStyle defaultAtomStyle = (AtomStyle) structureStyles
				.getDefaultStyle(ComponentType.ATOM);
		final BondStyle defaultBondStyle = (BondStyle) structureStyles
				.getDefaultStyle(ComponentType.BOND);

		str.getStructureMap().getStructureStyles()
				.removeStructureStylesEventListener(this);
		str.getStructureMap().getStructureStyles()
				.addStructureStylesEventListener(this);

		defaultChainGeometry.setRibbonForm(RibbonForm.RIBBON_SIMPLE_LINE);
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

		final Vector<Atom> atoms = new Vector<Atom>();
		final int ligCount = structureMap.getLigandCount();
		
		for (int i = 0; i < ligCount; i++)
		{
			final Residue r = structureMap.getLigandResidue(i);
			atoms.addAll(r.getAtoms());
			structureStyles.setSelected(r, true);
		}
		
		final Vector<Bond> bonds = structureMap.getBonds(atoms);

		final int bondCount = bonds.size();
		for (int i = 0; i < bondCount; i++) {
			final Bond b = bonds.get(i);

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
			final Atom a = atoms.get(i);

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

	public void clearStructure(boolean transitory)
	{
		super.clearStructure();
	}
	
	@Override
	public void handleUpdateEvent(UpdateEvent evt)
	{
		boolean transitory = (evt instanceof LXUpdateEvent)?
			transitory = ((LXUpdateEvent)evt).transitory : false;
		
		switch(evt.action)
		{
		case CLEAR_ALL:
			clearStructure(transitory);
			break;
			
		case EXTENDED:
			switch(((LXUpdateEvent)evt).lxAction)
			{
				case INTERACTIONS_CHANGED:
					requestRepaint();
					break;
					
				default:
					super.handleUpdateEvent(evt);
					break;
			}
			break;

		default:
			super.handleUpdateEvent(evt);
			break;
		}
	}
	

	// added for protein-ligand interactions
	public void ligandView(final Structure structure)
	{
		// if (inLigName != null && inLigName.length() > 0) {
		// // GeometryViewer gv = getGeometryViewer();
		// Transform3D trans3D = new Transform3D();
		// trans3D.set(getLigandVector(structure, inLigName));
		// this.setTransform(trans3D);
		// }

		final double[][] ligandBounds =
			getLigandBounds(structure, LigandExplorer.sgetSceneController().getLigandResidues());

		if (ligandBounds == null) {
			return;
		}

		final StructureMap sm = structure.getStructureMap();
		final LXSceneNode node = (LXSceneNode)sm.getUData();

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

		LXViewMovementThread.createMovementThread(
				currentOrientation, eye, currentPosition, center, currentUp, up, 0, 0, 0, 0).start();

		requestRepaint();
	}

	// added for protein-ligand interactions
	// returned:
	// - double[0]: minimum x,y,z values
	// - double[1]: maximum x,y,z values
	// can be null if the ligand name is not found.
	private double[][] getLigandBounds(final Structure s, final Residue curLigandResidues[]) {
		double maxX, maxY, maxZ;
		double minX, minY, minZ;

		maxX = maxY = maxZ = -(Double.MAX_VALUE - 2);
		minX = minY = minZ = Double.MAX_VALUE;

		for (Residue residue : curLigandResidues)
			for (Atom atom_j : residue.getAtoms())
			{
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

	public String getResidueName(final Atom atom)
	{
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
			dist = distString.substring(0, index + 3);
		} catch (final Exception ex) {
			// only one digit after the decimal to begin with
			dist = distString.substring(0, index + 2);
		}
		return dist;
	}

	public void renderResidue(final Residue r, final AtomStyle as, final AtomGeometry ag,
			final BondStyle bs, final BondGeometry bg, final boolean showLabel) {
		final StructureMap sm = r.structure.getStructureMap();
		final LXSceneNode node = (LXSceneNode)sm.getUData();

		final Vector<Atom> atoms = r.getAtoms();
		final Vector<Bond> bonds = sm.getBonds(atoms);
		for (int i = 0; i < atoms.size(); i++) {
			final Atom a = atoms.get(i);
			if (!node.isRendered(a)) {
				final DisplayListRenderable renderable = new DisplayListRenderable(a,
						as, ag);
				node.addRenderable(renderable);
			}
		}
		for (int i = 0; i < bonds.size(); i++) {
			final Bond b = bonds.get(i);
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
		final JoglSceneNode node = (JoglSceneNode)sm.getUData();

		final Vector<Atom> atoms = r.getAtoms();
		final Vector<Bond> bonds = sm.getBonds(atoms);
		for (int i = 0; i < atoms.size(); i++) {
			final Atom a = atoms.get(i);
			node.removeRenderable(a);
		}
		for (int i = 0; i < bonds.size(); i++) {
			final Bond b = bonds.get(i);
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
	public void drawInteraction(final Structure structure, final Atom a, final Atom b,
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

		final LXSceneNode node = (LXSceneNode)sm.getUData();

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
		final LXSceneNode node = (LXSceneNode)structure.getStructureMap().getUData();

		final Residue r = structure.getStructureMap().getResidue(atom);
		final String label = structure.getStructureMap().getChain(atom).getChainId()
				+ ":" + r.getCompoundCode() + r.getResidueId();
		node.lxCreateAndAddLabel(r, label, InteractionConstants.hydrophilicBondColor);
	}
}
