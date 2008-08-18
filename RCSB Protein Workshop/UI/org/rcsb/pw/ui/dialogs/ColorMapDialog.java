//  $Id: ColorMapDialog.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: ColorMapDialog.java,v $
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
//  Revision 1.3  2005/11/08 20:58:14  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.2  2004/10/27 20:03:53  moreland
//  Corrected javadoc SEE references.
//
//  Revision 1.1  2004/07/07 23:48:38  moreland
//  Added new Dialog wrapper classes from Dave for several GUI panels.
//


package org.rcsb.pw.ui.dialogs;


import java.lang.reflect.*;
import java.util.*;
import java.io.File;
import java.net.*;

// GUI
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.attributes.*;
import org.rcsb.mbt.ui.dialogs.InterpolatedColorMapEditor;

// MBT





/**
 *  A ColorMapDialog shows a dialog window for setting parameters in
 *  an interpolated color map.  The dialog presents an optional list
 *  of color maps to edit and an InterpolatedColorMapEditor panel with
 *  a color ramp and handles to change colors at those handles.
 *  <P>
 *  This dialog can be used in several ways.  When constructed with
 *  a color map, the dialog box presents that single color map for
 *  editing.  No pull-down menu of additional maps is shown.
 *  <P>
 *  Instead, if construction provides a structure document, a list of
 *  all interpolated color maps used by all structures in the document
 *  is presented in the GUI.  The user may select which color map to
 *  edit.
 *
 *  @author	David R. Nadeau / UCSD
 *  @see	org.rcsb.mbt.model.attributes.InterpolatedColorMap
 *  @see	org.rcsb.mbt.ui.dialogs.InterpolatedColorMapEditor
 */
