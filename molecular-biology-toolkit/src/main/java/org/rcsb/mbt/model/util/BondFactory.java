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
 * Created on 2007/07/19
 *
 */ 
package org.rcsb.mbt.model.util;



import java.util.*;

import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.geometry.ArrayLinearAlgebra;


/**
 *  This class provides methods for generating Bond objects from a
 *  Structure or vector of Atom objects. Bonds are typically generated
 *  and managed by the StrucureMap class.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Bond
 *  @see	org.rcsb.mbt.model.StructureMap
 */
public class BondFactory
{
	// JLM: Perhaps add set/get methods to let users change bond limits?

	/**
	 * Cut-off distance limit for covalent bond formation.
	 */
	private static final float covalentBondLimit   = 1.9f;

	/**
	 * Cut-off distance limit for peptide bond formation.
	 */
	private static float peptideBondLimit    = 3.0f;

	/**
	 *  Return a vector of Bond objects extracted from a Structure
	 *  using the default covalentBondLimit as the cut-off distance.
	 *  <P>
	 */
	public static Vector<Bond> generateBonds( final Structure structure )
	{
		return BondFactory.generateBonds( structure, BondFactory.covalentBondLimit);
	}

	/**
	 *  Return a vector of Bond objects extracted from a Structure
	 *  using the specified covalentBondLimit distance.
	 *  The algorithm uses fixed-size cells to "bucket" the atom neighbors.
	 *  <P>
	 */
	public static Vector<Bond> generateBonds( final Structure structure, final float bondLimit)
	{
		final double coordinateBounds[][] =
			AtomStats.getAtomCoordinateBounds( structure );

		//
		// Get the atom coordinate bounds.
		//

		final double minCoord[] = coordinateBounds[0];
		final double maxCoord[] = coordinateBounds[1];

		//
		// Compute search cell grid dimensions
		//

		// Range of search space
		final double gridRange[] = {
			maxCoord[0] - minCoord[0],
			maxCoord[1] - minCoord[1],
			maxCoord[2] - minCoord[2]
		};

		// Cell grid size
		// JLM DEBUG: A cell DIAGONAL distance should be bondLimit !?
		final double cellSize[] = {
			bondLimit * 0.5f,
			bondLimit * 0.5f,
			bondLimit * 0.5f
		};

		// Cell grid count
		final int cellCount[] = {
			(int) (gridRange[0] / cellSize[0]),
			(int) (gridRange[1] / cellSize[1]),
			(int) (gridRange[2] / cellSize[2])
		};

		//
		// Build the bond search structures.
		//

		final int atomCount = structure.getStructureComponentCount(
			ComponentType.ATOM );

		final Hashtable<Integer,Integer> occupiedCells = new Hashtable<Integer,Integer>( atomCount );
		final int cellCoord[] = new int[3];
		int cellIndex = -1;

		for ( int i=0; i<atomCount; i++ )
		{
			final Atom atom = (Atom) structure.getStructureComponentByIndex(
				ComponentType.ATOM, i );

			// Compute the 3D cell coordinate in which the atom lives
			cellCoord[0] = (int)
				((atom.coordinate[0] - minCoord[0]) / cellSize[0]);
			cellCoord[1] = (int)
				((atom.coordinate[1] - minCoord[1]) / cellSize[1]);
			cellCoord[2] = (int)
				((atom.coordinate[2] - minCoord[2]) / cellSize[2]);

			// Map the 3D cell coordinate to a 1D cell index
			cellIndex =
				(cellCoord[0]*cellCount[1]+cellCoord[1]) * cellCount[2] + cellCoord[2];

			// Store the atom index that lives at this 1D cell index
			occupiedCells.put( new Integer( cellIndex ), new Integer( i ) );
		}

		//
		// Extract the bonds.
		//

		final Vector<Bond> bondList = new Vector<Bond>( );

		for ( int i=0; i<atomCount; i++ )
		{
			final Atom atom = (Atom) structure.getStructureComponentByIndex(
				ComponentType.ATOM, i );

			// Compute the cell coordinate in which the atom lives
			cellCoord[0] = (int)
				((atom.coordinate[0] - minCoord[0]) / cellSize[0]);
			cellCoord[1] = (int)
				((atom.coordinate[1] - minCoord[1]) / cellSize[1]);
			cellCoord[2] = (int)
				((atom.coordinate[2] - minCoord[2]) / cellSize[2]);

			// Check the 26 neighbors around this cell for an atom index.
			for ( int x=-1; x<=1; x++ )
			{
				for ( int y=-1; y<=1; y++ )
				{
					for ( int z=-1; z<=1; z++ )
					{
						// Skip our own cell
						if ( (x == 0) && (y == 0) && (z == 0) ) {
							continue;
						}

						// Map the 3D cell coordinate to a 1D cell index
						cellIndex =
							((cellCoord[0]+x)*cellCount[1]+(cellCoord[1]+y)) * cellCount[2] + (cellCoord[2]+z);

						// Try to fetch the neighbor
						final Integer neighborAtom = occupiedCells.get( new Integer( cellIndex ) );

						// Is there an occupied neighbor cell?
						if ( neighborAtom != null )
						{
							final int i2 = neighborAtom.intValue( );
							// Only add a bond between two atoms in
							// one direction (ie: only add a bond once!)
							if ( i < i2 )
							{
								final Atom atom2 = (Atom)
									structure.getStructureComponentByIndex(
									ComponentType.ATOM, i2 );
								// Only add a bond if atoms are close enough
								if ( BondFactory.distance( atom, atom2 ) <= bondLimit )
								{
									final Bond bond = new Bond( atom, atom2 );
									bondList.add( bond );
								}
							}
						}
					}
				}
			}
		}

		return bondList;
	}

