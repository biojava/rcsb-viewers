package org.rcsb.mbt.tests.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.util.ChemicalComponentBonds;
import org.rcsb.mbt.model.util.ChemicalComponentBonds.BondType;
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
		BondType expected;
		
		public AtomTestPair(int bondAtoms[], BondType in_expected)
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
		File in = new File(datafilePath + "/Definitions.txt");
		
		if (!in.exists() || !in.canRead())
			throw new IOException("Definitions.txt file does not exist.");
		
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
							ChemicalComponentBonds.BondType.valueByShortName(splitLine[4])));
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
