package com.fiadot.springjsoncrypt.util;



import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;


public class CipherEncryptUtils {
	
//	private static final Logger logger = LoggerFactory.getLogger(CipherEncryptUtils.class);
	private static final String CIPHER_PROVIDER = "BC";
	
	//Cipher is No ThreadSafe  
	private Cipher encrypter;
	
	public CipherEncryptUtils(String keyAlgorithm, String cipherAlgorithm, String keyString, String initialVector) {

		if (Security.getProvider(CIPHER_PROVIDER) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		
		byte[] key = Base64.decode(keyString);
		byte[] iv = Base64.decode(initialVector);

		SecretKeySpec sks = new SecretKeySpec(key, keyAlgorithm);
		IvParameterSpec ips = new IvParameterSpec(iv);

		try {
			encrypter = Cipher.getInstance(cipherAlgorithm, CIPHER_PROVIDER);
			encrypter.init(Cipher.ENCRYPT_MODE, sks, ips);
		} catch (Exception e) {
			System.err.println("Caught an exception:" + e);
			throw new AssertionError(e);
		}
	}

	public String encrypt(String data) throws Exception {
		if (data == null) {
			return null;
		}

		byte[] encryptedData;
		try {
			encryptedData = encrypter.doFinal(data.getBytes("UTF8"));
		} catch (Exception e) {
			throw new Exception(e);
		}
		return new String(Base64.encode(encryptedData));
	}

}
