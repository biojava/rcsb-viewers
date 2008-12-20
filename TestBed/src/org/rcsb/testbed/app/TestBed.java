package org.rcsb.testbed.app;

import org.rcsb.testbed.UI.DocumentFrameBase;
import org.rcsb.testbed.UI.GLDocumentFrameBase;
import org.rcsb.testbed.UI.MainFrame;
import org.rcsb.testbed.controllers.SceneController;


/**
 * basic GL geometry tester
 * @author rickb
 *
 */
public class TestBed
{
	static private TestBed app;
	static public TestBed getApp() { return app; }
	
	private MainFrame mainFrame;
	
	static public MainFrame sgetMainFrame() { return app.mainFrame; }
	static public DocumentFrameBase sgetActiveFrame() { return sgetMainFrame().getActiveFrameController().getActiveFrame(); }
	static public SceneController sgetSceneController() { return sgetMainFrame().getSceneController(); }	

	static public void sputStatus(String msg) { sgetMainFrame().putStatus(msg); }
	
	private static int testNum = 0, caseNum = 0;
	public static int sgetTestNum() { return testNum; }
	public static int sgetCaseNum() { return caseNum; }
	
	private TestBed(String args[])
	{
		if (args.length > 0)
			testNum = Integer.parseInt(args[0]);
		
		if (args.length > 1)
			caseNum = Integer.parseInt(args[1]);
	}
	
	private void init()
	{
		mainFrame = new MainFrame("KIS Systems - Geometry Tester");
		mainFrame.setBounds(200, 100, 800, 800);
		mainFrame.init();
		mainFrame.setVisible(true);
		sgetSceneController().initGL();		
	}
	
	static public void main(String args[])
	{
		app = new TestBed(args);
		app.init();
	}
}
