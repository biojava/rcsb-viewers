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
 * Created on 2007/02/08
 *
 */ 
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

