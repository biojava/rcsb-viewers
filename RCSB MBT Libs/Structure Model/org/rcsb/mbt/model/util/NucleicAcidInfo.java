//  $Id: NucleicAcidInfo.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: NucleicAcidInfo.java,v $
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
//  Revision 1.7  2004/04/27 18:30:57  moreland
//  Added "isNucleotide" method.
//  Clarified comments to distinguish nucleotides from nucleic acid bases.
//
//  Revision 1.6  2004/04/09 00:15:21  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.5  2004/01/29 17:29:07  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.4  2003/04/30 17:57:44  moreland
//  If compound is not one letter, null is now returned.
//
//  Revision 1.3  2003/04/03 22:43:00  moreland
//  Added a comment to suggest that the class should eventually be divided
//  into separate "NucleicAcid" and "NucleicAcids" classes.
//
//  Revision 1.2  2002/10/24 18:05:40  moreland
//  Added some missing method comments.
//
//  Revision 1.1.1.1  2002/07/16 18:00:21  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//


package org.rcsb.mbt.model.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;


/**
 *  Provides static information about Nucleic Acids & Nucleotides for DNA/RNA
 *  such as nucleotide character codes, nucleic acid base names, and base
 *  pairing rules.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Residue
 */
public class NucleicAcidInfo
{
	// SCIENTIFIC NOTE:
	//
	// Nucleic acid chains consist of nucleotides.
	// Nucleotides consists of a base, sugar, and phosphate.
	//
	// The nucleotides are:
	//
	// CompoundCode = CompoundName               = Base     + Sugar + Phosphate
	// ------------   --------------------------   ----------------------------
	// A            = Adensoine-5'-monophosphate = Adenine  + Sugar + Phosphate
	// C            = Cytidine-5'-monophosphate  = Cytosine + Sugar + Phosphate
	// G            = Guanosine-5'-monophosphate = Guanine  + Sugar + Phosphate
	// T            = Thymidine-5'-monophosphate = Thymine  + Sugar + Phosphate
	// U            = Uridine-5'-monophosphate   = Uracil   + Sugar + Phosphate
	//
	
	static private final String setFileName = "NucleicAcidCompoundNames.dat";

	/**
	 *  Letter codes of the 4 nitrogenous bases for DNA.
	 */
	public static final String dna_bases[] = { "A", "C", "G", "T" };

	/**
	 *  Letter codes of the 4 nitrogenous bases for RNA.
	 */
	public static final String rna_bases[] = { "A", "C", "G", "U" };
	
	/**
	 * Key: String all compound names that are nucleic acids. Value: null.
	 */
	public static HashSet<String> allNames = null;

	/**
	 *  A 2D storage array for nucleotide character (chemical compound) codes,
	 *  and full nucleic acid base names.
	 *  <P>
	 *  This class should probably be broken into two classes:
	 *  "Nucleotide" to hold properties and and "Nucleotides" as
	 *  a container class.
	 *  http://bama.ua.edu/~hsmithso/class/bsc_495/macromolecules/macromolecule.html
	 *  <P>
	 *  names[][0] is the Nucleotide chemical compound code.
	 *  names[][1] is the Nucleic acid base name.
	 */
	private static final String names[][] =
	{
		{ "A", "Adenine" },   // A = ADE or ANE NA base + sugar + phosphate
		{ "B", "UNKNOWN" },
		{ "C", "Cytosine" },  // C = CYT? NA base + sugar + phosphate
		{ "D", "UNKNOWN" },
		{ "E", "UNKNOWN" },
		{ "F", "UNKNOWN" },
		{ "G", "Guanine" },   // G = GUN NA base + sugar + phosphate
		{ "H", "UNKNOWN" },
		{ "I", "UNKNOWN" },
		{ "J", "UNKNOWN" },
		{ "K", "UNKNOWN" },
		{ "L", "UNKNOWN" },
		{ "M", "UNKNOWN" },
		{ "N", "UNKNOWN" },
		{ "O", "UNKNOWN" },
		{ "P", "UNKNOWN" },
		{ "Q", "UNKNOWN" },
		{ "R", "UNKNOWN" },
		{ "S", "UNKNOWN" },
		{ "T", "Thymine" },   // T = TDR NA base + sugar + phosphate
		{ "U", "Uracil" },    // U = URA NA base + sugar + phosphate
		{ "V", "UNKNOWN" },
		{ "W", "UNKNOWN" },
		{ "X", "UNKNOWN" },
		{ "Y", "UNKNOWN" },
		{ "Z", "UNKNOWN" }
	};

	private static final int A_INDEX = 0;
	private static final int C_INDEX = 2;
	private static final int G_INDEX = 6;
	private static final int T_INDEX = 19;
	private static final int U_INDEX = 20;


