package com.temenos.arc.security.authenticationserver.server;

import junit.framework.TestCase;

public abstract class AbstractXmlUserManagementTest extends TestCase {

	static final String ARGS_START = XmlWrapper.ARGS_START;
	static final String ARGS_END = XmlWrapper.ARGS_END;
	static final String T24_USER_NAME_VAL = "Seymour Butts";
	static final String T24_PASSWORD_VAL = "666666";
	static final String ARC_USER_ID_VAL = "ARC_USER_666";
	static final String MEMORABLE_DATA_VAL = "bi938yt93hh900";
	static final String START_DATE_YEAR_VAL = "2006";
	static final String START_DATE_MONTH_VAL = "1";
	static final String START_DATE_DAY_VAL = "1";
    static final String ADD_USER_XML_START = ARGS_START
                                            + "<" + XmlUserManagement.T24_USER_NAME + ">"; 
    static final String ADD_USER_XML_END = "</" + XmlUserManagement.T24_USER_NAME + ">"
                                            + "<" + XmlUserManagement.MEMORABLE_DATA + ">"
                                            + MEMORABLE_DATA_VAL
                                            + "</" + XmlUserManagement.MEMORABLE_DATA+ ">"
                                            + "<" + XmlUserManagement.START_DATE_YEAR + ">"
                                            + START_DATE_YEAR_VAL 
                                            + "</" + XmlUserManagement.START_DATE_YEAR + ">"
                                            + "<" + XmlUserManagement.START_DATE_MONTH + ">"
                                            + START_DATE_MONTH_VAL  
                                            + "</" + XmlUserManagement.START_DATE_MONTH + ">"
                                            + "<" + XmlUserManagement.START_DATE_DAY + ">"
                                            + START_DATE_DAY_VAL 
                                            + "</" + XmlUserManagement.START_DATE_DAY + ">"
                                        + ARGS_END;
    static final String ADD_USER_XML = ADD_USER_XML_START
	                                          + T24_USER_NAME_VAL 
	                                          + ADD_USER_XML_END;
	static final String ADD_USER_RETURN_XML = 
		ARGS_START 
            + "<" + XmlUserManagement.T24_PASSWORD + ">" 
            + "[0-9a-zA-Z]*" 
            + "</" + XmlUserManagement.T24_PASSWORD + ">"
			+ "<" + XmlWrapper.RETURN_STATE + ">" 
			+ XmlWrapper.SUCCESS_STATE
            + "</" + XmlWrapper.RETURN_STATE + ">"
	    + ARGS_END;
	static final String ADD_USER_FAIL_RETURN_XML = 
		ARGS_START 
	       	+ "<" + XmlWrapper.RETURN_STATE + ">"
		   	+ XmlWrapper.FAILURE_STATE 
            + "</" + XmlWrapper.RETURN_STATE + ">"
		+ ARGS_END;
    static final String ADD_USER_UNEXPECTED_RETURN_XML = 
        ARGS_START 
            + "<" + XmlWrapper.RETURN_STATE + ">"
            + XmlUserManagement.UNEXPECTED_USER_EXISTENCE_STATE
            + "</" + XmlWrapper.RETURN_STATE + ">"
        + ARGS_END;
	static final String REMOVE_USER_XML = ARGS_START 
                                             + "<" + XmlUserManagement.T24_USER_NAME + ">" 
                                             + T24_USER_NAME_VAL 
                                             + "</" + XmlUserManagement.T24_USER_NAME + ">" 
	                                         + ARGS_END;
	static final String REMOVE_USER_RETURN_XML = ARGS_START 
                                        	    + "<" + XmlWrapper.RETURN_STATE + ">" 
                                        	    + XmlWrapper.SUCCESS_STATE 
                                                + "</" + XmlWrapper.RETURN_STATE + ">" 
                                               + ARGS_END;
	static final String REMOVE_USER_FAIL_RETURN_XML = ADD_USER_FAIL_RETURN_XML;
    static final String REMOVE_USER_UNEXPECTED_RETURN_XML = ADD_USER_UNEXPECTED_RETURN_XML;
	static final String USER_EXISTS_XML = REMOVE_USER_XML;
	static final String GET_ARC_USER_XML = REMOVE_USER_XML;
	protected XmlUserManagement toTest;

	public AbstractXmlUserManagementTest() {
		super();
	}

	public void testAddUser() {
	    // test normal add semantics
	    checkAndAddUser();
	    // test error condition
	    assertEquals(ADD_USER_UNEXPECTED_RETURN_XML, toTest.addUser(ADD_USER_XML));
	    
	    getArcUserId();
	    
	    // remove
	    removeUser();
	}

	private void getArcUserId() {
		// the string is a random 0-9 string
	    assertTrue(toTest.getArcUserId(GET_ARC_USER_XML).matches("[0-9]*"));
	}

	private void removeUser() {
	    // test normal remove semantics
	    assertEquals(Boolean.TRUE.toString(), toTest.userExists(USER_EXISTS_XML));
	    assertEquals(REMOVE_USER_RETURN_XML, toTest.removeUser(REMOVE_USER_XML));
	    
	    // test error condition
	    assertEquals(REMOVE_USER_UNEXPECTED_RETURN_XML, toTest.removeUser(REMOVE_USER_XML));
	    
	}

	public void testUserNotExists() {
	    assertEquals(Boolean.FALSE.toString(), toTest.userExists(USER_EXISTS_XML));
	}

	public void testGetNonExistentArcUserId() {
	    assertEquals("", toTest.getArcUserId(GET_ARC_USER_XML));
	}

	private void checkAndAddUser() {
	    assertEquals(Boolean.FALSE.toString(), toTest.userExists(USER_EXISTS_XML));
	    assertTrue(toTest.addUser(ADD_USER_XML).matches(ADD_USER_RETURN_XML));
	    assertEquals(Boolean.TRUE.toString(), toTest.userExists(USER_EXISTS_XML));
	}

}