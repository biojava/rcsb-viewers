package org.rcsb.ks.controllers.app;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import org.rcsb.mbt.controllers.scene.SceneState;
import org.rcsb.mbt.controllers.scene.ViewMovementThread;
import org.rcsb.ks.glscene.jogl.KSGlGeometryViewer;
import org.rcsb.ks.model.AnnotatedAtom;
import org.rcsb.ks.model.EntityDescriptor;
import org.rcsb.ks.model.KSStructureInfo;
import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.scene.PickController.PickLevel;
import org.rcsb.mbt.glscene.jogl.DisplayListRenderable;
import org.rcsb.mbt.glscene.jogl.JoglSceneNode;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.attributes.AtomColorByRgb;
import org.rcsb.mbt.model.attributes.AtomStyle;
import org.rcsb.mbt.model.attributes.ChainStyle;
import org.rcsb.mbt.model.attributes.IResidueColor;
import org.rcsb.mbt.model.attributes.ResidueColorByRgb;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.util.DebugState;

/*
 * Set up a thread to run a slide show.  Collects all the filenames from a directory
 * specified in a 'screensaver.properties' file, then insures they're retrieved if
 * on the net to a local area, and runs through them, displaying them in various states.
 * 
 * @author rickb
 *
 */
public class SlideShow extends Thread
{
	private boolean slideShow = true;

	private ArrayList<String> pdbIdList = new ArrayList<String>();

	// {{ load in the properties files }}
	private Properties pdbProperties = new Properties();

	private String fileDirectory = ".";

	private boolean threadSuspended = false;

	public SlideShow(String args[]) {
		new KioskViewer(args);
		loadList();
	}

	public void suspendSlideShow(boolean _suspendThread) {
		threadSuspended = _suspendThread;
	}

	public boolean isSlideShowSuspended() {
		return threadSuspended;
	}

