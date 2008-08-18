package org.rcsb.pw.ui;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.rcsb.mbt.model.Structure;
import org.rcsb.pw.ui.coloringDescriptions.backbone.BackboneColoringOptions;




public class ColoringOptions extends JPanel
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 505684529685316528L;
	private BackboneColoringOptions backbone = null;
    private AtomBondColoringOptions atomsAndBonds = null;
    private BackgroundColoringOptions background = null;
    //private MiscellaneousConvenienceOptionsPanel miscellaneous = null;
    
    
    public ColoringOptions()
    {
        super(null, false);
        super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        this.backbone = new BackboneColoringOptions();
        this.atomsAndBonds = new AtomBondColoringOptions();
        this.background = new BackgroundColoringOptions();
        //this.miscellaneous = new MiscellaneousConvenienceOptionsPanel(model);
        
        super.add(this.backbone);
        super.add(this.atomsAndBonds);
        super.add(this.background);
        //super.add(this.miscellaneous);
    }
}