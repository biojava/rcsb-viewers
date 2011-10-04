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
package org.rcsb.pw.ui.mutatorPanels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Color4f;

import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.Surface;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.attributes.SurfaceStyle;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.pw.ui.ColorPreviewerPanel;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.doc.SurfaceThread;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;


public class SurfacePanel extends JPanel implements IUpdateListener
{
	private int TRANSPARENCY_MIN = 0;
	private int TRANSPARENCY_MAX = 100;
	private int TRANSPARENCY_INIT = 0;
	private Color defaultColor = new Color(0.1f, 0.8f, 1.0f);
	private static final long serialVersionUID = -7205000642717901355L;
	private final JLabel colorLabel = new JLabel("Change Color");
	private final ColorPane colorPanel = new ColorPane(Color.CYAN);
	private final JSlider transparencySlider = new JSlider(JSlider.HORIZONTAL,
            TRANSPARENCY_MIN, TRANSPARENCY_MAX, TRANSPARENCY_INIT);


//	private final JLabel colorLabel = new JLabel("Color:");
//    private final JColorChooser colorPanel =  new JColorChooser(); (too large, open in separate window?)
	
	private class CustomLayout implements LayoutManager2 {
    	private Dimension size = new Dimension(0,0);
    	
		public void addLayoutComponent(String name, Component comp) {}

		public void layoutContainer(Container parent) {
			final int buffer = 3;
			final Insets insets = parent.getInsets();
			int curY = insets.top + buffer;
			int curX = insets.left + buffer;
			
			
			final Dimension transparencySliderSize = transparencySlider.getPreferredSize();
			System.out.println("SurfacePanel: size: " + transparencySliderSize);
			transparencySlider.setBounds(curX, curY, transparencySliderSize.width, transparencySliderSize.height);
			curY += transparencySliderSize.height + buffer;
			
			final Dimension colSize = colorLabel.getPreferredSize();
			colorLabel.setBounds(curX, curY, colSize.width, colSize.height);
			curY += colSize.height + buffer;
			
			Container parentParent = parent.getParent();
			Insets parentParentInsets = parentParent.getInsets();
			this.size.width = parentParent.getWidth() - parentParentInsets.left - parentParentInsets.right;
			this.size.height = curY + insets.bottom;
		}

		public Dimension minimumLayoutSize(Container parent) {
			return this.size;
		}

		public Dimension preferredLayoutSize(Container parent) {
			return this.size;
		}

		public void removeLayoutComponent(Component comp) {}

		public void addLayoutComponent(Component comp, Object constraints) {}

		public float getLayoutAlignmentX(Container target) {
			return 0;
		}

		public float getLayoutAlignmentY(Container target) {
			return 0;
		}

		public void invalidateLayout(Container target) {}

		public Dimension maximumLayoutSize(Container target) {
			return this.size;
		}
    }
	
    public SurfacePanel() {
        super(false);
 //       super.setLayout(new CustomLayout());
        super.setLayout(new BorderLayout());
        super.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Surface"),
                BorderFactory.createEmptyBorder(-1,1,1,1)));
        
        transparencySlider.setMajorTickSpacing(50);
        transparencySlider.setMinorTickSpacing(10);
        transparencySlider.setPaintTicks(true);
        //Create the label table
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put( new Integer( 0 ), new JLabel("Off") );
        labelTable.put( new Integer( 50 ), new JLabel("Transparent") );
        labelTable.put( new Integer( 100 ), new JLabel("Opaque") );
        transparencySlider.setLabelTable( labelTable );
        transparencySlider.setPaintLabels(true);
        super.add(transparencySlider, BorderLayout.PAGE_START);
        
        JPanel firstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstPanel.add(colorPanel);
        firstPanel.add(colorLabel);
        super.add(firstPanel, BorderLayout.PAGE_END);
   
        this.colorPanel.addMouseListener(new ColorPanelListener());   
        this.colorPanel.setToolTipText("Change surface color");
        
        
        this.transparencySlider.addChangeListener(new TransparencySliderListener());   
        this.transparencySlider.setToolTipText("Change transparency of the surface");
  
        this.reset();
        
        AppBase.sgetUpdateController().registerListener(this);
    }

    public void reset() {
        this.colorPanel.setColor(defaultColor);
    }
    
	/* (non-Javadoc)
	 * @see edu.sdsc.mbt.views_controller.IViewUpdateListener#handleModelChangedEvent(edu.sdsc.mbt.views_controller.ViewUpdateEvent)
	 */
	public void handleUpdateEvent(UpdateEvent evt)
	{
		if (evt.action == UpdateEvent.Action.VIEW_RESET)
			reset();
	}
	
	private class ColorPanelListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			final ColorPane source = (ColorPane)e.getSource();
	    	Color4f newColor = new Color4f(source.getColor());
	   
	    	Structure structure = AppBase.sgetModel().getStructures().get(0);
	    	if (structure.getStructureMap().getSurfaceCount() == 0) {
	    		return;
	    	}
	    	
	    	for (Surface s: structure.getStructureMap().getSurfaces()) {
                System.out.println("rendering surface");
	    		Color4f[] colors = s.getColors();

	    		if (colors != null && colors.length > 0) {
	    			// copy current transparency
	    			float transparency = colors[0].getW();
	    			newColor.setW(transparency);

	    			for (int i = 0; i < colors.length; i++) {
	    				colors[i] = newColor;
	    			}

	    			ProteinWorkshop.sgetGlGeometryViewer().surfaceRemoved(structure);
	    			ProteinWorkshop.sgetGlGeometryViewer().surfaceAdded(structure);
	    		}
	    	}
		}

		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	}
	
	private class TransparencySliderListener implements ChangeListener {
	    
		public void stateChanged(ChangeEvent e) {
	    	JSlider source = (JSlider)e.getSource();
	        if (!source.getValueIsAdjusting()) {
	            Structure structure = AppBase.sgetModel().getStructures().get(0);
	            
	            // lazy initialization of the surface
	            boolean newSurface = false;
	            if (structure.getStructureMap().getSurfaceCount() == 0) {
	    			SurfaceThread thread = new SurfaceThread();	
	    			thread.createSurface();
	    			newSurface = true;
	            }
	            
	            for (Surface s: structure.getStructureMap().getSurfaces()) {
//	        	Surface s = structure.getStructureMap().getSurface(0);
	        
	        	float transparency = ((int)source.getValue()) * 0.01f;
	        	System.out.println("Setting transparency: " + transparency);
	        	Color4f[] colors = s.getColors();
	        	if (colors != null && colors.length > 0) {
	                float currentTranspacency = colors[0].getW();

	        		if (currentTranspacency > 0.05f && transparency <= 0.05) {
	        			for (int i = 0; i < colors.length; i++) {
	        				colors[i].setW(transparency);
	        			}
	        			ProteinWorkshop.sgetGlGeometryViewer().surfaceRemoved(structure);
	        		} else if (newSurface || currentTranspacency <= 0.05f && transparency > 0.05f) { 
	        			for (int i = 0; i < colors.length; i++) {
	        				colors[i].setW(transparency);
	        			}
	        			ProteinWorkshop.sgetGlGeometryViewer().surfaceAdded(structure);
	        		} else if (currentTranspacency > 0.05f && transparency > 0.05f) {
	        			for (int i = 0; i < colors.length; i++) {
	        				colors[i].setW(transparency);
	        			}
	        			ProteinWorkshop.sgetGlGeometryViewer().surfaceRemoved(structure);
	        			ProteinWorkshop.sgetGlGeometryViewer().surfaceAdded(structure);
	        		}
	           	}
	            }
	        }
	    }
	}
}