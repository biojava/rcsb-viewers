package org.rcsb.mbt.structLoader;

import org.rcsb.mbt.controllers.scene.PdbToNdbConverter;
import org.rcsb.mbt.glscene.geometry.UnitCell;
import org.rcsb.mbt.model.Structure;
import org.xml.sax.Attributes;


/**
 * Temporary refactor to allow applications to construct app-specific versions of the 
 * structure XML handler.  Expected to go away when the XML handlers are merged.
 * 
 * @author rickb - 16-May-08
 *
 */
public interface IStructureXMLHandler
{
    public abstract Structure getStructure();

	public abstract PdbToNdbConverter getIDConverter();

	public abstract String[] getNonProteinChainIds();
	
	public UnitCell getUnitCell();
}