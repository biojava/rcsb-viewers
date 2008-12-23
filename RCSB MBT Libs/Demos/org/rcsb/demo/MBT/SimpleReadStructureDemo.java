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
import org.rcsb.mbt.model.util.PdbToNdbConverter;
import org.rcsb.mbt.structLoader.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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
 * 
 * <p style="color:green">
 * Note: This has been incorporated as a Unit Test, as well as a demo.  Consequently, there is a certain
 * amount of output that is concerned with verification.  Not necessarily a bad thing for a demo.  If there
 * are discrepancies, they are being worked on.
 * </p>
 * @author rickb
 *
 */
public class SimpleReadStructureDemo
{
	protected boolean isPdbFile = false;
	protected  boolean doAtoms = true;
	protected  boolean doResidues = true;
	protected boolean doBreakout = true;
	protected boolean doBonds = true;
	protected boolean doExternChains = true;
	
	protected int atomsCounted = 0;
	protected int residuesCounted = 0;
	protected int fragmentsCounted = 0;
	protected int chainsCounted = 0;
	protected int bondsCounted = 0;
	
	 protected Output output;
	 protected Structure struct = null;
	
	/**
	 * Here's the main...
	 * 
	 * @param args
	 */
	public static void main(String args[]) throws IOException
	{
		String fileName = null;
		
		SimpleReadStructureDemo rdr = new SimpleReadStructureDemo();
		
		for (String arg : args)
		{
			if (arg.equals("-noatoms"))
				rdr.doAtoms = false;
			
			if (arg.equals("-nobonds"))
				rdr.doBonds = false;
			
			if (arg.equals("-noresidues"))
				rdr.doResidues = rdr.doAtoms = rdr.doBonds = false;
			
			else if (arg.equals("-nobreakout"))
				rdr.doBreakout = false;
			
			else
				fileName = arg;
		}
		
		if (fileName == null)
		{
			System.err.println("Error: you need to specify a structure file or URL.");
			System.exit(1);
		}
		
		rdr.doDump(fileName, System.out);	
	}
	
	/**
	 * Do the actual dump - note this is used for Unit Testing, so take that into account, if modifying.
	 * 
	 * @param fileName - the structure file to dump
	 * @param out - the print stream to output messages to
	 */
	public void doDump(String fileName, PrintStream out) throws IOException
	{
		IFileStructureLoader loader = loadStructure(fileName, out);
		
		output.lineOut("Structure Loaded");
		Set<String> nprids = loader.getNonProteinChainIds();
		output.lineOut("Non Protein Chain ID Count: " + nprids.size());
		if (nprids.size() > 0)
		{
			output.indent();
			output.lineOut("IDs:");
			output.indent();
			for (String id : nprids)
				output.lineOut(id);
			output.outdent();
			output.outdent();
			output.lineOut("");
		}
		
		reportChains(struct);
		reportBonds(struct);		
		reportTransforms(loader, struct);
	}
	
	/**
	 * load structure from a filename and setup the output print stream.
	 * 
	 * @param fileName - the filename to load.  Can be system disk file or URL
	 * @param out - the output stream to write to.
	 * @return - and IFileStructureLoad object
	 * @throws IOException
	 */
	protected IFileStructureLoader loadStructure(final String fileName, PrintStream out) throws IOException
	{
		File file = new File(fileName);
		
		if (file == null || !file.exists())
			throw new IOException("Error: can't open file '" + fileName + "'");
			
		String fnTmp = fileName.endsWith(".gz")? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
		String sfx = fnTmp.substring(fnTmp.lastIndexOf('.') + 1);
		isPdbFile = sfx.equalsIgnoreCase("pdb");
							// get the true suffix - pdb or xml
		
		IFileStructureLoader loader = null;
				
		if (isPdbFile)
		{
			loader = new PdbStructureLoader();
		    ((PdbStructureLoader)loader).setBreakoutEmptyChainsByResId(doBreakout);
		    			// default behavior is to lump all the unassociated residues (including waters) into a single
		    			// default chain labelled "_".   Setting this flag will cause the loader to break out 'pseudo-chains'
		    			// by residue id.
		}
		
		else if (sfx.equalsIgnoreCase("xml"))
			loader = new XMLStructureLoader(new StructureXMLHandler(fileName));
			
		else
			throw new IOException("Error: a '.pdb' or '.xml' url is expected.");

		struct = loader.load(file);
								// load up the structures
		
		if (struct == null)
			throw new IOException("Error: could not read structure from '" + fileName + "'");
		
		new StructureMap(struct, null, loader.getIDConverter(), loader.getNonProteinChainIds());
							// create a structuremap associated with the structure.
							// the second argument is for user data.

		output = new Output(out);
		
		return loader;
	}
	
