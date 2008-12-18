package org.rcsb.ks.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.rcsb.ks.controllers.doc.KSDocController;
import org.rcsb.ks.glscene.jogl.KSGlGeometryViewer;
import org.rcsb.mbt.model.Structure;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;
import org.rcsb.vf.ui.VFDocumentFrameBase;
import org.rcsb.vf.controllers.scene.SceneState;


@SuppressWarnings("serial")
public class KSDocumentFrame extends VFDocumentFrameBase implements IUpdateListener, KeyEventDispatcher
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
	public KSDocumentFrame(String name, URL iconUrl)
	{
		super(name, iconUrl);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
	}
	
	@Override
	public void setVisible(boolean _value) {

		if (_value)
		{
			super.setVisible(_value);
			setCursor(getToolkit().createCustomCursor(
					new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB),
					new Point(0, 0), "null"));
		}
		
		else
		{
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
		getUpdateController().registerListener(this);
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

				container.add(glViewer);

				structurePanel = new StructurePanel();
				structurePanel.setDoubleBuffered(true);
				container.add(structurePanel);
				pack();

				setSize(size);
				if (System.getProperty("os.name").equals("Mac OS X"))
					gDevice.setFullScreenWindow(KSDocumentFrame.this);
				
				else
				{
					setLocation(0, 0);
					setSize(size.width, size.height);
				}
				
				

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
	
	public void updateState(Structure _structure, SceneState _state) {
		structurePanel.updateState(_structure, _state);
	}

	public void updateStructure(Structure structure2)
	{
		structurePanel.updateStructure(structure2);
		getGlGeometryViewer().repaint();
	}
	
	
	public void clear()
	{
		getModel().clear();
		resetView();
	}
	
	/* (non-Javadoc)
	 * @see edu.sdsc.mbt.views_controller.IViewUpdateListener#handleModelChangedEvent(edu.sdsc.mbt.views_controller.ViewUpdateEvent)
	 */
	public void handleUpdateEvent(UpdateEvent evt)
	{
		if (evt.action == UpdateEvent.Action.CLEAR_ALL) clear();
	}
	
	/*
	 * quit on ctrl-q
	 */
	public boolean dispatchKeyEvent(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			setVisible(false);

		return true;
	}
}
