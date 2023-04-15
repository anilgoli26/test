package com.anf.core.services.impl;

import com.anf.core.services.ContentService;
import com.day.cq.commons.jcr.JcrConstants;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = ContentService.class)
public class ContentServiceImpl implements ContentService {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Reference
	ResourceResolverFactory resolverFactory;

    @Override
    public void commitUserDetails(String userData) {
    	Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, "serviceuser");
        try {
        	Session session = getServiceUserSession();
			Node varNode = session.getNode("/var");
			if(varNode.hasNode("anf-code-challenge")) {
				Node exAnfNode = varNode.getNode("anf-code-challenge");
				createUserData(userData, exAnfNode, session);
			} else {
				Node anfNode = varNode.addNode("anf-code-challenge", "sling:Folder");
				session.save();
				createUserData(userData, anfNode, session);
			}
		} catch (RepositoryException | LoginException e) {
			 log.error("Error in commitUserDetails : {}", e.getMessage());
		}
			
    }
    
    @Override
    public void addPropertyOnPage(String pagePath) {
    	try {
    		Session serviceUserSession = getServiceUserSession();
			Node pageJcrNode = serviceUserSession.getNode(pagePath+"/"+JcrConstants.JCR_CONTENT);
			log.info(pageJcrNode.getPath());
			pageJcrNode.setProperty("pageCreated", true);
			serviceUserSession.save();
		} catch (Exception e) {
			log.error("Error in adding property on page : {}", e.getMessage());
		}
    }
    
    private void createUserData(String userData, Node parentNode, Session session) {
    	Gson g = new Gson();  
    	UserDetail us = g.fromJson(userData, UserDetail.class);
    	log.debug("creating userdata {}",userData);
    	try {
			Node addedUserNode = parentNode.addNode(us.getFname()+"-"+us.getAge(), JcrConstants.NT_UNSTRUCTURED);
			addedUserNode.setProperty("Firstname", us.getFname());
			addedUserNode.setProperty("Lastname", us.getLname());
			addedUserNode.setProperty("Age", us.getAge());
			addedUserNode.setProperty("Country", us.getCountry());
			session.save();
		} catch (RepositoryException e) {
			 log.error("Error in creating user data : {}", e.getMessage());
		} 
    }
    
    private Session getServiceUserSession() throws LoginException {
    	Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, "serviceuser");
        ResourceResolver resolver = resolverFactory.getServiceResourceResolver(param);
		Session session = resolver.adaptTo(Session.class);
		return session;
    }
    
    class UserDetail {
    	String fname;
        String lname;
        String age;
        String country;
        public String getFname() {
			return fname;
		}
		public String getLname() {
			return lname;
		}
		public String getAge() {
			return age;
		}
		public String getCountry() {
			return country;
		}
    }
    
}