	private void loadList() {
		try {
			File pdbPropertiesFile = new File("pdbscreensaver.properties");
			FileInputStream pdbPropertiesInputstream = new FileInputStream(
					pdbPropertiesFile);
			pdbProperties.load(pdbPropertiesInputstream);
			String fileDirectory = pdbProperties.getProperty("pdbFiles");
			setPDBFileDirectory(fileDirectory);

			// {{}}
			File file = new File(fileDirectory + "/list.txt");
			if (file.exists()) {
				FileReader file_reader = new FileReader(file);
				BufferedReader reader = new BufferedReader(file_reader);
				String line = reader.readLine();
				while (line != null) {
					// System.out.println(" the line " + line);
					String pdbId = line.substring(0, 4);
					// pdbIdList.add("http://www.pdb.org/pdb/files/" + pdbId +
					// ".xml.gz");
					pdbIdList.add(pdbId);
					line = reader.readLine();
				}
			} else {
				File moleculeDirectory = new File("" + fileDirectory);
				String[] files = moleculeDirectory.list();
				for (int i = 0; i < files.length; i++) {
					String name = files[i];
					if (name.matches("^[A-Za-z0-9]{4}.xml.gz$")) {
						// determine the pdb id
						name = name.substring(0, 4);
						pdbIdList.add(name);
					}
				}
			}
			startPreemptiveListLoadingThread();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setPDBFileDirectory(String _fileDirectory) {
		fileDirectory = _fileDirectory;
	}

	public String getPDBFileDirectory() {
		return fileDirectory;
	}

	/*
	 * Make sure files are local somewhere.  The only reason I can see this for being in
	 * it's own thread is that it can download files (if it has to) in background while the
	 * viewer can get started.
	 * 
	 * 28-Oct-08 - rickb
	 */
	public void startPreemptiveListLoadingThread()
	{
		Thread t = new Thread() {
			@Override
			public void run() {

				int failureIndex = 0;

				File f = new File("" + getPDBFileDirectory());
				String[] ids = f.list();

				// {{ start loading the pdb files }}
				for (int i = 0; i < pdbIdList.size(); i++) {
					String pdbidvalue = pdbIdList.get(i);
					pdbidvalue = pdbidvalue.toLowerCase();
					boolean isLocal = false;

					for (int j = 0; j < ids.length; j++) {
						String temp = ids[j].toLowerCase();
						if (temp.startsWith(pdbidvalue)) {
							isLocal = true;
						}
					}

					// {{ if its not local... get it }}
					if (!isLocal) {
						String url = "http://www.pdb.org/pdb/files/"
								+ pdbidvalue + ".xml.gz";
						try {
							URL fileurl = new URL(url);
							// System.out.println(" loading : " + pdbidvalue);
							URLConnection connection = fileurl.openConnection();
							InputStream stream = connection.getInputStream();
							BufferedInputStream in = new BufferedInputStream(
									stream);
							FileOutputStream file = new FileOutputStream(
									getPDBFileDirectory() + "/" + pdbidvalue
											+ ".xml.gz");
							BufferedOutputStream out = new BufferedOutputStream(
									file);
							int iy;
							while ((iy = in.read()) != -1) {
								out.write(iy);
							}
							out.flush();
						} catch (Exception e) {
							e.printStackTrace();
							failureIndex++;
							if (failureIndex >= 5)
								return;
						}
					}
				}
			}
		};
		t.start();
	}

	/*
	 * Trigger the slideshow.
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		if (pdbIdList.size() <= 0) {
			File local = new File("" + getPDBFileDirectory());
			String[] files = local.list();
			if (pdbIdList.size() == 0) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].endsWith(".gz")) {
						pdbIdList.add(files[i]);
					}
				}
			}
		}
		int index = 0;
		int movementDuration = 5000;  // milliseconds
		int pause = 1000;
		
		ViewMovementThread.setDuration(movementDuration);

		StructureModel model = AppBase.sgetModel();

		while (slideShow)
		{
			try
			{
				loadStructure(index);
				if (!model.hasStructures())
				{
					index++;
					continue;
				}
			}
			
			catch (Exception e)
			{
				System.err.println("Failed at structure index : " + index);
				e.printStackTrace();
				index++;
				int size = pdbIdList.size();
				if (index >= size)
					index = 0;

				continue;
			}
			
			KSGlGeometryViewer gviewer = KioskViewer.sgetGlGeometryViewer();
			KioskViewer.sgetActiveFrame().updateStructure(model.getStructures().get(0));

			// {{ check the size of the structure. If it is too big then we will
			// punt }}
			Structure structure = model.getStructures().get(0);
			System.out.println(" atom count : "
					+ structure.getStructureMap().getAtomCount());

			// double[] ccc = model.getStructures()[0].getStructureMap()
			// .getAtomCoordinateAverage();
			while (model == null) {
				try {
					sleep(100L);
				} catch (Exception _e) {

				}
				System.err.println(" failed... failed ... failed ");
			}
			gviewer.suspendAllPainting();
			ArrayList<SceneState> states = generateStates();
			gviewer.resumePainting();

			for (SceneState state : states)
			{
				KioskViewer.sgetActiveFrame().updateState(model.getStructures().get(0), state);

 				state.enact();

 				if (DebugState.isDebug())
 					System.out.println ( "Slideshow awake: enacting " + state.toString());
				// {{ check to make sure the thread is not suspended by calling
				// suspendThread }}
				synchronized (this)
				{
					while (threadSuspended) {
						try
						{
							wait();
						}
						
						catch (InterruptedException _ie)
						{
							if (DebugState.isDebug())
								System.err.println("Slideshow interrupted.");
						}
					}
				}

/* **/
				try
				{
					sleep(movementDuration + pause);
								// ok - it triggers the state change, which then animates.
								// when this times out, it goes to the next state, regardless of
								// whether the animation is complete or not...
				}
				
				catch (Exception _e)
				{
					if (DebugState.isDebug())
						System.err.println("Exception: Slideshow waken up from sleep?");
				}
/* **/
				ViewMovementThread.terminateMovementThread();
			}

			// System.out.println(" \n\n Loading the structure : "
			// + pdbIdList.get(index));
			index++;
			int size = pdbIdList.size();
			if (index >= size) {
				index = 0;
			}
		}
	}

	private void loadStructure(int _index) throws Exception
	{
		String pdbId = pdbIdList.get(_index);
		try {
			File lk = new File(getPDBFileDirectory() + "/" + pdbId + ".xml.gz");

			System.out.println(" file " + lk.getAbsolutePath());
			if (lk.exists())
				loadFromFile(lk, pdbId);
			else
				loadFromURL(pdbId);
		}
		
		catch (Exception _e)
		{
			// System.exit( 1 );
			throw new Exception("pdb file index " + _index + " id : " + pdbId
					+ " is not found ");
		}
	}

	private void loadFromFile(File lk, String _id)
	{
		AppBase.sgetDocController().loadStructure(lk.getAbsolutePath(), _id);
	}

	private void loadFromURL(String _id)
	{
		String url = "http://www.pdb.org/pdb/files/" + _id + ".xml.gz";
		AppBase.sgetDocController().loadStructure(url, _id);
	}

