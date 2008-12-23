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
package org.rcsb.uiApp.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.rcsb.mbt.model.util.StatusEvent;
import org.rcsb.mbt.model.util.StatusListener;

/**
 * UI to show progress.
 * Controlled by org.rcsb.mbt.ui.Controllers.ProgressPanelController,
 * registered with org.rcsb.mbt.model.util.Status
 *<p>
 * We want this to be updated either from within our outside the SDT, so we test the update
 * mechanisms (and create and destroy, as well) to determine whether or no we are in the SDT,
 * and act accordingly.</p>
 * <p>
 * If updated from the SDT, we lose the animations (on some systems - Mac, most notably), so
 * updates should be presented frequently.</p>
 * 
 * <b>Notes</b>
 * <ul>
 * <li>You can interchange between determinate or indeterminate at will.</li>
 * <li>You can interchange between SDT or non-SDT threads at will.</li>
 * </ul>
 */
@SuppressWarnings("serial")
public class ProgressPanel extends JDialog implements StatusListener
{
	private JProgressBar progressBar;
	private JLabel taskOutput;
	private Integer progress = 0;
	public int getProgress() { return progress; }
	private String info = new String();
	private Boolean indeterminate = false;
			
	/**
	 * Constructor - shouldn't be called from the application.
	 * Use 'StartProgress()' to get a progress dialog.
	 */
	public ProgressPanel(JFrame parent)
	{
		super(parent, false);
		
		Dimension mySize = new Dimension(300, 130);
		Dimension parentSize = (parent == null)? Toolkit.getDefaultToolkit().getScreenSize() :
											     parent.getSize();
		Point parentLoc = (parent == null)? new Point(0, 0) : parent.getLocation();
		Point myLoc = new Point(parentLoc.x + ((parentSize.width - mySize.width) / 2),
								   parentLoc.y + ((parentSize.height - mySize.height) / 2));

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		taskOutput = new JLabel();
		
		this.setBackground(Color.gray);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(progressBar, BorderLayout.PAGE_START);
		panel.add(taskOutput, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(panel);
		
		setSize(mySize);
		setLocation(myLoc);
		
		this.setAlwaysOnTop(true);
		setTitle("Progress");
		
		setVisible(true);
	}
	
	/*
 	 * if indeterminate, don't show a progress value (varies according to system.)
 	 * 
	 * @param flag
	 */
	private void setIndeterminate(boolean flag)
	{
		synchronized(indeterminate) { indeterminate = flag; }
		if (SwingUtilities.isEventDispatchThread())
		{
			internalSetIndeterminate();
			update(getGraphics());
		} else
			try {
				SwingUtilities.invokeAndWait(
					new Runnable()
					{
						public void run() { internalSetIndeterminate(); }
					});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
	}
	
	/*
	 * Do the actual work
	 */
	private void internalSetIndeterminate()
	{
		if (progressBar.isIndeterminate() != indeterminate)
			progressBar.setIndeterminate(indeterminate);
	}
	
	/*
	 * Progress update.  If in_progress < 0 ( -1 ), put the progress bar in
	 * 'indeterminate' mode.
	 */
	private void updateProgress(int in_progress, String in_info)
	{
		synchronized(progress) { progress = in_progress; }	
		synchronized(info){ info = in_info == null? "" : in_info; }
		
		if (SwingUtilities.isEventDispatchThread())
		{
			internalUpdateProgress();
			update(getGraphics());
		} else
			try {
				SwingUtilities.invokeAndWait(
					new Runnable()
					{
						public void run() { internalUpdateProgress(); }
					});
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
	}
	
	/*
	 * Do the actual work.
	 */
	private void internalUpdateProgress()
	{
		progressBar.setValue(progress);
		taskOutput.setText(info);
	}
	
	/**
	 * Implementation of StatusListener interface.
	 */
	public void processStatusEvent(StatusEvent statusEvent)
	{
		if (statusEvent.type == StatusEvent.TYPE_PROGRESS)
		{
			setIndeterminate(statusEvent.percent < 0);
			updateProgress(statusEvent.percent, statusEvent.message);
		}
	}
}
	
