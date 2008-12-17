package org.rcsb.ks.controllers.doc;

import org.rcsb.mbt.structLoader.IStructureLoader;
import org.rcsb.vf.controllers.doc.VFDocController;


public class KSDocController extends VFDocController
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
}
