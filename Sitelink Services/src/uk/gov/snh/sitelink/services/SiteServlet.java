package uk.gov.snh.sitelink.services;

import java.io.IOException;
import java.util.ArrayList;

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
		
		Elements docTableRows = doc.select("#documents_table tr"); 
		// ignore header row
		docTableRows.remove(0);
		site.documents = new ArrayList<SiteDocument>();
		
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
			site.documents.add(new SiteDocument(docName, fileSize, url));
		}
		
		response.setContentType("application/json");
		response.getWriter().append(toJSON(site));
		
	}

	private String toJSON(Site site) {
	    Moshi moshi = new Moshi.Builder().build();
		JsonAdapter<Site> siteAdapter = moshi.adapter(Site.class);
		return siteAdapter.toJson(site);
	}

}
