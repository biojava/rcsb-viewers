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
 * Created on 2011/11/08
 *
 */ 
package org.rcsb.lx.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color4f;

import org.jcolorbrewer.ColorBrewer;
import org.jcolorbrewer.ui.ColorPaletteChooserDialog;
import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.controllers.update.LXUpdateEvent;
import org.rcsb.lx.model.SurfaceState;
import org.rcsb.lx.model.LXModel;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.Surface;

import org.rcsb.mbt.model.attributes.SurfaceColorUpdater;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.doc.SurfaceThread;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;
import org.rcsb.uiApp.ui.dialogs.ColorChooserDialog;
import org.rcsb.vf.controllers.app.VFAppBase;


/**
 * Creates a panel to change transparency and color of surfaces.
 * 
 * @author Peter Rose
 *
 */
public class BindingSiteSurfacePanel extends JPanel implements IUpdateListener
{
	private static final long serialVersionUID = -7205000642717901355L;
	private SurfaceState state = null;
	
	private static final Color DEFAULT_COLOR = new Color(0.1f, 0.8f, 1.0f);
	private static final int TRANSPARENCY_MIN = 0;
	private static final int TRANSPARENCY_MAX = 100;
	private static final int TRANSPARENCY_INIT = 0;
	private static final float DISTANCE_THRESHOLD = 6.5f;
	private static final float MIN_DISTANCE_THRESHOLD = 3.0f;

	private final JSlider transparencySlider = new JSlider(JSlider.HORIZONTAL,
			TRANSPARENCY_MIN, TRANSPARENCY_MAX, TRANSPARENCY_INIT);
	
	private final String[] surfaceOptions = {"Hydrophobicity", "Single color", "Chain", "Entity"};
	private final String[] surfaceRepresentations = {"Solid", "Mesh", "Dots"};
	private JComboBox surfaceColorType;
	private JComboBox surfaceRepType;
	private SpinnerModel spinnerModel = new SpinnerNumberModel(DISTANCE_THRESHOLD, // initial value
			3.0f,
			1000.0f,
			0.5f);
	private JSpinner spinner = null;
	
	private Color newColor = null;
	private ColorBrewer newPalette = null;
	private JPanel firstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private JPanel secondPanel = new JPanel();

	public BindingSiteSurfacePanel() {
		super(false);
		setLayout(new BorderLayout());
		System.out.println("Creating new BindingSiteSurfacePanel");
		
		// create border around surface panel
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		border = BorderFactory.createTitledBorder(border, "Surfaces", 0, 0, null, Color.BLACK);
        setBorder(border);
		
		//Create ruler for slider
		Hashtable<Integer,JLabel> ruler = new Hashtable<Integer,JLabel>();
	    ruler.put(TRANSPARENCY_MIN, new JLabel("Off") );
		ruler.put(TRANSPARENCY_MAX/2, new JLabel("Transparent") );
		ruler.put(TRANSPARENCY_MAX, new JLabel("Opaque") );
		
		transparencySlider.setLabelTable(ruler);
		transparencySlider.setMajorTickSpacing(TRANSPARENCY_MAX/2);
		transparencySlider.setMinorTickSpacing(TRANSPARENCY_MAX/10);
		transparencySlider.setPaintTicks(true);
		transparencySlider.setPaintLabels(true);
		transparencySlider.setBackground(LXDocumentFrame.sidebarColor);
		
		add(transparencySlider, BorderLayout.PAGE_START);
		transparencySlider.addChangeListener(new TransparencySliderListener());   
		transparencySlider.setToolTipText("Change transparency of the surface");

		// add additional panels, they are hidden by default
		firstPanel.setBackground(LXDocumentFrame.sidebarColor);
		firstPanel.setVisible(false);
		add(firstPanel, BorderLayout.CENTER);
		
		secondPanel.setBackground(LXDocumentFrame.sidebarColor);
		secondPanel.setVisible(false);
		add(secondPanel, BorderLayout.PAGE_END);
		setVisible(true);
		
		AppBase.sgetUpdateController().registerListener(this);
	}
	
	/* (non-Javadoc)
	 * @see edu.sdsc.mbt.views_controller.IViewUpdateListener#handleModelChangedEvent(edu.sdsc.mbt.views_controller.ViewUpdateEvent)
	 */
	public void handleUpdateEvent(UpdateEvent evt)
	{			
		if (evt.action.equals(UpdateEvent.Action.EXTENDED)) {	
			// user has selected a new ligand, create surface for new ligand
			// using the current surface options from the "state" object
			Residue[] ligands = LigandExplorer.sgetSceneController().getLigandResidues();
			if (state != null && (state.getLigands() != ligands)) {
				if (ligands.length > 0) {
					Structure structure = AppBase.sgetModel().getStructures().get(0);
					state.setStructure(structure);
					state.setLigands(ligands);
					state.updateState();
					// remove the surface from the display list if transparency is below threshold
					if (state.getTransparency() <= 0.05) {
						LigandExplorer.sgetGlGeometryViewer().surfaceRemoved(structure);
					}
				}
			}
		}	
		else if (evt.action == UpdateEvent.Action.STRUCTURE_REMOVED) {
			// when a structure is removed, all surface information becomes invalid,
			// so set state to null.
			state = null;
		}
	}
	

	private void removeSurfaces() {
		if (AppBase.sgetModel().hasStructures()) {
			Structure structure = AppBase.sgetModel().getStructures().get(0);
			int n = structure.getStructureMap().getSurfaceCount();
			for (int i = n-1; i >= 0; i--) {
				Surface s= structure.getStructureMap().getSurface(i);
				structure.getStructureMap().removeSurface(s);
			}
		}
	}
	
