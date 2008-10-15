package org.rcsb.pw.ui;


import java.awt.Dimension;
import java.awt.Toolkit;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.app.ProgressPanelController;
import org.rcsb.mbt.controllers.scene.SceneController;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.pw.controllers.app.PWVersionInformation;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.PWSceneController;
import org.rcsb.pw.glscene.jogl.PWGlGeometryViewer;
import org.rcsb.pw.ui.mutatorPanels.LinesOptionsPanel;
import org.rcsb.pw.ui.mutatorPanels.MutatorBasePanel;
import org.rcsb.pw.ui.mutatorPanels.StylesOptionsPanel;
import org.rcsb.pw.ui.tree.TreeViewer;
import org.rcsb.vf.ui.VFDocumentFrameBase;


public class PWDocumentFrame extends VFDocumentFrameBase 
{
	private static final long serialVersionUID = -2377835483763485353L;
	public PWGlGeometryViewer getGlGeometryViewer() { return (PWGlGeometryViewer)super.getGlGeometryViewer(); }
	public PWSceneController getSceneController() { return (PWSceneController)super.getSceneController(); }
	
	class HorizontalSplitPaneListener implements AncestorListener
	{
		private PWDocumentFrame parent;
	
		public HorizontalSplitPaneListener(final PWDocumentFrame parent) 
		{
			this.parent = parent;
		}
	
		public void ancestorMoved(final AncestorEvent event)
		{
			this.parent.horizontalBarDistanceFromRight = this.parent.curSize.width
					- this.parent.horizontalSplitPane.getDividerLocation();
		}
	
		public void ancestorAdded(final AncestorEvent event) {}	
		public void ancestorRemoved(final AncestorEvent event) {}
	}
	
	class PictureMakerFrameListener extends ComponentAdapter
	{
		private PWDocumentFrame parent;
	
		public PictureMakerFrameListener(final PWDocumentFrame parent)
		{
			this.parent = parent;
		}
	
		public void componentResized(final ComponentEvent e)
		{
			final JFrame frame = (JFrame) e.getSource();
			this.parent.curSize = frame.getSize();
	
			this.parent.horizontalSplitPane
					.setDividerLocation(this.parent.curSize.width
							- this.parent.horizontalBarDistanceFromRight);
		}
	}
	
	class ProteinWorkshopUIBuilder extends VFDocumentFrameBase.UIBuilder
	{
		public void run()
		{
			super.run();
						// defines the base level UI items
			
			if (!ProteinWorkshop.backgroundScreenshotOnly)
			{
				//////////////////////////////////////////////////////////////////////
				// BEG define PW-Specific UI panels and components
				//////////////////////////////////////////////////////////////////////
				
				// Create a splitPane for the sidebar and the vertical
				// split
				// pane
				PWDocumentFrame.this.horizontalSplitPane = new JSplitPane(
						JSplitPane.HORIZONTAL_SPLIT, true);
				PWDocumentFrame.this.horizontalSplitPane.setOneTouchExpandable(true);
				getContentPane().add(PWDocumentFrame.this.horizontalSplitPane);

				Status.progress(-1, "Creating Sidebar...");
				
				// Create the sidebar
				PWDocumentFrame.this.sidebar = new Sidebar();
				PWDocumentFrame.this.horizontalSplitPane
						.setBottomComponent(PWDocumentFrame.this.sidebar);

				// Create a splitPane for the structure viewer and the
				// sequence viewer
				PWDocumentFrame.this.horizontalSplitPane.setTopComponent(getGlGeometryViewer());

				// Pack the frame
				// this.simpleViewerFrame.pack();
				PWDocumentFrame.this.validate();
				// this.simpleViewerFrame.setSize( 800, 600 );
				final Dimension screenSize = Toolkit
						.getDefaultToolkit().getScreenSize();
				if(screenSize.height < 768)
				{
					PWDocumentFrame.this.setSize( 800, 550 );
				}
				
				else
				{
					final int width = (int) (screenSize.getWidth() * 0.8);
					final int height = (int) (screenSize.getHeight() * 0.8);
					PWDocumentFrame.this
						.setBounds((int) (screenSize.getWidth() / 2 - width / 2),
								(int) (screenSize.getHeight() / 2 - height / 2),
								width, height);
				}

				PWDocumentFrame.this.horizontalSplitPane
						.setDividerLocation(PWDocumentFrame.this.horizontalSplitPane
								.getWidth()); // initially, there is
				// nothing for the sidebar
				// to show.

				// Make sure we clean up if the user hits the close box
				final WindowAdapter closer = ProteinWorkshop.getApp().new ViewerCloserListener(
						ProteinWorkshop.getApp().isApplication());
				PWDocumentFrame.this.addWindowListener(closer);
			}

			GlGeometryViewer glViewer = getGlGeometryViewer();
			
			Status.progress(-1, "Creating Scene...");
			for (Structure structure : getModel().getStructures())
			{
				// get the pdb id from the structure's url.
				String pdbId = "";
				if (structure != null)
				{
					final String url = structure.getUrlString();
					String[] split = url.split("[/\\\\]");
					split = split[split.length - 1].split("\\.");
					pdbId = split[0];
					structure.getStructureMap().setPdbId(pdbId);
					
					getUpdateController().fireUpdateViewEvent(UpdateEvent.Action.STRUCTURE_ADDED, structure);
									// notify listeners there's a new structure in town...
				}
			}
			
			//////////////////////////////////////////////////////////////////////
			// END define menu and file open dialog
			// BEG enable picking
			//////////////////////////////////////////////////////////////////////

			addWindowListener(glViewer);
			// -----------------------------------------------------------------------------

			//
			// Enable picking
			//
			SceneController sceneController = AppBase.sgetSceneController();
			glViewer.addPickEventListener(sceneController);

			// Reset the view to look at the center of the data.
			sceneController.resetView(false);

			if (!ProteinWorkshop.backgroundScreenshotOnly)
			{
				StructureModel model = AppBase.sgetModel();
				if (model.hasStructures())
					setTitle(model.getStructures().get(0).getStructureMap().getPdbId());
			}


			if (!ProteinWorkshop.backgroundScreenshotOnly)
				addComponentListener(PWDocumentFrame.this.new PictureMakerFrameListener(
								PWDocumentFrame.this));

			//////////////////////////////////////////////////////////////////////
			// BEG define split pane
			//////////////////////////////////////////////////////////////////////

			if (_showFrame)
				setVisible(true);

			horizontalSplitPane.addAncestorListener(PWDocumentFrame.this.new HorizontalSplitPaneListener(PWDocumentFrame.this));

			horizontalSplitPane.setDividerLocation(PWDocumentFrame.this.horizontalSplitPane.getWidth() - 375);
			curSize = getSize();
			horizontalBarDistanceFromRight = (int) PWDocumentFrame.this.curSize.getWidth() - horizontalSplitPane.getDividerLocation();

			glViewer.setDoubleBuffered(false);
			horizontalSplitPane.setDoubleBuffered(false);
			//////////////////////////////////////////////////////////////////////
			// END define split pane
			//////////////////////////////////////////////////////////////////////

		}
	}
	
