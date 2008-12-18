//  $Id: StyleEditor.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: StyleEditor.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
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
//  Revision 1.14  2005/11/08 20:58:15  moreland
//  Switched style code to new StructureStyles API.
//
//  Revision 1.13  2005/01/31 17:02:09  moreland
//  Renamed "enum" variable in preparation for evntual jdk1.5 migration.
//
//  Revision 1.12  2004/07/07 23:49:56  moreland
//  Incorporated improved editor classes code from Dave.
//
//  Revision 1.10  2004/04/09 00:07:37  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.9  2004/01/31 23:55:53  moreland
//  Added images.
//
//  Revision 1.8  2004/01/29 17:16:40  moreland
//  All property lists are now populated automatically from toolkit registries.
//  Copyright and class block comments were updated.
//
//  Revision 1.7  2003/12/22 17:15:43  moreland
//  Corrected BondColorByElement to BondColorByAtomColor for several styles.
//
//  Revision 1.6  2003/12/20 01:11:23  moreland
//  All relevant combo boxes now use "populate" method to construct item lists.
//  Implemented many private "set...Style" methods for macro buttons.
//  Implemented "apply" methods to call new structureStyles utility methods.
//
//  Revision 1.5  2003/12/16 21:44:01  moreland
//  Began populating combo boxes from registries.
//
//  Revision 1.4  2003/12/12 21:12:47  moreland
//  Added hooks to change atom colors on the fly.
//
//  Revision 1.3  2003/11/20 22:30:03  moreland
//  Rewrote GUI with more complete component set and nicer layout.
//
//  Revision 1.2  2003/09/15 20:24:57  moreland
//  Changed interface to use the JTabbedPane class.
//
//  Revision 1.1  2003/09/12 20:49:00  moreland
//  First version.
//
//
//  Revision 1.0  2003/09/02 16:06:13  moreland
//  First version.
//


package org.rcsb.pw.ui.dialogs;




import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.*;
import org.rcsb.uiApp.controllers.app.AppBase;

/**
 *  This class implements a GUI for editing StructureStyle styles
 *  in addition to hooks for editing other related subsidiary styles.
 *  <P>
 *  <CENTER>
 *  <IMG SRC="doc-files/StyleEditor.jpg">
 *  </CENTER>
 *  <P>
 *  @author	John L. Moreland
 *  @see	org.rcsb.mbt.model.attributes.StructureStyles
 */
