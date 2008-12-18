package org.rcsb.pw.controllers.app;


import java.net.URL;

import org.rcsb.mbt.model.StructureModel;
import org.rcsb.pw.controllers.scene.PWSceneController;
import org.rcsb.pw.glscene.jogl.PWGlGeometryViewer;
import org.rcsb.pw.ui.PWDocumentFrame;
import org.rcsb.uiApp.ui.mainframe.DocumentFrameBase;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.controllers.scene.SceneController;
import org.rcsb.vf.controllers.scene.mutators.MutatorBase;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;
import org.rcsb.vf.ui.VFDocumentFrameBase;

/**
* ProteinWorkshopViewer.java
* <P>
* 
* @author John L. Moreland
* @author rickb
* @copyright SDSC
* @see
*/
public class ProteinWorkshop extends VFAppBase
{
	public class PWAppModuleFactory extends VFAppBase.VFAppModuleFactory
	{
		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JVFAppBase.AppModuleFactory#createSceneController()
		 */
		@Override
		public SceneController createSceneController() { return new PWSceneController(); }

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JVFAppBase.AppModuleFactory#createDocFrame(java.lang.String)
		 */
		@Override
		public DocumentFrameBase createDocFrame(String name, URL iconUrl) {return new PWDocumentFrame(name, iconUrl); }

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JVFAppBase.AppModuleFactory#createGlGeometryViewer()
		 */
		@Override
		public GlGeometryViewer createGlGeometryViewer() {return new PWGlGeometryViewer(); }
		
	}
	
	// Accessors
	public static ProteinWorkshop getApp() { return (ProteinWorkshop)_theJApp; }	
	@Override
	public PWDocumentFrame getActiveFrame() { return (PWDocumentFrame)activeFrame; }
	public static PWDocumentFrame sgetActiveFrame() { return getApp().getActiveFrame(); }
	public static PWSceneController sgetSceneController() { return (PWSceneController)VFAppBase.sgetSceneController(); }
	public static PWGlGeometryViewer sgetGlGeometryViewer() { return sgetActiveFrame().getGlGeometryViewer(); }

	public ProteinWorkshop(final String args[])
	{
		super(args);
	}
	
	/**
	 * Main entry point for ProteinWorkshopViewer unit testing.
	 * <P>
	 */
	public static void main(final String[] args)
	{
		final ProteinWorkshop app = new ProteinWorkshop(args);	
		app.initialize(true, true);
	}
	
	public static boolean backgroundScreenshotOnly = false;
	
	public void initialize(final boolean isApplication, final boolean showFrame)
	{
		appModuleFactory = new PWAppModuleFactory();
		activeFrame = appModuleFactory.createDocFrame("PDB SimpleViewer (Powered by the MBT)",
													  ProteinWorkshop.class.getResource("images/icon_128_PW.png"));
		
		super.initialize(isApplication);
	
		final String structureUrlParam = this.properties.getProperty("structure_url");
		
		activeFrame.initialize(true);

		final StructureModel model = sgetModel();
		
		MutatorBase.setActivationType(MutatorBase.ActivationType.ATOMS_AND_BONDS);

		if (structureUrlParam != null)
			((VFDocumentFrameBase)activeFrame).loadURL(structureUrlParam);
	}
}