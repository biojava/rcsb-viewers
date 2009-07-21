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
package org.rcsb.mbt.model.attributes;

// MBT
// Core
import java.util.*;

import org.rcsb.mbt.model.*;
import org.rcsb.mbt.model.StructureComponentRegistry.ComponentType;
import org.rcsb.mbt.model.util.*;

/**
 * This class stores and retrieves styles associated with visible
 * representations of objects provided by a StructureMap. A StructureStyles
 * object is obtained by calling the getStructureStyles method in a StructureMap
 * instance (and a StructureMap is obtained by calling the getStructureMap
 * method in a Structure instance).
 * <P>
 * The StructureStyles class only represents the STYLES that should be used to
 * draw visible representations of objects. It does not specify WHAT should be
 * drawn or HOW it should be drawn (these choices are left up to each Viewer).
 * Also, while the set of style attributes maintained by this and subsidiary
 * classes can provide style information for all objects that COULD be displayed
 * by a Viewer, it does not mean that a given view MUST display a representation
 * of all objects. That is, this infrastructure simply provides viewable
 * attributes in case a Viewer chooses to create a visible representation of
 * certain data. The use of common styles accross multiple Viewers enables users
 * to compare representations and thus correlate features between those views.
 * <P>
 * <center> <img src="doc-files/StructureStyles.jpg" border=0></a> </center>
 * <P>
 * Replace the Style used by one StructureComponent object:<BR>
 * 
 * <PRE>
 * 
 * AtomStyle atomStyle = (AtomStyle) structureStyles.replaceStyle( atom );
 * atomStyle.setAtomColor( new AtomColorByRgb( 1.0f, 0.0f, 0.0f ) );
 * 
 * </PRE>
 * 
 * <P>
 * Replace the Style used by one or more StructureComponent objects:<BR>
 * 
 * <PRE>
 * 
 * AtomStyle atomStyle = (AtomStyle) structureStyles.replaceStyle( atom1 );
 * atomStyle.setAtomColor( new AtomColorByRgb( 1.0f, 0.0f, 0.0f ) );
 * structureStyles.setStyle( atom2, atomStyle ); structureStyles.setStyle(
 * atom3, atomStyle ); ...
 * 
 * </PRE>
 * 
 * <P>
 * Edit the Style shared by one or more StructureComponent objects:<BR>
 * 
 * <PRE>
 * 
 * AtomStyle atomStyle = (AtomStyle) structureStyles.getStyle( atom );
 * atomStyle.setAtomColor( new AtomColorByRgb( 1.0f, 0.0f, 0.0f ) );
 * 
 * </PRE>
 * 
 * <P>
 * <center> <img src="doc-files/SelectionModel.jpg" border=0></a> </center>
 * <P>
 * 
 * @author John L. Moreland
 * @see org.rcsb.mbt.model.StructureModel
 * @see org.rcsb.mbt.model.StructureMap
 * @see org.rcsb.mbt.model.Structure
 */
