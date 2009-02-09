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
package org.rcsb.mbt.model.misc;

import java.io.File;
import java.io.IOException;

public class TestsInputOutputDirs
{
	static public final String projectDirName = "RCSB MBT Libs";
	static public final String testInputTopDirName = "test-input";
	static public final String testInputGenDirName = "General";
	static public final String testInputMoleculeDirName = "molecules";
	static public final String compareFilesInputDirName = "Expected";
	static public final String testOutputTopDirName = "test-output";
	static public final String compareFilesOutputDirName = "Tested";
	static public final String compareFilesDiffDirName = "Diffs";
	
	public enum DirIndex{ PROJECTDIR,				// the top-level project directory
						  INPUT_TOPTESTDIR,			// the top-level test input directory (testInputDirName)
						  INPUT_GENERALDIR,		  	// directory containing general input data
						  INPUT_MOLECULESDIR,	  	// directory containing sample molecules
						  INPUT_CLASSTESTDIR,		// the class test input directory
						  INPUT_COMPAREFILESDIR, 	// the compare files input directory (if using)
						  OUTPUT_TOPTESTDIR,		// the top-level test output directory (testOutputDirName)
						  OUTPUT_CLASSTESTDIR,		// the class test output directory
						  OUTPUT_COMPAREFILESDIR, 	// the compare files output directory (if using)
						  OUTPUT_DIFFDIR;			// contains the diff results
	
						  protected File path;
						  public void setPath(File path) { this.path = path; }
						  }
	
	private String lastConvertedInputName = "", lastConvertedOutputName = "";
	private boolean argIsMoleculeFileName = true;
	public void setFileNamesAreMolecules(boolean flag) { argIsMoleculeFileName = flag; }
	private char sepChar = '/';

	public TestsInputOutputDirs(String invocationClassName, boolean doesFileCompares) throws IOException
	{
		File projectDir = new File(System.getProperties().getProperty("user.dir"));
		DirIndex.PROJECTDIR.setPath(projectDir);
							// change if the invocation directory is not the project directory
		
		//
		// BEG find the input directories
		//
		
		File workingDir;
		
		File topTestInputDir = new File(projectDir.getAbsolutePath() + sepChar + testInputTopDirName);
		if (!topTestInputDir.exists())
			throw new IOException("Can't find top level test directory.");

		 DirIndex.INPUT_TOPTESTDIR.setPath(topTestInputDir);
		 
		 workingDir = new File(
				 topTestInputDir.getAbsolutePath() + sepChar + testInputGenDirName);
		 if (!workingDir.exists())
			 throw new IOException("Can't find general input test directory.");
		 DirIndex.INPUT_GENERALDIR.setPath(workingDir);
		 
		 workingDir = new File(
				 workingDir.getAbsolutePath() + sepChar + testInputMoleculeDirName);
		 if (!workingDir.exists())
			 throw new IOException("Can't find molecule test directory");
		 DirIndex.INPUT_MOLECULESDIR.setPath(workingDir);
				 
		 
		 workingDir = new File(
				 topTestInputDir.getAbsolutePath() + sepChar + invocationClassName);
		 
		 if (!workingDir.exists())
			 throw new IOException("Can't find class input test directory");

		 DirIndex.INPUT_CLASSTESTDIR.setPath(workingDir);
		 
		 if (doesFileCompares)
		 {
			 workingDir = new File(
				 workingDir.getAbsolutePath() + sepChar + compareFilesInputDirName);
			 
			 if (!workingDir.exists())
				 throw new IOException("Can't find compare files input directory.");
			 
			 DirIndex.INPUT_COMPAREFILESDIR.setPath(workingDir);
		 }
		 
		 //
		 // END find the input directories
		 // BEG find/create the output directories
		 //
		 
		 try
	 	 {
			 String fullOutputPath = projectDir.getAbsolutePath() + sepChar + testOutputTopDirName +
				 sepChar + invocationClassName;
			 
			 workingDir = new File(fullOutputPath);
			 DirIndex.OUTPUT_CLASSTESTDIR.setPath(workingDir);
			 workingDir.mkdir();

			 if (doesFileCompares)
			 {
				 workingDir = new File(fullOutputPath + sepChar + compareFilesOutputDirName);
				 DirIndex.OUTPUT_COMPAREFILESDIR.setPath(workingDir);
				 workingDir.mkdir();
				
				 workingDir = new File(fullOutputPath + sepChar + compareFilesDiffDirName);
				 DirIndex.OUTPUT_DIFFDIR.setPath(workingDir);
				 workingDir.mkdir();
			 }
	 	 }
		 
		 catch (SecurityException e)
	 	 {
	 		 throw new IOException("Couldn't create output path.");
	 	 }
	}
	
