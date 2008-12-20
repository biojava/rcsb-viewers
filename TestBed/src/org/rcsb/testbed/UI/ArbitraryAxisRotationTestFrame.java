package org.rcsb.testbed.UI;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JToggleButton;

import org.rcsb.testbed.app.TestBed;
import org.rcsb.testbed.glscene.ArbitraryAxisRotationTestScene;
import org.rcsb.testbed.glscene.IScene;



@SuppressWarnings("serial")
public class ArbitraryAxisRotationTestFrame extends GLDocumentFrameBase
{
	
	private ArbitraryAxisRotationTestScene scene;

	@Override
	public IScene getScene()
	{
		return scene;
	}
	
	public ArbitraryAxisRotationTestFrame()
	{
		scene = new ArbitraryAxisRotationTestScene();
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
		
		btn = new JButton(">GL");
		btn.setToolTipText("Using OpenGL Rotation (toggle for MBT)");
		btn.addActionListener(this);
		btn.setEnabled(true);
		ctrlBar.add(btn);
		
		btn = new JButton("#");
		btn.setToolTipText("Using OpenGL Rotation (toggle for MBT)");
		btn.addActionListener(this);
		btn.setEnabled(true);
		ctrlBar.add(btn);
}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		super.actionPerformed(e);
		switch (e.getActionCommand().charAt(0))
		{
			case '+': scene.incRotAngle(); break;
			case '-': scene.decRotAngle(); break;
			case '>':
				JButton btn = (JButton)e.getSource();
				boolean useGL = !btn.getText().endsWith("GL");
				if (useGL)
				{
					btn.setText(">GL");
					btn.setToolTipText("Using OpenGL Rotation (toggle for MBT)");
				}
				
				else
				{
					btn.setText(">MBT");
					btn.setToolTipText("Using MBT Rotation (toggle for GL)");
				}
				scene.setUseGLRot(useGL);		// will reset
				break;
				
			case '#':
				scene.nextRotVector();			// will reset
				break;
				
			default: scene.reset(); break;
		}
		
		TestBed.sgetSceneController().requestSceneRedraw();
	}
}
