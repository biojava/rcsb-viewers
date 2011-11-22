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
package org.rcsb.ex.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Embeds the legal statement found in 'LegalStatementHeader' in java source files.
 * If a statement already exists, removes that and replaces with this.  If a creation date
 * is found, it tries to preserve it.  Otherwise, the creation date is the date the header
 * was added.
 * 
 * Can be run anytime to pick up newly submitted source, change the legal statement wording,
 * etc.  See the '@args' item in the main to get arguments.  Is expected to run from the
 * project root.
 * 
 * @author rickb
 *
 */
public class EmbedLegalStatement
{
	/**
	 * Contains the header recognition strings.  Right now, any found will qualify the string.
	 */
	static final String searchStrings[] = new String[] {
	};
	
	static final String dirNames[] = new String[] {
		"RCSB Excluded",
		"RCSB Demos",
		"RCSB MBT Libs",
		"RCSB Ligand Explorer",
		"RCSB PDB Kiosk",
		"RCSB Protein Workshop",
		"RCSB Simple Viewer",
		"RCSB UIApp Framework",
		"RCSB Viewer Framework",
		"TestBed"
	};
	
	static char legalStatement[];
	static int legalStatementDateInsertionStart = -1, legalStatementResidualAfterDate = -1;
	
	static boolean ignoreEmptyBuffers;
	
	static String today, date;
	
	static ApplyDirTreeAction dirAction;
		
	/**
	 * Action to apply in traverser.
	 * 
	 * @author rickb
	 *
	 */
	public static class EmbedLegalStatementAction implements ApplyDirTreeAction.DirTreeAction
	{
		/**
		 * Contains the file strings to be checked and re-output (source file).
		 * Has a built-in line number that gets updated when nextLine is called.
		 * 
		 * @author rickb
		 *
		 */
		@SuppressWarnings("serial")
		private class LineBuffer extends ArrayList<String>
		{
			int lineNo = -1;
			boolean inComment = false;
			
			void start() { lineNo = -1; }
			
			/**
			 * Access and keep track with our internal line number
			 * @return - the string at the current line number or null if end
			 */
			String nextLine()
			{
				lineNo++;
				if (lineNo < size())
					return get(lineNo);
				
				else return null;
			}
			
			/**
			 * Move the current line number back the requested number, or zero if it decrements
			 * below 0.
			 * 
			 * @param nBack
			 */
			void moveBack(int nBack)
			{
				lineNo = Math.max(-1, lineNo - nBack);
			}
		}
		
		private LineBuffer buf;
		
		/*
		 * Contains the start and ending lines of the discovered legal header, if any
		 * 
		 * @author rickb
		 *
		 */
		private class Range
		{
			int start, end;
			Range(int _start, int _end) { start = _start; end = _end; }
			boolean isValid() { return start != -1 && end != -1; }
		}
		
		/**
		 * Function that gets applied when the traverser calls it.
		 * Can also call it directly.
		 * 
		 * @args file - a file object containing the input file specification to check
		 * 				and rewrite
		 */
		public void apply(File file) throws IOException
		{
			System.out.println("Applying to: " + file.getPath());
			buf = new LineBuffer();
			String line;
						
			date = today;
			
			BufferedReader rdr = new BufferedReader(new FileReader(file));
			while ((line = rdr.readLine()) != null)
				buf.add(line);
			rdr.close();
							// suck in the file
			
			Range currentStatementRange = getCurrentStatementRange();
							// find the range of lines containing the current statement,
							// if there is one.
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
							// reopen the file for writing
			
			writer.write(legalStatement, 0, legalStatementDateInsertionStart);
			writer.write(date);
			writer.write(legalStatement, legalStatementDateInsertionStart + 4, legalStatementResidualAfterDate);
			writer.write("\n");
							// blop out the legal statement with a dated timestamp, first thing

			int ix, ixStart = 0;
			if (currentStatementRange.isValid())
							// we found a statement - write out everything but that.
			{
				ignoreEmptyBuffers = true;
				for (ix = 0; ix < currentStatementRange.start; ix++)
					outputLine(writer, buf.get(ix));
				
				ixStart = currentStatementRange.end + 1;
			}

			ignoreEmptyBuffers = true;
			for (ix = ixStart; ix < buf.size(); ix++)
					outputLine(writer, buf.get(ix));
							// write out the rest of the file (or all of the file, if
							// no legal statement found.)
			
			writer.close();
		}
		
		void outputLine(BufferedWriter writer, String line) throws IOException
		{
			if (ignoreEmptyBuffers)
				for (int ix = 0; ix < line.length(); ix++)
					if (!Character.isWhitespace(line.charAt(ix)))
						ignoreEmptyBuffers = false;
								// first non-empty line will turn off the ignore flag

			if (!ignoreEmptyBuffers)
				writer.write(line + "\n");
		}
		
		/*
		 * Search for the current statement, and set the line ranges, if discovered.
		 * Otherwise, will return an invalid range.
		 */
		private Range getCurrentStatementRange()
		{
			int commentStartIX = -1;
			int commentEndIX = -1;
			
			String line = "";
			for (buf.start(); line != null; line = buf.nextLine())
			{
				if (isCommentLine(line))
				{
					commentStartIX = buf.lineNo;
					commentEndIX = scanCommentBlock();
					
					if (commentEndIX != -1) break;

					commentStartIX = -1;
				}
			}
			
			return new Range(commentStartIX, commentEndIX);
		}
		
