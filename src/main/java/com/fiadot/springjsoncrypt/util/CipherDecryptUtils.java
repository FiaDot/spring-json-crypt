package com.fiadot.springjsoncrypt.util;



import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;


public class CipherDecryptUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(CipherDecryptUtils.class);
	private static final String CIPHER_PROVIDER = "BC";
	
	//Cipher is No ThreadSafe  
	private Cipher decrypter;

	
	public CipherDecryptUtils(String keyAlgorithm, String cipherAlgorithm, String keyString, String initialVector) {

		if (Security.getProvider(CIPHER_PROVIDER) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		
		byte[] key = Base64.decode(keyString);
		byte[] iv = Base64.decode(initialVector);

		SecretKeySpec sks = new SecretKeySpec(key, keyAlgorithm);
		IvParameterSpec ips = new IvParameterSpec(iv);

		try {
			decrypter = Cipher.getInstance(cipherAlgorithm, CIPHER_PROVIDER);
			decrypter.init(Cipher.DECRYPT_MODE, sks, ips);
		} catch (Exception e) {
			System.err.println("Caught an exception:" + e);
			throw new AssertionError(e);
		}
	}

	public String decrypt(String encryptedData) throws Exception {
		if (encryptedData == null) {
			return null;
		}
		
		encryptedData = encryptedData.replaceAll(" ", "+");
		encryptedData = encryptedData.replaceAll("%2f", "/");
		encryptedData = encryptedData.replaceAll("%3d", "=");
		byte[] decryptedData = Base64.decode(encryptedData);
		try {
			return new String(decrypter.doFinal(decryptedData));
		} catch (Exception e) {
			logger.error("CipherUtils - encrypt : encryptedData={}", encryptedData);
			throw new Exception(e);
		}
	}

}
