package org.rcsb.sv.ui;



import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.Structure;
import org.rcsb.sv.controllers.app.SVVersionInformation;
import org.rcsb.sv.controllers.app.SimpleViewer;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.ui.mainframe.UIBuilder;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;
import org.rcsb.vf.ui.VFDocumentFrameBase;



public class SVDocumentFrame extends VFDocumentFrameBase
{
	public class SimpleViewerUIBuilder extends UIBuilder
	{
		@Override
		public void run()
		{
			super.run();
							// create base level ui pieces
			
			GlGeometryViewer glViewer = getGlGeometryViewer();

			if (!AppBase.backgroundScreenshotOnly)
			{
				Container viewerFrameContainer = getContentPane();
				
				// Create a pane for the structure viewer and the
				// sequence viewer
				viewerFrameContainer.add(glViewer, BorderLayout.CENTER);

				validate();
				final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				
				if (screenSize.height < 768)
					setSize(800, 550);
				
				else
				{
					final int width = (int) (screenSize.getWidth() * 0.8);
					final int height = (int) (screenSize.getHeight() * 0.8);
					setBounds((int) (screenSize.getWidth() / 2 - width / 2),
							  (int) (screenSize.getHeight() / 2 - height / 2),
									width, height);
				}

				// Make sure we clean up if the user hits the close box
				final WindowAdapter closer = VFAppBase.getApp().new ViewerCloserListener(true);
				addWindowListener(closer);
			}

			//
			// progress update 0.85, "transferring structure"
			//

			// View the Structure
			StructureModel model = getModel();

			for (Structure structure : model.getStructures())
			{
				// get the pdb id from the structure's url.
				String pdbId = "";
				if (structure != null) {
					glViewer.structureAdded(structure);

					final String url = structure.getUrlString();
					String[] split = url.split("[/\\\\]");
					split = split[split.length - 1].split("\\.");
					pdbId = split[0];
					structure.getStructureMap().setPdbId(pdbId);
				}
			}

			addWindowListener(glViewer);
			// -----------------------------------------------------------------------------

			//
			// Enable picking
			//
			
			glViewer.addPickEventListener(getSceneController());

			// Reset the view to look at the center of the data.
			getSceneController().resetView(false);

			// Show the Structure's pdb id and the version in the title
			// bar.
			if (!AppBase.backgroundScreenshotOnly)
			{
				if (model.hasStructures())
					setTitle(model.getStructures().get(0).getStructureMap().getPdbId());
			}
			// progress.setProgress(0.90f);
			// progress.setNote("Displaying Main Frame...");

			if (_showFrame) 
				setVisible(true);

			// curSize = getSize();

			glViewer.setDoubleBuffered(false);
		}
	}
	
	final static long serialVersionUID = 0x43518477;
	
	public SVDocumentFrame(String title, URL iconUrl)
	{
		super(title, iconUrl);
						// let the super create the glviewer
				
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				// Check and put up performance warning dialog for large molecules,
				// if it hasn't been presented, already.
				//
				public void run()
				{					
					Preferences prefs = Preferences.userNodeForPackage(SimpleViewer.getApp().getClass());
					boolean performanceWarningShown =
						prefs.getBoolean(VFAppBase.PERFORMANCE_WARNING_KEY, false);
					
					if (!performanceWarningShown)
					{
						int option = JOptionPane.showOptionDialog(
										/*  progress, */ null,
										"Please be aware that the ability to manipulate large molecules is dependant on hardware configuration.\nThe MBT SimpleViewer documentation at www.pdb.org contains more information on this topic.",
										"Title", JOptionPane.YES_NO_OPTION,
										JOptionPane.WARNING_MESSAGE, null,
										new String[] { "Continue", "Exit" },
										"Continue");
						if (option != JOptionPane.OK_OPTION)
							System.exit(0);
						
						prefs.putBoolean(VFAppBase.PERFORMANCE_WARNING_KEY, true);
					}
				}
			});
		}
			
		catch (final InterruptedException e)
		{
			e.printStackTrace();
		}
		
		catch (final InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(boolean showFrame)
	{
		super.initialize(showFrame);
		
		try
		{
			SwingUtilities.invokeAndWait(new SimpleViewerUIBuilder());
		}

		catch (final InterruptedException e)
		{
			e.printStackTrace();
		}
		
		catch (final InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void setTitle(String title)
	{
		super.setTitle("PDB SimpleViewer " + SVVersionInformation.version() + " (powered by the MBT): " + title);
	}

}
