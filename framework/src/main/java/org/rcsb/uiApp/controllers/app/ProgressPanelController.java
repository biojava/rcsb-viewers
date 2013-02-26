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
package org.rcsb.uiApp.controllers.app;

import java.awt.Cursor;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.rcsb.mbt.model.util.DebugState;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.uiApp.ui.dialogs.ProgressPanel;


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
public class ProgressPanelController
{

	private static ProgressPanel progressDlg = null;
	private static JFrame parent = null;
	

	/**
	 * Call to start progress - optionally without a parent JFrame
	 */
	public static void StartProgress() { 
		
		if (DebugState.isDebug()){
			System.err.println("ProgressPanelController.StartProgress");
		}
		StartProgress(null); }

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
	
		if (DebugState.isDebug()){
			System.err.println("ProgressPanelController.StartProgress(jframe)");
		}
		
		
		/**
		 * Set up the threading
		 */
		if (SwingUtilities.isEventDispatchThread()) InternalStart();
		else
			try
			{
				SwingUtilities.invokeLater(
					new Runnable()
					{
						public void run() { InternalStart(); }
					});
			} catch (Exception e) {
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
		
		if (DebugState.isDebug()){
			System.err.println("ProgressPanelController.EndProgress");
		}
		
		
		/**
		 * set up the threads
		 */
		if (SwingUtilities.isEventDispatchThread()) InternalEnd();
		else
			try
			{
				SwingUtilities.invokeLater(
					new Runnable()
					{
						public void run() { InternalEnd(); }
					});
			
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	/*
	 * Does the real work.
	 */
	public static void InternalEnd()
	{
		if (DebugState.isDebug()){
			System.err.println("ProgressPanelController.InternalEnd");
		}
		
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
