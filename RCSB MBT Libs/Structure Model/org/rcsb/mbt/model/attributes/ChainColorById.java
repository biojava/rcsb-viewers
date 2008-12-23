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


/**
 *  This class implements the ChainColor interface by applying a color
 *  to the given Chain by using its chainId.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IChainColor
 *  @see	org.rcsb.mbt.model.Chain
 */
public class ChainColorById
	implements IChainColor
{
	public static final String NAME = "By ID";

	// Holds a singleton instance of this class.
	private static ChainColorById singleton = null;

	private IColorMap colorMap =
		new InterpolatedColorMap( InterpolatedColorMap.RAINBOW );

	/**
	 *  The constructor is PRIVATE so that the "create" method
	 *  is used to produce a singleton instance of this class.
	 */
	private ChainColorById( )
	{
	}

	/**
	 *  Return the singleton instance of this class.
	 */
	public static ChainColorById create( )
	{
		if ( ChainColorById.singleton == null ) {
			ChainColorById.singleton = new ChainColorById( );
		}
		return ChainColorById.singleton;
	}

	/**
	 *  Produce a color based upon the chain id.
	 */
	public void getChainColor( final Chain chain, final float[] color )
	{
		// Assuming chain id strings usually start with a letter,
		// use the first letter so we can compute a predictable range
		// and scale that to the needed normalized (0.0 - 1.0) range.
		final String chainId = chain.getChainId( );       // "A" .. "Z", "A1" ...
		final char firstChar = chainId.charAt( 0 );       // "A" .. "Z"
		final float firstCharFloat = firstChar;   // 65.0 - 90.0
		final float chainSample = (firstCharFloat - 65.0f) / (90.0f - 65.0f);

		this.colorMap.getColor( chainSample, color );
	}

	/**
	 *  Set the ColorMap used to color by Id.
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
	 *  Get the ColorMap used to color by Id.
	 */
	public IColorMap getColorMap( )
	{
		return this.colorMap;
	}
}

