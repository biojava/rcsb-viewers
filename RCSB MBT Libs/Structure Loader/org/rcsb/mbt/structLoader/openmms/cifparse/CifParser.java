//
// CifParser.java,v 1.3 2001/10/27 02:02:47 dsg Exp
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

import java.io.*;
import java.util.*;

/**
 * Central core of the mmCIF parser. This class calls the mmCIF
 * Tokenizer and uses the resulting tokens to parse the dictionary
 * and data files.
 * The data values read are passed to the Builder interface for
 * further processing or storage.
 *
 * @author Douglas S. Greer
 * @version 1.3
 */
public class CifParser
{
  DictionaryItem di = null;
  DictionaryCategory dcat = null;

  Builder buildr;

  // possible values for saveState
  static final int SAVE_CLOSED = 1;
  static final int SAVE_CATEGORY = 2;
  static final int SAVE_ITEM = 3;

  // possible values for loopState
  static final int LOOP_CLOSED = 1;
  static final int LOOP_READING_ITEM_LIST = 2;
  static final int LOOP_READING_VALUES = 3;

  public CifParser()
    {
    }

  public void setBuilder(final Builder bu)
    {
      this.buildr = bu;
    }

  public Builder getBuilder()
    {
      return this.buildr;
    }

  public void readDictionary(final CifTokenizer ct, final CifDictionary cd,
			     final boolean parseAll)
    throws CifParseException
    {
      try
	{
	  String token;
	  String child = null;
	  int loopState = CifParser.LOOP_CLOSED;
	  int saveState = CifParser.SAVE_CLOSED;
	  boolean saveEqualsLoop = false; // true if loop item = save name
	  int nLoopItem = 0;  // total number of data items in this loop
	  int iLoopItem = 0;  // index for data item read-value iteration

	  final ArrayList loopItem = new ArrayList();

	  while((token = ct.getToken()) != null)
	    {
	      if (token.startsWith("save_"))
		{
		  if (saveState == CifParser.SAVE_CLOSED)
		    {
		      // "save_" found outside of a save block
		      //  --> begin a new save block
		      final String saveName = token.substring(5);
		      saveState = this.startNewSaveBlock(ct, saveName,
						    cd, parseAll);
		    }
		  else
		    {		    
		      // "save_" found inside of a save block
		      //  --> end the save block
		      if (loopState == CifParser.LOOP_READING_ITEM_LIST)
			{
			  throw new CifParseException(
			      "readDictionary: No data inside 'save_' block");
			}
		      if (loopState == CifParser.LOOP_READING_VALUES)
			{
			  if (iLoopItem != 0)
			    {
			      throw new CifParseException
				("readDictionary: end of 'save_'"
				 + " block unexpected"
				 + "\n\tItem number: " + iLoopItem
				 + " of "+ nLoopItem
				 + "\n\t<Token>= " + token
				 + "<Next Token>= "+ ct.getToken());
			    }
			  loopState = CifParser.LOOP_CLOSED;
			  nLoopItem = 0;
			}
		      saveState = CifParser.SAVE_CLOSED;
		    }
		  continue;
		}

	      if (saveState != CifParser.SAVE_CLOSED)
		{
		  // process the data inside the save block
		  if (token.compareTo("loop_") == 0)
		    {
		      if (loopState == CifParser.LOOP_CLOSED)
			{
			  // "loop_" found outside of a loop
			  // --> begin a loop and read item names
			  loopState = CifParser.LOOP_READING_ITEM_LIST;
			  iLoopItem = 0;
			  nLoopItem = 0;
			  loopItem.clear();
			  continue;
			}
		      if (loopState == CifParser.LOOP_READING_ITEM_LIST)
			{
			  throw new CifParseException(
			      "readDictionary: loop contains no values");
			}
		      if (loopState == CifParser.LOOP_READING_VALUES)
			{
			  if (iLoopItem != 0)
			    {
			      throw new CifParseException(
			  "readDictionary: incomplete value inside loop"
			  + "\n\tItem number: " + iLoopItem
			  + " of "+ nLoopItem
			  + "\n\t<Token>= " + token
			  + "<Next Token>= "+ ct.getToken());
			    }
			  // "loop_" found inside of a loop and a
			  // multiple of nLoopItems have been read
			  // --> start a *new* loop
			  loopState = CifParser.LOOP_READING_ITEM_LIST;
			  nLoopItem = 0;
			  loopItem.clear();
			  continue;
			}
		    } // end "_loop" found
		
		  if (loopState == CifParser.LOOP_READING_ITEM_LIST)
		    {
		      if (ct.tokenIsName) 
			{	
			  loopItem.add(token);
			  nLoopItem++;
			}
		      else
			{
			  // first "non-name" denotes the beginning
			  // of the value list.
			  loopState = CifParser.LOOP_READING_VALUES;
			}
		    }
		  if (loopState == CifParser.LOOP_READING_VALUES)
		    {
		      if (ct.tokenIsName) 
			{	
			  if (iLoopItem != 0)
			    {
			      throw new CifParseException
				("readDictionary: Name found where value "
				 + "expected, inside 'loop_'"
				 + "\n\tItem number: " + iLoopItem
				 + " of "+ nLoopItem
				 + "\n\t<Token>= " + token
				 + "<Next Token>= "+ ct.getToken());
			    }
			  loopState = CifParser.LOOP_CLOSED;
			  nLoopItem = 0;
			  loopItem.clear();
			}
		      else
			{
			  if (nLoopItem == 0)
			    {
			      throw new CifParseException
				("readDictionary: No Item List inside loop"
				 + "\n\tItem number: " + iLoopItem
				 + " of "+ nLoopItem
				 + "\n\t<Token>= " + token
				 + "<Next Token>= "+ ct.getToken());
			    }
			  if (saveState == CifParser.SAVE_ITEM && 
			      ((String)loopItem.get(iLoopItem))
			      .compareTo("_item.name") == 0 &&
			      token.equals(this.di.getItemName()))
				{
				  saveEqualsLoop = true;
				}
			  if (saveEqualsLoop &&
			      ((String)loopItem.get(iLoopItem))
			      .compareTo("_item.mandatory_code") == 0)
			    {
			      if (this.di != null)
				{
				  this.di.setItemMandatoryCode(token);
				}
			    }
			  else if (saveEqualsLoop &&
			      ((String)loopItem.get(iLoopItem))
			      .compareTo("_item_type.code") == 0)
			    {
			      if (this.di != null)
				{
				  this.di.setItemType(token);
				}
			    }
			  else if (saveEqualsLoop &&
			      ((String)loopItem.get(iLoopItem))
			      .compareTo("_item_default.value") == 0)
			    {
			      if (this.di != null)
				{
				  this.di.setItemDefaultValue(token);
				}
			    }
			  else if (((String)loopItem.get(iLoopItem))
			      .compareTo("_item_linked.child_name") == 0)
			    {
			      if (child != null)
				{
				  throw new CifParseException
				    ("readDictionary: parent/child fault");
				}
			      else
				{
				  child = token;
				}
			    }
			  else if (((String)loopItem.get(iLoopItem))
			      .compareTo("_item_linked.parent_name") == 0)
			    {
			      if (child != null)
				{
				  cd.setParent(child, token);
				  child = null;
				}
			      else
				{
				  throw new CifParseException
				    ("readDictionary: no child index");
				}
			    }
			  iLoopItem++;
			  if (iLoopItem == nLoopItem) 
			    {
			      iLoopItem = 0;
			      saveEqualsLoop = false;
			    }
			}
		    }
		  if (loopState == CifParser.LOOP_CLOSED)
		    {
		      if (saveState == CifParser.SAVE_CATEGORY && 
			  token.compareTo("_category.description") == 0) 
			{
			  token = ct.getToken();
			  if (this.dcat != null)
			    {
			      this.dcat.setCategoryDescription(token);
			    }
			}
		      else if (saveState == CifParser.SAVE_CATEGORY && 
			  token.compareTo("_category.mandatory_code") == 0) 
			{
			  token = ct.getToken();
			  if (this.dcat != null)
			    {
			      this.dcat.setCategoryMandatoryCode(token);
			    }
			}
		      else if (saveState == CifParser.SAVE_ITEM && 
			  token.compareTo("_item.name") == 0) 
			{
			  token = ct.getToken();
			  if (token != null &&
			      ! token.equalsIgnoreCase(this.di.getItemName()))
			      {
				throw new CifParseException
				  ("_item.name does not match _save name"
				   + "\n\t_item.name= " + token
				   + "\n\t_save name= " + this.di.getItemName());
			      }
			}
		      else if (saveState == CifParser.SAVE_ITEM &&
			  token.compareTo("_item.mandatory_code") == 0)
			{
			  token = ct.getToken();
			  if (this.di != null)
			    {
			      this.di.setItemMandatoryCode(token);
			    }
			}
		      else if (saveState == CifParser.SAVE_ITEM && 
			  token.compareTo("_item_type.code") == 0)
			{
			  token = ct.getToken();
			  if (this.di != null)
			    {
			      this.di.setItemType(token);
			    }
			}
		      else if (saveState == CifParser.SAVE_ITEM && 
			  token.compareTo("_item_default.value") == 0) 
			{
			  token = ct.getToken();
			  if (this.di != null)
			    {
			      this.di.setItemDefaultValue(token);
			    }
			}
		      else if (saveState == CifParser.SAVE_ITEM &&
			  token.compareTo
			  ("_item_description.description") == 0)
			{
			  token = ct.getToken();
			  if (this.di != null)
			    {
			      this.di.setItemDescription(token);
			    }
			}
		    }
		}
	    }
	}
      catch(final IOException e)
	{
	  System.out.println("IOException");
	  System.exit(1);
	}
    }

