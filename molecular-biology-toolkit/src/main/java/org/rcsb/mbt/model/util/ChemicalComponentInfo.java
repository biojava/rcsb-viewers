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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


/**
 *  Provides static information about Amino Acids such as character codes,
 *  3-letter codes, and full names.
 *  <P>
 *  @author	Peter Rose
 *  @see	org.rcsb.mbt.model.Residue
 */
public class ChemicalComponentInfo
{
	static private final String fileName = "ChemicalComponentTypes.tsv";

	// AminoAcid tables.
	//
	// AminoAcid object fields:
	// letter, code, name, hydrophobicity, molecularWeight

	// Standard Amino Acids
	private static final AminoAcid stdAminoAcids[] =
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
		new AminoAcid( "K", "LYS", "Lysine",        0.283f, 0.0f ),
		new AminoAcid( "L", "LEU", "Leucine",       0.943f, 0.0f ),
		new AminoAcid( "M", "MET", "Methionine",    0.738f, 0.0f ),
		new AminoAcid( "N", "ASN", "Asparagine",    0.236f, 0.0f ),
		new AminoAcid( "P", "PRO", "Proline",       0.711f, 0.0f ),
		new AminoAcid( "Q", "GLN", "Glutamine",     0.251f, 0.0f ),
		new AminoAcid( "R", "ARG", "Arginine",      0.000f, 0.0f ),
		new AminoAcid( "S", "SER", "Serine",        0.359f, 0.0f ),
		new AminoAcid( "T", "THR", "Threonine",     0.450f, 0.0f ),
		new AminoAcid( "V", "VAL", "Valine",        0.825f, 0.0f ),
		new AminoAcid( "W", "TRP", "Tryptophane",   0.878f, 0.0f ),
		new AminoAcid( "X", "UNK", "UNKNOWN",       0.000f, 0.0f ),
		new AminoAcid( "Y", "TYR", "Tyrosine",      0.880f, 0.0f ),
		new AminoAcid( "Z", "GLX", "Glutamic Acid", 0.043f, 0.0f )
	};

	// Standard nucleic acids
	private static final List<String> nucleicAcids = Arrays.asList("A", "C", "G", "U", "T", "DA", "DC", "DG", "DI", "DT", "DU");
		
	private static List<List<String>> chemicalComponentInfo = null;
	private static Map<String, ChemicalComponentType> chemicalComponentType = new HashMap<String, ChemicalComponentType>();
	private static Map<String, Integer> chemicalComponentParentCount = new HashMap<String, Integer>();
	private static final Hashtable<String, AminoAcid> aminoAcids = new Hashtable<String, AminoAcid>( );

	static
	{
		loadChemicalComponentTypes();
		createHashMaps();
	}
    
	
	/**
	 * Returns the Chemical Component Type for the given 3-letter code (note, sometimes its a 1- or 2-letter code)
	 * @param code the 3-letter chemical component ID
	 * @return the chemical component type
	 */
	public static ChemicalComponentType getChemicalComponentType(String code) {
		ChemicalComponentType type = chemicalComponentType.get(code);
		if (type == null) {
			type = ChemicalComponentType.NON_POLYMER;
		}
		return type;
	}
	
	
	/**
	 * Returns the character code equivalent for the given 3-letter code.
	 * @param code 3-letter chemical component ID
	 * @return character code of chemical component
	 */
	public static String getLetterFromCode(String code)
	{
		AminoAcid aminoAcid = ChemicalComponentInfo.aminoAcids.get(code);
		if (aminoAcid == null) {
			if (nucleicAcids.contains(code)) {
				if (code.length() == 2) {
					return code.substring(1);
				} else {
					return code;
				}
			}
			return "X";
		}
		return aminoAcid.letter;
	}

	/**
	 *  Returns the full name equivalent for the given 3-letter code.
	 *  <P>
	 */
	public static String getNameFromCode(String code)
	{
		AminoAcid aminoAcid = ChemicalComponentInfo.aminoAcids.get(code);
		if (aminoAcid == null) {
			return null;
		}
		return aminoAcid.name;
	}


	/**
	 *  Returns hydrophobicity given the 3-letter code.
	 */
	public static float getHydrophobicityFromCode(String code)
	{
		AminoAcid aminoAcid = ChemicalComponentInfo.aminoAcids.get(code);
		if (aminoAcid == null) {
			return 0.0f;
		}
		return aminoAcid.hydrophobicity;
	}
	
	/**
	 * Returns the number of parent residues for a nonstandard residue
	 * @param code the 3-letter chemical component ID
	 * @return the number of parent residues
	 */
	public static int getChemicalComponentParentCount(String code) {
		Integer count = chemicalComponentParentCount.get(code);
		if (count == null) {
			count = 1;
		}
		return count;
	}
	
	/**
     * Returns true if the chemical component is composed of multiple parent components (i.e. CRO is made from GLY-TYR-GLY tripeptide)
     * @param code the 3-letter chemical component ID
     * @return true if chemical component in a nonstandard residue made from multiple components
     */
	public static boolean isMultiComponentModifiedResidue(String code) {
		return getChemicalComponentParentCount(code) > 1;
	}
	
	public static boolean isNonstandardAminoAcid(String code) {
		return !aminoAcids.containsKey(code) && getChemicalComponentType(code).isPeptide();
	}
	
	public static boolean isNonstandardNucleicAcid(String code) {
		return !nucleicAcids.contains(code) && getChemicalComponentType(code).isNucleotide();
	}

	private static void loadChemicalComponentTypes() {
		InputStream is = null;
		BufferedReader br = null;
		
		// read file embedded in jar file
		Class<ChemicalComponentInfo> myClass = ChemicalComponentInfo.class;
		try {
			is = myClass.getResource(fileName).openStream();
			
			if ( is == null ) {
				Status.output(Status.LEVEL_WARNING, "ChemicalComponentInfo: cannot find: " + fileName);
				return;
			}
			
			InputStreamReader isr = new InputStreamReader( is );
			br = new BufferedReader( isr );
		} catch (FileNotFoundException e) {
			Status.output(Status.LEVEL_WARNING, "ChemicalComponentInfo: cannot find: " + fileName);
			return;
		} catch (IOException e) {
			Status.output(Status.LEVEL_WARNING, "ChemicalComponentInfo: cannot find: " + fileName);
			return;
		}
		
		DelimitedFileReader reader = new DelimitedFileReader(br, "\t", false);
		reader.setTrimWhiteSpace(true);
		try {
			chemicalComponentInfo = reader.getData();
		} catch (IOException e) {
			Status.output(Status.LEVEL_WARNING, "ChemicalComponentInfo: cannot read infromation: " + fileName);
		}
	}
	
	private static void createHashMaps() {
		chemicalComponentType = new HashMap<String, ChemicalComponentType>();
		
		for (List<String> items: chemicalComponentInfo) {
			String name = items.get(0); // Chemical Component ID
			String type = items.get(1); // Chemical Component Type
			ChemicalComponentType cType = ChemicalComponentType.getChemicalComponentType(type);
			chemicalComponentType.put(name, cType);
			
			// count the number of parent residues for nonstandard residues
			int count = items.size() - 2;
			for (int i = 2; i < items.size(); i++) {
				if (items.get(i).length() == 0) {
					count--;
				}
			}
			chemicalComponentParentCount.put(name, count);
		}	
		
		// add water as a special component
		chemicalComponentType.put("HOH", ChemicalComponentType.WATER);
		chemicalComponentParentCount.put("HOH", 0);
		
		// create special hash map for standard amino acids
		for (AminoAcid a: stdAminoAcids) {
			aminoAcids.put(a.code, a);
		}
	}
	
}
