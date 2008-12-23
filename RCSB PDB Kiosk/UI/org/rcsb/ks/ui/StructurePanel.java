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
package org.rcsb.ks.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.rcsb.mbt.model.Structure;
import org.rcsb.vf.controllers.scene.SceneState;


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
