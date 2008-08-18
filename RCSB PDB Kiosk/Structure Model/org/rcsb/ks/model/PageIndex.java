package org.rcsb.ks.model;

public class PageIndex {
	/**
	 *  Represents the scope of the page ranges 
	 */
	public final static String GLOBAL = "default";

	
	private int startPage = 0;
	private int stopPage = 0;
	
	public PageIndex ( int _firstPage )
	{
		startPage = _firstPage;
	}
	public int getStartPage ()
	{
		return startPage;
	}
	public int getStopPage ()
	{
		return stopPage;
	}
	public void setStartPage ( int _startPage )
	{
		startPage = _startPage;
	}
	public void setStopPage ( int _stopPage )
	{
		stopPage = _stopPage;
	}
	
	
}