	private void addOptions() {
		// create ui components
		surfaceColorType = new JComboBox(surfaceOptions);
		surfaceRepType = new JComboBox(surfaceRepresentations);
		spinner = new JSpinner(spinnerModel);
		spinner.setEditor(new JSpinner.NumberEditor(spinner, "####.#"));
		
		// add listeners
		surfaceColorType.addActionListener(new SurfaceTypeListener());
		surfaceRepType.addActionListener(new SurfaceRepListener());
		spinner.addChangeListener(new SpinnerChangeListener());

		// add to the first and second panel
		firstPanel.add(new JLabel("Color by"));
		firstPanel.add(surfaceColorType);
		firstPanel.setVisible(true);
		
		secondPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		secondPanel.add(new JLabel("Distance"));
		secondPanel.add(spinner);
		secondPanel.add(new JLabel("Type"));
		secondPanel.add(surfaceRepType);
		secondPanel.setVisible(true);
	}
	
	private void removeOptions() {
		firstPanel.removeAll();
		secondPanel.removeAll();
		if (surfaceColorType != null) {
		    surfaceColorType = null;    
		}
		if (surfaceRepType != null) {
		    surfaceRepType = null;    
		}
		if (spinner != null) {
		    spinner = null;    
		}
		firstPanel.setVisible(false);
		secondPanel.setVisible(false);
	}
	
	private void redrawSurfaces() {
		Structure structure = AppBase.sgetModel().getStructures().get(0);   
		LigandExplorer.sgetGlGeometryViewer().surfaceRemoved(structure);
		LigandExplorer.sgetGlGeometryViewer().surfaceAdded(structure);
	}	
	
	private class TransparencySliderListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();
				
			if (!source.getValueIsAdjusting() && AppBase.sgetModel().hasStructures()) {
				Structure structure = AppBase.sgetModel().getStructures().get(0);

				// lazy initialization of the surface
				boolean newSurface = false;
				if (structure.getStructureMap().getSurfaceCount() == 0) {
					state = new SurfaceState(structure);
					state.setLigands(LigandExplorer.sgetSceneController().getLigandResidues());
					newSurface = true;
				}

				float currentTransparency = state.getTransparency();			
				float transparency = ((int)source.getValue()) * 1.0f/TRANSPARENCY_MAX;
				state.setTransparency(transparency);
				state.updateState();

				// if surface is currently visible and new transparency is <= 0.05
				// (off position of slider), remove the surface from the display list
				if (currentTransparency > 0.05f && transparency <= 0.05) {
					LigandExplorer.sgetGlGeometryViewer().surfaceRemoved(structure);
					
			    // if there is a new surface, or the visibility has changed from the off position
			    // to visible, then add the combo box (if not there yet) and surface to the display list
				} else if (newSurface || currentTransparency <= 0.05f && transparency > 0.05f) {
					if (surfaceColorType == null) {
						addOptions();
					}
					LigandExplorer.sgetGlGeometryViewer().surfaceAdded(structure);
					// reorient ligand so its visible
					LigandExplorer.sgetGlGeometryViewer().ligandViewWithSurface(structure);
					
			    // if surface is currently visible and the new transparency is in the
			    // visible range, remove and add surface to update transparency setting
				} else if (currentTransparency > 0.05f && transparency > 0.05f) {
					if (surfaceColorType == null) {
						addOptions();
					}
					redrawSurfaces();
				} 
			}
		}
	}

	private class SurfaceTypeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final JComboBox source = (JComboBox)e.getSource();
			source.hidePopup();
			
			if (source.getSelectedIndex() == 2 || source.getSelectedIndex() == 3) {
				ColorPaletteChooserDialog paletteChooser = new ColorPaletteChooserDialog(AppBase.sgetActiveFrame());
				paletteChooser.showDialog();
				if (paletteChooser.wasOKPressed()) {
					newPalette = paletteChooser.getColorPalette();
				}
				if (newPalette == null) {
					return;
				}
				state.setColorBrewer(newPalette);
			} else if (source.getSelectedIndex() == 1) {
				ColorChooserDialog colorDialog = new ColorChooserDialog(AppBase.sgetActiveFrame());
				colorDialog.setColor(DEFAULT_COLOR);
				colorDialog.showDialog();
				if(colorDialog.wasOKPressed()) {
					newColor = colorDialog.getColor();
				}       
				if (newColor == null) {
					return;
				}
				state.setColor(newColor);
			}

			state.setColorType(source.getSelectedIndex());	
			state.updateState();
			
			redrawSurfaces();
		}
	}
	
	private class SurfaceRepListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final JComboBox source = (JComboBox)e.getSource();
			source.hidePopup();	
			
			if (source.getSelectedIndex() == 0) {
				System.out.println("Solid");
			} else if (source.getSelectedIndex() == 1) {
				System.out.println("Mesh");
			} else if (source.getSelectedIndex() == 2) {
				System.out.println("Dot");
			}
			
			state.setSurfaceType(source.getSelectedIndex());
			state.updateState();

			redrawSurfaces();
		}
	}
	
	private class SpinnerChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			SpinnerModel model = spinner.getModel();
	        double distanceThreshold = (Double)model.getValue();
	        
			state.setDistance((float)distanceThreshold);
			state.updateState();
			
			redrawSurfaces();	
		}	
	}
}