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
 * Created on 2008/12/22
 *
 */ 
package org.rcsb.lx.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.glscene.jogl.LXGlGeometryViewer;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.util.Status;

public class LigandSideBar extends JPanel
{
	private final class InteractionListener implements ActionListener {
		private final JCheckBox box;
		private final FloatLimitField otherFLF1;
		private final FloatLimitField philicFLF1;
		private final FloatLimitField l_h2o_pflf1;
		private final JCheckBox hydrophilicBox;
		private final JCheckBox hydrophobicBox;
		private final JCheckBox otherBox;
		private final JCheckBox distanceBox;
		private final FloatLimitField interLigandFLF1;
		private final JCheckBox interLigandBox;
		private final FloatLimitField l_h2o_pflf2;
		private final FloatLimitField philicFLF2;
		private final FloatLimitField phobicFLF2;
		private final FloatLimitField interLigandFLF2;
		private final FloatLimitField phobicFLF1;
		private final FloatLimitField otherFLF2;

		private InteractionListener(JCheckBox box, FloatLimitField otherFLF1,
				FloatLimitField philicFLF1, FloatLimitField l_h2o_pflf1,
				JCheckBox hydrophilicBox, JCheckBox hydrophobicBox,
				JCheckBox otherBox, JCheckBox distanceBox,
				FloatLimitField interLigandFLF1, JCheckBox interLigandBox,
				FloatLimitField l_h2o_pflf2, FloatLimitField philicFLF2,
				FloatLimitField phobicFLF2, FloatLimitField interLigandFLF2,
				FloatLimitField phobicFLF1, FloatLimitField otherFLF2) {
			this.box = box;
			this.otherFLF1 = otherFLF1;
			this.philicFLF1 = philicFLF1;
			this.l_h2o_pflf1 = l_h2o_pflf1;
			this.hydrophilicBox = hydrophilicBox;
			this.hydrophobicBox = hydrophobicBox;
			this.otherBox = otherBox;
			this.distanceBox = distanceBox;
			this.interLigandFLF1 = interLigandFLF1;
			this.interLigandBox = interLigandBox;
			this.l_h2o_pflf2 = l_h2o_pflf2;
			this.philicFLF2 = philicFLF2;
			this.phobicFLF2 = phobicFLF2;
			this.interLigandFLF2 = interLigandFLF2;
			this.phobicFLF1 = phobicFLF1;
			this.otherFLF2 = otherFLF2;
		}

