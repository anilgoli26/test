package com.anf.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import junitx.util.PrivateAccessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class NewsFeedServletTest {

    @InjectMocks
    NewsFeedSevlet newsFeedServlet;
    @Mock
    SlingHttpServletRequest request;
    @Mock
    SlingHttpServletResponse response;
    @Mock
    PrintWriter writer;
    @Mock
    ResourceResolver resolver;
    @Mock
    Resource resource;
    @Mock
    Resource childResource;
    @Mock
    ValueMap vm;
    @Mock
    Iterator<Resource> itr;
    
    Map<String, Object> parameters = new HashMap<>();
    
    private final AemContext ctx = new AemContext();

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        PrivateAccessor.setField(newsFeedServlet, "newsFeedPath", "/var/commerce/products/anf-code-challenge/newsData");
    }
    
    @Test
    void servletConfig() {
    	NewsFeedSevlet devToolConformitySearchServlet = new NewsFeedSevlet();
        parameters.put("newsFeedPath", "/var/commerce/products/anf-code-challenge/newsData");
        ctx.registerInjectActivateService(devToolConformitySearchServlet, parameters);
        assertEquals("/var/commerce/products/anf-code-challenge/newsData", devToolConformitySearchServlet.newsFeedPath);
    }
    
	@Test																																																																																																																															
	void testServlet() throws ServletException, IOException {
		lenient().when(request.getResourceResolver()).thenReturn(resolver);
		lenient().when(resolver.getResource(newsFeedServlet.newsFeedPath)).thenReturn(resource);
		lenient().when(resource.listChildren()).thenReturn(itr);
		lenient().when(itr.hasNext()).thenReturn(true, false);
		lenient().when(itr.next()).thenReturn(childResource);
		lenient().when(childResource.getValueMap()).thenReturn(vm);
		lenient().when(response.getWriter()).thenReturn(writer);
		newsFeedServlet.doGet(request, response);
		assertNotNull(response);
	}

}
