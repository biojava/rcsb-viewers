//  $Id: InterpolatedColorMap.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
//
//  Copyright 2000-2004 The Regents of the University of California.
//  All Rights Reserved.
//
//  Permission to use, copy, modify and distribute any part of this
//  Molecular Biology Toolkit (MBT)
//  for educational, research and non-profit purposes, without fee, and without
//  a written agreement is hereby granted, provided that the above copyright
//  notice, this paragraph and the following three paragraphs appear in all
//  copies.
//
//  Those desiring to incorporate this MBT into commercial products
//  or use for commercial purposes should contact the Technology Transfer &
//  Intellectual Property Services, University of California, San Diego, 9500
//  Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815,
//  FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
//
//  IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
//  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//  LOST PROFITS, ARISING OUT OF THE USE OF THIS MBT, EVEN IF THE
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
//  THE MBT PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
//  UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
//  UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
//  NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
//  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
//  MBT WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
//
//  For further information, please see:  http://mbt.sdsc.edu
//
//  History:
//  $Log: InterpolatedColorMap.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/08/24 17:39:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.11  2005/06/17 22:17:52  moreland
//  The default color map now uses a new molscript-like but pastel color ramp.
//
//  Revision 1.10  2004/06/18 17:55:38  moreland
//  All uses of InterpolatedColorMap are now non-static.
//
//  Revision 1.9  2004/04/09 00:12:54  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.8  2004/01/29 17:53:41  moreland
//  Updated copyright and class comment block.
//
//  Revision 1.7  2004/01/01 17:14:43  moreland
//  Changed predefined static final color maps from int to float[][] type.
//  Added HSB_HUES predefined color map.
//  The InterpolatedColorMap(int) constructor will dynamically generate a color map.
//
//  Revision 1.6  2003/11/20 22:33:40  moreland
//  Brighted up the Indigo color a bit in the rainbow color map.
//
//  Revision 1.5  2003/11/13 23:39:28  moreland
//  Added getKnots method.
//
//  Revision 1.4  2003/11/06 23:20:57  moreland
//  Added setKnotLocation method.
//
//  Revision 1.3  2003/10/21 23:45:18  moreland
//  Added getKnotLocation method.
//  Improved look of the "cold to hot" color ramp.
//
//  Revision 1.2  2003/10/21 22:27:14  moreland
//  Fixed interpolation code (the application of knot weights were reversed).
//
//  Revision 1.1  2003/04/23 23:01:22  moreland
//  First version.
//
//  Revision 1.0  2003/02/25 18:33:19  moreland
//  First implementation.
//


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
 *  @see	org.rcsb.mbt.model.attributes.ColorMap
 */
public class InterpolatedColorMap
	implements ColorMap
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
	 * Indexable predefined knot sets.
	 */
	private static final float[][][] predefinedKnots =
	{
		InterpolatedColorMap.blackToWhite,
		InterpolatedColorMap.rainbow,
		InterpolatedColorMap.coldToHot,
		InterpolatedColorMap.coolToWarm,
		InterpolatedColorMap.hsbHues,
		InterpolatedColorMap.molscriptRamp
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

