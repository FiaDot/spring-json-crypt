package com.fiadot.springjsoncrypt.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;

public class CryptJsonHttpMessageConverter extends MappingJackson2HttpMessageConverter {
 

    private static final List<MediaType> SUPPORTED_MEDIA_TYPES = new ArrayList<MediaType>() {
		private static final long serialVersionUID = 1L;

	{
        add( new MediaType("application", "jsonext") );        
    }};
 
 
    public CryptJsonHttpMessageConverter() {    	
        setSupportedMediaTypes(SUPPORTED_MEDIA_TYPES);
    }
 
    
 
    @Override
    protected void writeInternal( Object object, HttpOutputMessage outputMessage )
        throws IOException, HttpMessageNotWritableException {
    	
    	System.out.println("WI="+object.toString());
    	super.writeInternal(object, outputMessage);
    }
 
 
}