		/*
		 * Called when a comment line is found - scans the block of comments
		 * for the searchString lines.  
		 * 
		 * @returns - the last line number of the block or -1 if no tokens found
		 */
		private int scanCommentBlock()
		{
			boolean foundIt = false;
			String line;
			buf.moveBack(1);
			while ((line = buf.nextLine()) != null)
			{
				if (!isCommentLine(line))
					return (foundIt)? buf.lineNo - 1 : -1;
			
				if (scanLineForTokens(line)) foundIt = true;
			}
			return -1;
		}
		
		/*
		 * Scan a line for the search strings.
		 * 
		 * @returns - true if any match, otherwise false
		 */
		private boolean scanLineForTokens(String line)
		{
			if (line.contains("$Id:") || line.contains("Created on"))
			{
				date = line.replaceAll(".*([0-9]{4}\\/[0-9]{2}\\/[0-9]{2}).*", "$1");
				return true;
			}
			
			else
				for (String sequence : searchStrings)
					if (line.contains(sequence)) return true;
			
			return false;
		}
		
		/*
		 * Checks to see if this is a comment line.  Keeps track of comment blocks.
		 * 
		 * @returns - true if the line is a comment line
		 */
		private boolean isCommentLine(String line)
		{			
			for (int ix = 0; ix < line.length(); ix++)
			{
				char cc = line.charAt(ix);
				if (!Character.isWhitespace(cc))
				{
					if (buf.inComment)
					{
						if (cc == '*' && nextCharMatches(line, ix, '/'))
						{
						  buf.inComment = false;
						  return true;
						}
						
						else return true;
					}
				
					else if (cc == '/')
					{
						if (nextCharMatches(line, ix, '*'))
							buf.inComment = true;
						
						else if (nextCharMatches(line, ix, '/'))
							return true;
					}
				}
			}
		
			return buf.inComment;
		}
			
		/*
		 * Look ahead - see if the next token in the string matches the
		 * provided char.
		 * 
		 * @args line - the string to check
		 * @args ix - the index from which to check (ix + 1)
		 * @args cc - the char to check
		 */
		private boolean nextCharMatches(String line, int ix, char cc)
		{
			ix++;
			return ix < line.length() && line.charAt(ix) == cc;
		}
	}
	
	/*
	 * Load the legal statement from the provided file name
	 */
	private static void loadLegalStatement(final String fn) throws IOException
	{
		File inFile = new File(fn);

		legalStatement = new char[(int)inFile.length()];
		
		FileReader rdr = new FileReader(fn);
		rdr.read(legalStatement);
		rdr.close();	
		
		for (int ix = 0; ix < legalStatement.length; ix++)
			if (legalStatement[ix] == 'D' && legalStatement[ix+1] == 'A' && legalStatement[ix+2] == 'T' && legalStatement[ix+3] == 'E')
			{
				legalStatementDateInsertionStart = ix;
				legalStatementResidualAfterDate = legalStatement.length - legalStatementDateInsertionStart - 4;
			}
	}
	
	/**
	 * The directory acceptance filter.  Note it is sensitive to traversal levels.
	 * '.svn' directories never pass.
	 * 
	 * @author rickb
	 *
	 */
	static class DirAcceptFilter implements FileFilter
	{
		public boolean accept(File pathname)
		{
			if (pathname.isDirectory() && !pathname.getName().equals(".svn"))
			{
				switch(dirAction.getLevel())
				{
				case 0:		// level zero is the top-level directories listed in dirNames, above.
					for (String dir : dirNames)
						if (pathname.getName().equals(dir))
							return true;
					break;
					
				case 1:		// level one can have a 'bin' or 'doc*' path - ignore
					if (!(pathname.getName().equals("bin") || pathname.getName().startsWith("doc")))
						return true;
					break;
					
				default:	// all other levels return true if directory
					return true;
				}
			}
			
			return false;	// not a directory or directory is '.svn' directory
		}
	}
	
	/**
	 * @param args
	 *   '-' - scan and apply to all .java files from the directory above this project directory. 
	 *   filename - a filename to scan (should be in the project directory.)
	 */
	public static void main (String args[])
	{
		try
		{
			SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
			df.applyPattern("yyyy/MM/dd");
			today = df.format(new Date());

			loadLegalStatement("LegalStatement.txt");
			EmbedLegalStatementAction action = new EmbedLegalStatementAction();
			if (args[0].equals("-"))
							// traverse and apply to all
			{
				File workingDir = new File(System.getProperty("user.dir"));
				workingDir = workingDir.getParentFile();
				
				dirAction = new ApplyDirTreeAction();
				dirAction.setDirAcceptFilter(new DirAcceptFilter());
				dirAction.setFileAcceptFilter("*.java");
				dirAction.run(workingDir, action);
			}
			
			else
				action.apply(new File(args[0]));
							// run against a single file
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
