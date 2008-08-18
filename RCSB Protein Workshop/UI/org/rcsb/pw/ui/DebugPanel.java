package org.rcsb.pw.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
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
	//JPanel innerPanel = new JPanel();   // otherwise, BorderLayout doesn't work with tabbed panes.
    private final JTextArea area = new JTextArea();
    public JScrollPane scroller = null;
    
    private PipedInputStream piErr;
    private PipedOutputStream poErr;

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
        this.poErr = new PipedOutputStream(this.piErr);
        
        // Add a scrolling text area
        this.area.setEditable(false);
        this.scroller = new JScrollPane(this.area);
        super.add(this.scroller);

        // Create reader threads
        new ReaderThread(this.piErr).start();
        
        //System.setErr(new PrintStream(poErr, true));
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
