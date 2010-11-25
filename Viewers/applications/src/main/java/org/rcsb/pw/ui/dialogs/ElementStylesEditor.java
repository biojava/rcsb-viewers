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


// Core
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.attributes.*;
import org.rcsb.mbt.model.util.*;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.ui.dialogs.ColorChooserDialog;


/**
 *  This class provides an element color editor interface in the form of a
 *  Periodic Table.
 *  <P>
 *  <CENTER>
 *  <IMG SRC="doc-files/ElementStylesEditor.jpg">
 *  </CENTER>
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IColorMap
 */
public class ElementStylesEditor
	extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3737639452643663691L;

	//
	// Define a 2D table of element numbers than will be used to build the GUI.
	// Each row has the same number of items to enable automatic GUI layout.
	// A "0" value means there should be an empty/invisible tile.
	//
	private static final int elementNumbers[][] =
	{
		{  1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  2 },
		{  3,  4,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  5,  6,  7,  8,  9, 10 },
		{ 11, 12,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 13, 14, 15, 16, 17, 18 },
		{ 19, 20,  0, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36 },
		{ 37, 38,  0, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54 },
		{ 55, 56,  0, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86 },
		{ 87, 88,  0,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118 },
		{  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0 },
		{  0,  0,  0, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70,  0,  0 },
		{  0,  0,  0, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99,100,101,102,  0,  0 }
	};

	private ColorChooserDialog colorChooser = null;

	private Color background = null;


	/**
	 *  Construct an instance of this class using the content described by the
	 *  PeriodicTable and ElementStyles classes.
	 */
	public ElementStylesEditor( )
	{
		// Use a grid of same-size tiles to form the table.
		this.setLayout( new GridLayout( ElementStylesEditor.elementNumbers.length, ElementStylesEditor.elementNumbers[0].length ) );
		// Leave some space around the edges of the panel.
		this.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );

		// Walk through the 2D table of element numbers to build the GUI.
		for ( int i=0; i<ElementStylesEditor.elementNumbers.length; i++ )
		{
			for ( int j=0; j<ElementStylesEditor.elementNumbers[i].length; j++ )
			{
				final int elementNumber = ElementStylesEditor.elementNumbers[i][j];
				JComponent component = null;
				if ( elementNumber == 0 )
				{
					// An empty cell.
					component = new JLabel( "" );
				}
				else
				{
					// A non-empty cell.
					final Element element = PeriodicTable.getElement( elementNumber );
					JButton button = null;
					// If there is an element object use the symbol as the name.
					if ( element == null ) {
						button = new JButton( String.valueOf( elementNumber ) );
					} else {
						button = new JButton( element.symbol );
					}
					// button = (JButton) new PtButton( element );
					component = button;

					// Enable the user to click the element tile.
					final PtListener ptListener = new PtListener( element, this, button );
					button.addActionListener( ptListener );

					// Color the element tile acording to the ElementStyles class.
					final float[] rgb =
						ElementStyles.getElementColor( elementNumber );
					if ( rgb != null )
					{
						final Color color = new Color( rgb[0], rgb[1], rgb[2] );
						if ( color != null ) {
							component.setBackground( color );
						}
					}

					// Replace the border to get rid of the text insets.
//					component.setBorder( BorderFactory.createEtchedBorder() );
					component.setBorder( new CompoundBorder(
						new EtchedBorder( ),
						new EmptyBorder( 4, 0, 4, 0 ) ) );
				}

				// Add the element tile to the panel layout.
				this.add( component );
			}
		}
	}

	/**
	 * Set the background color for dialogs created from this editor.
	 *
	 * @param	background	the new background color
	 */
	public void setBackgroundForDialogs( final Color background )
	{
		// Set the color chooser dialog's background color.
		if ( this.colorChooser != null ) {
			this.colorChooser.setBackground( background );
		}

		this.background = background;
	}

	/**
	 * Called in response to a click on an element tile.
	 */
	public void tileClicked( final Element element, final JButton button )
	{
		// Create the color chooser, if this is the first use.
		if ( this.colorChooser == null )
		{
			// Trace our lineage to a frame or dialog.
			Component parent = this.getParent( );
			while ( parent != null &&
				!(parent instanceof Dialog) &&
				!(parent instanceof Frame) )
			{
				parent = parent.getParent( );
			}
			if ( parent == null ) {
				this.colorChooser = new ColorChooserDialog( );
			} else if ( parent instanceof Dialog ) {
				this.colorChooser = new ColorChooserDialog( (Dialog)parent );
			} else {
				this.colorChooser = new ColorChooserDialog( (Frame)parent );
			}
			if ( this.background != null ) {
				this.colorChooser.setBackground( this.background );
			} else {
				this.colorChooser.setBackground( this.getBackground( ) );
			}
			this.colorChooser.setTitle( "Element color" );
		}

		// System.err.println( "tileClicked = " + element );
		final float[] color = ElementStyles.getElementColor( element.atomic_number );
		this.colorChooser.setColor( new Color( color[0], color[1], color[2] ) );
		this.colorChooser.show( );
		if ( this.colorChooser.wasOKPressed( ) == false ) {
			return;			// User canceled
		}
		final Color userColor = this.colorChooser.getColor( );
		userColor.getRGBColorComponents( color );
		ElementStyles.setElementColor(
			element.atomic_number, color[0], color[1], color[2] );
		button.setBackground( userColor );
		
		StructureModel model = AppBase.sgetModel();

		if ( model != null && model.hasStructures())
		{
			for ( Structure structure : model.getStructures())
			{
				final StructureMap structureMap = structure.getStructureMap( );
				final StructureStyles structureStyles =
					structureMap.getStructureStyles( );

				final int atomCount = structureMap.getAtomCount( );
				for ( int a=0; a<atomCount; a++ )
				{
					final Atom atom = structureMap.getAtom( a );
					final AtomStyle atomStyle = (AtomStyle)
						structureStyles.getStyle( atom );
					if ( structureStyles.isVisible( atom ) )
					{
						atomStyle.setAtomColor( atomStyle.getAtomColor() );
					}
				}

				final int bondCount = structureMap.getBondCount( );
				for ( int b=0; b<bondCount; b++ )
				{
					final Bond bond = structureMap.getBond( b );
					final BondStyle bondStyle = (BondStyle)
						structureStyles.getStyle( bond );
					if ( structureStyles.isVisible( bond ) )
					{
						bondStyle.setBondColor( bondStyle.getBondColor() );
					}
				}
			}
		}
	}

	//
	// Unit testing.
	//
	////////////////////////////////////////////////////////////////////////


	/**
	 *  This the main application entry point for the example program.
	 */
	public static void main( final String args[] )
	{
		final JFrame frame = new JFrame( "Element Styles Editor" );
		final Container container = frame.getContentPane( );
		container.setLayout( new GridLayout( 1, 1 ) );
		final ElementStylesEditor pte = new ElementStylesEditor( );
		container.add( pte );
		frame.setSize( 640, 480 );
		frame.setVisible( true );
	}


	//
	// Inner classes.
	//
	////////////////////////////////////////////////////////////////////////


	/**
	 * An inner class which knows how to respond to a click on an element tile.
	 */
	public class PtListener
		implements ActionListener
	{
		Element element = null;
		ElementStylesEditor elementStylesEditor = null;
		JButton button = null;
		public PtListener( final Element el, final ElementStylesEditor pte, final JButton bt )
		{
			this.element = el;
			this.elementStylesEditor = pte;
			this.button = bt;
		}
		public void actionPerformed( final ActionEvent actionEvent )
		{
			this.elementStylesEditor.tileClicked( this.element, this.button );
		}
	};


	/**
	 * An inner class which knows how to draw an element tile.
	 */
	public class PtButton
		extends JButton
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -5343224025900013460L;
		Element element = null;
		String atomicNumber = null;
		public PtButton( final Element el )
		{
			this.element = el;
			final float[] rgb =
				ElementStyles.getElementColor( this.element.atomic_number );
			if ( rgb != null )
			{
				final Color color = new Color( rgb[0], rgb[1], rgb[2] );
				if ( color != null ) {
					this.setBackground( color );
				}
			}
			this.atomicNumber = String.valueOf( this.element.atomic_number );
		}
		
		public void paint( final Graphics graphics )
		{
			final int width = this.getWidth( );
			final int height = this.getHeight( );
			final int halfWidth = width / 2;
			final int halfHeight = height / 2;

			// Clear the tile.
			graphics.setColor( this.getBackground() );
			graphics.fillRect( 0, 0, width, height );

			// Draw the element symbol.
			graphics.drawString( this.element.symbol, halfWidth, height );
			final FontMetrics fontMetrics = graphics.getFontMetrics( );
			int stringWidth = fontMetrics.stringWidth( this.element.symbol );
			int halfStringWidth = stringWidth / 2;
			graphics.setColor( Color.black );
			graphics.drawString( this.element.symbol, halfWidth-halfStringWidth, halfHeight );

			// Draw the element atomic number.
			stringWidth = fontMetrics.stringWidth( this.atomicNumber );
			halfStringWidth = stringWidth / 2;
			graphics.drawString( this.atomicNumber, halfWidth-halfStringWidth, halfHeight+fontMetrics.getAscent() );

			// Frame the tile.
			graphics.setColor( Color.black );
			graphics.drawRect( 0, 0, width-1, height-1 );
		}
	};
}

