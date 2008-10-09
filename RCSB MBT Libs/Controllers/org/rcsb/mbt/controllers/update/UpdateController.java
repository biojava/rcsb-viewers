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
	private UpdateListenerVec pending = null;
	private UpdateListenerVec blockedListeners = new UpdateListenerVec();
	
	private boolean inUpdate = false;
	
	public void clear()
	{
		fireUpdateViewEvent(UpdateEvent.Action.CLEAR_ALL);
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
		if (!views.contains(listener))
		{
			if (inUpdate)
						// oops - modifying during an update...
						// can't put on views queue.
						// put in temporary 'pending' queue
			{
				if (pending == null)
					pending = new UpdateListenerVec();
				if (!pending.contains(listener))
					pending.add(listener);
			}
		
			else
			{
				this.views.add( listener );
				fireUpdateViewEvent(UpdateEvent.Action.VIEW_ADDED, listener);
			}
		}
	}
	
	/**
	 * Prevent a listener from receiving events
	 * 
	 * @param listener
	 */
	public void blockListener( final IUpdateListener listener)
	{
		if (views.contains(listener) && !blockedListeners.contains(listener))
			blockedListeners.add(listener);
	}
	
	/**
	 * Listener can resume receiving events
	 * 
	 * @param listener
	 */
	public void unblockListener( final IUpdateListener listener)
	{
		if (blockedListeners.contains(listener))
			blockedListeners.remove(listener);
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
		boolean first = true;
		
		inUpdate = true;
		
		do
		{
			for (IUpdateListener view : views)
			{
				if (blockedListeners.contains(view)) continue;
					// ignore blocked listeners
				
				if (first)
					view.handleUpdateEvent(evt);
					// what!!!  This isn't firing an event!!
					// this is just an interface call!!!
					//
					// it would be better if it *were* an event, though...
					// think about that...
				
				else
					// new components may have been registered as a result of
					// an update.  They will have been put on the 'pending' queue
					// (otherwise, if they're put on the 'views' queue, we get a
					// 'concurrent modification' exception.)
					//
					// after the first pass, the main queue is updated from the
					// pending queue and the next pass initiated.  So, we only
					// want to send the update event to new registrants.  We
					// use the 'pending.contains' function to check that.
					//
					// if more views register in subsequent passes, that's ok -
					// they'll get pushed on to the pending queue (which we aren't
					// traversing) and get handled on the next pass.
					//
					if (pending.contains(view))
					{
						view.handleUpdateEvent(evt);
						pending.removeElement(view);
								// now we can remove the view
					}
			}
			
			// update the pending queue
			//
			if (pending != null)
				if (pending.isEmpty())
					pending = null;
				else
					for (IUpdateListener view : pending)
						views.add(view);
							// transfer references to the views list
							// for the next go round.  Don't remove, yet...
			
			first = false;
							// subsequent passes...
			
		} while (pending != null);
		
		inUpdate = false;
	}
}
