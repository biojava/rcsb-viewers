package org.rcsb.lx.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.rcsb.lx.controllers.app.LigandExplorer;
import org.rcsb.lx.model.Interaction;
import org.rcsb.lx.model.LXModel;
import org.rcsb.mbt.controllers.scene.PdbToNdbConverter;
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Residue;


public class ContactMap_InformationPane extends JScrollPane
{
	private static final long serialVersionUID = 4664966947615760843L;

	public JTree editor;
	
	// a vector of vectors of vectors (etc.) which forms the tree.
	public final CustomTreeModel model = new CustomTreeModel();
	
	// force the JEditorPane to do text antialiasing.
	public class CustomJTree extends JTree {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5764928355784710677L;

		public CustomJTree(final TreeModel model) {
			super(model);
//			super.setRootVisible(false);
			super.expandRow(super.getRowCount() - 1);	// semi-hack - expand the list of contact types
			
			// another semi-hack: expand all interaction types
			final int rowCount = super.getRowCount();
			for(int i = rowCount - 1; i > 3; i--) {
				super.expandRow(i);
			}
		}
		
//		public void paintComponent(Graphics g) {
//			Graphics2D g2 = (Graphics2D)g;
//			
//			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
////			g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//			
//			super.paintComponent(g);
//		}
	}
	
	public class InteractionTypes {
		public final Vector types = new Vector(); // <= InteractionType
		
		@Override
		public String toString() {
			return "Selected residue's contacts with the selected ligand atom...";
		}
	}
	
	public class InteractionType {
		public final String interactionType;
		public Vector interactingAtoms = null;
		
		public InteractionType(final String interactionType) {
			this.interactionType = interactionType;
		}
		
		@Override
		public String toString() {
			return this.interactionType;
		}
	}
	
	public class CustomTreeModel implements TreeModel {
		public final TreeRoot root = new TreeRoot();
		
		public class TreeRoot {
			public final Vector children = new Vector();
			
			@Override
			public String toString() {
				return "ContactMap Information";
			}
		}
		
		private final Vector listeners = new Vector(1);
		
		public CustomTreeModel() {
			super();
			this.root.children.add("Please click a box on the ContactMap (left).");
		}
		
		public void addTreeModelListener(final TreeModelListener l) {
			this.listeners.add(l);
		}
		
		public Object getChild(final Object parent, final int index) {
			if(parent instanceof InteractionType) {
				return ((InteractionType)parent).interactingAtoms.get(index);
			} else if(parent instanceof InteractionTypes) {
				return ((InteractionTypes)parent).types.get(index);
			} else if(parent instanceof LigandAtom) {
				return ((LigandAtom)parent).children[index];
			} else if(parent instanceof ContactingResidue) {
				return ((ContactingResidue)parent).children[index];
			} else if(parent instanceof ContactingAtom) {
				return ((ContactingAtom)parent).children[index];
			} else if(parent instanceof TreeRoot) {
				return ((TreeRoot)parent).children.get(index);
			} 
			
			return null;
		}

		public int getChildCount(final Object parent) {
			if(parent instanceof InteractionType) {
				return ((InteractionType)parent).interactingAtoms.size();
			} else if(parent instanceof InteractionTypes) {
				return ((InteractionTypes)parent).types.size();
			} else if(parent instanceof LigandAtom) {
				return ((LigandAtom)parent).childCount;
			} else if(parent instanceof ContactingResidue) {
				return ((ContactingResidue)parent).childCount;
			} else if(parent instanceof ContactingAtom) {
				return ((ContactingAtom)parent).children.length;
			} else if(parent instanceof TreeRoot) {
				return ((TreeRoot)parent).children.size();
			} 
			
			return 0;
		}
		
		public int indexOf(final Object child, final Object[] array) {
			for(int i = 0; i < array.length; i++) {
				if(array[i] == child) {
					return i;
				}
			}
			
			return -1;
		}
		
