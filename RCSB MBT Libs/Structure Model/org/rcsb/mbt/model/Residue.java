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
package org.rcsb.mbt.model;


import java.util.Vector;

import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.util.ChemicalComponentInfo;
import org.rcsb.mbt.model.util.ChemicalComponentType;
import org.rcsb.mbt.model.util.ExternReferences;


/**
 *  Implements a StructureComponent container for residue information.
 *  This may be an amino acid residue, a nucleic acid residue, or a ligand.
 *  This class generally contains a list of Atom records, though it is
 *  perfectly fine to be empty when it is used only for sequence information.
 *  <P>
 *  @author	John L. Moreland
 *  @author	Peter Rose (revised)
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
	 *  Identifier for each type in order to make run-time type comparisons
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
	 * Sets the classification of the residue based on the 3-letter compound code of an atom
	 * @param atom 
	 */
	public void setClassification(Atom atom)
	{
		ChemicalComponentType type = ChemicalComponentInfo.getChemicalComponentType(atom.compound);

		if (type.isPeptide()) {
			classification = Classification.AMINO_ACID;
		} else if (type.isNucleotide() ) {
			classification = Classification.NUCLEIC_ACID;	
		} else if (type.isWater()) {
			classification = Classification.WATER;
		} else {
			classification = Classification.LIGAND;
		}
		
	}

	/**
	 * Sets the classification of this residue
	 * @param classification classification of residue
	 */
	public void setClassifiction(Classification classification) {
		this.classification = classification;
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
		Atom atom = alphaAtomIndex >= 0 ? atoms.elementAt( alphaAtomIndex ) : atoms.elementAt(atoms.size() / 2);
		return atom;
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
		return ChemicalComponentInfo.getHydrophobicityFromCode(compoundCode);
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
	 * Get the author assigned chain id (as it is assigned in the first Atom record).
	 * Return null if there are no atom records.
	 */
	public String getAuthorChainId( )
	{
		if ( atoms == null ) {
			return null;
		}
		final Atom atom = atoms.elementAt( 0 );
		if ( atom == null ) {
			return null;
		}
		return atom.authorChain_id;
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
	 * Get the author assigned residue id (as it is assigned in the first Atom record).
	 * Return -1 if there are no atom records.
	 */
	public int getAuthorResidueId( )
	{
		if ( atoms == null ) {
			return -1;
		}
		final Atom atom = atoms.elementAt( 0 );
		if ( atom == null ) {
			return -1;
		}
		return atom.authorResidue_id;
	}
	
	/**
	 * Get the insertion code (as it is assigned in the first Atom record).
	 * Return "" if there are no atom records.
	 */
	public String getInsertionCode( )
	{
		if ( atoms == null ) {
			return "";
		}
		final Atom atom = atoms.elementAt( 0 );
		if ( atom == null ) {
			return "";
		}
		return atom.insertionCode;
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
		return ExternReferences.getModifiedResidueName(this);
	}

	public void trimToSize() {
		atoms.trimToSize();
	}
}

