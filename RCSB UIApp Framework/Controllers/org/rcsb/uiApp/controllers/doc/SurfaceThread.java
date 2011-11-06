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
package org.rcsb.uiApp.controllers.doc;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.vecmath.Point3f;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.Surface;
import org.rcsb.mbt.model.Residue.Classification;
import org.rcsb.mbt.model.StructureMap.BiologicUnitTransforms;
import org.rcsb.mbt.model.StructureMap.BiologicUnitTransforms.BiologicalUnitGenerationMapByChain;
import org.rcsb.mbt.model.attributes.AtomRadiusRegistry;
import org.rcsb.mbt.model.attributes.ColorBrewer;
import org.rcsb.mbt.model.attributes.IAtomRadius;
import org.rcsb.mbt.model.attributes.SurfaceColorUpdater;
import org.rcsb.mbt.model.geometry.ModelTransformationList;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.mbt.surface.EdtMolecularSurface;
import org.rcsb.mbt.surface.SurfaceCalculator;
import org.rcsb.mbt.surface.datastructure.Sphere;
import org.rcsb.mbt.surface.datastructure.TriangulatedSurface;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.app.ProgressPanelController;
import org.rcsb.uiApp.controllers.update.UpdateEvent;


/**
 * Actions create and start this thread to create the requested surface.
 * 
 * @author Peter Rose
 *
 */
public class SurfaceThread extends Thread {
	private static float PROBE_RADIUS = 1.4f;
	
	public void createSurface() {
		IAtomRadius registry = AtomRadiusRegistry.get("By CPK");
		
		Structure structure = AppBase.sgetModel().getStructures().get(0);
		StructureMap smap = structure.getStructureMap();	
		List<Chain> polymerChains = getPolymerChains();
		float resolution = calcResolution(polymerChains);	
		
		long t0 = System.nanoTime();
		// create progress bar
		ProgressPanelController.StartProgress(AppBase.sgetActiveFrame());
		Status.progress(0, "Creating surfaces");
		
		for (Chain c: polymerChains) {
			List<Sphere> spheres = new ArrayList<Sphere>();
			Vector<Residue> residues = c.getResidues();
			
			// TODO
			// How to deal with non-standard residues in a polymer?
			// Ligand, water, etc. could be part of chain
			for (Residue r: residues) {
				if (r.getClassification().equals(Classification.AMINO_ACID) ||
						r.getClassification().equals(Classification.NUCLEIC_ACID)) {
					Vector<Atom> atoms = r.getAtoms();
					for (Atom a: atoms) {
						double[] coord = a.coordinate;
						Point3f location = new Point3f((float)coord[0], (float)coord[1], (float)coord[2]);
						float radius = registry.getAtomRadius(a);
						// enlarge spheres by 10% to avoid that helices touch the surface
						spheres.add(new Sphere(location, radius *1.1f, a));
					}
				}
			}
			// should there be a size cutoff? I.e. min 24 residues?
			if (spheres.size() == 0) {
				continue;
			};
	
			// calculate molecular surface
			SurfaceCalculator s = new EdtMolecularSurface(spheres, PROBE_RADIUS, resolution);
			TriangulatedSurface ts = s.getSurface();
			s = null; // this is a very large object that's not needed anymore

			// smooth surface
			ts.laplaciansmooth(1);
			Surface surface = new Surface(c, structure);
			surface.setTriangulatedSurface(ts);
			
			// set default surface color
			SurfaceColorUpdater.setPaletteColor(surface, ColorBrewer.BrBG, polymerChains.size(), smap.getSurfaceCount());

			smap.addSurface(surface);
			
			// update progress bar			count++;
			Status.progress(((int)(100* smap.getSurfaceCount()/(float)polymerChains.size())), "Creating surfaces");
			
			AppBase.sgetUpdateController().fireUpdateViewEvent(UpdateEvent.Action.SURFACE_ADDED, surface); // has no effect
	//		AppBase.sgetUpdateController().fireUpdateViewEvent(UpdateEvent.Action.VIEW_UPDATE); 
		}
		ProgressPanelController.EndProgress();
		long t5 = System.nanoTime();
		System.out.println("Surface calculation: " + (t5-t0)/1000000 + " ms");
		AppBase.sgetUpdateController().fireUpdateViewEvent(UpdateEvent.Action.VIEW_UPDATE); 
	}

	/**
	 * @param chains
	 * @return
	 */
	private List<Chain> getPolymerChains() {
		Structure structure = AppBase.sgetModel().getStructures().get(0);
		StructureMap smap = structure.getStructureMap();
		Vector<Chain> chains = smap.getChains();
		List<Chain> polymerChains = new ArrayList<Chain>();
		for (Chain c: chains) {
			if (c.getClassification().equals(Residue.Classification.AMINO_ACID) ||
					c.getClassification().equals(Residue.Classification.NUCLEIC_ACID)) {
				polymerChains.add(c);
			}
		}
		return polymerChains;
	} 
	
	private float calcResolution(List<Chain> polymerChains) {		
		int sphereCount = getSphereCount(polymerChains);
		int symOps = getSymmetryOperationCount(polymerChains);
		float resolution = 0.4f - 0.00005f * sphereCount - 0.000005f * sphereCount*symOps;
		System.out.println("resolution: " + resolution + ", sheres: " + sphereCount + ", symmetry operations: " + symOps);
		// clamp lowest resolution
		resolution = Math.max(resolution, 0.1f);
		return resolution;
	}

	/**
	 * @param polymerChains
	 * @return
	 */
	private int getSphereCount(List<Chain> polymerChains) {
		int sphereCount = 0;
		for (Chain c: polymerChains) {
			for (Residue r: c.getResidues()) {
				if (r.getClassification().equals(Classification.AMINO_ACID) ||
						r.getClassification().equals(Classification.NUCLEIC_ACID)) {
					sphereCount++;
				}
			}
		}
		return sphereCount;
	}
	
	private int getSymmetryOperationCount(List<Chain> polymerChains) {
		Structure structure = AppBase.sgetModel().getStructures().get(0);
		
		// TODO
		// this creates a dependency on other packages! How can this be avoided?
		final String showAsymmetricUnitOnly = AppBase.getApp().properties.getProperty("show_asymmetric_unit_only");;
		if (showAsymmetricUnitOnly != null && showAsymmetricUnitOnly.equals("true")) {
			return 1;
		}
		
		if ( ! structure.getStructureMap().hasBiologicUnitTransforms()) {
			return 1;
		}

		BiologicUnitTransforms t = structure.getStructureMap().getBiologicUnitTransforms();
		BiologicalUnitGenerationMapByChain map = t.getBiologicalUnitGenerationMatricesByChain();
		
		if (map == null) {
			return 1;
		}
		
		int symOps = 0;
		
		for (Chain c: polymerChains) {
			ModelTransformationList list = map.get(c.getChainId());
			if (list != null) {
				symOps += list.size();
			}
		}

		return symOps;
	}
	
	public void run()
	{
		ProgressPanelController.StartProgress();

		if (AppBase.sgetModel().hasStructures())
            createSurface();
		ProgressPanelController.EndProgress();
		if (!AppBase.sgetModel().hasStructures())
			JOptionPane.showMessageDialog(null, "Structure not found: Cannot create surface"); 
	}

	public void handleUpdateEvent(UpdateEvent evt) {
		// TODO Auto-generated method stub
		
	}
};
