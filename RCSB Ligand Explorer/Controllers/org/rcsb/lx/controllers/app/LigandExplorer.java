package org.rcsb.lx.controllers.app;


//  $Id: JoglViewer.java,v 1.6 2007/07/19 13:26:36 jbeaver Exp $
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
//  $Log: JoglViewer.java,v $
//  Revision 1.6  2007/07/19 13:26:36  jbeaver
//  *** empty log message ***
//
//  Revision 1.5  2007/01/20 17:49:29  jbeaver
//  *** empty log message ***
//
//  Revision 1.4  2006/12/07 15:57:46  jbeaver
//  Used Eclipse' Clean Up... tool.
//
//  Revision 1.3  2006/12/07 15:48:41  jbeaver
//  Updated to 2.0 final
//
//  Revision 1.2  2006/11/25 03:10:14  jbeaver
//  beta 6
//
//  Revision 1.1  2006/11/01 18:00:25  jbeaver
//  First commit. This project is a rewrite of Ligand3D, which utilized Java3d for graphics. It now uses Jogl.
//
//  Revision 1.2  2006/10/04 17:21:06  jbeaver
//  Lots of changes from surfaces to improved picking
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.8  2006/07/18 21:06:38  jbeaver
//  *** empty log message ***
//
//  Revision 1.7  2006/06/21 21:00:53  jbeaver
//  fixed fog so it works with states
//
//  Revision 1.6  2006/06/06 22:06:34  jbeaver
//  *** empty log message ***
//
//  Revision 1.5  2006/06/02 23:41:25  jbeaver
//  *** empty log message ***
//
//  Revision 1.4  2006/05/30 09:43:44  jbeaver
//  Added lines and fog
//
//  Revision 1.3  2006/05/17 06:18:07  jbeaver
//  added some biological unit processing
//
//  Revision 1.2  2006/05/16 17:57:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.0 2005/04/04 00:12:54  moreland
//

// package edu.sdsc.vis.viewers;

// CORE JAVA

import java.awt.image.BufferedImage;

import org.rcsb.lx.controllers.scene.LXSceneController;
import org.rcsb.lx.controllers.update.LXUpdateController;
import org.rcsb.lx.glscene.jogl.LXGlGeometryViewer;
import org.rcsb.lx.glscene.jogl.LXSceneNode;
import org.rcsb.lx.model.LXModel;
import org.rcsb.lx.ui.LXDocumentFrame;
import org.rcsb.mbt.controllers.update.UpdateController;
import org.rcsb.mbt.glscene.controller.SceneController;
import org.rcsb.mbt.glscene.jogl.GlGeometryViewer;
import org.rcsb.mbt.glscene.jogl.JoglSceneNode;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.util.PickUtils;
import org.rcsb.mbt.ui.mainframe.DocumentFrameBase;
import org.rcsb.vf.controllers.app.VFAppBase;




/**
 * LigandExplorer.java
 * <P>
 * 
 * @author John L. Moreland
 * @copyright SDSC
 * @see
 */
public class LigandExplorer extends VFAppBase
{
	public class LXAppModuleFactory extends VFAppBase.VFAppModuleFactory
	{
		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createSceneController()
		 */
		@Override
		public SceneController createSceneController() {
			return new LXSceneController();
		}

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createDocFrame(java.lang.String)
		 */
		@Override
		public DocumentFrameBase createDocFrame(String name)
		{
			return new LXDocumentFrame(name);
		}

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createGlGeometryViewer()
		 */
		@Override
		public GlGeometryViewer createGlGeometryViewer()
		{
			return new LXGlGeometryViewer();
		}

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createModel()
		 */
		@Override
		public StructureModel createModel()
		{
			return new LXModel();
		}


		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createViewUpdateController()
		 */
		@Override
		public UpdateController createUpdateController()
		{
			return new LXUpdateController();
		}

		/* (non-Javadoc)
		 * @see org.rcsb.mbt.appController.AppBase.AppModuleFactory#createSceneNode()
		 */
		@Override
		public JoglSceneNode createSceneNode() {
			return new LXSceneNode();
		}
	}
	
	public static boolean saveInteractionsFlag = false;

	public static void main(final String[] args)
	{
		final LigandExplorer app = new LigandExplorer(args);
		
		for (int argIX = 0; argIX < args.length; argIX++)
		if (args[argIX].equals("-ligand"))
			app.properties.setProperty("ligand", args[++argIX]);
						// field args for app-specific args

		app.initialize(true);
	}
	
	@Override
	public String toString() { return "SDSC Ligand Explorer"; }
	
	/**
	 * Temporary hack - indicate to the subsystems the app.
	 * 
	 * @return
	 */
	@Override
	public boolean isLigandExplorer() { return true; }

	/**
	 * Constructor
	 * 
	 * @param args - argument list from the main() caller.
	 */
	public LigandExplorer(final String[] args)
	{
		super(args);
	}

	@Override
	public void initialize(final boolean isApplication)
	{
		appModuleFactory = new LXAppModuleFactory();
		activeFrame = appModuleFactory.createDocFrame("PDB LIgand Explorer (Powered by the MBT)");
	
		super.initialize(true);
		
		final String initialLigand = this.properties.getProperty("ligand");
		
		LXModel model = sgetModel();
		model.setInitialLigand(initialLigand);

		final String showAsymmetricUnitOnly = this.properties.getProperty("show_asymmetric_unit_only");
		SceneController sceneController = activeFrame.getSceneController();
		if (showAsymmetricUnitOnly != null && showAsymmetricUnitOnly.equals("true"))
			sceneController.setShowAsymmetricUnitOnly(true);
		

		else
			sceneController.setShowAsymmetricUnitOnly(false);


		PickUtils.setPickLevel(PickUtils.PICK_RESIDUES);
		
		final String structureUrlParam = this.properties.getProperty("structure_url");
		
		if (structureUrlParam != null)
			model.setStructures(activeFrame.getDocController().readStructuresFromUrl(structureUrlParam));

		activeFrame.initialize(true);
	}
	

	public BufferedImage screenshot = null;


	/*
	 * Convenience access overrides
	 * @return
	 */
	public static LigandExplorer getApp() { return (LigandExplorer)VFAppBase.getApp(); }	
	@Override
	public LXDocumentFrame getActiveFrame() { return (LXDocumentFrame)activeFrame; }
	public static LXDocumentFrame sgetActiveFrame() { return getApp().getActiveFrame(); }	
	public static LXModel sgetModel() { return sgetActiveFrame().getModel(); }
	public static LXUpdateController sgetUpdateController() { return sgetActiveFrame().getUpdateController(); }
	public static LXSceneController sgetSceneController() { return sgetActiveFrame().getSceneController(); }
	public static LXGlGeometryViewer sgetGlGeometryViewer() { return sgetActiveFrame().getGlGeometryViewer(); }
}