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
				"J.L. Moreland, A.Gramada, O.V. Buzko, Q. Zhang and P.E. Bourne 2005 The Molecular Biology Toolkit (mbt): A Modular Platform for Developing Molecular Visualization Applications. BMCBioinformatics, 6:21";
	
	public String html = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">" +
						 "<html>" +
							"<head>" +
								"<title> Title </title>" +
							"</head>" +
							"<body>" +
								"Online documentation: [<a href=\"http://www.pdb.org/robohelp/viewers/proteinworkshop.htm\">here</a>]<br>" +
								"Flash tutorial: [<a href=\"http://www.pdb.org/pdbstatic/tutorials/viewers/pw.swf\">here</a>]" +
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
