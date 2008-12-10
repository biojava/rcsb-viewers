package org.rcsb.vf.controllers.app;

//  $Id: JoglViewer.java,v 1.2 2007/07/19 13:18:49 jbeaver Exp $
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
//  Revision 1.2  2007/07/19 13:18:49  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2007/02/16 21:41:17  jbeaver
//  first commit
//
//  Revision 1.3  2007/01/03 19:33:49  jbeaver
//  *** empty log message ***
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.doc.DocController;
import org.rcsb.mbt.controllers.scene.SceneController;
import org.rcsb.vf.controllers.doc.VFDocController;
import org.rcsb.vf.glscene.jogl.VFGlGeometryViewer;
import org.rcsb.vf.ui.VFDocumentFrameBase;





import com.sun.opengl.impl.GLPbufferImpl;


/**
 * JoglViewer.java
 * <P>
 * 
 * @author John L. Moreland
 * @author rickb
 * @copyright SDSC
 * @see
 */
public abstract class VFAppBase extends AppBase
{
	public abstract class VFAppModuleFactory extends AppBase.AppModuleFactory
	{
		/* (non-Javadoc)
		 * @see org.rcsb.mbt.appController.AppBase.AppModuleFactory#createDocController()
		 */
		@Override
		public DocController createDocController() { return new VFDocController(); }
	}
	
	public static final String PERFORMANCE_WARNING_KEY = "Performance Warning";
	
	public static VFAppBase getApp() { return (VFAppBase)AppBase.getApp(); }
	public static VFDocumentFrameBase sgetActiveFrame() { return (VFDocumentFrameBase)AppBase.getApp().getActiveFrame(); }
	public static VFDocController sgetDocController() { return sgetActiveFrame().getDocController(); }
	public static VFGlGeometryViewer sgetGlGeometryViewer() { return sgetActiveFrame().getGlGeometryViewer(); }
	
	/**
	 * Constructor - parses args and registers a panel(?)
	 * 
	 * @param args - passed in from the derived class (from the commandline)
	 * @param isSimpleViewer - temporary indicator.  SimpleViewer has different
	 * 						   display capabilities that will have to be addressed
	 * 						   in the future.
	 * 
	 * 						   This is needed so the viewers will behave as they did
	 * 						   before the massive refactoring.
	 */
	public VFAppBase(String args[])
	{
		super(args);
		
		if (args != null)
		{
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-structure_url")) {
					properties.setProperty("structure_url", args[++i]);
				} else if (args[i].equals("-screenshot_only")) {
					AppBase.backgroundScreenshotOnly = true;
				} else if (args[i].equals("-screenshot_width")) {
					properties.setProperty("screenshot_width", args[++i]);
				} else if (args[i].equals("-screenshot_height")) {
					properties.setProperty("screenshot_height",args[++i]);
				} else if (args[i].equals("-allowShaders")) {
					_allowShaders = true;
				} else if (args[i].equals("-output_path")) {
					properties.setProperty("output_path", args[++i]);
				} else if (args[i].equals("-output_format")) {
					properties.setProperty("output_format", args[++i]);
				} else if (args[i].equals("-unit_id")) {
					properties.setProperty("unit_id", args[++i]);
				} else if (args[i].equals("-treat_models_as_subunits")) {
					properties.setProperty("treat_models_as_subunits", "true");
				} else if (args[i].equals("-log_folder")) {
					properties.setProperty("log_folder", args[++i]);
				} else if (args[i].equals("-disable_global_transforms")) {
					properties.setProperty("disable_global_transforms", "true");
				} else if (args[i].equals("-show_asymmetric_unit_only")) {
					properties.setProperty("show_asymmetric_unit_only", "true");
	
				} else if (args[i].equals("-global_rotation_matrices")) {
					properties.setProperty("global_rotation_matrices", args[++i]);
				} else if (args[i].equals("-global_translation_vectors")) {
					properties.setProperty("global_translation_vectors", args[++i]);
				}
				// simpleViewer.properties.setProperty( "immuno_base_path",
				// args[++i] );
			}
		}
	}
	
	/**
	 * Sets the properties from the args.
	 * Creates an (empty) model and registers the app with the model.
	 * 
	 * @param isApplication - TBD.  Presumably and application vs. an applet.
	 */
	protected void initialize(boolean isApplication)
	{
		assert (appModuleFactory != null);
		assert (activeFrame != null);
		
		_isApplication = isApplication;
		
		String outputFolder = this.properties.getProperty("log_folder");
		if (outputFolder != null) {
			outputFolder = outputFolder.trim();
			if (outputFolder.length() != 0) {
				final char lastChar = outputFolder
						.charAt(outputFolder.length() - 1);
				if (lastChar != '/' && lastChar != '\\') {
					outputFolder += '/';
				}
	
				try {
					System.setErr(new PrintStream(new FileOutputStream(
									new File(outputFolder
											+ "proteinWorkshop_err.txt"))));
					System.setOut(new PrintStream(new FileOutputStream(
									new File(outputFolder
											+ "proteinWorkshop_out.txt"))));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	
		SceneController sceneController = getActiveFrame().getSceneController();
		final String treatModelsAsSubunits = this.properties
				.getProperty("treat_models_as_subunits");
		if (treatModelsAsSubunits != null && treatModelsAsSubunits.equals("true"))
			sceneController.setTreatModelsAsSubunits(true);
		
		else
			sceneController.setTreatModelsAsSubunits(false);
	
		final String unitId = this.properties.getProperty("unit_id");
		getActiveFrame().getDocController().setInitialBiologicalUnitId(unitId);
	
		final String showAsymmetricUnitOnly = this.properties.getProperty("show_asymmetric_unit_only");
		if (showAsymmetricUnitOnly != null && showAsymmetricUnitOnly.equals("true"))
			sceneController.setShowAsymmetricUnitOnly(true);
		
		else
			sceneController.setShowAsymmetricUnitOnly(false);
	
		final String disableGlobalTransforms = this.properties
				.getProperty("disable_global_transforms");
		if (disableGlobalTransforms != null
				&& disableGlobalTransforms.equals("true"))
			sceneController.setDisableGlobalTransforms(true);
		
		else
			sceneController.setDisableGlobalTransforms(false);
	
		final String isDebug = this.properties.getProperty("debug");
		if (isDebug != null && isDebug.equals("true"))
			sceneController.setDebugEnabled(true);
		
		else
			sceneController.setDebugEnabled(false);
	}


	public GLPbufferImpl offscreenPBuffer = null;

	public boolean offscreenDisplayfinished = false;

	// -----------------------------------------------------------------------------


	/**
	 * JLM DEBUG: This will eventually be non-static method for handling a
	 * StructureDocumentEvent.
	 * <P>
	 * Generate renderable objects for the structure's visible components.
	 */

	public BufferedImage screenshot = null;
}
