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
package org.rcsb.vf.controllers.scene;

import java.util.ArrayList;
import java.util.Hashtable;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.StructureMap.BiologicUnitTransforms;
import org.rcsb.mbt.model.StructureMap.BiologicUnitTransforms.BiologicalUnitGenerationMapByChain;
import org.rcsb.mbt.model.geometry.ArrayLinearAlgebra;
import org.rcsb.mbt.model.geometry.ModelTransformationList;
import org.rcsb.mbt.model.geometry.ModelTransformationMatrix;
import org.rcsb.mbt.model.util.DebugState;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.glscene.jogl.AtomGeometry;
import org.rcsb.vf.glscene.jogl.BondGeometry;
import org.rcsb.vf.glscene.jogl.DisplayListGeometry;
import org.rcsb.vf.glscene.jogl.DisplayLists;
import org.rcsb.vf.glscene.jogl.GlGeometryViewer;
import org.rcsb.vf.glscene.jogl.GvPickEvent;
import org.rcsb.vf.glscene.jogl.GvPickEventListener;
import org.rcsb.vf.glscene.surfaces.SurfaceGeometry;


/**
 * Controls everything related to the 3d scene.  The exception is the glviewer, which is contained by the
 * active frame, since the viewer is parented by that.  May move that over here, too, though.
 * 
 * @see org.rcsb.ui.mainframe.DocumentFrameBase
 * 
 * @author rickb
 *
 */
public class SceneController implements GvPickEventListener, IUpdateListener
{
	public class DebugSettings
	{
		public boolean isAntialiasingEnabled = false;
		public float blurPlane = 100f;
		public float blurFactor = .33f;
		
		// 9 elements...
//			public final float[][] JITTER_ARRAY = {{0.5f, 0.5f}, {0.166666f, 0.944444f}, {0.5f, 0.166666f}, {0.5f,
//			          0.833333f}, {0.166666f, 0.277777f}, {0.833333f, 0.388888f}, {0.166666f, 0.611111f},
//			          {0.833333f, 0.722222f}, {0.833333f, 0.055555f}};
		
		// 16 elements...
		public final float[][] JITTER_ARRAY = {{0.375f, 0.4375f}, {0.625f, 0.0625f}, {0.875f, 0.1875f}, {0.125f, 0.0625f}, 

				{0.375f, 0.6875f}, {0.875f, 0.4375f}, {0.625f, 0.5625f}, {0.375f, 0.9375f}, 

				{0.625f, 0.3125f}, {0.125f, 0.5625f}, {0.125f, 0.8125f}, {0.375f, 0.1875f}, 

				{0.875f, 0.9375f}, {0.875f, 0.6875f}, {0.125f, 0.3125f}, {0.625f, 0.8125f}};
		
		// 4 elements...
//			public final float[][] JITTER_ARRAY = {{0.375f, 0.25f}, {0.125f, 0.75f}, {0.875f, 0.25f}, {0.625f, 0.75f}};
	}
	
    private Hashtable<ComponentType, DisplayListGeometry> defaultGeometry = null;
//	    private ArrayListStateOrganizer stateOrganizer = new ArrayListStateOrganizer();
    
    private boolean isBatchMode = false;	// otherwise, immediate mode.
    private boolean areSelectionsEnabled = true;
    private boolean isColorSelectorSampleModeEnabled = false;
    
    private boolean isAutoRotateEnabled = false;
    
    //deprecated. Use PDB XML generated biological units and the showAsymmetricUnitOnly flag below.
    private boolean shouldTreatModelsAsSubunits = false;
    
    // when true, only the asymmetric unit is shown; when false, the biological unit is generated, if specified. 
    private boolean showAsymmetricUnitOnly = false;
    
    // when true, all global transformations are disabled.
    private boolean disableGlobalTransforms = false;
    
    private boolean isDebugEnabled = false;
    private DebugSettings debugSettings = null;
    
    public SceneController()
    {
    	AppBase.sgetUpdateController().registerListener(this);
    }
    
	public boolean isBatchMode() {
		return this.isBatchMode;
	}

	public void setBatchMode(final boolean isBatchMode) {
		this.isBatchMode = isBatchMode;
	}


	public boolean areSelectionsEnabled() {
		return this.areSelectionsEnabled;
	}


	public void setAreSelectionsEnabled(final boolean areSelectionsEnabled) {
		this.areSelectionsEnabled = areSelectionsEnabled;
	}

	public Hashtable<ComponentType, DisplayListGeometry> getDefaultGeometry() {
		return this.defaultGeometry;
	}

