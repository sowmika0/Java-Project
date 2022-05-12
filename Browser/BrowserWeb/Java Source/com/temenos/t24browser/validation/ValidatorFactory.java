package com.temenos.t24browser.validation;

import com.temenos.t24browser.beans.TemenosBean;
import com.temenos.tsdk.exception.ValidationException;
import com.temenos.tsdk.validation.Validator;

// TODO: Auto-generated Javadoc
/**
 * The Class ValidatorFactory.
 * 
 * @author kdevlin
 */
public final class ValidatorFactory
{

	/**
	 * Gets the single instance of ValidatorFactory.
	 * 
	 * @param className the class name
	 * @param bean the bean
	 * 
	 * @return single instance of ValidatorFactory
	 * 
	 * @throws ValidationException the validation exception
	 */
	public static Validator getInstance(final String className, final TemenosBean bean) throws ValidationException
	{

		Validator validation = null;

		try
		{
			Class validationClass = Class.forName(className);
			validation = (Validator) validationClass.newInstance();

		}
		catch (ClassNotFoundException e)
		{
			throw (new ValidationException("Invalid validation class: " + e.getMessage()));
		}
		catch (IllegalAccessException e)
		{
			throw (new ValidationException("IllegalAccessException " + e.getMessage()));
		}
		catch (InstantiationException e)
		{
			throw (new ValidationException("IllegalAccessException " + e.getMessage()));
		}

		return validation;
	}
}
