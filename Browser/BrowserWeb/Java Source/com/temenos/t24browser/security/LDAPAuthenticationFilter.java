package com.temenos.t24browser.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

import com.temenos.arc.security.authenticationserver.common.AuthenticationServerConfiguration;
import com.temenos.arc.security.authenticationserver.common.ConfigurationFileParser;
import com.temenos.arc.security.filter.LoginParameterisedRequest;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.exceptions.LDAPAuthenticationException;
import com.temenos.t24browser.exceptions.LDAPServiceNotAvailableException;

public class LDAPAuthenticationFilter implements Filter{
   
	private static Logger logger = LoggerFactory.getLogger(LDAPAuthenticationFilter.class);  
	//Configuration Object for reding the config file
    AuthenticationServerConfiguration ldapConfig = null;
    //Session object
    HttpSession session;
    //JSP for LDAP login
    public static final String LDAP_LOGIN_PAGE = "/jsps/ldapLogin.jsp";
    //JSP for redirecting the login response to BrowserServlet agian, to avoid the frame requests
    public static final String LDAP_REDIRECT_PAGE = "/jsps/ldapRedirect.jsp";
    /**
	  * no-op.
	  */
    public void init(FilterConfig arg0) throws ServletException
    {
    	logger.info("Initialising LDAPAutenticationFilter...");
	}
    /**
	  * no-op.
	  */
    public void destroy()
    {
    	logger.info("Destroying LDAPAutenticationFilter...");
    }
    /**
     * Authenticates user through LDAP and enables Single Sign On
     * Checks if the request is a commandAPI request, gets the pre-authenticated user from the request and enables Single Sign on
     * If the pre-authenticated user is not in the request, rediects to the LDAP login page
     */
    public void doFilter(ServletRequest request, ServletResponse response,FilterChain filterChain) throws IOException,ServletException{
	   String strCommand = null;
	   String strT24Command = null;
	   boolean loginRedirect = false;
	   LoginParameterisedRequest requestWrapper = null;
	   HttpServletRequest httpServletRequest = (HttpServletRequest) request;
	   session = httpServletRequest.getSession(true);
	   //Retrieving the configuration details from the config file to configurationObject
	   ldapConfig = getConfig(0);
	   //Wrapping the request, wrapper enables put() method
	   requestWrapper = new LoginParameterisedRequest(httpServletRequest);
	   //Wrapping the response, wrapper enables reading the contents of the response
	   HttpServletResponse httpResponse = (HttpServletResponse) response;
	   ResponseReaderWrapper responseWrapper = new ResponseReaderWrapper(httpResponse);
	   
	   //Checking for t24command in session if available, restoring it to the request object
	   strT24Command = (String) session.getAttribute("t24command");
	   if(strT24Command !=null && !strT24Command.equals(""))
	   {
		   logger.debug("t24command exist in the session, setting the request parameters for commandapi");
		   storeAttribFromSessionToReq(requestWrapper, session);
		   requestWrapper.put("method", "post");
		   //Clearing the t24command attribute in the session for the subsequent requests
		   session.setAttribute("t24command", "");
	   }
	   //WindowName set to everyrequest, necessary for browserservlet as key to store parameters
	   requestWrapper.put("windowName","newWindow");
	   //If session already has ssoPrincipal then bypassing the DN check
	   Object objSSOPrincipal = session.getAttribute("ssoPrincipal");
	   if(objSSOPrincipal!=null && objSSOPrincipal instanceof SSOPrincipal)
	   {
		   logger.debug("Session contains SSOPrincipal, by passing DN Check");
		   filterChain.doFilter(requestWrapper, responseWrapper);
		   //Checking if the response is T24 Login, if it is redirecting it to the Ldap Login
		   checkResponse(request, response, responseWrapper.toString());
		   return;
	   }
	   //Check if the resquest contains the t24commandapi, then save the command in session
	   strCommand = request.getParameter("command");
	   if(strCommand != null && strCommand.equals("t24commandapi"))
	   {
		   logger.debug("t24commandapi exist in the request");
		   storeParaFromReqToSession(request, session);
	   }
		
	   //Checking for user DN in request
	   logger.debug("Retriving the userDN in the request");
	   String userDN = request.getParameter("userDN");
	   
	   //Retriving the LDAP userName and password from the request
	   logger.debug("Getting the user name and password to authenticate");
	   String userName=request.getParameter("signOnName");
	   String password=request.getParameter("password");	
	   
	   //If userName and password in the request and SSOPrincipal not available in session
	   //Ckeck for SSOPrincipal in the session is done on the top
	   if(userName!=null && password !=null && !userName.equals("") && !password.equals("")){ 
		   //If userName and password available
		   //Checking whether user and password are valid against LDAP
		   if(isValidUser(session, userName)&& isValidPassword(session, userName, password)){
			   logger.debug("UserName and Password are valid, Creating SSO Principal and storing in session");
			   setSSOPrincipalInSession(session, userName);
		   }else{
			   loginRedirect = true;
			   requestWrapper.put("Message", "Incorrect Credentials");
		   }
	   }else if (userDN != null && !userDN.equals("")){//If user DN is available in the cookie and SSOPrincipal not available
		   		//Eg: userDN = "cn=INTUSR5,ou=BankDept1,o=BankA,c=ch";
		   		//Checking the user is valid or not in LDAP
		   		logger.debug("UserDN available in cookie"+ userDN);
		   		//Retriving the user Name from the DN
				try{
					userName = userDN.substring(3,userDN.indexOf(","));   
				}catch(Exception e){
					//Error in retriving the userName from the DN
					userName = null;
				}
				//If ValidUser creating the SSOPrincipal and storing in the session
				if(userName!=null && isValidUser(session, userName)){
					logger.debug("User in the DN is a valid User, Creating SSO Principal and storing in session");
					setSSOPrincipalInSession(session, userName);
		   	   	}else{
		   	   		loginRedirect = true;
		   	   		requestWrapper.put("Message", "Invalid User, Please Enter UserName and Password");
		   	   	}
	   }else{//If nothing is available, redirect to the LDAP login page
		   loginRedirect = true;
		   requestWrapper.put("Message", "You are not logged in, Please Enter UserName and Password");
	   }
	   
	   //Redirecting to login page, if no cookie, error in DN, invalid user, no username & password, invalid username & password
	   if(loginRedirect){ 
		   logger.debug("Redirecting to ldap login page, as no userDN in cookie, no ssoprincipal in session, no username and password in the request");
		   request.getRequestDispatcher(LDAP_LOGIN_PAGE).forward(requestWrapper, response);
		   return;
	   }
	   //Setting the parameters requried for login
	   requestWrapper.put("command", "login");
	   requestWrapper.put("requestType", "CREATE.SESSION");
	   requestWrapper.put("counter", "0");
	   filterChain.doFilter(requestWrapper, responseWrapper);
	   
	   //If the request is commandapi, bypassing the homepage and redirecting to a dummy page for resubmission of the t24command
	   if(strCommand != null && strCommand.equals("t24commandapi"))
	   {
		   logger.debug("Redirecting to dummy page, for processing the command api request");
		   request.getRequestDispatcher(LDAP_REDIRECT_PAGE).forward(request, response);
	   }
	   //Checking if the response is T24 Login, if it is redirecting it to the Ldap Login
	   checkResponse(request, response, responseWrapper.toString());
	}
    
