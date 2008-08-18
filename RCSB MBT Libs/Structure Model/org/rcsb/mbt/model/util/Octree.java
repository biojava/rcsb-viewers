//  $Id: Octree.java,v 1.1 2007/02/08 02:38:52 jbeaver Exp $
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
//  $Log: Octree.java,v $
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
//  Revision 1.10  2004/04/09 00:15:21  moreland
//  Updated copyright to new UCSD wording.
//
//  Revision 1.9  2004/01/29 17:14:52  agramada
//  Removed General Atomics from copyright
//
//  Revision 1.8  2004/01/14 19:25:51  agramada
//  Remove useless code, commented public methods. Also, the build methods now
//  throw an ExcessiveDivisionException when the size of the box becomes too
//  "small".
//
//  Revision 1.7  2003/12/15 21:34:42  moreland
//  Corrected open comment.
//
//  Revision 1.6  2003/12/15 21:33:27  moreland
//  Commented out debug print statments.
//
//  Revision 1.5  2003/10/17 18:19:32  moreland
//  Fixed a javadoc comment.
//
//  Revision 1.4  2003/10/01 21:11:51  agramada
//  Added methods needed by DerivedInformation class for derivation of
//  secondary structures.
//
//  Revision 1.3  2003/07/17 16:40:32  agramada
//  Removed some comments.
//
//  Revision 1.2  2003/07/11 20:23:05  agramada
//  Made get*Bonds methods return Bond objects instead of BondInfo. Removed
//  some coments.
//
//  Revision 1.1  2003/07/11 18:17:53  moreland
//  Modifed Apostol's Octree classes to genate Bonds from the BondFactory
//  and in turn the StructureMap class.
//
//  Revision 1.3  2003/07/07 23:02:47  agramada
//  Added a method to return a vector of covalent bonds rather than an array.
//
//  Revision 1.2  2003/06/24 22:19:46  agramada
//  Reorganized the geometry package. Old classes removed, new classes added.
//
//  Revision 1.1  2003/04/24 18:46:36  agramada
//  First version of the Octree class to be used for an efficient search of
//  the set of bonds between a set of atoms.
//
//  Revision 1.2  2003/02/20 17:08:22  agramada
//  First working version.
//
//  Revision 1.0  2003/02/20 23:38:39  agramada
//

package org.rcsb.mbt.model.util;

import java.util.*;

import org.rcsb.mbt.model.Bond;

/**
 * Octree class constructed with recursion in mind. Each child is itself a tree.
 * <P>
 * 
 * @author Apostol Gramada
 */
public class Octree {
	//
	// Fields
	// 
	private int dimension; // Keep it general as long as practical. In
							// practice, maximum 3.

	private int maxNumberOfChildren;

	private int numberOfChildren;

	private int childId; // Number from 0 to 2^dimension

	private Octree root = null;

	private Octree[] children = null;

	private OctreeDataItem[] dataItems = null;

	private double[] firstCorner;

	private double[] secondCorner;

	private double[] margin;

	private int weight;

	private int leafCutOff = 1;

	private boolean leaf = false;

	private double cellRadius;

	private double[] size;

	private double[] mid; // Center of the cell

	private static int countAppBondOp = 0;

	private String path; // Sequence of digits showing the path from root to
							// this child

	/**
	 * A counter for the number of children instantiated in the tree
	 */
	public static int countChildren = 0;

	//
	// Constructors.
	//

