package com.temenos.t24browser.obfuscator.tests;

import junit.framework.TestCase;

import com.temenos.t24browser.obfuscator.AllTokensObfuscator;
import com.temenos.t24browser.obfuscator.CharMappingObfuscationAlgorithm;
import com.temenos.t24browser.obfuscator.Obfuscator;

public class ObfuscatorTests extends TestCase {
	Obfuscator obf = new AllTokensObfuscator(new CharMappingObfuscationAlgorithm());
	
	protected void setUp() throws Exception {
	}

	public void testTransform1() {
		String newString = obf.transform("abc");
		assertEquals(newString, obf.transform("abc"));
		String newString2 = obf.transform("abc123_");
		assertEquals(newString2, obf.transform("abc123_"));
		assertTrue(newString2.startsWith(newString));
	}
	
	public void testTransform2() {
		String newString = obf.transform("a_b_c");
		assertEquals(newString, obf.transform("a_b_c"));
		String newString2 = obf.transform("a_bxyz123_c");
		assertEquals(newString2, obf.transform("a_bxyz123_c"));
		assertTrue(newString2.startsWith(obf.transform("a_b")));
		assertTrue(newString2.endsWith(obf.transform("_c")));
	}

	public void testTransform3() {
		String newString = obf.transform("poiuytrewqlkjhgfdsamnbvcxz");
		assertEquals(newString, obf.transform("poiuytrewqlkjhgfdsamnbvcxz"));
	}

	public void testTransform4() {
		String newString = obf.transform("ABCDEFGHILJKLMNOPQRSTUVWXYZ");
		assertEquals(newString, obf.transform("ABCDEFGHILJKLMNOPQRSTUVWXYZ"));
		obf = new AllTokensObfuscator(new CharMappingObfuscationAlgorithm());
		newString = obf.transform("ABCDEFGHILJKLMNOPQRSTUVWXYZ");
		assertEquals(newString, obf.transform("ABCDEFGHILJKLMNOPQRSTUVWXYZ"));
	}
	/*public void testTransform4() {
		String newString = obf.transform("123456789abcdefghijklmnopqrstuvwxyzABCDEFGHILJKLMNOPQRSTUVWXYZ");
		assertEquals(newString, obf.transform("123456789abcdefghijklmnopqrstuvwxyzABCDEFGHILJKLMNOPQRSTUVWXYZ"));
		obf = new AllTokensObfuscator(new CharMappingObfuscationAlgorithm());
		newString = obf.transform("abcdefghijklmnopqrstuvwxyzABCDEFGHILJKLMNOPQRSTUVWXYZ123456789");
		assertEquals(newString, obf.transform("abcdefghijklmnopqrstuvwxyzABCDEFGHILJKLMNOPQRSTUVWXYZ123456789"));
	}*/
	
	public void testReverseTransform() {
		String originalString = "a_b_c";
		String newString = obf.transform("a_b_c");
		assertEquals(originalString, obf.reverseTransform(newString));
		String originalString2 = "a_bxyz123_c";
		String newString2 = obf.transform("a_bxyz123_c");
		assertEquals(originalString2, obf.reverseTransform(newString2));
	}

	public void testReverseTransform2() {
		String originalString = "abc.123.xyz";
		String newString = obf.transform(originalString);
		assertEquals(originalString, obf.reverseTransform(newString));
	}
	
	public void testCompound1() {
		String newString = obf.transformCompoundCommand("abc a1265 xyz");
		assertEquals(newString, obf.transformCompoundCommand("abc a1265 xyz"));
	}

	public void testReverseCompound1() {
		String orig = "abc a1265 xyz";
		String newString = obf.transformCompoundCommand(orig);
		assertEquals(newString, obf.transformCompoundCommand(orig));
		String reverseOrig = obf.reverseTransformCompoundCommand(newString);
		assertEquals(reverseOrig, orig);
	}

	public void testReverseCompound2() {
		String orig = "abc ,a1265  xyz";
		String newString = obf.transformCompoundCommand(orig);
		assertEquals(newString, obf.transformCompoundCommand(orig));
		String reverseOrig = obf.reverseTransformCompoundCommand(newString);
		assertEquals(reverseOrig, orig);
	}
	
	public void testReverseCompound3() {
		String orig = "ENQ ACC.ENTRY.STMT";
		String newString = obf.transformCompoundCommand(orig);
		assertEquals(newString, obf.transformCompoundCommand(orig));
		String reverseOrig = obf.reverseTransformCompoundCommand(newString);
		assertEquals(reverseOrig, orig);
	}
	

}
