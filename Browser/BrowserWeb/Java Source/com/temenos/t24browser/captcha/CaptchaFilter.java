package com.temenos.t24browser.captcha;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.octo.captcha.service.CaptchaServiceException;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.ofs.OfsServerConfig;
import com.temenos.t24browser.utils.Utils;

public class CaptchaFilter implements Filter
{
	/** The logger. */
	private static Logger LOGGER = LoggerFactory.getLogger(CaptchaFilter.class);

	/** Context Parameters **/
	public static final String CAPTCHA_IMAGE_TYPE_INIT_PARAM = "captchaImageType";
	
	FilterConfig ivFilterConfig = null;
	ServletContext ivServletContext = null;
	
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		LOGGER.debug("CaptchaFilter : doFilter");
	
		if (!(request instanceof HttpServletRequest)) {
            throw new ServletException("Filter supports only HTTP requests");
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
		if ( validCaptchaInput( httpRequest ) == Boolean.FALSE ) 
		{
			HttpServletResponse resp = (HttpServletResponse) response;
	          
            // now redirect to the error page either defined on the page or in the ofs-server.config file
            LOGGER.warn("Redirecting to error page...");
            
            OfsServerConfig ofsConfig = new OfsServerConfig( ivServletContext );
            String errorPage = Utils.getValue( httpRequest, "captchaFailurePage");
			
			if ( ( errorPage == null ) || ( errorPage.equals("") ) )
			{
				errorPage = ofsConfig.getDefaultCaptchaFailurePage();
			}
            
			errorPage = httpRequest.getContextPath() + errorPage;
            resp.sendRedirect( errorPage );
		}
		else
		{
           chain.doFilter(request, response);  
		}
	}

	public void init(FilterConfig config) throws ServletException 
	{
		LOGGER.debug("CaptchaFilter : init");
		ivFilterConfig = config;
		ivServletContext = ivFilterConfig.getServletContext();
	}
	
	public void destroy() 
	{

	}
	
	private Boolean validCaptchaInput( HttpServletRequest request )
	{
		Boolean isResponseCorrect = Boolean.FALSE;
		
        // Remember that we need an id to validate!
        String captchaId = request.getSession().getId();
        
        // Retrieve the response
        String response = request.getParameter("captchaResponse");
    	LOGGER.debug("CAPTCHA response received : " + response);
        
        // Call the Service method
        try
        {
	        // Get the type of Captcha image form the web.xml context parameter
	        String captchaImageType = Utils.getServletContextParameter(ivServletContext, CAPTCHA_IMAGE_TYPE_INIT_PARAM);
	        String servletContextPath = ivServletContext.getRealPath("");

        	isResponseCorrect = CaptchaServiceSingleton.getInstance(captchaImageType, servletContextPath).validateResponseForID(captchaId, response);
        }
        catch (CaptchaServiceException e)
        {
              // Should not happen, may be thrown if the id is not valid
        }
        
        return isResponseCorrect;
	}
}
