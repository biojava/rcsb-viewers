package org.rcsb.testbed.UI;

import javax.swing.JPanel;

import org.rcsb.testbed.glscene.IScene;


@SuppressWarnings("serial")
public abstract class DocumentFrameBase extends JPanel
{
	public abstract IScene getScene();
}
