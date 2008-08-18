package org.rcsb.pw.controllers.scene.mutators;

import org.rcsb.pw.controllers.app.ProteinWorkshop;


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
		  
		private final Mutator mutator;
		
		Id(Mutator mutator)
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
    
    public Mutator getCurrentMutator() {
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