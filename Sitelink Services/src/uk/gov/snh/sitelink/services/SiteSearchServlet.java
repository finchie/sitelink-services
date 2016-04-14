package uk.gov.snh.sitelink.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Servlet implementation class SiteSearchServlet
 */
@WebServlet(
		description = "searches for sites on Sitelink", 
		urlPatterns = { 
				"/SiteSearchServlet", 
				"/sites"
		})
public class SiteSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_PAGESIZE = 100;
	private static final int TOTAL_COUNT = 2135;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SiteSearchServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// decide whether to re-fetch sites
	    if (request.getServletContext().getAttribute("sites") == null
	            || "true".equals(request.getParameter("refresh"))) {
	        
	        try
            {
                // store sites in application scope
                request.getServletContext().setAttribute("sites", fetchSites());
            }
            catch (IOException e)
            {
                response.sendError(500, "error fetching sites data: " + e.getMessage());
                return;
            }
	    }
				
		// write sites JSON to response
	    @SuppressWarnings("unchecked")
		Map<Integer, Site> sites = (Map<Integer, Site>) request.getServletContext().getAttribute("sites");
	    String json = toJSON(sites);

	    response.getWriter().append(json);
	}
	
	private Map<Integer, Site> fetchSites() throws IOException {
	    int pageSize = DEFAULT_PAGESIZE;		
		int pageCount = (TOTAL_COUNT / pageSize) + 1;
		Map<Integer, Site> sites = new HashMap<Integer, Site>();
			
		Moshi moshi = new Moshi.Builder().build();
		JsonAdapter<SearchResult> jsonAdapter = moshi.adapter(SearchResult.class);

		for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
			
			String sitesJSON = fetchSitesJSON(pageSize, pageIndex);

			SearchResult result = jsonAdapter.fromJson(sitesJSON);
			
			for (List<String> siteData : result.aaData) {
				Site site = new Site(siteData);
				sites.put(site.id, site);
			}
		}
		
		return sites;
	}
	
	private String fetchSitesJSON(int pageSize, int pageNumber) throws IOException {
		// build parameters
		String params = "sEcho=7&iColumns=4&sColumns=&iDisplayStart=" + (pageNumber * pageSize) + "&iDisplayLength=" + pageSize + "&sNames=%252C%252C%252C&sSearch=&bRegex=false&sSearch_0=&bRegex_0=false&bSearchable_0=true&sSearch_1=&bRegex_1=false&bSearchable_1=true&sSearch_2=&bRegex_2=false&bSearchable_2=true&sSearch_3=&bRegex_3=false&bSearchable_3=true&iSortingCols=1&iSortCol_0=1&sSortDir_0=asc&sSearch0=&sSearch1=&sSearch2=";
		
		// fetch sites from sitelink
		OkHttpClient client = new OkHttpClient.Builder()
		          .connectTimeout(60, TimeUnit.SECONDS)
		          .writeTimeout(60, TimeUnit.SECONDS)
		          .readTimeout(60, TimeUnit.SECONDS)
		          .build();
		Request req = new Request.Builder()
		  .url("http://gateway.snh.gov.uk/sitelink/datatab.jsp?" + params)
		  .get()
		  .addHeader("cache-control", "no-cache")
		  .build();

		Response resp = client.newCall(req).execute();
		
		return resp.body().string().trim();
	}

	private String toJSON(Map<Integer, Site> sites) {
	    Moshi moshi = new Moshi.Builder().build();
		JsonAdapter<SiteCollection> siteAdapter = moshi.adapter(SiteCollection.class);
		SiteCollection siteCollection = new SiteCollection(sites);
		return siteAdapter.toJson(siteCollection);
	}
}
