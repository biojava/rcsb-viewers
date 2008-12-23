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


import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.attributes.*;


/**
 *  This class implements the ResidueColor interface by applying a color
 *  to the given Residue by using ResidueIndex.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IResidueColor
 *  @see	org.rcsb.mbt.model.Residue
 */
public class ResidueColorByResidueIndexDna
	implements IResidueColor
{
	public static final String NAME = "By Index";

	// Holds a singleton instance of this class.
	private static ResidueColorByResidueIndexDna singleton = null;

	private IColorMap colorMap =
		new InterpolatedColorMap( InterpolatedColorMap.BLACK_TO_WHITE );

	/**
	 *  The constructor is PRIVATE so that the "create" method
	 *  is used to produce a singleton instance of this class.
	 */
	private ResidueColorByResidueIndexDna( )
	{
	}

	/**
	 *  Return the singleton instance of this class.
	 */
	public static ResidueColorByResidueIndexDna create( )
	{
		if ( ResidueColorByResidueIndexDna.singleton == null ) {
			ResidueColorByResidueIndexDna.singleton = new ResidueColorByResidueIndexDna( );
		}
		return ResidueColorByResidueIndexDna.singleton;
	}

	/**
	 *  Produce a color based upon the residue element type.
	 */
	public void getResidueColor( final Residue residue, final float[] color )
	{
		final StructureMap structureMap = residue.structure.getStructureMap( );
		final Chain chain = structureMap.getChain( residue.getChainId() );
		final int residueCount = chain.getResidueCount( );
		final int residueIndex = chain.getResidueIndex( residue );

		final float value = (float) residueIndex / (float) residueCount;
		this.colorMap.getColor( value, color );
	}

	/**
	 *  Set the ColorMap used to color by Chain-Residue Index.
	 */
	public void setColorMap( final IColorMap colorMap )
		throws IllegalArgumentException
	{
		if ( colorMap == null ) {
			throw new IllegalArgumentException( "null colorMap" );
		}
		this.colorMap = colorMap;
	}

	/**
	 *  Get the ColorMap used to color by Chain-Residue Index.
	 */
	public IColorMap getColorMap( )
	{
		return this.colorMap;
	}
}

