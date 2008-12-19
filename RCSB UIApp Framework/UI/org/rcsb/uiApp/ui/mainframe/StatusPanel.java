//  $Id: StatusPanel.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: StatusPanel.java,v $
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
//  Revision 1.6  2004/07/07 23:26:18  moreland
//  Added ability to set background color.
//
//  Revision 1.5  2004/04/09 00:06:11  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.4  2004/01/29 18:12:52  moreland
//  Updated copyright and class comment block.
//
//  Revision 1.3  2003/04/03 22:45:47  moreland
//  Added progress meter display support.
//
//  Revision 1.2  2003/02/27 21:35:50  moreland
//  Corrected javadoc "see" path references.
//
//  Revision 1.1  2003/02/03 21:51:26  moreland
//  Created a StatusPanel GUI component to listen to and display toolkit messages.
//
//  Revision 1.0  2003/01/22 01:16:31  agramada
//  First verision.
//


package org.rcsb.uiApp.ui.mainframe;


// MBT

// Core
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import org.rcsb.mbt.model.util.*;

import java.awt.event.*;


/**
 *  Provides a GUI component to display toolkit-wide Status messages.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.util.Status
 *  @see	org.rcsb.mbt.model.util.StatusEvent
 *  @see	org.rcsb.mbt.model.util.StatusListener
 */
