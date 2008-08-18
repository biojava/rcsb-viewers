package org.rcsb.mbt.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;


/**
 * So, the question is, "What's the difference between this and 'Chain' - why is there a special break, and should it replace 'Chain'
 * in the MBT model?
 * 
 * TODO - resolve PDBChain and Chain.  Also, look at WaterChain and see what that has to do with things.
 * 
 * @author rickb
 *
 */
public class PdbChain extends StructureComponent {
	/* Data structure:
	 *		HashMap residues {
	 *			key: Residue r
	 *			value: null
	 *		}
	 */
    private HashMap residuesMap;
    private Vector residuesVec = null;	// exactly the same as 'residues'.
    private HashMap mbtChains = null;	// unique list of Chains (no values)
    public String pdbChainId = null;

	
	public void copy(final StructureComponent structureComponent) {
		final PdbChain sc = (PdbChain)structureComponent;
		sc.residuesMap = (HashMap)this.residuesMap.clone();
	}

	
	public String getStructureComponentType() {
		return "Water Chain";
	}
	
	public Iterator getResidueIterator() {
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
