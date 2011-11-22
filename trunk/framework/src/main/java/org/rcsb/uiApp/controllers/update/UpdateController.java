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
package org.rcsb.uiApp.controllers.update;

import java.util.Vector;

import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.Surface;
import org.rcsb.mbt.model.util.DebugState;


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
	private class UpdatePendingInfo
	{
		public IUpdateListener view;
		public UpdateEvent.Action action;

		public UpdatePendingInfo(UpdateEvent.Action _action, IUpdateListener _view) {action = _action; view = _view; }
	}

	@SuppressWarnings("serial")
	public class UpdateListenerVec extends Vector<IUpdateListener>{};
	
	@SuppressWarnings("serial")
	private class UpdatePendingEventsVec extends Vector<UpdatePendingInfo>
	{
		@Override
		public synchronized boolean add(UpdatePendingInfo info)
		{
			for (UpdatePendingInfo check : this)
				if (compare(check, info)) return false;
			
			return super.add(info);
		}
		
		private boolean compare(Object _l, Object _r)
		{
			UpdatePendingInfo lInfo = (UpdatePendingInfo)_l;
			UpdatePendingInfo rInfo = (UpdatePendingInfo)_r;
			
			return (lInfo.action == rInfo.action && lInfo.view == rInfo.view);
		}
		
	};
	
	private UpdateListenerVec views = new UpdateListenerVec();
	private UpdateListenerVec blockedListeners = new UpdateListenerVec();
	private UpdatePendingEventsVec pending = null;
	
	private UpdatePendingEventsVec getPending()
	{
		if (pending == null)
			pending = new UpdatePendingEventsVec();
		return pending;
	}
	
	private int inUpdate = 0;
	
	/**
	 * Clean out all the structures
	 */
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
			if (inUpdate > 0)
				getPending().add(new UpdatePendingInfo(UpdateEvent.Action.VIEW_ADDED, listener));
						// oops - modifying during an update...
						// can't put on views queue.
						// put in temporary 'pending' queue
		
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
		
		if (inUpdate > 0)
			getPending().add(new UpdatePendingInfo(UpdateEvent.Action.VIEW_REMOVED, view));

		else
		{
			this.views.remove( view );
			fireUpdateViewEvent(UpdateEvent.Action.VIEW_REMOVED);
		}
	}
	
	/**
	 *  A StructureDocumentEvent needs to be propagated to all Viewer objects.
	 */
	public void fireUpdateViewEvent(final UpdateEvent.Action action, final Structure structure)
	{
		UpdateEvent evt = new UpdateEvent(action, structure);
		fireUpdateEvent(evt);
		evt = null;
	}
	
	public void fireUpdateViewEvent(final UpdateEvent.Action action, final Surface surface)
	{
		UpdateEvent evt = new UpdateEvent(action, surface);
		fireUpdateEvent(evt);
		evt = null;
	}
	
	public void fireUpdateViewEvent(final UpdateEvent.Action action, final IUpdateListener view)
	{
		UpdateEvent evt = new UpdateEvent(action, view);
		fireUpdateEvent(evt);
		evt = null;
	}
	
	public void fireUpdateViewEvent( final UpdateEvent.Action action)
	{
		UpdateEvent evt = new UpdateEvent(action);
		evt.action = action;
		fireUpdateEvent(evt);
		evt = null;
	}
	
	public void fireUpdateEvent( final UpdateEvent evt )
	{
		inUpdate++;
		
		for (IUpdateListener view : views)
		{
			if (blockedListeners.contains(view)) continue;
				// ignore blocked listeners
			
	//		System.out.println("UpdateController: fireUpdateEvent: " + view);
			view.handleUpdateEvent(evt);
				// what!!!  This isn't firing an event!!
				// this is just an interface call!!!
				//
				// it would be better if it *were* an event, though...
				// think about that...
		}
		
		/*
		 * Ok, now handle the pending events que (note we wait until we back out of all recursions
		 * before doing this.
		 * 
		 * Currently, the only events pushed onto the pending queue are those that would modify the
		 * views list.  That is VIEW_ADDED, and VIEW_REMOVED.
		 */
		if (inUpdate == 1)
		{
			while (pending != null)
			{
				UpdatePendingEventsVec processPending = pending;
				pending = null;
								// shift over to temp variable and clear the pending.
								// it can be reinitialized and receive more events while
								// we're processing the current set.

				for (UpdatePendingInfo pendingInfo : processPending)
				{
					if (pendingInfo.action == UpdateEvent.Action.VIEW_ADDED)
						views.add(pendingInfo.view);
					
					else if (pendingInfo.action == UpdateEvent.Action.VIEW_REMOVED)
						views.remove(pendingInfo.view);
					
					this.fireUpdateViewEvent(pendingInfo.action, pendingInfo.view);
				}
			}
		}
		
		if (--inUpdate < 0)
			inUpdate = 0;
							// no lower than zero
		
		if (DebugState.isDebug() && evt.action == UpdateEvent.Action.CLEAR_ALL && inUpdate == 0)
		{
			System.gc();
			System.err.println("Update CLEAR_ALL sent.  Memory Used: " + 
					(Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory()));
		}
	}
}
