package com.temenos.arc.security.authticketclient.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

public class AESRSACryptoService {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(AESRSACryptoService.class);
	protected KeyStore store = null;
	protected String keystorePassword = "ticket";
    protected String keystorePath = "C:/RSAKey/rsa-keytool2.ks";
    protected String keyPassword = "ticket";
    protected String alias = "ticket";
    protected String keyAlgorithm = "RSA/NONE/OAEPWithSHA384AndMGF1Padding";
    protected String publicKeyPath = "C:/RSAKey/Public1.key";
    protected String signatureAlgorithm = "RSA/NONE/PKCS1Padding";

	/** To generate the symmertic key of given keysize
	 * 
	 * @param keySize - size of the Symmetric Key
	 * @return - AES Symmetric key with size as keysize
	 * @throws NoSuchAlgorithmException
	 */
	public Key generateSecretKey(int keySize) throws NoSuchAlgorithmException {
		//Generating the KeyGenerator with AES algorithm
		KeyGenerator kg = KeyGenerator.getInstance("AES");
		//initialise the keysize
		kg.init(keySize);
		logger.debug("Initialsed Keysize");
		Key secretKey = kg.generateKey();
		logger.debug("Secret key generated");
		return secretKey;
	}
	
	/**To create the digest for the given message, uses SHA-1 algorithm
	 * 
	 * @param dataBytes - Input message bytes
	 * @return - Message Digest using SHA-1 algorithm
	 * @throws NoSuchAlgorithmException
	 */
	public byte[] createDigest(byte[] dataBytes) throws NoSuchAlgorithmException {
		byte[] digestBytes=null;
		//Calculating the message digest of the message
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		if(sha != null){
			digestBytes = sha.digest(dataBytes);
			logger.debug("Digest generated");
		}else{
			logger.error("Error in creating the Message Digest Object");
		}
		return digestBytes;	
	}
	
	/**To generate the public key from the file path given
	 * 
	 * @param keyPath - location of the public key file
	 * @return - RSA public key
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchProviderException
	 */
	public PublicKey generatePublicKey() throws IOException,NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException	{
		PublicKey publicKey = null;
		KeyFactory keyFactory = null;
		//Creating a file object to public key path
		File pubKeyFile = new File(publicKeyPath);
		//Getting the file size to create a byte array
		byte[] pubEncKey = new byte[(int)pubKeyFile.length()];
		//reading the file into bytes
	    new FileInputStream(pubKeyFile).read(pubEncKey);
	    logger.debug("Public Key read from the file");
	    //creating a public key from the bytes
	    X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubEncKey);
	    System.out.println("Public Key Spec Created");
	    //adding service provider as Bouncy castle
	    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		keyFactory = KeyFactory.getInstance("RSA","BC");
		if(keyFactory != null)
		{
			logger.debug("KeyFactory Generated");	
		}else{
			logger.error("Error creating KeyFactory Object");	
		}
		publicKey = keyFactory.generatePublic(pubKeySpec);
		if(publicKey != null)
		{
			logger.debug("PublicKey Generated");	
		}else{
			logger.error("Error creating PublicKey Object");	
		}
		return publicKey;
	}
	
	/**To decrypt the given input bytes using the symmetric key using AES algorithm
	 * 
	 * @param inpBytes
	 * @param key
	 * @param xform
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws NoSuchPaddingException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public byte[] aesDecrypt(byte[] inpBytes, Key key, String xform) throws
		NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException,
		NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException{
		Cipher cipher = Cipher.getInstance(xform);
		logger.debug("aesDecrypt : Cipher instantiated");
		cipher.init(Cipher.DECRYPT_MODE, key);
		logger.debug("aesDecrypt : Cipher initialied");
		return cipher.doFinal(inpBytes);
	}
	
	/**To decrypt the given input bytes using the Asymmetric key using RSA OAEP algorithm
	 * 
	 * @param inpBytes
	 * @param key
	 * @param type - to specify which algorithm to use one for encrypting the key and one for digest
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws NoSuchPaddingException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public byte[] rsaDecrypt(byte[] inpBytes, Key key, String type) throws
		NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException,
		NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException{
		Cipher cipher = null;
		//deciding the algorithm based the data if digest PKCS and for key OAEP
		if (type.equalsIgnoreCase("Digest")){
			cipher = Cipher.getInstance(signatureAlgorithm);
		}else if(type.equalsIgnoreCase("Key")){
			cipher = Cipher.getInstance(keyAlgorithm);
		}
		logger.debug("rsaDecrypt : Cipher instantiated");
		cipher.init(Cipher.DECRYPT_MODE, key);
		logger.debug("rsaDecrypt : Cipher initialied");
		return cipher.doFinal(inpBytes);
	}
		
	/**To encrypt the given input bytes using the symmetric key using AES algorithm
	 * 
	 * @param inpBytes
	 * @param key
	 * @param xform
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws NoSuchPaddingException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public byte[] aesEncrypt(byte[] inpBytes, Key key, String xform) throws
		NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException,
		NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException {
		Cipher cipher = Cipher.getInstance(xform);
		logger.debug("aesEncrypt : Cipher instantiated");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		logger.debug("aesEncrypt: Cipher initialied");
		return cipher.doFinal(inpBytes);
	}
	
	/**To encrypt the given input bytes using the Asymmetric key using RSA OAEP algorithm
	 * 
	 * @param inpBytes
	 * @param key
	 * @param type - to specify which algorithm to use one for encrypting the key and one for digest
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws NoSuchPaddingException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */
	public byte[] rsaEncrypt(byte[] inpBytes, Key key, String type) throws
		NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException,
		NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException {
		Cipher cipher = null;
		//deciding the algorithm based the data if digest PKCS and for key OAEP	
		if (type.equalsIgnoreCase("Digest")){
			cipher = Cipher.getInstance(signatureAlgorithm);
		}else if(type.equalsIgnoreCase("Key")){
			cipher = Cipher.getInstance(keyAlgorithm);
		}
		logger.debug("rsaEncrypt : Cipher instantiated");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		logger.debug("rsaEncrypt : Cipher initialied");
		return cipher.doFinal(inpBytes);
	}
	/** To generate the private key from the Keystore
	 * 
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableKeyException
	 * @throws IOException 
	 * @throws CertificateException 
	 */
	public PrivateKey genPrvKeyFromStore() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, IOException {
		Key key = null;
		store = KeyStore.getInstance("JCEKS");
		logger.debug("keystore created");
	    FileInputStream fis = new FileInputStream(keystorePath);	
	    logger.debug("got file input stream for keystore path");
	    store.load(fis, keyPassword.toCharArray());	
	    logger.debug("keystore loaded");
		//retriving private key from keystore
		key = store.getKey(alias, keystorePassword.toCharArray());
		if (key instanceof PrivateKey){
        	logger.debug("Private key retrived from keystore");
        	return (PrivateKey)key;
		}else{
			logger.error("Not a private key");
			return null;
		}
    }
}
