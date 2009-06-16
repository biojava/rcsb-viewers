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
package org.rcsb.uiApp.controllers.doc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.util.DebugState;
import org.rcsb.mbt.model.util.Status;
import org.rcsb.mbt.structLoader.IFileStructureLoader;
import org.rcsb.mbt.structLoader.IStructureLoader;
import org.rcsb.mbt.structLoader.PdbStructureLoader;
import org.rcsb.mbt.structLoader.XMLStructureLoader;
import org.rcsb.uiApp.controllers.app.AppBase;

public class DocController
{
	private String initialBiologicalUnitId = null; 

	/**
	 * This is used by the StructureXMLHandler - nothing seems to set it, though...
	 * 
	 * @return - initial biological unit identifier.
	 */
	public String getInitialBiologicalUnitId() {
		return this.initialBiologicalUnitId;
	}

	public void setInitialBiologicalUnitId(String initialBiologicalUnitId)
	{
		if(initialBiologicalUnitId != null)
		{
			initialBiologicalUnitId = initialBiologicalUnitId.trim();
			if(initialBiologicalUnitId.length() == 0)
				initialBiologicalUnitId = null;
		}

		this.initialBiologicalUnitId = initialBiologicalUnitId;
	}


	public void loadStructure(final String url, final String pdbId)
	{
		if (url == null)
			; // stick up an error message box?

		AppBase.sgetUpdateController().clear();

		Structure[] structure = readStructuresFromUrl(url);
		if (structure != null)
		{
			if (pdbId != null && structure.length == 1)
				structure[0].getStructureMap().setPdbId(pdbId);

			AppBase.sgetModel().setStructures(structure);
		}
	}

	/**
	 * Given a url, read the structures and return the structures array.
	 * 
	 * @param structureUrlParam - the url to read.  can be a system filename.
	 * @return - an array of Structures
	 */
	public Structure[] readStructuresFromUrl(String structureUrlParam)
	{
		final Vector<Structure> structuresVec = new Vector<Structure>();
		Structure structureTmp = null;

		final String[] datasets = structureUrlParam.split(",");
		for (int i = 0; i < datasets.length; i++)
		{
			try
			{
				datasets[i] = datasets[i].trim();
				final String dataset = datasets[i];
				IFileStructureLoader loader = null;

				if (dataset.endsWith(".xml.gz") || dataset.endsWith(".xml"))
				{
					// long time = System.currentTimeMillis();
					Status.progress(-1, "Reading XML file: " + dataset);

					loader =
						new XMLStructureLoader(AppBase.sgetAppModuleFactory().createStructureXMLHandler(dataset));
					((XMLStructureLoader)loader).setInitialBiologicalUnitId(initialBiologicalUnitId);

					structureTmp = loader.load(dataset);
				}


				else if (dataset.matches("^.+\\.pdb\\d*(\\.gz)?$")
						|| dataset.endsWith(".ent.gz")
						|| dataset.endsWith(".ent"))
				{
					loader = new PdbStructureLoader();
					((PdbStructureLoader)loader).setTreatModelsAsSubunits(
							AppBase.getApp().properties.contains("treat_models_as_subunits") &&
							AppBase.getApp().properties.get("treat_models_as_subunits").equals("true"));

					Status.progress(0, "Reading PDB file: " + dataset);

					structureTmp = loader.load(dataset);
				}

				else
					Status.output(Status.LEVEL_ERROR,
					"Could not open file: the file must have an extension of .xml, xml.gz, .pdb, .pdb.gz, .ent, or .ent.gz.");

				if (structureTmp == null)
				{
					Status.output(Status.LEVEL_ERROR, "Could not load: " + dataset);
					return null;
				}

				else
					System.out.println("Data set loaded: " + dataset);
// PR
//				new StructureMap(structureTmp, AppBase.sgetAppModuleFactory().createStructureMapUserData(),
//						loader.getNonProteinChainIds());
				new StructureMap(structureTmp, AppBase.sgetAppModuleFactory().createStructureMapUserData());
				finalizeNewStructure(loader, structureTmp);

				if (loader.getUnitCell() != null)
					structureTmp.getStructureMap().setUnitCell(loader.getUnitCell());

				structuresVec.add(structureTmp);			
			}

			catch (final MalformedURLException e)
			{
				Status.output(Status.LEVEL_ERROR, "Error: Bad url to the structure xml file.");
			}

			catch (final IOException e)
			{
				Status.output(Status.LEVEL_ERROR, e.getMessage());
			}

		}
		// global transforms set in _BU version...

		// else no structure loaded.

		final Structure[] structures = new Structure[structuresVec.size()];
		for (int i = 0; i < structures.length; i++)
		{
			structures[i] = structuresVec.get(i);
			structures[i].getStructureMap().setPdbId(parsePdbId(datasets[i]));
			structures[i].getStructureMap().setImmutable();
		}

		if (DebugState.isDebug())
			System.err.println("--> DocController: Memory used: " +
					(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));

		return structures;
	}

	/**
	 * Parses the PDB ID from a url string
	 * @param urlString
	 * @return PdbId
	 */
	private String parsePdbId(String urlString) {
		String[] extensions = {".xml", ".xml.gz", ".pdb", ".pdb.gz", ".ent", ".ent.gz", ".pdb1.gz", ".pdb2.gz,", ".pdb3.gz", ".pdb4.gz", ".pdb5.gz"};

		for (String extension: extensions) {
			int endIndex = urlString.indexOf(extension);
			if (endIndex > 0) {
				int beginIndex = Math.max(0, endIndex-4);
				return urlString.substring(beginIndex, endIndex).toUpperCase();
			}
		}
		return "NOID";
	}

	/**
	 * 
	 * @param approveButtonText
	 * @return
	 */
	public File askUserForXmlFile(final String approveButtonText) {
		final JFileChooser chooser = new JFileChooser();
		final FileFilter filter = new FileFilter() {

			@Override
			public boolean accept(File f) {
				if (f.isFile()) {
					return f.getName().endsWith(".xml");
				}
				return true;
			}

			@Override
			public String getDescription() {
				return "XML Files";
			}
		};
		chooser.setFileFilter(filter);

		chooser.showDialog(AppBase.sgetActiveFrame(),
				approveButtonText);

		return chooser.getSelectedFile();
	}

	/**
	 * Hook function to allow subclasses to bless newly created structures read
	 * from the XML handler.
	 * 
	 * Optionally overridden
	 * 
	 * @param handler - the active XML handler
	 * @param structureTmp - the newly created structure
	 */
	public void finalizeNewStructure(IStructureLoader handler, Structure structureTmp)
	{
		StructureMap structureMap = structureTmp.getStructureMap();
		// explicitly create a structureMap for the struc

		if (handler.hasBiologicUnitTransformationMatrices())
		{
			StructureMap.BiologicUnitTransforms bu = structureMap.addBiologicUnitTransforms();
			bu.setBiologicalUnitGenerationMatrices(handler.getBiologicalUnitTransformationMatrices());
		}

		else if (handler.hasNonCrystallographicOperations())
		{
			StructureMap.NonCrystallographicTransforms nc = structureMap.addNonCrystallographicTransforms();
			nc.setNonCrystallographicTranslations(handler.getNonCrystallographicOperations());
		}
	}
}
