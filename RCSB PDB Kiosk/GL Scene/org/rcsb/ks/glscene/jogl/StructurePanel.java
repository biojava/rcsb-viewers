package org.rcsb.ks.glscene.jogl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.rcsb.ks.controllers.app.KSState;
import org.rcsb.ks.controllers.app.StateAnnotationPanel;
import org.rcsb.ks.controllers.app.StructureIdPanel;
import org.rcsb.mbt.model.Structure;



public class StructurePanel extends JPanel {
	private StructureIdPanel structureIdPanel = new StructureIdPanel();
	private StateAnnotationPanel stateAnnotationPanel = new StateAnnotationPanel();
	private JPanel panel = null;
	
	/**
	 * This is the lower panel that contains two subpanels:
	 *   - structureIdPanel (displays the author and id of the structure)
	 *   - stateAnnotationPanel (displays information about the state.)
	 */
	public StructurePanel() {
		setLayout( new BoxLayout ( this, BoxLayout.X_AXIS) );
		panel = new JPanel(){
			public void setBounds ( int _x, int _y, int _w, int _h)
			{
				super.setBounds ( _x, _y, _w, _h );
				structureIdPanel.setBounds ( 2, 2, 650, _h-2 );
				stateAnnotationPanel.setBounds ( _w-300, 2, 298, _h-2 );
			}
		};
		panel.setBackground( Color.black );
		panel.setLayout(null);

		panel.add(structureIdPanel);
		panel.add(stateAnnotationPanel);
		add( panel);
		panel.setPreferredSize(new Dimension ( 880, 150));
	}
	
	public void setBounds ( int _x, int _y, int _w, int _h )
	{
		super.setBounds ( _x,  _y, _w, _h );
		panel.setBounds ( 0, 0, _w, _h );
	}
	
	
	public void updateState(Structure _structure, KSState _state) {
		if (structureIdPanel.getStructure() != _structure) {
			structureIdPanel.updateStructure(_structure);
		}
		stateAnnotationPanel.updateState(_state);
	}

	public void updateStructure(Structure _structure2) {
		if (structureIdPanel.getStructure() != _structure2) {
			structureIdPanel.updateStructure(_structure2);
		}
	}
}
