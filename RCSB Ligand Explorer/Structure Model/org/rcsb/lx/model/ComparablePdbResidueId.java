package org.rcsb.lx.model;

/**
 * Wrapper that can be inserted into data structures like TreeMaps for comparing and sorting pdb residue ids. Pdb residue ids are numbers with, optionally, one or more letters at the end. For example, 23 would come before 23A when sorted using this class. 
 * @author John Beaver
 *
 */
public class ComparablePdbResidueId implements Comparable {
	public ComparablePdbResidueId(final String pdbResidueId) {
		this.pdbResidueId = pdbResidueId;
	}
	
	public String pdbResidueId;

	public int compareTo(final Object o) {
		final ComparablePdbResidueId r = (ComparablePdbResidueId)o;
		
		try {
			int numberPartOther = -1;
			String stringPartOther = null;
			int numberPartThis = -1;
			String stringPartThis = null;
			
			int firstLetterOther = -1;
			for(int i = 0; i < r.pdbResidueId.length(); i++) {
				if(Character.isLetter(r.pdbResidueId.charAt(i))) {
					firstLetterOther = i;
					break;
				}
			}
			
			if(firstLetterOther >= 0) {
				numberPartOther = Integer.parseInt(r.pdbResidueId.substring(0, firstLetterOther));
				stringPartOther = r.pdbResidueId.substring(firstLetterOther);
			} else {
				numberPartOther = Integer.parseInt(r.pdbResidueId);
			}
			
			int firstLetterThis = -1;
			for(int i = 0; i < this.pdbResidueId.length(); i++) {
				if(Character.isLetter(this.pdbResidueId.charAt(i))) {
					firstLetterThis = i;
					break;
				}
			}
			
			if(firstLetterThis >= 0) {
				numberPartThis = Integer.parseInt(this.pdbResidueId.substring(0, firstLetterThis));
				stringPartThis = this.pdbResidueId.substring(firstLetterThis);
			} else {
				numberPartThis = Integer.parseInt(this.pdbResidueId);
			}
			
			if(numberPartThis == numberPartOther) {
				if(stringPartThis == null) {
					if(stringPartOther == null) {
						return 0;
					}
					
					return -1;
				}
				
				if(stringPartOther == null) {
					return 1;
				}
				
				return stringPartThis.compareTo(stringPartOther);
			}
			
			return numberPartThis - numberPartOther;
		} catch(final Exception e) {
			System.err.println(e.getMessage() + ": in ContactMap_ContactingResiduesPane.ResidueIdString while comparing " + this.pdbResidueId + " to " + r.pdbResidueId);
		}
		
		// continuing from the Exception...
		return this.pdbResidueId.compareTo(r.pdbResidueId);
	}
}
