//  $Id: PickUtils.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.//
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
//  $Log: PickUtils.java,v $
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
//  Revision 1.2  2005/11/08 20:58:33  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.1  2004/10/22 23:42:31  moreland
//  First version.
//
//  Revision 1.0  2004/09/08 18:06:54  moreland
//  First version.
//
                                                                                
                                                                                
package org.rcsb.mbt.controllers.scene;


import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.geometry.Algebra;


/**
 *  This class provides a number of utility methods for handling picking
 *  and subsequent selection. Provides pick-level behavior mapping
 *  (eg: Atoms->Residues->Fragments->Chains). Provides closestAtom method
 *  to determine the closest atom relative to a specified StructureComponent
 *  and point in 3D space.
 *  <P>
 *  @author John L. Moreland
 *  @see    org.rcsb.mbt.model.StructureComponent
 */
public class PickController
{
	public enum PickLevel { AUTO, ATOMS, RIBBONS }

	protected PickLevel pickLevel;


	public PickController()
	{
		pickLevel = PickLevel.AUTO;
		/**
		 *  Set the structure component pick level.
		 */
	}

	public void setPickLevel( final PickLevel level )
	{
		pickLevel = level;
	}

	/**
	 *  Get the structure component pick level.
	 */
	public PickLevel getPickLevel( )
	{
		return pickLevel;
	}



	/**
	 *  Find the atom object within the specified structure component which
	 *  is closest to the given coordinate.
	 */
	public Atom closestAtom( final StructureComponent sc, final double[] coord )
	{
		Atom result = null;

		switch(sc.getStructureComponentType( ))
		{
		case ATOM:
			result = (Atom) sc;
			break;

		case BOND:
			final Bond bond = (Bond) sc;
			final Atom atom0 = bond.getAtom( 0 );
			final Atom atom1 = bond.getAtom( 1 );
			final double dist0 = Algebra.distance( coord, atom0.coordinate );
			final double dist1 = Algebra.distance( coord, atom1.coordinate );
			if ( dist0 <= dist1 ) {
				result = atom0;
			} else {
				result = atom1;
			}
			break;

		case RESIDUE:
		{
			final Residue residue = (Residue) sc;
			double minDist = 10000.0f;
			final int atomCount = residue.getAtomCount( );
			for ( int i=0; i<atomCount; i++ )
			{
				final Atom atom = residue.getAtom( i );
				final double dist = Algebra.distance( coord, atom.coordinate );
				if ( dist < minDist )
				{
					minDist = dist;
					result = atom;
				}
			}
		}
		break;

		case FRAGMENT:
		{
			final Fragment fragment = (Fragment) sc;
			double minDist = 10000.0f;
			final int residueCount = fragment.getResidueCount( );
			for ( int r=0; r<residueCount; r++ )
			{
				final Residue residue = fragment.getResidue( r );
				final int atomCount = residue.getAtomCount( );
				for ( int i=0; i<atomCount; i++ )
				{
					final Atom atom = residue.getAtom( i );
					final double dist = Algebra.distance( coord, atom.coordinate );
					if ( dist < minDist )
					{
						minDist = dist;
						result = atom;
					}
				}
			}
		}
		break;

		case CHAIN:
		{
			final Chain chain = (Chain) sc;
			double minDist = 10000.0f;
			final int residueCount = chain.getResidueCount( );
			for ( int r=0; r<residueCount; r++ )
			{
				final Residue residue = chain.getResidue( r );
				final int atomCount = residue.getAtomCount( );
				for ( int i=0; i<atomCount; i++ )
				{
					final Atom atom = residue.getAtom( i );
					final double dist = Algebra.distance( coord, atom.coordinate );
					if ( dist < minDist )
					{
						minDist = dist;
						result = atom;
					}
				}
			}
		}

		default:
			// If all else fails, walk through all the atoms!
			{
				final StructureMap structureMap = sc.getStructure().getStructureMap( );
				double minDist = 10000.0f;
				final int atomCount = structureMap.getAtomCount( );
				for ( int i=0; i<atomCount; i++ )
				{
					final Atom atom = structureMap.getAtom( i );
					final double dist = Algebra.distance( coord, atom.coordinate );
					if ( dist < minDist )
					{
						minDist = dist;
						result = atom;
					}
				}
			}
		}

		return result;
	}
}

