package com.anf.core.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "= Json Data in dynamic Dropdown",
        "sling.servlet.paths=" + "/bin/countryDropdown", "sling.servlet.methods=" + HttpConstants.METHOD_GET
})
public class CountriesDropDown extends SlingSafeMethodsServlet {
	
	private static final long serialVersionUID = 1L;

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private static final String SLASH = "/";
    transient ResourceResolver resourceResolver;
    transient Resource pathResource;
    transient ValueMap valueMap;
    transient List<Resource> resourceList;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        resourceResolver = request.getResourceResolver();
        pathResource = request.getResource();
        resourceList = new ArrayList<>();
        try {
            String jsonDataPath = Objects.requireNonNull(pathResource.getChild("datasource")).getValueMap().get("jsonDataPath", String.class);
            String jsonDataJcrPath = jsonDataPath.concat(SLASH).concat(JcrConstants.JCR_CONTENT)
            						.concat(SLASH).concat(DamConstants.RENDITIONS_FOLDER)
            						.concat(SLASH).concat(DamConstants.ORIGINAL_FILE)
            						.concat(SLASH).concat(JcrConstants.JCR_CONTENT);
            Resource jsonResource = resourceResolver.getResource(jsonDataJcrPath);
            log.debug("getting json data from path {}",jsonDataJcrPath);
            Node jsonNode = jsonResource.adaptTo(Node.class);
            InputStream inputStream = jsonNode.getProperty(JcrConstants.JCR_DATA).getBinary().getStream();

            StringBuilder stringBuilder = new StringBuilder();
            String eachLine;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            
            while ((eachLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(eachLine);
            }
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            Iterator<String> jsonKeys = jsonObject.keys();
            while (jsonKeys.hasNext()) {
                String jsonKey = jsonKeys.next();
                String jsonValue = jsonObject.getString(jsonKey);
                valueMap = new ValueMapDecorator(new HashMap<>());
                valueMap.put("text", jsonKey);
                valueMap.put("value", jsonValue);
                resourceList.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED , valueMap));
            }
            DataSource dataSource = new SimpleDataSource(resourceList.iterator());
            request.setAttribute(DataSource.class.getName(), dataSource);

        } catch (JSONException | IOException | RepositoryException e) {
            log.error("Error in Json Data Exporting : {}", e.getMessage());
        }
    }
}
