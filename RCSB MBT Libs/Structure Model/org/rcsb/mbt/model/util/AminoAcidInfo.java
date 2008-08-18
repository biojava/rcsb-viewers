//  $Id: AminoAcidInfo.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: AminoAcidInfo.java,v $
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
//  Revision 1.7  2004/04/09 00:15:20  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.6  2004/01/29 17:29:06  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.5  2004/01/15 20:45:56  moreland
//  Revamped to use the new AminoAcid container class.
//  Added non-standard amino acids to the dictionary.
//
//  Revision 1.4  2003/05/20 22:19:05  moreland
//  Corrected spelling of "Tyrosine" VS "Tryosine".
//
//  Revision 1.3  2003/04/23 22:50:56  moreland
//  Added static hydrophobicity lookup support.
//
//  Revision 1.2  2003/04/03 22:42:14  moreland
//  Added a comment to suggest that the class should eventually be divided
//  into separate "AminoAcid" and "AminoAcids" classes.
//
//  Revision 1.1.1.1  2002/07/16 18:00:21  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//


package org.rcsb.mbt.model.util;


import java.util.*;


/**
 *  Provides static information about Amino Acids such as character codes,
 *  3-letter codes, and full names.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Residue
 */
public class AminoAcidInfo
{
	// AminoAcid tables.
	//
	// AminoAcid object fields:
	// letter, code, name, hydrophobicity, molecularWeight

	// Standard Amino Acids
	private static final AminoAcid aminoAcids[] =
	{
		new AminoAcid( "A", "ALA", "Alanine",       0.616f, 0.0f ),
		new AminoAcid( "B", "ASX", "Asparagine",    0.000f, 0.0f ),
		new AminoAcid( "C", "CYS", "Cysteine",      0.680f, 0.0f ),
		new AminoAcid( "D", "ASP", "Aspartic Acid", 0.028f, 0.0f ),
		new AminoAcid( "E", "GLU", "Glutamic Acid", 0.043f, 0.0f ),
		new AminoAcid( "F", "PHE", "Phenylalanine", 1.000f, 0.0f ),
		new AminoAcid( "G", "GLY", "Glycine",       0.501f, 0.0f ),
		new AminoAcid( "H", "HIS", "Histidine",     0.165f, 0.0f ),
		new AminoAcid( "I", "ILE", "Isoleucine",    0.943f, 0.0f ),
		new AminoAcid( "J", "UNK", "UNKNOWN",       0.000f, 0.0f ),
		new AminoAcid( "K", "LYS", "Lysine",        0.283f, 0.0f ),
		new AminoAcid( "L", "LEU", "Leucine",       0.943f, 0.0f ),
		new AminoAcid( "M", "MET", "Methionine",    0.738f, 0.0f ),
		new AminoAcid( "N", "ASN", "Asparagine",    0.236f, 0.0f ),
		new AminoAcid( "O", "UNK", "UNKNOWN",       0.000f, 0.0f ),
		new AminoAcid( "P", "PRO", "Proline",       0.711f, 0.0f ),
		new AminoAcid( "Q", "GLN", "Glutamine",     0.251f, 0.0f ),
		new AminoAcid( "R", "ARG", "Arginine",      0.000f, 0.0f ),
		new AminoAcid( "S", "SER", "Serine",        0.359f, 0.0f ),
		new AminoAcid( "T", "THR", "Threonine",     0.450f, 0.0f ),
		new AminoAcid( "U", "UNK", "UNKNOWN",       0.000f, 0.0f ),
		new AminoAcid( "V", "VAL", "Valine",        0.825f, 0.0f ),
		new AminoAcid( "W", "TRP", "Tryptophane",   0.878f, 0.0f ),
		new AminoAcid( "X", "UNK", "UNKNOWN",       0.000f, 0.0f ),
		new AminoAcid( "Y", "TYR", "Tyrosine",      0.880f, 0.0f ),
		new AminoAcid( "Z", "GLX", "Glutamic Acid", 0.043f, 0.0f ),
		new AminoAcid( ".", "end", "End Chain",     0.000f, 0.0f )
	};

	private static final Hashtable letterHash = new Hashtable( );
	private static final Hashtable codeHash = new Hashtable( );
	private static final Hashtable nameHash = new Hashtable( );

	private static final Hashtable nonStandardCodes = new Hashtable( );

