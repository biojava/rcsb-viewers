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
package org.rcsb.ks.model;

import java.util.ArrayList;
import java.util.HashMap;

public class JournalIndex {

	
	public enum Source
	{
		PEER_REVIEWED,
		POPULAR_SCIENCE_NONPEER_REVIEWED,
		NEWS_ARTICLE
	}
	
	/**
	 *  What type of journal is this?
	 */
	private Source source = null;
	private String journalName = "";
	private String paper_title = "Unknown";
	
	// {{ list of available pages }}

	private ArrayList<String> authors = new ArrayList<String> ();
	private HashMap pageRanges = new HashMap ();
	private String abbreviation = "";
	private String volume = "";
	private String pubMedId = "";
	private int year = 1990;
	
	public JournalIndex() {
	}
	public void setTitle(String _trim) {
		paper_title = _trim;
	}
	public void setJournalAbbreviation(String _trim) {
		abbreviation = _trim;
		
	}
	public void setJournalVolume(String _volume) {
		volume  = _volume;
	}
	public void setFirstPage(int _firstPage) {
		Object o = pageRanges.get( PageIndex.GLOBAL );
		if ( o == null ){		
		PageIndex pageIndex = new PageIndex ( _firstPage );		
		pageRanges.put( PageIndex.GLOBAL, pageIndex );		
		}else 
		{
			PageIndex pageIndex = (PageIndex)o;
			pageIndex.setStartPage( _firstPage );
			pageRanges.put( PageIndex.GLOBAL, pageIndex );		
		}
	}
	public void setPubMed ( String _pubMedId )
	{
		pubMedId  = _pubMedId;
	}
	public void setLastPage(int _lastPage) {
		Object o = pageRanges.get( PageIndex.GLOBAL );
		if ( o == null ){		
		PageIndex pageIndex = new PageIndex ( _lastPage );		
		pageRanges.put( PageIndex.GLOBAL, pageIndex );		
		}else 
		{
			PageIndex pageIndex = (PageIndex)o;
			pageIndex.setStartPage( _lastPage );
			pageRanges.put( PageIndex.GLOBAL, pageIndex );		
		}		
	}
	public void setYear(int _year) {
		year  = _year;
	}
	public void setAuthors(ArrayList _authors) {
		authors = _authors;
	}
	public String getTitle() {
		return paper_title;
	}
	public int getYear() {
		return year;
	}
	public String getJournalInfo() {
		return "" + abbreviation + ", vol: " + volume;
	}
}
