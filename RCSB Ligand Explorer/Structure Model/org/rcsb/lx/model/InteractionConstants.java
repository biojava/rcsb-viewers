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
 * Created on  * Created on Jan 9, 2004
 *
 */ 
package org.rcsb.lx.model;

import java.awt.Color;

/**
 * @author qzhang
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InteractionConstants {

	/**
	 * Color used to display distance monitors in <code>StructureViewer</code>
	 */
	//public static float monitorColor[] = { 0.0f, 0.9f, 0.1f };
	public static float hydrophobicBondColor[]={1.0f, 0.0f, 1.0f};
	public static float hydrophilicBondColor[]={0.0f, 1.0f, 0.0f};
//	public static float dkRedColor[]={0.5f, 0.0f, 0.0f};
	public static float otherBondColor[]={1.0f, 1.0f, 1.0f};
	public static float interLigandBondColor[]={1.0f, 0.0f, 0.0f};
	public static float waterBondColor[]={0.0f,0.0f,1.0f};
	
	public static Color hydrophobicBondColorOb = new Color(InteractionConstants.hydrophobicBondColor[0], InteractionConstants.hydrophobicBondColor[1], InteractionConstants.hydrophobicBondColor[2]);
	public static Color hydrophilicBondColorOb = new Color(InteractionConstants.hydrophilicBondColor[0], InteractionConstants.hydrophilicBondColor[1], InteractionConstants.hydrophilicBondColor[2]);
	public static Color otherColorOb = new Color(InteractionConstants.otherBondColor[0], InteractionConstants.otherBondColor[1], InteractionConstants.otherBondColor[2]);
	public static Color interLigandBondColorOb = new Color(InteractionConstants.interLigandBondColor[0], InteractionConstants.interLigandBondColor[1], InteractionConstants.interLigandBondColor[2]);
	public static Color waterBondColorOb = new Color(InteractionConstants.waterBondColor[0], InteractionConstants.waterBondColor[1], InteractionConstants.waterBondColor[2]);

	public static final String hydrophobicType = "Hydrophobic";
	public static final String hydrophilicType = "Hydrophilic";
	public static final String interLigandType = "Inter-ligand";
	public static final String waterMediatedType = "Bridged H-Bond";
	public static final String otherType = "Other";
}
