////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   XSLTemplateWrapper
//
//  Description   :   A wrapper class for the XSL Transformer used by XMLToHtml.
//					  Determines whether a XSL file has changed since it was 
//					  last compiled.
//
//  Modifications :
//
//    03/09/02   -    Initial Version.
//
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.xslt;

import java.io.File;
import java.io.Serializable;
import javax.xml.transform.Templates;

// TODO: Auto-generated Javadoc
/**
 * The Class XSLTemplateWrapper.
 */
public class XSLTemplateWrapper implements Serializable
{
  
  /** The iv template. */
  private Templates ivTemplate;					// Compiled stylesheet
  
  /** The iv xsl file. */
  private File ivXslFile;						// XSL file
  
  /** The iv compile timestamp. */
  private long ivCompileTimestamp;				// Last time stylesheet compiled

  /**
   * Instantiates a new XSL template wrapper.
   * 
   * @param tStyleSheetTemplate the t style sheet template
   * @param fXslFile the f xsl file
   */
  public XSLTemplateWrapper( Templates tStyleSheetTemplate, File fXslFile )
  {
	ivTemplate = tStyleSheetTemplate;
	ivXslFile = fXslFile;
	ivCompileTimestamp = fXslFile.lastModified();
  }
  
  // Check to see if file has been updated or not
  /**
   * Checks if is stale.
   * 
   * @return true, if is stale
   */
  public boolean isStale()
  {
  	return( ivXslFile.lastModified() != ivCompileTimestamp );
  }
  
  // Get the XSL template
  /**
   * Gets the stylesheet template.
   * 
   * @return the stylesheet template
   */
  public Templates getStylesheetTemplate()
  {
  	return( ivTemplate );
  }
}

