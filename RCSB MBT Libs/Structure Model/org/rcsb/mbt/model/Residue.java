//  $Id: Residue.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Residue.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.19  2005/11/08 20:58:12  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.18  2004/06/24 21:11:21  moreland
//  With multiple occupancy, alphaAtomIndex is now set to the FIRST alpha atom.
//
//  Revision 1.17  2004/05/05 21:34:02  moreland
//  Added getPolymerHeadAtom and getPolymerTailAtom methods to enable the
//  formation of polymer bonds between adjacent residues.
//
//  Revision 1.16  2004/05/04 19:52:47  moreland
//  Added setAlphaAtomIndex method to enable handling of disordered residues.
//
//  Revision 1.15  2004/05/03 17:33:16  moreland
//  Updated comments to reflect how disordered residues (no alpha atom) are handled.
//
//  Revision 1.14  2004/04/09 00:17:00  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.13  2004/01/31 19:11:48  moreland
//  Removed outdated programming note.
//
//  Revision 1.12  2004/01/29 17:08:16  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.11  2003/12/20 01:01:43  moreland
//  Added support for determining the parent Fragment.
//
//  Revision 1.10  2003/07/11 22:57:10  moreland
//  Added getAtoms method which returns all Atom records contained in this Residue as a Vector.
//  NOTE: The private Vector is returned for speed. Callers should NOT MODIFY THIS VECTOR!
//
//  Revision 1.9  2003/04/30 19:57:25  moreland
//  Corrected alpha atom index assignment bug.
//
//  Revision 1.8  2003/04/30 17:50:34  moreland
//  Now correctly classifies nucleic acids.
//  Added binary search algorithm to find Atoms.
//
//  Revision 1.7  2003/04/25 00:58:21  moreland
//  Removed COMPOUND_UNKNOWN classification since there are only 3 types "ever" (bourne).
//
//  Revision 1.6  2003/04/24 21:04:31  moreland
//  Added getResidueId method.
//
//  Revision 1.5  2003/04/24 17:13:33  moreland
//  Conformation type now defaults to the Conformation.TYPE_UNDEFINED type.
//
//  Revision 1.4  2003/04/23 17:27:32  moreland
//  Removed StructureComponentID/scid and replaced it with a "structure" field
//  in the StructureComponent base class.
//  Changed "getType" method to "getStructureComponentType" to return dynamic
//  SC type (ie: class name).
//  Added residue classification (amino acid VS nucleic acid VS ligand) support.
//  Enabled compound code to be set when no Atoms exist (for pure sequence data).
//  Enabled adding/removing of Atom objects.
//
//  Revision 1.3  2003/04/03 18:23:36  moreland
//  Residue is now a container of Atom objects (or empty for sequence data).
//
//  Revision 1.2  2002/10/24 17:54:01  moreland
//  Provides implementation for the improved Structure API/implementation.
//
//  Revision 1.1.1.1  2002/07/16 18:00:18  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//


package org.rcsb.mbt.model;


import java.util.*;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.util.*;


/**
 *  Implements a StructureComponent container for residue information.
 *  This may be an amino acid residue, a nucleic acid residue, or a ligand.
 *  This class generally contains a list of Atom records, though it is
 *  perfectly fine to be empty when it is used only for sequence information.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.StructureComponent
 *  @see	org.rcsb.mbt.model.Structure
 *  @see	org.rcsb.mbt.model.StructureComponentIterator
 *  @see	org.rcsb.mbt.model.util.AminoAcidInfo
 */
