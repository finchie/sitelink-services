package uk.gov.snh.sitelink.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Servlet implementation class SiteServlet
 */
@WebServlet(
		description = "searches for sites on Sitelink", 
		urlPatterns = { 
				"/SiteServlet", 
				"/sites"
		})
public class SiteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_PAGESIZE = 100;
	private static final int TOTAL_COUNT = 2135;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SiteServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String countStr = request.getParameter("count");
		int pageSize;
		try {
			pageSize = Integer.parseInt(countStr);
		} catch (NumberFormatException e) {
			pageSize = DEFAULT_PAGESIZE;
		}
		
		int pageCount = (TOTAL_COUNT / pageSize) + 1;
		for (int pageIndex = 0; pageIndex <= pageCount; pageIndex++) {
			response.getWriter().append(fetchSites(pageSize, pageIndex));
		}
	}
	
	private String fetchSites(int pageSize, int pageNumber) throws IOException {
		// build parameters
		String params = "sEcho=7&iColumns=4&sColumns=&iDisplayStart=" + pageNumber + "&iDisplayLength=" + pageSize + "&sNames=%252C%252C%252C&sSearch=&bRegex=false&sSearch_0=&bRegex_0=false&bSearchable_0=true&sSearch_1=&bRegex_1=false&bSearchable_1=true&sSearch_2=&bRegex_2=false&bSearchable_2=true&sSearch_3=&bRegex_3=false&bSearchable_3=true&iSortingCols=1&iSortCol_0=1&sSortDir_0=asc&sSearch0=&sSearch1=&sSearch2=";
		
		// fetch sites from sitelink
		OkHttpClient client = new OkHttpClient();
		Request req = new Request.Builder()
		  .url("http://gateway.snh.gov.uk/sitelink/datatab.jsp?" + params)
		  .get()
		  .addHeader("cache-control", "no-cache")
		  .build();

		Response resp = client.newCall(req).execute();
		
		return resp.body().string();
	}

}
