package uk.gov.snh.sitelink.services;

import java.util.ArrayList;
import java.util.List;

public class Site {
	
	public static final String BASE_URL = "http://gateway.snh.gov.uk/sitelink/";

	public int id;
	
	public String name;
	
	public Designation designation;
	
	public Status status;
	
	public String leadSNHArea;
	
	public float documentedArea;
	
	public String mostRecentDesignatedDate;
	
	public List<SiteDocument> documents;
	
	public List<Feature> features;
	
	public List<Pressure> pressures;
	
	public List<Agreement> agreements;
	
	public List<Casework> cases;
	
	public String mapURL;
	
	public List<Link> links;
	
	public Site() {
		this.documents = new ArrayList<SiteDocument>();
		this.features = new ArrayList<Feature>();
		this.pressures = new ArrayList<Pressure>();
		this.agreements = new ArrayList<Agreement>();
		this.cases = new ArrayList<Casework>();
		this.links = new ArrayList<Link>();
	}
	
	public Site(String idString, String linkHTML, String designation, String status) {
		this.id = Integer.parseInt(idString);
		this.name = linkHTML.substring(linkHTML.indexOf(">"), linkHTML.lastIndexOf("<"));
		this.designation = Designation.get(designation);
		this.status = Status.get(status);
		this.documents = new ArrayList<SiteDocument>();
		this.features = new ArrayList<Feature>();
		this.pressures = new ArrayList<Pressure>();
		this.agreements = new ArrayList<Agreement>();
		this.cases = new ArrayList<Casework>();
		this.links = new ArrayList<Link>();
	}
	
	public Site(List<String> data) {
		this.id = Integer.parseInt(data.get(0));
		String linkHTML = data.get(1);
		this.name = linkHTML.substring(linkHTML.indexOf(">") + 1, linkHTML.lastIndexOf("<"));
		this.designation = Designation.get(data.get(2));
		this.status = Status.get(data.get(3));
		this.documents = new ArrayList<SiteDocument>();
		this.features = new ArrayList<Feature>();
		this.pressures = new ArrayList<Pressure>();
		this.agreements = new ArrayList<Agreement>();
		this.cases = new ArrayList<Casework>();
		this.links = new ArrayList<Link>();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Site other = (Site) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Site [id=" + id + ", name=" + name + ", designation=" + designation + ", status=" + status + "]";
	}
	
}
