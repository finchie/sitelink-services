package uk.gov.snh.sitelink.services;

import java.util.List;

public class SearchResult {
	
	public int iTotalRecords;
	
	public int iTotalDisplayRecords;
	
	public List<List<String>> aaData;

	@Override
	public String toString() {
		return "{iTotalRecords=" + iTotalRecords + ", iTotalDisplayRecords=" + iTotalDisplayRecords
				+ ", aaData=" + aaData + "}";
	}

}
