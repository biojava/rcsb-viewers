package org.rcsb.mbt.controllers.app;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.rcsb.mbt.controllers.doc.DocController;
import org.rcsb.mbt.controllers.scene.SceneController;
import org.rcsb.mbt.controllers.update.UpdateController;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.rcsb.mbt.glscene.jogl.JoglSceneNode;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.structLoader.IStructureXMLHandler;
import org.rcsb.mbt.structLoader.StructureXMLHandler;
import org.rcsb.mbt.ui.mainframe.DocumentFrameBase;


public abstract class AppBase
{
	/**
	 * Derived app should instantiate this so that the app closes when the
	 * main window closes.
	 * @author rickb
	 *
	 */
	public class ViewerCloserListener extends WindowAdapter
	{
		private boolean isApplication;

		public ViewerCloserListener(final boolean isApplication)
		{
			this.isApplication = isApplication;
		}

		@Override
		public void windowClosing(final WindowEvent event)
		{
			if (this.isApplication)
			{
				System.exit(0); // make sure the application terminates
				// completely.
			}
		}
	}
	
	/**
	 * The application needs to be able to derive certain architectural components, but their
	 * creation and destruction needs to be maintained by the framework.  Thus, all creation
	 * requests for derivable classes route through this subclass.
	 * 
	 * You have to derive from it in your App class, and you have to override CreateDocFrame,
	 * since you will at least have a custom app class, and a custom document frame.
	 * 
	 * Derive from this class, override the factory methods to create a derived version, but
	 * return the base version, and create an instance of the derived class to the appModuleFactory member.
	 * 
	 * The rest of the framework should be good to go, provided you've done your derivations and
	 * overriding correctly.
	 * 
	 * You can add whatever you need, here (or in the derived version), and then provide an accessor or call
	 * the create method, directly, as you need.
	 * 
	 * Every derivable class is created here - keeps the implementations from getting scattered throughout
	 * the app.
	 * 
	 * Resource conservation tip (still investigating):
	 * ------------------------------------------------
	 * If you are creating an optional module, use reflection to create it, rather than calling the
	 * constructor, here - keeps the class/module from getting loaded until it's needed.  If it's
	 * required, just go ahead and call the constructor
	 * 
	 * @author rickb
	 *
	 */
	public abstract class AppModuleFactory
	{
		/**
		 * DocumentFrameBase <em>has</em> to be derived.  The rest *may* be derived
		 */
		public abstract DocumentFrameBase createDocFrame(final String name);
		public DocController createDocController() { return new DocController(); }
		public UpdateController createUpdateController() { return new UpdateController(); }
		public SceneController createSceneController() { return new SceneController(); }
		public StructureModel createModel() { return new StructureModel(); }
		public GlGeometryViewer createGlGeometryViewer() { return new GlGeometryViewer(); }
		public JoglSceneNode createSceneNode() { return new JoglSceneNode(); }
		public IStructureXMLHandler createStructureXMLHandler(String dataset) { return new StructureXMLHandler(dataset); }

	}

	/**
	 * Convenience access functions.
	 * The app can get to any of the controllers, the scene, or the model via these accessors (and anything can get
	 * the app instance.)
	 * 
	 * These are static accessors that get the relevant pieces through the active frame (in this case, the mainFrame.)
	 * 
	 * Override these to get derived versions (means you have to override JAppBase, of course.)
	 * 
	 * @see org.rcsb.mbt.ui.mainframe.DocumentFrameBase
	 * @see org.rcsb.mbt.model.StructureModel
	 * @see org.rcsb.mbt.appController.ViewsController
	 * @see org.rcsb.mbt.appController.UpdateViewController
	 * @see org.rcsb.mbt.appController.DocController
	 * @see org.rcsb.mbt.controllers.scene.appController.SceneController
	 */
	public static AppModuleFactory sgetAppModuleFactory() { return _theJApp.appModuleFactory; }
	public static DocumentFrameBase sgetActiveFrame() { return _theJApp.activeFrame; }
	public static UpdateController sgetUpdateController() { return sgetActiveFrame().getUpdateController(); }
	public static DocController sgetDocController() { return sgetActiveFrame().getDocController(); }
	public static SceneController sgetSceneController() { return sgetActiveFrame().getSceneController(); }
	public static GlGeometryViewer sgetGlGeometryViewer() { return sgetActiveFrame().getGlGeometryViewer(); }
	public static StructureModel sgetModel() { return sgetActiveFrame().getModel(); }
	public DocumentFrameBase getActiveFrame() { return activeFrame; }

	
	/**
	 * Temporary: Application indication hacks - app overrides these so the subsystems can
	 * modify behavior, accordingly.
	 * 
	 * Goal is to remove them.
	 * 
	 * @return - whether the app is the top-level app indicated by the call.
	 */
	public boolean isSimpleViewer() { return false; }
	public boolean isLigandExplorer () { return false; }
	
	/**
	 * The singleton app
	 */
	protected static AppBase _theJApp = null;
	public static AppBase getApp() { return _theJApp; }

    /**
     * The singleton document (main) frame.
     */
	protected DocumentFrameBase activeFrame = null;
	
	/**
	 * The singleton module factory
	 */
	protected AppModuleFactory appModuleFactory = null;

	protected boolean _isApplication = false;
	public boolean isApplication() { return _isApplication; }

	public static boolean backgroundScreenshotOnly = false;
	
	protected boolean _allowShaders = false;
	public boolean allowShaders() { return _allowShaders; }


	/**
	 * Default constructor - calls the super and then sets the app singleton.
	 */
	public AppBase()
	{
		super();
		_theJApp = this;
	}
}