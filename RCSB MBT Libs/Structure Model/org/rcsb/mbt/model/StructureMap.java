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
 * Created on 2007/02/08
 *
 */ 
package org.rcsb.mbt.model;


// MBT

// Core
import java.util.*;

import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.*;
import org.rcsb.mbt.model.geometry.ModelTransformationList;
import org.rcsb.mbt.model.geometry.ModelTransformationMatrix;
import org.rcsb.mbt.model.interim.Coil;
import org.rcsb.mbt.model.interim.Conformation;
import org.rcsb.mbt.model.interim.Helix;
import org.rcsb.mbt.model.interim.Strand;
import org.rcsb.mbt.model.interim.Turn;
import org.rcsb.mbt.model.util.*;

/**
 *  After initial loading, the structure is incorporated into a referenceable
 *  structure by this class.
 *  <p>
 *  To do this, it
 *  generates a number of hierarchical links, indexes, and generally provides
 *  access to the numerous relationships that exists between chains,
 *  fragments (secondary structure conformations), residues, atoms, and bonds
 *  for a Structure.</p>
 *  <p>
 *  The map then enables one to "walk" a Structure's
 *  backbone, and finds "gaps" in the map (ie: segments of chains which are not
 *  spanned by Conformation objects). The set of map relationships that are
 *  managed by this class are suitable for applications and viewers to construct
 *  more spacially/biologically meaningful representations and displays.</p>
 *  <IMG style="text-align:center">SRC="doc-files/StructureMap.jpg"/>
 *  <p>
 *  The class provides a number of different access venues to traverse
 *  the underlying Structure data. The one an application should choose
 *  depends mostly on what the application wishes to accomplish. For
 *  example, while a basic sequence viewer might simply walk the raw list
 *  of residues (ie: by calling getResidueCount and getResidue in a loop)
 *  another sequence viewer may want to obtain residues by walking each
 *  chain (ie: by calling getChainCount, plus getChain and chain.getResidue
 *  in a nested loop) so that it knows where the residues of one chain ends
 *  and another begins. Again, its entirely up to the application.</p>
 *  <p>
 *  Additional:  This is contained in the {@linkplain org.rcsb.mbt.model.Structure} class.
 *  			 This class also contains the scene node (sub-scene) for the structure.
 *  			 13-May-08 - rickb</p>
 *  
 *  @author	John L. Moreland
 *  @author rickb (revisions)
 *  @see	org.rcsb.mbt.model.StructureComponent
 *  @see	org.rcsb.mbt.model.StructureComponentRegistry
 */
public class StructureMap
{
	// PROGRAMMER NOTE #1:
	// Eventually, if/when the Structure class adds "set/add/remove" methods,
	// this class could be made to listen for StructureComponentEvent messages
	// and (in response) automatically update the internal map values.

	// PROGRAMMER NOTE #2:
	// Conformation interpretation -
	// a) Although Conformation records contain both start and end chain
	//    identifiers, Conformations never actually cross chain boundaries.
	// b) Since chain idtentifiers are STRING values, there really is
	//    no practical way to compute and iteratively walk intermediate values.
	// c) Start and end residue numbers are always constrained within one chain.
	// d) Residue numbers may be reused (restart at 1) for every chain,
	//    or, may be continuous over the entire structure. So, don't count on
	//    residue numbers for indexing purposes.

	//
	// protected state
	//

	/**
	 * This adds Biologic Unit Transform components to the structure map.
	 * @author rickb
	 *
	 */
	public class BiologicUnitTransforms
	{
		protected ModelTransformationList biologicalUnitGenerationMatrices = null; // <= TransformationMatrix
		
		public class BiologicalUnitGenerationMapByChain extends HashMap<String, ModelTransformationList>
		{
			private static final long serialVersionUID = -1605651855087251797L;
		}
		
		protected BiologicalUnitGenerationMapByChain biologicalUnitGenerationHashByChain = null;
			
		/**
		 * This is a hack to allow an easy-to-use global transformation; it hijacks the biological unit machinery. Any global biological unit transformations will be replaced by a generic identity matrix. Once you have used this function, get the TransformationMatrix object by using (TransformationMatrix)structureMap.getBiologicalUnitGenerationMatricesByVector().get(0); This function is likely to disappear in a future release.
		 *
		 */
		public void generateGlobalTransformationMatrixHACK() {
			ModelTransformationList vec = new ModelTransformationList(1);
			ModelTransformationMatrix matrix = new ModelTransformationMatrix();
			matrix.init();
			matrix.setIdentity();
			
			vec.add(matrix);
			
			this.setBiologicalUnitGenerationMatrices(vec);
		}
	    
		/**
		 * Part of the hack that is set up by generateGlobalTransformationMatrixHACK(). Similarly, this may disappear in a future version.
		 * @return
		 */
		public ModelTransformationMatrix getFirstGlobalTransformationMatrixHACK() {
			return this.biologicalUnitGenerationMatrices.get(0);
		}
		
		public ModelTransformationList getBiologicalUnitGenerationMatrixVector() {
			return this.biologicalUnitGenerationMatrices;
		}
		
		public BiologicalUnitGenerationMapByChain getBiologicalUnitGenerationMatricesByChain() {
			return this.biologicalUnitGenerationHashByChain;
		}

		public void setBiologicalUnitGenerationMatrices(
				final ModelTransformationList biologicalUnitGenerationMatrices)
		{
			if(biologicalUnitGenerationMatrices != null && biologicalUnitGenerationMatrices.size() > 0)
			{
				// if the biological units have chain information, index them.
				if((biologicalUnitGenerationMatrices.get(0)).ndbChainId != null)
				{
					biologicalUnitGenerationHashByChain = new BiologicalUnitGenerationMapByChain();
					
					for(int i = 0; i < biologicalUnitGenerationMatrices.size(); i++)
					{
						final ModelTransformationMatrix mat = biologicalUnitGenerationMatrices.get(i);
						ModelTransformationList vec = biologicalUnitGenerationHashByChain.get(mat.ndbChainId);
						if(vec == null)
						{
							vec = new ModelTransformationList();
							this.biologicalUnitGenerationHashByChain.put(mat.ndbChainId, vec);
						}
						vec.add(mat);
					}
				} else {	// otherwise, these are global transformations; just use the Vector.
					this.biologicalUnitGenerationMatrices = biologicalUnitGenerationMatrices;
				}
			} 
		}
	}

	/**
	 * This adds NonCrystallographicTranslations components to the structure map.
	 * @author rickb
	 *
	 */
	public class NonCrystallographicTransforms
	{
		protected ModelTransformationList nonCrystallographicTranslations = null; // <= NonCrystallographicTranslation

		public ModelTransformationList getNonCrystallographicTranslations() {
			return this.nonCrystallographicTranslations;
		}

		public void setNonCrystallographicTranslations(
				final ModelTransformationList nonCrystallographicTranslations) {
			this.nonCrystallographicTranslations = nonCrystallographicTranslations;
		}
	}
	
	// Stores a reference to the Structure.
	protected Structure structure;

	// Primary StructureMap containers.
	protected Vector<Atom> atoms = null;      // All Atoms in the Stucture.
	protected Vector<Residue> residues = null;   // All Residues in the Stucture.
	protected Vector<Fragment> fragments = null;  // All Fragments in the Stucture.
	protected Vector<Chain> chains = null;     // All Chains in the Stucture.
	protected Vector<Residue> ligands = null;    // Only Ligand Residues.
	protected Vector<Bond> bonds = null;      // All Bond objects added to this StructureMap.
	protected TreeMap<String, Vector<Bond>> calculatedBonds = null;
	protected Set<Bond> bondUniqueness = null;  // Make sure Bond objects are unique.
	protected Hashtable<Atom, Vector<Bond>> atomToBonds = null;  // Find all Bonds connected to each Atom.
	
	protected UnitCell unitCell = null;
	protected BiologicUnitTransforms BUTransforms = null;
	protected NonCrystallographicTransforms NCTransforms = null;
	
	public Vector<Bond> getBonds() { return bonds; }
	public Vector<Chain> getChains() { return chains; }
	public Vector<Fragment> getFragments() { return fragments; }
	public Vector<Residue> getResidues() { return residues; }
	public Vector<Atom> getAtoms() { return atoms; }
	public Vector<Residue> getLigands() { return ligands; }
	
	public BiologicUnitTransforms addBiologicUnitTransforms()
		{ BUTransforms = new BiologicUnitTransforms(); return BUTransforms; }
	
