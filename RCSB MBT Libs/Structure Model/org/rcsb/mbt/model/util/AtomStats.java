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
