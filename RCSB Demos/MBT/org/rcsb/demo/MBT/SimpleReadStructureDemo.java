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
package org.rcsb.demo.MBT;

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
	static boolean doResidues = true;
	static boolean doBreakout = true;

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

			if (arg.equals("-noresidues"))
				doResidues = doAtoms = false;

			else if (arg.equals("-nobreakout"))
				doBreakout = false;

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
		{
			loader = new PdbStructureLoader();
		}

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

		Output.lineOut("Structure Loaded");

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
			Output.indent();
			Output.lineOut("Long Name : " + structInfo.getLongName());
			Output.lineOut("Short Name : " + structInfo.getShortName());
			Output.lineOut("Release Date : " + structInfo.getReleaseDate());
			Output.lineOut("Pdb ID : " + structInfo.getIdCode());
			Output.lineOut("Authors:" + structInfo.getAuthors());
			Output.outdent();
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
			Output.indent();
			Output.lineOut("Atom Count : " + structMap.getAtomCount());
			Output.lineOut("Bond Count : " + structMap.getBondCount());
			Output.lineOut("Residue Count : " + structMap.getResidueCount());
			Output.lineOut("Fragment Count : " + structMap.getFragmentCount());
			Output.lineOut("Chain Count : " + structMap.getChainCount());
			Output.lineOut("Aux Chain Count : " + ((doBreakout)? "(N/A - breakout set)" : auxChains.size() + " (no breakout"));
			Output.outdent();

			Output.lineOut("");
			Output.lineOut("Detail by chain:");
			Output.indent();

			for (Chain chain : structMap.getChains()) {
				Output.lineOut("Structure style: " + structMap.getStructureStyles().getStyle(chain)); // pr
				outputChainInfo(chain, null);
			}

			Output.outdent();
			Output.lineOut("--End StructureMap--");
		}

		if (isPdb)
		{
			if (auxChains.size() > 0)
			{
				Output.lineOut("");
				Output.lineOut("Auxilliary Chains (pdb file) (psuedo-ids):");
				Output.indent();

				for (String key : auxChains.keySet())
					outputChainInfo(auxChains.get(key), key);

				Output.outdent();
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
//		boolean isNonProteinChain = auxChainId != null || chain.isNonProteinChain();

		String chainId = (auxChainId != null)? auxChainId : chain.getAuthorChainId();
//		if (isNonProteinChain)
//			Output.lineOut("Pseudo (Non-Protein) Chain Id : " + chainId);
//
//		else
			Output.lineOut("Chain Id : " + chainId);

		StructureMap sm = chain.getStructure().getStructureMap(); // pr
		Output.lineOut("Structure style: " + sm.getStructureStyles().getStyle(chain)); // pr

		if (chainId.equals("_") || chainId.equals("HOH"))
			// don't output the default chain id - if it contains ions/ligands,
			// they'll be extracted and displayed elsewhere.
			// otherwise, it just shows waters - we don't care.
		{
			Output.indent();
			Output.lineOut("(default chain id group or explicit water chain)");
			Output.lineOut(chain.getResidueCount() + " residues, " + chain.getAtomCount() + " atoms.");
			Output.outdent();
			return;
		}

		Output.indent();
//		if (!isNonProteinChain)
//			// non-protein chains don't have fragments - put out fragment
//			// info for protein chain
//		{
//			Output.lineOut("Fragments: ");
//			Output.indent();
//
//			for (Fragment fragment : chain.getFragments())
//			{
//				Output.lineOut("--New Fragment--");
//				Output.lineOut("Conformation Type : " + fragment.getConformationType());
//				outputResidueInfo(fragment.getResidues());
//				Output.lineOut("--End Fragment--");
//				Output.lineOut("");
//			}
//
//			Output.outdent();
//			Output.lineOut("--End Fragments--");
//		}
//
//		else	// auxChain - no fragments
			outputResidueInfo(chain.getResidues());

		Output.outdent();
		Output.lineOut("--End Chain--");
		Output.lineOut("");
	}

	/**
	 * Walk the residue list, either from the Fragment or the Chain, and output internals
	 * 
	 * @param residues
	 */
	static private void outputResidueInfo(Vector<Residue> residues)
	{
		Output.lineOut("Residues: " + residues.size());

		if (!doResidues) return;

		Output.indent();
		for (Residue residue : residues)
		{
			if (residue.getClassification() == Residue.Classification.WATER)
				continue;
			// let's ignore waters

			Output.lineOut("Residue Id : " + residue.getResidueId());
			Output.lineOut("Insertion Code : " + residue.getInsertionCode());
			Output.lineOut("Compound Code : " + residue.getCompoundCode());
			Output.lineOut("Hydrophobicity : " + residue.getHydrophobicity());
			Output.lineOut("Num Atoms : " + residue.getAtomCount());
			if (doAtoms)
			{
				Output.lineOut("Atoms:");
				Output.indent();

				for (Atom atom : residue.getAtoms())
				{
					Output.lineOut("Atom Name : " + atom.name);
					Output.lineOut("Element : " + atom.element);
					Output.lineOut("Compound Id: " + atom.compound);
					Output.lineOut("Insertion Code: " + atom.insertionCode);
					Output.lineOut("Partial Charge : " + atom.partialCharge);
					Output.lineOut("Occupancy : " + atom.occupancy);
					Output.lineOut("");
				}

				Output.outdent();
			}

			Output.lineOut("--End Residue");
		}

		Output.outdent();
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
		Output.indent();

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

		Output.outdent();
		Output.lineOut("");

		Output.lineOut("Biological Unit Transforms:");
		Output.indent();

		if (loader.hasBiologicUnitTransformationMatrices())
		{
			txIX = 1;
			for (ModelTransformationMatrix modelTransform : loader.getBiologicalUnitTransformationMatrices())
				outputTransformValues(txIX++, modelTransform.values);
		}

		else
			Output.lineOut("(None)");

		Output.outdent();
		Output.lineOut("");

		Output.lineOut("Non-Crystallographic Transforms:");
		Output.indent();
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
