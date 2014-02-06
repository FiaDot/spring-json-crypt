package com.fiadot.springjsoncrypt.util;



import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

public class CipherUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(CipherUtils.class);
	private static final String CIPHER_PROVIDER = "BC";
	
	//Cipher is No ThreadSafe  
	private Cipher encrypter;
	private Cipher decrypter;
	

	public CipherUtils(String keyAlgorithm, String cipherAlgorithm,	String keyString) {
		
		if (Security.getProvider(CIPHER_PROVIDER) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}

		byte[] key = keyString.getBytes();

		SecretKeySpec sks = new SecretKeySpec(key, keyAlgorithm);

		try {
			encrypter = Cipher.getInstance(cipherAlgorithm, CIPHER_PROVIDER);
			encrypter.init(Cipher.ENCRYPT_MODE, sks);

			decrypter = Cipher.getInstance(cipherAlgorithm, CIPHER_PROVIDER);
			decrypter.init(Cipher.DECRYPT_MODE, sks);
		} catch (Exception e) {
			
			System.err.println("Caught an exception:" + e);
			throw new AssertionError(e);
		}
	}
	
	public CipherUtils(String keyAlgorithm, String cipherAlgorithm, String keyString, String initialVector) {

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

			decrypter = Cipher.getInstance(cipherAlgorithm, CIPHER_PROVIDER);
			decrypter.init(Cipher.DECRYPT_MODE, sks, ips);
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

	public static void main(String[] args) throws Exception {

		String KEY_ALGORITHM = "AES";
		String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
		String KEY_STRING = "2oGR5t0NCoewf2XEC85eyA==";
		String INITIAL_VECTOR = "keiGyA/DPwkpPjXKsgkAzg==";

		String decStr = "{\"ReqDto\":{\"plain_data\":\"test\"}}";
//		String decStr = "{\"loginReq\":{accessToken:\"4828288382\", deviceId: \"1234567890123456789012345678901234567890123456789012345678901234\", channelCd: \"002\", userId:\"12345678901234567\"}}";
		String encStr = "S14k/ReOPqV8rnKgNd0sPKvfVTsl0qwPZ5jf5g56E1jmyeSEm9Mly3YYLiqd9T70LJjQGy2eplN9wEFILHZK3z5Jzx5p6jFyoa7jCjVL1q1mxz5U /xRSG4XP1riXmQytBqMt1QhtBGOVCj4ZkSr1R2AB4jIfDL6pVQ3o 8/WQo=";
		encStr = "24cM+cYInHO83Fzsc0EfLR1jT8kn9shtjNxCa/5egzy3AXWhONBpCKtERveHNm+4";
		CipherUtils cu = new CipherUtils(KEY_ALGORITHM, CIPHER_ALGORITHM, KEY_STRING, INITIAL_VECTOR);
		System.out.println(cu.encrypt(decStr));
		System.out.println(cu.decrypt(encStr));
		// System.out.println(cu.decrypt(source);
	}
}
