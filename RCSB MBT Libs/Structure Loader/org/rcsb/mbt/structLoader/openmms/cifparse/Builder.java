//
// Builder.java,v 1.3 2001/10/27 02:02:47 dsg Exp
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
 * The "Builder" interface used by the mmCIF parser.
 * Applications implement this interface in a class that will
 * then receive the data as it is read and parsed from an mmCIF
 * data file.
 * <br>
 * The adjective "single" is used in Category Loaders and Builders
 * to mean Non-loop, i.e. values specified in a CIF file outside of
 * _loop statements
 *
 * @author Douglas S. Greer
 * @version 1.3
 */
public interface Builder
{
  public void beginCompound()
    throws CifParseException;
  public void endCompound()
    throws CifParseException;
  public void setSingleItem(DataItem di)
    throws CifParseException;
  public void insertSingleValue(DataItem d, String val)
    throws CifParseException;
  public void beginLoop()
    throws CifParseException;
  public void endLoop()
    throws CifParseException;
  public void beginRow()
    throws CifParseException;
  public void endRow()
    throws CifParseException;
  public void setLoopItem(int column, DataItem di)
    throws CifParseException;
  public void insertLoopValue(int column, DataItem d, String val)
    throws CifParseException;
}
