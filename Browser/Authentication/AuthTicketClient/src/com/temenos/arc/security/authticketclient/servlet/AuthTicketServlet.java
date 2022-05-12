package com.temenos.arc.security.authticketclient.servlet;

import java.io.IOException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import com.temenos.arc.security.authticketclient.common.AESRSACryptoService;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;


/**
 * Servlet implementation class for Servlet: ClientServlet
 *
 */
 public class AuthTicketServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	public static final String SUBMIT_PAGE = "/jsps/submit.jsp";
	public AESRSACryptoService cryptoService = new AESRSACryptoService();
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(AuthTicketServlet.class);
	 /** Constructor for the ClientServlet
	  */
	public AuthTicketServlet() {
		super();
		logger.info("Constructing the ClientServlet");
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}  	
	
	/** Creates the XML message (AuthenticationTicket), creates digest for the same
	 *  Encrypts the XML message (1) and the digest(2) using a AES symmetric key 
	 *  Encrypts the symmetric key(3) used, using a RSA public key
	 *  Encodes 1,2,3 in base64 and puts in the session and submits to the submit page
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Retriving the t24userid form request object
		String strMessage = request.getParameter("AuthTicket");
		
		if(strMessage!=null && !strMessage.equals(""))
		{
			logger.info("User id not null");
		}else
		{
			logger.error("user id is null");
		}
		//Time for Ticket to get expired in minutes
		int minutesToAdd = 5; 
		
		//Creating the expireTime and IssueTime of the ticket
		Date issueDate = new Date();
		Calendar cal = Calendar.getInstance();
        cal.setTime(issueDate);
        cal.add(Calendar.MINUTE, minutesToAdd);
        Date expireDate = cal.getTime();
        //SimpleDateFormat for converting the date format to "yyyy-MM-dd hh:mm:ss"
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	    
		//Converting to the format yyyy-MM-ddThh:mm:ss from yyyy-MM-dd hh:mm:ss
		String strIssueDateTime = sdf.format(issueDate);
	    strIssueDateTime = strIssueDateTime.replace(" ", "T");
	    
	    String strExpireDateTime = sdf.format(expireDate);
	    strExpireDateTime = strExpireDateTime.replace(" ", "T");
	    
		//Creating the XML message(AuthenticationTicket)
	    StringBuffer strBufXML = new StringBuffer("<?xml version='1.0' encoding='UTF-8'?>");
		strBufXML.append("<sfedid:Assertion ExpireTime='");
		strBufXML.append(strExpireDateTime);
		strBufXML.append("' IssueInstant='");
		strBufXML.append(strIssueDateTime);
		strBufXML.append("' UniqueID='1000' Version='1.0'");
		strBufXML.append(" xmlns:sfedid='http://www.swedbank.se/2008/sfedid' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.swedbank.se/2008/sfedid sfedid.xsd'><sfedid:Subject UserID='");
		strBufXML.append(strMessage);
		strBufXML.append("'/><sfedid:Extension/></sfedid:Assertion>");
		
		String strXMLMesaage = strBufXML.toString();
		
		byte[] dataBytes = null;
		byte[] encryDataBytes = null;
		byte[] encodedKeyBytes = null;
		byte[] encryKeyBytes = null;
		byte[] digestBytes = null;
		byte[] encryDigestBytes = null;
		Key secretKey = null;
		//Setting keysize to 256
		int keySize = 256;
		PublicKey publicKey = null;
		PrivateKey privateKey = null;
		try
		{
			//Converting message string into databytes
			dataBytes = strXMLMesaage.getBytes();
			//Generating a secret key
			secretKey = cryptoService.generateSecretKey(keySize);
			logger.info("Key Generated");
			//Creating Digest of dataBytes
			digestBytes = cryptoService.createDigest(dataBytes);
			logger.info("Digest Generated");
			//Encrypting the Data using AES algorithm
			encryDataBytes = cryptoService.aesEncrypt(dataBytes, secretKey, "AES");
			logger.info("Message Encrypted");
        	//Getting the encoded bytes of AES symmetric key
			encodedKeyBytes = secretKey.getEncoded();
        	logger.info("Key Encoded");
        	//Generating the public Key
        	publicKey = cryptoService.generatePublicKey();
        	logger.info("Public Key Created");
        	//Encrypting the key with RSA OAEP padding
			encryKeyBytes = cryptoService.rsaEncrypt(encodedKeyBytes,publicKey,"Key");
			logger.info("Key Encrypted with padding");
			
			//generating private key from the keystore
			privateKey = cryptoService.genPrvKeyFromStore();
			logger.debug("Private key generated");
			encryDigestBytes = cryptoService.rsaEncrypt(digestBytes, privateKey,"Digest");
			logger.info("Digest Encrypted");
			
			Base64 encoder = new Base64();
			byte[] encodDataBytes = encoder.encode(encryDataBytes);
			byte[] encodDigestBytes = encoder.encode(encryDigestBytes);
			byte[] encodkeyBytes = encoder.encode(encryKeyBytes);
			logger.info("All encoded to base 64");
			logger.info("After Encoding to base 64 :"+ new String(encodkeyBytes));
			request.getSession().setAttribute("encodDataBytes", new String(encodDataBytes));
			request.getSession().setAttribute("encodDigestBytes", new String(encodDigestBytes));
			request.getSession().setAttribute("encodkeyBytes", new String(encodkeyBytes));
			logger.info("All set to request parapmeter");
			request.getRequestDispatcher(SUBMIT_PAGE).forward(request, response);
			
		}
        catch(Exception e)
        {
        	e.printStackTrace();
        }
	} 
}