		public int getIndexOfChild(final Object parent, final Object child) {
			if(parent instanceof InteractionType) {
				return ((InteractionType)parent).interactingAtoms.indexOf(child);
			} else if(parent instanceof InteractionTypes) {
				return ((InteractionTypes)parent).types.indexOf(child);
			} else if(parent instanceof LigandAtom) {
				return this.indexOf(child, ((LigandAtom)parent).children);
			} else if(parent instanceof ContactingResidue) {
				return this.indexOf(child, ((ContactingResidue)parent).children);
			} else if(parent instanceof ContactingAtom) {
				return this.indexOf(child, ((ContactingAtom)parent).children);
			} else if(parent instanceof TreeRoot) {
				return ((TreeRoot)parent).children.indexOf(child);
			} 
			
			return -1;
		}

		public Object getRoot() {
			return this.root;
		}

		public boolean isLeaf(final Object node) {
			return this.getChildCount(node) == 0;
		}

		
		public void removeTreeModelListener(final TreeModelListener l) {
			this.listeners.remove(l);
		}

		public void valueForPathChanged(final TreePath path, final Object newValue) {
		}
	}
	
	public ContactMap_InformationPane() {
		super();
		this.setup();
	}
	
	public void setup() {
		this.editor = new CustomJTree(this.model);
		this.editor.setEditable(false);
		super.setViewportView(this.editor);		
	}
	
	public void report(final Residue r) {
		System.out.println(r.getResidueId());
	}
	
	public void report(final Atom a) {
		System.out.println(a.name);
	}
	
//	private class InteractionWrapper {
//		public InteractionWrapper(Interaction ia, boolean isLigandAtomFirstAtom, int number) {
//			this.ia = ia;
//			this.contactingAtom = new ContactingAtom(isLigandAtomFirstAtom ? ia.getSecondAtom() : ia.getFirstAtom(), number);
//		}
//		
//		public String toString() {
//			Atom residueAtom = this.contactingAtom.atom;
//			
//			return residueAtom.name;
//		}
//		
//		public final Interaction ia;
//		public final ContactingAtom contactingAtom;
//	}
	
	private class LigandAtom {
		private final String[] children = new String[6];
		private final String overviewDescription;
		public final int childCount;
		
		public LigandAtom(final Atom atom) {
			final Object[] atomPdbIds = atom.structure.getStructureMap().getPdbToNdbConverter().getPdbIds(atom.chain_id, new Integer(atom.residue_id));
			String atomChainId;
			String atomResId;
			if(atomPdbIds == null) {
				atomChainId = atom.chain_id;
				atomResId = Integer.toString(atom.residue_id);
			} else {
				atomChainId = (String)atomPdbIds[0];
				atomResId = (String)atomPdbIds[1];
			}
			
			int curIndex = 0;
			if(atomChainId != null && atomChainId.length() != 0) {
				this.children[curIndex++] = "Chain " + atomChainId;
			}
			this.children[curIndex++] = "Residue: " + atomResId;
			this.children[curIndex++] = "Compound: " + atom.compound;
			this.children[curIndex++] = "Name: " + atom.name;
			this.children[curIndex++] = "Element: " + atom.element;
			this.children[curIndex++] = "Id: " + atom.number;
			
			this.childCount = curIndex;
			
			final StringBuffer overviewDescription = new StringBuffer();
			overviewDescription.append("Ligand Atom: ");
			if(atomChainId != null && atomChainId.length() != 0) {
				overviewDescription.append(atomChainId);
				overviewDescription.append('/');
			}
			overviewDescription.append(atomResId);
			overviewDescription.append('/');
			overviewDescription.append(atom.name);
			this.overviewDescription = overviewDescription.toString();
		}
		
		public Object getChild(final int index) {
			return this.children[index];
		}
		
		@Override
		public String toString() {
			return this.overviewDescription;
		}
	}
	
	private class ContactingResidue {
		private final String[] children = new String[5];
		private final String overviewDescription;
		public final int childCount;
		