  public int startNewSaveBlock(final CifTokenizer ct, final String saveName,
			       final CifDictionary cd, final boolean parseAll)
    throws CifParseException, IOException
    {
      int svs = CifParser.SAVE_CLOSED;
      final boolean isUpperCase = saveName.equals(saveName.toUpperCase());
      
      if (saveName == null || saveName.length() == 0)
	{
	  throw new CifParseException( "startNewSaveBlock: no block name");
	}

      // The Cif dictionary uses the convention that
      // Category names are uppercase and Item names
      // are lower case.
      if (isUpperCase)
	{
	  // uppercase Category
	  if (parseAll)
	    {
	      if (cd.getDictionaryCategoryList()
		  .isAlreadyDefined(saveName))
		{
		  this.dcat = null;
		}
	      else
		{
		  this.dcat = new DictionaryCategory(saveName);
		  cd.getDictionaryCategoryList().add(this.dcat);
		}
	    }
	  else
	    {
	      this.dcat = cd.getDictionaryCategoryList()
		.findDictionaryCategory(saveName);
	    }
	  if (this.dcat != null)
	    {
	      svs = CifParser.SAVE_CATEGORY;
	      this.dcat.definitionFound = true;
	    }
	}
      else
	{
	  // lowercase DataItem
	  if (parseAll)
	    {
	      if (cd.getDictionaryItemList()
		  .isAlreadyDefined(saveName))
		{
		  this.di = null;
		}
	      else
		{
		  this.di = new DictionaryItem(saveName);
		  cd.getDictionaryItemList().add(this.di);
		}
	    }
	  else
	    {
	      this.di = cd.getDictionaryItemList()
		.findDictionaryItem(saveName);
	    }
	  if (this.di != null)
	    {
	      svs = CifParser.SAVE_ITEM;
	      this.di.definitionFound = true;
	    }
	}
      
      if (svs == CifParser.SAVE_CLOSED)
	{
	  // Since this block is not used, 
	  // for scanning efficiency gobble
	  // up the remainder
	  String token;
	  while((token = ct.getToken()) != null)
	    {
	      if (token.startsWith("save_"))
		{
		  break;
		}
	    }
	}
      return svs;
    }

