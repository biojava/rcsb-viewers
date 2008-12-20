package org.rcsb.testbed.UI;

import java.awt.event.ActionEvent;

import javax.swing.JButton;

import org.rcsb.testbed.app.TestBed;
import org.rcsb.testbed.glscene.BasicConeTestScene;
import org.rcsb.testbed.glscene.IScene;



@SuppressWarnings("serial")
public class BasicConeTestFrame extends GLDocumentFrameBase
{
	private BasicConeTestScene scene;
	
	public BasicConeTestFrame()
	{
		scene = new BasicConeTestScene();
		TestBed.sgetSceneController().registerScene(scene);
		
		ctrlBar.addSeparator();
		JButton btn = new JButton("<");
		btn.addActionListener(this);
		btn.setEnabled(true);
		ctrlBar.add(btn);
		
		btn = new JButton(">");
		btn.addActionListener(this);
		btn.setEnabled(true);
		ctrlBar.add(btn);
		
		btn = new JButton("^");
		btn.addActionListener(this);
		btn.setEnabled(true);
		ctrlBar.add(btn);
		
		btn = new JButton("v");
		btn.addActionListener(this);
		btn.setEnabled(true);
		ctrlBar.add(btn);
		
		btn = new JButton("(");
		btn.addActionListener(this);
		btn.setEnabled(true);
		ctrlBar.add(btn);
		
		btn = new JButton(")");
		btn.addActionListener(this);
		btn.setEnabled(true);
		ctrlBar.add(btn);		
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch (e.getActionCommand().charAt(0))
		{
			case '<': scene.incYRot(); break;
			case '^': scene.decXRot(); break;
			case '>': scene.decYRot(); break;
			case 'v': scene.incXRot(); break;
			case '(': scene.incZRot(); break;
			case ')': scene.decZRot(); break;
			default:
				scene.reset(); break;
		}
		
		TestBed.sgetSceneController().requestSceneRedraw();		
		
		TestBed.sputStatus("Rotated: " + scene.getXRot() + ", " + scene.getYRot() + ", " + scene.getZRot());
	}

	@Override
	public IScene getScene() { return scene; }
}
