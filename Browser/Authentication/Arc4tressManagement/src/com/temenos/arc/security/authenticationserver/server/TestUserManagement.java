package com.temenos.arc.security.authenticationserver.server;

import java.util.Calendar;
import com.temenos.arc.security.authenticationserver.ftress.server.UserManagement;

public class TestUserManagement {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UserManagement um = new UserManagement();
        Calendar cal = Calendar.getInstance();
        try {
            int year = 2007;
            int month = 5;
            int day = 1;
            cal.set(year, month, day);            
        } catch (NumberFormatException e) {
        	System.out.println("arrrggghhhh");
        }
        
        try {
        	um.addUser("bob", "memdataenc", cal,null);
        } catch (Exception e) {
        	e.printStackTrace();
        }

	}

}
