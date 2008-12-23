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
 * Stores and manages information about a single cif category type.
 *
 * @author Douglas S. Greer
 * @version 1.3
 */
public class DictionaryCategory
{
  public boolean definitionFound = false;
  public static final int CATEGORY_MANDATORY_CODE_UNDEFINED = 1;
  public static final int CATEGORY_MANDATORY_CODE_YES = 2;
  public static final int CATEGORY_MANDATORY_CODE_NO = 3;

  // dictionary part
  private String categoryName;
  private String categoryDescription;
  private int mandatoryCode = DictionaryCategory.CATEGORY_MANDATORY_CODE_UNDEFINED;
  private Object uObj;

  public DictionaryCategory(final String cname)
    {
      this.categoryName = cname;
    }

  public String getCategoryName()
    {
      return this.categoryName;
    }

  public void setCategoryDescription(final String desc)
    {
      this.categoryDescription = desc;
    }

  public String getCategoryDescription()
    {
      return this.categoryDescription;
    }

  public void setCategoryMandatoryCode(final String code)
    {
      if (code.compareTo("yes") == 0)
	{
	  this.mandatoryCode = DictionaryCategory.CATEGORY_MANDATORY_CODE_YES;
	}
      else if (code.compareTo("no") == 0)
	{
	  this.mandatoryCode = DictionaryCategory.CATEGORY_MANDATORY_CODE_NO;
	}
      else
	{
	  this.mandatoryCode = DictionaryCategory.CATEGORY_MANDATORY_CODE_UNDEFINED;
	}
    }

  public int getCategoryMandatoryCode()
    {
      return this.mandatoryCode;
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
