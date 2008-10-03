//  $Id: Chain.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Chain.java,v $
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
//  Revision 1.2  2006/07/18 21:06:38  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.29  2005/11/08 20:58:12  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.28  2005/06/21 20:45:40  moreland
//  Added hasGapAfter method that returns true when a residue ID gap is detected.
//
//  Revision 1.27  2005/06/21 19:45:34  moreland
//  Fragment count now correctly returns 1 (default) when getRangeCount returns 0.
//
//  Revision 1.26  2004/04/09 00:17:00  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.25  2004/01/31 19:11:48  moreland
//  Removed outdated programming note.
//
//  Revision 1.24  2004/01/29 17:08:15  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.23  2004/01/16 23:05:14  moreland
//  Added exception handling for (startResidue > endResidue) condition.
//
//  Revision 1.22  2004/01/15 00:47:02  moreland
//  Added more bounds checking and exception handling in the setFragment method.
//
//  Revision 1.21  2003/12/20 00:59:21  moreland
//  The generateFragments method now sets the parent Fragment for each Residue.
//
//  Revision 1.20  2003/12/09 21:17:50  moreland
//  Commented out debug print statements.
//
//  Revision 1.19  2003/11/20 21:31:40  moreland
//  Added setChain, getChain, getResidueCount and getResidue methods to Fragment.
//  Added call to fragment.setChain method to Chain class.
//  Both changes above enable the Fragment class to access parent and child objects.
//
//  Revision 1.18  2003/10/06 23:12:44  moreland
//  Cleaned up code to generate Fragments in StructureMap so that Fragments are set
//  as complete ranges (instead of individual residues - which didn't work well).
//
//  Revision 1.17  2003/10/03 23:59:16  moreland
//  Added support for setCollapseOn flag to maintain fragment integrity.
//
//  Revision 1.16  2003/10/01 21:16:15  agramada
//  Changes made by John M in the generating of fragments when derived from
//  data.
//
//  Revision 1.15  2003/09/19 22:27:13  moreland
//  Added getResidueIndex method by using the residueToIndexHash Hashtable.
//
//  Revision 1.14  2003/09/16 17:18:22  moreland
//  Added code to enable secondary structure generation from data VS derivation.
//
//  Revision 1.13  2003/09/11 19:40:42  moreland
//  Set "structure" field when a new Fragment object is created.
//
//  Revision 1.12  2003/07/21 20:48:09  moreland
//  Added getFragment method which returns a Fragment object.
//
//  Revision 1.11  2003/06/24 23:32:14  moreland
//  Added null alphaAtom checks to resetFragments method.
//
//  Revision 1.10  2003/04/30 17:48:52  moreland
//  Changed default chain id from "A" to "-" (bourne).
//
//  Revision 1.9  2003/04/28 21:56:34  moreland
//  If the fragmentType is a real conformation type (Helix, Turn, Strand) and the
//  fragment only covers one residue, then replace the fragment type with Coil.
//
//  Revision 1.8  2003/04/25 17:28:29  moreland
//  Fragment methods check "fragments" field rather than "residues" field for null-ness.
//
//  Revision 1.7  2003/04/25 00:56:09  moreland
//  Added code to resetFragments method to fill gaps after conformations are applied.
//
//  Revision 1.6  2003/04/24 21:07:02  moreland
//  Added gap/Coil/fragment filling code to resetFragments method.
//
//  Revision 1.5  2003/04/24 17:56:22  moreland
//  Corrected private VS public declaration in fragment methods.
//
//  Revision 1.4  2003/04/24 17:52:47  moreland
//  Corrected spelling error of Fragment routines.
//
//  Revision 1.3  2003/04/24 17:11:54  moreland
//  Added secondary structure conformation fragment support.
//
//  Revision 1.2  2003/04/23 17:20:10  moreland
//  Removed StructureComponentID/scid and replaced it with a "structure" field
//  in the StructureComponent base class.
//  Changed "getType" method to "getStructureComponentType" to return dynamic
//  SC type (ie: class name).
//  Enabled adding/removing of Residue objects.
//
//  Revision 1.1  2003/04/03 23:08:40  moreland
//  First version.
//
//  Revision 1.0  2002/10/24 17:54:01  moreland
//  First implementation.
//


package org.rcsb.mbt.model;


// MBT

