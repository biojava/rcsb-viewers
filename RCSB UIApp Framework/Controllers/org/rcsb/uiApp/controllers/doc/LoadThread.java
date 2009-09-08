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
package org.rcsb.uiApp.controllers.doc;

import java.io.File;

import javax.swing.JOptionPane;

import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.app.ProgressPanelController;

/**
 * Once the action decides to load a structure, we need to get out of the SWT so the
 * progress bar will work properly (more properly, anyway.)
 * 
 * Actions create and start this thread to load the requested structure.
 * 
 * @author rickb
 *
 */
public class LoadThread extends Thread
{
	private String _url, _pdbid;
	
	/**
	 * This can be either a file spec or a url.  It is what comes in from the commandline
	 * or a url specification.
	 * 
	 * @param url - one or more urls, concatenated with a ',' if more than one.
	 */
	public LoadThread(String url)
	{
		_url = url;		
		_pdbid = url.replaceFirst("^.*[/\\\\]([A-Za-z0-9]{4})\\.(xml|pdb[0-9]*)(.gz)*$", "$1");
						// extract the id from the url specification
	}
	
	/**
	 * This is what comes from the 'file open' dialog.  Note that we turn the absolute file
	 * paths and pdbids into a concatenated list of strings.
	 * @param files
	 */
	public LoadThread(File files[])
	{
		_url = "";
		_pdbid = "";
		for (File file: files) {
			_url += file.getAbsolutePath() + ",";
			_pdbid += file.getName().substring(0,file.getName().indexOf('.')) + ", ";
		}
		_url = _url.substring(0, _url.length()-1); // remove last comma
	    _pdbid = _pdbid.substring(0, _pdbid.length()-2);
	}
	
	/**
	 * Converts an array of PDB Ids to a comma-separated string of urls
	 * @param pdbIds list of PDB Ids
	 */
	public LoadThread(String[] pdbIds)
	{
		_url = "";
		_pdbid = "";
	    for (String id: pdbIds) {
	    	_url += "http://www.pdb.org/pdb/files/" + id + ".xml.gz" + ",";
	    	_pdbid += id + ", ";
	    }
	    _url = _url.substring(0, _url.length()-1); // remove last comma
	    _pdbid = _pdbid.substring(0, _pdbid.length()-2);
	}
	
	public LoadThread(String url, String pdbid)
	{ _url = url; _pdbid = pdbid; }
	
	public void run()
	{
		ProgressPanelController.StartProgress();
		AppBase.sgetDocController().loadStructure(_url, _pdbid);

		if (AppBase.sgetModel().hasStructures())
			AppBase.sgetActiveFrame().setTitle(_pdbid);
		ProgressPanelController.EndProgress();
		if (!AppBase.sgetModel().hasStructures())
			JOptionPane.showMessageDialog(null, "Structure not found: " + _pdbid + "\nPlease check file/url specification and try again.", "Error", JOptionPane.ERROR_MESSAGE); 
	}
};