	/**
	 * Walk the list of chains, outputting the internals of each chain
	 * 
	 * @param struct
	 */
	protected void reportChains(Structure struct)
	{
		int chainsCountedHere = 0;
		
		StructureMap structMap = struct.getStructureMap();
		StructureInfo structInfo = struct.getStructureInfo();
		TreeMap<String, Chain> auxChains = extractAuxilliaries(structMap);
		
		output.lineOut("Url : (xxxx)" + struct.getUrlString().substring(struct.getUrlString().lastIndexOf('/')));
		// just get the filespec of the url so we don't generate false diffs.

		output.lineOut("Structure ID: " + structMap.getPdbId());
		
		/*
		 * output the structureInfo - haven't seen this filled by anything, yet.
		 */
		if (structInfo == null)
			output.lineOut("(No StructureInfo struct - skipping Structure Info section)");
		else
		{
			output.lineOut("Structure Info:");
			output.indent();
			output.lineOut("Long Name : " + structInfo.getLongName());
			output.lineOut("Short Name : " + structInfo.getShortName());
			output.lineOut("Release Date : " + structInfo.getReleaseDate());
			output.lineOut("Pdb ID : " + structInfo.getIdCode());
			output.lineOut("Authors:" + structInfo.getAuthors());
			output.outdent();
		}
		
		output.lineOut("");
		
		/*
		 * output the chain info using the structure map
		 */
		if (structMap == null)
			output.lineOut("(No StructureMap - skipping Structure Map section)");
		
		else
		{
			output.lineOut("StructureMap:");
			output.indent();
			output.lineOut("Atom Count : " + structMap.getAtomCount());
			output.lineOut("Bond Count : " + structMap.getBondCount());
			output.lineOut("Residue Count : " + structMap.getResidueCount());
			output.lineOut("Fragment Count : " + structMap.getFragmentCount());
			output.lineOut("Chain Count : " + structMap.getChainCount());
			output.lineOut("Aux Chain Count : " + ((doBreakout)? "(N/A - breakout set)" : auxChains.size() + " (no breakout"));
			output.outdent();
			
			output.lineOut("");
			output.lineOut("Detail by chain:");
			output.indent();
			
			for (Chain chain : structMap.getChains())
			{
				outputChainInfo(chain, null, struct);
				chainsCountedHere++;
			}
		    
		    output.outdent();
			output.lineOut("--End StructureMap--");
		}
		
		if (isPdbFile)
		{
			if (auxChains.size() > 0)
			{
				output.lineOut("");
				output.lineOut("Auxilliary Chains (pdb file) (psuedo-ids):");
				output.indent();
				
				for (String key : auxChains.keySet())
				{
					outputChainInfo(auxChains.get(key), key, struct);
					chainsCountedHere++;
				}
				
				output.outdent();
			}
		}
		output.lineOut("Number Chains Encountered here: " + chainsCountedHere);
		output.lineOut("");
		
		chainsCounted += chainsCountedHere;
	}
	
