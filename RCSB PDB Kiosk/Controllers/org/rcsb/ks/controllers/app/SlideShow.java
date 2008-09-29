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
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import org.rcsb.ks.controllers.app.KSState;
import org.rcsb.ks.glscene.jogl.KSGlGeometryViewer;
import org.rcsb.ks.model.AnnotatedAtom;
import org.rcsb.ks.model.EntityDescriptor;
import org.rcsb.ks.model.KSStructureInfo;
import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.scene.PickLevel;
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

public class SlideShow extends Thread {
	private boolean slideShow = true;

	private ArrayList<String> pdbIdList = new ArrayList<String>();

	// {{ load in the properties files }}
	private Properties pdbProperties = new Properties();

	private String fileDirectory = ".";

	private boolean threadSuspended = false;

	public SlideShow() {
		new KioskViewer();
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
			// TODO: handle exceptio
			e.printStackTrace();
		}
	}

	private void setPDBFileDirectory(String _fileDirectory) {
		fileDirectory = _fileDirectory;
	}

	public String getPDBFileDirectory() {
		return fileDirectory;
	}

	public void startPreemptiveListLoadingThread() {
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
							// TODO: handle exception
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
			
////			if (true) break;  // XXX TEMPORARY - DEBUGGING

			// {{ check the size of the structure. If it is too big then we will
			// punt }}
			Structure structure = model.getStructures().get(0);
			System.out.println(" atom count : "
					+ structure.getStructureMap().getAtomCount());

			// double[] ccc = model.getStructures()[0].getStructureMap()
			// .getAtomCoordinateAverage();
			while (model == null) {
				try {
					sleep(2000l);
				} catch (Exception _e) {

				}
				System.err.println(" failed... failed ... failed ");
			}
			gviewer.suspendAllPainting();
			ArrayList states = generateStates();
			gviewer.resumePainting();

			for (Iterator iter = states.iterator(); iter.hasNext();)
			{
				KSState element = (KSState) iter.next();
				KioskViewer.sgetActiveFrame().updateState(model.getStructures().get(0), element);

 				element.enact();

				System.out.println ( " enacting " + element.toString());
				// {{ check to make sure the thread is not suspended by calling
				// suspendThread }}
				synchronized (this) {
					while (threadSuspended) {
						try {
							wait();
						} catch (InterruptedException _ie) {
						}
					}
				}

				try {
					sleep(10000l);

				} catch (Exception _e) {
				}

			}

			// System.out.println(" \n\n Loading the structure : "
			// + pdbIdList.get(index));
			index++;
			int size = pdbIdList.size();
			if (index >= size) {
				index = 0;
			}
			
// XXX	KioskViewer.clearMemory();
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

	private ArrayList generateStates() {

		ArrayList statelist = new ArrayList();
		StructureModel model = AppBase.sgetModel();
		KSGlGeometryViewer gl = KioskViewer.sgetGlGeometryViewer();
		double[] pivot = model.getStructures().get(0).getStructureMap()
				.getAtomCoordinateAverage();
		
		generateState(pivot, statelist, "");

		// {{ }}
		StructureMap map = model.getStructures().get(0).getStructureMap();

//		System.out.println(" the structure class "
//				+ model.getStructures()[0].getClass().toString());

		Vector chains = map.getChains();
		KSStructureInfo structureInfo = (KSStructureInfo)model.getStructures().get(0).getStructureInfo();
		ArrayList ligands = structureInfo.getDescriptors();

		StructureMap structureMap = model.getStructures().get(0).getStructureMap();
		int ligandCount = structureMap.getLigandCount();
		if (ligandCount <= 4) {
			for (int j = 0; j < ligandCount; j++) {
				Residue r = structureMap.getLigandResidue(j);
				if (!r.getCompoundCode().equalsIgnoreCase("HOH")) {
					if (r.getAtoms().size() > 5) {
						Atom atom = r.getAtom(0);
						double c[] = atom.coordinate;
						double[] eye = new double[3];
						eye[0] = c[0] - 20;
						eye[1] = c[1] - 20;
						eye[2] = c[2] - 20;
						KSState viewerState = new KSState();

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
			KSState orig = new KSState();
			orig.captureCurrentState("");
			statelist.add(orig);
			for (int j = 0; j < ligandCount; j++) {
				Residue r = structureMap.getLigandResidue(j);

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
						KSState viewerState = new KSState();
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

		Structure s = model.getStructures().get(0);
		StructureMap sm = s.getStructureMap();
		StructureStyles ss = sm.getStructureStyles();

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

		KSState viewerState1 = new KSState();
		try {
			// viewer.requestRepaint();
			viewerState1.captureCurrentState("");
			statelist.add(viewerState1);
		} catch (Exception _ee) {
			_ee.printStackTrace();
		}
		double coords[] = structureMap.getAtomCoordinateAverage();
		double[] eye = new double[3];
		eye[0] = coords[0] - 20;
		eye[1] = coords[1] - 20;
		eye[2] = coords[2] - 20;

		generateOscilatingState(coords, 140, statelist, "");
		KSState viewerState = new KSState();
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

		switch (PickLevel.pickLevel) {
		case PickLevel.COMPONENTS_ATOMS_BONDS:
			Vector atoms = r.getAtoms();

			Iterator atomIt = atoms.iterator();
			while (atomIt.hasNext()) {
				Atom a = (Atom) atomIt.next();
				this.changeColor(a, _color);
			}

			Vector bonds = sm.getBonds(atoms);
			Iterator bondsIt = bonds.iterator();
			while (bondsIt.hasNext()) {
				Bond b = (Bond) bondsIt.next();
				this.changeColor(b, _color);
			}
			break;
		default:
		case PickLevel.COMPONENTS_RIBBONS:
			Chain c = sm.getChain(r.getChainId());
			
			KSGlGeometryViewer viewer = KioskViewer.sgetGlGeometryViewer();
			DisplayListRenderable renderable = sm.getSceneNode().getRenderable(c);
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
		JoglSceneNode sn = sm.getSceneNode();

		switch (PickLevel.pickLevel)
		{
		case PickLevel.COMPONENTS_ATOMS_BONDS:
			// this.options.getCurrentColor().getColorComponents(colorFl);

			KSGlGeometryViewer viewer = KioskViewer.sgetGlGeometryViewer();
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
			
		case PickLevel.COMPONENTS_RIBBONS:
			Residue r = sm.getResidue(a);
			changeColor(r.getFragment(), _color);
			break;
			
		default:
			(new Exception()).printStackTrace();
		}
	}

	private void changeColor(Bond b, float[] _color)
	{
		Structure struc = b.structure;
		StructureMap sm = struc.getStructureMap();
		StructureStyles ss = sm.getStructureStyles();

		switch (PickLevel.pickLevel)
		{
		case PickLevel.COMPONENTS_ATOMS_BONDS:
			// delegate to atoms...
			this.changeColor(b.getAtom(0), _color);
			this.changeColor(b.getAtom(1), _color);
			break;
			
		case PickLevel.COMPONENTS_RIBBONS:
			// even if this is between fragments, just change one of them
			// arbitrarily.
			Residue r = sm.getResidue(b.getAtom(0));
			this.changeColor(r.getFragment(), _color);
			break;
			
		default:
			(new Exception()).printStackTrace();
		}
	}

	private void changeColor(Fragment f, float[] _color) {
		for (int i = 0; i < f.getResidueCount(); i++) {
			Residue r = f.getResidue(i);
			changeColor(r, _color);
		}
	}


	private void generateOscilatingState(double[] pivot,
			double _distance, ArrayList _list, String _title)
	{
		double radius = 10; // back about 10 angstroms
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
			KSState state = new KSState();

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

	private void generateState(Chain _c, Residue _r, ArrayList _list)
	{
		double radius = 10; // back about 10 angstroms
		Atom a = _r.getAtom(0);
		double[] c = a.coordinate;
		double[] eye = new double[3];
		// double[] centerPoint = new double[3];
		eye[0] = c[0] - 5;
		eye[1] = c[1] - 5;
		eye[2] = c[2] - 5;

		double distance = 10;// Math.sqrt(eye[0]*eye[0] + eye[1]*eye[1] +
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
			KSState state = new KSState();

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
			state.captureCurrentState("Residue View: " + _r.getCompoundCode());
			_list.add(state);
		}
	}

	private void generateState(double[] pivot, ArrayList _list,
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

		for (double i = 0; i < 360.0; i += 120) {
			theta = Math.toRadians(i);
			x = (distance * Math.sin(theta)) * Math.cos(phi);
			y = (distance * Math.sin(theta)) * Math.sin(phi);
			z = (distance * Math.cos(theta));
			KSState state = new KSState();

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
			} catch (Exception _ee) {
			}
		}
	}

	private void generateState(double[] pivot, double _distance,
			ArrayList _list, String _title) {
		// distance = sqrt( x*x + y*y + z*z )
		// x /= distance;
		// y /= distance;
		// z /= distance;
		//
		// xz_dist = sqrt( x*x + z*z )
		// latitude = atan2( xz_dist, y ) * RADIANS
		// longitude = atan2( x, z ) * RADIANS

		double radius = 10; // back about 10 angstroms
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
			KSState state = new KSState();

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