//$Id: AtomColorByRandom.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: AtomColorByRandom.java,v $
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
//  Revision 1.1  2005/10/06 17:07:53  jbeaver
//  Initial commit
//
//  Revision 1.1  2005/06/23 08:25:02  jbeaver
//  *** empty log message ***
//
//  Revision 1.1  2004/05/14 22:53:53  moreland
//  First version.
//
//  Revision 1.0  2004/05/14 18:33:19  moreland
//  First implementation.
//


package org.rcsb.mbt.model.attributes;


import java.util.Random;

import org.rcsb.mbt.model.Atom;



/**
 *  This class implements the ResidueColor interface by applying a color
 *  to the given Residue by using the secondary structure Fragment type.
 *  <P>
 *  @author John L. Moreland
 *  @see    org.rcsb.mbt.model.attributes.IResidueColor
 *  @see    org.rcsb.mbt.model.Residue
 */
public class AtomColorByRandom
    implements IAtomColor
{
    public static final String NAME = "By Fragment Type";

    // Holds a singleton instance of this class.
    private static AtomColorByRandom singleton = null;
    
    /**
     *  The constructor is PRIVATE so that the "create" method
     *  is used to produce a singleton instance of this class.
     */
    private AtomColorByRandom( ) {
    }


    /**
     *  Return the singleton instance of this class.
     */
    public static AtomColorByRandom create( )
    {
        if ( AtomColorByRandom.singleton == null ) {
			AtomColorByRandom.singleton = new AtomColorByRandom( );
		}

        return AtomColorByRandom.singleton;
    }


    
    private final Random random = new Random();
    /**
     *  Produce a random color.
     */
	public void getAtomColor(final Atom atom, final float[] color) {
		for(int i = 0; i < 3; i++) {
            color[i] = this.random.nextFloat();
        }
	}
}

