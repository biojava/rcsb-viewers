package org.rcsb.pw.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.update.IUpdateListener;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.mbt.ui.dialogs.ColorChooserDialog;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.mutators.ColorMutator;
import org.rcsb.pw.controllers.scene.mutators.MutatorEnum;
import org.rcsb.pw.controllers.scene.mutators.options.ColorOptions;




// A panel for viewing the currently active color.
// Contains a hook into the StructureViewer so component colors can be sampled.
public class ColorPreviewerPanel extends JPanel implements IUpdateListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8523263275862028834L;

	public class ColorPane extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2991605107517741310L;
		public ColorPane() { 
			super(false);
			super.setPreferredSize(new Dimension(50,50));
			
			super.addMouseListener(new MouseAdapter() {
				
				
				@Override
				public void mouseClicked(final MouseEvent e) {
					final ColorPane source = (ColorPane)e.getSource();
					
					final ColorChooserDialog dialog = CommonDialogs.getColorDialog();
					dialog.setColor(source.getColor());
					dialog.show();
					if(dialog.wasOKPressed()) {
						source.setColor(dialog.getColor());
						//source.repaint();
					}
				}
				
				});
		}
		
		public ColorPane(final Color c) {
			this();
			
			this.setColor(c);
		}
		
		public void setColor(final Color c) {
			super.setBackground(c);
			
			final MutatorEnum mutatorModel = ProteinWorkshop.sgetSceneController().getMutatorEnum();
            
            final ColorMutator mutator = mutatorModel.getColorMutator();
            final ColorOptions options = mutator.getOptions();
            options.setCurrentColor(c);
		}
		
		public Color getColor() {
			return super.getBackground();
		}
	}
	
    private class SampleColorAction implements ActionListener {
        ColorPane colorPane = null;
        
        public SampleColorAction(final ColorPane pane) {
            this.colorPane = pane;
        }
        
        public void actionPerformed(final ActionEvent e) {
        	// cause the next mutation to instead change the color of this panel.
        	// note that every attempt is made to disable this functionaliy if the user implicity cancels it, such as by clicking something else.
        	AppBase.sgetSceneController().setColorSelectorSampleModeEnabled(true);
            
            Status.output(Status.LEVEL_REMARK, "Choose an atom, bond, or chain in the 3d viewer or tree to sample its color.");
        }
    }
	
    public ColorPane pane = null;
	public JButton sampleButton = null;
	
	public ColorPane getColorPane() {
		return this.pane;
	}
	
	public ColorPreviewerPanel() {
		super(null, false);
		super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.pane = new ColorPane();
		this.sampleButton = new JButton("Sample");
		
		this.sampleButton.addActionListener(new SampleColorAction(this.pane));
		
		this.pane.setToolTipText("Click here to change the active color.");
		this.sampleButton.setToolTipText("Sample a color from the 3d viewer or tree.");
		
		super.add(this.pane);
		super.add(this.sampleButton);
		ProteinWorkshop.sgetActiveFrame().setColorPreviewerPanel(this);
		
		this.reset();

		AppBase.sgetUpdateController().registerListener(this);		
	}

	public void reset() {
		this.pane.setColor(Color.white);
	}

	/* (non-Javadoc)
	 * @see edu.sdsc.mbt.views_controller.IViewUpdateListener#handleModelChangedEvent(edu.sdsc.mbt.views_controller.ViewUpdateEvent)
	 */
	public void handleModelChangedEvent(UpdateEvent evt)
	{
		if (evt.action == UpdateEvent.Action.VIEW_RESET)
			reset();
	}

}