		public void actionPerformed(final ActionEvent ae) {
			final boolean saveInteractionsFlag = LigandExplorer.saveInteractionsFlag;
			LigandExplorer.saveInteractionsFlag = false;

			final StructureModel model = LigandExplorer.sgetModel();
			final LXGlGeometryViewer glViewer = LigandExplorer.sgetGlGeometryViewer();

			// **J check to make sure all text fields have three
			// characters in them; if not, make the correction
			String interLigandText1 = interLigandFLF1.getText();
			switch (interLigandText1.length()) {
			case 0:
				interLigandText1 = "0.0";
				interLigandFLF1.setText(interLigandText1);
				break;
			case 1:
				interLigandText1 = interLigandText1 + ".0";
				interLigandFLF1.setText(interLigandText1);
				break;
			case 2:
				interLigandText1 = interLigandText1 + "0";
				interLigandFLF1.setText(interLigandText1);
			}
			String interLigandText2 = interLigandFLF2.getText();
			switch (interLigandText2.length()) {
			case 0:
				interLigandText2 = "0.0";
				interLigandFLF2.setText(interLigandText2);
				break;
			case 1:
				interLigandText2 = interLigandText2 + ".0";
				interLigandFLF2.setText(interLigandText2);
				break;
			case 2:
				interLigandText2 = interLigandText2 + "0";
				interLigandFLF2.setText(interLigandText2);
			}
			String philicText1 = philicFLF1.getText();
			switch (philicText1.length()) {
			case 0:
				philicText1 = "0.0";
				philicFLF1.setText(philicText1);
				break;
			case 1:
				philicText1 = philicText1 + ".0";
				philicFLF1.setText(philicText1);
				break;
			case 2:
				philicText1 = philicText1 + "0";
				philicFLF1.setText(philicText1);
			}
			String philicText2 = philicFLF2.getText();
			switch (philicText2.length()) {
			case 0:
				philicText2 = "0.0";
				philicFLF2.setText(philicText2);
				break;
			case 1:
				philicText2 = philicText2 + ".0";
				philicFLF2.setText(philicText2);
				break;
			case 2:
				philicText2 = philicText2 + "0";
				philicFLF2.setText(philicText2);
			}
			String phobicText1 = phobicFLF1.getText();
			switch (phobicText1.length()) {
			case 0:
				phobicText1 = "0.0";
				phobicFLF1.setText(phobicText1);
				break;
			case 1:
				phobicText1 = phobicText1 + ".0";
				phobicFLF1.setText(phobicText1);
				break;
			case 2:
				phobicText1 = phobicText1 + "0";
				phobicFLF1.setText(phobicText1);
			}
			String phobicText2 = phobicFLF2.getText();
			switch (phobicText2.length()) {
			case 0:
				phobicText2 = "0.0";
				phobicFLF2.setText(phobicText2);
				break;
			case 1:
				phobicText2 = phobicText2 + ".0";
				phobicFLF2.setText(phobicText2);
				break;
			case 2:
				phobicText2 = phobicText2 + "0";
				phobicFLF2.setText(phobicText2);
			}
			String l_h2o_pText1 = l_h2o_pflf1.getText();
			switch (l_h2o_pText1.length()) {
			case 0:
				l_h2o_pText1 = "0.0";
				l_h2o_pflf1.setText(l_h2o_pText1);
				break;
			case 1:
				l_h2o_pText1 = l_h2o_pText1 + ".0";
				l_h2o_pflf1.setText(l_h2o_pText1);
				break;
			case 2:
				l_h2o_pText1 = l_h2o_pText1 + "0";
				l_h2o_pflf1.setText(l_h2o_pText1);
			}
			String l_h2o_pText2 = l_h2o_pflf2.getText();
			switch (l_h2o_pText2.length()) {
			case 0:
				l_h2o_pText2 = "0.0";
				l_h2o_pflf2.setText(l_h2o_pText2);
				break;
			case 1:
				l_h2o_pText2 = l_h2o_pText2 + ".0";
				l_h2o_pflf2.setText(l_h2o_pText2);
				break;
			case 2:
				l_h2o_pText2 = l_h2o_pText2 + "0";
				l_h2o_pflf2.setText(l_h2o_pText2);
			}
			String otherText1 = otherFLF1.getText();
			switch (otherText1.length()) {
			case 0:
				otherText1 = "0.0";
				otherFLF1.setText(otherText1);
				break;
			case 1:
				otherText1 = otherText1 + ".0";
				otherFLF1.setText(otherText1);
				break;
			case 2:
				otherText1 = otherText1 + "0";
				otherFLF1.setText(otherText1);
			}
			String otherText2 = otherFLF2.getText();
			switch (otherText2.length()) {
			case 0:
				otherText2 = "0.0";
				otherFLF2.setText(otherText2);
				break;
			case 1:
				otherText2 = otherText2 + ".0";
				otherFLF2.setText(otherText2);
				break;
			case 2:
				otherText2 = otherText2 + "0";
				otherFLF2.setText(otherText2);
			}
			
			if (model.hasStructures())
			{
				final Structure structure = model.getStructures().get(0);

				final TreePath treeSelection[] = ligandJList.getSelectionPaths();
				if (treeSelection != null)
				{
					Residue residues[] = null;
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeSelection[0].getLastPathComponent();
					StructureComponent nodeValue = (StructureComponent)node.getUserObject();
					if (nodeValue instanceof Chain)
					{
						assert(treeSelection.length == 1);
						Chain chain = (Chain) nodeValue;
						residues = new Residue[chain.getResidueCount()];
						for (int ix = 0; ix < chain.getResidueCount(); ix++)
							residues[ix] = chain.getResidue(ix);
					}
					
					else 
					{
						int ix = 0;
						residues = new Residue[treeSelection.length];
						
						for (TreePath path : treeSelection)
						{
							node = (DefaultMutableTreeNode)path.getLastPathComponent();
							nodeValue = (StructureComponent)node.getUserObject();
							assert(nodeValue instanceof Residue);
							residues[ix] = (Residue)nodeValue;
						}
						
						LigandExplorer.sgetSceneController().setLigandResidues(residues);
					}
				}

				final float interLigandf1 = Float
				.parseFloat(interLigandText1);
				final float interLigandf2 = Float
				.parseFloat(interLigandText2);
				final float philicf1 = Float
						.parseFloat(philicText1);
				final float philicf2 = Float
						.parseFloat(philicText2);
				final float phobicf1 = Float
						.parseFloat(phobicText1);
				final float phobicf2 = Float
						.parseFloat(phobicText2);
				final float l_h2o_pf1 = Float.parseFloat(l_h2o_pText1);
				final float l_h2o_pf2 = Float.parseFloat(l_h2o_pText2);
				final float otherf1 = Float.parseFloat(otherText1);
				final float otherf2 = Float.parseFloat(otherText2);

				// Thread runner = new Thread() {
				//
				// public void run() {
				try
				{
					LigandExplorer.sgetSceneController().processLeftPanelEvent(structure,
							l_h2o_pf1, l_h2o_pf2,
							box.isSelected(),
							interLigandf1,interLigandf2,
							interLigandBox.isSelected(),
							hydrophilicBox.isSelected(),
							philicf1, philicf2,
							hydrophobicBox.isSelected(),
							phobicf1, phobicf2,
							otherBox.isSelected(), otherf1,
							otherf2,
							distanceBox.isSelected(),
							saveInteractionsFlag);
				}
				
				catch (final Exception e)
				{
					e.printStackTrace();
					Status.progress(100, "Error while processing your options...Please select different options");
				}
				// }
				// };
				// runner.start();

			}
			
			else
			{
				// add a display message here, ask user to load a
				// structure, do this later
			}
		}
	}
	