	/**
	 *  Given a vector of Atom objects, return a Vector of Bond objects.
	 *  The cutOffDistance parameter specifies the maximum cartesian
	 *  distance allowed to form a bond.
	 *  The algorithm uses an octree to find atom neighbors.
	 *  <P>
	 */
	public static Vector<Bond> generateBonds( final Vector<Atom> atoms, final float cutOffDistance, final boolean useCovalentRestrictions )
	{
		Vector<Bond> result = null;

		// Create an octree and and populate it with Atom coordinates.
		try
		{
			final int atomCount = atoms.size( );
			final OctreeAtomItem[] octreeAtomItems = new OctreeAtomItem[ atomCount ];

			for( int i=0; i<atomCount; i++ )
			{
				final Atom atom = atoms.elementAt( i );
				octreeAtomItems[i] = new OctreeAtomItem( atom, i );
			}

			// TODO: The Octree code needs to handle cases that are causing
			// out of memory conditions and stack overflows.
			final float[] margin = new float[] { 0.0f, 0.0f, 0.0f };
			final Octree octree = new Octree( 3, octreeAtomItems, margin );
			octree.build( );

			// Generate a vector of Bond objects.
			result = octree.getBondsVector( cutOffDistance );
		}
		catch ( final org.rcsb.mbt.model.util.ExcessiveDivisionException ede )
		{
			Status.output( Status.LEVEL_ERROR, "Some atom coordinates may be too close to create bonds (" + ede.toString() + ")." );
			result = null;
		}
		catch( final OutOfMemoryError oome )
		{
			Status.output( Status.LEVEL_ERROR, "Not enough memory to create bonds (" + oome.toString() + ")." );
			result = null;
		}
		catch( final StackOverflowError soe )
		{
			Status.output( Status.LEVEL_ERROR, "Not enough stack space to create bonds (" + soe.toString() + ")." );
			result = null;
		}

		// No point in continuing if the result is null.
		if ( result == null ) {
			return null;
		}

		// Walk the bonds to make sure they are chemically valid.
		final int bondCount = result.size( );
		for ( int b=bondCount-1; b>=0; b-- )
		{
			final Bond bond = result.elementAt( b );
			final Atom atom0 = bond.getAtom( 0 );
			final Atom atom1 = bond.getAtom( 1 );

			// A bond can't connect two hydrogen atoms!
			if ( useCovalentRestrictions && atom0.element.equals( "H" ) && atom1.element.equals( "H" ) ) {
				result.remove( b );
			}

			// A bond can't connect an atom to itself!
			if ( atom0 == atom1 ) {
				result.remove( b );
			}
			if ( atom0.hashCode() == atom1.hashCode() ) {
				result.remove( b );
			}
		}

		return result;
	}

