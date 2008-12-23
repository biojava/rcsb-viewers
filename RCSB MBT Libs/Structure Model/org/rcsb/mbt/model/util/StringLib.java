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
package org.rcsb.mbt.model.util;


/**
 *  This class provides a number of general String processing utility methods.
 *  <P>
 *  @author	John L. Moreland
 */
public class StringLib
{
	/**
	 * Split the supplied string in two, returning the resulting strings as
	 * elements of an array.
	 * 
	 * @param str the string to split in two
	 * @param sep the separator character
	 * @return the strings resulting from the split operation
	 */
	public static String[] split( final String str, final String sep )
	{
		final String[] ret = new String[2];
	
		final int separator = str.indexOf( sep );
		ret[0] = str.substring( 0, separator );
		ret[1] = str.substring( separator+1 );
	
		return ret;
	}	
	
	/**
	 * Join separate strings to make a single string.
	 * 
	 * @param strs the array containing the strings to concatenate into a single
	 * string
	 * @param sep the character that will separate the individual strings
	 * @return the string that results from joining the supplied strings
	 */
	public static String join( final String[] strs, final String sep )
	{
		if ( strs == null ) {
			return null;
		}
		if ( sep == null ) {
			return null;
		}
	
		String ret = new String( strs[0] );
		for ( int i=1; i<strs.length; i++ ) {
			ret = ret.concat( sep + strs[i] );
		}
	
		return ret;
	}
	
	/**
	 * Sort the supplied array of strings in lexicographic order.
	 *
	 * @param theList the array of strings to sort
	 */
	public static void sort( final String[] theList )
	{
		StringLib.QuickSort( theList, 0, theList.length - 1 );
	}

	/**
	 * Swap the positions of two strings within an array
	 */
	private static void swap( final String[] s, final int i, final int j )
	{
		String S;
		S = s[i];
		s[i] = s[j];
		s[j] = S;
	}
	
	/**
	 * Sort an array of strings using the Quick Sort algorithm.
	 *
	 * @param a the array of strings to sort
	 * @param lo0 the low watermark for the sort
	 * @param hi0 the high watermark for the sort
	 */
	public static void QuickSort( final String[] a, final int lo0, final int hi0 )
	{
		int lo = lo0;
		int hi = hi0;
		String mid;
	
		if ( hi0 > lo0 )
		{
			// Establish partition element as the midpoint of the array
			mid = a[ ( lo0 + hi0 ) / 2 ];
		
			// loop through the array until indices cross
			while ( lo <= hi )
			{
				// Find 1st element >= partition element
				// starting from the left Index
				while ( ( lo < hi0 ) && ( a[lo].compareTo(mid) < 0 ) )
				{
					++lo;
				}
				// Find element <= partition element
				// starting from the right Index
				while ( ( hi > lo0 ) && ( a[hi].compareTo(mid) > 0 ) )
				{
					--hi;
				}
				// If the indexes have not crossed, then swap
				if ( lo <= hi )
				{
					StringLib.swap( a, lo, hi );
					++lo;
					--hi;
				}
			}
		
			// If the right index has not reached the left side of array
			// then sort the left partition
			if ( lo0 < hi )
			{
				StringLib.QuickSort( a, lo0, hi );
			}
		
			// If the left index has not reached the right side of array
			// then sort the right partition
			if ( lo < hi0 )
			{
				StringLib.QuickSort( a, lo, hi0 );
			}
		}
	}
}

