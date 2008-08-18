package org.rcsb.ks.model;

public class DisplayInformation {

	private String title = "unknown";
	
	public DisplayInformation ( String _title )
	{
		title = _title;
	}
	public String getTitle ()
	{
		return title;
	}
	public void setTitle(String _title) {
		title = _title;
//		System.out.println (" title: " + title);
	}
}