    /**
     * Checks if the response returned from the servlet is T24 Login page, if so redirect to the error page
     * @param resquest - resquest object
     * @param response - response object
     * @param responseString - response content
     * @throws IOException
     */
	private void checkResponse(ServletRequest request, ServletResponse response, String responseString) throws IOException {
		//Scanning the response, if it is T24 login page, throw exception to redirect to error page
		logger.debug("Content of the response ********************************"+responseString);
		String errMsg = "";
		if(responseString.indexOf("T24 Sign")>0 && responseString.indexOf("CREATE.SESSION")>0){
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			session = httpServletRequest.getSession(true);
			int startPos = responseString.indexOf("<span class=\"error\">");
			int endPos = responseString.indexOf("</span>",startPos);
			endPos = responseString.indexOf("</span>",endPos+6);
			if(startPos > 0 && endPos >0)
				errMsg = responseString.substring(startPos,endPos)+"</span>";
			session.setAttribute("LDAPERRMSG", errMsg);
			throw new LDAPAuthenticationException(); //Throwing exception to redirect to the error page
		}else{
			//If not a T24 login page, writing back the response to the printWrite for normal display
			PrintWriter pw = response.getWriter();
			pw.write(responseString);
		}
	}
    
    /**
     * Method store all the attributes in the session as request parameters
     * @param wrapper - wrapped request object, enables put method
     * @param session - valid session object
     */
	private void storeAttribFromSessionToReq(LoginParameterisedRequest wrapper, HttpSession session) {
		Enumeration atrribNames = session.getAttributeNames();
		   while(atrribNames.hasMoreElements()){
				String strAttribName = (String) atrribNames.nextElement();
				Object atrribValue = session.getAttribute(strAttribName);
				logger.info("Attribute Name : " + strAttribName + ", Value : " +atrribValue );
				wrapper.put(strAttribName,atrribValue.toString());
			}
	}
	
