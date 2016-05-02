package uk.gov.snh.sitelink.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

/**
 * Servlet implementation class SiteServlet
 */
@WebServlet(
		description = "creates full representation of site and stores in application scope", 
		urlPatterns = { 
				"/SiteServlet", 
				"/site"
		})
public class SiteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SiteServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String siteIdString = request.getParameter("siteId");
		int siteId;
		try {
			siteId = Integer.parseInt(siteIdString);
		} catch (NumberFormatException e) {
			response.sendError(500, "invalid siteId: " + siteIdString);
			return;
		}
		
		Document doc = Jsoup
						.connect("http://gateway.snh.gov.uk/sitelink/siteinfo.jsp?pa_code=" + siteId)
						.timeout(60*1000)
						.get();
		Elements summaryCells = doc.select("#site_table td");
		
		Site site = new Site();
		site.id = siteId;
		site.name = summaryCells.get(1).text();
		site.leadSNHArea = summaryCells.get(2).text();
		site.designation = Designation.get(summaryCells.get(3).text());
		
		String documentedAreaString = summaryCells.get(4).text();
		try {
			site.documentedArea = Float.parseFloat(documentedAreaString);
		} catch (NumberFormatException e) {
			System.out.println("invalid documentedAreaString: " + documentedAreaString);
		}

		site.status = Status.get(summaryCells.get(5).text());
		
		site.mostRecentDesignatedDate = summaryCells.get(6).text();
		
		// documents
		Elements docTableRows = doc.select("#documents_table tr"); 
		// ignore header row
		docTableRows.remove(0);
		
		for (Element tr : docTableRows) {
			
			Element a = tr.select("a").get(0);
			String docName = a.text().trim();
			String url = a.attr("href");
			if (url.contains(";jsessionid")) {
				url = url.substring(0, url.indexOf(";")) + url.substring(url.indexOf("?"));
			}
			String fileSizeString = tr.select("td").get(1).text().replace("\u00a0", "").trim();
			int fileSize = 0;
			try {
				fileSize = Integer.parseInt(fileSizeString);
			} catch (NumberFormatException e) {
				System.out.println("invalid fileSizeString: " + fileSizeString);
			}
			site.documents.add(new SiteDocument(docName, fileSize, Site.BASE_URL + url));
		}
		
		// features
		Elements featureTableRows = doc.select("#features_table tbody tr");
		for (Element tr : featureTableRows) {
			Elements tds = tr.select("td");
			String name = tds.get(0).text().replace("\u00a0", "").trim();
			String category = tds.get(1).text().replace("\u00a0", "").trim();
			String latestAssessedCondition = tds.get(2).text().replace("\u00a0", "").trim();
			String summaryCondition = tds.get(3).text().replace("\u00a0", "").trim();
			String lastVisitDate = tds.get(4).text().replace("\u00a0", "").trim();
			site.features.add(new Feature(name, category, latestAssessedCondition, summaryCondition, lastVisitDate));
		}
		
		// pressures
		Elements pressureTableRows = doc.select("#featurePressures_table tbody tr");
		for (Element tr : pressureTableRows) {
			Elements tds = tr.select("td");
			if ( "No negative pressures".equals(tds.get(1).text().replace("\u00a0", "").trim()) ) {
				// skip feature
			}
			else {				
				String feature = tr.select("td").get(0).text().replace("\u00a0", "").trim();
				String pressure = tds.get(1).text().replace("\u00a0", "").trim();
				String recordedAs = tds.get(2).text().replace("\u00a0", "").trim();
				String keywords = tds.get(3).text().replace("\u00a0", "").trim();
				site.pressures.add(new Pressure(feature, pressure, recordedAs, keywords));
			}
		}
		
		// agreements
		Elements agreementTableRows = doc.select("#agreements_table tbody tr");
		for (Element tr : agreementTableRows) {
			Elements tds = tr.select("td");
			String caseCodeString = tr.select("td").get(0).text().replace("\u00a0", "").trim();
			int caseCode = 0;
			try {
				caseCode = Integer.parseInt(caseCodeString);
			} catch (NumberFormatException e) {
				// ignore
			}
			String caseAreaString = tds.get(1).text().replace("\u00a0", "").trim();
			float caseArea = 0;
			try {
				caseArea = Float.parseFloat(caseAreaString);
			} catch (NumberFormatException e) {
				// ignore
			}
			String type = tds.get(2).text().replace("\u00a0", "").trim();
			String propertyName = tds.get(3).text().replace("\u00a0", "").trim();
			String managementScheme = tds.get(4).text().replace("\u00a0", "").trim();
			String termString = tds.get(5).text().replace("\u00a0", "").trim();
			int term = 0;
			try {
				term = Integer.parseInt(termString);
			} catch (NumberFormatException e) {
				// ignore
			}
			String expiryDate = tds.get(6).text().replace("\u00a0", "").trim();
			site.agreements.add(new Agreement(caseCode, caseArea, type, propertyName, managementScheme, term, expiryDate));
		}
		
		// casework
		Elements caseworkTableRows = doc.select("#caseworks_table tbody tr");
		for (Element tr : caseworkTableRows) {
			Elements tds = tr.select("td");
			String caseCode = tds.get(0).text().replace("\u00a0", "").trim();
			String consultationType = tds.get(1).text().replace("\u00a0", "").trim();
			String receivedDate = tds.get(2).text().replace("\u00a0", "").trim();
			String resp = tds.get(3).text().replace("\u00a0", "").trim();
			site.cases.add(new Casework(caseCode, consultationType, receivedDate, resp));
		}
		
		// links
		Elements links = doc.select("a.rlink");
		for (Element link : links) {
			String url = link.attr("href");
			String text = link.text().replace("\u00a0", "").trim();
			site.links.add(new Link(url, text));
		}
		
		// map
		site.mapURL = Site.BASE_URL + "sitemap.jsp?pa_code=" + siteId + "&desig_code=" + site.designation.name();
		
		response.setContentType("application/json");
		response.getWriter().append(toJSON(site));
		
	}

	private String toJSON(Site site) {
	    Moshi moshi = new Moshi.Builder().build();
		JsonAdapter<Site> siteAdapter = moshi.adapter(Site.class);
		return siteAdapter.toJson(site);
	}

}
