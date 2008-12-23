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

