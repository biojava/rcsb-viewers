package org.rcsb.demo.utils;

//Copyright 2000-2008 The Regents of the University of California.
//All Rights Reserved.
//
//Permission to use, copy, modify and distribute any part of this
//Molecular Biology Toolkit (MBT)
//for educational, research and non-profit purposes, without fee, and without
//a written agreement is hereby granted, provided that the above copyright
//notice, this paragraph and the following three paragraphs appear in all
//copies.
//
//Those desiring to incorporate this MBT into commercial products
//or use for commercial purposes should contact the Technology Transfer &
//Intellectual Property Services, University of California, San Diego, 9500
//Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858) 534-5815,
//FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
//
//IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
//DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING
//LOST PROFITS, ARISING OUT OF THE USE OF THIS MBT, EVEN IF THE
//UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.//
//THE MBT PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE
//UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
//UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES
//NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
//EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
//MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
//MBT WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
//
//For further information, please see:  http://mbt.sdsc.edu

/**
 * Output demo strings and control indenting
 * 
 * @author rickb
 *
 */
public class Output
{
	static private int indentSize = 4;
	static private String indent = "";
	static private String indentBuf = null;
	
	/**
	 * Bump the indent
	 */
	static public void incrementIndent()
	{
		if (indentBuf == null)
		{
			indentBuf = new String();
			for (int i = 0; i < indentSize; i++)
				indentBuf += ' ';
		}
		
       indent += indentBuf;
	}
	
	/**
	 * Back out the current indent
	 */
	static public void decrementIndent()
	{
		if (indent.length() >= indentSize)
			indent = indent.substring(0, indent.length() - indentSize);
	}
	
	/**
	 * Output a line with the current indent factor.
	 * 
	 * @param line
	 */
	static public void lineOut(String line)
	{
		System.out.println(line.length() == 0? "" : indent + line);
	}
}
