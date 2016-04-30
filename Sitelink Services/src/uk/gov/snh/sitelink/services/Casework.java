package uk.gov.snh.sitelink.services;

public class Casework {
	
	public String caseCode;
	
	public String consultationType;
	
	public String receivedDate;
	
	public String response;

	public Casework(String caseCode, String consultationType, String receivedDate, String response) {
		this.caseCode = caseCode;
		this.consultationType = consultationType;
		this.receivedDate = receivedDate;
		this.response = response;
	}

}
