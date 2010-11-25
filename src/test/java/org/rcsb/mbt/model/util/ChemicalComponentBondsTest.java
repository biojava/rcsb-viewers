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
package org.rcsb.mbt.model.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.util.ChemicalComponentBonds;
import org.rcsb.mbt.model.util.ChemicalComponentBonds.BondOrder;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ChemicalComponentBondsTest // implements ITest
{
	private final String datafilePath = "test-input/ChemicalComponentBondsTest";
	
	private enum AtomStringParts { CHAIN_ID, COMPOUND_CODE, ATOM_NAME }
	
	private class AtomTestPair
	{
		Atom[] atoms = new Atom[2];
		int[] atomIX = new int[2];
		BondOrder expected;
		
		public AtomTestPair(int bondAtoms[], BondOrder in_expected)
		{
			for (int ix = 0; ix < 2; ix++)
			{
				String thisAtomInfo[] = atomInfo.get(bondAtoms[ix]);
				atomIX[ix] = bondAtoms[ix];
				atoms[ix] = new Atom();
				atoms[ix].chain_id = thisAtomInfo[AtomStringParts.CHAIN_ID.ordinal()];
				atoms[ix].compound = thisAtomInfo[AtomStringParts.COMPOUND_CODE.ordinal()];
				atoms[ix].name = thisAtomInfo[AtomStringParts.ATOM_NAME.ordinal()];
			}
			
			expected = in_expected;
		}
	}
	
	ArrayList<String[]> atomInfo = new ArrayList<String[]>();
	Object[][] atomTestPairRetObj = null;

	private enum FileSection { NONE, ATOMS, TESTPAIRS }
	
	public ChemicalComponentBondsTest() throws IOException
	{
		
		String path = "src/test/resources/"+ datafilePath + "/Definitions.txt";
		File in = new File(path);
		
		if (!in.exists() || !in.canRead())
			throw new IOException("Definitions.txt file does not exist. " + path);
		
		BufferedReader rdr = new BufferedReader(new FileReader(in));
		
		String line;
		FileSection whichSection = FileSection.NONE;
		ArrayList<AtomTestPair> atomTestPairsList = new ArrayList<AtomTestPair>();
		String splitLine[] = null;
		
		int lineNum = 0;
		while ((line = rdr.readLine()) != null)
		{
			lineNum++;
			if (line.charAt(0) == '#') continue;
			else if (line.charAt(0) == '>')
			{
				if (line.contains("ATOM"))
					whichSection = FileSection.ATOMS;

				else if (line.contains("PAIR"))
				{
					if (whichSection != FileSection.ATOMS)
						throw new IOException("No Atoms defined for Pair section.");
					else
						whichSection = FileSection.TESTPAIRS;
				}
				else
					throw new IOException("Invalid header...");
			}
			
			else
			{
				splitLine = line.split(" +");
				if (splitLine.length != 5)
					throw new IOException("Invalid number of items in line " + lineNum + ": \"" + line + '"');
								// just so happens both sections have the same number of items.
								// change, if this changes. (note the first item is empty because of the
								// leading space)
				
				switch(whichSection)
				{
					case ATOMS:
						atomInfo.add(new String[] {splitLine[2], splitLine[3], splitLine[4]});
						break;
						
					case TESTPAIRS:
						atomTestPairsList.add(new AtomTestPair(
							new int[] {Integer.parseInt(splitLine[2]), Integer.parseInt(splitLine[3])},
							ChemicalComponentBonds.BondOrder.valueByShortName(splitLine[4])));
						break;
				}
			}
		}
		
		rdr.close();
		
		atomTestPairRetObj = new Object[atomTestPairsList.size()][2];
		for (int nx = 0; nx < atomTestPairsList.size(); nx++)
		{
			AtomTestPair atomTestPair = atomTestPairsList.get(nx);
			atomTestPairRetObj[nx][0] = new String(atomTestPair.atomIX[0] + "," + atomTestPair.atomIX[1] + outputAtomInfo(atomTestPair.atoms));
			atomTestPairRetObj[nx][1] = atomTestPair;
		}			
	}
		
	public String outputAtomInfo(Atom atoms[])
	{
		String outputInfo = " Atom Info: ";
		boolean first = true;
		for (Atom atom : atoms)
		{
			outputInfo += " " + atom.name + " " + atom.compound + " " + atom.chain_id;
			if (!first)
				continue;
			
			outputInfo += ",";
			first = false;
		}
		
		return outputInfo;
	}
	
	@DataProvider (name = "get-pair-set")
	public Object[][] getPairSet()
	{
		return atomTestPairRetObj;
	}
	
	@Test(dataProvider = "get-pair-set")
	public void TestBondTypeLookupForward(String pairIX, AtomTestPair atomTestPair)
	{
		Assert.assertEquals(ChemicalComponentBonds.bondType(atomTestPair.atoms[0], atomTestPair.atoms[1]), atomTestPair.expected);
	}
	
	@Test(dataProvider = "get-pair-set")
	public void TestBondTypeLookupReverse(String pairIX, AtomTestPair atomTestPair)
	{
		Assert.assertEquals(ChemicalComponentBonds.bondType(atomTestPair.atoms[1], atomTestPair.atoms[0]), atomTestPair.expected);
	}
}
