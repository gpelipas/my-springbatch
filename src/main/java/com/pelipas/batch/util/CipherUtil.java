/**
 * Genaro Pelipas (c) 2020
 */
package com.pelipas.batch.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * Encrypt-Decrypt Helper class
 * 
 * @author gpelipas
 *
 */
public class CipherUtil {

	public static final String ALGO_SHA5AES128 = "PBEWithHmacSHA512AndAES_128";

	public static final int DEFAULT_ITERATION = 4096;

	private String algo;

	private CipherUtil(String algo) {
		this.algo = algo;
	}

	public final static CipherUtil createInstance() {
		return new CipherUtil(ALGO_SHA5AES128);
	}
	
	public final static CipherUtil createInstance(String algo) {
		return new CipherUtil(algo);
	}

	public final String encrypt(final String plainText, final String password)
			throws GeneralSecurityException, UnsupportedEncodingException {

		byte[] ivBytes = generateRandomKeys(16);
		byte[] saltBytes = generateRandomKeys(16);
		byte[] dataToEncrypt = plainText.getBytes("UTF-8");

		Cipher cipher = Cipher.getInstance(algo);

		PBEParameterSpec pbeParams = new PBEParameterSpec(saltBytes, DEFAULT_ITERATION, new IvParameterSpec(ivBytes));

		cipher.init(Cipher.ENCRYPT_MODE, getPBESecretKey(password, saltBytes), pbeParams);
		byte[] encryptedData = cipher.doFinal(dataToEncrypt);

		byte[] ivWithSalt = mergeArray(ivBytes, saltBytes);
		byte[] encryptedDataWithIVAndSalt = mergeArray(ivWithSalt, encryptedData);
		
		return base64Encode(encryptedDataWithIVAndSalt);
	}

	public String decrypt(final String cipherText, final String password)
			throws GeneralSecurityException, UnsupportedEncodingException {

		byte[] encryptedDataWithIVAndSalt = base64Decode(cipherText);
		byte[] ivBytes = subArray(encryptedDataWithIVAndSalt, 0, 16);
		byte[] saltBytes = subArray(encryptedDataWithIVAndSalt, 16, 16);
		byte[] dataToDecrypt = subArray(encryptedDataWithIVAndSalt, 32, encryptedDataWithIVAndSalt.length - 32);

		return decrypt(ALGO_SHA5AES128, dataToDecrypt, password, ivBytes, saltBytes, DEFAULT_ITERATION, true);
	}

	public String decrypt(final String cipherText, final String password, final byte[] iv, final int iteration)
			throws GeneralSecurityException, UnsupportedEncodingException {

		byte[] encryptedDataWithSalt = base64Decode(cipherText);
		byte[] saltBytes = subArray(encryptedDataWithSalt, encryptedDataWithSalt.length - 8, 8);
		byte[] dataToDecrypt = subArray(encryptedDataWithSalt, 0, encryptedDataWithSalt.length - 8);

		return decrypt(ALGO_SHA5AES128, dataToDecrypt, password, iv, saltBytes, iteration, true);
	}

	public String decrypt(final String algo, final byte[] data, final String password, final byte[] iv,
			final byte[] salt, int iteration, boolean useSaltInKey)
			throws GeneralSecurityException, UnsupportedEncodingException {

		Cipher cipher = Cipher.getInstance(algo);

		SecretKey pbeKey = null;
		if (useSaltInKey) {
			pbeKey = getPBESecretKey(password, salt);
		} else {
			pbeKey = getPBESecretKey(password);
		}

		PBEParameterSpec pbeParams = new PBEParameterSpec(salt, iteration, new IvParameterSpec(iv));

		cipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParams);
		byte[] decryptedData = cipher.doFinal(data);

		return new String(decryptedData, "UTF-8");
	}

	private SecretKey getPBESecretKey(final String password) throws GeneralSecurityException {
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algo);
		SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);

		return pbeKey;
	}

	private SecretKey getPBESecretKey(final String password, final byte[] salt) throws GeneralSecurityException {
		PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, DEFAULT_ITERATION, 128);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algo);
		SecretKey pbeKey = keyFactory.generateSecret(pbeKeySpec);

		return pbeKey;
	}

	public final static String base64Encode(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	public final static byte[] base64Decode(String base64Str) {
		return Base64.getDecoder().decode(base64Str);
	}

	public static final byte[] generateRandomKeys(final int size) throws NoSuchAlgorithmException {
		SecureRandom secureRandom = SecureRandom.getInstanceStrong();
		byte[] randomBytes = new byte[size];
		secureRandom.nextBytes(randomBytes);
		return randomBytes;
	}

	private final byte[] mergeArray(byte[] data1, byte[] data2) {
		final byte[] merged = new byte[data1.length + data2.length];

		System.arraycopy(data1, 0, merged, 0, data1.length);
		System.arraycopy(data2, 0, merged, data1.length, data2.length);

		return merged;
	}

	private static final byte[] subArray(byte[] data, int start, int length) {
		if (start + length > data.length) {
			throw new IllegalArgumentException("Cannot extract " + length + " bytes starting on start index " + start
					+ " from data with length " + data.length);
		}

		byte[] extracted = new byte[length];
		System.arraycopy(data, start, extracted, 0, length);

		return extracted;
	}

	public static class Vault {
		private final CipherUtil cipher;
		private final String password;

		public Vault(CipherUtil cipher, String p) {
			this.cipher = cipher;
			this.password = p;
		}

		public Vault(CipherUtil cipher, File fileKey) {
			this.cipher = cipher;
			this.password = getCipherKey(fileKey);
		}

		public final String get(String cipherText) {
			try {
				return cipher.decrypt(cipherText, password);
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}

		private final String getCipherKey(File f) {
			try (FileReader fr = new FileReader(f); BufferedReader br = new BufferedReader(fr);) {
				return br.readLine();
			} catch (Throwable t) {
				String path = f != null ? f.getAbsolutePath() : null;
				throw new RuntimeException("Error while reading key file - " + path, t);
			}
		}

	}
	
	public static void main(String[] args) throws Exception {
		
		CipherUtil cipherUtil = CipherUtil.createInstance();
		
		String plainText = "Small Brown Fox Jump to Lazy Dog";
		
		String cipherPasswd = "WinterIsComing";
		
		String cipherText = cipherUtil.encrypt(plainText, cipherPasswd);
		
		System.out.println("cipherText = " + cipherText);
		
		String decipherText = cipherUtil.decrypt(cipherText, cipherPasswd);
		
		System.out.println("decipherText = " + decipherText);
	}
	
}