	/**
	 * Construct an octree as a child of "parent" in a tree rooted at "root"
	 * with given data and corners.
	 */
	public Octree(final Octree root, final Octree parent, final int id, final OctreeDataItem[] data,
			final double[] firstCorner, final double[] secondCorner) {
		this.root = root;
		this.childId = id;
		this.path = parent.path + id;
		this.dimension = root.getDimension(); // Assume dimension passed is
												// the same as dimension of data
		// this.dataItems = data;
		this.firstCorner = firstCorner;
		this.secondCorner = secondCorner;
		this.maxNumberOfChildren = root.getMaxNumberOfChildren();

		//
		// Set the size in each direction.
		//

		double maxSize = 0.0f;
		double minSize = 1.0E6f;
		this.size = new double[this.dimension];
		this.mid = new double[this.dimension];
		this.cellRadius = 0.0f;
		for (int j = 0; j < this.dimension; j++) {
			this.size[j] = Math.abs(secondCorner[j] - firstCorner[j]);
			this.mid[j] = (secondCorner[j] + firstCorner[j]) / 2.0f;
			this.cellRadius += this.size[j] * this.size[j];
			if (this.size[j] > maxSize) {
				maxSize = this.size[j];
			}
			if (this.size[j] < minSize) {
				minSize = this.size[j];
			}
		}

		this.cellRadius = (float)Math.sqrt(this.cellRadius);
		this.cellRadius /= 2.0;
	}

	/**
	 * Constructor that is mostly used for instantiating the root tree.
	 */
	public Octree(final int spaceDimension, final OctreeDataItem[] data, final float[] offset) {
		//
		// Derive the domain spaned by the coordinates of the elements.
		//

		this.dimension = spaceDimension; // Assume dimension passed is the same as
									// dimension of data
		this.dataItems = data;
		this.weight = data.length;
		this.margin = new double[this.dimension];

		// System.out.println( "Root tree data length: " + dataItems.length );
		this.firstCorner = new double[this.dimension];
		this.secondCorner = new double[this.dimension];
		for (int i = 0; i < this.dimension; i++) {
			this.margin[i] = offset[i];
			this.firstCorner[i] = 1.0E6f;
			this.secondCorner[i] = -1.0E6f;
		}

		this.maxNumberOfChildren = 1;
		for (int i = 0; i < this.dimension; i++) {
			this.maxNumberOfChildren <<= 1;
		}

		//
		// Determine the two corners from data coordinates
		//

		double x;
		for (int i = 0; i < this.dataItems.length; i++) {
			final double coordinate[] = this.dataItems[i].getCoordinate();
			for (int j = 0; j < this.dimension; j++) {
				x = coordinate[j];
				if (x <= this.firstCorner[j]) {
					this.firstCorner[j] = x;
				}
				if (x >= this.secondCorner[j]) {
					this.secondCorner[j] = x;
				}
			}
		}

		//
		// Add margins to the corners to make the box somewhat larger than
		// the container of spatial data and set the size in each direction.
		//

		double maxSize = 0.0f;
		double minSize = 1.0E6f;
		this.size = new double[this.dimension];
		this.cellRadius = 0.0f;
		this.mid = new double[this.dimension];
		for (int j = 0; j < this.dimension; j++) {
			this.firstCorner[j] -= this.margin[j];
			this.secondCorner[j] += this.margin[j];
			this.size[j] = Math.abs(this.secondCorner[j] - this.firstCorner[j]);
			this.mid[j] = (this.secondCorner[j] + this.firstCorner[j]) / 2.0f;
			this.cellRadius += this.size[j] * this.size[j];
			if (this.size[j] > maxSize) {
				maxSize = this.size[j];
			}
			if (this.size[j] < minSize) {
				minSize = this.size[j];
			}
		}

		this.cellRadius = (float)Math.sqrt(this.cellRadius);
		this.cellRadius /= 2.0;

		//
		// We have a root of the octree now
		//

		this.childId = 0;
		this.path = "" + this.childId;
		// levels = 0;
		this.setRoot(this);
		// if ( data.length > 0 ) build( );
	}

	//
	// Methods.
	//

	public boolean existMultipleItems() {
		if(this.children != null) {
			for(int i = 0; i < this.children.length; i++) {
				if(this.children[i] != null) {
					return this.children[i].existMultipleItems();
				}
			}
		} else if(this.leaf) {
			if(this.dataItems != null && this.dataItems.length > 1) {
				return true;
			} else if(this.dataItems == null) {
				new Exception("").printStackTrace();
			}
		} else {
			new Exception("").printStackTrace();
		}
		return false;
	}
	