	private class LigandTreeCellRenderer extends DefaultTreeCellRenderer
	{
		private static final long serialVersionUID = -5623805877904497060L;

		private final ImageIcon residueIcon = new ImageIcon(this.getClass()
				.getResource("residue_16.jpg"));

		private final ImageIcon chainIcon = new ImageIcon(this.getClass()
				.getResource("chain_16.jpg"));
		
		@Override
		public Component getTreeCellRendererComponent(final JTree tree,
				final Object value, final boolean selected,
				final boolean expanded, final boolean leaf, final int row,
				final boolean hasFocus)
		{
			final LigandTreeCellRenderer component = (LigandTreeCellRenderer) super
					.getTreeCellRendererComponent(tree, value, selected,
							expanded, leaf, row, hasFocus);
			
			ImageIcon imageIcon = null;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			
			if (node.getUserObject() instanceof Chain)
				imageIcon = chainIcon;
			
			else if (node.getUserObject() instanceof Residue)
				imageIcon = residueIcon;

			setIcon(imageIcon);
			
			return component;
		}
	}

	private static final long serialVersionUID = 119898371139373941L;
	
	private InteractionListener interactionListener;

	Vector<Chain> ligandList = null;
	public Vector<Chain> getLigandList() { return ligandList; }

	JTree ligandJList = null;

	public JButton applyButton = null;
	public JTree getLigandJList () { return ligandJList; }
	private JScrollPane ligandScroller;

