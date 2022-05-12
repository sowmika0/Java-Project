package com.temenos.t24browser.obfuscator;

import java.io.Serializable;
import java.util.Properties;
import java.util.StringTokenizer;

// TODO: Auto-generated Javadoc
/**
 * Obfuscator that obfuscates all tokens of compound commands. Shares
 * common mapping tables of AbstractObfuscator.
 * 
 * @author mludvik
 */
public final class AllTokensObfuscator extends AbstractObfuscator 
	implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 123123123;
	/* Delimiters split string to smaller tokens which are obfuscated separately.
	 * | splits type of item and the target, e.g. 
	CONTEXT.ENQUIRY|CUSTOMER_CUSTOMER.POSITION so split this out*/
	/** The Constant DEFAULT_DELIMITERS. */
	private static final String DEFAULT_DELIMITERS = " ,|";

	/** The translation table. */
	private Properties translationTable = new Properties();
	
	/** The reverse translation table. */
	private Properties reverseTranslationTable = new Properties();
	
	/** The alg. */
	private ObfuscationAlgorithm alg;
	
	/** The delimiters. */
	private String delimiters;
	
	/* (non-Javadoc)
	 * @see com.temenos.t24browser.obfuscator.Obfuscator#containsOriginalCommand(java.lang.String)
	 */
	public boolean containsOriginalCommand(String originalCommand) {
		return translationTable.containsKey(originalCommand); 
	}
	
	/* (non-Javadoc)
	 * @see com.temenos.t24browser.obfuscator.Obfuscator#containsObfuscatedCommand(java.lang.String)
	 */
	public boolean containsObfuscatedCommand(String obfuscatedCommand) {
		return reverseTranslationTable.containsKey(obfuscatedCommand);
	}
	
	/**
	 * Returns copy of translation table.
	 * 
	 * @return copy of translation table.
	 */
	public Properties getTranslationTable() { 
		return (Properties)translationTable.clone(); // defensive copy
	}
	
	/**
	 * Creates Obfuscator.
	 * Defaul delimiters are ' ' and ';'
	 * 
	 * @param alg Obfuscation algorighm used for obfuscation.
	 */
	public AllTokensObfuscator(ObfuscationAlgorithm alg) {
		this(alg, DEFAULT_DELIMITERS);
	}
	
	/**
	 * Creates Obfuscator.
	 * 
	 * @param alg Obfuscation algorighm used for obfuscation.
	 * @param delimiters Sequence of chars which represents delimiters in the
	 * token stream.
	 */
	public AllTokensObfuscator(ObfuscationAlgorithm alg, String delimiters) {
		this.alg = alg;
		this.delimiters = delimiters;
	}
	
	/* (non-Javadoc)
	 * @see com.temenos.t24browser.obfuscator.Obfuscator#transform(java.lang.String)
	 */
	public String transform(String originalString) {
		if(translationTable.containsKey(originalString)) {
			return (String)translationTable.get(originalString);
		} else if(commonTableLoaded() && COMMON_TABLE.containsKey(originalString)) {
			return (String)COMMON_TABLE.get(originalString);
		}
		String obfuscatedString = alg.obfuscate(originalString);
		translationTable.put(originalString, obfuscatedString);
		reverseTranslationTable.put(obfuscatedString, originalString);
		return obfuscatedString;
	}
	
	/* (non-Javadoc)
	 * @see com.temenos.t24browser.obfuscator.Obfuscator#reverseTransform(java.lang.String)
	 */
	public String reverseTransform(String obfuscatedString) {
		if(reverseTranslationTable.containsKey(obfuscatedString)) {
			return (String)reverseTranslationTable.get(obfuscatedString);
		} else if(commonTableLoaded() && COMMON_REVERSE_TABLE.containsKey(obfuscatedString)) {
			return (String)COMMON_REVERSE_TABLE.get(obfuscatedString);
		} else {
			return obfuscatedString;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.temenos.t24browser.obfuscator.Obfuscator#transformCompoundCommand(java.lang.String)
	 */
	public String transformCompoundCommand(String command) {
		StringTokenizer tokenizer = new StringTokenizer(command, delimiters, true);
		StringBuffer obfuscatedCommand = new StringBuffer();
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			/* 
			 * Obfuscation of letters is error prone, e.g. if letter I would 
			 * be mapped to L, all L, even these not obfuscated,
			 * would be in reverse transformation mapped to I, which is not 
			 * what should happen.
			 */
			if(token.length() == 1) {// if token is delimiter or single Letter
				obfuscatedCommand.append(token);
			} else {
				obfuscatedCommand.append(transform(token));
			}
		}
		return obfuscatedCommand.toString();
	}


	/* (non-Javadoc)
	 * @see com.temenos.t24browser.obfuscator.Obfuscator#reverseTransformCompoundCommand(java.lang.String)
	 */
	public String reverseTransformCompoundCommand(String command) {
		StringTokenizer tokenizer = new StringTokenizer(command, delimiters, true);
		StringBuffer obfuscatedCommand = new StringBuffer();
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if(token.length() == 1) {// if token is delimiter or single Letter
				obfuscatedCommand.append(token);
			} else {
				obfuscatedCommand.append(reverseTransform(token));
			}
		}
		return obfuscatedCommand.toString();
	}

}
