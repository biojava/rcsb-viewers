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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * Simple class to traverse a tree from some directory and apply an action to the
 * files encountered in the traversal.  (Odd we have to write one of these - oh, well)
 * 
 * @author rickb
 *
 */
public class ApplyDirTreeAction
{
	/**
	 * Implement this to provide an action.
	 * 
	 * @author rickb
	 *
	 */
	public interface DirTreeAction
	{
		public void apply(File file) throws IOException;
	}
	
	/**
	 * Defines a filter that will be used in searches.
	 * @author rickb
	 *
	 */
	private class AcceptFilter implements FileFilter
	{
		private String cards[] = null;
		
		AcceptFilter(String _strFilter)
		{
			if (!(_strFilter == null || _strFilter.equals("*")))
				cards = _strFilter.split("\\*");			
		}
		
		/**
		 * Accept or deny.
		 */
		public boolean accept(File pathname) 
		{
			return pathname.isFile() && wildMatch(pathname.getName());
		}
		
		//
		// adapted from: http://www.adarshr.com/papers/wildcard
		//
		private boolean wildMatch(String matchString)
		{
			if (cards == null) return true;
			
			int ix = -1;
			for (String card : cards)
			{
				ix = matchString.indexOf(card);
				
				if (ix == -1)
					return false;
				
				matchString = matchString.substring(ix + card.length());
			}
			
			return true;
		}		
	}
	
	private FileFilter fileAcceptFilter = null;
	private FileFilter dirAcceptFilter = null;
	private int level = -1;
	public int getLevel() { return level; }
	
	/**
	 * Specify a filter using wildcard spec.  Default is "*";
	 * @param wildcard
	 */
	public void setFileAcceptFilter(String wildcard)
	{
		fileAcceptFilter = new AcceptFilter(wildcard);
	}
	
	/**
	 * Specify your own dir filter
	 * @param _acceptFilter
	 */
	public void setDirAcceptFilter(FileFilter _acceptFilter)
	{
		dirAcceptFilter = _acceptFilter;
	}
	
	/**
	 * Specify your own file filter
	 * @param _acceptFilter
	 */
	public void setFileAcceptFilter(FileFilter _acceptFilter)
	{
		fileAcceptFilter = _acceptFilter;
	}
	
	/**
	 * Do a depth first search of the dir tree and apply actions.
	 * 
	 * @param topLevel
	 * @param action
	 * @param filter
	 */
	public void run(File topLevel, DirTreeAction action) throws IOException
	{
		level++;
		if (dirAcceptFilter == null)
			dirAcceptFilter = new FileFilter()
			{
				public boolean accept(File pathname)
				{ return pathname.isDirectory(); }
			};
			
		File[] files = topLevel.listFiles(dirAcceptFilter);
		
		for (File file : files)
			run(file, action);
						// run subdirs, first
		
		files = topLevel.listFiles(fileAcceptFilter);
		for (File file : files)
			action.apply(file);
						// now get files against the accept filter and
						// apply the action.
		
		level--;
	}
}