	public boolean hasBiologicUnitTransforms() { return BUTransforms != null; }
	public BiologicUnitTransforms getBiologicUnitTransforms(){ return BUTransforms; }
	public NonCrystallographicTransforms addNonCrystallographicTransforms()
		{ NCTransforms = new NonCrystallographicTransforms(); return NCTransforms; }
	
	public boolean hasNonCrystallographicTransforms() { return NCTransforms != null; }
	public NonCrystallographicTransforms getNonCrystallographicTransforms(){ return NCTransforms; }

	/**
	 * The scene node this structure represents.
	 */
	protected final Object udata;

	// Stores Chain object references by atom.chain_id value.
	protected Hashtable<String, Chain> chainById = null;

	// Stores Residue object references by atom.chain_id+atom_residue_id value.
	protected Hashtable<String, Residue> residueByChainKeyId = null;

	protected PdbToNdbConverter pdbToNdbConverter = null;
	protected Set<String> nonproteinChainIds = null;
	protected Vector<StructureComponent> pdbTopLevelElements = null;
	
	// If a chain_id in the data is every empty, use this value instead
	public static final String defaultChainId = "_";

	// Should bonds be generated using the simple distance method,
	// or using a dictionary and inter-compound bond heuristics?
	protected boolean generateBondsByDistance = false;

	// If the residue does not have a valid alpha atom/index,
	// should we assign a "reasonable" alpha atom/index
	// and "fill in" the disordered residue gaps with random coil?
	protected boolean fillDisorderedGaps = false;

	protected String pdbId = null;

	//
	// Constructors
	//

	/**
	 * Constructs a StructureMap object for a given Structure, containing
	 * a userdata object.
	 */
	public StructureMap( final Structure structure, final Object udata,
			final PdbToNdbConverter in_converter, final Set<String> in_nonproteinChainIds)
	{
		this.udata = udata;
		if ( structure == null ) {
			throw new IllegalArgumentException( "null Structure" );
		}
		this.structure = structure;
		
		nonproteinChainIds = in_nonproteinChainIds;
		
		if (!structure.hasStructureMap())
			structure.setStructureMap(this);
								// inverse construction - happens for derived structureMap types
								// see comments in setStructureMap for further info.

		this.initialize( );

		if ( Status.getOutputLevel() >= Status.LEVEL_DUMP ) {
			print( ); // System.exit(1);
		}
		
		setConverter(in_converter);
	}

	/**
	 * Initialize all of the internal StructureMap state.
	 * @param computeSecondaryStructure  Should secondary structure be computed
	 * (or loaded as it is defined from the source data)?
	 */
	protected void initialize( )
	{
		// All Atoms in the Stucture.
		final int atomCount = this.structure.getStructureComponentCount(
			ComponentType.ATOM );
		if ( atomCount > 0 ) {
			this.atoms = new Vector<Atom>( atomCount );
		} else {
			this.atoms = new Vector<Atom>( );
		}

		// All Residues in the Stucture.
		final int residueCount = this.structure.getStructureComponentCount(
			ComponentType.RESIDUE );
		if ( residueCount > 0 ) {
			this.residues = new Vector<Residue>( residueCount );
		} else {
			this.residues = new Vector<Residue>( );
		}
		this.ligands = new Vector<Residue>( ); // Only Ligand Residues.
		// Residue object references by atom.chain_id+atom_residue_id value.
		// Maps each Atom record to the appropriate Residue.
		this.residueByChainKeyId = new Hashtable<String, Residue>( );

		// All Bonds in the Structure.
		final int bondCount = this.structure.getStructureComponentCount(
			ComponentType.BOND );
		if ( bondCount > 0 ) {
			this.bonds = new Vector<Bond>( bondCount );
		} else {
			this.bonds = new Vector<Bond>( );
		}
		this.bondUniqueness = new HashSet<Bond>( ); // Ensure Bonds are unique.
		this.atomToBonds = new Hashtable<Atom, Vector<Bond>>( );    // All Bonds connected to each Atom.

		// All Chains in the Stucture.
		this.chains = new Vector<Chain>( );
		this.chainById = new Hashtable<String, Chain>( ); // Chains by atom.chain_id.

		// All Fragments in the Stucture.
		this.fragments = new Vector<Fragment>( );

		//
		// Process the Structure to populate this map.
		//

		if ( atomCount > 0 )
		{
			// Its molecule data
			this.processAtomRecords( );
			this.generateFragments( );
			this.extractLigands( );
			this.generateBonds( );
								// Debugging tip: This is a good place to isolate problems - comment out
								// a function or two to see where the problems lie
		}
		else if ( residueCount > 0 )
		{
			// Its sequence data
			this.processResidueRecords( );
		}
		else
		{
			// Its useless data
		}
	}


	//
	// Initialization helper methods (protected)
	//

	/**
	 * Processes the atom records for the Structure and builds the hierarchy.
	 * <P>
	 * <UL>
	 *    <LI>Chains
	 *    <LI>Residues
	 *    <LI>Atoms
	 * </UL>
	 */
	protected void processAtomRecords( )
	{
		int atomCount = this.structure.getStructureComponentCount(
			ComponentType.ATOM );
		
		for ( int i=0; i<atomCount; i++ )
		{
			final Atom atom = (Atom) this.structure.getStructureComponentByIndex(
				ComponentType.ATOM, i );

			String chainKeyId = atom.chain_id + atom.residue_id;

			if (DebugState.isDebug())
				assert(atom.chain_id.length() > 0);
						// with new structure loader paradigm, this should never happen
			
			boolean newChain = false;
			Chain chain = this.chainById.get( atom.chain_id );
			if ( chain == null )
			{
				chain = new Chain( );
				chain.setIsNonProteinChain(nonproteinChainIds.contains(atom.chain_id));
				chain.setStructure( this.structure );

				newChain = true;
				this.chainById.put( atom.chain_id, chain );
			}

			assert(chain != null);
			chainKeyId = atom.chain_id + atom.residue_id;
			
			Residue residue = this.residueByChainKeyId.get( chainKeyId );
			if ( residue == null )
			{
				residue = new Residue( );
				residue.setStructure( this.structure );

				chain.addResidue( residue );
				this.residueByChainKeyId.put( chainKeyId, residue );
			}
			
			assert(residue != null);
			residue.addAtom( atom );

			if (residue.getClassification() == Residue.Classification.LIGAND &&
					 chain.getClassification() != Residue.Classification.LIGAND)
					chain.addModifiedResidue(residue);
						// this means a residue came up unclassified (and got classified
						// as a ligand.)  But the whole chain is *not* a ligand.
						//
						// We're calling this a 'modified residue' at the moment,
						// and we store that in the chain in a list.
						//
						// 31-Dec-08 - rickb
			
			// Need to add the chain to our master list LAST
			// so that it has a valid chain id (that needs a residue and an atom)!
			if ( newChain )
				this.addChain( chain );
			}

		// Walk the tree and build our ordered linear lists (residues, atoms)

		for (Chain chain : chains)
		{		
			if (nonproteinChainIds.contains(chain.getChainId()) && chain.getResidueCount() < 24)
				chain.reClassifyAsLigand();
						// any chain with less than 24 residues is a ligand
			
			for (Residue residue : chain.getResidues())
			{
				residues.add( residue );
				atomCount = residue.getAtomCount( );
				for (Atom atom : residue.getAtoms())
					atoms.add( atom );
			}
		}
	}

	/**
	 * Add a chain, making sure that it is in chain_id order so that it can
	 * be found quicker with a binary search rather than a linear search.
	 */
	protected void addChain( final Chain chain )
	{
		// System.err.println( "StructureMap.addChain: " + chain );

		if ( chain == null ) {
			throw new IllegalArgumentException( "null chain" );
		}

		if ( this.chains.size() < 1 )
		{
			this.chains.add( chain );
			return;
		}

		this.chains.add( chain );

	/*
		// Do a binary search to determine where this new chain should be added.

		String chainId = chain.getChainId( );

		int low = 0;
		int high = chains.size() - 1;
		int mid = 0;
		while ( low <= high )
		{
			mid = (low + high) / 2;
			Chain chain2 = (Chain) chains.elementAt( mid );
			String chainId2 = chain2.getChainId( );

			int chainCompare = chainId.compareTo( chainId2 );
			if ( chainCompare < 0 )
				high = mid - 1;
			else // ( chainCompare > 0 )
				low = mid + 1;
		}

		mid = (low + high) / 2;
		chains.add( mid+1, chain );

		System.err.println( "StructureMap.addChain: after insert " + chainId + " @ " + (mid+1) );
		for ( int c=0; c<chains.size(); c++ )
		{
			Chain chain3 = (Chain) chains.elementAt( c );
			System.err.println( "StructureMap.addChain: " + chain3.getChainId() );
		}
		System.err.println( "StructureMap.addChain: -----------------------------" );
	*/
	}

