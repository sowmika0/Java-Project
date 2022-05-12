package com.temenos.t24browser.validation;

import com.temenos.tsdk.foundation.T24Connection;
import com.temenos.tsdk.foundation.TContract;
import com.temenos.tsdk.foundation.TProperty;
import com.temenos.tsdk.foundation.UtilityRequest;
import com.temenos.tsdk.validation.ValidationResponse;
import com.temenos.tsdk.validation.Validator;
import com.temenos.tsdk.xml.XmlUtilities;

// TODO: Auto-generated Javadoc
/**
 * Using the WEB.VALIDATE mechanism provide enrichment for CHECKFILE fields
 */
public class CheckFileValidator implements Validator
{

	/** The Constant COMI_VALUE_TAG. */
	private static final String COMI_VALUE_TAG = "cval";
	
	/** The Constant COMI_ENRI_TAG. */
	private static final String COMI_ENRI_TAG = "cenri";
	
	/** The Constant CHECKFILE_ROUTINE. */
	private static final String CHECKFILE_ROUTINE = "OS.CHECKFILE";

	/**
	 * Instantiates a new check file validator.
	 */
	public CheckFileValidator()
	{
		super();
	}


	/**
	 * Resets the value for the field.
	 * 
	 * @param contract TContract
	 * @param property TProperty
	 * @param connection the connection
	 * 
	 * @return contract containing the enrichment ValidationResponse
	 */
	public ValidationResponse processRequest(TContract contract, TProperty property, T24Connection connection)
	{

		//generate utility request to call OS.CHECKFILE
		String requestArguments;
		requestArguments = contract.getApplicationName();
		requestArguments += ":" + property.getName();
		requestArguments += ":" + property.getValue();
        requestArguments += ":"+ contract.getVersionName();
		String sResponse = null;

		UtilityRequest ur = new UtilityRequest(CHECKFILE_ROUTINE, requestArguments, connection);
		sResponse = ur.sendRequest();

		//extract return values and set properties
		ValidationResponse response = null;
		String sValue, sEnrichment, sErrorMessage;
		response = new ValidationResponse();
		response.addProperty(property);

		XmlUtilities xutils = new XmlUtilities();

		sValue = xutils.getNodeFromString(sResponse, COMI_VALUE_TAG);
		sEnrichment = xutils.getNodeFromString(sResponse, COMI_ENRI_TAG);
		sErrorMessage = xutils.getNodeFromString(sResponse, "msg");

		if (sEnrichment != null && !sEnrichment.equals(""))
			property.setEnrichment(sEnrichment);
		else
			property.setEnrichment("");

		if (sValue != null && !sValue.equals(""))
			property.setValue(sValue);

		return response;
	}
}