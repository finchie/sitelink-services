package uk.gov.snh.sitelink.services;

public class Agreement {
	
	public int caseCode;
	
	public float caseArea;
	
	public String type;
	
	public String propertyName;
	
	public String managementScheme;
	
	public int term;
	
	public String expiryDate;

	public Agreement(int caseCode, float caseArea, String type, String propertyName,
					 String managementScheme, int term, String expiryDate) {
		this.caseCode = caseCode;
		this.caseArea = caseArea;
		this.type = type;
		this.propertyName = propertyName;
		this.managementScheme = managementScheme;
		this.term = term;
		this.expiryDate = expiryDate;
	}

}
