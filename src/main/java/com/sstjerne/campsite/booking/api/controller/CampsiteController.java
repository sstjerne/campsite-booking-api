package com.sstjerne.campsite.booking.api.controller;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.sstjerne.campsite.booking.api.model.Campsite;
import com.sstjerne.campsite.booking.api.model.ResponseMessage;
import com.sstjerne.campsite.booking.api.service.CampsiteService;

@RestController
@RequestMapping("/campsite")
public class CampsiteController {

    private static final Log logger = LogFactory.getLog(CampsiteController.class);

    @Autowired
    private CampsiteService campsiteService;


    @RequestMapping(method = RequestMethod.GET)
    public Page<Campsite> getAll( 
    		@RequestParam(value = "page", required = true, defaultValue = "0") Integer page,
    		@RequestParam(value = "size", required = true, defaultValue = "50") Integer size,
    		HttpServletRequest request, HttpServletResponse response) throws NoSuchRequestHandlingMethodException {
        
        Page<Campsite> campsites = campsiteService.getAll(page, size);


        return campsites;
    }
    
    
    @RequestMapping(method = RequestMethod.POST,
            consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"})
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Campsite campsite,
                                 HttpServletRequest request, HttpServletResponse response) throws Exception {
    	Campsite campsiteP = campsiteService.create(campsite);
    	
        response.setHeader("Location", request.getRequestURL().append("/").append(campsiteP.getId()).toString());
    }
    
    
    @RequestMapping(value = "/{id}",
    		method = RequestMethod.GET,
            consumes = {"application/json", "application/xml"},
            produces = {"application/json", "application/xml"})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public HttpEntity<Campsite> get(@PathVariable("id") final Long id,
                                 HttpServletRequest request, 
                                 HttpServletResponse response) throws Exception {
    	Campsite campsiteP = campsiteService.get(id);
    	
        response.setHeader("Location", request.getRequestURL().toString());
        
		return new HttpEntity<Campsite>(campsiteP);    
	}
    
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ResponseMessage> cancel(@PathVariable("id") final Long id, HttpServletRequest request, HttpServletResponse response) {
    	campsiteService.delete(id);

    	ResponseMessage message = new ResponseMessage();
		message.setMessage("The Campasite was remove succesfully");
		message.setReason(HttpStatus.NO_CONTENT.getReasonPhrase());
		message.setCode(HttpStatus.NO_CONTENT.value());
		
		return new ResponseEntity<ResponseMessage>(message, HttpStatus.NO_CONTENT);

    
    }
}