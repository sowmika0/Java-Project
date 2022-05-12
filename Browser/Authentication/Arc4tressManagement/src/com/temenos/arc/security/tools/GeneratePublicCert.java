package com.temenos.arc.security.tools;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/** Class to generate the public and private key pair in files*/
public class GeneratePublicCert {
	/** Main method to generate publickey and public certificate from keystore
	 * 
	 * @param args
	 * @throws java.io.IOException
	 * @throws Exception
	 */
	public static void main(String[] args) throws java.io.IOException, Exception{
		//keytool -genkey -keyalg RSA -keysize 2048 -storetype JCEKS -keystore C:\RSAKey\rsa-keytool.ks -alias ticket -storepass ticket -keypass ticket
		String strKeyStorePath = args[0]; //KeyStore location
		String strPublicKeyPath = args[1]; //To be saved public key location
		String strAlias = args[2];     //Alias Name
		String strStorePass = args[3];   //Store password
		
		//Initialsing the KeyStore file
		KeyStore store = KeyStore.getInstance("JCEKS");
  		FileInputStream fis = new FileInputStream(strKeyStorePath);
  		store.load(fis, strStorePass.toCharArray());
        java.security.cert.Certificate cert = store.getCertificate(strAlias);		
        try {
            // Get the encoded form which is suitable for exporting
            byte[] buf = cert.getEncoded();
    
            FileOutputStream os = new FileOutputStream(strPublicKeyPath);
            boolean binary = false;
            if (binary) {
                // Write in binary form
                os.write(buf);
            } else {
                // Write in text form
                Writer wr = new OutputStreamWriter(os, Charset.forName("UTF-8"));
                wr.write("-----BEGIN CERTIFICATE-----\n");
                wr.write(new sun.misc.BASE64Encoder().encode(buf));
                wr.write("\n-----END CERTIFICATE-----\n");
                wr.flush();
            }
            os.close();
        } catch (CertificateEncodingException e) {
        } catch (IOException e) {
        }
    
        PublicKey pubKey = cert.getPublicKey();
		byte[] x509Bytes = pubKey.getEncoded();
		//Generates public key file
		FileOutputStream fOut = new FileOutputStream(strPublicKeyPath.substring(0,strPublicKeyPath.indexOf("."))+".key");
		System.out.println("The Public Certificate is generated in the location : " + strPublicKeyPath);
		System.out.println("The Public Key is generated in the location : " + strPublicKeyPath.substring(0,strPublicKeyPath.indexOf("."))+".key");
		fOut.write(x509Bytes);
		fOut.close();

	}
	public static java.security.cert.Certificate importCertificate(File file) {
        try {
            FileInputStream is = new FileInputStream(file);
    
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            java.security.cert.Certificate cert = cf.generateCertificate(is);
            return cert;
        } catch (CertificateException e) {
        } catch (IOException e) {
        }
        return null;
    }
}
