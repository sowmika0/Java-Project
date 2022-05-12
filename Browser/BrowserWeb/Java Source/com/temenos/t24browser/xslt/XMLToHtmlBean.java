package com.temenos.t24browser.xslt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.jclark.xsl.sax.OutputMethodHandlerImpl;
import com.jclark.xsl.sax.XSLProcessor;
import com.jclark.xsl.sax.XSLProcessorImpl;
import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class XMLToHtmlBean.
 */
public class XMLToHtmlBean implements java.io.Serializable
{
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLToHtmlBean.class);
	
    /** The xsl. */
    private XSLProcessor xsl;             //Used to transform the xml to html
	
	/** The XSL directory. */
	private String XSLDirectory;
	
	/** The separator. */
	private String separator;
    
    /** The xsl error handler. */
    private ErrorHandler xslErrorHandler;

	/**
	 * Instantiates a new XML to html bean.
	 */
	public XMLToHtmlBean()
	{
	}

	/**
	 * Instantiates a new XML to html bean.
	 * 
	 * @param XSLDirectory the XSL directory
	 */
	public XMLToHtmlBean( String XSLDirectory )
	{
		this.separator = System.getProperty("file.separator");
		
		if (XSLDirectory.endsWith(separator) || XSLDirectory.endsWith("/") )
		{
			this.XSLDirectory = XSLDirectory.substring(0, XSLDirectory.length() - 1 );
		}
		else
		{
			this.XSLDirectory = XSLDirectory;
		}
	}
	
	/**
	 * Transform xml.
	 * 
	 * @param styleSheetFileName the style sheet file name
	 * @param xmlText the xml text
	 * 
	 * @return the string
	 * 
	 * @throws Exception the exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public String transformXml( String styleSheetFileName, String xmlText )
    	throws Exception, UnsupportedEncodingException

  	{
  		String htmlResult=""; 
  		try
  		{		
	 		xsl = getTransformer( styleSheetFileName );
	
	  		//Set up output to go to a ByteArrayOutputStream
			OutputMethodHandlerImpl	out= new OutputMethodHandlerImpl(xsl);
	
			//Set up the output stream
			ByteArrayOutputStream stream=new ByteArrayOutputStream();
			out.setDestination(new XTOutputStream(stream));
			xsl.setOutputMethodHandler(out);
	
			stream.reset();
	
			//run the transform
			xsl.parse(new InputSource(new StringReader(xmlText)));
			htmlResult = stream.toString();
  		}
  		catch(Exception e)
  		{
            if (e instanceof SAXParseException) {
                xslErrorHandler.error((SAXParseException)e);
            }
           	LOGGER.error("Exception in XMLToHtmlBean.transformXml():" + e.getMessage());
  		}
		
		return htmlResult;
  	}

  	/**
	   * Gets the transformer.
	   * 
	   * @param xslFileName the xsl file name
	   * 
	   * @return the transformer
	   * 
	   * @throws Exception the exception
	   */
	  private XSLProcessor getTransformer( String xslFileName ) throws Exception
  	{
  		String systemId = XSLDirectory;
  		String homeDirectory = getHomeDirectory(xslFileName);
  		
  		// Create parser (JAXP)
		SAXParserFactory spf = SAXParserFactory.newInstance ();
		spf.setValidating(false);
		Parser parser =	spf.newSAXParser().getParser();

		//check to see if the xsl has a new home directory, and if it does
		//then add it onto the systemId.  This will act as the new base directory.
		if (!homeDirectory.equals("")){
			int idLength = systemId.length();
			//check to see if there is a dot on the end of the system id.
			if (systemId.charAt(idLength-1) == '.')
			{
				//remove the last file separator and the '.'
				systemId = systemId.substring(0,idLength-2);
			}
			//  check the stylesheet path , it's start with .(Dot) or ..(Double Dot) or '/'
			// with help of getFileNamePath function get the appropriate shylesheet file path
			
			String saveXSLDirectory = XSLDirectory;
			XSLDirectory = systemId;
			systemId = getFileNamePath(homeDirectory).concat(separator).concat(".");
			XSLDirectory = saveXSLDirectory;
		}
		else
		{
			systemId = XSLDirectory.concat(separator).concat(".");
		}
		//replace all of the forward slashes with OS specific file separators
		char sep = separator.charAt(0);
		systemId = systemId.replace('/',sep);

		// Create a processor & load stylesheet
		xsl = new XSLProcessorImpl();
		xsl.setParser(parser);
        xslErrorHandler = new ErrorHandlerImpl();
        xsl.setErrorHandler(xslErrorHandler);
  		
  		//Read in the xsl file
		File xslFile = new File( getFileNamePath(xslFileName) );

		//generate the inputSource from the file and the load it into xslProcessor
		InputSource inputSource = new InputSource( new FileInputStream(xslFile) );
		
		if ( inputSource.getSystemId()== null )
		{
			inputSource.setSystemId( systemId );
		}
		
		//load the xsl into the processor
	    xsl.loadStylesheet( inputSource );

	    return xsl;
  	}
  	
  	//Looks to see if the xsl name has an additional directory structure attached to it
	//and if it does it stores it for later use in the XT transformer.  It will
	//use this additional path as the basis for the home directory (where the xsl live)
	/**
	   * Gets the home directory.
	   * 
	   * @param xsl the xsl
	   * 
	   * @return the home directory
	   */
	  private String getHomeDirectory(String xsl){	
		int pathSeparatorPosition = xsl.lastIndexOf("/");
		
		if (pathSeparatorPosition!=-1)
			return xsl.substring(0,pathSeparatorPosition);
		else
			return "";
	}
	
	//return file name in full path
	/**
	 * Gets the file name path.
	 * 
	 * @param fileName the file name
	 * 
	 * @return the file name path
	 */
	private String getFileNamePath(String fileName) {
		
		//check stylesheet path start with slash or not	
	    if (fileName.startsWith("/") )
			return XSLDirectory.concat(fileName);
		else
			return XSLDirectory.concat(separator).concat(fileName);
	}
    
    /**
     * The Class ErrorHandlerImpl.
     */
    static class ErrorHandlerImpl implements ErrorHandler {

        /**
         * Instantiates a new error handler impl.
         */
        public ErrorHandlerImpl() {
        }

        /* (non-Javadoc)
         * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
         */
        public void warning(SAXParseException saxparseexception) {
            printErrorMessage(saxparseexception, "Warning");
        }
    
        /* (non-Javadoc)
         * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
         */
        public void error(SAXParseException saxparseexception) {
            printErrorMessage(saxparseexception, "Error");
        }
    
        /* (non-Javadoc)
         * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
         */
        public void fatalError(SAXParseException saxparseexception) throws SAXException {
            printErrorMessage(saxparseexception, "Fatal Error");
            throw saxparseexception;
        }

        /**
         * Prints the error message.
         * 
         * @param saxparseexception the saxparseexception
         * @param severity the severity
         */
        private void printErrorMessage(SAXParseException saxparseexception, String severity) {

            StringBuffer errString = new StringBuffer("XSL Processor " + severity + ": ");

            String s = saxparseexception.getSystemId();
            int i = saxparseexception.getLineNumber();
            if(s != null)
                errString.append("SystemId=" + s + ": ");
            if(i >= 0)
                errString.append("LineNum=" + i + ": ");

            errString.append(saxparseexception.getMessage());

            LOGGER.debug(errString);

        }

    }
}
