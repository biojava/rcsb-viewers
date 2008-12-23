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

import java.util.*;

/**
 * Stores and manages the list of Data Items defined in a cif
 * dictionary.
 * <br>
 * There may be multiple null objects in the ArrayList, which are
 * stored with a null DataItem index; e.g. when a data file loop
 * is read that contains multiple unwanted value fields.
 * The HashMap will only keep a pointer (Integer) to
 * the last one stored;  but since the elementAt() method will
 * return null for all the indicies stored with null values
 * this doesn't matter.
 *
 * @author Douglas S. Greer
 * @version 1.3
 */
public class DataItemList
{
  /**
   * The ArrayList "di" stores all the DataItems put in this
   * DataItem List with the add() method.
   */
  public ArrayList di;

  /**
   * The HashMap "hm" serves as a quick lookup to find a particular
   * DataItem by name (String value).  The HashMap stores an
   * Integer object which is the index into the ArrayList di.
   */
  public HashMap hm;

  public DataItemList()
    {
      this.hm = new HashMap();
      this.di = new ArrayList();
    }
  
  public void add(final String iname)
    {
      this.add(new DataItem(iname));
    }
  
  public void add(final DataItem item)
    {
      final int s = this.di.size();
      this.di.add(item);
      if (item == null)
	{
	  this.hm.put(item, new Integer(s));
	}
      else
	{
	  this.hm.put(item.getItemName().toLowerCase(), new Integer(s));
	}
    }

  public int size()
    {
      return this.di.size();
    }

  public DataItem elementAt(final int i)
    {
      return (DataItem) this.di.get(i);
    }

  public DataItem findDataItem(final String iname)
    {
      Object o;
      if (iname == null)
	{
	  o = this.hm.get(iname);
	}
      else
	{
	  o = this.hm.get(iname.toLowerCase());
	}
      if (o == null)
	{
	  return null;
	}
      else
	{
	  return (DataItem) this.di.get(((Integer)o).intValue());
	}
    }

  public boolean isAlreadyDefined(final String iname)
    {
      return this.hm.containsKey(iname.toLowerCase());
    }

  public void clear()
    {
      this.hm.clear();
      this.di.clear();
    }

  public void printItems()
    {
      System.out.println("DataItemList.printItems()");
      for (int i = 0; i < this.size(); i++)
	{
	  System.out.println("\t" + this.elementAt(i).getItemName());
	}
    }
}
