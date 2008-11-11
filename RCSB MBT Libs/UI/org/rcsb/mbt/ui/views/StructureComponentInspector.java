//  $Id: StructureComponentInspector.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: StructureComponentInspector.java,v $
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
//  Revision 1.14  2005/11/08 20:58:34  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.13  2004/07/01 19:33:38  moreland
//  Changed labels for MBT-specific items to include the word "MBT".
//
//  Revision 1.12  2004/06/02 18:23:04  moreland
//  Added support for bond order.
//
//  Revision 1.11  2004/05/13 17:04:47  moreland
//  Added StructureInfo display support.
//
//  Revision 1.10  2004/05/04 20:33:14  moreland
//  Added display handler block for TYPE_FRAGMENT.
//
//  Revision 1.9  2004/04/09 00:06:11  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.8  2004/01/29 18:12:52  moreland
//  Updated copyright and class comment block.
//
//  Revision 1.7  2004/01/16 23:06:39  moreland
//  Now displays the element name along with the code for an atom record.
//
//  Revision 1.6  2003/07/14 21:38:44  moreland
//  Added Bond, Residue, and Chain display support.
//
//  Revision 1.5  2003/05/16 23:02:24  moreland
//  Renamed first table header string from "Name" to "Property".
//
//  Revision 1.4  2003/05/15 22:40:12  moreland
//  Set cell spacing, first column width, and overall border sizes.
//
//  Revision 1.2  2003/05/15 17:26:53  moreland
//  Addded GridBagLayout support.
//
//  Revision 1.1  2003/05/14 18:16:06  moreland
//  First revision.
//
//  Revision 1.0  2003/05/14 01:19:41  moreland
//  First version.
//


package org.rcsb.mbt.ui.views;


// MBT

// Core
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import org.rcsb.mbt.controllers.app.AppBase;
import org.rcsb.mbt.controllers.update.IUpdateListener;
import org.rcsb.mbt.controllers.update.UpdateEvent;
import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.*;
import org.rcsb.mbt.model.util.*;


/**
 *  This class impements a StructureComponent inspector styles viewer in which
 *  each field of a StructureComponent object is displayed in a key-value pair table.
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.controllers.update.IUpdateListener
 *  @see	org.rcsb.mbt.model.StructureModel
 *  @see	org.rcsb.mbt.controllers.update.UpdateEvent
 *  @see	org.rcsb.mbt.model.attributes.StructureStylesEvent
 */
