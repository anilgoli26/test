package com.anf.core.listeners;

import java.util.Iterator;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anf.core.services.ContentService;
import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;

@Component(service = EventHandler.class,
immediate = true,
property = {
        EventConstants.EVENT_TOPIC + "=" + PageEvent.EVENT_TOPIC,
        
})
public class PageCreationEventHandler implements EventHandler {

	private static final Logger log = LoggerFactory.getLogger(PageCreationEventHandler.class);

	@Reference
    private ContentService contentService;
	
	@Override
    public void handleEvent(final Event event)  {
        try {
            Iterator<PageModification> pageInfo=PageEvent.fromEvent(event).getModifications();
            while (pageInfo.hasNext()){
                PageModification pageModification=pageInfo.next();
                String eventType = pageModification.getEventProperties().get("type").toString();
                String pagePath = pageModification.getEventProperties().get("path").toString();
                if(eventType.equals("PageCreated")) {
                	log.info("triggered on page creation");
                	contentService.addPropertyOnPage(pagePath);
                }
            }

        }catch (Exception e){
            log.info(" Error while accessing page created event - {} " , e.getMessage());
        }
    }

}
