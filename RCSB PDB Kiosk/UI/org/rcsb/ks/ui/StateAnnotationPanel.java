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
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.rcsb.vf.controllers.scene.SceneState;

/**
 * Represents the right lower panel in the Kiosk Viewer.
 * This area displays the ligand being animated in the Kiosk Viewer.
 * 
 * @author Peter Rose
 *
 */
public class StateAnnotationPanel extends JPanel {
	private static final long serialVersionUID = 8133311298423034878L;
	private JTextArea ligandName = new JTextArea ();


	/**
	 * Sets up the StateAnnoationPanel
	 */
	public StateAnnotationPanel() {
		setLayout ( new BoxLayout ( this, BoxLayout.Y_AXIS ));
		setBackground(Color.black);
		
		ligandName.setFont( new Font ("Helvetica", Font.BOLD, 15));
		ligandName.setBackground (Color.black);
		ligandName.setForeground(Color.green);
		ligandName.setWrapStyleWord(true);
		ligandName.setLineWrap(true);
		
		add(ligandName);
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(400, 150);
	}

	/**
	 * Updated the ligand name and repaints the panel
	 * 
	 * @param state
	 */
	public void updateState(SceneState state) {
		String name = state.toString();
		// pad short names with blanks
		int diff = 25 - name.length();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < diff; i++) {
			sb.append(" ");
		}
		sb.append(name);
		ligandName.setText(sb.toString());
		repaint ();
	}
}
