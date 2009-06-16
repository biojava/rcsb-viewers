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
import java.awt.LayoutManager;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class DebugPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 747173995048569880L;
    private final JTextArea area = new JTextArea();
    public JScrollPane scroller = null;
    
    private PipedInputStream piErr;

    private PrintStream normalErrOut;
    
    public DebugPanel() throws IOException {
        super(new LayoutManager() {
            private final Dimension preferredSize = new Dimension(-1,-1);
            private final Dimension minimumSize = new Dimension(-1,-1);
            private Dimension lastSize = null;
            
            public void removeLayoutComponent(final Component comp) { }

            public void layoutContainer(final Container parent) {
                this.setSize(parent);
            }
            
            private void setSize(final Container parent) {
                // if the component has not been resized, this is not necessary...
                final Dimension newSize = parent.getSize();
                if(this.lastSize != null && this.lastSize.equals(newSize)) {
                    return;
                }
                this.lastSize = newSize;
                
                final DebugPanel parent_ = (DebugPanel)parent;
                
                int curY = 0;
                parent_.scroller.setBounds(0, curY, parent.getWidth(), parent.getHeight());
                curY += parent.getHeight();
                
                this.preferredSize.height = curY;
                this.preferredSize.width = parent.getWidth();
                this.minimumSize.height = curY;
                this.minimumSize.width = parent.getWidth();
            }

            public void addLayoutComponent(final String name, final Component comp) {}

            public Dimension minimumLayoutSize(final Container parent) {
                this.setSize(parent);
                
                return this.minimumSize;
            }

            public Dimension preferredLayoutSize(final Container parent) {
                this.setSize(parent);
                
                return this.preferredSize;
            }
            
        }, false);

        this.normalErrOut = System.out;
        
        // Set up System.err
        this.piErr = new PipedInputStream();
        
        // Add a scrolling text area
        this.area.setEditable(false);
        this.scroller = new JScrollPane(this.area);
        super.add(this.scroller);

        // Create reader threads
        new ReaderThread(this.piErr).start();
    }

    class ReaderThread extends Thread {
        PipedInputStream pi;

        ReaderThread(final PipedInputStream pi) {
            this.pi = pi;
        }

        
		public void run() {
            final byte[] buf = new byte[1024];
            try {
                while (true) {
                    final int len = this.pi.read(buf);
                    if (len == -1) {
                        break;
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            final String text = new String(buf, 0, len); 
                            DebugPanel.this.area.append(text);
                            DebugPanel.this.normalErrOut.print(text);

                            // Make sure the last line is always visible
                            DebugPanel.this.area.setCaretPosition(DebugPanel.this.area.getDocument().getLength());

                            // Keep the text area down to a certain character size
                            /*int idealSize = 1000;
                            int maxExcess = 500;
                            int excess = area.getDocument().getLength() - idealSize;
                            if (excess >= maxExcess) {
                                area.replaceRange("", 0, excess);
                            }*/
                        }
                    });
                }
            } catch (final IOException e) {
            }
        }
    }
    
    /*public DebugPanel() {
        super(null, false);
        this.area.setDoubleBuffered(false);
        
        
        
        this.scroller = new JScrollPane(this.area);
        this.scroller.setDoubleBuffered(false);
        
        super.add(this.scroller);
    }*/

    public void append(final String text) {
        this.area.append(text + "\n");
        System.out.println(text);
    }
    
    /*public void paintComponent(Graphics g) {
        
        this.scroller.setBounds(0,0,super.getWidth(),super.getHeight());
    }*/
}
