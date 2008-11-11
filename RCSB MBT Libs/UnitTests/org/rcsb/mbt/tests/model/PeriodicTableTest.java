package org.rcsb.mbt.tests.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.zip.GZIPOutputStream;

import org.rcsb.demo.MBT.DumpPeriodicTable;
import org.rcsb.mbt.tests.utils.TestsInputOutputDirs;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PeriodicTableTest
{
	private TestsInputOutputDirs ioDirs;
	final String testName = "PeriodicTable_txt.gz";
	
    public PeriodicTableTest() throws IOException
	{
		ioDirs = new TestsInputOutputDirs(getClass().getSimpleName(), true);
		ioDirs.setFileNamesAreMolecules(false);
	}
	
	@Test
	public void doTest() throws IOException
	{
		String outputPath = ioDirs.getFullOutputTestFileCompareFilePath(testName);
		File outputFile = new File(outputPath);
		PrintStream ps = 
			new PrintStream(
				new GZIPOutputStream(
						new FileOutputStream(outputFile)));
		
		DumpPeriodicTable.doDump(ps);
		ps.close();
		
		Assert.assertTrue(ioDirs.compareOutputToExpected(testName));
	}
}
