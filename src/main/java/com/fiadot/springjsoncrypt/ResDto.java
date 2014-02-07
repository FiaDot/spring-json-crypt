package com.fiadot.springjsoncrypt;

import com.thoughtworks.xstream.annotations.XStreamAlias;

// 밑에 안붙여 주면 com... 전부 붙어서 fullname이 들어간다
@XStreamAlias("ResDto")
public class ResDto {	
	private String encrypted_data;

	public String getEncrypted_data() {
		return encrypted_data;
	}

	public void setEncrypted_data(String encrypted_data) {
		this.encrypted_data = encrypted_data;
	}
	
}