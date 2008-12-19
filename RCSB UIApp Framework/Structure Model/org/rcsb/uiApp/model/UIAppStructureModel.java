package org.rcsb.uiApp.model;

import org.rcsb.mbt.model.Structure;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.update.UpdateController;
import org.rcsb.uiApp.controllers.update.UpdateEvent;

/**
 *  When a Structure object is added or removed from a StructureDocument,
 *  each registered Viewer will automatically receive a StructureDocumentEvent
 *  telling the Viewer that a given Structure was added (or removed as the case
 *  may be). It is then the responsibility of the Viewer to use the
 *  Structure object reference to display a suitable visual representation
 *  (often this entails getting the StructureMap and then the StructureStyles
 *  object in order to display a representation using the defined colors
 *  and other visual display properties).
 * @author rickb
 *
 */
public class UIAppStructureModel extends org.rcsb.mbt.model.StructureModel
{

	/**
	 * Add a structure.
	 */
	@Override
	public synchronized void addStructure(Structure structure)
			throws IllegalArgumentException {
		super.addStructure(structure);

		UpdateController update = AppBase.sgetUpdateController();
		update.fireUpdateViewEvent(UpdateEvent.Action.STRUCTURE_ADDED, structure);
	}

	/**
	 * Remove a structure
	 */
	@Override
	public synchronized void removeStructure( final Structure structure )
	throws IllegalArgumentException
	{
		if ( structure == null ) {
			throw new IllegalArgumentException( "null structure" );
		}
		if ( ! this.structures.contains( structure ) ) {
			throw new IllegalArgumentException( "structure not found" );
		}
		
		UpdateController update = AppBase.sgetUpdateController();
		update.fireUpdateViewEvent(UpdateEvent.Action.STRUCTURE_REMOVED, structure);
	
		this.structures.remove( structure );
	}
	
	/**
	 * Set an array of structures (happens on load.)
	 */
	@Override
	public synchronized void setStructures(Structure[] structure_array)
	{
		if (structure_array != null)
		{
			UpdateController update = AppBase.sgetUpdateController();
			
			for (Structure struc : structure_array)
			{
			   structures.add(struc);
			   update.fireUpdateViewEvent(UpdateEvent.Action.STRUCTURE_ADDED, struc);
			}
			
			update.fireUpdateViewEvent(UpdateEvent.Action.VIEW_UPDATE);
		}
	}
}
