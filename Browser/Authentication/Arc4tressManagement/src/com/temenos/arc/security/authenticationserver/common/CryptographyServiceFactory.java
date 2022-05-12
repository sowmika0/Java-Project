package com.temenos.arc.security.authenticationserver.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

final class CryptographyServiceFactory {

	private static CryptographyServiceFactory INSTANCE = new CryptographyServiceFactory();
    protected Logger logger = LoggerFactory.getLogger(CryptographyServiceFactory.class);

	static final Object create(final String className, final Class[] paramTypes, final Object[] params) 
	{
		return INSTANCE.createDelegate(className, paramTypes, params);
	}

	private CryptographyServiceFactory() {
		super();
	}

	private Object createDelegate(final String className, final Class[] paramTypes, final Object[] params) {
		Class clazz = this.loadDelegateClass(className);
		Constructor ctor = this.delegateConstructor(clazz, paramTypes);
		return this.invokeConstructor(className, ctor, params);
	}

	private Constructor delegateConstructor(Class clazz, Class[] paramTypes) {
		Constructor ctor = null;
		try {
			ctor = clazz.getDeclaredConstructor(paramTypes);
		} catch (NoSuchMethodException e) {
			throw new ArcAuthenticationServerException("Expected constructor was not found for " + clazz.getName());			
		}
		return ctor;
	}

	private Object invokeConstructor(String className, Constructor ctor, Object[] params) {
		Object obj = null;
		try {
			obj = ctor.newInstance(params);
		} catch (IllegalArgumentException e) {
			throw new ArcAuthenticationServerException("Incorrect parameters used to construct " + className);
		} catch (InstantiationException e) {
			throw new ArcAuthenticationServerException("Not an instantiable " + className);
		} catch (IllegalAccessException e) {
			throw new ArcAuthenticationServerException("Insufficient privileges to construct " + className);
		} catch (InvocationTargetException e) {
			logger.error("class name = " + className);
			logger.error("exception: " + e);
			throw new ArcAuthenticationServerException(e.toString());
		}
		return obj;
	}

	private Class loadDelegateClass(String className) {
		Class clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new ArcAuthenticationServerException("Unable to load " + className);
		}
		return clazz;
	}
}
