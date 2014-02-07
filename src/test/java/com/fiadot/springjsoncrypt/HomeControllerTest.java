package com.fiadot.springjsoncrypt;


import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fiadot.springjsoncrypt.util.CipherUtils;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/appServlet/servlet-context.xml")
public class HomeControllerTest {
	private static final Logger logger =  LoggerFactory.getLogger(HomeControllerTest.class);	
    
	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext wac;
       
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
    
    
    // Accept: application/json, application/crypto 반드시 추가 해야 함!
    
    @Test
    public void plain_test() throws Exception {
    	
    	String decStr = "{\"ReqDto\":{\"plain_data\":\"test\"}}";		

    	MvcResult result = mockMvc.perform(post("/enc")
											.contentType(MediaType.APPLICATION_JSON)
											.accept(MediaType.APPLICATION_JSON)
											.content(decStr))                   
									.andExpect(status().isOk())
									.andExpect(content().contentType(MediaType.APPLICATION_JSON))
									.andReturn();
    	
    	String str_res = result.getResponse().getContentAsString();    	
    	logger.info("RES=" + str_res);
    	
    	assertTrue(str_res.contains("implementation"));		
    }       	
    
    
    
    @Test
    public void enc_test() throws Exception {
    	
		String encStr = "mkZC0LeBOiM234YglFAElK78DW1ll26fy7MBkQf/U5QSqzvvfMbtMNeU8v1f56pe";

    	MvcResult result = mockMvc.perform(post("/enc")																			
											.header("Accept", "application/crypto")
											.header("Content-Type", "application/crypto")											
											.content(encStr))                   
									.andExpect(status().isOk())
									.andReturn();
    	
    	String str_res = result.getResponse().getContentAsString();    	
    	logger.info("ENC RES=" + str_res);
        	
    	
		String KEY_STRING = "ls4h+XaXU+A5m72HRpwkeQ==";
		String INITIAL_VECTOR = "W46YspHuEiQlKDcLTqoySw==";
			
		String KEY_ALGORITHM = "AES";
		String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
			
				
		CipherUtils cu = new CipherUtils(KEY_ALGORITHM, CIPHER_ALGORITHM, KEY_STRING, INITIAL_VECTOR);
		
		String plain_str = cu.decrypt(str_res);
		logger.info("PLAIN RES=" + plain_str);
		
		assertTrue(plain_str.contains("implementation"));		
    }   
    
}