	/**
	 * Build the whole tree recursively.
	 */
	public void build() throws ExcessiveDivisionException {
		Octree.countChildren++;

		this.children = new Octree[this.maxNumberOfChildren];
		final Vector[] dataSets = new Vector[this.maxNumberOfChildren]; // Collects data
																// for each
																// child
		// float[] mid = new float[ dimension ];
		final double[] childSize = new double[this.dimension];
		double[] corner2;
		final int setFirstBit = 1;

		//
		// Determine the center of the parallelepiped.
		//

		for (int i = 0; i < this.dimension; i++) {
			childSize[i] = this.size[i] / 2.0f;
			// mid[i] = ( firstCorner[i] + secondCorner[i] ) / 2;
		}

		//
		// Split the data according to the cell.
		//

		for (int i = 0; i < this.maxNumberOfChildren; i++) {
			dataSets[i] = new Vector();
		}

		for (int i = 0; i < this.dataItems.length; i++) {
			int child = 0;
			final double coordinate[] = this.dataItems[i].getCoordinate();
			for (int j = 0; j < this.dimension; j++) {
				child <<= 1;
				if (coordinate[j] <= this.mid[j]) {
					child |= setFirstBit;
				}
			}
			dataSets[child].add(this.dataItems[i]);
		}

		OctreeDataItem[] tmpData = null;
		int dataSize = 0;
		Iterator dataIterator = null;
		int l = 0;
		int testBit;
		this.numberOfChildren = 0;

		if (this.dataItems.length > this.leafCutOff) {
			for (int i = 0; i < this.maxNumberOfChildren; i++) {
				dataSize = dataSets[i].size();
				if (dataSize > 0) {
					l = 0;
					tmpData = new OctreeDataItem[dataSize];
					dataIterator = dataSets[i].iterator();
					while (dataIterator.hasNext()) {
						tmpData[l] = (OctreeDataItem) dataIterator.next();
						l++;
					}

					//
					// Calculate the second corner of the child
					//

					corner2 = new double[this.dimension];
					testBit = 1;
					for (int j = this.dimension - 1; j >= 0; j--) {
						if ((i & testBit) > 0) {
							corner2[j] = this.mid[j] - childSize[j];
						} else {
							corner2[j] = this.mid[j] + childSize[j];
						}
						testBit <<= 1;
					}

					this.children[i] = new Octree(this.root, this, i, null, this.mid,
							corner2);
					this.children[i].build(tmpData);
				}
				this.numberOfChildren++;
			}
		} else {
			this.setLeafFlag(true);
			// System.out.println( "Warning: Root is also leaf " );
			// TODO: Perhaps this should throw an exception?
		}
	}

