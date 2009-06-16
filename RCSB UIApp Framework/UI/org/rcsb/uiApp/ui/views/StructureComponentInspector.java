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
package org.rcsb.uiApp.ui.views;


// MBT

// Core
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;
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
					// PR
//					label.setText( atom.chain_id );
					label.setText( atom.authorChain_id );
					row++;

					label = (JLabel) this.table.getValueAt( row, 0 );
					label.setText( "Residue:" );
					label = (JLabel) this.table.getValueAt( row, 1 );
					// PR
//					label.setText( Integer.toString( atom.residue_id ) );
					label.setText( Integer.toString( atom.authorResidue_id ) );
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

