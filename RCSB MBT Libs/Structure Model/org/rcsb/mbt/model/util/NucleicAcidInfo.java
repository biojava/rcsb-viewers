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
