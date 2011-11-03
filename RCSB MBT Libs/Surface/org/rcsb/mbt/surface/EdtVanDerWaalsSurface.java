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
public class EdtVanDerWaalsSurface implements SurfaceCalculator {
    private static float PROBE_RADIUS = 0.0f;
    private static boolean bcolor = true; // not sure what this is used for, it's always true in EDTSurf
    private static int stype = 1;
    private TriangulatedSurface surface;
 
    public EdtVanDerWaalsSurface(List<Sphere> spheres, float resolution) {
        EdtSurfaceCalculator c = new EdtSurfaceCalculator(spheres, PROBE_RADIUS, resolution);
        c.initparam();
        c.boundingatom();
        c.fillvoxels(bcolor);
        c.buildbounary();
        c.marchingcube(stype);

        surface = c.getSurface();
    }

    public EdtVanDerWaalsSurface(List<Sphere> patch, List<Sphere> context, float resolution) {
        List<Sphere> all = new ArrayList<Sphere>(patch);
        all.addAll(SurfacePatchCalculator.calcSurroundings(patch, context));

        SurfaceCalculator evs = new EdtVanDerWaalsSurface(all, resolution);
        surface = evs.getSurface();

        SurfacePatchCalculator sp = new SurfacePatchCalculator(surface, patch.size(), patch);
        surface = sp.getSurfacePatch();
    }

    public TriangulatedSurface getSurface() {
        return surface;
    }

}