	/**
	 * Traverse the contents of the chain and output.
	 * 
	 * @param chain - the chain to traverse
	 * @param auxChainId - if this is an auxilliary chain, this is the name.  Otherwise null.
	 */
	protected void outputChainInfo(Chain chain, String auxChainId, Structure struct)
	{
		boolean isNonProteinChain = auxChainId != null || chain.isNonProteinChain();
		
		String chainId = (auxChainId != null)? auxChainId : chain.getChainId();
		if (isNonProteinChain)
			output.lineOut("Pseudo (Non-Protein) Chain Id : " + chainId);
		
		else
			output.lineOut("Chain Id : " + chainId);
		
		if (chainId.equals("_") || chainId.equals("HOH"))
						// don't output the default chain id - if it contains ions/ligands,
						// they'll be extracted and displayed elsewhere.
						// otherwise, it just shows waters - we don't care.
		{
			output.indent();
			output.lineOut("(default chain id group or explicit water chain)");
			output.lineOut(chain.getResidueCount() + " residues, " + chain.getAtomCount() + " atoms.");
			output.outdent();
			return;
		}
		
		output.indent();
		if (!isNonProteinChain)
						// non-protein chains don't have fragments - put out fragment
						// info for protein chain
		{
			int fragmentsCountedHere = 0;
			
			output.lineOut("Fragments: ");
			output.indent();
			
			for (Fragment fragment : chain.getFragments())
			{
				output.lineOut("--New Fragment--");

				Object cfObj = fragment.getConformationType();
				if (cfObj instanceof String)
				{
					String cfName = (String)cfObj;
					output.lineOut("Conformation Type : " + cfName.substring(cfName.lastIndexOf('.') + 1).toUpperCase());
				}
				else
					output.lineOut("Conformation Type : " + fragment.getConformationType());

				outputResidueInfo(fragment.getResidues(), chain, struct);
				output.lineOut("--End Fragment--");
				output.lineOut("");
				
				fragmentsCountedHere++;
			}

			output.outdent();
			output.lineOut("Number Fragments Encountered Here: " + fragmentsCountedHere);
			output.lineOut("--End Fragments--");
			
			fragmentsCounted += fragmentsCountedHere;
		}
		
		else	// auxChain - no fragments
			outputResidueInfo(chain.getResidues(), chain, struct);
			
		output.outdent();
		output.lineOut("--End Chain--");
		output.lineOut("");
	}
	
	/**
	 * output the bond info from the structure map
	 * 
	 * @param struct
	 */
	protected void reportBonds(Structure struct)
	{	
	    if (!doBonds) return;

	    StructureMap structMap = struct.getStructureMap();
    	int bondsCountedHere = 0;
	    output.lineOut("Bonds:");
	    output.indent();
	    TreeSet<String> orderedBondSet = new TreeSet<String>();
	    HashMap<String, Integer> dups = new HashMap<String, Integer>();
	    
	    for (Bond bond : structMap.getBonds())
	    {
	    	String bondString = getBondLine(bond);
	    	
	    	if (orderedBondSet.contains(bondString))
	    	{
	    		int numHit = (dups.containsKey(bondString))? dups.get(bondString) : 0;
	    		dups.put(bondString, ++numHit);
	    		bondString += " -- DUP (" + numHit + ")";
	    	}
	    	
	    	orderedBondSet.add(bondString);
	    	bondsCountedHere++;
	    }
	    
	    for (String orderedBondString : orderedBondSet)
	    	output.lineOut(orderedBondString);
	    
	    output.lineOut("--End Bonds--");
	    output.lineOut("Number Bonds Encountered Here: " + bondsCountedHere);
	    output.outdent();
	    
	    bondsCounted += bondsCountedHere;
	}
	
    
	/**
	 * Collect and output a line of bond information
	 * 
	 * @param bond
	 * @return
	 */
   protected String getBondLine(Bond bond)
	    {
	    	Atom atoms[] = { bond.getAtom(0), bond.getAtom(1) };
	    	boolean notEqual = atoms[0].chain_id != atoms[1].chain_id ||
	    					   atoms[0].residue_id != atoms[1].residue_id ||
	    					   atoms[0].structure != atoms[1].structure;
	    	String atomInfoSep = notEqual? " !! " : " -- ";
	    	String bondString = atoms[0].compound + " : " + bond.getAtom(0).name + " - " + bond.getAtom(1).name + atomInfoSep
	    			+ atoms[0].structure.getStructureMap().getPdbId() + "," +
	    				atoms[0].chain_id + "," + atoms[0].residue_id;
	    	
	    	if (notEqual)
	    		bondString += " / " + atoms[1].compound + " : " + atoms[1].structure.getStructureMap().getPdbId() + "," +
				atoms[1].chain_id + "," + atoms[1].residue_id;

	    	bondString += atomInfoSep + 
	    			   + bond.getOrder() + " (" + bond.getBondType().toString().toUpperCase() + ")";
	    	
	    	return bondString;
	    }
	   
