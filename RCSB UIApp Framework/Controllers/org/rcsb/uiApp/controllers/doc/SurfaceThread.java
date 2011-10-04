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
import javax.vecmath.Color4b;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.Surface;
import org.rcsb.mbt.model.Residue.Classification;
import org.rcsb.mbt.model.attributes.AtomRadiusRegistry;
import org.rcsb.mbt.model.attributes.IAtomRadius;
import org.rcsb.mbt.model.attributes.InterpolatedColorMap;
import org.rcsb.mbt.surface.EdtMolecularSurface;
import org.rcsb.mbt.surface.SurfaceCalculator;
import org.rcsb.mbt.surface.datastructure.Sphere;
import org.rcsb.mbt.surface.datastructure.TriangulatedSurface;
import org.rcsb.mbt.surface.datastructure.VertInfo;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.app.ProgressPanelController;
import org.rcsb.uiApp.controllers.update.UpdateEvent;

/**
 * Actions create and start this thread to create the requested surface.
 * 
 * @author Peter Rose
 *
 */
public class SurfaceThread extends Thread
{
	private static InterpolatedColorMap map = new InterpolatedColorMap(InterpolatedColorMap.HYDROPHOBICITY_RAMP);
	public void createSurface() {
		IAtomRadius registry = AtomRadiusRegistry.get("By CPK");
		float probeRadius = 1.4f;
		
		Structure structure = AppBase.sgetModel().getStructures().get(0);
		StructureMap smap = structure.getStructureMap();
		Vector<Chain> chains = smap.getChains();

		
		long t0 = System.nanoTime();
		for (Chain c: chains) {
			List<Atom> atomList = new ArrayList<Atom>();
			List<Float> hydrophobicity = new ArrayList<Float>();
			List<Sphere> spheres = new ArrayList<Sphere>();
			Vector<Residue> residues = c.getResidues();
			// How to deal with non-standard residue in a polymer. If excluded, 
			// there will be holes in the surface
			for (Residue r: residues) {
				if (r.getClassification().equals(Classification.AMINO_ACID) ||
						r.getClassification().equals(Classification.NUCLEIC_ACID)) {
					Vector<Atom> atoms = r.getAtoms();
					for (Atom a: atoms) {
						double[] coord = a.coordinate;
						Point3f location = new Point3f((float)coord[0], (float)coord[1], (float)coord[2]);
						float radius = registry.getAtomRadius(a);
						spheres.add(new Sphere(location, radius));
						atomList.add(a);
						hydrophobicity.add(r.getHydrophobicity());
					}
				}
			}
			// should there be a size cutoff? I.e. min 24 residues?
			if (spheres.size() == 0) {
				continue;
			}
			//	}
	//		float resolution = 0.4f - 0.01f* chains.size(); // original
			float resolution = 0.5f - 0.01f* chains.size() - 0.00005f * spheres.size();
	//		float resolution = 0.5f - 0.005f* chains.size();
			resolution = Math.min(resolution, 0.4f);
			resolution = Math.max(resolution, 0.1f);
			System.out.println("resolution: " + resolution + " spheres: " + spheres.size());
			long t1 = System.nanoTime();
			SurfaceCalculator s = new EdtMolecularSurface(spheres, probeRadius, resolution);
			TriangulatedSurface ts = s.getSurface();
			long t2 = System.nanoTime();
			System.out.println("Surface calculation: " + (t2-t1)/1000000 + "ms");
			List<VertInfo> verts = ts.getVertices();
			s = null; // this is a very large object that's not needed anymore, try to trigger GC
	//		System.gc();
			long t3 = System.nanoTime();
			System.out.println("Surface gc: " + (t3-t2)/1000000 + "ms");
	//		ts.laplaciansmooth(2);
			ts.laplaciansmooth(1);
			long t4 = System.nanoTime();
			System.out.println("Surface smoothing: " + (t4-t3)/1000000 + "ms");
			

			// set default color
			int vertexCount = ts.getVertices().size();
			System.out.println("# vertices: " + vertexCount + " # atoms: " + atomList.size());
			Color4f[] colors = new Color4f[vertexCount];
	//		Color4f defaultColor = new Color4f(0.1f, 0.8f, 1.0f, 0.0f);
			
			Color4f defaultColor = getDivergingColorScheme(smap.getSurfaceCount());
//			Color4f defaultColor = getSequentialColorScheme(c.getEntityId());
			for (int i = 0; i < vertexCount; i++) {
				VertInfo v = verts.get(i);
//				colors[i] = getBFactorColorScheme(atomList.get(v.atomid));
	//			colors[i] = getHydrophobicityColorScheme(hydrophobicity.get(v.atomid));
				colors[i] = defaultColor; // default transparency
			}

			long t5 = System.nanoTime();
			System.out.println("Surface total: " + (t5-t0)/1000000 + "ms");
			Surface surface = new Surface(c, structure);
			surface.setTriangulatedSurface(ts);
			surface.setColors(colors);

			smap.addSurface(surface);
			AppBase.sgetUpdateController().fireUpdateViewEvent(UpdateEvent.Action.SURFACE_ADDED, surface); // has no effect
			AppBase.sgetUpdateController().fireUpdateViewEvent(UpdateEvent.Action.VIEW_UPDATE); 
		}
	} 
	
