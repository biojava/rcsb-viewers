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
package org.rcsb.uiApp.controllers.app;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Properties;

import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.util.DebugState;
import org.rcsb.mbt.structLoader.StructureXMLHandler;
import org.rcsb.uiApp.controllers.doc.DocController;
import org.rcsb.uiApp.controllers.update.UpdateController;
import org.rcsb.uiApp.model.UIAppStructureModel;
import org.rcsb.uiApp.ui.mainframe.DocumentFrameBase;


public abstract class AppBase
{
	/**
	 * Derived app should instantiate this so that the app closes when the
	 * main window closes.
	 * @author rickb
	 *
	 */
	public class ViewerCloserListener extends WindowAdapter
	{
		private boolean isApplication;

		public ViewerCloserListener(final boolean isApplication)
		{
			this.isApplication = isApplication;
		}

		@Override
		public void windowClosing(final WindowEvent event)
		{
			if (this.isApplication)
			{
				System.exit(0); // make sure the application terminates
				// completely.
			}
		}
	}
	
	/**
	 * The application needs to be able to derive certain architectural components, but their
	 * creation and destruction needs to be maintained by the framework.  Thus, all creation
	 * requests for derivable classes route through this subclass.
	 * 
	 * You have to derive from it in your App class, and you have to override CreateDocFrame,
	 * since you will at least have a custom app class, and a custom document frame.
	 * 
	 * Derive from this class, override the factory methods to create a derived version, but
	 * return the base version, and create an instance of the derived class to the appModuleFactory member.
	 * 
	 * The rest of the framework should be good to go, provided you've done your derivations and
	 * overriding correctly.
	 * 
	 * You can add whatever you need, here (or in the derived version), and then provide an accessor or call
	 * the create method, directly, as you need.
	 * 
	 * Every derivable class is created here - keeps the implementations from getting scattered throughout
	 * the app.
	 * 
	 * Resource conservation tip (still investigating):
	 * ------------------------------------------------
	 * If you are creating an optional module, use reflection to create it, rather than calling the
	 * constructor, here - keeps the class/module from getting loaded until it's needed.  If it's
	 * required, just go ahead and call the constructor
	 * 
	 * @author rickb
	 *
	 */
	public abstract class AppModuleFactory
	{
		/**
		 * DocumentFrameBase <em>has</em> to be derived.  The rest *may* be derived
		 */
		public abstract DocumentFrameBase createDocFrame(final String name, URL iconUrl);
		public DocController createDocController() { return new DocController(); }
		public UpdateController createUpdateController() { return new UpdateController(); }
		public StructureModel createModel() { return new UIAppStructureModel(); }
		public StructureXMLHandler createStructureXMLHandler(String dataset) { return new StructureXMLHandler(dataset); }
		public Object createStructureMapUserData() { return null; }

	}

	/**
	 * Convenience access functions.
	 * The app can get to any of the controllers, the scene, or the model via these accessors (and anything can get
	 * the app instance.)
	 * 
	 * These are static accessors that get the relevant pieces through the active frame (in this case, the mainFrame.)
	 * 
	 * Override these to get derived versions (means you have to override JAppBase, of course.)
	 * 
	 * @see org.rcsb.uiApp.ui.mainframe.DocumentFrameBase
	 * @see org.rcsb.mbt.model.StructureModel
	 * @see org.rcsb.mbt.appController.ViewsController
	 * @see org.rcsb.mbt.appController.UpdateViewController
	 * @see org.rcsb.mbt.appController.DocController
	 * @see org.rcsb.mbt.controllers.scene.appController.SceneController
	 */
	public static AppModuleFactory sgetAppModuleFactory() { return _theJApp.appModuleFactory; }
	public static DocumentFrameBase sgetActiveFrame() { return _theJApp.activeFrame; }
	public static UpdateController sgetUpdateController() { return sgetActiveFrame().getUpdateController(); }
	public static DocController sgetDocController() { return sgetActiveFrame().getDocController(); }
	public static UIAppStructureModel sgetModel() { return sgetActiveFrame().getModel(); }
	public DocumentFrameBase getActiveFrame() { return activeFrame; }

	
	// Runtime properties (options or parameters)
	public Properties properties = new Properties();
	
	/**
	 * The singleton app
	 */
	protected static AppBase _theJApp = null;
	public static AppBase getApp() { return _theJApp; }

    /**
     * The singleton document (main) frame.
     */
	protected DocumentFrameBase activeFrame = null;
	
	/**
	 * The singleton module factory
	 */
	protected AppModuleFactory appModuleFactory = null;

	protected boolean _isApplication = false;
	public boolean isApplication() { return _isApplication; }

	public static boolean backgroundScreenshotOnly = false;
	
	protected boolean _allowShaders = false;
	public boolean allowShaders() { return _allowShaders; }


	/**
	 * Default constructor - calls the super and then sets the app singleton.
	 */
	public AppBase()
	{
		super();
		_theJApp = this;
	}
	
	public AppBase(String args[])
	{
		super();
		
		if (args != null)
			for (int i = 0; i < args.length; i++)
			{
				if (args[i].equals("-debug"))
					DebugState.setDebugState(true);
				else if (args[i].equals("-treat_models_as_subunits"))
					properties.setProperty("treat_models_as_subunits", "true");
				
				if (args[i].equals("-trace"))
					DebugState.setTraceState(true);
			}
		
		_theJApp = this;
		
		if (DebugState.isDebug())
			System.err.println("-->AppBase - Free memory: " +
					Runtime.getRuntime().freeMemory());
	}
}
