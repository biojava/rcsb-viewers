package org.rcsb.mbt.glscene.jogl;

//  $Id: Interpolator.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.//
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
//  $Log: Interpolator.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.1  2006/08/24 17:39:03  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2006/03/09 00:18:55  jbeaver
//  Initial commit
//
//  Revision 1.0 2005/01/05 00:12:54  moreland
//
                                                                                
                                                                                
// package org.rcsb.mbt.util;


public class Interpolator
{
	public static final int LINEAR  = 0;
	public static final int COSINE  = 1;
	public static final int CUBIC   = 2;
	public static final int HERMITE = 3;

	private int algorithm = -1;


	/**
	 * Primary Constructor.
	 */
	public Interpolator( final int algorithm )
	{
		this.setAlgorithm( algorithm );
	}


	/**
	 * Utility Constructor.
	 */
	public Interpolator( )
	{
		this( Interpolator.LINEAR );
	}


	/**
	 * Set the interpolation algorithm.
	 * <P>
	 * @param	algorithm	the algorithm used when interpolate is called.
	 * @throws	IllegalArgumentException	for invalid algorithm values.
	 */
	public void setAlgorithm( final int algorithm )
	{
		if ( (algorithm < Interpolator.LINEAR) || (algorithm > Interpolator.HERMITE) ) {
			throw new IllegalArgumentException( "Bad algorithm " + algorithm );
		}
		this.algorithm = algorithm;
	}


	/**
	 * Get the interpolation algorithm.
	 * <P>
	 */
	public int getAlgorithm( )
	{
		return this.algorithm;
	}


