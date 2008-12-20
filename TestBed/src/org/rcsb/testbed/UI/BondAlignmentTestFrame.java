package org.rcsb.testbed.UI;

import java.awt.event.ActionEvent;

import javax.swing.JButton;

import org.rcsb.testbed.app.TestBed;
import org.rcsb.testbed.glscene.BondAlignmentTestScene;
import org.rcsb.testbed.glscene.IScene;


@SuppressWarnings("serial")
public class BondAlignmentTestFrame extends GLDocumentFrameBase
{
	
	BondAlignmentTestScene scene;

	public BondAlignmentTestFrame()
	{
		scene = new BondAlignmentTestScene();
		TestBed.sgetSceneController().registerScene(scene);		
		
		ctrlBar.addSeparator();
		JButton btn = new JButton("+");
		btn.addActionListener(this);
		btn.setEnabled(true);
		ctrlBar.add(btn);
		
		btn = new JButton("-");
		btn.addActionListener(this);
		btn.setEnabled(true);
		ctrlBar.add(btn);
		
		btn = new JButton("<->");
		btn.addActionListener(this);
		btn.setEnabled(true);
		btn.setToolTipText("Disconnect transform (original condition)");
		ctrlBar.add(btn);
		
		btn = new JButton(">-<");
		btn.addActionListener(this);
		btn.setEnabled(true);
		btn.setToolTipText("Connect transform (the fix)");
		ctrlBar.add(btn);
		
		btn = new JButton("#");
		btn.addActionListener(this);
		btn.setEnabled(true);
		btn.setToolTipText("Next case");
		ctrlBar.add(btn);
		
	}
	
	@Override
	public IScene getScene()
	{
		return scene;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		switch (e.getActionCommand().charAt(0))
		{
			case '+': scene.incRotAngle(); break;
			case '-': scene.decRotAngle(); break;
			case '<': scene.connectTransform(false); scene.reset(); break;
			case '>': scene.connectTransform(true); scene.reset(); break;
			case '#':
				scene.nextCase();			// will reset
				break;
				
			default: scene.reset(); break;
		}
		
		TestBed.sgetSceneController().requestSceneRedraw();
	}

}