	/**
	 * Method to store all the request parameters as session attributes
	 * @param request - request object
	 * @param session - valid session object
	 */
	private void storeParaFromReqToSession(ServletRequest request, HttpSession session) {
		Enumeration paraNames = request.getParameterNames();
		while(paraNames.hasMoreElements()){
			String strParaName = (String) paraNames.nextElement();
			String strParaValue = request.getParameter(strParaName);
			logger.info("Parameter Name : " + strParaName + ", Value : " + strParaValue );
			session.setAttribute(strParaName,strParaValue);
		}
	}
	
	/**
	 * Method to create the SSOPrincipal with LDAP user DN and store it in the session object
	 * @param session - Valid session object
	 * @param userName - authenticated user name
	 */
	private void setSSOPrincipalInSession(HttpSession session, String userName) {
		SSOPrincipal ssoPrincipal;
		T24Principal t24Principal;
		//retriving the DN pattern form the config file
		String strDNPattern = ldapConfig.getConfigValue(AuthenticationServerConfiguration.DN_PATTERN);
		//replacing the string "userid" to actuall userid in DN
		String strDN = strDNPattern.replaceFirst("<userid>",userName);
		//creating the T24Principal by setting the DN in name
		t24Principal = new T24Principal(strDN);
		//Creating the ssoPrinicpal
		ssoPrincipal = new SSOPrincipal();
		logger.debug("sso Principal created");
		//adding the T24Principal into the ssoPrincipal
		ssoPrincipal.setSSOPrincipal(t24Principal);
		logger.info("DN set in ssoPrincipal:" + t24Principal.getName());
		if(session != null){
			session.setAttribute("ssoPrincipal", ssoPrincipal);
			logger.info("ssoPrincipal added to session");
		   	}
	}
	
	/**
	 * Method to validate password against the LDAP
	 * @param session - valid Session Object
	 * @param userName - username entered in ldap login page
	 * @param password - password entered in ldap login page
	 * @return true or false, valid password or not
	 */
	private boolean isValidPassword(HttpSession session, String userName, String password) {
		DirContext dir;
		NamingEnumeration results = null;
		boolean isValidPassword = false;
		Attribute passwordAttribute = null;
		Attributes attributes = null;
		String strLDAPPassword = null;
		String algorithm = null;
		String strUserPassword = null;
		String strAlgoAndPass = null;
		Enumeration passAtrribValList = null;
		//Establishing the LDAP connection
		dir = getLdapConnection();
		if(dir==null){
			throw new LDAPServiceNotAvailableException(); //Throwing exception to redirect to the error page
		}
		logger.debug("Connection established "+dir);
		//creating the search control and setting the scope as SUBTREE
		SearchControls searchControl = new SearchControls();
		//Retriving the password field name from config file
		String passwordField = ldapConfig.getConfigValue(AuthenticationServerConfiguration.PASSWORD_FIELD);
		String[] pwdAttrib = {passwordField};
		//setting the attributes to be return in the search
		searchControl.setReturningAttributes(pwdAttrib);
		searchControl.setSearchScope(SearchControls.SUBTREE_SCOPE);
		try {
			//Searching the userName in the LDAP
			logger.debug("Checking the username in LDAP");
			//Getting the search Dn from the config file
			String searchDN = ldapConfig.getConfigValue(AuthenticationServerConfiguration.SEARCH_DN);
			results = dir.search(searchDN,"cn="+userName,searchControl);
			if(results.hasMore()){
				logger.debug("User is valid, user record found in LDAP");
				SearchResult searchResult = (SearchResult)results.next();
				//Getting all the attributes of the DN
				logger.debug("Getting the attributes in the DN");
				attributes = searchResult.getAttributes();
				//Rretriving the password of the user
				logger.debug("Retriving the password attribute");
				passwordAttribute =  attributes.get(passwordField);
				if(passwordAttribute != null){
					passAtrribValList = passwordAttribute.getAll();
				}else{
					throw new LDAPServiceNotAvailableException(); //Throwing exception to redirect to the error page
				}
				//Retriving all the values in the passwordAtrribute
				
				while(passAtrribValList.hasMoreElements()){
					//Temporary Object to check whether the value coming in is a byte array or String
					Object objTemp = passAtrribValList.nextElement();
					//Eg: {MD5}ZIYFwRvblV4W5/68jbVBAw==
					if(objTemp instanceof byte[])
						strAlgoAndPass = new String((byte[])objTemp); //If byte array converting it to string
					else
						strAlgoAndPass = (String) objTemp; //if not type cating to string
					try{
						//Spliting the string to get the algorithm and encrypted password separately
						logger.debug("Retriving the algorithm and the digest separately");
						algorithm = strAlgoAndPass.split("}")[0];
						strLDAPPassword = strAlgoAndPass.split("}")[1];
						//Removing the leading char '{' to get the algorithm
						algorithm = algorithm.substring(1);
						//Creating the Digest Object out of the algorithm
						MessageDigest objMD = MessageDigest.getInstance(algorithm);
						logger.debug("Digest object created");
						byte[] digestPasswordBytes = objMD.digest(password.getBytes());
						logger.debug("Digest for the user password created");
						Base64 encoder = new Base64();
						//Encoding the digest bytes to compare with the LDAP password
						byte[] encodPasswordBytes = encoder.encode(digestPasswordBytes);
						strUserPassword = new String(encodPasswordBytes);
						logger.debug("UserPassword encoded");
						logger.debug("Comapring the password LDAP password == password entered in screen");
						if(strUserPassword!=null && strUserPassword.equals(strLDAPPassword))
						{
							isValidPassword = true;	
							logger.info("valid password");
							break;
						}
					}catch(NoSuchAlgorithmException e){
						System.out.println("No Such Alogorithm :"+ algorithm);
					}catch(StringIndexOutOfBoundsException e){
						System.out.println("Cannot Split values");
					}
				}
			}
		   	}catch (NamingException e) {
				logger.error("Error: ParserConfigurationException " + e);
				session.invalidate();
		   	}
		return isValidPassword;
	}
	
