package org.rcsb.vf.ui;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.doc.LoadThread;
import org.rcsb.mbt.ui.mainframe.DocumentFrameBase;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.controllers.doc.VFDocController;
import org.rcsb.vf.controllers.scene.SceneController;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;

public abstract class VFDocumentFrameBase extends DocumentFrameBase
{
	private static final long serialVersionUID = 1761606139608488229L;

	@Override
	public VFDocController getDocController() { return (VFDocController)super.getDocController(); }

	
	private GlGeometryViewer _glGeometryViewer = null;
	public GlGeometryViewer getGlGeometryViewer()
	{
		if (_glGeometryViewer == null) _glGeometryViewer = VFAppBase.sgetAppModuleFactory().createGlGeometryViewer();
		return _glGeometryViewer;
	}

	/**
	 * The scene controller has all the machinery to manipulate the scene.
	 */
	private SceneController sceneController = null;
	public SceneController getSceneController()
	{
		if (sceneController == null) sceneController = VFAppBase.sgetAppModuleFactory().createSceneController();
		return sceneController;
	}
	
	public VFDocumentFrameBase(String title, URL iconURL)
	{
		super(title, iconURL);
		
		try
		{
			SwingUtilities.invokeAndWait(
				new Runnable()
				{
					public void run()
					{
						// set up the standard tool tips
						final ToolTipManager ttm = ToolTipManager.sharedInstance();
						ttm.setLightWeightPopupEnabled(false); // only heavy
						// components show
						// above the 3d
						// viewer.
						ttm.setDismissDelay(20000); // wait 20 seconds before the
						// tooltip disappears.
		
						JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
						try
						{
							UIManager.setLookAndFeel(UIManager
									.getSystemLookAndFeelClassName());
							// UIManager.setLookAndFeel("net.java.plaf.windows.WindowsLookAndFeel");
						}
						
						catch (final Exception e) {}
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
	
	public void loadURL(String url)
	{
		LoadThread loader = new LoadThread(url);
		loader.run();
	}
	
	@Override
	public void initialize(boolean showFrame)
	{
		super.initialize(showFrame);
		VFAppBase.sgetGlGeometryViewer();
		// force the geometryviewer creation
	}
}
