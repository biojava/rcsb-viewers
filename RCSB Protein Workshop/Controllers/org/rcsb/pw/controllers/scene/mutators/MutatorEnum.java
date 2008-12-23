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
package org.rcsb.pw.controllers.scene.mutators;

import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.vf.controllers.scene.mutators.MutatorBase;


/**
 * Defines an enumeration for each of the mutators, and holds a mutator for each
 * enumeration.
 * 
 * @author rickb
 *
 */
public class MutatorEnum
{
	public enum Id	
	{
		NOID(null),
		VISIBILITY_MUTATOR(new StructureElement_VisibilityMutator()),
		COLORING_MUTATOR(new ColorMutator()),
		LABELING_MUTATOR(new LabelsMutator()),
		STYLES_MUTATOR(new StylesMutator()),
		LINES_MUTATOR(new LinesMutator()),
		RECENTER_MUTATOR(new ReCenterMutator()),
		SELECTION_MUTATOR(new SelectionMutator());
		  
		private final MutatorBase mutator;
		
		Id(MutatorBase mutator)
		{
			this.mutator = mutator;
		}
	}

    private static final Id defaultId = Id.VISIBILITY_MUTATOR;
    private Id currentMutatorId = defaultId;	// default
    
    public void setDefaults()
    {
    	this.currentMutatorId = defaultId;
    }
    
    public void setCurrentMutator(final Id mutator)
    {
    	currentMutatorId = mutator;

        // this affects the tree's renderers. Repaint it.
    	ProteinWorkshop.sgetActiveFrame().getTreeViewer().getTree().repaint();
        
    	ProteinWorkshop.sgetActiveFrame().getMutatorBasePanel().updateOptionsPanel();
    }
    
    public MutatorBase getCurrentMutator() {
    	return this.currentMutatorId.mutator;
    }
    
    // returns a value corresponding to one of the constants in this class.
    public Id getCurrentMutatorId() {
    	return this.currentMutatorId;
    }

	public StructureElement_VisibilityMutator getVisibilityMutator() {
		return (StructureElement_VisibilityMutator)Id.VISIBILITY_MUTATOR.mutator;
	}
    
	public ColorMutator getColorMutator() {
		return (ColorMutator)Id.COLORING_MUTATOR.mutator;
	}

	public StylesMutator getStylesMutator() {
		return (StylesMutator)Id.STYLES_MUTATOR.mutator;
	}
	
	public LabelsMutator getLabelsMutator() {
		return (LabelsMutator)Id.LABELING_MUTATOR.mutator;
	}
	
	public SelectionMutator getSelectionMutator() {
		return (SelectionMutator)Id.SELECTION_MUTATOR.mutator;
	}

	public LinesMutator getLinesMutator() {
		return (LinesMutator)Id.LINES_MUTATOR.mutator;
	}
}