// Core
import java.util.*;

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
	private Vector<Residue> residues = null;
	public Vector<Residue> getResidues() { return residues; }
	// The residue-to-index hash for the chain.
	
	
	private Hashtable residueToIndexHash = null;

	// The default chain id.
	private static final String defaultChainId = "-";

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
	private RangeMap fragments = null;
	private Vector fragmentObjects = null;


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
	public void copy( final StructureComponent structureComponent )
	{
		this.setStructure( structureComponent.getStructure() );

		// xxx = chain.xxx;
	}

	/**
	 *  Clone this object.
	 */
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
	public String getStructureComponentType( )
	{
		return Chain.className;
	}

	//
	// Chain methods.
	//

	/**
	 * Return the chain classification by asking the first residue.
	 * (eg: amino acid, nucleic acid, ligand). If there are no residues
	 * in this chain, this method will return null.
	 */
	public Residue.Classification getClassification( )
	{
		if ( residues == null || residues.size() == 0)
			return null;
		
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
		
		if (residues.size() < 24 && Residue.Classification.values()[highestIndex] !=
								  Residue.Classification.LIGAND)
		{
			reClassifyAsLigand();
// XXX			return Residue.Classification.LIGAND;
		}

		return Residue.Classification.values()[highestIndex];
	}
	
	/**
	 * This will reclassify all of the residues as being part of a ligand,
	 * which will, in turn, reclassify the chain as a ligand.
	 */
	public void reClassifyAsLigand()
	{
/* ** XXX temporarily turned off.  See above XXX, too.
 * 
		for (Residue residue : residues)
			if (residue.getClassification() == Residue.Classification.LIGAND)
				return;
							// shortcut - we're presuming if one is correct,
							// they're all correct.
			else
				residue.reClassifyAsLigand();
* **/
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
	 * Get the number of Atoms contained in the Residues of this Chain.
	 */
	public int getAtomCount( )
	{
		int atomCount = 0;
		final int residueCount = this.getResidueCount( );
		for ( int r=0; r<residueCount; r++ )
		{
			final Residue residue = (Residue) this.residues.elementAt( r );
			if ( residue != null ) {
				atomCount += residue.getAtomCount( );
			}
		}
		return atomCount;
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
			return (Residue) this.residues.elementAt( index );
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

		final Integer integer = (Integer) this.residueToIndexHash.get( residue );
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

		if ( false /* TODO: CAN'T ENFORCE MISMATCH residues != null */ ) 
		{
			if ( residue.getClassification() != this.getClassification() )
			{
				/*
				System.err.println( "residue = { " +
					"compoundCode=" + residue.getCompoundCode() + ", " + 
					"atomCount=" + residue.getAtomCount() + ", " + 
					"alphaAtomIndex=" + residue.getAlphaAtomIndex() + ", " + 
					"conformationType=" + residue.getConformationType() +
					" }"
				);
				*/
				throw new IllegalArgumentException(
					"classification mismatch: " + residue.getClassification() +
					" != " + this.getClassification()
				);
			}
		}

		if ( this.residues == null )
		{
			this.residues = new Vector<Residue>( );
			this.residueToIndexHash = new Hashtable( );
		}

		// Do a binary search to determine where this new residue should be added.
	/*
		int low = 0;
		int high = residues.size() - 1;
		int mid = 0;
		int residueId = residue.getResidueId( );
		while ( low <= high )
		{
			mid = (low + high) / 2;
			Residue residue2 = (Residue) residues.elementAt( mid );
			int residueId2 = residue2.getResidueId( );

			if ( residue == residue2 )
				throw new IllegalArgumentException( "residue alreay exists!" );
			else if ( residueId < residueId2 )
				high = mid - 1;
			else // ( residueId > residueId2 )
				low = mid + 1;
		}
		if ( mid < 0 ) mid = 0;
		residues.add( mid, residue );
	*/
		this.residueToIndexHash.put( residue, new Integer( this.residues.size() ) );
		this.residues.add( residue );

		// Update the fragments map.
		if ( this.fragments == null )
		{
			this.fragments = new RangeMap( 0, 0, Conformation.TYPE_UNDEFINED );
			this.fragments.setCollapseOn( false );
			// Don't append the first value othewise we'll have an invalid rangeMax.
			this.fragments.setValue( 0, residue.getConformationType() );
		}
		else
		{
			this.fragments.append( residue.getConformationType() );
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
		this.fragments = null;
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
		final Residue residue = (Residue) this.residues.elementAt( index );
		this.residueToIndexHash.remove( residue );
		this.residues.remove( index );

		// Remove the corresponding fragment entry.
		this.fragments.remove( index );
		if ( this.getResidueCount() == 0 ) {
			this.fragments = null;
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
		final Residue residue1 = (Residue) this.residues.elementAt( chainResidueIndex );
		if ( chainResidueIndex >= (this.getResidueCount()-1) ) {
			return false;
		}
		final Residue residue2 = (Residue) this.residues.elementAt( chainResidueIndex+1 );

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
	public void setFragment( final int startResidue, final int endResidue, final String type )
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
		this.fragments.setRange( startResidue, endResidue, type );

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
		if ( this.fragments == null ) {
			return 0;
		}
		final int fc = this.fragments.getRangeCount( );
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
	public String getFragmentType( final int fragmentIndex )
	{
		if ( this.fragments == null ) {
			throw new IndexOutOfBoundsException( "chain has no fragments" );
		}
		return (String) this.fragments.getRangeValue( fragmentIndex );
	}

	/** 
	 *  Get the start residue index for the given fragment.
	 *  <P>
	 */ 
	public int getFragmentStartResidue( final int fragmentIndex )
	{
		if ( this.fragments == null ) {
			throw new IndexOutOfBoundsException( "chain has no fragments" );
		}
		return this.fragments.getRangeStart( fragmentIndex );
	}

	/** 
	 *  Get the end residue index for the given fragment.
	 *  <P>
	 */ 
	public int getFragmentEndResidue( final int fragmentIndex )
	{
		if ( this.fragments == null ) {
			throw new IndexOutOfBoundsException( "chain has no fragments" );
		}
		return this.fragments.getRangeEnd( fragmentIndex );
	}

	/**
	 *  Regenerate the vector of Fragment objects from the fragments RangeMap.
	 *  <P>
	 */ 
	public void generateFragments( )
	{
		if ( this.fragments == null ) {
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
		this.fragmentObjects = new Vector( fragmentCount );

		for ( int i=0; i<fragmentCount; i++ )
		{
			final String conformationType = (String) this.fragments.getRangeValue( i );
			final int[] range = this.fragments.getRange( i );

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
		return (Fragment) this.fragmentObjects.elementAt( fragmentIndex );
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
}

