package org.rcsb.demo.MBT;

//Copyright 2000-2008 The Regents of the University of California.
//All Rights Reserved.
//
//Permission to use, copy, modify and distribute any part of this
//Molecular Biology Toolkit (MBT)
//for educational, research and non-profit purposes, without fee, and without
//a written agreement is hereby granted, provided that the above copyright
//notice, this paragraph and the following three paragraphs appear in all
//copies.
//
//Those desiring to incorporate this MBT into commercial products
//or use for commercial purposes should contact the Technology Transfer &
//Intellectual Property Services, University of California, San Diego, 9500
//Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815,
//FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
//
//IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
//DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//LOST PROFITS, ARISING OUT OF THE USE OF THIS MBT, EVEN IF THE
//UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.//
//THE MBT PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
//UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
//UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
//NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
//EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
//MBT WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
//
//For further information, please see:  http://mbt.sdsc.edu

import org.rcsb.demo.utils.Output;
import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.geometry.ModelTransformationMatrix;
import org.rcsb.mbt.structLoader.*;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import java.util.Vector;

/**
 * This first demo will read a structure file (.pdb{.gz} or .xml{.gz} specified from the command line)
 * and create a structure, then traverse the structure by chain and output some interesting
 * information about the structure components.
 * 
 * (This is just one way you can access the structure and is the most straightforward
 * method.)
 * <p>
 * This is the minimal project you can create with the MBT suite.  It uses only the following
 * packages:</p>
 * <dl>
 * <dt>{@link org.rcsb.mbt.model}</dt>
 * <dd>
 * This is the set of model packages that describe the structure model.</dd>
 * 
 * <dt>{@link org.rcsb.mbt.structLoader}</dt>
 * <dd>
 * This is the set of packages used to load structure files.</dd>
 * </dl>
 * @author rickb
 *
 */
public class SimpleReadStructureDemo
{
	static boolean isPdb = false;
	static boolean doAtoms = true;
	
	/**
	 * Here's the main...
	 * 
	 * @param args
	 */
	public static void main(String args[])
	{
		String fileName = null;
		
		for (String arg : args)
		{
			if (arg.equals("-noatoms"))
				doAtoms = false;
			
			else
				fileName = arg;
		}
		
		if (fileName == null)
		{
			System.err.println("Error: you need to specify a structure file or URL.");
			System.exit(1);
		}

		
		File file = new File(fileName);
		
		if (file == null || !file.exists())
		{
			System.err.println("Error: can't open file '" + fileName + "'");
			System.exit(2);
		}
			
		String fnTmp = fileName.endsWith(".gz")? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
		String sfx = fnTmp.substring(fnTmp.lastIndexOf('.') + 1);
		isPdb = sfx.equalsIgnoreCase("pdb");
							// get the true suffix - pdb or xml
		
		IFileStructureLoader loader = null;
		
		Structure struct = null;
		
		if (isPdb)			
			loader = new PdbStructureLoader();
		
		else if (sfx.equalsIgnoreCase("xml"))
			loader = new XMLStructureLoader(new StructureXMLHandler(fileName));
			
		else
		{
			System.err.print("Error: a '.pdb' or '.xml' url is expected.");
			System.exit(3);
		}

		try
		{
			struct = loader.load(file);
								// load up the structures
		}
		
		catch (IOException e)
		{
			System.err.print("Error: " + e.getMessage());
		}		
		
		if (struct == null)
		{
			System.err.println("Error: could not read structure from '" + fileName + "'");
			System.exit(4);
		}
		
		new StructureMap(struct, null);
							// create a structuremap associated with the structure.
							// the second argument is for user data.
		
		reportChains(struct);
							// and output the report
		
		reportTransforms(loader, struct);
		
		System.exit(0);
	}
	
