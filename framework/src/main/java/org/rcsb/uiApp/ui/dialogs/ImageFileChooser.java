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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
 
/**
 *  ImageFileChooser is a custom file chooser for image files.
 *  An accessory panel provides options to change image size
 *  and resolution.
 *
 *  @author Peter W. Rose
 */
public class ImageFileChooser extends JFileChooser
{	
	private static final long serialVersionUID = -6032740684618123482L;
	/*
	 * unit conversion factors
	 */
	private static float mmToInch = 1.0f/25.4f;
	private static Map<String, Float> conversionFactors = new TreeMap<String, Float>();
	static {
		conversionFactors.put("inch", 1.0f);
		conversionFactors.put("mm", mmToInch);
	}
	/*
	 * image dimension
	 */
	private int imageWidth = 0;
	private int imageHeight = 0;
	/*
	 * physical dimension
	 */
	private int dpi = 96;
	private float scale = 1.0f;
	private float printWidth = 0.0f;
	private float printHeight = 0.0f;
	/*
	 * component to use as the parent for the chooser window.
	 */
	private Component parentComponent;
	/*
	 * UI components for accessory panel
	 */
	private final JComboBox unitComboBox = new JComboBox(conversionFactors.keySet().toArray());
	private final JTextField imageWidthField = new JTextField();
	private final JTextField imageHeightField = new JTextField();
	private final JTextField printWidthField = new JTextField(); 
	private final JTextField printHeightField = new JTextField();
	private final JTextField dpiField = new JTextField();

	/**
	 *  Constructs a custom JFileChooser with the supported image file formats
	 *  @param parentComponent parent of this component
	 */
	public ImageFileChooser(final Component parentComponent)
	{
		super(new File(System.getProperty("user.dir")));
		this.parentComponent = parentComponent;
		this.setAcceptAllFileFilterUsed(false);
	//	this.addChoosableFileFilter(new ImageFileFilter(ImageFileFormat.TIFF));
		this.addChoosableFileFilter(new ImageFileFilter(ImageFileFormat.PNG));
	//	this.addChoosableFileFilter(new ImageFileFilter(ImageFileFormat.JPEG));
	}
	
	/**
	 * Prompts the user to save an image with options to customize the image
	 * size and resolution and returns the file specified by the user. The
	 * image size and resolution can be retrieved with the get methods of this class.
	 * @param defaultWidth initial with of image in pixels
	 * @param defaultHeight initial with of image in pixels
	 * @return File file to be saved, or null if the save operation was canceled
	 * or the image size or resolution were invalid.
	 */
	public File save(final int defaultWidth, final int defaultHeight)
	{
		addAccessoryPanel();
		setDialogTitle("Save Image");
		initialize(defaultWidth, defaultHeight);

		final int ret = showSaveDialog(parentComponent);
		if (ret == JFileChooser.APPROVE_OPTION)
		{
			if (! validImageDimensionSettings()) {
				JOptionPane.showMessageDialog(this,
						"Invalid image dimensions",
						"Incorrect image dimensions settings",
						JOptionPane.ERROR_MESSAGE); 
				return null;
			}
			if (! validPhysicalDimensionSettings()) {
				JOptionPane.showMessageDialog(this,
						"Invalid physical dimensions",
						"Incorrect physical dimension settings",
						JOptionPane.ERROR_MESSAGE); 
				return null;
			}
			ImageFileFilter filter = (ImageFileFilter) this.getFileFilter();
			File file = getSelectedFile();
			return addExtension(file, filter);
		}

		return null;
	}

	/**
	 *  Returns the user-requested image width as set by the last call
	 *  to the save method.
	 */
	public int getImageWidth()
	{
		return this.imageWidth;
	}

	/**
	 *  Returns the user-requested image height as set by the last call
	 *  to the save method.
	 */
	public int getImageHeight()
	{
		return this.imageHeight;
	}
	
	/**
	 *  Returns the user-requested dpi (dots per inch) as set by the last call
	 *  to the save method.
	 */
	public int getDpi()
	{
		return this.dpi;
	}
	
	/**
	 *  Adds an accessory panel to adjust image size and resolution
	 *  to the file save menu.
	 */
	private void addAccessoryPanel() {
		final JPanel accessoryPanel = new JPanel();
		accessoryPanel.setLayout(new BoxLayout(accessoryPanel, BoxLayout.Y_AXIS));
		accessoryPanel.add(createImageSettingsPanel());
		accessoryPanel.add(createPrintSettingsPanel());
		setAccessory(accessoryPanel);
		validate();
		repaint();
	}
	
