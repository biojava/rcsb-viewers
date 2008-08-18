//  $Id: CodonInfo.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: CodonInfo.java,v $
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
//  Revision 1.3  2004/04/09 00:15:20  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.2  2004/01/29 17:29:07  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.1.1.1  2002/07/16 18:00:21  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//


package org.rcsb.mbt.model.util;


/**
 *  Provides static information about mRNA Codons such nucleic acid tuple
 *  / amino acid code mapping.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.Residue
 *  @see	org.rcsb.mbt.model.util.AminoAcidInfo
 *  @see	org.rcsb.mbt.model.util.NucleicAcidInfo
 */
public class CodonInfo
{
	/**
	 *  A 2D storage array for nucleic acid tuple / amino acid code mapping.
	 *  <P>
	 *  codons[0][][] is the 1st letter of the nucleic acid codon 3-tuple.
	 *  codons[][0][] is the 2nd letter of the nucleic acid codon 3-tuple.
	 *  codons[][][0] is the 3rd letter of the nucleic acid codon 3-tuple.
	 *  codons[i][j][k] is the amino acid character code.
	 */
	private static final String codons[][][] =
	{
		// 1st letter = U
		{
			// 2nd letter = U
			{ "F", "F", "L", "L" },  // 3rd letter = U, C, A, G
			// 2nd letter = C
			{ "S", "S", "S", "S" },  // 3rd letter = U, C, A, G
			// 2nd letter = A
			{ "Y", "Y", ".", "." },  // 3rd letter = U, C, A, G
			// 2nd letter = G
			{ "C", "C", ".", "W" }   // 3rd letter = U, C, A, G
		},
		// 1st letter = C
		{
			// 2nd letter = U
			{ "L", "L", "L", "L" },  // 3rd letter = U, C, A, G
			// 2nd letter = C
			{ "P", "P", "P", "P" },  // 3rd letter = U, C, A, G
			// 2nd letter = A
			{ "H", "H", "Q", "Q" },  // 3rd letter = U, C, A, G
			// 2nd letter = G
			{ "R", "R", "R", "R" }   // 3rd letter = U, C, A, G
		},
		// 1st letter = A
		{
			// 2nd letter = U
			{ "I", "I", "I", "I" },  // 3rd letter = U, C, A, G
			// 2nd letter = C
			{ "T", "T", "T", "T" },  // 3rd letter = U, C, A, G
			// 2nd letter = A
			{ "B", "B", "K", "K" },  // 3rd letter = U, C, A, G
			// 2nd letter = G
			{ "S", "S", "R", "R" }   // 3rd letter = U, C, A, G
		},
		// 1st letter = G
		{
			// 2nd letter = U
			{ "V", "V", "V", "V" },  // 3rd letter = U, C, A, G
			// 2nd letter = C
			{ "A", "A", "A", "A" },  // 3rd letter = U, C, A, G
			// 2nd letter = A
			{ "D", "D", "E", "E" },  // 3rd letter = U, C, A, G
			// 2nd letter = G
			{ "G", "G", "G", "G" }   // 3rd letter = U, C, A, G
		}
	};

	private static final int getBaseIndex( final char letter )
	{
		if ( letter == 'U' ) {
			return 0;
		}
		if ( letter == 'C' ) {
			return 1;
		}
		if ( letter == 'A' ) {
			return 2;
		}
		if ( letter == 'G' ) {
			return 3;
		}
		return -1;
	}

	private static final String letters[] = { "U", "C", "A", "G" };
	/**
	 *  Returns the number of codon/amino-acid tuples in the database.
	 *  <P>
	 */
	public static int getCodonCount( )
	{
		return 64; // There are 64 3-tuple mRNA codons.
	}

	/**
	 *  Returns the amino acid letter code for the given 3-letter codon
	 *  tuple.
	 *  <P>
	 */
	public static String getAminoAcidLetter( final String codon )
	{
		if ( codon == null ) {
			return null;
		}

		final int i = CodonInfo.getBaseIndex( codon.charAt(0) );
		final int j = CodonInfo.getBaseIndex( codon.charAt(1) );
		final int k = CodonInfo.getBaseIndex( codon.charAt(2) );

		if ( (i < 0) || (j < 0) || (k < 0) ) {
			return null;
		}
		if ( i >= CodonInfo.codons.length ) {
			return null;
		}
		if ( j >= CodonInfo.codons[i].length ) {
			return null;
		}
		if ( k >= CodonInfo.codons[i][j].length ) {
			return null;
		}

		return CodonInfo.codons[i][j][k];
	}

	/**
	 *  Returns the amino acid letter code for the given 3-letter codon
	 *  tuple.
	 *  <P>
	 */
	public static String getAminoAcidLetter( final String letter1, final String letter2, final String letter3 )
	{
		if ( letter1 == null ) {
			return null;
		}
		if ( letter2 == null ) {
			return null;
		}
		if ( letter3 == null ) {
			return null;
		}

		final int i = CodonInfo.getBaseIndex( letter1.charAt(0) );
		final int j = CodonInfo.getBaseIndex( letter2.charAt(0) );
		final int k = CodonInfo.getBaseIndex( letter3.charAt(0) );

		if ( (i < 0) || (j < 0) || (k < 0) ) {
			return null;
		}
		if ( i >= CodonInfo.codons.length ) {
			return null;
		}
		if ( j >= CodonInfo.codons[i].length ) {
			return null;
		}
		if ( k >= CodonInfo.codons[i][j].length ) {
			return null;
		}

		return CodonInfo.codons[i][j][k];
	}
}
