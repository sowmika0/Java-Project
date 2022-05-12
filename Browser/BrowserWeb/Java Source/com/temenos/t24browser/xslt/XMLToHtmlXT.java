package com.temenos.t24browser.xslt;


/*
 * 					XMLToHtmlXT.java
 * 
 * Passes the following into the contructor:
 * 1) The relative path to the xsl file to be read in
 * 2) The actual XML string to be parsed
 * 3) The path context to set the System Id of the input Source to.
 * 
 * Creates an html file by parsing an xml file into a xslProcessor
 *
 */

//
// MODIFICATIONS:
//
// 10/09/03 - Added a new param to the constructor.  homeDirectory is used 
//			  to change the system id - the base directory for the transforms (where
//			  the xsl live)
//
// 21/10/03 - Cleaned up the systemId path so that it now has OS specific path
//            separaors and only one '.' at the end
//
 


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.Parser;

import com.jclark.xsl.sax.OutputMethodHandlerImpl;
import com.jclark.xsl.sax.XSLProcessor;
import com.jclark.xsl.sax.XSLProcessorImpl;



// TODO: Auto-generated Javadoc
/**
 * The Class XMLToHtmlXT.
 */
public class XMLToHtmlXT {
	
	/** The xsl. */
	private XSLProcessor xsl;             //Used to transform the xml to html
	
	
	//Creates a sax parser then adds an xsl input stream to it.
	/**
	 * Instantiates a new XML to html XT.
	 * 
	 * @param sXslText the s xsl text
	 * @param systemId the system id
	 * @param homeDirectory the home directory
	 * 
	 * @throws Exception the exception
	 */
	public XMLToHtmlXT(String sXslText, String systemId, String homeDirectory ) throws Exception
	{
		
		// Create parser (JAXP)
		SAXParserFactory spf = SAXParserFactory.newInstance ();
		spf.setValidating(false);
		Parser parser =	spf.newSAXParser().getParser();
		
		//check to see if the xsl has a new home directory, and if it does
		//then add it onto the systemId.  This will act as the new base directory.
		String separator = System.getProperty("file.separator");
		if (!homeDirectory.equals("")){
			int idLength = systemId.length();
			//check to see if there is a dot on the end of the system id.
			if (systemId.charAt(idLength-1) == '.')
			{
				//remove the last file separator and the '.'
				systemId = systemId.substring(0,idLength-2);
			}
			systemId = systemId+homeDirectory+separator+".";
		}
		//replace all of the forward slashes with OS specific file separators
		char sep = separator.charAt(0);
		systemId = systemId.replace('/',sep);

		// Create a processor & load stylesheet
		xsl = new XSLProcessorImpl();
		xsl.setParser(parser);
		
		//Read in the xsl file
		File file = new File(sXslText);
		
		//generate the inputSource from the file and the load it into xslProcessor
		InputSource inputSource = new InputSource( new FileInputStream(file) );
		if (inputSource.getSystemId()==null){
			inputSource.setSystemId(systemId);
		}	
		
		//load the xsl into the processor
	    xsl.loadStylesheet(inputSource);

	}


	//Creates an output stream to send the result to.  Runs the transformer
	//by passing the xml into the xslProcessor
	/**
	 * Transform xml.
	 * 
	 * @param sXmlText the s xml text
	 * 
	 * @return the string
	 * 
	 * @throws Exception the exception
	 */
	public String transformXml(String sXmlText) throws Exception
	{
		//Set up output to go to a ByteArrayOutputStream
		OutputMethodHandlerImpl	out= new OutputMethodHandlerImpl(xsl);

        //Set up the output stream
		ByteArrayOutputStream stream=new ByteArrayOutputStream();
		out.setDestination(new XTOutputStream(stream));
		xsl.setOutputMethodHandler(out);

		stream.reset();

		//run the transform        
		xsl.parse(new InputSource(new StringReader(sXmlText)));
		
		String htmlResult = stream.toString();
		return htmlResult;
	}
}

