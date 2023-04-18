package com.anf.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component(service = { Servlet.class })
@SlingServletPaths(
        value = "/bin/newsfeed"
)
@Designate(ocd = NewsFeedSevlet.Config.class)
public class NewsFeedSevlet extends SlingSafeMethodsServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 935472192046672195L;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	String newsFeedPath;
	
	@Activate
    public void activate(final Config config) {
        this.newsFeedPath = config.newsFeedPath();
    }
	
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		ResourceResolver resolver = request.getResourceResolver();
		Resource resource = resolver.getResource(newsFeedPath);
		log.debug("NewsFeed path from config{}",newsFeedPath);
		Iterator<Resource> children = resource.listChildren();
		List<String> jsonList = new ArrayList<String>();
		while (children.hasNext()) {
			Resource res = children.next();
			Gson json = new GsonBuilder().create();
			String resJson = json.toJson(res.getValueMap());
	        jsonList.add(resJson);
		}
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonList.toString());
		
	}

	@ObjectClassDefinition(name = "NewsFeed Servlet Configuration")
	public @interface Config {
		@AttributeDefinition(name = "NewsFeed global path",
				description = "This property gives the path for Global Newsfeed")
		String newsFeedPath() default "";
	}
	
}
