//  $Id: BondFactory.java,v 1.2 2007/07/19 13:09:31 jbeaver Exp $
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
//  $Log: BondFactory.java,v $
//  Revision 1.2  2007/07/19 13:09:31  jbeaver
//  Fixed long bonds
//
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
//  Revision 1.17  2005/02/03 18:15:27  moreland
//  Fixed bug that could produce Bonds connecting one atom to itself.
//
//  Revision 1.16  2004/10/22 23:34:19  moreland
//  Added support for TRIPPLE bond orders.
//
//  Revision 1.15  2004/06/09 00:34:57  moreland
//  Added a variant of "generateCovalentBonds" that also takes a culling cut-off.
//
//  Revision 1.14  2004/06/07 16:33:55  moreland
//  Now assigns a bond order when a Bond is generated from the dictionary.
//
//  Revision 1.13  2004/05/12 23:51:05  moreland
//  Commented out disulphide bond block (hydrogen bonds are TBD later).
//
//  Revision 1.12  2004/05/05 21:36:28  moreland
//  Added support for inter-residue bonds (ie: polypeptide, nucleotide).
//  Disulphide bond are still to come...
//
//  Revision 1.11  2004/04/29 23:14:53  moreland
//  This class is now the toolkit-wide home for all bond cut-off limits.
//  Added new cut-off limits for peptide, disulphide, and hydrogen bonds.
//  Improved dictionary-based bond generation and added inter-residue bond support.
//
//  Revision 1.10  2004/04/15 20:44:45  moreland
//  Changed to new UCSD copyright statement.
//  Added improveCovalentBonds method (uses chemical component bond dictionary).
//
//  Revision 1.9  2004/02/05 18:35:40  moreland
//  Now computes distances using the Algebra class methods.
//
//  Revision 1.8  2004/01/29 17:29:07  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.7  2004/01/15 00:58:33  moreland
//  Added code to catch new ExcessiveDivisionException when two atoms with
//  very close (or equal) coordinates create too many octree subdivisions.
//
//  Revision 1.6  2004/01/13 21:38:44  moreland
//  Returns null bond list if out of memory or stack problem is encountered.
//
//  Revision 1.5  2004/01/13 18:53:11  moreland
//  Added code to walk the candidate bonds to make sure they are chemically valid.
//  For example, a bond can't connect two hydrogen atoms.
//
//  Revision 1.4  2003/12/16 21:48:02  moreland
//  Added more exception handling.
//  The getDistance utility method now simply calls Bond.getDistance.
//
//  Revision 1.3  2003/07/11 23:08:46  moreland
//  Added generateCovalentBonds methods which use a logN Octree algorithm.
//
//  Revision 1.2  2003/04/24 21:00:42  moreland
//  Moved distance calculation code to a public method since its used a lot.
//
//  Revision 1.1  2003/04/23 22:52:43  moreland
//  Created utility methods for creating Bond objects.
//
//  Revision 1.0  2003/04/03 23:08:11  moreland
//  First version.
//


package org.rcsb.mbt.model.util;



import java.util.*;

import org.rcsb.mbt.model.*;


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
	private static float peptideBondLimit    = 2.3f;

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
			StructureComponentRegistry.TYPE_ATOM );

		final Hashtable occupiedCells = new Hashtable( atomCount );
		final int cellCoord[] = new int[3];
		int cellIndex = -1;

		for ( int i=0; i<atomCount; i++ )
		{
			final Atom atom = (Atom) structure.getStructureComponentByIndex(
				StructureComponentRegistry.TYPE_ATOM, i );

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
				StructureComponentRegistry.TYPE_ATOM, i );

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
						final Integer neighborAtom = (Integer)
							occupiedCells.get( new Integer( cellIndex ) );

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
									StructureComponentRegistry.TYPE_ATOM, i2 );
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
				final Atom atom = (Atom) atoms.elementAt( i );
				octreeAtomItems[i] = new OctreeAtomItem( atom, i );
			}

			// TODO: The Octree code needs to handle cases that are causing
			// out of memory conditions and stack overflows.
			final float[] margin = new float[] { 0.0f, 0.0f, 0.0f };
			final Octree octree = new Octree( margin.length, octreeAtomItems, margin );
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
			final Bond bond = (Bond) result.elementAt( b );
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
			final String classification = residue.getClassification( );
			final Atom alphaAtom = residue.getAlphaAtom( );

			//
			// Handle inter-residue bonds (ie: polypeptide,
			// nucleotide, and disulphide).
			//

			if ( (classification == Residue.COMPOUND_AMINO_ACID) ||
				(classification == Residue.COMPOUND_NUCLEIC_ACID) )
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
							final double distance = Algebra.distance(tailAtom.coordinate, headAtom.coordinate);
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
				if ( atomCount <= 1 ) {
					continue;  // Ignore single-atom compounds (eg: HOH).
				}
				for ( int a0=0; a0<atomCount; a0++ )
				{
					final Atom atom0 = residue.getAtom( a0 );
					if ( atom0 == null ) {
						continue;  // Shouldn't happen
					}
					for ( int a1=a0 + 1; a1<atomCount; a1++ )
					{
						final Atom atom1 = residue.getAtom( a1 );
						if ( atom1 == null ) {
							continue;  // Shouldn't happen
						}
						if ( atom0 == atom1 ) {
							continue;  // Shouldn't happen
						}
						final Bond bond = new Bond( atom0, atom1 );
						if ( bond == null ) {
							continue;  // Shouldn't happen
						}

						// Don't allow an atom to bond to itself
						if ( atom0 == atom1 )
						{
							structureMap.removeBond( bond );
							continue;
						}

						final String bondType =
							ChemicalComponentBonds.bondType( atom0, atom1 );

						if ( bondType == null ) {
							continue; // Should not happen.
						}
						if ( bondType == ChemicalComponentBonds.BOND_TYPE_UNKNOWN ) {
							continue;
						}

						// System.err.println( "BondFactory.generateCovalentBonds: bond " + atom0.compound + "_" + atom0.name + ":" + atom1.name );
						if ( bondType == ChemicalComponentBonds.BOND_TYPE_NONE )
						{
							structureMap.removeBond( bond );
						}
						else
						{
							// Assign a bond order from the dictionary.
							if ( bondType == ChemicalComponentBonds.BOND_TYPE_SINGLE ) {
								bond.setOrder( 1.0f );
							} else if ( bondType == ChemicalComponentBonds.BOND_TYPE_DOUBLE ) {
								bond.setOrder( 2.0f );
							} else if ( bondType == ChemicalComponentBonds.BOND_TYPE_TRIPPLE ) {
								bond.setOrder( 3.0f );
							} else if ( bondType == ChemicalComponentBonds.BOND_TYPE_AROMATIC ) {
								bond.setOrder( 1.5f );
							}

							// If the bond is a "sane" distance, then add it.
							if ( bond.getDistance() < BondFactory.peptideBondLimit ) {
								structureMap.addBond( bond );
							}
						}
					}
				}
			}
			else // Unknown compoundCode
			{
				// The compound is not in the dictionary, so at least
				// generate bonds for it using the octree / distances.
				structureMap.addBonds( BondFactory.generateBonds(
					residue.getAtoms(), BondFactory.covalentBondLimit, true ) );
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
		return Algebra.distance( atomA.coordinate, atomB.coordinate );
	}
}

