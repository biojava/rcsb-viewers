package org.rcsb.lx.model;

import org.rcsb.lx.ui.ContactMap;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.util.PdbToNdbConverter;
import org.rcsb.mbt.model.util.IResidueNameModifier;
import org.rcsb.uiApp.model.UIAppStructureModel;


public class LXModel extends UIAppStructureModel implements IResidueNameModifier
{
	private String initialLigand = null;	
	private final InteractionMap interactionMap = new InteractionMap();
    private ContactMap contactMap = null;
    
    @Override
    public void clear()
    {
    	super.clear();
    	initialLigand = null;
    }
    
    /**
     * Gets the residue name and id from the residue and atom.  Used primarily in the
     * ligandJList in the sidebar
     * 
     * @param atom
     * @return
     */
	public String getResidueName(final Atom atom)
	{
		final PdbToNdbConverter converter = atom.structure.getStructureMap().getPdbToNdbConverter();
		final Object[] tmp = converter.getPdbIds(atom.chain_id, new Integer(atom.residue_id));
		String name = "";
		
		if (tmp == null)
		{
			// use ndb ids
			name = atom.chain_id;
			if (name.length() > 0)
				name += " " + atom.residue_id;
			
			else
				name = atom.residue_id + "";

		}
		
		else
		{
			// use pdb ids
			if (tmp[0] != null)
				name = (String) tmp[0];

			if (tmp[1] != null)
			{
				if (name.length() > 0)
					name += " " + tmp[1];
				
				else
					name = (String) tmp[1];
			}
		}

		if (name.length() > 0)
			name = atom.compound + " (" + name + ")";
		
		else
			name = atom.compound;

		return name;
	}

	/**
	 * Get the residue name given a residue structure.
	 * 
	 * Deliberately hides base.
	 */
	public String getResidueName(final Residue residue)
	{
		return this.getResidueName(residue.getAtom(0));
	}


	
	public InteractionMap getInteractionMap()
	{
		return this.interactionMap;
	}

	public String getInitialLigand()
	{
		return this.initialLigand;
	}

	public void setInitialLigand(String initialLigand)
	{
		this.initialLigand = initialLigand;
	}

	/**
	 * ContactMap derives off JPanel...
	 * Shouldn't be part of the model...
	 * 
	 * @return
	 */
	public ContactMap getContactMap()
	{
		return contactMap;
	}

	public void setContactMap(ContactMap contactMap)
	{
		this.contactMap = contactMap;
	}

	@Override
	public String getModifiedResidueName(Residue residue)
	{
		return getResidueName(residue);
	}
}
