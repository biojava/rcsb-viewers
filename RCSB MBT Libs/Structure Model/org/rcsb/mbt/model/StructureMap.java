//  $Id: StructureMap.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
//
//  Copyright 2000-2004 The Regents of the University of California.
//  All Rights Reserved.
//
//  Permission to use, copy, modify and distribute any part of this
//  Molecular Biology Toolkit (MBT)
//  for educational, research and non-profit purposes, without fee, and without
//  a written agreement is hereby granted, provided that the above copyright
//  notice, this paragraph and the following three paragraphs appear in all
//  copies.
//
//  Those desiring to incorporate this MBT into commercial products
//  or use for commercial purposes should contact the Technology Transfer &
//  Intellectual Property Services, University of California, San Diego, 9500
//  Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815,
//  FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
//
//  IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
//  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//  LOST PROFITS, ARISING OUT OF THE USE OF THIS MBT, EVEN IF THE
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//  THE MBT PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
//  UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
//  UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
//  NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
//  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
//  MBT WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
//
//  For further information, please see:  http://mbt.sdsc.edu
//
//  History:
//  $Log: StructureMap.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.3  2007/01/03 19:33:49  jbeaver
//  *** empty log message ***
//
//  Revision 1.2  2006/10/04 17:21:06  jbeaver
//  Lots of changes from surfaces to improved picking
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.3  2006/05/16 17:57:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.2  2006/04/14 23:37:34  jbeaver
//  Update with some (very broken) surface rendering stuff
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.61  2005/11/08 20:58:12  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.60  2005/06/21 19:48:43  moreland
//  If we end up with a single-residue coil that is surrounded by residue
//  numbering gaps or UNDEFINED fragments, then set the fragment to UNDEFINED
//  because it's orphaned and can't be rendered.
//
//  Revision 1.59  2005/06/17 21:45:36  moreland
//  PDB-derived mmCIF files that set _struct_asym.pdbx_blank_PDB_chainid_flag
//  now cause a subsitution of "_" for all asym_id field values (as per RCSB).
//
//  Revision 1.58  2005/04/25 22:13:11  moreland
//  Improved heuristics for handling fragment assignments of polymers having no CA
//  and for non-polymers.
//
//  Revision 1.57  2005/04/21 23:04:34  moreland
//  Modified the loadFrgments method to replace "short" fragments with coil.
//
//  Revision 1.56  2005/03/15 02:28:23  moreland
//  Added getResidue method variant that takes chain id and residue id arguments.
//
//  Revision 1.55  2005/01/31 17:01:53  moreland
//  Renamed "enum" variable in preparation for evntual jdk1.5 migration.
//
//  Revision 1.54  2004/10/27 20:03:30  moreland
//  Corrected javadoc SEE references.
//
//  Revision 1.53  2004/08/19 19:43:33  moreland
//  Added getStructureComponentCount method.
//  Added getStructureComponentByIndex method.
//
//  Revision 1.52  2004/07/01 20:04:40  moreland
//  Added thrown exceptions to "addBond" method for null arguments.
//
//  Revision 1.51  2004/06/23 00:48:51  moreland
//  When residues have no atoms, we can't do a binary search using chainId,
//  so break and do a linear search. Mostly needed for pure sequence data.
//
//  Revision 1.50  2004/06/23 00:40:53  moreland
//  Added "getAtoms" method which returns a Vector CLONE of Atom references.
//  Fixed a bug in the "loadFragments" method which left out many coil segments.
//
//  Revision 1.49  2004/06/01 16:01:43  moreland
//  If fragments can't be derived for some reason, then try generating coils.
//  If no bonds are generated using the dictionary, then fall back to distances.
//
//  Revision 1.48  2004/05/13 17:32:22  moreland
//  Re-ordered intialization code to make it more organized and readable.
//  Added support to load "additional" Bond objects from the data.
//
//  Revision 1.47  2004/05/05 19:12:41  moreland
//  Changed default values for generateBondsByDistance and fillDisorderedGaps.
//  Made sure that non-amino/nucleic acids are marked as UNDEFINED fragments.
//
//  Revision 1.46  2004/05/04 19:54:01  moreland
//  Simplified and optimized loadFragments method and added disorder support.
//
//  Revision 1.45  2004/05/03 17:36:50  moreland
//  Added get/setGenerateBondsByDistance methods to control generateBonds behavior.
//  The loadFragments method now handles disordered residues (no "CA" atom).
//
//  Revision 1.44  2004/04/29 23:07:51  moreland
//  In loadFragments, if chain_id is empty, substitute defaultChainId ("_").
//  Reverted to distance-based bond generation until ChemComp dictionary is fixed.
//
//  Revision 1.43  2004/04/15 20:43:14  moreland
//  Changed to new UCSD copyright statement.
//  Optimized getBondIndex, addBond and removeBond methods.
//  Added BondFactory.improveCovalentBonds call to generateBonds method.
//
//  Revision 1.42  2004/02/12 17:55:27  moreland
//  Corrected typo in loadFragments method which was preventing coil growth.
//
//  Revision 1.41  2004/02/05 18:36:35  moreland
//  Now computes distances using the Algebra class methods.
//
//  Revision 1.40  2004/01/30 22:47:56  moreland
//  Added more detail descriptions for the class block comment.
//
//  Revision 1.39  2004/01/30 21:23:56  moreland
//  Added new diagrams.
//
//  Revision 1.38  2004/01/29 17:08:16  moreland
//  Updated copyright and class block comments.
//
//  Revision 1.37  2004/01/17 00:45:34  moreland
//  After loading conformations, coalesce contiguous runs of coil fragments.
//
//  Revision 1.36  2004/01/16 23:04:21  moreland
//  Added code to warn-about/skip reversed conformation records.
//  Added code to warn-about/skip oversized conformation records.
//
//  Revision 1.35  2004/01/16 18:13:24  moreland
//  Corrected index bug when single-residue fragments were replaced with coil.
//
//  Revision 1.34  2004/01/15 17:13:11  moreland
//  Removed debug print statement.
//
//  Revision 1.33  2004/01/15 00:52:37  moreland
//  Moved code that requests that each chain regenerate Fragment objects
//  to generateFragments method to make loaded and derived methods consistent.
//
//  Revision 1.32  2003/12/20 01:03:44  moreland
//  Cleaned up formatting a bit.
//
//  Revision 1.31  2003/12/09 21:19:50  moreland
//  Now throws an IllegalArgumentException if the Structure argument to the contructor is null.
//
//  Revision 1.30  2003/11/20 21:33:52  moreland
//  Added code to fill a new fragments Vector.
//  Added getFragmentCount, getFragment, and getFragmentIndex access methods.
//
//  Revision 1.29  2003/10/23 22:05:38  moreland
//  Changed initialize and print method from public to protected methods.
//
//  Revision 1.28  2003/10/17 18:19:56  moreland
//  Fixed a javadoc comment.
//
//  Revision 1.27  2003/10/06 23:12:44  moreland
//  Cleaned up code to generate Fragments in StructureMap so that Fragments are set
//  as complete ranges (instead of individual residues - which didn't work well).
//
//  Revision 1.26  2003/10/01 21:19:24  agramada
//  Added code to create fragments according to the output from the Kabsch-Sander
//  algorithm.
//
//  Revision 1.25  2003/09/16 17:18:22  moreland
//  Added code to enable secondary structure generation from data VS derivation.
//
//  Revision 1.24  2003/09/11 19:41:41  moreland
//  Added a getChain method variant that takes a chainId argument.
//
//  Revision 1.23  2003/07/17 23:14:45  moreland
//  The getBonds(atomVector) now returns a Vector of UNIQUE Bond objects.
//
//  Revision 1.22  2003/07/17 22:54:39  moreland
//  Fixed trivial bug in getBondIndex method which always returned a -1 index value. Oops!
//
//  Revision 1.21  2003/07/11 23:06:26  moreland
//  Covalent Bonds are now generated and added at construction time.
//  Added bonds are kept sorted by both Atom's numbers for retrieval performance.
//  Two "getBonds" methods return a Bond Vector given one or multiple Atom objects.
//
//  Revision 1.20  2003/04/30 17:53:31  moreland
//  Added addChain method to add chains in sorted order.
//  The getAtomIndex method now does a binary search.
//  The getResidueIndex method now does a binary search.
//
//  Revision 1.19  2003/04/24 17:14:34  moreland
//  Enabled the processConformations to call resetFragments for each chain.
//
//  Revision 1.18  2003/04/23 17:56:04  moreland
//  Completely rewrote this class from scratch to use an direct object hierarchy
//  rather than a index/table based model. The excentricities of data and relations
//  were too problematic to maintain a complex cross-referencing index based model.
//
//  Revision 1.17  2003/04/03 18:18:51  moreland
//  Changed Atom field "type" to "element" due to naming and meaning conflict.
//  Changed ATOM_MAP_ and TYPES[] fields from public to protected,
//  and removed the "getAtomMapFlags" method.
//
//  Revision 1.16  2003/03/14 21:08:18  moreland
//  Divided state initialization code into separate methods in prepration to
//  eventually add computated secondary structure code.
//  Also added support for extracting ligands.
//
//  Revision 1.15  2003/03/10 23:25:54  moreland
//  Moved "chain classification" comment to appear after the overview diagrams.
//
//  Revision 1.14  2003/03/10 22:52:08  moreland
//  Changed getLeadingCaAtomIndex and getTrailingCaAtomIndex method names
//  to getLeadingAlphaIndex and getTrailingAlphaIndex in order to reflect
//  support for C-Alpha (amino acid) and P-alpha (nucleic acid) chain
//  primary backbone atoms.
//  The "residues" array's "C-alpha" index is now just the "alpha" index.
//  The "residues" array is now fully intialized with -1 values.
//  Added the "ligands" array to support mapping of ligands (het groups).
//  Added methods to return ligandCount and ligand index values.
//  When a residue has one atom (eg: O=water), the end index is now set properly.
//
//  Revision 1.13  2003/03/07 20:25:19  moreland
//  Added support to provide an atom coordinate average.
//
//  Revision 1.12  2003/02/21 21:55:30  moreland
//  Added bounding box computation for a Structure's atom coordinates.
//  Added start_atom and end_atom elements to "chains" array.
//  Added methods to get atom start/stop indexes for a residue or a chain.
//  Added support for generating a bond list.
//
//  Revision 1.11  2003/02/07 17:33:29  moreland
//  Fixed inter-chain fragment split bug.
//
//  Revision 1.10  2003/02/03 22:49:51  moreland
//  Added support for Status message output.
//
//  Revision 1.9  2003/01/22 18:18:57  moreland
//  Commented out debug call to printTables method.
//
//  Revision 1.8  2003/01/17 22:16:29  moreland
//  Corrected getTrailingAlphaAtomIndex and getLeadingAlphaAtomIndex method
//  index calculations.
//
//  Revision 1.7  2003/01/17 02:28:01  moreland
//  Fixed the "chains" and "fragments" index calculations in the constructor.
//  Still need to re-test the operation of the other methods in the class...
//
//  Revision 1.6  2003/01/07 19:35:45  moreland
//  Changed assignment algorithm for conformation records to use the residues
//  table for finding gaps instead of atomMap flags.
//
//  Revision 1.5  2002/12/20 22:27:53  moreland
//  Fixed bug in computing end atom index for gap fragments.
//
//  Revision 1.4  2002/12/19 21:12:25  moreland
//  Oops. Fixed a cut and paste error when calling getType method during
//  the Conformation pass.
//
//  Revision 1.3  2002/12/17 19:19:14  moreland
//  Added public and protected overview diagrams.
//
//  Revision 1.2  2002/12/16 18:28:10  moreland
//  Updated getLeadingAlphaIndex and getTrailingAlphaIndex methods (still need testing!)
//
//  Revision 1.1  2002/12/16 06:31:06  moreland
//  Added new class to enable vital derived data to be built from Structure objects.
//  This class, in fact, forms the basis for many new/upcomming StructureDocument
//  and Viewer features and capabilities.
//
//  Revision 1.0  2002/11/14 18:47:33  moreland
//  Corrected "see" document reference.
//