	public void setDefaultGeometry(final Hashtable<ComponentType, DisplayListGeometry> defaultGeometry) {
		this.defaultGeometry = defaultGeometry;
	}

	public boolean isColorSelectorSampleModeEnabled() {
		return this.isColorSelectorSampleModeEnabled;
	}

	public void setColorSelectorSampleModeEnabled(
			final boolean isColorSelectorSampleModeEnabled) {
		this.isColorSelectorSampleModeEnabled = isColorSelectorSampleModeEnabled;
	}

	public boolean isAutoRotateEnabled() {
		return this.isAutoRotateEnabled;
	}

	public void setAutoRotateEnabled(final boolean isAutoRotateEnabled) {
		this.isAutoRotateEnabled = isAutoRotateEnabled;
	}

	public boolean isDebugEnabled() {
		return this.isDebugEnabled;
	}

	public void setDebugEnabled(final boolean isDebugEnabled) {
		this.isDebugEnabled = isDebugEnabled;
		if(isDebugEnabled) {
			this.debugSettings = new DebugSettings();
		} else {
			this.debugSettings = null;
		}
	}

	public DebugSettings getDebugSettings() {
		return this.debugSettings;
	}

	public boolean shouldTreatModelsAsSubunits() {
		return this.shouldTreatModelsAsSubunits;
	}

	public void setTreatModelsAsSubunits(final boolean shouldTreatModelsAsSubunits) {
		this.shouldTreatModelsAsSubunits = shouldTreatModelsAsSubunits;
	}

	public boolean showAsymmetricUnitOnly() {
		return this.showAsymmetricUnitOnly;
	}

	public void setShowAsymmetricUnitOnly(final boolean showAsymmetricUnitOnly) {
		this.showAsymmetricUnitOnly = showAsymmetricUnitOnly;
	}

	public boolean areGlobalTransformsDisabled() {
		return this.disableGlobalTransforms;
	}

	public void setDisableGlobalTransforms(boolean disableGlobalTransforms) {
		this.disableGlobalTransforms = disableGlobalTransforms;
		}	// added for protein-ligand interactions
		
		
	// returned:
	// - double[0]: minimum x,y,z values
	// - double[1]: maximum x,y,z values
	// can be null if the ligand name is not found.
	private double[] tempDouble = new double[3];

	private ModelTransformationList singleElementVectorTemp = new ModelTransformationList(1);
	private ModelTransformationList singleElementVectorTemp2 = new ModelTransformationList(1);

    private ModelTransformationList getSingleElementVectorTemp()
    {
    	if (singleElementVectorTemp.size() == 0)
    		singleElementVectorTemp.add(null);
    	
    	if (singleElementVectorTemp2.size() == 0)
    		singleElementVectorTemp2.add(null);
    	
    	return singleElementVectorTemp; 
    }
    
    private ModelTransformationList getSingleElementVectorTemp2()
    {
       	if (singleElementVectorTemp2.size() == 0)
    		singleElementVectorTemp2.add(null);
    	
		return singleElementVectorTemp2;
	}

