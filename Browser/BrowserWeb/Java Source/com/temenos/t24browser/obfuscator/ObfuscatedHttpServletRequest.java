package com.temenos.t24browser.obfuscator;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

// TODO: Auto-generated Javadoc
/**
 * Wrapper for HttpServletRequest which for each requested parameter check
 * if the parameter name is in the list of obfuscated parameters and in such
 * case returns de-obfuscated parameter value.
 * 
 * @author mludvik
 */
public class ObfuscatedHttpServletRequest extends HttpServletRequestWrapper {
	//public static final Set DEFAULT_OBFUSCATED_PARAM_NAMES = new HashSet();
	/** The obf. */
	private Obfuscator obf;
	
	/**
	 * Creates ObfuscatedHttpServletRequest.
	 * 
	 * @param original Original HttpServletRequest.
	 * @param obf Obfuscator used for de-obfuscation.
	 * @param obfuscatedParamNames Set of parameter names which are to be
	 * de-obfuscated.
	 */
	public ObfuscatedHttpServletRequest(HttpServletRequest original, Obfuscator obf,
			Set obfuscatedParamNames) {

		super(original);
		if(obf == null)
			throw new NullPointerException();
		this.obf = obf;
	}

	/**
	 * Creates ObfuscatedHttpServletRequest. This version uses
	 * DEFAULT_OBFUSCATED_PARAM_NAMES as set of parameter names.
	 * 
	 * @param original Original HttpServletRequest.
	 * @param obf Obfuscator used for de-obfuscation.
	 */
	public ObfuscatedHttpServletRequest(HttpServletRequest original, Obfuscator obf) {
		this(original, obf, null/*DEFAULT_OBFUSCATED_PARAM_NAMES*/);
		if(obf == null)
			throw new NullPointerException();
		this.obf = obf;
	}

	/**
	 * Returns either de-obfuscated parameter value or original parameter value.
	 * 
	 * @param param Parameter which it to be return.
	 * 
	 * @return If the name of parameter is in the list of obfuscated parameters,
	 * returns de-obfuscated parameter value, otherwise returns original
	 * parameter value.
	 */
	public String getParameter(String param) {
		String value = super.getParameter(param);
		String deObfuscated;
		if(value == null) {
			deObfuscated = value;
		} else {
			deObfuscated = (String)obf.reverseTransformCompoundCommand(value);
		}
		return deObfuscated;
	}

	/**
	 * {@inheritDoc}
	 */
	public Map getParameterMap() {
		// return wrapper for original Map
		return new AbstractMap() {

			public Set entrySet() {
				
				return new AbstractSet(){
					
					Set paramEntrySet = ObfuscatedHttpServletRequest.super.
						getParameterMap().entrySet(); 
					public Iterator iterator() {
						return new Iterator(){

							Iterator orig = paramEntrySet.iterator();
							
							public boolean hasNext() {
								return orig.hasNext();
							}

							public Object next() {
								if(!hasNext()) 
									throw new NoSuchElementException();
								return new Map.Entry() {
									// original entry
									Map.Entry entry = (Map.Entry)orig.next();
									
									public Object getKey() {
										return entry.getKey();
									}

									// value is de-obfuscated
									public Object getValue() {
										String[] values = (String[])entry.getValue();
										String[] deObfuscated = new String[values.length];
										for(int i = 0; i < values.length; i++) {
											deObfuscated[i] = (String)obf.
												reverseTransformCompoundCommand(values[i]);
										}
										return deObfuscated;
									}

									public Object setValue(Object arg0) {
										throw new UnsupportedOperationException();
									}
								};
							}

							public void remove() {
								throw new UnsupportedOperationException();
							}
						};
					}

					public int size() {
						return paramEntrySet.size();
					}
				};
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getParameterValues(String name) {
		String[] orig = super.getParameterValues(name);
		String[] deObfuscated = new String[orig.length];
		for(int i = 0; i < orig.length; i++) {
			String value = orig[i];
			if(value != null){
				value = (String)obf.reverseTransformCompoundCommand(value);
			}
			deObfuscated[i] = value;
		}
		return deObfuscated;
	}
	
	
	
}