	public LigandSideBar(LXDocumentFrame mainFrame)
	{
		super();

		final StructureModel model = LigandExplorer.sgetModel();

		if (!model.hasStructures())
		{
			this.setBackground(LXDocumentFrame.sidebarColor);
			this.setLayout(new BorderLayout());

			final JPanel pdbPanel = new JPanel();
			pdbPanel.setLayout(new FlowLayout());
			pdbPanel.setBackground(LXDocumentFrame.sidebarColor);

			final JLabel pdbLabel = new JLabel("PDB id");
			pdbLabel.setFont(pdbLabel.getFont().deriveFont(Font.BOLD));
			pdbLabel.setBackground(LXDocumentFrame.sidebarColor);

			mainFrame.getPdbIdList().setBackground(LXDocumentFrame.sidebarColor);

			pdbPanel.add(pdbLabel);
			pdbPanel.add(mainFrame.getPdbIdList());

			this.add(pdbPanel, BorderLayout.NORTH);

		}
		
		else
		{
			this.ligandList = this.getLigandList(model.getStructures().get(0));

			this.setLayout(null);
//			this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

			final JLabel centerView = new JLabel(
					"Step 1: Choose a ligand to analyze...");
			centerView.setFont(centerView.getFont().deriveFont(
					Font.BOLD + Font.ITALIC));
			this.add(centerView);

			// JPanel ligandPanel = new JPanel();
			
			if (this.ligandList.size() > 0)
			{
				DefaultMutableTreeNode root = new DefaultMutableTreeNode("Ligands:");
				ligandJList = new JTree(root);
				ligandJList.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
				ligandJList.setCellRenderer(new LigandTreeCellRenderer());
				ligandJList.setExpandsSelectedPaths(true);
				ligandJList.setRootVisible(false);
				ligandJList.setShowsRootHandles(true);
	
				for (Chain chain : ligandList)
				{
					DefaultMutableTreeNode chainNode = new DefaultMutableTreeNode(chain);
					root.add(chainNode);
					if (chain.getClassification() == Residue.Classification.LIGAND)
						for (Residue residue : chain.getResidues())
							chainNode.add(new DefaultMutableTreeNode(residue));
									// add all the residues.
					
					else if (chain.hasModifiedResidues())
						for (Residue residue : chain.getModifiedResidues())
							chainNode.add(new DefaultMutableTreeNode(residue));
				}
			
				ligandJList.getSelectionModel().addTreeSelectionListener(
					new TreeSelectionListener()
					{
						public void valueChanged(TreeSelectionEvent e)
						{
							applyButton.doClick();
							LigandExplorer.sgetGlGeometryViewer().ligandView(
								LigandExplorer.sgetModel().getStructures().get(0));
						}				
					});
				
				ligandScroller = new JScrollPane(this.ligandJList);

				add(ligandScroller);
			}
			
			else
				centerView.setText("No ligands in this structure");

			final JLabel displayView = new JLabel(
					"Step 2: Choose interactions and thresholds...");
			displayView.setFont(displayView.getFont().deriveFont(
					Font.BOLD + Font.ITALIC));
			this.add(displayView);

			final JCheckBox interLigandBox = new JCheckBox("Inter-ligand");
			interLigandBox.setBackground(LXDocumentFrame.sidebarColor);
			final LegendPanel interLigandPanel = new LegendPanel(Color.RED,
					LXDocumentFrame.sidebarColor);
			final FloatLimitField interLigandFLF1 = new FloatLimitField("0.0");
			final JLabel interLigandDashLabel = new JLabel("-");
			final FloatLimitField interLigandFLF2 = new FloatLimitField("5.0");
			this.add(interLigandBox);
			this.add(interLigandFLF1);
			this.add(interLigandDashLabel);
			this.add(interLigandFLF2);
			this.add(interLigandPanel);

			final JLabel displayIntType = new JLabel(
					"...protein-ligand interactions...");
			displayIntType.setFont(displayIntType.getFont().deriveFont(
					Font.BOLD + Font.ITALIC));
			this.add(displayIntType);

			final JCheckBox hydrophilicBox = new JCheckBox("Hydrophilic");
			final LegendPanel hydrophilicPanel = new LegendPanel(Color.GREEN,
					LXDocumentFrame.sidebarColor);
			final FloatLimitField philicFLF1 = new FloatLimitField("2.7");
			final JLabel philicDashLabel = new JLabel("-");
			final FloatLimitField philicFLF2 = new FloatLimitField("3.3");
			this.add(hydrophilicBox);
			this.add(philicFLF1);
			this.add(philicDashLabel);
			this.add(philicFLF2);
			this.add(hydrophilicPanel);

			final JCheckBox hydrophobicBox = new JCheckBox("Hydrophobic C-C");
			final LegendPanel hydrophobicPanel = new LegendPanel(Color.MAGENTA,
					LXDocumentFrame.sidebarColor);
			final FloatLimitField phobicFLF1 = new FloatLimitField("1.9");
			final JLabel phobicDashLabel = new JLabel("-");
			final FloatLimitField phobicFLF2 = new FloatLimitField("3.9");
			this.add(hydrophobicBox);
			this.add(phobicFLF1);
			this.add(phobicDashLabel);
			this.add(phobicFLF2);
			this.add(hydrophobicPanel);

			final JCheckBox l_h2o_pBox = new JCheckBox("Bridged H-Bond");
			l_h2o_pBox.setBackground(LXDocumentFrame.sidebarColor);
			final LegendPanel l_h2o_pPanel = new LegendPanel(Color.BLUE,
					LXDocumentFrame.sidebarColor);
			l_h2o_pBox.setBackground(LXDocumentFrame.sidebarColor);
			final FloatLimitField l_h2o_pFLF1 = new FloatLimitField("0.0");
			final JLabel l_h2o_pDashLabel = new JLabel("-");
			final FloatLimitField l_h2o_pFLF2 = new FloatLimitField("5.0");
			this.add(l_h2o_pBox);
			this.add(l_h2o_pFLF1);
			this.add(l_h2o_pDashLabel);
			this.add(l_h2o_pFLF2);
			this.add(l_h2o_pPanel);

			final JCheckBox otherBox = new JCheckBox("Hydrophobic-Polar");
			final LegendPanel otherPanel = new LegendPanel(Color.WHITE,
					LXDocumentFrame.sidebarColor);
			final FloatLimitField otherFLF1 = new FloatLimitField("1.9");
			final JLabel otherDashLabel = new JLabel("-");
			final FloatLimitField otherFLF2 = new FloatLimitField("3.9");
			this.add(otherBox);
			this.add(otherFLF1);
			this.add(otherDashLabel);
			this.add(otherFLF2);
			this.add(otherPanel);

			// Adjust parameters of the interactions
//			final JLabel thresholdParam = new JLabel(
//					"Step 3: Adjust thresholds (Angstroms)...");
//			thresholdParam.setFont(thresholdParam.getFont().deriveFont(
//					Font.BOLD + Font.ITALIC));
//			this.add(thresholdParam);


			final JLabel miscParam = new JLabel("Step 3: Miscellaneous...");
			miscParam.setFont(miscParam.getFont().deriveFont(
					Font.BOLD + Font.ITALIC));
			this.add(miscParam);

			final JCheckBox distanceBox = new JCheckBox(
					"Label interactions by distance");
			distanceBox.setBackground(LXDocumentFrame.sidebarColor);
			distanceBox.setSelected(true);
			this.add(distanceBox);

			final JLabel finishParam = new JLabel("Step 4: Finish...");
			finishParam.setFont(finishParam.getFont().deriveFont(
					Font.BOLD + Font.ITALIC));
			this.add(finishParam);

			applyButton = new JButton("Apply");
			final JButton resetButton = new JButton("Reset");
			this.add(applyButton);
			this.add(resetButton);
			
			this.setLayout(
				new LayoutManager()
				{
					public void addLayoutComponent(String arg0, Component arg1) {}
	
					private Dimension layoutSize = null;
					
					public void layoutContainer(Container parent)
					{
						final int visualBuffer = 3;
						Insets parentInsets = parent.getInsets();
						
						if(ligandList == null || ligandList.size() == 0)
						{
							Dimension preferred = centerView.getPreferredSize();
							centerView.setBounds(parentInsets.left + visualBuffer, parentInsets.top + visualBuffer, preferred.width, preferred.height);
						}
						
						else
						{
							Dimension step1Preferred = centerView.getPreferredSize();
							Dimension step2Preferred = displayView.getPreferredSize();
	//						Dimension step3Preferred = thresholdParam.getPreferredSize();
							Dimension step4Preferred = miscParam.getPreferredSize();
							Dimension step5Preferred = finishParam.getPreferredSize();
							Dimension interLigandBoxPreferred = interLigandBox.getPreferredSize();
							Dimension displayIntTypePreferred = displayIntType.getPreferredSize();
							Dimension hydrophilicBoxPreferred = hydrophilicBox.getPreferredSize();
							Dimension hydrophobicBoxPreferred = hydrophobicBox.getPreferredSize();
							Dimension l_h2o_pBoxPreferred = l_h2o_pBox.getPreferredSize();
							Dimension otherBoxPreferred = otherBox.getPreferredSize();
							Dimension distancePreferred = distanceBox.getPreferredSize();
							Dimension applyPreferred = applyButton.getPreferredSize();
							Dimension resetPreferred = resetButton.getPreferredSize();
							Dimension interLigandFLF1Preferred = interLigandFLF1.getPreferredSize();
							Dimension interLigandDashPreferred = interLigandDashLabel.getPreferredSize();
							Dimension interLigandFLF2Preferred = interLigandFLF2.getPreferredSize();
							Dimension hydrophilicFLF1Preferred = philicFLF1.getPreferredSize();
							Dimension hydrophilicDashPreferred = philicDashLabel.getPreferredSize();
							Dimension hydrophilicFLF2Preferred = philicFLF2.getPreferredSize();
							Dimension hydrophobicFLF1Preferred = phobicFLF1.getPreferredSize();
							Dimension hydrophobicDashPreferred = phobicDashLabel.getPreferredSize();
							Dimension hydrophobicFLF2Preferred = phobicFLF2.getPreferredSize();
							Dimension l_h2o_pFLF1Preferred = l_h2o_pFLF1.getPreferredSize();
							Dimension l_h2o_pDashPreferred = l_h2o_pDashLabel.getPreferredSize();
							Dimension l_h2o_pFLF2Preferred = l_h2o_pFLF2.getPreferredSize();
							Dimension otherFLF1Preferred = interLigandFLF1.getPreferredSize();
							Dimension otherDashPreferred = interLigandDashLabel.getPreferredSize();
							Dimension otherFLF2Preferred = interLigandFLF2.getPreferredSize();
	
							int parentHeight = parent.getHeight();
							int parentWidth = parent.getWidth();
							int fullWidth = parentWidth - parentInsets.left - parentInsets.right - visualBuffer * 2;
							
							int listHeight = parentHeight - parentInsets.top - parentInsets.bottom - (step1Preferred.height + step2Preferred.height + step4Preferred.height + step5Preferred.height + interLigandBoxPreferred.height + hydrophilicBoxPreferred.height + 
									hydrophobicBoxPreferred.height + displayIntTypePreferred.height + l_h2o_pBoxPreferred.height + otherBoxPreferred.height + distancePreferred.height + applyPreferred.height + visualBuffer * 13);
							
							int curY = parentInsets.top + visualBuffer;
							int curX = parentInsets.left + visualBuffer;
							int maxWidth = 0;
							
							centerView.setBounds(curX, curY, step1Preferred.width, step1Preferred.height);
							curY += step1Preferred.height + visualBuffer;
							maxWidth = step1Preferred.width;
							
							ligandScroller.setBounds(curX,curY,fullWidth, listHeight);
							curY += listHeight + visualBuffer;
							
							displayView.setBounds(curX, curY, step2Preferred.width, step2Preferred.height);
							curY += step2Preferred.height + visualBuffer;
							maxWidth = Math.max(maxWidth, step2Preferred.width);
							
							int interLigandBoxStartY = curY;
							interLigandBox.setBounds(curX, curY, interLigandBoxPreferred.width, interLigandBoxPreferred.height);
							curY += interLigandBoxPreferred.height + visualBuffer;
							maxWidth = Math.max(maxWidth, interLigandBoxPreferred.width);
							int maxCheckboxWidth = interLigandBoxPreferred.width;
							
							displayIntType.setBounds(curX, curY, displayIntTypePreferred.width, displayIntTypePreferred.height);
							curY += displayIntTypePreferred.height + visualBuffer;
							maxWidth = Math.max(maxWidth, displayIntTypePreferred.width);
							
							int hydrophilicBoxStartY = curY;
							hydrophilicBox.setBounds(curX, curY, hydrophilicBoxPreferred.width, hydrophilicBoxPreferred.height);
							curY += hydrophilicBoxPreferred.height + visualBuffer;
							maxWidth = Math.max(maxWidth, hydrophilicBoxPreferred.width);
							maxCheckboxWidth = Math.max(maxCheckboxWidth, hydrophilicBoxPreferred.width);
							
							int hydrophobicBoxStartY = curY;
							hydrophobicBox.setBounds(curX, curY, hydrophobicBoxPreferred.width, hydrophobicBoxPreferred.height);
							curY += hydrophobicBoxPreferred.height + visualBuffer;
							maxWidth = Math.max(maxWidth, hydrophobicBoxPreferred.width);
							maxCheckboxWidth = Math.max(maxCheckboxWidth, hydrophobicBoxPreferred.width);
							
							int l_h2o_pBoxStartY = curY;
							l_h2o_pBox.setBounds(curX, curY, l_h2o_pBoxPreferred.width, l_h2o_pBoxPreferred.height);
							curY += l_h2o_pBoxPreferred.height + visualBuffer;
							maxWidth = Math.max(maxWidth, l_h2o_pBoxPreferred.width);
							maxCheckboxWidth = Math.max(maxCheckboxWidth, l_h2o_pBoxPreferred.width);
							
							int otherBoxStartY = curY;
							otherBox.setBounds(curX, curY, otherBoxPreferred.width, otherBoxPreferred.height);
							curY += otherBoxPreferred.height + visualBuffer;
							maxWidth = Math.max(maxWidth, otherBoxPreferred.width);
							maxCheckboxWidth = Math.max(maxCheckboxWidth, otherBoxPreferred.width);
							
							// now align the constraint boxes to a grid defined by the check boxes.
							final int legendWidth = 50;
							curX += maxCheckboxWidth + visualBuffer * 2;
							interLigandFLF1.setBounds(curX, interLigandBoxStartY, interLigandFLF1Preferred.width, interLigandFLF1Preferred.height);
							philicFLF1.setBounds(curX, hydrophilicBoxStartY, hydrophilicFLF1Preferred.width, hydrophilicFLF1Preferred.height);
							phobicFLF1.setBounds(curX, hydrophobicBoxStartY, hydrophobicFLF1Preferred.width, hydrophobicFLF1Preferred.height);
							l_h2o_pFLF1.setBounds(curX, l_h2o_pBoxStartY, l_h2o_pFLF1Preferred.width, l_h2o_pFLF1Preferred.height);
							otherFLF1.setBounds(curX, otherBoxStartY, otherFLF1Preferred.width, otherFLF1Preferred.height);
							curX += interLigandFLF1Preferred.width + visualBuffer;
							interLigandDashLabel.setBounds(curX, interLigandBoxStartY, interLigandDashPreferred.width, interLigandDashPreferred.height);
							philicDashLabel.setBounds(curX, hydrophilicBoxStartY, hydrophilicDashPreferred.width, hydrophilicDashPreferred.height);
							phobicDashLabel.setBounds(curX, hydrophobicBoxStartY, hydrophobicDashPreferred.width, hydrophobicDashPreferred.height);
							l_h2o_pDashLabel.setBounds(curX, l_h2o_pBoxStartY, l_h2o_pDashPreferred.width, l_h2o_pDashPreferred.height);
							otherDashLabel.setBounds(curX, otherBoxStartY, otherDashPreferred.width, otherDashPreferred.height);
							curX += interLigandDashPreferred.width + visualBuffer;
							interLigandFLF2.setBounds(curX, interLigandBoxStartY, interLigandFLF2Preferred.width, interLigandFLF2Preferred.height);
							philicFLF2.setBounds(curX, hydrophilicBoxStartY, hydrophilicFLF2Preferred.width, hydrophilicFLF2Preferred.height);
							phobicFLF2.setBounds(curX, hydrophobicBoxStartY, hydrophobicFLF2Preferred.width, hydrophobicFLF2Preferred.height);
							l_h2o_pFLF2.setBounds(curX, l_h2o_pBoxStartY, l_h2o_pFLF2Preferred.width, l_h2o_pFLF2Preferred.height);
							otherFLF2.setBounds(curX, otherBoxStartY, otherFLF2Preferred.width, otherFLF2Preferred.height);
							curX += interLigandFLF2Preferred.width + visualBuffer * 4;
							interLigandPanel.setBounds(curX, interLigandBoxStartY, legendWidth, interLigandFLF2Preferred.height);
							hydrophilicPanel.setBounds(curX, hydrophilicBoxStartY, legendWidth, hydrophilicFLF2Preferred.height);
							hydrophobicPanel.setBounds(curX, hydrophobicBoxStartY, legendWidth, hydrophobicFLF2Preferred.height);
							l_h2o_pPanel.setBounds(curX, l_h2o_pBoxStartY, legendWidth, l_h2o_pFLF2Preferred.height);
							otherPanel.setBounds(curX, otherBoxStartY, legendWidth, otherFLF2Preferred.height);
							curX = parentInsets.left + visualBuffer;							
							
							miscParam.setBounds(curX, curY, step4Preferred.width, step4Preferred.height);
							curY += step4Preferred.height + visualBuffer;
							maxWidth = Math.max(maxWidth, step4Preferred.width);
							
							distanceBox.setBounds(curX, curY, distancePreferred.width, distancePreferred.height);
							curY += distancePreferred.height + visualBuffer;
							maxWidth = Math.max(maxWidth, distancePreferred.width);
							
							finishParam.setBounds(curX, curY, step5Preferred.width, step5Preferred.height);
							curY += step5Preferred.height + visualBuffer;
							maxWidth = Math.max(maxWidth, step5Preferred.width);
							
							applyButton.setBounds(curX, curY, applyPreferred.width, applyPreferred.height);
							curX += applyPreferred.width + visualBuffer;
							
							resetButton.setBounds(curX, curY, resetPreferred.width, resetPreferred.height);
							maxWidth = Math.max(maxWidth, curX + resetPreferred.width);
							
							this.layoutSize.width = maxWidth + parentInsets.left + parentInsets.right + visualBuffer * 2;
						}
					}
	
					public Dimension minimumLayoutSize(Container parent) {
						if(this.layoutSize == null) {
							this.layoutSize = new Dimension();
							this.layoutContainer(parent);
						}
						return this.layoutSize;
					}
	
					public Dimension preferredLayoutSize(Container parent) {
						if(this.layoutSize == null) {
							this.layoutSize = new Dimension();
							this.layoutContainer(parent);
						}
						return this.layoutSize;
					}
	
					public void removeLayoutComponent(Component comp) {}
				});


			resetButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(final ActionEvent ae)
				{
					LXDocumentFrame frame = LigandExplorer.sgetActiveFrame();
					frame.getUpdateController().resetEverything();
					frame.getGlGeometryViewer().requestRepaint();
				}
			});

