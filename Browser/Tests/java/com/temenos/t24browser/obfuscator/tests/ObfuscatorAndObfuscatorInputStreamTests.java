package com.temenos.t24browser.obfuscator.tests;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import com.temenos.t24browser.obfuscator.AllTokensObfuscator;
import com.temenos.t24browser.obfuscator.CharMappingObfuscationAlgorithm;
import com.temenos.t24browser.obfuscator.ObfuscatedInputStream;
import com.temenos.t24browser.obfuscator.Obfuscator;

public class ObfuscatorAndObfuscatorInputStreamTests extends TestCase {
	Obfuscator obf = new AllTokensObfuscator(new CharMappingObfuscationAlgorithm());
	StringBuffer buff = new StringBuffer();
	
	void makeEquealTest(String original, String expected) throws IOException {
		ObfuscatedInputStream in = 
			new ObfuscatedInputStream(new ByteArrayInputStream(original.getBytes()), 
					obf);
		for(int ch = in.read(); ch != -1; ch = in.read()) {
			buff.append((char) ch);
		}
		String obfuscated = buff.toString();
		assertEquals(expected, obfuscated);
	}
	
	public void testNoUnderscore1() throws IOException {
		String original = "abcdef";
		String expected = "abcdef";
		makeEquealTest(original, expected);
	}

	public void testNoUnderscore2() throws IOException {
		String original = "abcdef aga gdga ghash";
		String expected = "abcdef aga gdga ghash";
		makeEquealTest(original, expected);
	}

	public void testUnderscore1() throws IOException {
		String original = "abcdef _aga ";
		String expected = "abcdef _aga ";
		makeEquealTest(original, expected);
	}

	public void testUnderscore2() throws IOException {
		String original = "abcdef_ _aga ";
		String expected = "abcdef_ _aga ";
		makeEquealTest(original, expected);
	}

	public void testConstant1() throws IOException {
		String original = "abcdef _ENQ_ SGA";
		String expected = "abcdef \"" + obf.transform("ENQ") + "\" SGA";
		makeEquealTest(original, expected);
	}

	public void testConstant2() throws IOException {
		String original = "abcdef _ENQ__ABC_ SGA";
		String expected = "abcdef \"" + obf.transform("ENQ.ABC") + "\" SGA";
		makeEquealTest(original, expected);
	}
	
	public void testNoConstant2() throws IOException {
		String original = "abcdef ENQ_abc SGA";
		String expected = "abcdef ENQ_abc SGA";
		makeEquealTest(original, expected);
	}

	public void testNoConstant3() throws IOException {
		String original = "abcdef ENQ__abc SGA";
		String expected = "abcdef ENQ__abc SGA";
		makeEquealTest(original, expected);
	}
}