	/**
	 * Build a subtree recursively.
	 */
	public void build(final OctreeDataItem[] data) throws ExcessiveDivisionException {
		Octree.countChildren++;
		this.weight = data.length;

		if (data.length <= this.leafCutOff) {
			this.setData(data);
			this.leaf = true;
			return;
		}

		this.children = new Octree[this.maxNumberOfChildren];
		final Vector[] dataSets = new Vector[this.maxNumberOfChildren]; // Collects data
																// for each
																// child
		// float[] mid = new float[ dimension ];
		final double[] childSize = new double[this.dimension];
		double[] corner2;
		final int setFirstBit = 1;

		//
		// Determine the center of the parallelepiped.
		//

		for (int i = 0; i < this.dimension; i++) {
			childSize[i] = this.size[i] / 2.0f;
			// mid[i] = ( firstCorner[i] + secondCorner[i] ) / 2;
		}

		boolean throwException = true;
		for (int i = 0; i < this.dimension; i++) {
			if (childSize[i] > 0.001) {
				throwException = false;
				break;
			}
		}

		//
		// Split the data according to the cell.
		//

		for (int i = 0; i < this.maxNumberOfChildren; i++) {
			dataSets[i] = new Vector();
		}

		for (int i = 0; i < data.length; i++) {
			int child = 0;
			final double coordinate[] = data[i].getCoordinate();
			for (int j = 0; j < this.dimension; j++) {
				child <<= 1;
				if (coordinate[j] <= this.mid[j]) {
					child |= setFirstBit;
				}
			}
			dataSets[child].add(data[i]);
		}

		OctreeDataItem[] tmpData = null;
		int dataSize = 0;
		Iterator dataIterator = null;
		int l = 0;
		int testBit;
		this.numberOfChildren = 0;

		/*
		 * for( int i=0; i<maxNumberOfChildren; i++ ) { System.out.println(
		 * "Data Sizes: " + i + " " + dataSets[i].size() ); }
		 */

		if (throwException) {
			/*
			 * for( int i = 0; i < data.length; i++ ) { float[] coord =
			 * data[i].getCoordinate(); System.err.println( "Point " + i + " " +
			 * coord[0] + " " + coord[1] + " " + coord[2] ); }
			 */
			throw new ExcessiveDivisionException(
					"Excessive division of the data bounding box");
		}

		for (int i = 0; i < this.maxNumberOfChildren; i++) {
			dataSize = dataSets[i].size();
			if (dataSize > 0) {
				l = 0;
				tmpData = new OctreeDataItem[dataSize];
				dataIterator = dataSets[i].iterator();
				while (dataIterator.hasNext()) {
					tmpData[l] = (OctreeDataItem) dataIterator.next();
					l++;
				}

				//
				// Calculate the second corner of the child.
				//

				corner2 = new double[this.dimension];
				testBit = 1;
				for (int j = this.dimension - 1; j >= 0; j--) {
					if ((i & testBit) > 0) {
						corner2[j] = this.mid[j] - childSize[j];
					} else {
						corner2[j] = this.mid[j] + childSize[j];
					}
					testBit <<= 1;
				}

				this.children[i] = new Octree(this.root, this, i, null, this.mid, corner2);
				this.children[i].build(tmpData);
				this.numberOfChildren++;
			}
		}
	}

	//
	// Methods to simulate vector operations in an arbitrary dimension space.
	//

	/**
	 * Calculate the distance between two vectors represented as a float array.
	 */
	private final double getDistance(final double[] coord1, final double[] coord2) {
		//
		// Assume that the two coords have the same dimensionality.
		//

		double distance = 0.0f;
		for (int i = 0; i < coord1.length; i++) {
			distance += (coord2[i] - coord1[i]) * (coord2[i] - coord1[i]);
		}
		distance = (float)Math.sqrt(distance);
		return distance;
	}

	/**
	 * Sets the octree root of the octree to rootTree.
	 */
	public void setRoot(final Octree rootTree) {
		this.root = rootTree;
	}

	/**
	 * Sets the parent of the octree to parentTree.
	 */
	public void setParent(final Octree parentTree) {
	}

	/**
	 * Sets the data set of the octree to data.
	 */
	public void setData(final OctreeDataItem[] data) {
		this.dataItems = data;
	}

	/**
	 * Sets the leaf flag for this tree.
	 */
	private void setLeafFlag(final boolean leafFlag) {
		this.leaf = leafFlag;
	}

	/**
	 * Sets the cutoff number of elements for assigning a leaf status to a child
	 * in this tree.
	 */
	public void setLeafCutOffCriterion(final int leafCutOff) {
		this.leafCutOff = leafCutOff;
	}

	//
	// Get methods.
	//

	/**
	 * Return an array of all (potential) covalent bonds. Potential bond is a
	 * bond that satisfy the distance criterion (i.e. distance less than
	 * cutOff).
	 */
	public Object[] getBonds(final float cutOff) {
		final Vector bondVector = new Vector();
		if (this.dataItems != null) {
			for (int i = 0; i < this.dataItems.length; i++) {
				this.appendBonds(this.dataItems[i], cutOff, bondVector);
			}
		} else {
			// System.out.println( "This doesn't seem to be a root tree, No data
			// found " );
			// TODO: Perhaps this should throw an exception?
		}

		bondVector.trimToSize();
		return bondVector.toArray();
	}