	/**
	 *  Given a vector of Atom objects, return a Vector of Bond objects.
	 *  Use the default covalentBondLimit as the cut-off distance.
	 *  The algorithm uses an octree to find atom neighbors.
	 *  <P>
	 */
	public static Vector<Bond> generateCovalentBonds( final Vector<Atom> atoms )
	{
		return BondFactory.generateBonds( atoms, BondFactory.covalentBondLimit, true );
	}


	/**
	 *  Given an existing StructureMap, generate (add/remove) Bond objects
	 *  by walking the residue list then using a chemical compound
	 *  bond dictionary to look for known valid/invalid bonds.
	 *  For compounds that are not in the dictionary, generate bonds
	 *  using the octree / distances.
	 *  If a chemical compound is not in the dictionary, an attempt is made
	 *  to generate bonds by the distance method.
	 *  Also generate inter-residue bonds (peptide, nucleotide, disulphide).
	 *  <P>
	 */
	public static void generateCovalentBonds( final StructureMap structureMap )
	{
		if ( structureMap == null ) {
			return;
		}

		final int residueCount = structureMap.getResidueCount( );
		for ( int r=0; r<residueCount; r++ )
		{
			final Residue residue = structureMap.getResidue( r );
			final String compoundCode = residue.getCompoundCode( );
			final Residue.Classification classification = residue.getClassification( );
			if ( (classification == Residue.Classification.AMINO_ACID) ||
				(classification == Residue.Classification.NUCLEIC_ACID) ||
				classification == Residue.Classification.BIRD)
			{
				// Check for disulphide bonds.
				/*
				if ( "CYS".equals( compoundCode ) )
				{
					// Examples: 1dl0, 1eh5, 1ei9, 1flg, 10br, 4aah
					// JLM DEBUG: Search for disulphide bonds...
					// ie: other cystines with sulfur in this chain?
					// Perhaps just add all Cys into a vector and
					// then go back and process them after we've
					// walked all the residues?
				}
				*/

				// Look ahead at the next residue (if there is one)
				// to see if there should be an inter-residue bond.

				// Try to get the next residue
				final int nextResidueIndex = r + 1;
				if ( nextResidueIndex < residueCount )
				{
					final Residue nextResidue = structureMap.getResidue( nextResidueIndex );

					// Only care about residues in the same chain.
					final String resChainId = residue.getChainId( );
					final String nextResChainId = nextResidue.getChainId( );
					if ( resChainId.equals( nextResChainId ) )
					{
						// Try to get the "tail" and "head" atoms.
						final Atom tailAtom = residue.getPolymerTailAtom( );
						final Atom headAtom = nextResidue.getPolymerHeadAtom( );
						if ( (tailAtom != null) && (headAtom != null) )
						{
							final double distance = ArrayLinearAlgebra.distance(tailAtom.coordinate, headAtom.coordinate);
							if (distance < 5) { // ensure that the maximum bond
								// length is less than 5
								// angstroms. Handles cases when
								// residues are missing.
								structureMap.addBond(new Bond(tailAtom, headAtom));
							}
						}
					}
				}
			}

			//
			// Handle intra-residue bonds.
			//

			if ( ChemicalComponentBonds.knownCompound( compoundCode ) )
			{
				final int atomCount = residue.getAtomCount( );
				
				if ( atomCount <= 1 )
					continue;  // Ignore single-atom compounds (eg: HOH).
				
				for ( int a0 = 0; a0 < atomCount; a0++ )
								// loop through all the atoms in the residue
				{
					final Atom atom0 = residue.getAtom( a0 );
					
					if ( atom0 == null )
						continue;  // Shouldn't happen

					for ( int a1 = a0 + 1; a1<atomCount; a1++ )
									// loop through all the atoms following a0
					{
						final Atom atom1 = residue.getAtom( a1 );
						if ( atom1 == null)
							continue;  // Shouldn't happen
						
/* **
						if (DebugState.isDebug())
						{
							String checkCompoundCode = "998",
								   atomCheck0 = "O1",
								   atomCheck1 = "C1";
							if (compoundCode.equals(checkCompoundCode)) // &&
								//(atom0.name.equals(atomCheck0) && atom1.name.equals(atomCheck1)) ||
								//(atom0.name.equals(atomCheck1) && atom1.name.equals(atomCheck0)))
							{
								int xix = 0;
											// just a place to stop...
							}
						}
/* **/
						final Bond bond = new Bond( atom0, atom1 );
												
						if ( bond == null )
								continue;  // Shouldn't happen

						// Don't allow an atom to bond to itself
						if ( atom0 == atom1 )
						{
							structureMap.removeBond( bond );
							continue;
						}

						final ChemicalComponentBonds.BondOrder bondType =
							ChemicalComponentBonds.bondType( atom0, atom1 );

						switch (bondType)
						{
							case UNKNOWN: continue;
							case NONE: structureMap.removeBond( bond ); break;

							default:
								// Assign a bond order from the dictionary.
								bond.setOrder(bondType.order);
	
								// If the bond is a "sane" distance, then add it.
								if ( bond.getDistance() < BondFactory.peptideBondLimit )
									structureMap.addBond( bond );
						}
					}
				}
			}
			else // Unknown compoundCode
			{
				// The compound is not in the dictionary, so at least
				// generate bonds for it using the octree / distances.
				Vector<Bond> bondVector = BondFactory.generateBonds(
						residue.getAtoms(), BondFactory.covalentBondLimit, true );
				
				structureMap.addBonds( bondVector );
				if (DebugState.isDebug())
					structureMap.markCalculatedBonds(
							compoundCode + " (ChainID: " + residue.getAtom(0).chain_id + ")",
							bondVector);
			}

		}
	}


