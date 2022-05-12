package com.temenos.t24browser.captcha;

import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

/**
 * Singleton class to to get an instance of the Captcha service.
 */
public class CaptchaServiceSingleton
{
	/** The logger. */
	private static Logger LOGGER = LoggerFactory.getLogger(CaptchaServiceSingleton.class);
	
	private static final String TEXT_IMAGE_TYPE = "Text";
	private static final String FISH_EYE_IMAGE_TYPE = "FishEye";
	
	// private static ImageCaptchaService instance = new DefaultManageableImageCaptchaService();

	static CaptchaStore captchaStore = null;
	static CaptchaEngine captchaEngine = null;
	static int minGuarantedStorageDelayInSeconds = 180;
	static int maxCaptchaStoreSize = 100000;
	static int captchaStoreLoadBeforeGarbageCollection = 75000;  
	
	private static ImageCaptchaService instance = null;


	public static ImageCaptchaService getInstance( String captchaImageType, String servletContextPath )
	{
		if (instance == null)
		{
			if ( captchaImageType.equals(FISH_EYE_IMAGE_TYPE) )
			{
				captchaStore = new FastHashMapCaptchaStore();
				captchaEngine = CaptchaFishEyeEngine.create(servletContextPath);
				instance = new DefaultManageableImageCaptchaService(captchaStore, captchaEngine, minGuarantedStorageDelayInSeconds, maxCaptchaStoreSize, captchaStoreLoadBeforeGarbageCollection);
			}
			else
			{
				instance = new DefaultManageableImageCaptchaService();
			}
		}
		
		return instance;
	}
}
