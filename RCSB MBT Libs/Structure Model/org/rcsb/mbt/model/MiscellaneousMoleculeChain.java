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
package org.rcsb.mbt.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


public class MiscellaneousMoleculeChain extends StructureComponent {
	/* Data structure:
	 *		HashMap residues {
	 *			key: Residue r
	 *			value: null
	 *		}
	 */
    private HashMap residuesMap;
    private Vector residuesVec = null;	// exactly the same as 'residues'.
    private HashMap mbtChains = null;	// unique list of Chains (no values)

	
	public void copy(final StructureComponent structureComponent) {
		final MiscellaneousMoleculeChain sc = (MiscellaneousMoleculeChain)structureComponent;
		sc.residuesMap = (HashMap)this.residuesMap.clone();
	}

	
	public String getStructureComponentType() {
		return "Water Chain";
	}
	
	public Iterator getIterator() {
		return this.residuesVec.iterator();
	}
	
	public void setResidues(final Vector residues) {
		this.residuesVec = residues;
		this.residuesMap = new HashMap();
		this.mbtChains = new HashMap();
		
		final Iterator it = residues.iterator();
		while(it.hasNext()) {
			final Residue r = (Residue)it.next();
			this.residuesMap.put(r, null);
			
			this.mbtChains.put(r.structure.getStructureMap().getChain(r.getChainId()), null);
		}
		
		if(residues.size() != 0) {
			super.structure = ((Residue)residues.get(0)).structure;
		}
	}
	
	public Iterator getResidueIterator() {
		return this.residuesVec.iterator();
	}
	
	public Iterator getMbtChainIterator() {
		return this.mbtChains.keySet().iterator();
	}
	
	public boolean contains(final Residue r) {
		return this.residuesMap.containsKey(r);
	}
	
	public Residue getResidue(final int index) {
		return (Residue)this.residuesVec.get(index);
	}
	
	public int getResidueCount() {
		return this.residuesMap.size();
	}
	
	public Vector getResiduesVec() {
		return this.residuesVec;
	}
}
