//  $Id: ProgressDialog.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
//
//  Copyright 2000-2004 The Regents of the University of California.
//  All Rights Reserved.
//
//  Permission to use, copy, modify and distribute any part of this
//  Molecular Biology Toolkit (MBT)
//  for educational, research and non-profit purposes, without fee, and without
//  a written agreement is hereby granted, provided that the above copyright
//  notice, this paragraph and the following three paragraphs appear in all
//  copies.
//
//  Those desiring to incorporate this MBT into commercial products
//  or use for commercial purposes should contact the Technology Transfer &
//  Intellectual Property Services, University of California, San Diego, 9500
//  Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815,
//  FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
//
//  IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
//  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//  LOST PROFITS, ARISING OUT OF THE USE OF THIS MBT, EVEN IF THE
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//  THE MBT PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
//  UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
//  UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
//  NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
//  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
//  MBT WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
//
//  For further information, please see:  http://mbt.sdsc.edu
//
//  History:
//  $Log: ProgressDialog.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.3  2004/10/27 20:03:53  moreland
//  Corrected javadoc SEE references.
//
//  Revision 1.2  2004/08/16 16:28:51  moreland
//  Improved drawing performance.
//
//  Revision 1.1  2004/07/07 23:48:39  moreland
//  Added new Dialog wrapper classes from Dave for several GUI panels.
//


package org.rcsb.pw.ui.dialogs;


// GUI
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import org.rcsb.mbt.model.util.*;

// MBT





/**
 * A ProgressDialog manages a dialog window illustrating the progress
 * for a task that takes awhile.  The window shows a general message,
 * a progress (note) message, and a progress bar that fills in as the task
 * proceeds.
 * <P>
 * By default, the dialog listens to status events from the MBT Status
 * class.  Those events set the progress message and bar location.
 * Status event listening can be disabled by calling setStatusMonitored().
 * <P>
 * The progress bar area normally shows a bar that extends from left
 * to right as the percentage value grows from 0% to 100%.  This can be
 * used to indicate how much of a task has been completed.  Alternately,
 * for tasks with an indeterminate completion time, the progress bar can
 * be set into an 'indeterminate' mode in which the bar animates dot
 * back and forth in a continuous cycle until the task is completed.
 *
 * @author	David R. Nadeau / UCSD
 * @see		org.rcsb.mbt.model.util.Status
 */
