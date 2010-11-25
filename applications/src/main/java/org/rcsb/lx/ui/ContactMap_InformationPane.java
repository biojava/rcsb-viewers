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
import org.rcsb.mbt.model.Atom;
import org.rcsb.mbt.model.Residue;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;


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
	
	private class LigandAtom {
		private final String[] children = new String[6];
		private final String overviewDescription;
		public final int childCount;
		
		public LigandAtom(final Atom atom) {
			String atomChainId = atom.authorChain_id;
			
			int curIndex = 0;
			if(atomChainId != null && atomChainId.length() != 0) {
				this.children[curIndex++] = "Chain: " + atomChainId;
			}
			this.children[curIndex++] = "Residue: " + atom.authorResidue_id;
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
			overviewDescription.append(atom.authorResidue_id);
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
			String atomChainId = res.getAuthorChainId();
			
			ComponentType conformation = res.getConformationType();
			
			int curIndex = 0;
			if(atomChainId != null && atomChainId.length() != 0) {
				this.children[curIndex++] = "Chain: " + atomChainId;
			}
			this.children[curIndex++] = "Residue: " + res.getAuthorResidueId();
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
			overviewDescription.append(res.getAuthorResidueId());
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
			
			this.children[0] = "Name: " + atom.name;
			this.children[1] = "ID: " + atom.number;
			this.children[2] = "Element: " + atom.element;
			this.children[3] = "BFactor: " + atom.bfactor;
			this.children[4] = "Occupancy: " + atom.occupancy;
			
			this.overviewDescription = "Contacting atom: " + atom.name + ", " + interaction.getDistance();
		}
		
		public Object getChild(final int index) {
			return this.children[index];
		}
		
		@Override
		public String toString() {
			return this.overviewDescription;
		}
	}
	
	public void reportInteractions(final Residue contactingResidue, final Atom ligandAtom) {
		final LXModel model = LigandExplorer.sgetModel();
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
				inters.add(contactingAtom);
			}
		}
		
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
