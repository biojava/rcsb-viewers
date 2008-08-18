package org.rcsb.mbt.ui.mainframe;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.doc.DocController;
import org.rcsb.mbt.controllers.update.UpdateController;
import org.rcsb.mbt.glscene.controller.SceneController;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.ui.dialogs.ProgressPanel;


/**
 * We introduce the notion of an 'Document Frame'.  The Document Frame contains a complete
 * document representation, with views (3d view, UI panel) related to that document.
 * 
 * Note MDI functionality isn't currently supported - this is just a design implementation
 * with an eye towards facilitating that, should we desire.  Things like the menubar
 * and statusbar creation and maintaince would have to be broken out as makes sense for
 * that kind of architecture.
 * 
 * In the normal context, this would be the 'MainFrame'.  However, should we wish to support
 * multiple documents in multiple frames (i.e. MS 'MDI' mode, the Active Frame would be derived
 * from an MDI Frame equivalent.
 * 
 * @author rickb
 *
 */
public abstract class DocumentFrameBase extends JFrame
{
	/**
	 * This is a swing runnable - it is invoked to build the ActiveFrame UI at the appropriate
	 * moment.  Derived frames will derive from this, as well, and override 'run()'.
	 * 
	 * App-specific UI gets created in the overridden 'run()' function.
	 * 
	 * @author rickb
	 *
	 */
	protected class UIBuilder implements Runnable
	{
		/**
		 * Swing thread runner.  Override and implement app-specific ActiveFrame UI, here.
		 */
		public void run()
		{
			if (!AppBase.backgroundScreenshotOnly)
			{
				if (AppBase.getApp().isApplication())
				{
					//////////////////////////////////////////////////////////////////////
					setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				}
			}
		}
	}
	
	protected boolean _showFrame;
	
	/**
	 * The doc controller has all the machinery to load, save, and parse documents (files).
	 */
	private DocController docController = null;
	public DocController getDocController()
	{
		if (docController == null) docController = AppBase.sgetAppModuleFactory().createDocController();
		return docController;
	}
	
	/**
	 * The scene controller has all the machinery to manipulate the scene.
	 */
	private SceneController sceneController = null;
	public SceneController getSceneController()
	{
		if (sceneController == null) sceneController = AppBase.sgetAppModuleFactory().createSceneController();
		return sceneController;
	}
	
	/**
	 * This is the molecule model.  Contains all of the structures.
	 */
	private StructureModel model = null;
	public StructureModel getModel()
	{
		if (model == null) model = AppBase.sgetAppModuleFactory().createModel();
		return model;
	}
	
	private GlGeometryViewer _glGeometryViewer = null;
	public GlGeometryViewer getGlGeometryViewer()
	{
		if (_glGeometryViewer == null) _glGeometryViewer = AppBase.sgetAppModuleFactory().createGlGeometryViewer();
		return _glGeometryViewer;
	}

	private UpdateController _updateController = null;
	public UpdateController getUpdateController()
	{
		if (_updateController == null) _updateController = AppBase.sgetAppModuleFactory().createUpdateController();
		return _updateController;
	}

	
	/**
	 * Shared progress panel
	 */
	protected ProgressPanel progress = null;
	
	protected Dimension curSize;
	
	/**
	 * Constructor
	 * @throws HeadlessException
	 */
	public DocumentFrameBase() throws HeadlessException {
		super();
	}

	/**
	 * Constructor
	 * @throws HeadlessException
	 */
	public DocumentFrameBase(GraphicsConfiguration gc) {
		super(gc);
	}

	/**
	 * Constructor
	 * @throws HeadlessException
	 */
	public DocumentFrameBase(String title) throws HeadlessException {
		super(title);
	}

	/**
	 * Constructor
	 * @throws HeadlessException
	 */
	public DocumentFrameBase(String title, GraphicsConfiguration gc) {
		super(title, gc);
	}

	public void initialize(boolean showFrame)
	{
		_showFrame = showFrame;
		AppBase.sgetAppModuleFactory().createGlGeometryViewer();
	}
}