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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color4f;

import org.jcolorbrewer.ColorBrewer;
import org.jcolorbrewer.ui.ColorPaletteChooserDialog;
import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.controllers.update.LXUpdateEvent;
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


/**
 * Creates a panel to change transparency and color of surfaces.
 * 
 * @author Peter Rose
 *
 */
public class BindingSiteSurfacePanel extends JPanel implements IUpdateListener
{
	private static final long serialVersionUID = -7205000642717901355L;
	private static final Color DEFAULT_COLOR = new Color(0.1f, 0.8f, 1.0f);
	private static final int TRANSPARENCY_MIN = 0;
	private static final int TRANSPARENCY_MAX = 100;
	private static final int TRANSPARENCY_INIT = 0;

	private final JSlider transparencySlider = new JSlider(JSlider.HORIZONTAL,
			TRANSPARENCY_MIN, TRANSPARENCY_MAX, TRANSPARENCY_INIT);
	
	private final JLabel colorLabel = new JLabel("Color by");
	private final String[] surfaceOptions = {"Hydrophobicity", "Single color", "Chain", "Entity"};
	private final String[] surfaceRepresentations = {"Solid", "Mesh", "Dots"};
	private JComboBox surfaceColorType;
	private JComboBox surfaceRepType;
	
	private Color newColor = null;
	private ColorBrewer newPalette = null;
	private List<Integer> entitySet = null;
	
	JPanel firstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	public BindingSiteSurfacePanel() {
		super(false);
		setLayout(new BorderLayout());
		Border border = null;
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

		colorLabel.setBackground(LXDocumentFrame.sidebarColor);
		firstPanel.setBackground(LXDocumentFrame.sidebarColor);
		firstPanel.setVisible(false);
		firstPanel.add(colorLabel);
		add(firstPanel, BorderLayout.PAGE_END);
		setVisible(true);
		
		AppBase.sgetUpdateController().registerListener(this);
	}
	
	/* (non-Javadoc)
	 * @see edu.sdsc.mbt.views_controller.IViewUpdateListener#handleModelChangedEvent(edu.sdsc.mbt.views_controller.ViewUpdateEvent)
	 */
	public void handleUpdateEvent(UpdateEvent evt)
	{
		System.out.println("BindingSiteSurfacePanel event: " + evt);
		if (evt instanceof LXUpdateEvent) {
			System.out.println("BindingSiteSurfacePanel: LXUpdateEvent event");
			reset();		
		}
		if (evt.action == UpdateEvent.Action.VIEW_RESET) {
			System.out.println("BindingSiteSurfacePanel: VIEW_RESET event");
			reset();
		}
		if (evt.action == UpdateEvent.Action.STRUCTURE_REMOVED) {
		    System.out.println("BindingSiteSurfacePanel: VIEW_RESET event");
			reset();
		}
		if (evt.action == UpdateEvent.Action.STRUCTURE_ADDED) {
			reset();
		}
	}
	
