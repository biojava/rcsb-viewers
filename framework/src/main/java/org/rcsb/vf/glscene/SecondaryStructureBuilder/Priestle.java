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
 * Created on 2007/02/08
 *
 */ 
package org.rcsb.vf.glscene.SecondaryStructureBuilder;

import org.rcsb.vf.glscene.SecondaryStructureBuilder.vec.Vec3f;

import javax.vecmath.Vector3f;


/**
 * Priestle encapsulates a procedure used to smooth the coordinates passed to
 * one of the geometry classes.
 * 
 * @author Apostol Gramada
 * @author John L. Moreland
 * @author John Tate
 * @version $Revision: 1.1 $
 * @since JDK1.2.2
 */
public class Priestle {

	public static void smooth(final Vec3f[] coords, final int steps) {

		if (coords.length < 3) {
			return;
		}

		// This is how it works:
		// Assume we have 3 consequtive points x0, x1, x2. We need to move the
		// midle point, x1, allong the height to the triangle side
		// associated with vertices x0 and x2, by half the length of the height.
		// Three formulas are used
		//
		// // x1' = 0.5*( x0 + x ) ( 1 )
		//
		// where vector x1' is the new position of x1 and x is given by the
		// formula
		//
		// // x = x0 + [ (x1-x0) .dot. n ] n ( 2 )
		//
		// and n is the versor in the direction of the line determined by x0 and
		// x2, i.e.
		//
		// // n = (x2-x0)/|x2-x0| ( 3 )
		//
		final int size = coords.length;
		float scale = 0.0f;
		Vector3f vx0, vx1, vx2, vx1p, vx, vn, dvx;
		vn = new Vector3f();
		dvx = new Vector3f();
		vx = new Vector3f();
		vx1p = new Vector3f();
		vx0 = new Vector3f();
		vx1 = new Vector3f();
		vx2 = new Vector3f();
		for (int i = 0; i < steps; i++) {
			// Adopt a sliding window approach
			//
			vx0.set(coords[0].value[0], coords[0].value[1], coords[0].value[2]);
			vx1.set(coords[1].value[0], coords[1].value[1], coords[1].value[2]);
			vx2.set(coords[2].value[0], coords[2].value[1], coords[2].value[2]);
			for (int j = 0; j < size - 2; j++) {
				vn.sub(vx2, vx0);
				vn.normalize();
				dvx.sub(vx1, vx0);
				scale = dvx.dot(vn);
				vx.scaleAdd(scale, vn, vx0);
				vx1p.add(vx, vx1);
				vx1p.scale(0.5f);

				// Now we can (re)set the coordinate at j+1
				coords[j + 1].value[0] = vx1p.x;
				coords[j + 1].value[1] = vx1p.y;
				coords[j + 1].value[2] = vx1p.z;

				// And reset the vectors we need
				if (j != (size - 3)) {
					/*
					 * vx0 = vx1; vx1 = vx2; vx2 = new Vector3f(
					 * coords[j+3].value[0], coords[j+3].value[1],
					 * coords[j+3].value[2] );
					 */
					vx0.set(vx1);
					vx1.set(vx2);
					// System.err.println( "Coordinates: " +
					// coords[j+3].value[0] + " " + coords[j+3].value[1] + " " +
					// coords[j+3].value[2] );
					vx2.set(coords[j + 3].value[0], coords[j + 3].value[1],
							coords[j + 3].value[2]);
				}
			}
		}
	}

	public static void smooth(final Vector3f[] coords, final int steps) {

		if (coords.length < 3) {
			return;
		}

		// This is how it works:
		// Assume we have 3 consequtive points x0, x1, x2. We need to move the
		// midle point, x1, allong the height to the triangle side
		// associated with vertices x0 and x2, by half the length of the height.
		// Three formulas are used
		//
		// // x1' = 0.5*( x0 + x ) ( 1 )
		//
		// where vector x1' is the new position of x1 and x is given by the
		// formula
		//
		// // x = x0 + [ (x1-x0) .dot. n ] n ( 2 )
		//
		// and n is the versor in the direction of the line determined by x0 and
		// x2, i.e.
		//
		// // n = (x2-x0)/|x2-x0| ( 3 )
		//
		final int size = coords.length;
		float scale = 0.0f;
		Vector3f vx0, vx1, vx2, vx1p, vx, vn, dvx;
		vn = new Vector3f();
		dvx = new Vector3f();
		vx = new Vector3f();
		vx1p = new Vector3f();
		vx0 = new Vector3f();
		vx1 = new Vector3f();
		vx2 = new Vector3f();
		for (int i = 0; i < steps; i++) {
			// Adopt a sliding window approach
			//
			vx0.set(coords[0]);
			vx1.set(coords[1]);
			vx2.set(coords[2]);
			for (int j = 0; j < size - 2; j++) {
				vn.sub(vx2, vx0);
				vn.normalize();
				dvx.sub(vx1, vx0);
				scale = dvx.dot(vn);
				vx.scaleAdd(scale, vn, vx0);
				vx1p.add(vx, vx1);
				vx1p.scale(0.5f);

				// Now we can (re)set the coordinate at j+1
				coords[j + 1].set(vx1p);

				// And reset the vectors we need
				if (j != (size - 3)) {
					/*
					 * vx0 = vx1; vx1 = vx2; vx2 = new Vector3f(
					 * coords[j+3].value[0], coords[j+3].value[1],
					 * coords[j+3].value[2] );
					 */
					vx0.set(vx1);
					vx1.set(vx2);
					// System.err.println( "Coordinates: " +
					// coords[j+3].value[0] + " " + coords[j+3].value[1] + " " +
					// coords[j+3].value[2] );
					vx2.set(coords[j + 3]);
				}
			}
		}
	}

}
