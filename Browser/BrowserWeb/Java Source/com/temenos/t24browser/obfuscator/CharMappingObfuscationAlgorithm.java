package com.temenos.t24browser.obfuscator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// TODO: Auto-generated Javadoc
/**
 * This class represent obfuscation algorithm which maps each character to
 * randomly chosen character. As soon as the mapping for any character is chose,
 * it remains the same for all identical characters.
 * 
 * @author mludvik
 */
public class CharMappingObfuscationAlgorithm implements ObfuscationAlgorithm, Serializable {
	
	/** The Constant serialVersionUID. */
	static final long serialVersionUID = 10042007;
	
	/** The Constant RANDOM. */
	private static final Random RANDOM = new Random();  
	
	/** The Constant DEFAULT_UNCHANGEABLE_CHARS. */
	public  static final String DEFAULT_UNCHANGEABLE_CHARS = "?%:$@";
	
	/** The mapping. */
	private char[] mapping = new char[128];
	
	/** The char list. */
	private List charList;
	
	/** The unchangeable chars. */
	private String unchangeableChars;
	
	/**
	 * Creates CharMappingObfuscationAlgorithm with user defined set of
	 * characters which are unchangeable.
	 * 
	 * @param unchangeableChars Set of characters which are not to be changed.
	 */
	public CharMappingObfuscationAlgorithm(String unchangeableChars/*, String charSet*/) {
		// prepare initial set of characters 
		charList = new ArrayList(127);
		for(char ch = 48; ch <= 57; ch++)
			charList.add(new Character(ch));
		for(char ch = 59; ch <= 62; ch++)
			charList.add(new Character(ch));
		for(char ch = 65; ch <= 90; ch++)
			charList.add(new Character(ch));
		charList.add(new Character((char)91));
		for(char ch = 93; ch <= 94; ch++)
			charList.add(new Character(ch));
		charList.add(new Character((char)96));
		for(char ch = 97; ch <= 122; ch++)
			charList.add(new Character(ch));
		charList.add(new Character((char)123));
		for(char ch = 125; ch <= 126; ch++)
			charList.add(new Character(ch));
		this.unchangeableChars = unchangeableChars;
	}

	/**
	 * Creates CharMappingObfuscationAlgorithm with default set of unchangeable
	 * characters.
	 */
	public CharMappingObfuscationAlgorithm() {
		this(DEFAULT_UNCHANGEABLE_CHARS/*, CHAR_SET_1*/);
	}

	/* (non-Javadoc)
	 * @see com.temenos.t24browser.obfuscator.ObfuscationAlgorithm#obfuscate(java.lang.String)
	 */
	public String obfuscate(String original) {
		StringBuffer obfuscated = new StringBuffer();
		for(int i = 0; i < original.length(); i++) {
			char ch = original.charAt(i);
			if(unchangeableChars.indexOf(ch) != -1) { // is unchangeable
				obfuscated.append(ch);
			} else {
				char mappedChar = mapping[ch]; // TODO check legal character
				if(mappedChar == 0) { // not set yet
					// map character to any unused character from initial set
					mappedChar =((Character)charList.remove(
							RANDOM.nextInt(charList.size()))).charValue();
					mapping[ch] = mappedChar;
				}
				obfuscated.append(mappedChar);
			}
		}
		return obfuscated.toString();
	}
}
