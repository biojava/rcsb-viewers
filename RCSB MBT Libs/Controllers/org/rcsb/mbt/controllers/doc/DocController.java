package org.rcsb.mbt.controllers.doc;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.scene.PdbToNdbConverter;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.rcsb.mbt.glscene.jogl.JoglSceneNode;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.mbt.structLoader.IStructureXMLHandler;
import org.rcsb.mbt.structLoader.PdbStructureLoader;
import org.rcsb.mbt.structLoader.StructureXMLHandler;
import org.rcsb.mbt.ui.dialogs.ImageFileManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class DocController
{
    private String initialBiologicalUnitId = null; 
    
    /**
     * This is used by the StructureXMLHandler - nothing seems to set it, though...
     * 
     * @return - initial biological unit identifier.
     */
	public String getInitialBiologicalUnitId() {
		return this.initialBiologicalUnitId;
	}

	public void setInitialBiologicalUnitId(String initialBiologicalUnitId)
	{
		if(initialBiologicalUnitId != null)
		{
			initialBiologicalUnitId = initialBiologicalUnitId.trim();
			if(initialBiologicalUnitId.length() == 0)
				initialBiologicalUnitId = null;
		}
		
		this.initialBiologicalUnitId = initialBiologicalUnitId;
	}
	
	
	public void loadStructure(final String url, final String pdbId)
	{
		if (url == null)
			; // stick up an error message box.
		
		Structure[] structure = readStructuresFromUrl(url);
		if (pdbId != null && structure.length == 1)
			structure[0].getStructureMap().setPdbId(pdbId);
		
		AppBase.sgetModel().setStructures(structure);
		
		AppBase.sgetUpdateController().fireUpdateViewEvent(
				UpdateEvent.Action.STRUCTURE_ADDED,
				AppBase.sgetModel().getStructures().get(0));
	}

	/**
	 * Given a url, read the structures and return the structures array.
	 * 
	 * @param structureUrlParam - the url to read.  can be a system filename.
	 * @return - an array of Structures
	 */
	public Structure[] readStructuresFromUrl(String structureUrlParam)
	{
		final Vector<Structure> structuresVec = new Vector<Structure>();
		PdbToNdbConverter residueConverter = null;
		String[] nonProteinChainIds = null;
		Structure structureTmp = null;
	
		final String[] datasets = structureUrlParam.split(",");
		for (int i = 0; i < datasets.length; i++) {
			final String dataset = datasets[i];
	
			final int colonIndex = dataset.indexOf(':');
			final boolean isUrl = dataset.charAt(colonIndex + 1) == '/'
					&& dataset.charAt(colonIndex + 2) == '/'; // else,
			// this
			// is a
			// file...
	
			if (dataset.endsWith(".xml.gz") || dataset.endsWith(".xml"))
			{
				try
				{
					// long time = System.currentTimeMillis();
					final IStructureXMLHandler handler = AppBase.sgetAppModuleFactory().createStructureXMLHandler(dataset);
					final SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
	
					BufferedReader reader = null;
	
					// if this is a url (starts with protocol://)
					if (isUrl)
					{
						final URL url = new URL(dataset);
						final URLConnection urlConnection = url
								.openConnection();
						final InputStream inputStream = urlConnection
								.getInputStream();
	
						if (dataset.endsWith(".gz"))
							reader = new BufferedReader(
									new InputStreamReader(
											new GZIPInputStream(
													inputStream)));
						else
							reader = new BufferedReader(
									new InputStreamReader(inputStream));
					}
					
					else
					{ // if this is a file
						if (dataset.endsWith(".gz"))
							reader = new BufferedReader(
									new InputStreamReader(
											new GZIPInputStream(
													new FileInputStream(
															dataset))));
						
						else
							reader = new BufferedReader(
									new InputStreamReader(
											new FileInputStream(dataset)));
					}
	
					saxParser.parse(new InputSource(reader), (DefaultHandler)handler);
	
					// retrieve the data from the handler.
					structureTmp = handler.getStructure();
					residueConverter = handler.getIDConverter();
					nonProteinChainIds = handler.getNonProteinChainIds();
					
					finalizeNewStructure(handler, structureTmp);
					
					if (handler.getUnitCell() != null)
						structureTmp.getStructureMap().setUnitCell(
							handler.getUnitCell());
					// TODO getUnitCell, set on structure map
				}
				
				catch (final MalformedURLException e) {
					e.printStackTrace();
					System.out
							.println("Error: Bad url to the structure xml file.");
				}
				
				catch (final IOException e) {
					e.printStackTrace();
					System.out.println("Error: " + e.toString());
				}
				
				catch (final ParserConfigurationException pce) {
					pce.printStackTrace();
					// System.exit(1);
				}
				
				catch (final SAXException se) {
					se.printStackTrace();
					// System.exit(1);
				}
			}
			
			else if (dataset.matches("^.+\\.pdb\\d*(\\.gz)?$")
					|| dataset.endsWith(".ent.gz")
					|| dataset.endsWith(".ent"))
			{
				final PdbStructureLoader loader = new PdbStructureLoader();
				if (isUrl)
				{
					try
					{
						structureTmp = loader.load(new URL(dataset));
					}
					
					catch (final MalformedURLException e)
					{
						e.printStackTrace();
					}
				}
				
				else 
					structureTmp = loader.load(new File(dataset));
	
				residueConverter = loader.getConverter();
				nonProteinChainIds = new String[] {};
						// **JB don't worry about this for now...
			}
			
			else
				Status.output(Status.LEVEL_ERROR,
								"Could not open file: the file must have an extension of .xml, xml.gz, .pdb, .pdb.gz, .ent, or .ent.gz.");
	
			if (structureTmp == null)
			{
				System.out.println("Could not load: " + dataset);
				System.exit(1);
			}
			
			else
				System.out.println("Data set loaded: " + dataset);
	
			structureTmp.getStructureMap().setConverter(residueConverter);
			structureTmp.getStructureMap().setNonproteinChainIds(nonProteinChainIds);
	
			structuresVec.add(structureTmp);
		}
			// global transforms set in _BU version...
		
		// else no structure loaded.
	
		final Structure[] structures = new Structure[structuresVec.size()];
		for (int i = 0; i < structures.length; i++)
		{
			structures[i] = (Structure) structuresVec.get(i);
			structures[i].getStructureMap().setPdbId("NOID");
			structures[i].getStructureMap().setImmutable();
		}
		
		return structures;
	}

///////////////////////////////////////////////////////////////////////////////////////////////
//
// Beg Image Save implementation
//
///////////////////////////////////////////////////////////////////////////////////////////////
	
	private final class ImageSaverThread extends Thread
	{
		private static final int DURATION_BETWEEN_SCREENSHOT_CHECKS_IN_MILLISECONDS = 1000;

		private class SaverRunnable implements Runnable {
			private int width;
			private int height;
			private File file;
			private ImageFileManager manager;
			
			public SaverRunnable(int width, int height, File file, ImageFileManager manager) {
				this.width = width;
				this.height = height;
				this.file = file;
				this.manager = manager;
			}
			
			public void run() {
				final GlGeometryViewer viewer = AppBase.sgetGlGeometryViewer();
				viewer.requestScreenShot(width, height);

				Thread t = new Thread() {

					@Override
					public void run() {
//						 check for the screenshot regularly until it appears or the
						// timeout expires.
						BufferedImage screenshot = null;
						while (screenshot == null && !viewer.hasScreenshotFailed()) {
							try {
								Thread
										.sleep(ImageSaverThread.DURATION_BETWEEN_SCREENSHOT_CHECKS_IN_MILLISECONDS);
							} catch (final InterruptedException e) {
							}

							screenshot = viewer.getScreenshot();
						}

						if (screenshot != null) {
							manager.save(screenshot, file);
							viewer.clearScreenshot();
							Status.output(Status.LEVEL_REMARK, "Image saved.");
						} else {
							Status.output(Status.LEVEL_REMARK, "Error saving the image.");
						}
					}
					
				};
				
				t.start();
			}
		}
		
		@Override
		public void run() 
		{
			final GlGeometryViewer viewer = AppBase.sgetGlGeometryViewer();

			int width = viewer.getWidth();
			int height = viewer.getHeight();

			// Ask the user for file name, image file format, and image size.
			final ImageFileManager imageFileManager = new ImageFileManager(
					AppBase.sgetActiveFrame());
			final File file = imageFileManager.save(width, height);
			if (file == null)
			{
				return; // User canceled the save.
			}
			width = imageFileManager.getSaveWidth();
			height = imageFileManager.getSaveHeight();

			SaverRunnable run = new SaverRunnable(width, height, file, imageFileManager);
			SwingUtilities.invokeLater(run);
		}
	}
	
	/**
	 * Save the image (to where???)
	 */
	public void saveImage() {
//		Runnable runnable = new Runnable() {
//			public void run() {
				final ImageSaverThread screenshotWaiter = new ImageSaverThread();
				// wait until the image is obtained or the timeout occurs
				screenshotWaiter.start();
//			}
//		};
//		
//		SwingUtilities.invokeLater(runnable);
	}
///////////////////////////////////////////////////////////////////////////////////////////////
//
// End Image Save Implementation
//
///////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param approveButtonText
	 * @return
	 */
	public File askUserForXmlFile(final String approveButtonText) {
		final JFileChooser chooser = new JFileChooser();
		final FileFilter filter = new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.isFile()) {
					return f.getName().endsWith(".xml");
				}
				return true;
			}

			@Override
			public String getDescription() {
				return "XML Files";
			}
		};
		chooser.setFileFilter(filter);

		chooser.showDialog(AppBase.sgetActiveFrame(),
				approveButtonText);

		return chooser.getSelectedFile();
	}

	/**
	 * Hook function to allow subclasses to bless newly created structures read
	 * from the XML handler.
	 * 
	 * Optionally overridden
	 * 
	 * @param handler - the active XML handler
	 * @param structureTmp - the newly created structure
	 */
	public void finalizeNewStructure(IStructureXMLHandler handler, Structure structureTmp)
	{
		StructureMap structureMap = new StructureMap(structureTmp, AppBase.sgetAppModuleFactory().createSceneNode());
		// explicitly create a structureMap for the struc

		StructureXMLHandler xmlHandler = (StructureXMLHandler)handler;
		if (xmlHandler.hasBiologicUnitTransformationMatrices())
		{
			StructureMap.BiologicUnitTransforms bu = structureMap.addBiologicUnitTransforms();
			bu.setBiologicalUnitGenerationMatrices(xmlHandler.getBiologicalUnitTransformationMatrices());
		}

		else if (xmlHandler.hasNonCrystallographicOperations())
		{
			StructureMap.NonCrystallographicTransforms nc = structureMap.addNonCrystallographicTransforms();
			nc.setNonCrystallographicTranslations(xmlHandler.getNonCrystallographicOperations());
		}
	}
}