public class ColorMapDialog
	extends JDialog
{
/**
	 * 
	 */
	private static final long serialVersionUID = 992931431377007405L;

	//----------------------------------------------------------------------
//  Inner Classes
//----------------------------------------------------------------------
	/**
	 * A MapUse object records the use of a residue colorer that
	 * uses an interpolated color map.  The object notes the
	 * structure the colorer is used on, it's color map,
	 * and the range of residues the colorer is applied to.
	 *
	 * @author	David R. Nadeau / UCSD
	 */
	protected final class MapUse
		extends Object
	{
	//--------------------------------------------------------------
	//  Fields
	//--------------------------------------------------------------
		/**
		 * The colorer itself.
		 */
		protected IResidueColor colorer = null;

		/**
		 * The structure it is applied to.
		 */
		protected Structure structure = null;

		/**
		 * The color map for that colorer.
		 */
		protected InterpolatedColorMap colorMap = null;

		/**
		 * A list of residue indexes marking the starts of
		 * ranges of use of this colorer.
		 */
		protected int rangeFirsts[] = null;

		/**
		 * A list of residue indexes marking the ends of
		 * ranges of use of this colorer.  The residue at
		 * this index *is* included in the range.
		 */
		protected int rangeLasts[] = null;

		/**
		 * The number of entries allocated to the first
		 * and last arrays.
		 */
		protected int nAlloc = 0;

		/**
		 * The index of the next available array entry.
		 */
		protected int nUsed = 0;


	//--------------------------------------------------------------
	//  Constructors
	//--------------------------------------------------------------
		/**
		 * Creates a map usage object for the given structure,
		 * residue colorer, and interpolated color map.
		 *
		 * @param	strct		the structure
		 * @param	clr		the colorer
		 * @param	icm		the color map
		 */
		public MapUse( final Structure strct, final IResidueColor clr,
			final InterpolatedColorMap icm )
		{
			this.structure = strct;
			this.colorer   = clr;
			this.colorMap  = icm;
			this.nAlloc = 10;
			this.rangeFirsts = new int[this.nAlloc];
			this.rangeLasts  = new int[this.nAlloc];
		}


	//--------------------------------------------------------------
	//  Methods
	//--------------------------------------------------------------
		/**
		 * Get the structure in the usage object.
		 *
		 * @return			the structure
		 */
		public Structure getStructure( )
		{
			return this.structure;
		}

		/**
		 * Get the residue colorer in the usage object.
		 *
		 * @return			the colorer
		 */
		public IResidueColor getResidueColor( )
		{
			return this.colorer;
		}

		/**
		 * Get the color map in the usage object.
		 *
		 * @return			the color map
		 */
		public InterpolatedColorMap getInterpolatedColorMap( )
		{
			return this.colorMap;
		}

		/**
		 * Add a range to the list of usage ranges.
		 *
		 * @param	first		the range start
		 * @param	last		the range end
		 */
		public void addRange( final int first, final int last )
		{
			if ( this.nUsed >= this.nAlloc )
			{
				this.nAlloc += 10;
				final int rf[] = new int[this.nAlloc];
				final int rl[] = new int[this.nAlloc];
				for ( int i = 0; i < this.nUsed; i++ )
				{
					rf[i] = this.rangeFirsts[i];
					rl[i] = this.rangeLasts[i];
				}
				this.rangeFirsts = rf;
				this.rangeLasts  = rl;
			}
			this.rangeFirsts[this.nUsed] = first;
			this.rangeLasts[this.nUsed] = last;
			++this.nUsed;
		}

		/**
		 * Get the number of ranges in the range list.
		 *
		 * @return			the number of ranges
		 */
		public int getRangeCount( )
		{
			return this.nUsed;
		}

		/**
		 * Get the range start for the indicated range
		 * in the list.
		 *
		 * @param	index		the range index
		 * @return			the range start
		 */
		public int getRangeFirst( final int index )
		{
			return this.rangeFirsts[index];
		}

		/**
		 * Get the range end for the indicated range
		 * in the list.
		 *
		 * @param	index		the range index
		 * @return			the range end
		 */
		public int getRangeLast( final int index )
		{
			return this.rangeLasts[index];
		}

		/**
		 * Return a string describing the usage.  The string
		 * has the form:
		 * <pre>
		 *	ID residues F-L, F-L, ...
		 * </pre>
		 * where ID is the structure ID of the structure,
		 * and F and L are the first and last of each range.
		 * The ranges are a comma-separated list.
		 *
		 * @return			the string
		 */
		
		public String toString( )
		{
			final StringBuffer buf = new StringBuffer( );

			final StructureInfo info = this.structure.getStructureInfo( );
			String name = null;
			if ( info != null )
			{
				// Use the id code.
				name = info.getIdCode( );
			}
			else
			{
				// Build the ID code from the URL.
				final String urlString = this.structure.getUrlString( );
				try
				{
					final URL url = new URL( urlString );
					final File file = new File( url.getPath( ) );
					name = file.getName( );
					final int dot = name.indexOf( '.' );
					if ( dot != -1 ) {
						name = name.substring( 0, dot );
					}
				}
				catch ( final MalformedURLException e )
				{
					// Oh well.  Use the url string.
					name = urlString;
				}
			}
			buf.append( name );

			buf.append( " residues " );
			for ( int i = 0; i < this.nUsed; i++ )
			{
				if ( i != 0 ) {
					buf.append( ", " );
				}
				buf.append( this.rangeFirsts[i] );
				buf.append( "-" );
				buf.append( this.rangeLasts[i] );
			}
			return buf.toString( );
		}
	};





//----------------------------------------------------------------------
//  Fields
//----------------------------------------------------------------------
	/**
	 * A table with one entry for each unique residue
	 * colorer used, and the range of residues over
	 * which it is used.
	 */
	protected Hashtable mapUsage = new Hashtable( );

	/**
	 * The parent window.
	 */
	protected Window parent = null;

	/**
	 * The color map combo box listing color maps to edit.
	 */
	protected JComboBox mapsCombo = null;

	/**
	 * The inner panel containing everything.
	 */
	protected JPanel innerPanel = null;

	/**
	 * The color map editor.
	 */
	protected InterpolatedColorMapEditor colorEditor = null;

	/**
	 * The color map to edit.
	 */
	protected InterpolatedColorMap colorMap = null;

//----------------------------------------------------------------------
//  Constructors / Initializers
//----------------------------------------------------------------------
    // Edit a single interpolated color map
	/**
	 * Create a non-modal color map editor dialog to set the
	 * given color map.
	 * <P>
	 * This constructor does not require a parent argument to specify
	 * a parent window or dialog for this dialog.  As a result, this
	 * dialog is always non-modal - it does not block interaction with
	 * the rest of the application.
	 *
	 * @param	map		the color map to edit
	 */
	public ColorMapDialog( final InterpolatedColorMap map )
	{
		super( );			// Non-modal
		if ( map == null ) {
			throw new NullPointerException( "null color map" );
		}

		this.parent = null;
		this.colorMap = map;

		this.initialize( );
	}

	/**
	 * Create a modal color map editor dialog to set the
	 * color map.
	 *
	 * @param	parent		the parent frame for this dialog
	 * @param	map		the color map to edit
	 * @throws	NullPointerException
	 *				if the parent or map is null
	 */
	public ColorMapDialog( final Frame parent, final InterpolatedColorMap map )
	{
		super( parent, true );		// Always modal
		if ( map == null ) {
			throw new NullPointerException( "null color map" );
		}

		this.parent = parent;
		this.colorMap = map;

		this.initialize( );
	}

	/**
	 * Create a modal color map editor dialog to set the
	 * color map.
	 *
	 * @param	parent		the parent dialog for this dialog
	 * @param	map		the color map to edit
	 * @throws	NullPointerException
	 *				if the parent or map is null
	 */
	public ColorMapDialog( final Dialog parent, final InterpolatedColorMap map )
	{
		super( parent, true );		// Always modal
		if ( map == null ) {
			throw new NullPointerException( "null color map" );
		}

		this.parent = parent;
		this.colorMap = map;

		this.initialize( );
	}

    // Edit any interpolated color map in the document
	/**
	 * Create a non-modal color map editor dialog to set any
	 * of the color maps in use by structures in the structure
	 * document.
	 * <P>
	 * This constructor does not require a parent argument to specify
	 * a parent window or dialog for this dialog.  As a result, this
	 * dialog is always non-modal - it does not block interaction with
	 * the rest of the application.
	 *
	 * @param	document	the structure document
	 * @throws	NullPointerException
	 *				if the parent or document is null
	 */
	public ColorMapDialog()
	{
		super( );			// Non-modal
		this.parent = null;
		this.buildMapUsage( );
		this.colorMap = null;

		this.initialize( );
	}

	/**
	 * Create a modal color map editor dialog to set any
	 * of the color maps in use by structures in the structure
	 * document.
	 *
	 * @param	parent		the parent frame for this dialog
	 * @param	document	the structure document
	 * @throws	NullPointerException
	 *				if the parent or document is null
	 */
	public ColorMapDialog( final Frame parent)
	{
		super( parent, true );		// Always modal

		this.parent = parent;
		this.buildMapUsage( );
		this.colorMap = null;

		this.initialize( );
	}

	/**
	 * Create a modal color map editor dialog to set any
	 * of the color maps in use by structures in the structure
	 * document.
	 *
	 * @param	parent		the parent dialog for this dialog
	 * @param	document	the structure document
	 * @throws	NullPointerException
	 *				if the parent or document is null
	 */
	public ColorMapDialog( final Dialog parent)
	{
		super( parent, true );		// Always modal
		this.parent = parent;
		this.buildMapUsage( );
		this.colorMap = null;

		this.initialize( );
	}



	/**
	 * Initializes the GUI for the window.  That GUI
	 * includes an InterpolatedColorMapEditor panel, framed
	 * with an empty border.
	 */
	protected void initialize( )
	{
		//
		// Configure the window.
		//
		this.setTitle( "Edit color map" );
		this.setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
		this.setResizable( true );


		//
		// Fill the window.
		//	An inner panel provides an empty border.  The
		//	JColorMap panel is inside the inner panel.
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
		tabs.addTab( "Color map", colorsTab );

		if ( AppBase.sgetModel() != null )
		{
			// Panel for color map selection.
			final JPanel mapsPanel = new JPanel( );
			mapsPanel.setBorder( new EmptyBorder( 0, 0, 10, 0 ) );
			mapsPanel.setLayout( new GridBagLayout( ) );
			colorsTab.add( mapsPanel, BorderLayout.NORTH );

			// Label for combo box for color map usage.
			final JLabel mapsLabel = new JLabel( "Color maps:" );
			GridBagConstraints gbc = new GridBagConstraints( );
			gbc.insets = new Insets( 0, 0, 0, 10 );
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			mapsPanel.add( mapsLabel, gbc );

			// Combo box of color map usage.
			final MapUse[] uses = this.getMapUseArray( );
			this.mapsCombo = new JComboBox( uses );
			this.mapsCombo.addItemListener( new ItemListener( )
			{
				public void itemStateChanged( final ItemEvent event )
				{
					final JComboBox mapsCombo = (JComboBox)event.getSource( );
					final MapUse use = (MapUse)mapsCombo.getSelectedItem( );
					final InterpolatedColorMap icm = use.getInterpolatedColorMap( );
					ColorMapDialog.this.colorEditor.setInterpolatedColorMap( icm );
				}
			} );
			gbc = new GridBagConstraints( );
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1.0;
			mapsPanel.add( this.mapsCombo, gbc );

			// Set the initial color map as the 1st in the array.
			if ( uses.length != 0 ) {
				this.colorMap = uses[0].getInterpolatedColorMap( );
			}
		}

		// Color map editor panel
		final JPanel colorEditorPanel = new JPanel( );
		colorEditorPanel.setLayout( new BorderLayout( ) );
		colorEditorPanel.setBorder( new CompoundBorder(
			new BevelBorder( BevelBorder.LOWERED ),
			new EmptyBorder( 10, 10, 10, 10 ) ) );
		colorsTab.add( colorEditorPanel, BorderLayout.CENTER );

		// Color map editor
		this.colorEditor = new InterpolatedColorMapEditor( this.colorMap );
		final Dimension d = new Dimension( 300, 50 );
		this.colorEditor.setPreferredSize( d );
		this.colorEditor.setMinimumSize( d );
		this.colorEditor.setSize( d );
		colorEditorPanel.add( this.colorEditor, BorderLayout.CENTER );

		// Close button
		final JPanel closePanel = new JPanel( );
		closePanel.setLayout( new FlowLayout( FlowLayout.RIGHT, 0, 0 ));
		closePanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
		final JButton closeButton = new JButton( "Close" );
		closeButton.setDefaultCapable( true );
		this.getRootPane( ).setDefaultButton( closeButton );
		closeButton.addActionListener( new ActionListener( )
		{
			public void actionPerformed( final ActionEvent e )
			{
				ColorMapDialog.this.hide( );
			}
		} );
		closePanel.add( closeButton );

		colorsTab.add( closePanel, BorderLayout.SOUTH );

		this.pack( );
		this.validate( );
	}





//----------------------------------------------------------------------
//  Methods to manage a list of color map usage
//----------------------------------------------------------------------
	/**
	 * Build a list of places in which interpolated color maps are
	 * used in the current structure document.
	 */
	protected void buildMapUsage( )
	{
		StructureModel model = AppBase.sgetModel();
		
		if ( model == null ) {
			return;		// no document
		}

		// Scan all the structures in the document.
		// For each one, look for residue colorers that
		// use interpolated color maps.  Each time we
		// find one, add information about it to a list
		// of color map usage.
		for ( Structure structure : model.getStructures() )
		{
			// Get the structure, it's map, and it's styles.
			final StructureMap smap       = structure.getStructureMap( );
			final StructureStyles sstyles = smap.getStructureStyles( );

			// Scan through it's residues, looking for
			// colorers that use interpolated color maps.
			final int nResidue = smap.getResidueCount( );
			MapUse thisMap = null;
			IResidueColor previousColor = null;
			IResidueColor thisColor     = null;
			InterpolatedColorMap icm   = null;
			int rangeFirst = 0;

			for ( int r = 0; r < nResidue; r++ )
			{
				final Residue residue = smap.getResidue( r );
				final ResidueStyle residueStyle = (ResidueStyle) sstyles.getStyle( residue );
				thisColor = residueStyle.getResidueColor( );

				// Is it the same as the previous residue?
				// If so, continue building the range of
				// residues it is applied to.
				if ( thisColor == previousColor ) {
					continue;	// Continue the current range
				}

				// It's not the same colorer as for the previous
				// residue.  End the range on that previous
				// colorer.
				if ( thisMap != null ) {
					thisMap.addRange( rangeFirst, r - 1 );
				}

				// And start set the previous colorer to null
				// until we're sure we've got a new colorer
				// that uses interpolated color maps.
				previousColor = null;

				// Is this new colorer one we've encountered
				// before?
				thisMap = (MapUse)this.mapUsage.get( thisColor );
				if ( thisMap != null )
				{
					// Yup.  Good, then all we need to
					// do is start a new range for it.
					rangeFirst = r;
					previousColor = thisColor;
					continue;
				}

				// We haven't seen this colorer before.
				// Does it have a 'getColorMap' method?
				final Class thisColorClass = thisColor.getClass( );
				Method thisGetMethod = null;
				try
				{
					thisGetMethod = thisColorClass.getMethod( "getColorMap", (Class[])null );
				}
				catch ( final NoSuchMethodException e )
				{
					continue;	// No such method.
				}
				catch ( final SecurityException e )
				{
					continue;	// Problem.  Skip it.
				}

				// It has a 'getColorMap' method.  Call it to get
				// the color map and see if it is an interpolated
				// color map.
				Object result = null;
				try
				{
					result = thisGetMethod.invoke( thisColor, (Class[])null );
				}
				catch ( final IllegalAccessException e )
				{
					continue;	// Problem.  Skip it.
				}
				catch ( final IllegalArgumentException e )
				{
					continue;	// Problem.  Skip it.
				}
				catch ( final InvocationTargetException e )
				{
					continue;	// Problem.  Skip it.
				}
				if ( result == null ) {
					continue;	// No cmap
				}
				if ( !(result instanceof InterpolatedColorMap) ) {
					continue;	// Not interpolated
				}
				icm = (InterpolatedColorMap)result;

				// Yup, it has an interpolated color map.
				// Create a new usage entry for it and
				// start a range of it's use.
				thisMap = new MapUse( structure, thisColor, icm );
				this.mapUsage.put( thisColor, thisMap );
				rangeFirst = r;
				previousColor = thisColor;
			}

			// End the current range.
			if ( thisMap != null ) {
				thisMap.addRange( rangeFirst, nResidue - 1 );
			}
		}
	}

	/**
	 * Return the number of color map uses.
	 *
	 * @return			the number of colorers
	 */
	protected int getMapUseCount( )
	{
		return this.mapUsage.size( );
	}

	/**
	 * Return an array of color map usage objects, each one
	 * describing a unique residue colorer and the range(s) of
	 * residues it is applied to.  Each entry in the array is
	 * a MapUse object.
	 * <P>
	 * The MapUse object's toString() method returns a friendly
	 * string usable in a GUI list or combobox element.
	 *
	 * @return			the map use array
	 */
	protected MapUse[] getMapUseArray( )
	{
		final Collection c = this.mapUsage.values( );
		final Object[] objs = c.toArray( );

		final int n = objs.length;
		final MapUse[] uses = new MapUse[n];
		for ( int i = 0; i < n; i++ ) {
			uses[i] = (MapUse)objs[i];
		}
		return uses;
	}





//----------------------------------------------------------------------
//  Methods
//----------------------------------------------------------------------
	/**
	 * Show the dialog box.  The dialog is automatically centered
	 * on the parent window, or on the screen if there is no parent.
	 * <P>
	 * By default, the dialog box is modal and this method call
	 * blocks until the dialog box is closed by the user or by
	 * calling the hide() method.
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

		// Set the background color used by the editor's dialogs.
		if ( this.colorEditor != null ) {
			this.colorEditor.setBackgroundForDialogs( background );
		}
	}
}