		public ContactingResidue(final Residue res) {
			final Object[] resPdbIds = res.structure.getStructureMap().getPdbToNdbConverter().getPdbIds(res.getChainId(), new Integer(res.getResidueId()));
			String atomChainId;
			String atomResId;
			if(resPdbIds == null) {
				atomChainId = res.getChainId();
				atomResId = Integer.toString(res.getResidueId());
			} else {
				atomChainId = (String)resPdbIds[0];
				atomResId = (String)resPdbIds[1];
			}
			
			String conformation = res.getConformationType();
			final int lastDot = conformation.lastIndexOf('.');
			if(lastDot >= 0) {
				conformation = conformation.substring(lastDot + 1);
			}
			
			int curIndex = 0;
			if(atomChainId != null && atomChainId.length() != 0) {
				this.children[curIndex++] = "Chain: " + atomChainId;
			}
			this.children[curIndex++] = "Residue: " + atomResId;
			this.children[curIndex++] = "Compound: " + res.getCompoundCode();
			this.children[curIndex++] = "Conformation: " + conformation;
			this.children[curIndex++] = "Hydrophobicity: " + res.getHydrophobicity();
			
			this.childCount = curIndex;
			
			final StringBuffer overviewDescription = new StringBuffer();
			overviewDescription.append("Contacting Residue: ");
			if(atomChainId != null && atomChainId.length() != 0) {
				overviewDescription.append(atomChainId);
				overviewDescription.append('/');
			}
			overviewDescription.append(atomResId);
			overviewDescription.append('/');
			overviewDescription.append(res.getCompoundCode());
			this.overviewDescription = overviewDescription.toString();
		}
		
		public Object getChild(final int index) {
			return this.children[index];
		}
		
		@Override
		public String toString() {
			return this.overviewDescription;
		}
	}

	private class ContactingAtom {
//		private int number = -1;
		public final String[] children = new String[5];
		public final Interaction interaction;
		private final String overviewDescription;
		
		public ContactingAtom(final Atom atom, final Interaction interaction)
		{
			this.interaction = interaction;
//			this.atom = atom;
			
			this.children[0] = "Name: " + atom.name;
			this.children[1] = "ID: " + atom.number;
			this.children[2] = "Element: " + atom.element;
			this.children[3] = "BFactor: " + atom.bfactor;
			this.children[4] = "Occupancy: " + atom.occupancy;
			
//			Object[] resPdbIds = atom.structure.getStructureMap().getPdbToNdbConverter().getPdbIds(atom.chain_id, new Integer(atom.residue_id));
//			String atomChainId;
//			String atomResId;
//			if(resPdbIds == null) {
//				atomChainId = atom.chain_id;
//				atomResId = Integer.toString(atom.residue_id);
//			} else {
//				atomChainId = (String)resPdbIds[0];
//				atomResId = (String)resPdbIds[1];
//			}
			
//			StringBuffer overviewDescription = new StringBuffer();
//			overviewDescription.append("Contacting Atom: ");
//			if(atomChainId != null && atomChainId.length() != 0) {
//				overviewDescription.append(atomChainId);
//				overviewDescription.append('/');
//			}
//			overviewDescription.append(atomResId);
//			overviewDescription.append('/');
//			overviewDescription.append(atom.name);
			this.overviewDescription = "Contacting atom: " + atom.name + ", " + interaction.getDistance();
		}
		
		public Object getChild(final int index) {
			return this.children[index];
		}
		
//		public void setNumber(int number) {
//			this.number = number;
//			
//			
//		}
		
		@Override
		public String toString() {
			return this.overviewDescription;
		}
	}
	