	/**
	 * Walk the residue list, either from the Fragment or the Chain, and output internals
	 * 
	 * @param residues
	 */
	protected void outputResidueInfo(Vector<Residue> residues, Chain chain, Structure struct)
	{
		output.lineOut("Residues: " + residues.size());
		PdbToNdbConverter conv = struct.getStructureMap().getPdbToNdbConverter();

		if (!doResidues) return;
		
		int residuesCountedHere = 0;
		
		output.indent();
		for (Residue residue : residues)
		{	
			output.lineOut("Res - Id: " + residue.getResidueId() + "/" + conv.getResidueNdbId(residue, "(NONE)") + ", " +
						   "CmpCd: " + residue.getCompoundCode() + ", " +
						   "HPhob: " + residue.getHydrophobicity() + ", " +
						   "NAtoms: " + residue.getAtomCount() + " (" +
	    				   "C_ID: " + ((residue.getChainId() == chain.getChainId())? "OK" : "BAD") + ")");

		    if (doAtoms)
		    {
		    	int atomsCountedHere = 0;
		    	output.lineOut("Atoms:");
		    	output.indent();

			    for (Atom atom : residue.getAtoms())
			    {
			    	output.lineOut("Name: " + atom.name + ", " +
			    				   "El: " + atom.element + ", " +
			    				   "PartChrg: " + atom.partialCharge + ", " +
			    				   "Occ: " + atom.occupancy + "  (" +
			    				   "R_ID: " + ((atom.residue_id == residue.getResidueId())? "OK" : "FAULT") + ", " +
			    				   "C_ID: " + ((atom.chain_id == chain.getChainId())? "OK" : "FAULT") + ")");
			    	atomsCountedHere++;
			    }
			    
			    output.lineOut("Number Atoms Encountered Here: " + atomsCountedHere);
			    output.outdent();
			    atomsCounted += atomsCountedHere;
		    }

		    output.lineOut("--End Residue");
		    output.lineOut("");
		    residuesCountedHere++;
		}

		output.outdent();
		output.lineOut("Number Residues Encountered Here: " + residuesCountedHere);
		residuesCounted += residuesCountedHere;
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
	protected TreeMap<String, Chain> extractAuxilliaries(StructureMap structMap)
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
	
	protected void reportTransforms(IStructureLoader loader, Structure struct)
	{
		int txIX;
		
		output.lineOut("Unit Cell:");
		output.indent();
		
		if (loader.hasUnitCell())
		{
			UnitCell unitCell = loader.getUnitCell();
			output.lineOut("angleAlpha: " + unitCell.angleAlpha);
			output.lineOut("angleBeta: " + unitCell.angleBeta);
			output.lineOut("angleGamma: " + unitCell.angleGamma);
			output.lineOut("length A: " + unitCell.lengthA);
			output.lineOut("length B: " + unitCell.lengthB);
			output.lineOut("length C: " + unitCell.lengthC);
		}
		
		else
			output.lineOut("(None)");
		
		output.outdent();
		output.lineOut("");
		
		output.lineOut("Biological Unit Transforms:");
		output.indent();

		if (loader.hasBiologicUnitTransformationMatrices())
		{
			txIX = 1;
			for (ModelTransformationMatrix modelTransform : loader.getBiologicalUnitTransformationMatrices())
				outputTransformValues(txIX++, modelTransform.values);
		}
		
		else
			output.lineOut("(None)");
		
		output.outdent();
		output.lineOut("");
		
		output.lineOut("Non-Crystallographic Transforms:");
		output.indent();
		if (loader.hasNonCrystallographicOperations())
		{
			txIX = 1;
			for (ModelTransformationMatrix modelTransform : loader.getNonCrystallographicOperations())
				outputTransformValues(txIX++, modelTransform.values);
		}
		
		else
			output.lineOut("(None)");
		
		output.outdent();
		output.lineOut("");
	}
	
	protected void outputTransformValues(int ix, float[] values)
	{
		output.lineOut(ix + ": " + values[0] + ", " + values[1] + ", " + values[2] + ", " + values[3] + ",");
		output.lineOut(" : " + values[4] + ", " + values[5] + ", " + values[6] + ", " + values[7] + ",");
		output.lineOut(" : " + values[8] + ", " + values[9] + ", " + values[10] + ", " + values[11] + ",");
		output.lineOut(" : " + values[12] + ", " + values[13] + ", " + values[14] + ", " + values[15]);
		output.lineOut("");
	}

}
