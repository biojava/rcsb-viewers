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
import javax.swing.*;
import javax.swing.border.*;


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
