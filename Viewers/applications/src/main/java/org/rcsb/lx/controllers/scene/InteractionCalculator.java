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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.glscene.jogl.LXGlGeometryViewer;
import org.rcsb.lx.glscene.jogl.LXSceneNode;
import org.rcsb.lx.model.InteractionConstants;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.BondStyle;
import org.rcsb.mbt.model.geometry.ArrayLinearAlgebra;
import org.rcsb.mbt.model.util.Element;
import org.rcsb.mbt.model.util.PeriodicTable;
import org.rcsb.vf.glscene.jogl.AtomGeometry;
import org.rcsb.vf.glscene.jogl.BondGeometry;
import org.rcsb.vf.glscene.jogl.DisplayListRenderable;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;

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
		final StructureMap structureMap = structure.getStructureMap();
		Vector<Atom> ligandAtoms = new Vector<Atom>();
		for (Residue residue : currentLigandResidues)
			if (residue != null)
				ligandAtoms.addAll(residue.getAtoms());
		
		final Vector<Atom> proteinAtoms = new Vector<Atom>();

		String interactionType = null;
		String distString = null;

		if (!hbondflag && !hydroflag && !otherflag) {
			return;
		}
		
		LXGlGeometryViewer glViewer = LigandExplorer.sgetGlGeometryViewer();

		for (Chain chain : structureMap.getChains())
			if (chain.getClassification() == Residue.Classification.AMINO_ACID ||
					chain.getClassification() == Residue.Classification.NUCLEIC_ACID)
				proteinAtoms.addAll(chain.getAtoms());
		
		final AtomGeometry ag = (AtomGeometry) GlGeometryViewer.defaultGeometry
				.get(ComponentType.ATOM);
		final AtomStyle as = (AtomStyle) structure.getStructureMap()
				.getStructureStyles().getDefaultStyle(
						ComponentType.ATOM);
		final BondGeometry bg = (BondGeometry) GlGeometryViewer.defaultGeometry
				.get(ComponentType.BOND);
		final BondStyle bs = (BondStyle) structure.getStructureMap()
				.getStructureStyles().getDefaultStyle(
						ComponentType.BOND);

		for (int i = 0; i < ligandAtoms.size(); i++) {
			double distance = 0.0;

			final Atom atom_i = ligandAtoms.get(i);

			// -PR
			// The following loop seemed to start at the wrong index
            //	for (int j = i + 1; j < proteinAtoms.size(); j++) {
			// changed it to: 
			
				for (int j = 0; j < proteinAtoms.size(); j++) {
				final Atom atom_j = proteinAtoms.get(j);

				if (hbondflag) {

					if ((atom_i.element.equals("N") || atom_i.element
							.equals("O"))
							&& (atom_j.element.equals("N") || atom_j.element
									.equals("O"))) {

						distance = ArrayLinearAlgebra.distance(atom_i.coordinate,
								atom_j.coordinate);

						if (distance <= hbondupper && distance >= hbondlower) {
							interactionType = InteractionConstants.hydrogenBondType;
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
						distance = ArrayLinearAlgebra.distance(atom_i.coordinate,
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
			}
		}
		if (otherflag) {
			calMetalInteractions(structure, otherlower, otherupper, displayDisLabel, interactionsOut);
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
					// restrict to polar atoms
					if (isNOSAtom(ligAtom)) {
					for (Residue hohResidue : hohChain.getResidues())
						if (hohResidue.getClassification() == Residue.Classification.WATER)
						{
							Atom hohAtom = hohResidue.getAtom(0);
										// water residue only contains a single 'O'
							
							distance = ArrayLinearAlgebra.distance(hohAtom.coordinate,
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
		}
		
		this.calWaterProInt(structure, waterAtoms, lowerBound, upperBound, displayDisLabel, interactionsOut);
					// now calculate the protein interactions with the located water atoms

		if (interactionsOut == null) {
			final LXSceneNode node = (LXSceneNode)structure.getStructureMap().getUData();

			final AtomGeometry ag = (AtomGeometry) GlGeometryViewer.defaultGeometry
					.get(ComponentType.ATOM);
			final AtomStyle as = (AtomStyle) structure.getStructureMap()
					.getStructureStyles().getDefaultStyle(
							ComponentType.ATOM);

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
				.get(ComponentType.ATOM);
		final AtomStyle as = (AtomStyle) structure.getStructureMap()
				.getStructureStyles().getDefaultStyle(
						ComponentType.ATOM);
		final BondGeometry bg = (BondGeometry) GlGeometryViewer.defaultGeometry
				.get(ComponentType.BOND);
		final BondStyle bs = (BondStyle) structure.getStructureMap()
				.getStructureStyles().getDefaultStyle(
						ComponentType.BOND);

		for (Atom atom_j : waterAtoms)
		{
			for (int k = 0; k < proAtoms.size(); k++) {
				final Atom atom_k = proAtoms.get(k);
				if (isNOSAtom(atom_k)) {
					distance = ArrayLinearAlgebra.distance(atom_j.coordinate,
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
	}

	public void calMetalInteractions(final Structure structure, final double lowerBound, final double upperBound,
			final boolean displayDisLabel, final PrintWriter interactionsOut) {

		double distance = 0.0;
		final String interactionType = InteractionConstants.metalInteractionType;
		
		List<Atom> metalAtoms = getMetalAtoms(structure);
		
		final HashSet<Atom> coordinatedMetal = new HashSet<Atom>();
		
		
		//
		// First, find the closest waters to any of the atoms in the the current ligand.
		// Traverse all the atoms in the current ligand and compare against all the water
		// atoms
		//
		List<Atom> currentLigandAtoms = getCurrentLigandAtoms();
		
		for (Atom ligAtom : currentLigandAtoms) {
			Element element = PeriodicTable.getElement(ligAtom.element);
			if (PeriodicTable.isMetal(element.atomic_number)) {
				coordinatedMetal.add(ligAtom);
			}
			if (isNOSAtom(ligAtom)) {
				for (Atom metalAtom : metalAtoms) {
					distance = ArrayLinearAlgebra.distance(ligAtom.coordinate,
							metalAtom.coordinate);
					if (distance < upperBound && distance > lowerBound)
					{
						String distString = LXGlGeometryViewer.getDistString(distance);
						LigandExplorer.sgetGlGeometryViewer().drawInteraction(structure, ligAtom, metalAtom,
								interactionType, displayDisLabel, distString, distance,
								interactionsOut);

						coordinatedMetal.add(metalAtom);
					}							
				}
			}
		}

		// find other ligand atoms that may interact with the metal
		List<Atom> otherLigandAtoms = getLigandNOSAtoms(structure);
		otherLigandAtoms.removeAll(currentLigandAtoms);

		// now calculate the protein interactions with the located metal atoms and other ligand atoms
		this.calMetalProInt(structure, coordinatedMetal, otherLigandAtoms, lowerBound, upperBound, displayDisLabel, interactionsOut);


		if (interactionsOut == null) {
			final LXSceneNode node = (LXSceneNode)structure.getStructureMap().getUData();

			final AtomGeometry ag = (AtomGeometry) GlGeometryViewer.defaultGeometry
			.get(ComponentType.ATOM);
			final AtomStyle as = (AtomStyle) structure.getStructureMap()
			.getStructureStyles().getDefaultStyle(
					ComponentType.ATOM);

			for (Atom a : coordinatedMetal)
			{
				if (!node.isRendered(a)) {
					final DisplayListRenderable renderable = new DisplayListRenderable(
							a, as, ag);
					node.addRenderable(renderable);
				}
			}
		}
	}


	public void calMetalProInt(final Structure structure, HashSet<Atom> metalAtoms, List<Atom> ligandAtoms, final double lowerBound, final double upperBound,
			final boolean displayDisLabel, final PrintWriter interactionsOut) {

		final StructureMap structureMap = structure.getStructureMap();
		final int atomCt = structureMap.getAtomCount();
		
		double distance = 0.0;
		String distString = null;
		final String interactionType = InteractionConstants.metalInteractionType;
		HashSet<Residue> uniqRes = new HashSet<Residue>();

		List<Atom> proAtoms = new ArrayList<Atom>();
		proAtoms.addAll(ligandAtoms);
		for (int i = 0; i < atomCt; i++) {
			final Atom atom = structureMap.getAtom(i);
			if (structureMap.getChain(atom).getClassification() ==
				Residue.Classification.AMINO_ACID)
				proAtoms.add(atom);
		}

		LXGlGeometryViewer glViewer = LigandExplorer.sgetGlGeometryViewer();

		final LXSceneNode node = (LXSceneNode)structure.getStructureMap().getUData();
		final AtomGeometry ag = (AtomGeometry) GlGeometryViewer.defaultGeometry
		.get(ComponentType.ATOM);
		final AtomStyle as = (AtomStyle) structure.getStructureMap()
		.getStructureStyles().getDefaultStyle(
				ComponentType.ATOM);
		final BondGeometry bg = (BondGeometry) GlGeometryViewer.defaultGeometry
		.get(ComponentType.BOND);
		final BondStyle bs = (BondStyle) structure.getStructureMap()
		.getStructureStyles().getDefaultStyle(
				ComponentType.BOND);

		for (Atom metalAtom : metalAtoms)
		{
			for (Atom proteinAtom: proAtoms) {
				if (proteinAtom.element.equals("N") || proteinAtom.element.equals("O") || proteinAtom.element.equals("S")) {
					distance = ArrayLinearAlgebra.distance(metalAtom.coordinate,
							proteinAtom.coordinate);
					distString = LXGlGeometryViewer.getDistString(distance);
				
					if (distance < upperBound && distance > lowerBound) {
						glViewer.drawInteraction(structure, metalAtom, proteinAtom,
								interactionType, displayDisLabel, distString, distance,
								interactionsOut);

						if (interactionsOut == null) {
							if (!node.isRendered(metalAtom)) {
								final DisplayListRenderable renderable = new DisplayListRenderable(
										metalAtom, as, ag);
								node.addRenderable(renderable);
							}
							
							Residue res = structureMap.getResidue(proteinAtom);
						
							if (!uniqRes.contains(res) && !node.isRendered(proteinAtom)) {
								glViewer.renderResidue(res, as, ag, bs, bg, true);
								uniqRes.add(res);
							}
						}
					}
				}
			}
		}
	}
	
	public void calcNeighbors(final Structure structure, final float upperBound,final PrintWriter interactionsOut) {
		
		final AtomGeometry ag = (AtomGeometry) GlGeometryViewer.defaultGeometry
		.get(ComponentType.ATOM);
		final AtomStyle as = (AtomStyle) structure.getStructureMap()
		.getStructureStyles().getDefaultStyle(
				ComponentType.ATOM);
		final BondGeometry bg = (BondGeometry) GlGeometryViewer.defaultGeometry
		.get(ComponentType.BOND);
		final BondStyle bs = (BondStyle) structure.getStructureMap()
		.getStructureStyles().getDefaultStyle(
				ComponentType.BOND);
		
		LXGlGeometryViewer glViewer = LigandExplorer.sgetGlGeometryViewer();
		StructureMap structureMap = structure.getStructureMap();
		
		List<Atom> otherAtoms = getAtoms(structure);
		List<Atom> currentLigandAtoms = getCurrentLigandAtoms();
		otherAtoms.removeAll(currentLigandAtoms);
		HashSet<Residue> uniqRes = new HashSet<Residue>();
		
		for (Atom ligAtom : currentLigandAtoms) {
			for (Atom otherAtom: otherAtoms) {
					double distance = ArrayLinearAlgebra.distance(ligAtom.coordinate,
							otherAtom.coordinate);
				
					if (distance < upperBound) {
						if (interactionsOut == null) {
							Residue res = structureMap.getResidue(otherAtom);		
							if (!uniqRes.contains(res)) {
								glViewer.renderResidue(res, as, ag, bs, bg, true);
								uniqRes.add(res);
							}
						}
					}
			}
		}
	}
	/**
	 * Returns a list of metal atoms in ligand residues
	 * @param structure
	 * @return metal atoms
	 */
	private List<Atom> getMetalAtoms(Structure structure) {
		List<Atom> metalAtoms = new ArrayList<Atom>();

		final StructureMap structureMap = structure.getStructureMap();
		for (Residue residue : structureMap.getLigands()) {	

			for (Atom atom: residue.getAtoms()) {
				Element element = PeriodicTable.getElement(atom.element);
				if (PeriodicTable.isMetal(element.atomic_number)) {
					metalAtoms.add(atom);
				}
			}
		}

		return metalAtoms;
	}
	
	private List<Atom> getLigandNOSAtoms(Structure structure) {
		List<Atom> atoms = new ArrayList<Atom>();

		final StructureMap structureMap = structure.getStructureMap();
		for (Residue residue : structureMap.getLigands()) {	

			for (Atom atom: residue.getAtoms()) {
				if (isNOSAtom(atom) ) {
					atoms.add(atom);
				}
			}
		}

		return atoms;
	}
	/**
	 * Returns a list of current ligand atoms
	 * @return current ligand atoms
	 */
	private List<Atom> getCurrentLigandAtoms() {
		List<Atom> atoms = new ArrayList<Atom>();
		for (Residue residue : currentLigandResidues) {
			atoms.addAll(residue.getAtoms());
		}
		return atoms;
	}
	
	/**
	 * Returns true if passed in atom is a nitrogen, oxygen, or sulfur atom
	 * @param atom
	 * @return true if atom is a nitrogen, oxygen, or sulfur atom
	 */
	private boolean isNOSAtom(Atom atom) {
		if (atom.element.equals("N")) {
			return true;
		}
		if (atom.element.equals("O")) {
			return true;
		}
		if (atom.element.equals("S")) {
			Vector<Bond> bonds = atom.structure.getStructureMap().getBonds(atom);
			// only bivalent sulfur can coordinate metal
			if (bonds.size() < 3) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns a list of atoms in the structure
	 * @param structure
	 * @return list of atoms
	 */
	private List<Atom> getAtoms(Structure structure) {
		List<Atom> atoms = new ArrayList<Atom>();
		atoms.addAll(structure.getStructureMap().getAtoms());
		return atoms;
	}
}
