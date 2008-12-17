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
