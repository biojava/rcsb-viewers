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
 * Structure to hold information read from a cif dictionary
 *
 * @author Douglas S. Greer
 * @version 1.3
 */
public class CifDictionary
{
  private DictionaryCategoryList dcl;
  private DictionaryItemList dil;
  private Properties parents;
  
  public CifDictionary()
    {
      this.dcl = new DictionaryCategoryList();
      this.dil = new DictionaryItemList();
      this.parents = new Properties();
    }

  public DictionaryCategoryList getDictionaryCategoryList()
    {
      return this.dcl;
    }

  public DictionaryItemList getDictionaryItemList()
    {
      return this.dil;
    }

  public void checkAllItemsDefined()
    throws CifParseException
    {
      this.dcl.checkAllItemsDefined();
      this.dil.checkAllItemsDefined();
    }

  public boolean isCategory(final String s_)
    {
	  String s = s_;
      // ignore leading "_"
      // Category names do not begin with a "_"; Item names do. 
      if (s.charAt(0) == '_')
	  {
	    s = s.substring(1, s.length());
	  }
      return this.dcl.isAlreadyDefined(s);
    }

  public boolean isItem(final String s_)
    {
	  String s = s_;
      // ignore leading "_"
      // Item names begin with a "_", Category names do not.
      if (s.charAt(0) != '_')
	  {
	    s = "_" + s;
	  }
      return this.dil.isAlreadyDefined(s);
    }

  public void setParent(final String child, final String parent)
    {
      this.parents.setProperty(child, parent);
    }

  public String getParent(final String child)
    {
      return this.parents.getProperty(child);
    }
}