public class ProgressDialog
	extends JDialog
	implements StatusListener
{
/**
	 * 
	 */
	private static final long serialVersionUID = 7879449801723534426L;

	//----------------------------------------------------------------------
//  Fields
//----------------------------------------------------------------------
	/**
	 * The parent frame.
	 */
	protected Window parent = null;

	/**
	 * The progress bar.
	 */
	protected JProgressBar progressBar = null;

	/**
	 * The message label.
	 */
	protected JLabel messageLabel = null;

	/**
	 * The progress note label.
	 */
	protected JLabel noteLabel = null;

	/**
	 * The progress bar percentage.
	 */
	protected float percent = 0.0f;

	/**
	 * True if the dialog is listening for events on the Status
	 * class in MBT.
	 */
	protected boolean statusMonitored = true;





//----------------------------------------------------------------------
//  Constructors
//----------------------------------------------------------------------
	/**
	 * Create a non-modal progress dialog box with the given parent, title,
	 * and general message.  The progress note is initialized to
	 * empty, and the bar set to 0%.
	 * <P>
	 * This constructor does not require a parent argument to specify
	 * a parent window or dialog for this dialog.  As a result, this
	 * dialog is always non-modal - it does not block interaction with
	 * the rest of the application.
	 * <P>
	 * By default, the dialog box monitors status events on the
	 * MBT Status class, and updates the note and progress
	 * accordingly.  Status monitoring can be disabled by calling
	 * the setStatusMonitored() method.
	 *
	 * @param	title		the dialog's title
	 * @param	message		the general message
	 */
	public ProgressDialog( final String title, final String message )
	{
		super( );			// Always non-modal
		this.parent = null;

		this.initialize( title, message );
	}

	/**
	 * Create a modal progress dialog box with the given parent, title,
	 * and general message.  The progress note is initialized to
	 * empty, and the bar set to 0%.
	 * <P>
	 * The dialog box is modal by default, blocking interaction with
	 * all other windows and dialogs that are children of the given
	 * frame parent.
	 * <P>
	 * By default, the dialog box monitors status events on the
	 * MBT Status class, and updates the note and progress
	 * accordingly.  Status monitoring can be disabled by calling
	 * the setStatusMonitored() method.
	 *
	 * @param	parent		the parent window
	 * @param	title		the dialog's title
	 * @param	message		the general message
	 */
	public ProgressDialog( final Frame parent, final String title, final String message )
	{
		super( parent, true );		// Always modal
		this.parent = parent;

		this.initialize( title, message );
	}

	/**
	 * Create a modal progress dialog box with the given parent, title,
	 * and general message.  The progress note is initialized to
	 * empty, and the bar set to 0%.
	 * <P>
	 * The dialog box is modal by default, blocking interaction with
	 * all other windows and dialogs that are children of the given
	 * frame parent.
	 * <P>
	 * By default, the dialog box monitors status events on the
	 * MBT Status class, and updates the note and progress
	 * accordingly.  Status monitoring can be disabled by calling
	 * the setStatusMonitored() method.
	 *
	 * @param	parent		the parent window
	 * @param	title		the dialog's title
	 * @param	message		the general message
	 */
	public ProgressDialog( final Dialog parent, final String title, final String message )
	{
		super( parent, true );		// Always modal
		this.parent = parent;

		this.initialize( title, message );
	}



	/**
	 * Initialize the dialog box, creating it's GUI components
	 * and setting their initial values.
	 *
	 * @param	title		the dialog's title
	 * @param	message		the general message
	 */
	protected void initialize( final String title, final String message_ )
	{
		String message = message_;
		//
		// Configure the window.
		//
		if ( title == null ) {
			this.setTitle( "Progress" );
		} else {
			this.setTitle( title );
		}

		this.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );


		//
		// Add an empty border around the dialog, and use a border
		// layout.  Prevent resizing.
		//
		this.setResizable( false );
		final Container pane = this.getContentPane();
		final JPanel content = new JPanel( );
		content.setLayout( new BorderLayout( ) );
		content.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
		pane.add( content );


		//
		// Add the general message.
		//
		if ( message == null ) {
			message = "";
		}
		this.messageLabel = new JLabel( message );
		final Font font = this.messageLabel.getFont( );
		final Font boldFont = font.deriveFont( Font.BOLD );
		this.messageLabel.setFont( boldFont );
		content.add( this.messageLabel, BorderLayout.NORTH );

		//
		// Add the note, initially empty.
		//
		this.noteLabel = new JLabel( " ");
		content.add( this.noteLabel, BorderLayout.CENTER );

		//
		// Add the progress bar.
		//
		this.progressBar = new JProgressBar( SwingConstants.HORIZONTAL,0,100 );
		this.progressBar.setBorderPainted( true );
		this.progressBar.setStringPainted( false );
		this.progressBar.setValue( 0 );
		final Dimension d = new Dimension( 300, 20 );
		this.progressBar.setMinimumSize( d );
		this.progressBar.setPreferredSize( d );
		content.add( this.progressBar, BorderLayout.SOUTH );

		//
		// Pack and size the dialog.
		//
		this.pack( );
		this.validate( );


		//
		// Listen to status events.
		//
		Status.addStatusListener( this );
	}




//----------------------------------------------------------------------
//  Overridden JDialog methods
//----------------------------------------------------------------------
	/**
	 * Show the dialog box.  The dialog is automatically centered
	 * on the parent window, or on the screen if there is no parent.
	 * <P>
	 * This method always returns immediately after showing the
	 * dialog box.  However, if the dialog has a parent, the
	 * dialog defaults to modal.  When modal, interaction with the
	 * application will be blocked until this dialog box is closed
	 * by calling the hide() method.  If the dialog has no parent,
	 * it is not modal and application interaction is not blocked.
	 * <P>
	 * While the dialog box is up, an internal thread monitors
	 * changes to the dialog box's parameters and keeps the dialog
	 * drawn and uptodate.
	 */
	
	public void show( )
	{
		// Center
		final Rectangle windowDim = this.getBounds( );
		int x, y;
		if ( this.parent == null )
		{
			// Center on the screen.
			final Toolkit toolkit = Toolkit.getDefaultToolkit( );
			final Dimension screenDim = toolkit.getScreenSize( );
			x = screenDim.width /2 - windowDim.width /2;
			y = screenDim.height/2 - windowDim.height/2;
		}
		else
		{
			// Center on the parent.
			final Rectangle parentDim = this.parent.getBounds( );
			x = parentDim.x + parentDim.width /2 - windowDim.width /2;
			y = parentDim.y + parentDim.height/2 - windowDim.height/2;
		}
		if ( x < 0 ) {
			x = 0;
		}
		if ( y < 0 ) {
			y = 0;
		}
		this.setLocation( x, y );


		// If the dialog box is not modal, just call the
		// superclass's show method.  That method returns
		// quickly, and then we return.
		if ( this.isModal( ) == false )
		{
			super.show( );
			return;
		}

		// Otherwise the dialog is modal.  Start a thread
		// to show the dialog box.  That thread will block
		// waiting for the dialog box to be hidden.
		final ProgressDialog thisDialog = this;
		final Thread showThread = new Thread( new Runnable( )
		{
			public void run( )
			{
				// Doesn't return until dialog is hidden.
				thisDialog.showIt( );
			}
		} );
		showThread.start( );

		// Return, leaving the dialog box up.
	}

	/**
	 * Show the dialog box via the superclass.  This method is only
	 * called by the show thread.
	 */
	private void showIt( )
	{
		super.show( );
	}

	/**
	 * Hide the dialog box, removing it from the screen.  If the
	 * dialog was modal, application interaction is unblocked.
	 */
	
	public void hide( )
	{
		super.hide( );
		if ( this.statusMonitored ) {
			Status.removeStatusListener( this );
		}
	}