			// set colors and initial state
			this.setBackground(LXDocumentFrame.sidebarColor);
//			hydrophilicBox.setBackground(JoglViewer.sidebarColor);
			// hydrophilicBox.setSelected(true);

			// atomButton.setSelected(true);

			hydrophilicBox.setBackground(LXDocumentFrame.sidebarColor);
			hydrophilicBox.setSelected(false);
			hydrophobicBox.setBackground(LXDocumentFrame.sidebarColor);
			hydrophobicBox.setSelected(false);
			otherBox.setBackground(LXDocumentFrame.sidebarColor);
			otherBox.setSelected(false);

			applyButton.setBackground(LXDocumentFrame.sidebarColor);
			resetButton.setBackground(LXDocumentFrame.sidebarColor);

			interactionListener = new InteractionListener(l_h2o_pBox, otherFLF1, philicFLF1,
					l_h2o_pFLF1, hydrophilicBox, hydrophobicBox, otherBox,
					distanceBox, interLigandFLF1, interLigandBox,
					l_h2o_pFLF2, philicFLF2, phobicFLF2, interLigandFLF2,
					phobicFLF1, otherFLF2);
	
			interLigandBox.addActionListener(interactionListener);
			applyButton.addActionListener(interactionListener);
			hydrophilicBox.addActionListener(interactionListener);
			hydrophobicBox.addActionListener(interactionListener);
			l_h2o_pBox.addActionListener(interactionListener);
			otherBox.addActionListener(interactionListener);
			distanceBox.addActionListener(interactionListener);
		}
	}

	private Vector<Chain> getLigandList(final Structure structure)
	{
		final Vector<Chain> ligandList = new Vector<Chain>();
		
		for (Chain chain : structure.getStructureMap().getChains())
			if (chain.getClassification() == Residue.Classification.LIGAND ||
			    chain.hasModifiedResidues())
				ligandList.add(chain);

		return ligandList;
	}
	
	/**
	 * This will select the intial ligand in the tree and trigger an update
	 */
	public void selectInitialLigand()
	{
		String initialLigand = LigandExplorer.sgetModel().getInitialLigand();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)ligandJList.getModel().getRoot();
		TreePath paths[] = null;
		
		for (int ix = 0; ix < rootNode.getChildCount(); ix++)
		{
			DefaultMutableTreeNode chainNode = (DefaultMutableTreeNode)rootNode.getChildAt(ix);
			if (initialLigand != null)
			{
				for (int lx = 0; lx < chainNode.getChildCount(); lx++)
				{
					DefaultMutableTreeNode residueNode = (DefaultMutableTreeNode)chainNode.getChildAt(lx);
					Residue residue = (Residue)residueNode.getUserObject();
					if (residue.getCompoundCode().startsWith(initialLigand))
					{
						if (paths == null)
							paths = new TreePath[chainNode.getChildCount()];
						paths[lx] = new TreePath(residueNode.getPath());
					}
				}
			}
		}
		
		TreePath rootPath = new TreePath(rootNode);
		for (int ix = 0; ix < rootNode.getChildCount(); ix++)
			ligandJList.expandPath(rootPath.pathByAddingChild(rootNode.getChildAt(ix)));

		if (paths != null)
			ligandJList.setSelectionPaths(paths);
							// set the discovered intial ligand path(s) as selected
		else
		{
			DefaultMutableTreeNode firstChainNode = (DefaultMutableTreeNode)rootNode.getFirstChild();
			TreePath selectedPath = rootPath.pathByAddingChild(firstChainNode);
			selectedPath = selectedPath.pathByAddingChild(firstChainNode.getFirstChild());
			ligandJList.setSelectionPath(selectedPath);
							// set the first residue in the first chain as selected
							// (jeez, this is a lot of work to do something this simple, I might add...)
		}
	}
}
