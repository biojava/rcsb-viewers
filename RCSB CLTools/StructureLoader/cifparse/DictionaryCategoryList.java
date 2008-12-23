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
