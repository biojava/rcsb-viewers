//
// DictionaryCategoryList.java,v 1.3 2001/10/27 02:02:47 dsg Exp
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

import java.util.*;

/**
 * Stores the list of categories found when parsing a cif dictionary.
 *
 * @author Douglas S. Greer
 * @version 1.3
 */
public class DictionaryCategoryList
{
/**
 * The HashMap "hm" stores all the DictionaryCategories put in this
 * DictionaryCategory List with the add() method and
 * serves as a quick lookup to find a particular
 * DictionaryCategory by name (String value). 
 */
  public HashMap hm;

  public DictionaryCategoryList()
    {
      this.hm = new HashMap();
    }
  
  public void add(final String iname)
    {
      this.add(new DictionaryCategory(iname));
    }
  
  public void add(final DictionaryCategory item)
    {
      if (item == null)
	{
	  this.hm.put(item, item);
	}
      else
	{
	  this.hm.put(item.getCategoryName().toLowerCase(), item);
	}
    }

  public DictionaryCategory findDictionaryCategory(final String iname)
    {
      Object o;
      o = this.hm.get(iname.toLowerCase());
      if (o == null)
	{
	  return null;
	}
      else
	{
	  return (DictionaryCategory) o;
	}
    }

  public boolean isAlreadyDefined(final String iname)
    {
      return this.hm.containsKey(iname.toLowerCase());
    }

  public void checkAllItemsDefined()
    throws CifParseException
    {
      DictionaryCategory d;
      final Iterator i = this.hm.values().iterator();

      while (i.hasNext())
	{
	  d = (DictionaryCategory) i.next();
	  if ( ! d.definitionFound)
	    {
	      throw new CifParseException(d.getCategoryName()
			  + " is not defined in dictionary");
	    }
	}
    }

  public void clear()
    {
      this.hm.clear();
    }

  // for statistics reporting
  public Map getMap()
    {
      return this.hm;
    }
}
