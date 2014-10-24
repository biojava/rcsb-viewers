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
package org.rcsb.pw.controllers.app;


import java.net.URL;

import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureModel.StructureList;
import org.rcsb.pw.controllers.scene.PWSceneController;
import org.rcsb.pw.glscene.jogl.PWGlGeometryViewer;
import org.rcsb.pw.ui.PWDocumentFrame;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.doc.SurfaceThread;
import org.rcsb.uiApp.ui.mainframe.DocumentFrameBase;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.controllers.scene.SceneController;
import org.rcsb.vf.controllers.scene.mutators.MutatorBase;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;
import org.rcsb.vf.ui.VFDocumentFrameBase;


/**
* ProteinWorkshopViewer.java
* <P>
* 
* @author John L. Moreland
* @author rickb
* @copyright SDSC
* @see
*/
public class ProteinWorkshop extends VFAppBase
{
	public class PWAppModuleFactory extends VFAppBase.VFAppModuleFactory
	{
		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JVFAppBase.AppModuleFactory#createSceneController()
		 */
		@Override
		public SceneController createSceneController() { return new PWSceneController(); }

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JVFAppBase.AppModuleFactory#createDocFrame(java.lang.String)
		 */
		@Override
		public DocumentFrameBase createDocFrame(String name, URL iconUrl) {return new PWDocumentFrame(name, iconUrl); }

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JVFAppBase.AppModuleFactory#createGlGeometryViewer()
		 */
		@Override
		public GlGeometryViewer createGlGeometryViewer() {return new PWGlGeometryViewer(); }
		
	}
	
	// Accessors
	public static ProteinWorkshop getApp() { return (ProteinWorkshop)_theJApp; }	
	@Override
	public PWDocumentFrame getActiveFrame() { return (PWDocumentFrame)activeFrame; }
	public static PWDocumentFrame sgetActiveFrame() { return getApp().getActiveFrame(); }
	public static PWSceneController sgetSceneController() { return (PWSceneController)VFAppBase.sgetSceneController(); }
	public static PWGlGeometryViewer sgetGlGeometryViewer() { return sgetActiveFrame().getGlGeometryViewer(); }

	public ProteinWorkshop(final String args[])
	{
		super(args);
	}
	
	/**
	 * Main entry point for ProteinWorkshopViewer unit testing.
	 * <P>
	 */
	public static void main(final String[] args)
	{
		final ProteinWorkshop app = new ProteinWorkshop(args);	
		app.initialize(true, true);
	}
	
	public static boolean backgroundScreenshotOnly = false;
	
	public void initialize(final boolean isApplication, final boolean showFrame)
	{
		appModuleFactory = new PWAppModuleFactory();
		activeFrame = appModuleFactory.createDocFrame("RCSB PDB Protein Workshop "  + PWVersionInformation.version() + " (powered by the MBT)",
													  ProteinWorkshop.class.getResource("images/icon_128_PW.png"));
		
		super.initialize(isApplication);
		
		activeFrame.initialize(true);

//		final StructureModel model = sgetModel();
		
		MutatorBase.setActivationType(MutatorBase.ActivationType.ATOMS_AND_BONDS);
		
		String structureUrlParam = this.properties.getProperty("structure_url");
		
		
		if (structureUrlParam != null) {
			((VFDocumentFrameBase)activeFrame).loadURL(structureUrlParam);
		}
		
		String structureIdList = this.properties.getProperty("structure_id_list");

		if (structureIdList != null)
			((VFDocumentFrameBase)activeFrame).loadURL(structureIdList.split(","));
		
		// PR adding surface
		boolean cAlphaFlag = AppBase.getApp().properties.get("cAlphaFlag") != null;
		System.out.println("SimpleViewer: " + cAlphaFlag);
		if (cAlphaFlag) {
			initializeSurface();
		}
	}

	/**
	 * Draws multi-scale surface
	 */
	private void initializeSurface() {
		StructureList s = AppBase.sgetModel().getStructures();
		Structure str = s.get(0);
		SurfaceThread st = new SurfaceThread();
		st.createCAlphaSurface();
		((VFDocumentFrameBase)activeFrame).getGlGeometryViewer().surfaceAdded(str);
	}
}