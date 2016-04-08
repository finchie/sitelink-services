package uk.gov.snh.sitelink.services;

import java.util.List;

public class Site {

	public int id;
	
	public String name;
	
	public String designation;
	
	public String status;
	
	public Site(String idString, String linkHTML, String designation, String status) {
		this.id = Integer.parseInt(idString);
		this.name = linkHTML.substring(linkHTML.indexOf(">"), linkHTML.lastIndexOf("<"));
		this.designation = designation;
		this.status = status;
	}
	
	public Site(List<String> data) {
		this.id = Integer.parseInt(data.get(0));
		String linkHTML = data.get(1);
		this.name = linkHTML.substring(linkHTML.indexOf(">") + 1, linkHTML.lastIndexOf("<"));
		this.designation = data.get(2);
		this.status = data.get(3);
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