package com.temenos.arc.security.jaas;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Helper class providing reflection functionality for use by factory classes  
 * @author jannadani
 *
 */
final class DelegateFactory {

    private static DelegateFactory INSTANCE = new DelegateFactory();

    static final Object create(final String className, final Class[] paramTypes, final Object[] params) 
    		throws LoginModuleException {
        return INSTANCE.createDelegate(className, paramTypes, params);
    }

    private DelegateFactory() {
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
        } catch (SecurityException e) {
            throw new LoginModuleException("Insufficient privileges to construct " + clazz.getName());
        } catch (NoSuchMethodException e) {
            throw new LoginModuleException("Expected constructor was not found for " + clazz.getName());
        }
        return ctor;
    }

    private Object invokeConstructor(String className, Constructor ctor, Object[] params) {
        Object obj = null;
        try {
            obj = ctor.newInstance(params);
        } catch (IllegalArgumentException e) {
            throw new LoginModuleException("Incorrect parameters used to construct " + className);
        } catch (InstantiationException e) {
            throw new LoginModuleException("Not an instantiable " + className);
        } catch (IllegalAccessException e) {
            throw new LoginModuleException("Insufficient privileges to construct " + className);
        } catch (InvocationTargetException e) {
            throw new LoginModuleException(e.toString());
        }
        return obj;
    }

    private Class loadDelegateClass(String className) {
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new LoginModuleException("Unable to load " + className);
        }
        return clazz;
    }
}
