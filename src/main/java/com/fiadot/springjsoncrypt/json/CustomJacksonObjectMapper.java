package com.fiadot.springjsoncrypt.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import static com.fasterxml.jackson.annotation.JsonInclude.*;

public class CustomJacksonObjectMapper extends ObjectMapper {

/**
	 * 
	 */
	private static final long serialVersionUID = -4553168783182393595L;

public CustomJacksonObjectMapper() {
    super();
    this.configure(DeserializationFeature.UNWRAP_ROOT_VALUE , true);
    this.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
    this.setSerializationInclusion(Include.NON_NULL);
   }
}
