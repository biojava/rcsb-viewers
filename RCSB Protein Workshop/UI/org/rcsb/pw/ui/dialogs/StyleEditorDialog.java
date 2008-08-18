//  $Id: StyleEditorDialog.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: StyleEditorDialog.java,v $
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
//  Revision 1.1  2004/07/07 23:48:40  moreland
//  Added new Dialog wrapper classes from Dave for several GUI panels.
//


package org.rcsb.pw.ui.dialogs;


// GUI
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.attributes.*;






/**
 *  A StyleEditorDialog shows a dialog window for controlling the style
 *  attributes of an associated structure document.  The dialog contains
 *  a single StyleEditor panel.
 *
 *  @author	David R. Nadeau / UCSD
 *  @see	org.rcsb.pw.ui.dialogs.StyleEditor
 */
public class StyleEditorDialog
	extends JDialog
{
/**
	 * 
	 */
	private static final long serialVersionUID = -6497733096422771873L;

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
	protected StyleEditor styleEditor = null;





//----------------------------------------------------------------------
//  Constructors / Initializers
//----------------------------------------------------------------------
	/**
	 * Create a non-modal structure style editor dialog window.
	 * The window's GUI components set style attributes for the
	 * given structure document.
	 *
	 * @param	document	the structure document
	 * @throws	NullPointerException
	 *				if the document is null
	 */
	public StyleEditorDialog()
	{
		super( );			// Not modal
		this.parent = null;
		this.initialize( );
	}

	/**
	 * Create a non-modal structure style editor dialog window.
	 * The window's GUI components set style attributes for the
	 * given structure document.
	 *
	 * @param	parent		the parent frame for this dialog
	 * @param	document	the structure document
	 * @throws	NullPointerException
	 *				if the parent or document are null
	 */
	public StyleEditorDialog( final Frame parent)
	{
		super( parent, false );		// Not modal
		this.parent = parent;
		this.initialize( );
	}

	/**
	 * Create a non-modal structure style editor dialog window.
	 * The window's GUI components set style attributes for the
	 * given structure document.
	 *
	 * @param	parent		the parent dialog for this dialog
	 * @param	document	the structure document
	 * @throws	NullPointerException
	 *				if the parent or document are null
	 */
	public StyleEditorDialog( final Dialog parent)
	{
		super( parent, false );		// Not modal
		this.parent = parent;
		this.initialize( );
	}



	/**
	 * Initializes the GUI for the window.  That GUI
	 * includes a StyleEdtior panel, framed with an empty border.
	 */
	protected void initialize( )
	{
		//
		// Configure the window.
		//
		this.setTitle( "Structure style" );
		this.setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
		this.setResizable( false );


		//
		// Fill the window.
		//	An inner panel provides an empty border.  The
		//	StyleEditor panel is inside the inner panel.
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

		// Style editor
		this.styleEditor = new StyleEditor();
		this.innerPanel.add( this.styleEditor, BorderLayout.CENTER );

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
	 * By default, the dialog is not modal - it does not block continued
	 * interaction with the rest of the application.  However, some
	 * components on the dialog box may bring up other dialogs (such as
	 * for selecting colors) that are modal.  When those dialogs are
	 * up, interaction with the application is blocked until they
	 * are closed.
	 * <P>
	 * In any case, this method call returns immediately after making
	 * the dialog visible.
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

		super.show( );
	}

	/**
	 * Set the background color for the window.
	 *
	 * @param	background		the new background color
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
			this.styleEditor.setBackground( background );
		}
	}
}
