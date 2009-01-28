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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;


public class GlobalOptionsPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4797520110521514989L;

//	BatchModeOptionsPanel batchPanel = null;
	
    private final JButton resetButton = new JButton("Reset");
    private final JButton saveImageButton = new JButton("Save Image");
//    JButton saveStateButton = new JButton("Save Orientation, Coloring, etc.");
//    private final JButton advancedSaveImageButton = new JButton("Advanced Image Editor");
    		// image editor removed, was non-functional, anyway - rickb
//    JButton measureDistanceButton = new JButton("Draw Line");
//    LoadStructurePanel loadStructurePanel;
    private final StatePanel statePanel = new StatePanel();
    
    
    public GlobalOptionsPanel() {
        super(false);
        super.setLayout(new FullWidthBoxLayout());
        
        //this.batchPanel = new BatchModeOptionsPanel(model);
//        this.loadStructurePanel = new LoadStructurePanel();
        
        //super.add(this.batchPanel);
        super.add(this.resetButton);  
        super.add(this.saveImageButton);
//        super.add(this.saveStateButton);
//       super.add(this.advancedSaveImageButton);  
//        super.add(this.loadStructurePanel);
        //super.add(this.measureDistanceButton);
        super.add(this.statePanel);
        
        this.resetButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(final ActionEvent e)
            {
                VFAppBase.sgetUpdateController().resetEverything();
                VFAppBase.sgetGlGeometryViewer().requestRepaint();
            }
            
        });
        
        this.resetButton.setToolTipText("Reset everything to its original state.");
        
        this.saveImageButton.addActionListener(new SaveImageListener());
//        this.saveStateButton.addActionListener(new SaveStateListener());
        
        this.saveImageButton.setToolTipText("Save the image to a file.");
        
        
        //this.measureDistanceButton.addActionListener(new DrawLineAction(this.model));
        
//        this.measureDistanceButton.setToolTipText("Click two items in the 3d viewer (left) to draw a line between them. TODO: selections in the tree will work too.");
        
        // this.advancedSaveImageButton.addActionListener(new AdvancedImageAction());
     }

    private final class SaveImageListener implements ActionListener {
    	
		public void actionPerformed(final ActionEvent e) {
			VFAppBase.sgetDocController().saveImage();
		}
	}
    
//    private final class SaveStateListener implements ActionListener {
//    	
//		public void actionPerformed(ActionEvent e) {
//		    Controller.updateController().saveState();
//		}
//	}
    
 /* **
 // Image Editor removed - not working.  28-Jan-08, rickb
 //
	private class AdvancedImageAction implements ActionListener {
    	private final class ImageEditorOpenerThread extends Thread {
    		private final class ScreenshotWaiterThread extends Thread {
				private static final int DURATION_BETWEEN_SCREENSHOT_CHECKS_IN_MILLISECONDS = 1000;
				@Override
				public void run() {
					super.run();
					
					// check for the screenshot each second until it appears. Timeout is fifteen seconds.
					boolean finished = false;
					while(!finished) {
						try {
							super.sleep(ScreenshotWaiterThread.DURATION_BETWEEN_SCREENSHOT_CHECKS_IN_MILLISECONDS);
						} catch (final InterruptedException e) {}
						
						GlGeometryViewer glViewer = VFAppBase.sgetGlGeometryViewer();
						
						if (glViewer != null && !glViewer.hasScreenshotFailed())
							finished = true;
					}
				}
			}
    		
			
			@Override
			public void run()
			{
				GlGeometryViewer glViewer = VFAppBase.sgetGlGeometryViewer();
				glViewer.requestScreenShot(glViewer.getWidth(), glViewer.getHeight());
				
				final Thread screenshotWaiter = new ScreenshotWaiterThread();
				// wait until the image is obtained or the timeout occurs
				screenshotWaiter.start();
				try {
					screenshotWaiter.join();
				} catch (final InterruptedException e) {}
				
				// this application prefers the native look and feel for speed reasons.
				// PDBImage prefers the cross-platform look and feel because of some odd things it does with the interface.
				LookAndFeel oldFeel = null;
				try {
					oldFeel = UIManager.getLookAndFeel();
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				} catch (final Exception e) {e.printStackTrace();}
				
				try {
					UIManager.setLookAndFeel(oldFeel);
				} catch (final Exception e) {e.printStackTrace();}
			}
		}
    	
		public void actionPerformed(final ActionEvent e) {
			// get the screenshot. The screenshot must, unfortunately, be taken during a normal rendering call to the GLCanvas, so a thread must be launched to wait for this to occur and then send the image to the editor.
			final Thread imageEditorOpener = new ImageEditorOpenerThread();
			imageEditorOpener.start();
		}
    	
    }
/* **/
}
