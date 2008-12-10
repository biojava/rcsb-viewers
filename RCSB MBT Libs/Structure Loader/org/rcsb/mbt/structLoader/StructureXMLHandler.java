package org.rcsb.mbt.structLoader;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.geometry.ModelTransformationList;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureComponentRegistry;
import org.rcsb.mbt.model.UnitCell;
import org.rcsb.mbt.model.geometry.ModelTransformationMatrix;
import org.rcsb.mbt.model.util.PdbToNdbConverter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Invoked by the SAX while parsing the XML file.
 * 
 * This class is deriveable.  See 'RCSB PDB Kiosk' for an example of how it's done.
 * 
 *
 * <h3>Further Info</h3>
 * This class was originally three separate classes in three different viewers with a huge amount of overlap.
 * I've consolidated them down into this base class, which does most everything, and one derived for the
 * Kiosk viewer that:
 *  <ol>
 *  <li>handles a few more elements and</li>
 *  <li>demonstrates how a subclass can be derived.</li>
 *  </ol>
 * 
 * Another issue was the element handlers, which were originally implemented as anonymous classes.
 * Those have been reimplemented as named classes which provides two benefits:
 *  <ul>
 *  <li>It's much easier to see what handlers are added, where, and the context in which they're added.</li>
 *  <li>As named classes, they're deriveable, so a derived class can derive and re-implement as needed</li>
 *  </ul>
 * 
 * <h3>Further Thoughts</h3>
 * <p>
 * Another possible implementation would be to push everything down into a base class that provides stubs
 * for <em>all</em> elements.  An application could then pick and choose which elements it wanted to
 * implement by overriding and optionally loading the class (have to figure out a nice mechanism to do that.)</p>
 * <p>
 * Sample implementations could be built on that, including a reference test implementation.<br/>
 * 04-Aug-08 - rickb</p>
 * 
 * <h3>Non-Protein Chains (ligands/waters/ions)</h3>
 * <p>
 * Non protein chains are accumulated into their own chains, typically by using the Ndb identifier
 * (hence the need for the {@linkplain org.rcsb.mbt.model.util.PdbToNdbConverter}, created by the
 * loader and passed off to the {@linkplain org.rcsb.mbt.model.StructureMap} class.</p>
 * 
 * <p>
 * Some important mappings:</p>
 * <h3>Atoms</h3>
 * <dl>
 * <dt>group_PDB</dt>
 * <dd>The PDB Atom record identifier equivalent (ATOM or HETATM)</dd>
 * 
 * <dt>label_atom_id</dt>
 * <dd>The atom NAME</dd>
 * 
 * <dt>label_comp_id</dt>
 * <dd>The atom's residue compound code</dd>
 * 
 * <dt>label_asym_id</dt>
 * <dd>The atom's NDB chain id</dd>
 * 
 * <dt>label_seq_id</dt>
 * <dd>The atom's NDB residue id (can be empty) (Integer)</dd>
 * 
 * <dt>auth_seq_id</dt>
 * <dd>The atom's PDB residue id (also tracks non-protein chain changes.)</dd>
 * 
 * <dt>pdb_strand_id</dt>
 * <dd>The atom's PDB id (only recorded for strands [secondary structures])
 * 
 * <p>
 * Note that what is stored in the atom is <em>NDB</em> ids.  PDB ids are looked up as needed.</p>
 * 
 * @author John Beaver, Jeff Milton
 * @author (revised) rickb
 */
public class StructureXMLHandler extends DefaultHandler implements IStructureLoader
{
	/**
	 * Base class for all XML Parser Runnables declared here
	 * @author rickb
	 *
	 */
    public abstract class XMLRunnable implements Runnable
    {
    	public Attributes attrs = null;
    }
    
    private String urlString;   // for status reporting purposes.
    private String initialBioId = null;
    public void setInitialBiologicalUnitId(final String id) { initialBioId = id; }
    
    // used temporarily to store the current atom before putting it into atomVector.
    protected Atom curAtom = null;
    protected Vector<Atom> atomVector = new Vector<Atom>();
    // used temporarily to store the information needed by the ResidueIdConverter constructor.
    // parallel arrays.
    protected Vector<String> pdbChainIds = null;
    protected Vector<String> ndbChainIds = null;
    protected Vector<String> pdbResidueIds = new Vector<String>();
    protected Vector<Integer> ndbResidueIds = null;
    
    /**
     * Parsing flags - some of the end element operations are controlled by these
     * @author rickb
     *
     */
    protected enum eIsParsing { NONE, CONVERSIONS, CELL, ATOM_SITES, DATABASE_PDB_MATRIX, NON_POLY_CONVERSIONS, STRUCT_BIOLGEN,
    						    NON_CRYSTALLOGRAPHIC_OPERATIONS, LEGACY_BIOLOGIC_UNIT_OPERATIONS }
    private Stack<eIsParsing> isParsingStack = new Stack<eIsParsing>();
    
    /**
     * Set current parsing to this flag
     * @param flag
     */
    protected void setParsingFlag(eIsParsing flag)
    {
    	isParsingStack.push(flag);
    }
    
    /**
     * Clear current parsing flag
     */
    protected void clearParsingFlag(eIsParsing flag)
    {
    	if (flag != isParsingStack.peek())
    		System.err.println("Error - cleared parse flag not on stack.");
    	else isParsingStack.pop();
    }
    
    /**
     * Test to see if we are parsing a particular flag
     * @param flag
     * @return
     */
    protected boolean isParsing(eIsParsing flag)
    {
    	return isParsingStack.size() > 0 && isParsingStack.peek() == flag;
    }
    
    protected eIsParsing getCurrentParsingFlag()
    {
    	return (isParsingStack.size() > 0)? isParsingStack.peek() : eIsParsing.NONE;
    }
    
    // used temporarily to store the information needed by the ChainIdConverter constructor.
    private String curNdbChainId = null;
    private String curPdbChainId = null;
    
    ModelTransformationMatrix curStructBiolGen = null;
    ModelTransformationMatrix fractionalTransformation = null;
    ModelTransformationMatrix fractionalTransformationInverse = null;
    
    UnitCell unitCell = null;
    
    private Hashtable<ComponentType, Vector<Atom>> componentsHash = new Hashtable<ComponentType, Vector<Atom>>();
    protected Structure structure = null;
    private PdbToNdbConverter idConverter = new PdbToNdbConverter();
    
    protected HashMap<String, XMLRunnable> startElementRunnables = new HashMap<String, XMLRunnable>();	// key: qName, value: Runnable
    protected HashMap<String, XMLRunnable> endElementAtomRunnables = new HashMap<String, XMLRunnable>();	// key: qName, value: Runnable
    protected HashMap<String, XMLRunnable> endElementPolyConversionsRunnables = new HashMap<String, XMLRunnable>();	// key: qName, value: Runnable
    protected HashMap<String, XMLRunnable> endElementNonpolyConversionsRunnables = new HashMap<String, XMLRunnable>();	// key: qName, value: Runnable
    protected HashMap<String, XMLRunnable> endElementNonCrystallographicRunnables = new HashMap<String, XMLRunnable>();	// key: qName, value: Runnable
    protected HashMap<String, XMLRunnable> endElementDatabasePDBMatrixRunnables = new HashMap<String, XMLRunnable>();	// key: qName, value: Runnable
    protected HashMap<String, XMLRunnable> endElementAtomSitesRunnables = new HashMap<String, XMLRunnable>();	// key: qName, value: Runnable
    
    protected boolean pdbStrandIdEncountered = false;
    protected boolean ndbSeqNumEncountered = false;
    protected boolean pdbSeqNumEncountered = false;   

    protected static final String xmlPrefix = "PDBx:";
    
    /**
     * Beg type overrides
     * 
     * Override these if you need to create app-specific types
     * (see Ligand Explorer for example)
     * @return
     */
    protected Atom createAtom() { return new Atom(); }
    ///
    /// end type overrides
    
    public StructureXMLHandler(final String urlString)
    {
        this.urlString = urlString;

	    //
        // BEG General
        //
        startElementRunnables.put(xmlPrefix + "database_PDB_matrix", createXMLRunnable__database_PDB_matrix__Start());
        startElementRunnables.put(xmlPrefix + "pdbx_poly_seq_schemeCategory", createXMLRunnable__pdbx_poly_seq_schemeCategory__Start());
        startElementRunnables.put(xmlPrefix + "cell", createXMLRunnable__cell__Start());       
        //
        // END General
	    // BEG Non Crystallographic
        //
        //------------------
	    startElementRunnables.put(xmlPrefix + "struct_ncs_oper", createXMLRunnable__struct_ncs_oper__Start());
        endElementNonCrystallographicRunnables.put(xmlPrefix + "struct_ncs_oper", createXMLRunnable__struct_ncs_oper__End());
        //--------------
        //
        // END Non Crystallographic
        // BEG Legacy Biologic Unit
        //
	    /*
	     * This appears to be one way (legacy way) to create a biologic unit.
	     */
        startElementRunnables.put(xmlPrefix + "pdbx_struct_legacy_oper_list", createXMLRunnable__pdbx_struct_legacy_oper_list__Start());
        endElementNonCrystallographicRunnables.put(xmlPrefix + "pdbx_struct_legacy_oper_list", createXMLRunnable__pdbx_struct_legacy_oper_list__End());
        //
        // END Legacy Biologic Unit
        // BEG BiolGen
        //
        startElementRunnables.put(xmlPrefix + "struct_biol_gen", createXMLRunnable__struct_biol_gen__Start());
        //
        // END BiolGen
        //   
        endElementNonCrystallographicRunnables.put(xmlPrefix + "code", createXMLRunnable__code__End());
        
        //
        // beg general rotation - no start elements
        //
        endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix11", createXMLRunnable__matrix11_End());
        endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix12", createXMLRunnable__matrix12_End());
        endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix13", createXMLRunnable__matrix13_End());
        endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix21", createXMLRunnable__matrix21_End());
        endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix22", createXMLRunnable__matrix22_End());
        endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix23", createXMLRunnable__matrix23_End());
        endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix31", createXMLRunnable__matrix31_End());
        endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix32", createXMLRunnable__matrix32_End());
        endElementNonCrystallographicRunnables.put(xmlPrefix + "matrix33", createXMLRunnable__matrix33_End());
        //
        // end general rotation
        // beg general translation - no start elements
        //
        
        endElementNonCrystallographicRunnables.put(xmlPrefix + "vector1", createXMLRunnable__vector1__End());
        endElementNonCrystallographicRunnables.put(xmlPrefix + "vector2", createXMLRunnable__vector2__End());
        endElementNonCrystallographicRunnables.put(xmlPrefix + "vector3", createXMLRunnable__vector3__End());
        //
        // end general translation
        // END Non Crystallographic
        // BEG Atom
        //
        endElementAtomRunnables.put(xmlPrefix + "atom_siteCategory", createXMLRunnable__atom_siteCategory__End());
        endElementAtomRunnables.put(xmlPrefix + "type_symbol", createXMLRunnable__type_symbol__End());
        endElementAtomRunnables.put(xmlPrefix + "group_PDB", createXMLRunnable__group_PDB__End());
        endElementAtomRunnables.put(xmlPrefix + "label_atom_id", createXMLRunnable__label_atom_id__End());
        endElementAtomRunnables.put(xmlPrefix + "label_comp_id", createXMLRunnable__label_comp_id__End());
        endElementAtomRunnables.put(xmlPrefix + "label_asym_id", createXMLRunnable__label_asym_id__End());
        endElementAtomRunnables.put(xmlPrefix + "label_seq_id", createXMLRunnable__label_seq_id__End());
        endElementAtomRunnables.put(xmlPrefix + "auth_seq_id", createXMLRunnable__auth_seq_id__End());
        endElementAtomRunnables.put(xmlPrefix + "Cartn_x", createXMLRunnable__Cartn_x__End());
        endElementAtomRunnables.put(xmlPrefix + "Cartn_y", createXMLRunnable__Cartn_y__End());
        endElementAtomRunnables.put(xmlPrefix + "Cartn_z", createXMLRunnable__Cartn_z__End());        
        endElementAtomRunnables.put(xmlPrefix + "occupancy", createXMLRunnable__occupancy__End());
        endElementAtomRunnables.put(xmlPrefix + "B_iso_or_equiv", createXMLRunnable__B_iso_or_equiv__End());
        //-----------
        startElementRunnables.put(xmlPrefix + "atom_site", createXMLRunnable__Atom_Site__Start());        
        endElementAtomRunnables.put(xmlPrefix + "atom_site", createXMLRunnable__atom_site__End());
        //------------
        endElementAtomRunnables.put(xmlPrefix + "pdbx_PDB_model_num", createXMLRunnable__pdbx_PDB_model_num__End());
        //
        // END Atom
        // BEG Atom Sites
        //      
        // ------------
        startElementRunnables.put(xmlPrefix + "atom_sites", createXMLRunnable__atom_sites__Start());
        endElementAtomSitesRunnables.put(xmlPrefix + "atom_sites", createXMLRunnable__atom_sites__End());
        // -------------
        endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix11", createXMLRunnable__fract_transf_matrix11__End());
        endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix12", createXMLRunnable__fract_transf_matrix12__End());
        endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix13", createXMLRunnable__fract_transf_matrix13__End());
        endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix21", createXMLRunnable__fract_transf_matrix21__End());
        endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix22", createXMLRunnable__fract_transf_matrix22__End());
        endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix23", createXMLRunnable__fract_transf_matrix23__End());
        endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix31", createXMLRunnable__fract_transf_matrix31__End());
        endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix32", createXMLRunnable__fract_transf_matrix32__End());
        endElementAtomSitesRunnables.put(xmlPrefix + "fract_transf_matrix33", createXMLRunnable__fract_transf_matrix33__End());
        //
        // END Atom Sites
        // BEG Poly Conversions
        //
        
        //---------------
        startElementRunnables.put(xmlPrefix + "pdbx_poly_seq_scheme", createXMLRunnable__pdbx_poly_seq_scheme__Start());
        endElementPolyConversionsRunnables.put(xmlPrefix + "pdbx_poly_seq_scheme", createXMLRunnable__pdbx_poly_seq_scheme__End());
         //-------------
        endElementPolyConversionsRunnables.put(xmlPrefix + "pdb_strand_id", createXMLRunnable__pdb_strand_id__End());
        endElementPolyConversionsRunnables.put(xmlPrefix + "pdbx_poly_seq_schemeCategory", createXMLRunnable__pdbx_poly_seq_schemeCategory__End());
        endElementPolyConversionsRunnables.put(xmlPrefix + "ndb_seq_num", createXMLRunnable__ndb_seq_num__End());
        endElementPolyConversionsRunnables.put(xmlPrefix + "pdb_seq_num", createXMLRunnable__pdb_seq_num__End());
        //
        // END Poly Conversions
        // BEG Non Poly Conversions
        //
 
        // -------------
        startElementRunnables.put(xmlPrefix + "pdbx_nonpoly_schemeCategory", createXMLRunnable__pdbx_nonpoly_schemeCategory__Start());
        endElementNonpolyConversionsRunnables.put(xmlPrefix + "pdbx_nonpoly_schemeCategory", createXMLRunnable__pdbx_nonpoly_schemeCategory__End());
        //------------------------
        
        //-------------------------
        startElementRunnables.put(xmlPrefix + "pdbx_nonpoly_scheme", createXMLRunnable__pdbx_nonpoly_scheme__Start());
        endElementNonpolyConversionsRunnables.put(xmlPrefix + "pdbx_nonpoly_scheme", createXMLRunnable_pdbx_nonpoly_scheme__End());
        //-------------------------
        endElementNonpolyConversionsRunnables.put(xmlPrefix + "pdb_strand_id", createXMLRunnable__pdb_seq_num__End_np());
        endElementNonpolyConversionsRunnables.put(xmlPrefix + "pdb_seq_num", createXMLRunnable__pdb_seq_num__End_np());
        					// these last two are tricky - we have to further qualify the runnable class
        					// for the nonpoly conversions
        //
        // END Non Poly Conversions
        //
     }

    /* (non-Javadoc)
	 * @see edu.sdsc.lx.model.IStructureXMLHandler#getStructure()
	 */
    public Structure getStructure() {
        return this.structure;
    }
      
    /* (non-Javadoc)
	 * @see edu.sdsc.lx.model.IStructureXMLHandler#getIDConverter()
	 */
    public PdbToNdbConverter getIDConverter() {
        return this.idConverter;
    }
    
    /* (non-Javadoc)
	 * @see edu.sdsc.lx.model.IStructureXMLHandler#getNonProteinChainIds()
	 */
    public Set<String> getNonProteinChainIds() {
        return this.nonProteinChainIds;
    }
        
	@Override
	public void endDocument() throws SAXException
	{
        this.atomVector.trimToSize();
        this.componentsHash.put(ComponentType.ATOM, this.atomVector );
        
        //
        // Create the Structure object
        //
        this.structure = new CustomStructure(this.componentsHash, this.urlString);
        
        // create inverses of the fractional and original transforms...
        if (this.fractionalTransformation != null) {
        	this.fractionalTransformationInverse = this.fractionalTransformation.inverse3();
        	this.fractionalTransformationInverse.printMatrix("**Inverse fractional transform**");
        }
        
        // update the struct_biol_gen matrices with the unit cell size
        if (this.structBiolGens != null && this.unitCell != null) {
        	for(int i = 0; i < this.structBiolGens.size(); i++) {
        		final ModelTransformationMatrix matrix = this.structBiolGens.get(i);
        		if(this.fractionalTransformation != null &&  this.fractionalTransformationInverse != null) {
        			matrix.updateFullSymmetryDataWithInverseFractionalTransform(this.fractionalTransformation, this.fractionalTransformationInverse);
        		}
        	}
        }

		if (nonCrystallographicOperations != null)
			nonCrystallographicOperations.trimToSize();
    }
    
    protected static final String emptyString = ""; 
    
    private ModelTransformationMatrix currentBUTransform = null;
    private ModelTransformationMatrix currentNcsTranslation = null;
    private Matrix3f currentRotationMatrix = null;
    private Vector3f currentTranslationVector = null;
    
    private ModelTransformationList structBiolGens = new ModelTransformationList();
    private ModelTransformationList nonCrystallographicOperations = new ModelTransformationList();
    private final ModelTransformationList biologicalUnitCreationMatrices = new ModelTransformationList();    
    
	@Override
	public void startElement(final String namespaceURI,
            final String sName, // simple name
            final String qName, // qualified name
            final Attributes attrs) throws SAXException {
        //System.out.println(qName);
        
    	final XMLRunnable runnable = startElementRunnables.get(qName);
    	if(runnable != null) {
	        runnable.attrs = attrs;
	        runnable.run();
    	}
    	
        //System.out.println(this.buf);
        this.buf = emptyString;
    }
    
    private Set<String> nonProteinChainIds = new TreeSet<String>();
    private boolean isCurrentNonProteinChain = false;
    private String previousPdbResidueId = null;
    private String previousNdbChainId = null;
    private int curGeneratedNdbResidueId = -1;
    private int currentModelNumber = -1;
    
	@Override
	public void endElement(final String namespaceURI,
            final String sName, // simple name
            final String qName  // qualified name
            ) throws SAXException {
        
		try
		{
	    	if(this.curAtom != null)
	    	{
	    		final XMLRunnable runnable = endElementAtomRunnables.get(qName);
	        	if(runnable != null) {
	    	        runnable.run();
	        	}
	        }
	    	
	    	else 
	    	{
	    		XMLRunnable runnable = null;
	    		switch(getCurrentParsingFlag())
		    	{
		    	case CONVERSIONS:
		        	runnable = endElementPolyConversionsRunnables.get(qName);
		        	break;
	
		    	case NON_POLY_CONVERSIONS:
		        	runnable = endElementNonpolyConversionsRunnables.get(qName);
		        	break;
		        
		    	case NON_CRYSTALLOGRAPHIC_OPERATIONS:
		        case LEGACY_BIOLOGIC_UNIT_OPERATIONS:
		    		runnable = endElementNonCrystallographicRunnables.get(qName);
		        	break;
	
		        case STRUCT_BIOLGEN:
		        	if(qName.endsWith("pdbx_full_symmetry_operation"))
		        	{
		        		if (this.curStructBiolGen.id == null ||
		        				(initialBioId == null && this.curStructBiolGen.id.equals("1")) ||
		        				(initialBioId != null && this.curStructBiolGen.id.equals(initialBioId)))
		        		{
			        		this.curStructBiolGen.setFullSymmetryOperation(this.buf.trim());
			        		this.structBiolGens.add(this.curStructBiolGen);
			        		this.curStructBiolGen = null;
							clearParsingFlag(eIsParsing.STRUCT_BIOLGEN); // should never be more than one full_symmetry_operation under a struct_biol_gen.
		        		}
		        	}
		        	break;
		        
		        case CELL:
		        	if(qName.endsWith("length_a")) 
		        		this.unitCell.lengthA = Double.parseDouble(this.buf.trim());
		        	else if(qName.endsWith("length_b")) 
		        		this.unitCell.lengthB = Double.parseDouble(this.buf.trim());
		        	else if(qName.endsWith("length_c")) 
		        		this.unitCell.lengthC = Double.parseDouble(this.buf.trim());
		        	else if(qName.endsWith("angle_alpha")) 
		        		this.unitCell.angleAlpha = Double.parseDouble(this.buf.trim());
		        	else if(qName.endsWith("angle_beta")) 
		        		this.unitCell.angleBeta = Double.parseDouble(this.buf.trim());
		        	else if(qName.endsWith("angle_gamma")) 
		        		this.unitCell.angleGamma = Double.parseDouble(this.buf.trim());
		        	else if(qName.endsWith("cell")) 
		        		clearParsingFlag(eIsParsing.CELL);
		        	break;
		        
		        case ATOM_SITES:
		        	runnable = endElementAtomSitesRunnables.get(qName);
		        	break;
		        
		        case DATABASE_PDB_MATRIX:
		        	runnable = endElementDatabasePDBMatrixRunnables.get(qName);
		        	break;
		        }
	    		
	        if (runnable != null)
	        	runnable.run();
	    	}
	    	
		}
		
		catch(final Exception e)
		{
			e.printStackTrace();
		}
    } 
    
    protected String buf = null;  // a running character buffer, for the use of startCDATA() and endCDATA().
    
	@Override
	public void characters(final char inbuf[], final int offset, final int len) throws SAXException
	{
       if (curAtom != null || !isParsing(eIsParsing.NONE))
       {
           final String tmpString = new String(inbuf,offset,len);           
           buf += tmpString;
       }
    }
    
    // ensure the uniqueness of all strings to save space.
    Hashtable<String, String> uniqueStrings = new Hashtable<String, String>();
    protected String getUnique(final String s) {
        if(s == null) {
            return null;
        }
        
        final String unique = this.uniqueStrings.get(s);
        if(unique == null) {
            this.uniqueStrings.put(s, s);
            return s;
        }
        return unique;
    }

    public boolean hasBiologicUnitTransformationMatrices()
    	{ return biologicalUnitCreationMatrices.size() + structBiolGens.size() > 0; }
    
	public ModelTransformationList getBiologicalUnitTransformationMatrices()
	{
		// first, combine the (incorrectly used) ncs matrices with the struct_biol_gen entries.
		// Either (but not both) ncs matrix or struct_biol_gen vectors should contain useful data. In the unexpected case where both contain useful data, use struct_biol_gen.
		
		if (this.biologicalUnitCreationMatrices.size() > 0) {
			this.biologicalUnitCreationMatrices.trimToSize();
			return this.biologicalUnitCreationMatrices;
		}
		
		this.structBiolGens.trimToSize();
		return this.structBiolGens;
	}
	
	public boolean hasUnitCell()
	{
		return unitCell!= null;
	}

	public UnitCell getUnitCell() {
		return this.unitCell;
	}

	public boolean hasNonCrystallographicOperations()
	{ return this.nonCrystallographicOperations.size() > 0; }
	public ModelTransformationList getNonCrystallographicOperations()
	{ return this.nonCrystallographicOperations; }
	
	/**
	 * BEG Overrideable Runnables
	 *
	 * These Runnable types are declared discretely, so that an XMLHandler-derived class can override them.
	 * This is the sausage.  Look at the creations above for clarity.  Look down here for the nitty-gritty
	 * details.
	 * 
	 * 01-Aug-08 - rickb
	 **/
	public XMLRunnable__Atom_Site__Start createXMLRunnable__Atom_Site__Start() { return new XMLRunnable__Atom_Site__Start(); }

    protected class XMLRunnable__database_PDB_matrix__Start extends XMLRunnable
    {
    	public void run()
    	{
			setParsingFlag(eIsParsing.DATABASE_PDB_MATRIX);
    	}
    }
    protected XMLRunnable__database_PDB_matrix__Start createXMLRunnable__database_PDB_matrix__Start()
		{ return new XMLRunnable__database_PDB_matrix__Start(); }
    
    protected class XMLRunnable__pdbx_poly_seq_schemeCategory__Start extends XMLRunnable
    {
		public void run()
		{
            setParsingFlag(eIsParsing.CONVERSIONS);
			pdbChainIds = new Vector<String>();
			ndbChainIds = new Vector<String>();
			pdbResidueIds = new Vector<String>();
			ndbResidueIds = new Vector<Integer>();
		}
    }
    protected XMLRunnable__pdbx_poly_seq_schemeCategory__Start createXMLRunnable__pdbx_poly_seq_schemeCategory__Start()
		{ return new XMLRunnable__pdbx_poly_seq_schemeCategory__Start(); }
    
    protected class XMLRunnable__cell__Start extends XMLRunnable
    {
		public void run()
		{
			setParsingFlag(eIsParsing.CELL);
			unitCell = new UnitCell();
		}
    }
   protected XMLRunnable__cell__Start createXMLRunnable__cell__Start() { return new XMLRunnable__cell__Start(); }

   protected class XMLRunnable__struct_ncs_oper__Start extends XMLRunnable
   {
		public void run()
		{
			setParsingFlag(eIsParsing.NON_CRYSTALLOGRAPHIC_OPERATIONS);
			currentNcsTranslation = new ModelTransformationMatrix();
			currentRotationMatrix = new Matrix3f();
			currentTranslationVector = new Vector3f();
   		
			currentNcsTranslation.id = this.attrs.getValue("id");
		}
   }
   protected XMLRunnable__struct_ncs_oper__Start createXMLRunnable__struct_ncs_oper__Start()
   { return new XMLRunnable__struct_ncs_oper__Start(); }

   class XMLRunnable__struct_ncs_oper__End extends XMLRunnable
   {
		public void run() {
			currentNcsTranslation.setTransformationMatrix(currentRotationMatrix, currentTranslationVector);
			nonCrystallographicOperations.add(currentNcsTranslation);
			
			clearParsingFlag(eIsParsing.NON_CRYSTALLOGRAPHIC_OPERATIONS);
			currentNcsTranslation = null;
			currentRotationMatrix = null;
		}
   }
   protected XMLRunnable__struct_ncs_oper__End createXMLRunnable__struct_ncs_oper__End()
   { return new XMLRunnable__struct_ncs_oper__End(); }

   protected class XMLRunnable__pdbx_struct_legacy_oper_list__Start extends XMLRunnable
   {
		public void run()
		{
			setParsingFlag(eIsParsing.LEGACY_BIOLOGIC_UNIT_OPERATIONS);
			currentBUTransform = new ModelTransformationMatrix();
			currentRotationMatrix = new Matrix3f();
			currentTranslationVector = new Vector3f();
   		
			currentBUTransform.id = this.attrs.getValue("id");
		}
   }
   protected XMLRunnable__pdbx_struct_legacy_oper_list__Start createXMLRunnable__pdbx_struct_legacy_oper_list__Start()
   { return new XMLRunnable__pdbx_struct_legacy_oper_list__Start(); }

   protected class XMLRunnable__pdbx_struct_legacy_oper_list__End extends XMLRunnable
   {
		public void run() {
			currentBUTransform.setTransformationMatrix(currentRotationMatrix, currentTranslationVector);
			biologicalUnitCreationMatrices.add(currentBUTransform);
			
			clearParsingFlag(eIsParsing.LEGACY_BIOLOGIC_UNIT_OPERATIONS);
			currentBUTransform = null;
			currentRotationMatrix = null;
		}
   }
   protected XMLRunnable__pdbx_struct_legacy_oper_list__End createXMLRunnable__pdbx_struct_legacy_oper_list__End()
   { return new XMLRunnable__pdbx_struct_legacy_oper_list__End(); }

   protected class XMLRunnable__struct_biol_gen__Start extends XMLRunnable
   {
		public void run() {
			curStructBiolGen = new ModelTransformationMatrix();
			curStructBiolGen.id = super.attrs.getValue("biol_id");
			curStructBiolGen.ndbChainId = super.attrs.getValue("asym_id");		
			curStructBiolGen.symmetryShorthand = super.attrs.getValue("symmetry");
			
			setParsingFlag(eIsParsing.STRUCT_BIOLGEN);
		}
   }
   protected XMLRunnable__struct_biol_gen__Start createXMLRunnable__struct_biol_gen__Start()
   { return new XMLRunnable__struct_biol_gen__Start(); }

   protected class XMLRunnable__code__End extends XMLRunnable
   {
		public void run() {
			final String trim = buf.trim();
			
			if (getCurrentParsingFlag() == eIsParsing.LEGACY_BIOLOGIC_UNIT_OPERATIONS)
				currentBUTransform.code = trim;
			else if (getCurrentParsingFlag() == eIsParsing.NON_CRYSTALLOGRAPHIC_OPERATIONS)
				currentNcsTranslation.code = trim;
		}
   }
   protected XMLRunnable__code__End createXMLRunnable__code__End() { return new XMLRunnable__code__End(); }

   protected class XMLRunnable__matrix11_End extends XMLRunnable       
   {
		public void run() {
			final String trim = buf.trim();			
			currentRotationMatrix.m00 = Float.parseFloat(trim);
		}
   }
   protected XMLRunnable__matrix11_End createXMLRunnable__matrix11_End() { return new XMLRunnable__matrix11_End(); }

   protected class XMLRunnable__matrix12_End extends XMLRunnable       
   {
		public void run() {
			final String trim = buf.trim();			
			currentRotationMatrix.m01 = Float.parseFloat(trim);
		}
   }
   protected XMLRunnable__matrix12_End createXMLRunnable__matrix12_End() { return new XMLRunnable__matrix12_End(); }

   protected class XMLRunnable__matrix13_End extends XMLRunnable       
   {
		public void run() {
			final String trim = buf.trim();			
			currentRotationMatrix.m02 = Float.parseFloat(trim);
		}
   }
   protected XMLRunnable__matrix13_End createXMLRunnable__matrix13_End() { return new XMLRunnable__matrix13_End(); }

   protected class XMLRunnable__matrix21_End extends XMLRunnable       
   {
		public void run() {
			final String trim = buf.trim();			
			currentRotationMatrix.m10 = Float.parseFloat(trim);
		}
   }
   protected XMLRunnable__matrix21_End createXMLRunnable__matrix21_End() { return new XMLRunnable__matrix21_End(); }

   protected class XMLRunnable__matrix22_End extends XMLRunnable       
   {
		public void run() {
			final String trim = buf.trim();			
			currentRotationMatrix.m11 = Float.parseFloat(trim);
		}
   }
   protected XMLRunnable__matrix22_End createXMLRunnable__matrix22_End() { return new XMLRunnable__matrix22_End(); }

   protected class XMLRunnable__matrix23_End extends XMLRunnable       
   {
		public void run() {
			final String trim = buf.trim();			
			currentRotationMatrix.m12 = Float.parseFloat(trim);
		}
   }
   protected XMLRunnable__matrix23_End createXMLRunnable__matrix23_End() { return new XMLRunnable__matrix23_End(); }

   protected class XMLRunnable__matrix31_End extends XMLRunnable       
   {
		public void run() {
			final String trim = buf.trim();			
			currentRotationMatrix.m20 = Float.parseFloat(trim);
		}
   }
   protected XMLRunnable__matrix31_End createXMLRunnable__matrix31_End() { return new XMLRunnable__matrix31_End(); }

   protected class XMLRunnable__matrix32_End extends XMLRunnable       
   {
		public void run() {
			final String trim = buf.trim();			
			currentRotationMatrix.m21 = Float.parseFloat(trim);
		}
   }
   protected XMLRunnable__matrix32_End createXMLRunnable__matrix32_End() { return new XMLRunnable__matrix32_End(); }

   protected class XMLRunnable__matrix33_End extends XMLRunnable       
   {
		public void run() {
			final String trim = buf.trim();			
			currentRotationMatrix.m22 = Float.parseFloat(trim);
		}
   }
   protected XMLRunnable__matrix33_End createXMLRunnable__matrix33_End() { return new XMLRunnable__matrix33_End(); }
   
   protected class XMLRunnable__vector1__End extends XMLRunnable
   {
		public void run() {
			final String trim = buf.trim();			
			currentTranslationVector.x = Float.parseFloat(trim);
		}
   }
   protected XMLRunnable__vector1__End createXMLRunnable__vector1__End() { return new XMLRunnable__vector1__End(); }
   
   protected class XMLRunnable__vector2__End extends XMLRunnable
   {
		public void run() {
			final String trim = buf.trim();			
			currentTranslationVector.y = Float.parseFloat(trim);
		}
   }
   protected XMLRunnable__vector2__End createXMLRunnable__vector2__End() { return new XMLRunnable__vector2__End(); }
   
   protected class XMLRunnable__vector3__End extends XMLRunnable
   {
		public void run() {
			final String trim = buf.trim();			
			currentTranslationVector.z = Float.parseFloat(trim);
		}
   }
   protected XMLRunnable__vector3__End createXMLRunnable__vector3__End() { return new XMLRunnable__vector3__End(); }

   protected class XMLRunnable__atom_siteCategory__End extends XMLRunnable
   {
		public void run()
		{           
           curAtom = null;    // flags the end of the atoms.
		}
   }
   protected XMLRunnable__atom_siteCategory__End createXMLRunnable__atom_siteCategory__End()
   { return new XMLRunnable__atom_siteCategory__End(); }
   
   protected class XMLRunnable__type_symbol__End extends XMLRunnable
   {
		public void run() {
			final String trim = buf.trim();               
			curAtom.element = getUnique(trim);
		}
   }
   protected XMLRunnable__type_symbol__End createXMLRunnable__type_symbol__End()
   { return new XMLRunnable__type_symbol__End(); }
   
   class XMLRunnable__group_PDB__End extends XMLRunnable
   {
		public void run()
		{
			final String trim = buf.trim();
           
			isCurrentNonProteinChain = !trim.equals("ATOM");
						// in particular HETATM records, but any other type of atom PDB types
						// (other than ATOM) would fall under the 'non-protein' classification.
           
           curAtom.name = getUnique(trim);
		}
   }
   protected XMLRunnable__group_PDB__End createXMLRunnable__group_PDB__End() { return new XMLRunnable__group_PDB__End(); }
   
   protected class XMLRunnable__label_atom_id__End extends XMLRunnable
   {
		public void run() {
			final String trim = buf.trim();
           
			curAtom.name = getUnique(trim);
		}
   }
   protected XMLRunnable__label_atom_id__End createXMLRunnable__label_atom_id__End()
   { return new XMLRunnable__label_atom_id__End(); }
   
   protected class XMLRunnable__label_comp_id__End extends XMLRunnable
   {
		public void run() {
			final String trim = buf.trim();
           
			curAtom.compound = getUnique(trim);
		}
   }
   protected XMLRunnable__label_comp_id__End createXMLRunnable__label_comp_id__End()
   { return new XMLRunnable__label_comp_id__End(); }
   
   protected class XMLRunnable__label_asym_id__End extends XMLRunnable
   {
		public void run() {
			final String trim = buf.trim();
           
           final String chainId = getUnique(trim);
           
           if (isCurrentNonProteinChain && !nonProteinChainIds.contains(chainId))
           	  nonProteinChainIds.add(chainId);
           
           curAtom.chain_id = chainId;
           
           if (isCurrentNonProteinChain && !previousNdbChainId.equals(chainId))
           {
           	 curGeneratedNdbResidueId = 0;
           	 previousPdbResidueId = "";
           	 				// a new chain has to start a new residue, as well.
           }
           
           previousNdbChainId = chainId;
		}
   }
   protected XMLRunnable__label_asym_id__End createXMLRunnable__label_asym_id__End()
   { return new XMLRunnable__label_asym_id__End(); }

   protected class XMLRunnable__label_seq_id__End extends XMLRunnable
   {
		public void run() {
			final String residueId = buf.trim();
           if (residueId.length() != 0)
           	 curAtom.residue_id = Integer.parseInt(residueId);
           
           else
           	 curAtom.residue_id = Integer.MIN_VALUE;  
           			// the previous quick-fix for this situation was, ++this.curResidueId;
           			// But it caused the mbt's bond calculation to fail. 
		}
   }
   protected XMLRunnable__label_seq_id__End createXMLRunnable__label_seq_id__End()
   { return new XMLRunnable__label_seq_id__End(); }
   
   protected class XMLRunnable__auth_seq_id__End extends XMLRunnable
   {
		public void run() {
			final String trim = buf.trim();
           
           if (isCurrentNonProteinChain && !previousPdbResidueId.equals(trim))
           	 curGeneratedNdbResidueId++;
           
           previousPdbResidueId = trim;
		}
   }
   protected XMLRunnable__auth_seq_id__End createXMLRunnable__auth_seq_id__End()
   { return new XMLRunnable__auth_seq_id__End(); }
   
   protected class XMLRunnable__Cartn_x__End extends XMLRunnable
   {
		public void run() {
			final String trim = buf.trim();          
			curAtom.coordinate[0] = Double.parseDouble(trim);
		}
   }
   protected XMLRunnable__Cartn_x__End createXMLRunnable__Cartn_x__End()
   { return new XMLRunnable__Cartn_x__End(); }
   
   protected class XMLRunnable__Cartn_y__End extends XMLRunnable
   {
		public void run() {
			final String trim = buf.trim();          
			curAtom.coordinate[1] = Double.parseDouble(trim);
		}
   }
   protected XMLRunnable__Cartn_y__End createXMLRunnable__Cartn_y__End()
   { return new XMLRunnable__Cartn_y__End(); }
   
   protected class XMLRunnable__Cartn_z__End extends XMLRunnable
   {
		public void run() {
			final String trim = buf.trim();          
			curAtom.coordinate[2] = Double.parseDouble(trim);
		}
   }
   protected XMLRunnable__Cartn_z__End createXMLRunnable__Cartn_z__End()
   { return new XMLRunnable__Cartn_z__End(); }
   
   protected class XMLRunnable__occupancy__End extends XMLRunnable
   {
		public void run() {
			final String trim = buf.trim();
           
			curAtom.occupancy = Float.parseFloat(trim);
		}
   }
   protected XMLRunnable__occupancy__End createXMLRunnable__occupancy__End()
   { return new XMLRunnable__occupancy__End(); }
   
   protected class XMLRunnable__B_iso_or_equiv__End extends XMLRunnable
   {
		public void run() {
			final String trim = buf.trim();
           
			curAtom.bfactor = Float.parseFloat(trim);
		}
   }
   protected XMLRunnable__B_iso_or_equiv__End createXMLRunnable__B_iso_or_equiv__End()
   { return new XMLRunnable__B_iso_or_equiv__End(); }

   protected class XMLRunnable__Atom_Site__Start extends XMLRunnable
   {
   	public void run()
   	{
			curAtom = createAtom();		// user can override
			curAtom.number = Integer.parseInt(super.attrs.getValue("id"));      // the primary key for this atom.
			curAtom.coordinate = new double[3];
   	}
   }

   protected class XMLRunnable__atom_site__End extends XMLRunnable
   {
		public void run() {
       	// ignore all but the first model, for now.
       	if(currentModelNumber != 1 && currentModelNumber != -1)
       		return;
       	
           if (curAtom.residue_id == Integer.MIN_VALUE)
           	  curAtom.residue_id = curGeneratedNdbResidueId;
 
           atomVector.add(curAtom);
		}
   }
   protected XMLRunnable__atom_site__End createXMLRunnable__atom_site__End()
   { return new XMLRunnable__atom_site__End(); }
   
   protected class XMLRunnable__pdbx_PDB_model_num__End extends XMLRunnable
   {
		public void run() {
			final String trim = buf.trim();
			currentModelNumber = Integer.parseInt(trim);
		}
   }
   protected XMLRunnable__pdbx_PDB_model_num__End createXMLRunnable__pdbx_PDB_model_num__End()
   { return new XMLRunnable__pdbx_PDB_model_num__End(); }
   
   protected class XMLRunnable__atom_sites__Start extends XMLRunnable
   {
   	public void run()
   	{
			setParsingFlag(eIsParsing.ATOM_SITES);
			fractionalTransformation = new ModelTransformationMatrix();
			fractionalTransformation.init();
   	}
   }
   protected XMLRunnable__atom_sites__Start createXMLRunnable__atom_sites__Start()
   { return new XMLRunnable__atom_sites__Start(); }
   
   protected class XMLRunnable__atom_sites__End extends XMLRunnable
   {
   	public void run() {
   		fractionalTransformation.values[3] = 0f;
   		fractionalTransformation.values[7] = 0f;
   		fractionalTransformation.values[11] = 0f;
   		fractionalTransformation.values[15] = 1f;
   		
   		fractionalTransformation.printMatrix("fractional transform");
   	}
   }
   protected XMLRunnable__atom_sites__End createXMLRunnable__atom_sites__End()
   { return new XMLRunnable__atom_sites__End(); }

   protected class XMLRunnable__fract_transf_matrix11__End extends XMLRunnable
   {
	   public void run() {
		   fractionalTransformation.values[0] = Float.parseFloat(buf.trim());
	   }
   }
   protected XMLRunnable__fract_transf_matrix11__End createXMLRunnable__fract_transf_matrix11__End()
   { return new XMLRunnable__fract_transf_matrix11__End(); }

   protected class XMLRunnable__fract_transf_matrix12__End extends XMLRunnable
   {
	   public void run() {
		   fractionalTransformation.values[4] = Float.parseFloat(buf.trim());
	   }
   }
   protected XMLRunnable__fract_transf_matrix12__End createXMLRunnable__fract_transf_matrix12__End()
   { return new XMLRunnable__fract_transf_matrix12__End(); }

   protected class XMLRunnable__fract_transf_matrix13__End extends XMLRunnable
   {
	   public void run() {
		   fractionalTransformation.values[8] = Float.parseFloat(buf.trim());
	   }
   }
   protected XMLRunnable__fract_transf_matrix13__End createXMLRunnable__fract_transf_matrix13__End()
   { return new XMLRunnable__fract_transf_matrix13__End(); }

   protected class XMLRunnable__fract_transf_matrix21__End extends XMLRunnable
   {
	   public void run() {
		   fractionalTransformation.values[1] = Float.parseFloat(buf.trim());
	   }
   }
   protected XMLRunnable__fract_transf_matrix21__End createXMLRunnable__fract_transf_matrix21__End()
   { return new XMLRunnable__fract_transf_matrix21__End(); }

   protected class XMLRunnable__fract_transf_matrix22__End extends XMLRunnable
   {
	   public void run() {
		   fractionalTransformation.values[5] = Float.parseFloat(buf.trim());
	   }
   }
   protected XMLRunnable__fract_transf_matrix22__End createXMLRunnable__fract_transf_matrix22__End()
   { return new XMLRunnable__fract_transf_matrix22__End(); }

   protected class XMLRunnable__fract_transf_matrix23__End extends XMLRunnable
   {
	   public void run() {
		   fractionalTransformation.values[9] = Float.parseFloat(buf.trim());
	   }
   }
   protected XMLRunnable__fract_transf_matrix23__End createXMLRunnable__fract_transf_matrix23__End()
   { return new XMLRunnable__fract_transf_matrix23__End(); }

   protected class XMLRunnable__fract_transf_matrix31__End extends XMLRunnable
   {
	   public void run() {
		   fractionalTransformation.values[2] = Float.parseFloat(buf.trim());
	   }
   }
   protected XMLRunnable__fract_transf_matrix31__End createXMLRunnable__fract_transf_matrix31__End()
   { return new XMLRunnable__fract_transf_matrix31__End(); }

   protected class XMLRunnable__fract_transf_matrix32__End extends XMLRunnable
   {
	   public void run() {
		   fractionalTransformation.values[6] = Float.parseFloat(buf.trim());
	   }
   }
   protected XMLRunnable__fract_transf_matrix32__End createXMLRunnable__fract_transf_matrix32__End()
   { return new XMLRunnable__fract_transf_matrix32__End(); }

   protected class XMLRunnable__fract_transf_matrix33__End extends XMLRunnable
   {
	   public void run() {
		   fractionalTransformation.values[10] = Float.parseFloat(buf.trim());
	   }
   }
   protected XMLRunnable__fract_transf_matrix33__End createXMLRunnable__fract_transf_matrix33__End()
   { return new XMLRunnable__fract_transf_matrix33__End(); }

   protected class XMLRunnable__pdbx_poly_seq_scheme__Start extends XMLRunnable
   {
		public void run() {
			curNdbChainId = super.attrs.getValue("asym_id");
			ndbChainIds.add(curNdbChainId);
		}
   }
   protected XMLRunnable__pdbx_poly_seq_scheme__Start createXMLRunnable__pdbx_poly_seq_scheme__Start()
   { return new XMLRunnable__pdbx_poly_seq_scheme__Start(); }
   
   protected class XMLRunnable__pdbx_poly_seq_scheme__End extends XMLRunnable
   {	// handles cases when not all tags were encountered.
		public void run() {
			if(!pdbStrandIdEncountered) {
				buf = emptyString;
				final XMLRunnable runnable = (XMLRunnable)endElementPolyConversionsRunnables.get(xmlPrefix + "pdb_strand_id");
				runnable.run();
			}
			if(!ndbSeqNumEncountered) {
				buf = emptyString;
				final XMLRunnable runnable = (XMLRunnable)endElementPolyConversionsRunnables.get(xmlPrefix + "ndb_seq_num");
				runnable.run();
			}
			if(!pdbSeqNumEncountered) {
				buf = emptyString;
				final XMLRunnable runnable = (XMLRunnable)endElementPolyConversionsRunnables.get(xmlPrefix + "pdb_seq_num");
				runnable.run();
			}
			
			pdbStrandIdEncountered = false;
			ndbSeqNumEncountered = false;
			pdbSeqNumEncountered = false;
		}
   }
   protected XMLRunnable__pdbx_poly_seq_scheme__End createXMLRunnable__pdbx_poly_seq_scheme__End()
   { return new XMLRunnable__pdbx_poly_seq_scheme__End(); }

   protected class XMLRunnable__pdb_strand_id__End extends XMLRunnable
   {
	   public void run() {
		   String trim = buf.trim();
		   trim = getUnique(trim);

		   pdbChainIds.add(trim);

		   pdbStrandIdEncountered = true;
	   }
   }
   protected XMLRunnable__pdb_strand_id__End createXMLRunnable__pdb_strand_id__End()
   { return new XMLRunnable__pdb_strand_id__End(); }

   protected class XMLRunnable__pdbx_poly_seq_schemeCategory__End extends XMLRunnable
   {
	   public void run() {
		   clearParsingFlag(eIsParsing.CONVERSIONS);

		   idConverter.append(pdbChainIds, ndbChainIds, pdbResidueIds, ndbResidueIds);

		   pdbChainIds = null;
		   ndbResidueIds = null;
		   pdbResidueIds = null;
		   curNdbChainId = null;
	   }
   }
   protected XMLRunnable__pdbx_poly_seq_schemeCategory__End createXMLRunnable__pdbx_poly_seq_schemeCategory__End()
   { return new XMLRunnable__pdbx_poly_seq_schemeCategory__End(); }

   protected class XMLRunnable__ndb_seq_num__End extends XMLRunnable
   {
	   public void run() {
		   final String trim = buf.trim();

		   ndbResidueIds.add(new Integer(trim));

		   ndbSeqNumEncountered = true;
	   }
   }
   protected XMLRunnable__ndb_seq_num__End createXMLRunnable__ndb_seq_num__End()
   { return new XMLRunnable__ndb_seq_num__End(); } 

   protected class XMLRunnable__pdb_seq_num__End extends XMLRunnable
   {
	   public void run() {
		   final String trim = buf.trim();

		   pdbResidueIds.add(trim);

		   pdbSeqNumEncountered = true;
	   }
   }
   protected XMLRunnable__pdb_seq_num__End createXMLRunnable__pdb_seq_num__End()
   { return new XMLRunnable__pdb_seq_num__End(); }
           
   protected class XMLRunnable__pdbx_nonpoly_schemeCategory__Start extends XMLRunnable
   {
		public void run() {
			setParsingFlag(eIsParsing.NON_POLY_CONVERSIONS);
           
			pdbChainIds = new Vector<String>();
			ndbChainIds = new Vector<String>();
			pdbResidueIds = new Vector<String>();
			ndbResidueIds = new Vector<Integer>();
		}
   }
   protected XMLRunnable__pdbx_nonpoly_schemeCategory__Start createXMLRunnable__pdbx_nonpoly_schemeCategory__Start()
   { return new XMLRunnable__pdbx_nonpoly_schemeCategory__Start(); }
   
   protected class XMLRunnable__pdbx_nonpoly_schemeCategory__End extends XMLRunnable
   {
		public void run() {
			clearParsingFlag(eIsParsing.NON_POLY_CONVERSIONS);
           
			idConverter.append(pdbChainIds, ndbChainIds, pdbResidueIds, ndbResidueIds);
           
			pdbChainIds = null;
			ndbResidueIds = null;
			pdbResidueIds = null;
			curNdbChainId = null;
		}
   }
   protected XMLRunnable__pdbx_nonpoly_schemeCategory__End createXMLRunnable__pdbx_nonpoly_schemeCategory__End()
   { return new XMLRunnable__pdbx_nonpoly_schemeCategory__End(); }

   protected class XMLRunnable__pdbx_nonpoly_scheme__Start extends XMLRunnable
   {
	   public void run() {
		   curNdbChainId = super.attrs.getValue("asym_id");
		   ndbChainIds.add(curNdbChainId);

		   curPdbChainId = null;

		   ndbResidueIds.add(new Integer(this.attrs.getValue("ndb_seq_num")));
	   }
   }

   protected XMLRunnable__pdbx_nonpoly_scheme__Start createXMLRunnable__pdbx_nonpoly_scheme__Start()
   { return new XMLRunnable__pdbx_nonpoly_scheme__Start(); }

   protected class XMLRunnable__pdb_strand_id__End_np extends XMLRunnable
   {
	   public void run() {
		   String trim = buf.trim();
		   trim = getUnique(trim);

		   curPdbChainId = trim;
	   }
   }
   protected XMLRunnable__pdb_strand_id__End_np createXMLRunnable__pdb_strand_id__End_np()
   { return new XMLRunnable__pdb_strand_id__End_np(); }

   protected class XMLRunnable__pdb_seq_num__End_np extends XMLRunnable
   {
	   public void run() {
		   final String trim = buf.trim();

		   pdbResidueIds.add(trim);
	   }
   }
   protected XMLRunnable__pdb_seq_num__End_np createXMLRunnable__pdb_seq_num__End_np()
   { return new XMLRunnable__pdb_seq_num__End_np(); }

   protected class XMLRunnable_pdbx_nonpoly_scheme__End extends XMLRunnable
   {
	   public void run() {
		   if(curPdbChainId != null) {
			   pdbChainIds.add(curPdbChainId);
		   } else {
			   pdbChainIds.add("");   // many nonpolymers have no chain ids. This is a flag to IdConverter.append().
		   }
	   }
   }
   protected XMLRunnable_pdbx_nonpoly_scheme__End createXMLRunnable_pdbx_nonpoly_scheme__End()
   { return new XMLRunnable_pdbx_nonpoly_scheme__End(); }
    ///
    /// END Overrideable Runnables
    ///

	public boolean canLoad(String name) {
		return true;
	}
	
	public String getLoaderName() {
		return null;
	}
	
	/**
	 * XML is handled differently (but I'm not sure why....)
	 * So, 'load' doesn't do anything.
	 * This may change...
	 */
	public Structure load(String name) {
		return null;
	}
}

class CustomStructure extends Structure {
    //  A hashtable of vectors where
    // each hash KEY is the StructureComponent type String.
    // each hash VALUE is a Vector of StructureComponent objects.
    protected Hashtable<ComponentType, Vector<Atom>> structureComponents = null;

    // To free up the global state for another load call.
    private String localUrlString;

    // public Structure()  Anonymous inner class constructor.
    public CustomStructure(final Hashtable<ComponentType, Vector<Atom>> structureComponents, final String localUrlString) {
        super();
        this.structureComponents = structureComponents;

        this.localUrlString = localUrlString;
    }

    
	@Override
	public String getUrlString( )
    {
        return this.localUrlString;
    }

    
	@Override
	public int getStructureComponentCount( final ComponentType scType )
    {
        final Vector<?> records = this.structureComponents.get( scType );
        if ( records == null ) {
			return 0;
		}
        
        return records.size( );
    }
    
    
	@Override
	public StructureComponent getStructureComponentByIndex( final ComponentType type,
        final int index )
        throws IndexOutOfBoundsException, IllegalArgumentException
    {
        final Vector<?> records = this.structureComponents.get( type );
        if ( records == null ) {
			throw new IllegalArgumentException( "no records of type " + type );
		}

        final StructureComponent structureComponent =
            (StructureComponent) records.elementAt( index );
        structureComponent.structure = this;

        return structureComponent;
    }
}
