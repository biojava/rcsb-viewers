package org.rcsb.vf.controllers.doc;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.SwingUtilities;

import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.geometry.ModelTransformationMatrix;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.doc.DocController;
import org.rcsb.uiApp.ui.dialogs.ImageFileManager;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.controllers.scene.SceneController;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;


public class VFDocController extends DocController
{
	/**
	 * Load the structure, reset the view, and request a repaint.
	 */
	public void loadStructure(final String url, final String pdbId)
	{
		super.loadStructure(url, pdbId);
		
		VFAppBase.sgetSceneController().resetView(true);
		VFAppBase.sgetGlGeometryViewer().requestRepaint();
	}
	
	/**
	 * Override - after reading the url, checks for global transforms in the properties and
	 * generates them if set.
	 */
	@Override
	public Structure[] readStructuresFromUrl(String structureUrlParam)
	{
		Structure[] structures = super.readStructuresFromUrl(structureUrlParam);
		
		if (structures != null)
		{
			String globalTranslationVectors = VFAppBase.getApp().properties.getProperty("global_translation_vectors");
			String globalRotationMatrices = VFAppBase.getApp().properties.getProperty("global_rotation_matrices");
			boolean error = false;
			if (globalTranslationVectors != null)
			{
				if (globalRotationMatrices == null)
					error = true;

				else
				{
					String[] transSplit = globalTranslationVectors.split("\\s*;\\s*");
					String[] rotSplit = globalRotationMatrices.split("\\s*;\\s*");

					if (transSplit != null && rotSplit != null
							&& transSplit.length == structures.length
							&& rotSplit.length == structures.length) {
						float[][] globalTranslationVectorsFl = new float[transSplit.length][3];
						for (int i = 0; i < transSplit.length; i++)
						{
							String[] transSplit2 = transSplit[i].split("\\s++");

							if (transSplit2 == null || transSplit2.length != 3)
								error = true;

							else
							{
								try
								{
									for (int j = 0; j < 3; j++)
										globalTranslationVectorsFl[i][j] = Float.parseFloat(transSplit2[j]);
								}

								catch (Exception e)
								{
									error = true;
								}
							}
						}

						float[][] globalRotationMatricesFl = new float[rotSplit.length][9];
						for (int i = 0; i < rotSplit.length; i++)
						{
							String[] rotSplit2 = rotSplit[i].split("\\s++");

							if (rotSplit2 == null || rotSplit2.length != 9)
								error = true;

							else
							{
								try 
								{
									for (int j = 0; j < 9; j++)
										globalRotationMatricesFl[i][j] = Float.parseFloat(rotSplit2[j]);
								}

								catch (Exception e)
								{
									error = true;
								}
							}
						}

						if (!error)
						{
							for (int i = 0; i < structures.length; i++)
							{
								Structure s = (Structure) structures[i];
								StructureMap sm = s.getStructureMap();
								if (sm.hasBiologicUnitTransforms())
								{
									StructureMap.BiologicUnitTransforms bu = sm.getBiologicUnitTransforms();
									bu.generateGlobalTransformationMatrixHACK();
									ModelTransformationMatrix t = bu.getFirstGlobalTransformationMatrixHACK();
									t.setTransformationMatrix(
											globalRotationMatricesFl[i][0],
											globalRotationMatricesFl[i][1],
											globalRotationMatricesFl[i][2],
											globalRotationMatricesFl[i][3],
											globalRotationMatricesFl[i][4],
											globalRotationMatricesFl[i][5],
											globalRotationMatricesFl[i][6],
											globalRotationMatricesFl[i][7],
											globalRotationMatricesFl[i][8],
											globalTranslationVectorsFl[i][0],
											globalTranslationVectorsFl[i][1],
											globalTranslationVectorsFl[i][2]);
								}
							}
						}
					}
				}
			} 

			else if (globalRotationMatrices != null)
				error = true;

			if (error)
				System.err.println("Error: if you choose to specify translation vectors and rotation matrices, you must specify both for each structure.");
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
					final GlGeometryViewer viewer = VFAppBase.sgetGlGeometryViewer();
					viewer.requestScreenShot(width, height);

					Thread t = new Thread() {

						@Override
						public void run() {
//							 check for the screenshot regularly until it appears or the
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
				final GlGeometryViewer viewer = VFAppBase.sgetGlGeometryViewer();

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
//			Runnable runnable = new Runnable() {
//				public void run() {
					final ImageSaverThread screenshotWaiter = new ImageSaverThread();
					// wait until the image is obtained or the timeout occurs
					screenshotWaiter.start();
//				}
//			};
//			
//			SwingUtilities.invokeLater(runnable);
		}
	///////////////////////////////////////////////////////////////////////////////////////////////
	//
	// End Image Save Implementation
	//
	///////////////////////////////////////////////////////////////////////////////////////////////

}
