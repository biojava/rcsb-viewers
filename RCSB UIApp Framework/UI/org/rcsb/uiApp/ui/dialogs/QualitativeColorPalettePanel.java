package org.rcsb.uiApp.ui.dialogs;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;

import org.rcsb.mbt.model.attributes.ColorBrewer;

public class QualitativeColorPalettePanel extends AbstractColorChooserPanel
                               implements ActionListener {
	private static final long serialVersionUID = 1L;

	public void updateChooser() {}
	
	protected JToggleButton createPalette(ColorBrewer brewer, Border normalBorder) {
		JToggleButton palette = new JToggleButton();
		palette.setActionCommand(brewer.name());
		palette.addActionListener(this);
		Icon icon = new PaletteIcon(brewer, 5, 15, 15);
		palette.setIcon(icon);
		palette.setToolTipText(brewer.getPaletteDescription());
		palette.setBorder(normalBorder);
		return palette;
	}

	protected void buildChooser() {
		setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

		ButtonGroup boxOfPalettes = new ButtonGroup();
		Border border = BorderFactory.createEmptyBorder(2,2,2,2);

		for (ColorBrewer palette: ColorBrewer.getQualitativeColorPalettes()) {
			JToggleButton button = createPalette(palette, border);
			boxOfPalettes.add(button);
			add(button);
		}
	}

	public void actionPerformed(ActionEvent e) {
		ColorSelectionModel model = getColorSelectionModel();

		String command = ((JToggleButton)e.getSource()).getActionCommand();
		for (ColorBrewer palette: ColorBrewer.getQualitativeColorPalettes()) {
			if (palette.name().equals(command)) {
				((ColorPanelSelectionModel) model).setColorBrewer(palette);
				break;
			}
		}
	}

	public String getDisplayName() {return "Qualitative";}
	
	public void stateChanged(ChangeEvent ce) {
		getColorSelectionModel().setSelectedColor(new Color(1));
	}

	@Override
	public Icon getLargeDisplayIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Icon getSmallDisplayIcon() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
