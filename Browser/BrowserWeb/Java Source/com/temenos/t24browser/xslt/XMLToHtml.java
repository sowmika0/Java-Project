
////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   XMLToHtml
//
//  Description   :   Converts a XML file to HTML using a XSL Style Sheet.
//
//  Modifications :
//
//    02/04/02   -    Initial Version.
//    03/09/02	 -	  Store compiled stylesheets in a cache to aid performance.
//
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.xslt;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

// TODO: Auto-generated Javadoc
/**
 * The Class XMLToHtml.
 */
public class XMLToHtml
{
  
  /** The iv transformer. */
  private Transformer ivTransformer = null;			// The xsl transformer  

  // Create XML from XSL String
  /**
   * Instantiates a new XML to html.
   * 
   * @param sXslText the s xsl text
   * 
   * @throws TransformerException the transformer exception
   * @throws TransformerConfigurationException the transformer configuration exception
   * @throws FileNotFoundException the file not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public XMLToHtml( String sXslText )
    throws TransformerException, TransformerConfigurationException,
           FileNotFoundException, IOException
  {
  	// Using pure XSL text so just create a new transformer
    TransformerFactory tFactory = TransformerFactory.newInstance();
    StreamSource xslStreamSource = new StreamSource( new ByteArrayInputStream( sXslText.getBytes() ) );
    ivTransformer = tFactory.newTransformer( xslStreamSource );
  }

  // Create XML from XSL File
  /**
   * Instantiates a new XML to html.
   * 
   * @param sXslFileName the s xsl file name
   * @param ivCache the iv cache
   * 
   * @throws TransformerException the transformer exception
   * @throws TransformerConfigurationException the transformer configuration exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public XMLToHtml( String sXslFileName, Map ivCache )
    throws TransformerException, TransformerConfigurationException,
           IOException
  {
  	// Retrieves compiled XSL file
  	XSLTemplateWrapper tWrapper = (XSLTemplateWrapper) ivCache.get( sXslFileName );
  	
  	ivTransformer = tWrapper.getStylesheetTemplate().newTransformer();
  }

  // Create HTML String from XML String
  /**
   * Transform xml.
   * 
   * @param sXmlText the s xml text
   * 
   * @return the string
   * 
   * @throws TransformerException the transformer exception
   * @throws TransformerConfigurationException the transformer configuration exception
   * @throws FileNotFoundException the file not found exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public String transformXml( String sXmlText )
    throws TransformerException, TransformerConfigurationException,
           FileNotFoundException, IOException
  {
  	// Set-up the streams to transform
    StreamResult htmlStreamResult = new StreamResult( new ByteArrayOutputStream() );
    StringReader sReader = new StringReader( sXmlText );
    StreamSource strSource = new StreamSource( sReader );
    
    // Encode the transformed output to allow for foreign character sets
    ivTransformer.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
    
    ivTransformer.transform( strSource, htmlStreamResult );
    ByteArrayOutputStream outStream = (ByteArrayOutputStream) htmlStreamResult.getOutputStream();
    return( outStream.toString( "UTF-8" ) );
  }
}

