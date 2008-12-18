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
    private final JButton advancedSaveImageButton = new JButton("Advanced Image Editor");
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
        super.add(this.advancedSaveImageButton);
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
        
        this.advancedSaveImageButton.addActionListener(new AdvancedImageAction());
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
}