	private double[][] getSceneBounds()
	{
		double maxX, maxY, maxZ;
		double minX, minY, minZ;
		boolean ignorePerChainTransforms = showAsymmetricUnitOnly();
		boolean ignoreGlobalTransforms = areGlobalTransformsDisabled();
	
		maxX = maxY = maxZ = -(Double.MAX_VALUE - 2);
		minX = minY = minZ = Double.MAX_VALUE;
	
		for(Structure s : AppBase.sgetModel().getStructures())
		{
			final StructureMap sm = s.getStructureMap();
			int chainCount = sm.getChainCount();
			BiologicalUnitGenerationMapByChain transformMatricesMap = null;
			ModelTransformationList globalTransforms = null;
			
			if (sm.hasBiologicUnitTransforms())
			{
				final BiologicUnitTransforms bu = s.getStructureMap().getBiologicUnitTransforms();
				transformMatricesMap = bu.getBiologicalUnitGenerationMatricesByChain();
				globalTransforms = bu.getBiologicalUnitGenerationMatrixVector();
			}
			
			// so we don't have to repeat code below.
			if (globalTransforms == null || ignoreGlobalTransforms)
				globalTransforms = getSingleElementVectorTemp();
			
			for(int m = 0; m < globalTransforms.size(); m++)
			{
				final ModelTransformationMatrix globalTransform = globalTransforms.get(m);
				
				for(int l = 0; l < chainCount; l++)
				{
					final Chain c = sm.getChain(l);
					int resCount = c.getResidueCount();
					
					final ArrayList<ModelTransformationMatrix> byChainTransforms;
					if (transformMatricesMap == null || ignorePerChainTransforms)
						byChainTransforms = getSingleElementVectorTemp2();
					
					else
						byChainTransforms = transformMatricesMap.get(c.getChainId());

					
					if(byChainTransforms != null)
					{	// biological units often use fewer than the total number of chains. If per-chain matrices exist, but none is specified for this chain, the chain won't be drawn at all.
						for(int n = 0; n < byChainTransforms.size(); n++)
						{
							ModelTransformationMatrix byChainTransform = byChainTransforms.get(n);
							
							for (int j = 0; j < resCount; j++)
							{
								final Residue r = c.getResidue(j);
					
								final int atomCount = r.getAtomCount();
								for (int k = 0; k < atomCount; k++)
								{
									final Atom atom_k = r.getAtom(k);
									
									// mirror the JoglSceneNode's operation: first transform by the global transform, and then by the per-chain transform. Typically, only one of the two types of transforms is specified.
									if(globalTransform != null && !ignoreGlobalTransforms)
										globalTransform.transformPoint(atom_k.coordinate, tempDouble);

									if(byChainTransform != null && !ignorePerChainTransforms)
										byChainTransform.transformPoint(atom_k.coordinate, tempDouble);

									
									if(byChainTransform != null || globalTransform != null) {
										maxX = Math.max(tempDouble[0], maxX);
										maxY = Math.max(tempDouble[1], maxY);
										maxZ = Math.max(tempDouble[2], maxZ);
						
										minX = Math.min(tempDouble[0], minX);
										minY = Math.min(tempDouble[1], minY);
										minZ = Math.min(tempDouble[2], minZ);
									} else {
										maxX = Math.max(atom_k.coordinate[0], maxX);
										maxY = Math.max(atom_k.coordinate[1], maxY);
										maxZ = Math.max(atom_k.coordinate[2], maxZ);
						
										minX = Math.min(atom_k.coordinate[0], minX);
										minY = Math.min(atom_k.coordinate[1], minY);
										minZ = Math.min(atom_k.coordinate[2], minZ);
									}
								}
							}
						}
					}
				}
			}
		}
		
		final double[][] bounds = new double[][] { { minX, minY, minZ }, { maxX, maxY, maxZ } };
		
		return bounds;
	}
	
	/**
	 * Reset the view to look at the center of the data. JLM DEBUG: This will
	 * eventually be non-static method.
	 * <P>
	 */
	public void resetView(final boolean forceRecalculation)
	{
		if (!AppBase.sgetModel().hasStructures())
			return;

		GlGeometryViewer viewer = VFAppBase.sgetGlGeometryViewer();

		if (forceRecalculation || viewer.bounds == null)
			viewer.bounds = getSceneBounds();

		final double maxStructureLength = ArrayLinearAlgebra.distance(viewer.bounds[0],
				viewer.bounds[1]);

		final double[] center = {
				(viewer.bounds[0][0] + viewer.bounds[1][0]) / 2,
				(viewer.bounds[0][1] + viewer.bounds[1][1]) / 2,
				(viewer.bounds[0][2] + viewer.bounds[1][2]) / 2 };
		final double[] eye = { center[0], center[1],
				center[2] + maxStructureLength };
		final double[] up = { 0.0f, 1.0f, 0.0f };
		viewer.lookAt(eye, center, up);
	}
	
	/**
	 * Clear all the display lists.
	 */
	public void clearMemory()
	{
		DisplayLists.uniqueColorMap.clear();
		AtomGeometry.sharedDisplayLists.clear();
		BondGeometry.sharedDisplayLists.clear();
		if (DebugState.isDebug())
			System.err.println("--> SceneController.clearMemory() (cleared display lists.)");
	}

	/**
	 * Override to handle pick events
	 * 
	 * @see org.rcsb.vf.glscene.jogl.GvPickEventListener#processPickEvent(org.rcsb.vf.glscene.jogl.GvPickEvent)
	 */
	public void processPickEvent(GvPickEvent pickEvent)
	{
	}

	/* (non-Javadoc)
	 * @see org.rcsb.mbt.views_controller.IUpdateListener#handleModelChangedEvent(org.rcsb.mbt.views_controller.UpdateEvent)
	 */
	public void handleUpdateEvent(UpdateEvent evt)
	{
		if (evt.action == UpdateEvent.Action.CLEAR_ALL)
			clearMemory();
	}

}
