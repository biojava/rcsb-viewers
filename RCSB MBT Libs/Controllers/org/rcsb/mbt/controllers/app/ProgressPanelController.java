package org.rcsb.mbt.controllers.app;

import java.awt.Cursor;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.rcsb.mbt.model.util.Status;
import org.rcsb.mbt.ui.dialogs.ProgressPanel;


/**
 * Progress handler in dialog form.  Brings up the progress dialog, or puts it away.  The dialog is non-modal and always stays on top.
 * <p>
 * The progress dialog is a singleton in this class, since you would only want one up at a time.</p>
 *
 * <b>Usage</b>
 * <ul>
 * <li>Call <code>ProgressDlg.StartProgress()</code> to bring up the dialog</li>
 * <li>Code calls Status.progress(int percent, String "Message")</li>
 * <li>Call <code>ProgressDlg.EndProgress()</code> when you're done.</li>
 * </ul>
 * 
 * <b>Notes</b>
 * <ul>
 * <li>You can interchange between determinate or indeterminate at will.</li>
 * <li>You can interchange between SDT or non-SDT threads at will.</li>
 * </ul>
 * 
 */
@SuppressWarnings("serial")
public class ProgressPanelController
{

	private static ProgressPanel progressDlg = null;
	private static JFrame parent = null;
	

	/**
	 * Call to start progress - optionally without a parent JFrame
	 */
	public static void StartProgress() { StartProgress(null); }

	/**
	 * Call to start progress - with parent JFrame.
	 * 
	 * This will create the progress dialog and position it in the center of the parent.
	 * The progress dialog is always on top of other windows.
	 * 
	 * @param in_parent - the parent of the progress dialog.  Can be null.
	 */
	public static void StartProgress(JFrame in_parent)
	{
		parent = in_parent;
		
		/**
		 * Set up the threading
		 */
		if (SwingUtilities.isEventDispatchThread()) InternalStart();
		else
			try
			{
				SwingUtilities.invokeAndWait(
					new Runnable()
					{
						public void run() { InternalStart(); }
					});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}				
	}
	
	/*
	 * Called from proper thread to do actual work
	 */
	private static void InternalStart()
	{
		progressDlg = new ProgressPanel(parent);
		Status.addStatusListener(progressDlg);
		if (parent != null)
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	/**
	 * Called when the task is done.
	 * 
	 * Closes and deletes the dialog.
	 */
	public static void EndProgress()
	{
		/**
		 * set up the threads
		 */
		if (SwingUtilities.isEventDispatchThread()) InternalEnd();
		else
			try
			{
				SwingUtilities.invokeAndWait(
					new Runnable()
					{
						public void run() { InternalEnd(); }
					});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
	}
	
	/*
	 * Does the real work.
	 */
	public static void InternalEnd()
	{
		Status.removeStatusListener(progressDlg);
		if (progressDlg != null)
		{
			progressDlg.setVisible(false);
			progressDlg = null;
			if (parent != null)
				parent.setCursor(null);
		}
	}
}