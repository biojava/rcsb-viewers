//
// DictionaryItem.java,v 1.3 2001/10/27 02:02:47 dsg Exp
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
 * Stores and manages information about a single cif Item type.
 *
 * @author Douglas S. Greer
 * @version 1.3
 */
public class DictionaryItem extends DataItem
{
  public static final int ITEM_MANDATORY_CODE_UNDEFINED = 1;
  public static final int ITEM_MANDATORY_CODE_YES = 2;
  public static final int ITEM_MANDATORY_CODE_NO = 3;

  public boolean definitionFound = false;
  private String itemType;
  private String itemDescription;
  private int mandatoryCode = DictionaryItem.ITEM_MANDATORY_CODE_UNDEFINED;
  private Object uObj;

  public void setItemType(final String itype)
    {
      this.itemType = itype;
    }

  public String getItemType()
    {
      return this.itemType;
    }

  public void setItemDescription(final String idesc)
    {
      this.itemDescription = idesc;
    }

  public String getItemDescription()
    {
      return this.itemDescription;
    }

  public void setItemMandatoryCode(final String code)
    {
      if (code.compareTo("yes") == 0)
	{
	  this.mandatoryCode = DictionaryItem.ITEM_MANDATORY_CODE_YES;
	}
      else if (code.compareTo("no") == 0)
	{
	  this.mandatoryCode = DictionaryItem.ITEM_MANDATORY_CODE_NO;
	}
      else
	{
	  this.mandatoryCode = DictionaryItem.ITEM_MANDATORY_CODE_UNDEFINED;
	}
    }

  public int getItemMandatoryCode()
    {
      return this.mandatoryCode;
    }

  public DictionaryItem(final String iname)
    {
      super(iname);
    }

  public void setUObj(final Object obj)
    {
      this.uObj = obj;
    }

  public Object getUObj()
    {
      return this.uObj;
    }
}