public class StatusPanel
	extends JPanel
	implements StatusListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3651341998939847709L;

	// The scrollable text area that caches Status messages.
	private final JTextArea textArea = new JTextArea( );

	// The scroll-pane for the message area.
	private JScrollPane jScrollPane = null;
	private JLabel statusButton = null;

	//
	// Constructor
	//

	public StatusPanel( )
	{
		this.setLayout( new java.awt.BorderLayout( ) );

/*
		Border textAreaBorder = BorderFactory.createEmptyBorder( 1, 2, 1, 2 );
		textArea.setBorder( textAreaBorder );
*/
		this.textArea.setBorder( new EtchedBorder( EtchedBorder.LOWERED ) );
		this.textArea.setEditable( false );
		this.textArea.setLineWrap( true );
		this.textArea.setRows( 1 ); // Sets component size (not text rows)
		this.textArea.setBackground( Color.lightGray );

		this.jScrollPane = new JScrollPane( this.textArea );
		this.jScrollPane.setHorizontalScrollBarPolicy(
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		this.jScrollPane.setVerticalScrollBarPolicy(
			ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER );
		this.add( java.awt.BorderLayout.CENTER, this.jScrollPane );

		final JPopupMenu statusPopupMenu = new JPopupMenu( );

		final JMenuItem oneLine = new JMenuItem( "1 Line" );
		final ActionListener oneLineListener = new ActionListener( )
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StatusPanel.this.setRows( 1 );
			}
		};
		oneLine.addActionListener( oneLineListener );
		statusPopupMenu.add( oneLine );

		final JMenuItem twoLine = new JMenuItem( "2 Line" );
		final ActionListener twoLineListener = new ActionListener( )
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StatusPanel.this.setRows( 2 );
			}
		};
		twoLine.addActionListener( twoLineListener );
		statusPopupMenu.add( twoLine );

		final JMenuItem threeLine = new JMenuItem( "3 Line" );
		final ActionListener threeLineListener = new ActionListener( )
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StatusPanel.this.setRows( 3 );
			}
		};
		threeLine.addActionListener( threeLineListener );
		statusPopupMenu.add( threeLine );

		final JMenuItem fourLine = new JMenuItem( "4 Line" );
		final ActionListener fourLineListener = new ActionListener( )
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StatusPanel.this.setRows( 4 );
			}
		};
		fourLine.addActionListener( fourLineListener );
		statusPopupMenu.add( fourLine );

		final JMenuItem addLine = new JMenuItem( "+ Line" );
		final ActionListener addLineListener = new ActionListener( )
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StatusPanel.this.setRows( StatusPanel.this.getRows( ) + 1 );
			}
		};
		addLine.addActionListener( addLineListener );
		statusPopupMenu.add( addLine );

		final JMenuItem subtractLine = new JMenuItem( "- Line" );
		final ActionListener subtractLineListener = new ActionListener( )
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StatusPanel.this.setRows( StatusPanel.this.getRows( ) - 1 );
			}
		};
		subtractLine.addActionListener( subtractLineListener );
		statusPopupMenu.add( subtractLine );

		statusPopupMenu.add( new JSeparator( ) );

		final JMenuItem clearLine = new JMenuItem( "Clear" );
		final ActionListener clearLineListener = new ActionListener( )
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StatusPanel.this.clear( );
			}
		};
		clearLine.addActionListener( clearLineListener );
		statusPopupMenu.add( clearLine );

		statusPopupMenu.add( new JSeparator( ) );

		//
		// Build status output level menu items
		//

		final ActionListener levelMenuItemListener = new ActionListener( )
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				String level_name = actionEvent.getActionCommand();
				Status.setOutputLevel( level_name );
				// Ask for the level back (in case it did not get changed!)
				int level = Status.getOutputLevel( );
				level_name = Status.getLevelName( level );
				Status.output( Status.LEVEL_QUIET, "status Level set to " + level_name );
			}
		};
		final int statusLevelCount = Status.getLevelCount( );
		for ( int level=0; level<statusLevelCount; level++ )
		{
			final String levelName = Status.getLevelName( level );
			final JMenuItem levelMenuItem = new JMenuItem( levelName );
			levelMenuItem.addActionListener( levelMenuItemListener );
			statusPopupMenu.add( levelMenuItem );
		}

		//
		// Build status popup menu button
		//

		this.statusButton = new JLabel( "Status:" );
		final Border statusButtonBorder = BorderFactory.createEmptyBorder( 2, 2, 2, 2 );
		this.statusButton.setBorder( statusButtonBorder );
		final MouseAdapter statusPopupListener = new MouseAdapter()
		{
			
			public void mousePressed( MouseEvent e )
			{
				this.maybeShowPopup( e );
			}

			
			public void mouseReleased( MouseEvent e )
			{
				this.maybeShowPopup( e );
			}

			private void maybeShowPopup( MouseEvent e )
			{
				if ( e.isPopupTrigger() ) {
					statusPopupMenu.show( e.getComponent(), 0, 0 );
				}
			}
		};
		this.statusButton.addMouseListener( statusPopupListener );
		this.add( java.awt.BorderLayout.WEST, this.statusButton );

		Status.addStatusListener( this );
	}

	
	public java.awt.Dimension getMinimumSize()
	{
		return new java.awt.Dimension( 100, 20 );
	}

	public int getRows( )
	{
		return this.textArea.getRows( );
	}

	public void setRows( final int rows )
	{
		if ( rows <= 0 ) {
			return;
		} else if ( rows == 1 ) {
			this.jScrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER );
		} else {
			this.jScrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );
		}

		this.textArea.setRows( rows ); // Sets component size (not text rows)
		this.showLastLine( ); // Make sure we can see at least the last line.
		this.getParent().validate( );  // Force the parent layout to resize us.
	}

	public void clear( )
	{
		this.textArea.setText( null );
	}

	//
	// StatusListener interface methods
	//

	/**
	 * Gets called when the toolkit's Status class desides that there is
	 * a status message that the user should see (ie: on "output").
	 */
	public void processStatusEvent( final StatusEvent statusEvent )
	{
		if ( statusEvent.type == StatusEvent.TYPE_OUTPUT )
		{
			this.textArea.setText( statusEvent.message );
			this.showLastLine( );

		}
	}

	/**
	 * Cause at least the last line of the text area to scroll into view.
	 */
	public void showLastLine( )
	{
		try
		{
			int lastLine = this.textArea.getLineCount( );
			lastLine -= 1; // lines start at 0
			lastLine -= 1; // don't include the last/empty line
			final int lastLineStartOffset = this.textArea.getLineStartOffset( lastLine );
			// lastLineStartOffset -= 1; // don't include the new line
			this.textArea.setCaretPosition( lastLineStartOffset );
		}
		catch ( final javax.swing.text.BadLocationException e )
		{
			this.textArea.setCaretPosition( this.textArea.getText().length() );
		}
	}


	/**
	 * Set the background color for this panel.
	 */
	
	public void setBackground( final Color color )
	{
		super.setBackground( color );
		if ( this.textArea != null ) {
			this.textArea.setBackground( color );
		}
		if ( this.statusButton != null ) {
			this.statusButton.setBackground( color );
		}
	}


	/**
	 * Get the background color for this panel.
	 */
	
	public void setForeground( final Color color )
	{
		super.setForeground( color );
		if ( this.textArea != null ) {
			this.textArea.setForeground( color );
		}
		if ( this.statusButton != null ) {
			this.statusButton.setForeground( color );
		}
	}
}

