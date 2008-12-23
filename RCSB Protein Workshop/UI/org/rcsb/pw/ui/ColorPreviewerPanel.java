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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.rcsb.mbt.model.util.Status;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.mutators.ColorMutator;
import org.rcsb.pw.controllers.scene.mutators.MutatorEnum;
import org.rcsb.pw.controllers.scene.mutators.options.ColorOptions;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;
import org.rcsb.uiApp.ui.dialogs.ColorChooserDialog;
import org.rcsb.vf.controllers.app.VFAppBase;




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
        	VFAppBase.sgetSceneController().setColorSelectorSampleModeEnabled(true);
            
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
	public void handleUpdateEvent(UpdateEvent evt)
	{
		if (evt.action == UpdateEvent.Action.VIEW_RESET)
			reset();
	}

}