	static
	{
		// Initialize non-standard amino acid codes hash
		AminoAcidInfo.nonStandardCodes.put( "143", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "1LU", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "1PA", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "2AS", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "2LU", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "2ML", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "2MR", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "3AH", "HIS" );
		AminoAcidInfo.nonStandardCodes.put( "3MD", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "5CS", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "5HP", "GLU" );
		AminoAcidInfo.nonStandardCodes.put( "AA3", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "AA4", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "AAR", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "ABA", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "ACB", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "ACL", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "ADY", "ADN" );
		AminoAcidInfo.nonStandardCodes.put( "AEI", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "AFA", "ASN" );
		AminoAcidInfo.nonStandardCodes.put( "AGM", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "AHB", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "AHO", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "AHP", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "AIB", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "ALC", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "ALG", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "ALM", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "ALN", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "ALO", "THR" );
		AminoAcidInfo.nonStandardCodes.put( "ALS", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "ALT", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "ALY", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "APH", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "API", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "ARM", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "ARO", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "ASA", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "ASB", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "ASI", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "ASK", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "ASL", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "ASQ", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "AYA", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "B1F", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "B2A", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "B2F", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "B2I", "ILE" );
		AminoAcidInfo.nonStandardCodes.put( "B2V", "VAL" );
		AminoAcidInfo.nonStandardCodes.put( "BAL", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "BCS", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "BCX", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "BFD", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "BHD", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "BLE", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "BLY", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "BMT", "THR" );
		AminoAcidInfo.nonStandardCodes.put( "BNN", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "BOR", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "BSE", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "BTA", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "BTC", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "BTR", "TRP" );
		AminoAcidInfo.nonStandardCodes.put( "BUC", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "BUG", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "C5C", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "C6C", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CAB", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "CAF", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CAS", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CAY", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CCS", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CEA", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CGU", "GLU" );
		AminoAcidInfo.nonStandardCodes.put( "CHG", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "CHP", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "CLB", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "CLD", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "CLE", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "CLG", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "CLH", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "CME", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CMT", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CR5", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "CRO", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "CRQ", "GLN" );
		AminoAcidInfo.nonStandardCodes.put( "CS8", "COA" );
		AminoAcidInfo.nonStandardCodes.put( "CSA", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CSB", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CSD", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CSE", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CSI", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "CSO", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CSP", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CSR", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CSS", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CSU", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CSW", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CSX", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CSZ", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CTH", "THR" );
		AminoAcidInfo.nonStandardCodes.put( "CXM", "MET" );
		AminoAcidInfo.nonStandardCodes.put( "CY1", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CY3", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CY4", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CYD", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CYF", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CYG", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CYM", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CYQ", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "CZZ", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "DAB", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "DAH", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "DAL", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "DAR", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "DAS", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "DBS", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "DBY", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "DCY", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "DGL", "GLU" );
		AminoAcidInfo.nonStandardCodes.put( "DGN", "GLN" );
		AminoAcidInfo.nonStandardCodes.put( "DHA", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "DHI", "HIS" );
		AminoAcidInfo.nonStandardCodes.put( "DHN", "VAL" );
		AminoAcidInfo.nonStandardCodes.put( "DIL", "ILE" );
		AminoAcidInfo.nonStandardCodes.put( "DIV", "VAL" );
		AminoAcidInfo.nonStandardCodes.put( "DLE", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "DLS", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "DLY", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "DMH", "ASN" );
		AminoAcidInfo.nonStandardCodes.put( "DMK", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "DNE", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "DNL", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "DNM", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "DNP", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "DOH", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "DPH", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "DPL", "PRO" );
		AminoAcidInfo.nonStandardCodes.put( "DPN", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "DPP", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "DPR", "PRO" );
		AminoAcidInfo.nonStandardCodes.put( "DSE", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "DSN", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "DSP", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "DTH", "THR" );
		AminoAcidInfo.nonStandardCodes.put( "DTR", "TRP" );
		AminoAcidInfo.nonStandardCodes.put( "DTY", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "DVA", "VAL" );
		AminoAcidInfo.nonStandardCodes.put( "EFC", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "EHP", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "ESC", "MET" );
		AminoAcidInfo.nonStandardCodes.put( "FGL", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "FLA", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "FLE", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "FLT", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "FME", "MET" );
		AminoAcidInfo.nonStandardCodes.put( "FOE", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "FOG", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "FPA", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "FTR", "TRP" );
		AminoAcidInfo.nonStandardCodes.put( "FTY", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "GGL", "GLU" );
		AminoAcidInfo.nonStandardCodes.put( "GHP", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "GL3", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "GLH", "GLN" );
		AminoAcidInfo.nonStandardCodes.put( "GLZ", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "GMA", "GLU" );
		AminoAcidInfo.nonStandardCodes.put( "GMU", "5MU" );
		AminoAcidInfo.nonStandardCodes.put( "GPL", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "GSC", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "GT9", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "H5M", "PRO" );
		AminoAcidInfo.nonStandardCodes.put( "HAC", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "HAR", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "HIC", "HIS" );
		AminoAcidInfo.nonStandardCodes.put( "HIP", "HIS" );
		AminoAcidInfo.nonStandardCodes.put( "HMF", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "HMR", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "HPC", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "HPE", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "HPQ", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "HSE", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "HSL", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "HTI", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "HTR", "TRP" );
		AminoAcidInfo.nonStandardCodes.put( "HV5", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "HYP", "PRO" );
		AminoAcidInfo.nonStandardCodes.put( "IAS", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "IGL", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "IIL", "ILE" );
		AminoAcidInfo.nonStandardCodes.put( "ILG", "GLU" );
		AminoAcidInfo.nonStandardCodes.put( "ILX", "ILE" );
		AminoAcidInfo.nonStandardCodes.put( "IML", "ILE" );
		AminoAcidInfo.nonStandardCodes.put( "IPG", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "IYR", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "KCX", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "LCX", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "LEF", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "LLP", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "LLY", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "LPG", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "LPS", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "LTR", "TRP" );
		AminoAcidInfo.nonStandardCodes.put( "LYM", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "LYN", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "LYX", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "LYZ", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "M3L", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "MAA", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "MAI", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "MC1", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "MCL", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "MEA", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "MEN", "ASN" );
		AminoAcidInfo.nonStandardCodes.put( "MEQ", "GLN" );
		AminoAcidInfo.nonStandardCodes.put( "MGG", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "MGN", "GLN" );
		AminoAcidInfo.nonStandardCodes.put( "MGY", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "MHL", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "MHO", "MET" );
		AminoAcidInfo.nonStandardCodes.put( "MHS", "HIS" );
		AminoAcidInfo.nonStandardCodes.put( "MIS", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "MLE", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "MLY", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "MLZ", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "MME", "MET" );
		AminoAcidInfo.nonStandardCodes.put( "MNL", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "MNV", "VAL" );
		AminoAcidInfo.nonStandardCodes.put( "MPQ", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "MSA", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "MSE", "MET" );
		AminoAcidInfo.nonStandardCodes.put( "MSO", "MET" );
		AminoAcidInfo.nonStandardCodes.put( "MTY", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "MVA", "VAL" );
		AminoAcidInfo.nonStandardCodes.put( "NAL", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "NAM", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "NC1", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "NCB", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "NEM", "HIS" );
		AminoAcidInfo.nonStandardCodes.put( "NEP", "HIS" );
		AminoAcidInfo.nonStandardCodes.put( "NIY", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "NLE", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "NLN", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "NLP", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "NMC", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "NNH", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "NPH", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "NRQ", "MET" );
		AminoAcidInfo.nonStandardCodes.put( "NVA", "VAL" );
		AminoAcidInfo.nonStandardCodes.put( "OAS", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "OCS", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "OCY", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "OMT", "MET" );
		AminoAcidInfo.nonStandardCodes.put( "OPR", "ARG" );
		AminoAcidInfo.nonStandardCodes.put( "ORN", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "P2Y", "PRO" );
		AminoAcidInfo.nonStandardCodes.put( "PAQ", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "PAS", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "PAT", "TRP" );
		AminoAcidInfo.nonStandardCodes.put( "PBB", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "PBF", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "PCA", "GLU" );
		AminoAcidInfo.nonStandardCodes.put( "PCC", "PRO" );
		AminoAcidInfo.nonStandardCodes.put( "PCS", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "PEC", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "PG1", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "PGY", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "PHA", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "PHD", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "PHI", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "PHL", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "PHM", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "PLE", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "POM", "PRO" );
		AminoAcidInfo.nonStandardCodes.put( "PPH", "LEU" );
		AminoAcidInfo.nonStandardCodes.put( "PPN", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "PR3", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "PRR", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "PRS", "PRO" );
		AminoAcidInfo.nonStandardCodes.put( "PTH", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "PTM", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "PTR", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "PYA", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "PYX", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "RAB", "ADE" );
		AminoAcidInfo.nonStandardCodes.put( "S1H", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "SAC", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "SAR", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "SBD", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "SBL", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "SCH", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "SCS", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "SCY", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "SDP", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "SEB", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "SEC", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "SEG", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "SEL", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "SEP", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "SET", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "SHC", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "SHP", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "SHR", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "SLZ", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "SMC", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "SME", "MET" );
		AminoAcidInfo.nonStandardCodes.put( "SMF", "PHE" );
		AminoAcidInfo.nonStandardCodes.put( "SNC", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "SOC", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "STY", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "SUI", "ASP" );
		AminoAcidInfo.nonStandardCodes.put( "SVA", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "TBG", "GLY" );
		AminoAcidInfo.nonStandardCodes.put( "TBM", "THR" );
		AminoAcidInfo.nonStandardCodes.put( "THC", "THR" );
		AminoAcidInfo.nonStandardCodes.put( "TIH", "ALA" );
		AminoAcidInfo.nonStandardCodes.put( "TMB", "THR" );
		AminoAcidInfo.nonStandardCodes.put( "TMD", "THR" );
		AminoAcidInfo.nonStandardCodes.put( "TNB", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "TNR", "SER" );
		AminoAcidInfo.nonStandardCodes.put( "TPL", "TRP" );
		AminoAcidInfo.nonStandardCodes.put( "TPO", "THR" );
		AminoAcidInfo.nonStandardCodes.put( "TPQ", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "TRF", "TRP" );
		AminoAcidInfo.nonStandardCodes.put( "TRG", "LYS" );
		AminoAcidInfo.nonStandardCodes.put( "TRN", "TRP" );
		AminoAcidInfo.nonStandardCodes.put( "TRO", "TRP" );
		AminoAcidInfo.nonStandardCodes.put( "TRQ", "TRP" );
		AminoAcidInfo.nonStandardCodes.put( "TRW", "TRP" );
		AminoAcidInfo.nonStandardCodes.put( "TRX", "TRP" );
		AminoAcidInfo.nonStandardCodes.put( "TYB", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "TYI", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "TYN", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "TYQ", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "TYS", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "TYT", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "TYY", "TYR" );
		AminoAcidInfo.nonStandardCodes.put( "VAD", "VAL" );
		AminoAcidInfo.nonStandardCodes.put( "VAF", "VAL" );
		AminoAcidInfo.nonStandardCodes.put( "XYA", "ADE" );
		AminoAcidInfo.nonStandardCodes.put( "YCM", "CYS" );
		AminoAcidInfo.nonStandardCodes.put( "YOF", "TYR" );

		// Populate lookup hash tables
		for ( int i=0; i<AminoAcidInfo.aminoAcids.length; i++ )
		{
			AminoAcidInfo.letterHash.put( AminoAcidInfo.aminoAcids[i].letter, AminoAcidInfo.aminoAcids[i] );
			AminoAcidInfo.codeHash.put( AminoAcidInfo.aminoAcids[i].code, AminoAcidInfo.aminoAcids[i] );
			AminoAcidInfo.nameHash.put( AminoAcidInfo.aminoAcids[i].name, AminoAcidInfo.aminoAcids[i] );
		}

		// Add the non-standard codes to the codeHash.
		final Enumeration keys = AminoAcidInfo.nonStandardCodes.keys( );
		while ( keys.hasMoreElements( ) )
		{
			final String nsCode = (String) keys.nextElement( );
			if ( nsCode == null ) {
				continue;
			}
			final String parentCode = (String) AminoAcidInfo.nonStandardCodes.get( nsCode );
			if ( parentCode == null ) {
				continue;
			}
			final AminoAcid aminoAcid = (AminoAcid) AminoAcidInfo.codeHash.get( parentCode );
			if ( aminoAcid == null ) {
				continue;
			}
			AminoAcidInfo.codeHash.put( nsCode, aminoAcid );
		}
	}

	// Hastable indexes


	/**
	 *  Returns the number of amino acid name tuples in the database.
	 *  <P>
	 */
	public static int getNameCount( )
	{
		return AminoAcidInfo.aminoAcids.length;
	}

	/**
	 *  Returns the 3-letter code equivalent for the given letter.
	 *  <P>
	 */
	public static String getCodeFromLetter( final String letter )
	{
		if ( letter == null ) {
			return null;
		}
		final AminoAcid aminoAcid = (AminoAcid) AminoAcidInfo.letterHash.get( letter );
		if ( aminoAcid == null ) {
			return null;
		}
		return aminoAcid.code;
	}

	/**
	 *  Returns the 3-letter code equivalent for the given letter.
	 *  <P>
	 */
	public static String getCodeFromLetter( final byte letter )
	{
		final byte letterArray[] = { letter };
		return AminoAcidInfo.getCodeFromLetter( new String( letterArray ) );
	}

	/**
	 *  Returns the character code equivalent for the given 3-letter code.
	 *  <P>
	 */
	public static String getLetterFromCode( final String code )
	{
		if ( code == null ) {
			return null;
		}
		final AminoAcid aminoAcid = (AminoAcid) AminoAcidInfo.codeHash.get( code );
		if ( aminoAcid == null ) {
			return null;
		}
		return aminoAcid.letter;
	}

	/**
	 *  Returns the full name equivalent for the given 3-letter code.
	 *  <P>
	 */
	public static String getNameFromCode( final String code )
	{
		if ( code == null ) {
			return null;
		}
		final AminoAcid aminoAcid = (AminoAcid) AminoAcidInfo.codeHash.get( code );
		if ( aminoAcid == null ) {
			return null;
		}
		return aminoAcid.name;
	}

	/**
	 *  Returns the full name equivalent for the given character code.
	 *  <P>
	 */
	public static String getNameFromLetter( final String letter )
	{
		if ( letter == null ) {
			return null;
		}
		final AminoAcid aminoAcid = (AminoAcid) AminoAcidInfo.letterHash.get( letter );
		if ( aminoAcid == null ) {
			return null;
		}
		return aminoAcid.name;
	}

	/**
	 *  Returns hydrophobicity given the 3-letter code.
	 *  <P>
	 *  Hydrophobicity of an amino acid may be obtained experimentally
	 *  or may be computed using one of 37 different algorithms. There
	 *  are many sources of hydrophobicity methods/scales:
	 *  http://psyche.uthct.edu/shaun/SBlack/aagrease.html
	 *  http://bioinformatics.weizmann.ac.il/hydroph/data_used.html
	 *  http://prowl.rockefeller.edu/aainfo/hydro.htm
	 *  http://blanco.biomol.uci.edu/hydrophobicity_scales.html
	 *  etc...
	 *  <P>
	 */
	public static float getHydrophobicityFromCode( final String code )
	{
		if ( code == null ) {
			return 0.0f;
		}
		final AminoAcid aminoAcid = (AminoAcid) AminoAcidInfo.codeHash.get( code );
		if ( aminoAcid == null ) {
			return 0.0f;
		}
		return aminoAcid.hydrophobicity;
	}

	//
	// AminoAcid object access.
	//

	/**
	 *  Returns an enumeration of Amino Acid 3-letter codes.
	 */
	public static Enumeration getCodes( )
	{
		return AminoAcidInfo.codeHash.keys( );
	}

	/**
	 *  Returns an enumeration of Amino Acid letters.
	 */
	public static Enumeration getLetters( )
	{
		return AminoAcidInfo.letterHash.keys( );
	}

	/**
	 *  Returns an enumeration of Amino Acid names.
	 */
	public static Enumeration getNames( )
	{
		return AminoAcidInfo.nameHash.keys( );
	}

	/**
	 *  Returns an AminoAcid object for the given 3-letter code.
	 */
	public static AminoAcid getFromCode( final String code )
	{
		return (AminoAcid) AminoAcidInfo.codeHash.get( code );
	}

	/**
	 *  Returns an AminoAcid object for the given letter.
	 */
	public static AminoAcid getFromLetter( final String letter )
	{
		return (AminoAcid) AminoAcidInfo.letterHash.get( letter );
	}

	/**
	 *  Returns an AminoAcid object for the given name.
	 */
	public static AminoAcid getFromName( final String name )
	{
		return (AminoAcid) AminoAcidInfo.nameHash.get( name );
	}

	//
	// Unit testing.
	//

	/**
	 *  Unit testing entry point.
	 */
	public static void main( final String[] args )
	{
		System.err.println( "AminoAcidInfo: ALA = " +
			AminoAcidInfo.getNameFromCode( "ALA" ) );

		System.err.println( "AminoAcidInfo: SVA = non-standard " +
			AminoAcidInfo.getNameFromCode( "SVA" ) );

		System.err.println( "AminoAcidInfo: B = " +
			AminoAcidInfo.getNameFromLetter( "B" ) );

		System.err.println( "AminoAcidInfo: GLU = " +
			AminoAcidInfo.getFromCode( "GLU" ) );

		System.err.println( "AminoAcidInfo: Z = " +
			AminoAcidInfo.getFromLetter( "Z" ) );
	}
}
