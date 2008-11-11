package org.rcsb.mbt.tests.model;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.rcsb.demo.MBT.SimpleReadStructureDemo;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.util.DebugState;

/**
 * This is an extension of the SimpleReadStructureDemo - it amplifies that with more
 * information suitable for testing.
 * 
 * @author rickb
 *
 */
final class SimpleReadStructureDump extends SimpleReadStructureDemo
{
	SimpleReadStructureDump() { DebugState.setDebugState(true); }
	
	@Override
	public void doDump(String fileName, PrintStream out) throws IOException
	{
		super.doDump(fileName, out);
		reportCounts(struct);
		reportCountDiscrepancies();
	}
	
	@Override
	protected void reportBonds(Structure struct)
	{
		super.reportBonds(struct);
		
		StructureMap sm = struct.getStructureMap();
		
		output.lineOut("");
		output.lineOut("CalculatedBonds:");
		output.indent();
		TreeMap<String, Vector<Bond>> calcBondmap = sm.getCalculatedBonds();
		if (calcBondmap == null || calcBondmap.size() == 0)
			output.lineOut("(NONE)");
		
		else
			for (String compoundCode : calcBondmap.keySet())
			{
				output.lineOut(compoundCode);
				output.indent();
				
				if (calcBondmap.size() > 0)
					for (Bond bond : calcBondmap.get(compoundCode))
						output.lineOut(getBondLine(bond));
				
				else
					output.lineOut("(NONE added)");
				
				output.outdent();
				output.lineOut("");
			}
		
		output.outdent();
		output.lineOut("");
	}
	
	private boolean chainCountsAgree, fragmentCountsAgree, residueCountsAgree, atomCountsAgree, bondCountsAgree;

	/**
	 * Output the number of counts reported versus those actually encountered
	 * 
	 * @param struct
	 */
	protected void reportCounts(Structure struct)
	{
		StructureMap structMap = struct.getStructureMap();
		output.lineOut("Summary Count Checks:");
		output.indent();
		
		int reportedCount = structMap.getChainCount();
		chainCountsAgree = chainsCounted == reportedCount;
		String buf = "Number Chains Agree: " + (chainCountsAgree? "Y" : "N");
		if (!chainCountsAgree) buf += " (Reported: " + reportedCount + ", Counted: " + chainsCounted + ")";
		output.lineOut(buf);
		
		reportedCount = structMap.getFragmentCount();
		fragmentCountsAgree = fragmentsCounted == reportedCount;
		buf = "Number Fragments Agree: " + (fragmentCountsAgree? "Y" : "N");
		if (!fragmentCountsAgree) buf += " (Reported: " + reportedCount + ", Counted: " + fragmentsCounted + ")";
		output.lineOut(buf);
		
		reportedCount = structMap.getResidueCount();
		residueCountsAgree = residuesCounted == reportedCount;
		buf = "Number Residues Agree: " + (residueCountsAgree? "Y" : "N");
		if (!residueCountsAgree) buf += " (Reported: " + reportedCount + ", Counted: " + residuesCounted + ")";
		output.lineOut(buf);			
	
		reportedCount = struct.getStructureMap().getAtomCount();
		atomCountsAgree = atomsCounted == reportedCount;
		buf = "Number Atoms Agree: " + (atomCountsAgree? "Y" : "N");
		if (!atomCountsAgree) buf += " (Reported: " + reportedCount + ", Counted: " + atomsCounted + ")";
		output.lineOut(buf);			

		reportedCount = struct.getStructureMap().getBondCount();
		bondCountsAgree = bondsCounted == reportedCount;
		buf = "Number Bonds Agree: " + (bondCountsAgree? "Y" : "N");
		if (!bondCountsAgree) buf += " (Reported: " + reportedCount + ", Counted: " + bondsCounted + ")";
		output.lineOut(buf);			

		output.outdent();
	}
	
	protected void reportCountDiscrepancies()
	{
		if (!fragmentCountsAgree)
			reportFragmentCountDiscrepancies();
	}
	
	protected void reportFragmentCountDiscrepancies()
	{
		output.lineOut("Fragment Count Discrepancies");
		output.indent();
		
		Set<Fragment> setReported = new HashSet<Fragment>();
		Set<Fragment> setEncountered = new HashSet<Fragment>();
		StructureMap sm = struct.getStructureMap();
		
		setReported.addAll(sm.getFragments());
		
		for (Chain chain : sm.getChains())
			setEncountered.addAll(chain.getFragments());
		
		output.lineOut("Fragments reported from StructureMap not in Encountered:");
		output.indent();
		for (Fragment fragReported : setReported)
			if (!setEncountered.contains(fragReported))
				output.lineOut(fragReported.getConformationType() + ": C_ID - " + fragReported.getChain().getChainId());
				
		output.outdent();
		output.lineOut("");
		output.lineOut("Fragments Encountered but not reported in StructureMap:");
		output.indent();
		for (Fragment fragEncountered : setEncountered)
			if (!setReported.contains(fragEncountered))
				output.lineOut(fragEncountered.getConformationType() + ": C_ID - " + fragEncountered.getChain().getChainId());
		
		output.outdent();
		
		output.outdent();
		output.lineOut("");
	}
	
	
	/*
	 * Allows invocation from the commandline
	 */
	public static void main(String args[]) throws IOException
	{
		SimpleReadStructureDump dumper = new SimpleReadStructureDump();
		dumper.doDump(args[0], System.out);
	}
}