	//
	// Get methods.
	//

	/**
	 * Return the vector of all (potential) covalent bonds. Potential bond is a
	 * bond that satisfy the distance criterion (i.e. distance less than
	 * cutOff).
	 */
	public Vector getBondsVector(final float cutOff) {
		final Vector bondVector = new Vector();
		if (this.dataItems != null) {
			for (int i = 0; i < this.dataItems.length; i++) {
				this.appendBonds(this.dataItems[i], cutOff, bondVector);
			}
		} else {
			// System.out.println( "This doesn't seem to be a root tree, No data
			// found " );
			// TODO: Perhaps this should throw an exception?
		}
		bondVector.trimToSize();
		return bondVector;
	}

	/**
	 * Return an array of all (potential) hydrogen bonds. Potential bond is a
	 * bond that satisfy the distance criterion (i.e. distance less than
	 * cutOff). The indexCutOff criterion is different than the one for covalent
	 * bonds to correctly select the hydrogen bonds along the backbone in the
	 * definition of secondary structures in the Kabsch-Sander algorithm.
	 */
	public Object[] getHBonds(final float cutOff) {
		final Vector bondVector = new Vector();
		if (this.dataItems != null) {
			for (int i = 0; i < this.dataItems.length; i++) {
				this.appendHBonds(this.dataItems[i], cutOff, 2, bondVector);
			}
		} else {
			// System.out.println( "This doesn't seem to be a root tree, No data
			// found " );
			// TODO: Perhaps this should throw an exception?
		}

		bondVector.trimToSize();
		return bondVector.toArray();
	}

	/**
	 * Return the vector of all (potential) hydrogen bonds. Potential bond is a
	 * bond that satisfy the distance criterion (i.e. distance less than
	 * cutOff). The indexCutOff criterion is different than the one for covalent
	 * bonds to correctly select the hydrogen bonds along the backbone in the
	 * definition of secondary structures in the
	 */
	public Vector getHBondsVector(final float cutOff) {
		final Vector bondVector = new Vector();
		if (this.dataItems != null) {
			for (int i = 0; i < this.dataItems.length; i++) {
				this.appendHBonds(this.dataItems[i], cutOff, 2, bondVector);
			}
		} else {
			// System.out.println( "This doesn't seem to be a root tree, No data
			// found " );
			// TODO: Perhaps this should throw an exception?
		}

		return bondVector;
	}

	/**
	 * Return the vector of all (potential) hydrogen bonds in a format used by
	 * the Kabsch-Sander algorithm implemented by the DerivedInformation class.
	 * Potential bond is a bond that satisfy the distance criterion (i.e.
	 * distance less than cutOff). The indexCutOff criterion is different than
	 * the one for covalent bonds to correctly select the hydrogen bonds along
	 * the backbone in the definition of secondary structures in the
	 */
	public Vector getHBondInfoVector(final double cutOff) {
		final Vector bondList = new Vector();
		double[] firstCoord = new double[this.dimension];
		int firstIndex;

		// System.out.println ( "Detecting bonds at a cutoff of " + cutOff );
		if (this.dataItems != null) {
			for (int i = 0; i < this.dataItems.length; i++) {
				firstCoord = this.dataItems[i].getCoordinate();
				firstIndex = this.dataItems[i].getIndex();
				this.appendHBondInfo(firstCoord, firstIndex, cutOff, 2, bondList);
			}
		} else {
			// System.out.println( "This doesn't seem to be a root tree, No data
			// found " );
			// TODO: Perhaps this should throw an exception?
		}
		return bondList;
	}