	/**
	 * Walk the list of chains, outputting the internals of each chain
	 * 
	 * @param struct
	 */
	static private void reportChains(Structure struct)
	{
		StructureMap structMap = struct.getStructureMap();
		StructureInfo structInfo = struct.getStructureInfo();
		TreeMap<String, Chain> auxChains = extractAuxilliaries(structMap);
		
		Output.lineOut("Url : " + struct.getUrlString());
		
		/*
		 * output the structureInfo - haven't seen this filled by anything, yet.
		 */
		if (structInfo == null)
			Output.lineOut("(No StructureInfo struct - skipping Structure Info section)");
		else
		{
			Output.lineOut("Structure Info:");
			Output.incrementIndent();
			Output.lineOut("Long Name : " + structInfo.getLongName());
			Output.lineOut("Short Name : " + structInfo.getShortName());
			Output.lineOut("Release Date : " + structInfo.getReleaseDate());
			Output.lineOut("Pdb ID : " + structInfo.getIdCode());
			Output.lineOut("Authors:" + structInfo.getAuthors());
			Output.decrementIndent();
		}
		
		Output.lineOut("");
		
		/*
		 * output the chain info using the structure map
		 */
		if (structMap == null)
			Output.lineOut("(No StructureMap - skipping Structure Map section)");
		
		else
		{
			Output.lineOut("StructureMap Info:");
			Output.incrementIndent();
			Output.lineOut("Atom Count : " + structMap.getAtomCount());
			Output.lineOut("Bond Count : " + structMap.getBondCount());
			Output.lineOut("Residue Count : " + structMap.getResidueCount());
			Output.lineOut("Fragment Count : " + structMap.getFragmentCount());
			Output.lineOut("Chain Count : " + structMap.getChainCount());
			Output.lineOut("Aux Chain Count : " + auxChains.size());
			Output.decrementIndent();
			
			Output.lineOut("");
			Output.lineOut("Detail by chain:");
			Output.incrementIndent();
			
			for (Chain chain : structMap.getChains())
				outputChainInfo(chain, null);
				
			Output.decrementIndent();
			Output.lineOut("--End StructureMap--");
		}
		
		if (isPdb)
		{
			if (auxChains.size() > 0)
			{
				Output.lineOut("");
				Output.lineOut("Auxilliary Chains (pdb file) (psuedo-ids):");
				Output.incrementIndent();
				
				for (String key : auxChains.keySet())
					outputChainInfo(auxChains.get(key), key);
				
				Output.decrementIndent();
			}
		}
		
		Output.lineOut("");
	}
	
	/**
	 * Traverse the contents of the chain and output.
	 * 
	 * @param chain - the chain to traverse
	 * @param auxChainId - if this is an auxilliary chain, this is the name.  Otherwise null.
	 */
	static private void outputChainInfo(Chain chain, String auxChainId)
	{
		boolean auxChain = auxChainId != null;
		
		String chainId = (auxChain)? auxChainId : chain.getChainId();
		if (auxChain)
			Output.lineOut("Pseudo Chain Id : " + chainId);
		
		else
			Output.lineOut("Chain Id : " + chainId);
		
		if (chainId.equals("_"))
						// don't output the default chain id - if it contains ions/ligands,
						// they'll be extracted and displayed elsewhere.
						// otherwise, it just shows waters - we don't care.
		{
			Output.incrementIndent();
			Output.lineOut("(default chain id group)");
			Output.decrementIndent();
			return;
		}
		
		Output.incrementIndent();
		if (!auxChain)
						// auxilliary chains don't have fragments - put out fragment
						// info for normal chain
		{
			Output.lineOut("Fragments: ");
			Output.incrementIndent();
			
			for (Fragment fragment : chain.getFragments())
			{
				Output.lineOut("--New Fragment--");
				Output.lineOut("Conformation Type : " + fragment.getConformationType());
				Output.lineOut("Residues:");
				Output.incrementIndent();
				
				outputResidueInfo(fragment.getResidues());
					
				Output.decrementIndent();
				Output.lineOut("--End Fragment--");
			}
			
			Output.decrementIndent();
			Output.lineOut("--End Fragments--");
		}
		
		else	// auxChain - no fragments
			outputResidueInfo(chain.getResidues());
		
		Output.decrementIndent();
		Output.lineOut("--End Chain--");
	}
	
