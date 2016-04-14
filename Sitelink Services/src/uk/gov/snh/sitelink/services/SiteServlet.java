package uk.gov.snh.sitelink.services;

import java.io.IOException;
import java.util.Map;

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
		
		@SuppressWarnings("unchecked")
		Map<Integer, Site> sites = (Map<Integer, Site>) request.getServletContext().getAttribute("sites");
		
		if (sites == null) {
			response.sendError(500, "site index not built\n GET /sites to build index");
			return;
		}
				
		Site site = sites.get(siteId);
		
		if (site == null) {
			response.sendError(500, "invalid siteId: " + siteIdString);
			return;
		}
		else {
			Document doc = Jsoup
							.connect("http://gateway.snh.gov.uk/sitelink/siteinfo.jsp?pa_code=" + siteId)
							.timeout(60*1000)
							.get();
			Elements summaryCells = doc.select("#site_table td");
			
			site.leadSNHArea = summaryCells.get(2).text();
			
			String documentedAreaString = summaryCells.get(4).text();
			try {
				site.documentedArea = Float.parseFloat(documentedAreaString);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("invalid documentedAreaString: " + documentedAreaString);
			}
			
			site.mostRecentDesignatedDate = summaryCells.get(6).text();
			
			Elements docTableRows = doc.select("#documents_table tr");
			Element docTableRow = docTableRows.get(1);
			Element a = docTableRow.select("a").get(0);
			String docName = a.text().trim();
			String url = a.attr("href");
			if (url.contains(";jsessionid")) {
				url = url.substring(0, url.indexOf(";")) + url.substring(url.indexOf("?"));
			}
			String fileSizeString = docTableRow.select("td").get(1).text().replace("\u00a0", "").trim();
			int fileSize = 0;
			try {
				fileSize = Integer.parseInt(fileSizeString);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("invalid fileSizeString: " + fileSizeString);
			}
			site.citation = new SiteDocument(docName, fileSize, url);
			
			response.getWriter().append(toJSON(site));
		}
		
	}

	private String toJSON(Site site) {
	    Moshi moshi = new Moshi.Builder().build();
		JsonAdapter<Site> siteAdapter = moshi.adapter(Site.class);
		return siteAdapter.toJson(site);
	}

}
