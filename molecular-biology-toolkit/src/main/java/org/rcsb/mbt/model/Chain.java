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


// MBT

// Core
import java.util.*;

import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.util.*;


/**
 *  Implements a StructureComponent container for chain information.
 *  This may be an amino acid chain (eg: protein), a nucleic acid
 *  chain (eg: RNA/DNA), or a ligand chain.
 *  This class contains a list of Residue records.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Residue
 *  @see	org.rcsb.mbt.model.StructureComponent
 *  @see	org.rcsb.mbt.model.Structure
 *  @see	org.rcsb.mbt.model.StructureMap
 */
public class Chain
	extends StructureComponent
	implements java.lang.Cloneable
{
	// The Residue records for the chain.
	protected Vector<Residue> residues = null;
	private String entityName = new String("");
	public Vector<Residue> getResidues() { return residues; }
	// The residue-to-index hash for the chain.
	
	private Residue.Classification chainClassification = null;
	
	
	protected Hashtable<Residue,Integer> residueToIndexHash = null;

	// The default chain id.
	protected static final String defaultChainId = "-";

	/** 
	 *  Secondary structure conformation fragments.
	 *  This fragment map can be assigned in one of two ways:
	 *  <P>
	 *  1) From the Structure's conformation record assignments
	 *     (asigned to each Residue) by calling the resetFragments method.<BR>
	 *  OR<BR>
	 *  2) By explictly assigning fragment ranges by calling
	 *     the setFragment method to configure ranges.
	 *  <P>
	 */ 
	private RangeMap fragmentRanges = null;
	private Vector<Fragment> fragmentObjects = null;
	
	public ArrayList<Residue> modifiedResidues = null;
	public void addModifiedResidue(Residue residue)
	{
		if (modifiedResidues == null)
			modifiedResidues = new ArrayList<Residue>();
		
		if (!modifiedResidues.contains(residue))
			modifiedResidues.add(residue);
	}
	
	public boolean hasModifiedResidues() { return modifiedResidues != null && modifiedResidues.size() > 0; }
	
	public ArrayList<Residue> getModifiedResidues() { return modifiedResidues; }


	//
	// Constructors
	//

	/**
	 * Construct a Chain.
	 */
	public Chain( )
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
		this.setStructure( structureComponent.getStructure() );
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
		if ( Chain.className == null ) {
			Chain.className = Chain.class.getName();
		}
		return Chain.className;
	}

	/**
	 *  This method returns the fully qualified name of this class.
	 */
	@Override
	public ComponentType getStructureComponentType( )
	{
		return ComponentType.CHAIN;
	}

	//
	// Chain methods.
	//

	/**
	 * Return the chain classification by tallying up the number of residues of
	 * each classification and returning the biggest number.
	 */
	/*
	 * Seems odd, to me - a chain should have residues of only one type, yes?
	 * Anyway, I've optimized this so it only goes through the loop once.  If chain
	 * is mutable, add a dirty flag.
	 * 
	 * 10-22-08	- rickb
	 */
	public Residue.Classification getClassification( )
	{
		if ( residues == null || residues.size() == 0)
			return null;
		
		// single residues in a chain are classified as ligands.
		if (getResidueCount() == 1) {
			if (residues.get(0).getClassification() == Residue.Classification.WATER) {
				return Residue.Classification.WATER;
			} else {
				if (residues.get(0).getClassification() != Residue.Classification.BIRD)
					return Residue.Classification.LIGAND;
			}
		}
		
		if (chainClassification == null)
		{	
			// Classify the chain based upon how many residues of each type exist.
			int classTallies[] = new int[Residue.Classification.values().length];
			for ( Residue residue : residues)
				classTallies[residue.getClassification().ordinal()]++;
	
			// See which classification type had the highest count.
			int highestCount = -1;
			int highestIndex = -1;
			for ( Residue.Classification residue : Residue.Classification.values())
				if ( classTallies[residue.ordinal()] > highestCount )
				{
					highestCount = classTallies[residue.ordinal()];
					highestIndex = residue.ordinal();
				}
	
			if ( highestIndex < 0 ) {
				return null;
			}
			
			/* **/
			if (DebugState.isDebug())
			{
				int numTallies = 0;
				for (int tally : classTallies)
					if (tally > 0) numTallies++;
				assert (numTallies == 1);
									// check to see if this ever happens...
			/* **/
			}
	
			chainClassification = Residue.Classification.values()[highestIndex];
		}
		
		return chainClassification;
	}
	
	/**
	 * This will reclassify all of the residues as being part of a ligand,
	 * which will, in turn, reclassify the chain as a ligand.
	 */
	public void reClassifyAsLigand()
	{
		// classify chains with one residue as a ligand
		if (residues.size() == 1) {
			residues.get(0).reClassifyAsLigand();
		} else {
			for (Residue residue : residues) {
				if (residue.getClassification() == Residue.Classification.LIGAND || residue.getClassification() == Residue.Classification.BIRD) {
					return;
					// shortcut - we're presuming if one is correct,
					// they're all correct.
				} else {
					residue.reClassifyAsLigand();
				}
			}
		}
	}

	/**
	 * Return the entity ID by asking the first Atom of the first Residue.
	 * If there are no residues or atoms, 0 is returned.
	 */
	public int getEntityId( )
	{
		if ( this.residues == null ) {
			return 0;
		}
		final Residue residue = this.getResidue( 0 );
		if ( residue == null ) {
			return 0;
		}
		final int atomCount = residue.getAtomCount( );
		if ( atomCount <= 0 ) {
			return 0;
		}
		final Atom atom = residue.getAtom( 0 );
		if ( atom == null ) {
			return 0;
		}
		return atom.entity_id;
	}
	
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	protected String getEntityName() {
		return entityName;
	}

	/**
	 * Return the chain ID by asking the first Atom of the first Residue.
	 * If there are no residues or atoms, "A" is returned.
	 */
	public String getChainId( )
	{
		if ( this.residues == null ) {
			return Chain.defaultChainId;
		}
		final Residue residue = this.getResidue( 0 );
		if ( residue == null ) {
			return Chain.defaultChainId;
		}
		final int atomCount = residue.getAtomCount( );
		if ( atomCount <= 0 ) {
			return Chain.defaultChainId;
		}
		final Atom atom = residue.getAtom( 0 );
		if ( atom == null ) {
			return Chain.defaultChainId;
		}
		return atom.chain_id;
	}
	
	/**
	 * Return the author assigned chain ID by asking the first Atom of the first Residue.
	 * If there are no residues or atoms, "A" is returned.
	 */
	public String getAuthorChainId( )
	{
		if ( this.residues == null ) {
			return Chain.defaultChainId;
		}
		final Residue residue = this.getResidue( 0 );
		if ( residue == null ) {
			return Chain.defaultChainId;
		}
		final int atomCount = residue.getAtomCount( );
		if ( atomCount <= 0 ) {
			return Chain.defaultChainId;
		}
		final Atom atom = residue.getAtom( 0 );
		if ( atom == null ) {
			return Chain.defaultChainId;
		}
		return atom.authorChain_id;
	}

	/**
	 * Get the number of Atoms contained in the Residues of this Chain.
	 */
	public int getAtomCount( )
	{
		int atomCount = 0;
		final int residueCount = this.getResidueCount( );
		for ( int r=0; r<residueCount; r++ )
		{
			final Residue residue = this.residues.elementAt( r );
			if ( residue != null ) {
				atomCount += residue.getAtomCount( );
			}
		}
		return atomCount;
	}
	
	/**
	 * Get all the atoms from all the residues in the chain.
	 * 
	 * @return
	 */
	public Vector<Atom> getAtoms()
	{
		Vector<Atom> atomVec = new Vector<Atom>();
		for (Residue residue : getResidues())
			atomVec.addAll(residue.getAtoms());
		return atomVec;
	}

	/**
	 * Return the number of Residue records contained in this Chain.
	 */
	public int getResidueCount( )
	{
		if ( this.residues == null ) {
			return 0;
		} else {
			return this.residues.size();
		}
	}

	/**
	 * Return the specified Residue record contained in this Chain.
	 */
	public Residue getResidue( final int index )
	{
		if ( this.residues == null ) {
			throw new IndexOutOfBoundsException( "chain has no residues" );
		} else {
			return this.residues.elementAt( index );
		}
	}

	/**
	 * Return the specified Residue index in this Chain.
	 */
	public int getResidueIndex( final Residue residue )
	{
		if ( this.residues == null ) {
			throw new IndexOutOfBoundsException( "chain has no residues" );
		}

		final Integer integer = this.residueToIndexHash.get( residue );
		if ( integer == null ) {
			return -1;
		}
		return integer.intValue( );
	}

	/**
	 * Add a Residue record to this Chain.
	 */
	public void addResidue( final Residue residue )
	{
		if ( residue == null ) {
			throw new IllegalArgumentException( "null residue" );
		}

//		if ( false /* TODO: CAN'T ENFORCE MISMATCH residues != null */ ) 
//		{
//			if ( residue.getClassification() != this.getClassification() )
//			{
//				/*
//				System.err.println( "residue = { " +
//					"compoundCode=" + residue.getCompoundCode() + ", " + 
//					"atomCount=" + residue.getAtomCount() + ", " + 
//					"alphaAtomIndex=" + residue.getAlphaAtomIndex() + ", " + 
//					"conformationType=" + residue.getConformationType() +
//					" }"
//				);
//				*/
//				throw new IllegalArgumentException(
//					"classification mismatch: " + residue.getClassification() +
//					" != " + this.getClassification()
//				);
//			}
//		}

		if ( this.residues == null )
		{
			this.residues = new Vector<Residue>( );
			this.residueToIndexHash = new Hashtable<Residue,Integer>( );
		}

		this.residueToIndexHash.put( residue, new Integer( this.residues.size() ) );
		this.residues.add( residue );

		// Update the fragments map.
		if ( this.fragmentRanges == null )
		{
			this.fragmentRanges = new RangeMap( 0, 0, ComponentType.UNDEFINED_CONFORMATION );
			this.fragmentRanges.setCollapseOn( false );
			// Don't append the first value othewise we'll have an invalid rangeMax.
			this.fragmentRanges.setValue( 0, residue.getConformationType() );
		}
		else
		{
			this.fragmentRanges.append( residue.getConformationType() );
		}
		this.fragmentObjects = null;  // Invalidate the old fragmentObjects.

		// TODO: Should we fire a "residue" StructureComponentEvent here?
	}

	/**
	 * Remove all Residue records from this Chain.
	 */
	public void removeAllResidues( )
	{
		if ( this.residues == null ) {
			return;
		}

		this.residues.removeAllElements( );
		this.residues = null;
		this.residueToIndexHash = null;

		// Toss the fragments map.
		this.fragmentRanges = null;
		this.fragmentObjects = null;  // Invalidate the old fragmentObjects.

		// TODO: Should we fire a StructureComponentEvent here?
	}

	/**
	 * Remove the specified Residue record from this Chain.
	 */
	public void removeResidue( final int index )
	{
		if ( this.residues == null ) {
			throw new IndexOutOfBoundsException( "chain has no residues" );
		}
		final Residue residue = this.residues.elementAt( index );
		this.residueToIndexHash.remove( residue );
		this.residues.remove( index );

		// Remove the corresponding fragment entry.
		this.fragmentRanges.remove( index );
		if ( this.getResidueCount() == 0 ) {
			this.fragmentRanges = null;
		}
		this.fragmentObjects = null;  // Invalidate the old fragmentObjects.

		// TODO: Should we fire a StructureComponentEvent here?
	}


	/**
	 * Returns true if there is a residue ID gap between the specified
	 * chain residue index and the next residue in the chain.
	 */
	public boolean hasGapAfter( final int chainResidueIndex )
	{
		if ( this.residues == null ) {
			throw new IndexOutOfBoundsException( "chain has no residues" );
		}
		final Residue residue1 = this.residues.elementAt( chainResidueIndex );
		if ( chainResidueIndex >= (this.getResidueCount()-1) ) {
			return false;
		}
		final Residue residue2 = this.residues.elementAt( chainResidueIndex+1 );

		final int idDiff = residue2.getResidueId() - residue1.getResidueId();

		if ( idDiff == 1 ) {
			return false;
		} else {
			return true;
		}
	}


	//
	// Secondary structure conformation fragments.
	//


	/** 
	 *  Set a range of residues in this chain's fragment map to the specified
	 *  Conformation type.
	 *  <P>
	 */ 
	public void setFragmentRange( final int startResidue, final int endResidue, final ComponentType type )
	{
		if ( this.residues == null ) {
			throw new IndexOutOfBoundsException( "chain has no residues" );
		}

		if ( startResidue < 0 ) {
			throw new IndexOutOfBoundsException( "start index < 0" );
		}

		if ( endResidue >= this.getResidueCount( ) ) {
			throw new IndexOutOfBoundsException( "end index >= residue count" );
		}

		if ( startResidue > endResidue ) {
//			throw new IllegalArgumentException( "start > end (" + startResidue	//**JB this was the original code. It was correct in that this condition should not occur. The code below is a quick fix to make 2CJK work. A real fix should be found. 
//			+ ">" + endResidue + ")" );
			System.err.println("Error (ignoring fragment): start > end (" + startResidue + ">" + endResidue + ")" );
			return;
		}

		this.fragmentObjects = null;  // Invalidate the old fragmentObjects.
		this.fragmentRanges.setRange( startResidue, endResidue, type );

		// Make sure that we set each underlying residue's conformation
		// assignment to match the new fragment assignment!
		for ( int r=startResidue; r<=endResidue; r++ )
		{
			final Residue residue = this.getResidue( r );
			residue.setConformationType( type );
		}

		// TODO: Should we fire a StructureComponentEvent here?
	}

	/** 
	 *  Get the number of fragments (residue ranges) currently assigned to this chain.
	 *  <P>
	 */ 
	public int getFragmentCount( )
	{
		if ( this.residues == null ) {
			return 0;
		}
		if ( this.fragmentRanges == null ) {
			return 0;
		}
		final int fc = this.fragmentRanges.getRangeCount( );
		if ( fc <= 0 ) {
			return 1;
		} else {
			return fc;
		}
	}

	/** 
	 *  Get the conformation type currently assigned to the given fragment.
	 *  <P>
	 */ 
	public ComponentType getFragmentType( final int fragmentIndex )
	{
		if ( this.fragmentRanges == null ) {
			throw new IndexOutOfBoundsException( "chain has no fragments" );
		}
		return (ComponentType)fragmentRanges.getRangeValue( fragmentIndex );
	}

	/** 
	 *  Get the start residue index for the given fragment.
	 *  <P>
	 */ 
	public int getFragmentStartResidue( final int fragmentIndex )
	{
		if ( this.fragmentRanges == null ) {
			throw new IndexOutOfBoundsException( "chain has no fragments" );
		}
		return this.fragmentRanges.getRangeStart( fragmentIndex );
	}

	/** 
	 *  Get the end residue index for the given fragment.
	 *  <P>
	 */ 
	public int getFragmentEndResidue( final int fragmentIndex )
	{
		if ( this.fragmentRanges == null ) {
			throw new IndexOutOfBoundsException( "chain has no fragments" );
		}
		return this.fragmentRanges.getRangeEnd( fragmentIndex );
	}

	/**
	 *  Regenerate the vector of Fragment objects from the fragments RangeMap.
	 *  <P>
	 */ 
	public void generateFragments( )
	{
		if ( this.fragmentRanges == null ) {
			return;
		}

		//
		// Clear the current fragmentObjects list.
		// Any existing Fragment object for this Chain are now invalid!
		//

		// TODO: Should we fire a "remove" StructureComponentEvent here?

		if ( this.fragmentObjects != null ) {
			this.fragmentObjects.clear( );
		}
		final int fragmentCount = this.getFragmentCount( );
		this.fragmentObjects = new Vector<Fragment>( fragmentCount );

		for ( int i=0; i<fragmentCount; i++ )
		{
			final ComponentType conformationType = (ComponentType)this.fragmentRanges.getRangeValue( i );
			final int[] range = this.fragmentRanges.getRange( i );

			final Fragment fragment =
				new Fragment( range[0], range[1], conformationType );
			fragment.setChain( this );
			fragment.setStructure( this.getStructure() );
			// Set the parent fragment for each residue in the range!
			for ( int r=range[0]; r<=range[1]; r++ )
			{
				final Residue residue = this.getResidue( r );
				residue.setFragment( fragment );
			}

			this.fragmentObjects.add( fragment );
		}

		// TODO: Should we fire a "replaced" StructureComponentEvent here?
	}

	/** 
	 *  Get the specified Fragment object.
	 *  <P>
	 */ 
	public Fragment getFragment( final int fragmentIndex )
	{
		if ( this.fragmentObjects == null ) {
			this.generateFragments( );
		}
		return this.fragmentObjects.elementAt( fragmentIndex );
	}

	/** 
	 *  Get the fragments for this chain.
	 *  <P>
	 */ 
	public Vector<Fragment> getFragments( )
	{
		final Vector<Fragment> frags = new Vector<Fragment>( );
		final int count = this.getFragmentCount( );
		for ( int i=0; i<count; i++ ) {
			frags.add( this.getFragment( i ) );
		}
		return frags;
	}

	/**
	 * Returns information about Biologically Interesting Molecule Reference Dictionary (BIRD)
	 * @return
	 */
	public Bird getBird() {
		if (residues.size() > 0) {
			return getResidue(0).getBird();
		}
		return null;
	}
	
	@Override
	public String toString()
	{
		Bird bird = getResidue(0).getBird();
		if (bird != null) {
//			return bird.getPrdId() + " " + entityName;
			return bird.getPrdId() + " " + bird.getName();
		} else {
			return getAuthorChainId();
		}
	}
	
	public void trimToSize() {
		residues.trimToSize();
	}
}

