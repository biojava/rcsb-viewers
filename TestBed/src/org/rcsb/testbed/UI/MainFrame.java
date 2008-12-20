package org.rcsb.testbed.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.rcsb.mbt.model.util.StatusEvent;
import org.rcsb.testbed.app.TestBed;
import org.rcsb.testbed.controllers.IActiveFrameController;
import org.rcsb.testbed.controllers.SceneController;
import org.rcsb.testbed.controllers.TabbedFrameController;
import org.rcsb.uiApp.ui.mainframe.StatusPanel;


@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener
{
	private TabbedFrameController activeFrameController;
	public IActiveFrameController getActiveFrameController() { return activeFrameController; }
	
	private SceneController sceneController;
	public SceneController getSceneController() { return sceneController; }
	
	private JMenu fileMenu;
	private StatusPanel statusPanel;
	
	private JMenuItem fileExitMenuItem;
	
	public MainFrame(String title)
	{
		super(title);
	}
	
	public void init()
	{
		try {
			SwingUtilities.invokeAndWait(
				new Runnable()
				{
					public void run()
					{
						setBackground(Color.black);
						setJMenuBar(new JMenuBar());
						JMenuBar menuBar = getJMenuBar();
						
						fileMenu = new JMenu("File");
						fileExitMenuItem = new JMenuItem("Exit");
						fileExitMenuItem.addActionListener(MainFrame.this); 
						
						fileMenu.add(fileExitMenuItem);
						
						menuBar.add(fileMenu);
						
						sceneController = new SceneController();
						
						activeFrameController = new TabbedFrameController();
						
						activeFrameController.add("Basic Cone", new BasicConeTestFrame());
						activeFrameController.add("Arbitrary Axis Rotation", new ArbitraryAxisRotationTestFrame());
						activeFrameController.add("Bond Alignment", new BondAlignmentTestFrame());

						activeFrameController.addChangeListener(sceneController);
						
						getContentPane().add(activeFrameController);
						
						statusPanel = new StatusPanel();
						add(BorderLayout.SOUTH, statusPanel);
						
						setDefaultCloseOperation(DISPOSE_ON_CLOSE);
						activeFrameController.setSelectedIndex(TestBed.sgetTestNum());
					}
				});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void putStatus(String message)
	{
		StatusEvent status = new StatusEvent();
		status.message = message;
		statusPanel.processStatusEvent(status);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == fileExitMenuItem)
			System.exit(0);
	}
}
