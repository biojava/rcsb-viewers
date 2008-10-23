package org.rcsb.mbt.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/*
 * Previously called: pdbChain
 * <p>
 * Appears to be used to carry external out-of-structure chains.
 * (as opposed to {@linkplain org.rcsb.mbt.model.Chain}, which is holds in-structure chains.)</p>
 * <p>
 * This was in the viewers and I've pushed it down into the MBT.  It appears to be lighter weight than
 * the Chain class.  I'm not sure if it can't ultimately either be derived from Chain or if Chain can't
 * be derived from it.  It also seems to have something to do with the mutator mechanism, so I'm hesitant
 * to blow it off or simply derive it out of Chain, initially.</p>
 * <p>
 * Note also the presence of a residues set and a residues vector and the mbt chain set.  Suggests this
 * can hold arbitrary lists of residues that cut across MBT Chain Boundaries.</p>
 * <p>
 * So, I've upgraded it to 1.5 spec and taken two formerly identical classes
 * ({@linkplain org.rcsb.mbt.model.WaterChain} and {@linkplain org.rcsb.mbt.model.MiscellaneousModelChain})
 * and derived them from this.  Better to fold them into a single class and provide an indicator...</p>
 * 
 * @author rickb
 *
 */
public class ExternChain extends StructureComponent
{
	public enum ExternChainType { BASIC, WATER, MISCELLANEOUS }
	
    private Set<Residue> residuesSet = null;
    private Vector<Residue> residuesVec = null;	// equivalent to Chain residues
    private Set<Chain> mbtChains = null;	// unique list of Chains (no values)

    private ExternChainType chainType = null;
    private String chainId = null;
    
    public boolean isWaterChain() { return chainType == ExternChainType.WATER; }
    public boolean isBasicChain() { return chainType == ExternChainType.BASIC; }
    public boolean isMiscellaneousChain() { return chainType == ExternChainType.MISCELLANEOUS; }
    
    /*
     * public factory methods - insures creation of type requested.
     * note the constructor is private.
     */
    static public ExternChain createBasicChain(final String pdbChainId, final Vector<Residue> residues)
    {
    	return new ExternChain(ExternChainType.BASIC, pdbChainId, residues);
    }
    
    static public ExternChain createWaterChain(final Vector<Residue> residues)
    {
    	return new ExternChain(ExternChainType.WATER, "HOH", residues);
    }
    
    static public ExternChain createMiscellaneousMoleculeChain(final Vector<Residue> residues)
    {
    	return new ExternChain(ExternChainType.MISCELLANEOUS, "_", residues);
    }
    
    /*
     * Private constructors
     */
    private ExternChain(ExternChainType type, final String in_chainId, final Vector<Residue> residues)
    {
    	setResidues(residues);
    	chainType = type;
    	chainId = in_chainId;
    }
    
	private void setResidues(final Vector<Residue> residues)
	{
		residuesVec = residues;
		residuesSet = new HashSet<Residue>();
		mbtChains = new HashSet<Chain>();
		
		if (residues.size() != 0)
		{
			super.structure = (residues.get(0)).structure;

			for ( Residue r : residues)
			{
				residuesSet.add(r);
				mbtChains.add(r.structure.getStructureMap().getChain(r.getChainId()));
			}			
		}
	}
	   
	@Override
	public void copy(final StructureComponent structureComponent)
	{
		final ExternChain sc = (ExternChain)structureComponent;
		sc.chainId = chainId;
		sc.chainType = sc.chainType;
		sc.residuesSet.clear();
		sc.residuesSet.addAll(residuesSet);
		sc.residuesVec.clear();
		sc.residuesVec.addAll(residuesVec);
		sc.mbtChains.clear();
		sc.mbtChains.addAll(mbtChains);
						// I'm presuming these are copied rather than just assigned for a reason...
						// Also flushed it out so it does a complete copy
						// 10-22-08 - rickb
	}
	
	public ExternChainType getExternChainType() { return chainType; }
	public String getChainId()
	{
		return chainId;
	}
	
	@Override
	public String getStructureComponentType() { return chainType.name() + " Chain"; }
	public Iterator<Chain> getMbtChainIterator() { return mbtChains.iterator(); }
	public Residue getResidue(final int index) { return this.residuesVec.get(index); }
	public int getResidueCount() { return this.residuesSet.size(); }
//	public Iterator<Residue> getResidueIterator() { return this.residuesVec.iterator(); }
	public boolean contains(final Residue r) { return this.residuesSet.contains(r); }
	public Vector<Residue> getResiduesVec() { return this.residuesVec; }	
	public Set<Chain> getMbtChains() { return mbtChains; }
}
