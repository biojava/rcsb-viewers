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
import org.rcsb.mbt.model.StructureMap.BiologicUnitTransforms;
import org.rcsb.mbt.model.StructureMap.BiologicUnitTransforms.BiologicalUnitGenerationMapByChain;
import org.rcsb.mbt.model.geometry.ModelTransformationList;
import org.rcsb.mbt.model.geometry.ModelTransformationMatrix;


/**
 *  The AtomStats class provides a number of static methods for computing
 *  useful information about a collection of Atom objects.
 *  <P>
 *  @author	John L. Moreland
 *  @author Peter Rose (additions)
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
	 * Returns the coordinate bounds for the biological molecule.
	 * <P>
	 * double[0][0] = min x<BR>
	 * double[0][1] = min y<BR>
	 * double[0][2] = min z<BR>
	 * double[1][0] = max x<BR>
	 * double[1][1] = max y<BR>
	 * double[1][2] = max z<BR>
	 * <P>
	 * @param structure
	 * @return bounds
	 */
	public static double[][] getBiologicalMoleculeBounds( final Structure structure )
	{
		if ( structure == null ) {
			throw new IllegalArgumentException( "null structure" );
		}

		if ( ! structure.getStructureMap().hasBiologicUnitTransforms()) {
			return getAtomCoordinateBounds(structure);
		}

		BiologicUnitTransforms t = structure.getStructureMap().getBiologicUnitTransforms();
		BiologicalUnitGenerationMapByChain map = t.getBiologicalUnitGenerationMatricesByChain();
		
		if (map == null) {
			return getAtomCoordinateBounds(structure);
		}

		final int atomCount = structure.getStructureComponentCount(
				ComponentType.ATOM );

		final double coordinateBounds[][] = new double[2][3];
		if ( atomCount <= 0 ) {
			return coordinateBounds;
		}

		Atom atom = (Atom) structure.getStructureComponentByIndex(
				ComponentType.ATOM, 0 );

		coordinateBounds[0][0] = Double.MAX_VALUE;  // min x
		coordinateBounds[0][1] = Double.MAX_VALUE;  // min y
		coordinateBounds[0][2] = Double.MAX_VALUE;  // min z
		coordinateBounds[1][0] = Double.MIN_VALUE;  // max x
		coordinateBounds[1][1] = Double.MIN_VALUE;  // max y
		coordinateBounds[1][2] = Double.MIN_VALUE;  // max z
		double[] transformedCoordinate = new double[3];

		for ( int i = 0; i < atomCount; i++ )
		{
			atom = (Atom) structure.getStructureComponentByIndex(
					ComponentType.ATOM, i );

			ModelTransformationList list = map.get(atom.chain_id);
			if (list != null) {
				for (ModelTransformationMatrix m: list) {
					m.transformPoint(atom.coordinate, transformedCoordinate);

					if (transformedCoordinate[0] < coordinateBounds[0][0] ) {
						coordinateBounds[0][0] = transformedCoordinate[0];  // min x
					}

					if (transformedCoordinate[1] < coordinateBounds[0][1] ) {
						coordinateBounds[0][1] = transformedCoordinate[1];  // min y
					}

					if (transformedCoordinate[2] < coordinateBounds[0][2] ) {
						coordinateBounds[0][2] = transformedCoordinate[2];  // min z
					}

					if (transformedCoordinate[0] > coordinateBounds[1][0] ) {
						coordinateBounds[1][0] = transformedCoordinate[0];  // max x
					}

					if (transformedCoordinate[1] > coordinateBounds[1][1] ) {
						coordinateBounds[1][1] = transformedCoordinate[1];  // max y
					}

					if (transformedCoordinate[2] > coordinateBounds[1][2] ) {
						coordinateBounds[1][2] = transformedCoordinate[2];  // max z
					}
				}
			}
		}

		return coordinateBounds;
	}
	
    /**
     * Returns the maximum extend of the structure in the x, y, or z direction.
     * @param structure
     * @return maximum extend
     */
	public static double getMaximumExtend( final Structure structure ) {
		double[][] bounds = getAtomCoordinateBounds(structure);
		double xMax = Math.abs(bounds[0][0] - bounds[1][0]);
		double yMax = Math.abs(bounds[0][1] - bounds[1][1]);
		double zMax = Math.abs(bounds[0][2] - bounds[1][2]);
		return Math.max(xMax, Math.max(yMax, zMax));
	}
	
	/**
     * Returns the maximum extend of the biological molecule in the x, y, or z direction.
     * @param structure
     * @return maximum extend
     */
	public static double getBiologicalMoleculeMaximumExtend( final Structure structure ) {
		double[][] bounds = getBiologicalMoleculeBounds(structure);
		double xMax = Math.abs(bounds[0][0] - bounds[1][0]);
		double yMax = Math.abs(bounds[0][1] - bounds[1][1]);
		double zMax = Math.abs(bounds[0][2] - bounds[1][2]);
		return Math.max(xMax, Math.max(yMax, zMax));
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

	/**
	 * Returns the centroid of the biological molecule.
	 * @param structure
	 * @return centroid
	 * @throws IllegalArgumentException if structure is null
	 */
	
	public static double[] getBiologicalMoleculeCentroid( final Structure structure )
	throws IllegalArgumentException
	{
		if ( structure == null ) {
			throw new IllegalArgumentException( "null structure" );
		}

		final int atomCount = structure.getStructureComponentCount(
				ComponentType.ATOM );

		double[] centroid = new double[3];

		if ( atomCount <= 0 ) {
			return centroid;
		}

		if ( ! structure.getStructureMap().hasBiologicUnitTransforms()) {
			return getAtomCoordinateAverage(structure);
		}

		BiologicUnitTransforms t = structure.getStructureMap().getBiologicUnitTransforms();
		BiologicalUnitGenerationMapByChain map = t.getBiologicalUnitGenerationMatricesByChain();
		
		if (map == null) {
			return getAtomCoordinateAverage(structure);
		}

		Atom atom = (Atom) structure.getStructureComponentByIndex(
				ComponentType.ATOM, 0 );

		int count = 0;
		double[] transformedCoordinate = new double[3];

		for (int i = 0; i < atomCount; i++)
		{
			atom = (Atom) structure.getStructureComponentByIndex(
					ComponentType.ATOM, i );

			ModelTransformationList list = map.get(atom.chain_id);
			if (list != null) {
				for (ModelTransformationMatrix m: list) {
					m.transformPoint(atom.coordinate, transformedCoordinate);
					centroid[0] += transformedCoordinate[0];
					centroid[1] += transformedCoordinate[1];
					centroid[2] += transformedCoordinate[2];
					count++;
				}
			}
		}

		centroid[0] /= count;
		centroid[1] /= count;
		centroid[2] /= count;

		return centroid;
	}
}
