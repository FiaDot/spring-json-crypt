package com.fiadot.springjsoncrypt.util;



import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.Security;
import java.util.Random;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Hex;

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
		
		logger.info("CipherUtils:decrypt() raw=" + encryptedData);
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


	
	public static String get_random_enc_base64(int size) {
		Random random = new Random();
		byte[] randomBytes = new byte[size];
		random.nextBytes(randomBytes);
		
		byte[] apacheBytes =  org.apache.commons.codec.binary.Base64.encodeBase64(randomBytes);
        return new String(apacheBytes);
	}
	
	
	public static void main(String[] args) throws Exception {
/*		
		String PLAIN_KEY = "1234567890123456";
		String PLAIN_IV = "1234567890123456";
				
        String KEY_STRING = get_random_enc_base64(16);
        String INITIAL_VECTOR = get_random_enc_base64(16);

		System.out.println("KEY_STRING=" + KEY_STRING);
		System.out.println("INITIAL_VECTOR=" + INITIAL_VECTOR);
*/		
		
		String KEY_STRING = "ls4h+XaXU+A5m72HRpwkeQ==";
		String INITIAL_VECTOR = "W46YspHuEiQlKDcLTqoySw==";
			
		String KEY_ALGORITHM = "AES";
		String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
			

		String decStr = "{\"ReqDto\":{\"plain_data\":\"test\"}}";		
		String encStr = "mkZC0LeBOiM234YglFAElK78DW1ll26fy7MBkQf/U5QSqzvvfMbtMNeU8v1f56pe";

				
		CipherUtils cu = new CipherUtils(KEY_ALGORITHM, CIPHER_ALGORITHM, KEY_STRING, INITIAL_VECTOR);
		System.out.println(cu.encrypt(decStr));
		System.out.println(cu.decrypt(encStr));
	}
}
