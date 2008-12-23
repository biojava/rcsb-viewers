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
