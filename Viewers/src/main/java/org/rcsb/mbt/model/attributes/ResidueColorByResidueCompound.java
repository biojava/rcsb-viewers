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
package org.rcsb.mbt.model.attributes;


import java.awt.Color;
import java.util.TreeMap;

import org.rcsb.mbt.model.*;



/**
 *  This class implements the ResidueColor interface by applying a color
 *  to the given Residue by using ResidueIndex.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IResidueColor
 *  @see	org.rcsb.mbt.model.Residue
 */
public class ResidueColorByResidueCompound
	implements IResidueColor
{
	public static final String NAME = "By Compound";

	// Holds a singleton instance of this class.
	private static ResidueColorByResidueCompound singleton = null;

	public static final TreeMap colorByCompound = new TreeMap();
	private static final Color unknownColor = new Color(204, 204, 204);
	
	{
		final Object[] mapping = {
			"phe" , new Color(153, 255, 153),
			"val" , new Color(0, 204, 0),
			"ala" , new Color(0, 153, 0),
			"ili" , new Color(51, 255, 51),
			"leu" , new Color(51, 255, 51),
			"pro" , new Color(204, 204, 0),
			"met" , new Color(51, 255, 51),
			"asp" , new Color(204, 0, 0),
			"glu" , new Color(255, 51, 51),
			"arg" , new Color(255, 204, 51),
			"lys" , new Color(255, 153, 51),
			"ser" , new Color(0, 0, 255),
			"thr" , new Color(0, 102, 255),
			"asn" , new Color(0, 153, 204),
			"gln" , new Color(0, 204, 255),
			"cys" , new Color(255, 51, 204),
			"tyr" , new Color(0, 255, 204),
			"his" , new Color(204, 102, 255),
			"trp" , new Color(255, 255, 0),
			"gly" , new Color(153, 153, 153),
			"unk" , new Color(204, 204, 204),
			"asx" , new Color(255, 255, 255),
			"glx" , new Color(255, 255, 255)
		};
		
		for(int i = 0; i < mapping.length; i += 2) {
			ResidueColorByResidueCompound.colorByCompound.put(mapping[i], mapping[i + 1]);
		}	
	}
	
	/**
	 *  The constructor is PRIVATE so that the "create" method
	 *  is used to produce a singleton instance of this class.
	 */
	private ResidueColorByResidueCompound( )
	{
	}

	/**
	 *  Return the singleton instance of this class.
	 */
	public static ResidueColorByResidueCompound create( )
	{
		if ( ResidueColorByResidueCompound.singleton == null ) {
			ResidueColorByResidueCompound.singleton = new ResidueColorByResidueCompound( );
		}
		return ResidueColorByResidueCompound.singleton;
	}

	/**
	 *  Produce a color based upon the residue element type.
	 */
	public void getResidueColor( final Residue residue, final float[] color )
	{
		final Color colorOb = (Color)ResidueColorByResidueCompound.colorByCompound.get(residue.getCompoundCode().toLowerCase());
		if(colorOb != null) {
			colorOb.getColorComponents(color);
		} else {
			ResidueColorByResidueCompound.unknownColor.getColorComponents(color);
		}
	}
}

