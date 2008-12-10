//  $Id: Priestle.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
//
//  Copyright 2000-2004 The Regents of the University of California.
//  All Rights Reserved.
//
//  Permission to use, copy, modify and distribute any part of this
//  Molecular Biology Toolkit (MBT)
//  for educational, research and non-profit purposes, without fee, and without
//  a written agreement is hereby granted, provided that the above copyright
//  notice, this paragraph and the following three paragraphs appear in all
//  copies.
//
//  Those desiring to incorporate this MBT into commercial products
//  or use for commercial purposes should contact the Technology Transfer &
//  Intellectual Property Services, University of California, San Diego, 9500
//  Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815,
//  FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
//
//  IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
//  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//  LOST PROFITS, ARISING OUT OF THE USE OF THIS MBT, EVEN IF THE
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//  THE MBT PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
//  UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
//  UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
//  NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
//  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
//  MBT WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
//
//  For further information, please see:  http://mbt.sdsc.edu
//
//  History:
//  $Log: Priestle.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.6  2005/06/29 17:02:50  agramada
//  Added another smooth method with new argument types.
//
//  Revision 1.5  2004/04/09 00:04:29  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.4  2004/01/29 18:05:30  agramada
//  Removed General Atomics from copyright
//
//  Revision 1.3  2003/12/09 00:44:05  agramada
//  Removed reference to debug class.
//
//  Revision 1.2  2003/07/11 00:01:59  agramada
//  Minor reformating.
//
//  Revision 1.1  2003/01/10 00:28:58  agramada
//  Initial checkin of geometry generation package.
//
//  Revision 1.0  2002/06/10 23:38:39  agramada
//

package org.rcsb.mbt.glscene.SecondaryStructureBuilder;

import org.rcsb.mbt.glscene.SecondaryStructureBuilder.vec.Vec3f;

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