	/**
	 * Get the number of Conformation records (if any) in the Structure.
	 */
	protected int getConformationCount( )
	{
		int conformationCount = 0;

		conformationCount += this.structure.getStructureComponentCount(
			ComponentType.COIL );
		conformationCount += this.structure.getStructureComponentCount(
			ComponentType.HELIX );
		conformationCount += this.structure.getStructureComponentCount(
			ComponentType.STRAND );
		conformationCount += this.structure.getStructureComponentCount(
			ComponentType.TURN );

		return conformationCount;
	}

	/**
	 * Generate secondary structure fragments for the Structure.
	 * Use Conformation records loaded from the Structure if available.
	 * Otherwise, derive the secondary structure fragments.
	 */
	protected void generateFragments( )
	{
		// Try loading secondary structure from the data,
		// and also apply fragment "cleaning" heuristics.

		if ( this.getConformationCount() > 0 )
		{
			this.loadFragments( );
		}
		else 
		{
			try
			{
				this.deriveFragments( );  // Derive the secondary structure
			}
			catch( final Exception e )
			{
				// JML DEBUG
				// The DerivedInformation class needs to be made more robust.
				// Until then, catch the exceptions and do something graceful.
				e.printStackTrace();
				this.loadFragments( );
			}
		}

		// Tell each chain to regenerate their Fragment objects,
		// and, build the global fragment list.
		final int chainCount = this.getChainCount( );
		for ( int c=0; c<chainCount; c++ )
		{
			final Chain chain = this.getChain( c );
			chain.generateFragments( );
			final int fragmentCount = chain.getFragmentCount( );
			for ( int f=0; f<fragmentCount; f++ )
			{
				final Fragment fragment = chain.getFragment( f );
				this.fragments.add( fragment );
			}
		}
	}


	/** 
	 * Should we "fill in" the disordered residue gaps with random coil
	 * when we load fragments?
	 *  <P>
	 *  @see #getFillDisorderedGaps( )
	 *  <P>
	 */ 
	public void setFillDisorderedGaps( final boolean state )
	{
		this.fillDisorderedGaps = state;
	}


	/** 
	 * Should we "fill in" the disordered residue gaps with random coil
	 * when we load fragments?
	 *  <P>
	 *  @see #setFillDisorderedGaps( boolean state )
	 *  <P>
	 */ 
	public boolean getFillDisorderedGaps( )
	{
		return this.fillDisorderedGaps;
	}


