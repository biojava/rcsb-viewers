package org.rcsb.pw.ui;

import java.io.IOException;

import javax.swing.JPanel;

public class Debug {
    private static DebugPanel panel = null;
    
    public static JPanel getPanel() {
        if(Debug.panel == null) {
            
            try {
                Debug.panel = new DebugPanel();
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        return Debug.panel;
    }
    
    public static void println(final String line) {
        Debug.panel.append(line + "\n");
    }
}
