package com.temenos.t24browser.servlets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
public class HttpServletWrappedRequest extends HttpServletRequestWrapper
{
	private final Map<String, String> modifiableParameters;
	private Map<String, String> allParameters = null;
	/**
      * Create a new request wrapper that will merge additional parameters into
      * the request object without prematurely reading parameters from the
      * original request.
      * 
      * @param request
      * @param additionalParams
      */
	public HttpServletWrappedRequest(final HttpServletRequest request,final Map<String, String> additionalParams)
	{
		super(request);
		modifiableParameters = new TreeMap<String, String>();
		modifiableParameters.putAll(additionalParams);
		}
	//@Override
	public String getParameter(final String name)
	{
		String strings = getParameterMap().get(name);
		return strings;
		}
	//@Override
	public Map<String, String> getParameterMap()
	{
		if (allParameters == null)
		{
			allParameters = new TreeMap<String, String>();
			//allParameters.putAll(super.getParameterMap());
			allParameters.putAll(modifiableParameters);
			}
		//Return an unmodifiable collection because we need to uphold the interface contract.
		return Collections.unmodifiableMap(allParameters);
		}
	//@Override
	public Enumeration<String> getParameterNames()
	{
		return Collections.enumeration(getParameterMap().keySet());
		}
	//@Override
	public String getParameterValue(final String name)
	{
		return getParameterMap().get(name);
		}
	}