	/** 
	 * Use the Conformation records loaded from the Structure (if available)
	 * to configure the seconary structure fragments for each Chain.
	 *  <P>
	 */ 
	protected void loadFragments( )
	{
		//
		// Clear the fragment map for each chain.
		//

		final int chainCount = this.getChainCount( );
		for ( int c=0; c<chainCount; c++ )
		{
			final Chain chain = this.chains.elementAt( c );
			final int residueCount = chain.getResidueCount( );
			chain.setFragmentRange( 0, residueCount-1, ComponentType.UNDEFINED_CONFORMATION );
		}

		//
		// Process the Structure's Conformation records
		// setting each fragment range for the appropriate chain.
		//

		Conformation conformation = null;

		final int coilCount = this.structure.getStructureComponentCount(
			ComponentType.COIL );
		for ( int i=0; i<coilCount; i++ )
		{
			final Coil coil = (Coil) this.structure.getStructureComponentByIndex(
				ComponentType.COIL, i );
			conformation = coil;

			String chain_id = StructureMap.defaultChainId;
			if ( conformation.start_chain.length() > 0 ) {
				chain_id = conformation.start_chain;
			}
			final Chain chain = this.chainById.get( chain_id );

			final String res = chain_id + conformation.start_residue;
			final Residue startResidue = this.residueByChainKeyId.get( res );
			if ( startResidue == null ) {
				continue;
			}
			final int rIndex = chain.getResidueIndex( startResidue );
			if ( rIndex < 0 ) {
				continue;
			}
			final int range = conformation.end_residue - conformation.start_residue;
			if ( conformation.end_residue < conformation.start_residue )
			{
				Status.output( Status.LEVEL_WARNING, "Skipping reversed conformation record in chain " + chain_id + " at residue " + conformation.start_residue );
				continue;
			}
			if ( (rIndex + range) >= chain.getResidueCount() )
			{
				Status.output( Status.LEVEL_WARNING, "Skipping oversized conformation record in chain " + chain_id + " at residue " + conformation.start_residue );
				continue;
			}

			chain.setFragmentRange( rIndex, rIndex+range, ComponentType.COIL );
		}

		final int helixCount = this.structure.getStructureComponentCount(
			ComponentType.HELIX );
		for ( int i=0; i<helixCount; i++ )
		{
			final Helix helix = (Helix) this.structure.getStructureComponentByIndex(
				ComponentType.HELIX, i );
			conformation = helix;

			String chain_id = StructureMap.defaultChainId;
			if ( conformation.start_chain.length() > 0 ) {
				chain_id = conformation.start_chain;
			}
			final Chain chain = this.chainById.get( chain_id );

			final String res = chain_id + conformation.start_residue;
			final Residue startResidue = this.residueByChainKeyId.get( res );
			if ( startResidue == null ) {
				continue;
			}
			final int rIndex = chain.getResidueIndex( startResidue );
			if ( rIndex < 0 ) {
				continue;
			}
			final int range = conformation.end_residue - conformation.start_residue;
			if ( conformation.end_residue < conformation.start_residue )
			{
				Status.output( Status.LEVEL_WARNING, "Skipping reversed conformation record in chain " + chain_id + " at residue " + conformation.start_residue );
				continue;
			}
			if ( (rIndex + range) >= chain.getResidueCount() )
			{
				Status.output( Status.LEVEL_WARNING, "Skipping oversized conformation record in chain " + chain_id + " at residue " + conformation.start_residue );
				continue;
			}

			chain.setFragmentRange( rIndex, rIndex+range, ComponentType.HELIX );
		}

		final int strandCount = this.structure.getStructureComponentCount(
			ComponentType.STRAND );
		for ( int i=0; i<strandCount; i++ )
		{
			final Strand strand = (Strand) this.structure.getStructureComponentByIndex(
				ComponentType.STRAND, i );
			conformation = strand;

			String chain_id = StructureMap.defaultChainId;
			if ( conformation.start_chain.length() > 0 ) {
				chain_id = conformation.start_chain;
			}
			final Chain chain = this.chainById.get( chain_id );

			final String res = chain_id + conformation.start_residue;
			final Residue startResidue = this.residueByChainKeyId.get( res );
			if ( startResidue == null ) {
				continue;
			}
			final int rIndex = chain.getResidueIndex( startResidue );
			if ( rIndex < 0 ) {
				continue;
			}
			final int range = conformation.end_residue - conformation.start_residue;
			if ( conformation.end_residue < conformation.start_residue )
			{
				Status.output( Status.LEVEL_WARNING, "Skipping reversed conformation record in chain " + chain_id + " at residue " + conformation.start_residue );
				continue;
			}
			if ( (rIndex + range) >= chain.getResidueCount() )
			{
				Status.output( Status.LEVEL_WARNING, "Skipping oversized conformation record in chain " + chain_id + " at residue " + conformation.start_residue );
				continue;
			}

			chain.setFragmentRange( rIndex, rIndex+range, ComponentType.STRAND );
		}

		final int turnCount = this.structure.getStructureComponentCount(
			ComponentType.TURN );
		for ( int i=0; i<turnCount; i++ )
		{
			final Turn turn = (Turn) this.structure.getStructureComponentByIndex(
				ComponentType.TURN, i );
			conformation = turn;

			String chain_id = StructureMap.defaultChainId;
			if ( conformation.start_chain.length() > 0 ) {
				chain_id = conformation.start_chain;
			}
			final Chain chain = this.chainById.get( chain_id );

			final String res = chain_id + conformation.start_residue;
			final Residue startResidue = this.residueByChainKeyId.get( res );
			if ( startResidue == null ) {
				continue;
			}
			final int rIndex = chain.getResidueIndex( startResidue );
			if ( rIndex < 0 ) {
				continue;
			}
			final int range = conformation.end_residue - conformation.start_residue;
			if ( conformation.end_residue < conformation.start_residue )
			{
				Status.output( Status.LEVEL_WARNING, "Skipping reversed conformation record in chain " + chain_id + " at residue " + conformation.start_residue );
				continue;
			}
			if ( (rIndex + range) >= chain.getResidueCount() )
			{
				Status.output( Status.LEVEL_WARNING, "Skipping oversized conformation record in chain " + chain_id + " at residue " + conformation.start_residue );
				continue;
			}

			chain.setFragmentRange( rIndex, rIndex+range, ComponentType.TURN );
		}

		//
		// Examine the residues of each chain to see if any "random coil"
		// fragments need to be added.
		//

		for ( int c=0; c<chainCount; c++ )
		{
			final Chain chain = this.chains.elementAt( c );
			final int residueCount = chain.getResidueCount( );

			// Skip chains that have no real fragments and <2 residues.
			if ( (chain.getFragmentCount() <= 1) && (residueCount < 2) )
			{
				if ( chain.getFragmentType(0) != ComponentType.UNDEFINED_CONFORMATION ) {
					chain.setFragmentRange( 0, residueCount-1, ComponentType.UNDEFINED_CONFORMATION );
				}
				continue;
			}

			// Walk all the residues in the chain. Set any amino or nucleic
			// acid residue having an "alpha" atom but an UNDEFINED
			// conformation assignment to COIL. Residues having missing atom
			// coordinates means it is "disordered" and we will either leave
			// a gap, or fill it in with COIL (see fillDisorderedGaps flag).

			for ( int r=0; r<residueCount; r++ )
			{
				final Residue residue = chain.getResidue( r );

				// Is the residue a ligand/non-polymer?
				if ( (residue.getClassification() != Residue.Classification.AMINO_ACID) &&
			        (residue.getClassification() != Residue.Classification.NUCLEIC_ACID) )
				{
					// Mark ligands/non-polymers as an UNDEFINED fragment.
					chain.setFragmentRange( r, r, ComponentType.UNDEFINED_CONFORMATION );
					continue; // This residue needs no further attention.
				}

				// Does the residue have a valid alpha atom/index?
				if ( residue.getAlphaAtomIndex() >= 0 )
				{
					// The residue has a valid alpha atom/index,
					// but it has no fragment assignment, so assign coil.
					final ComponentType conformationType = residue.getConformationType( );
					if ( conformationType == ComponentType.UNDEFINED_CONFORMATION ) {
						chain.setFragmentRange( r, r, ComponentType.COIL );
					}
					continue; // This residue needs no further attention.
				}
				else
				{
					// The residue has no valid alpha atom/index,
					// should we fill with coil or leave a gap?
					if ( this.fillDisorderedGaps )
					{
						// Assign a "reasonable" alpha atom/index,
						// and fill in the disordered residue gap.
						final int reasonableAtom = residue.getAtomCount() / 2;
						residue.setAlphaAtomIndex( reasonableAtom );
						chain.setFragmentRange( r, r, ComponentType.COIL );
						continue; // This residue needs no further attention.
					}
					else
					{
						// Leave a gap for the disordered residue.
						chain.setFragmentRange( r, r, ComponentType.UNDEFINED_CONFORMATION );
						continue; // This residue needs no further attention.
					}
				}
			}

			//
			// Walk the fragments of the current chain,
			// replacing "short" non-coil fragments with coil.
			//

			for ( int i=0; i<chain.getFragmentCount(); i++ )
			{
				final ComponentType fragmentType = chain.getFragmentType( i );
				final int range0 = chain.getFragmentStartResidue( i );
				final int range1 = chain.getFragmentEndResidue( i );
				final int fragLen = range1 - range0;

				if ( fragLen >= 3 ) {
					continue; // 4 or more residues
				}
				if ( fragmentType == ComponentType.UNDEFINED_CONFORMATION ) {
					continue;
				}

				// Set defined fragments that are "too short" to coil.
				if ( fragmentType != ComponentType.COIL )
				{
					chain.setFragmentRange( range0, range1,
						ComponentType.COIL );
				}

				// If we end up with a single-residue coil that is
				// surrounded by residue numbering gaps or UNDEFINED fragments,
				// then set the fragment to UNDEFINED because it's orphaned
				// and can't be rendered.
				if (
					( fragLen <= 0 ) &&
					( i >= 1 ) &&
					( i < (chain.getFragmentCount()-1) )
				)
				{
					//      i-1           i           i+1
					// |--FRAGMENT--|----COIL----|--FRAGMENT--|
					// |rrrrrrrrrrrR|R-----r----R|Rrrrrrrrrrrr|
					//             A B          C D
					//              ^            ^
					//             GAP?         GAP?

					final int aI = chain.getFragmentEndResidue( i-1 );
					final Residue aR = chain.getResidue( aI );
					final int aId = aR.getResidueId( );
					final ComponentType pT = chain.getFragmentType( i-1 ); // prior type

					final Residue bR = chain.getResidue( range0 );
					final int bId = bR.getResidueId( );

					final Residue cR = chain.getResidue( range1 );
					final int cId = cR.getResidueId( );

					final int dI = chain.getFragmentStartResidue( i+1 );
					final Residue dR = chain.getResidue( dI );
					final int dId = dR.getResidueId( );
					final ComponentType nT = chain.getFragmentType( i+1 ); // next type

					final int abDiff = bId - aId;
					final int cdDiff = dId - cId;

					if (
						( (abDiff>1) || (pT == ComponentType.UNDEFINED_CONFORMATION) )
						&&
						( (cdDiff>1) || (nT == ComponentType.UNDEFINED_CONFORMATION) )
					)
					{
						chain.setFragmentRange( range0, range1,
							ComponentType.UNDEFINED_CONFORMATION );
					}
				}
			}

			//
			// Walk the fragments of the current chain,
			// coalescing contiguous runs of coil (where needed).
			//

			final int range[] = new int[2];
			final int fragmentCount = chain.getFragmentCount( );
			if ( fragmentCount <= 0 ) {
				continue;
			}
			ComponentType savedType = chain.getFragmentType( 0 );
			final int savedRange[] = new int[2];
			savedRange[0] = chain.getFragmentStartResidue( 0 );
			savedRange[1] = chain.getFragmentEndResidue( 0 );
			for ( int i=1; i<chain.getFragmentCount(); i++ )
			{
				final ComponentType fragmentType = chain.getFragmentType( i );
				range[0] = chain.getFragmentStartResidue( i );
				range[1] = chain.getFragmentEndResidue( i );

				// Can we coalese?
				if ( (fragmentType == ComponentType.COIL)
				&& (savedType == ComponentType.COIL) )
				{
					// Are the residue IDs contiguous between fragments?
					final Residue tailRes = chain.getResidue( savedRange[1] );
					final Residue headRes = chain.getResidue( range[0] );
					final int resIdDiff = headRes.getResidueId()
						- tailRes.getResidueId();
					if ( resIdDiff <= 1 )
					{
						// Coalese.
						chain.setFragmentRange( savedRange[0], range[1],
							ComponentType.COIL );
						i--; // Since we effectively removed a fragment.
						savedType = fragmentType;
						// savedRange[0] = range[0];
						savedRange[1] = range[1];
					}
					else
					{
						// Leave a gap.
						chain.setFragmentRange( range[0], range[1], ComponentType.COIL );
						// System.err.println( "StructureMap.loadFragments: chain " + chain.getChainId() + ", residues " + tailRes.getResidueId() + "-GAP-" + headRes.getResidueId() );
						// chain.setFragment( savedRange[1], savedRange[1], ComponentType.UNDEFINED_CONFORMATION );
						// i++; // Since we effectively added a fragment.
						savedType = fragmentType;
						savedRange[0] = range[0];
						savedRange[1] = range[1];
					}
				}
				else
				{
					savedType = fragmentType;
					savedRange[0] = range[0];
					savedRange[1] = range[1];
				}
			}
		}
	}


	/**
	 *  Re-assign this chain's fragment map by deriving secondary structure
	 *  using the Kabsch-Sander algorithm.
	 *  <P>
	 */ 
	protected void deriveFragments( )
	{
		final DerivedInformation derivInfo =
			new DerivedInformation( this.structure, this );
		derivInfo.setConformationType( this.residues );
	}

