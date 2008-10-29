package org.rcsb.ks.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import org.rcsb.mbt.controllers.scene.SceneState;

/*
 * This is the panel on the right.
 */
@SuppressWarnings("serial")
public class StateAnnotationPanel extends JPanel {

	private SceneState state = null;

	public StateAnnotationPanel()
	{
	}

	public Dimension getPreferredSize() {
		return new Dimension(250, 150);
	}

	public void paintComponent(Graphics _graphics)
	{
		Dimension size = getSize();
		_graphics.setColor(Color.black);
		_graphics.fill3DRect(0, 0, size.width, size.height, true);

		if (state != null) {

			String stateName = state.toString();
			_graphics.setColor(Color.white);
			_graphics.drawString(stateName, 20, 20);
		}
	}

	public void updateState(SceneState _state) {
		state = _state;
		repaint ();
	}

}
