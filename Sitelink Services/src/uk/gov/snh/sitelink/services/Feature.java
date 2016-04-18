package uk.gov.snh.sitelink.services;

public class Feature {
	
	public String name;
	
	public String category;
	
	public String latestAssessedCondition;
	
	public String summaryCondition;
	
	public String lastVisitDate;

	public Feature(String name, String category, String latestAssessedCondition, String summaryCondition,
			String lastVisitDate) {
		this.name = name;
		this.category = category;
		this.latestAssessedCondition = latestAssessedCondition;
		this.summaryCondition = summaryCondition;
		this.lastVisitDate = lastVisitDate;
	}

}
