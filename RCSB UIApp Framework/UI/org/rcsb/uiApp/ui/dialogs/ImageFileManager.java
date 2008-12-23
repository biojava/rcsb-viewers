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


import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.spi.*;
import javax.imageio.stream.*;
import javax.imageio.plugins.jpeg.*;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.swing.*;

/**
 *  This class provides methods for reading and writting image files
 *  in addition to providing filtered file browsing.
 *  <P>
 *  @author	John L. Moreland
 */
public class ImageFileManager
	extends JFileChooser
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7928466769150897385L;

	private static final String TIFF_DESCRIPTION = "TIFF (*.tif)";
	
	// Enables selection of any supported Image Format plug-in.
	private JComboBox imageFormatComboBox;

	// Which AWT Component to use as the parent for the chooser window.
	private Component parentComponent;

	// The last requested image width and height (see "save" call).
	private int saveWidth = 512;
	private int saveHeight = 512;
	private final JTextField widthField = new JTextField( "9999", 4 );
	private final JTextField heightField = new JTextField( "9999", 4 );


	/**
	 *  Construct a new ImageFileManager object.
	 */
	public ImageFileManager( final Component parentComponent )
	{
		super( new File( System.getProperty( "user.dir" ) ) );
		this.parentComponent = parentComponent;
	}

	/**
	 *  Describes a Format plug-in for use as a JFileChooser filter.
	 */
	static private class FormatDescriptor
	{
		public String suffix;
		public ImageWriter imageWriter;
		public String description;

		public FormatDescriptor( final String suffix, final ImageWriter imageWriter,
			final String description )
		{
			this.suffix = suffix;
			this.imageWriter = imageWriter;
			this.description = description;
		}

		
		public String toString( )
		{
			return this.description;
		}
	}

	/**
	 *  Return a vector of FormatDescriptor objects, one for each
	 *  supported Image Format plug-ins.
	 */
	static private Vector getWriterFormats( )
	{
		final Vector formats = new Vector( );
		final Hashtable seen = new Hashtable( );

		final String names[] = ImageIO.getWriterFormatNames( );
		for ( int i=0; i<names.length; ++i )
		{
			final String name = names[i];
			final Iterator writers = ImageIO.getImageWritersByFormatName( name );
			while ( writers.hasNext() )
			{
				final ImageWriter iw = (ImageWriter) writers.next( );
				final ImageWriterSpi iws = iw.getOriginatingProvider( );
				final String suffixes[] = iws.getFileSuffixes( );
				for ( int j=0; j<suffixes.length; ++j )
				{
					String suffix = suffixes[j];
					suffix = suffix.toLowerCase( );
					if ( ! seen.containsKey( suffix ) )
					{
						seen.put( suffix, suffix );
						final String description = name + " (*." + suffix + ")";
						final FormatDescriptor fd =
							new FormatDescriptor( suffix, iw, description );
						formats.addElement( fd );
					}
				}
			}
		}

		return formats;
	}

	/**
	 *  Load an image file using any of the supported
	 *  Image Format plug-ins.
	 */
	public BufferedImage load( final File file )
	{
		if ( file == null ) {
			return null;
		}

		try
		{
			final String filename = file.getName( );

			// Get the file suffix
			final String suffix =
				filename.substring( filename.lastIndexOf( '.' ) + 1 );

			// Get a set of ImageReaders that support that suffix
			final Iterator readers = ImageIO.getImageReadersBySuffix( suffix );

			// Pick the first one to use
			final ImageReader imageReader = (ImageReader) readers.next( );

			// Get an input stream for the file
			final FileInputStream fin = new FileInputStream( file );

			// Turn it into an ImageInputStream
			final ImageInputStream iis = ImageIO.createImageInputStream( fin );

			// Plug this stream into the ImageReader
			imageReader.setInput( iis, false );

			// Read the image
			final BufferedImage image = imageReader.read( 0 );

			// Dont forget to close the file!
			fin.close( );

			return image;
		}
		catch( final IOException ie )
		{
			ie.printStackTrace( );
			return null;
		}
	}

	/**
	 *  Prompt the user to load an image file using any of the supported
	 *  Image Format plug-ins.
	 */
	public BufferedImage load( )
	{
		final int ret = this.showOpenDialog( this.parentComponent );
		if ( ret == JFileChooser.APPROVE_OPTION )
		{
			return this.load( this.getSelectedFile() );
		}
		return null;
	}

	/**
	 *  Whenever the user selects a different image file format plug-in,
	 *  automatically update the file name extension of the user-supplied
	 *  file name.
	 */
	private void updateFileSuffix( final String suffix )
	{
		// Note that you can't get the selected/typed file name until
		// after the user hits the action button, so there's no
		// way to get/set the current file name string without
		// hacking into the raw panel components. But, since the
		// components change from version to version of the JDK, we
		// can't do that! Basically, the code below will never work! :^(
		final File file = this.getSelectedFile( );
		String filename = null;
		if ( file == null ) {
			// HACK. It looks like there is never more than one text field in the file chooser. This text field is the path.
			final Component[] components = super.getComponents();
			for(int i = 0; i < components.length; i++) {
				final Component component = components[i];
				if(component instanceof JTextField) {
					final JTextField field = (JTextField)component;
					filename = field.getText();
					System.err.println(filename);
				}
			}
		} else {
			filename = file.getPath( );
		}
		
		if(filename == null) {
			return;
		}

		// Modify the file extension in the file name text field.
		final int lp = filename.lastIndexOf( "." );
		if ( lp == -1 ) {
			return;
		}
		filename = filename.substring( 0, lp ) + "." + suffix;
		this.setSelectedFile( new File( filename ) );
	}

	/**
	 *  Show the custom Image Save dialog box.
	 */
	private void augmentSaveDialog( )
	{
		final Vector formats = ImageFileManager.getWriterFormats( );
		formats.add(0, ImageFileManager.TIFF_DESCRIPTION);

		this.imageFormatComboBox = new JComboBox( formats );
		this.imageFormatComboBox.addActionListener( new ActionListener( )
		{
			public void actionPerformed( final ActionEvent e )
			{
				if(ImageFileManager.this.imageFormatComboBox.getSelectedItem( ) instanceof FormatDescriptor) {
					final FormatDescriptor fd =
						(FormatDescriptor) ImageFileManager.this.imageFormatComboBox.getSelectedItem( );
					final String suffix = fd.suffix;
					ImageFileManager.this.updateFileSuffix( suffix );
				} else if(ImageFileManager.this.imageFormatComboBox.getSelectedItem( ) instanceof String) {
					String suffix = (String) ImageFileManager.this.imageFormatComboBox.getSelectedItem( );
					if(suffix == ImageFileManager.TIFF_DESCRIPTION) {
						suffix = "tif";
					}
					ImageFileManager.this.updateFileSuffix( suffix );
				}
			}
		} );

		final JLabel formatLabel = new JLabel( "Format:" );

		final JPanel formatRow = new JPanel( );
		formatRow.setLayout( new BorderLayout( 10, 10 ) );
		formatRow.add( formatLabel, BorderLayout.WEST );
		formatRow.add( this.imageFormatComboBox, BorderLayout.CENTER );

		final JPanel sizePanel = new JPanel( );
		sizePanel.setLayout( new FlowLayout( ) );
		sizePanel.add( new JLabel( "Width:" ) );
		sizePanel.add( this.widthField );
		sizePanel.add( new JLabel( "Height:" ) );
		sizePanel.add( this.heightField );
		formatRow.add( sizePanel, BorderLayout.SOUTH );

//		widthField.setEnabled(false);
//		heightField.setEnabled(false);
		
		final JPanel augmentation = new JPanel( );
		augmentation.setLayout( new BorderLayout( 10, 10 ) );
		augmentation.add( formatRow, BorderLayout.NORTH );

		augmentation.setBorder( BorderFactory.createTitledBorder("Image Settings") );

		this.setAccessory( augmentation );
	}

	/**
	 *  Save the given BufferedImage object to the specified file.
	 */
	public void save( final BufferedImage image, final File file )
	{
		try
		{
			String filename = file.getAbsolutePath();
			
			String dialogSuffix = null;
			if(this.imageFormatComboBox.getSelectedItem( )  instanceof FormatDescriptor) {
			final FormatDescriptor fd = (FormatDescriptor) this.imageFormatComboBox.getSelectedItem( );
				dialogSuffix = fd.suffix;
			} else if(this.imageFormatComboBox.getSelectedItem( )  instanceof String) {
				dialogSuffix = (String) this.imageFormatComboBox.getSelectedItem( );
				if(dialogSuffix == ImageFileManager.TIFF_DESCRIPTION) {
					dialogSuffix = "tif";
				}
			} else {
				new Exception(this.imageFormatComboBox.getSelectedItem( ).getClass().toString()).printStackTrace();
				return;
			}
			
			String suffix =
				filename.substring( filename.lastIndexOf( '.' ) + 1 );

			if(!suffix.equals(dialogSuffix)) {
				suffix = dialogSuffix;
				filename += "." + suffix;
			}
			
			// Get a set of ImageWriters that support that suffix
			if(suffix.equals("tif")) {	// use JAI.
//			    String filename = file.getPath();
			    final String format = "TIFF";

			    final RenderedOp op = JAI.create("filestore", image,
			                               filename, format);
			    op.dispose();
			} else {	// this is probably a standard java writer
				final Iterator writers = ImageIO.getImageWritersBySuffix( suffix );
				
				// Pick the first one to use
				final ImageWriter imageWriter = (ImageWriter) writers.next( );

				// Set the image quality parameters (if possible) to "best"
				final ImageWriteParam imageWriteParam =
					imageWriter.getDefaultWriteParam( );
				IIOImage iioImage = null;
				if ( imageWriteParam instanceof JPEGImageWriteParam )
				{
					imageWriteParam.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
					imageWriteParam.setCompressionQuality( 1.0f );
					iioImage = new IIOImage( image, null, null );
				}

				// Get an output stream for the file
				final FileOutputStream fout = new FileOutputStream( new File(filename) );

				// Turn it into an ImageOutputStreamn
				final ImageOutputStream ios = ImageIO.createImageOutputStream( fout );

				// Plug this stream into the ImageWriter
				imageWriter.setOutput( ios );

				// Write the image
				if ( iioImage != null ) {
					imageWriter.write( null, iioImage, imageWriteParam );
				} else {
					imageWriter.write( image );
				}

				// Dont forget to close the file!
				ios.close();
				fout.close();
			}

			
		}
		catch( final IOException ie )
		{
			ie.printStackTrace( );
		}
	}

	/**
	 *  Prompt the user to save the given BufferedImage object, and,
	 *  if the user approves, save the file to the user-specified file.
	 */
	public void save( final BufferedImage image )
	{
		this.augmentSaveDialog( );

		final int ret = this.showSaveDialog( this.parentComponent );
		if ( ret == JFileChooser.APPROVE_OPTION )
		{
			this.save( image, this.getSelectedFile() );
		}
	}

	/**
	 *  Prompt the user to save an image with default (re-setable)
	 *  resolution without doing the actual save, only returning a File
	 *  if the user hit "Save" or NULL otherwise.
	 */
	public File save( final int defaultWidth, final int defaultHeight )
	{
		this.augmentSaveDialog( );

		this.widthField.setText( String.valueOf( defaultWidth ) );
		this.heightField.setText( String.valueOf( defaultHeight ) );

		final int ret = this.showSaveDialog( this.parentComponent );
		if ( ret == JFileChooser.APPROVE_OPTION )
		{
			this.saveWidth = Integer.parseInt( this.widthField.getText() );
			this.saveHeight = Integer.parseInt( this.heightField.getText() );
			final File selectedFile = this.getSelectedFile();
			final String path = selectedFile.getAbsolutePath();
			//if(path)
			return selectedFile;
		}

		return null;
	}

	/**
	 *  Return the user-requested image width as set by the last call
	 *  to the "save" method.
	 */
	public int getSaveWidth( )
	{
		return this.saveWidth;
	}

	/**
	 *  Return the user-requested image height as set by the last call
	 *  to the "save" method.
	 */
	public int getSaveHeight( )
	{
		return this.saveHeight;
	}
}