	/**
	 * Interpolate the input knots with the specified number of subdivisions
	 * per input segment. A segment is the region between each adjacent knot
	 * pair. The interpolation path will always pass through (include) the
	 * original input knots.
	 * <P>
	 * @param	knots	the starting knots from which to interpolate.
	 * @param	subs	the number of subdivisions per input segment.
	 * @return	interpolated point array.
	 * @throws	NullPointerException	if knots array is null.
	 * @throws	IllegalArgumentException	if knots.length < 2.
	 * @throws	IllegalArgumentException	if subs < 0.
	 */
	public float[][] interpolate( final float[][] knots, final int subs )
	{
		if ( knots == null ) {
			throw new NullPointerException( "null knots" );
		}

		if ( knots.length < 2 ) {
			throw new IllegalArgumentException( "knots.length < 2" );
		}

		if ( subs < 0 ) {
			throw new IllegalArgumentException( "subs < 0" );
		}


		// Calculate the new knot count.
		final int newKnotCount = (knots.length - 1) * (subs + 1) + 1;
		if ( (newKnotCount == knots.length) || (subs == 0) ) {
			return knots;
		}

		// Allocate the new knots.
		final int dim = knots[0].length; // Dimension of each knot.
		final float[][] newKnots = new float[newKnotCount][dim];

		// Walk each input knot (except the last one; its copied at the end).
		for ( int k=0; k<knots.length-1; k++ )
		{
			// Walk each inter-knot sample.
			for ( int s=0; s<=subs; s++ )
			{
				// The Sample Point (sp) varies from 0.0 to 1.0 and always
				// samples the range between knots[k] and knots[k+1], but the
				// interpolator can look at knots ahead/behind as needed.
				// Also, sp never actually reaches 1.0 since this last value
				// will be computed for sp=0.0 when the next knot is visited.
				final double sp = s / (subs+1.0);

				// Calculate the current newKnots index.
				final int i = k * (subs+1) + s;

				// Walk each dimension (ie: 0=x, 1=y, 2=z, etc)
				// and compute the interpolated value "val".
				for ( int d=0; d<dim; d++ )
				{
					double val = 0.0; // Value computed by an algorithm.

					// Algorithms adapted from a write-up by Paul Bourke,
					// Centre for Astrophysics and Supercomputing,
					// Melbourne, Australia.
					// pdb@swin.edu.au
					// http://astronomy.swin.edu.au/~pbourke/analysis/interpolation/

					if ( this.algorithm == Interpolator.LINEAR )
					{
						// Implements a very basic linear interpolation.
						val = knots[k][d] * (1.0 - sp) + knots[k+1][d] * sp;
					}
					else if ( this.algorithm == Interpolator.COSINE )
					{
						// Provides a nice "ease-in/out" around the knots.
						final double mu2 = (1.0 - Math.cos( sp * Math.PI )) / 2.0;
						val = knots[k][d] * (1.0 - mu2) + knots[k+1][d] * mu2;
					}
					else if ( (this.algorithm == Interpolator.CUBIC) || (this.algorithm == Interpolator.HERMITE) )
					{
						// Common code for algorithms needing four input knots.
						// Generally we have:
						//                    Sample Point
						//                          |
						//                          v
						//       y0           y1         y2          y3
						//    knots[k-1]   knots[k]   knots[k+1]   knots[k+2]
						//
						// But, at end-points, we substitute neerest neigbors.

						double y0, y1, y2, y3;
						if ( k == 0 ) {
							y0 = knots[k][d];
						} else {
							y0 = knots[k-1][d];
						}

						y1 = knots[k][d];

						if ( k == (knots.length-1) ) {
							y2 = knots[k][d];
						} else {
							y2 = knots[k+1][d];
						}

						if ( k == (knots.length-2) ) {
							y3 = knots[k+1][d];
						} else {
							y3 = knots[k+2][d];
						}

						// Four-Point algorithms.

						if ( this.algorithm == Interpolator.CUBIC )
						{
							// Implements cubic interpolation.
							final double mu2 = sp * sp;
							final double a0 = y3 - y2 - y0 + y1;
							final double a1 = y0 - y1 - a0;
							final double a2 = y2 - y0;
							final double a3 = y1;

							val = a0 * sp * mu2 + a1 * mu2 + a2 * sp + a3;
						}
						else if ( this.algorithm == Interpolator.HERMITE )
						{
							// Implements hermite interpolation.

							// Tension tightens curvature at the known points.
							// 1 is high, 0 normal, -1 is low.
							final double tension = 0.0;
							// Bias twists the curve about the known points.
							// 0 is even, positive is towards first segment,
							// negative is towards the other.
							final double bias = 0.0;

							final double mu2 = sp * sp;
							final double mu3 = mu2 * sp;
							double m0  = (y1-y0)*(1.0+bias)*(1.0-tension)/2.0;
							       m0 += (y2-y1)*(1.0-bias)*(1.0-tension)/2.0;
							double m1  = (y2-y1)*(1.0+bias)*(1.0-tension)/2.0;
							       m1 += (y3-y2)*(1.0-bias)*(1.0-tension)/2.0;
							final double a0 =  2*mu3 - 3*mu2 + 1;
							final double a1 =    mu3 - 2*mu2 + sp;
							final double a2 =    mu3 -   mu2;
							final double a3 = -2*mu3 + 3*mu2;

							val = a0 * y1 + a1 * m0 + a2 * m1 + a3 * y2;
						}
					}

					newKnots[i][d] = (float) val;
				}
			}
		}

		// The last newKnot is always just a copy of the last input knot value.
		for ( int d=0; d<dim; d++ )
		{
			newKnots[newKnots.length-1][d] = knots[knots.length-1][d];
		}

		return newKnots;
	}


	/**
	 * Unit testing.
	 * <P>
	 * <PRE>
	 * java Interpolator > Interpolator.dat
	 * gnuplot
	 * plot "Interpolator.dat" with linespoints
	 * set term post landscape color "Times-Roman" 12
	 * set output "Interpolator.ps"
	 * replot
	 * quit
	 * </PRE>
	 */
	public static void main( final String args[] )
	{
		final float [][] knots = {
			{ 0.0f, 0.0f, 0.0f },
			{ 1.0f, 3.0f, 1.0f },
			{ 3.0f, 1.0f, 0.0f },
		};

		final Interpolator interpolator = new Interpolator( );

		for ( int a=Interpolator.LINEAR; a<=Interpolator.HERMITE; a++ )
		{
			interpolator.setAlgorithm( a );
			final float [][] newKnots = interpolator.interpolate( knots, 8 );

			System.out.println( "# algorithm = " + a );

			for ( int k=0; k<newKnots.length; k++ )
			{
				System.out.println( newKnots[k][0] + " " + newKnots[k][1] + " " + newKnots[k][2] );
			}

			System.out.println( );
		}
	}
}