	private Color4f getBFactorColorScheme(Atom atom) {
		float b = (atom.bfactor -20) / 50f;
		if (b > 1) {
			b = 1;
		}
		if (b < 0) {
			b = 0;
		}
		return new Color4f(b, 0.0f, 1.0f-b, 1.0f);
	}
	
	private Color4f getHydrophobicityColorScheme(float hydrophobicity) {	
		float[] colors = new float[3];
		map.getColor(hydrophobicity, colors);
		return new Color4f(colors[0], colors[1], colors[2], 1.0f);
	}
	
	/*
	 * Sequential color scheme, colorblind save
	 * http://colorbrewer2.org/
	 * Cynthia A. Brewer, 1994, "Color Use Guidelines for Mapping and Visualization",
	 * Chapter 7 (pp. 123-147) in Visualization in Modern Cartography, edited by 
	 * A.M. MacEachren and D.R.F. Taylor, Elsevier Science, Tarrytown, NY. 
	 */
	private Color4f getSequentialColorScheme(int index) {
		Color4f color;

		switch (index % 8) {
		case 0: color = new Color4f(8/256f, 69/256f, 148/256f, 1.0f); break;
		case 1: color = new Color4f(33/256f, 113/256f, 181/256f, 1.0f); break;
		case 2: color = new Color4f(66/256f, 146/256f, 198/256f, 1.0f); break;
		case 3: color = new Color4f(107/256f, 174/256f, 214/256f, 1.0f); break;
		case 4: color = new Color4f(158/256f, 202/256f, 225/256f, 1.0f); break;
		case 5: color = new Color4f(198/256f, 219/256f, 239/256f, 1.0f); break;
		case 6: color = new Color4f(222/256f, 235/256f, 247/256f, 1.0f); break;
		default: color = new Color4f(247/256f, 251/256f, 225/256f, 1.0f); break;
		}
		return color;
	}
	/*
	 * Diverging color scheme, colorblind save
	 * http://colorbrewer2.org/
	 */
	private Color4f getDivergingColorScheme(int index) {
		Color4f color;

		switch (index % 8) {
		case 0: color = new Color4f(1/256f, 102/256f, 94/256f, 1.0f); break;
		case 1: color = new Color4f(140/256f, 81/256f, 10/256f, 1.0f); break;
		case 2: color = new Color4f(53/256f, 151/256f, 143/256f, 1.0f); break;
		case 3: color = new Color4f(191/256f, 129/256f, 45/256f, 1.0f); break;
		case 4: color = new Color4f(128/256f, 205/256f, 193/256f, 1.0f); break;
		case 5: color = new Color4f(223/256f, 129/256f, 125/256f, 1.0f); break;
		case 6: color = new Color4f(199/256f, 234/256f, 229/256f, 1.0f); break;
		default: color = new Color4f(246/256f, 232/256f, 195/256f, 1.0f); break;
		}
		return color;
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
