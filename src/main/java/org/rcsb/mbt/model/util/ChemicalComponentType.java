package org.rcsb.mbt.model.util;

public enum ChemicalComponentType {

	DNA_LINKING ("DNA LINKING"),
	DNA_3PRIME_TERMINUS ("DNA OH 3 PRIME TERMINUS"),
	D_PEPTIDE_LINKING ("D-PEPTIDE LINKING"),
	D_PEPTIDE_N_TERMINUS ("D-PEPTIDE NH3 AMINO TERMINUS"),
	D_SACCHARIDE ("D-SACCHARIDE"),
	D_SACCHARIDE_14_LINKING ("D-SACCHARIDE 1,4 AND 1,4 LINKING"),
	L_DNA_LINKING ("L-DNA LINKING"),
	L_PEPTIDE_C_TERMINUS ("L-PEPTIDE COOH CARBOXY TERMINUS"),
	L_PEPTIDE_LINKING ("L-PEPTIDE LINKING"),
	L_PEPTIDE_N_TERMINUS ("L-PEPTIDE NH3 AMINO TERMINUS"),
	L_SACCARIDE ("L-SACCHARIDE"),
	L_SACCHARIDE_14_LINKING ("L-SACCHARIDE 1,4 AND 1,4 LINKING"),
	NON_POLYMER ("NON-POLYMER"),
	PEPTIDE_LINKING ("PEPTIDE LINKING"),
	PEPTIDE_LIKE ("PEPTIDE-LIKE"),
	RNA_LINKING ("RNA LINKING"),
	L_RNA_LINKING ("L-RNA LINKING"),
	RNA_3PRIME_TERMINUS ("RNA OH 3 PRIME TERMINUS"),
	SACCHARIDE ("SACCHARIDE"),
	WATER ("WATER"); // note, WATER is not at type in the Chem. Comp. Dictionary. It was added for convenience.

	private final String name;
	
	/**
	 * Creates a Chemical Component Type object with the given name from the Chemical Component Dictionary
	 * @param name name of Chemical Component Type
	 */
	ChemicalComponentType(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the name of the Chemical Component Type used in Chemical Component Dictionary
	 * @return name of Chemical Component Type
	 */
	public String getName() {
		return name;
	}
	
	/** 
	 * Returns true if Chemical Component Type is for a nucleotide
	 * @return true if nucleotide
	 */
	public boolean isNucleotide() {
		return this.equals(DNA_LINKING) ||
		this.equals(DNA_3PRIME_TERMINUS) ||
		this.equals(L_DNA_LINKING) ||
		this.equals(RNA_LINKING) ||
		this.equals(L_RNA_LINKING) ||
		this.equals(RNA_3PRIME_TERMINUS);
	} 
	
	/**
	 * Returns true if Chemical Component Type is for a peptide
	 * @return true if peptide
	 */
	public boolean isPeptide() {
		return this.equals(D_PEPTIDE_LINKING) ||
		this.equals(D_PEPTIDE_N_TERMINUS) ||
		this.equals(L_PEPTIDE_C_TERMINUS) ||
		this.equals(L_PEPTIDE_LINKING) ||
		this.equals(L_PEPTIDE_N_TERMINUS) ||
		this.equals(PEPTIDE_LINKING) ||
		this.equals(PEPTIDE_LIKE);
	} 
	
	/**
	 * Returns true if Chemical Component Type is for a saccharide
	 * @return true if saccharide
	 */
	public boolean isSaccharide() {
		return this.equals(D_SACCHARIDE) ||
		this.equals(D_SACCHARIDE_14_LINKING) ||
		this.equals(L_SACCARIDE) ||
		this.equals(L_SACCHARIDE_14_LINKING) ||
		this.equals(SACCHARIDE);
	} 
	
	/**
	 * Returns true if Chemical Component Type is for a non-polymer
	 * @return true if non-polymer
	 */
	public boolean isNonPolymer() {
		return this.equals(NON_POLYMER) || this.equals(PEPTIDE_LIKE);
	} 
	
	/**
	 * Returns true if Chemical Component Type is WATER
	 * @return true if water
	 */
	public boolean isWater() {
		return this.equals(WATER);
	} 
	
	/**
	 * Return the Chemical Component Type given the name from the Chemical Component Dictionary
	 * @param name
	 * @return Chemical Component
	 */
	public static ChemicalComponentType getChemicalComponentType(String name) {
		for (ChemicalComponentType t: ChemicalComponentType.values()) {
			if (t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		if (name.equalsIgnoreCase("HOH")) {
			return WATER;
		}
		return NON_POLYMER;
	}
}
