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
package org.rcsb.pw.ui.mutatorPanels;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color4f;

import org.jcolorbrewer.ColorBrewer;
import org.jcolorbrewer.ui.ColorPaletteChooserDialog;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.Surface;

import org.rcsb.mbt.model.attributes.SurfaceColorUpdater;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
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
public class SurfacePanel extends JPanel implements IUpdateListener
{
	private static final long serialVersionUID = -7205000642717901355L;
	private static final Color DEFAULT_COLOR = new Color(0.1f, 0.8f, 1.0f);
	private static final int TRANSPARENCY_MIN = 0;
	private static final int TRANSPARENCY_MAX = 100;
	private static final int TRANSPARENCY_INIT = 0;

	private final JSlider transparencySlider = new JSlider(JSlider.HORIZONTAL,
			TRANSPARENCY_MIN, TRANSPARENCY_MAX, TRANSPARENCY_INIT);
	
	private final JLabel colorLabel = new JLabel("Color by");
	private final String[] surfaceOptions = {"Chain", "Entity", "Single color", "Hydrophobicity"};
	private JComboBox surfaceColorType;
	
	JPanel firstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	public SurfacePanel() {
		super(false);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Surfaces"),
				BorderFactory.createEmptyBorder(0,0,0,0)));

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
		
		add(transparencySlider, BorderLayout.PAGE_START);
		transparencySlider.addChangeListener(new TransparencySliderListener());   
		transparencySlider.setToolTipText("Change transparency of the surface");

		firstPanel.setVisible(false);
		firstPanel.add(colorLabel);
		add(firstPanel, BorderLayout.PAGE_END);
		
		AppBase.sgetUpdateController().registerListener(this);
	}
	
	/* (non-Javadoc)
	 * @see edu.sdsc.mbt.views_controller.IViewUpdateListener#handleModelChangedEvent(edu.sdsc.mbt.views_controller.ViewUpdateEvent)
	 */
	public void handleUpdateEvent(UpdateEvent evt)
	{
		if (evt.action == UpdateEvent.Action.VIEW_RESET)
			reset();
		if (evt.action == UpdateEvent.Action.STRUCTURE_REMOVED)
			reset();
		if (evt.action == UpdateEvent.Action.STRUCTURE_ADDED)
			reset();
	}
	
	private void reset() {
		transparencySlider.setValue(TRANSPARENCY_INIT);
		removeComboBox();
	}
	
	private void addComboBox() {
		surfaceColorType = new JComboBox(surfaceOptions);
		surfaceColorType.addActionListener(new SurfaceTypeListener());
		firstPanel.add(surfaceColorType);
		firstPanel.setVisible(true);
	}
	
	private void removeComboBox() {
		if (surfaceColorType != null) {
		    firstPanel.remove(surfaceColorType);
		    surfaceColorType = null;    
		}
		firstPanel.setVisible(false);
	}

	private class TransparencySliderListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();

			if (!source.getValueIsAdjusting() && AppBase.sgetModel().hasStructures()) {
				Structure structure = AppBase.sgetModel().getStructures().get(0);

				// lazy initialization of the surface
				boolean newSurface = false;
				if (structure.getStructureMap().getSurfaceCount() == 0) {
					SurfaceThread thread = new SurfaceThread();	
					// can't run this as a thread since transparency needs to be updated
					// and surfaceRemoved/Added needs to be called.
					// thread.start();
					thread.createSurface();
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

				if (currentTransparency > 0.05f && transparency <= 0.05) {
					ProteinWorkshop.sgetGlGeometryViewer().surfaceRemoved(structure);
				} else if (newSurface || currentTransparency <= 0.05f && transparency > 0.05f) {
					if (surfaceColorType == null) {
						addComboBox();
					}
					ProteinWorkshop.sgetGlGeometryViewer().surfaceAdded(structure);
				} else if (currentTransparency > 0.05f && transparency > 0.05f) {
					if (surfaceColorType == null) {
						addComboBox();
					}
					ProteinWorkshop.sgetGlGeometryViewer().surfaceRemoved(structure);
					ProteinWorkshop.sgetGlGeometryViewer().surfaceAdded(structure);
				} 
			}
		}
	}

	private class SurfaceTypeListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			final JComboBox source = (JComboBox)e.getSource();
			source.hidePopup();
			
			Color newColor = null;
			ColorBrewer newPalette = null;
			
			if (source.getSelectedIndex() < 2) {
				ColorPaletteChooserDialog paletteChooser = new ColorPaletteChooserDialog(AppBase.sgetActiveFrame());
				paletteChooser.showDialog();
				if (paletteChooser.wasOKPressed()) {
					newPalette = paletteChooser.getColorPalette();
				}
				if (newPalette == null) {
					return;
				}
			} else if (source.getSelectedIndex() == 2) {
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

			Structure structure = AppBase.sgetModel().getStructures().get(0);
			int surfaceCount = structure.getStructureMap().getSurfaceCount();
			if (surfaceCount == 0) {
				return;
			}
			
			// create a list of unique entity ids
			List<Integer> entitySet = null;
			if (source.getSelectedIndex() == 1) {
				entitySet = createEntitySet(structure);
			}

			int index = 0;
			for (Surface s: structure.getStructureMap().getSurfaces()) {
				if (source.getSelectedIndex() == 0) {
					// color by chain
					SurfaceColorUpdater.setPaletteColor(s, newPalette, surfaceCount, index);
					index++;
				} else if (source.getSelectedIndex() == 1) {
					// color by chain type (entity id)
					int entityId = s.getChain().getEntityId();
					SurfaceColorUpdater.setPaletteColor(s, newPalette, entitySet.size(), entitySet.indexOf(entityId));
				} else if (source.getSelectedIndex() == 2) {
					// color by a single color
					Color4f color = new Color4f(newColor);
					SurfaceColorUpdater.setSurfaceColor(s, color);
				} else if (source.getSelectedIndex() == 3) {
					// color by hydrophobicity
					SurfaceColorUpdater.setHydrophobicSurfaceColor(s);
				}
			}
			ProteinWorkshop.sgetGlGeometryViewer().surfaceRemoved(structure);
			ProteinWorkshop.sgetGlGeometryViewer().surfaceAdded(structure);
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
	}
}