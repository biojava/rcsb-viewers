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
package org.rcsb.mbt.structLoader.openmms.cifparse;


/**
 * Base interface for all category loaders.  Methods in this interface
 * are called by the builder to pass values to specific categories.
 * For each category, a class that implements a CatLoader is used
 * to handle values parsed in the CIF file.
 *
 * The methods and their parameters are determined by the mmCIF
 * grammer and syntax, e.g. there is no beginLoop() method since the
 * category name is not known when the first "_loop" statement is
 * encountered.  The first defineLoopItem (or beginRow) call can
 * however be used a "beginLoop" method.
 *
 * The adjective "single" is used in Category Loaders and Builders
 * to mean Non-loop, i.e. values specified in a CIF file outside of
 * _loop statements.
 *
 * @author Douglas S. Greer
 * @version 1.3
 */
public interface CatLoader
{
  // BEGIN/END METHODS
  // beginCategory is called the first time this category is seen in
  // a CIF file
  public void beginCategory()
    throws CifParseException;
  // endCompund is called at the end of the CIF file for each category loaded
  public void endCompound(Object e)
    throws CifParseException;

  // SINGLE METHODS
  // defineSingleItem() is called when a non-loop variable name is encountered.
  public void defineSingleItem(Object e, int fieldCode)
    throws CifParseException;
  public void insertSingleValue(Object e, int fieldCode, String value)
    throws CifParseException;

  // LOOP METHODS
  // defineLoopItem() is called when a variable name is encountered in a loop.
  public void defineLoopItem(Object e, int fieldCode)
    throws CifParseException;
  public void beginRow()
    throws CifParseException;
  public void endRow()
    throws CifParseException;
  // insertLoopValue() is called for every data value in a loop.
  public void insertLoopValue(int fieldCode, String value)
    throws CifParseException;
  public void endLoop(Object e)
    throws CifParseException;
}
