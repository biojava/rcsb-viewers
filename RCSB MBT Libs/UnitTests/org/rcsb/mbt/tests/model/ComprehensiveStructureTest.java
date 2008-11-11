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