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
		if(allNames == null) {
			allNames = new HashSet<String>();
			allNames.add("0AD");
			allNames.add("0AM");
			allNames.add("0AP");
			allNames.add("0AU");
			allNames.add("0AV");
			allNames.add("10C");
			allNames.add("125");
			allNames.add("126");
			allNames.add("127");
			allNames.add("128");
			allNames.add("12A");
			allNames.add("1AP");
			allNames.add("1MA");
			allNames.add("1MG");
			allNames.add("1PR");
			allNames.add("1SC");
			allNames.add("2AR");
			allNames.add("2AT");
			allNames.add("2AU");
			allNames.add("2BD");
			allNames.add("2BT");
			allNames.add("2BU");
			allNames.add("2DA");
			allNames.add("2DF");
			allNames.add("2DM");
			allNames.add("2DT");
			allNames.add("2EG");
			allNames.add("2GT");
			allNames.add("2MA");
			allNames.add("2MG");
			allNames.add("2MU");
			allNames.add("2NT");
			allNames.add("2OT");
			allNames.add("2PR");
			allNames.add("2SA");
			allNames.add("2ST");
			allNames.add("3DA");
			allNames.add("3DR");
			allNames.add("3ME");
			allNames.add("4AC");
			allNames.add("4MF");
			allNames.add("4OC");
			allNames.add("4PC");
			allNames.add("4PD");
			allNames.add("4PE");
			allNames.add("4SC");
			allNames.add("4SU");
			allNames.add("4TA");
			allNames.add("5AA");
			allNames.add("5AT");
			allNames.add("5BU");
			allNames.add("5CG");
			allNames.add("5CM");
			allNames.add("5FA");
			allNames.add("5FC");
			allNames.add("5FU");
			allNames.add("5HT");
			allNames.add("5HU");
			allNames.add("5IC");
			allNames.add("5IT");
			allNames.add("5IU");
			allNames.add("5MC");
			allNames.add("5MD");
			allNames.add("5MU");
			allNames.add("5NC");
			allNames.add("5PC");
			allNames.add("5PY");
			allNames.add("64T");
			allNames.add("6CT");
			allNames.add("6HA");
			allNames.add("6HC");
			allNames.add("6HG");
			allNames.add("6HT");
			allNames.add("6IA");
			allNames.add("6MA");
			allNames.add("6MC");
			allNames.add("6MI");
			allNames.add("6MT");
			allNames.add("6MZ");
			allNames.add("6OG");
			allNames.add("70U");
			allNames.add("7DA");
			allNames.add("7GU");
			allNames.add("7MG");
			allNames.add("8FG");
			allNames.add("8MG");
			allNames.add("8OG");
			allNames.add("A");
			allNames.add("A1P");
			allNames.add("A23");
			allNames.add("A2L");
			allNames.add("A2M");
			allNames.add("A34");
			allNames.add("A35");
			allNames.add("A38");
			allNames.add("A39");
			allNames.add("A3A");
			allNames.add("A3P");
			allNames.add("A40");
			allNames.add("A43");
			allNames.add("A44");
			allNames.add("A47");
			allNames.add("A5M");
			allNames.add("ABR");
			allNames.add("ABS");
			allNames.add("ABT");
			allNames.add("AD2");
			allNames.add("ADX");
			allNames.add("AET");
			allNames.add("AFF");
			allNames.add("AFG");
			allNames.add("AP7");
			allNames.add("APN");
			allNames.add("AS");
			allNames.add("ASU");
			allNames.add("ATD");
			allNames.add("ATL");
			allNames.add("ATM");
			allNames.add("AVC");
			allNames.add("B1P");
			allNames.add("BGM");
			allNames.add("BMP");
			allNames.add("BOE");
			allNames.add("BRU");
			allNames.add("BT5");
			allNames.add("BVP");
			allNames.add("BZG");
			allNames.add("C");
			allNames.add("C25");
			allNames.add("C2L");
			allNames.add("C2S");
			allNames.add("C31");
			allNames.add("C32");
			allNames.add("C34");
			allNames.add("C36");
			allNames.add("C37");
			allNames.add("C38");
			allNames.add("C42");
			allNames.add("C43");
			allNames.add("C45");
			allNames.add("C46");
			allNames.add("C49");
			allNames.add("CAR");
			allNames.add("CB2");
			allNames.add("CBR");
			allNames.add("CBV");
			allNames.add("CCC");
			allNames.add("CFL");
			allNames.add("CH");
			allNames.add("CM0");
			allNames.add("CMR");
			allNames.add("CP1");
			allNames.add("CPN");
			allNames.add("CS8");
			allNames.add("CSF");
			allNames.add("CSL");
			allNames.add("CTG");
			allNames.add("D1P");
			allNames.add("D3");
			allNames.add("D4M");
			allNames.add("DA");
			allNames.add("DBM");
			allNames.add("DC");
			allNames.add("DCG");
			allNames.add("DCT");
			allNames.add("DDG");
			allNames.add("DDN");
			allNames.add("DDX");
			allNames.add("DFC");
			allNames.add("DFG");
			allNames.add("DFT");
			allNames.add("DG");
			allNames.add("DGI");
			allNames.add("DHU");
			allNames.add("DI");
			allNames.add("DNR");
			allNames.add("DOC");
			allNames.add("DPB");
			allNames.add("DPY");
			allNames.add("DRM");
			allNames.add("DRP");
			allNames.add("DRT");
			allNames.add("DRZ");
			allNames.add("DT");
			allNames.add("DU");
			allNames.add("DXD");
			allNames.add("DXN");
			allNames.add("E");
			allNames.add("E1X");
			allNames.add("EDA");
			allNames.add("EDC");
			allNames.add("EIT");
			allNames.add("ENA");
			allNames.add("ENP");
			allNames.add("FA2");
			allNames.add("FA5");
			allNames.add("FAG");
			allNames.add("FAI");
			allNames.add("FFD");
			allNames.add("FHU");
			allNames.add("FMU");
			allNames.add("FOX");
			allNames.add("G");
			allNames.add("G25");
			allNames.add("G2L");
			allNames.add("G2S");
			allNames.add("G31");
			allNames.add("G32");
			allNames.add("G33");
			allNames.add("G36");
			allNames.add("G38");
			allNames.add("G42");
			allNames.add("G46");
			allNames.add("G47");
			allNames.add("G48");
			allNames.add("G49");
			allNames.add("G4P");
			allNames.add("G7M");
			allNames.add("GAO");
			allNames.add("GCK");
			allNames.add("GDP");
			allNames.add("GDR");
			allNames.add("GFL");
			allNames.add("GH3");
			allNames.add("GMS");
			allNames.add("GMU");
			allNames.add("GN7");
			allNames.add("GNE");
			allNames.add("GOM");
			allNames.add("GPN");
			allNames.add("GS");
			allNames.add("GSR");
			allNames.add("GSS");
			allNames.add("GTP");
			allNames.add("H2U");
			allNames.add("HDP");
			allNames.add("HEU");
			allNames.add("HOB");
			allNames.add("HOL");
			allNames.add("I");
			allNames.add("I5C");
			allNames.add("IC");
			allNames.add("IG");
			allNames.add("IGU");
			allNames.add("IMC");
			allNames.add("IPN");
			allNames.add("IU");
			allNames.add("JW5");
			allNames.add("KAG");
			allNames.add("LC");
			allNames.add("LCA");
			allNames.add("LCC");
			allNames.add("LCG");
			allNames.add("LCH");
			allNames.add("LG");
			allNames.add("LGP");
			allNames.add("LHU");
			allNames.add("LKC");
			allNames.add("LMS");
			allNames.add("M1G");
			allNames.add("M2G");
			allNames.add("M5M");
			allNames.add("MA6");
			allNames.add("MA7");
			allNames.add("MAD");
			allNames.add("MBZ");
			allNames.add("MCY");
			allNames.add("MDR");
			allNames.add("MEP");
			allNames.add("MG1");
			allNames.add("MGQ");
			allNames.add("MGV");
			allNames.add("MIA");
			allNames.add("MMT");
			allNames.add("MNU");
			allNames.add("MRG");
			allNames.add("MTR");
			allNames.add("MTU");
			allNames.add("N");
			allNames.add("N5I");
			allNames.add("N6G");
			allNames.add("NCX");
			allNames.add("NDN");
			allNames.add("NF2");
			allNames.add("NMS");
			allNames.add("NMT");
			allNames.add("NP3");
			allNames.add("NYM");
			allNames.add("O2C");
			allNames.add("OAD");
			allNames.add("ODP");
			allNames.add("OIP");
			allNames.add("OMC");
			allNames.add("OMG");
			allNames.add("OMU");
			allNames.add("ONE");
			allNames.add("P");
			allNames.add("P1P");
			allNames.add("P2T");
			allNames.add("P2U");
			allNames.add("P5P");
			allNames.add("PBT");
			allNames.add("PDU");
			allNames.add("PG7");
			allNames.add("PGN");
			allNames.add("PGP");
			allNames.add("PMT");
			allNames.add("PPU");
			allNames.add("PPW");
			allNames.add("PQ1");
			allNames.add("PR5");
			allNames.add("PRN");
			allNames.add("PST");
			allNames.add("PSU");
			allNames.add("PU");
			allNames.add("PUY");
			allNames.add("PYO");
			allNames.add("PYY");
			allNames.add("QUO");
			allNames.add("R");
			allNames.add("RIA");
			allNames.add("RMP");
			allNames.add("RT");
			allNames.add("RTP");
			allNames.add("S2M");
			allNames.add("S4C");
			allNames.add("S4U");
			allNames.add("S6G");
			allNames.add("SC");
			allNames.add("SDG");
			allNames.add("SMP");
			allNames.add("SMT");
			allNames.add("SPT");
			allNames.add("SRA");
			allNames.add("SSU");
			allNames.add("SUR");
			allNames.add("T");
			allNames.add("T23");
			allNames.add("T2S");
			allNames.add("T2T");
			allNames.add("T31");
			allNames.add("T32");
			allNames.add("T36");
			allNames.add("T37");
			allNames.add("T38");
			allNames.add("T39");
			allNames.add("T3P");
			allNames.add("T41");
			allNames.add("T48");
			allNames.add("T49");
			allNames.add("T4S");
			allNames.add("T5S");
			allNames.add("T6A");
			allNames.add("TA3");
			allNames.add("TAF");
			allNames.add("TAL");
			allNames.add("TC1");
			allNames.add("TCP");
			allNames.add("TFE");
			allNames.add("TFO");
			allNames.add("TFT");
			allNames.add("TGP");
			allNames.add("THX");
			allNames.add("TLB");
			allNames.add("TLC");
			allNames.add("TLN");
			allNames.add("TP1");
			allNames.add("TPC");
			allNames.add("TPG");
			allNames.add("TPN");
			allNames.add("TS");
			allNames.add("TT");
			allNames.add("TTD");
			allNames.add("TTM");
			allNames.add("TYU");
			allNames.add("U");
			allNames.add("U25");
			allNames.add("U2L");
			allNames.add("U2N");
			allNames.add("U2P");
			allNames.add("U31");
			allNames.add("U33");
			allNames.add("U34");
			allNames.add("U36");
			allNames.add("U37");
			allNames.add("U8U");
			allNames.add("UAR");
			allNames.add("UCL");
			allNames.add("UD5");
			allNames.add("UDP");
			allNames.add("UFP");
			allNames.add("UFR");
			allNames.add("UMP");
			allNames.add("UMS");
			allNames.add("UR3");
			allNames.add("URD");
			allNames.add("US1");
			allNames.add("X");
			allNames.add("X4A");
			allNames.add("XAD");
			allNames.add("XAE");
			allNames.add("XAL");
			allNames.add("XAR");
			allNames.add("XCL");
			allNames.add("XCS");
			allNames.add("XCT");
			allNames.add("XCY");
			allNames.add("XGA");
			allNames.add("XGL");
			allNames.add("XGU");
			allNames.add("XTH");
			allNames.add("XTL");
			allNames.add("XTS");
			allNames.add("XTY");
			allNames.add("XUG");
			allNames.add("Y");
			allNames.add("YG");
			allNames.add("YRR");
			allNames.add("YYG");
			allNames.add("Z");
			allNames.add("ZAD");
			allNames.add("ZCY");
			allNames.add("ZDU");
			allNames.add("ZGU");
			allNames.add("ZHP");
			allNames.add("ZTH");
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
