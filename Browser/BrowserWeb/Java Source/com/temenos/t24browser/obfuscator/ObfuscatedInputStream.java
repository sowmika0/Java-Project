package com.temenos.t24browser.obfuscator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * This class is used for obfuscation of JavaScript files. Actually,
 * replaces tokens which contain capital letters, start and
 * end by '_' by obfuscated tokens starting and ending by '"'. E.g.
 * replaces _ENQ_ by something as "n#5".
 * 
 * @author mludvik
 */
public class ObfuscatedInputStream extends InputStream  {
	
	/** The in. */
	private InputStream in;
	
	/** The translation table. */
	private Map translationTable;
	
	/** The obf. */
	private Obfuscator obf;

	/*
	 * If before first _ is alfabet letter,
	 * following string cannot be _xxx_ token (e.g. ABC_xxx_)
	 */
	/** The Constant VALID_START. */
	private static final int VALID_START = 6;
	
	/** The Constant START. */
	private static final int START = 0;
	
	/** The Constant FIRST_UNDERSCORE. */
	private static final int FIRST_UNDERSCORE = 1;
	
	/** The Constant CONST_BEGINING. */
	private static final int CONST_BEGINING = 2;
	
	/** The Constant SECOND_UNDERSCORE. */
	private static final int SECOND_UNDERSCORE = 3;

	/** The Constant CONST_CONFIRMED. */
	private static final int CONST_CONFIRMED = 4;
	
	/** The Constant NOT_CONST. */
	private static final int NOT_CONST = 5;
	
	/** The Constant EOF. */
	private static final int EOF = 6;

	/** The state. */
	private int state = START;
	
	/** The pre read. */
	private int preRead; // used after the constant was accepted

	/** The buff. */
	private StringBuffer buff;

	/**
	 * Instantiates a new obfuscated input stream.
	 * 
	 * @param in the in
	 * @param translationTable the translation table
	 */
	public ObfuscatedInputStream(InputStream in, Map translationTable){
		if(in == null || translationTable == null)
			throw new NullPointerException();
		this.in = in;
		this.translationTable = translationTable; // consider to create defensive copy
	}

	/**
	 * Instantiates a new obfuscated input stream.
	 * 
	 * @param in the in
	 * @param obfuscator the obfuscator
	 */
	public ObfuscatedInputStream(InputStream in, Obfuscator obfuscator){
		if(in == null || obfuscator == null)
			throw new NullPointerException();
		this.in = in;
		obf = obfuscator; // consider to create defensive copy
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		if(state == START) {
			int ch = in.read();
			if(isValidStartingChar(ch)) {
				state = VALID_START;
			}
			return ch;
		} else if(state == VALID_START) {
			int ch = in.read();
			if(ch == '_') {
				state = FIRST_UNDERSCORE;
				buff = new StringBuffer();
				buff.append('_');
				return read();
			} else if(!isValidStartingChar(ch)) {
				state = START; // invalid start
			}
			return ch;
		} else if(state == FIRST_UNDERSCORE) {
			int ch = in.read();
			if(ch == -1) {
				state = EOF;
				return '_';
			} else if(isLetterOrDigit(ch)) {
				state = CONST_BEGINING;
				buff.append((char)ch);
				return read();
			} else { // '_' or otherwise
				state = NOT_CONST;
				buff.append((char)ch);
				return read();
			}
		} else if(state == CONST_BEGINING) {
			int ch = in.read();
			if(ch == -1) {
				state = NOT_CONST;
				return read();
			} else if(ch == '_') {
				state = SECOND_UNDERSCORE;
				buff.append('_');
				return read();
			} else if(isLetterOrDigit(ch)) {
				buff.append((char) ch);
				return read();
			} else {
				state = NOT_CONST;
				buff.append((char)ch);
				return read();
			}
		} else if(state == SECOND_UNDERSCORE) {
			int ch = in.read();
			// '_' or number of alphabet letter
			if(ch == '_' || isLetterOrDigit(ch)) {
				state = CONST_BEGINING;
				buff.append((char)ch);
				return read();
			} else { // the preRead character is returned after the const is processed
				state = CONST_CONFIRMED;
				preRead = ch;
				buff.deleteCharAt(0); // first '_'
				buff.deleteCharAt(buff.length() - 1); // last '_'
				String t24Const = buff.toString().replaceAll("__", "."); // "__" in javascript means ".", cannot use "." as name of variable
				Object translatedT24Const;
				if(obf != null)
					 translatedT24Const = obf.transform(t24Const);
				else if(!translationTable.containsKey(t24Const)) {
					translatedT24Const = t24Const;
				} else
					translatedT24Const = translationTable.get(t24Const);
				if(!(translatedT24Const instanceof String))
						throw new RuntimeException("Translation table must return String, "
								+ translatedT24Const.getClass().getName()
								+ " was returned.");
				buff = new StringBuffer();
				buff.append('"' + (String)translatedT24Const + '"');
				return read();
			}
		} else if(state == CONST_CONFIRMED) {
			if(buff.length() == 0) {
				state = VALID_START;
				return preRead;
			} else {
				char ch = buff.charAt(0);
				buff.deleteCharAt(0);
				return ch;
			}
		} else if(state == NOT_CONST){
			char ch = buff.charAt(0);
			buff.deleteCharAt(0);
			if(buff.length() == 0) {
				if(isValidStartingChar(ch))
					state = VALID_START;
				else
					state = START;
			}
			return ch;
		} else //state == EOF
			return -1;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	public void close() throws IOException {
		in.close();
	}

	/**
	 * Checks if is letter or digit.
	 * 
	 * @param ch the ch
	 * 
	 * @return true, if is letter or digit
	 */
	private boolean isLetterOrDigit(int ch) {
		return (ch >= '0' && ch <= '9') || /*(ch >= 'a' && ch <= 'z') ||*/
		(ch >= 'A' && ch <= 'Z');
	}
	
	/**
	 * Checks if is valid starting char.
	 * 
	 * @param ch the ch
	 * 
	 * @return true, if is valid starting char
	 */
	private boolean isValidStartingChar(int ch) {
		return !isLetterOrDigit(ch) && (char)ch != '"';	// "_ALL_" is not valid T24 string
	}
}
