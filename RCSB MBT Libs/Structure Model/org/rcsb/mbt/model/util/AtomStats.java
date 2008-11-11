//  $Id: AtomStats.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: AtomStats.java,v $
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
//  Revision 1.3  2004/04/09 00:15:20  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.2  2004/01/29 17:29:06  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.1  2003/04/23 22:52:06  moreland
//  Created utility methods for computing coordinate bounds and averages.
//
//  Revision 1.1.1.1  2002/07/16 18:00:21  moreland
//  Initial revision.
//


package org.rcsb.mbt.model.util;

import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;


/**
 *  The AtomStats class provides a number of static methods for computing
 *  useful information about a collection of Atom objects.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Atom
 */
public class AtomStats
{
	/**
	 * Return the coordinate bounds for given a Structure.
	 * <P>
	 * float[0][0] = min x<BR>
	 * float[0][1] = min y<BR>
	 * float[0][2] = min z<BR>
	 * float[1][0] = max x<BR>
	 * float[1][1] = max y<BR>
	 * float[1][2] = max z<BR>
	 * <P>
	 */
	public static double[][] getAtomCoordinateBounds( final Structure structure )
	{
		if ( structure == null ) {
			throw new IllegalArgumentException( "null structure" );
		}

		final int atomCount = structure.getStructureComponentCount(
			ComponentType.ATOM );

		final double coordinateBounds[][] = new double[2][3];
		if ( atomCount <= 0 ) {
			return coordinateBounds;
		}

		Atom atom = (Atom) structure.getStructureComponentByIndex(
			ComponentType.ATOM, 0 );

		coordinateBounds[0][0] = atom.coordinate[0];  // min x
		coordinateBounds[0][1] = atom.coordinate[1];  // min y
		coordinateBounds[0][2] = atom.coordinate[2];  // min z
		coordinateBounds[1][0] = atom.coordinate[0];  // max x
		coordinateBounds[1][1] = atom.coordinate[1];  // max y
		coordinateBounds[1][2] = atom.coordinate[2];  // max z

		for ( int i=1; i<atomCount; i++ )
		{
			atom = (Atom) structure.getStructureComponentByIndex(
				ComponentType.ATOM, i );

			if ( atom.coordinate[0] < coordinateBounds[0][0] ) {
				coordinateBounds[0][0] = atom.coordinate[0];  // min x
			}

			if ( atom.coordinate[1] < coordinateBounds[0][1] ) {
				coordinateBounds[0][1] = atom.coordinate[1];  // min y
			}

			if ( atom.coordinate[2] < coordinateBounds[0][2] ) {
				coordinateBounds[0][2] = atom.coordinate[2];  // min z
			}

			if ( atom.coordinate[0] > coordinateBounds[1][0] ) {
				coordinateBounds[1][0] = atom.coordinate[0];  // max x
			}

			if ( atom.coordinate[1] > coordinateBounds[1][1] ) {
				coordinateBounds[1][1] = atom.coordinate[1];  // max y
			}

			if ( atom.coordinate[2] > coordinateBounds[1][2] ) {
				coordinateBounds[1][2] = atom.coordinate[2];  // max z
			}
		}

		return coordinateBounds;
	}

	/**
	 * Return the coordinate average for a Structure's atom coordinates.
	 * <P>
	 * float[0] = x<BR>
	 * float[1] = y<BR>
	 * float[2] = z<BR>
	 * <P>
	 */
	public static double[] getAtomCoordinateAverage( final Structure structure )
		throws IllegalArgumentException
	{
		if ( structure == null ) {
			throw new IllegalArgumentException( "null structure" );
		}

		final int atomCount = structure.getStructureComponentCount(
			ComponentType.ATOM );

		final double coordinateAverage[] = new double[3];
		if ( atomCount <= 0 ) {
			return coordinateAverage;
		}

		Atom atom = (Atom) structure.getStructureComponentByIndex(
				ComponentType.ATOM, 0 );

		for ( int i=1; i<atomCount; i++ )
		{
			atom = (Atom) structure.getStructureComponentByIndex(
				ComponentType.ATOM, i );

			coordinateAverage[0] += atom.coordinate[0];
			coordinateAverage[1] += atom.coordinate[1];
			coordinateAverage[2] += atom.coordinate[2];
		}

		coordinateAverage[0] /= atomCount;
		coordinateAverage[1] /= atomCount;
		coordinateAverage[2] /= atomCount;

		return coordinateAverage;
	}
}