package org.rcsb.mbt.model;


// MBT

// Core
import java.util.*;

import org.rcsb.mbt.controllers.scene.PdbToNdbConverter;
import org.rcsb.mbt.glscene.geometry.UnitCell;
import org.rcsb.mbt.glscene.jogl.JoglSceneNode;
import org.rcsb.mbt.glscene.jogl.TransformationMatrix;
import org.rcsb.mbt.model.attributes.*;
import org.rcsb.mbt.model.util.*;



/**
 *  This class implements a derived data map for a Structure object. It
 *  generates a number of hierarchical links, indexes, and generally provides
 *  access to the numerous relationships that exists between chains,
 *  fragments (secondary structure conformations), residues, atoms, and bonds
 *  for a Structure. The map enables one to "walk" a Structure's
 *  backbone, and finds "gaps" in the map (ie: segments of chains which are not
 *  spanned by Conformation objects). The set of map relationships that are
 *  managed by this class are suitable for applications and viewers to construct
 *  more spacially/biologically meaningful representations and displays.
 *  <P>
 *  <center>
 *  <IMG SRC="doc-files/StructureMap.jpg">
 *  </center>
 *  <P>
 *  This class provides a number of different "entry points" to traverse
 *  the underlying Structure data. The one an application should choose
 *  depeonds mostly on what the application wishes to accomplish. For
 *  example, while a basic sequence viewer might simply walk the raw list
 *  of residues (ie: by calling getResidueCount and getResidue in a loop)
 *  another sequence viewer may want to obtain residues by walking each
 *  chain (ie: by calling getChainCount, plus getChain and chain.getResidue
 *  in a nested loop) so that it knows where the residues of one chain ends
 *  and another begins. Again, its entirely up to the application.
 *  <P>
 *  <HR WIDTH="50%">
 *  <P>
 *  <p>
 *  Additional:  This is contained in the structure component.  The structure
 *  			 also contains the scene node for the structure.
 *  			 13-May-08 - rickb
 *  
 *  @author	John L. Moreland
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
		protected TransformationList biologicalUnitGenerationMatrices = null; // <= TransformationMatrix
		
		public class BiologicalUnitGenerationMapByChain extends HashMap<String, TransformationList>
		{
			private static final long serialVersionUID = -1605651855087251797L;
		}
		
		protected BiologicalUnitGenerationMapByChain biologicalUnitGenerationHashByChain = null;
			
		/**
		 * This is a hack to allow an easy-to-use global transformation; it hijacks the biological unit machinery. Any global biological unit transformations will be replaced by a generic identity matrix. Once you have used this function, get the TransformationMatrix object by using (TransformationMatrix)structureMap.getBiologicalUnitGenerationMatricesByVector().get(0); This function is likely to disappear in a future release.
		 *
		 */
		public void generateGlobalTransformationMatrixHACK() {
			TransformationList vec = new TransformationList(1);
			TransformationMatrix matrix = new TransformationMatrix();
			matrix.init();
			matrix.setIdentity();
			
			vec.add(matrix);
			
			this.setBiologicalUnitGenerationMatrices(vec);
		}
	    
		/**
		 * Part of the hack that is set up by generateGlobalTransformationMatrixHACK(). Similarly, this may disappear in a future version.
		 * @return
		 */
		public TransformationMatrix getFirstGlobalTransformationMatrixHACK() {
			return this.biologicalUnitGenerationMatrices.get(0);
		}
		
		public TransformationList getBiologicalUnitGenerationMatrixVector() {
			return this.biologicalUnitGenerationMatrices;
		}
		
		public BiologicalUnitGenerationMapByChain getBiologicalUnitGenerationMatricesByChain() {
			return this.biologicalUnitGenerationHashByChain;
		}

		public void setBiologicalUnitGenerationMatrices(
				final TransformationList biologicalUnitGenerationMatrices)
		{
			if(biologicalUnitGenerationMatrices != null && biologicalUnitGenerationMatrices.size() > 0)
			{
				// if the biological units have chain information, index them.
				if((biologicalUnitGenerationMatrices.get(0)).ndbChainId != null)
				{
					biologicalUnitGenerationHashByChain = new BiologicalUnitGenerationMapByChain();
					
					for(int i = 0; i < biologicalUnitGenerationMatrices.size(); i++)
					{
						final TransformationMatrix mat = biologicalUnitGenerationMatrices.get(i);
						TransformationList vec = biologicalUnitGenerationHashByChain.get(mat.ndbChainId);
						if(vec == null)
						{
							vec = new TransformationList();
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
		protected TransformationList nonCrystallographicTranslations = null; // <= NonCrystallographicTranslation

		public TransformationList getNonCrystallographicTranslations() {
			return this.nonCrystallographicTranslations;
		}

		public void setNonCrystallographicTranslations(
				final TransformationList nonCrystallographicTranslations) {
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
	protected Set<Bond> bondUniqueness = null;  // Make sure Bond objects are unique.
	protected Hashtable<Atom, Vector<Bond>> atomToBonds = null;  // Find all Bonds connected to each Atom.
	
	protected UnitCell unitCell = null;
	protected BiologicUnitTransforms BUTransforms = null;
	protected NonCrystallographicTransforms NCTransforms = null;
	
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
	protected final JoglSceneNode sceneNode;

	// Stores Chain object references by atom.chain_id value.
	protected Hashtable<String, Chain> chainById = null;

	// Stores Residue object references by atom.chain_id+atom_residue_id value.
	protected Hashtable<String, Residue> residueByChainAndResidueId = null;

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
	 * Constructs a StructureMap object for a given Structure.
	 */
	public StructureMap( final Structure structure, final JoglSceneNode sceneNode )
	{
		this.sceneNode = sceneNode;
		if ( structure == null ) {
			throw new IllegalArgumentException( "null Structure" );
		}
		this.structure = structure;
		
		if (!structure.hasStructureMap())
			structure.setStructureMap(this);
								// inverse construction - happens for derived structureMap types
								// see comments in setStructureMap for further info.

		this.initialize( );

		if ( Status.getOutputLevel() >= Status.LEVEL_DUMP ) {
			this.print( ); // System.exit(1);
		}
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
			StructureComponentRegistry.TYPE_ATOM );
		if ( atomCount > 0 ) {
			this.atoms = new Vector<Atom>( atomCount );
		} else {
			this.atoms = new Vector<Atom>( );
		}

		// All Residues in the Stucture.
		final int residueCount = this.structure.getStructureComponentCount(
			StructureComponentRegistry.TYPE_RESIDUE );
		if ( residueCount > 0 ) {
			this.residues = new Vector<Residue>( residueCount );
		} else {
			this.residues = new Vector<Residue>( );
		}
		this.ligands = new Vector<Residue>( ); // Only Ligand Residues.
		// Residue object references by atom.chain_id+atom_residue_id value.
		// Maps each Atom record to the appropriate Residue.
		this.residueByChainAndResidueId = new Hashtable<String, Residue>( );

		// All Bonds in the Structure.
		final int bondCount = this.structure.getStructureComponentCount(
			StructureComponentRegistry.TYPE_BOND );
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
			StructureComponentRegistry.TYPE_ATOM );

		for ( int i=0; i<atomCount; i++ )
		{
			final Atom atom = (Atom) this.structure.getStructureComponentByIndex(
				StructureComponentRegistry.TYPE_ATOM, i );

			// If the chain_id is empty, replace it by a default value.
			if ( atom.chain_id.length() <= 0 ) {
				atom.chain_id = StructureMap.defaultChainId;
			}

			boolean newChain = false;
			Chain chain = this.chainById.get( atom.chain_id );
			if ( chain == null )
			{
				chain = new Chain( );
				chain.setStructure( this.structure );

				newChain = true;
				this.chainById.put( atom.chain_id, chain );
			}

			final String chainAndResidueId = atom.chain_id + atom.residue_id;
			Residue residue = this.residueByChainAndResidueId.get( chainAndResidueId );
			if ( residue == null )
			{
				residue = new Residue( );
				residue.setStructure( this.structure );
				residue.addAtom( atom );

				chain.addResidue( residue );
				this.residueByChainAndResidueId.put( chainAndResidueId, residue );
			} else {
				residue.addAtom( atom );
			}

			// Need to add the chain to our master list LAST
			// so that it has a valid chain id (that needs a residue and an atom)!
			if ( newChain ) {
				this.addChain( chain );
			}
		}

		// Walk the tree and build our ordered linear lists (residues, atoms)

		final int chainCount = this.getChainCount( );
		for ( int c=0; c<chainCount; c++ )
		{
			final Chain chain = this.getChain( c );
			final int residueCount = chain.getResidueCount( );
			for ( int r=0; r<residueCount; r++ )
			{
				final Residue residue = chain.getResidue( r );
				this.residues.add( residue );
				atomCount = residue.getAtomCount( );
				for ( int a=0; a<atomCount; a++ )
				{
					final Atom atom = residue.getAtom( a );
					this.atoms.add( atom );
				}
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
			StructureComponentRegistry.TYPE_COIL );
		conformationCount += this.structure.getStructureComponentCount(
			StructureComponentRegistry.TYPE_HELIX );
		conformationCount += this.structure.getStructureComponentCount(
			StructureComponentRegistry.TYPE_STRAND );
		conformationCount += this.structure.getStructureComponentCount(
			StructureComponentRegistry.TYPE_TURN );

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
			chain.setFragment( 0, residueCount-1, Conformation.TYPE_UNDEFINED );
		}

		//
		// Process the Structure's Conformation records
		// setting each fragment range for the appropriate chain.
		//

		Conformation conformation = null;

		final int coilCount = this.structure.getStructureComponentCount(
			StructureComponentRegistry.TYPE_COIL );
		for ( int i=0; i<coilCount; i++ )
		{
			final Coil coil = (Coil) this.structure.getStructureComponentByIndex(
				StructureComponentRegistry.TYPE_COIL, i );
			conformation = coil;

			String chain_id = StructureMap.defaultChainId;
			if ( conformation.start_chain.length() > 0 ) {
				chain_id = conformation.start_chain;
			}
			final Chain chain = this.chainById.get( chain_id );

			final String res = chain_id + conformation.start_residue;
			final Residue startResidue = this.residueByChainAndResidueId.get( res );
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

			chain.setFragment( rIndex, rIndex+range, StructureComponentRegistry.TYPE_COIL );
		}

		final int helixCount = this.structure.getStructureComponentCount(
			StructureComponentRegistry.TYPE_HELIX );
		for ( int i=0; i<helixCount; i++ )
		{
			final Helix helix = (Helix) this.structure.getStructureComponentByIndex(
				StructureComponentRegistry.TYPE_HELIX, i );
			conformation = helix;

			String chain_id = StructureMap.defaultChainId;
			if ( conformation.start_chain.length() > 0 ) {
				chain_id = conformation.start_chain;
			}
			final Chain chain = this.chainById.get( chain_id );

			final String res = chain_id + conformation.start_residue;
			final Residue startResidue = this.residueByChainAndResidueId.get( res );
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

			chain.setFragment( rIndex, rIndex+range, StructureComponentRegistry.TYPE_HELIX );
		}

		final int strandCount = this.structure.getStructureComponentCount(
			StructureComponentRegistry.TYPE_STRAND );
		for ( int i=0; i<strandCount; i++ )
		{
			final Strand strand = (Strand) this.structure.getStructureComponentByIndex(
				StructureComponentRegistry.TYPE_STRAND, i );
			conformation = strand;

			String chain_id = StructureMap.defaultChainId;
			if ( conformation.start_chain.length() > 0 ) {
				chain_id = conformation.start_chain;
			}
			final Chain chain = this.chainById.get( chain_id );

			final String res = chain_id + conformation.start_residue;
			final Residue startResidue = this.residueByChainAndResidueId.get( res );
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

			chain.setFragment( rIndex, rIndex+range, StructureComponentRegistry.TYPE_STRAND );
		}

		final int turnCount = this.structure.getStructureComponentCount(
			StructureComponentRegistry.TYPE_TURN );
		for ( int i=0; i<turnCount; i++ )
		{
			final Turn turn = (Turn) this.structure.getStructureComponentByIndex(
				StructureComponentRegistry.TYPE_TURN, i );
			conformation = turn;

			String chain_id = StructureMap.defaultChainId;
			if ( conformation.start_chain.length() > 0 ) {
				chain_id = conformation.start_chain;
			}
			final Chain chain = this.chainById.get( chain_id );

			final String res = chain_id + conformation.start_residue;
			final Residue startResidue = this.residueByChainAndResidueId.get( res );
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

			chain.setFragment( rIndex, rIndex+range, StructureComponentRegistry.TYPE_TURN );
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
				if ( chain.getFragmentType(0) != Conformation.TYPE_UNDEFINED ) {
					chain.setFragment( 0, residueCount-1, Conformation.TYPE_UNDEFINED );
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
				final String classification = residue.getClassification( );
				if ( (classification != Residue.COMPOUND_AMINO_ACID) &&
			        (classification != Residue.COMPOUND_NUCLEIC_ACID) )
				{
					// Mark ligands/non-polymers as an UNDEFINED fragment.
					chain.setFragment( r, r, Conformation.TYPE_UNDEFINED );
					continue; // This residue needs no further attention.
				}

				// Does the residue have a valid alpha atom/index?
				if ( residue.getAlphaAtomIndex() >= 0 )
				{
					// The residue has a valid alpha atom/index,
					// but it has no fragment assignment, so assign coil.
					final String conformationType = residue.getConformationType( );
					if ( conformationType == Conformation.TYPE_UNDEFINED ) {
						chain.setFragment( r, r, StructureComponentRegistry.TYPE_COIL );
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
						chain.setFragment( r, r, StructureComponentRegistry.TYPE_COIL );
						continue; // This residue needs no further attention.
					}
					else
					{
						// Leave a gap for the disordered residue.
						chain.setFragment( r, r, Conformation.TYPE_UNDEFINED );
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
				final String fragmentType = chain.getFragmentType( i );
				final int range0 = chain.getFragmentStartResidue( i );
				final int range1 = chain.getFragmentEndResidue( i );
				final int fragLen = range1 - range0;

				if ( fragLen >= 3 ) {
					continue; // 4 or more residues
				}
				if ( fragmentType == Conformation.TYPE_UNDEFINED ) {
					continue;
				}

				// Set defined fragments that are "too short" to coil.
				if ( fragmentType != StructureComponentRegistry.TYPE_COIL )
				{
					chain.setFragment( range0, range1,
						StructureComponentRegistry.TYPE_COIL );
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
					final String pT = chain.getFragmentType( i-1 ); // prior type

					final Residue bR = chain.getResidue( range0 );
					final int bId = bR.getResidueId( );

					final Residue cR = chain.getResidue( range1 );
					final int cId = cR.getResidueId( );

					final int dI = chain.getFragmentStartResidue( i+1 );
					final Residue dR = chain.getResidue( dI );
					final int dId = dR.getResidueId( );
					final String nT = chain.getFragmentType( i+1 ); // next type

					final int abDiff = bId - aId;
					final int cdDiff = dId - cId;

					if (
						( (abDiff>1) || (pT == Conformation.TYPE_UNDEFINED) )
						&&
						( (cdDiff>1) || (nT == Conformation.TYPE_UNDEFINED) )
					)
					{
						chain.setFragment( range0, range1,
							Conformation.TYPE_UNDEFINED );
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
			String savedType = chain.getFragmentType( 0 );
			final int savedRange[] = new int[2];
			savedRange[0] = chain.getFragmentStartResidue( 0 );
			savedRange[1] = chain.getFragmentEndResidue( 0 );
			for ( int i=1; i<chain.getFragmentCount(); i++ )
			{
				final String fragmentType = chain.getFragmentType( i );
				range[0] = chain.getFragmentStartResidue( i );
				range[1] = chain.getFragmentEndResidue( i );

				// Can we coalese?
				if ( (fragmentType == StructureComponentRegistry.TYPE_COIL)
				&& (savedType == StructureComponentRegistry.TYPE_COIL) )
				{
					// Are the residue IDs contiguous between fragments?
					final Residue tailRes = chain.getResidue( savedRange[1] );
					final Residue headRes = chain.getResidue( range[0] );
					final int resIdDiff = headRes.getResidueId()
						- tailRes.getResidueId();
					if ( resIdDiff <= 1 )
					{
						// Coalese.
						chain.setFragment( savedRange[0], range[1],
							StructureComponentRegistry.TYPE_COIL );
						i--; // Since we effectively removed a fragment.
						savedType = fragmentType;
						// savedRange[0] = range[0];
						savedRange[1] = range[1];
					}
					else
					{
						// Leave a gap.
						chain.setFragment( range[0], range[1], StructureComponentRegistry.TYPE_COIL );
						// System.err.println( "StructureMap.loadFragments: chain " + chain.getChainId() + ", residues " + tailRes.getResidueId() + "-GAP-" + headRes.getResidueId() );
						// chain.setFragment( savedRange[1], savedRange[1], Conformation.TYPE_UNDEFINED );
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
			StructureComponentRegistry.TYPE_RESIDUE );

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
				StructureComponentRegistry.TYPE_RESIDUE, r );

			this.residues.add( residue );
			chain.addResidue( residue );
			final String chainAndResidueId = chain_id + r;
			this.residueByChainAndResidueId.put( chainAndResidueId, residue );
		}
	}

	/**
	 * Processes the residues for the Structure by picking out the ligands.
	 */
	protected void extractLigands( )
	{
		final int residueCount = this.residues.size();
		for ( int r=0; r<residueCount; r++ )
		{
			final Residue residue = this.residues.elementAt( r );
			final String classification = residue.getClassification( );
			if ( classification == Residue.COMPOUND_LIGAND ) {
				this.ligands.add( residue );
			}
		}
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
	 *  Get a Vector of all Atom objects in the Structure.
	 *  <P>
	 *  WARNING: Since the Atom count may be large in complex structures,
	 *  the returned vector may take a large amount of memory (even though
	 *  the vector contains only references to the exsting Atom objects).
	 *  <P>
	 */
	public Vector<Atom> getAtoms( )
	{
		if ( this.atoms == null ) {
			return null;
		}
		return new Vector<Atom>( this.atoms );
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
		if ( this.residueByChainAndResidueId == null ) {
			return null;
		}

		final String chainAndResidue = atom.chain_id + atom.residue_id;

		return this.residueByChainAndResidueId.get( chainAndResidue );
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
		return this.residueByChainAndResidueId.get( chainAndResidue );
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
		if ( this.residueByChainAndResidueId == null ) {
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
			StructureComponentRegistry.TYPE_BOND );
		if ( bondCount > 0 )
		{
			// Load bonds from the dataset.
			for ( int b=0; b<bondCount; b++ )
			{
				final Bond bond = (Bond) this.structure.getStructureComponentByIndex(
					StructureComponentRegistry.TYPE_BOND, b );
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
		return (Chain) this.chains.elementAt( chainIndex );
	}

	/**
	 * Return the chains from the Structure.
	 */
	public Vector<Chain> getChains( )
	{
		return this.chains;
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
			final Chain chain2 = (Chain) this.chains.elementAt( i );
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
	public int getStructureComponentCount( final String type )
	{
		if ( type == StructureComponentRegistry.TYPE_ATOM ) {
			return this.atoms.size( );
		} else if ( type == StructureComponentRegistry.TYPE_RESIDUE ) {
			return this.residues.size( );
		} else if ( type == StructureComponentRegistry.TYPE_FRAGMENT ) {
			return this.fragments.size( );
		} else if ( type == StructureComponentRegistry.TYPE_CHAIN ) {
			return this.chains.size( );
		} else if ( type == StructureComponentRegistry.TYPE_BOND ) {
			return this.bonds.size( );
		} else {
			return this.structure.getStructureComponentCount( type );
		}
	}


	/**
	 * Return the StructureComponent object specified by its type and index.
	 * <P>
	 */
	public StructureComponent getStructureComponentByIndex( final String type, final int index )
	{
		if ( type == StructureComponentRegistry.TYPE_ATOM ) {
			return (Atom) this.atoms.elementAt( index );
		} else if ( type == StructureComponentRegistry.TYPE_RESIDUE ) {
			return (Residue) this.residues.elementAt( index );
		} else if ( type == StructureComponentRegistry.TYPE_FRAGMENT ) {
			return (Fragment) this.fragments.elementAt( index );
		} else if ( type == StructureComponentRegistry.TYPE_CHAIN ) {
			return (Chain) this.chains.elementAt( index );
		} else if ( type == StructureComponentRegistry.TYPE_BOND ) {
			return (Bond) this.bonds.elementAt( index );
		} else {
			return this.structure.getStructureComponentByIndex( type, index );
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
		final String type = sc.getStructureComponentType( );
		Vector<Object> parents = null;

		if ( type == StructureComponentRegistry.TYPE_ATOM )
		{
			parents = new Vector<Object>( );
			parents.add( this.getResidue( (Atom) sc ) );
		}
		else if ( type == StructureComponentRegistry.TYPE_BOND )
		{
			parents = new Vector<Object>( );
			final Bond bond = (Bond) sc;
			parents.add( bond.getAtom(0) );
			parents.add( bond.getAtom(1) );
		}
		else if ( type == StructureComponentRegistry.TYPE_RESIDUE )
		{
			parents = new Vector<Object>( );
			parents.add( ((Residue)sc).getFragment() );
		}
		else if ( type == StructureComponentRegistry.TYPE_FRAGMENT )
		{
			parents = new Vector<Object>( );
			parents.add( ((Fragment)sc).getChain() );
		}
		else if ( type == StructureComponentRegistry.TYPE_CHAIN )
		{
			parents = new Vector<Object>( );
			parents.add( structure );
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
		final String type = sc.getStructureComponentType( );

		if ( type == StructureComponentRegistry.TYPE_CHAIN )
		{
			return ((Chain) sc).getFragments( );
		}
		else if ( type == StructureComponentRegistry.TYPE_FRAGMENT )
		{
			return ((Fragment) sc).getResidues( );
		}
		else if ( type == StructureComponentRegistry.TYPE_RESIDUE )
		{
			return ((Residue) sc).getAtoms( );
		}
		else if ( type == StructureComponentRegistry.TYPE_ATOM )
		{
			return null;
		}
		else if ( type == StructureComponentRegistry.TYPE_BOND )
		{
			// Even though physically Atoms are children, they
			// are not concidered children logically.
			// Note that Atoms ARE parents to Bonds.
			return null;
		}
		else
		{
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

	public JoglSceneNode getSceneNode() {
		return this.sceneNode;
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
            final Object[] pdbIds = converter.getPdbIds(r.getChainId(), new Integer(r.getResidueId()));
            if(pdbIds == null) {
				continue;
			}
            final String pdbChainId = (String)pdbIds[0];
            if(pdbChainId == null) {
                if(r.getCompoundCode().equals("HOH")) {
                    waterResidues.add(r);
                } else {   // if this is a non-protein residue...
                    nonProteinResidues.add(r);
                }
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
        
        final Comparator<Residue> residueComparitor = new Comparator<Residue>() {
            public int compare(Residue r1, Residue r2) {
                return (r1.getResidueId() - r2.getResidueId());
            }
        };
        
        for (String pdbId : byPdbId.keySet())
        {
        	Vector<Residue> residues = byPdbId.get(pdbId);
            if(pdbId != null && pdbId.length() != 0) {
                final PdbChain c = new PdbChain();
                c.pdbChainId = pdbId;
                c.setResidues(residues);
                
                this.pdbTopLevelElements.add(c);
                
                Collections.sort(residues, residueComparitor);
                residues.trimToSize();
            }
            
            else
                for (Residue r : residues)
                    this.pdbTopLevelElements.add(r);
        }
        
        Collections.sort(waterResidues, residueComparitor);
        Collections.sort(nonProteinResidues, residueComparitor);
        
        if(waterResidues.size() != 0) {
        	waterResidues.trimToSize();
            final WaterChain waterChain = new WaterChain();
            waterChain.setResidues(waterResidues);
            this.pdbTopLevelElements.add(waterChain);
        }
        
        if(nonProteinResidues.size() != 0) {
            nonProteinResidues.trimToSize();
            final MiscellaneousMoleculeChain nonProteinChain = new MiscellaneousMoleculeChain();
            nonProteinChain.setResidues(nonProteinResidues);
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
    public void setNonproteinChainIds( final String[] nonproteinChainIds) {
    	if(nonproteinChainIds == null) {
    		return;
    	}
    	
        this.nonproteinChainIds = new HashSet<String>();
        for(int i = 0; i < nonproteinChainIds.length; i++) {
            this.nonproteinChainIds.add(nonproteinChainIds[i]);
        }
    }
    
    public StructureComponent getTopPdbComponent(final Residue r)
    {
		for (StructureComponent next : pdbTopLevelElements)
		{
			if(next instanceof PdbChain) {
				final PdbChain next_ = (PdbChain)next;
				if(next_.contains(r)) {
					return next;
				}
			} else if(next instanceof WaterChain) {
				final WaterChain next_ = (WaterChain)next;
				if(next_.contains(r)) {
					return next;
				}
			} else if(next instanceof MiscellaneousMoleculeChain) {
				final MiscellaneousMoleculeChain next_ = (MiscellaneousMoleculeChain)next;
				if(next_.contains(r)) {
					return next;
				}
			}
		}
		
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

