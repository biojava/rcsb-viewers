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


/**
 *  This class implements the ColorMap interface by generating
 *  a linear interpolated color from a list of user-defined color knots.
 *  <P>
 *  Each knot consists of a float[4] tuple where:<BR>
 *  float[0] is the knot location (0.0 to 1.0)<BR>
 *  float[1] is the knot red value (0.0 to 1.0)<BR>
 *  float[2] is the knot green value (0.0 to 1.0)<BR>
 *  float[3] is the knot blue value (0.0 to 1.0)<BR>
 *  <P>
 *  A knot set consists of a float[n][4] tuple.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.IColorMap
 */
public class InterpolatedColorMap
	implements IColorMap
{
	/**
	 * The currently active set of color knots.
	 */
	private float knots[][] = null;

	/**
	 * Predefined knots defining a black to white ramp.
	 */
	private static final float[][] blackToWhite =
	{
		{ 0.00f,    0.00f, 0.00f, 0.00f },  // Black
		{ 1.00f,    1.00f, 1.00f, 1.00f }   // White
	};

	/**
	 * Predefined knots defining a rainbow (R-O-Y-G-B-I-V) ramp.
	 */
	private static final float[][] rainbow =
	{
		{ 0.00f,    1.00f, 0.00f, 0.00f },  // Red
		{ 0.16f,    1.00f, 0.50f, 0.00f },  // Orange
		{ 0.33f,    1.00f, 1.00f, 0.00f },  // Yellow
		{ 0.50f,    0.00f, 1.00f, 0.00f },  // Green
		{ 0.66f,    0.00f, 0.00f, 1.00f },  // Blue
		{ 0.83f,    0.40f, 0.00f, 0.40f },  // Indigo
		{ 1.00f,    0.93f, 0.51f, 0.93f }   // Violet
	};

	/**
	 * Predefined knots defining a blue to red ramp.
	 */
	private static final float[][] coldToHot =
	{
		{ 0.00f,    0.00f, 0.00f, 1.00f },  // Blue
		{ 0.50f,    0.75f, 0.00f, 0.75f },  // Purple
		{ 1.00f,    1.00f, 0.00f, 0.00f }   // Red
	};

	/**
	 * Predefined knots defining a light-blue to light-red ramp.
	 */
	private static final float[][] coolToWarm =
	{
		{ 0.00f,    0.50f, 0.50f, 1.00f },  // Blue
		{ 0.50f,    0.75f, 0.50f, 0.75f },  // Purple
		{ 1.00f,    1.00f, 0.50f, 0.50f }   // Red
	};

	/**
	 * Predefined knots defining a continuous HSB sweep ramp.
	 */
	private static final float[][] hsbHues =
	{
		{ 0.00f,    1.00f, 0.00f, 0.00f },  // Red
		{ 0.16f,    1.00f, 1.00f, 0.00f },  // Yellow
		{ 0.33f,    0.00f, 1.00f, 0.00f },  // Green
		{ 0.50f,    0.00f, 1.00f, 1.00f },  // Cyan
		{ 0.66f,    0.00f, 0.00f, 1.00f },  // Blue
		{ 0.83f,    1.00f, 0.00f, 1.00f },  // Magenta
		{ 1.00f,    1.00f, 0.00f, 0.00f }   // Red
	};

	/**
	 * Predefined knots defining a continuous MolScript-like sweep ramp.
	 */
	private static final float[][] molscriptRamp =
	{
		{ 0.00f,    0.50f, 0.50f, 1.00f },  // Blue
		{ 0.25f,    0.50f, 1.00f, 1.00f },  // Cyan
		{ 0.50f,    0.50f, 1.00f, 0.50f },  // Green
		{ 0.75f,    1.00f, 1.00f, 0.50f },  // Yellow
		{ 1.00f,    1.00f, 0.50f, 0.50f }   // Red
	};
	
	/**
	 * Predefined knots defining a continuous hydyrophobicity sweep ramp.
	 */
	private static final float[][] hydrophobicityRamp =
	{
		{ 0.00f,    0.00f, 0.00f, 0.50f },  // Blue
		{ 0.30f,    0.90f, 0.90f, 1.00f },  // White
//		{ 1.00f,    0.00f, 0.50f, 0.00f },  // Green
		{ 1.00f,    0.85f, 0.70f, 0.10f },  // Dark yellow
	};

	/**
	 * Predefined knot index defining a black to white ramp.
	 */
	public static final int BLACK_TO_WHITE = 0;

	/**
	 * Predefined knot index defining a rainbow (R-O-Y-G-B-I-V) ramp.
	 */
	public static final int RAINBOW = 1;

	/**
	 * Predefined knot index defining a blue to red ramp.
	 */
	public static final int COLD_TO_HOT = 2;

	/**
	 * Predefined knot index defining a blue to red ramp.
	 */
	public static final int COOL_TO_WARM = 3;

	/**
	 * Predefined knot index defining a continuous HSB sweep ramp.
	 */
	public static final int HSB_HUES = 4;

	/**
	 * Predefined knot index defining a continuous MolScript-like sweep ramp.
	 */
	public static final int MOLSCRIPT_RAMP = 5;
	
	/**
	 * Predefined knot index defining a continuous hydrophobicity sweep ramp.
	 */
	public static final int HYDROPHOBICITY_RAMP = 6;

	/**
	 * Indexable predefined knot sets.
	 */
	private static final float[][][] predefinedKnots =
	{
		InterpolatedColorMap.blackToWhite,
		InterpolatedColorMap.rainbow,
		InterpolatedColorMap.coldToHot,
		InterpolatedColorMap.coolToWarm,
		InterpolatedColorMap.hsbHues,
		InterpolatedColorMap.molscriptRamp,
		InterpolatedColorMap.hydrophobicityRamp
	};


	/**
	 * Construct an empty InterpolatedColorMap.
	 * NOTE that this constructor is private since an empty map is illegal,
	 * so, this constructor can never be called.
	 */
	private InterpolatedColorMap( )
	{
	}

	/**
	 * Construct an InterpolatedColorMap with predefined knots.
	 */
	public InterpolatedColorMap( final int predefined )
	{
		this.setKnots( predefined );
	}

	/**
	 * Generate the N-th "nice" high-contrast color.
	 * The color index must be >= 0.
	 */
	public static void getNiceColor( final int index, final float[] rgb )
		throws IllegalArgumentException
	{
		if ( true ) {
			throw new IllegalArgumentException( "not implemented" );
		}
		if ( index < 0 ) {
			throw new IllegalArgumentException( "negative index" );
		}
		if ( rgb == null ) {
			throw new IllegalArgumentException( "null rgb array" );
		}

		// TODO: JLM DEBUG
		// Convert the index into an HSB tuple.

		final float h = 0.0f;
		final float s = 1.0f;
		final float b = 1.0f;

		// Convert the HSB tuple to an sRGB value.

		final int sRGB = Color.HSBtoRGB( h, s, b );

		// TODO: JLM DEBUG
		// Extract the sRGB value components into the rgb[3] tuple.
	/*
		rgb[0] = (sRgb | 0xff) / aaa;
		rgb[1] = (sRgb | 0xff) / bbb;
		rgb[2] = (sRgb | 0xff) / ccc;
	*/
	}

	/**
	 * Construct an InterpolatedColorMap with explicit knots.
	 */
	public InterpolatedColorMap( final float[][] knots )
	{
		this.setKnots( knots );
	}

	/**
	 * Set the knots used by the color map.
	 */
	public void setKnots( final int predefined )
		throws IllegalArgumentException
	{
		try
		{
			this.setKnots( InterpolatedColorMap.predefinedKnots[predefined] );
		}
		catch ( final IndexOutOfBoundsException e )
		{
			throw new IllegalArgumentException( "Unknown predefined knots" );
		}
	}

	/**
	 * Set the knots used by the color map (note that the knots are copied
	 * to prevent knot or color changes outside of this class).
	 */
	public void setKnots( final float[][] knots )
		throws IllegalArgumentException
	{
		if ( knots == null ) {
			throw new IllegalArgumentException( "null knots" );
		}
		if ( knots[0].length != 4 ) {
			throw new IllegalArgumentException( "knot element length != 4" );
		}

		final float newKnots[][] = new float[knots.length][4];

		float priorWeight = -1.0f;
		for ( int i=0; i<knots.length; i++ )
		{
			// Make sure that the knot locations are in bounds.
			if ( knots[i][0] < 0.0f ) {
				throw new IllegalArgumentException( "knot < 0.0" );
			}
			if ( knots[i][0] > 1.0f ) {
				throw new IllegalArgumentException( "knot > 1.0" );
			}

			// Make sure that the weights are ordered.
			if ( priorWeight > knots[i][0] ) {
				throw new IllegalArgumentException( "unordered weights" );
			}
			priorWeight = knots[i][0];

			// Make sure the colors are normalized.
			if ( knots[i][1] < 0.0f ) {
				throw new IllegalArgumentException( "non-normalized color" );
			}
			if ( knots[i][1] > 1.0f ) {
				throw new IllegalArgumentException( "non-normalized color" );
			}
			if ( knots[i][2] < 0.0f ) {
				throw new IllegalArgumentException( "non-normalized color" );
			}
			if ( knots[i][2] > 1.0f ) {
				throw new IllegalArgumentException( "non-normalized color" );
			}
			if ( knots[i][3] < 0.0f ) {
				throw new IllegalArgumentException( "non-normalized color" );
			}
			if ( knots[i][3] > 1.0f ) {
				throw new IllegalArgumentException( "non-normalized color" );
			}

			// Copy the values
			newKnots[i][0] = knots[i][0];
			newKnots[i][1] = knots[i][1];
			newKnots[i][2] = knots[i][2];
			newKnots[i][3] = knots[i][3];
		}

		this.knots = newKnots;
	}

	/**
	 * Get a copy of the knots used by the color map.
	 */
	public void getKnots( final float[][] knotsCopy )
		throws IllegalArgumentException
	{
		if ( knotsCopy == null ) {
			throw new IllegalArgumentException( "null knots" );
		}
		if ( knotsCopy[0].length != 4 ) {
			throw new IllegalArgumentException( "knot element length != 4" );
		}

		for ( int i=0; i<this.knots.length; i++ )
		{
			for ( int j=0; j<this.knots[0].length; j++ )
			{
				knotsCopy[i][j] = this.knots[i][j];
			}
		}
	}
	
	
	public Object clone() {
		final InterpolatedColorMap map = new InterpolatedColorMap();
		map.knots = new float[this.knots.length][];
		for(int i = 0; i < this.knots.length; i++) {
			map.knots[i] = new float[this.knots[i].length];
			for(int j = 0; j < this.knots[i].length; j++) {
				map.knots[i][j] = this.knots[i][j];
			}
		}
		
		return map;
	}

	/**
	 * Returns the number of knots in the color map.
	 */
	public int getKnotCount( )
	{
		if ( this.knots == null ) {
			return 0;
		}
		return this.knots.length;
	}

	/**
	 * Set the color at the specified knot index.
	 */
	public void setKnotColor( final int knotIndex, final float[] color )
		throws IllegalArgumentException
	{
		if ( color == null ) {
			throw new IllegalArgumentException( "null color array" );
		}

		if ( this.knots == null ) {
			throw new IllegalArgumentException( "null knot list" );
		}

		this.knots[knotIndex][1] = color[0];
		this.knots[knotIndex][2] = color[1];
		this.knots[knotIndex][3] = color[2];
	}

	/**
	 * Get the color at the specified knot index.
	 */
	public void getKnotColor( final int knotIndex, final float[] color )
		throws IllegalArgumentException
	{
		if ( color == null ) {
			throw new IllegalArgumentException( "null color array" );
		}

		if ( this.knots == null ) {
			throw new IllegalArgumentException( "null knot list" );
		}

		color[0] = this.knots[knotIndex][1];
		color[1] = this.knots[knotIndex][2];
		color[2] = this.knots[knotIndex][3];
	}

	/**
	 * Get the location value of the specified knot.
	 */
	public float getKnotLocation( final int knotIndex )
		throws IllegalArgumentException
	{
		if ( this.knots == null ) {
			throw new IllegalArgumentException( "null knot list" );
		}

		return this.knots[knotIndex][0];
	}

	/**
	 * Set the location value of the specified knot.
	 */
	public void setKnotLocation( final int knotIndex, final float location_ )
		throws IllegalArgumentException
	{
		float location = location_;
		if ( this.knots == null ) {
			throw new IllegalArgumentException( "null knot list" );
		}

		// Silently ignore attempts to set first or last knot locations
		// since they must always be fixed to 0.0 and 1.0.
		if ( knotIndex == 0 ) {
			return;
		}
		if ( knotIndex == this.knots.length-1 ) {
			return;
			// Since we return on end-knots, remaining knots are internal.
		}

		// Silently constrain location to the 0.0 to 1.0 range.
		if ( location < 0.0f ) {
			location = 0.0f;
		}
		if ( location > 1.0f ) {
			location = 1.0f;
		}

		// Silently constrain location between neighboring knot locations.
		if ( location <= this.knots[knotIndex-1][0] ) {
			return;
		}
		if ( location >= this.knots[knotIndex+1][0] ) {
			return;
		}

		this.knots[knotIndex][0] = location;
	}

	/**
	 * Given a normalized float (0.0-1.0) value, the getColor
	 * method returns an interpolated RGB color value based upon
	 * the user-defined color knots. Values may be clamped.
	 */
	public void getColor( final float value, final float[] color )
		throws IllegalArgumentException
	{
		if ( color == null ) {
			throw new IllegalArgumentException( "null color array" );
		}
		if ( value < 0.0f ) {
			throw new IllegalArgumentException( "value < 0.0" );
		}
		if ( value > 1.0f ) {
			throw new IllegalArgumentException( "value > 1.0" );
		}

		//
		// If 0 knots, then use the "value" as a grayscale.
		//

		if ( (this.knots == null) || (this.knots.length <= 0) )
		{
			color[0] = value;
			color[1] = value;
			color[2] = value;
			return;
		}

		//
		// If 1 knot, then use the one knot's fixed color.
		//

		if ( this.knots.length == 1 )
		{
			final float knot[] = this.knots[0];
			color[0] = knot[1];
			color[1] = knot[2];
			color[2] = knot[3];
			return;
		}

		//
		// We have 2 or more knots, so we can interpolate.
		//

		final int knotCount = this.knots.length;
		float knotA[] = this.knots[0];
		float knotB[] = this.knots[0];
		// First, find the two closest knots
		for ( int i=0; i<knotCount; i++ )
		{
			final float knot[] = this.knots[i];
			if ( knot[0] == value )
			{
				// We have an exact value match so just return the color.
				color[0] = knot[1];
				color[1] = knot[2];
				color[2] = knot[3];
				return;
			}
			else if ( knot[0] < value )
			{
				knotA = knot;
				knotB = knot;
				continue;
			}
			else if ( knot[0] > value )
			{
				knotB = knot;
				break;
			}
		}

		final float weightA = (value - knotA[0]) / (knotB[0] - knotA[0]);
		final float weightB = 1.0f - weightA;

		color[0] = knotA[1] * weightB + knotB[1] * weightA;
		color[1] = knotA[2] * weightB + knotB[2] * weightA;
		color[2] = knotA[3] * weightB + knotB[3] * weightA;
	}
}

