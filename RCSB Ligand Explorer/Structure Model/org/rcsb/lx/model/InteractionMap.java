package org.rcsb.lx.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.awt.Color;

import org.rcsb.lx.model.Interaction;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.StructureMap;




/**
 * @author John Beaver
 */
// a class for storing interactions by their type. Best when the type's description is enumerated.
public class InteractionMap {
    /* Data structure:
     *   HashMap interactionsByType {
     *     key: String interactionType
     *     value: Vector interactions <= Interaction
     *   }
     */
    private final TreeMap interactionsByType = new TreeMap();
    
    /* Data structure:
     *   HashMap interactionsByResidue
     *   key: Residue r
     *   value: Vector interactions <= Interaction
     */
    private final HashMap interactionsByResidue = new HashMap();
    
//    private HashMap colorsByInteractionType = new HashMap();
    private final HashMap defaultColorsByInteractionType = new HashMap();
    
    /* Data structure:
     *   HashMap visibleTypes {
     *     key: String interactionType
     *     value: Object[2] {
     *          Boolean isVisible
     *          Color color
     *     } 
     *   }
     */
    private final HashMap selectedTypes = new HashMap();
    
    private static final Color defaultColor = Color.WHITE;
    
    public InteractionMap() {
    }
    
    public void putInteractionType(final String type, final boolean selectionState, final Color color) {
    	final Boolean selectionStateObject = selectionState ? Boolean.TRUE : Boolean.FALSE;
    	
        // if the interaction type is already added, save the color.
        final Object[] tmp = (Object[])this.selectedTypes.get(type);
        if(tmp != null) {
            tmp[0] = new Boolean(selectionState);
        } else {
            this.selectedTypes.put(type, new Object[] {selectionStateObject, color});
            
            // pick a new color with relation to the last color.
//            curColIndex = curColIndex > interactionColors.length - 2 ? 0 : curColIndex + 1; 
        }
    }
    
    // return iterator for the residues which have interactions
    public Set getResidues() {
        return this.interactionsByResidue.keySet();
    }
    
    public boolean getInteractionTypeSelection(final String type) {
        final Object[] tmp = (Object[])this.selectedTypes.get(type);
        
        return ((Boolean)tmp[0]).booleanValue();
    }
    
    public Color getInteractionTypeColor(final String type) {
    	Color col = (Color)this.defaultColorsByInteractionType.get(type);
    	if(col == null) {
    		col = InteractionMap.defaultColor;
    	}
    	
    	return col;
    }
	
	public void setInteractionTypeColor(final String type, final Color color)
	{
		this.defaultColorsByInteractionType.put(type, color);
	}
    
    public void putInteraction(final Interaction ia)
    {
        Vector v = (Vector)this.interactionsByType.get(ia.getInteractionType());
        
        if(v == null)
        {
            v = new Vector();
            v.add(ia);
            this.interactionsByType.put(ia.getInteractionType(),v);
        }
        
        else
            v.add(ia);
        
        final StructureMap sm = ia.getFirstAtom().structure.getStructureMap();
        final Residue firstResidue = sm.getResidue(ia.getFirstAtom());
        final Residue secondResidue = sm.getResidue(ia.getSecondAtom());
        
        v = (Vector)this.interactionsByResidue.get(firstResidue);
        if(v == null)
        {
            v = new Vector();
            v.add(ia);
            this.interactionsByResidue.put(firstResidue,v);
        }
        
        else
            v.add(ia);
        
        v = (Vector)this.interactionsByResidue.get(secondResidue);
        if(v == null) {
            v = new Vector();
            v.add(ia);
            this.interactionsByResidue.put(secondResidue,v);
        }
        
        else
            v.add(ia);
    }
    
    public void clear() {
        this.interactionsByType.clear();
        this.interactionsByResidue.clear();
        this.selectedTypes.clear();
    }
    
    public void deselectAllTypes() {
        final Iterator valueIt = this.selectedTypes.values().iterator();
        
        while(valueIt.hasNext()) {
            final Object[] tmp = (Object[])valueIt.next();
            tmp[0] = Boolean.FALSE;
        }
    }
    
    public Vector getInteractions(final String interactionType) {
        return (Vector)this.interactionsByType.get(interactionType);
    }
    
    public Iterator getAllInteractions() {
        final Iterator vecIt = this.interactionsByType.values().iterator();
        final Vector allInters = new Vector();
        while(vecIt.hasNext()) {
            allInters.addAll((Vector)vecIt.next());
        }
        
        return allInters.iterator();
    }
    
    public Vector getInteractions(final Residue r) {
        return (Vector)this.interactionsByResidue.get(r);
    }
    
    // returns an array of the type descriptions (Strings).
    public Object[] getInteractionTypes() {
        return this.interactionsByType.keySet().toArray();
    }
    
    public Vector getSelectedInteractions()
    {
        final Vector selectedInteractions = new Vector();
        
        final Iterator keyIt = this.selectedTypes.keySet().iterator();
        final Iterator valueIt = this.selectedTypes.values().iterator();
        
        while(keyIt.hasNext()) {
            final String type = (String)keyIt.next();
            final Object[] tmp = (Object[])valueIt.next();
            final boolean isSelected = ((Boolean)tmp[0]).booleanValue();
            
            if(isSelected) {
                selectedInteractions.addAll((Vector)this.interactionsByType.get(type));
            }
        }
        
        return selectedInteractions;
    }
}
