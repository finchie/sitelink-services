package uk.gov.snh.sitelink.services;

public class Pressure {
	
	public String feature;
	
	public String pressure;
	
	public String recordedAs;
	
	public String keywords;

	public Pressure(String feature, String pressure, String recordedAs, String keywords) {
		this.feature = feature;
		this.pressure = pressure;
		this.recordedAs = recordedAs;
		this.keywords = keywords;
	}

}
