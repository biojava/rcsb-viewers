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
 * Created on 2010/10/16
 *
 */ 
package org.rcsb.uiApp.ui.dialogs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * ImageFileSaver saves BufferedImages in a specified resolution to standard image file formats.
 * 
 * @author John L. Moreland
 * @author Peter W. Rose
 *
 */
public final class ImageFileReader {

    /*  Reads an image file using any of the supported
	 *  Image Format plug-ins. 
	 */
	public static BufferedImage read(final File file)
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
			final Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix( suffix );

			// Pick the first one to use
			final ImageReader imageReader = readers.next( );

			// Get an input stream for the file
			final FileInputStream fin = new FileInputStream( file );

			// Turn it into an ImageInputStream
			final ImageInputStream iis = ImageIO.createImageInputStream( fin );

			// Plug this stream into the ImageReader
			imageReader.setInput( iis, false );

			// Read the image
			final BufferedImage image = imageReader.read( 0 );
			fin.close( );

			return image;
		}
		catch( final IOException ie )
		{
			ie.printStackTrace( );
			return null;
		}
	}
}
