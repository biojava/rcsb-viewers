//  $Id: TreeViewer.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: TreeViewer.java,v $
//  Revision 1.1  2007/02/08 02:38:52  jbeaver
//  version 1.50
//
//  Revision 1.3  2007/01/03 19:33:49  jbeaver
//  *** empty log message ***
//
//  Revision 1.2  2006/10/04 17:21:06  jbeaver
//  Lots of changes from surfaces to improved picking
//
//  Revision 1.1  2006/09/20 16:50:43  jbeaver
//  first commit - branched from ProteinWorkshop
//
//  Revision 1.2  2006/09/02 18:52:28  jbeaver
//  *** empty log message ***
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
//  Revision 1.19  2004/05/13 17:34:25  moreland
//  Now tries to use StructureInfo data (if available) for Structure label.
//
//  Revision 1.18  2004/05/10 18:02:49  moreland
//  Invisible atoms are now "grayed out" but are still selectable.
//
//  Revision 1.17  2004/04/09 00:06:11  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.16  2004/01/29 18:16:03  moreland
//  Updated copyright and class block comment.
//
//  Revision 1.15  2004/01/15 23:19:37  moreland
//  Added document icon.
//
//  Revision 1.14  2004/01/15 23:02:33  moreland
//  Added Structure icon.
//
//  Revision 1.13  2003/11/24 17:32:38  moreland
//  Removed debug print statements.
//
//  Revision 1.12  2003/11/22 00:14:01  moreland
//  Wrapped some long lines.
//
//  Revision 1.11  2003/07/15 22:11:32  moreland
//  Partial implementation after StructureStyle event updates.
//
//  Revision 1.10  2003/05/23 00:10:00  moreland
//  Rewrote TreeViewer to use TreeViewerModel custom TreeModel in order to reduce
//  memory usage and increase performance.
//
//  Revision 1.9  2003/05/16 23:28:42  moreland
//  Commented out the "scroll the viewable path into view" feature because it was anoying!
//
//  Revision 1.8  2003/05/15 19:49:03  moreland
//  Wrote initial structureRemoved method code.
//
//  Revision 1.7  2003/05/14 16:25:59  moreland
//  Added structureRemoved method support.
//
//  Revision 1.6  2003/05/14 01:16:43  moreland
//  Added StructureStylesEventListener support.
//  Improved and enabled custom cell renderer code.
//  Added preliminary picking support.
//  Added preliminary selection support.
//
//  Revision 1.5  2003/04/23 23:47:00  moreland
//  Updated code to use the new object-based hierarchical StructureMap implementation.
//
//  Revision 1.4  2002/12/16 06:50:36  moreland
//  Began implementing picking/selection handling code.
//  Began implementing cell renderer code to enable custom JTree icons.
//
//  Revision 1.3  2002/11/14 18:49:56  moreland
//  Corrected "see" document reference.
//
//  Revision 1.2  2002/11/14 18:21:31  moreland
//  Began implementation of the basic TreeViewer.
//  Changed to match Viewer change from class to interface.
//  Added new event callback methods to implement Viewer interface.
//  Changed old "MbtController" references to new "StructureDocument" class.
//
//  Revision 1.1.1.1  2002/07/16 18:00:21  moreland
//  Imported sources
//
//  Revision 1.0  2002/06/10 23:38:39  moreland
//

package org.rcsb.pw.ui.tree;

// MBT
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Bond;
import org.rcsb.mbt.model.Chain;
import org.rcsb.mbt.model.Fragment;
import org.rcsb.mbt.model.StructureModel;
import org.rcsb.mbt.model.ExternChain;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.Structure;
import org.rcsb.mbt.model.StructureComponent;
import org.rcsb.mbt.model.StructureMap;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.attributes.StructureStyles;
import org.rcsb.mbt.model.attributes.StructureStylesEvent;
import org.rcsb.mbt.model.attributes.IStructureStylesEventListener;
import org.rcsb.mbt.model.util.PdbToNdbConverter;
import org.rcsb.pw.controllers.app.ProteinWorkshop;
import org.rcsb.uiApp.controllers.app.AppBase;
import org.rcsb.uiApp.controllers.update.IUpdateListener;
import org.rcsb.uiApp.controllers.update.UpdateEvent;
import org.rcsb.vf.controllers.app.VFAppBase;
import org.rcsb.vf.controllers.scene.mutators.MutatorBase;


