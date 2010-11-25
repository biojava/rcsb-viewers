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
package org.rcsb.vf.glscene.jogl;

import java.awt.HeadlessException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class SplashFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8558438987863916475L;
	private JPanel contentPane = null;
	private final JLabel title = new JLabel("MBT ProteinWorkshop");;
	private final JLabel subTitle = new JLabel("-- A protein visualization tool developed by the Protein Data Bank (www.pdb.org)");
	private final JLabel subTitle2 = new JLabel("-- Created with the Molecular Biology Toolkit (mbt.sdsc.edu)");
	private JProgressBar progress = null;
	
	public SplashFrame() throws HeadlessException {
		super("MBT ProteinWorkshop");
		this.initialize();
	}

	public SplashFrame(final String title) throws HeadlessException {
		super(title);
		this.initialize();
	}

	private void initialize() {
		this.contentPane = (JPanel)super.getContentPane();
		this.contentPane.setLayout(new BoxLayout(this.contentPane, BoxLayout.Y_AXIS));
		
		super.setResizable(false);
		
		this.contentPane.add(this.title);
		this.contentPane.add(this.subTitle);
		this.contentPane.add(this.subTitle2);
		
		this.progress = new JProgressBar();
		this.progress.setIndeterminate(true);
		this.contentPane.add(this.progress);
		
		super.pack();
	}
	
	
}
