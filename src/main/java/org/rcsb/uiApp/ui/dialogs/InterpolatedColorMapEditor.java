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
package org.rcsb.uiApp.ui.dialogs;


// Core
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.rcsb.mbt.model.attributes.*;
import org.rcsb.uiApp.ui.dialogs.ColorChooserDialog;


import java.text.*;

// MBT


/**
 *  A graphical component which enables editing of an InterpolatedColorMap
 *  instance.
 *  <P>
 *  <CENTER>
 *  <IMG SRC="doc-files/InterpolatedColorMapEditor.jpg">
 *  </CENTER>
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IColorMap
 */
public class InterpolatedColorMapEditor
	extends JPanel
	implements FocusListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3786922196090624044L;
	// Private viarables.
	private InterpolatedColorMap interpolatedColorMap = null;
	private float color[] = null;
	private final int controlAreaHeight = 30;
	private final int markerWidth = 3;
	private final int handleSize = 7;
	private int editKnot = -1;
	private boolean knotMoved = false;
	private long lastClickTime = 0;
	private boolean hasFocus = false;

	private ColorChooserDialog colorChooser = null;
	private Color background = null;


	/**
	 *  Construct an instance of this class using the given
	 *  InterpolatedColorMap object.
	 */
	public InterpolatedColorMapEditor( final InterpolatedColorMap interpolatedColorMap )
	{
		this.interpolatedColorMap = interpolatedColorMap;
		this.color = new float[3];

		final int knotCount = interpolatedColorMap.getKnotCount( );
		// System.err.println( "InterpolatedColorMapEditor.main: knotCount = " + knotCount );
		for ( int k=0; k<knotCount; k++ )
		{
			interpolatedColorMap.getKnotColor( k, this.color );
			// System.err.println( "InterpolatedColorMapEditor.main: knot[" + k + "] = " + color[0] + ", " + color[1] + ", " + color[2] );
		}

		final MouseAdapter mouseAdapter = new MouseAdapter()
		{
			
			public void mousePressed( MouseEvent mouseEvent )
			{
				InterpolatedColorMapEditor.this.doMousePressed( mouseEvent );
			}

			
			public void mouseReleased( MouseEvent mouseEvent )
			{
				InterpolatedColorMapEditor.this.doMouseReleased( mouseEvent );
			}
		};
		this.addMouseListener( mouseAdapter );

		final MouseMotionAdapter mouseMotionAdapter = new MouseMotionAdapter()
		{
			
			public void mouseDragged( MouseEvent mouseEvent )
			{
				InterpolatedColorMapEditor.this.doMouseDragged( mouseEvent );
			}
		};
		this.addMouseMotionListener( mouseMotionAdapter );

		this.setFocusable( true );
		this.addFocusListener( this );

		final KeyAdapter keyAdapter = new KeyAdapter()
		{
			
			public void keyPressed( KeyEvent keyEvent )
			{
				InterpolatedColorMapEditor.this.doKeyPressed( keyEvent );
			}
		};
		this.addKeyListener( keyAdapter );
	}


	/**
	 * Called when the panel gains focus and ensures that the selected
	 * focus highlight is drawn.
	 */
	public void focusGained( final FocusEvent focusEvent )
	{
		this.hasFocus = true;
		this.repaint( );
	}


	/**
	 * Called when the panel loses focus and ensures that the selected
	 * edit knot (if any) is deselected.
	 */
	public void focusLost( final FocusEvent focusEvent )
	{
		this.hasFocus = false;
		this.editKnot = -1; // Since it can not be manipulated without focus.
		this.repaint( );
	}


	/**
	 *  Called when the mouse button is pressed inside the panel.
	 */
	private void doMousePressed( final MouseEvent mouseEvent )
	{
		this.requestFocus( );
		this.editKnot = this.knotHit( mouseEvent.getX(), mouseEvent.getY() );
		final int rampHeight = this.getHeight() - this.controlAreaHeight;

		if ( this.editKnot >= 0 )
		{
			// The user clicked down on a knot, so select it.
			this.setEditKnot( this.editKnot );
		}
		else if ( (this.editKnot < 0) && (mouseEvent.getY() < rampHeight) )
		{
			// The user clicked in the ramp area, so add a new knot.
			final int knotCount = this.interpolatedColorMap.getKnotCount( );
			final float oldKnots[][] = new float[knotCount][4];
			final float newKnots[][] = new float[knotCount+1][4];
			this.interpolatedColorMap.getKnots( oldKnots );
			final float location = this.positionToSample( mouseEvent.getX() );
			final float newColor[] = new float[3];
			this.interpolatedColorMap.getColor( location, newColor );
			int oldIndex = 0;
			boolean inserted = false;
			for ( int newIndex=0; newIndex<newKnots.length; newIndex++ )
			{
				newKnots[newIndex][0] = oldKnots[oldIndex][0];
				newKnots[newIndex][1] = oldKnots[oldIndex][1];
				newKnots[newIndex][2] = oldKnots[oldIndex][2];
				newKnots[newIndex][3] = oldKnots[oldIndex][3];

				if ( (! inserted) && (location > oldKnots[oldIndex][0]) && (location < oldKnots[oldIndex+1][0]) )
				{
					// The new knot can be inserted here.
					newKnots[newIndex+1][0] = location;
					newKnots[newIndex+1][1] = newColor[0];
					newKnots[newIndex+1][2] = newColor[1];
					newKnots[newIndex+1][3] = newColor[2];
					this.editKnot = newIndex + 1;
					inserted = true;
					newIndex++;
					// newIndex++ will cause loop to end one iteration sooner
					// which is OK since we have filled in the next knot,
					// and, the oldIndex will also stay in bounds when we
					// increment it below.
				}
				oldIndex++;
			}
			this.interpolatedColorMap.setKnots( newKnots );

			// Select the new knot.
			this.setEditKnot( this.editKnot );
		}
		else if ( this.editKnot < 0 )
		{
			// The user clicked outside of a knot, so de-select.
			this.setEditKnot( this.editKnot );
		}

		this.knotMoved = false;
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
	 *  Called when the mouse button is released inside the panel.
	 */
	private void doMouseReleased( final MouseEvent mouseEvent )
	{
		final long newClickTime = System.currentTimeMillis( );
		if ( (this.editKnot != -1) && (! this.knotMoved ) )
		{
			// The user clicked on a knot without moving it.
			if ( (newClickTime - this.lastClickTime) < 500 )
			{
				// User double-clicked on a knot without moving it,
				// so bring up the color editor for that knot.
				this.interpolatedColorMap.getKnotColor( this.editKnot, this.color );
				final int oldEditKnot = this.editKnot; // Window focus will change it!

				// Create the color chooser, if this is
				// the first use.
				if ( this.colorChooser == null )
				{
					// Trace our lineage to a frame or
					// dialog.
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
					this.colorChooser.setTitle( "Color" );
				}

				this.colorChooser.setColor( new Color( this.color[0], this.color[1], this.color[2] ) );
				this.colorChooser.show( );
				if ( this.colorChooser.wasOKPressed( ) == true )
				{
					final Color userColor = this.colorChooser.getColor( );

					this.setEditKnot( oldEditKnot ); // ReSet editKnot after focus chng.
					if ( userColor != null )
					{
						userColor.getRGBColorComponents( this.color );
						this.interpolatedColorMap.setKnotColor( oldEditKnot, this.color );
					}
				}
			}
		}
		this.lastClickTime = newClickTime;
		this.knotMoved = false;
	}


	/**
	 *  Called when the mouse is moved with button down inside the panel.
	 */
	private void doMouseDragged( final MouseEvent mouseEvent )
	{
		if ( this.editKnot != -1 )
		{
			this.knotMoved = true;

			if ( this.interpolatedColorMap == null ) {
				return;
			}

			// Don't edit position of first knot.
			if ( this.editKnot == 0 ) {
				return;
			}

			// Don't edit position of last knot.
			final int knotCount = this.interpolatedColorMap.getKnotCount( );
			if ( this.editKnot >= (knotCount-1) ) {
				return;
			}

			// A knot handle is being dragged.
			final float location = this.positionToSample( mouseEvent.getX() );
			this.interpolatedColorMap.setKnotLocation( this.editKnot, location );
			this.repaint( );
		}
	}


	/**
	 *  Called when a key is pressed inside the panel.
	 */
	public void doKeyPressed( final KeyEvent keyEvent )
	{
		final char keyChar = keyEvent.getKeyChar( );
		final int keyCode = keyEvent.getKeyCode( );
		if ( (keyChar == 0x08) || ( keyChar == 0x7F) )
		{
			// The BACKSPACE or DELETE key was pressed.
			if ( this.editKnot >= 0 )
			{
				// A knot is selected (editKnot is defined).

				// If its the first of or last knot, don't delete it!
				final int knotCount = this.interpolatedColorMap.getKnotCount( );
				if ( this.editKnot == 0 ) {
					return;
				}
				if ( this.editKnot >= (knotCount-1) ) {
					return;
				}

				// There is a selected knot, so delete it.
				boolean knotRemoved = false;
				final float oldKnots[][] = new float[knotCount][4];
				final float newKnots[][] = new float[knotCount-1][4];
				this.interpolatedColorMap.getKnots( oldKnots );
				for ( int i=0,j=0; i<newKnots.length; i++,j++ )
				{
					if ( (i == this.editKnot) && ! knotRemoved )
					{
						knotRemoved = true;
						j++;
					}
					newKnots[i][0] = oldKnots[j][0];
					newKnots[i][1] = oldKnots[j][1];
					newKnots[i][2] = oldKnots[j][2];
					newKnots[i][3] = oldKnots[j][3];
				}
				this.interpolatedColorMap.setKnots( newKnots );
				this.editKnot = -1;
				this.repaint( );
			}
		}
		else if ( (keyCode == KeyEvent.VK_LEFT) || ( keyCode == KeyEvent.VK_RIGHT) )
		{
			// The LEFT or RIGHT arrow key was pressed.
			if ( this.editKnot >= 0 )
			{
				// There is a selected knot, so nudge it.
				float nudge = 0.0f;
				if ( keyCode == KeyEvent.VK_LEFT ) {
					nudge = -0.01f;
				}
				if ( keyCode == KeyEvent.VK_RIGHT ) {
					nudge = +0.01f;
				}
				final float val = this.interpolatedColorMap.getKnotLocation( this.editKnot );
				this.interpolatedColorMap.setKnotLocation( this.editKnot, val + nudge );
				this.repaint( );
			}
		}
	}


	/**
	 *  Given an (x,y) location (in panel coordinates) return the index of
	 *  the knot if one was hit or -1 if no knot was hit.
	 */
	private int knotHit( final int x, final int y )
	{
		final int knotCount = this.interpolatedColorMap.getKnotCount( );
		final int rampHeight = this.getHeight() - this.controlAreaHeight;
		final int width = this.getWidth( );
		final int mhOffset = (this.handleSize - this.markerWidth) / 2;
		for ( int k=0; k<knotCount; k++ )
		{
			final float knotLocation = this.interpolatedColorMap.getKnotLocation( k );
			final int handleLeft = (int) (knotLocation * width);
			if ( x < handleLeft-mhOffset ) {
				continue;
			}
			if ( x > handleLeft-mhOffset+this.handleSize ) {
				continue;
			}
			if ( y < rampHeight ) {
				continue;
			}
			if ( y > rampHeight+this.handleSize ) {
				continue;
			}
			return k;
		}
		return -1;
	}


	/**
	 *  Given an X-location (in panel coordinates) return the sample
	 *  value represented by the offset.
	 */
	private float positionToSample( final int position )
	{
		if ( position <= 0 ) {
			return 0.0f;
		}
		final int width = this.getWidth( );
		if ( position >= width ) {
			return 1.0f;
		}
		return (float) position / (float) width;
	}


	/**
	 *  Set the index of the knot to edit.
	 */
	private void setEditKnot( final int knotIndex )
	{
		this.editKnot = knotIndex;
		this.repaint( );
	}


	/**
	 *  Set a new InterpolatedColorMap to edit.
	 */
	public void setInterpolatedColorMap( final InterpolatedColorMap icm )
	{
		this.interpolatedColorMap = icm;
		this.editKnot = -1;
		this.repaint( );
	}


	/**
	 *  Paint the color ramp.
	 */
	
	public void paint( final Graphics g )
	{
		// Sanity checks.
		if ( g == null ) {
			return;
		}
		if ( this.interpolatedColorMap == null ) {
			return;
		}

		// Get the size of the panel
		final int width = this.getWidth( );
		final int height = this.getHeight( );
		final int rampHeight = height - this.controlAreaHeight;
		final float sampleStep = 1.0f / width;

		//
		// Fill the entire panel with a background color.
		//

		g.setColor( this.getBackground( ) );
		g.fillRect( 0, 0, width, height );

		//
		// Color the ramp area.
		//

		for ( int i=0; i<width; i++ )
		{
			final float f = sampleStep * i;
			this.interpolatedColorMap.getColor( f, this.color );
			final Color colorObject = new Color( this.color[0], this.color[1], this.color[2] );
			g.setColor( colorObject );
			g.fillRect( i, 0, 1, rampHeight );
		}
		g.setColor( Color.black );
		g.drawRect( 0, 0, width, rampHeight );

		//
		// Draw knot markers.
		//

		final NumberFormat nf = NumberFormat.getInstance( );
		nf.setMaximumFractionDigits( 3 );
		final Font font = g.getFont( );
		final FontMetrics fontMetrics = g.getFontMetrics( );
		final int fontHeight = fontMetrics.getHeight( );

		final int knotCount = this.interpolatedColorMap.getKnotCount( );
		final int mhOffset = (this.handleSize - this.markerWidth) / 2;
		for ( int k=0; k<knotCount; k++ )
		{
			final float knotLocation = this.interpolatedColorMap.getKnotLocation( k );
			int x = (int) (knotLocation * width);
			if ( k == (knotCount-1) ) {
				x = width - this.markerWidth;
			}

			// Draw the knot indicator.
			g.setColor( Color.black );
			g.drawRect( x, 0, this.markerWidth, rampHeight );

			// Draw the knot handle.
			this.interpolatedColorMap.getKnotColor( k, this.color );
			g.setColor( new Color( this.color[0], this.color[1], this.color[2] ) );
			g.fillRect( x-mhOffset, rampHeight, this.handleSize, this.handleSize );
			g.setColor( Color.black );
			g.drawRect( x-mhOffset, rampHeight, this.handleSize, this.handleSize );
			if ( k == this.editKnot )
			{
				// If the knot is the editKnot,t he highlight its handle.
				g.drawRect( x-mhOffset-1, rampHeight-1, this.handleSize+2, this.handleSize+2 );
				g.drawRect( x-mhOffset-2, rampHeight-2, this.handleSize+4, this.handleSize+4 );
			}

			// Draw the knot value/label.
			final String label = nf.format( knotLocation );
			final int stringWidth = fontMetrics.stringWidth( label );
			int stringX = x - stringWidth / 2;
			if ( k == 0 ) {
				stringX = 1; // Make end knot labels visible!
			}
			if ( k == (knotCount-1) ) {
				stringX = x - stringWidth;
			}
			final int stringY = rampHeight + this.handleSize + fontHeight;
			g.drawString( label, stringX, stringY );

			// Draw the focus hilight.
			if ( this.hasFocus )
			{
				// JLM DEBUG: Need to get the system focus color? where?
				g.setColor( Color.blue );
				g.drawRect( 0, 0, width-1, height-1 );
			}
		}
	}


	//
	// Unit testing.
	//
	///////////////////////////////////////////////////////////////////////


	/**
	 *  This is the main application entry point for the example program.
	 */
	public static void main( final String args[] )
	{
		// Create an instance of each pre-defined color map.
		final InterpolatedColorMap icm[] =
		{
			new InterpolatedColorMap( InterpolatedColorMap.RAINBOW ),
			new InterpolatedColorMap( InterpolatedColorMap.COLD_TO_HOT ),
			new InterpolatedColorMap( InterpolatedColorMap.BLACK_TO_WHITE ),
			new InterpolatedColorMap( InterpolatedColorMap.HSB_HUES )
		};
		// Names for the test buttons.
		final String icmn[] =
		{
			"RAINBOW",
			"COLD_TO_HOT",
			"BLACK_TO_WHITE",
			"HSB_HUES"
		};

		// Create an editor and add it to a frame.
		final InterpolatedColorMapEditor icme =
			new InterpolatedColorMapEditor( icm[0] );
		final JFrame frame = new JFrame( "InterpolatedColorMapEditor" );
		final Container container = frame.getContentPane( );
		container.setLayout( new BorderLayout( ) );
		container.add( BorderLayout.CENTER, icme );

		// Add buttons to enable editing of each pre-defined color map.
		final JPanel buttonPanel = new JPanel( );
		buttonPanel.setLayout( new FlowLayout( ) );
		container.add( BorderLayout.SOUTH,  buttonPanel );
		for ( int i=0; i<icm.length; i++ )
		{
			final JButton button = new JButton( icmn[i] );
			final MyActionListener actionListener =
				icme.createMyActionListener( icme, icm[i] );
			button.addActionListener( actionListener );
			buttonPanel.add( button );
		}

		frame.setSize( 500, 150 );
		frame.setVisible( true );
	}

	/**
	 * Only used for unit testing from "main" method.
	 */
	protected MyActionListener createMyActionListener(
		final InterpolatedColorMapEditor icme, final InterpolatedColorMap icm )
	{
		return new MyActionListener( icme, icm );
	}

	/**
	 * Only used for unit testing from "main" method.
	 */
	private class MyActionListener
		implements ActionListener
	{
		InterpolatedColorMap icm = null;
		InterpolatedColorMapEditor icme = null;
		public MyActionListener( final InterpolatedColorMapEditor icme,
			final InterpolatedColorMap icm )
		{
			this.icm = icm;
			this.icme = icme;
		}
		public void actionPerformed( final ActionEvent e )
		{
			this.icme.setInterpolatedColorMap( this.icm );
		}
	};
}

