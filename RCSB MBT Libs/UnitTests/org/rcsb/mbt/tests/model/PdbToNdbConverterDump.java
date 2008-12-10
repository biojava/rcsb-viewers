package org.rcsb.mbt.tests.model;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import org.rcsb.demo.MBT.SimpleReadStructureDemo;
import org.rcsb.mbt.model.util.PdbToNdbConverter;

public class PdbToNdbConverterDump extends SimpleReadStructureDemo
{
	private class AccessiblePdbToNdbConverter extends PdbToNdbConverter
	{
		public HashMap<String, HashMap<String,Object[]>> getByPdbIdsMap() { return byPdbIds; }
		public HashMap<String, HashMap<Integer,Object[]>> getByNdbIdsMap() { return byNdbIds; }
	}

	@Override
	public void doDump(final String fileName, PrintStream ps) throws IOException
	{
		loadStructure(fileName, ps);
		
		AccessiblePdbToNdbConverter conv = (AccessiblePdbToNdbConverter)struct.getStructureMap().getPdbToNdbConverter();
		
		output.lineOut("Dumping PDB IDs");
		output.indent();
		String buf;
		
		//
		// First, dump the 'byPdbIds' table - (pdbIds->ndbIds)
		//
		HashMap<String, HashMap<String,Object[]>> byPdbIds = conv.getByPdbIdsMap();
		for (String pdbChainId : byPdbIds.keySet())
		{
			output.lineOut("Pdb Chain Id: " + pdbChainId);
			HashMap<String,Object[]> pdbResIdMap = byPdbIds.get(pdbChainId);
			for (String pdbResId : pdbResIdMap.keySet())
			{
				buf = "Pdb Residue Id: " + pdbResId + " --> ";
				Object ndbIds[] = pdbResIdMap.get(pdbResId);
				if (ndbIds == null)
					buf += "No NDB IDS";
				
				else 
				{
					buf += "NdbChainId: " + (ndbIds[0] == null? "(NONE)" : ndbIds[0]) + ", ";
					buf += "NdbResidueId: " + (ndbIds[1] == null? "(NONE)" : ndbIds[1]);
				}
				
				output.lineOut(buf);
			}
			
			
			output.lineOut("");
		}
		
		//
		// Next, dump the 'byNdbIds' table - (ndbIds->pdbIds)
		//
		HashMap<String, HashMap<Integer,Object[]>> byNdbIds = conv.getByNdbIdsMap();
		for (String pdbChainId : byNdbIds.keySet())
		{
			output.lineOut("Ndb Chain Id: " + pdbChainId);
			HashMap<Integer,Object[]> ndbResIdMap = byNdbIds.get(pdbChainId);
			for (Integer ndbResId : ndbResIdMap.keySet())
			{
				buf = "Ndb Residue Id: " + ndbResId + " --> ";
				Object pdbIds[] = ndbResIdMap.get(ndbResId);
				if (pdbIds == null)
					buf += "No PDB IDS";
				
				else 
				{
					buf += "PdbChainId: " + (pdbIds[0] == null? "(NONE)" : pdbIds[0]) + ", ";
					buf += "PdbResidueId: " + (pdbIds[1] == null? "(NONE)" : pdbIds[1]);
				}
				
				output.lineOut(buf);
			}
			
			
			output.lineOut("");
		}
	}
}