/**
 * This class impements a pop-out list-based tree viewer.
 * <P>
 * 
 * @author John L. Moreland
 * @see org.rcsb.uiApp.controllers.update.IUpdateListener
 * @see org.rcsb.uiApp.controllers.update.UpdateEvent
 */
public class TreeViewer extends JPanel implements IUpdateListener,
		TreeSelectionListener, IStructureStylesEventListener, MouseListener
{
	private static final long serialVersionUID = -3564103946574642455L;

	public static final int SELECTION_MODEL_NONSELECTOR = 0;

	public static final int SELECTION_MODEL_REGULAR = 1;

	public static final int SELECTION_MODEL_MULTIPLE = 2;

	//
	// Private variables.
	//

	private final TreeSelectionModel nonSelectorSelectionModel = new NonSelectionModel();

	private final TreeSelectionModel defaultSelectionModel = new DefaultTreeSelectionModel();

	private final TreeSelectionModel multipleSelectionModel = new DefaultTreeSelectionModel();

	private final class CustomJTree extends JTree {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5618034603390785737L;

		public CustomJTree(final TreeViewerModel tvm) {
			super(tvm);
		}

		/*
		 * public void addSelectionPath(TreePath path) {
		 * super.addSelectionPath(path);
		 * 
		 * Mutator m = this.model.getMutatorModel().getCurrentMutator(); if(m !=
		 * null) { Object[] pathArray = path.getPath(); for(int i = 0; i <
		 * pathArray.length; i++) { m.toggleMutee(pathArray[i]); } } }
		 * 
		 * public void setSelectionPath(TreePath path) {
		 * super.setSelectionPath(path);
		 * 
		 * Mutator m = this.model.getMutatorModel().getCurrentMutator(); if(m !=
		 * null) { Object[] pathArray = path.getPath(); for(int i = 0; i <
		 * pathArray.length; i++) { m.toggleMutee(pathArray[i]); } } }
		 * 
		 * public void removeSelectionPath(TreePath path) {
		 * super.removeSelectionPath(path);
		 *  }
		 * 
		 * public void setSelectionInterval(int index0, int index1) {
		 * super.setSelectionInterval(index0, index1);
		 *  }
		 */
	}

	private class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6648235647292439163L;

		public CustomTreeCellRenderer() {

			// Set the DefaultTreeCellRenderer's selection color
			final float color[] = new float[3];
			StructureStyles.getSelectionColor(color);
			super.backgroundSelectionColor = // Color.lightGray;
			new Color(color[0], color[1], color[2]);

		}

		private final ImageIcon atomIcon = new ImageIcon(this.getClass()
				.getResource("atom_16.jpg"));

		private final ImageIcon residueIcon = new ImageIcon(this.getClass()
				.getResource("residue_16.jpg"));

		private final ImageIcon chainIcon = new ImageIcon(this.getClass()
				.getResource("chain_16.jpg"));

		private final ImageIcon bondIcon = new ImageIcon(this.getClass()
				.getResource("bonds_16.jpg"));

		private final ImageIcon documentIcon = new ImageIcon(this.getClass()
				.getResource("document_16.jpg"));

		@Override
		public Component getTreeCellRendererComponent(final JTree tree,
				final Object value, final boolean selected,
				final boolean expanded, final boolean leaf, final int row,
				final boolean hasFocus) {
			final CustomTreeCellRenderer component = (CustomTreeCellRenderer) super
					.getTreeCellRendererComponent(tree, value, selected,
							expanded, leaf, row, hasFocus);

			ImageIcon imageIcon = null;
			String componentText = null;

			if (value instanceof StructureModel) {
				componentText = "Document";
				// setToolTipText( "A StructureDocument contains Structures" );
				imageIcon = this.documentIcon;
			} else if (value instanceof Structure) {
				final Structure struc = (Structure) value;
				final StructureMap sm = struc.getStructureMap();

				// Try the url.
				if (componentText == null) {
					componentText = sm.getPdbId();
				}

				// Ensure that its short enough to display nicely.
				if (componentText != null) {
					final int ctLen = componentText.length();
					if (ctLen > 20) {
						// Shorten the url so that the ends are visible.
						componentText = componentText.substring(0, 5) + " ... "
								+ componentText.substring(ctLen - 15, ctLen);
					}
				}
			}
			
			else if (value instanceof ExternChain)
			{
				final ExternChain xc = (ExternChain) value;

				componentText = (xc.isBasicChain())? "Chain " + xc.getChainId() :
							    (xc.isWaterChain())? "Water molecules" :
							    	"Miscellaneous molecules (no pdb chain id)";

				imageIcon = this.chainIcon;
			}
			
			else if (value instanceof StructureComponent) {
				final StructureComponent structureComponent = (StructureComponent) value;
				final StructureMap structureMap = structureComponent.structure
						.getStructureMap();
				final StructureStyles structureStyles = structureMap
						.getStructureStyles();
				final ComponentType type = structureComponent.getStructureComponentType();
				componentText = type.toString(); // Default

				// determine if we should worry about atom mode or backbone
				// mode.
				boolean isAtomMode = false;
				boolean isBackboneMode = false;
				final boolean disableVisibilityCheck = false;

				switch (MutatorBase.getActivationType())
				{
				case ATOMS_AND_BONDS:
					isAtomMode = true;
					break;
				default:
					isBackboneMode = true;
					break;
				}
				// }
				// break;
				// default:
				// // for everything but the VISIBILITY_MUTATOR, we want the
				// residues to always appear as "visible".
				// disableVisibilityCheck = true;
				// }

				if (structureStyles.isSelected(structureComponent)) {
					component.setBackgroundNonSelectionColor(Color.YELLOW);
				} else {
					component.setBackgroundNonSelectionColor(Color.WHITE);
				}

				if (type == ComponentType.ATOM) {
					final Atom atom = (Atom) structureComponent;
					componentText = atom.number + " " + atom.name;
					imageIcon = this.atomIcon;
					// setToolTipText( atom.coordinate[0] + ", " +
					// atom.coordinate[1] + ", " + atom.coordinate[2] );

					// If atom is visible, draw text black else lightGray.
					// StructureMap structureMap =
					// atom.structure.getStructureMap( );
					// StructureStyles structureStyles =
					// structureMap.getStructureStyles( );
					if (disableVisibilityCheck
							|| (isAtomMode && structureStyles.isVisible(atom))
							|| (isBackboneMode && structureStyles
									.isVisible(structureMap.getResidue(atom)
											.getFragment()))) {
						component.setForeground(Color.black);
					} else {
						component.setForeground(Color.lightGray);

					}
				} else if (type == ComponentType.RESIDUE) {
					final Residue residue = (Residue) value;

					// StructureMap structureMap =
					// residue.structure.getStructureMap( );
					// StructureStyles structureStyles =
					// structureMap.getStructureStyles( );

					final PdbToNdbConverter converter = residue.structure
							.getStructureMap().getPdbToNdbConverter();
					final Object[] pdbIds = converter.getPdbIds(residue
							.getChainId(), new Integer(residue.getResidueId()));

					if (pdbIds != null) {
						componentText = pdbIds[1] + " "
								+ residue.getCompoundCode();
					} else {
						componentText = "not found";
					}
					imageIcon = this.residueIcon;

					boolean isVisible = false;
					if (isAtomMode) {
						// denote the residue as visible if even one of its
						// atoms are visible.
						for (Atom a : residue.getAtoms())
							if (structureStyles.isVisible(a))
							{
								isVisible = true;
								break;
							}
					}
					
					else if (isBackboneMode) {
						final Fragment f = residue.getFragment();
						if (f != null && structureStyles.isVisible(f)) {
							isVisible = true;
						}
					}

					if (disableVisibilityCheck || isVisible) {
						component.setForeground(Color.black);
					} else {
						component.setForeground(Color.lightGray);
					}
				} else if (type == ComponentType.FRAGMENT) {
					final Fragment fragment = (Fragment) value;
					ComponentType conformation = fragment.getConformationType();

					final PdbToNdbConverter converter = fragment.structure
							.getStructureMap().getPdbToNdbConverter();
					final Chain c = fragment.getChain();
					final Object[] startIds = converter.getPdbIds(c
							.getChainId(), new Integer(c.getResidue(
							fragment.getStartResidueIndex()).getResidueId()));
					if (startIds != null) {
						final Object[] endIds = converter.getPdbIds(fragment
								.getChain().getChainId(), new Integer(c
								.getResidue(fragment.getEndResidueIndex())
								.getResidueId()));
						componentText = conformation + ": " + startIds[1]
								+ " to " + endIds[1];
						imageIcon = this.chainIcon;
					} else { // many nonpolymers have no pdb ids, and so I
								// can't convert them.
						componentText = "<nonpolymer>";
					}
					// setToolTipText( chain.getClassification() );
				} else if (type == ComponentType.CHAIN) {
					final Chain chain = (Chain) value;
					componentText = chain.structure.getStructureMap()
							.getPdbToNdbConverter().getFirstPdbChainId(
									chain.getChainId());
					imageIcon = this.chainIcon;

					if (componentText == null) {
						componentText = "(no chain id)";
					}
					// setToolTipText( chain.getClassification() );
				} else if (type == ComponentType.BOND) {
					final Bond bond = (Bond) value;
					final Atom atom0 = bond.getAtom(0);
					final Atom atom1 = bond.getAtom(1);
					componentText = atom0.number + " " + atom0.name + " - "
							+ atom1.number + " " + atom1.name;
					imageIcon = this.bondIcon;
					// setToolTipText( chain.getClassification() );
				}
			}

			if (componentText != null) {
				this.setText(componentText);
			}
			if (imageIcon != null) {
				this.setIcon(imageIcon);
			}
			return component;
		}
	}

	/**
	 * The primary JTree objects.
	 */
	public JTree tree = null;

	public TreeViewerModel treeViewerModel = null;

	public JScrollPane scrollPane = null;

	/**
	 * Constructs a new TreeViewer object.
	 */
	public TreeViewer()
	{
		super(null, false);
		super.setBorder(BorderFactory.createCompoundBorder(
								BorderFactory.createTitledBorder("4)  Choose items from the tree or 3d viewer."),
								BorderFactory.createEmptyBorder(-1, 1, 1, 1)));

		// Create a JTree with default models/nodes
		this.treeViewerModel = new TreeViewerModel();
		this.tree = new CustomJTree(this.treeViewerModel);
		// make sure this mouse listener receives events first...
		final MouseListener[] listeners = this.tree.getMouseListeners();
		for (int i = 0; i < listeners.length; i++) {
			this.tree.removeMouseListener(listeners[i]);
		}
		this.tree.addMouseListener(this);
		for (int i = 0; i < listeners.length; i++) {
			this.tree.addMouseListener(listeners[i]);
		}
		// this.tree.setCellRenderer(new DefaultTreeCellRenderer() {

		// });
		// ((DefaultTreeCellRenderer)this.tree.getCellRenderer()).setBackgroundSelectionColor(Color.lightGray);
		// ((DefaultTreeCellRenderer)this.tree.getCellRenderer()).setBackgroundNonSelectionColor(Color.red);

		this.multipleSelectionModel
				.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		ProteinWorkshop.sgetActiveFrame().setTreeViewer(this);

		// Set the basic JTree properties
		this.tree.setRootVisible(false);
		this.tree.setShowsRootHandles(true);
		this.tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		this.tree.putClientProperty("JTree.lineStyle", "Angled");
		this.setSelectionModel(TreeViewer.SELECTION_MODEL_NONSELECTOR);

		// Enable tool tips.
		// ToolTipManager.sharedInstance().registerComponent( tree );
		// ToolTipManager.sharedInstance().setLightWeightPopupEnabled( false );

		// Set a custom JTree cell renderer
		final DefaultTreeCellRenderer treeCellRenderer = new CustomTreeCellRenderer();
		this.tree.setCellRenderer(treeCellRenderer);

		// Set up a tree selection listener
		this.tree.addTreeSelectionListener(this);

		// Add the JTree to a JScrollPane
		this.setLayout(new java.awt.GridLayout(1, 1));
		this.scrollPane = new JScrollPane(this.tree);
		this.add(this.scrollPane);

		// model.getStructure().getStructureMap().getStructureStyles().addStructureStylesEventListener(this);

		AppBase.sgetUpdateController().registerListener(this);
	}

	/**
	 * JTree TreeSelectionListener handler. This gets called when the user
	 * picks/clicks-on a tree node. The TreeSelectionEvent message has a
	 * reference to the StructureComponent object. The actual hilighing does NOT
	 * happen here (see processStructureStylesEvent).
	 */
	public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {
		// System.out.println("changed");
		// ** quick fix to make the NonSelectionModel work.
		if (this.tree.getSelectionModel() instanceof NonSelectionModel
				&& !treeSelectionEvent.isAddedPath()) {
			return;
		}

		// System.err.println( "TreeViewer.processTreeSelectionEvent:
		// treeSelectionEvent=" + treeSelectionEvent );

		final TreePath treePaths[] = treeSelectionEvent.getPaths();
		for (int i = treePaths.length - 1; i >= 0; i--) {
			final Object userObject = treePaths[i].getLastPathComponent();
			if (userObject == null) {
				return;
			}

			final MutatorBase curMut = ProteinWorkshop.sgetSceneController().getMutatorEnum().getCurrentMutator();

			if (userObject instanceof Structure)
			{
				curMut.setConsiderClickModifiers(false);
				curMut.setConsiderSelectedFlag(true);
				curMut.setSelected(treeSelectionEvent.isAddedPath(i));

				if (VFAppBase.sgetSceneController().areSelectionsEnabled()) {
					if (curMut.supportsBatchMode()) {
						curMut.toggleMutee(userObject);
					} else {
						curMut.doMutationSingle(userObject);
					}
				}
			}
			
			else if (userObject instanceof StructureComponent)
			{
				final StructureComponent structureComponent = (StructureComponent) userObject;

				curMut.setConsiderClickModifiers(false);
				curMut.setConsiderSelectedFlag(true);
				curMut.setSelected(treeSelectionEvent.isAddedPath(i));
				curMut.setCtrlDown(this.latestClickEvent.isControlDown());
				curMut.setShiftDown(this.latestClickEvent.isShiftDown());
				if (VFAppBase.sgetSceneController().areSelectionsEnabled()) {
					if (curMut.supportsBatchMode()) {
						curMut.toggleMutee(structureComponent);
					} else {
						curMut.doMutationSingle(structureComponent);
					}
				}
			} else if (userObject instanceof StructureModel) {
				continue; // StructureDocument (root)
			}
		}
	}

	public void setSelectionModel(final int model) {
		switch (model) {
		case SELECTION_MODEL_NONSELECTOR:
			this.tree.setSelectionModel(this.nonSelectorSelectionModel);
			break;
		case SELECTION_MODEL_REGULAR:
			this.tree.setSelectionModel(this.defaultSelectionModel);
			break;
		case SELECTION_MODEL_MULTIPLE:
			this.tree.setSelectionModel(this.multipleSelectionModel);
			break;
		default:
			(new Exception()).printStackTrace();
		}
	}

	//
	// Viewer methods
	// 

	/**
	 * Process a StructureStylesEvent. This method reacts to color and selection
	 * events from the StructureStyles class. The picking does NOT happen here
	 * (see valueChanged).
	 */
	public void processStructureStylesEvent(
			final StructureStylesEvent structureStylesEvent) {
		// System.err.println( "TreeViewer.processStructureStylesEvent: " +
		// structureStylesEvent );

		if (structureStylesEvent.attribute == StructureStyles.ATTRIBUTE_SELECTION) {
			this.tree.repaint();
			this.tree.repaint();
		}
	}

	/**
	 * Process a StructureDocumentEvent message.
	 */
	public void handleUpdateEvent(
			final UpdateEvent evt)
	{
		switch (evt.action)
		{
		case STRUCTURE_ADDED:
			structureAdded(evt.structure);
			break;
			
		case STRUCTURE_REMOVED:
			structureRemoved(evt.structure);
			break;
			
		case VIEW_ADDED:
			viewerAdded(evt.view);
			break;
			
		case VIEW_REMOVED:
			viewerRemoved(evt.view);
			break;
			
		case VIEW_RESET:
			reset();
			break;
		}
	}

	/**
	 * A Viewer was just added to a StructureDocument.
	 */
	private void viewerAdded(final IUpdateListener viewer)
	{
		if (viewer == null)
			return;

		// This viewer doesn't care about other viewers.
		if (viewer != this)
			return;

		this.treeViewerModel.setRoot(AppBase.sgetModel());
		this.tree.expandRow(1);
	}

	/**
	 * A Viewer was just removed from the StructureDocument.
	 */
	private void viewerRemoved(final IUpdateListener viewer)
	{
		if (viewer == null) {
			return;
		}

		// This viewer doesn't care about other viewers.
		if (viewer != this) {
			return;
		}

		this.treeViewerModel.setRoot(null); // Ditch the StructureDocument
											// reference.
	}

	/**
	 * A Structure was just added to the StructureDocument.
	 */
	public void structureAdded(final Structure structure) {
		if (structure == null) {
			return;
		}

		// Force a root-level TreeModelEvent.
		this.treeViewerModel.setRoot((StructureModel) this.treeViewerModel
				.getRoot());

		// Add a StructureStylesEventListener so we recieve updates.
		final StructureMap structureMap = structure.getStructureMap();
		final StructureStyles structureStyles = structureMap
				.getStructureStyles();
		structureStyles.addStructureStylesEventListener(this);
		
		StructureModel model = AppBase.sgetModel();

		// Expand the path for the structure
		TreePath treePath = new TreePath(new Object[] { model, structure });
		this.tree.expandPath(treePath);

		// expand the path for the non-protein atoms, if any are present...
		for (StructureComponent next : structureMap.getPdbTopLevelElements())
			if (next instanceof ExternChain && ((ExternChain)next).isMiscellaneousChain())
			{
				treePath = new TreePath(new Object[] { model, structure, next });
				this.tree.expandPath(treePath);
			}
	}

	/**
	 * A Structure was just removed from the StructureDocument.
	 */
	private void structureRemoved(final Structure structure) {
		if (structure == null) {
			return;
		}

		// Force a root-level TreeModelEvent.
		this.treeViewerModel.setRoot((StructureModel) this.treeViewerModel
				.getRoot());

		// Remove the StructureStylesEventListener so don't recieve updates.
		final StructureMap structureMap = structure.getStructureMap();
		final StructureStyles structureStyles = structureMap
				.getStructureStyles();
		structureStyles.removeStructureStylesEventListener(this);
	}

	public void reset()
	{
		this.tree.clearSelection();

		// collapse everything...
		try {
			for (int i = this.tree.getRowCount() - 1; i >= 0; i--) {
				this.tree.collapseRow(i);
			}

			this.tree.expandPath(new TreePath(
					new Object[] { AppBase.sgetModel() }));
		} catch (final Exception e) {
			// usually not necessary to debug here (?)
			System.err.println("Warning: exception in TreeViewer.reset()");
		}

		this.setSelectionModel(TreeViewer.SELECTION_MODEL_NONSELECTOR);
	}

	public JTree getTree() {
		return this.tree;
	}

	private MouseEvent latestClickEvent = null;

	public void mouseClicked(final MouseEvent e) {
	}

	public void mouseEntered(final MouseEvent e) {
	}

	public void mouseExited(final MouseEvent e) {
	}

	public void mousePressed(final MouseEvent e) {
		this.latestClickEvent = e;
	}

	public void mouseReleased(final MouseEvent e) {
	}
}

