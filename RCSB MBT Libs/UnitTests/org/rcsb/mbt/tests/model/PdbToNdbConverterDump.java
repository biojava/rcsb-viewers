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

