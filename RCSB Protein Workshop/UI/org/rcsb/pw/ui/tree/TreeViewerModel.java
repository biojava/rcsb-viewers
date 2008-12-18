//  $Id: TreeViewerModel.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: TreeViewerModel.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
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
//  Revision 1.3  2004/04/09 00:06:14  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.2  2004/01/29 18:19:10  moreland
//  Updated copyright and class block comment.
//
//  Revision 1.1  2003/05/23 00:05:16  moreland
//  Added a custom TreeModel in order to reduce memory usage and increase performance.
//
//  Revision 1.0  2003/05/21 23:38:39  moreland
//  First version
//


package org.rcsb.pw.ui.tree;


// MBT
import java.util.Iterator;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.ExternChain;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureComponentRegistry;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.uiApp.controllers.app.AppBase;

/**
 *  This class impements a custom TreeModel for the TreeViewer class.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.uiApp.controllers.update.IUpdateListener
 *  @see	org.rcsb.uiApp.controllers.update.UpdateEvent
 */
public class TreeViewerModel
	implements TreeModel
{
	//
	// Private variables.
	//

	private final Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>( );
	private StructureModel structureDocument = null;
    
	//
	// Constructors.
	//

	//
	// TreeModel implementation.
	//

	/**
	 * Adds a listener for the TreeModelEvent posted after the tree changes.
	 */
	public void addTreeModelListener( final TreeModelListener l )
	{
		treeModelListeners.addElement( l );
	}

	/**
	 * Returns the child of parent at the given index in the parent's child list.
	 */
	public Object getChild( final Object parent, final int index )
	{
		if ( parent instanceof StructureModel )
			return AppBase.sgetModel().getStructures().get(index);

		else if ( parent instanceof Structure )
		{
			final Structure struc = (Structure)parent;
            return struc.getStructureMap().getPdbTopLevelElements().get(index);
		}
        else if ( parent instanceof ExternChain )
        {
            return ((ExternChain)parent).getResidue(index);
        }
		else if ( parent instanceof StructureComponent )
		{
			final StructureComponent structureComponent = (StructureComponent) parent;
			final ComponentType type = structureComponent.getStructureComponentType( );
			if ( type == ComponentType.CHAIN )
			{
				final Chain chain = (Chain) structureComponent;
                
                if(chain.structure.getStructureMap().isNonproteinChainId(chain.getChainId())) {
                    return chain.getResidue(index);
                }
                
                return chain.getFragment( index );
			}
            else if ( type == ComponentType.FRAGMENT )
            {
                final Fragment fragment = (Fragment) structureComponent;
                return fragment.getResidue( index );
            }
			else if ( type == ComponentType.RESIDUE )
			{
				final Residue residue = (Residue) structureComponent;
				return residue.getAtom( index );
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Returns the number of children of parent.
	 */
	public int getChildCount( final Object parent )
	{
		if ( parent instanceof StructureModel )
			return AppBase.sgetModel().getStructures().size();

		else if ( parent instanceof Structure )
		{
			final Structure struc = (Structure)parent;
			return struc.getStructureMap().getPdbTopLevelElements().size();
		}
        else if ( parent instanceof ExternChain )
        {
            return ((ExternChain)parent).getResidueCount();
        }
		else if ( parent instanceof StructureComponent )
		{
			final StructureComponent structureComponent = (StructureComponent) parent;
			final ComponentType type = structureComponent.getStructureComponentType( );
			if ( type == ComponentType.CHAIN )
			{
                final Chain chain = (Chain) structureComponent;
                
			    if(chain.structure.getStructureMap().isNonproteinChainId(chain.getChainId())) {
                    return chain.getResidueCount();
                }
                
				return chain.getFragmentCount( );
			}
            else if ( type == ComponentType.FRAGMENT )
            {
                final Fragment fragment = (Fragment) structureComponent;
                return fragment.getResidueCount( );
            }
			else if ( type == ComponentType.RESIDUE )
			{
				final Residue residue = (Residue) structureComponent;
				return residue.getAtomCount( );
			} else {
				return 0;
			}
		} else {
			return 0;
		}
	}

	/**
	 * Returns the index of child in parent.
	 */
	public int getIndexOfChild( final Object parent, final Object child )
	{
		if ( parent instanceof StructureModel )
			return AppBase.sgetModel().getStructures().indexOf(child);
		
		else if ( parent instanceof Structure )
		{
			final Structure struc = (Structure)parent;
            final Iterator<StructureComponent> it = struc.getStructureMap().getPdbTopLevelElements().iterator();
            for(int i = 0; it.hasNext(); i++) {
                if(it.next() == child) {
                    return i;
                }
            }
            return -1;
		}

        else if ( parent instanceof ExternChain )
        {
        	ExternChain xc = (ExternChain)parent;
	        for (int i = 0; i < xc.getResidueCount(); i++)
	                if (xc.getResidue(i) == child)
	                    return i;
	        
            return -1;
        }
		
		else if ( parent instanceof StructureComponent )
		{
			final StructureComponent structureComponent = (StructureComponent) parent;
			final ComponentType parentType = structureComponent.getStructureComponentType( );
			if ( parentType == ComponentType.CHAIN )
			{
				final Chain chain = (Chain) structureComponent;
				final int fragment = chain.getFragmentCount( );
				for ( int i=0; i<fragment; i++ )
				{
					if ( chain.getFragment(i) == child ) {
						return i;
					}
				}
				return -1;
			}
            else if ( parentType == ComponentType.FRAGMENT )
            {
                final Fragment fragment = (Fragment) structureComponent;
                final int residueCount = fragment.getResidueCount( );
                for ( int i=0; i<residueCount; i++ )
                {
                    if ( fragment.getResidue(i) == child ) {
						return i;
					}
                }
                return -1;
            }
			else if ( parentType == ComponentType.RESIDUE )
			{
				final Residue residue = (Residue) structureComponent;
				final int atomCount = residue.getAtomCount( );
				for ( int i=0; i<atomCount; i++ )
				{
					if ( residue.getAtom(i) == child ) {
						return i;
					}
				}
				return -1;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	/**
	 * Returns the root of the tree (the StructureDocument).
	 */
	public Object getRoot( )
	{
		return this.structureDocument;
	}

	/**
	 * Sets the root of the tree (the StructureDocument).
	 */
	public void setRoot( final StructureModel root )
	{
		final StructureModel oldRoot = this.structureDocument;
		this.structureDocument = root;

		if ( oldRoot == null ) {
			return;
		}

		final int len = this.treeModelListeners.size( );
		final TreeModelEvent e = new TreeModelEvent( root, new Object[] {oldRoot} );
		for ( int i=0; i<len; i++ )
		{
			((TreeModelListener)this.treeModelListeners.elementAt(i)).treeStructureChanged(e);
		}
	}

	/**
	 * Returns true if the node is a leaf.
	 */
	public boolean isLeaf( final Object node )
	{
		if ( node instanceof Atom ) {
			return true;
		}

        return false;
	}

	/**
	 * Removes a listener previously added with addTreeModelListener.
	 */
	public void removeTreeModelListener( final TreeModelListener l )
	{
		this.treeModelListeners.removeElement( l );
	}

	/**
	 * Messaged when the user has altered the value for the item
     * identified by path to newValue.  Not used by this model.
	 */
	public void valueForPathChanged( final TreePath path, final Object newValue )
	{
		throw new IllegalArgumentException( "TreeViewerModel.valueForPathChanged: Not implemented!" );
	}
}