	/**
	 * Processes residue records (ie: for a sequence).
	 */
	protected void processResidueRecords( )
	{
		final int residueCount = this.structure.getStructureComponentCount(
			ComponentType.RESIDUE );

		if ( residueCount <= 0 ) {
			return;
		}

		final Chain chain = new Chain( );
		chain.setStructure( this.structure );
		this.addChain( chain );
		final String chain_id = chain.getChainId( );
		this.chainById.put( chain_id, chain );

		for ( int r=0; r<residueCount; r++ )
		{
			final Residue residue = (Residue) this.structure.getStructureComponentByIndex(
				ComponentType.RESIDUE, r );

			this.residues.add( residue );
			chain.addResidue( residue );
			final String chainAndResidueId = chain_id + r;
			this.residueByChainKeyId.put( chainAndResidueId, residue );
		}
	}

	/**
	 * Processes the residues for the Structure by picking out the ligands.
	 */
	protected void extractLigands( )
	{
		for ( Residue residue : residues)
			if ( residue.getClassification() == Residue.Classification.LIGAND )
				ligands.add( residue );
	}
	
	/**
	 * This is mostly used for debugging, but also for dumping
	 * 
	 * @param compoundCode
	 * @param bondVector
	 */
	public void markCalculatedBonds(String compoundCode, Vector<Bond> bondVector)
	{
		if (DebugState.isDebug())
		{
			if (calculatedBonds == null)
				calculatedBonds = new TreeMap<String, Vector<Bond>>();
			calculatedBonds.put(compoundCode, bondVector);
		}
	}
	
	public TreeMap<String, Vector<Bond>> getCalculatedBonds()
	{
		return calculatedBonds;
	}

	//
	// StructureMap Methods.
	//

	/**
	 * Returns the Structure object used to construct this StructureMap.
	 */
	public Structure getStructure( )
	{
		return this.structure;
	}


	/**
	 * Return the coordinate bounds for a Structure's atom coordinates.
	 * <P>
	 * These bounding values are used in several parts of the toolkit
	 * including:
	 * <P>
	 * <UL>
	 *    <LI>To compute a default camera view in the StructureViewer.
	 *    <LI>To partition space for bond searching.
	 *    <LI>The structure size, as a statistic, is useful to users.
	 * <UL>
	 * <P>
	 * coordinateBounds[0][0] = min x<BR>
	 * coordinateBounds[0][1] = min y<BR>
	 * coordinateBounds[0][2] = min z<BR>
	 * coordinateBounds[1][0] = max x<BR>
	 * coordinateBounds[1][1] = max y<BR>
	 * coordinateBounds[1][2] = max z<BR>
	 * <P>
	 */
	public double[][] getAtomCoordinateBounds( )
	{
		return AtomStats.getAtomCoordinateBounds( this.structure );
	}

	/**
	 * Return the coordinate average for a Structure's atom coordinates.
	 * <P>
	 * float[0] = x<BR>
	 * float[1] = y<BR>
	 * float[2] = z<BR>
	 * <P>
	 */
	public double[] getAtomCoordinateAverage( )
	{
		return AtomStats.getAtomCoordinateAverage( this.structure );
	}

	/**
	 * Return the total Atom count extracted from the Structure.
	 */
	public int getAtomCount( )
	{
		if ( this.atoms == null ) {
			return 0;
		}
		return this.atoms.size( );
	}


	/**
	 *  Get the Atom at the specified index.
	 *  <P>
	 */
	public Atom getAtom( final int atomIndex )
	{
		if ( this.atoms == null ) {
			return null;
		}
		return this.atoms.elementAt( atomIndex );
	}


	/**
	 *  Get the index of the specified Atom.
	 *  <P>
	 */
	public int getAtomIndex( final Atom atom )
	{
		if ( atom == null ) {
			throw new IllegalArgumentException( "null atom" );
		}
		if ( this.atoms == null ) {
			throw new IllegalArgumentException( "no atoms!" );
		}

		// Do a binary search of the atoms vector.
		int low = 0;
		int high = this.atoms.size() - 1;
		while ( low <= high )
		{
			final int mid = (low + high) / 2;
			Atom atom2 = this.atoms.elementAt( mid );

			// If the atom matches, return the index.
			if ( atom == atom2 )
			{
				return mid;
			}
			else
			{
				final int chainCompare = atom.chain_id.compareTo( atom2.chain_id );
				if ( chainCompare == 0 )
				{
					if ( atom.residue_id == atom2.residue_id )
					{
						// Since we currently don't have an atom number,
						// do a linear search of the residue
						for ( int a=low; a<=high; a++ )
						{
							atom2 = this.atoms.elementAt( a );
							if ( atom == atom2 ) {
								return a;
							}
						}
						// Give up and do the exhastive linear search
						break;
						// return mid;
					}
					else if ( atom.residue_id < atom2.residue_id )
					{
						high = mid - 1;
					}
					else // ( atom.residue_id > atom2.residue_id )
					{
						low = mid + 1;
					}
				}
				else if ( chainCompare < 0 )
				{
					high = mid - 1;
				}
				else // ( chainCompare > 0 )
				{
					low = mid + 1;
				}
			}
		}

		// Try a much more expensive linear search in case the atom is out of order.

		final int atomCount = this.atoms.size();
		for ( int a=0; a<atomCount; a++ )
		{
			final Atom atom2 = this.atoms.elementAt( a );
			if ( atom == atom2 ) {
				return a;
			}
		}

		throw new IllegalArgumentException( "eek, no atom found!" );
		// return -1;
	}


	/**
	 *  Get the Residue object to which this Atom belongs.
	 *  <P>
	 */
	public Residue getResidue( final Atom atom )
	{
		if ( atom == null ) {
			return null;
		}
		if ( this.residueByChainKeyId == null ) {
			return null;
		}

		final String chainAndResidue = atom.chain_id + atom.residue_id;

		return this.residueByChainKeyId.get( chainAndResidue );
	}


	/**
	 *  Get the Residue object for the given chain id and residue id pair.
	 *  <P>
	 */
	public Residue getResidue( final String chainId, final int residueId )
	{
		if ( chainId == null ) {
			throw new NullPointerException( "null chainId" );
		}
		if ( residueId < 0 ) {
			throw new IllegalArgumentException( "negative residueId" );
		}
		if ( residueId >= this.getResidueCount() ) {
			throw new IllegalArgumentException( "residueId out of bounds" );
		}

		final String chainAndResidue = chainId + residueId;
		return this.residueByChainKeyId.get( chainAndResidue );
	}


	/**
	 *  Get the Chain object to which this Atom belongs.
	 *  <P>
	 */
	public Chain getChain( final Atom atom )
	{
		if ( atom == null ) {
			return null;
		}
		if ( this.residueByChainKeyId == null ) {
			return null;
		}

		return this.chainById.get( atom.chain_id );
	}

	/**
	 *  Get the Chain object given its ID.
	 *  <P>
	 */
	public Chain getChain( final String chainId )
	{
		if ( chainId == null ) {
			return null;
		}
		if ( this.chainById == null ) {
			return null;
		}

		return this.chainById.get( chainId );
	}

	/**
	 *  Get a count of bonds contained in this StructureMap.
	 */
	public int getBondCount( )
	{
		return this.bonds.size( );
	}

	/**
	 *  Get the Bond at the specified index.
	 *  <P>
	 */
	public Bond getBond( final int bondIndex )
	{
		return this.bonds.elementAt( bondIndex );
	}


	/**
	 *  Get the index of the specified Bond.
	 *  <P>
	 */
	public int getBondIndex( final Bond bond )
	{
		if ( bond == null ) {
			return -1;
		}
		final int bondCount = this.bonds.size( );
		if ( bondCount < 1 ) {
			return -1;
		}

		final Atom atom0 = bond.getAtom( 0 );
		if ( atom0 == null ) {
			return -1;
		}
		final Atom atom1 = bond.getAtom( 1 );
		if ( atom1 == null ) {
			return -1;
		}

		// Do a binary search to find the Bond index.

		int low = 0;
		int high = bondCount - 1;
		int mid = (low + high) / 2;
		final int bondHashcode = bond.hashCode( );
		Bond bond2 = this.getBond( mid );
		int bond2Hashcode = bond2.hashCode( );
		while ( low <= high )
		{
			mid = (low + high) / 2;
			bond2 = this.getBond( mid );
			bond2Hashcode = bond2.hashCode( );

			if ( bondHashcode < bond2Hashcode )
			{
				high = mid - 1;
			}
			else if ( bondHashcode > bond2Hashcode )
			{
				low = mid + 1;
			}
			else // ( bondHashcode == bond2Hashcode )
			{
				break; // Bond found!
			}
		}
		// mid = (low + high) / 2;

		if ( bondHashcode == bond2Hashcode ) {
			return mid;
		} else {
			return -1;
		}
	}