class NonSelectionModel extends DefaultTreeSelectionModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7668948115407156550L;

	private final NonSelectionIndicatorThread nonSelector = new NonSelectionIndicatorThread(
			this);

	public NonSelectionModel() {
		super.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}

	@Override
	public void setSelectionPath(final TreePath path) {
		super.setSelectionPath(path);
		this.nonSelector.setLivePath(path);
	}
}

class NonSelectionIndicatorThread extends Thread {
	private TreeSelectionModel selectionModel;

	private TreePath livePath;

	private static final int TIMER_LENGTH = 500;

	private static final int FOREVER = 1000000000;

	public NonSelectionIndicatorThread(final TreeSelectionModel selectionModel) {
		this.selectionModel = selectionModel;
		super.start();
	}

	public void setLivePath(final TreePath livePath) {
		this.livePath = livePath;
		this.reset();
	}

	public void reset() {
		this.interrupt();
	}

	@Override
	public void run() {
		while (true) {
			try {
				super.join(NonSelectionIndicatorThread.TIMER_LENGTH);
			} catch (final InterruptedException e) {
				continue;
			}

			if (this.livePath != null) {
				this.selectionModel.removeSelectionPath(this.livePath);
			}
			this.livePath = null;

			try {
				super.join(NonSelectionIndicatorThread.FOREVER);
			} catch (final InterruptedException e) {

			}
		}
	}
}
