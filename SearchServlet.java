package com.anf.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anf.core.services.ContentService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;

@Component(service = { Servlet.class })
@SlingServletPaths(
        value = "/bin/searchpages"
)
public class SearchServlet extends SlingSafeMethodsServlet{

    /**
	 * 
	 */
	private static final long serialVersionUID = -3231677479547245833L;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Reference
    private ContentService contentService;

    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
    	log.info("Invoking search servlet");
    	String prop = req.getParameter("prop");
    	if (null!=prop && !prop.isBlank()) {
				List<String> fetchPages = fetchPages(prop,req);
				resp.getWriter().write(StringUtils.join(fetchPages, "\n"));
		}
    }
    
    private List<String> fetchPages(String prop, SlingHttpServletRequest req) {
    	ResourceResolver resourceResolver = req.getResourceResolver();
		Map<String, String> queryOptionsMap = new HashMap<String, String>();
		queryOptionsMap.put("p.limit", "10");
		queryOptionsMap.put("path", "/content/anf-code-challenge/us/en");
		queryOptionsMap.put("property", prop);
		queryOptionsMap.put("property.operation", "exists");
		queryOptionsMap.put("orderby", "@jcr:title");
		queryOptionsMap.put("orderby.sort", "asc");
		log.debug("querymap {}", queryOptionsMap.toString());
		List<String> pagePathList = new ArrayList<String>();
		try {
			Query queryExecute = resourceResolver.adaptTo(QueryBuilder.class).createQuery(PredicateGroup.create(queryOptionsMap), resourceResolver.adaptTo(Session.class));
			if (null!=queryExecute.getResult().getNodes()) {
				Iterator<Node> resultNodes = queryExecute.getResult().getNodes();
				while (resultNodes.hasNext()) {
					 Node node = (Node)resultNodes.next();
					 pagePathList.add(node.getPath());
				}
			} 
		} catch (RepositoryException e) {
			 log.error("Error in creating user data : {}", e.getMessage());
		}
		return !pagePathList.isEmpty() && pagePathList!=null ? pagePathList : Collections.EMPTY_LIST;
    }
	
}