	/**
	 *  Add a Bond to the StructureMap.
	 *  <P>
	 */
	public void addBond( final Bond bond )
	{
		if ( bond == null ) {
			throw new NullPointerException( "null bond argument" );
		}

		final Atom atom0 = bond.getAtom( 0 );
		final Atom atom1 = bond.getAtom( 1 );
		if ( (atom0 == null) || (atom1 == null) ) {
			throw new IllegalArgumentException( "bond has null atom" );
		}

		// Make sure Bond is unique (note: Bond has custom hashCode method)
		if ( bondUniqueness.contains( bond ))
			return;

		// Keep track of the atom-to-bonds relationship.
		for ( int a=0; a<=1; a++ )  // Examine both atoms of the bond
		{
			final Atom atom = bond.getAtom( a );
			Vector<Bond> atomBonds = this.atomToBonds.get( atom );
			if ( atomBonds == null )
			{
				atomBonds = new Vector<Bond>( );
				this.atomToBonds.put( atom, atomBonds );
			}
			atomBonds.add( bond );
		}

		final int bondCount = this.bonds.size( );
		if ( bondCount < 1 )
		{
			this.bonds.add( bond );
			return;
		}

		// Do a binary search to determine where this new Bond should be added.

		int low = 0;
		int high = bondCount - 1;
		int mid = (low + high) / 2;
		final int bondHashcode = bond.hashCode( );
		Bond bond2 = this.getBond( mid );
		int bond2Hashcode = bond2.hashCode( );
		while ( low <= high )
		{
			mid = (low + high) / 2;
			bond2 = this.getBond( mid );
			bond2Hashcode = bond2.hashCode( );

			if ( bondHashcode < bond2Hashcode )
			{
				high = mid - 1;
			}
			else if ( bondHashcode > bond2Hashcode )
			{
				low = mid + 1;
			}
			else // if ( bondHashcode == bond2Hashcode )
			{
				return; // Bond already added!
			}
		}

		// Add the bond.
		if ( bondHashcode == bond2Hashcode ) {
			return; // Bond already added!
		} else if ( bondHashcode < bond2Hashcode ) {
			this.bonds.add( mid, bond );
		} else {
			this.bonds.add( mid+1, bond );
		}
	}

	/**
	 *  Add a vector of Bond objects to the StructureMap.
	 *  <P>
	 */
	public void addBonds( final Vector<Bond> bondVector )
	{
		if ( bondVector == null ) {
			return;
		}
		final int bvCount = bondVector.size( );
		for ( int i=0; i<bvCount; i++ )
		{
			this.addBond( bondVector.elementAt( i ) );
		}
	}

	/**
	 *  Remove a Bond from the StructureMap.
	 *  <P>
	 */
	public void removeBond( final Bond bond )
	{
		if ( bond == null ) {
			return;
		}
		this.removeBond( this.getBondIndex( bond ) );
	}

	/**
	 *  Remove a Bond from the StructureMap.
	 *  <P>
	 */
	public void removeBond( final int bondIndex )
	{
		// Remove the Bond from the master Vector.
		if ( bondIndex < 0 ) {
			return;
		}
		final Bond bond = this.bonds.elementAt( bondIndex );
		if ( bond == null ) {
			return;
		}
		this.bonds.removeElementAt( bondIndex );

		// Make sure the Bond is removed from the unique hash
		// (note: the Bond has a custom hashCode method).
		this.bondUniqueness.remove( bond );

		// Keep track of the atom-to-bonds relationship.
		for ( int a=0; a<=1; a++ )  // Examine both atoms of the bond
		{
			final Atom atom = bond.getAtom( a );
			final Vector<Bond> atomBonds = atomToBonds.get( atom );
			// Does the atom have any Bond objects connected to it?
			if ( atomBonds != null )
			{
				atomBonds.remove( bond ); // Toss the relationship if it exists.
				if ( atomBonds.size() <= 0 ) {
					this.atomToBonds.remove( atom );
				}
			}
		}
	}

	/**
	 *  Remove all Bond objects from the StructureMap.
	 *  <P>
	 */
	public void removeAllBonds( )
	{
		// Since we are taking a short cut below by blowing away the data directly,
		// we will eventually have to generate events for Bond in "bonds" here...
		this.bonds.clear( );
		this.atomToBonds.clear( );
		this.bondUniqueness.clear( );
	}

	/**
	 *  Return a Vector of all Bond objects connected to the given Atom object.
	 *  NOTE: The Vector returned here is the internal copy (for speed purposes),
	 *  so DO NOT MODIFY THE VECTOR!
	 *  <P>
	 */
	public Vector<Bond> getBonds( final Atom atom )
	{
		return atomToBonds.get( atom );
	}

	/**
	 *  Return a Vector of all Bond objects connected to the given Atom objects.
	 *  <P>
	 */
	public Vector<Bond> getBonds( final Vector<Atom> atomVector )
	{
		final Set<Bond> uniqueBonds = new HashSet<Bond>( );

		final int atomCount = atomVector.size( );
		for ( int a=0; a<atomCount; a++ )
		{
			final Atom atom = atomVector.elementAt( a );
			final Vector<Bond> aBonds = this.getBonds( atom );
			if ( aBonds != null )
			{
				final int bondCount = aBonds.size( );
				for ( int b=0; b<bondCount; b++ )
				{
					final Bond bond = aBonds.elementAt( b );
					uniqueBonds.add( bond );
				}
			}
		}

		final int uniqueBondCount = uniqueBonds.size();
		final Vector<Bond> atomBonds = new Vector<Bond>( uniqueBondCount );

		for (Bond uniqueBond : uniqueBonds)
			atomBonds.add( uniqueBond );

		return atomBonds;
	}


	/**
	 * Sets whether bonds are generated using the simple distance method,
	 * or using a dictionary and inter-compound bond heuristics.
	 * Callers should then generally make a call to the generateBonds method.
	 *  <P>
	 *  @see #getGenerateBondsByDistance( )
	 *  @see #generateBonds( )
	 *  <P>
	 */
	public void setGenerateBondsByDistance( final boolean state )
	{
		this.generateBondsByDistance = state;
	}


	/**
	 * Gets whether bonds are generated using the simple distance method,
	 * or using a dictionary and inter-compound bond heuristics.
	 *  <P>
	 *  @see #setGenerateBondsByDistance( boolean state )
	 *  @see #generateBonds( )
	 *  <P>
	 */
	public boolean getGenerateBondsByDistance( )
	{
		return this.generateBondsByDistance;
	}


	/**
	 *  Remove all existing bonds for the structure and generate a new set.
	 *  Bonds may generated using the simple distance method,
	 *  or using a dictionary and inter-compound bond heuristics
	 *  based upon the state set by the setGenerateBondsByDistance method
	 *  (the default is by distance).
	 *  <P>
	 *  @see #setGenerateBondsByDistance( boolean state )
	 *  @see #getGenerateBondsByDistance( )
	 *  <P>
	 */
	public void generateBonds( )
	{
		this.removeAllBonds( );

		//
		// Derive bonds algorithmically.
		//

		if ( this.generateBondsByDistance )
		{
 			// Use the simple distance method.
			this.addBonds( BondFactory.generateCovalentBonds( this.atoms ) );
		}
		else
		{
 			// Use a dictionary and inter-compound bond heuristics.
			BondFactory.generateCovalentBonds( this );
		}

		//
		// Add extra bonds specified by the Structure data.
		//

		final int bondCount = this.structure.getStructureComponentCount(
			ComponentType.BOND );
		if ( bondCount > 0 )
		{
			// Load bonds from the dataset.
			for ( int b=0; b<bondCount; b++ )
			{
				final Bond bond = (Bond) this.structure.getStructureComponentByIndex(
					ComponentType.BOND, b );
				this.addBond( bond );
			}
		}

		//
		// If absolutely no bonds were generated, the user's data
		// might be messed up. Perhap they have a small molecule
		// whos name matches a ligand dictionary name, but the atom
		// names don't match and no bonds are generated.
		// In any case, if no bonds were generated, make one last
		// effort to do so using a pure distance algorithm.
		//

		if ( this.getBondCount() <= 0 )
		{
			this.addBonds( BondFactory.generateCovalentBonds( this.atoms ) );
		}
	}


