package org.rcsb.testbed.controllers;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rcsb.testbed.UI.GLDocumentFrameBase;
import org.rcsb.testbed.app.TestBed;
import org.rcsb.testbed.glscene.GLViewer;
import org.rcsb.testbed.glscene.IScene;


public class SceneController implements ChangeListener
{
	private GLViewer glViewer;
	private GLDocumentFrameBase activeFrame = null;
	private ArrayList<IScene> scenes = new ArrayList<IScene>();
	public ArrayList<IScene> getScenes() { return scenes; }
	public SceneController()
	{
		glViewer = new GLViewer();
	}
	
	public void initGL()
	{
		activeFrame = (GLDocumentFrameBase)TestBed.sgetActiveFrame();
		activeFrame.setGLViewer(glViewer);
		glViewer.initGL();
		glViewer.requestRedraw();
	}
	
	public void requestSceneRedraw()
	{
		glViewer.requestRedraw();
	}
	
	public void registerScene(IScene scene)
	{
		scenes.add(scene);
	}

	public void stateChanged(ChangeEvent evt)
	{
		TabbedFrameController frameController = (TabbedFrameController)evt.getSource();
		if (activeFrame != null)
			activeFrame.removeGLViewer(glViewer);
		
		activeFrame = (GLDocumentFrameBase)frameController.getActiveFrame();
		activeFrame.setGLViewer(glViewer);
	}	
}
