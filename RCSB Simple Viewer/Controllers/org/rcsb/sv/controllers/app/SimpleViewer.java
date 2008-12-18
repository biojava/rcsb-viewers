package org.rcsb.sv.controllers.app;

import java.net.URL;

import org.rcsb.sv.ui.SVDocumentFrame;
import org.rcsb.uiApp.ui.mainframe.DocumentFrameBase;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;
import org.rcsb.vf.ui.VFDocumentFrameBase;

/**
 * Viewer without a frame or any UI other than the menu.
 * 
 * @author rickb
 */
public class SimpleViewer extends VFAppBase
{
	{
	}
	
	public class SVAppModuleFactory extends VFAppBase.VFAppModuleFactory
	{

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createGlGeometryViewer()
		 */
		@Override
		public GlGeometryViewer createGlGeometryViewer() { return new GlGeometryViewer(); }

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createDocFrame()
		 */
		@Override
		public DocumentFrameBase createDocFrame(final String title, URL iconUrl)
		    { activeFrame =  new SVDocumentFrame(title, iconUrl); return activeFrame; }
		
	}
	
	/**
	 * Accessor overrides
	 * @return
	 */
	public static SimpleViewer getApp() { return (SimpleViewer)_theJApp; }
	@Override
	public SVDocumentFrame getActiveFrame() { return (SVDocumentFrame)super.getActiveFrame(); }
	public static SVDocumentFrame sgetActiveFrame() { return getApp().getActiveFrame(); }

	
	/**
	 * Constructor
	 * 
	 * @param args - the commandline args passed in to the main() call
	 */
	public SimpleViewer(final String[] args)
	{
		super(args);
	}

	public static void main(final String[] args)
	{
		final SimpleViewer app = new SimpleViewer(args);
		app.initialize(true, true);
	}

	public void initialize(final boolean isApplication, final boolean showFrame)
	{
		appModuleFactory = new SVAppModuleFactory();
		activeFrame = new SVDocumentFrame("PDB SimpleViewer (Powered by the MBT)",
										  SimpleViewer.class.getResource("images/icon_128_SV.png"));

		super.initialize(isApplication);
		
		activeFrame.initialize(true);
	
		final String structureUrlParam = this.properties.getProperty("structure_url");
	
		if (structureUrlParam != null)
			((VFDocumentFrameBase)activeFrame).loadURL(structureUrlParam);
	}
}
