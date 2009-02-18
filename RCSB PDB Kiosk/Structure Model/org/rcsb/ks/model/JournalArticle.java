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
import java.util.List;

/**
 * The JournalArticle class represents a literature reference.
 *
 * @author Peter Rose
 *
 */
public class JournalArticle {

	private String title = "";
	private List<String> authors = new ArrayList<String> ();
	private String abbreviation = "";
	private String volume = "";
	private int firstPage = -1;
	private int lastPage = -1;;
	private int year = -1;
	
	/**
	 * Sets title of the article.
	 * @param title title of the article
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Sets the abbreviated name of the journal.
	 * @param abbreviation abbreviated journal name
	 */
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
		
	}
	
	/**
	 * Sets the volume of the article.
	 * @param volume
	 */
	public void setJournalVolume(String volume) {
		this.volume  = volume;
	}
	
	/**
	 * Sets the first page of the article.
	 * @param firstPage first page of the article
	 */
	public void setFirstPage(int firstPage) {
		this.firstPage = firstPage;
	}
	
	/**
	 * Sets the last page for this article.
	 * @param lastPage
	 */
	public void setLastPage(int lastPage) {
		this.lastPage = lastPage;
	}
	
	/**
	 * Sets the publication year for this article.
	 * @param year year of publication
	 */
	public void setYear(int year) {
		this.year = year;
	}
	
	/**
	 * Sets the list of authors for this article.
	 * @param authors the authors of this article
	 */
	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}
	
	/**
	 * Append an author to the list of authors for this article.
	 * @param author the author to be appended
	 */
	public void appendAuthor(String author) {
		this.authors.add(author);
	}
	
	/**
	 * Gets the title of this article
	 * @return title of this article
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Gets a comma separated list of the authors of this article.
	 * If the number of authors exceeds the maximum number of
	 * specified authors, et al. is added at the end of the list.
	 * @param maxAuthorNames maximum number of authors to display
	 * @return
	 */
	public String getAuthorsAsString(int maxAuthorNames) {
		StringBuilder sb = new StringBuilder();

		int n = Math.min(maxAuthorNames, authors.size());
		for (int i = 0; i < n; i++) {
			sb.append(authors.get(i));
			if (i < authors.size()-1) {
				sb.append(", ");
			}
		}

		if (n < authors.size()) {
			sb.append("et al.");
		}
		return sb.toString();
	}

	/**
	 * Returns the journal reference of this article in standard form,
	 * for example: Nature 125, 234-246 (2001)
	 * @return journal reference in standard format
	 */
	public String getJournalReference() {
		StringBuilder sb = new StringBuilder();
		sb.append(abbreviation);
		if (volume != null) {
			sb.append(" " + volume);
		}
		if (firstPage > 0) {
			sb.append(", " + firstPage);
			if (lastPage > 0) {
				sb.append("-" + lastPage);
			}
		}
		if (year > 0) {
			sb.append(" (" + year + ")");
		}
		return sb.toString();
	}
}
