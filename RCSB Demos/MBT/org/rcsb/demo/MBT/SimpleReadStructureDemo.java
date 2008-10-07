package org.rcsb.demo.MBT;

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
 * <dt>{@link org.rcsb.mbt.model.*}</dt>
 * <dd>
 * This is the set of model packages that describe the structure model.</dd>
 * 
 * <dt>{@link org.rcsb.mbt.structLoader.*}</dt>
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
		
		lineOut("Url : " + struct.getUrlString());
		
		/*
		 * output the structureInfo - haven't seen this filled by anything, yet.
		 */
		if (structInfo == null)
			lineOut("(No StructureInfo struct - skipping Structure Info section)");
		else
		{
			lineOut("Structure Info:");
			incrementIndent();
			lineOut("Long Name : " + structInfo.getLongName());
			lineOut("Short Name : " + structInfo.getShortName());
			lineOut("Release Date : " + structInfo.getReleaseDate());
			lineOut("Pdb ID : " + structInfo.getIdCode());
			lineOut("Authors:" + structInfo.getAuthors());
			decrementIndent();
		}
		
		lineOut("");
		
		/*
		 * output the chain info using the structure map
		 */
		if (structMap == null)
			lineOut("(No StructureMap - skipping Structure Map section)");
		
		else
		{
			lineOut("StructureMap Info:");
			incrementIndent();
			lineOut("Atom Count : " + structMap.getAtomCount());
			lineOut("Bond Count : " + structMap.getBondCount());
			lineOut("Residue Count : " + structMap.getResidueCount());
			lineOut("Fragment Count : " + structMap.getFragmentCount());
			lineOut("Chain Count : " + structMap.getChainCount());
			lineOut("Aux Chain Count : " + auxChains.size());
			decrementIndent();
			
			lineOut("");
			lineOut("Detail by chain:");
			incrementIndent();
			
			for (Chain chain : structMap.getChains())
				outputChainInfo(chain, null);
				
			decrementIndent();
			lineOut("--End StructureMap--");
		}
		
		if (isPdb)
		{
			if (auxChains.size() > 0)
			{
				lineOut("");
				lineOut("Auxilliary Chains (pdb file) (psuedo-ids):");
				incrementIndent();
				
				for (String key : auxChains.keySet())
					outputChainInfo(auxChains.get(key), key);
				
				decrementIndent();
			}
		}
		
		lineOut("");
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
			lineOut("Pseudo Chain Id : " + chainId);
		
		else
			lineOut("Chain Id : " + chainId);
		
		if (chainId.equals("_"))
						// don't output the default chain id - if it contains ions/ligands,
						// they'll be extracted and displayed elsewhere.
						// otherwise, it just shows waters - we don't care.
		{
			incrementIndent();
			lineOut("(default chain id group)");
			decrementIndent();
			return;
		}
		
		incrementIndent();
		if (!auxChain)
						// auxilliary chains don't have fragments - put out fragment
						// info for normal chain
		{
			lineOut("Fragments: ");
			incrementIndent();
			
			for (Fragment fragment : chain.getFragments())
			{
				lineOut("--New Fragment--");
				lineOut("Conformation Type : " + fragment.getConformationType());
				lineOut("Residues:");
				incrementIndent();
				
				outputResidueInfo(fragment.getResidues());
					
				decrementIndent();
				lineOut("--End Fragment--");
			}
			
			decrementIndent();
			lineOut("--End Fragments--");
		}
		
		else	// auxChain - no fragments
			outputResidueInfo(chain.getResidues());
		
		decrementIndent();
		lineOut("--End Chain--");
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
			
			lineOut("Residue Id : " + residue.getResidueId());
		    lineOut("Compound Code : " + residue.getCompoundCode());
		    lineOut("Hydrophobicity : " + residue.getHydrophobicity());
		    lineOut("Num Atoms : " + residue.getAtomCount());
		    if (doAtoms)
		    {
		    	lineOut("Atoms:");
		    	incrementIndent();

			    for (Atom atom : residue.getAtoms())
			    {
			    	lineOut("Atom Name : " + atom.name);
			    	lineOut("Element : " + atom.element);
			    	lineOut("Partial Charge : " + atom.partialCharge);
			    	lineOut("Occupancy : " + atom.occupancy);
			    	lineOut("");
			    }
			    
			    decrementIndent();
		    }

		    lineOut("--End Residue");
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
		
		lineOut("Unit Cell:");
		incrementIndent();
		
		if (loader.hasUnitCell())
		{
			UnitCell unitCell = loader.getUnitCell();
			lineOut("angleAlpha: " + unitCell.angleAlpha);
			lineOut("angleBeta: " + unitCell.angleBeta);
			lineOut("angleGamma: " + unitCell.angleGamma);
			lineOut("length A: " + unitCell.lengthA);
			lineOut("length B: " + unitCell.lengthB);
			lineOut("length C: " + unitCell.lengthC);
		}
		
		else
			lineOut("(None)");
		
		decrementIndent();
		lineOut("");
		
		lineOut("Biological Unit Transforms:");
		incrementIndent();

		if (loader.hasBiologicUnitTransformationMatrices())
		{
			txIX = 1;
			for (ModelTransformationMatrix modelTransform : loader.getBiologicalUnitTransformationMatrices())
				outputTransformValues(txIX++, modelTransform.values);
		}
		
		else
			lineOut("(None)");
		
		decrementIndent();
		lineOut("");
		
		lineOut("Non-Crystallographic Transforms:");
		incrementIndent();
		if (loader.hasNonCrystallographicOperations())
		{
			txIX = 1;
			for (ModelTransformationMatrix modelTransform : loader.getNonCrystallographicOperations())
				outputTransformValues(txIX++, modelTransform.values);
		}
		
		else
			lineOut("(None)");
	}
	
	static private void outputTransformValues(int ix, float[] values)
	{
		lineOut(ix + ": " + values[0] + ", " + values[1] + ", " + values[2] + ", " + values[3] + ",");
		lineOut(" : " + values[4] + ", " + values[5] + ", " + values[6] + ", " + values[7] + ",");
		lineOut(" : " + values[8] + ", " + values[9] + ", " + values[10] + ", " + values[11] + ",");
		lineOut(" : " + values[12] + ", " + values[13] + ", " + values[14] + ", " + values[15]);
		lineOut("");
	}
	
	/* ******************************************************************************************
	 * Following code is support for indenting and outputting strings.
	 * ******************************************************************************************/
	static private int indentSize = 4;
	static private String indent = "";
	static private String indentBuf = null;
	
	static private void incrementIndent()
	{
		if (indentBuf == null)
		{
			indentBuf = new String();
			for (int i = 0; i < indentSize; i++)
				indentBuf += ' ';
		}
		
       indent += indentBuf;
	}
	
	static private void decrementIndent()
	{
		if (indent.length() >= indentSize)
			indent = indent.substring(0, indent.length() - indentSize);
	}
	
	static private void lineOut(String line)
	{
		System.out.println(line.length() == 0? "" : indent + line);
	}
}