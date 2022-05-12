package com.temenos.t24browser.obfuscator;

// TODO: Auto-generated Javadoc
/**
 * Represents algorithm for obfuscation of string to obfuscated string.
 * 
 * @author mludvik
 */
public interface ObfuscationAlgorithm {
	
	/**
	 * Translates string to obfuscated string.
	 * 
	 * @param original Original string.
	 * 
	 * @return Obfuscated string.
	 */
	String obfuscate(String original);
}
