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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.zip.GZIPInputStream;

public class InputOutputDirsHelper
{
	//static public final String projectDirName = "RCSB MBT Libs";
	static public final String testInputTopDirName = "test-input";
	static public final String testInputGenDirName = "General";
	static public final String testInputMoleculeDirName = "molecules";
	static public final String compareFilesInputDirName = "Expected";
	static public final String testOutputTopDirName = "test-output";
	static public final String compareFilesOutputDirName = "Tested";
	static public final String compareFilesDiffDirName = "Diffs";

	public DirIndex DirIndex = new DirIndex();

	private String lastConvertedInputName = "", lastConvertedOutputName = "";
	private boolean argIsMoleculeFileName = true;
	public void setFileNamesAreMolecules(boolean flag) { argIsMoleculeFileName = flag; }
	private char sepChar = '/';

	public InputOutputDirsHelper(String invocationClassName, boolean doesFileCompares) throws IOException
	{



		File projectDir = new File(System.getProperties().getProperty("user.dir"));
		if ( DirIndex.PROJECTDIR.getPath() != null && DirIndex.PROJECTDIR.getPath().length() > 0)
			throw new RuntimeException(" Concurrent modification of Enum value");
		DirIndex.PROJECTDIR.setPath(projectDir);
		// change if the invocation directory is not the project directory

		//
		// BEG find the input directories
		//

		File workingDir;
		String path = projectDir.getAbsolutePath() + sepChar + 
		"src"+sepChar + "test" + sepChar + "resources" + sepChar +
		testInputTopDirName;

		File topTestInputDir = new File(path);
		if (!topTestInputDir.exists())
			throw new IOException("Can't find top level test directory. " + path);

		DirIndex.INPUT_TOPTESTDIR.setPath(topTestInputDir);

		workingDir = new File(
				topTestInputDir.getAbsolutePath() + sepChar + 				
				testInputGenDirName);
		if (!workingDir.exists())
			throw new IOException("Can't find general input test directory. " + workingDir);
		DirIndex.INPUT_GENERALDIR.setPath(workingDir);

		workingDir = new File(
				workingDir.getAbsolutePath() + sepChar +				
				testInputMoleculeDirName);
		if (!workingDir.exists())
			throw new IOException("Can't find molecule test directory: " + workingDir);
		DirIndex.INPUT_MOLECULESDIR.setPath(workingDir);


		workingDir = new File(
				topTestInputDir.getAbsolutePath() + sepChar +				
				invocationClassName);

		if (!workingDir.exists())
			throw new IOException("Can't find class input test directory : " + workingDir);

		DirIndex.INPUT_CLASSTESTDIR.setPath(workingDir);

		if (doesFileCompares)
		{
			workingDir = new File(
					workingDir.getAbsolutePath() + sepChar +				
					compareFilesInputDirName);

			if (!workingDir.exists())
				throw new IOException("Can't find compare files input directory. " + workingDir);

			DirIndex.INPUT_COMPAREFILESDIR.setPath(workingDir);
		}

		//
		// END find the input directories
		// BEG find/create the output directories
		//

		try
		{
			File testoD = new File("target/test-output");
			if ( ! testoD.exists())
				testoD.mkdir();

			File testOutD = new File(testoD + testOutputTopDirName);
			if ( ! testOutD.exists())
				testOutD.mkdir();

			String fullOutputPath = projectDir.getAbsolutePath() + sepChar +
			//"src" + sepChar + "test" + sepChar + "resources" + sepChar +
			"target" + sepChar +
			testOutputTopDirName +
			sepChar + invocationClassName;

			workingDir = new File(fullOutputPath);

			if ( ! workingDir.exists())
				workingDir.mkdir();

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
		File testoD = new File("target/test-output");
		if ( ! testoD.exists())
			testoD.mkdir();


		return DirIndex.OUTPUT_COMPAREFILESDIR.getPath().getAbsolutePath() + sepChar +		
		getTestFileName(inputTestFilename);
	}

	public String getFullExpectedCompareFilePath(String inputTestFileName)
	{
		return DirIndex.INPUT_COMPAREFILESDIR.getPath().getAbsolutePath() + sepChar + 
		getTestFileName(inputTestFileName);
	}

	public String getFullDiffFilePath(String inputTestFileName)
	{
		return DirIndex.OUTPUT_DIFFDIR.getPath().getAbsolutePath() + sepChar +
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
		//File diffFile = new File(getFullDiffFilePath(name));

		if (!expectedFile.exists() || !testOutputFile.exists()) {

			//System.out.println(expectedFile + " vs " + testOutputFile);

			throw new IOException("Input file for compare: \"" +
					(!expectedFile.exists()? expectedFile.getAbsolutePath() : testOutputFile.getAbsolutePath()) +
			"\" does not exist.");
		}

		String expectedFileEsc = expectedFile.getAbsolutePath().replaceAll(" ", "\\\\ ");
		String testOutputFileEsc = testOutputFile.getAbsolutePath().replaceAll(" ", "\\\\ ");
		//String diffFileEsc = diffFile.getAbsolutePath().replaceAll(" ", "\\\\ ");

		String diff = diff(expectedFile,testOutputFile);

		//boolean success = diffFile.exists() && diffFile.length() == 0L;
		boolean success = (diff.length() == 0 );

		if (success) {
			//diffFile.delete();
			// only fails have non-empty diff files -
			// don't leave the empties hanging out there.
		} else {
			System.err.println("These two files are not equal:");
			System.err.println("expected: " + expectedFileEsc);
			System.err.println("tested  : " + testOutputFileEsc);
			System.err.println("diff: " + diff );
		}
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

	private String[] readAll(File f) throws IOException{
		byte[] buffer = new byte[(int) f.length()];
	    BufferedInputStream buf = null;
	    try {
	        buf = new BufferedInputStream(new FileInputStream(f));
	        //buf.read(buffer);
	        GZIPInputStream gzip = new GZIPInputStream(buf);
	        gzip.read(buffer);
	    } finally {
	        if (f != null) try { buf.close(); } catch (IOException ignored) { }
	    }
	    String s= new String(buffer);
	    return s.split(String.format("%n"));
	}
	
	private String diff(File f1, File f2) throws IOException{
		StringWriter writer = new StringWriter();
		String[] x = readAll(f1);
		String[] y = readAll(f2);
		// number of lines of each file
		int M = x.length;
		int N = y.length;

		// opt[i][j] = length of LCS of x[i..M] and y[j..N]
		int[][] opt = new int[M+1][N+1];

		// compute length of LCS and all subproblems via dynamic programming
		for (int i = M-1; i >= 0; i--) {
			for (int j = N-1; j >= 0; j--) {
				if (x[i].equals(y[j]))
					opt[i][j] = opt[i+1][j+1] + 1;
				else 
					opt[i][j] = Math.max(opt[i+1][j], opt[i][j+1]);
			}
		}

		// recover LCS itself and print out non-matching lines to standard output
		int i = 0, j = 0;
		while(i < M && j < N) {
			if (x[i].equals(y[j])) {
				i++;
				j++;
			}
			else if (opt[i+1][j] >= opt[i][j+1]) writer.append("< " + x[i++]+String.format("%n"));
			else                                 writer.append("> " + y[j++]+String.format("%n"));
		}

		// dump out one remainder of one string if the other is exhausted
		while(i < M || j < N) {
			if      (i == M) writer.append("> " + y[j++]+String.format("%n"));
			else if (j == N) writer.append("< " + x[i++]+String.format("%n"));
		}
		
		return writer.toString();
	}

}
