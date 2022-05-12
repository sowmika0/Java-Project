package com.temenos.arc.security.authenticationserver.server;

import com.temenos.arc.security.authenticationserver.server.ArcUserManagement;
import com.temenos.arc.security.authenticationserver.server.XmlUserManagement;

public class XmlUserManagementTest extends AbstractXmlUserManagementTest {
    static final String FAILURE_USER = "temenos";
    static final String UNEXPECTED_USER = "user";
    
    ArcUserManagement userManagement = new MockUserManagement();
    
    protected void setUp() throws Exception {
        super.setUp();
        toTest = new XmlUserManagement(userManagement);
    }
    
    public void testAddUserFailure() {
        testAddUserHelper(FAILURE_USER, XmlUserManagement.FAILURE_STATE);
    }

    public void testAddUserAlreadyExists() {
        testAddUserHelper(UNEXPECTED_USER, XmlUserManagement.UNEXPECTED_USER_EXISTENCE_STATE);
    }           

    private void testAddUserHelper(String user, String expectedStatus) {
        String addUserXml = ADD_USER_XML_START + user + ADD_USER_XML_END;
        String returnXml = toTest.addUser(addUserXml);
        String status = XmlWrapper.getArg(returnXml, XmlWrapper.RETURN_STATE);
        assertEquals(expectedStatus, status);
    }
    
}
