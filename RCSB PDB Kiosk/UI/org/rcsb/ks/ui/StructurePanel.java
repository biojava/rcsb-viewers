package org.rcsb.ks.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.rcsb.mbt.controllers.scene.SceneState;
import org.rcsb.mbt.model.Structure;


@SuppressWarnings("serial")
public class StructurePanel extends Box
{
	private StructureIdPanel structureIdPanel = new StructureIdPanel();
	private StateAnnotationPanel stateAnnotationPanel = new StateAnnotationPanel();
	private JPanel panel = null;
	
	/**
	 * This is the lower panel that contains two subpanels:
	 *   - structureIdPanel (displays the author and id of the structure)
	 *   - stateAnnotationPanel (displays information about the state.)
	 */
	public StructurePanel()
	{
		super(BoxLayout.X_AXIS);

		
		panel = new JPanel(){
			@Override
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
		add(createHorizontalStrut(10));
		add( panel);
		add(createHorizontalStrut(10));
		
		panel.setPreferredSize(new Dimension ( 880, 150));
	}
	
	@Override
	public void setBounds ( int _x, int _y, int _w, int _h )
	{
		super.setBounds ( _x,  _y, _w, _h );
		panel.setBounds ( 0, 0, _w, _h );
	}
	
	
	public void updateState(Structure _structure, SceneState _state) {
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
