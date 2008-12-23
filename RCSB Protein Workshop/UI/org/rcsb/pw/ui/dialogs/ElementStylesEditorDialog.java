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
package org.rcsb.pw.ui.dialogs;


// GUI
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.attributes.*;






/**
 *  An ElementStylesEditorDialog shows a dialog window for controlling the
 *  colors associated with elements in the Periodic Table.  Changes to
 *  these colors automatically update attributes of an associated
 *  structure document.  The dialog contains a single ElementStylesEditor
 *  panel.
 *
 *  @author	David R. Nadeau / UCSD
 *  @see	org.rcsb.pw.ui.dialogs.ElementStylesEditor
 */
public class ElementStylesEditorDialog
	extends JDialog
{
/**
	 * 
	 */
	private static final long serialVersionUID = 1211308202976196202L;

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
	protected ElementStylesEditor styleEditor = null;

//----------------------------------------------------------------------
//  Constructors / Initializers
//----------------------------------------------------------------------
	/**
	 * Create a non-modal element style editor dialog window.
	 * The window's GUI components set element style attributes for the
	 * given structure document.
	 * <P>
	 * This constructor does not require a parent argument to specify
	 * a parent window or dialog for this dialog.  As a result, this
	 * dialog is always non-modal - it does not block interaction with
	 * the rest of the application.
	 *
	 * @param	document	the structure document
	 * @throws	NullPointerException
	 *				if the document is null
	 */
	public ElementStylesEditorDialog()
	{
		super( );			// Not modal
		this.parent = null;
		this.initialize( );
	}

	/**
	 * Create a modal element style editor dialog window.
	 * The window's GUI components set element style attributes for the
	 * given structure document.
	 *
	 * @param	parent		the parent frame for this dialog
	 * @param	document	the structure document
	 * @throws	NullPointerException
	 *				if the parent or document are null
	 */
	public ElementStylesEditorDialog( final Frame parent)
	{
		super( parent, true );		// Always modal
		this.parent = parent;
		this.initialize( );
	}

	/**
	 * Create a modal element style editor dialog window.
	 * The window's GUI components set element style attributes for the
	 * given structure document.
	 *
	 * @param	parent		the parent dialog for this dialog
	 * @param	document	the structure document
	 * @throws	NullPointerException
	 *				if the parent or document are null
	 */
	public ElementStylesEditorDialog( final Dialog parent)
	{
		super( parent, true );		// Always modal
		this.parent = parent;
		this.initialize( );
	}



	/**
	 * Initializes the GUI for the structure window.  That GUI
	 * includes a StyleEdtior panel, framed with an empty border.
	 */
	protected void initialize( )
	{
		//
		// Configure the window.
		//
		this.setTitle( "Element colors" );
		this.setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
		this.setResizable( false );


		//
		// Fill the window.
		//	An inner panel provides an empty border.  The
		//	ElementStylesEditor panel is inside the inner panel.
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

		// Elements Tab
		final JPanel elementsTab = new JPanel( );
		elementsTab.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
		elementsTab.setLayout( new BorderLayout( ) );
		tabs.addTab( "Elements", elementsTab );

		// Elements table
		this.styleEditor = new ElementStylesEditor();
		this.styleEditor.setBorder( new EmptyBorder( 0, 0, 10, 0 ) );
		elementsTab.add( this.styleEditor, BorderLayout.CENTER );


		// Close button
		final JPanel closePanel = new JPanel( );
		closePanel.setLayout( new FlowLayout( FlowLayout.RIGHT, 0, 0 ));
		final JButton closeButton = new JButton( "Close" );
		closeButton.setDefaultCapable( true );
		this.getRootPane( ).setDefaultButton( closeButton );
		closeButton.addActionListener( new ActionListener( )
		{
			public void actionPerformed( final ActionEvent e )
			{
				ElementStylesEditorDialog.this.setVisible(false);
			}
		} );
		closePanel.add( closeButton );

		elementsTab.add( closePanel, BorderLayout.SOUTH );

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
	 * Close.
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

		// Set the style editor's background color.
		if ( this.styleEditor != null ) {
			this.styleEditor.setBackgroundForDialogs( background );
		}
	}
}
