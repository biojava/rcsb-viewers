package org.rcsb.sv.controllers.app;

import org.rcsb.mbt.controllers.app.ProgressPanelController;
import org.rcsb.mbt.controllers.doc.DocController;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.rcsb.mbt.model.util.PickUtils;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.mbt.ui.mainframe.DocumentFrameBase;
import org.rcsb.sv.ui.SVDocumentFrame;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.glscene.jogl.VFGlGeometryViewer;



/**
 * 
 * @author rickb
 *
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
		public GlGeometryViewer createGlGeometryViewer() { return new VFGlGeometryViewer(); }

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createDocFrame()
		 */
		@Override
		public DocumentFrameBase createDocFrame(final String title)
		    { activeFrame =  new SVDocumentFrame(title); return activeFrame; }
		
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
	 * Temporary hack - tell the subsystem what the current app is.
	 * 
	 * @return
	 */
	@Override
	public boolean isSimpleViewer() { return true; }
	
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
		activeFrame = new SVDocumentFrame("PDB SimpleViewer (Powered by the MBT)");

		super.initialize(isApplication);
		
		PickUtils.setPickLevel(PickUtils.PICK_RESIDUES);
	
		final String structureUrlParam = this.properties.getProperty("structure_url");
	
		if (structureUrlParam != null)
		{
			ProgressPanelController.StartProgress();
			getActiveFrame().getModel().setStructures(getActiveFrame().getDocController().readStructuresFromUrl(structureUrlParam));
		}
		
		if (!getActiveFrame().getModel().hasStructures())
		{
			Status.output(Status.LEVEL_WARNING,
							"No structure loaded. Please load a structure from the File menu, above.");
			// System.exit( 1 );
		}
		
		activeFrame.initialize(true);
		ProgressPanelController.EndProgress();
	}
}
