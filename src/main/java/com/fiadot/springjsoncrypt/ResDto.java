package com.fiadot.springjsoncrypt;


import com.thoughtworks.xstream.annotations.XStreamAlias;

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