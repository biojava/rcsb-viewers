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
 * Created on 2008/12/22
 *
 */ 
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
