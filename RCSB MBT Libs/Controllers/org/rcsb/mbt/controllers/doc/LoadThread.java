package org.rcsb.mbt.controllers.doc;

import java.io.File;

import javax.swing.JOptionPane;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.app.ProgressPanelController;

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
	 * paths into a concatenated list of strings, like above, and then extract the pdb id
	 * from the first file name
	 * @param files
	 */
	public LoadThread(File files[])
	{
		_url = files[0].getAbsolutePath();
		for (int ixFile = 1; ixFile < files.length; ixFile++)
			_url += ',' + files[0].getAbsolutePath();
		_pdbid = files[0].getName().substring(0,files[0].getName().indexOf('.'));
						// extract the id from the file specification
	}
	
	public LoadThread(String url, String pdbid)
	{ _url = url; _pdbid = pdbid; }
	
	public void run()
	{
		ProgressPanelController.StartProgress();
		AppBase.sgetDocController().loadStructure(_url, _pdbid);
		if (AppBase.sgetModel().hasStructures())
			AppBase.sgetActiveFrame().setTitle(AppBase.sgetModel().getStructures().get(0).getStructureMap().getPdbId());
		ProgressPanelController.EndProgress();
		if (!AppBase.sgetModel().hasStructures())
			JOptionPane.showMessageDialog(null, "Structure not found: " + _pdbid + "\nPlease check file/url specification and try again.", "Error", JOptionPane.ERROR_MESSAGE); 
	}
};
