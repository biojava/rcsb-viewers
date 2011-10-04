package org.rcsb.pw.ui.mutatorPanels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.controllers.scene.mutators.ColorMutator;
import org.rcsb.pw.controllers.scene.mutators.MutatorEnum;
import org.rcsb.pw.controllers.scene.mutators.options.ColorOptions;
import org.rcsb.pw.ui.CommonDialogs;
import org.rcsb.uiApp.ui.dialogs.ColorChooserDialog;

public class ColorPane extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 29916051075177413L;
	public ColorPane() { 
		super(false);
		super.setPreferredSize(new Dimension(13,13)); // default was 50 x 50
		
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
