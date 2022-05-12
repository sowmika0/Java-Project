////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   XMLTemplateManager
//
//  Description   :   Provides a storage facility for xml templates.
//                    Names (keys) and Values are stored in a Hashtable
//                    hPropertiesTable.
//                    
//
//  Modifications :
//
//  14/01/04    Now implements cloneable and its method clone.
//
////////////////////////////////////////////////////////////////////////////////
package com.temenos.t24browser.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

import javax.servlet.ServletContext;

import com.temenos.t24browser.utils.HashTableManager;

// TODO: Auto-generated Javadoc
/**
 * The Class XMLTemplateManager.
 */
public class XMLTemplateManager extends HashTableManager implements Serializable, Cloneable
{
  
  /** The iv path separator. */
  private String ivPathSeparator;
  
  /** The iv xml template path. */
  private String ivXmlTemplatePath;


  /**
   * Instantiates a new XML template manager.
   * 
   * @param context the context
   */
  public XMLTemplateManager(ServletContext context)
  {
      super();
      ivPathSeparator = System.getProperty("file.separator");
      ivXmlTemplatePath = "WEB-INF" + ivPathSeparator + "xml" + ivPathSeparator + "xmlTemplates";
      retrieveXmlTemplates(context);
  }
  
  
  
	/**
	 * Retrieves all of the xml templates from a directory and adds
	 * them to a hashtable.
	 * 
	 * @param context the context
	 */
	private void retrieveXmlTemplates(ServletContext context){
		
		//ivXmlTemplates = new Hashtable();
		String xmlDirPath = context.getRealPath("") +ivPathSeparator+ivXmlTemplatePath+ivPathSeparator;
		File xmlDir = new File(xmlDirPath);
    	String[] xmlList = xmlDir.list();
    	
    	//run through the list of templates and process each on in turn
    	for (int i=0;i<xmlList.length;i++){
    		getXmlTemplate(xmlDirPath, xmlList[i]);
    	}
	}
	
	
	/**
	 * Reads in a template and adds it to a hashtable in String form.
	 * 
	 * @param xmlDirPath the xml dir path
	 * @param xmlName the xml name
	 */
	private void getXmlTemplate(String xmlDirPath, String xmlName){
    	try {
			File file = new File(xmlDirPath+ivPathSeparator+xmlName);	
			InputStreamReader is = new InputStreamReader(new FileInputStream(file));
			BufferedReader br = new BufferedReader(is);

			String s;
			StringBuffer buffer = new StringBuffer();
			while ((s = br.readLine()) != null) {
				buffer.append(s);
			}
			setProperty(xmlName,buffer.toString());
			is.close();
			is = null;
			br = null;
		} catch (Exception e) {
		}
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException {
			return super.clone();
	}


}