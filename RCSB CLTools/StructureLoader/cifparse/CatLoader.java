//
// CatLoader.java,v 1.3 2001/10/27 02:02:47 dsg Exp
//
// Copyright 2001 The Regents of the University of California
// All Rights Reserved
//
// OpenMMS was developed by Dr. Douglas S. Greer at the San Diego
// Supercomputer Center, a research unit of the University of California,
// San Diego.  Support for this effort was provided by NSF through the
// Protein Data Bank (Grant DBI-9814284) and the National Partnership for
// Advanced Computational Infrastructure (Grant ACI-9619020)
//
// Permission to use, copy, modify and distribute any part of OpenMMS for
// educational, research and non-profit purposes, without fee, and
// without a written agreement is hereby granted, provided that the above
// copyright notice, this paragraph and the following paragraphs appear
// in all copies.
//
// Those desiring to incorporate this OpenMMS into commercial products or
// use for commercial purposes should contact the Technology Transfer
// Office, University of California, San Diego, 9500 Gilman Drive, La
// Jolla, CA 92093-0910, Ph: (619) 534-5815, FAX: (619) 534-7345.
//
// In no event shall the University of California be liable to any party
// for direct, indirect, special, incidental, or consequential damages,
// including lost profits, arising out of the use of this OpenMMS, even
// if the University of California has been advised of the possibility of
// such damage.
//
// The OpenMMS provided herein is on an "as is" basis, and the
// University of California has no obligation to provide maintenance,
// support, updates, enhancements, or modifications.  The University of
// California makes no representations and extends no warranties of any
// kind, either implied or express, including, but not limited to, the
// implied warranties of merchantability or fitness for a particular
// purpose, or that the use of the OpenMMS will not infringe any patent,
// trademark or other rights.

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
