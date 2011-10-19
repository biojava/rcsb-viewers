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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rcsb.mbt.model.Structure;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;
import org.rcsb.uiApp.ui.dialogs.ColorChooserDialog;
import org.rcsb.uiApp.ui.dialogs.ColorPaletteChooserDialog;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;



public class BackgroundColoringOptions extends JPanel implements IUpdateListener
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8276335779764505250L;
	JButton changeBackgroundColorButton = new JButton("Change the Background Color");
//    private JSlider fogStartSlider = null;
//    private final JLabel startSliderLabel = new JLabel("Change the Fog Distance:");
//    private int initialSliderValue;
//    private ChangeFogStartListener listener = null;
    
    public BackgroundColoringOptions()
    {
        super(null, false);
        super.setLayout(new FullWidthBoxLayout());
        
        super.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Background"),
                BorderFactory.createEmptyBorder(-1,1,1,1)));
        
//        this.fogStartSlider = new JSlider();
        
        this.changeBackgroundColorButton.addActionListener(new ChangeBackgroundColorListener());
//        this.listener = new ChangeFogStartListener(this.getFogRange());
//        this.fogStartSlider.addChangeListener(this.listener);
        
        this.reset();
        
        super.add(this.changeBackgroundColorButton);
//        super.add(this.startSliderLabel);
//        super.add(this.fogStartSlider);
        
        AppBase.sgetUpdateController().registerListener(this);
    }

    public void reset()
    {
    	VFAppBase.sgetSceneController().resetView(false);
    }
    
//    private int getFogRange() {
//    	double maxVal = 0;
//    	Structure[] structures = Model.getSingleton().getStructures();
//    	if(structures != null) {
//	    	for(int i = 0; i < structures.length; i++) {
//	    		JoglSceneNode scene = structures[i].getStructureMap().getSceneNode();
//	    		maxVal = Math.max(maxVal, (Math.max(scene.bigX, Math.max(scene.bigY, scene.bigZ)) * 1.07)); // 1.07 is an arbitrary value to expand the range by a bit.
//	    	}
//    	}
//    	
//    	return (int)maxVal;
//    }

	public void clearStructure() {
		
	}

	public void newStructureAdded(final Structure struc) {
		
	}

	/* (non-Javadoc)
	 * @see edu.sdsc.mbt.views_controller.IViewUpdateListener#handleModelChangedEvent(edu.sdsc.mbt.views_controller.ViewUpdateEvent)
	 */
	public void handleUpdateEvent(UpdateEvent evt)
	{
		if (evt.action == UpdateEvent.Action.VIEW_RESET)
			reset();
	}
}

class ChangeBackgroundColorListener implements ActionListener {

    public void actionPerformed(final ActionEvent e) {
        final GlGeometryViewer glViewer = VFAppBase.sgetGlGeometryViewer();
        
        final float[] colorFl = new float[] {0,0,0,0};
        glViewer.getBackgroundColor(colorFl);
        
        final ColorChooserDialog colorDialog = new ColorChooserDialog(AppBase.sgetActiveFrame());
        
        colorDialog.setColor(new Color(colorFl[0],colorFl[1],colorFl[2],colorFl[3]));
        colorDialog.show();
        if(colorDialog.wasOKPressed()) {
            colorDialog.getColor().getColorComponents(colorFl);
            
            glViewer.setBackgroundColor(colorFl[0], colorFl[1], colorFl[2], colorFl[3]);
//            Model.getSingleton().getViewer().requestFogColorChange(colorFl);
        }
        
        glViewer.requestRepaint();
    }
}

class ChangeFogStartListener implements ChangeListener {
	
	public ChangeFogStartListener(final int range) {
	}
	
	public void stateChanged(final ChangeEvent e) {
		final JSlider source = (JSlider)e.getSource();
		final float fogStartValue = source.getValue();
	}

	public void setRange(final int range) {
	}
}