	private JPanel createImageSettingsPanel() {
		final JPanel imageSettings = new JPanel( );
		imageSettings.setLayout( new GridLayout(2, 2, 0, 5) );
		JLabel width = new JLabel("Width:");
		width.setToolTipText("Width of image in pixels");
		imageSettings.add(width);
		imageSettings.add(imageWidthField);
		imageWidthField.setToolTipText("Width of image in pixels");
		imageWidthField.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				updateImageWidth();
			}
			public void keyPressed(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}	
		});
		JLabel height = new JLabel("Height:");
		height.setToolTipText("Height of image in pixels");
		imageSettings.add(height);
		imageSettings.add(imageHeightField);
		imageHeightField.setToolTipText("Height of image in pixels");
		imageHeightField.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				updateImageHeight();
			}
			public void keyPressed(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}	
		});
		
		final JPanel imagePanel = new JPanel( );
		imagePanel.setLayout(new BorderLayout(10, 10));
		imagePanel.add(imageSettings, BorderLayout.NORTH );
		imagePanel.setBorder( BorderFactory.createTitledBorder("Image Dimensions (pixels)") );
		
		return imagePanel;
	}
	
	private JPanel createPrintSettingsPanel() {
		final JPanel printSettings = new JPanel( );
//		printSettings.setLayout(new GridLayout(4, 2, 0, 5) );
		printSettings.setLayout(new GridLayout(6, 2, 0, 10) );
		JLabel unit = new JLabel("Unit:");
		unit.setToolTipText("Unit for print dimensions");
		printSettings.add(new JLabel("Unit:"));
		unitComboBox.setToolTipText("Unit for print dimensions");
		unitComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				updateUnit();
			}
		});
		printSettings.add(unitComboBox);
		JLabel width = new JLabel("Width:");
		width.setToolTipText("Width of printed image");
		printSettings.add(width);
		printSettings.add(printWidthField);
		printWidthField.setToolTipText("Width of printed image");
		printWidthField.addKeyListener(new KeyListener() {	
			public void keyReleased(KeyEvent e) {
				updatePrintWidth();
			}
			public void keyPressed(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}	
		});
		JLabel height = new JLabel("Height:" );
		height.setToolTipText("Height of printed image");
		printSettings.add(height);
		printSettings.add(printHeightField );
		printHeightField.setToolTipText("Height of printed image");
		printHeightField.addKeyListener(new KeyListener() {	
			public void keyReleased(KeyEvent e) {
				updatePrintHeight();
			}
			public void keyPressed(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}	
		});
		JLabel resolution = new JLabel("Resolution (dpi): ");
		resolution.setToolTipText("Resolution in dots per inch in a digital print. \n" +
				 "A typical range for high resolution printing is 300-600 dpi.");
		printSettings.add(resolution);
		printSettings.add(dpiField);
		dpiField.setToolTipText("Resolution in dots per inch in a digital print. \n" +
				 "A typical range for high resolution printing is 300-600 dpi.");
		dpiField.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
				updateDpi();
			}
			public void keyPressed(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}	
		});

		final JPanel printPanel = new JPanel();
		printPanel.setLayout(new BorderLayout(10, 10));
		printPanel.add(printSettings, BorderLayout.NORTH);
		printPanel.setBorder( BorderFactory.createTitledBorder("Physical Dimensions") );
		
		return printPanel;
	}
	
	private void initialize(final int defaultWidth, final int defaultHeight) {
		// initialize image dimension panel
		imageWidth = defaultWidth;
		imageHeight = defaultHeight;
		imageWidthField.setText(String.valueOf(imageWidth));
		imageHeightField.setText(String.valueOf(imageHeight));
		
		// initialize physical dimension panel
		printWidth = imageWidth/(float)dpi;
		printHeight = imageHeight/(float)dpi;
		printWidthField.setText(String.valueOf(printWidth));
	    printHeightField.setText(String.valueOf(printHeight));
		dpiField.setText(String.valueOf(dpi));
	}

	private void updatePrintWidth() {
		try {
		   printWidth = Float.parseFloat(printWidthField.getText());
		   imageWidth = Math.round(scale * printWidth * dpi);
		   imageWidthField.setText(String.valueOf(imageWidth));
		} catch (NumberFormatException e) {
		   printWidthField.setText("");
		   imageWidthField.setText("");
		}
	}
	
	private void updatePrintHeight() {
		try {
		   printHeight = Float.parseFloat(printHeightField.getText());
		   imageHeight = Math.round(scale * printHeight * dpi);
		   imageHeightField.setText(String.valueOf(imageHeight));
		} catch (NumberFormatException e) {
		   printHeightField.setText("");
		   imageHeightField.setText("");
		}
	}
	
	private void updateImageWidth() {
		try {
			imageWidth = Integer.parseInt(imageWidthField.getText());
			printWidth = imageWidth/(scale * dpi);
			printWidthField.setText(String.valueOf(printWidth));
		} catch (NumberFormatException e) {
			imageWidthField.setText("");
			printWidthField.setText("");
		}
	}
	
	private void updateImageHeight() {
		try {
			imageHeight = Integer.parseInt(imageHeightField.getText());
			printHeight = imageHeight/(scale * dpi);
			printHeightField.setText(String.valueOf(printHeight));
		} catch (NumberFormatException e) {
			imageHeightField.setText("");
			printHeightField.setText("");
		}
	}
	
	private void updateDpi() {
		try {
			dpi = Integer.parseInt(dpiField.getText());
			imageWidth = Math.round(scale * printWidth * dpi);
			imageHeight = Math.round(scale * printHeight * dpi);
			imageWidthField.setText(String.valueOf(imageWidth));
			imageHeightField.setText(String.valueOf(imageHeight));
		} catch (NumberFormatException e) {
			dpiField.setText("");
		}
	}
	
	private void updateUnit() {
		String unit = (String)unitComboBox.getSelectedItem();
		scale = conversionFactors.get(unit);
		imageWidth = Math.round(scale * printWidth * dpi);
		imageHeight = Math.round(scale * printHeight * dpi);
		imageWidthField.setText(String.valueOf(imageWidth));
		imageHeightField.setText(String.valueOf(imageHeight));
	}
	
	private boolean validImageDimensionSettings() {
		return ! (imageWidthField.getText().equals("") ||
		imageHeightField.getText().equals(""));
	}
	
	private boolean validPhysicalDimensionSettings() {
		return ! (printWidthField.getText().equals("") ||
		printHeightField.getText().equals("") ||
		dpiField.getText().equals(""));
	}
	
	private File addExtension(File file, ImageFileFilter filter) {
		String filename = file.getAbsolutePath();
		if (filename.endsWith("." + filter.getExtension())) {
			return file;
		} else {
			return new File(filename + "." + filter.getExtension());
		}
	}
}

