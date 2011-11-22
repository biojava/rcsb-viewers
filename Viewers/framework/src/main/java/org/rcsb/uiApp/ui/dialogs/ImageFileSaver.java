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
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.Element;


import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;
import com.sun.media.jai.codecimpl.TIFFImageEncoder;

/**
 * ImageFileSaver saves BufferedImages in a specified resolution to standard image file formats.
 * This class supports the file formats specified in {@link ImageFileFormat}.
 * 
 * @author Peter W. Rose
 *
 */
public final class ImageFileSaver {
	/**
	 * Saves image in the specified file at the default resolution of 96 dots per inch (dpi). 
	 * The file extension must match on of the supported image file formats defined in {@link ImageFileFormat};
	 * If the file extension doesn't match any of the supported file formats, no file will be saved.
	 * 
	 * @param image image to be saved
	 * @param file file in which the image to be saved
	 * @throws IOException 
	 */
	public static void save(final BufferedImage image, final File file) throws IOException {
		int dpi = 96;
		save(image, dpi, file);
	}
	
	/**
	 * Saves image in the specified file at the specified resolution. 
	 * The file extension must match on of the supported image file formats defined in {@link ImageFileFormat};
	 * If the file extension doesn't match any of the supported file formats, no file will be saved.
	 * *
	 * @param image image to be saved
	 * @param dpi resolution in dots per inch
	 * @param file file in which the image to be saved
	 * @throws IOException 
	 */
	public static void save(final BufferedImage image, int dpi, final File file) throws IOException {
		String filename = file.getAbsolutePath();
		int position = filename.lastIndexOf('.') + 1;
		if (position < 1) {
			throw new IOException("Invalid image file extension.");
		}
		String extension = filename.substring(position);

		// save image in the requested format
		if (extension.equals(ImageFileFormat.JPEG.getExtension())) {
			saveJPEG(image, dpi, filename);
		} else if (extension.equals(ImageFileFormat.PNG.getExtension())) {
			savePNG(image, dpi, filename);
		} else if (extension.equals(ImageFileFormat.TIFF.getExtension())) {
			saveTIFF(image, dpi, filename);
		} else {
			throw new IOException("Nonsupported file extension.");
		}
	}
	
   
	
	/**
	 * Saves image as a JPEG file at the specified resolution.
	 * 
	 * @param image image to be saved
	 * @param dpi resolution in dots per inch
	 * @param file filename name of the JPEG file
     * @throws IOException
     */
	private static void saveJPEG(final BufferedImage image, final int dpi, final String filename) throws IOException
	{	
		// get image writer 
        ImageWriter imageWriter = ImageIO.getImageWritersBySuffix(ImageFileFormat.JPEG.name()).next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(new File(filename));
        imageWriter.setOutput(ios);

        JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();
        // Compression - disabled, images don't look good with compression!
//        jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
//        jpegParams.setCompressionQuality(0.85f);

        // Some resources related to setting metadata:
        // http://stackoverflow.com/questions/233504/write-dpi-metadata-to-a-jpeg-image-in-java
        // http://download.oracle.com/javase/6/docs/api/javax/imageio/metadata/doc-files/jpeg_metadata.html
        // http://java.sun.com/javase/6/docs/api/javax/imageio/metadata/doc-files/jpeg_metadata.html
        // http://forums.sun.com/thread.jspa?threadID=5425938
        // http://incubator.apache.org/sanselan/site/index.html
        IIOMetadata data = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(image), jpegParams);

        Element tree = (Element)data.getAsTree("javax_imageio_jpeg_image_1.0");
        Element jfif = (Element)tree.getElementsByTagName("app0JFIF").item(0);
        jfif.setAttribute("Xdensity", Integer.toString(dpi));
        jfif.setAttribute("Ydensity", Integer.toString(dpi));
        jfif.setAttribute("resUnits", "1"); // specifies dots per inch   
        data.setFromTree("javax_imageio_jpeg_image_1.0", tree);

        imageWriter.write(null, new IIOImage(image, null, data), jpegParams);
        ios.close();
        imageWriter.dispose();
	}
	
	/**
	 * Saves image as a PNG file at the specified resolution.
	 * 
	 * @param image image to be saved
	 * @param dpi resolution in dots per inch
	 * @param file filename name of the PNG file
     * @throws IOException
     */
	private static void savePNG(final BufferedImage image, final int dpi, final String filename) throws IOException
	{
		final FileOutputStream fout = new FileOutputStream( new File(filename) );

		PNGEncodeParam pngEncodeParam = PNGEncodeParam.getDefaultEncodeParam(image);
		// Sets the physical dimension information to be stored with this image. 
		// The physicalDimension parameter should be the number of pixels per unit 
		// in the X direction, the number of pixels per unit in the Y direction, 
		// and the unit specifier (0 = unknown, 1 = meters). 
		float metersToInches = 39.3700787f;
		int dpm = (int) Math.ceil(dpi * metersToInches);
		pngEncodeParam.setPhysicalDimension(dpm, dpm , 1);
		
		ImageEncoder encoder = ImageCodec.createImageEncoder(ImageFileFormat.PNG.toString(), fout, pngEncodeParam);
		encoder.encode(image);
		fout.close();
	}

	/**
	 * Saves image as a TIFF file at the specified resolution. Also sets the software metadata tag.
	 * 
	 * @param image image to be saved
	 * @param dpi resolution in dots per inch
	 * @param file filename name of the TIFF file
     * @throws IOException
     */
	private static void saveTIFF(final BufferedImage image, final int dpi, final String filename) throws IOException 
	{
		FileOutputStream fout = new FileOutputStream(new File(filename));
			
		// set no compression		
		TIFFEncodeParam param = new TIFFEncodeParam();
		param.setCompression(TIFFEncodeParam.COMPRESSION_NONE);
		
		// set the TIFF image resolution and other meta tags
		// http://forums.sun.com/thread.jspa?threadID=5372459
		// http://partners.adobe.com/public/developer/en/tiff/TIFF6.pdf
		TIFFField[] extras = new TIFFField[4];
        // TIFF RATIONAL type is represented by two LONGs: the first represents the numerator of a
        // fraction; the second, the denominator.
		// XResolution  in pixels per ResolutionUnit (is inches by default)
		extras[0] = new TIFFField(282, TIFFField.TIFF_RATIONAL, 1, (Object)new long[][] {{(long)dpi, (long)1},{(long)0 ,(long)0}});
		// YResolution in pixels per ResolutionUnit
		extras[1] = new TIFFField(283, TIFFField.TIFF_RATIONAL, 1, (Object)new long[][] {{(long)dpi, (long)1},{(long)0 ,(long)0}});
		// Software
		extras[2] = new TIFFField(305, TIFFField.TIFF_ASCII, 1, (Object)new String[] {"RCSB MBT Viewer"});
		// Artist, this does not seem to work.
		extras[3] = new TIFFField(315, TIFFField.TIFF_ASCII, 1, (Object)new String[] {"RCSB PDB"});
		param.setExtraFields(extras);

		TIFFImageEncoder encoder = new TIFFImageEncoder (fout, param);
		encoder.encode(image);
		fout.close();
	}

}
