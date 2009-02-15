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
 * Created on 2007/07/19
 *
 */ 
package org.rcsb.vf.controllers.app;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.doc.DocController;
import org.rcsb.vf.controllers.doc.VFDocController;
import org.rcsb.vf.controllers.scene.SceneController;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;
import org.rcsb.vf.glscene.jogl.JoglSceneNode;
import org.rcsb.vf.ui.VFDocumentFrameBase;

import com.sun.opengl.impl.GLPbufferImpl;


/**
 * JoglViewer.java
 * <P>
 * 
 * @author John L. Moreland
 * @author rickb
 * @see
 */
public abstract class VFAppBase extends AppBase
{
	public abstract class VFAppModuleFactory extends AppBase.AppModuleFactory
	{
		@Override
		public Object createStructureMapUserData() { return createSceneNode(); }
		public GlGeometryViewer createGlGeometryViewer() { return new GlGeometryViewer(); }
		public JoglSceneNode createSceneNode() { return new JoglSceneNode(); }
		public SceneController createSceneController() { return new SceneController(); }

		/* (non-Javadoc)
		 * @see org.rcsb.mbt.appController.AppBase.AppModuleFactory#createDocController()
		 */
		@Override
		public DocController createDocController() { return new VFDocController(); }
	}
	
	public static final String PERFORMANCE_WARNING_KEY = "Performance Warning";
	
	public static VFAppBase getApp() { return (VFAppBase)AppBase.getApp(); }
	public static VFDocumentFrameBase sgetActiveFrame() { return VFAppBase.getApp().getActiveFrame(); }
	public static VFDocController sgetDocController() { return sgetActiveFrame().getDocController(); }
	public static GlGeometryViewer sgetGlGeometryViewer() { return sgetActiveFrame().getGlGeometryViewer(); }
	public static SceneController sgetSceneController() { return sgetActiveFrame().getSceneController(); }
	
	public static VFAppModuleFactory sgetAppModuleFactory() { return (VFAppModuleFactory)AppBase.sgetAppModuleFactory(); }

	public VFDocumentFrameBase getActiveFrame() { return (VFDocumentFrameBase)super.getActiveFrame(); }
	
	/**
	 * Constructor - parses args and registers a panel(?)
	 * 
	 * @param args - passed in from the derived class (from the command line)
	 */
	public VFAppBase(String args[])
	{
		
		super(args);
		
		if (args != null)
		{
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-structure_url")) {
					properties.setProperty("structure_url", args[++i]);
				} else if (args[i].equals("-structure_id_list")) {
					properties.setProperty("structure_id_list", args[++i]);
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
		if (showAsymmetricUnitOnly != null && showAsymmetricUnitOnly.equals("true")) {
			sceneController.setShowAsymmetricUnitOnly(true);
		}
		else {
			sceneController.setShowAsymmetricUnitOnly(false);
		}
	
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
