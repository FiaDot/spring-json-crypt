package com.fiadot.springjsoncrypt.json;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.MediaType;

public class JsonIntegration {

	public static final MediaType APPLICATION_JSON_UTF8 = 
			new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), 
			Charset.forName("utf8"));

	
	public static String convert_obj_to_json(Object object) throws IOException {
		CustomJacksonObjectMapper mapper = new CustomJacksonObjectMapper();

		byte[] output = mapper.writeValueAsBytes(object);
		return new String(output, 0, output.length);
	}

}