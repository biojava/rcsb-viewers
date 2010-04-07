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
package org.rcsb.mbt.structLoader;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.geometry.ModelTransformationList;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.UnitCell;
import org.rcsb.mbt.model.geometry.ModelTransformationMatrix;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Invoked by the SAX while parsing the XML file.
 * 
 * This class is deriveable. See 'RCSB PDB Kiosk' for an example of how it's
 * done.
 * 
 * 
 * <h3>Further Info</h3> This class was originally three separate classes in
 * three different viewers with a huge amount of overlap. I've consolidated them
 * down into this base class, which does most everything, and one derived for
 * the Kiosk viewer that:
 * <ol>
 * <li>handles a few more elements and</li>
 * <li>demonstrates how a subclass can be derived.</li>
 * </ol>
 * 
 * Another issue was the element handlers, which were originally implemented as
 * anonymous classes. Those have been reimplemented as named classes which
 * provides two benefits:
 * <ul>
 * <li>It's much easier to see what handlers are added, where, and the context
 * in which they're added.</li>
 * <li>As named classes, they're deriveable, so a derived class can derive and
 * re-implement as needed</li>
 * </ul>
 * 
 * <h3>Further Thoughts</h3>
 * <p>
 * Another possible implementation would be to push everything down into a base
 * class that provides stubs for <em>all</em> elements. An application could
 * then pick and choose which elements it wanted to implement by overriding and
 * optionally loading the class (have to figure out a nice mechanism to do
 * that.)
 * </p>
 * <p>
 * Sample implementations could be built on that, including a reference test
 * implementation.<br/> 04-Aug-08 - rickb
 * </p>
 * 
 * <h3>Non-Protein Chains (ligands/waters/ions)</h3>
 * <p>
 * Non protein chains are accumulated into their own chains.
 * </p>
 * 
 * 
 * @author John Beaver, Jeff Milton
 * @author Rick Berger
 * @author Peter Rose (revised)
 */
