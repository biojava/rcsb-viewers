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