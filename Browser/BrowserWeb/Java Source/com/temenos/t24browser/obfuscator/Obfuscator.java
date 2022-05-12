package com.temenos.t24browser.obfuscator;

// TODO: Auto-generated Javadoc
/**
 * This interface represents obfuscator, i.e. text processor which translates
 * string to obfuscated string.
 * 
 * @author mludvik
 */
public interface Obfuscator {
	
	/**
	 * Checks if Obfuscator already contains mapping for input string.
	 * 
	 * @param originalCommand string which is to be obfuscated.
	 * 
	 * @return true if Obfuscator contains Obfuscated string, i.e. if
	 * Obfuscator already obfuscated string A to B, this method returns
	 * true for A as argument.
	 */
	boolean containsOriginalCommand(String originalCommand);
	
	/**
	 * Checks if Obfuscator already mapping for input string.
	 * 
	 * @param obfuscatedCommand string which is to be obfuscated.
	 * 
	 * @return true if Obfuscator contains Obfuscated string, i.e. if
	 * Obfuscator already obfuscated string A to B, this method returns
	 * true for B as argument.
	 */
	boolean containsObfuscatedCommand(String obfuscatedCommand);

	/**
	 * Transforms original string to the obfuscated one based on implementation.
	 * Rule is that if Obfuscator already obfuscated A to B, then it must return
	 * B for every input A.
	 * 
	 * @param originalCommand string that is to be obfuscated.
	 * 
	 * @return Obfuscated string.
	 */
	String transform(String originalCommand);

	/**
	 * Transform sequence of string tokens to the sequence of obfuscated tokens. For
	 * every token uses method <code>transform()</code>. Characters which separates
	 * tokens are implementation dependant.
	 * 
	 * @param compoundCommand sequence of commands.
	 * 
	 * @return sequence of obfuscated commands.
	 */
	String transformCompoundCommand(String compoundCommand);

	/**
	 * Translates obfuscated string to the original string. If the obfuscated
	 * string does not have mapping to the original string, returns original
	 * String.
	 * 
	 * @param obfuscatedCommand Obfuscated string.
	 * 
	 * @return Original strings.
	 * 
	 * TODO maybe should return null or error if there is no mapping, otherwise
	 * can be security leak
	 */
	String reverseTransform(String obfuscatedCommand);

	
	/**
	 * Transform sequence of obfuscated string tokens to the sequence of original
	 * tokens. For every token uses method <code>reverseTransform()</code>.
	 * Characters which separates tokens are implementation dependant.
	 * 
	 * @param compoundObfuscatedCommand SquenceObfuscated of obfuscated string.
	 * 
	 * @return Sequence of original strings.
	 */
	String reverseTransformCompoundCommand(String compoundObfuscatedCommand);
}
