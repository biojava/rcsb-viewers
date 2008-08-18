package org.rcsb.ks.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.rcsb.ks.controllers.app.KSState;
import org.rcsb.ks.controllers.doc.KSDocController;
import org.rcsb.ks.glscene.jogl.KSGlGeometryViewer;
import org.rcsb.ks.glscene.jogl.StructurePanel;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.util.PickUtils;
import org.rcsb.mbt.ui.mainframe.DocumentFrameBase;


public class KSDocumentFrame extends DocumentFrameBase
{
	@Override
	public KSGlGeometryViewer getGlGeometryViewer() { return (KSGlGeometryViewer)super.getGlGeometryViewer(); }
	public KSDocController getDocController() { return (KSDocController)super.getDocController(); }
	class PictureMakerFrameListener extends ComponentAdapter
	{
		private KSDocumentFrame parent;

		public PictureMakerFrameListener(KSDocumentFrame parent)
		{
			this.parent = parent;
		}

		@Override
		public void componentResized(ComponentEvent e)
		{
			JFrame frame = (JFrame) e.getSource();
			this.parent.curSize = frame.getSize();
		}
	}
	
	GraphicsEnvironment gEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();

	GraphicsDevice gDevice = gEnvironment.getDefaultScreenDevice();
	
	protected StructurePanel structurePanel;
	
	public static double bigX = 0.0;
	
	public static double bigY = 0.0;
	
	public static double bigZ = 0.0;
	
	/**
	 * Simple constructor - satisfies framework requirments
	 * 
	 * @param name
	 */
	public KSDocumentFrame(String name) { super(); }
	
	@Override
	public void setVisible(boolean _value) {

		if (_value) {
			super.setVisible(_value);
			
		} else {
			super.setVisible(_value);
			System.exit(1);
		}
	}

	@Override
	public void finalize() {
		try {
			throw new Exception("dot dot dot.");
		} catch (Exception _e) {
			_e.printStackTrace();
		}

	}

	public void initialize(boolean isApplication, boolean showFrame)
	{
		super.initialize(showFrame);
		
		PickUtils.setPickLevel(PickUtils.PICK_RESIDUES);

		try
		{
			SwingUtilities.invokeAndWait(
				new Runnable()
				{
					public void run()
					{
						setFullScreen();
					}
				});
		}
		
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}

		// this.structureViewer.setBackgroundColor(new float[] {255,255,255 });

		// Status.bringBackToTop();
	}

	public void setFullScreen()
	{
		setUndecorated(true);

		final Dimension size = Toolkit.getDefaultToolkit().getScreenSize();

		if (gDevice.isFullScreenSupported())
		{
			try
			{
				Container container = getContentPane();
				container.setBackground ( Color.black );

				container.setLayout(new BoxLayout(container,
						BoxLayout.Y_AXIS));
				KSGlGeometryViewer glViewer = getGlGeometryViewer();
				glViewer.setPreferredSize( new Dimension ( size.width-10, size.height-20 ) );
				glViewer.setBackground( Color.blue );
//				glViewer.setFocusable( true );
//				glViewer.setFocusCycleRoot( true );

				container.add(glViewer);

				structurePanel = new StructurePanel();
				structurePanel.setDoubleBuffered(true);
				container.add(structurePanel);
//				pack();
//				structurePanel.requestFocus();
				// glViewer.requestFocus();
				// KioskViewer.this.add();

				// {{ the default screen size }}
				setSize(size);
				// Dimension prefsize = new Dimension ( size.width,
				// size.height);
				 gDevice.setFullScreenWindow(KSDocumentFrame.this);
				// int x_location = (size.width - prefsize.width) / 2;
				// int y_location = (size.height - prefsize.height) / 2;

				// setSize( prefsize );
				// setLocation( 0, 0 );
				setResizable( false );
				setVisible(true);
			}
			
			catch (Exception _E)
			{
				_E.printStackTrace();
			}
		}
	}

	public void resetView()
	{
		getGlGeometryViewer().resetView(false, false);
	}
	
	public void updateState(Structure _structure, KSState _state) {
		structurePanel.updateState(_structure, _state);
	}

	public void updateStructure(Structure structure2)
	{
		structurePanel.updateStructure(structure2);
		getGlGeometryViewer().repaint();
	}
}
