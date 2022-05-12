package com.temenos.arc.security.authticketclient.common;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

import com.temenos.arc.security.authticketclient.common.AESRSACryptoService;

public class AESRSACryptoServiceTest {
	
	AESRSACryptoService testObj = new AESRSACryptoService();
	@Test
	public void testGenerateSecretKey() throws NoSuchAlgorithmException {
		Key secretKey = testObj.generateSecretKey(256);
		if("AES".equals(secretKey.getAlgorithm()))
			assert(true);
		else
			fail("Wrong Algorithm Used");
	}

	@Test
	public void testCreateDigest() throws NoSuchAlgorithmException {
		byte[] dataBytes = "<?xml version='1.0' encoding='UTF-8'?><sfedid:Assertion ExpireTime='2001-12-31T12:00:00' IssueInstant='2001-12-31T12:00:00' UniqueID='1000' Version='1.0' xmlns:sfedid='http://www.swedbank.se/2008/sfedid' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.swedbank.se/2008/sfedid sfedid.xsd '><sfedid:Subject UserID='Sarvanan'/><sfedid:Extension/></sfedid:Assertion>".getBytes();
		byte[] digest1Bytes = testObj.createDigest(dataBytes); 
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		byte[] digest2Bytes = sha.digest(dataBytes);
		boolean expected = java.util.Arrays.equals(digest1Bytes, digest2Bytes);
		if(expected)
			assert(true);
		else
			fail("Digest does not match");
	}

	@Test
	public void testGeneratePublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchProviderException {
		Key secretKey = testObj.generatePublicKey();
		if("RSA".equals(secretKey.getAlgorithm()))
			assert(true);
		else
			fail("Wrong Algorithm Used");
	}

	@Test
	public void testAesDecrypt() throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException {
		byte[] dataBytes = "<?xml version='1.0' encoding='UTF-8'?><sfedid:Assertion ExpireTime='2001-12-31T12:00:00' IssueInstant='2001-12-31T12:00:00' UniqueID='1000' Version='1.0' xmlns:sfedid='http://www.swedbank.se/2008/sfedid' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.swedbank.se/2008/sfedid sfedid.xsd '><sfedid:Subject UserID='Sarvanan'/><sfedid:Extension/></sfedid:Assertion>".getBytes();
		Key secretKey = testObj.generateSecretKey(256);
		byte[] encryBytes = testObj.aesEncrypt(dataBytes, secretKey, "AES");
		byte[] decryBytes = testObj.aesDecrypt(encryBytes, secretKey, "AES");
		boolean expected = java.util.Arrays.equals(decryBytes, dataBytes);
		if(expected)
			assert(true);
		else
			fail("Values does not match");
	}

	@Test
	public void testRsaDecrypt() throws InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException, IOException, NoSuchProviderException, KeyStoreException, UnrecoverableKeyException, CertificateException {
		byte[] dataBytes = "<?xml version='1.0' encoding='UTF-8'?><sfedid:Assertion ExpireTime='2001-12-31T12:00:00' IssueInstant='2001-12-31T12:00:00' UniqueID='1000' Version='1.0' xmlns:sfedid='http://www.swedbank.se/2008/sfedid' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.swedbank.se/2008/sfedid sfedid.xsd '><sfedid:Subject UserID='Sarvanan'/><sfedid:Extension/></sfedid:Assertion>".getBytes();
		Key secretKey = testObj.generateSecretKey(256);
		byte[] encryBytes = testObj.aesEncrypt(dataBytes, secretKey, "AES");
		PublicKey publicKey = testObj.generatePublicKey();
		byte[] encryKeyBytes = testObj.rsaEncrypt(secretKey.getEncoded(), publicKey, "Digest");
		testObj.keystorePath = "C:/RSAKey/rsa-keytool1.ks";
		PrivateKey privateKey = testObj.genPrvKeyFromStore();
		byte[] decryKeyBytes = testObj.rsaDecrypt(encryKeyBytes, privateKey, "Digest");
		SecretKey afterkey =new SecretKeySpec(decryKeyBytes,"AES");
		byte[] decryBytes = testObj.aesDecrypt(encryBytes, afterkey, "AES");
		System.out.println(new String(decryBytes));
		boolean expected = java.util.Arrays.equals(decryBytes, dataBytes);
		if(expected)
			assert(true);
		else
			fail("Values does not match");
		
	}

}