	/**
	 * Method to validate user against the LDAP
	 * @param session - valid session object
	 * @param userName - username entered in ldap login page
	 * @return true or false, valid user or not
	 */
	private boolean isValidUser(HttpSession session, String userName) {
		DirContext dir = null;
		NamingEnumeration results = null;
		boolean isValidUser = false;
		//Establishing the LDAP connection
		dir = getLdapConnection();
		if(dir==null){
			throw new LDAPServiceNotAvailableException(); //Throwing exception to redirect to the error page
		}
		logger.debug("Connection established "+dir);
		//creating the search control and setting the scope as SUBTREE
		SearchControls searchControl = new SearchControls();
		searchControl.setSearchScope(SearchControls.SUBTREE_SCOPE);
		try {
			//Searching the userName in the LDAP
			logger.debug("Checking the username in LDAP");
			//Getting the search Dn from the config file
			String SearchDN = ldapConfig.getConfigValue(AuthenticationServerConfiguration.SEARCH_DN);
			results = dir.search(SearchDN,"cn="+userName,searchControl);
			if(results.hasMore()){
				logger.debug("User is valid, user record found in LDAP");
				isValidUser = true;
				}
			}catch (NamingException e){
				logger.error("Error: ParserConfigurationException " + e);
				session.invalidate();
			}
		return isValidUser;
	}
	
	/**
	 * Method to create a connection with the LDAP
	 * @return connection object of LDAP
	 */
	public DirContext getLdapConnection(){
        Hashtable ldapparams = new Hashtable();
	    //Retriving the LDAP parameters from the config file and storing it in the hash table
	    ldapparams.put(Context.INITIAL_CONTEXT_FACTORY,ldapConfig.getConfigValue(AuthenticationServerConfiguration.INITIAL_CONTEXT_FACTORY));
	    ldapparams.put(Context.PROVIDER_URL, ldapConfig.getConfigValue(AuthenticationServerConfiguration.PROVIDER_URL));
	    ldapparams.put(Context.SECURITY_AUTHENTICATION,ldapConfig.getConfigValue(AuthenticationServerConfiguration.SECURITY_AUTHENTICATION));
	    ldapparams.put(Context.SECURITY_PRINCIPAL,ldapConfig.getConfigValue(AuthenticationServerConfiguration.SECURITY_PRINCIPAL));
	    ldapparams.put(Context.SECURITY_CREDENTIALS,ldapConfig.getConfigValue(AuthenticationServerConfiguration.SECURITY_CREDENTIALS));
		try{
			return new InitialDirContext(ldapparams);
		}catch(NamingException ne){
			logger.error(" Error in Connecting to LDAP server "+ne);
		}
		return null;
	}
	
	/**
	 * Method to initialise the Configuration Object with the values in the config file
	 * @param section - integer value to start with
	 * @return Configuration object
	 */
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
