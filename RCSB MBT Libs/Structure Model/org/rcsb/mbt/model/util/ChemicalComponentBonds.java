//  $Id: java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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

package org.rcsb.mbt.model.util;


import java.util.*;
import java.util.zip.GZIPInputStream;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

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
	public enum BondOrder
	{
		/**
		 * Value returned by the bondType method when nothing is known
		 * about the existance of a bond between two atoms.
		 */
		UNKNOWN
		{
			{
				shortName = "UNKN";
			}
		},
		
		/**
		 * Value returned by the bondType method when it recognizes a compound
		 * and determines that there should be no bond between two atoms.
		 */
		NONE
		{
			{
				shortName = "NONE";				
			}
		},
		
		/**
		 * Value returned by the bondType method when it recognizes a compound
		 * and determines that there should be a single bond between two atoms.
		 */
		SINGLE
		{
			{
				order = 1.0f;
				shortName = "SING";
			}
		},
		
		/**
		 * Value returned by the bondType method when it recognizes a compound
		 * and determines that there should be a double bond between two atoms.
		 */
		DOUBLE
		{
			{
				order = 2.0f;
				shortName = "DOUB";
			}
		},
		
		/**
		 * Value returned by the bondType method when it recognizes a compound
		 * and determines that there should be a tripple bond between two atoms.
		 */
		TRIPLE
		{
			{
				order = 3.0f;
				shortName = "TRIP";
			}
		},
	
		/**
		 * Value returned by the bondType method when it recognizes a compound
		 * and determines that there should be an aromatic bond between two atoms.
		 */
		AROMATIC
		{
			{
				order = 1.0f;
				shortName = "AROM";
			}
		};
		
		protected float order = -1.0f;
		protected String shortName;
		
		static public BondOrder valueByShortName (String pfx)
		{
			for (BondOrder bondType : BondOrder.values())
				if (pfx.startsWith(bondType.shortName))
						return bondType;
				
			return NONE;				
		}
	}
	
	private enum ItemParts { COMPOUND_CODE, ATOM0, ATOM1, BOND_TYPE }
					// keys into the split strings.
	
	static private final String bondFileUrlPrefix = "http://www.pdb.org/pdb/files/ligand/",
								bondFileUrlSuffix = ".cif.gz";

	@SuppressWarnings("serial")
	static private class CompoundMap extends Hashtable<String, BondOrder> {}
			// "<atom0>:<atom1>" -> BondType
	
	@SuppressWarnings("serial")
	static private class BondsMap extends Hashtable<String, CompoundMap> {}
			// compound code -> CompoundMap
	
	static private BondsMap bonds = load( "ChemicalComponentBonds.dat" );
	
	static private Set<String> compoundsTriedNotFound = null;


	/**
	 *  Attempt to initialize the chemical component bond dictionary.
	 */
	static private BondsMap load( final String dictionaryFile )
	{
		BondsMap lclBonds = new BondsMap( );

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
		try
		{
			while ( (line = br.readLine()) != null )
				addBondLineToMap(line, sharedStrings, lclBonds);
		}
		catch ( final java.io.IOException e )
		{
			Status.output(Status.LEVEL_WARNING, e.getMessage());
		}

		sharedStrings = null;

		return lclBonds;
	}

	private static void addBondLineToMap(String line, SharedObjects sharedStrings, BondsMap bonds) throws IOException
	{
		// ALA N CA SING
		// items[0] = compoundName
		// items[1] = atom 1
		// items[2] = atom 2
		// items[3] = bond type
		
		final String items[] = line.split( "\t" );
		if ( (items == null) || (items.length != 4) )
			throw new IOException( "ChemicalComponentBonds: Dictionary is corrupt: ");

		//
		// Check the bond dictionary for the compound
		//
		String compoundKey = items[ItemParts.COMPOUND_CODE.ordinal()];
		CompoundMap compoundMap = (bonds.containsKey(compoundKey))? bonds.get(compoundKey) : null;

		if (compoundMap == null)
		{
			compoundMap = new CompoundMap( );
			bonds.put( sharedStrings.share(compoundKey), compoundMap );
		}

		//
		// Check the compound for the bond
		//	
		String bondKey = sharedStrings.share(items[ItemParts.ATOM0.ordinal()] + ':' + items[ItemParts.ATOM1.ordinal()]);
		if (!compoundMap.containsKey(bondKey))
			compoundMap.put( bondKey, BondOrder.valueByShortName(items[ItemParts.BOND_TYPE.ordinal()].substring(0, 4)) );
						// only the first four characters count
	}
	
	
	/**
	 *  Try to determine what type of bond might exist between the two atoms
	 *  using a dictionary of known chemical compounds.
	 *
	 *  See BOND_TYPE_* fields for possible return values.
	 *
	 *  @exception	NullPointerException	if either atom argument is null.
	 */
	public static BondOrder bondType( final Atom atom0, final Atom atom1 )
	{
		if ( bonds == null )
			return BondOrder.UNKNOWN;
		
		if (atom0 == null || atom1 == null)
			throw new NullPointerException( "Error: atom " + ((atom0 == null)? "0" : "1") + " is null in ChemicalComponentBonds.bondType." );
							// can't have a null atom - illegal call...

		if ( atom0.getStructure() != atom1.getStructure() ||
			 atom0.residue_id != atom1.residue_id ||
			 !atom0.compound.equals( atom1.compound ) ||
			 !atom0.chain_id.equals( atom1.chain_id )
			 ) {
			return BondOrder.UNKNOWN;
							// constrain atoms to same structure, chain, residue, and compound
							// TODO: I think we want cross-chain, residue, and compound bonds to be enabled, if possible
							// 30-Oct-08 - rickb
		}

		if (bonds.containsKey(atom0.compound))
		{
			final CompoundMap compoundMap = bonds.get( atom0.compound );
		
			for (int ix = 0; ix < 2; ix++)
			{
				String bondKey = (ix == 0)? atom0.name + ":" + atom1.name : atom1.name + ":" + atom0.name;
							// try natural, then reverse atom orders
				
				if (compoundMap.containsKey(bondKey))
					return compoundMap.get( bondKey );
			}

			return BondOrder.UNKNOWN;
		}

		return BondOrder.NONE;
	}


	/**
	 *  Try to determine what type of bond might exist between the two atoms
	 *  in the given Bond using a dictionary of know chemical compounds.
	 *
	 *  See BOND_TYPE_* fields for possible return values.
	 *
	 *  @exception	NullPointerException	if the bond argument is null.
	 */
	public static BondOrder bondType( final Bond bond )
	{
		if ( bond == null ) {
			throw new NullPointerException( "bond is null" );
		}
		return bondType( bond.getAtom(0), bond.getAtom(1) );
	}


	/**
	 *  Determine if the given compound code is contained in the dictionary
	 *  and return true if it is or false if it is not.
	 */
	public static boolean knownCompound( final String compoundCode )
	{
		if (compoundCode != null && bonds != null)
		{
			if (bonds.containsKey(compoundCode)) return true;
		
			else
				return tryAddBondsForCompound(compoundCode);
		}
		
		return false;
	}
	
	/**
	 * If the bond wasn't found for the compound, try to get it from the pdb site and
	 * add it.
	 */
	private static boolean tryAddBondsForCompound(String compoundCode)
	{
		boolean retval = false;
		
		if (compoundCode.length() < 3 && PeriodicTable.getElement(compoundCode) != null)
				return retval;
							// don't look up single elements
		
		try
		{
			Status.output(Status.LEVEL_REMARK, "Looking up bond information for the ligand \"" + compoundCode + "\" on pdb.org...");
			URL bondUrl = new URL(bondFileUrlPrefix + compoundCode + bondFileUrlSuffix);
			URLConnection urlConnection = bondUrl.openConnection();
			InputStream inputStream = urlConnection.getInputStream();
			GZIPInputStream zin = new GZIPInputStream(inputStream);
			ArrayList<String> bondStrings = parseCifFileForBonds(zin, null);
			SharedObjects sharedStrings = new SharedObjects( );
			for (String bondString : bondStrings)
				addBondLineToMap(bondString, sharedStrings, bonds);
			
			retval = bonds.containsKey(compoundCode);
			return retval;
		}
		
		catch (MalformedURLException e) {}
		
		catch (UnknownHostException e)
		{
			Status.output(Status.LEVEL_WARNING, "Internet connection to pdb.org failed...  calculating bonds for Ligand \"" + compoundCode + "\"");
		}
		
		catch (IOException e)
		{
			Status.output(Status.LEVEL_WARNING, "Ligand \"" + compoundCode + "\" not found... calculating bonds for Ligand ");			
		}
		
		return retval;
	}
	

	/*
	 * Reads a .cif file for bonds, condenses the bond information into a four-token line,
	 * and does one of two things with the line:
	 * 
	 *   1) if (ps == null), adds it to an array.
	 *   
	 *   2) if (ps != null), prints out to ps.
	 *   
	 * This gets called from the external process ChemicalComponentBondsCreator to create
	 * the initial dictionary (which is why it's public.)  It is also called from this class
	 * in 'tryAddBondsForCompound()'.  Input from this are typically very tiny streams,
	 * containing the bond info for a single ligand.  OTOH, when called from
	 * ChemicalComponentBondsCreator, the input stream is huge, hence the need to
	 * output as it comes in.
	 * 
	 * @arg is - input stream to parse.
	 * @arg ps - output file to write.  Null, if none.
	 * @return - ArrayList of compressed bond strings to be added to the bonds
	 *           dictionary.
	 */
	public static ArrayList<String> parseCifFileForBonds(InputStream is, PrintWriter pw) throws IOException
	{
		BufferedReader in = null;
		ArrayList<String> out = (pw == null)? new ArrayList<String>() : null;
		
		InputStreamReader isReader = new InputStreamReader(is);
		in = new BufferedReader(isReader);
		
		boolean isInChemCompBondBlock = false;
		
		String line = null;
		String outStr = null;
		int lineCount = 0, lineEvery = 20;
		
		while((line = in.readLine()) != null)
		{
			line = line.trim();
			
			if(isInChemCompBondBlock) {
				if(line.equals("#")) {
					isInChemCompBondBlock = false;
				} else {
					String dqline = line.replaceFirst("\"([A-Z0-9]+) ([A-Z0-9]+)\"", "$1=$2");						
					String[] split = dqline.split("\\s++");
					if(split == null || split.length != 7) {
						new Exception("Encountered unexpected data").printStackTrace();
					} else
					{
						for(int i = 0; i < 4; i++)
						{
							String s = split[i];
							if(s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
								split[i] = s.substring(1,s.length() - 1);
							}
						}
						if (DebugState.isDebug() && ++lineCount % lineEvery == 1)
							Status.output(Status.LEVEL_REMARK, lineCount + ": " + line);
						
						outStr = split[0] + "\t" + split[1] + "\t" + split[2] + "\t" + split[3];
						if (pw != null)
							pw.println(outStr);
						else
							out.add(outStr);
					}
				}
			}
			
			else if(line.equals("_chem_comp_bond.pdbx_ordinal"))
				isInChemCompBondBlock = true;
		}
		
		return out;
	}
}

