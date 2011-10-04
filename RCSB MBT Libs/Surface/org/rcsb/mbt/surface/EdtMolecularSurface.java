/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.rcsb.mbt.surface;

import java.util.ArrayList;
import java.util.List;

import org.rcsb.mbt.surface.core.EdtSurfaceCalculator;
import org.rcsb.mbt.surface.core.SurfacePatchCalculator;
import org.rcsb.mbt.surface.datastructure.Sphere;
import org.rcsb.mbt.surface.datastructure.TriangulatedSurface;

/**
 *
 * @author Peter Rose
 */
public class EdtMolecularSurface implements SurfaceCalculator {
    private static boolean bcolor = true; // not sure what this is used for, it's always true in EDTSurf
    private static int stype = 4;//
    private TriangulatedSurface surface;

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

    public EdtMolecularSurface(List<Sphere> patch, List<Sphere> context, float probeRadius, float resolution) {
        List<Sphere> all = new ArrayList<Sphere>(patch);
        all.addAll(SurfacePatchCalculator.calcSurroundings(patch, context));

        EdtMolecularSurface ems = new EdtMolecularSurface(all, probeRadius, resolution);
        surface = ems.getSurface();

        SurfacePatchCalculator sp = new SurfacePatchCalculator(surface, patch.size());
        surface = sp.getSurfacePatch();
    }
    
    public TriangulatedSurface getSurface() {
        return surface;
    }

}