public class StructureComponentInspector
	extends JPanel
	implements IUpdateListener, IStructureStylesEventListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6761807003699371212L;
	private StructureModel structureDocument = null;
	private final int itemCount = 20;
	private JTable table = null;
	private JLabel data[][] = null;
	private final String emptyString = "";


	//
	// Constructor
	//


	/**
	 *  Construct a StructureComponentInspector object.
	 */
	public StructureComponentInspector( )
	{
		this.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );

		final String names[] = new String[2];
		names[0] = new String( "Property" );
		names[1] = new String( "Value" );
		this.data = new JLabel[this.itemCount][2];
		for ( int i=0; i<this.itemCount; i++ )
		{
			this.data[i][0] = new JLabel( );
			this.data[i][1] = new JLabel( );
		}

		this.table = new JTable( this.data, names );
		final TableCellRenderer tableCellRenderer = new TableCellRenderer( )
		{
			public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column )
			{
				return (JLabel) value;
			}
		};
		this.table.setDefaultRenderer( this.table.getColumnClass(0), tableCellRenderer );
		this.table.setDefaultRenderer( this.table.getColumnClass(1), tableCellRenderer );
		this.table.setDefaultEditor( this.table.getColumnClass(0), null );
		this.table.setDefaultEditor( this.table.getColumnClass(1), null );

		this.table.setIntercellSpacing( new Dimension( 4, 1 ) );

		final int propColWidth = 160;
		this.table.getColumnModel().getColumn(0).setPreferredWidth( propColWidth );
		this.table.getColumnModel().getColumn(0).setMaxWidth( propColWidth );
		this.table.getColumnModel().getColumn(0).setMinWidth( propColWidth );

		final JScrollPane scrollPane = new JScrollPane( this.table );
		this.setLayout( new BorderLayout() );
		this.add( scrollPane, BorderLayout.CENTER );
	}


	//
	// Viewer methods
	//

	/**
	 * Process in incoming StructureDocumentEvent.
	 */
	public void handleUpdateEvent( final UpdateEvent evt )
	{
		switch (evt.action)
		{
			case VIEW_ADDED:
				viewAdded( evt.view );
				break;
			
			case VIEW_REMOVED:
				viewRemoved( evt.view );
				break;
			
			case STRUCTURE_ADDED:
				structureAdded(evt.structure);
				break;
			
			case STRUCTURE_REMOVED:
				structureRemoved(evt.structure);
				break;
		}
	}

	/**
	 *  A Viewer was just added to a StructureDocument.
	 */
	private void viewAdded( final IUpdateListener viewer )
	{
		// This viewer doesn't care about other viewers.
		if ( viewer != this ) {
			return;
		}

		for (Structure structure : AppBase.sgetModel().getStructures())
			this.structureAdded( structure );
	}

	/**
	 *  A Viewer was just removed from the StructureDocument.
	 */
	private void viewRemoved( final IUpdateListener viewer )
	{
		// This viewer doesn't care about other viewers.
		if ( viewer != this ) {
			return;
		}
			
		for (Structure structure : AppBase.sgetModel().getStructures())
			this.structureRemoved( structure );
	}

	/**
	 *  A Structure was just added to the StructureDocument.
	 */
	private void structureAdded( final Structure structure )
	{
		if (structure != null)
		{
			final StructureMap structureMap = structure.getStructureMap( );
			final StructureStyles structureStyles = structureMap.getStructureStyles( );
			structureStyles.addStructureStylesEventListener( this );
		}
	}

	/**
	 *  A Structure was just removed from the StructureDocument.
	 */
	private void structureRemoved( final Structure structure )
	{
		if (structure != null)
		{
			final StructureMap structureMap = structure.getStructureMap( );
			final StructureStyles structureStyles = structureMap.getStructureStyles( );
			structureStyles.removeStructureStylesEventListener( this );
		}
	}

	/**
	 * Process in incoming StructureStylesEvent.
	 */
	public void processStructureStylesEvent( final StructureStylesEvent sse )
	{
		if ( sse.attribute == StructureStyles.ATTRIBUTE_SELECTION )
		{
			final StructureComponent structureComponent = sse.structureComponent;
			final Structure structure = structureComponent.getStructure( );
			final StructureMap structureMap = structure.getStructureMap( );
			final StructureStyles structureStyles = structureMap.getStructureStyles( );
			final ComponentType structureComponentType = structureComponent.getStructureComponentType( );

			int row = 0;
			JLabel label = null;

			//
			// Show general StructureInfo data.
			//

			label = (JLabel) this.table.getValueAt( row, 0 );
			label.setForeground( Color.blue );
			label.setText( "Structure Info" );
			label = (JLabel) this.table.getValueAt( row, 1 );
			label.setForeground( Color.black );
			label.setText( "" );
			row++;

			final StructureInfo structureInfo = structure.getStructureInfo( );

			if ( structureInfo != null )
			{
				label = (JLabel) this.table.getValueAt( row, 0 );
				label.setText( "IdCode:" );
				label = (JLabel) this.table.getValueAt( row, 1 );
				label.setText( structureInfo.getIdCode() );
				row++;

				label = (JLabel) this.table.getValueAt( row, 0 );
				label.setText( "ShortName:" );
				label = (JLabel) this.table.getValueAt( row, 1 );
				label.setText( structureInfo.getShortName() );
				row++;

				label = (JLabel) this.table.getValueAt( row, 0 );
				label.setText( "LongName:" );
				label = (JLabel) this.table.getValueAt( row, 1 );
				label.setText( structureInfo.getLongName() );
				row++;

				label = (JLabel) this.table.getValueAt( row, 0 );
				label.setText( "ReleaseDate:" );
				label = (JLabel) this.table.getValueAt( row, 1 );
				label.setText( structureInfo.getReleaseDate() );
				row++;

				label = (JLabel) this.table.getValueAt( row, 0 );
				label.setText( "Authors:" );
				label = (JLabel) this.table.getValueAt( row, 1 );
				label.setText( structureInfo.getAuthors() );
				row++;

				label = (JLabel) this.table.getValueAt( row, 0 );
				label.setText( "DeterminationMethod:" );
				label = (JLabel) this.table.getValueAt( row, 1 );
				label.setText( structureInfo.getDeterminationMethod() );
				row++;
			}

			label = (JLabel) this.table.getValueAt( row, 0 );
			label.setText( "URL:" );
			label = (JLabel) this.table.getValueAt( row, 1 );
			label.setText( structure.getUrlString() );
			row++;

			//
			// Show specific StructureComponent data.
			//

			label = (JLabel) this.table.getValueAt( row, 0 );
			label.setText( "Structure Component" );
			label.setForeground( Color.blue );
			label = (JLabel) this.table.getValueAt( row, 1 );
			label.setText( "" );
			label.setForeground( Color.black );
			row++;

			if ( structureComponentType == ComponentType.ATOM )
			{
				final Atom atom = (Atom) structureComponent;
				final int atomIndex = structureMap.getAtomIndex( atom );
				if ( structureStyles.isSelected( atom ) )
				{
					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "MBT Type:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( structureComponentType.toString() );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "MBT Index:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Integer.toString( atomIndex ) );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Number:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Integer.toString( atom.number ) );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Element:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( atom.element + " (" + PeriodicTable.getElementName( atom.element ) + ")" );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Name:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( atom.name );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Compound:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( atom.compound );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Chain:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( atom.chain_id );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Residue:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Integer.toString( atom.residue_id ) );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Coordinate:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( atom.coordinate[0] + ", " + atom.coordinate[1] + ", " + atom.coordinate[2] );
					row++;


					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Occupancy:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Float.toString( atom.occupancy ) );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "B-Factor:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Float.toString( atom.bfactor ) );
					row++;
				}
			}
			else if ( structureComponentType == ComponentType.BOND )
			{
				final Bond bond = (Bond) structureComponent;
				final int bondIndex = structureMap.getBondIndex( bond );
				if ( structureStyles.isSelected( bond ) )
				{
					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "MBT Type:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( structureComponentType.toString() );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "MBT Index:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Integer.toString( bondIndex ) );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Atoms:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					final Atom atom0 = bond.getAtom( 0 );
					final Atom atom1 = bond.getAtom( 1 );
					label.setText( atom0.element + " (" + atom0.number + ") - " +
						atom1.element + " (" + atom1.number + ")" );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Distance:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Double.toString( bond.getDistance() ) );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Order:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Float.toString( bond.getOrder() ) );
					row++;
				}
			}
			else if ( structureComponentType == ComponentType.RESIDUE )
			{
				final Residue residue = (Residue) structureComponent;
				final int residueIndex = structureMap.getResidueIndex( residue );
				if ( structureStyles.isSelected( residue ) )
				{
					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "MBT Type:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( structureComponentType.toString() );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "MBT Index:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Integer.toString( residueIndex ) );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Chain ID:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( residue.getChainId() );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Residue ID:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Integer.toString( residue.getResidueId() ) );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Compound:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					final String compoundCode = residue.getCompoundCode( );
					label.setText( compoundCode + " (" + AminoAcidInfo.getNameFromCode( compoundCode ) + ")" );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Classification:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( residue.getClassification().toString() );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Hydrophobicity:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Float.toString( residue.getHydrophobicity() ) );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Conformation:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( residue.getConformationType().toString() );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Atoms:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Integer.toString( residue.getAtomCount() ) );
					row++;
				}
			}
			else if ( structureComponentType == ComponentType.FRAGMENT )
			{
				final Fragment fragment = (Fragment) structureComponent;
				final int fragmentIndex = structureMap.getFragmentIndex( fragment );
				final Chain chain = fragment.getChain( );
				final int chainIndex = structureMap.getChainIndex( chain );
				if ( structureStyles.isSelected( fragment ) )
				{
					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "MBT Type:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( structureComponentType.toString() );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "MBT Index:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Integer.toString( fragmentIndex ) );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Chain ID:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( chain.getChainId() );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Type:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( fragment.getConformationType().toString() );
					row++;
				}
			}
			else if ( structureComponentType == ComponentType.CHAIN )
			{
				final Chain chain = (Chain) structureComponent;
				final int chainIndex = structureMap.getChainIndex( chain );
				if ( structureStyles.isSelected( chain ) )
				{
					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "MBT Type:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( structureComponentType.toString() );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "MBT Index:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Integer.toString( chainIndex ) );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Chain ID:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( chain.getChainId() );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Classification:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( chain.getClassification().toString() );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Fragments:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Integer.toString( chain.getFragmentCount() ) );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Residues:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					label.setText( Integer.toString( chain.getResidueCount() ) );
					row++;
				}
			}

			// Make sure the rest is empty.
			for ( int i=row; i<this.itemCount; i++ )
			{
				label = (JLabel) this.table.getValueAt( i, 0 );
				label.setText( this.emptyString );
				label = (JLabel) this.table.getValueAt( i, 1 );
				label.setText( this.emptyString );
			}

			this.repaint( );
		}
	}
}

