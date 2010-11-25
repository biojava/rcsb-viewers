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



import java.util.ArrayList;


/**
 *  This class implements the top-level container for one or more
 *  Structures.
 *  
 *  <P>
 *  <center>
 *  <img src="doc-files/StructureDocument.jpg" border=0></a>
 *  </center>
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.uiApp.controllers.update.UpdateEvent
 *  @see	org.rcsb.mbt.model.attributes.StructureStyles
 *  @see	org.rcsb.mbt.model.StructureMap
 *  @see	org.rcsb.mbt.model.Structure
 */
public class StructureModel
{
	//
	// Private variables.
	//

	/**
	 * Stores the repository of Structure objects in this StructureDocument.
	 */
	@SuppressWarnings("serial")
	public class StructureList extends ArrayList<Structure>{};
	
	protected StructureList structures = new StructureList();
	public synchronized StructureList getStructures() { return structures; }
	
	/**
	 * @return - whether we have structures in the model.
	 */
	public synchronized boolean hasStructures() { return structures != null && structures.size() > 0; }

	/**
	 *  Add a structure to the list of managed/viewed structures,
	 *  then send an event to inform all registered Viewer objects.
	 */
	public synchronized void addStructure( final Structure structure )
		throws IllegalArgumentException
	{
		if ( structure == null ) {
			throw new IllegalArgumentException( "null structure" );
		}
		if ( this.structures.contains( structure ) ) {
			throw new IllegalArgumentException( "structure already added" );
		}

		this.structures.add( structure );
	}
	
	public synchronized void setStructures(Structure[] structure_array)
	{
		if (structure_array != null)
		{
			for (Structure struc : structure_array)
			   this.structures.add(struc);
		}
	}

	/**
	 * Remove a Structure from this StructureDocument.
	 * Send the event, first, in case the receiver may want to do something from the list.
	 */
	public synchronized void removeStructure( final Structure structure )
		throws IllegalArgumentException
	{
		if ( structure == null ) {
			throw new IllegalArgumentException( "null structure" );
		}
		if ( ! this.structures.contains( structure ) ) {
			throw new IllegalArgumentException( "structure not found" );
		}
		
		this.structures.remove( structure );
	}

	/**
	 * Remove all Structures from this StructureDocument.
	 */
	public synchronized void clear()
	{
		structures.clear();
	}
	
	/**
	 * override if you need to modify the residue name output
	 * 
	 * @param residue
	 * @return
	 */
	public String getModifiedResidueName(Residue residue)
	{
		return residue.getCompoundCode();
	}
}

