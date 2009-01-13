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
 * Created on 2006/07/30
 *
 */ 
package org.rcsb.ks.controllers.app;

// package edu.sdsc.vis.viewers;

import java.net.URL;

import org.rcsb.ks.controllers.doc.KSDocController;
import org.rcsb.ks.controllers.doc.KSStructureXMLHandler;
import org.rcsb.ks.glscene.jogl.KSGlGeometryViewer;
import org.rcsb.ks.ui.KSDocumentFrame;
import org.rcsb.uiApp.controllers.doc.DocController;
import org.rcsb.uiApp.ui.mainframe.DocumentFrameBase;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.mbt.structLoader.StructureXMLHandler;
import org.rcsb.vf.controllers.scene.mutators.MutatorBase;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;


/**
 * KioskViewer.java
 * <P>
 * 
 * @author John L. Moreland
 * @copyright SDSC
 * @see
 */
public class KioskViewer extends VFAppBase
{
	public class KSAppModuleFactory extends VFAppBase.VFAppModuleFactory
	{

		/* (non-Javadoc)
		 * @see org.rcsb.mbt.appController.AppBase.AppModuleFactory#createStructureXMLHandler(java.lang.String)
		 */
		@Override
		public StructureXMLHandler createStructureXMLHandler(String dataset) {
			return new KSStructureXMLHandler(dataset);
		}

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createDocController()
		 */
		@Override
		public DocController createDocController() {
			return new KSDocController();		}

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createDocFrame(java.lang.String)
		 */
		@Override
		public DocumentFrameBase createDocFrame(String name, URL iconUrl)
		{
			return new KSDocumentFrame(name, iconUrl);
		}

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createGlGeometryViewer()
		 */
		@Override
		public GlGeometryViewer createGlGeometryViewer()
		{
			return new KSGlGeometryViewer();
		}
		
	}
	
	public KioskViewer(String args[])
	{
		super(args);
		initialize();
		sgetSceneController().setShowAsymmetricUnitOnly(true);
	}
	
	private void initialize()
	{
		appModuleFactory = new KSAppModuleFactory();
		MutatorBase.setActivationType(MutatorBase.ActivationType.RIBBONS);
		activeFrame = appModuleFactory.createDocFrame("PDB Kiosk (Powered by the MBT)", null);
		((KSDocumentFrame)activeFrame).initialize(true, true);
	}

	public static KioskViewer getApp() { return (KioskViewer)_theJApp; }	
	@Override
	public KSDocumentFrame getActiveFrame() { return (KSDocumentFrame)activeFrame; }
	public static KSDocumentFrame sgetActiveFrame() { return getApp().getActiveFrame(); }
	public static KSGlGeometryViewer sgetGlGeometryViewer() { return sgetActiveFrame().getGlGeometryViewer(); }
	
	private boolean initializedComplete = false;
	private boolean downloadMode = false;
	public boolean isDownloadMode() { return downloadMode; }

	public boolean isInitialized()
	{
		return initializedComplete;
	}
	
	public void setInitialized() { initializedComplete = true; }

	public static void main(String[] _args)
	{
		SlideShow s = new SlideShow(_args);
		s.start();
	}
}