public class StructureXMLHandler extends DefaultHandler implements
		IStructureLoader {
	/**
	 * Base class for all XML Parser Runnables declared here
	 * 
	 * @author rickb
	 * 
	 */
	public abstract class XMLRunnable implements Runnable {
		public Attributes attrs = null;
	}

	private String urlString; // for status reporting purposes.
	private String initialBioId = "1"; // default first biological unit

	public void setInitialBiologicalUnitId(final String id) {
		if (id == null) {
			initialBioId = "1";
		} else {
			initialBioId = id;
		}
	}


	protected Atom curAtom = null; 
	protected Atom prevAtom = null;
	protected Vector<Atom> atomVector = new Vector<Atom>();

	/**
	 * Parsing flags - some of the end element operations are controlled by
	 * these
	 * 
	 * @author rickb
	 * 
	 */
	protected enum eIsParsing {
		NONE, CELL, ATOM_SITES, ATOM_SITE, DATABASE_PDB_MATRIX, STRUCT_BIOLGEN, STRUCT_ASSEMBLY, NON_CRYSTALLOGRAPHIC_OPERATIONS, LEGACY_BIOLOGIC_UNIT_OPERATIONS
	}

	private Stack<eIsParsing> isParsingStack = new Stack<eIsParsing>();

	/**
	 * Set current parsing to this flag
	 * 
	 * @param flag
	 */
	protected void setParsingFlag(eIsParsing flag) {
		isParsingStack.push(flag);
	}

	/**
	 * Clear current parsing flag
	 */
	protected void clearParsingFlag(eIsParsing flag) {
		if (flag != isParsingStack.peek())
			System.err.println("Error - cleared parse flag not on stack.");
		else
			isParsingStack.pop();
	}

	/**
	 * Test to see if we are parsing a particular flag
	 * 
	 * @param flag
	 * @return
	 */
	protected boolean isParsing(eIsParsing flag) {
		return isParsingStack.size() > 0 && isParsingStack.peek() == flag;
	}

	protected eIsParsing getCurrentParsingFlag() {
		return (isParsingStack.size() > 0) ? isParsingStack.peek()
				: eIsParsing.NONE;
	}

	ModelTransformationMatrix currentModelTransform = null;
	ModelTransformationMatrix fractionalTransformation = null;
	ModelTransformationMatrix fractionalTransformationInverse = null;

	UnitCell unitCell = null;

	private Hashtable<ComponentType, Vector<Atom>> componentsHash = new Hashtable<ComponentType, Vector<Atom>>();
	protected Structure structure = null;

	// key : qName , value : Runnable
	protected HashMap<String, XMLRunnable> startElementRunnables = new HashMap<String, XMLRunnable>(); 
	protected HashMap<String, XMLRunnable> endElementAtomRunnables = new HashMap<String, XMLRunnable>(); 
	protected HashMap<String, XMLRunnable> endElementNonCrystallographicRunnables = new HashMap<String, XMLRunnable>(); 
	protected HashMap<String, XMLRunnable> endElementDatabasePDBMatrixRunnables = new HashMap<String, XMLRunnable>(); 
	protected HashMap<String, XMLRunnable> endElementAtomSitesRunnables = new HashMap<String, XMLRunnable>(); 

	protected static final String xmlPrefix = "PDBx:";

	/**
	 * Beg type overrides
	 * 
	 * Override these if you need to create app-specific types (see Ligand
	 * Explorer for example)
	 * 
	 * @return
	 */
	protected Atom createXAtom() {
		return new Atom();
	}
	protected Atom createFinalAtom(Atom src) {
		return new Atom(src);
	}

	public StructureXMLHandler(final String urlString) {
		this.urlString = urlString;

		//
		// BEG General
		//
		startElementRunnables.put(xmlPrefix + "database_PDB_matrix",
				createXMLRunnable__database_PDB_matrix__Start());
		startElementRunnables.put(xmlPrefix + "cell",
				createXMLRunnable__cell__Start());
		//
		// END General
		// BEG Non Crystallographic
		//
		// ------------------
		startElementRunnables.put(xmlPrefix + "struct_ncs_oper",
				createXMLRunnable__struct_ncs_oper__Start());
		endElementNonCrystallographicRunnables.put(xmlPrefix
				+ "struct_ncs_oper", createXMLRunnable__struct_ncs_oper__End());
		// --------------
		//
		// END Non Crystallographic
		
		/*
		 * Two legacy ways to create a biological units.
		 */
		// BEG Legacy Biologic Unit
		//
		
		startElementRunnables.put(xmlPrefix + "pdbx_struct_legacy_oper_list",
				createXMLRunnable__pdbx_struct_legacy_oper_list__Start());
		endElementNonCrystallographicRunnables.put(xmlPrefix
				+ "pdbx_struct_legacy_oper_list",
				createXMLRunnable__pdbx_struct_legacy_oper_list__End());
		//
		// END Legacy Biologic Unit
		// BEG BiolGen 
		//
		startElementRunnables.put(xmlPrefix + "struct_biol_gen",
				createXMLRunnable__struct_biol_gen__Start());
		//
		// END BiolGen
		
		/*
		 * New way to specify biological units (since PDB v3.15, v3.2)
		 */
		// BEG Struct Assembly Gen
		//
		startElementRunnables.put(xmlPrefix + "pdbx_struct_oper_listCategory",
				createXMLRunnable__pdbx_struct_oper_listCategory__Start());
		startElementRunnables.put(xmlPrefix + "pdbx_struct_assembly_gen",
				createXMLRunnable__pdbx_struct_assembly_gen__Start());
		startElementRunnables.put(xmlPrefix + "pdbx_struct_oper_list",
				createXMLRunnable__pdbx_struct_oper_list__Start());
		endElementNonCrystallographicRunnables.put(xmlPrefix
				+ "pdbx_struct_oper_list",
				createXMLRunnable__pdbx_struct_oper_list__End());
		//
		// END Struct Assembly Gen
		//

		endElementNonCrystallographicRunnables.put(xmlPrefix + "code",
				createXMLRunnable__code__End());

		//
		// beg general rotation - no start elements
		//
		endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix11",
				createXMLRunnable__matrix11_End());
		endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix12",
				createXMLRunnable__matrix12_End());
		endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix13",
				createXMLRunnable__matrix13_End());
		endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix21",
				createXMLRunnable__matrix21_End());
		endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix22",
				createXMLRunnable__matrix22_End());
		endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix23",
				createXMLRunnable__matrix23_End());
		endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix31",
				createXMLRunnable__matrix31_End());
		endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix32",
				createXMLRunnable__matrix32_End());
		endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix33",
				createXMLRunnable__matrix33_End());
		//
		// end general rotation
		// beg general translation - no start elements
		//

		endElementNonCrystallographicRunnables.put(xmlPrefix + "vector1",
				createXMLRunnable__vector1__End());
		endElementNonCrystallographicRunnables.put(xmlPrefix + "vector2",
				createXMLRunnable__vector2__End());
		endElementNonCrystallographicRunnables.put(xmlPrefix + "vector3",
				createXMLRunnable__vector3__End());
		//
		// end general translation
		// END Non Crystallographic
		// BEG Atom
		//
		endElementAtomRunnables.put(xmlPrefix + "atom_siteCategory",
				createXMLRunnable__atom_siteCategory__End());
		endElementAtomRunnables.put(xmlPrefix + "type_symbol",
				createXMLRunnable__type_symbol__End());
		endElementAtomRunnables.put(xmlPrefix + "label_atom_id",
				createXMLRunnable__label_atom_id__End());
		endElementAtomRunnables.put(xmlPrefix + "label_comp_id",
				createXMLRunnable__label_comp_id__End());
		endElementAtomRunnables.put(xmlPrefix + "label_asym_id",
				createXMLRunnable__label_asym_id__End());
		endElementAtomRunnables.put(xmlPrefix + "auth_asym_id",
				createXMLRunnable__auth_asym_id__End());
		endElementAtomRunnables.put(xmlPrefix + "label_seq_id",
				createXMLRunnable__label_seq_id__End());
		endElementAtomRunnables.put(xmlPrefix + "auth_seq_id",
				createXMLRunnable__auth_seq_id__End());
		endElementAtomRunnables.put(xmlPrefix + "pdbx_PDB_ins_code",
				createXMLRunnable__pdbx_PDB_ins_code__End());
		endElementAtomRunnables.put(xmlPrefix + "Cartn_x",
				createXMLRunnable__Cartn_x__End());
		endElementAtomRunnables.put(xmlPrefix + "Cartn_y",
				createXMLRunnable__Cartn_y__End());
		endElementAtomRunnables.put(xmlPrefix + "Cartn_z",
				createXMLRunnable__Cartn_z__End());
		endElementAtomRunnables.put(xmlPrefix + "occupancy",
				createXMLRunnable__occupancy__End());
		endElementAtomRunnables.put(xmlPrefix + "B_iso_or_equiv",
				createXMLRunnable__B_iso_or_equiv__End());
		// -----------
		startElementRunnables.put(xmlPrefix + "atom_site",
				createXMLRunnable__Atom_Site__Start());
		endElementAtomRunnables.put(xmlPrefix + "atom_site",
				createXMLRunnable__atom_site__End());
		// ------------
		endElementAtomRunnables.put(xmlPrefix + "pdbx_PDB_model_num",
				createXMLRunnable__pdbx_PDB_model_num__End());
		//
		// END Atom
		// BEG Atom Sites
		//      
		// ------------
		startElementRunnables.put(xmlPrefix + "atom_sites",
				createXMLRunnable__atom_sites__Start());
		endElementAtomSitesRunnables.put(xmlPrefix + "atom_sites",
				createXMLRunnable__atom_sites__End());
		// -------------
		endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix11",
				createXMLRunnable__fract_transf_matrix11__End());
		endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix12",
				createXMLRunnable__fract_transf_matrix12__End());
		endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix13",
				createXMLRunnable__fract_transf_matrix13__End());
		endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix21",
				createXMLRunnable__fract_transf_matrix21__End());
		endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix22",
				createXMLRunnable__fract_transf_matrix22__End());
		endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix23",
				createXMLRunnable__fract_transf_matrix23__End());
		endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix31",
				createXMLRunnable__fract_transf_matrix31__End());
		endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix32",
				createXMLRunnable__fract_transf_matrix32__End());
		endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix33",
				createXMLRunnable__fract_transf_matrix33__End());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.sdsc.lx.model.IStructureXMLHandler#getStructure()
	 */
	public Structure getStructure() {
		return this.structure;
	}

	@Override
	public void endDocument() throws SAXException {
		atomVector.trimToSize();
		componentsHash.put(ComponentType.ATOM, atomVector);

		//
		// Create the Structure object
		//
		structure = new CustomStructure(componentsHash, urlString);

		// create inverses of the fractional and original transforms...
		if (this.fractionalTransformation != null) {
			this.fractionalTransformationInverse = this.fractionalTransformation
					.inverse3();
			this.fractionalTransformationInverse
					.printMatrix("**Inverse fractional transform**");
		}

		// update the struct_biol_gen matrices with the unit cell size
		if (bioUnitTransformationsStructBiolGens != null && this.unitCell != null) {
			for (int i = 0; i < this.bioUnitTransformationsStructBiolGens.size(); i++) {
				ModelTransformationMatrix matrix = bioUnitTransformationsStructBiolGens.get(i);
				if (this.fractionalTransformation != null
						&& this.fractionalTransformationInverse != null) {
					matrix.updateFullSymmetryDataWithInverseFractionalTransform(
									this.fractionalTransformation,
									this.fractionalTransformationInverse);
				}
			}
		}
		
		if (bioUnitTransformationsStructBiolGens != null)
			bioUnitTransformationsStructBiolGens.trimToSize();
		
		if (nonCrystallographicOperations != null)
			nonCrystallographicOperations.trimToSize();
		
		if (bioUnitTransformationsStructLegacy != null)
			bioUnitTransformationsStructLegacy.trimToSize();
		
		bioUnitTransformationsStructAssembly = bioUnitsStructAssembly.getBioUnitTransformationList(initialBioId);
	}

	protected static final String emptyString = "";

	private ModelTransformationMatrix currentBUTransform = null;
	private ModelTransformationMatrix currentNcsTranslation = null;
	private Matrix3f currentRotationMatrix = null;
	private Vector3f currentTranslationVector = null;

	private ModelTransformationList nonCrystallographicOperations = new ModelTransformationList();
	// Used to store transformation matrices derived from struct_biol_gen category (from legacy files)
	private ModelTransformationList bioUnitTransformationsStructBiolGens = new ModelTransformationList();
	// Used to store transformation matrices derived from pdbx_struct_legacy_oper_list (from legacy files)
	private ModelTransformationList bioUnitTransformationsStructLegacy = new ModelTransformationList();
	// Used to store transformation matrixes from pdbx_struct_oper_list (since PDB v3.15, v3.2)
	private BioUnitStructureAssembly bioUnitsStructAssembly = new BioUnitStructureAssembly();
	private ModelTransformationList bioUnitTransformationsStructAssembly = null;

	@Override
	public void startElement(final String namespaceURI, 
			final String sName, // simple name
			final String qName, // qualified name
			final Attributes attrs) throws SAXException {

		final XMLRunnable runnable = startElementRunnables.get(qName);
		if (runnable != null) {
			runnable.attrs = attrs;
			runnable.run();
		}
		this.buf = emptyString;
	}

	private int currentModelNumber = -1;

	@Override
	public void endElement(final String namespaceURI, final String sName, // simple name
			final String qName // qualified name
	) throws SAXException {

		try {
			XMLRunnable runnable = null;
			switch (getCurrentParsingFlag()) {
			case ATOM_SITE:
				runnable = endElementAtomRunnables.get(qName);
				break;

			case NON_CRYSTALLOGRAPHIC_OPERATIONS:
				
			case LEGACY_BIOLOGIC_UNIT_OPERATIONS:
				runnable = endElementNonCrystallographicRunnables.get(qName);
				break;

			case STRUCT_BIOLGEN:
				if (qName.endsWith("pdbx_full_symmetry_operation")) {
					if (currentModelTransform.id == null
							|| (initialBioId == null && currentModelTransform.id
									.equals("1"))
							|| (initialBioId != null && currentModelTransform.id
									.equals(initialBioId))) {
						currentModelTransform.setFullSymmetryOperation(buf.trim());
						bioUnitTransformationsStructBiolGens.add(currentModelTransform);
						currentModelTransform = null;
						clearParsingFlag(eIsParsing.STRUCT_BIOLGEN); 
						// should never be more than one full_symmetry_operation
				        // under a struct_biol_gen.
					}
				}
				break;

			case STRUCT_ASSEMBLY:
				runnable = endElementNonCrystallographicRunnables.get(qName); 
				if (qName.endsWith("pdbx_struct_oper_listCategory")) {
					clearParsingFlag(eIsParsing.STRUCT_ASSEMBLY);
				}
				break;

			case CELL:
				if (qName.endsWith("length_a"))
					this.unitCell.lengthA = Double.parseDouble(this.buf.trim());
				else if (qName.endsWith("length_b"))
					this.unitCell.lengthB = Double.parseDouble(this.buf.trim());
				else if (qName.endsWith("length_c"))
					this.unitCell.lengthC = Double.parseDouble(this.buf.trim());
				else if (qName.endsWith("angle_alpha"))
					this.unitCell.angleAlpha = Double.parseDouble(this.buf
							.trim());
				else if (qName.endsWith("angle_beta"))
					this.unitCell.angleBeta = Double.parseDouble(this.buf
							.trim());
				else if (qName.endsWith("angle_gamma"))
					this.unitCell.angleGamma = Double.parseDouble(this.buf
							.trim());
				else if (qName.endsWith("cell"))
					clearParsingFlag(eIsParsing.CELL);
				break;

			case ATOM_SITES:
				// make sure not to parse atom sites for models 2 and above
				if (currentModelNumber == 1 || currentModelNumber == -1) {
					runnable = endElementAtomSitesRunnables.get(qName);
				} 
				break;

			case DATABASE_PDB_MATRIX:
				runnable = endElementDatabasePDBMatrixRunnables.get(qName);
				break;
			}

			if (runnable != null)
				runnable.run();
		}

		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	protected String buf = null; // a running character buffer, for the use of
									// startCDATA() and endCDATA().

	@Override
	public void characters(final char inbuf[], final int offset, final int len)
			throws SAXException {
		if (curAtom != null || !isParsing(eIsParsing.NONE)) {
			final String tmpString = new String(inbuf, offset, len);
			buf += tmpString;
		}
	}

	public boolean hasBiologicUnitTransformationMatrices() {
		return bioUnitTransformationsStructAssembly.size() + bioUnitTransformationsStructLegacy.size() + bioUnitTransformationsStructBiolGens.size() > 0;
	}

	public ModelTransformationList getBiologicalUnitTransformationMatrices() {
		// this is the new way of handling biological units (PDB v3.15, and v3.2)
		if (bioUnitTransformationsStructAssembly.size() > 0) {
			return bioUnitTransformationsStructAssembly;
		}
		
		// supports legacy mmCIF and PDBML files, mostly for virus assemblies
		if (this.bioUnitTransformationsStructLegacy.size() > 0) {
			return this.bioUnitTransformationsStructLegacy;
		}

		// supports legacy mmCIF and PDBML files
		return this.bioUnitTransformationsStructBiolGens;
	}

	public boolean hasUnitCell() {
		return unitCell != null;
	}

	public UnitCell getUnitCell() {
		return this.unitCell;
	}

	public boolean hasNonCrystallographicOperations() {
		return this.nonCrystallographicOperations.size() > 0;
	}

	public ModelTransformationList getNonCrystallographicOperations() {
		return this.nonCrystallographicOperations;
	}

	/**
	 * BEG Overrideable Runnables
	 * 
	 * These Runnable types are declared discretely, so that an
	 * XMLHandler-derived class can override them. This is the sausage. Look at
	 * the creations above for clarity. Look down here for the nitty-gritty
	 * details.
	 * 
	 * 01-Aug-08 - rickb
	 **/
	public XMLRunnable__Atom_Site__Start createXMLRunnable__Atom_Site__Start() {
		return new XMLRunnable__Atom_Site__Start();
	}

	protected class XMLRunnable__database_PDB_matrix__Start extends XMLRunnable {
		public void run() {
			setParsingFlag(eIsParsing.DATABASE_PDB_MATRIX);
		}
	}

	protected XMLRunnable__database_PDB_matrix__Start createXMLRunnable__database_PDB_matrix__Start() {
		return new XMLRunnable__database_PDB_matrix__Start();
	}

	protected class XMLRunnable__cell__Start extends XMLRunnable {
		public void run() {
			setParsingFlag(eIsParsing.CELL);
			unitCell = new UnitCell();
		}
	}

	protected XMLRunnable__cell__Start createXMLRunnable__cell__Start() {
		return new XMLRunnable__cell__Start();
	}

	protected class XMLRunnable__struct_ncs_oper__Start extends XMLRunnable {
		public void run() {
			setParsingFlag(eIsParsing.NON_CRYSTALLOGRAPHIC_OPERATIONS);
			currentNcsTranslation = new ModelTransformationMatrix();
			currentRotationMatrix = new Matrix3f();
			currentTranslationVector = new Vector3f();

			currentNcsTranslation.id = this.attrs.getValue("id");
		}
	}

	protected XMLRunnable__struct_ncs_oper__Start createXMLRunnable__struct_ncs_oper__Start() {
		return new XMLRunnable__struct_ncs_oper__Start();
	}

	class XMLRunnable__struct_ncs_oper__End extends XMLRunnable {
		public void run() {
			currentNcsTranslation.setTransformationMatrix(
					currentRotationMatrix, currentTranslationVector);
			nonCrystallographicOperations.add(currentNcsTranslation);

			clearParsingFlag(eIsParsing.NON_CRYSTALLOGRAPHIC_OPERATIONS);
			currentNcsTranslation = null;
			currentRotationMatrix = null;
		}
	}

	protected XMLRunnable__struct_ncs_oper__End createXMLRunnable__struct_ncs_oper__End() {
		return new XMLRunnable__struct_ncs_oper__End();
	}

	protected class XMLRunnable__pdbx_struct_legacy_oper_list__Start extends
			XMLRunnable {
		public void run() {
			setParsingFlag(eIsParsing.LEGACY_BIOLOGIC_UNIT_OPERATIONS);
			currentBUTransform = new ModelTransformationMatrix();
			currentRotationMatrix = new Matrix3f();
			currentTranslationVector = new Vector3f();
			currentBUTransform.id = this.attrs.getValue("id");
		}
	}

	protected XMLRunnable__pdbx_struct_legacy_oper_list__Start createXMLRunnable__pdbx_struct_legacy_oper_list__Start() {
		return new XMLRunnable__pdbx_struct_legacy_oper_list__Start();
	}

	protected class XMLRunnable__pdbx_struct_legacy_oper_list__End extends
			XMLRunnable {
		public void run() {
			currentBUTransform.setTransformationMatrix(currentRotationMatrix,
					currentTranslationVector);
			bioUnitTransformationsStructLegacy.add(currentBUTransform);

			clearParsingFlag(eIsParsing.LEGACY_BIOLOGIC_UNIT_OPERATIONS);
			currentBUTransform = null;
			currentRotationMatrix = null;
		}
	}

	protected XMLRunnable__pdbx_struct_legacy_oper_list__End createXMLRunnable__pdbx_struct_legacy_oper_list__End() {
		return new XMLRunnable__pdbx_struct_legacy_oper_list__End();
	}

	protected class XMLRunnable__struct_biol_gen__Start extends XMLRunnable {
		public void run() {
			currentModelTransform = new ModelTransformationMatrix();
			currentModelTransform.id = attrs.getValue("biol_id");
			currentModelTransform.ndbChainId = attrs.getValue("asym_id");
			currentModelTransform.symmetryShorthand = attrs.getValue("symmetry");

			setParsingFlag(eIsParsing.STRUCT_BIOLGEN);
		}
	}

	protected XMLRunnable__struct_biol_gen__Start createXMLRunnable__struct_biol_gen__Start() {
		return new XMLRunnable__struct_biol_gen__Start();
	}

	protected class XMLRunnable__pdbx_struct_oper_listCategory__Start extends
			XMLRunnable {
		public void run() {
			setParsingFlag(eIsParsing.STRUCT_ASSEMBLY);
		}
	}

	protected XMLRunnable__pdbx_struct_oper_listCategory__Start createXMLRunnable__pdbx_struct_oper_listCategory__Start() {
		return new XMLRunnable__pdbx_struct_oper_listCategory__Start();
	}

	protected class XMLRunnable__pdbx_struct_oper_list__End extends XMLRunnable {
		public void run() {
			currentModelTransform.setTransformationMatrix(currentRotationMatrix, currentTranslationVector);
			bioUnitsStructAssembly.addModelTransformationMatrix(currentModelTransform);
			currentRotationMatrix = null;
			currentTranslationVector = null;
		}
	}

	protected XMLRunnable__pdbx_struct_oper_list__End createXMLRunnable__pdbx_struct_oper_list__End() {
		return new XMLRunnable__pdbx_struct_oper_list__End();
	}

	protected class XMLRunnable__pdbx_struct_assembly_gen__Start extends
	XMLRunnable {
		public void run() {
			StructAssemblyGenItem item = new StructAssemblyGenItem();
			item.setAssemblyId(attrs.getValue("assembly_id"));
			item.parseAsymIdString(attrs.getValue("asym_id_list"));
			item.parseOperatorExpressionString(attrs.getValue("oper_expression"));
			bioUnitsStructAssembly.addStructAssemblyGenItem(item);
		}
	}

	protected XMLRunnable__pdbx_struct_assembly_gen__Start createXMLRunnable__pdbx_struct_assembly_gen__Start() {
		return new XMLRunnable__pdbx_struct_assembly_gen__Start();
	}

	protected class XMLRunnable__pdbx_struct_oper_list__Start extends
			XMLRunnable {
		public void run() {
			currentModelTransform = new ModelTransformationMatrix();
			currentModelTransform.id = attrs.getValue("id");
			currentRotationMatrix = new Matrix3f();
			currentTranslationVector = new Vector3f();
		}
	}

	protected XMLRunnable__pdbx_struct_oper_list__Start createXMLRunnable__pdbx_struct_oper_list__Start() {
		return new XMLRunnable__pdbx_struct_oper_list__Start();
	}

	protected class XMLRunnable__code__End extends XMLRunnable {
		public void run() {
			final String trim = buf.trim();

			if (getCurrentParsingFlag() == eIsParsing.LEGACY_BIOLOGIC_UNIT_OPERATIONS) {
				currentBUTransform.code = trim;
			} else if (getCurrentParsingFlag() == eIsParsing.NON_CRYSTALLOGRAPHIC_OPERATIONS) {
				currentNcsTranslation.code = trim;
			}
		}
	}

	protected XMLRunnable__code__End createXMLRunnable__code__End() {
		return new XMLRunnable__code__End();
	}

	protected class XMLRunnable__matrix11_End extends XMLRunnable {
		public void run() {
			currentRotationMatrix.m00 = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__matrix11_End createXMLRunnable__matrix11_End() {
		return new XMLRunnable__matrix11_End();
	}

	protected class XMLRunnable__matrix12_End extends XMLRunnable {
		public void run() {
			currentRotationMatrix.m01 = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__matrix12_End createXMLRunnable__matrix12_End() {
		return new XMLRunnable__matrix12_End();
	}

	protected class XMLRunnable__matrix13_End extends XMLRunnable {
		public void run() {
			currentRotationMatrix.m02 = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__matrix13_End createXMLRunnable__matrix13_End() {
		return new XMLRunnable__matrix13_End();
	}

	protected class XMLRunnable__matrix21_End extends XMLRunnable {
		public void run() {
			currentRotationMatrix.m10 = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__matrix21_End createXMLRunnable__matrix21_End() {
		return new XMLRunnable__matrix21_End();
	}

	protected class XMLRunnable__matrix22_End extends XMLRunnable {
		public void run() {;
			currentRotationMatrix.m11 = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__matrix22_End createXMLRunnable__matrix22_End() {
		return new XMLRunnable__matrix22_End();
	}

	protected class XMLRunnable__matrix23_End extends XMLRunnable {
		public void run() {
			currentRotationMatrix.m12 = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__matrix23_End createXMLRunnable__matrix23_End() {
		return new XMLRunnable__matrix23_End();
	}

	protected class XMLRunnable__matrix31_End extends XMLRunnable {
		public void run() {
			currentRotationMatrix.m20 = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__matrix31_End createXMLRunnable__matrix31_End() {
		return new XMLRunnable__matrix31_End();
	}

	protected class XMLRunnable__matrix32_End extends XMLRunnable {
		public void run() {
			currentRotationMatrix.m21 = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__matrix32_End createXMLRunnable__matrix32_End() {
		return new XMLRunnable__matrix32_End();
	}

	protected class XMLRunnable__matrix33_End extends XMLRunnable {
		public void run() {
			currentRotationMatrix.m22 = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__matrix33_End createXMLRunnable__matrix33_End() {
		return new XMLRunnable__matrix33_End();
	}

	protected class XMLRunnable__vector1__End extends XMLRunnable {
		public void run() {
			currentTranslationVector.x = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__vector1__End createXMLRunnable__vector1__End() {
		return new XMLRunnable__vector1__End();
	}

	protected class XMLRunnable__vector2__End extends XMLRunnable {
		public void run() {
			currentTranslationVector.y = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__vector2__End createXMLRunnable__vector2__End() {
		return new XMLRunnable__vector2__End();
	}

	protected class XMLRunnable__vector3__End extends XMLRunnable {
		public void run() {
			currentTranslationVector.z = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__vector3__End createXMLRunnable__vector3__End() {
		return new XMLRunnable__vector3__End();
	}

	protected class XMLRunnable__atom_siteCategory__End extends XMLRunnable {
		public void run() {
			prevAtom = curAtom = null;
		}
	}

	protected XMLRunnable__atom_siteCategory__End createXMLRunnable__atom_siteCategory__End() {
		return new XMLRunnable__atom_siteCategory__End();
	}

	protected class XMLRunnable__type_symbol__End extends XMLRunnable {
		public void run() {
			curAtom.element = buf.trim();
		}
	}

	protected XMLRunnable__type_symbol__End createXMLRunnable__type_symbol__End() {
		return new XMLRunnable__type_symbol__End();
	}

	protected class XMLRunnable__label_atom_id__End extends XMLRunnable {
		public void run() {
				curAtom.name = buf.trim();
		}
	}

	protected XMLRunnable__label_atom_id__End createXMLRunnable__label_atom_id__End() {
		return new XMLRunnable__label_atom_id__End();
	}

	protected class XMLRunnable__label_comp_id__End extends XMLRunnable {
		public void run() {
				curAtom.compound = buf.trim();
		}
	}

	protected XMLRunnable__label_comp_id__End createXMLRunnable__label_comp_id__End() {
		return new XMLRunnable__label_comp_id__End();
	}

	protected class XMLRunnable__label_asym_id__End extends XMLRunnable {
		public void run() {
				curAtom.chain_id = buf.trim();
		}
	}

	protected XMLRunnable__label_asym_id__End createXMLRunnable__label_asym_id__End() {
		return new XMLRunnable__label_asym_id__End();
	}

	protected class XMLRunnable__auth_asym_id__End extends XMLRunnable {
		public void run() {
			    curAtom.authorChain_id = buf.trim();
		}
	}

	protected XMLRunnable__auth_asym_id__End createXMLRunnable__auth_asym_id__End() {
		return new XMLRunnable__auth_asym_id__End();
	}
	protected class XMLRunnable__label_seq_id__End extends XMLRunnable {
		public void run() {
			    try {
			    	curAtom.residue_id =  Integer.parseInt(buf.trim());
				} catch (Exception e) {
					// mark this residue id as undefined.
					curAtom.residue_id = Integer.MIN_VALUE;
				}
//
//			    _atom_site.label_seq_id can be ".", i.e. for GD9
//			    ATOM   6811 C CG1 . VAL A 1 949 ? 32.433  83.946 14.442 1.00 53.95 ? ? ? ? ? ? 1091 VAL A CG1 1 
//			    ATOM   6812 C CG2 . VAL A 1 949 ? 31.651  81.961 15.822 1.00 55.57 ? ? ? ? ? ? 1091 VAL A CG2 1 
//			    HETATM 6813 O O3  . GD9 B 2 .   ? 19.686  59.525 24.439 1.00 89.60 ? ? ? ? ? ? 1    GD9 A O3  1 
//			    HETATM 6814 C C4  . GD9 B 2 .   ? 22.255  61.781 22.108 1.00 90.57 ? ? ? ? ? ? 1    GD9 A C4  1 
//	    
		}
	}

	protected XMLRunnable__label_seq_id__End createXMLRunnable__label_seq_id__End() {
		return new XMLRunnable__label_seq_id__End();
	}

	protected class XMLRunnable__auth_seq_id__End extends XMLRunnable {
		public void run() {
			    curAtom.authorResidue_id = Integer.parseInt(buf.trim());
		}
	}

	protected XMLRunnable__auth_seq_id__End createXMLRunnable__auth_seq_id__End() {
		return new XMLRunnable__auth_seq_id__End();
	}

	protected class XMLRunnable__pdbx_PDB_ins_code__End extends XMLRunnable {
		public void run() {
			String insertionCode = buf.trim();
			if (insertionCode.equals("?")) {
				curAtom.insertionCode = "";
			} else {
				curAtom.insertionCode = insertionCode;
			}
		}
	}

	protected XMLRunnable__pdbx_PDB_ins_code__End createXMLRunnable__pdbx_PDB_ins_code__End() {
		return new XMLRunnable__pdbx_PDB_ins_code__End();
	}
	
	protected class XMLRunnable__Cartn_x__End extends XMLRunnable {
		public void run() {
				curAtom.coordinate[0] = Double.parseDouble(buf.trim());
		}
	}

	protected XMLRunnable__Cartn_x__End createXMLRunnable__Cartn_x__End() {
		return new XMLRunnable__Cartn_x__End();
	}

	protected class XMLRunnable__Cartn_y__End extends XMLRunnable {
		public void run() {
				curAtom.coordinate[1] = Double.parseDouble(buf.trim());
		}
	}

	protected XMLRunnable__Cartn_y__End createXMLRunnable__Cartn_y__End() {
		return new XMLRunnable__Cartn_y__End();
	}

	protected class XMLRunnable__Cartn_z__End extends XMLRunnable {
		public void run() {
				curAtom.coordinate[2] = Double.parseDouble(buf.trim());
		}
	}

	protected XMLRunnable__Cartn_z__End createXMLRunnable__Cartn_z__End() {
		return new XMLRunnable__Cartn_z__End();
	}

	protected class XMLRunnable__occupancy__End extends XMLRunnable {
		public void run() {
				curAtom.occupancy = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__occupancy__End createXMLRunnable__occupancy__End() {
		return new XMLRunnable__occupancy__End();
	}

	protected class XMLRunnable__B_iso_or_equiv__End extends XMLRunnable {
		public void run() {
				curAtom.bfactor = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__B_iso_or_equiv__End createXMLRunnable__B_iso_or_equiv__End() {
		return new XMLRunnable__B_iso_or_equiv__End();
	}

	protected class XMLRunnable__Atom_Site__Start extends XMLRunnable {
		public void run() {
			curAtom = createXAtom(); // user can override
			curAtom.number = Integer.parseInt(super.attrs.getValue("id")); 
			setParsingFlag(eIsParsing.ATOM_SITE);
		}
	}

	protected class XMLRunnable__atom_site__End extends XMLRunnable
	// all the info for the current atom definition is collected.
	{
		public void run() {
			// ignore all but the first model, for now.
			if (currentModelNumber != 1 && currentModelNumber != -1) {
				clearParsingFlag(eIsParsing.ATOM_SITE);
				return;
			}

			atomVector.add(createFinalAtom(curAtom));
			assignMissingResidueIds();
			prevAtom = curAtom;
			curAtom = null;

			clearParsingFlag(eIsParsing.ATOM_SITE);
		}
	}
	
	/** 
	 * Heterogen residues (ligands, waters, etc.) don't have a label_seq_id.
	 * Assign the auth_seq_id as a placeholder.
	 */
	private void assignMissingResidueIds() {
		for (Atom atom: atomVector) {
			if (atom.residue_id == Integer.MIN_VALUE) {
				atom.residue_id = atom.authorResidue_id;
			}
		}
	}

	protected XMLRunnable__atom_site__End createXMLRunnable__atom_site__End() {
		return new XMLRunnable__atom_site__End();
	}

	protected class XMLRunnable__pdbx_PDB_model_num__End extends XMLRunnable {
		public void run() {
			currentModelNumber = Integer.parseInt(buf.trim());
		}
	}

	protected XMLRunnable__pdbx_PDB_model_num__End createXMLRunnable__pdbx_PDB_model_num__End() {
		return new XMLRunnable__pdbx_PDB_model_num__End();
	}

	protected class XMLRunnable__atom_sites__Start extends XMLRunnable {
		public void run() {
			setParsingFlag(eIsParsing.ATOM_SITES);
			fractionalTransformation = new ModelTransformationMatrix();
			fractionalTransformation.init();
		}
	}

	protected XMLRunnable__atom_sites__Start createXMLRunnable__atom_sites__Start() {
		return new XMLRunnable__atom_sites__Start();
	}

	protected class XMLRunnable__atom_sites__End extends XMLRunnable {
		public void run() {
			fractionalTransformation.values[3] = 0f;
			fractionalTransformation.values[7] = 0f;
			fractionalTransformation.values[11] = 0f;
			fractionalTransformation.values[15] = 1f;

			fractionalTransformation.printMatrix("fractional transform");
			clearParsingFlag(eIsParsing.ATOM_SITES);
		}
	}

	protected XMLRunnable__atom_sites__End createXMLRunnable__atom_sites__End() {
		return new XMLRunnable__atom_sites__End();
	}

	protected class XMLRunnable__fract_transf_matrix11__End extends XMLRunnable {
		public void run() {
			fractionalTransformation.values[0] = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__fract_transf_matrix11__End createXMLRunnable__fract_transf_matrix11__End() {
		return new XMLRunnable__fract_transf_matrix11__End();
	}

	protected class XMLRunnable__fract_transf_matrix12__End extends XMLRunnable {
		public void run() {
			fractionalTransformation.values[4] = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__fract_transf_matrix12__End createXMLRunnable__fract_transf_matrix12__End() {
		return new XMLRunnable__fract_transf_matrix12__End();
	}

	protected class XMLRunnable__fract_transf_matrix13__End extends XMLRunnable {
		public void run() {
			fractionalTransformation.values[8] = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__fract_transf_matrix13__End createXMLRunnable__fract_transf_matrix13__End() {
		return new XMLRunnable__fract_transf_matrix13__End();
	}

	protected class XMLRunnable__fract_transf_matrix21__End extends XMLRunnable {
		public void run() {
			fractionalTransformation.values[1] = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__fract_transf_matrix21__End createXMLRunnable__fract_transf_matrix21__End() {
		return new XMLRunnable__fract_transf_matrix21__End();
	}

	protected class XMLRunnable__fract_transf_matrix22__End extends XMLRunnable {
		public void run() {
			fractionalTransformation.values[5] = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__fract_transf_matrix22__End createXMLRunnable__fract_transf_matrix22__End() {
		return new XMLRunnable__fract_transf_matrix22__End();
	}

	protected class XMLRunnable__fract_transf_matrix23__End extends XMLRunnable {
		public void run() {
			fractionalTransformation.values[9] = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__fract_transf_matrix23__End createXMLRunnable__fract_transf_matrix23__End() {
		return new XMLRunnable__fract_transf_matrix23__End();
	}

	protected class XMLRunnable__fract_transf_matrix31__End extends XMLRunnable {
		public void run() {
			fractionalTransformation.values[2] = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__fract_transf_matrix31__End createXMLRunnable__fract_transf_matrix31__End() {
		return new XMLRunnable__fract_transf_matrix31__End();
	}

	protected class XMLRunnable__fract_transf_matrix32__End extends XMLRunnable {
		public void run() {
			fractionalTransformation.values[6] = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__fract_transf_matrix32__End createXMLRunnable__fract_transf_matrix32__End() {
		return new XMLRunnable__fract_transf_matrix32__End();
	}

	protected class XMLRunnable__fract_transf_matrix33__End extends XMLRunnable {
		public void run() {
			fractionalTransformation.values[10] = Float.parseFloat(buf.trim());
		}
	}

	protected XMLRunnable__fract_transf_matrix33__End createXMLRunnable__fract_transf_matrix33__End() {
		return new XMLRunnable__fract_transf_matrix33__End();
	}

	public boolean canLoad(String name) {
		return true;
	}

	public String getLoaderName() {
		return null;
	}

	/**
	 * XML is handled differently (but I'm not sure why....) So, 'load' doesn't
	 * do anything. This may change...
	 */
	public Structure load(String name) {
		return null;
	}
}

class CustomStructure extends Structure {
	// A hashtable of vectors where
	// each hash KEY is the StructureComponent type String.
	// each hash VALUE is a Vector of StructureComponent objects.
	protected Hashtable<ComponentType, Vector<Atom>> structureComponents = null;

	// To free up the global state for another load call.
	private String localUrlString;

	// public Structure() Anonymous inner class constructor.
	public CustomStructure(
			final Hashtable<ComponentType, Vector<Atom>> structureComponents,
			final String localUrlString) {
		super();
		this.structureComponents = structureComponents;

		this.localUrlString = localUrlString;
	}

	@Override
	public String getUrlString() {
		return this.localUrlString;
	}

	@Override
	public int getStructureComponentCount(final ComponentType scType) {
		final Vector<?> records = this.structureComponents.get(scType);
		if (records == null) {
			return 0;
		}

		return records.size();
	}

	@Override
	public StructureComponent getStructureComponentByIndex(
			final ComponentType type, final int index)
			throws IndexOutOfBoundsException, IllegalArgumentException {
		final Vector<?> records = this.structureComponents.get(type);
		if (records == null) {
			throw new IllegalArgumentException("no records of type " + type);
		}

		final StructureComponent structureComponent = (StructureComponent) records
				.elementAt(index);
		structureComponent.structure = this;

		return structureComponent;
	}
}
