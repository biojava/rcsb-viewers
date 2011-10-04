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
package org.rcsb.pw.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import org.rcsb.vf.controllers.app.BBBrowserLauncher;


public class CreditsPanel extends JPanel implements ActionListener, ClipboardOwner, HyperlinkListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4624398343284115661L;

	public String citation = 
				"J.L. Moreland, A.Gramada, O.V. Buzko, Q. Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (mbt): A Modular Platform for Developing Molecular Visualization Applications. BMC Bioinformatics, 6:21";
	
	public String html = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">" +
						 "<html>" +
							"<head>" +
								"<title> Title </title>" +
							"</head>" +
							"<body>" +
							    "<a href=\"http://www.pdb.org/pdb/staticHelp.do?p=help/viewers/proteinWorkshop_viewer.html\">Protein Workshop Tutorial</a><br>" +
//								"Online documentation: [<a href=\"http://www.pdb.org/robohelp/viewers/proteinworkshop.htm\">here</a>]<br>" +
//								"Flash tutorial: [<a href=\"http://www.pdb.org/pdb/tutorials/viewers/pw.swf\">here</a>]" +
								"<p>Scientific Lead: <u>Dr. Peter Rose, Dr. Phil Bourne</u></p>" +
								"<p>Original implementation: John Beaver, with contributions from Philipp Steeger, based on a specification provided by Phil Bourne and using the molecular biology toolkit (MBT).</p>" +
								"<p>Version 3 re-architect: Garrick Berger</p>" +
								"<p>Published figures using this tool should cite:</p>" +
								"<p>" +
									this.citation + " [<a href=\"http://www.biomedcentral.com/1471-2105/6/21\">paper here</a>]" +
						        "</p>" +
						        "<p>This work was supported in part by NIH grant GM63208</a></p>" +
						    "</body>" +
						  "</html>";
	
	public JEditorPane area = null;
	public JScrollPane scroller = null;
	public JButton copyCreditsButton = new JButton("Copy citation to clipboard");
	
	public CreditsPanel() {
		super();
		super.setLayout(new LayoutManager2() {

			public void addLayoutComponent(final String name, final Component comp) {}

			public void removeLayoutComponent(final Component comp) {}

			public Dimension preferredLayoutSize(final Container parent) {
				return parent.getSize();
			}

			public Dimension minimumLayoutSize(final Container parent) {
				return null;
			}

			public void layoutContainer(final Container parent) {
				final int visualBuffer = 3;
				
				final Dimension copySize = CreditsPanel.this.copyCreditsButton.getPreferredSize();
				
//				CreditsPanel.this.area.setPreferredSize(new Dimension(parent.getWidth() - 50, parent.getHeight()));
				
                CreditsPanel.this.scroller.setBounds(visualBuffer,visualBuffer,parent.getWidth() - visualBuffer * 2,parent.getHeight() - copySize.height - visualBuffer * 3);
				
				// put this at the bottom of the screen.
				CreditsPanel.this.copyCreditsButton.setBounds(parent.getWidth() / 2 - copySize.width / 2, parent.getHeight() - copySize.height - visualBuffer, copySize.width, copySize.height);
			}

			public void addLayoutComponent(final Component comp, final Object constraints) {}

			public Dimension maximumLayoutSize(final Container target) {
				// TODO Auto-generated method stub
//				return target.getSize();
				return null;
			}

			public float getLayoutAlignmentX(final Container target) {
				// TODO Auto-generated method stub
				return 0;
			}

			public float getLayoutAlignmentY(final Container target) {
				// TODO Auto-generated method stub
				return 0;
			}

			public void invalidateLayout(final Container target) {}
			
		});
		
//		JPanel encloser = new JPanel();
		
		this.area = new JEditorPane("text/html; charset=EN", this.html);
		
		this.scroller = new JScrollPane(this.area);
		
		this.area.addHyperlinkListener(this);
		
//		this.area.setText(this.creditsTop + this.citation + this.creditsBottom);
//		this.area.setFont(Font.getFont("SansSerif"));
		this.area.setEditable(false);
//		this.area.setWrapStyleWord(true);
//		this.area.setLineWrap(true);
		this.area.setBorder(new EmptyBorder(3,3,3,3));
		
		this.copyCreditsButton.addActionListener(this);
		
//		encloser.add(this.scroller);
		
		super.add(this.scroller);
		super.add(this.copyCreditsButton);
	}

	public void actionPerformed(final ActionEvent e) {
		if(e.getSource() == this.copyCreditsButton) {
			final StringSelection stringSelection = new StringSelection( this.citation );
		    final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( stringSelection, this );
		}
	}

	public void lostOwnership(final Clipboard clipboard, final Transferable contents) {}

	public void hyperlinkUpdate(final HyperlinkEvent e) {
		if(e.getEventType() == EventType.ACTIVATED) {
	    	// try opening the page in a real browser. But if that fails, just use the internal browser.
			try {
				BBBrowserLauncher.openURL(e.getURL().toExternalForm());
			} catch (final IOException e1) {
				e1.printStackTrace();
				
				try {
					this.area.setPage(e.getURL());
				} catch (final IOException e2) {
					e2.printStackTrace();
					
					// nothing else to try.
				}
			}
		}
	}

}