	/**
	 * 
	 */
	private void appendHBondInfo(final double[] coords1, final int index1, final double cutOff,
			final int indexCutOff, final Vector bondList) {
		Octree.countAppBondOp++;
		double distance;
		int index2;
		distance = this.getDistance(coords1, this.mid);
		distance -= this.cellRadius;
		if (distance <= cutOff) {
			// System.out.println( "Distance in " + path + " " + distance );
			if (this.leaf) {
				// System.out.println( "Leaf with " + dataItems.length + "
				// components" );
				for (int j = 0; j < this.dataItems.length; j++) {
					if (this.getDistance(this.dataItems[j].getCoordinate(), coords1) <= cutOff) {
						// System.out.println( "Appending bonds in " + path );
						index2 = this.dataItems[j].getIndex();
						if (index1 < index2 - indexCutOff) {
							bondList.add(new BondInfo(index1, this.dataItems[j]
									.getIndex()));
						}
					}
				}
			} else {
				for (int child = 0; child < this.maxNumberOfChildren; child++) {
					if (this.children[child] != null) {
						this.children[child].appendHBondInfo(coords1, index1,
								cutOff, indexCutOff, bondList);
					}
				}
			}
		} else {
			// root.rejectedPaths++;
			// System.out.println( "Rejecting Path: " + path );
			return;
		}
		return;
	}

	/**
	 * 
	 */
	private void appendBonds(final OctreeDataItem dataItem, final double cutOff,
			final Vector bondVector) {
		Octree.countAppBondOp++;
		double distance;
		int index1, index2;

		final double[] coords1 = dataItem.getCoordinate();
		index1 = dataItem.getIndex();
		distance = this.getDistance(coords1, this.mid);
		distance -= this.cellRadius;

		if (distance > cutOff) {
			return;
		}

		if (this.leaf) {
			for (int j = 0; j < this.dataItems.length; j++) {
				if (this.getDistance(this.dataItems[j].getCoordinate(), coords1) <= cutOff) {
					index2 = this.dataItems[j].getIndex();
					if (index1 < index2) {
						bondVector.add(new Bond(((OctreeAtomItem) dataItem)
								.getAtom(), ((OctreeAtomItem) this.dataItems[j])
								.getAtom()));
					}
				}
			}
		} else {
			for (int child = 0; child < this.maxNumberOfChildren; child++) {
				if (this.children[child] != null) {
					this.children[child].appendBonds(dataItem, cutOff, bondVector);
				}
			}
		}
	}

	/**
	 * 
	 */
	private void appendHBonds(final OctreeDataItem dataItem, final double cutOff,
			final int indexCutOff, final Vector bondVector) {
		Octree.countAppBondOp++;
		double distance;
		int index1, index2;

		final double[] coords1 = dataItem.getCoordinate();
		index1 = dataItem.getIndex();
		distance = this.getDistance(coords1, this.mid);
		distance -= this.cellRadius;

		if (distance > cutOff) {
			return;
		}

		if (this.leaf) {
			for (int j = 0; j < this.dataItems.length; j++) {
				if (this.getDistance(this.dataItems[j].getCoordinate(), coords1) <= cutOff) {
					index2 = this.dataItems[j].getIndex();
					if (index1 < index2 - indexCutOff) {
						bondVector.add(new Bond(((OctreeAtomItem) dataItem)
								.getAtom(), ((OctreeAtomItem) this.dataItems[j])
								.getAtom()));
					}
				}
			}
		} else {
			for (int child = 0; child < this.maxNumberOfChildren; child++) {
				if (this.children[child] != null) {
					this.children[child].appendHBonds(dataItem, cutOff, indexCutOff,
							bondVector);
				}
			}
		}
	}

	/**
	 * Return the dimensionality of this tree.
	 */
	public int getDimension() {
		return this.dimension;
	}

	/**
	 * Return the maximum number of children.
	 */
	public int getMaxNumberOfChildren() {
		return this.maxNumberOfChildren;
	}

	/**
	 * Return the actual number of children
	 */
	public int getNumberOfChildren() {
		return this.numberOfChildren;
	}

	/**
	 * Return the weight of the tree.
	 */
	public int getWeight() {
		return this.weight;
	}

	/**
	 * Print the child naming scheme. Not yet implemented
	 */
	public void printChildNamingScheme() {
		// TODO
	}
}
