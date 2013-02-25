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
package org.rcsb.lx.controllers.scene;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.controllers.update.LXUpdateController;
import org.rcsb.lx.controllers.update.LXUpdateEvent;
import org.rcsb.lx.glscene.jogl.LXGlGeometryViewer;
import org.rcsb.lx.glscene.jogl.LXSceneNode;
import org.rcsb.lx.model.LXModel;
import org.rcsb.lx.ui.LXDocumentFrame;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.util.DebugState;
import org.rcsb.uiApp.controllers.update.UpdateEvent;
import org.rcsb.vf.controllers.scene.SceneController;


public class LXSceneController extends SceneController
{
	private static int firstReset = 0;

	private InteractionCalculator interactionsCalculator = new InteractionCalculator();
	
	private boolean newDocument = true;
	public void setNewDocument(boolean flag) { newDocument = flag; }
	
	public void processLeftPanelEvent(final Structure structure,
		    final float ligwaterbondupper, final boolean ligWaterProOn, 
			final boolean hbondflag,
		 final float hbondupper, final boolean hydroflag,
			final float hydroupper, final boolean otherflag,
			final float otherupper, final boolean displayDisLabel,
			final float neighborUpper, final boolean neighborFlag,
			boolean saveInteractionsToFile) {

		PrintWriter interactionsOut = null;
		if (saveInteractionsToFile) {
			final JFileChooser chooser = new JFileChooser();

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
			
			LXGlGeometryViewer glViewer = LigandExplorer.sgetGlGeometryViewer();
			if (newDocument)
			{
				glViewer.resetView(true, false);
				newDocument = false;
			}

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
			interactionsCalculator.calWaterInteractions(structure, 0.0f, ligwaterbondupper, displayDisLabel, interactionsOut);
		}

		// added for protein-ligand interactions
		interactionsCalculator.calculateInteractions(structure, hbondflag, hydroflag, otherflag,
				hbondupper, 0.0f, hydroupper, 0.0f, otherupper,
				0.0f, displayDisLabel, interactionsOut);
		
		if (neighborFlag) {
			interactionsCalculator.calcNeighbors(structure, neighborUpper, interactionsOut);
		}
		
		if (interactionsOut != null) {
			interactionsOut.close();
		}
		

		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				LigandExplorer.sgetUpdateController().fireInteractionChanged();
			}
		});
		

		// XXX Status.progress(1.0f, null);
	}
	
	public void setLigandResidues(final Residue[] residues)
	{
		interactionsCalculator.currentLigandResidues = residues;
	}
	
	public Residue[] getLigandResidues() { return interactionsCalculator.currentLigandResidues; }
	
	public void clearStructure(boolean transitory)
	{
		if (!transitory)
			interactionsCalculator.currentLigandResidues = null;
	}
	
	/**
	 * Reset the view to look at the center of the data. JLM DEBUG: This will
	 * eventually be non-static method.
	 * 
	 * Deliberately hides base implementation
	 */
	public void resetView(final boolean forceRecalculation)
	{
		StructureModel model = LigandExplorer.sgetModel();
		if (!model.hasStructures())
			return;

		StructureModel.StructureList structures = model.getStructures();
		
		for (Structure struc : structures)
		{
			final StructureMap sm = struc.getStructureMap();
			final LXSceneNode scene = (LXSceneNode)sm.getUData();

			if (firstReset < structures.size() || forceRecalculation) {

				scene.rotationCenter = sm.getAtomCoordinateAverage();
				scene.bounds = sm.getAtomCoordinateBounds();
				scene.bigX = Math.max(Math.abs(scene.bounds[0][0]), Math
						.abs(scene.bounds[1][0]));
				scene.bigY = Math.max(Math.abs(scene.bounds[0][1]), Math
						.abs(scene.bounds[1][1]));
				scene.bigZ = Math.max(Math.abs(scene.bounds[0][2]), Math
						.abs(scene.bounds[1][2]));
				firstReset++;
			}
			final double maxDistance = Math.sqrt(scene.bigX * scene.bigX
					+ scene.bigY * scene.bigY + scene.bigZ * scene.bigZ);

			// float[] eye = { 0.0, 0.0, maxDistance * 1.4 };
			final double[] eye = { scene.rotationCenter[0],
					scene.rotationCenter[1],
					scene.rotationCenter[2] + maxDistance * 1.4f };
			final double[] up = { 0.0f, 1.0f, 0.0f };
			scene.lookAt(eye, scene.rotationCenter, up);
		}
	}

	@Override
	public void handleUpdateEvent(UpdateEvent evt)
	{
		if ( DebugState.isDebug()){
			System.err.println("LXSceneController. handleUpdateEvent");
		}
		
		boolean transitory = (evt instanceof LXUpdateEvent)?
			transitory = ((LXUpdateEvent)evt).transitory : false;
			
		if (evt.action == UpdateEvent.Action.STRUCTURE_REMOVED)
			clearStructure(transitory);
		
		if (evt.action == UpdateEvent.Action.STRUCTURE_ADDED) {
			if (!transitory) newDocument = true;
		}
		
		super.handleUpdateEvent(evt);
	}
	
	
}
