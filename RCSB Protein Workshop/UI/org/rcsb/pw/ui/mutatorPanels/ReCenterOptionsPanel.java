package org.rcsb.pw.ui.mutatorPanels;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.rcsb.pw.ui.FullWidthBoxLayout;


public class ReCenterOptionsPanel extends JPanel
{
	private static final long serialVersionUID = 2690970962493561450L;
	private final JLabel nothingLabel = new JLabel("This tool has no options.");
    
    public ReCenterOptionsPanel() {
        super(null, false);
        super.setLayout(new FullWidthBoxLayout());
        super.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("3)  Change the tool's options, if necessary."),
                BorderFactory.createEmptyBorder(-1,1,1,1)));
        
        super.add(this.nothingLabel);
    }
}