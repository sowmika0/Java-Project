package com.temenos.t24browser.captcha;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.octo.captcha.service.CaptchaServiceException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.utils.Utils;


public class CaptchaImageServlet extends HttpServlet
{
	/** The logger. */
	private static Logger LOGGER = LoggerFactory.getLogger(CaptchaImageServlet.class);
	
	/** Context Parameters **/
	public static final String CAPTCHA_IMAGE_TYPE_INIT_PARAM = "captchaImageType";
	
	
	public void init(ServletConfig servletConfig) throws ServletException
	{
        super.init(servletConfig);
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException
    {
       byte[] captchaChallengeAsJpeg = null;
       
       // The output stream to render the Captcha image as jpeg into
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        
        try
        {
	        // Get the session id that will identify the generated Captcha.
	        // The same id must be used to validate the response, the session id is a good candidate!
	        String captchaId = httpServletRequest.getSession().getId();
	        
	        // Get the type of Captcha image form the web.xml context parameter
	        String captchaImageType = Utils.getServletContextParameter(this.getServletContext(), CAPTCHA_IMAGE_TYPE_INIT_PARAM);
	        String servletContextPath = getServletContext().getRealPath("");
	        	
	        // Call the ImageCaptchaService getChallenge method
            BufferedImage challenge = CaptchaServiceSingleton.getInstance(captchaImageType, servletContextPath).getImageChallengeForID(captchaId, httpServletRequest.getLocale());

            // A jpeg encoder
            JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(jpegOutputStream);
            jpegEncoder.encode(challenge);
        }
        catch (IllegalArgumentException e)
        {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        } 
        catch (CaptchaServiceException e)
        {
            httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();

        // Flush it in the response
        httpServletResponse.setHeader("Cache-Control", "no-store");
        httpServletResponse.setHeader("Pragma", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = httpServletResponse.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
    }
}