	/**
	 * Return the ligand (het group) count.
	 */
	public int getLigandCount( )
	{
		if ( this.ligands == null ) {
			return 0;
		}
		return this.ligands.size( );
	}

	/**
	 * Return the Residue for a given ligand index.
	 * <P>
	 */
	public Residue getLigandResidue( final int ligandIndex )
	{
		if ( this.ligands == null ) {
			return null;
		}
		return this.ligands.elementAt( ligandIndex );
	}

	/**
	 * Return the residue count extracted from the Structure.
	 */
	public int getResidueCount( )
	{
		if ( this.residues == null ) {
			return 0;
		}
		return this.residues.size( );
	}

	/**
	 * Return the start Atom index for a given residue index.
	 * <P>
	 */
	public Residue getResidue( final int residueIndex )
	{
		if ( this.residues == null ) {
			return null;
		}
		return this.residues.elementAt( residueIndex );
	}

	/**
	 *  Get the index of the specified Residue.
	 *  <P>
	 */
	public int getResidueIndex( final Residue residue )
		throws IllegalArgumentException
	{
		if ( residue == null ) {
			throw new IllegalArgumentException( "null residue" );
		}
		if ( this.residues == null ) {
			throw new IllegalArgumentException( "no residues" );
		}

		// Do a binary search of the residues vector.
		int low = 0;
		int high = this.residues.size() - 1;
		while ( low <= high )
		{
			final int mid = (low + high) / 2;
			final Residue residue2 = this.residues.elementAt( mid );

			// If the residue matches, return the index.
			if ( residue == residue2 )
			{
				return mid;
			}
			else
			{
				final String chainId = residue.getChainId( );
				final String chainId2 = residue2.getChainId( );
				if ( (chainId == null) || (chainId2 == null) ) {
					break;
				}
				final int chainCompare = chainId.compareTo( chainId2 );
				if ( chainCompare == 0 )
				{
					final int residueId = residue.getResidueId( );
					final int residueId2 = residue2.getResidueId( );
					if ( residueId == residueId2 )
					{
						if ( residue == residue2 ) {
							return mid;
						} else {
							break; // Give up and do an exhastive linear search
						}
					}
					else if ( residueId < residueId2 )
					{
						high = mid - 1;
					}
					else // ( residueId > residueId2 )
					{
						low = mid + 1;
					}
				}
				else if ( chainCompare < 0 )
				{
					high = mid - 1;
				}
				else // ( chainCompare > 0 )
				{
					low = mid + 1;
				}
			}
		}

		// Try a much more expensive linear search in case the residue
		// is out of order.

		final int residueCount = this.residues.size();
		for ( int i=0; i<residueCount; i++ )
		{
			final Residue residue2 = this.residues.elementAt( i );
			if ( residue == residue2 ) {
				return i;
			}
		}

		return -1;
	}


	/**
	 * Return the total Fragment count extracted from the Structure.
	 */
	public int getFragmentCount( )
	{
		if ( this.fragments == null ) {
			return 0;
		}
		return this.fragments.size( );
	}

	/**
	 *  Get the Fragment at the specified index.
	 *  <P>
	 */
	public Fragment getFragment( final int fragmentIndex )
	{
		if ( this.fragments == null ) {
			return null;
		}
		return this.fragments.elementAt( fragmentIndex );
	}

	/**
	 *  Get the index of the specified Fragment.
	 *  <P>
	 */
	public int getFragmentIndex( final Fragment fragment )
	{
		if ( fragment == null ) {
			throw new IllegalArgumentException( "null fragment" );
		}
		if ( this.fragments == null ) {
			throw new IllegalArgumentException( "no fragments!" );
		}

		final int fragmentCount = this.fragments.size( );
		for ( int f=0; f<fragmentCount; f++ )
		{
			final Fragment fragment2 = this.fragments.elementAt( f );
			if ( fragment2 == fragment ) {
				return f;
			}
		}

		throw new IllegalArgumentException( "eek, no fragment found!" );
		// return -1;
	}


	/**
	 * Return the chain count extracted from the Structure.
	 */
	public int getChainCount( )
	{
		if ( this.chains == null ) {
			return 0;
		}
		return this.chains.size( );
	}

	/**
	 * Return the Chain at the given chain index.
	 */
	public Chain getChain( final int chainIndex )
	{
		if ( this.chains == null ) {
			return null;
		}
		return this.chains.elementAt( chainIndex );
	}

	/**
	 *  Get the index of the specified Chain.
	 *  <P>
	 */
	public int getChainIndex( final Chain chain )
	{
		if ( this.chains == null ) {
			return -1;
		}
		final int chainCount = this.chains.size( );

		// Do a linear search of the chains vector.
		for ( int i=0; i<chainCount; i++ )
		{
			final Chain chain2 = this.chains.elementAt( i );
			if ( chain == chain2 ) {
				return i;
			}
		}

		return -1;
	}

	//
	// StructureStyles factory.
	//

	/**
	 *  Stores a reference to a StructureStyles object for this StructureMap.
	 *  Only one StructureStyles needs to be created for a given StructureMap,
	 *  because no inter-method-call state is kept by the object.
	 *  Once a single StructureStyles is created, subsequent calls to
	 *  the getStructureStyles method return the same StructureStyles object.
	 */
	protected StructureStyles structureStyles = null;

	/**
	 * Return the StructureStyles object that describes the style (authored)
	 * attributes needed to produce consistant views of StructureMap elements.
	 * <P>
	 */
	public StructureStyles getStructureStyles( )
	{
		if ( this.structureStyles == null ) {
			this.structureStyles = new StructureStyles( this );
		}
		return this.structureStyles;
	}


	/**
	 * Return the number of StructureComponent objects specified by type.
	 * <P>
	 */
	public int getStructureComponentCount( final ComponentType type )
	{
		switch (type)
		{
			case ATOM: return this.atoms.size( );
			case RESIDUE: return this.residues.size( );
			case FRAGMENT: return this.fragments.size( );
			case CHAIN: return this.chains.size( );
			case BOND: return this.bonds.size( );
			default: return this.structure.getStructureComponentCount( type );
		}
	}


	/**
	 * Return the StructureComponent object specified by its type and index.
	 * <P>
	 */
	public StructureComponent getStructureComponentByIndex( final ComponentType type, final int index )
	{
		switch (type)
		{
			case ATOM: return this.atoms.elementAt( index );
			case RESIDUE: return this.residues.elementAt( index );
			case FRAGMENT: return this.fragments.elementAt( index );
			case CHAIN: return this.chains.elementAt( index );
			case BOND: return this.bonds.elementAt( index );
			default: return this.structure.getStructureComponentByIndex( type, index );
		}
	}


	/**
	 * Return the StructureComponent parent objects.
	 * Atom->Residue->Fragment->Chain->Structure->null, or,
	 * Bond->{Atom,Atom}->null, or null
	 * 
	 * Typing complication - would like to return an array of
	 * 'StructureComponent' types, but this can return 'Structure',
	 * as well, which does not derive from 'StructureComponent'.
	 * <P>
	 */
	public Vector<Object> getParents( final Object object )
	{
		if ( object == null ) {
			return null;
		}

		if ( object instanceof Structure ) {
			return null;
		}

		if ( ! (object instanceof StructureComponent) ) {
			return null;
		}

		final StructureComponent sc = (StructureComponent) object;
		Vector<Object> parents = null;
		
		switch(sc.getStructureComponentType( ))
		{
			case ATOM:
				parents = new Vector<Object>( );
				parents.add( this.getResidue( (Atom) sc ) );
				break;

			case BOND:
				parents = new Vector<Object>( );
				final Bond bond = (Bond) sc;
				parents.add( bond.getAtom(0) );
				parents.add( bond.getAtom(1) );
				break;

			case RESIDUE:
				parents = new Vector<Object>( );
				parents.add( ((Residue)sc).getFragment() );
				break;

			case FRAGMENT:
				parents = new Vector<Object>( );
				parents.add( ((Fragment)sc).getChain() );
				break;

			case CHAIN:
				parents = new Vector<Object>( );
				parents.add( structure );
				break;
		}

		return parents;
	}


	/**
	 * Return the Structure or StructureComponent children objects.
	 * Structure->Chain->Fragment->Residue->Atom->null, or,
	 * Bond->null.
	 * <P>
	 */
	public Vector<?> getChildren( final Object object )
	{
		if ( object == null ) {
			return null;
		}

		if ( object instanceof Structure ) {
			return getChains( );
		}

		if ( ! (object instanceof StructureComponent) ) {
			return null;
		}

		final StructureComponent sc = (StructureComponent) object;

		switch(sc.getStructureComponentType())
		{
			case CHAIN:  return ((Chain) sc).getFragments( );
			case FRAGMENT: return ((Fragment) sc).getResidues( );
			case RESIDUE: ((Residue) sc).getAtoms( );
			default:
				return null;
		}
	}


