package org.rcsb.lx.controllers.scene;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.controllers.update.LXUpdateController;
import org.rcsb.lx.glscene.jogl.LXGlGeometryViewer;
import org.rcsb.lx.glscene.jogl.LXSceneNode;
import org.rcsb.lx.model.InteractionConstants;
import org.rcsb.lx.model.LXModel;
import org.rcsb.lx.ui.LXDocumentFrame;
import org.rcsb.mbt.glscene.jogl.AtomGeometry;
import org.rcsb.mbt.glscene.jogl.BondGeometry;
import org.rcsb.mbt.glscene.jogl.DisplayListRenderable;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponentRegistry;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.BondStyle;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.geometry.Algebra;

/*
 * Calculates the interactions between the ligand and the protein.
 */
public class InteractionCalculator
{

	public Residue[] currentLigandResidues = null;
	
	public void calculateInteractions(final Structure structure, boolean hbondflag,
			boolean hydroflag, boolean otherflag, final double hbondupper,
			final double hbondlower, final double hydroupper, final double hydrolower,
			final double otherupper, final double otherlower, final boolean displayDisLabel,
			final PrintWriter interactionsOut) {
		int count = 0;
		int count_hydro = 0;
		int count_other = 0;
		final StructureMap structureMap = structure.getStructureMap();
		Vector<Atom> ligandAtoms = new Vector<Atom>();
		for (Residue residue : currentLigandResidues)
			ligandAtoms.addAll(residue.getAtoms());
		
		final Vector<Atom> proteinAtoms = new Vector<Atom>();

		String interactionType = null;
		String distString = null;

		if (!hbondflag && !hydroflag && !otherflag) {
			return;
		}
		
		LXGlGeometryViewer glViewer = LigandExplorer.sgetGlGeometryViewer();

		for (Chain chain : structureMap.getChains())
			if (chain.getClassification() == Residue.Classification.AMINO_ACID)
				proteinAtoms.addAll(chain.getAtoms());

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

			final Atom atom_i = ligandAtoms.get(i);

			for (int j = i + 1; j < proteinAtoms.size(); j++) {
				final Atom atom_j = proteinAtoms.get(j);

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

							if (interactionsOut == null) {
								glViewer.renderResidue(structureMap.getResidue(atom_j), as, ag, bs, bg,true);
							}

							count++;

							glViewer.drawInteraction(structure, atom_i, atom_j,
									interactionType, displayDisLabel,
									distString, distance, interactionsOut);
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

							if (interactionsOut == null) {
								glViewer.renderResidue(structureMap
										.getResidue(atom_j), as, ag, bs, bg,
										true);
							}

							// Display distance label

							glViewer.drawInteraction(structure, atom_i, atom_j,
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

							if (interactionsOut == null) {
								glViewer.renderResidue(structureMap
										.getResidue(atom_j), as, ag, bs, bg,
										true);
							}

							glViewer.drawInteraction(structure, atom_i, atom_j,
									interactionType, displayDisLabel,
									distString, distance, interactionsOut);

						}

					}
				}

			}
		}
	}
	
	public void calWaterInteractions(final Structure structure, final float lowerBound, final float upperBound,
			final boolean displayDisLabel, final PrintWriter interactionsOut) {
		final StructureMap structureMap = structure.getStructureMap();

		double distance = 0.0;
		String distString = null;
		final String interactionType = InteractionConstants.waterMediatedType;
		final HashSet<Atom> waterAtoms = new HashSet<Atom>();
		
		for (Chain hohChain : structureMap.getChains())
		{
			if (hohChain.getResidue(0).getClassification() != Residue.Classification.WATER
				&& hohChain.getChainId() != "_") continue;
						// find the water chain

			//
			// First, find the closest waters to any of the atoms in the the current ligand.
			// Traverse all the atoms in the current ligand and compare against all the water
			// atoms
			//
			for (Residue residue : currentLigandResidues)
				for (Atom ligAtom : residue.getAtoms())
					for (Residue hohResidue : hohChain.getResidues())
						if (hohResidue.getClassification() == Residue.Classification.WATER)
						{
							Atom hohAtom = hohResidue.getAtom(0);
										// water residue only contains a single 'O'
							
							distance = Algebra.distance(hohAtom.coordinate,
														ligAtom.coordinate);
							if (distance < upperBound && distance > lowerBound)
							{
								distString = LXGlGeometryViewer.getDistString(distance);
								LigandExplorer.sgetGlGeometryViewer().drawInteraction(structure, ligAtom, hohAtom,
										interactionType, displayDisLabel, distString, distance,
										interactionsOut);
								
								waterAtoms.add(hohAtom);
							}							
						}
		}
		
		this.calWaterProInt(structure, waterAtoms, lowerBound, upperBound, displayDisLabel, interactionsOut);
					// now calculate the protein interactions with the located water atoms

		if (interactionsOut == null) {
			final LXSceneNode node = (LXSceneNode)structure.getStructureMap().getUData();

			final AtomGeometry ag = (AtomGeometry) GlGeometryViewer.defaultGeometry
					.get(StructureComponentRegistry.TYPE_ATOM);
			final AtomStyle as = (AtomStyle) structure.getStructureMap()
					.getStructureStyles().getDefaultStyle(
							StructureComponentRegistry.TYPE_ATOM);

			for (Atom a : waterAtoms)
			{
				if (!node.isRendered(a)) {
					final DisplayListRenderable renderable = new DisplayListRenderable(
							a, as, ag);
					node.addRenderable(renderable);
				}
			}
		}
	}

	/*
	 * Calculate the water protein interactions.  The waters are traversed and compared against the
	 * amino acid atoms.
	 */
	public void calWaterProInt(final Structure structure, final HashSet<Atom> waterAtoms, final float lowerBound, final float upperBound,
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
		
		LXGlGeometryViewer glViewer = LigandExplorer.sgetGlGeometryViewer();

		final LXSceneNode node = (LXSceneNode)structure.getStructureMap().getUData();
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

		for (Atom atom_j : waterAtoms)
		{
			for (int k = 0; k < proAtoms.size(); k++) {
				final Atom atom_k = proAtoms.get(k);
				distance = Algebra.distance(atom_j.coordinate,
						atom_k.coordinate);
				distString = LXGlGeometryViewer.getDistString(distance);
				final Residue res = structureMap.getResidue(atom_k);
				if (!uniqRes.contains(res)) {

					if (distance < upperBound && distance > lowerBound) {
						glViewer.drawInteraction(structure, atom_j, atom_k,
								interactionType, displayDisLabel, distString, distance,
								interactionsOut);

						if (interactionsOut == null) {
							if (!node.isRendered(atom_j)) {
								final DisplayListRenderable renderable = new DisplayListRenderable(
										atom_j, as, ag);
								node.addRenderable(renderable);
							}

							glViewer.renderResidue(res, as, ag, bs, bg, true);
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
			final Atom atom_m = atoms.get(m);
			final Residue atomResidueM = structureMap.getResidue(atom_m);
			Atom atom_n = null;
			for (int n = m + 1; n < atoms.size(); n++) {
				atom_n = atoms.get(n);
				final Residue atomResidueN = structureMap.getResidue(atom_n);
				
				if (!atom_m.compound.equals("HOH")
						&& !atom_n.compound.equals("HOH")
						&& (atomResidueM != atomResidueN) &&
							(atomResidueM.getChainId().equals(currentLigandResidues[0].getChainId()) ||
							 atomResidueN.getChainId().equals(currentLigandResidues[0].getChainId()))) {
					distance = Algebra.distance(atom_m.coordinate,
							atom_n.coordinate);
					distString = LXGlGeometryViewer.getDistString(distance);

					if (distance < upperBound && distance > lowerBound) {
						LigandExplorer.sgetGlGeometryViewer().drawInteraction(structure, atom_m, atom_n,
								interactionType, displayDisLabel, distString, distance,
								interactionsOut);
					}
				}
			}
		}
	}
}
