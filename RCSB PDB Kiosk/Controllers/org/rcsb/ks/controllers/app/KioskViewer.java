package org.rcsb.ks.controllers.app;

//  $Id: KioskViewer.java,v 1.9 2006/07/30 15:53:30 milton Exp $
//
//  Copyright 2000-2004 The Regents of the University of California.
//  All Rights Reserved.
//
//  Permission to use, copy, modify and distribute any part of this
//  Molecular Biology Toolkit (MBT)
//  for educational, research and non-profit purposes, without fee, and without
//  a written agreement is hereby granted, provided that the above copyright
//  notice, this paragraph and the following three paragraphs appear in all
//  copies.
//
//  Those desiring to incorporate this MBT into commercial products
//  or use for commercial purposes should contact the Technology Transfer &
//  Intellectual Property Services, University of California, San Diego, 9500
//  Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815,
//  FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
//
//  IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
//  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//  LOST PROFITS, ARISING OUT OF THE USE OF THIS MBT, EVEN IF THE
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.//
//  THE MBT PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
//  UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
//  UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
//  NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
//  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
//  MBT WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
//
//  For further information, please see:  http://mbt.sdsc.edu
//
//  History:
//  $Log: KioskViewer.java,v $
//  Revision 1.9  2006/07/30 15:53:30  milton
//  screen flicker on the pc
//
//  Revision 1.8  2006/07/03 21:53:42  milton
//  screen flicker on the pc
//
//  Revision 1.7  2006/06/30 22:54:59  milton
//  *** empty log message ***
//
//  Revision 1.5  2006/06/27 03:43:09  milton
//  latest.. but still bugs
//
//  Revision 1.4  2006/06/08 05:23:44  milton
//  udpate with secondary structure fix and fog!
//
//  Revision 1.2  2006/05/11 00:04:45  milton
//  added swing utils runnable
//
//  Revision 1.1  2006/05/10 21:47:43  milton
//  OutReach Code
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.0 2005/04/04 00:12:54  moreland
//

// package edu.sdsc.vis.viewers;

import java.net.URL;

import org.rcsb.ks.controllers.doc.KSDocController;
import org.rcsb.ks.controllers.doc.KSStructureXMLHandler;
import org.rcsb.ks.glscene.jogl.KSGlGeometryViewer;
import org.rcsb.ks.ui.KSDocumentFrame;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.mbt.controllers.doc.DocController;
import org.rcsb.mbt.structLoader.StructureXMLHandler;
import org.rcsb.mbt.ui.mainframe.DocumentFrameBase;
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
