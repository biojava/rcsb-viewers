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
