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
 * The code for surface generation was contributed by Dong Xu
 * and Yang Zhang at the University of Michigan, Ann Arbor. This
 * class represents the Java version translated from the original C++
 * code (http://zhanglab.ccmb.med.umich.edu/EDTSurf).
 * 
 * Please reference D. Xu, Y. Zhang (2009) 
 * Generating Triangulated Macromolecular Surfaces by Euclidean 
 * Distance Transform. PLoS ONE 4(12): e8140.
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
 * Created on 2011/11/08
 *
 */

package org.rcsb.mbt.surface;

import java.util.ArrayList;
import java.util.List;

import org.rcsb.mbt.model.Surface;
import org.rcsb.mbt.surface.core.EdtSurfaceCalculator;
import org.rcsb.mbt.surface.core.SurfacePatchCalculator;
import org.rcsb.mbt.surface.datastructure.Sphere;
import org.rcsb.mbt.surface.datastructure.TriangulatedSurface;

/**
 * Creates a molecular surface using the Euclidean Distance Transform method
 * by D. Xu, Y. Zhang (2009) Generating Triangulated Macromolecular Surfaces by Euclidean 
 * Distance Transform. PLoS ONE 4(12): e8140.
 * 
 * @author Dong Xu (original C++ version)
 * @author Peter Rose (converted and refactored in Java)
 */
public class EdtMolecularSurface implements SurfaceCalculator {
    private static boolean bcolor = true; // not sure what this is used for, it's always true in EDTSurf
    private static int stype = 4;//
    private TriangulatedSurface surface = new TriangulatedSurface();

    public EdtMolecularSurface(List<Sphere> spheres, float probeRadius, float resolution) {
        EdtSurfaceCalculator c = new EdtSurfaceCalculator(spheres, probeRadius, resolution);
        c.initparam();
        c.boundingatom();
        c.fillvoxels(bcolor);
        c.buildbounary();
        c.fastdistancemap();
        c.marchingcube(stype);

        surface = c.getSurface();
    }

    public EdtMolecularSurface(List<Sphere> patch, List<Sphere> context, float probeRadius, float distanceThreshold, float resolution) {
        System.out.println("EdtMolecularSurface: patch " + patch.size());
        System.out.println("EdtMolecularSurface: context " + context.size());
    	List<Sphere> surrounding = new ArrayList<Sphere>();
        surrounding.addAll(SurfacePatchCalculator.calcSurroundings(patch, context, distanceThreshold + 10.0f));

        System.out.println("EdtMolecularSurface: all " + surrounding.size());
        if (surrounding.size() == 0) {
        	return;
        }
        EdtMolecularSurface ems = new EdtMolecularSurface(surrounding, probeRadius, resolution);
        surface = ems.getSurface();

        SurfacePatchCalculator sp = new SurfacePatchCalculator(surface, context, distanceThreshold);
        surface = sp.getSurfacePatch();
    }
    
    public TriangulatedSurface getSurface() {
        return surface;
    }

}
