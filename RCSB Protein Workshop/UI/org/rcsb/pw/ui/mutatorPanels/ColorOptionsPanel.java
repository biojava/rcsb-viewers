package org.rcsb.pw.ui.mutatorPanels;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.rcsb.pw.ui.ColorPreviewerPanel;
import org.rcsb.pw.ui.FullWidthBoxLayout;


public class ColorOptionsPanel extends JPanel
{
	private static final long serialVersionUID = 4557607549738209715L;
	public JLabel activeColorLabel = null;
	public ColorPreviewerPanel activeColorPanel = null;
	    
    public ColorOptionsPanel() {
        super(null, false);
        super.setLayout(new FullWidthBoxLayout());
        super.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("3)  Change the tool's options, if necessary."),
                BorderFactory.createEmptyBorder(-1,1,1,1)));
        
        
        // create the interface objects...
        this.activeColorLabel = new JLabel("Active Color:");
        this.activeColorPanel = new ColorPreviewerPanel();
        
        super.add(this.activeColorLabel);
        super.add(this.activeColorPanel);
    }
}