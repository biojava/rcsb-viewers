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
package org.rcsb.vf.glscene.jogl;


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.attributes.*;
import org.rcsb.vf.glscene.SecondaryStructureBuilder.SsGeometry;
import org.rcsb.vf.glscene.SecondaryStructureBuilder.CrossSectionStyle.CrossSectionType;
import org.rcsb.vf.glscene.SecondaryStructureBuilder.SsGeometry.ConformationShape;



import com.sun.opengl.util.GLUT;



/**
 * ChainGeometry.java<BR>
 *
 * Please complete these missing tags
 * @author	John L. Moreland
 */
public class ChainGeometry
	extends DisplayListGeometry
{
	public enum RibbonForm
	{
		RIBBON_SIMPLE_LINE ("Simple Line"),
		RIBBON_TRADITIONAL ("Traditional"),
		RIBBON_CYLINDRICAL_HELICES ("Traditional / Cylinders");
		
		private RibbonForm(String in_description) { description = in_description; }
		protected String description;
		public String getDescription() { return description; }
	}
	
	private RibbonForm ribbonForm = RibbonForm.RIBBON_TRADITIONAL;
	private boolean ribbonsAreSmoothed = true;
					// these were set from defaults specified in the mutators.  Problem is,
					// that requires bringing the mutator suite into this project, which
					// we're trying to avoid, at the moment.
					//
					// they would be better set from the constructor.  I've provided a
					// 'setter' function for each, for the interim.
					//
					// these are the values that the mutator defaults to.
					//
					// 07-May-08  rickb
					//
	/**
	 *  Construct a new ChainGeometry object.
	 */
	public ChainGeometry( )
	{
	}


	/**
	 * Please complete the missing tags for main
	 * @param
	 * @return
	 * @throws
	 */
	public final int getDisplayList( final int displayList, final StructureComponent structureComponent, final Style style, final GL gl, final GLU glu, final GLUT glut)
	{
		return this.getDisplayList( displayList, structureComponent, style, gl, glu, glut);
	}

	// This does not apply to ChainGeometry. Use setHelixForm, etc. instead.
	
	public void setForm(final int form) {

	}	
	
	/**
	 * Provide setter for this, right now.  May change to put in the constructor...
	 *
	 * @param eForm - one of the values for the ribbon form.  These should be enums,
	 * 				  but they're currently defined as ints (boo.)
	 * @return
	 */

	public DisplayLists[] getDisplayLists(final StructureComponent structureComponent, final Style style, final GL gl, final GLU glu, final GLUT glut) {
		//
		// Handle quality, form, and shared display lists.
		//

		DisplayLists[] lists = null;

		final Chain chain = (Chain)structureComponent;
		
		//
		// Generate the display List
		//

		final Structure structure = chain.getStructure( );
		final StructureMap structureMap = structure.getStructureMap( );
		final StructureStyles structureStyles = structureMap.getStructureStyles( );

		try {
			boolean ribbon = false;
	    	
	    	final float helixQuality = 0.8f;
	        final float coilQuality = 0.8f;
	        final float turnQuality = 0.8f;
	        final float strandQuality = 0.8f;

	        int helixSmoothingSteps = -1;
	        int strandSmoothingSteps = -1;
	        int turnSmoothingSteps = -1;
	        int coilSmoothingSteps = -1;
	        
	        CrossSectionType helixCsType = null;
	        CrossSectionType strandCsType = null;
	        CrossSectionType turnCsType = null;
	        CrossSectionType coilCsType = null;
	        ConformationShape helixSsShape = null;
			
	        if(this.ribbonsAreSmoothed) {
	        	coilSmoothingSteps = 2;
	        	turnSmoothingSteps = 2;
	        	helixSmoothingSteps = 0;
	        	strandSmoothingSteps = 2;
	        } else {
	        	coilSmoothingSteps = 0;
	        	turnSmoothingSteps = 0;
	        	helixSmoothingSteps = 0;
	        	strandSmoothingSteps = 0;
	        }
	        
			switch(this.ribbonForm) {
        	case RIBBON_CYLINDRICAL_HELICES:
                coilCsType = CrossSectionType.ROUNDED_TUBE;
                turnCsType = CrossSectionType.ROUNDED_TUBE;
                helixCsType = CrossSectionType.REGULAR_POLYGON;
                helixSsShape = ConformationShape.CYLINDER;
            	strandCsType = CrossSectionType.RECTANGULAR_RIBBON;
                
                ribbon = true;
        		break;
        	case RIBBON_TRADITIONAL:
        		coilCsType = CrossSectionType.ROUNDED_TUBE;
                turnCsType = CrossSectionType.ROUNDED_TUBE;
                helixCsType = CrossSectionType.REGULAR_POLYGON;
                helixSsShape = ConformationShape.RIBBON;
                strandCsType = CrossSectionType.RECTANGULAR_RIBBON;
            	
                ribbon = true;
        		break;
        	case RIBBON_SIMPLE_LINE:
        		coilCsType = CrossSectionType.ROUNDED_TUBE;
                turnCsType = CrossSectionType.ROUNDED_TUBE;
                helixCsType = CrossSectionType.REGULAR_POLYGON;
                helixSsShape = ConformationShape.RIBBON;
            	strandCsType = CrossSectionType.RECTANGULAR_RIBBON;
            	
            	ribbon = false;
        		break;
        	default:
        		(new Exception()).printStackTrace();
        	}
			
			lists = SsGeometry.createSs(chain, structureMap, structureStyles, ribbon, 
					helixQuality, strandQuality, turnQuality, coilQuality,
					helixSmoothingSteps, strandSmoothingSteps, turnSmoothingSteps,
					coilSmoothingSteps, helixCsType, strandCsType, turnCsType,
					coilCsType, helixSsShape, gl, glu, glut);
		} catch(final Exception e) {
			e.printStackTrace();
		}
		return lists;
	}


	public RibbonForm getRibbonForm() {
		return this.ribbonForm;
	}


	public void setRibbonForm(final RibbonForm ribbonForm) {
		this.ribbonForm = ribbonForm;
	}


	public boolean isRibbonsAreSmoothed() {
		return this.ribbonsAreSmoothed;
	}


	public void setRibbonsAreSmoothed(final boolean ribbonsAreSmoothed) {
		this.ribbonsAreSmoothed = ribbonsAreSmoothed;
	}
}