	private void reset() {
		transparencySlider.setValue(TRANSPARENCY_INIT);
		removeComboBox();
		removeSurfaces();
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
	
	/**
	 * @param structure
	 * @return
	 */
	private List<Integer> createEntitySet(Structure structure) {
		List<Integer> entitySet = new ArrayList<Integer>();
		for (Surface s: structure.getStructureMap().getSurfaces()) {
			if (! entitySet.contains(s.getChain().getEntityId())) {
				entitySet.add(s.getChain().getEntityId());
			}
		}
		return entitySet;
	}
	
	private void addComboBox() {
		surfaceColorType = new JComboBox(surfaceOptions);
		surfaceColorType.addActionListener(new SurfaceTypeListener());
		surfaceRepType = new JComboBox(surfaceRepresentations);
		surfaceRepType.addActionListener(new SurfaceRepListener());
		firstPanel.add(surfaceColorType);
		firstPanel.add(surfaceRepType);
		firstPanel.setVisible(true);
	}
	
	private void removeComboBox() {
		if (surfaceColorType != null) {
		    firstPanel.remove(surfaceColorType);
		    surfaceColorType = null;    
		}
		if (surfaceRepType != null) {
		    firstPanel.remove(surfaceRepType);
		    surfaceRepType = null;    
		}
		firstPanel.setVisible(false);
	}
	
	private void updateSurfaceColor(int index, Color newColor, ColorBrewer newPalette) {		
		Structure structure = AppBase.sgetModel().getStructures().get(0);
		int surfaceCount = structure.getStructureMap().getSurfaceCount();
		if (surfaceCount == 0) {
			return;
		}
		
		// setup list of unique entity ids
		if (index == 3) {
			entitySet = createEntitySet(structure);
		}
		
		int sIndex = 0;
		for (Surface s: structure.getStructureMap().getSurfaces()) {
			if (index == 2) {
				// color by chain
				SurfaceColorUpdater.setPaletteColor(s, newPalette, surfaceCount, sIndex);
				sIndex++;
			} else if (index == 3) {
				// color by chain type (entity id)			
				int entityId = s.getChain().getEntityId();
				SurfaceColorUpdater.setPaletteColor(s, newPalette, entitySet.size(), entitySet.indexOf(entityId));
			} else if (index == 1) {
				// color by a single color
				Color4f color = new Color4f(newColor);
				SurfaceColorUpdater.setSurfaceColor(s, color);
			} else if (index == 0) {
				// color by hydrophobicity
				SurfaceColorUpdater.setHydrophobicSurfaceColor(s);
			}
		}
	}


	private class TransparencySliderListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();

			if (!source.getValueIsAdjusting() && AppBase.sgetModel().hasStructures()) {
				Structure structure = AppBase.sgetModel().getStructures().get(0);

				// lazy initialization of the surface
				boolean newSurface = false;
				if (structure.getStructureMap().getSurfaceCount() == 0) {
					Residue[] ligands = LigandExplorer.sgetSceneController().getLigandResidues();
					SurfaceThread thread = new SurfaceThread();	
					thread.createBindingSiteSurface(ligands, 0);
					newSurface = true;
				}

				float currentTransparency = 1.0f;
				float transparency = ((int)source.getValue()) * 1.0f/TRANSPARENCY_MAX;
				for (Surface s: structure.getStructureMap().getSurfaces()) {
					Color4f[] colors = s.getColors();
					if (colors != null && colors.length > 0) {
						currentTransparency = Math.max(currentTransparency, colors[0].w);
						SurfaceColorUpdater.setSurfaceTransparency(s, transparency);
					}
				}

				// if surface is currently visible and new transparency is <= 0.05
				// (off position of slider), turn the surface display off
				if (currentTransparency > 0.05f && transparency <= 0.05) {
					LigandExplorer.sgetGlGeometryViewer().surfaceRemoved(structure);
			    // if there is a new surface, or the visibility has changed from the off position
			    // to visible, then add the combo box (if not there yet) and show surface
				} else if (newSurface || currentTransparency <= 0.05f && transparency > 0.05f) {
					if (surfaceColorType == null) {
						addComboBox();
					}
					LigandExplorer.sgetGlGeometryViewer().surfaceAdded(structure);
					// reorient ligand so its visible
					LigandExplorer.sgetGlGeometryViewer().ligandViewWithSurface(structure);
			    // if surface is currently visible and the new transparency is in the
			    // visible range, remove and add surface to update transparency setting
				} else if (currentTransparency > 0.05f && transparency > 0.05f) {
					if (surfaceColorType == null) {
						addComboBox();
					}
					// remove current surface from display list and add new surface
					LigandExplorer.sgetGlGeometryViewer().surfaceRemoved(structure);
					LigandExplorer.sgetGlGeometryViewer().surfaceAdded(structure);
				} 
			}
		}
	}

	private class SurfaceTypeListener implements ActionListener {

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
			}

			updateSurfaceColor(source.getSelectedIndex(), newColor, newPalette);
			
			// remove current surface from display list and add new surface
			Structure structure = AppBase.sgetModel().getStructures().get(0);
			LigandExplorer.sgetGlGeometryViewer().surfaceRemoved(structure);
			LigandExplorer.sgetGlGeometryViewer().surfaceAdded(structure);
			
			
		}
	}
	
	private class SurfaceRepListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			final JComboBox source = (JComboBox)e.getSource();
			source.hidePopup();
			
			boolean mesh = false;
			boolean dot = false;
			if (source.getSelectedIndex() == 0) {
				System.out.println("Solid");
			} else if (source.getSelectedIndex() == 1) {
				System.out.println("Mesh");
			} else if (source.getSelectedIndex() == 2) {
				System.out.println("Dot");
			}
			
//			Structure structure = AppBase.sgetModel().getStructures().get(0);
//			for (Surface s: structure.getStructureMap().getSurfaces()) {
//				s.setDotSurface(dot);
//				s.setMeshSurface(mesh);
//			}
			// clear any existing surface
			removeSurfaces();

			Residue[] ligands = LigandExplorer.sgetSceneController().getLigandResidues();
			SurfaceThread thread = new SurfaceThread();	
			thread.createBindingSiteSurface(ligands, source.getSelectedIndex());
		    updateSurfaceColor(surfaceColorType.getSelectedIndex(), newColor, newPalette);
		    
		    // remove current surface from display list and add new surface
			Structure structure = AppBase.sgetModel().getStructures().get(0);
			LigandExplorer.sgetGlGeometryViewer().surfaceRemoved(structure);
			LigandExplorer.sgetGlGeometryViewer().surfaceAdded(structure);	
		}
	}
}