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
package org.rcsb.uiApp.model;

import javax.swing.SwingUtilities;

import org.rcsb.mbt.model.Structure;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.update.UpdateController;
import org.rcsb.uiApp.controllers.update.UpdateEvent;

/**
 *  When a Structure object is added or removed from a StructureDocument,
 *  each registered Viewer will automatically receive a StructureDocumentEvent
 *  telling the Viewer that a given Structure was added (or removed as the case
 *  may be). It is then the responsibility of the Viewer to use the
 *  Structure object reference to display a suitable visual representation
 *  (often this entails getting the StructureMap and then the StructureStyles
 *  object in order to display a representation using the defined colors
 *  and other visual display properties).
 * @author rickb
 *
 */
public class UIAppStructureModel extends org.rcsb.mbt.model.StructureModel
{

	/**
	 * Add a structure.
	 */
	@Override
	public synchronized void addStructure(Structure structure)
			throws IllegalArgumentException {
		super.addStructure(structure);

		UpdateController update = AppBase.sgetUpdateController();
		System.out.println("UIAppStructureModel: addStructure");
		update.fireUpdateViewEvent(UpdateEvent.Action.STRUCTURE_ADDED, structure);
	}

	/**
	 * Remove a structure
	 */
	@Override
	public synchronized void removeStructure( final Structure structure )
			throws IllegalArgumentException
			{
		if ( structure == null ) {
			throw new IllegalArgumentException( "null structure" );
		}
		if ( ! this.structures.contains( structure ) ) {
			throw new IllegalArgumentException( "structure not found" );
		}

		UpdateController update = AppBase.sgetUpdateController();
		update.fireUpdateViewEvent(UpdateEvent.Action.STRUCTURE_REMOVED, structure);

		this.structures.remove( structure );
			}

	/**
	 * Set an array of structures (happens on load.)
	 */
	@Override
	public synchronized void setStructures(Structure[] structure_array)
	{
		if (structure_array != null)
		{
			for (final Structure struc : structure_array)
			{
				structures.add(struc);
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						UpdateController update = AppBase.sgetUpdateController();
						update.fireUpdateViewEvent(UpdateEvent.Action.STRUCTURE_ADDED, struc);
						update.fireUpdateViewEvent(UpdateEvent.Action.VIEW_UPDATE);

					}
				});
			}
	//		UpdateController update = AppBase.sgetUpdateController();
	//		update.fireUpdateViewEvent(UpdateEvent.Action.VIEW_UPDATE);
		}
	}
}