	/**
	 *  Given an existing StructureMap, generate (add/remove) Bond objects
	 *  by walking the residue list then using a chemical compound
	 *  bond dictionary to look for known valid/invalid bonds.
	 *  For compounds that are not in the dictionary, generate bonds
	 *  using the octree / distances.
	 *  If a chemical compound is not in the dictionary, an attempt is made
	 *  to generate bonds by the distance method.
	 *  Also generate inter-residue bonds (peptide, nucleotide, disulphide).
	 *  <P>
	 *  This variant also provides a means to specify a culling cut-off
	 *  value so that, even if a dictionary-specified bond is called for,
	 *  if the actual atom distances are larger than the specified cut-off,
	 *  the bond will not be added.
	 *  <P>
	 */
	public static void generateCovalentBonds( final StructureMap structureMap,
		final float cullCutoff )
	{
		final float savedCutOff = BondFactory.peptideBondLimit; // Save the global limit
		BondFactory.generateCovalentBonds( structureMap );
		BondFactory.peptideBondLimit = savedCutOff;        // Restore the global limit
	}


	/**
	 *  Return the coordinate distance between two Atom objects.
	 *  <P>
	 */
	public static double distance( final Atom atomA, final Atom atomB )
	{
		return ArrayLinearAlgebra.distance(atomA.coordinate, atomB.coordinate );
	}
}

