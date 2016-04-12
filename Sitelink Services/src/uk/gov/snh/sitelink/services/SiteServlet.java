package uk.gov.snh.sitelink.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
		
		Document doc = Jsoup.connect("http://gateway.snh.gov.uk/sitelink/siteinfo.jsp?pa_code=" + siteId).get();
		Elements summaryCells = doc.select("#site_table td");
		
		String leadSNHArea = summaryCells.get(2).text();
		String documentedAreaString = summaryCells.get(4).text();
		float documentedArea = 0;
		try {
			documentedArea = Float.parseFloat(documentedAreaString);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("invalid documentedAreaString: " + documentedAreaString);
		}
		
		response.getWriter().append("leadSNHArea: " + leadSNHArea + "\n");
		response.getWriter().append("documentedArea: " + documentedArea + "\n");
	}

}
