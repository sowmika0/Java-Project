package com.temenos.arc.security.authticketclient.common;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
/** Class to generate the public and private key pair in files*/
public class GeneratePublic {
	/** Main method to generate publickey from keystore
	 * 
	 * @param args
	 * @throws java.io.IOException
	 * @throws Exception
	 */
	public static void main(String[] args) throws java.io.IOException, Exception{
		//keytool -genkey -keyalg RSA -keysize 2048 -storetype JCEKS -keystore C:\RSAKey\rsa-keytool.ks -alias ticket -storepass ticket -keypass ticket
		String strKeyStorePath = args[0]; //KeyStore location
		String strPublicKeyPath = args[1]; //To be saved public key location
		String strStorePass = args[2];  //Key store password
		String strAlias = args[3];     //Alias Name
		String strKeyPass = args[4];   //Key password
		
		//Initialsing the KeyStore file
		KeyStore store = KeyStore.getInstance("JCEKS");
  		FileInputStream fis = new FileInputStream(strKeyStorePath);
  		store.load(fis, strKeyPass.toCharArray());
		
		//Retriving the keyPair from keystore
  		KeyPair kp= getKeyPair(store,strAlias,strStorePass.toCharArray());
		//Retriving the public key from keypair
		PublicKey pubKey = kp.getPublic();
		byte[] x509Bytes = pubKey.getEncoded();
		//Generates public key file
		FileOutputStream fOut1 = new FileOutputStream(strPublicKeyPath);
		System.out.println("The Public Key is generated in the location " + strPublicKeyPath);
		fOut1.write(x509Bytes);
		fOut1.close();
	}
	/** To generate Keypair from the Keystore
	 * 
	 * @param keystore - KeyStore object
	 * @param alias    - alias name
	 * @param password - keystore password
	 * @return
	 */
	public static KeyPair getKeyPair(KeyStore keystore, String alias, char[] storePass) {
        try {
            // Get private key
            Key key = keystore.getKey(alias, storePass);
            if (key instanceof PrivateKey) {
                // Get certificate of public key
                java.security.cert.Certificate cert = keystore.getCertificate(alias);
                // Get public key
                PublicKey publicKey = cert.getPublicKey();
                // Return a key pair
                return new KeyPair(publicKey, (PrivateKey)key);
            }
        } catch (UnrecoverableKeyException e) {
        } catch (NoSuchAlgorithmException e) {
        } catch (KeyStoreException e) {
        }
        return null;
    }

}
