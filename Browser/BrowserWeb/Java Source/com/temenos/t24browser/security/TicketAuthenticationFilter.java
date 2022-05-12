package com.temenos.t24browser.security;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar ;
import java.util.Date;
import java.util.Map;
import com.temenos.t24browser.exceptions.TicketAuthenticationException;


import com.temenos.arc.security.authenticationserver.common.AESRSACryptoService;
import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.filter.LoginParameterisedRequest;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import com.temenos.t24browser.security.T24Principal;
import com.temenos.t24browser.security.SSOPrincipal;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.text.ParseException;

import org.apache.commons.codec.binary.Base64;

public class TicketAuthenticationFilter implements Filter {
		/** The logger. */
		private static Logger logger = LoggerFactory.getLogger(TicketAuthenticationFilter.class);
		//COnfiguration Object for reding the config file
		AuthenticationServerConfiguration authTicketConfig = null;
		
		/* (non-Javadoc)
		 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
		 */
		public void init(FilterConfig arg0) throws ServletException 
		{
			logger.info("Initialising TicketAutenticationFilter...");
		}

		/**
	    * no-op.
	    */
		public void destroy() 
		{
		}
		
		/** Decrypts the incoming encrypted Key and encrypted message using RSA and AES algorithm
		 *  Creates the digest out of the recieved message and checks against the recieved digest
		 *  If same , creates ssoPrincipal and puts the same in to the session object
		 */
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException 
		{
			authTicketConfig = getConfig(0);
			SSOPrincipal ssoPrincipal = null; 
			LoginParameterisedRequest wrapper = null;
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			//Creating a new session
			HttpSession session = httpServletRequest.getSession(true);
			logger.info("Session successfully created");
			
			//If session already has ssoPrincipal then bypassing the decrypting process
			Object objSSOPrincipal = session.getAttribute("ssoPrincipal");
			if(objSSOPrincipal!=null && objSSOPrincipal instanceof SSOPrincipal)
			{
				logger.info("Session contains SSOPrincipal, bypassing decrypting process");
				filterChain.doFilter(request, response);
				return;
			}
			
			//Getting the input parameters from request object
			String strEncMsg = request.getParameter("enc_message");
			String strEncKey = request.getParameter("enc_key");
			String strEncDigest = request.getParameter("signature");
			
			if(strEncMsg!=null && !strEncMsg.equals("")){
				logger.debug("encrypted message recieved : "+ strEncMsg);
			}else{
				logger.debug("encrypted message is null");
			}
			if(strEncKey!=null && !strEncKey.equals("")){
				logger.debug("encrypted key recieved : "+ strEncKey);
			}else{
				logger.debug("encrypted key is null");
			}
			if(strEncDigest!=null && !strEncDigest.equals("")){
				logger.debug("encrypted digest recieved : "+ strEncDigest);
			}else{
				logger.debug("encrypted digest is null");
			}
			PrivateKey privateKey = null;
			PublicKey publicKey = null;
			
			try
			{
				//AESRSACryptoService object contains method to encrypt and decrypt
				AESRSACryptoService aesRsaCryptoService = new AESRSACryptoService(authTicketConfig);
				Base64 decoder = new Base64();
				
				byte[] encodedKeyBytes = strEncKey.getBytes();
				byte[] encodedMsgBytes = strEncMsg.getBytes();
				byte[] encodedDigBytes = strEncDigest.getBytes();
				
				byte[] encMsgBytes = decoder.decode(encodedMsgBytes);
				byte[] encDigBytes = decoder.decode(encodedDigBytes);
				byte[] encKeyBytes = decoder.decode(encodedKeyBytes);
				logger.debug("Decoding is successful");
				
				//generating private key from the keystore
				privateKey = aesRsaCryptoService.genPrvKeyFromStore();
				logger.debug("Private key generated");
				//Decrypting the encrypted symmetric key using RSA OAEP padding algorithm with IV 128 bits
				byte[] decKeyBytes = aesRsaCryptoService.rsaDecrypt(encKeyBytes, privateKey, "Key");
				logger.info("Decrypting the symmetric is successful using RSA OAEP algorithm");
				
				//AES symmetric key generated from decrypted key bytes
				SecretKey decryptedkey =new SecretKeySpec(decKeyBytes,"AES");
				logger.debug("AES Symmetric key generated");
				
				//decrypting the message and digest using the symmetric key
				byte[] decMsgBytes = aesRsaCryptoService.aesDecrypt(encMsgBytes, decryptedkey, "AES");
				
				//generating the Public Key
				publicKey = aesRsaCryptoService.generatePublicKey();
				//Decrypting the digest using the public key  
				byte[] decDigBytes = aesRsaCryptoService.rsaDecrypt(encDigBytes, publicKey, "Digest");
				logger.info("Decrypting the message and digest is successful");
				
				//creating a local digest from the recieved message
				byte[] digBytes = aesRsaCryptoService.createDigest(decMsgBytes); 
				//Checking the recieved digest and created digest are same
				boolean isSameDigest = java.util.Arrays.equals(decDigBytes, digBytes);
				
				//Wrap the request.  Wrapper enables put() method
				wrapper = new LoginParameterisedRequest(httpServletRequest);
				wrapper.put("command", "login");
				wrapper.put("requestType", "CREATE.SESSION");
		        wrapper.put("counter", "0");
		        //wrapper.put("signOnName", t24UserName);
		        //wrapper.put("password", t24PassPhrase);	 
				logger.debug("command,requestType,counter added in request parameters");
				if(isSameDigest)
				{
					//Validating the recieved XML message against the XML schema
					ssoPrincipal = validateXML(decMsgBytes);
					if(session != null && ssoPrincipal!=null)
					{
						session.setAttribute("ssoPrincipal", ssoPrincipal);
						logger.info("ssoPrincipal added to session");
					}else{
						logger.error("Session or ssoPrincipal is null");
					}
			        
				}else {
					logger.error("Recieved and created digest does not match");
				}
				//Putting the wrapper in the filter chain
				filterChain.doFilter(wrapper, response);
				
			}catch(ParserConfigurationException e){
				logger.error("Error: ParserConfigurationException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (NoSuchAlgorithmException e) {
				logger.error("Error: NoSuchAlgorithmException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (ParseException e) {
				logger.error("Error: ParseException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (SAXException e) {
				logger.error("Error: SAXException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (InvalidKeyException e) {
				logger.error("Error: InvalidKeyException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (IllegalBlockSizeException e) {
				logger.error("Error: IllegalBlockSizeException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (NoSuchPaddingException e) {
				logger.error("Error: NoSuchPaddingException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (BadPaddingException e) {
				logger.error("Error: BadPaddingException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (InvalidAlgorithmParameterException e) {
				logger.error("Error: InvalidAlgorithmParameterException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (KeyStoreException e) {
				logger.error("Error: KeyStoreException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (CertificateException e) {
				logger.error("Error: CertificateException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (UnrecoverableKeyException e) {
				logger.error("Error: UnrecoverableKeyException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (InvalidKeySpecException e) {
				logger.error("Error: InvalidKeySpecException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (NoSuchProviderException e) {
				logger.error("Error: NoSuchProviderException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (ClassNotFoundException e) {
				logger.error("Error: ClassNotFoundException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (InstantiationException e) {
				logger.error("Error: InstantiationException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			} catch (IllegalAccessException e) {
				logger.error("Error: IllegalAccessException " + e);
				session.invalidate();
				throw new TicketAuthenticationException();
			}
			
		}
		
		/** Method to validate the XML recieved from the Test Client
		 * 
		 * @param decMsgBytes - Contains the Decrypted XML message from Test Client
		 * @return ssoPrincipal -contains the user id
		 * @throws ParserConfigurationException
		 * @throws ParseException
		 * @throws FileNotFoundException
		 * @throws IOException
		 * @throws SAXException
		 */
		private SSOPrincipal validateXML(byte[] decMsgBytes) throws ParserConfigurationException, ParseException, FileNotFoundException, IOException, SAXException {
			Node tempNode = null;
			
			//Setting the factory class to instanciate from com.sun.org.apache.xerces.internal.jaxp
			System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			logger.info("DocumentBuilderFactory: "+ factory.getClass().getName());
			
			factory.setNamespaceAware(true);
			factory.setValidating(true);
			factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
			//Setting the schema location
			String strXmlSchemaLocation = "file:"+authTicketConfig.getConfigValue(AuthenticationServerConfiguration.XML_SCHEMA);
			logger.info("Schema Location retrieved from the config file");
			factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", strXmlSchemaLocation);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			//creating a temporary xml file from the xml message recieved for validation
			File xmlMsgFile = new File("temp.xml");
			FileOutputStream  xmlFileStream = new FileOutputStream(xmlMsgFile);
			xmlFileStream.write(decMsgBytes);
			
			//Validating the xml file againt the schema and getting the root node
			Document document = builder.parse(xmlMsgFile);
			logger.info("XML validation Successful");
			Node rootNode  = document.getFirstChild();
			NamedNodeMap attribMap= rootNode.getAttributes();
			
			//Getting the value of the attribute ExpireTime
			tempNode = attribMap.getNamedItem("ExpireTime");
			String strExpireTime = tempNode.getNodeValue();
			logger.debug("ExpireTime retrived from XML");
			
			//Getting the value of the attribute IssueInstant
			tempNode = attribMap.getNamedItem("IssueInstant");
			String strIssueInstant = tempNode.getNodeValue();
			logger.debug("IssueTime retrived from XML");
			
			//Converting from yyyy-MM-ddThh:mm:ss to yyyy-MM-dd hh:mm:ss
			strExpireTime = strExpireTime.replace("T", " ");
			strIssueInstant = strIssueInstant.replace("T", " ");
			
			logger.info("ExpireTime recieved:" + strExpireTime);
			logger.info("IssueInstant recieved:" + strIssueInstant);
			
			//Validating the ticket against the ExpireTime and Issue Time
			SSOPrincipal ssoPrincipal = validateTicket(rootNode, strExpireTime, strIssueInstant);
			return ssoPrincipal;
			
		}
		/** Method to validate the Ticket against the expire time and issue time and create the ssoPrincipal
		 * 
		 * @param rootNode - Reference to the root element in the XML message
		 * @param strExpireTime - String form of exprie time of ticket
		 * @param strIssueInstant - String form of issue time of ticket
		 * @return ssoPrinical - conatins the ldap user id
		 * @throws ParseException
		 */
		private SSOPrincipal validateTicket(Node rootNode, String strExpireTime, String strIssueInstant) throws ParseException {
			Node tempNode;
			SSOPrincipal ssoPrincipal = null;
			T24Principal t24Principal = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			//Creating Date object from String format of expire and issue time
			Date expireDateTime = sdf.parse(strExpireTime);
			Date issueDateTime = sdf.parse(strIssueInstant);
			Date tempDateTime = new Date();
			
			String strTempDateTime = sdf.format(tempDateTime);
		    Date curDateTime = sdf.parse(strTempDateTime);
		    logger.debug("Current time generated");
		    
		    //Ticket is valid if issueTime <= currentTime <= expireTime
		    if (curDateTime.getTime() <= expireDateTime.getTime() && curDateTime.getTime() >= issueDateTime.getTime())
			{
				tempNode = rootNode.getFirstChild().getAttributes().getNamedItem("UserID");
				String userId = tempNode.getNodeValue();
				logger.debug("user id retrived from the XML message");
				//retriving the DN pattern form the config file
				String strDNPattern = authTicketConfig.getConfigValue(AuthenticationServerConfiguration.DN_PATTERN);
				//replacing the string "userid" to actuall userid in DN
				String strDN = strDNPattern.replaceFirst("<userid>",userId);
				//creating the T24Principal by setting the DN in name
				t24Principal = new T24Principal(strDN);
				//Creating the ssoPrinicpal
				ssoPrincipal = new SSOPrincipal();
				logger.debug("sso Principal created");
				//adding the T24Principal into the ssoPrincipal
				ssoPrincipal.setSSOPrincipal(t24Principal);
				logger.info("Ticket is Valid");
				logger.info("DN set in ssoPrincipal:" + t24Principal.getName());
				return ssoPrincipal;
			}else {
				logger.error("Invalid Ticket or Ticket expired");
				throw new TicketAuthenticationException();
			}
		}
		public AuthenticationServerConfiguration getConfig(int section) {
			String configFile = System.getProperty("ARC_CONFIG_PATH");
			if(configFile != null && !configFile.equals("")){
				logger.debug("Config file path retrived : " + configFile);
			}else{
				logger.error("ARC_CONFIG_PATH not set in System Property");
			}
			String appName = System.getProperty("ARC_CONFIG_APP_NAME");
			if(appName != null && !appName.equals("")){
				logger.debug("Application name retrived : " + appName);
			}else{
				logger.error("ARC_CONFIG_APP_NAME not set in System Property");
			}
			ConfigurationFileParser parser = new ConfigurationFileParser(configFile, appName);
			logger.debug("parser object created from configfile");
			Map[] configMap = parser.parse();
			logger.debug("ConfigMap is created by parsing the configfile");
			return new AuthenticationServerConfiguration(configMap[section]);
		}
}

