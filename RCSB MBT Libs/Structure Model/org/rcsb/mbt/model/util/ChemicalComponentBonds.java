//  $Id: ChemicalComponentBonds.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: ChemicalComponentBonds.java,v $
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
//  Revision 1.4  2005/11/08 20:58:33  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.3  2004/10/22 23:35:55  moreland
//  Added support for tripple bond orders.
//  Changed separator character from space to tab.
//
//  Revision 1.2  2004/08/16 16:21:36  moreland
//  Now uses SharedObjects class to reduce memory usage.
//
//  Revision 1.1  2004/04/15 20:46:54  moreland
//  Added chemical component bond dictionary.
//
//  Revision 1.0  2004/04/06 19:11:48  moreland
//


package org.rcsb.mbt.model.util;


import java.util.*;
import java.io.*;

import org.rcsb.mbt.model.*;



/**
 *  Provides a dictionary of chemical component bonds enabling the
 *  intelligent detection, creation, and culling of bonds for known
 *  molecules.
 *  
 *  WARNING: If you change the package name, you MUST change the fully qualified
 *  classname string in the load() function!!!
 *
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.util.BondFactory
 *  @see	org.rcsb.mbt.model.Atom
 *  @see	org.rcsb.mbt.model.Bond
 */
public class ChemicalComponentBonds
{
	/**
	 * Value returned by the bondType method when nothing is known
	 * about the existance of a bond between two atoms.
	 */
	public static final String BOND_TYPE_UNKNOWN  = "UNKN";

	/**
	 * Value returned by the bondType method when it recognizes a compound
	 * and determines that there should be no bond between two atoms.
	 */
	public static final String BOND_TYPE_NONE     = "NONE";

	/**
	 * Value returned by the bondType method when it recognizes a compound
	 * and determines that there should be a single bond between two atoms.
	 */
	public static final String BOND_TYPE_SINGLE   = "SING";

	/**
	 * Value returned by the bondType method when it recognizes a compound
	 * and determines that there should be a double bond between two atoms.
	 */
	public static final String BOND_TYPE_DOUBLE   = "DOUB";

	/**
	 * Value returned by the bondType method when it recognizes a compound
	 * and determines that there should be a tripple bond between two atoms.
	 */
	public static final String BOND_TYPE_TRIPPLE   = "TRIP";

	/**
	 * Value returned by the bondType method when it recognizes a compound
	 * and determines that there should be an aromatic bond between two atoms.
	 */
	public static final String BOND_TYPE_AROMATIC = "AROM";


	//
	// Maintains A hash of hashes to a string value in the form:
	// bonds{compound_code}->compound{atomName:atomName}->bondType
	//
	private static final Hashtable bonds = ChemicalComponentBonds.load( "ChemicalComponentBonds.dat" );


