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

import java.util.HashMap;


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
	public static HashMap allNames = null;

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
		if(allNames == null) {
			allNames = new HashMap();
			allNames.put("125", null);
			allNames.put("126", null);
			allNames.put("127", null);
			allNames.put("128", null);
			allNames.put("12A", null);
			allNames.put("1AP", null);
			allNames.put("1MA", null);
			allNames.put("1MG", null);
			allNames.put("1PR", null);
			allNames.put("2AR", null);
			allNames.put("2AT", null);
			allNames.put("2AU", null);
			allNames.put("2BD", null);
			allNames.put("2BT", null);
			allNames.put("2BU", null);
			allNames.put("2DA", null);
			allNames.put("2DM", null);
			allNames.put("2DT", null);
			allNames.put("2GT", null);
			allNames.put("2MA", null);
			allNames.put("2MG", null);
			allNames.put("2MU", null);
			allNames.put("2NT", null);
			allNames.put("2OT", null);
			allNames.put("2PR", null);
			allNames.put("2ST", null);
			allNames.put("3DR", null);
			allNames.put("3ME", null);
			allNames.put("4AC", null);
			allNames.put("4OC", null);
			allNames.put("4PC", null);
			allNames.put("4PD", null);
			allNames.put("4PE", null);
			allNames.put("4SC", null);
			allNames.put("4SU", null);
			allNames.put("5AA", null);
			allNames.put("5AT", null);
			allNames.put("5BU", null);
			allNames.put("5CG", null);
			allNames.put("5CM", null);
			allNames.put("5FC", null);
			allNames.put("5HT", null);
			allNames.put("5HU", null);
			allNames.put("5IC", null);
			allNames.put("5IT", null);
			allNames.put("5IU", null);
			allNames.put("5MC", null);
			allNames.put("5MD", null);
			allNames.put("5MU", null);
			allNames.put("5NC", null);
			allNames.put("5PC", null);
			allNames.put("64T", null);
			allNames.put("6CT", null);
			allNames.put("6HA", null);
			allNames.put("6HC", null);
			allNames.put("6HG", null);
			allNames.put("6HT", null);
			allNames.put("6IA", null);
			allNames.put("6MA", null);
			allNames.put("6MC", null);
			allNames.put("6MT", null);
			allNames.put("6OG", null);
			allNames.put("70U", null);
			allNames.put("7DA", null);
			allNames.put("7GU", null);
			allNames.put("7MG", null);
			allNames.put("8FG", null);
			allNames.put("8MG", null);
			allNames.put("8OG", null);
			allNames.put("A", null);
			allNames.put("A23", null);
			allNames.put("A2L", null);
			allNames.put("A2M", null);
			allNames.put("A3A", null);
			allNames.put("A5M", null);
			allNames.put("ABR", null);
			allNames.put("ABS", null);
			allNames.put("AD2", null);
			allNames.put("ADX", null);
			allNames.put("AET", null);
			allNames.put("AFF", null);
			allNames.put("AFG", null);
			allNames.put("AP7", null);
			allNames.put("APN", null);
			allNames.put("AS", null);
			allNames.put("ASU", null);
			allNames.put("ATD", null);
			allNames.put("ATL", null);
			allNames.put("ATM", null);
			allNames.put("AVC", null);
			allNames.put("BGM", null);
			allNames.put("BMP", null);
			allNames.put("BOE", null);
			allNames.put("BRU", null);
			allNames.put("BT5", null);
			allNames.put("C", null);
			allNames.put("C25", null);
			allNames.put("C2L", null);
			allNames.put("C2S", null);
			allNames.put("CAR", null);
			allNames.put("CB2", null);
			allNames.put("CBR", null);
			allNames.put("CCC", null);
			allNames.put("CH", null);
			allNames.put("CMR", null);
			allNames.put("CP1", null);
			allNames.put("CPN", null);
			allNames.put("CSF", null);
			allNames.put("D1P", null);
			allNames.put("D3", null);
			allNames.put("D4M", null);
			allNames.put("DA", null);
			allNames.put("DC", null);
			allNames.put("DCG", null);
			allNames.put("DCT", null);
			allNames.put("DDG", null);
			allNames.put("DDN", null);
			allNames.put("DDX", null);
			allNames.put("DFC", null);
			allNames.put("DFG", null);
			allNames.put("DFT", null);
			allNames.put("DG", null);
			allNames.put("DGI", null);
			allNames.put("DGP", null);
			allNames.put("DHU", null);
			allNames.put("DI", null);
			allNames.put("DNR", null);
			allNames.put("DOC", null);
			allNames.put("DPB", null);
			allNames.put("DRM", null);
			allNames.put("DRT", null);
			allNames.put("DRZ", null);
			allNames.put("DT", null);
			allNames.put("DU", null);
			allNames.put("DXD", null);
			allNames.put("DXN", null);
			allNames.put("E", null);
			allNames.put("E1X", null);
			allNames.put("EDA", null);
			allNames.put("EDC", null);
			allNames.put("EIT", null);
			allNames.put("ENA", null);
			allNames.put("ENP", null);
			allNames.put("FA2", null);
			allNames.put("FA5", null);
			allNames.put("FHU", null);
			allNames.put("G", null);
			allNames.put("G25", null);
			allNames.put("G2L", null);
			allNames.put("G2S", null);
			allNames.put("G4P", null);
			allNames.put("G7M", null);
			allNames.put("GAO", null);
			allNames.put("GDP", null);
			allNames.put("GDR", null);
			allNames.put("GH3", null);
			allNames.put("GMS", null);
			allNames.put("GMU", null);
			allNames.put("GN7", null);
			allNames.put("GNE", null);
			allNames.put("GOM", null);
			allNames.put("GPN", null);
			allNames.put("GS", null);
			allNames.put("GSR", null);
			allNames.put("GSS", null);
			allNames.put("GTP", null);
			allNames.put("H2U", null);
			allNames.put("HDP", null);
			allNames.put("HEU", null);
			allNames.put("HOB", null);
			allNames.put("HOL", null);
			allNames.put("I", null);
			allNames.put("I5C", null);
			allNames.put("IGU", null);
			allNames.put("IMC", null);
			allNames.put("IPN", null);
			allNames.put("IU", null);
			allNames.put("KAG", null);
			allNames.put("LC", null);
			allNames.put("LCG", null);
			allNames.put("LG", null);
			allNames.put("LGP", null);
			allNames.put("LHU", null);
			allNames.put("M1G", null);
			allNames.put("M2G", null);
			allNames.put("M5M", null);
			allNames.put("MA6", null);
			allNames.put("MA7", null);
			allNames.put("MAD", null);
			allNames.put("MBZ", null);
			allNames.put("MCY", null);
			allNames.put("MEP", null);
			allNames.put("MIA", null);
			allNames.put("MMT", null);
			allNames.put("MNU", null);
			allNames.put("MRG", null);
			allNames.put("MTR", null);
			allNames.put("MTU", null);
			allNames.put("N", null);
			allNames.put("N6G", null);
			allNames.put("NF2", null);
			allNames.put("NMS", null);
			allNames.put("NMT", null);
			allNames.put("OMC", null);
			allNames.put("OMG", null);
			allNames.put("OMU", null);
			allNames.put("ONE", null);
			allNames.put("P", null);
			allNames.put("P1P", null);
			allNames.put("P2T", null);
			allNames.put("P2U", null);
			allNames.put("P5P", null);
			allNames.put("PDU", null);
			allNames.put("PG7", null);
			allNames.put("PGN", null);
			allNames.put("PGP", null);
			allNames.put("PMT", null);
			allNames.put("PPU", null);
			allNames.put("PPW", null);
			allNames.put("PQ1", null);
			allNames.put("PR5", null);
			allNames.put("PRN", null);
			allNames.put("PST", null);
			allNames.put("PSU", null);
			allNames.put("PU", null);
			allNames.put("PUY", null);
			allNames.put("PYO", null);
			allNames.put("QUO", null);
			allNames.put("R", null);
			allNames.put("RIA", null);
			allNames.put("RMP", null);
			allNames.put("RT", null);
			allNames.put("S2M", null);
			allNames.put("S4C", null);
			allNames.put("S4U", null);
			allNames.put("S6G", null);
			allNames.put("SC", null);
			allNames.put("SMP", null);
			allNames.put("SMT", null);
			allNames.put("SPT", null);
			allNames.put("SRA", null);
			allNames.put("SSU", null);
			allNames.put("SUR", null);
			allNames.put("T23", null);
			allNames.put("T2S", null);
			allNames.put("T2T", null);
			allNames.put("T3P", null);
			allNames.put("T4S", null);
			allNames.put("T6A", null);
			allNames.put("TAF", null);
			allNames.put("TC1", null);
			allNames.put("TCP", null);
			allNames.put("TFE", null);
			allNames.put("TFO", null);
			allNames.put("TFT", null);
			allNames.put("TGP", null);
			allNames.put("TLC", null);
			allNames.put("TLN", null);
			allNames.put("TP1", null);
			allNames.put("TPC", null);
			allNames.put("TPG", null);
			allNames.put("TPN", null);
			allNames.put("TS", null);
			allNames.put("TSP", null);
			allNames.put("TTD", null);
			allNames.put("TTM", null);
			allNames.put("TYU", null);
			allNames.put("U", null);
			allNames.put("U25", null);
			allNames.put("U2L", null);
			allNames.put("U8U", null);
			allNames.put("UAR", null);
			allNames.put("UD5", null);
			allNames.put("UMP", null);
			allNames.put("UMS", null);
			allNames.put("UR3", null);
			allNames.put("URD", null);
			allNames.put("X", null);
			allNames.put("XAE", null);
			allNames.put("XCS", null);
			allNames.put("XCY", null);
			allNames.put("XGA", null);
			allNames.put("XTY", null);
			allNames.put("XUG", null);
			allNames.put("Y", null);
			allNames.put("YG", null);
			allNames.put("YRR", null);
			allNames.put("YYG", null);
			allNames.put("Z", null);
			allNames.put("ZDU", null);

		}
		
		return allNames.containsKey(compoundCode);
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
