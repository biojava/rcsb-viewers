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
package org.rcsb.vf.ui;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import org.rcsb.uiApp.controllers.doc.LoadThread;
import org.rcsb.uiApp.ui.mainframe.DocumentFrameBase;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.controllers.doc.VFDocController;
import org.rcsb.vf.controllers.scene.SceneController;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;

public abstract class VFDocumentFrameBase extends DocumentFrameBase
{
	private static final long serialVersionUID = 1761606139608488229L;

	@Override
	public VFDocController getDocController() { return (VFDocController)super.getDocController(); }

	
	private GlGeometryViewer _glGeometryViewer = null;
	public GlGeometryViewer getGlGeometryViewer()
	{
		if (_glGeometryViewer == null) _glGeometryViewer = VFAppBase.sgetAppModuleFactory().createGlGeometryViewer();
		return _glGeometryViewer;
	}

	/**
	 * The scene controller has all the machinery to manipulate the scene.
	 */
	private SceneController sceneController = null;
	public SceneController getSceneController()
	{
		if (sceneController == null) sceneController = VFAppBase.sgetAppModuleFactory().createSceneController();
		return sceneController;
	}
	
	public VFDocumentFrameBase(String title, URL iconURL)
	{
		super(title, iconURL);
		
		try
		{
			SwingUtilities.invokeAndWait(
				new Runnable()
				{
					public void run()
					{
						// set up the standard tool tips
						final ToolTipManager ttm = ToolTipManager.sharedInstance();
						ttm.setLightWeightPopupEnabled(false); // only heavy
						// components show
						// above the 3d
						// viewer.
						ttm.setDismissDelay(20000); // wait 20 seconds before the
						// tooltip disappears.
		
						JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
						try
						{
							UIManager.setLookAndFeel(UIManager
									.getSystemLookAndFeelClassName());
							// UIManager.setLookAndFeel("net.java.plaf.windows.WindowsLookAndFeel");
						}
						
						catch (final Exception e) {}
					}
				});
		}
		
		catch (final InterruptedException e)
		{
			e.printStackTrace();
		}
		
		catch (final InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadURL(String url)
	{
		LoadThread loader = new LoadThread(url);
		loader.run();
	}
	
	public void loadURL(String[] pdbIds)
	{
		LoadThread loader = new LoadThread(pdbIds);
		loader.run();
	}

	@Override
	public void initialize(boolean showFrame)
	{
		super.initialize(showFrame);
		VFAppBase.sgetGlGeometryViewer();
		// force the geometryviewer creation
	}
}