	/**
	 * Walk the residue list, either from the Fragment or the Chain, and output internals
	 * 
	 * @param residues
	 */
	static private void outputResidueInfo(Vector<Residue> residues)
	{
		for (Residue residue : residues)
		{
			if (residue.getClassification() == Residue.Classification.WATER)
				continue;
							// let's ignore waters
			
			Output.lineOut("Residue Id : " + residue.getResidueId());
		    Output.lineOut("Compound Code : " + residue.getCompoundCode());
		    Output.lineOut("Hydrophobicity : " + residue.getHydrophobicity());
		    Output.lineOut("Num Atoms : " + residue.getAtomCount());
		    if (doAtoms)
		    {
		    	Output.lineOut("Atoms:");
		    	Output.incrementIndent();

			    for (Atom atom : residue.getAtoms())
			    {
			    	Output.lineOut("Atom Name : " + atom.name);
			    	Output.lineOut("Element : " + atom.element);
			    	Output.lineOut("Partial Charge : " + atom.partialCharge);
			    	Output.lineOut("Occupancy : " + atom.occupancy);
			    	Output.lineOut("");
			    }
			    
			    Output.decrementIndent();
		    }

		    Output.lineOut("--End Residue");
		}
	}
	
	/**
	 * PDB files will typically contain a number of records that don't specify
	 * chains.  They specify ligands, ions and waters.  The pdb loader accumulates
	 * these into a default chain with an id of "_".
	 * 
	 * So, to break them out (particularly the ligands and ions), we run through
	 * the default chain and extract them.
	 * 
	 * Note 1: this functionality is likely to get implemented in the loader, in the
	 * future.
	 * 
	 * Note 2: in the xml file, the records typically have secondary identifier.  The
	 * XML loader uses that to break out into individual chains.
	 * 
	 * @param structMap
	 * @return
	 */
	static private TreeMap<String, Chain> extractAuxilliaries(StructureMap structMap)
	{
		TreeMap<String, Chain> auxChains = new TreeMap<String, Chain>();
		
		Chain defaultChain = structMap.getChain("_");
		if (defaultChain != null)
		{
			for (Residue residue : defaultChain.getResidues())
			{
				if (residue.getClassification() == Residue.Classification.WATER)
					continue;
								// ignore waters
				String key = residue.getCompoundCode() + " (" + residue.getResidueId() + ")";
				Chain auxChain = auxChains.get(key);
				if (auxChain == null)
				{
					auxChain = new Chain();
					auxChains.put(key, auxChain);
				}
				
				auxChain.addResidue(residue);
			}
		}
		return auxChains;
	}
	
	static private void reportTransforms(IStructureLoader loader, Structure struct)
	{
		int txIX;
		
		Output.lineOut("Unit Cell:");
		Output.incrementIndent();
		
		if (loader.hasUnitCell())
		{
			UnitCell unitCell = loader.getUnitCell();
			Output.lineOut("angleAlpha: " + unitCell.angleAlpha);
			Output.lineOut("angleBeta: " + unitCell.angleBeta);
			Output.lineOut("angleGamma: " + unitCell.angleGamma);
			Output.lineOut("length A: " + unitCell.lengthA);
			Output.lineOut("length B: " + unitCell.lengthB);
			Output.lineOut("length C: " + unitCell.lengthC);
		}
		
		else
			Output.lineOut("(None)");
		
		Output.decrementIndent();
		Output.lineOut("");
		
		Output.lineOut("Biological Unit Transforms:");
		Output.incrementIndent();

		if (loader.hasBiologicUnitTransformationMatrices())
		{
			txIX = 1;
			for (ModelTransformationMatrix modelTransform : loader.getBiologicalUnitTransformationMatrices())
				outputTransformValues(txIX++, modelTransform.values);
		}
		
		else
			Output.lineOut("(None)");
		
		Output.decrementIndent();
		Output.lineOut("");
		
		Output.lineOut("Non-Crystallographic Transforms:");
		Output.incrementIndent();
		if (loader.hasNonCrystallographicOperations())
		{
			txIX = 1;
			for (ModelTransformationMatrix modelTransform : loader.getNonCrystallographicOperations())
				outputTransformValues(txIX++, modelTransform.values);
		}
		
		else
			Output.lineOut("(None)");
	}
	
	static private void outputTransformValues(int ix, float[] values)
	{
		Output.lineOut(ix + ": " + values[0] + ", " + values[1] + ", " + values[2] + ", " + values[3] + ",");
		Output.lineOut(" : " + values[4] + ", " + values[5] + ", " + values[6] + ", " + values[7] + ",");
		Output.lineOut(" : " + values[8] + ", " + values[9] + ", " + values[10] + ", " + values[11] + ",");
		Output.lineOut(" : " + values[12] + ", " + values[13] + ", " + values[14] + ", " + values[15]);
		Output.lineOut("");
	}

}