	/**
	 *  Returns the full nucleic acid base name for the given letter
	 *  (chemical compound) code.
	 *  <P>
	 */
	public static boolean isNucleotide( String compoundCode )
	{
		if ( compoundCode == null ) {
			return false;
		}
		if(allNames == null)
		{
			BufferedReader br = null;
			allNames = new HashSet<String>();
			Class<ChemicalComponentBonds> myClass = ChemicalComponentBonds.class;
			InputStream is = null;
			try
			{
				is = myClass.getResource( setFileName ).openStream();
				
				if ( is == null )
				{
					Status.output( Status.LEVEL_WARNING, "NucleicAcidInfo: No lookup set: " + setFileName );
					return false;
				}
				
				InputStreamReader isr = new InputStreamReader( is );
				br = new BufferedReader( isr );
				String line;
				while ((line = br.readLine()) != null)
					allNames.add(line);
			}
			
			catch (IOException e1)
			{
				if (DebugState.isDebug())
					e1.printStackTrace();
				return false;
			}
			
			finally
			{
				try
				{
					if (br != null)
						br.close();
				}
				
				catch (IOException e) {}
			}
		}
		
		return allNames.contains(compoundCode);
	}


	/**
	 *  Returns the number of Nucleotide/Nucleic-Acid-base-name tuples that
	 *  are in the database.
	 *  <P>
	 */
	public static int getBaseCount( )
	{
		return 5; // There are only 5 Nucleotide/Nucleic-Acid-base tuples.
	}

	/**
	 *  Returns the nucleic acid base name for the given letter
	 *  (nucleotide chemical compound code).
	 *  
	 *  <P>
	 */
	public static String getNameFromLetter( String letter )
	{
		// The "UNKNOWN" names are pad entries to enable direct indexing
		// by character/byte value into the array.
		if ( letter == null ) {
			return null;
		}
		if(letter.equals("DA") || letter.equals("DC") || letter.equals("DT") || letter.equals("DG") || letter.equals("DU")) {
			letter = letter.substring(1);
		}
		if ( letter.length() != 1 ) {
			return null;
		}
		final int offset = letter.charAt(0) - 'A';
		if ( offset < 0 ) {
			return null;
		}
		if ( offset > NucleicAcidInfo.names.length ) {
			return null;
		}
		return NucleicAcidInfo.names[offset][1];
	}

	/**
	 *  Returns the full nucleic acid base name for the given character
	 *  (chemical compound) code.
	 *  <P>
	 *  Note: does not respect mutated nucleic acids
	 */
	public static String getLetterFromName( final String name )
	{
		if ( NucleicAcidInfo.names[NucleicAcidInfo.A_INDEX][1].equals( name ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.A_INDEX][0];
		}
		if ( NucleicAcidInfo.names[NucleicAcidInfo.C_INDEX][1].equals( name ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.C_INDEX][0];
		}
		if ( NucleicAcidInfo.names[NucleicAcidInfo.G_INDEX][1].equals( name ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.G_INDEX][0];
		}
		if ( NucleicAcidInfo.names[NucleicAcidInfo.T_INDEX][1].equals( name ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.T_INDEX][0];
		}
		if ( NucleicAcidInfo.names[NucleicAcidInfo.U_INDEX][1].equals( name ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.U_INDEX][0];
		}
		return null;
	}

	/**
	 *  Returns the base pair pairing for DNA.
	 *  <P>
	 */
	public static String getDnaPairing( final String letter )
	{
		if ( letter.equals( NucleicAcidInfo.names[NucleicAcidInfo.A_INDEX][0] ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.T_INDEX][0];
		}
		if ( letter.equals( NucleicAcidInfo.names[NucleicAcidInfo.T_INDEX][0] ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.A_INDEX][0];
		}
		if ( letter.equals( NucleicAcidInfo.names[NucleicAcidInfo.G_INDEX][0] ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.C_INDEX][0];
		}
		if ( letter.equals( NucleicAcidInfo.names[NucleicAcidInfo.C_INDEX][0] ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.G_INDEX][0];
		}
		return null;  // UNKNOWN
	}

	/**
	 *  Returns the base pair pairing for RNA-DNA interaction.
	 *  <P>
	 */
	public static String getRnaDnaPairing( final String letter )
	{
		if ( letter.equals( NucleicAcidInfo.names[NucleicAcidInfo.U_INDEX][0] ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.A_INDEX][0];
		}
		if ( letter.equals( NucleicAcidInfo.names[NucleicAcidInfo.A_INDEX][0] ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.T_INDEX][0];
		}
		if ( letter.equals( NucleicAcidInfo.names[NucleicAcidInfo.G_INDEX][0] ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.C_INDEX][0];
		}
		if ( letter.equals( NucleicAcidInfo.names[NucleicAcidInfo.C_INDEX][0] ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.G_INDEX][0];
		}
		return null;  // UNKNOWN
	}

	/**
	 *  Returns the base pair pairing for RNA-RNA interaction.
	 *  <P>
	 */
	public static String getRnaRnaPairing( final String letter )
	{
		if ( letter.equals( NucleicAcidInfo.names[NucleicAcidInfo.A_INDEX][0] ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.U_INDEX][0];
		}
		if ( letter.equals( NucleicAcidInfo.names[NucleicAcidInfo.U_INDEX][0] ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.A_INDEX][0];
		}
		if ( letter.equals( NucleicAcidInfo.names[NucleicAcidInfo.G_INDEX][0] ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.C_INDEX][0];
		}
		if ( letter.equals( NucleicAcidInfo.names[NucleicAcidInfo.C_INDEX][0] ) ) {
			return NucleicAcidInfo.names[NucleicAcidInfo.G_INDEX][0];
		}
		return null;  // UNKNOWN
	}
}
