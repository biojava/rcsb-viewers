//  $Id: ChainStyle.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: ChainStyle.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:42  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.3  2006/09/06 04:46:12  jbeaver
//  Added initial capability for labeling ribbons
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
//  Revision 1.1  2005/11/10 23:19:47  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.0 2005/04/04 00:12:54  moreland
//


package org.rcsb.mbt.model.attributes;


import java.util.HashMap;

import org.rcsb.mbt.model.*;



/**
 * ChainStyle.java<BR>
 * <P>
 * Please complete these missing tags
 * @author	John L. Moreland
 * @copyright	UCSD
 * @see
 */
public class ChainStyle
	extends Style
	implements Cloneable
{
	// Style properties
	private IResidueColor residueColor = ResidueColorRegistry.getDefault( );
	public static int PROPERTY_COLOR = 1;
	public static int PROPERTY_BINDING = 2;
	private IResidueLabel residueLabel = ResidueLabelCustom.getSingleton();

	private boolean residueBinding = false;
	private final HashMap rBinding = new HashMap();
	//
	// Constructors
	//


	/**
	 * Construct an object which stores styles for a Chain.
	 * <P>
	 */
	public ChainStyle( )
	{
	}


	//
	// Cloneable interface
	//


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param	NA
	 * @return	NA
	 * @throws	NA
	 */
	public void copy( final ResidueStyle residueStyle )
	{
		this.residueColor = residueStyle.getResidueColor( );
	}


	/**
	 * Please complete the missing tags for main
	 * <P>
	 * @param	NA
	 * @return	NA
	 * @throws	NA
	 */
	
	public Object clone( )
		throws CloneNotSupportedException
	{
		return super.clone( );
	}


	//
	// ChainStyle methods
	//


	/**
	 * Return true if the Style implementation is type safe for
	 * the given StructureComponent type. That is, can the style
	 * be applied to the type?
	 * <P>
	 * @return
	 * @throws
	 */
	
	public final boolean isTypeSafe( final String scType )
	{
		return (scType == StructureComponentRegistry.TYPE_CHAIN);
	}


	/**
	 * xxx.
	 * <P>
	 */
	public void setResidueColor( final IResidueColor residueColor )
	{
		if ( residueColor == null ) {
			throw new NullPointerException( "null residueColor argument" );
		}

		this.residueColor = residueColor;
	}


	/**
	 * xxx.
	 * <P>
	 */
	public IResidueColor getResidueColor( )
	{
		return this.residueColor;
	}


	/**
	 * xxx.
	 * <P>
	 */
	public void getResidueColor( final Residue residue, final float[] color )
	{
		if ( residue == null ) {
			throw new NullPointerException( "null residue argument" );
		}
		if ( color == null ) {
			throw new NullPointerException( "null color argument" );
		}

		this.residueColor.getResidueColor( residue, color );
	}


	public IResidueLabel getResidueLabel() {
		return this.residueLabel;
	}


	public void setResidueLabel(final IResidueLabel residueLabel) {
		this.residueLabel = residueLabel;
	}
	
	
	// Scratch event that is re-used for all fired events.
	private final StructureStylesEvent structureStylesEvent =
		new StructureStylesEvent();
	
	public void resetBinding(final Structure struc) {
		final StructureMap structureMap = struc.getStructureMap();
		for (int i=0; i<structureMap.getResidueCount(); i++)
		{
			final Residue r=structureMap.getResidue(i);
			if (this.rBinding.containsKey(r))
			{
				this.rBinding.remove(r);
				this.structureStylesEvent.structureComponent=r;
				this.structureStylesEvent.property=ChainStyle.PROPERTY_BINDING;
				this.fireStructureStylesEvent(this.structureStylesEvent);
			}
		}
	}


}

