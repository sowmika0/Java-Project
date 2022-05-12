package com.temenos.arc.security.authenticationserver.common;

/**
 * PasswordGenerator.java 
 * A simple random password generator, user can specify the length.
 * Default length is 8, the common unix system effective password length.
 */
 
public class StringGenerator {
	/** 
	 * Returns an alphanumeric string of the length specified. 
	 * @param n  The length of the string to be generated
	 * @return	 The random string
	 */
	public static String getRandomAlphaNumericString(int n) {
		char[] returnChars = new char[n];
	    int c  = 'A';
	    int  r1 = 0;
	    for (int i=0; i < n; i++)
	    {
	    	r1 = (int)(Math.random() * 3);
	    	switch(r1) {
		  		// 1/3 of characters will be numbers, 2/3 will be letters
	    		case 0: c = '0' +  (int)(Math.random() * 10); break;
	    		case 1: c = 'a' +  (int)(Math.random() * 26); break;
	    		case 2: c = 'A' +  (int)(Math.random() * 26); break;
	    	}
	    	returnChars[i] = (char)c;
	    }
	    return new String(returnChars);
	}
  
	/** 
	 * Returns an numeric string of the length specified. 
	 * @param n  The length of the string to be generated
	 * @return	 The random string
	 */
	public static String getRandomNumericString(int n) {
		char[] returnChars = new char[n];
	    int c  = '0';
	    for (int i=0; i < n; i++)
	    {
	    	c = '0' +  (int)(Math.random() * 10);
	    	returnChars[i] = (char)c;
	    }
	    return new String(returnChars);
	}
}  