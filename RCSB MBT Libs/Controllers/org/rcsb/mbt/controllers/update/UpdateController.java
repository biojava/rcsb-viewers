package org.rcsb.mbt.controllers.update;

import java.util.Iterator;
import java.util.Vector;

import org.rcsb.mbt.controllers.update.UpdateEvent.Action;
import org.rcsb.mbt.glscene.jogl.AtomGeometry;
import org.rcsb.mbt.glscene.jogl.BondGeometry;
import org.rcsb.mbt.glscene.jogl.DisplayLists;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.Structure;


/**
 * Contains a list of registered listeners that need to be updated
 * either when the model changes, or when other controllers decide they need
 * to be reset.  Listeners must implement the IUpdateListener
 * interface to receive and act on update notifications.
 * 
 * @author rickb
 *
 */
public class UpdateController
{
	/**
	 * Simplified vector class
	 * @author rickb
	 *
	 */
	public class UpdateListenerVec extends Vector<IUpdateListener>{};	

	private UpdateListenerVec views = new UpdateListenerVec();
	
	public void clear()
	{
		fireUpdateViewEvent(UpdateEvent.Action.CLEAR_ALL);
		views = new UpdateListenerVec();
	}
	/**
	 * Calls 'reset' on all of the panels.
	 */
	public void resetEverything()
	{
		fireUpdateViewEvent(UpdateEvent.Action.VIEW_RESET);
	}

	/**
	 * Add a Viewer to the list of viewers.
	 */
	public void registerListener( final IUpdateListener listener )
		throws IllegalArgumentException
	{
		assert (listener != null );
		assert (!views.contains(listener));

		this.views.add( listener );
		
		fireUpdateViewEvent(UpdateEvent.Action.VIEW_ADDED, listener);
	}

	/**
	 * Remove a Viewer from the list of viewers.
	 */
	public void viewRemoved( final IUpdateListener view )
		throws IllegalArgumentException
	{
		if ( view == null ) {
			throw new IllegalArgumentException( "null viewer" );
		}
		if ( ! this.views.contains( view ) ) {
			throw new IllegalArgumentException( "view not found" );
		}
		
		fireUpdateViewEvent(UpdateEvent.Action.VIEW_REMOVED);

		this.views.remove( view );
	}
	
	/**
	 *  A StructureDocumentEvent needs to be propagated to all Viewer objects.
	 */
	public void fireUpdateViewEvent(final UpdateEvent.Action action, final Structure structure)
	{
		UpdateEvent evt = new UpdateEvent(action, structure);
		fireUpdateViewEvent(evt);
		evt = null;
	}
	
	public void fireUpdateViewEvent(final UpdateEvent.Action action, final IUpdateListener view)
	{
		UpdateEvent evt = new UpdateEvent(action, view);
		fireUpdateViewEvent(evt);
		evt = null;
	}
	
	public void fireUpdateViewEvent( final UpdateEvent.Action action)
	{
		UpdateEvent evt = new UpdateEvent(action);
		evt.action = action;
		fireUpdateViewEvent(evt);
		evt = null;
	}
	
	protected void fireUpdateViewEvent( final UpdateEvent evt )
	{
		for (IUpdateListener view : views)
			view.handleUpdateEvent(evt);
					// what!!!  This isn't firing an event!!
					// this is just an interface call!!!
					//
					// it would be better if it *were* an event, though...
					// think about that...
	}
}