//----------------------------------------------------------------------
//  StatusListener Methods
//----------------------------------------------------------------------
	/**
	 * Process an incoming status event.  Output events set the
	 * note text in the dialog.  Progress events set the note text
	 * and progress bar percentage.  All other status events are
	 * ignored.
	 *
	 * @param	statusEvent	the new event
	 */
	public void processStatusEvent( final StatusEvent statusEvent )
	{
		switch ( statusEvent.type )
		{
		case StatusEvent.TYPE_OUTPUT:
			// Change the note.
			this.setNote( statusEvent.message );
			break;

		case StatusEvent.TYPE_PROGRESS:
			// Change the progress level.
			this.setNote( statusEvent.message );
			this.setProgress( statusEvent.percent );
		}
	}





//----------------------------------------------------------------------
//  Methods
//----------------------------------------------------------------------
	/**
	 * Get the most recently set progress bar percentage.
	 *
	 * @return			the progress bar percentage
	 */
	public float getProgress( )
	{
		return this.percent;
	}

	/**
	 * Set the progress bar percentage.
	 *
	 * @param	percent		the progress bar percentage
	 */
	public void setProgress( final float percent_ )
	{
		float percent = percent_;
		if ( percent > 1.0f ) {
			percent = 1.0f;
		} else if ( percent < 0.0f ) {
			percent = 0.0f;
		}

		this.percent = percent;
		this.progressBar.setValue( (int)(percent * 100.0f) );
		this.repaint( );
	}

	/**
	 * Get the message text set at construction.
	 *
	 * @return			the message text
	 */
	public String getMessage( )
	{
		return this.messageLabel.getText( );
	}

	/**
	 * Get the most recently set progress note text.
	 *
	 * @return			the note text
	 */
	public String getNote( )
	{
		return this.noteLabel.getText( );
	}

	/**
	 * Set the progress note text.
	 *
	 * @param	note		the note text
	 */
	public void setNote( final String note )
	{
		this.noteLabel.setText( note );
		this.repaint( );
	}

	/**
	 * Return true if the progress bar is in indeterminate mode,
	 * and false otherwise.
	 *
	 * @return			true for indeterminate; false
	 *				for determinate
	 */
	public boolean isIndeterminate( )
	{
		return this.progressBar.isIndeterminate( );
	}

	/**
	 * Set the progress bar into determinate or indeterminate progress
	 * mode.  In determinate mode, the bar shows the percentage of
	 * completion.  In indeterminate mode, the bar cycles back and
	 * forth just showing that something is happening.
	 *
	 * @param	onOff		true for indeterminate; false
	 *				for determinate
	 */
	public void setIndeterminate( final boolean onOff )
	{
		this.progressBar.setIndeterminate( onOff );
	}

	/**
	 * Returns true if MBT status events are being monitored;
	 * false otherwise.
	 *
	 * @return			true if status is monitored
	 * @see	org.rcsb.mbt.model.util.Status
	 */
	public boolean isStatusMonitored( )
	{
		return this.statusMonitored;
	}

	/**
	 * Sets whether the dialog box monitors MBT Status events.
	 * When true, the dialog box registers itself as a listener
	 * for MBT Status events and displays them as they arrive.
	 * When false, the dialog box is not a registered listener
	 * and does not display status events.  In both cases,
	 * calls to setNote() or setProgress() continue to update
	 * the dialog.
	 *
	 * @param	onOff		true if status is monitored
	 */
	public void setStatusMonitored( final boolean onOff )
	{
		if ( onOff == this.statusMonitored ) {
			return;		// No change
		}

		if ( onOff == true ) {
			Status.addStatusListener( this );
		} else {
			Status.removeStatusListener( this );
		}

		this.statusMonitored = onOff;
	}
}