	// Views
	public Sidebar sidebar = null;
	
	/**
	 * Saves the number of pixels that the horizontal bar is from the right side of the screen. For resize operations.
	 */
	public int horizontalBarDistanceFromRight = 0;
	
	/**
	 * The splitpane on the main frame's frame's contentPane.
	 */
	public JSplitPane horizontalSplitPane = null;

	/**
	 * Contains the structure tree representation.
	 */
	private TreeViewer treeView = null;
    public TreeViewer getTreeViewer() {
        return this.treeView;
    }
    
    public void setTreeViewer(final TreeViewer tree) {
        this.treeView = tree;
    }

    private MutatorBasePanel mutatorBasePanel = null;
    private ColorPreviewerPanel colorPreviewerPanel = null;
    private LinesOptionsPanel linesPanel = null;
	private StylesOptionsPanel stylesOptionsPanel = null;

	public MutatorBasePanel getMutatorBasePanel() {
		return this.mutatorBasePanel;
	}


	public void setMutatorBasePanel(final MutatorBasePanel mutatorBasePanel) {
		this.mutatorBasePanel = mutatorBasePanel;
	}
	
	public ColorPreviewerPanel getColorPreviewerPanel() {
		return this.colorPreviewerPanel;
	}

	public void setColorPreviewerPanel(final ColorPreviewerPanel colorPreviewerPanel) {
		this.colorPreviewerPanel = colorPreviewerPanel;
	}
	
	public LinesOptionsPanel getLinesPanel() {
		return this.linesPanel;
	}

	public void setLinesPanel(final LinesOptionsPanel linesPanel) {
		this.linesPanel = linesPanel;
	}
	
	public StylesOptionsPanel getStylesOptionsPanel() {
		return stylesOptionsPanel;
	}

	public void setStylesOptionsPanel(final StylesOptionsPanel stylesOptionsPanel_) {
		stylesOptionsPanel = stylesOptionsPanel_;
	}
	/**
	 * Constructor
	 * @param title - main window title
	 */
	public PWDocumentFrame(String title, URL iconUrl)
	{
		super(title, iconUrl);
	}

	public void initialize(boolean showFrame)
	{
		super.initialize(showFrame);
		
		try
		{
			SwingUtilities.invokeAndWait(
				new ProteinWorkshopUIBuilder());
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
		super.setTitle("PDB ProteinWorkshop " + PWVersionInformation.version() +
					   " (powered by the MBT): " + title);
	}

}
