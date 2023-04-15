package com.anf.core.services;

import java.util.List;

import javax.jcr.RepositoryException;

public interface ContentService {
	void commitUserDetails(String userData);
	void addPropertyOnPage(String pagePath);
	//List<String> fetchPages(String prop); 
}
