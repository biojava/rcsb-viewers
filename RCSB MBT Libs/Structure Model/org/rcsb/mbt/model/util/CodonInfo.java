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