	//
	// StructureMap Debug Methods.
	//

	/*
	 * Walk and print the primary StructureMap tree.
	 */
	protected void print( )
	{
		System.err.println( "StructureMap.print: BEGIN" );
		final int chainCount = this.getChainCount( );
		System.err.println( "chainCount = " + chainCount );
		for ( int c=0; c<chainCount; c++ )
		{
			final Chain chain = this.getChain( c );
			System.err.println(
				"chain " + c + " = { " +
				"id=" + chain.getChainId( ) + ", " +
				"classification=" + chain.getClassification( ) + ", " +
				"residueCount=" + chain.getResidueCount( ) +
				"}"
			);

			final int residueCount = chain.getResidueCount( );
			for ( int r=0; r<residueCount; r++ )
			{
				final Residue residue = chain.getResidue( r );
				System.err.println(
					"   residue " + r + " = { " +
					"compoundCode=" + residue.getCompoundCode( ) + ", " +
					"hydrophobicity=" + residue.getHydrophobicity( ) + ", " +
					"alphaAtomIndex=" + residue.getAlphaAtomIndex( ) + ", " +
					"conformationType=" + residue.getConformationType( ) + ", " +
					"atomCount=" + residue.getAtomCount( ) +
					"}"
				);

				final int a = residue.getAlphaAtomIndex( );
				final Atom atom = residue.getAlphaAtom( );
				if ( atom != null ) {
					System.err.println(
						"      atom " + a + " = { " +
						"element=" + atom.element + ", " +
						"coordinate=[" + atom.coordinate[0] +
						"," + atom.coordinate[1] +
						"," + atom.coordinate[2] + "]" +
						"}"
					);
				}
			}
		}

		final int bondCount = this.getBondCount( );
		System.err.println( "bondCount = " + bondCount );
		for ( int b=0; b<bondCount; b++ )
		{
			final Bond bond = this.getBond( b );
			final Atom atom0 = bond.getAtom( 0 );
			final Atom atom1 = bond.getAtom( 1 );
			System.err.println( "bond: " + atom0.number + " - " + atom1.number );
		}

		System.err.println( "StructureMap.print: END" );
	}

	/**
	 * Retrieve the user data object.
	 * Note the caller has to cast.
	 * @return
	 */
	public Object getUData() {
		return this.udata;
		/*
		 * I've racked my head trying to figure out how to do this without the user having
		 * to cast the result, and I come up empty.
		 * 
		 * Tried to define the class as a generic, and it works in principal, 
		 * but the problem is this is carried by 'Structure',
		 * and Structure needs to return it, but Structure doesn't know what type the
		 * generic should be, and so we're back to square one.
		 * 
		 * Another notion would be to have the application subclass this class, and create
		 * a hiding retriever that would return the proper type.  That's ok, but then the
		 * application has to know and reference the derived type everywhere, just to
		 * access this piece of data.  Seems like overkill.
		 * 
		 * So, I've had to sprinkle the code that uses this with casts.  Yuck.
		 * 
		 * 03-Oct-08 - rickb
		 */
	}
	
	public PdbToNdbConverter getPdbToNdbConverter() {
		return this.pdbToNdbConverter;
	}

	public void setPdbToNdbConverter(final PdbToNdbConverter pdbToNdbonverter) {
		this.pdbToNdbConverter = pdbToNdbonverter;
	}
	
	/**
     * @param converter The converter to set.
     * Note: structure must already be set.
     */
    public void setConverter(final PdbToNdbConverter converter) {
    	this.pdbToNdbConverter = converter;
        
        /*
         * Data structure: 
         *  TreeMap byPdbId {
         *      key: String pdbId
         *      value: Vector residues <= Residue
         *  }
         */
        final TreeMap<String, Vector<Residue>> byPdbId = new TreeMap<String, Vector<Residue>>();
        final Vector<Residue> waterResidues = new Vector<Residue>();
        final Vector<Residue> nonProteinResidues = new Vector<Residue>();
        
        for(int i = this.getResidueCount() - 1; i >= 0; i--) {
            final Residue r = this.getResidue(i);
        	System.out.println("StructureMap: residue" + r.getCompoundCode());
            final Object[] pdbIds = converter.getPdbIds(r.getChainId(), new Integer(r.getResidueId()));
            if(pdbIds == null) {
            	System.out.println("StructureMap: pdbId == null for residue" + r.getCompoundCode());
				continue;
			}
            final String pdbChainId = (String)pdbIds[0];

            if (r.getClassification() == Residue.Classification.WATER) {
                waterResidues.add(r); 
                continue;
            } else if (r.getClassification() != Residue.Classification.AMINO_ACID &&
            		r.getClassification() != Residue.Classification.NUCLEIC_ACID) {
            	nonProteinResidues.add(r);
            	continue;
            }
            
            Vector<Residue> residues = byPdbId.get(pdbChainId);
            if(residues == null) {
                residues = new Vector<Residue>();
                byPdbId.put(pdbChainId, residues);
            }
            
            residues.add(r);
        }
        
        pdbTopLevelElements = new Vector<StructureComponent>();
        
        final Comparator<Residue> residueComparator = new Comparator<Residue>() {
            public int compare(Residue r1, Residue r2) {
                return (r1.getResidueId() - r2.getResidueId());
            }
        };
        
        for (String pdbId : byPdbId.keySet())
        {
        	Vector<Residue> residues = byPdbId.get(pdbId);
            if (pdbId != null && pdbId.length() != 0)
            { 
                Collections.sort(residues, residueComparator);
                residues.trimToSize();
                
                ExternChain c = ExternChain.createBasicChain(pdbId, residues);               
                this.pdbTopLevelElements.add(c);
            }
            
            else
                for (Residue r : residues)
                    this.pdbTopLevelElements.add(r);
        }
        
        Collections.sort(waterResidues, residueComparator);
        Collections.sort(nonProteinResidues, residueComparator);
        
        if(waterResidues.size() != 0) {
        	waterResidues.trimToSize();
            final ExternChain waterChain = ExternChain.createWaterChain(waterResidues);
            this.pdbTopLevelElements.add(waterChain);
        }
        
        if(nonProteinResidues.size() != 0) {
            nonProteinResidues.trimToSize();
            final ExternChain nonProteinChain = ExternChain.createMiscellaneousMoleculeChain(nonProteinResidues);
            this.pdbTopLevelElements.add(nonProteinChain);
        }
        
        this.pdbTopLevelElements.trimToSize();
    }
    



    /**
     * @return Returns the nonproteinChainIds.
     */
    public boolean isNonproteinChainId( final String chainId) {
        return this.nonproteinChainIds.contains(chainId);
    }


    /**
     * @param nonproteinChainIds The nonproteinChainIds to set.
     */
    public void setNonproteinChainIds( Set<String> in_nonProteinChainIds )
    {
        nonproteinChainIds = in_nonProteinChainIds;
    }
    
    public StructureComponent getTopPdbComponent(final Residue r)
    {
		for (StructureComponent next : pdbTopLevelElements)
			if (next instanceof ExternChain)
				if (((ExternChain)next).contains(r)) return next;
		
		return null;
	}

	public String getPdbId() {
		return this.pdbId;
	}

	public void setPdbId(final String pdbId) {
		this.pdbId = pdbId;
	}

	public Vector<StructureComponent> getPdbTopLevelElements() {
		return this.pdbTopLevelElements;
	}

	public void setPdbTopLevelElements(final Vector<StructureComponent> pdbTopLevelElements) {
		this.pdbTopLevelElements = pdbTopLevelElements;
	}

	protected boolean isImmutable = false;
	
	/**
	 * Meant to clean up memory after the map has been created. Warning: only call this if you do not intend to call further mutator functions for this class. Behavior is undefined, for example, if you attempt to add an atom after calling this function. 
	 *
	 */
    public void setImmutable() {
    	if(!this.isImmutable) {
    		this.isImmutable = true;
    		this.bondUniqueness = null;
    	}
    }

	public UnitCell getUnitCell() {
		return this.unitCell;
	}

	public void setUnitCell(final UnitCell unitCell) {
		this.unitCell = unitCell;
	}
}

