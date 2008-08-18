package org.rcsb.pw.controllers.app;


import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.doc.DocController;
import org.rcsb.mbt.controllers.scene.SceneController;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.util.PickUtils;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.mbt.ui.mainframe.DocumentFrameBase;
import org.rcsb.pw.controllers.scene.PWSceneController;
import org.rcsb.pw.glscene.jogl.PWGlGeometryViewer;
import org.rcsb.pw.ui.PWDocumentFrame;
import org.rcsb.vf.controllers.app.VFAppBase;

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
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createSceneController()
		 */
		@Override
		public SceneController createSceneController() { return new PWSceneController(); }

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createDocFrame(java.lang.String)
		 */
		@Override
		public DocumentFrameBase createDocFrame(String name) {return new PWDocumentFrame(name); }

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createGlGeometryViewer()
		 */
		@Override
		public GlGeometryViewer createGlGeometryViewer() {return new PWGlGeometryViewer(); }
		
	}
	
	// Accessors
	public static ProteinWorkshop getApp() { return (ProteinWorkshop)_theJApp; }	
	@Override
	public PWDocumentFrame getActiveFrame() { return (PWDocumentFrame)activeFrame; }
	public static PWDocumentFrame sgetActiveFrame() { return getApp().getActiveFrame(); }
	public static PWSceneController sgetSceneController() { return (PWSceneController)AppBase.sgetSceneController(); }
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
		activeFrame = appModuleFactory.createDocFrame("PDB SimpleViewer (Powered by the MBT)");
		
		super.initialize(isApplication);
		
		PickUtils.setPickLevel(PickUtils.PICK_RESIDUES);
	
		final String structureUrlParam = this.properties.getProperty("structure_url");
		
		final StructureModel model = sgetModel();

		if (structureUrlParam != null)	
			model.setStructures(sgetDocController().readStructuresFromUrl(structureUrlParam));
		
		if (!model.hasStructures())
		{
			Status.output(Status.LEVEL_WARNING,
							"No structure loaded. Please load a structure from the File menu, above.");
			// System.exit( 1 );
		}
		
		activeFrame.initialize(true);		
	}
}