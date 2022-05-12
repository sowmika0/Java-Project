package com.temenos.t24browser.captcha;

import java.awt.image.ImageFilter;
import java.io.File;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.FileReaderRandomBackgroundGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.deformation.ImageDeformationByFilters;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.fisheye.FishEyeFactory;

public class CaptchaFishEyeEngine extends ListImageCaptchaEngine 
{
	// Path t the Fish Eye Captcha background images
	private static String BACKGROUND_IMAGES_PATH = "/plaf/images/captchaFishEyeBackgrounds";
	private static String ivImagesPath = "";
	

	protected void buildInitialFactories()
	{
		// Build filters 
		com.jhlabs.image.SphereFilter sphere = new com.jhlabs.image.SphereFilter(); 
		sphere.setRefractionIndex(6); 
		ImageDeformation sphereDef = new ImageDeformationByFilters(new ImageFilter[]{sphere}); 

		// Add background from files 
		// The FishEyeFactory constructor takes the following :-
		//		background image		- random image from a directory
		//		image deformation		- deformation shape to be apply on the background image
		//		scale					- size of the deformed part (percent)
		//		tolerance				- the max distance to the centre of the deformation accepted by the validation routine in pixels.
			 
		BackgroundGenerator generator = new FileReaderRandomBackgroundGenerator(new Integer(250), new Integer(250), ivImagesPath);
		addFactory(new FishEyeFactory(generator, sphereDef, new Integer(20), new Integer(20))); 
	}
	
	private CaptchaFishEyeEngine()
	{
		
	}
	
	public synchronized static CaptchaFishEyeEngine create(String contextPath)
	{
		ivImagesPath = contextPath + BACKGROUND_IMAGES_PATH;
		return new CaptchaFishEyeEngine();
	}
}