	public void reportInteractions(final Residue contactingResidue, final Atom ligandAtom) {
		final LXModel model = LigandExplorer.sgetModel();
		final PdbToNdbConverter conv = contactingResidue.structure.getStructureMap().getPdbToNdbConverter();
		final Vector interactions = model.getInteractionMap().getInteractions(contactingResidue);
		final int interCount = interactions.size();
		
		
		// filter interactions to remove those that don't include the atom, and group the interactions by type. Types are sorted alphabetically.
		final TreeMap interactionsByType = new TreeMap();
		for(int i = 0; i < interCount; i++) {
			final Interaction ia = (Interaction)interactions.get(i);
			
			final Atom firstAtom = ia.getFirstAtom();
			final Atom secondAtom = ia.getSecondAtom();
			
			ContactingAtom contactingAtom = null;
			if(ligandAtom == firstAtom) {
				contactingAtom = new ContactingAtom(secondAtom, ia);
			} else if(ligandAtom == secondAtom) {
				contactingAtom = new ContactingAtom(firstAtom, ia);
			}
			
			if(contactingAtom != null) {
				Vector inters = (Vector)interactionsByType.get(ia.getInteractionType());
				if(inters == null) {
					inters = new Vector();
					interactionsByType.put(ia.getInteractionType(), inters);
				}
//				ca.number = inters.size() + 1;
				inters.add(contactingAtom);
			}
		}
		
//		Object[] atomPdbIds = ligandAtom.structure.getStructureMap().getPdbToNdbConverter().getPdbIds(ligandAtom.chain_id, new Integer(ligandAtom.residue_id));
//		String atomChainId;
//		String atomResId;
//		if(atomPdbIds == null) {
//			atomChainId = ligandAtom.chain_id;
//			atomResId = Integer.toString(ligandAtom.residue_id);
//		} else {
//			atomChainId = (String)atomPdbIds[0];
//			atomResId = (String)atomPdbIds[1];
//		}
//		
//		String resNdbChainId = contactingResidue.getChainId();
//		int resNdbResidueId = contactingResidue.getResidueId();
//		Object[] residuePdbIds = conv.getPdbIds(resNdbChainId, new Integer(resNdbResidueId));
//		String residueChainId;
//		String residueResId;
//		if(atomPdbIds == null) {
//			residueChainId = resNdbChainId;
//			residueResId = Integer.toString(resNdbResidueId);
//		} else {
//			residueChainId = (String)residuePdbIds[0];
//			residueResId = (String)residuePdbIds[1];
//		}
//		
		final Vector treeRootChildren = this.model.root.children;
		treeRootChildren.clear();
		
		// add the general information...
		treeRootChildren.add(new LigandAtom(ligandAtom));
		treeRootChildren.add(new ContactingResidue(contactingResidue));
		
		if(interactionsByType.size() == 0) {
			treeRootChildren.add("No interactions present.");
		} else {
			final InteractionTypes interactionTypes = new InteractionTypes();
			treeRootChildren.add(interactionTypes);
			
			final Iterator it = interactionsByType.values().iterator();
			while(it.hasNext()) {
				final Vector contactingAtoms = (Vector)it.next();
				final int vecSize = contactingAtoms.size();
				
				if(vecSize > 0) {
					final String type = ((ContactingAtom)contactingAtoms.get(0)).interaction.getInteractionType();
					
					final InteractionType typeOb = new InteractionType(type);
					interactionTypes.types.add(typeOb);
					
					typeOb.interactingAtoms = contactingAtoms;
					
					// sort by contact distance
					Collections.sort(contactingAtoms, new Comparator() {
						public int compare(Object o1, Object o2) {
							ContactingAtom a1 = (ContactingAtom)o1;
							ContactingAtom a2 = (ContactingAtom)o2;
							
							double a1Dist = a1.interaction.getDistanceDouble();
							double a2Dist = a2.interaction.getDistanceDouble();
							
							if(a1Dist < a2Dist) {
								return -1;
							}
							if(a1Dist > a2Dist) {
								return 1;
							}
							
							return 0;
						}
					});
				}
			}
		}
		
		// seems a bit severe, but the only easy way I can find to update the tree's model in full is to recreate it.
		this.setup();
	}
}