	/**
	 *  Attempt to initialize the chemical component bond dictionary.
	 */
	private static Hashtable load( final String dictionaryFile )
	{
		final Hashtable bonds = new Hashtable( );

		//
		// Read the bond dictionary
		//

		Class<ChemicalComponentBonds> myClass = ChemicalComponentBonds.class;
		InputStream is = null;
		try {
			is = myClass.getResource( dictionaryFile ).openStream();
			
			if ( is == null )
			{
				Status.output( Status.LEVEL_WARNING, "ChemicalComponentBonds: No dictionary: " + dictionaryFile );
				return null;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		final InputStreamReader isr = new InputStreamReader( is );
		final BufferedReader br = new BufferedReader( isr );
		SharedObjects sharedStrings = new SharedObjects( );

		String line = null;
		while ( true )
		{
			try
			{
				line = br.readLine( );
			}
			catch ( final java.io.IOException e )
			{
				e.printStackTrace( );
				break; // At least return what we have so far.
			}
			if ( line == null ) {
				break; // We're done reading!
			}

			// ALA N CA SING
			final String items[] = line.split( "\t" );
			if ( (items == null) || (items.length != 4) )
			{
				Status.output( Status.LEVEL_WARNING, "ChemicalComponentBonds: No dictionary: " + dictionaryFile );
				return null;
			}

			//
			// Check the bond dictionary for the compound
			//

			Hashtable compound = (Hashtable) bonds.get( items[0] );
			if ( compound == null )
			{
				compound = new Hashtable( );
				bonds.put( items[0], compound );
			}

			//
			// Check the compound for the bond
			//

			String bondKey = items[1] + ":" + items[2];
			bondKey = sharedStrings.share( bondKey );
			final String bondType = (String) compound.get( bondKey );
			if ( bondType == null )
			{
				if ( items[3].startsWith( ChemicalComponentBonds.BOND_TYPE_AROMATIC ) ) {
					items[3] = ChemicalComponentBonds.BOND_TYPE_AROMATIC;
				} else if ( items[3].startsWith( ChemicalComponentBonds.BOND_TYPE_SINGLE ) ) {
					items[3] = ChemicalComponentBonds.BOND_TYPE_SINGLE;
				} else if ( items[3].startsWith( ChemicalComponentBonds.BOND_TYPE_DOUBLE ) ) {
					items[3] = ChemicalComponentBonds.BOND_TYPE_DOUBLE;
				} else if ( items[3].startsWith( ChemicalComponentBonds.BOND_TYPE_TRIPPLE ) ) {
					items[3] = ChemicalComponentBonds.BOND_TYPE_TRIPPLE;
				}

				compound.put( bondKey, items[3] );
			}
		}

		sharedStrings = null;

		return bonds;
	}


	/**
	 *  Try to determine what type of bond might exist between the two atoms
	 *  using a dictionary of know chemical compounds.
	 *
	 *  See BOND_TYPE_* fields for possible return values.
	 *
	 *  @exception	NullPointerException	if either atom argument is null.
	 */
	public static String bondType( final Atom atom0, final Atom atom1 )
	{
		if ( ChemicalComponentBonds.bonds == null ) {
			return ChemicalComponentBonds.BOND_TYPE_UNKNOWN;
		}

		if ( atom0 == null ) {
			throw new NullPointerException( "atom0 is null" );
		}
		if ( atom1 == null ) {
			throw new NullPointerException( "atom1 is null" );
		}

		if ( atom0.getStructure() != atom1.getStructure() ) {
			return ChemicalComponentBonds.BOND_TYPE_UNKNOWN;
		}

		if ( atom0.residue_id != atom1.residue_id ) {
			return ChemicalComponentBonds.BOND_TYPE_UNKNOWN;
		}

		if ( ! atom0.compound.equals( atom1.compound ) ) {
			return ChemicalComponentBonds.BOND_TYPE_UNKNOWN;
		}

		if ( ! atom0.chain_id.equals( atom1.chain_id ) ) {
			return ChemicalComponentBonds.BOND_TYPE_UNKNOWN;
		}

		final Hashtable compound = (Hashtable) ChemicalComponentBonds.bonds.get( atom0.compound );
		if ( compound == null ) {
			return ChemicalComponentBonds.BOND_TYPE_UNKNOWN;
		}

		// Try natural atom order
		String bondKey = atom0.name + ":" + atom1.name;
		String bondType = (String) compound.get( bondKey );
		if ( bondType != null ) {
			return bondType;
		}

		// Try reverse atom order
		bondKey = atom1.name + ":" + atom0.name;
		bondType = (String) compound.get( bondKey );
		if ( bondType != null ) {
			return bondType;
		}

		return ChemicalComponentBonds.BOND_TYPE_NONE;
	}


	/**
	 *  Try to determine what type of bond might exist between the two atoms
	 *  in the given Bond using a dictionary of know chemical compounds.
	 *
	 *  See BOND_TYPE_* fields for possible return values.
	 *
	 *  @exception	NullPointerException	if the bond argument is null.
	 */
	public static String bondType( final Bond bond )
	{
		if ( bond == null ) {
			throw new NullPointerException( "bond is null" );
		}
		return ChemicalComponentBonds.bondType( bond.getAtom(0), bond.getAtom(1) );
	}


	/**
	 *  Determine if the given compound code is contained in the dictionary
	 *  and return true if it is or false if it is not.
	 */
	public static boolean knownCompound( final String compoundCode )
	{
		if ( compoundCode == null ) {
			return false;
		}
		if ( ChemicalComponentBonds.bonds == null ) {
			return false;
		}
		final Hashtable hash = (Hashtable) ChemicalComponentBonds.bonds.get( compoundCode );
		if ( hash == null ) {
			return false;
		} else {
			return true;
		}
	}


	// Unit testing
	public static void main( final String args[] )
	{
		final Atom atom0 = new Atom( );
		atom0.chain_id = "A";
		atom0.compound = "ALA";
		atom0.name = "CA";

		final Atom atom1 = new Atom( );
		atom1.chain_id = "A";
		atom1.compound = "ALA";
		atom1.name = "CB";

		final Atom atom2 = new Atom( );
		atom2.chain_id = "A";
		atom2.compound = "ALA";
		atom2.name = "O";

		final Atom atom3 = new Atom( );
		atom3.chain_id = "A";
		atom3.compound = "XXX";
		atom3.name = "C";

		final Atom atom4 = new Atom( );
		atom4.chain_id = "A";
		atom4.compound = "XXX";
		atom4.name = "N";

		System.err.println( "type = " + ChemicalComponentBonds.bondType( atom0, atom1 ) );
		System.err.println( "type = " + ChemicalComponentBonds.bondType( atom0, atom2 ) );
		System.err.println( "type = " + ChemicalComponentBonds.bondType( atom3, atom4 ) );
	}
}

