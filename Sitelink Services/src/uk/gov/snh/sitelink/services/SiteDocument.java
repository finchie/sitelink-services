package uk.gov.snh.sitelink.services;

public class SiteDocument {
	
	public String name;
	
	public int fileSize;
	
	public String url;

	public SiteDocument(String name, int fileSize, String url) {
		this.name = name;
		this.fileSize = fileSize;
		this.url = url;
	}

}
