package org.rcsb.mbt.controllers.doc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.rcsb.mbt.glscene.jogl.JoglSceneNode;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.mbt.structLoader.IFileStructureLoader;
import org.rcsb.mbt.structLoader.IStructureLoader;
import org.rcsb.mbt.structLoader.PdbStructureLoader;
import org.rcsb.mbt.structLoader.StructureXMLHandler;
import org.rcsb.mbt.structLoader.XMLStructureLoader;
import org.rcsb.mbt.ui.dialogs.ImageFileManager;


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
			; // stick up an error message box?
		
		AppBase.sgetUpdateController().clear();
		
		Structure[] structure = readStructuresFromUrl(url);
		if (structure != null)
		{
			if (pdbId != null && structure.length == 1)
				structure[0].getStructureMap().setPdbId(pdbId);

			AppBase.sgetModel().setStructures(structure);
		}
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
		Structure structureTmp = null;
		
		if (AppBase.isDebug())
			System.err.println("--> DocController: Available memory: " +
					Runtime.getRuntime().freeMemory());
	
		final String[] datasets = structureUrlParam.split(",");
		for (int i = 0; i < datasets.length; i++)
		{
			try
			{
				final String dataset = datasets[i];
				IFileStructureLoader loader = null;
			
				if (dataset.endsWith(".xml.gz") || dataset.endsWith(".xml"))
				{
					// long time = System.currentTimeMillis();
					Status.progress(-1, "Reading XML file: " + dataset);
					
					loader =
						new XMLStructureLoader((StructureXMLHandler)AppBase.sgetAppModuleFactory().createStructureXMLHandler(dataset));
					((XMLStructureLoader)loader).setInitialBiologicalUnitId(initialBiologicalUnitId);
					
					structureTmp = loader.load(dataset);
				}
					
				
				else if (dataset.matches("^.+\\.pdb\\d*(\\.gz)?$")
						|| dataset.endsWith(".ent.gz")
						|| dataset.endsWith(".ent"))
				{
					loader = new PdbStructureLoader();
					((PdbStructureLoader)loader).setTreatModelsAsSubunits(AppBase.sgetSceneController().shouldTreatModelsAsSubunits());
					
					Status.progress(0, "Reading PDB file: " + dataset);
					
					structureTmp = loader.load(dataset);
				}
				
				else
					Status.output(Status.LEVEL_ERROR,
									"Could not open file: the file must have an extension of .xml, xml.gz, .pdb, .pdb.gz, .ent, or .ent.gz.");
		
				if (structureTmp == null)
				{
					Status.output(Status.LEVEL_ERROR, "Could not load: " + dataset);
					return null;
				}
				
				else
					System.out.println("Data set loaded: " + dataset);
		
				new StructureMap(structureTmp, AppBase.sgetAppModuleFactory().createSceneNode());
				structureTmp.getStructureMap().setConverter(loader.getIDConverter());
				structureTmp.getStructureMap().setNonproteinChainIds(loader.getNonProteinChainIds());
				finalizeNewStructure(loader, structureTmp);
								
				if (loader.getUnitCell() != null)
					structureTmp.getStructureMap().setUnitCell(loader.getUnitCell());
		
				structuresVec.add(structureTmp);			
			}
			
			catch (final MalformedURLException e)
			{
				Status.output(Status.LEVEL_ERROR, "Error: Bad url to the structure xml file.");
			}
			
			catch (final IOException e)
			{
				Status.output(Status.LEVEL_ERROR, e.getMessage());
			}

		}
			// global transforms set in _BU version...
		
		// else no structure loaded.
	
		final Structure[] structures = new Structure[structuresVec.size()];
		for (int i = 0; i < structures.length; i++)
		{
			structures[i] = structuresVec.get(i);
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
	public void finalizeNewStructure(IStructureLoader handler, Structure structureTmp)
	{
		StructureMap structureMap = structureTmp.getStructureMap();
		// explicitly create a structureMap for the struc

		if (handler.hasBiologicUnitTransformationMatrices())
		{
			StructureMap.BiologicUnitTransforms bu = structureMap.addBiologicUnitTransforms();
			bu.setBiologicalUnitGenerationMatrices(handler.getBiologicalUnitTransformationMatrices());
		}

		else if (handler.hasNonCrystallographicOperations())
		{
			StructureMap.NonCrystallographicTransforms nc = structureMap.addNonCrystallographicTransforms();
			nc.setNonCrystallographicTranslations(handler.getNonCrystallographicOperations());
		}
	}
}
