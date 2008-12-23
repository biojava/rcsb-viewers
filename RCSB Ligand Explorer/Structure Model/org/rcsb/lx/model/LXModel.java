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