/* **
	private StateAnnotationPanel createStateAnnotationPanel() {
		return new StateAnnotationPanel();
	}

	private StructureIdPanel createStuctureIdPanel() {
		StructureIdPanel structureIdPanel = new StructureIdPanel();
		return structureIdPanel;
	}

	private ArrayList generateSlowStates()
	{
		ArrayList list = new ArrayList();
		StructureModel model = AppBase.sgetModel();
		KSGlGeometryViewer gl = KioskViewer.sgetGlGeometryViewer();
		double[] pivot = model.getStructures().get(0).getStructureMap().getAtomCoordinateAverage();
		generateState(pivot, list, "");
		return list;
	}
* **/

	private ArrayList<SceneState> generateStates()
	{

		ArrayList<SceneState> statelist = new ArrayList<SceneState>();
		StructureModel model = AppBase.sgetModel();
		KSGlGeometryViewer gl = KioskViewer.sgetGlGeometryViewer();
		double[] pivot = model.getStructures().get(0).getStructureMap()
				.getAtomCoordinateAverage();
		
		generateState(pivot, statelist, "");
		
/* **
// uncomment if you want only the pivot states generated.
		if (DebugState.isDebug())
			return(statelist);
* **/

		KSStructureInfo structureInfo = (KSStructureInfo)model.getStructures().get(0).getStructureInfo();

		StructureMap sm = model.getStructures().get(0).getStructureMap();
		int ligandCount = sm.getLigandCount();
		if (ligandCount <= 4)
		{
			for (int j = 0; j < ligandCount; j++) {
				Residue r = sm.getLigandResidue(j);
				if (!r.getCompoundCode().equalsIgnoreCase("HOH")) {
					if (r.getAtoms().size() > 5) {
						Atom atom = r.getAtom(0);
						double c[] = atom.coordinate;
						double[] eye = new double[3];
						eye[0] = c[0] - 20;
						eye[1] = c[1] - 20;
						eye[2] = c[2] - 20;
						SceneState viewerState = new SceneState();

						// EntityDescriptor ed = structureInfo.getDescriptor( r
						// );
						String descr = "nope!";
						Atom aatom = r.getAlphaAtom();
						if (aatom instanceof AnnotatedAtom) {
							AnnotatedAtom aa = (AnnotatedAtom) atom;
							descr = aa.getEntityId();
							EntityDescriptor ee = structureInfo.getDescriptor(descr);
							if (ee != null)
								descr = ee.getDescription();
						}

						generateState(c, 40, statelist, " " + descr);
						// generateState(model, c, 40, statelist, " "
						// + r.getCompoundCode());
						gl.lookAt(eye, c);
						viewerState.captureCurrentState("");
						statelist.add(viewerState);
					}
				}
			}
		}
		
		else
		{
			SceneState orig = new SceneState();
			orig.captureCurrentState("");
			statelist.add(orig);
			for (int j = 0; j < ligandCount; j++)
			{
				Residue r = sm.getLigandResidue(j);

				if (!r.getCompoundCode().equalsIgnoreCase("HOH")) {
					if (r.getAtoms().size() > 5) {
						Atom atom = r.getAtom(0);
						double c[] = atom.coordinate;
						double[] eye = new double[3];
						eye[0] = c[0] - 20;
						eye[1] = c[1] - 20;
						eye[2] = c[2] - 20;
						String descr = "nope!";
						Atom aatom = r.getAlphaAtom();
						if (aatom instanceof AnnotatedAtom) {
							AnnotatedAtom aa = (AnnotatedAtom) atom;
							descr = aa.getEntityId();
							EntityDescriptor ee = structureInfo
									.getDescriptor(descr);
							if (ee != null)
								descr = ee.getDescription();

						}
						SceneState viewerState = new SceneState();
						generateOscilatingState(c, 40, statelist, descr);
						gl.lookAt(eye, c);

						viewerState.captureCurrentState("" + descr);
						statelist.add(viewerState);

						// ViewerState viewerState = new ViewerState();
						// GlGeometryViewer viewer = model.getViewer();
						// viewer.lookAt(eye, c);
						// viewerState.captureCurrentState("booyaa" + j
						// + r.getCompoundCode());
						statelist.add(viewerState);
						statelist.add(orig);
					}
				}
			}
		}

		int chainCount = sm.getChainCount();
		for (int i = 0; i < chainCount; i++) {
			double colorv = Math.random();

			float[] colorf = new float[3];
			colorf[0] = (float) colorv;
			colorf[1] = 0.0f;
			colorf[2] = 1.0f;
			Chain c = sm.getChain(i);
			changeColor(c, colorf);
		}

		SceneState viewerState1 = new SceneState();
		try {
			// viewer.requestRepaint();
			viewerState1.captureCurrentState("");
			statelist.add(viewerState1);
		} catch (Exception _ee) {
			_ee.printStackTrace();
		}
		double coords[] = sm.getAtomCoordinateAverage();
		double[] eye = new double[3];
		eye[0] = coords[0] - 20;
		eye[1] = coords[1] - 20;
		eye[2] = coords[2] - 20;

		generateOscilatingState(coords, 140, statelist, "");
		SceneState viewerState = new SceneState();
		try {
			gl.lookAt(eye, coords);
			viewerState.captureCurrentState("");
			statelist.add(viewerState);
		} catch (Exception _ee) {
			_ee.printStackTrace();
		}
		return statelist;
	}

	private void changeColor(Chain c, float[] _color) {
		int resCount = c.getResidueCount();
		for (int i = 0; i < resCount; i++) {
			Residue r = c.getResidue(i);
			changeColor(r, _color);
		}
	}

	private void changeColor(Residue r, float[] _color) {
		Structure struc = r.structure;
		StructureMap sm = struc.getStructureMap();
		StructureStyles ss = sm.getStructureStyles();

		PickLevel pickLevel = AppBase.sgetPickController().getPickLevel();
		switch (pickLevel)
		{
		case ATOMS:
			Vector<Atom> atoms = r.getAtoms();

			for (Atom a : atoms)
				this.changeColor(a, _color);

			for (Bond b : sm.getBonds(atoms))
				this.changeColor(b, _color);
			break;
			
		default:
		case RIBBONS:
			Chain c = sm.getChain(r.getChainId());
			
			DisplayListRenderable renderable = ((JoglSceneNode)sm.getUData()).getRenderable(c);
			if (renderable != null) {
				// this.options.getCurrentColor().getColorComponents(colorFl);

				ChainStyle style = (ChainStyle) ss.getStyle(c);
				IResidueColor residueColor = style.getResidueColor();
				ResidueColorByRgb residueColorByRgb = null;
				if (residueColor != null
						&& residueColor instanceof ResidueColorByRgb) {
					residueColorByRgb = (ResidueColorByRgb) residueColor;
				} else {
					residueColorByRgb = new ResidueColorByRgb(residueColor);
					style = new ChainStyle();
					style.setResidueColor(residueColorByRgb);
					ss.setStyle(c, style);
				}
				residueColorByRgb.setColor(r, _color);

				// for(int i = 0; i < renderable.arrayLists.length; i++) {
				// if(renderable.arrayLists[i] != null) {
				// renderable.arrayLists[i].setEntityColor(r, colorFl);
				// }
				// }

				renderable.style = style;
				// renderable.setDirty();
			}
			break;
		}
	}

	private void changeColor(Atom a, float[] _color) {
		Structure struc = a.structure;
		StructureMap sm = struc.getStructureMap();
		StructureStyles ss = sm.getStructureStyles();
		JoglSceneNode sn = (JoglSceneNode)sm.getUData();

		PickLevel pickLevel = AppBase.sgetPickController().getPickLevel();
		switch (pickLevel)
		{
		case ATOMS:
			// this.options.getCurrentColor().getColorComponents(colorFl);

			DisplayListRenderable renderable = sn.getRenderable(a);
			if (renderable != null) {
				AtomStyle oldStyle = (AtomStyle) renderable.style;
				AtomStyle style = new AtomStyle();
				style.setAtomColor(new AtomColorByRgb(_color));
				if (oldStyle != null) {
					style.setAtomLabel(oldStyle.getAtomLabel());
					style.setAtomRadius(oldStyle.getAtomRadius());
				}
				ss.setStyle(a, style);

				renderable.style = style;
				// renderable.setDirty();
			}
			break;
			
		case RIBBONS:
			Residue r = sm.getResidue(a);
			changeColor(r.getFragment(), _color);
			break;
			
		default:
            (new Exception("Invalid option: " + pickLevel)).printStackTrace();            
		}
	}

	private void changeColor(Bond b, float[] _color)
	{
		Structure struc = b.structure;
		StructureMap sm = struc.getStructureMap();

		PickLevel pickLevel = AppBase.sgetPickController().getPickLevel();
		switch (pickLevel)
		{
		case ATOMS:
			// delegate to atoms...
			this.changeColor(b.getAtom(0), _color);
			this.changeColor(b.getAtom(1), _color);
			break;
			
		case RIBBONS:
			// even if this is between fragments, just change one of them
			// arbitrarily.
			Residue r = sm.getResidue(b.getAtom(0));
			this.changeColor(r.getFragment(), _color);
			break;
			
		default:
            (new Exception("Invalid option: " + pickLevel)).printStackTrace();            
		}
	}

	private void changeColor(Fragment f, float[] _color) {
		for (int i = 0; i < f.getResidueCount(); i++) {
			Residue r = f.getResidue(i);
			changeColor(r, _color);
		}
	}


	private void generateOscilatingState(double[] pivot,
			double _distance, ArrayList<SceneState> _list, String _title)
	{
		double[] c = pivot;
		double[] eye = new double[3];
		// double[] centerPoint = new double[3];
		eye[0] = c[0] - 120;
		eye[1] = c[1] - 120;
		eye[2] = c[2] - 120;

		double distance = _distance;// Math.sqrt(eye[0]*eye[0] +
		// eye[1]*eye[1] +
		// eye[2]*eye[2]);
		double phi = 0;
		double theta = Math.PI;
		double x = (distance * Math.sin(theta)) * Math.cos(phi);
		double y = (distance * Math.sin(theta)) * Math.sin(phi);
		double z = (distance * Math.cos(theta));

		for (double i = 0; i < 120.0; i += 20) {
			theta = Math.toRadians(i);
			x = (distance * Math.sin(theta)) * Math.cos(phi);
			y = (distance * Math.sin(theta)) * Math.sin(phi);
			z = (distance * Math.cos(theta));
			SceneState state = new SceneState();

			if (i > 90) {
				y *= (-1);
			} else if (i > 180) {
				y *= (-1);
				x *= (-1);
			} else if (i > 270) {
				x *= (-1);
			} else {

			}
			eye[0] = c[0] - x;
			eye[1] = c[1] - y;
			eye[2] = c[2] - z;

			KioskViewer.sgetGlGeometryViewer().lookAt(eye, c);

			try {
				state.captureCurrentState("" + _title);
				_list.add(state);
			} catch (Exception _ee) {
				_ee.printStackTrace();
			}
		}
	}

	private void generateState(double[] pivot, ArrayList<SceneState> _list,
			String _title) {
		// distance = sqrt( x*x + y*y + z*z )
		// x /= distance;
		// y /= distance;
		// z /= distance;
		//
		// xz_dist = sqrt( x*x + z*z )
		// latitude = atan2( xz_dist, y ) * RADIANS
		// longitude = atan2( x, z ) * RADIANS

//		double radius = 90; // back about 10 angstroms
		double[] c = pivot;
		double[] eye = new double[3];
		// double[] centerPoint = new double[3];
		eye[0] = c[0] - 120;
		eye[1] = c[1] - 120;
		eye[2] = c[2] - 120;

		double distance = 150;// Math.sqrt(eye[0]*eye[0] + eye[1]*eye[1] +
		// eye[2]*eye[2]);
		double phi = 0;
		double theta = Math.PI;
		double x = (distance * Math.sin(theta)) * Math.cos(phi);
		double y = (distance * Math.sin(theta)) * Math.sin(phi);
		double z = (distance * Math.cos(theta));

		for (double i = 0.0; i < 360.0; i += 120.0)
		{
			theta = Math.toRadians(i);
			x = (distance * Math.sin(theta)) * Math.cos(phi);
			y = (distance * Math.sin(theta)) * Math.sin(phi);
			z = (distance * Math.cos(theta));
			SceneState state = new SceneState();

			if (i > 90) {
				y *= (-1);
			} else if (i > 180) {
				y *= (-1);
				x *= (-1);
			} else if (i > 270) {
				x *= (-1);
			} else {

			}
			eye[0] = c[0] - x;
			eye[1] = c[1] - y;
			eye[2] = c[2] - z;

			KioskViewer.sgetGlGeometryViewer().lookAt(eye, c);
			try {
				state.captureCurrentState(_title);
				_list.add(state);
			}
			
			catch (Exception _ee)
			{
				if (DebugState.isDebug())
					System.err.println("Exception: Error trying to add pivot state to state list.");
			}
			
/* ** 
// XXX_DEBUG - uncomment if you want to return after single state generated.
			if (DebugState.isDebug())
				return;
* **/
		}
	}

	private void generateState(double[] pivot, double _distance,
			ArrayList<SceneState> _list, String _title) {
		// distance = sqrt( x*x + y*y + z*z )
		// x /= distance;
		// y /= distance;
		// z /= distance;
		//
		// xz_dist = sqrt( x*x + z*z )
		// latitude = atan2( xz_dist, y ) * RADIANS
		// longitude = atan2( x, z ) * RADIANS

		double[] c = pivot;
		double[] eye = new double[3];
		// double[] centerPoint = new double[3];
		eye[0] = c[0] - 120;
		eye[1] = c[1] - 120;
		eye[2] = c[2] - 120;

		double distance = _distance;// Math.sqrt(eye[0]*eye[0] + eye[1]*eye[1] +
		// eye[2]*eye[2]);
		double phi = 0;
		double theta = Math.PI;
		double x = (distance * Math.sin(theta)) * Math.cos(phi);
		double y = (distance * Math.sin(theta)) * Math.sin(phi);
		double z = (distance * Math.cos(theta));

		for (double i = 0; i < 360.0; i += 80) {
			theta = Math.toRadians(i);
			x = (distance * Math.sin(theta)) * Math.cos(phi);
			y = (distance * Math.sin(theta)) * Math.sin(phi);
			z = (distance * Math.cos(theta));
			SceneState state = new SceneState();

			if (i > 90) {
				y *= (-1);
			} else if (i > 180) {
				y *= (-1);
				x *= (-1);
			} else if (i > 270) {
				x *= (-1);
			} else {

			}
			eye[0] = c[0] - x;
			eye[1] = c[1] - y;
			eye[2] = c[2] - z;

			KioskViewer.sgetGlGeometryViewer().lookAt(eye, c);
			try {
				state.captureCurrentState("" + _title);
				_list.add(state);
			} catch (Exception _ee) {
			}
		}
	}
}