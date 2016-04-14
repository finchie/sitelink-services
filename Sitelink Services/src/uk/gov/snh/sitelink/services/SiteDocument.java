package uk.gov.snh.sitelink.services;

public class SiteDocument {
	
	public final static String BASE_URL = "http://gateway.snh.gov.uk/sitelink/";
	
	public String name;
	
	public int fileSize;
	
	public String url;

	public SiteDocument(String name, int fileSize, String url) {
		this.name = name;
		this.fileSize = fileSize;
		this.url = url;
	}

}