public class Residue
	extends StructureComponent
	implements java.lang.Cloneable
{
	public enum Classification { AMINO_ACID, NUCLEIC_ACID, LIGAND, WATER }


	// The Atom index for the alpha (backbone) atom
	private int alphaAtomIndex = -1;
	private static final String aaAlphaAtomName = "CA";
	private static final String naAlphaAtomName = "P";

	// The Atom records (if any) for the residue.
	private Vector<Atom> atoms = null;

	// The parent Fragment (if any) for the residue.
	private Fragment parentFragment = null;

	// The 3-letter residue compound code, or, "UNK" for UNKNOWN.
	// See the AminoAcids and NucleicAcids classes for details.
	private String compoundCode = "UNK";

	// The compound classification for this residue.
	private Classification classification = Classification.LIGAND;

	// The secondary structure conformation type assigned to this residue.
	private ComponentType conformationType = ComponentType.UNDEFINED_CONFORMATION;

	// The polymer "head" and "tail" atoms for this amino acid or nucleic acid
	// residue (or null for ligands) can be used to form polymer bonds
	// between adjacent residues.
	private Atom polymerHeadAtom = null;
	private Atom polymerTailAtom = null;

	// The well-known polymer "head" and "tail" atom names for
	// amino acid or nucleic acid residues.
	// This can be used to form polypeptide bonds between adjacent amino
	// acids, or nucleic acid bonds between adjacent nucleic acids.
	private static final String aaHeadAtomName = "N";
	private static final String aaTailAtomName = "C";
	private static final String naHeadAtomName = "P";
	private static final String naTailAtomName = "O3'";

	//
	// Constructors
	//

	/**
	 * Constructor variant used when there will likely be no atom list
	 * (eg: when only sequence data is loaded).
	 */
	public Residue( final String in_compoundCode )
	{
		compoundCode = in_compoundCode;
	}

	/**
	 * Constructor variant used when there will likely be an atom list.
	 */
	public Residue( )
	{
	}

	//
	// StructureComponent methods.
	//

	/**
	 *  Copy all of the field values from the parameter object into "this".
	 */
	
	@Override
	public void copy( final StructureComponent structureComponent )
	{
		setStructure( structureComponent.getStructure() );
		final Residue residue = (Residue) structureComponent;

		compoundCode   = residue.compoundCode;
		alphaAtomIndex = residue.alphaAtomIndex;
	}

	/**
	 *  Clone this object.
	 */
	
	@Override
	public Object clone( )
		throws CloneNotSupportedException
	{
		return super.clone( );
	}

	private static String className = null;
	/**
	 *  This method returns the fully qualified name of this class.
	 *  <P>
	 *  This name is used by the StructureComponentRegistry class to enable
	 *  dynamic registration and discovery of new StructureComponent
	 *  sub-classes/types. The name is also used to create a unique integer
	 *  indentifier for each type in order to make run-time type comparisons
	 *  fast.
	 */
	public static String getClassName()
	{
		if ( Residue.className == null ) {
			Residue.className = Residue.class.getName();
		}
		return Residue.className;
	}

	/**
	 *  This method returns the fully qualified name of this class.
	 */
	
	@Override
	public ComponentType getStructureComponentType( )
	{
		return ComponentType.RESIDUE;
	}

	/**
	 *  Set the parent fragment (if any) for this residue.
	 *  Protected because only Chain should manage fragments.
	 */
	protected void setFragment( final Fragment fragment )
	{
		parentFragment = fragment;
	}

	/**
	 *  Get the parent fragment (if any) for this residue.
	 */
	public Fragment getFragment( )
	{
		return parentFragment;
	}


	//
	// Residue methods.
	//

	/**
	 * Return the compound classification for this residue.
	 * (eg: amino acid, nucleic acid, ligand, unknown).
	 */
	public Classification getClassification( )
	{
		return classification;
	}
	
	/**
	 * From the chain information, we've determined that the residue is
	 * really part of a ligand.  Reclassify it as such.
	 * Note we don't change waters.
	 */
	public void reClassifyAsLigand()
	{
		if (classification != Classification.WATER)
			classification = Classification.LIGAND;
	}

	/**
	 * Set the classification, one of:
	 * <ul>
	 * <li>AMINO_ACID</li>
	 * <li>NUCLEIC_ACID</li>
	 * <li>LIGAND</li>
	 * <li>WATER</li>
	 */
	public void setClassification( final Atom atom )
	{
		if (atom == null)
		{
			compoundCode = "UNK";
			classification = Classification.LIGAND;
		}
		
		else
		{
			compoundCode = atom.compound; 
		
			if ( compoundCode.equals("HOH"))
				classification = Classification.WATER;
			
			else if (atom.getStructure().getStructureMap().getChain(atom.chain_id).isNonProteinChain())
				classification = Classification.LIGAND;
			
			else if ( AminoAcidInfo.getNameFromCode( compoundCode ) != null )
				classification = Classification.AMINO_ACID;
			
			else if ( NucleicAcidInfo.isNucleotide( compoundCode ) )
				classification = Classification.NUCLEIC_ACID;
		}
	}

	/**
	 * Get the 3-letter compound code for this residue.
	 */
	public String getCompoundCode( )
	{
		return compoundCode;
	}

	/**
	 * Return the number of Atom records contained in this Residue.
	 */
	public int getAtomCount( )
	{
		if ( atoms == null ) {
			return 0;
		} else {
			return atoms.size();
		}
	}

	/**
	 * Return the specified Atom record contained in this Residue.
	 */
	public Atom getAtom( final int index )
	{
		if ( atoms == null ) {
			throw new IndexOutOfBoundsException( "residue has no atoms" );
		}

		return atoms.elementAt( index );
	}

	/**
	 * Return all Atom records contained in this Residue as a Vector.
	 * NOTE: The reference to the internal Vector is returned here
	 * for performance reasons, so DO NOT MODIFY THE VECTOR!
	 */
	public Vector<Atom> getAtoms( )
	{
		return atoms;
	}

	/**
	 * Add an Atom record to this Residue.
	 */
	public void addAtom( final Atom atom )
	{
		if ( atom == null ) {
			throw new IllegalArgumentException( "null atom" );
		}

		if ( atoms == null )
		{
			atoms = new Vector<Atom>( );
			setClassification( atom );
		}

		// Do a binary search to determine where this atom should be added.

		int low = 0;
		final int atomCount = atoms.size();
		int high = atomCount - 1;
		int mid = 0;
		final int atomNumber = atom.number;
		while ( low <= high )
		{
			mid = (low + high) / 2;
			final Atom atom2 = atoms.elementAt( mid );
			final int atomNumber2 = atom2.number;

			if ( atomNumber < atomNumber2 ) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}
		mid = (low + high) / 2;
		if ( mid < 0 ) {
			mid = 0;
		} else if ( mid >= atomCount ) {
			mid = atomCount;
		} else {
			mid += 1;
		}

		atoms.add( mid, atom );


		// Set the alphaAtomIndex, or the polymer head or tail atoms.

		if ( classification == Classification.AMINO_ACID )
		{
			if ( atom.name.equals( Residue.aaAlphaAtomName ) && (alphaAtomIndex < 0) ) {
				alphaAtomIndex = mid;
			}
			if ( atom.name.equals( Residue.aaHeadAtomName ) ) {
				polymerHeadAtom = atom;
			}
			if ( atom.name.equals( Residue.aaTailAtomName ) ) {
				polymerTailAtom = atom;
			}
		}
		else if ( classification == Classification.NUCLEIC_ACID )
		{
			if ( atom.name.equals( Residue.naAlphaAtomName ) && (alphaAtomIndex < 0) ) {
				alphaAtomIndex = mid;
			}
			if ( atom.name.equals( Residue.naHeadAtomName ) ) {
				polymerHeadAtom = atom;
			}
			if ( atom.name.equals( Residue.naTailAtomName ) ) {
				polymerTailAtom = atom;
			}
		}
	}

	/**
	 * Remove all Atom records from this Residue.
	 */
	public void removeAllAtoms( )
	{
		if ( atoms != null ) {
			atoms.removeAllElements( );
		}
		atoms = null;
		setClassification( null );
		alphaAtomIndex = -1;
		polymerHeadAtom = null;
		polymerTailAtom = null;
	}

	/**
	 * Remove the specified Atom record from this Residue.
	 */
	public void removeAtom( final int index )
	{
		if ( atoms == null ) {
			throw new IndexOutOfBoundsException( "residue has no atoms" );
		}
		final Atom atom = getAtom( index );
		atoms.remove( index );
		// Just removed the alpha atom
		if ( index == alphaAtomIndex ) {
			alphaAtomIndex = -1;
		}
		// Adjust the alpha atom index
		if ( index < alphaAtomIndex ) {
			alphaAtomIndex -= 1;
		}
		if ( alphaAtomIndex < 0 ) {
			alphaAtomIndex = -1;
		}
		if ( atoms.size() <= 0 ) {
			removeAllAtoms( );
		}

		if ( classification == Classification.AMINO_ACID )
		{
			if ( atom.name.equals( Residue.aaHeadAtomName ) ) {
				polymerHeadAtom = null;
			} else if ( atom.name.equals( Residue.aaTailAtomName ) ) {
				polymerTailAtom = null;
			}
		}
		else if ( classification == Classification.NUCLEIC_ACID )
		{
			if ( atom.name.equals( Residue.naHeadAtomName ) ) {
				polymerHeadAtom = null;
			} else if ( atom.name.equals( Residue.naTailAtomName ) ) {
				polymerTailAtom = null;
			}
		}
	}


	/**
	 * Get the alpha (backbone) Atom index for this amino acid or nucleic
	 * acid residue (or -1 for ligands or disordered residues that have no
	 * alpha atom).
	 */
	public int getAlphaAtomIndex( )
	{
		return alphaAtomIndex;
	}


	/**
	 * Set the alpha (backbone) Atom index for this
	 * amino acid or nucleic acid residue. For for ligands this will be
	 * set to -1 (ie: no valid alpha atom). For disordered residues
	 * (that have no alpha atom) it may be set to an alternate index
	 * so that applicatons may still draw backbone traces through the
	 * residue.
	 */
	public void setAlphaAtomIndex( final int index )
	{
		alphaAtomIndex = index;
	}

	/**
	 * Get the alpha (backbone) Atom for this
	 * amino acid or nucleic acid residue (or -1 for ligands).
	 */
	public Atom getAlphaAtom( )
	{
		if ( atoms == null ) {
			return null;
		}
		if ( alphaAtomIndex < 0 ) {
			return getAtom(0);
							// This is from the kiosk viewer version of residue.  It seems like
							// a harmless enough change I've gone ahead and put it in
							// the base code.  Eyes up.
							//
							// 05-Jun-08 - rickb
		}
		if ( alphaAtomIndex >= atoms.size() ) {
			return null;
		}
		return alphaAtomIndex >= 0 ? atoms.elementAt( alphaAtomIndex ) : atoms.elementAt(atoms.size() / 2);
	}

	/**
	 * Get the polymer "head" atom for this amino acid or nucleic acid residue
	 * (or null for ligands).
	 * This can be used to form polypeptide bonds between adjacent amino
	 * acids, or nucleic acid bonds between adjacent nucleic acids.
	 * <P>
	 */
	public Atom getPolymerHeadAtom( )
	{
		return polymerHeadAtom;
	}

	/**
	 * Get the polymer "tail" atom for this amino acid or nucleic acid residue
	 * (or null for ligands).
	 * This can be used to form polypeptide bonds between adjacent amino
	 * acids, or nucleic acid bonds between adjacent nucleic acids.
	 * <P>
	 */
	public Atom getPolymerTailAtom( )
	{
		return polymerTailAtom;
	}

	/**
	 * Return the Hydrophobicity of the amino acid residue as a normalized
	 * value from 0.0 to 1.0 inclusive.
	 * See the AminoAcid class for a description of what method is used
	 * to produce the hydrophobicity scale.
	 */
	public float getHydrophobicity( )
	{
		// AminoAcid aminoAcid = AminoAcids.getByCode( compoundCode );
		// return aminoAcid.getHydrophobicity( );
		return AminoAcidInfo.getHydrophobicityFromCode( compoundCode );
	}

	/**
	 * Set the secondary structure conformation type that should be assigned
	 * to this residue.
	 */
	public void setConformationType( final ComponentType type )
	{
		if ( type == null ) {
			throw new IllegalArgumentException( "null type" );
		// JLM DEBUG: We should throw an exception if the type is not one of
		// Conformation subclass type, or, Conformation.TYPE_UNDEFINED.
		}

		conformationType = type;
	}

	/**
	 * Get the secondary structure conformation type that is assigned
	 * to this residue.
	 */
	public ComponentType getConformationType( )
	{
		return conformationType;
	}

	/**
	 * Get the chain id (as it is assigned in the first Atom record).
	 * Return null if there are no atom records.
	 */
	public String getChainId( )
	{
		if ( atoms == null ) {
			return null;
		}
		final Atom atom = atoms.elementAt( 0 );
		if ( atom == null ) {
			return null;
		}
		return atom.chain_id;
	}

	/**
	 * Get the residue id (as it is assigned in the first Atom record).
	 * Return -1 if there are no atom records.
	 */
	public int getResidueId( )
	{
		if ( atoms == null ) {
			return -1;
		}
		final Atom atom = atoms.elementAt( 0 );
		if ( atom == null ) {
			return -1;
		}
		return atom.residue_id;
	}
	
	/**
	 * Apps may need to embellish the residue name with further information
	 * (Ligand Explorer does this).
	 * 
	 * One way to do this would be to allow the app to derive this class, but
	 * then we have construction problems, since this is instantiated from
	 * deep within the framework.
	 * 
	 * The original implementation forwards the name request to the model, so
	 * we'll preserve that, for now - note that the base implementation will
	 * simply invoke 'getCompoundCode()'.
	 */
	@Override
	public String toString()
	{
		return AppBase.sgetModel().getModifiedResidueName(this);
	}

}

