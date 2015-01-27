/*
 * BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence. This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 * http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors. These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 * http://www.biojava.org/
 *
 * This code was contributed from the Molecular Biology Toolkit
 * (MBT) project at the University of California San Diego.
 *
 * Please reference J.L. Moreland, A.Gramada, O.V. Buzko, Qing
 * Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (MBT):
 * A Modular Platform for Developing Molecular Visualization
 * Applications. BMC Bioinformatics, 6:21.
 *
 * The MBT project was funded as part of the National Institutes
 * of Health PPG grant number 1-P01-GM63208 and its National
 * Institute of General Medical Sciences (NIGMS) division. Ongoing
 * development for the MBT project is managed by the RCSB
 * Protein Data Bank(http://www.pdb.org) and supported by funds
 * from the National Science Foundation (NSF), the National
 * Institute of General Medical Sciences (NIGMS), the Office of
 * Science, Department of Energy (DOE), the National Library of
 * Medicine (NLM), the National Cancer Institute (NCI), the
 * National Center for Research Resources (NCRR), the National
 * Institute of Biomedical Imaging and Bioengineering (NIBIB),
 * the National Institute of Neurological Disorders and Stroke
 * (NINDS), and the National Institute of Diabetes and Digestive
 * and Kidney Diseases (NIDDK).
 *
 * Created on 2008/12/22
 *
 */ 
package org.rcsb.pw.ui;


import java.awt.Dimension;
import java.awt.Event;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.rcsb.uiApp.controllers.update.UpdateEvent;
import org.rcsb.vf.controllers.app.BBBrowserLauncher;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.pw.controllers.app.PWVersionInformation;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.PWSceneController;
import org.rcsb.pw.glscene.jogl.PWGlGeometryViewer;
import org.rcsb.pw.ui.mutatorPanels.ColorOptionsPanel;
import org.rcsb.pw.ui.mutatorPanels.LinesOptionsPanel;
import org.rcsb.pw.ui.mutatorPanels.MutatorBasePanel;
import org.rcsb.pw.ui.mutatorPanels.StylesOptionsPanel;
import org.rcsb.pw.ui.tree.TreeViewer;
import org.rcsb.vf.controllers.scene.SceneController;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;
import org.rcsb.vf.ui.VFDocumentFrameBase;
import org.rcsb.vf.ui.VFUIBuilder;


public class PWDocumentFrame extends VFDocumentFrameBase 
{
	private static final long serialVersionUID = -2377835483763485353L;
	private final String helpURL = "http://www.pdb.org/pdb/staticHelp.do?p=help/viewers/proteinWorkshop_viewer.html";

	@Override
	public PWGlGeometryViewer getGlGeometryViewer() { return (PWGlGeometryViewer)super.getGlGeometryViewer(); }
	@Override
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
	
		@Override
		public void componentResized(final ComponentEvent e)
		{
			final JFrame frame = (JFrame) e.getSource();
			this.parent.curSize = frame.getSize();
	
			this.parent.horizontalSplitPane
					.setDividerLocation(this.parent.curSize.width
							- this.parent.horizontalBarDistanceFromRight);
		}
	}
	
	class ProteinWorkshopUIBuilder extends VFUIBuilder
	{
		@Override
		public void run()
		{
			super.run();
						// defines the base level UI items
			
			
			if (!ProteinWorkshop.backgroundScreenshotOnly)
			{
				final JMenu viewMenu = new JMenu("View");
				final JMenuItem refreshItem = new JMenuItem("Refresh");
				final ActionListener refreshListener =
					new ActionListener()
					{
						public void actionPerformed(ActionEvent actionEvent)
						{
							ProteinWorkshop.sgetGlGeometryViewer().requestRepaint();
						}
					};
				
				refreshItem.addActionListener(refreshListener);
				viewMenu.add(refreshItem);
				menuBar.add(viewMenu);	
				
				final JMenu helpMenu = new JMenu("Help");
				final JMenuItem helpItem = new JMenuItem("Help");
				final ActionListener helpListener =
					new ActionListener()
					{
						public void actionPerformed(ActionEvent actionEvent)
						{
							Thread runner = new Thread()
							{
								@Override
								public void run()
								{
									String address = helpURL;
									try
									{
										BBBrowserLauncher.openURL(address);
									}
									
									catch (IOException e)
									{
										e.printStackTrace();
										displayErrorMessage("Unable to open help site.");
									}
								}
							};
							runner.start();
						}
					};
				
				helpItem.addActionListener(helpListener);
				helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
						Event.CTRL_MASK));
				helpMenu.add(helpItem);
				menuBar.add(helpMenu);		

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
					
					getUpdateController().fireUpdateViewEvent(UpdateEvent.Action.STRUCTURE_ADDED, structure); // -pr 20100530 this call has no effect, structure is displayed without it
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
			SceneController sceneController = VFAppBase.sgetSceneController();
			glViewer.addPickEventListener(sceneController);

			// Reset the view to look at the center of the data.
			sceneController.resetView(false);

			if (!ProteinWorkshop.backgroundScreenshotOnly)
			{
				StructureModel model = VFAppBase.sgetModel();
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

			JLabel sample = new JLabel("   2) Choose what you want the tool to affect.     ");
				
//			int sWidth = sample.getPreferredSize().width + 60;
			int sWidth = sample.getPreferredSize().width + 100; // this sets the width of the side bar
	//		System.out.println("PWDocumentFrame: " + sWidth);

			horizontalSplitPane.addAncestorListener(PWDocumentFrame.this.new HorizontalSplitPaneListener(PWDocumentFrame.this));

			horizontalSplitPane.setDividerLocation(PWDocumentFrame.this.horizontalSplitPane.getWidth() - sWidth);
			curSize = getSize();
			horizontalBarDistanceFromRight = (int) PWDocumentFrame.this.curSize.getWidth() - horizontalSplitPane.getDividerLocation();

			glViewer.setDoubleBuffered(false);
			horizontalSplitPane.setDoubleBuffered(false);
			//////////////////////////////////////////////////////////////////////
			// END define split pane
			//////////////////////////////////////////////////////////////////////

		}
	}
	
	public void displayErrorMessage(final String text) {
		Status.output(Status.LEVEL_ERROR, text);
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
    private ColorOptionsPanel colorOptionsPanel = null;
    private ColorPreviewerPanel colorPreviewerPanel = null;
    private LinesOptionsPanel linesPanel = null;
	private StylesOptionsPanel stylesOptionsPanel = null;

	public MutatorBasePanel getMutatorBasePanel() {
		return this.mutatorBasePanel;
	}


	public void setMutatorBasePanel(final MutatorBasePanel mutatorBasePanel) {
		this.mutatorBasePanel = mutatorBasePanel;
	}
	
	public ColorOptionsPanel getColorOptionsPanel() {
		return this.colorOptionsPanel;
	}

	public void setColorOptionsPanel(final ColorOptionsPanel colorOptionsPanel) {
		this.colorOptionsPanel = colorOptionsPanel;
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

	@Override
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
		super.setTitle("RCSB PDB Protein Workshop " + PWVersionInformation.version() +
					   " (powered by the MBT): " + title);
	}

}