	public File getDir(DirIndex whichDir)
	{ return whichDir.path; }
	
	/**
	 * Takes an input file specification and turns it into an output specification - 
	 * replaces '.' with '_' and appends '.txt'
	 * 
	 * @param inputTestFilename - a filename, usually something like '1I5D.xml.gz'
	 * @return - the name modified for output, like '1I5D_xml_gz.txt.gz'
	 */
	public String getTestFileName(String inputTestFilename)
	{
		if (!argIsMoleculeFileName)
			return inputTestFilename;
		
		if (lastConvertedInputName != inputTestFilename)
		{		
			lastConvertedInputName = inputTestFilename;
			lastConvertedOutputName = inputTestFilename.replaceAll("\\.", "_") + ".txt.gz";
		}
		return lastConvertedOutputName;
	}
	
	/**
	 * Prepends the output compare files directory to the generated name.
	 * 
	 * @param inputTestFilename - a filename, usually something like '1I5D.xml.gz'
	 * @return - absolute path, usually something like
	 * 			'/.../&lt;project&gt/test-out/&lt;classname&gt/&lt;output-compare-files-dir&gt;/1I5D_xml_gz.txt.gz'
	 */
	public String getFullOutputTestFileCompareFilePath(String inputTestFilename)
	{
		return getDir(DirIndex.OUTPUT_COMPAREFILESDIR).getAbsolutePath() + sepChar +
					  getTestFileName(inputTestFilename);
	}
	
	public String getFullExpectedCompareFilePath(String inputTestFileName)
	{
		return getDir(DirIndex.INPUT_COMPAREFILESDIR).getAbsolutePath() + sepChar + 
					  getTestFileName(inputTestFileName);
	}
	
	public String getFullDiffFilePath(String inputTestFileName)
	{
		return getDir(DirIndex.OUTPUT_DIFFDIR).getAbsolutePath() + sepChar +
					  getTestFileName(inputTestFileName).replace(".gz", ".diff");
	}

	/**
	 * Compares input (expected) to output (generated this test).
	 * 
	 * @param name - the input filename
	 * @return - true if identical, false if not.
	 * @throws IOException
	 */
	public boolean compareOutputToExpected(String name) throws IOException
	{
		File expectedFile = new File(getFullExpectedCompareFilePath(name));
		File testOutputFile = new File(getFullOutputTestFileCompareFilePath(name));
		File diffFile = new File(getFullDiffFilePath(name));
		
		if (!expectedFile.exists() || !testOutputFile.exists())
			throw new IOException("Input file for compare: \"" +
				(!expectedFile.exists()? expectedFile.getAbsolutePath() : testOutputFile.getAbsolutePath()) +
				"\" does not exist.");
	
		String expectedFileEsc = expectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ");
		String testOutputFileEsc = testOutputFile.getAbsolutePath().replaceAll(" ", "\\\\ ");
		String diffFileEsc = diffFile.getAbsolutePath().replaceAll(" ", "\\\\ ");

		String execStr[] = {
				"sh",
				"-c",
				"/usr/bin/zdiff " + expectedFileEsc + " " + testOutputFileEsc + " > " + diffFileEsc};
		
		Process proc = Runtime.getRuntime().exec(execStr);
		try
		{
			proc.waitFor();
		}
		
		catch (InterruptedException e)
		{
			return false;
		}
		
		boolean success = diffFile.exists() && diffFile.length() == 0L;
		
		if (success)
			diffFile.delete();
						// only fails have non-empty diff files -
						// don't leave the empties hanging out there.
		
		return success;

/* **
		InputStream expectedFileRdr = 
				new GZIPInputStream(new FileInputStream(expectedFile));
			
		InputStream testOutputFileRdr = 
				new GZIPInputStream(new FileInputStream(testOutputFile));
		
		int expectedChar, testOutputChar;
		while (true)
		{
			expectedChar = expectedFileRdr.read();
			testOutputChar = testOutputFileRdr.read();
			
			if (expectedChar == -1 || testOutputChar == -1)
			{
				retval = expectedChar == testOutputChar;
				break;
							// they should both be null
			}
			
			if (expectedChar != testOutputChar)
			{
				retval = false;	
				break;
			}
		}
		
		expectedFileRdr.close();
		testOutputFileRdr.close();
		
		return retval;
* **/
	}
}