  //
  // Uses "Builder" pattern to store data as it is read
  // ( ref: "Design Patterns" by Gamma et. al.) 
  //
  public void readDataBlock(final CifTokenizer ct, final DataItemList dil)
    throws CifParseException
    {									
      final DataItemList loopList = new DataItemList();
      String token;							

      try
	{
	  boolean itemOpen = false;
	  if (this.buildr == null)
	    {
	      throw new CifParseException(
		  "readDataBlock: No Builder Installed");
	    }
	  int loopState = CifParser.LOOP_CLOSED;
	  DataItem dloopItem, d;
	  DataItem lastRead = null;	
	  DataItem it = null;
	  int nLoopItem = 0;
	  int iLoopItem = 0;					

	  // Start the builder going ...
	  this.buildr.beginCompound();
	  while(true)
	    {
	      token = ct.getToken();
	      if(token ==  null)
		{
		  if (loopState == CifParser.LOOP_READING_ITEM_LIST)
		    {
		      throw new CifParseException
			("readDataBlock: premature EOF inside loop"
			 + this.itemErrorMsg(nLoopItem, lastRead));
		    }
		  else if (loopState == CifParser.LOOP_READING_VALUES)
		    {
		      if (iLoopItem != 0)
			{
			  throw new CifParseException
			    ("readDataBlock: EOF found while reading values"
			  + this.itemErrorMsg(nLoopItem, lastRead));
			}
		      loopState = CifParser.LOOP_CLOSED;
		      this.buildr.endLoop();
		    }
		  // ok we're finished
		  break;
		}
	      if (token.startsWith("data_"))
		{
		  continue; // JLM DEBUG - Ignore "data_" lines.
		/*
		  // only ignore the first one, later ones may be 
		  // item value strings that begin with "data"
		  if (! inDataBlock)
		    {
		      inDataBlock = true;
		      continue;
		    }
		*/
		}
	      if (token.compareTo("loop_") == 0)
		{
		  if (itemOpen)
		    {
		      throw new CifParseException(
		  "readDataBlock: 'loop_' statement inside item definition");
		    }
		
		  if (loopState == CifParser.LOOP_CLOSED)
		    {
		      loopState = CifParser.LOOP_READING_ITEM_LIST;
		      iLoopItem=0;
		      nLoopItem = 0;
		      loopList.clear();
		      continue;
		    }
		  if (loopState == CifParser.LOOP_READING_ITEM_LIST)
		    {
		      throw new CifParseException
			("readDataBlock: loop contains no values"
			 + this.itemErrorMsg(nLoopItem, lastRead));
		    }
		  if (loopState == CifParser.LOOP_READING_VALUES)
		    {
		      if (iLoopItem != 0)
			{
			  throw new CifParseException
		    ("readDataBlock: incomplete set of values inside loop"
			     + this.itemErrorMsg(nLoopItem, lastRead));
			}
		      loopState = CifParser.LOOP_READING_ITEM_LIST;
		      nLoopItem = 0;
		      loopList.clear();
		      // New loop statement while reading vaulues
		      // => done with this list set
		      this.buildr.endLoop();
		      continue;
		    }
		}

	      if (loopState == CifParser.LOOP_READING_ITEM_LIST)
		{
		  if (ct.tokenIsName) 
		    {	
		      dloopItem = dil.findDataItem(token);
		      loopList.add(dloopItem);
		      if (nLoopItem == 0)
			{
			  this.buildr.beginLoop();
			}
		      this.buildr.setLoopItem(nLoopItem, dloopItem);
		      nLoopItem++;
		    }
		  else
		    {
		      // Not a "_" Name -> Must be the start of the values
		      loopState = CifParser.LOOP_READING_VALUES;
		    }
		}

	      if (loopState == CifParser.LOOP_READING_VALUES)
		{
		  if (ct.tokenIsName) 
		    {	
		      if (iLoopItem != 0)
			{
			  throw new CifParseException(
		  "readDataBlock: Item name found while reading values"
			  + this.itemErrorMsg(nLoopItem, lastRead));
			}
		      loopState = CifParser.LOOP_CLOSED;
		      // "_" Name while reading values -> must be done
		      this.buildr.endLoop();
		    }
		  else
		    {
		      if (nLoopItem == 0)
			{
			  throw new CifParseException(
			      "readDataBlock: No Item List in loop");
			}
		      // first item marks beginning of a row
		      if (iLoopItem == 0)
			{
			  // Note: beginRow() and endRow() will be called 
			  // even when there are no dataItems of
			  // interest (i.e. non-null dataItems).
			  // The builder must check for this condition
			  this.buildr.beginRow();
			}
		      d = loopList.elementAt(iLoopItem);
		      lastRead = d;
		      if (d != null)
			{
			  // convert and insert value
			  this.buildr.insertLoopValue(iLoopItem, d, token);
			}
		      iLoopItem++;
		      // last item marks end of a row
		      if (iLoopItem == nLoopItem)
			{
			  iLoopItem = 0;
			  this.buildr.endRow();
			}
		      continue;
		    }
		}
	    
	      if (loopState == CifParser.LOOP_CLOSED)
		{
		  if (ct.tokenIsName) 
		    {
		      if (itemOpen)
			{
			  throw new CifParseException
		    ("readDataBlock: Item name found where value expected");
			}
		      it = dil.findDataItem(token);
		      lastRead = it;
		      if (it != null)
			{
			  this.buildr.setSingleItem(it);
			}
		      itemOpen = true;
		      continue;
		    }
		  else
		    {
		      if (!itemOpen)
			{
			  throw new CifParseException(
 		      "readDataBlock: Item value found where name expected"
			      + this.itemErrorMsg(nLoopItem, lastRead)
			      + "\n\t<Token>= " + token);
			}
		      if (it != null) 
			{
			  // convert and insert value
			  this.buildr.insertSingleValue(it, token);
			}
		      itemOpen = false;
		      continue;
		    }
		}
	    }
	}
      catch(final IOException e)
	{
	  System.out.println("IOException");
	  System.exit(1);
	}
      this.buildr.endCompound();
    }

  protected String itemErrorMsg(final int n, final DataItem d)
    {
      String rv;
      if (n > 0)
	{
	  if (d != null)
	    {
	      rv = d.getItemName();
	    }
	  else
	    {
	      rv = "(Unused DataItem)";
	    }
	}
      else
	{
	  rv = "No DataItems read in this category";
	}
      return "\n\tLast DataItem read: " + rv;
    }
}
