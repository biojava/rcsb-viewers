package org.rcsb.ks.controllers.doc;

import org.rcsb.ks.controllers.app.KioskViewer;
import org.rcsb.mbt.controllers.doc.DocController;
import org.rcsb.mbt.structLoader.IStructureLoader;


public class KSDocController extends DocController
{
	/**
	 * returns a structureXMLHandler from our local namespace
	 * 
	 * @param dataset
	 * @return
	 */
	protected IStructureLoader createStructureXMLHandler(String dataset)
	{
		return new KSStructureXMLHandler(dataset);
	}
	
	@Override
	public void loadStructure(String url, String pdbId)
	{
		KioskViewer.getApp().reset();
		super.loadStructure(url, pdbId);
	}
}