@SuppressWarnings("serial")
public class StyleEditor
	extends JPanel
{
	private static final String NO_CHANGE = "No Change";
	private static final String SHOW = "Show";
	private static final String HIDE = "Hide";

	private JComboBox atomsColorCombo;
	private JPanel fragmentsPanel;
	private JLabel bondsVisibilityLabel;
	private JPanel residuesFragmentsMacrosPanel;
	private JPanel residuesFragmentsMacrosGridPanel;
	private JLabel atombsBondsMacrosLabel;
	private JButton wireframeBondsButton;
	private JComboBox fragmentsVisibilityCombo;
	private JLabel bondsLabelsLabel;
	private JPanel residuesPanel;
	private JButton residuesFragmentsHideButton;
	private JLabel residuesColorLabel;
	private JLabel fragmentsVisibilityLabel;
	private JPanel atomsBondsPanel;
	private JComboBox bondsLabelsCombo;
	private JButton residuesFragmentsApplyButton;
	private JLabel atomsLabelsLabel;
	private JComboBox bondsRadiusCombo;
	private JPanel atomsBondsMacrosPanel;
	private JPanel atomsBondsMacrosGridPanel;
	private JComboBox residuesColorCombo;
	private JLabel fragmentsStyleLabel;
	private JComboBox atomsLabelsCombo;
	private JLabel atomsColorLabel;
	private JPanel bondsPanel;
	private JLabel atomsRadiusLabel;
	private JLabel bondsColorLabel;
	private JButton atomsBondsApplyButton;
	private JComboBox atomsVisibilityCombo;
	private JButton cpkButton;
	private JTabbedPane stylesTabPanel;
	private JPanel atomsPanel;
	private JButton tubeBackboneButton;
	private JButton wireframeBackboneButton;
	private JButton atomsBondsHideButton;
	private JComboBox bondsVisibilityCombo;
	private JLabel residuesFragmentsMacrosLabel;
	private JLabel atomsVisibilityLabel;
	private JButton editElementStylesButton;
	private JComboBox fragmentsStyleCombo;
	private JButton atomMarkersButton;
	private JButton ballAndStickButton;
	private JComboBox bondsColorCombo;
	private JButton editColorMapButton;
	private JComboBox atomsRadiusCombo;
	private JLabel bondsRadiusLabel;
	private JButton smoothBondsButton;
	private JButton cartoonButton;
	private JPanel residuesFragmentsPanel;

	private ElementStylesEditorDialog elementStylesDialog = null;
	private ColorMapDialog colorMapDialog = null;

	/**
	 *  Construct a new StyleEditor object.
	 */
	public StyleEditor()
	{
		this.initialize( );
	}


	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initialize( )
	{
		//
		// Instantiate the GUI widgets.
		//

		GridBagConstraints gridBagConstraints;

		this.stylesTabPanel = new JTabbedPane();
		this.atomsBondsPanel = new JPanel();
		this.atomsPanel = new JPanel();
		this.atomsVisibilityLabel = new JLabel();
		this.atomsVisibilityCombo = new JComboBox();
		this.atomsColorLabel = new JLabel();
		this.atomsColorCombo = new JComboBox();
		this.atomsRadiusLabel = new JLabel();
		this.atomsRadiusCombo = new JComboBox();
		this.atomsLabelsLabel = new JLabel();
		this.atomsLabelsCombo = new JComboBox();
		this.bondsPanel = new JPanel();
		this.bondsVisibilityLabel = new JLabel();
		this.bondsVisibilityCombo = new JComboBox();
		this.bondsColorLabel = new JLabel();
		this.bondsColorCombo = new JComboBox();
		this.bondsRadiusLabel = new JLabel();
		this.bondsRadiusCombo = new JComboBox();
		this.bondsLabelsLabel = new JLabel();
		this.bondsLabelsCombo = new JComboBox();
		this.atomsBondsMacrosPanel = new JPanel();
		this.atomsBondsMacrosGridPanel = new JPanel();
		this.atombsBondsMacrosLabel = new JLabel();
		this.ballAndStickButton = new JButton();
		this.atomMarkersButton = new JButton();
		this.smoothBondsButton = new JButton();
		this.cpkButton = new JButton();
		this.wireframeBondsButton = new JButton();
		this.atomsBondsHideButton = new JButton();
		this.editElementStylesButton = new JButton();
		this.atomsBondsApplyButton = new JButton();
		this.residuesFragmentsPanel = new JPanel();
		this.residuesPanel = new JPanel();
		this.residuesColorLabel = new JLabel();
		this.residuesColorCombo = new JComboBox();
		this.fragmentsPanel = new JPanel();
		this.fragmentsVisibilityLabel = new JLabel();
		this.fragmentsVisibilityCombo = new JComboBox();
		this.fragmentsStyleLabel = new JLabel();
		this.fragmentsStyleCombo = new JComboBox();
		this.residuesFragmentsMacrosPanel = new JPanel();
		this.residuesFragmentsMacrosGridPanel = new JPanel();
		this.residuesFragmentsMacrosLabel = new JLabel();
		this.wireframeBackboneButton = new JButton();
		this.cartoonButton = new JButton();
		this.tubeBackboneButton = new JButton();
		this.residuesFragmentsHideButton = new JButton();
		this.editColorMapButton = new JButton();
		this.residuesFragmentsApplyButton = new JButton();

		//
		// Lay out the GUI widgets.
		//

		this.setLayout(new java.awt.BorderLayout());


		//
		// Atoms/Bonds tab
		//	Atoms panel
		//	Bonds panel
		//	Macros panel
		//
		this.atomsBondsPanel.setLayout(new java.awt.GridBagLayout());
		this.atomsBondsPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );

		// Atoms panel
		this.atomsPanel.setLayout(new java.awt.GridBagLayout());
		this.atomsPanel.setBorder( new CompoundBorder(
			new TitledBorder("Atoms"),
			new EmptyBorder( 10, 10, 10, 10 ) ) );

		// Visibility
		this.atomsVisibilityLabel.setText("Visibility:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		this.atomsPanel.add(this.atomsVisibilityLabel, gridBagConstraints);

		this.populate( VisibilityRegistry.names(), this.atomsVisibilityCombo );
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets( 0, 5, 0, 0 );
		this.atomsPanel.add(this.atomsVisibilityCombo, gridBagConstraints);

		// Color
		this.atomsColorLabel.setText("Color:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		this.atomsPanel.add(this.atomsColorLabel, gridBagConstraints);

		this.populate( AtomColorRegistry.names(), this.atomsColorCombo );
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets( 0, 5, 0, 0 );
		this.atomsPanel.add(this.atomsColorCombo, gridBagConstraints);

		// Radius
		this.atomsRadiusLabel.setText("Radius:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		this.atomsPanel.add(this.atomsRadiusLabel, gridBagConstraints);

		this.populate( AtomRadiusRegistry.names(), this.atomsRadiusCombo );
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets( 0, 5, 0, 0 );
		this.atomsPanel.add(this.atomsRadiusCombo, gridBagConstraints);

		// Labels
		this.atomsLabelsLabel.setText("Labels:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		this.atomsPanel.add(this.atomsLabelsLabel, gridBagConstraints);

		this.populate( AtomLabelRegistry.names(), this.atomsLabelsCombo );
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets( 0, 5, 0, 0 );
		this.atomsPanel.add(this.atomsLabelsCombo, gridBagConstraints);


		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		this.atomsBondsPanel.add(this.atomsPanel, gridBagConstraints);


		// Bonds panel
		this.bondsPanel.setLayout(new java.awt.GridBagLayout());
		this.bondsPanel.setBorder( new CompoundBorder(
			new TitledBorder("Bonds"),
			new EmptyBorder( 10, 10, 10, 10 ) ) );

		// Visibility
		this.bondsVisibilityLabel.setText("Visibility:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		this.bondsPanel.add(this.bondsVisibilityLabel, gridBagConstraints);

		this.populate( VisibilityRegistry.names(), this.bondsVisibilityCombo );
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets( 0, 5, 0, 0 );
		this.bondsPanel.add(this.bondsVisibilityCombo, gridBagConstraints);

		// Color
		this.bondsColorLabel.setText("Color:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		this.bondsPanel.add(this.bondsColorLabel, gridBagConstraints);

		this.populate( BondColorRegistry.names(), this.bondsColorCombo );
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets( 0, 5, 0, 0 );
		this.bondsPanel.add(this.bondsColorCombo, gridBagConstraints);

		// Radius
		this.bondsRadiusLabel.setText("Radius:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		this.bondsPanel.add(this.bondsRadiusLabel, gridBagConstraints);

		this.populate( BondRadiusRegistry.names(), this.bondsRadiusCombo );
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets( 0, 5, 0, 0 );
		this.bondsPanel.add(this.bondsRadiusCombo, gridBagConstraints);

		// Labels
		this.bondsLabelsLabel.setText("Labels:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		this.bondsPanel.add(this.bondsLabelsLabel, gridBagConstraints);

		this.populate( BondLabelRegistry.names(), this.bondsLabelsCombo );
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets( 0, 5, 0, 0 );
		this.bondsPanel.add(this.bondsLabelsCombo, gridBagConstraints);


		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		this.atomsBondsPanel.add(this.bondsPanel, gridBagConstraints);


		// Macros panel
		this.atomsBondsMacrosPanel.setLayout(new java.awt.GridBagLayout());
		this.atomsBondsMacrosPanel.setBorder( new CompoundBorder(
			new TitledBorder("Macros"),
			new EmptyBorder( 10, 10, 10, 10 ) ) );

		this.atombsBondsMacrosLabel.setText("Auto-configure attributes above:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		this.atomsBondsMacrosPanel.add(this.atombsBondsMacrosLabel, gridBagConstraints);

		// Buttons
		//	Ball-And-Stick		Wireframe Bonds
		//	Smooth Bonds		Atom Markers
		//	CPK Spheres		Hide
		this.atomsBondsMacrosGridPanel.setLayout( new GridLayout( 3, 2, 5, 5 ) );

		// Ball-And-Stick
		this.ballAndStickButton.setText("Ball-And-Stick");
		this.atomsBondsMacrosGridPanel.add(this.ballAndStickButton);
		final ActionListener ballAndStickListener = new ActionListener()
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StyleEditor.this.setBallAndStickStyle( );
			}
		};
		this.ballAndStickButton.addActionListener( ballAndStickListener );

		// Wireframe Bonds
		this.wireframeBondsButton.setText("Wireframe Bonds");
		this.atomsBondsMacrosGridPanel.add(this.wireframeBondsButton);
		final ActionListener wireframeBondsListener = new ActionListener()
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StyleEditor.this.setWireframeBondsStyle( );
			}
		};
		this.wireframeBondsButton.addActionListener( wireframeBondsListener );

		// Smooth Bonds
		this.smoothBondsButton.setText("Smooth Bonds");
		this.atomsBondsMacrosGridPanel.add(this.smoothBondsButton);
		final ActionListener smoothBondsListener = new ActionListener()
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StyleEditor.this.setSmoothBondsStyle( );
			}
		};
		this.smoothBondsButton.addActionListener( smoothBondsListener );

		// Atom Markers
		this.atomMarkersButton.setText("Atom Markers");
		this.atomsBondsMacrosGridPanel.add(this.atomMarkersButton);
		final ActionListener atomMarkersListener = new ActionListener()
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StyleEditor.this.setAtomMarkersStyle( );
			}
		};
		this.atomMarkersButton.addActionListener( atomMarkersListener );

		// CPK Spheres
		this.cpkButton.setText("CPK Spheres");
		this.atomsBondsMacrosGridPanel.add(this.cpkButton);
		final ActionListener cpkListener = new ActionListener()
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StyleEditor.this.setCPKStyle( );
			}
		};
		this.cpkButton.addActionListener( cpkListener );

		// Hide
		this.atomsBondsHideButton.setText("Hide");
		this.atomsBondsMacrosGridPanel.add(this.atomsBondsHideButton);
		final ActionListener atomsBondsHideListener = new ActionListener()
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StyleEditor.this.setAtomsBondsHideStyle( );
			}
		};
		this.atomsBondsHideButton.addActionListener( atomsBondsHideListener );

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;

		gridBagConstraints = new java.awt.GridBagConstraints();
		this.atomsBondsMacrosPanel.add( this.atomsBondsMacrosGridPanel, gridBagConstraints );


		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
		this.atomsBondsPanel.add(this.atomsBondsMacrosPanel, gridBagConstraints);



		this.editElementStylesButton.setText("Element Colors...");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
		this.atomsBondsPanel.add(this.editElementStylesButton, gridBagConstraints);
		final ActionListener editElementStylesListener = new ActionListener()
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StyleEditor.this.showElementStylesEditor( );
			}
		};
		this.editElementStylesButton.addActionListener( editElementStylesListener );

		this.atomsBondsApplyButton.setText("Apply");
		this.atomsBondsApplyButton.setDefaultCapable( true );
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		this.atomsBondsPanel.add(this.atomsBondsApplyButton, gridBagConstraints);
		final ActionListener atomsBondsApplyListener = new ActionListener()
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StyleEditor.this.applyAtomsBondsStyles( );
			}
		};
		this.atomsBondsApplyButton.addActionListener( atomsBondsApplyListener );


		this.stylesTabPanel.addTab("Atoms/Bonds", this.atomsBondsPanel);



		//
		// Residues/Fragments tab
		//	Residues panel
		//	Fragments panel
		//	Macros panel
		//
		this.residuesFragmentsPanel.setLayout(new java.awt.GridBagLayout());
		this.residuesFragmentsPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );

		// Residues panel
		this.residuesPanel.setLayout(new java.awt.GridBagLayout());
		this.residuesPanel.setBorder( new CompoundBorder(
			new TitledBorder("Residues"),
			new EmptyBorder( 10, 10, 10, 10 ) ) );

		// Color
		this.residuesColorLabel.setText("Color:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		this.residuesPanel.add(this.residuesColorLabel, gridBagConstraints);

		this.populate( ResidueColorRegistry.names(), this.residuesColorCombo );
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets( 0, 5, 0, 0 );
		this.residuesPanel.add(this.residuesColorCombo, gridBagConstraints);


		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		this.residuesFragmentsPanel.add(this.residuesPanel, gridBagConstraints);


		// Fragments panel
		this.fragmentsPanel.setLayout(new java.awt.GridBagLayout());
		this.fragmentsPanel.setBorder( new CompoundBorder(
			new TitledBorder("Fragments"),
			new EmptyBorder( 10, 10, 10, 10 ) ) );

		// Visibility
		this.fragmentsVisibilityLabel.setText("Visibility:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		this.fragmentsPanel.add(this.fragmentsVisibilityLabel, gridBagConstraints);

		this.populate( VisibilityRegistry.names(), this.fragmentsVisibilityCombo );
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets( 0, 5, 0, 0 );
		this.fragmentsPanel.add(this.fragmentsVisibilityCombo, gridBagConstraints);

		// Style
		this.fragmentsStyleLabel.setText("Style:");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		this.fragmentsPanel.add(this.fragmentsStyleLabel, gridBagConstraints);

		this.populate( FragmentFormRegistry.names(), this.fragmentsStyleCombo );
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets( 0, 5, 0, 0 );
		this.fragmentsPanel.add(this.fragmentsStyleCombo, gridBagConstraints);


		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets( 10, 0, 0, 0 );
		this.residuesFragmentsPanel.add(this.fragmentsPanel, gridBagConstraints);


		// Macros panel
		this.residuesFragmentsMacrosPanel.setLayout(new java.awt.GridBagLayout());
		this.residuesFragmentsMacrosPanel.setBorder( new CompoundBorder(
			new TitledBorder("Macros"),
			new EmptyBorder( 10, 10, 10, 10 ) ) );

		// Auto-configures label
		this.residuesFragmentsMacrosLabel.setText("Auto-configure attributes above");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		this.residuesFragmentsMacrosPanel.add(this.residuesFragmentsMacrosLabel, gridBagConstraints);


		// Buttons
		//	Wireframe Backbone	Cartoon
		//	Tube Backbone		Hide
		this.residuesFragmentsMacrosGridPanel.setLayout( new GridLayout( 2, 2, 5, 5 ) );

		// Wireframe Backbone
		this.wireframeBackboneButton.setText("Wireframe Backbone");
		this.residuesFragmentsMacrosGridPanel.add(this.wireframeBackboneButton);
		final ActionListener wireframeBackboneListener = new ActionListener()
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StyleEditor.this.setResiduesFragmentsWireframeBackboneStyle( );
			}
		};
		this.wireframeBackboneButton.addActionListener( wireframeBackboneListener );

		// Cartoon
		this.cartoonButton.setText("Cartoon");
		gridBagConstraints = new java.awt.GridBagConstraints();
		this.residuesFragmentsMacrosGridPanel.add(this.cartoonButton);
		final ActionListener cartoonListener = new ActionListener()
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StyleEditor.this.setCartoonStyle( );
			}
		};
		this.cartoonButton.addActionListener( cartoonListener );

		// Tube Backbone
		this.tubeBackboneButton.setText("Tube Backbone");
		this.residuesFragmentsMacrosGridPanel.add(this.tubeBackboneButton);
		final ActionListener tubeBackboneListener = new ActionListener()
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StyleEditor.this.setTubeBackboneStyle( );
			}
		};
		this.tubeBackboneButton.addActionListener( tubeBackboneListener );

		// Hide
		this.residuesFragmentsHideButton.setText("Hide");
		gridBagConstraints = new java.awt.GridBagConstraints();
		this.residuesFragmentsMacrosGridPanel.add(this.residuesFragmentsHideButton);
		final ActionListener residuesFragmentsHideListener = new ActionListener()
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StyleEditor.this.setResiduesFragmentsHideStyle( );
			}
		};
		this.residuesFragmentsHideButton.addActionListener( residuesFragmentsHideListener );


		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;

		gridBagConstraints = new java.awt.GridBagConstraints();
		this.residuesFragmentsMacrosPanel.add( this.residuesFragmentsMacrosGridPanel, gridBagConstraints );

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
		this.residuesFragmentsPanel.add(this.residuesFragmentsMacrosPanel, gridBagConstraints);


		// Dummy expandable empty space panel
		final JPanel residuesFragmentsDummyPanel = new JPanel( );
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		this.residuesFragmentsPanel.add( residuesFragmentsDummyPanel, gridBagConstraints );




		this.editColorMapButton.setText("Color Map...");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		this.residuesFragmentsPanel.add(this.editColorMapButton, gridBagConstraints);
		final ActionListener editColorMapListener = new ActionListener()
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StyleEditor.this.showColorMapEditor( );
			}
		};
		this.editColorMapButton.addActionListener( editColorMapListener );

		this.residuesFragmentsApplyButton.setText("Apply");
		this.residuesFragmentsApplyButton.setDefaultCapable( true );
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		this.residuesFragmentsPanel.add(this.residuesFragmentsApplyButton, gridBagConstraints);
		final ActionListener residuesFragmentsApplyListener = new ActionListener()
		{
			public void actionPerformed( ActionEvent actionEvent )
			{
				StyleEditor.this.applyResiduesFragmentsStyles( );
			}
		};
		this.residuesFragmentsApplyButton.addActionListener( residuesFragmentsApplyListener );

		this.stylesTabPanel.addTab( "Residues/Fragments", this.residuesFragmentsPanel );

		this.add( this.stylesTabPanel, java.awt.BorderLayout.CENTER );
	}


	/**
	 *  Populate the JComboBox with the enumerated String objects.
	 */
	private void populate( final Enumeration strEnum, final JComboBox jComboBox )
	{
		jComboBox.addItem( StyleEditor.NO_CHANGE ); // Always prepend the "NC" item.
		while ( strEnum.hasMoreElements() )
		{
			jComboBox.addItem( strEnum.nextElement( ) );
		}
	}



	/**
	 * Trace our lineage to a frame or dialog ancestor and return
	 * that ancestor.  This is used to find the parent to use for
	 * new dialog boxes.
	 *
	 * @return			parent frame or dialog
	 */
	private Window findParentWindow( )
	{
		// Trace our lineage to a frame or dialog.
		Component parent = this.getParent( );
		while ( parent != null &&
			!(parent instanceof Dialog) &&
			!(parent instanceof Frame) )
		{
			parent = parent.getParent( );
		}

		return (Window)parent;
	}

	/**
	 *  Display the InterpolatedColorMapEditor window.
	 */
	private void showColorMapEditor( )
	{
		if ( this.colorMapDialog == null )
		{
/*
			//
			// Determine which InterpolatedColorMap to edit
			//

			InterpolatedColorMap icm = null;
			int structureCount = structureDocument.getStructureCount( );
			for ( int s=0; s<structureCount; s++ )
			{
				Structure structure = structureDocument.getStructure( s );
				StructureMap structureMap = structure.getStructureMap( );
				StructureStyles structureStyles = structureMap.getStructureStyles( );
				int residueCount = structureMap.getResidueCount( );
				for ( int r=0; r<residueCount; r++ )
				{
					ResidueColor residueColor =
						structureStyles.getResidueColorObject( r );
					if ( residueColor instanceof ResidueColorByResidueIndex )
					{
						icm = (InterpolatedColorMap) ((ResidueColorByResidueIndex)residueColor).getColorMap( );
					}
				}
			}
*/


			final Window parent = this.findParentWindow( );
			if ( parent == null ) {
				this.colorMapDialog = new ColorMapDialog();
			} else if ( parent instanceof Dialog ) {
				this.colorMapDialog = new ColorMapDialog( (Dialog)parent);
			} else {
				this.colorMapDialog = new ColorMapDialog( (Frame)parent);
			}
			this.colorMapDialog.setBackground( this.getBackground( ) );
		}
		this.colorMapDialog.show( );
	}

	/**
	 *  Display the ElementStylesEditor window.
	 */
	private void showElementStylesEditor( )
	{
		// On the first request for the editor, create it.
		if ( this.elementStylesDialog == null )
		{
			// Trace our lineage to a frame or dialog.
			final Window parent = this.findParentWindow( );

			if ( parent == null ) {
				this.elementStylesDialog = new ElementStylesEditorDialog();
			} else if ( parent instanceof Dialog ) {
				this.elementStylesDialog = new ElementStylesEditorDialog(
					(Dialog)parent);
			} else {
				this.elementStylesDialog = new ElementStylesEditorDialog(
					(Frame)parent);
			}
			this.elementStylesDialog.setBackground( this.getBackground( ) );
		}
		this.elementStylesDialog.show( );
	}

	//
	// Atoms/Bonds style macros
	//

	/**
	 *  Configure the Atoms/Bonds tab for ball-and-stick style.
	 */
	private void setBallAndStickStyle( )
	{
		this.atomsVisibilityCombo.setSelectedItem( StyleEditor.SHOW );
		this.atomsColorCombo.setSelectedItem( AtomColorByElement.NAME );
		this.atomsRadiusCombo.setSelectedItem( AtomRadiusByScaledCpk.NAME );
		this.atomsLabelsCombo.setSelectedItem( StyleEditor.NO_CHANGE );

		this.bondsVisibilityCombo.setSelectedItem( StyleEditor.SHOW );
		this.bondsColorCombo.setSelectedItem( BondColorByAtomColor.NAME );
		this.bondsRadiusCombo.setSelectedItem( BondRadiusByScaledAtomRadius.NAME );
		this.bondsLabelsCombo.setSelectedItem( StyleEditor.NO_CHANGE );
	}

	/**
	 *  Configure the Atoms/Bonds tab for smooth bonds style.
	 */
	private void setSmoothBondsStyle( )
	{
		this.atomsVisibilityCombo.setSelectedItem( StyleEditor.SHOW );
		this.atomsColorCombo.setSelectedItem( AtomColorByElement.NAME );
		this.atomsRadiusCombo.setSelectedItem( AtomRadiusByConstant.NAME );
		this.atomsLabelsCombo.setSelectedItem( StyleEditor.NO_CHANGE );

		this.bondsVisibilityCombo.setSelectedItem( StyleEditor.SHOW );
		this.bondsColorCombo.setSelectedItem( BondColorByAtomColor.NAME );
		this.bondsRadiusCombo.setSelectedItem( BondRadiusByAtomRadius.NAME );
		this.bondsLabelsCombo.setSelectedItem( StyleEditor.NO_CHANGE );
	}

	/**
	 *  Configure the Atoms/Bonds tab for CPK style.
	 */
	private void setCPKStyle( )
	{
		this.atomsVisibilityCombo.setSelectedItem( StyleEditor.SHOW );
		this.atomsColorCombo.setSelectedItem( AtomColorByElement.NAME );
		this.atomsRadiusCombo.setSelectedItem( AtomRadiusByCpk.NAME );
		this.atomsLabelsCombo.setSelectedItem( StyleEditor.NO_CHANGE );

		this.bondsVisibilityCombo.setSelectedItem( StyleEditor.HIDE );
		this.bondsColorCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.bondsRadiusCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.bondsLabelsCombo.setSelectedItem( StyleEditor.NO_CHANGE );
	}

	/**
	 *  Configure the Atoms/Bonds tab for wireframe bonds style.
	 */
	private void setWireframeBondsStyle( )
	{
		this.atomsVisibilityCombo.setSelectedItem( StyleEditor.HIDE );
		this.atomsColorCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.atomsRadiusCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.atomsLabelsCombo.setSelectedItem( StyleEditor.NO_CHANGE );

		this.bondsVisibilityCombo.setSelectedItem( StyleEditor.SHOW );
		this.bondsColorCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.bondsRadiusCombo.setSelectedItem( BondRadiusAsWire.NAME );
		this.bondsLabelsCombo.setSelectedItem( StyleEditor.NO_CHANGE );
	}

	/**
	 *  Configure the Atoms/Bonds tab for Atom Markers style.
	 */
	private void setAtomMarkersStyle( )
	{
		this.atomsVisibilityCombo.setSelectedItem( StyleEditor.SHOW );
		this.atomsColorCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.atomsRadiusCombo.setSelectedItem( AtomRadiusByConstant.NAME );
		this.atomsLabelsCombo.setSelectedItem( StyleEditor.NO_CHANGE );

		this.bondsVisibilityCombo.setSelectedItem( StyleEditor.HIDE );
		this.bondsColorCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.bondsRadiusCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.bondsLabelsCombo.setSelectedItem( StyleEditor.NO_CHANGE );
	}

	/**
	 *  Configure the Atoms/Bonds tab for hidden style.
	 */
	private void setAtomsBondsHideStyle( )
	{
		this.atomsVisibilityCombo.setSelectedItem( StyleEditor.HIDE );
		this.atomsColorCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.atomsRadiusCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.atomsLabelsCombo.setSelectedItem( StyleEditor.NO_CHANGE );

		this.bondsVisibilityCombo.setSelectedItem( StyleEditor.HIDE );
		this.bondsColorCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.bondsRadiusCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.bondsLabelsCombo.setSelectedItem( StyleEditor.NO_CHANGE );
	}

	/**
	 *  Configure the Atoms/Bonds tab for no change.
	 */
	private void setAtomsBondsNoChange( )
	{
		this.atomsVisibilityCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.atomsColorCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.atomsRadiusCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.atomsLabelsCombo.setSelectedItem( StyleEditor.NO_CHANGE );

		this.bondsVisibilityCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.bondsColorCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.bondsRadiusCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.bondsLabelsCombo.setSelectedItem( StyleEditor.NO_CHANGE );
	}

	//
	// Residues/Fragments style macros
	//

	/**
	 *  Configure the Residues/Fragments tab for wireframe backbone style.
	 */
	private void setResiduesFragmentsWireframeBackboneStyle( )
	{
		this.residuesColorCombo.setSelectedItem( StyleEditor.NO_CHANGE );

		this.fragmentsVisibilityCombo.setSelectedItem( StyleEditor.SHOW );
		this.fragmentsStyleCombo.setSelectedItem( FragmentFormWireframe.NAME );
	}

	/**
	 *  Configure the Residues/Fragments tab for tube backbone style.
	 */
	private void setTubeBackboneStyle( )
	{
		this.residuesColorCombo.setSelectedItem( StyleEditor.NO_CHANGE );

		this.fragmentsVisibilityCombo.setSelectedItem( StyleEditor.SHOW );
		this.fragmentsStyleCombo.setSelectedItem( FragmentFormTube.NAME );
	}

	/**
	 *  Configure the Residues/Fragments tab for secondary structure cartoon
	 *  style.
	 */
	private void setCartoonStyle( )
	{
		this.residuesColorCombo.setSelectedItem( StyleEditor.NO_CHANGE );

		this.fragmentsVisibilityCombo.setSelectedItem( StyleEditor.SHOW );
		this.fragmentsStyleCombo.setSelectedItem( FragmentFormCartoon.NAME );
	}

	/**
	 *  Configure the Residues/Fragments tab for hidden style.
	 */
	private void setResiduesFragmentsHideStyle( )
	{
		this.residuesColorCombo.setSelectedItem( StyleEditor.NO_CHANGE );

		this.fragmentsVisibilityCombo.setSelectedItem( StyleEditor.HIDE );
		this.fragmentsStyleCombo.setSelectedItem( StyleEditor.NO_CHANGE );
	}

	/**
	 *  Configure the Residues/Fragments tab for no change.
	 */
	private void setResiduesFragmentsNoChange( )
	{
		this.residuesColorCombo.setSelectedItem( StyleEditor.NO_CHANGE );

		this.fragmentsVisibilityCombo.setSelectedItem( StyleEditor.NO_CHANGE );
		this.fragmentsStyleCombo.setSelectedItem( StyleEditor.NO_CHANGE );
	}

	//
	// Apply methods.
	//

	/**
	 *  Apply the Atoms/Bonds tab styles to StructureComponent objects
	 *  that are selected, or if nothing is selected, what is visible.
	 */
	private void applyAtomsBondsStyles( )
	{
		// Get the current configuration.

		final String atomsVisibility = (String) this.atomsVisibilityCombo.getSelectedItem( );
		final String atomsColor = (String) this.atomsColorCombo.getSelectedItem( );
		final String atomsRadius = (String) this.atomsRadiusCombo.getSelectedItem( );
		final String atomsLabels = (String) this.atomsLabelsCombo.getSelectedItem( );

		final String bondsVisibility = (String) this.bondsVisibilityCombo.getSelectedItem( );
		final String bondsColor = (String) this.bondsColorCombo.getSelectedItem( );
		final String bondsRadius = (String) this.bondsRadiusCombo.getSelectedItem( );
		final String bondsLabels = (String) this.bondsLabelsCombo.getSelectedItem( );

		// Walk each structure in the document.

		for ( Structure structure : AppBase.sgetModel().getStructures() )
		{
			final StructureMap structureMap = structure.getStructureMap( );
			final StructureStyles structureStyles = structureMap.getStructureStyles( );

			// Apply changes to either what is selected or what is visible.
			Enumeration scEnum = structureStyles.getSelected( );
			if ( ! scEnum.hasMoreElements() ) {
				scEnum = structureStyles.getVisible( );
			}

			while ( scEnum.hasMoreElements() )
			{
				final StructureComponent sc = (StructureComponent)
					scEnum.nextElement();
				final ComponentType scType = sc.getStructureComponentType( );

				if ( scType == ComponentType.ATOM )
				{
					final Atom atom = (Atom) sc;

					if ( atomsVisibility != StyleEditor.NO_CHANGE )
					{
						if ( atomsVisibility == StyleEditor.SHOW ) {
							structureStyles.setVisible( atom, true );
						} else {
							structureStyles.setVisible( atom, false );
						}
					}

					final AtomStyle atomStyle = (AtomStyle)
						structureStyles.getStyle( atom );

					if ( atomsColor != StyleEditor.NO_CHANGE ) {
						atomStyle.setAtomColor( AtomColorRegistry.get( atomsColor ) );
					}

					if ( atomsRadius != StyleEditor.NO_CHANGE ) {
						atomStyle.setAtomRadius( AtomRadiusRegistry.get( atomsRadius ) );
					}

					if ( atomsLabels != StyleEditor.NO_CHANGE ) {
						atomStyle.setAtomLabel( AtomLabelRegistry.get( atomsLabels ) );
					}
				}

				if ( scType == ComponentType.BOND )
				{
					final Bond bond = (Bond) sc;

					if ( bondsVisibility != StyleEditor.NO_CHANGE )
					{
						if ( bondsVisibility == StyleEditor.SHOW ) {
							structureStyles.setVisible( bond, true );
						} else {
							structureStyles.setVisible( bond, false );
						}
					}

					final BondStyle bondStyle = (BondStyle)
						structureStyles.getStyle( bond );

					if ( bondsColor != StyleEditor.NO_CHANGE ) {
						bondStyle.setBondColor( BondColorRegistry.get( bondsColor ) );
					}
					if ( bondsRadius != StyleEditor.NO_CHANGE ) {
						bondStyle.setBondRadius( BondRadiusRegistry.get( bondsRadius ) );
					}
					if ( bondsLabels != StyleEditor.NO_CHANGE ) {
						bondStyle.setBondLabel( BondLabelRegistry.get( bondsLabels ) );
					}
				}
			}
		}

		// Reset options to "No Change".
		this.setAtomsBondsNoChange( );
	}

	/**
	 *  Apply the Residues/Fragments styles to either what is selected or
	 *  what is visible.
	 */
	private void applyResiduesFragmentsStyles( )
	{
		// Get the current configuration.

		final String residuesColor = (String) this.residuesColorCombo.getSelectedItem( );

		final String fragmentVisibility = (String) this.fragmentsVisibilityCombo.getSelectedItem( );
		final String fragmentForm = (String) this.fragmentsStyleCombo.getSelectedItem( );

		// Walk each structure in the document.

		for (Structure structure : AppBase.sgetModel().getStructures())
		{
			final StructureMap structureMap = structure.getStructureMap( );
			final StructureStyles structureStyles = structureMap.getStructureStyles( );

			// Apply changes to either what is selected or what is visible.
			Enumeration scEnum = structureStyles.getSelected( );
			if ( ! scEnum.hasMoreElements() ) {
				scEnum = structureStyles.getVisible( );
			}

			while ( scEnum.hasMoreElements() )
			{
				final StructureComponent sc = (StructureComponent)
					scEnum.nextElement();
				final ComponentType scType = sc.getStructureComponentType( );

				if ( scType == ComponentType.RESIDUE )
				{
					if ( residuesColor != StyleEditor.NO_CHANGE )
					{
						final Residue residue = (Residue) sc;
						final ResidueStyle residueStyle = (ResidueStyle)
							structureStyles.getStyle( residue );
						residueStyle.setResidueColor( ResidueColorRegistry.get( residuesColor ) );
					}
				}
				else if ( scType == ComponentType.FRAGMENT )
				{
					final Fragment fragment = (Fragment) sc;

					if ( fragmentVisibility != StyleEditor.NO_CHANGE )
					{
						if ( fragmentVisibility == StyleEditor.SHOW ) {
							structureStyles.setVisible( fragment, true );
						} else {
							structureStyles.setVisible( fragment, false );
						}
					}
	
					if 	( fragmentForm != StyleEditor.NO_CHANGE )
					{
						/* FRAGMENT STYLE NOT IMPLEMENTED YET...
						FragmentStyle fragmentStyle = (FragmentStyle)
							structureStyles.getStyle( fragment );
						fragmentStyle.setFragmentForm( FragmentFormRegistry.get( fragmentForm ) );
						*/
					}
				}
			}
		}

		// Reset options to "No Change".
		this.setResiduesFragmentsNoChange( );
	}

	/**
	 * Set the background color for the window.
	 *
	 * @param	background	the new background color
	 */
	
	public void setBackground( final Color background )
	{
		// Set the dialog's background color.
		super.setBackground( background );

		// Set the element style's dialog background color.
		if ( this.elementStylesDialog != null ) {
			this.elementStylesDialog.setBackground( background );
		}

		// Set the color map editor's dialog background color.
		if ( this.colorMapDialog != null ) {
			this.colorMapDialog.setBackground( background );
		}
	}
}

