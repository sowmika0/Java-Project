package com.temenos.t24browser.obfuscator.tests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import com.temenos.t24browser.obfuscator.ObfuscatedInputStream;

public class ObfuscatorInputStreamTests extends TestCase {
	Map table = new HashMap();
	StringBuffer buff = new StringBuffer();
	
	void makeEquealTest(String original, String expected) throws IOException {
		ObfuscatedInputStream in = 
			new ObfuscatedInputStream(new ByteArrayInputStream(original.getBytes()), 
					table);
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
		String expected = "abcdef \"XYZ\" SGA";
		table.put("ENQ", "XYZ");
		makeEquealTest(original, expected);
	}

	public void testConstant2() throws IOException {
		String original = "abcdef _ENQ__ABC_ SGA";
		String expected = "abcdef \"XYZ\" SGA";
		table.put("ENQ.ABC", "XYZ");
		makeEquealTest(original, expected);
	}
	
	public void testNoConstant1() throws IOException {
		String original = "abcdef _ENQ__abc SGA";
		String expected = "abcdef _ENQ__abc SGA";
		makeEquealTest(original, expected);
	}

	 public void testNoConstant2() throws IOException {
		String original = "abcdef ENQ_abc_ SGA";
		String expected = "abcdef ENQ_abc_ SGA";
		makeEquealTest(original, expected);
	}

	public void testNoConstant3() throws IOException {
		String original = "abcdef ENQ__abc SGA";
		String expected = "abcdef ENQ__abc SGA";
		makeEquealTest(original, expected);
	}
	public void testNoConstant4() throws IOException {
		String original = "abcdef A_ENQ_";
		String expected = "abcdef A_ENQ_";
		makeEquealTest(original, expected);
	}
	public void testConstant3() throws IOException {
		String original = "abcdef _ENQ+_ABC_ _ABC_";
		table.put("ABC", "XYZ");
		String expected = "abcdef _ENQ+\"XYZ\" \"XYZ\"";
		makeEquealTest(original, expected);
	}
	public void testConstant4() throws IOException {
		String original = "a\"_ABC_\"";
		String expected = "a\"_ABC_\"";
		makeEquealTest(original, expected);
	}
}
