package org.rcsb.mbt.ui.dialogs;

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
		
		Dimension mySize = new Dimension(300, 100);
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
	