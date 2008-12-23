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
package org.rcsb.sv.controllers.app;

import java.net.URL;

import org.rcsb.sv.ui.SVDocumentFrame;
import org.rcsb.uiApp.ui.mainframe.DocumentFrameBase;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;
import org.rcsb.vf.ui.VFDocumentFrameBase;

/**
 * Viewer without a frame or any UI other than the menu.
 * 
 * @author rickb
 */
public class SimpleViewer extends VFAppBase
{
	{
	}
	
	public class SVAppModuleFactory extends VFAppBase.VFAppModuleFactory
	{

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createGlGeometryViewer()
		 */
		@Override
		public GlGeometryViewer createGlGeometryViewer() { return new GlGeometryViewer(); }

		/* (non-Javadoc)
		 * @see edu.sdsc.mbt.app_controller.JAppBase.AppModuleFactory#createDocFrame()
		 */
		@Override
		public DocumentFrameBase createDocFrame(final String title, URL iconUrl)
		    { activeFrame =  new SVDocumentFrame(title, iconUrl); return activeFrame; }
		
	}
	
	/**
	 * Accessor overrides
	 * @return
	 */
	public static SimpleViewer getApp() { return (SimpleViewer)_theJApp; }
	@Override
	public SVDocumentFrame getActiveFrame() { return (SVDocumentFrame)super.getActiveFrame(); }
	public static SVDocumentFrame sgetActiveFrame() { return getApp().getActiveFrame(); }

	
	/**
	 * Constructor
	 * 
	 * @param args - the commandline args passed in to the main() call
	 */
	public SimpleViewer(final String[] args)
	{
		super(args);
	}

	public static void main(final String[] args)
	{
		final SimpleViewer app = new SimpleViewer(args);
		app.initialize(true, true);
	}

	public void initialize(final boolean isApplication, final boolean showFrame)
	{
		appModuleFactory = new SVAppModuleFactory();
		activeFrame = new SVDocumentFrame("PDB SimpleViewer (Powered by the MBT)",
										  SimpleViewer.class.getResource("images/icon_128_SV.png"));

		super.initialize(isApplication);
		
		activeFrame.initialize(true);
	
		final String structureUrlParam = this.properties.getProperty("structure_url");
	
		if (structureUrlParam != null)
			((VFDocumentFrameBase)activeFrame).loadURL(structureUrlParam);
	}
}
