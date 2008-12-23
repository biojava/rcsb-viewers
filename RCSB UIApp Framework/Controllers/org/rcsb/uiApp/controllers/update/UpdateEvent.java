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
package org.rcsb.uiApp.controllers.update;

import org.rcsb.mbt.model.Structure;


/**
 *  An event generated when structural changes are made to a StructureDocument.
 *  This event class is used when Viewer or Structure objects are
 *  added or removed from a StructureDocument. But, this event type is NOT
 *  used when the internal state of any of those objects change (see the
 *  StructureEvent, StructureMapEvent, and StructureStyleEvent classes).
 * 
 *  @author rickb
 *  @see	org.rcsb.mbt.model.StructureModel
 *  @see	org.rcsb.uiApp.controllers.update.IUpdateListener
 *  @see	org.rcsb.mbt.model.Structure
 */
public class UpdateEvent
{
	 /**
	  * The possible actions for this event type
	  * EXTENDED is for derived classes to use.
	  * 
	  * @author rickb
	  *
	  */
	 public enum Action { STRUCTURE_ADDED, STRUCTURE_REMOVED, VIEW_ADDED, VIEW_REMOVED, VIEW_RESET, VIEW_UPDATE, CLEAR_ALL, EXTENDED }
	 
	 /**
	  * Constructor - takes an action
	  * @param action = the action to apply
	  */
	 public UpdateEvent(final Action action)
	 {
		 this.action = action;
		 this.view = null;
		 this.structure = null;
	 }
	 
	 /**
	  * Constructor 
	  * @param action - {@inheritDoc}
	  * @param structure - the affected structure
	  */
	 public UpdateEvent(final Action action, final Structure structure)
	 {
		 this.action = action;
		 this.structure = structure;
		 this.view = null;
	 }
	 
	 /**
	  * Constructor
	  * @param action - {@inheritDoc}
	  * @param view - the affected view
	  */
	 public UpdateEvent(final Action action, final IUpdateListener view)
	 {
		 this.action = action;
		 this.view = view;
		 this.structure = null;
	 }
	 
	/**
	 * The Structure object that is affected in this change.
	 */
	 public Structure structure;
	 
	 /**
	  * The view affected in this change.
	  */
	 public IUpdateListener view;
	 
	 /**
	  * The action taken
	  */
	 public Action action;
}

