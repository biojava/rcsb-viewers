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
 * Created on 2011/11/08
 *
 */

package org.rcsb.mbt.surface.core;

import org.rcsb.mbt.surface.datastructure.Sphere;

/**
 * TwoSphereOverlap calculates the solvent accessible and buried surface area of
 * two spheres that overlap using an analytical method.
 *
 * @author Peter Rose
 */
public class TwoSphereOverlap {
    private float radius1;
    private float radius2;
    private float probeRadius;
    private float distance;

    public TwoSphereOverlap(Sphere sphere1, Sphere sphere2, float probeRadius) {
       this.radius1 = sphere1.getRadius();
       this.radius2 = sphere2.getRadius();
       this.probeRadius = probeRadius;
       this.distance = sphere1.getLocation().distance(sphere2.getLocation());
    }

    public float getAccessibleSurfaceArea1() {
        return area(radius1, probeRadius) - getBuriedSurfaceArea1();
    }

    public float getBuriedSurfaceArea1() {
        return buriedArea(radius1, radius2, probeRadius, distance);
    }

    public float getAccessibleSurfaceArea2() {
        return area(radius2, probeRadius) - getBuriedSurfaceArea2();
    }

    public float getBuriedSurfaceArea2() {
        return buriedArea(radius2, radius1, probeRadius, distance);
    }

    private static float area(float radius, float probeRadius) {
        return (float) (4 * Math.PI * (radius + probeRadius)*(radius + probeRadius));
    }

    /*
     * Returns buried surface area for the first sphere based on the formula (3) in
     * S. Wodak, J Janin, Proc. Natl. Acad. Sci. USA (1980), 77, 1736-1740.
     */
    private static float buriedArea(float radius1, float radius2, float probeRadius, float distance) {
        float r2 = radius1 + radius2 + 2* probeRadius - distance;
        if (r2 < 0.0f) {
            return 0.0f;
        }
        if (distance == 0.0f) {
        // how should we deal with this case??
        }
        return (float)Math.PI * (radius2 + probeRadius) * r2 * (1 + (radius1 - radius2)/distance);
    }
}
