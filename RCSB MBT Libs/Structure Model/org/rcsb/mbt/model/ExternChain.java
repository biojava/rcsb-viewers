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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;

/*
 * Previously called: pdbChain
 * <p>
 * Appears to be used to carry external out-of-structure chains.
 * (as opposed to {@linkplain org.rcsb.mbt.model.Chain}, which is holds in-structure chains.)</p>
 * <p>
 * This was in the viewers and I've pushed it down into the MBT.  It appears to be lighter weight than
 * the Chain class.  I'm not sure if it can't ultimately either be derived from Chain or if Chain can't
 * be derived from it.  It also seems to have something to do with the mutator mechanism, so I'm hesitant
 * to blow it off or simply derive it out of Chain, initially without further study.</p>
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
	public ComponentType getStructureComponentType() { return ComponentType.CHAIN; }
	public Iterator<Chain> getMbtChainIterator() { return mbtChains.iterator(); }
	public Residue getResidue(final int index) { return this.residuesVec.get(index); }
	public int getResidueCount() { return this.residuesSet.size(); }
//	public Iterator<Residue> getResidueIterator() { return this.residuesVec.iterator(); }
	public boolean contains(final Residue r) { return this.residuesSet.contains(r); }
	public Vector<Residue> getResiduesVec() { return this.residuesVec; }	
	public Set<Chain> getMbtChains() { return mbtChains; }
}
