//  $Id: ColorChooserDialog.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: ColorChooserDialog.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.1  2004/07/07 23:48:38  moreland
//  Added new Dialog wrapper classes from Dave for several GUI panels.
//
//


package org.rcsb.mbt.ui.dialogs;


// GUI
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;





/**
 *  A ColorChooserDialog shows a dialog window for selecting a color.
 *  The dialog contains a JColorChooser's panels with color swatches
 *  and samples using the color.
 *  <P>
 *  @author	David R. Nadeau / UCSD
 *  @see	javax.swing.JColorChooser
 */
public class ColorChooserDialog
	extends JDialog
{
/**
	 * 
	 */
	private static final long serialVersionUID = -5953604049348574107L;

	//----------------------------------------------------------------------
//  Fields
//----------------------------------------------------------------------
	/**
	 * The parent window.
	 */
	protected Window parent = null;

	/**
	 * The inner panel containing everything.
	 */
	protected JPanel innerPanel = null;

	/**
	 * The style editor panel.
	 */
	protected JColorChooser colorChooser = null;

	/**
	 * Starting color, set by setColor, and the color we return
	 * to on a reset.
	 */
	protected Color startingColor = Color.white;

	/**
	 * True if OK was pressed; false otherwise.
	 */
	protected boolean okWasPressed = false;





//----------------------------------------------------------------------
//  Constructors / Initializers
//----------------------------------------------------------------------
	/**
	 * Create a non-modal color chooser dialog to select a color.
	 * <P>
	 * This constructor does not require a parent argument to specify
	 * a parent window or dialog for this dialog.  As a result, this
	 * dialog is always non-modal - it does not block interaction with
	 * the rest of the application.
	 */
	public ColorChooserDialog( )
	{
		super( );			// Non-modal
		this.parent = null;

		this.initialize( );
	}

	/**
	 * Create a modal color chooser dialog to select a color.
	 *
	 * @param	parent		the parent frame for this dialog
	 */
	public ColorChooserDialog( final Frame parent )
	{
		super( parent, true );		// Always modal

		this.parent = parent;

		this.initialize( );
	}

	/**
	 * Create a modal color chooser dialog to select a color.
	 *
	 * @param	parent		the parent dialog for this dialog
	 */
	public ColorChooserDialog( final Dialog parent )
	{
		super( parent, true );		// Always modal

		this.parent = parent;

		this.initialize( );
	}



	/**
	 * Initializes the GUI for the window.  That GUI
	 * includes a JColorChooser panel, framed with an empty border.
	 */
	protected void initialize( )
	{
		//
		// Configure the window.
		//
		this.setTitle( "Color" );
		this.setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
		this.setResizable( false );


		//
		// Fill the window.
		//	An inner panel provides an empty border.  The
		//	JColorChooser panel is inside the inner panel.
		//
		final Container pane = this.getContentPane();
		pane.setLayout( new BorderLayout( ) );


		// Inner panel
		this.innerPanel = new JPanel( );
		this.innerPanel.setLayout( new BorderLayout( ) );
		this.innerPanel.setBorder( new CompoundBorder(
			new BevelBorder( BevelBorder.LOWERED ),
			new EmptyBorder( new Insets( 10, 10, 10, 10 ) ) ) );
		pane.add( this.innerPanel, BorderLayout.CENTER );


		// Tabs
		final JTabbedPane tabs = new JTabbedPane( );
		this.innerPanel.add( tabs, BorderLayout.CENTER );

		// Colors Tab
		final JPanel colorsTab = new JPanel( );
		colorsTab.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
		colorsTab.setLayout( new BorderLayout( ) );
		tabs.addTab( "Color", colorsTab );

		// Color chooser
		this.colorChooser = new JColorChooser( );
		colorsTab.add( this.colorChooser, BorderLayout.CENTER );


		// OK, Cancel, and Reset buttons
		final JPanel buttonPanel = new JPanel( );
		buttonPanel.setLayout( new FlowLayout( FlowLayout.RIGHT,0,0 ) );
		buttonPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
		colorsTab.add( buttonPanel, BorderLayout.SOUTH );

		final JPanel buttonGridPanel = new JPanel( );
		buttonGridPanel.setLayout( new GridLayout( 1, 3, 5, 0 ) );
		buttonPanel.add( buttonGridPanel );

		// Reset
		final JButton resetButton = new JButton( "Reset" );
		resetButton.addActionListener( new ActionListener( )
		{
			public void actionPerformed( final ActionEvent e )
			{
				ColorChooserDialog.this.colorChooser.setColor( ColorChooserDialog.this.startingColor );
			}
		} );
		buttonGridPanel.add( resetButton );

		// OK
		final JButton okButton = new JButton( "OK" );
		okButton.setDefaultCapable( true );
		this.getRootPane( ).setDefaultButton( okButton );
		okButton.addActionListener( new ActionListener( )
		{
			public void actionPerformed( final ActionEvent e )
			{
				ColorChooserDialog.this.okWasPressed = true;
				ColorChooserDialog.this.hide( );
			}
		} );
		buttonGridPanel.add( okButton );

		// Cancel
		final JButton cancelButton = new JButton( "Cancel" );
		cancelButton.addActionListener( new ActionListener( )
		{
			public void actionPerformed( final ActionEvent e )
			{
				ColorChooserDialog.this.colorChooser.setColor( ColorChooserDialog.this.startingColor );
				ColorChooserDialog.this.okWasPressed = false;
				ColorChooserDialog.this.hide( );
			}
		} );
		buttonGridPanel.add( cancelButton );


		this.pack( );
		this.validate( );
	}





//----------------------------------------------------------------------
//  Overridden JDialog methods
//----------------------------------------------------------------------
	/**
	 * Show the dialog box.  The dialog is automatically centered
	 * on the parent window, or on the screen if there is no parent.
	 * <P>
	 * By default, the dialog box is modal and this method call
	 * blocks until the dialog box is closed by the user pressing
	 * OK or Cancel.  The wasOKPressed() method returns true if
	 * OK was pressed.
	 *
	 * @see	#wasOKPressed()
	 */
	
	public void show( )
	{
		// Center
		final Rectangle windowDim = this.getBounds( );
		int x, y;
		if ( this.parent == null )
		{
			// Center on the screen
			final Toolkit toolkit = Toolkit.getDefaultToolkit( );
			final Dimension screenDim = toolkit.getScreenSize( );
			x = screenDim.width /2 - windowDim.width /2;
			y = screenDim.height/2 - windowDim.height/2;
		}
		else
		{
			// Center on the parent window
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

		this.okWasPressed = false;

		super.show( );
	}

	/**
	 * Set the background color for the window.
	 *
	 * @param	background	the new background color
	 */
	
	public void setBackground( final Color background )
	{
		// Set the dialog's background color.
		super.setBackground( background );

		// Set the inner panel's background color.
		if ( this.innerPanel != null ) {
			this.innerPanel.setBackground( background );
		}
	}





//----------------------------------------------------------------------
//  Methods
//----------------------------------------------------------------------
	/**
	 * Shows the dialog box and waits for the user to press OK or
	 * Cancel.  When either is pressed, the dialog box is hidden.
	 * A true is returned if OK was pressed, and false otherwise.
	 * <P>
	 * This method blocks until the dialog is closed by the user,
	 * regardless of whether the dialog box is modal or not.
	 *
	 * @return			true if OK was pressed
	 */
	public boolean showDialog( )
	{
		if ( this.isModal( ) )
		{
			this.show( );
			return this.okWasPressed;
		}
		this.setModal( true );
		this.show( );
		final boolean status = this.okWasPressed;
		this.setModal( false );
		return status;
	}

	/**
	 * Returns true if the OK button was pressed to close the
	 * window, and false otherwise.
	 *
	 * @return			true if OK was pressed
	 */
	public boolean wasOKPressed( )
	{
		return this.okWasPressed;
	}

	/**
	 * Get the current color in the color chooser.
	 *
	 * @return			the current color
	 */
	public Color getColor( )
	{
		return this.colorChooser.getColor( );
	}

	/**
	 * Set the current color in the color chooser.
	 *
	 * @param	color		the new color
	 */
	public void setColor( final Color color )
	{
		this.colorChooser.setColor( color );
		this.startingColor = color;
	}

	/**
	 * Set the current color in the color chooser.
	 *
	 * @param	red		the red component of the new color
	 * @param	green		the green component of the new color
	 * @param	blue		the blue component of the new color
	 */
	public void setColor( final int red, final int green, final int blue )
	{
		this.startingColor = new Color( red, green, blue );
		this.colorChooser.setColor( this.startingColor );
	}
}
