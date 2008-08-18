package org.rcsb.ks.model;

import java.util.ArrayList;

public class PrimaryCitation {

	private JournalIndex journalIndex = null;

	private ArrayList authors = null;

	/**
	 * We're going to start with this.. but I know there is more we can add in
	 * the future. For example, I can see that Mesh terms may be applicable to
	 * this object...
	 */
	public PrimaryCitation(JournalIndex _journal, ArrayList _authors) {
		journalIndex = _journal;
		authors = _authors;
//		System.out.println (" the title of the priary citation is : " + journalIndex.getTitle () );
	}

	public ArrayList getAuthors() {
		return authors;
	}

	public JournalIndex getJournalIndex() {
		return journalIndex;
	}

	public String getAuthorsAsString() {

		if ( authors == null || authors.size() <= 0 )
			return "Unknown";
		String authorsList = "";
		for (int i = 0; i < authors.size(); i++) {
			String a = (String) authors.get(i);
			if (i < authors.size()-1) {
				authorsList += a + ", ";
			} else {
				authorsList += a;
			}
		}
		return authorsList;
	}

	public void setAuthors(ArrayList _authors) {
		authors = _authors;
	}

}
