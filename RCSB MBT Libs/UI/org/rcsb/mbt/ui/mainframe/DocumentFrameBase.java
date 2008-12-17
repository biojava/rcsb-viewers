package org.rcsb.mbt.ui.mainframe;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.JFrame;
import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.doc.DocController;
import org.rcsb.mbt.controllers.update.UpdateController;
import org.rcsb.mbt.model.StructureModel;


/**
 * <p>
 * We introduce the notion of an 'Document Frame'.  The Document Frame contains a complete
 * document representation, with views (3d view, UI panel) related to that document.</p>
 * <p>
 * Note MDI functionality isn't currently supported - this is just a design implementation
 * with an eye towards facilitating that, should we desire.  Things like the menubar
 * and statusbar creation and maintaince would have to be broken out as makes sense for
 * that kind of architecture.</p>
 * <p>
 * In the normal context, this would be the 'MainFrame'.  However, should we wish to support
 * multiple documents in multiple frames (i.e. MS 'MDI' mode, the Active Frame would be derived
 * from an MDI Frame equivalent.</p>
 * 
 * @author rickb
 *
 */
public abstract class DocumentFrameBase extends JFrame
{
	private static final long serialVersionUID = 682613576170299667L;	
	
	protected boolean _showFrame;
	public boolean showFrame() { return _showFrame; }
	
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
	 * This is the molecule model.  Contains all of the structures.
	 */
	private StructureModel model = null;
	public StructureModel getModel()
	{
		if (model == null) model = AppBase.sgetAppModuleFactory().createModel();
		return model;
	}

	private UpdateController _updateController = null;
	public UpdateController getUpdateController()
	{
		if (_updateController == null) _updateController = AppBase.sgetAppModuleFactory().createUpdateController();
		return _updateController;
	}
	
	protected Dimension curSize;
	
	/**
	 * Constructor
	 * @throws HeadlessException
	 */
	public DocumentFrameBase(String title, URL iconUrl) throws HeadlessException
	{
		super(title);
		if (iconUrl != null)
			this.setIconImage(Toolkit.getDefaultToolkit().createImage(iconUrl));
	}


	public void initialize(boolean showFrame)
	{
		_showFrame = showFrame;
	}
}