package com.temenos.t24browser.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.temenos.t24browser.utils.PropertyManager;

// TODO: Auto-generated Javadoc
/**
 * The Class SecurityUtils.
 */
public class SecurityUtils {
    
    /** The evil tag pattern. */
    private static Pattern evilTagPattern = null;

    /**
     * Return value indicates whether the specified input string is valid (as
     * specified by validInputRegex).
     * 
     * @param input the string to check
     * @param validInputRegex a regular expression specifying the pattern which
     * the input string must satisfy to be considered valid
     * 
     * @return indicates whether the specified string matched the specified
     * regular expression
     */
    public static boolean matches(String input, Pattern validInputRegex) {
        Matcher matcher = validInputRegex.matcher(input);
        return matcher.find();
    }

    /**
     * A method which replaces 'evil' HTML tags in the input String. For
     * example, <script> is replaced with &lt;blocked-tag&gt; Thus, it will
     * protect against some forms of cross site scripting attack. It only
     * replaces a subset of HTML strings. The method is quite crude and not
     * particularly efficient. Also, it is not clear if the list of blocked tags
     * is all those that may be used for attacks.
     * 
     * @param inputString the input string
     * @param properties the properties
     * 
     * @return a 'safe' version of the input String
     */
    public static String replaceEvilHTMLtags(String inputString, PropertyManager properties) {
		 String regEx = properties.getParameterValue("T24XmlFilterRegEx");
		 if (regEx != null && regEx.length() > 0) {
			 if (evilTagPattern == null) {
				 evilTagPattern = Pattern.compile(regEx);
			 }
             if (inputString != null && !inputString.equals("")) {
                 Matcher m = evilTagPattern.matcher(inputString);
                 inputString = m.replaceAll("&lt;blocked-tag&gt;");
             }
		 }

		 return inputString;
    }
}