public class StructureStyles 
// implements StructureStylesEventListener
{
	// Changed attribute flags
	public static final int ATTRIBUTE_STYLE = 1;

	public static final int ATTRIBUTE_VISIBILITY = 2;

	public static final int ATTRIBUTE_SELECTION = 3;

	// Internal flags for special-case visibility and selection states.
	private static final int FLAG_NONE = 0;

	private static final int FLAG_SOME = 1;

	private static final int FLAG_ALL = 2;

	// Holds visibility state (Boolean) for a structure component
	private final Hashtable<StructureComponent, Boolean> visibility =
		new Hashtable<StructureComponent, Boolean>();
				// should be a more efficient way of doing this...
	
	private int visibilityFlag = StructureStyles.FLAG_NONE;

	/**
	 * Data structure: HashMap selection { key: StructureComponent component
	 * value: Integer childCount }
	 * 
	 * Represents a minimal tree, mirroring the StructureMap tree. All leaves
	 * are selected.
	 */
	private final HashMap<StructureComponent, Object> selection = new HashMap<StructureComponent, Object>();

	// Holds a default style object for a given StructureComponent type.
	private final Hashtable<ComponentType, Object> defaultStyle = new Hashtable<ComponentType, Object>();

	// Holds Style objects.
	private final Hashtable<StructureComponent, Style> styles = new Hashtable<StructureComponent, Style>();

	// Reference to our parent object.
	private StructureMap structureMap = null;

	// Event listeners.
	private final ArrayList<IStructureStylesEventListener> listeners = new ArrayList<IStructureStylesEventListener>();

	/**
	 * The color used to hilight selected objects. Notice that this value is
	 * static so that the same color is applied toolkit-wide. Default color =
	 * yellow.
	 */
	private static final float selectionColor[] = { 1.0f, 1.0f, 0.0f };

	private static String myPackagePrefix = StructureStyles.class.getName().replaceFirst(StructureStyles.class.getSimpleName(), "");

	//
	// Constructors
	//

	/**
	 * Primary constructor.
	 */
	public StructureStyles(final StructureMap structureMap) {
		if (structureMap == null) {
			throw new NullPointerException("null StructureMap");
		}
		this.structureMap = structureMap;

		// Create default styles (if available) for registered SC types.
		String styleClassName = null;
		for (ComponentType ctype : ComponentType.values())
		{
			try {
				// Build the expected Style class name for the given SC.
				String scTypeName = ctype.toString().charAt(0) + ctype.toString().substring(1).toLowerCase();
				styleClassName = myPackagePrefix + scTypeName + "Style";

				// Try to load the expected Style class.
				final ClassLoader classLoader = Style.class.getClassLoader();
				final Class styleClass = classLoader.loadClass(styleClassName);
				final Object style = styleClass.newInstance();
				// Set the default style
				this.defaultStyle.put(ctype, style);
			} catch (final ClassNotFoundException cnfe) {
				// We couldn't find a corresponding Style class.
				// Status.output( Status.LEVEL_REMARK, styleClassName + " not
				// found." );
			} catch (final Exception e) {
				// Something else went wrong.
				Status.output(Status.LEVEL_ERROR, "StructureStyles: "
						+ e.toString());
			}
		}

		// TODO pr new: don't show DNA/RNA atoms anymore, we show ribbon instead
		// ATOMS (show ligand and DNA/RNA atoms, but not water)
		final int residueCount = structureMap.getResidueCount();
		for (int r = 0; r < residueCount; r++) {
			final Residue residue = structureMap.getResidue(r);
			if (residue.getClassification() == Residue.Classification.AMINO_ACID ||
					residue.getClassification()== Residue.Classification.NUCLEIC_ACID ||
				residue.getClassification() == Residue.Classification.WATER	)
				continue;

			final int atomCount = residue.getAtomCount();
			String lastAtomName = "x_X_x";
			for (int a = 0; a < atomCount; a++) {
				final Atom atom = residue.getAtom(a);
				// Handle multiple atom occupancy
				if (!atom.name.equals(lastAtomName)) {
					this.setVisible(atom, true);
				}
				lastAtomName = atom.name;
			}
		}

		// BONDS (show bonds with two visible atoms)
		final int bondCount = structureMap.getBondCount();
		for (int b = 0; b < bondCount; b++) {
			final Bond bond = structureMap.getBond(b);
			if (this.isVisible(bond.getAtom(0)) && this.isVisible(bond.getAtom(1))) {
				this.setVisible(bond, true);
			}
		}

		// CHAINS (show protein and DNA chains)
		final int chainCount = structureMap.getChainCount();
		for (int c = 0; c < chainCount; c++) {
			final Chain chain = structureMap.getChain(c);
			if (chain.getClassification() == Residue.Classification.LIGAND) {
				continue;
			}

			this.setVisible(chain, true);
		}
	}

	//
	// Utility Methods
	//

	/**
	 * Return a reference to our StructureMap object.
	 */
	public StructureMap getStructureMap() {
		return this.structureMap;
	}

	/**
	 * Return a reference to our StructureMap object.
	 */
	public static void getSelectionColor(final float[] color) {
		if (color == null) {
			throw new NullPointerException("null color");
		}

		color[0] = StructureStyles.selectionColor[0];
		color[1] = StructureStyles.selectionColor[1];
		color[2] = StructureStyles.selectionColor[2];
	}

	//
	// Style methods
	//

	/**
	 * Return the Style associated with the specified StructureComponent. If a
	 * custom style is not set, the default style will be returned.
	 * <P>
	 * 
	 * @param
	 * @return
	 * @throws
	 */
	public Style getStyle(final StructureComponent structureComponent) {
		// First try to get the object-specific style.
		Style style = (Style) this.styles.get(structureComponent);

		// Otherwise try to get the default style for the type.
		if (style == null) {
			style = (Style) this.defaultStyle.get(structureComponent
					.getStructureComponentType());
		// The style still might be null.
		}

		return style;
	}

	/**
	 * Set the style for the given structure component and then fire an event to
	 * all listeners.
	 * <P>
	 * 
	 * @param
	 * @return
	 * @throws
	 */
	public void setStyle(final StructureComponent structureComponent, final Style style) {
		if (structureComponent == null) {
			throw new NullPointerException("null structureComponent");
		}

		final StructureStylesEvent structureStylesEvent = new StructureStylesEvent();

		if (style != null) {
			if (style
					.isTypeSafe(structureComponent.getStructureComponentType())) {
				structureStylesEvent.structureComponent = structureComponent;
				structureStylesEvent.style = style;
				structureStylesEvent.attribute = StructureStyles.ATTRIBUTE_STYLE;
				structureStylesEvent.property = null;
				this.styles.put(structureComponent, style);
				// style.addStructureStylesEventListener( this ); JLM DEBUG
				// JLM DEBUG: We need to loop through the styles
				// hash looking for all structurecomponents of this type
				// that happen to share this object and then send an update to
				// them as well...
			} else {
				throw new IllegalArgumentException("style type mismatch");
			}
		} else {
			structureStylesEvent.structureComponent = structureComponent;
			structureStylesEvent.style = (Style) this.styles.get(structureComponent);
			structureStylesEvent.attribute = StructureStyles.ATTRIBUTE_STYLE;
			structureStylesEvent.property = null;
			this.styles.remove(structureComponent);
			if (structureStylesEvent.style != null) {
				; // structureStylesEvent.style.removeStructureStylesEventListener(
					// this ); JLM DEBUG
			}

			structureStylesEvent.style = this.getStyle(structureComponent);
		}

		// Fire the event
		this.processStructureStylesEvent(structureStylesEvent);
	}

	/**
	 * Get the style object that is currently assigned to the given
	 * StructureComponent, clone the Style object, assign the cloned Style
	 * object as the new StructureComponent Style, and return the newly assigned
	 * Style object.
	 * <P>
	 * 
	 * @param the
	 *            StructureComponent object who's Style is to be replaced.
	 * @return the cloned Style object.
	 */
	public Style replaceStyle(final StructureComponent structureComponent) {
		final Style style = this.getStyle(structureComponent);
		if (style == null) {
			return null;
		}

		Style styleClone;
		try {
			styleClone = (Style) style.clone();
			this.setStyle(structureComponent, styleClone);
		} catch (final CloneNotSupportedException e) {
			styleClone = null;
		}

		return styleClone;
	}

	/**
	 * Set the default style for the specified structure component type.
	 * <P>
	 * 
	 * @param NA
	 * @return NA
	 * @throws NA
	 */
	public void setDefaultStyle(final ComponentType scType, final Style style) {
		if (scType == null) {
			throw new NullPointerException("null scType");
		}
		if (style == null) {
			throw new NullPointerException("null style");
		}

		if (style.isTypeSafe(scType)) {
			this.defaultStyle.put(scType, style);
		} else {
			throw new IllegalArgumentException("style type mismatch");
		}

		// JLM DEBUG: We need to loop through all visible structure components
		// and, for each matching type that uses the default style
		// (ie: no custom style), fire a style change event.
		// if ( visibility.get( structureComponent ) != null )
		// Style style2 = (Style) styles.get( structureComponent );
	}

	/**
	 * Get the default style for the specified structure component type.
	 * <P>
	 * 
	 * @param NA
	 * @return NA
	 * @throws NA
	 */
	public Style getDefaultStyle(final ComponentType scType) {
		if (scType == null) {
			throw new NullPointerException("null scType");
		}

		return (Style) this.defaultStyle.get(scType);
	}

	//
	// Visibility methods
	//

	/**
	 * Set the visibility state for the given structure component and then fire
	 * an event to all listeners.
	 * <P>
	 * 
	 * @param
	 * @return
	 * @throws
	 */
	public void setVisible(final StructureComponent structureComponent,
			boolean newState) {
		if (structureComponent == null) {
			throw new NullPointerException("null structureComponent");
		}

		boolean oldState = this.isVisible(structureComponent);

		// Check special cases where no action needs to be taken.
		if (newState && oldState) {
			return; // Already visible
		} else if (!newState && !oldState) {
			return; // Already invisible
		}

		// Check standard cases.
		if (newState) {
			this.visibility.put(structureComponent, Boolean.TRUE);
		} else {
			this.visibility.remove(structureComponent);
		}

		// If necessary, set the special case flags.
		if (this.visibility.size() <= 0) {
			this.visibilityFlag = StructureStyles.FLAG_NONE;
		} else {
			this.visibilityFlag = StructureStyles.FLAG_SOME;
		}

		// Fire the event
		final StructureStylesEvent structureStylesEvent = new StructureStylesEvent();
		structureStylesEvent.structureComponent = structureComponent;
		structureStylesEvent.style = (Style) this.styles.get(structureComponent);
		structureStylesEvent.attribute = StructureStyles.ATTRIBUTE_VISIBILITY;
		structureStylesEvent.property = null;
		this.processStructureStylesEvent(structureStylesEvent);
	}

	/**
	 * Get the visibility state for the given structure component.
	 * <P>
	 * 
	 * @param
	 * @return
	 * @throws
	 */
	public boolean isVisible(final StructureComponent structureComponent) {
		if (structureComponent == null) {
			throw new NullPointerException("null structureComponent");
		}

		if (this.visibilityFlag == StructureStyles.FLAG_ALL) {
			return true;
		} else if (this.visibilityFlag == StructureStyles.FLAG_NONE) {
			return false;
		} else if (this.visibility.get(structureComponent) != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Set the visibility state for the entire structure to true and then fire
	 * an event to all listeners.
	 * <P>
	 * 
	 * @param
	 * @return
	 * @throws
	 */
	public void showAll() {
		this.visibility.clear();
		this.visibilityFlag = StructureStyles.FLAG_ALL;

		// Fire the event
		final StructureStylesEvent structureStylesEvent = new StructureStylesEvent();
		structureStylesEvent.structureComponent = null;
		structureStylesEvent.style = null;
		structureStylesEvent.attribute = StructureStyles.ATTRIBUTE_VISIBILITY;
		structureStylesEvent.property = null;
		structureStylesEvent.flag = this.visibilityFlag;
		this.processStructureStylesEvent(structureStylesEvent);
	}
	
	public boolean isAnythingSelected() {
		return this.selection.size() > 0;
	}
	
	public ArrayList<StructureComponent> getSelectedItems()
	{
		final ArrayList<StructureComponent> items = new ArrayList<StructureComponent>();
		
		for (final StructureComponent comp : this.selection.keySet())
		{	
			final SelectionNode node = (SelectionNode)selection.get(comp);
				// what if it's an Integer??
			
			if(comp.getStructureComponentType() == ComponentType.ATOM) {
				items.add(comp);
			} else if(comp.getStructureComponentType() == ComponentType.RESIDUE) {
				if(node.completeChildren == ((Residue)comp).getAtomCount()) {
					items.add(comp);
				}
			} else if(comp.getStructureComponentType() == ComponentType.FRAGMENT) {
				if(node.completeChildren == ((Fragment)comp).getResidueCount()) {
					items.add(comp);
				}
			} else if(comp.getStructureComponentType() == ComponentType.CHAIN) {
				if(node.completeChildren == ((Chain)comp).getFragmentCount()) {
					items.add(comp);
				}
			}
		}
		
		return items;
	}

	/**
	 * Set the visibility state for the entire structure to false and then fire
	 * an event to all listeners.
	 * <P>
	 * 
	 * @param
	 * @return
	 * @throws
	 */
	public void hideAll() {
		this.visibility.clear();
		this.visibilityFlag = StructureStyles.FLAG_NONE;

		// Fire the event
		final StructureStylesEvent structureStylesEvent = new StructureStylesEvent();
		structureStylesEvent.structureComponent = null;
		structureStylesEvent.style = null;
		structureStylesEvent.attribute = StructureStyles.ATTRIBUTE_VISIBILITY;
		structureStylesEvent.property = null;
		structureStylesEvent.flag = this.visibilityFlag;
		this.processStructureStylesEvent(structureStylesEvent);
	}

	/**
	 * Returns an enumeration of visible StructureComponent objects.
	 * <P>
	 * 
	 * @param
	 * @return
	 * @throws
	 */
	public Enumeration<StructureComponent> getVisible() {
		return this.visibility.keys();
	}

	//
	// Selection methods
	//

	/**
	 * Set the selection state for the given structure component and then fire
	 * an event to all listeners.
	 * <P>
	 * 
	 * @param stuctureComponent
	 *            The StrucureComponent to be selected.
	 * @param newState
	 *            The new selection state for the object.
	 * @return NONE.
	 * @throws NullPointerException
	 *             if StructureComponent is null.
	 */
	public void setSelected(final StructureComponent structureComponent,
			final boolean newState) {
		if (structureComponent.getStructureComponentType() == ComponentType.ATOM) {
			final Atom a = (Atom) structureComponent;
			this.setSelected(a, newState, true);
		} else if (structureComponent.getStructureComponentType() == ComponentType.BOND) {
			final Bond b = (Bond) structureComponent;
			this.setSelected(b, newState);
		} else if (structureComponent.getStructureComponentType() == ComponentType.RESIDUE) {
			final Residue r = (Residue) structureComponent;
			this
					.setSelected(r, newState, null, null, false, false, true,
							false);
		} else if (structureComponent.getStructureComponentType() == ComponentType.FRAGMENT) {
			final Fragment f = (Fragment) structureComponent;
			this
					.setSelected(f, newState, null, null, false, false, true,
							false);
		} else if (structureComponent.getStructureComponentType() == ComponentType.CHAIN) {
			final Chain c = (Chain) structureComponent;
			this
					.setSelected(c, newState, null, null, false, false, true,
							false);
		}

		// Fire the event
		final StructureStylesEvent structureStylesEvent = new StructureStylesEvent();
		structureStylesEvent.structureComponent = structureComponent;
		structureStylesEvent.style = (Style) this.styles.get(structureComponent);
		structureStylesEvent.attribute = StructureStyles.ATTRIBUTE_SELECTION;
		structureStylesEvent.property = null;
		this.processStructureStylesEvent(structureStylesEvent);
	}

	// the chain must have been complete, and the child must be being
	// deselected.
	// neither the chain's nor the child's SelectionNode is modified by this
	// function.
	private void splitNode(final Chain c, final Fragment child) {
		final int fragCount = c.getFragmentCount();
		for (int i = 0; i < fragCount; i++) {
			final Fragment f = c.getFragment(i);
			if (f != child) {
				final SelectionNode node = new SelectionNode();
				node.completeChildren = f.getResidueCount();
				node.totalChildren = f.getResidueCount();
				this.selection.put(f, node);
			}
		}
	}

	// the fragment must have been complete, and the child must be being
	// deselected.
	// neither the fragment's nor the child's SelectionNode is modified by this
	// function.
	private void splitNode(final Fragment f, final Residue child) {
		final int resCount = f.getResidueCount();
		for (int i = 0; i < resCount; i++) {
			final Residue r = f.getResidue(i);
			if (r != child) {
				final SelectionNode node = new SelectionNode();
				node.completeChildren = r.getAtomCount();
				node.totalChildren = r.getAtomCount();
				this.selection.put(r, node);
			}
		}
	}

	// the fragment must have been complete, and the child must be being
	// deselected.
	// neither the fragment's nor the child's SelectionNode is modified by this
	// function.
	private void splitNode(final Residue r, final Atom child) {
		final int fragCount = r.getAtomCount();
		for (int i = 0; i < fragCount; i++) {
			final Atom a = r.getAtom(i);
			if (a != child) {
				final SelectionNode node = new SelectionNode();
				node.completeChildren = -1;
				node.totalChildren = -1;
				this.selection.put(a, node);
			}
		}
	}

	private void setSelected(final Chain c, final boolean newState, final Fragment child,
			final SelectionNode childNode, final boolean childNoLongerExists,
			final boolean childIsNewlyAdded, final boolean isLeaf, final boolean isChildLeaf) {
		SelectionNode node = (SelectionNode) this.selection.get(c);

		if (node == null) {
			if (newState) {
				node = new SelectionNode();

				if (isLeaf) {
					node.completeChildren = c.getFragmentCount();
					node.totalChildren = c.getFragmentCount();
				} else {
					node.totalChildren = 1;
					if (childNode.completeChildren == child.getResidueCount()
							&& childNode.completeChildren == child
									.getResidueCount()) {
						node.completeChildren = 1;
					} else {
						node.completeChildren = 0;
					}
				}

				this.selection.put(c, node);
			} else {
				if (isLeaf) {
					// nothing to do...
				} else {
					// nothing to do...
				}
			}
		} else {
			if (newState) {
				if (isLeaf) {
					node.completeChildren = c.getFragmentCount();
					node.totalChildren = c.getFragmentCount();
					this.removeSelectionChildren(c);
				} else {
					if (childNode.completeChildren == child.getResidueCount()) { // is
																					// the
																					// child
																					// completely
																					// selected?
						node.completeChildren++;

						if (node.completeChildren == c.getFragmentCount()) { // is
																				// this
																				// node
																				// now
																				// completely
																				// selected?
							this.removeSelectionChildren(c);
						} else {
							// nothing to do...
						}
					} else {
						// nothing to do...
					}

					if (childIsNewlyAdded) {
						node.totalChildren++;
					} else {
						// nothing to do...
					}
				}
			} else {
				if (isLeaf) {
					this.selection.remove(c);
					this.removeSelectionChildren(c);
				} else {
					if (childNoLongerExists) {
						if (node.totalChildren == 1) { // will this node be
														// completely
														// deselected?
							this.selection.remove(c);
						} else {
							node.totalChildren--;

							if (childNode.completeChildren == child
									.getResidueCount()) { // was the child
															// complete before
															// it was deleted?
								if (node.completeChildren == c
										.getFragmentCount()) { // is this node
																// currently
																// complete?
									this.splitNode(c, child);
								} else {
									// nothing else to do
								}

								node.completeChildren--;
							} else {
								// nothing else to do
							}
						}
					} else {
						// this is a deselection, so either this is an
						// unnecessary call or the child has become incomplete.
						if (childNode == null) {
							// this is a deselection of something below. Just
							// split this node.
							if(isChildLeaf) {
								if (c.getFragmentCount() == 1) {
									this.selection.remove(c);
								} else {
									if(node.completeChildren == c.getFragmentCount()) {
										this.splitNode(c, child);
									}
									node.completeChildren--;
									node.totalChildren--;
								}
							} else {
								if(node.completeChildren == c.getFragmentCount()) {
									this.splitNode(c, child);
								}
								node.completeChildren--;
							}
						} else if (childNode.completeChildren == child.getResidueCount()) {
							if (node.completeChildren == c.getFragmentCount()) { // is
																					// this
																					// node
																					// currently
																					// complete?
								this.splitNode(c, child);
							} else {
								// nothing else to do
							}

							node.completeChildren--;
						} else {
							node.completeChildren--;
						}
					}
				}
			}
		}

//		System.out.print("Chain selection: ");
//		if (node == null) {
//			System.out.println("Node was null at the end.");
//		} else {
//			System.out.println("Total children: " + node.totalChildren
//					+ ", complete children: " + node.completeChildren);
//		}
	}

	private void setSelected(final Fragment f, final boolean newState, final Residue child,
			final SelectionNode childNode, final boolean childNoLongerExists,
			final boolean childIsNewlyAdded, final boolean isLeaf, final boolean isChildLeaf) {
		SelectionNode node = (SelectionNode) this.selection.get(f);

		if (node == null) {
			if (newState) {
				node = new SelectionNode();

				if (isLeaf) {
					node.completeChildren = f.getResidueCount();
					node.totalChildren = f.getResidueCount();
				} else {
					node.totalChildren = 1;

					if (childNode.completeChildren == child.getAtomCount()
							&& childNode.completeChildren == child
									.getAtomCount()) {
						node.completeChildren = 1;
					} else {
						node.completeChildren = 0;
					}
				}

				this.selection.put(f, node);

				// propogate up...
				this.setSelected(f.getChain(), newState, f, node, false, true,
						false, isLeaf);
			} else {
				// propogate up...
				this.setSelected(f.getChain(), newState, f, node, false, false,
						false, isLeaf);

				if (isLeaf) {
					// nothing to do...
				} else {
					// if this is not the leaf, and the parent has been split,
					// split this node too...
					final Chain parent = f.getChain();
					final SelectionNode parentNode = (SelectionNode) this.selection
							.get(parent);
					if (parentNode != null) {
						this.splitNode(f, child);
						node = new SelectionNode();
						
						if(isChildLeaf) {
							node.totalChildren = f.getResidueCount() - 1;
						} else {
							node.totalChildren = f.getResidueCount();
						}
						
						node.completeChildren = f.getResidueCount() - 1;
						if(node.totalChildren == 0) {
							this.selection.remove(f);
						} else {
							this.selection.put(f, node);
						}
					} else {
						this.selection.remove(f);
					}
				}
			}
		} else {
			if (newState) {
				if (isLeaf) {
					node.completeChildren = f.getResidueCount();
					node.totalChildren = f.getResidueCount();
					this.removeSelectionChildren(f);

					// propogate up...
					this.setSelected(f.getChain(), newState, f, node, false,
							false, false, isLeaf);
				} else {
					if (childNode.completeChildren == child.getAtomCount()) { // is
																				// the
																				// child
																				// completely
																				// selected?
						node.completeChildren++;

						if (node.completeChildren == f.getResidueCount()) { // is
																			// this
																			// node
																			// now
																			// completely
																			// selected?
							this.removeSelectionChildren(f);

							// propogate up...
							this.setSelected(f.getChain(), newState, f, node,
									false, false, false, isLeaf);
						} else {
							// nothing to do...
						}
					} else {
						// nothing to do...
					}

					if (childIsNewlyAdded) {
						node.totalChildren++;
					} else {
						// nothing to do...
					}
				}
			} else {
				if (isLeaf) {
					this.selection.remove(f);
					this.removeSelectionChildren(f);
					this.setSelected(f.getChain(), newState, f, node, true,
							false, false, isLeaf);
				} else {
					if (childNoLongerExists) {
						if (node.totalChildren == 1) { // will this node be
														// completely
														// deselected?
							this.selection.remove(f);
							this.setSelected(f.getChain(), newState, f, node,
									true, false, false, isLeaf);
						} else {
							node.totalChildren--;

							if (childNode.completeChildren == child
									.getAtomCount()) { // was the child
														// complete before it
														// was deleted?
								if (node.completeChildren == f
										.getResidueCount()) { // is this node
																// currently
																// complete?
									this.splitNode(f, child);

									this.setSelected(f.getChain(), newState, f,
											node, false, false, false, isLeaf);
								} else {
									// nothing else to do
								}

								node.completeChildren--;
							} else {
								// nothing else to do
							}
						}
					} else {
						// this is a deselection, so either this is an
						// unnecessary call or the child has become incomplete.
						if (childNode == null) {
							// this is a deselection of something below. Just
							// split this node.
							if(isChildLeaf) {
								if (f.getResidueCount() == 1) {
									this.selection.remove(f);
									
									// propogate up...
									this.setSelected(f.getChain(), newState, f, node, true, false, false, isLeaf);
								} else {
									if(node.completeChildren == f.getResidueCount()) {
										this.splitNode(f, child);
									}
									node.completeChildren--;
									node.totalChildren--;
									
									// propogate up...
									this.setSelected(f.getChain(), newState, f, node, false, false, false, isLeaf);
								}
							} else {
								if(node.completeChildren == f.getResidueCount()) {
									this.splitNode(f, child);
								}
								node.completeChildren--;
								
								// propogate up...
								this.setSelected(f.getChain(), newState, f, node, false, false, false, isLeaf);
							}
						} else if (childNode.completeChildren == child
								.getAtomCount()) {
							if (node.completeChildren == f.getResidueCount()) { // is
																				// this
																				// node
																				// currently
																				// complete?
								this.splitNode(f, child);

								this.setSelected(f.getChain(), newState, f,
										node, false, false, false, isLeaf);
							} else {
								// nothing else to do
							}

							node.completeChildren--;
						} else {
							node.completeChildren--;
						}
					}
				}
			}
		}

//		System.out.print("Fragment selection: ");
//		if (node == null) {
//			System.out.println("Node was null at the end.");
//		} else {
//			System.out.println("Total children: " + node.totalChildren
//					+ ", complete children: " + node.completeChildren);
//		}
	}

	private void setSelected(final Residue r, final boolean newState, final Atom child,
			final SelectionNode childNode, final boolean childNoLongerExists,
			final boolean childIsNewlyAdded, final boolean isLeaf, final boolean isChildLeaf) {
		SelectionNode node = (SelectionNode) this.selection.get(r);

		if (node == null) {
			if (newState) {
				node = new SelectionNode();

				if (isLeaf) {
					node.completeChildren = r.getAtomCount();
					node.totalChildren = r.getAtomCount();
				} else {
					node.totalChildren = 1;
					node.completeChildren = 1;
				}

				this.selection.put(r, node);

				// propogate up...
				this.setSelected(r.getFragment(), newState, r, node, false,
						true, false, isLeaf);
			} else {
				// propogate up...
				this.setSelected(r.getFragment(), newState, r, node, false,
						false, false, isLeaf);

				if (isLeaf) {
					// nothing to do...
				} else {
					// if this is not the leaf, and the parent has been split,
					// split this node too...
					final Fragment parent = r.getFragment();
					final SelectionNode parentNode = (SelectionNode) this.selection
							.get(parent);
					if (parentNode != null) {
						this.splitNode(r, child);
						node = new SelectionNode();
						
						if(isChildLeaf) {
							node.totalChildren = r.getAtomCount() - 1;
						} else {
							node.totalChildren = r.getAtomCount();
						}
						
						node.completeChildren = r.getAtomCount() - 1;
						if(node.totalChildren == 0) {
							this.selection.remove(r);
						} else {
							this.selection.put(r, node);
						}
					} else {
						this.selection.remove(r);
					}
				}
			}
		} else {
			if (newState) {
				if (isLeaf) {
					node.completeChildren = r.getAtomCount();
					node.totalChildren = r.getAtomCount();
					this.removeSelectionChildren(r);

					// propogate up...
					this.setSelected(r.getFragment(), newState, r, node, false,
							false, false, isLeaf);
				} else {
					node.completeChildren++;

					if (node.completeChildren == r.getAtomCount()) { // is
																		// this
																		// node
																		// now
																		// completely
																		// selected?
						this.removeSelectionChildren(r);

						// propogate up...
						this.setSelected(r.getFragment(), newState, r, node,
								false, false, false, isLeaf);
					} else {
						// nothing to do...
					}

					if (childIsNewlyAdded) {
						node.totalChildren++;
					} else {
						// nothing to do...
					}
				}
			} else {
				if (isLeaf) {
					this.selection.remove(r);
					this.removeSelectionChildren(r);
					this.setSelected(r.getFragment(), newState, r, node, true,
							false, false, isLeaf);
				} else {
					if (childNoLongerExists) {
						if (node.totalChildren == 1) { // will this node be
														// completely
														// deselected?
							this.selection.remove(r);
							this.setSelected(r.getFragment(), newState, r,
									node, true, false, false, isLeaf);
						} else {
							node.totalChildren--;

							if (node.completeChildren == r.getAtomCount()) { // is
																				// this
																				// node
																				// currently
																				// complete?
								this.splitNode(r, child);

								this.setSelected(r.getFragment(), newState, r, node, false, false, false, isLeaf);
							} else {
								// nothing else to do
							}

							node.completeChildren--;
						}
					} else {
						if (childNode == null) {
							// this is a deselection of something below. Just
							// split this node.
							if(isChildLeaf) {
								if (r.getAtomCount() == 1) {
									this.selection.remove(r);
									
									// propogate up...
									this.setSelected(r.getFragment(), newState, r, node, true, false, false, isLeaf);
								} else {
									if(node.completeChildren == r.getAtomCount()) {
										this.splitNode(r, child);
									}
									node.completeChildren--;
									node.totalChildren--;
									
									// propogate up...
									this.setSelected(r.getFragment(), newState, r, node, false, false, false, isLeaf);
								}
							} else {
								if(node.completeChildren == r.getAtomCount()) {
									this.splitNode(r, child);
								}
								node.completeChildren--;
								
								// propogate up...
								this.setSelected(r.getFragment(), newState, r, node, false, false, false, isLeaf);
							}
						} else {
							if (node.completeChildren == r.getAtomCount()) { // is
																				// this
																				// node
																				// currently
																				// complete?
								this.splitNode(r, child);

								this.setSelected(r.getFragment(), newState, r,
										node, false, false, false, isLeaf);
							} else {
								// nothing else to do
							}

							node.completeChildren--;
						}
					}
				}
			}
		}

//		System.out.print("Residue selection: ");
//		if (node == null) {
//			System.out.println("Node was null at the end.");
//		} else {
//			System.out.println("Total children: " + node.totalChildren
//					+ ", complete children: " + node.completeChildren);
//		}
	}

	private void setSelected(final Atom a, final boolean newState, final boolean isLeaf) {
		SelectionNode node = (SelectionNode) this.selection.get(a);

		if(newState) {
			if(node == null) {
				node = new SelectionNode();
				node.completeChildren = -1;
				node.totalChildren = -1;

				this.selection.put(a, node);
				this.setSelected(this.structureMap.getResidue(a), newState, a,
						node, false, true, false, true);
			} else {
				// shouldn't happen
				(new Exception()).printStackTrace();
			}
		} else {
			if(node == null) {
				this.setSelected(this.structureMap.getResidue(a), newState, a, node, false, false, false, true);
			} else {
				this.selection.remove(a);
				this.setSelected(this.structureMap.getResidue(a), newState, a, node, true, false, false, true);
			}
		}

//		System.out.print("Atom selection: ");
//		if (node == null) {
//			System.out.println("Node was null at the end.");
//		} else {
//			System.out.println("Total children: " + node.totalChildren
//					+ ", complete children: " + node.completeChildren);
//		}
	}

	private void setSelected(final Bond b, final boolean newState) {
		if(this.selection.containsKey(b.getAtom(0)) != newState) {
			this.setSelected(b.getAtom(0), newState, false);
		}
		if(this.selection.containsKey(b.getAtom(1)) != newState) {
			this.setSelected(b.getAtom(1), newState, false);
		}

//		System.out.println("Bond selection.");
	}

	private class SelectionNode {
		public int totalChildren = -1;

		public int completeChildren = -1;
	}

	private void removeSelectionChildren(final Residue r) {
		final int atomCount = r.getAtomCount();
		for (int i = 0; i < atomCount; i++) {
			this.selection.remove(r.getAtom(i));
		}
	}

	private void removeSelectionChildren(final Fragment f) {
		final int resCount = f.getResidueCount();
		for (int i = 0; i < resCount; i++) {
			final Residue r = f.getResidue(i);
			final Object returned = this.selection.remove(r);
			if (returned != null) {
				this.removeSelectionChildren(r);
			}
		}
	}

	private void removeSelectionChildren(final Chain c) {
		final int fragCount = c.getFragmentCount();
		for (int i = 0; i < fragCount; i++) {
			final Fragment f = c.getFragment(i);
			final Object returned = this.selection.remove(f);
			if (returned != null) {
				this.removeSelectionChildren(f);
			}
		}
	}

	public void clearSelections() {
		final Object[] ob = this.selection.keySet().toArray();
		for (int i = 0; i < ob.length; i++) {
			final StructureComponent comp = (StructureComponent) ob[i];
			this.selection.remove(comp);

			
		}
		
		final StructureStylesEvent structureStylesEvent = new StructureStylesEvent();
		structureStylesEvent.structureComponent = null;
//		structureStylesEvent.style = (Style) styles.get(comp);
		structureStylesEvent.attribute = StructureStyles.ATTRIBUTE_SELECTION;
		structureStylesEvent.property = null;
		this.processStructureStylesEvent(structureStylesEvent);
	}

	/**
	 * Get the selection state for the given structure component. If the
	 * component is null, then the test refers to the entire Structure (that is,
	 * NONE of or ALL the components are selected).
	 * <P>
	 * 
	 * @param
	 * @return
	 * @throws
	 */
	public boolean isSelected(final StructureComponent structureComponent) {
		// If the entire structure is not selected/unselected
		// and no component was specified, then return false
		// because the structure is not entirely selected.
		if (structureComponent == null) {
			return false;
		}

		boolean isSelected = false;

		// Is the component in the hash with an explicit value?
		final SelectionNode node = (SelectionNode) this.selection.get(structureComponent);
		;
		if (node != null) {
			if (structureComponent.getStructureComponentType() == ComponentType.ATOM) {
				isSelected = true;
			} else if (structureComponent.getStructureComponentType() == ComponentType.RESIDUE) {
				final Residue r = (Residue) structureComponent;
				isSelected = r.getAtomCount() == node.completeChildren;
			} else if (structureComponent.getStructureComponentType() == ComponentType.FRAGMENT) {
				final Fragment f = (Fragment) structureComponent;
				isSelected = f.getResidueCount() == node.completeChildren;
			} else if (structureComponent.getStructureComponentType() == ComponentType.CHAIN) {
				final Chain c = (Chain) structureComponent;
				isSelected = c.getFragmentCount() == node.completeChildren;
			}
		} else if (structureComponent.getStructureComponentType() == ComponentType.ATOM) {
			final Atom a = (Atom) structureComponent;
			isSelected = this.isSelected(this.structureMap.getResidue(a));
		} else if (structureComponent.getStructureComponentType() == ComponentType.BOND) {
			final Bond b = (Bond) structureComponent;
			isSelected = this.isSelected(b.getAtom(0))
					&& this.isSelected(b.getAtom(1));
		} else if (structureComponent.getStructureComponentType() == ComponentType.RESIDUE) {
			final Residue r = (Residue) structureComponent;
			isSelected = this.isSelected(r.getFragment());
		} else if (structureComponent.getStructureComponentType() == ComponentType.FRAGMENT) {
			final Fragment f = (Fragment) structureComponent;
			isSelected = this.isSelected(f.getChain());
		}

		return isSelected;
	}

	/**
	 * Don't send bonds - send their component atoms. If a simple Object is at
	 * the end of the array, nothing was selected.
	 * 
	 * @param structureComponent
	 * @return
	 */
	public ArrayList<Object> getPathToClosestSelectedElement(
			final StructureComponent structureComponent) {
		// If the entire structure is not selected/unselected
		// and no component was specified, then return false
		// because the structure is not entirely selected.
		if (structureComponent == null) {
			return null;
		}

		final ArrayList<Object> path = new ArrayList<Object>();

		// Is the component in the hash with an explicit value?
		StructureComponent curComp = structureComponent;
		while (true) {
			final Integer childCount = (Integer) this.selection.get(curComp);
						// this seems not good....
						// these should be SelectionNode type...

			if (childCount != null) {
				break;
			} else if (structureComponent.getStructureComponentType() == ComponentType.ATOM) {
				final Atom a = (Atom) structureComponent;
				curComp = this.structureMap.getResidue(a);
			} else if (structureComponent.getStructureComponentType() == ComponentType.RESIDUE) {
				final Residue r = (Residue) structureComponent;
				curComp = r.getFragment();
			} else if (structureComponent.getStructureComponentType() == ComponentType.FRAGMENT) {
				final Fragment f = (Fragment) structureComponent;
				curComp = f.getChain();
			} else if (structureComponent.getStructureComponentType() == ComponentType.CHAIN) {
				path.add(new Object());
				break;
			}

			path.add(curComp);
		}

		return path;
	}

	/**
	 * Returns an enumeration of selected StructureComponent objects.
	 * <P>
	 * 
	 * @param
	 * @return
	 * @throws
	 */
	public Enumeration<StructureComponent> getSelected() {
		/*Enumeration enum = new Enumeration() {
			Enumeration selKeys = selection.keys();

			Object nextElement = selKeys.nextElement();

			public boolean hasMoreElements() {
				if (nextElement != null)
					return true;
				else
					return false;
			}

			public Object nextElement() {
				Object result = nextElement;
				do {
					nextElement = selKeys.nextElement();
					if (nextElement != null) {
						if (((Boolean) nextElement).booleanValue())
							return result;
					}
				} while (nextElement != null);
				return result;
			}
		};
		return enum;*/
		return null;
	}

	//
	// StructureStyleEvent methods
	//

	/**
	 * Informs all registered listeners that a style has changed. Called
	 * explictly when setStyle is called, or, implictly when any individual
	 * style changes state.
	 * <P>
	 * 
	 * @param
	 * @return
	 * @throws
	 */
	public void processStructureStylesEvent(
			final StructureStylesEvent structureStylesEvent) {
		// Loop through and call all StructureStylesEventListener objects.
		final int listenerCount = this.listeners.size();
		for (int i = 0; i < listenerCount; i++) {
			final IStructureStylesEventListener listener = this.listeners.get(i);
			listener.processStructureStylesEvent(structureStylesEvent);
		}
	}

	/**
	 * Please complete the missing tags for main
	 * <P>
	 * 
	 * @param
	 * @return
	 * @throws
	 */
	public void addStructureStylesEventListener(
			final IStructureStylesEventListener structureStylesEventListener) {
		this.listeners.add(structureStylesEventListener);
	}

	/**
	 * Please complete the missing tags for main
	 * <P>
	 * 
	 * @param
	 * @return
	 * @throws
	 */
	public void removeStructureStylesEventListener(
			final IStructureStylesEventListener structureStylesEventListener) {
		this.listeners.remove(structureStylesEventListener);
	}

	//
	// StructureComponentEvent methods
	//

	/**
	 * Please complete the missing tags for main
	 * <P>
	 * 
	 * @param
	 * @return
	 * @throws
	 */
	public void processStructureComponentEvent(final StructureComponentEvent sce) {
		final StructureComponent structureComponent = sce.structureComponent;

		if (sce.changeType == StructureComponentEvent.TYPE_REMOVE) {
			// Remove all style references to the given StructureComponent.
			this.styles.remove(structureComponent);
			this.visibility.remove(structureComponent);
			this.selection.remove(structureComponent);

			// Fire a style event to viewers
			final StructureStylesEvent structureStylesEvent = new StructureStylesEvent();
			structureStylesEvent.structureComponent = structureComponent;
			structureStylesEvent.style = (Style) this.styles.get(structureComponent);
			structureStylesEvent.attribute = StructureStyles.ATTRIBUTE_VISIBILITY;
			structureStylesEvent.property = null;
			this.processStructureStylesEvent(structureStylesEvent);
		}
	}
}
