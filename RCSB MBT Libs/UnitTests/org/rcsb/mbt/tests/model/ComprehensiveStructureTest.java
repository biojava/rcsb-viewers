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

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeMap;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.util.DebugState;
import org.rcsb.mbt.tests.utils.TestsInputOutputDirs;
import org.rcsb.mbt.tests.utils.TestsInputOutputDirs.DirIndex;
import org.rcsb.demo.MBT.SimpleReadStructureDemo;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ComprehensiveStructureTest implements FileFilter
{
	private TestsInputOutputDirs ioDirs;

    public ComprehensiveStructureTest() throws IOException
	{
		ioDirs = new TestsInputOutputDirs(getClass().getSimpleName(), true);
	}
    
    public boolean accept(File file)
    {
    	return !file.isDirectory();
    }
	
	@DataProvider (name = "get-filenames")
	public Object[][] getInputStructureFileNames()
	{
		File[] files = ioDirs.getDir(DirIndex.INPUT_MOLECULESDIR).listFiles(this);
		
		Object retSet[][] = new Object[files.length][1];

		for (int ix = 0; ix < files.length; ix++)
			retSet[ix][0] = files[ix];
		
		return retSet;
	}
	
	@Test (dataProvider="get-filenames" )
	public void doTest(File inputFile) throws IOException
	{
		String outputPath = ioDirs.getFullOutputTestFileCompareFilePath(inputFile.getName());
		
		PrintStream ps = 
			new PrintStream(
					new GZIPOutputStream(
						new FileOutputStream(outputPath)));
		
		SimpleReadStructureDump rdr = new SimpleReadStructureDump();
		rdr.doDump(inputFile.getAbsolutePath(), ps);
		
		ps.close();
		
		Assert.assertTrue(ioDirs.compareOutputToExpected(inputFile.getName()));
	}
}
