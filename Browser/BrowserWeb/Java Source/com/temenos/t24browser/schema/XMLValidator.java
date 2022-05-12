package com.temenos.t24browser.schema;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import oracle.xml.parser.schema.XMLSchema;
import oracle.xml.parser.schema.XSDBuilder;
import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.XMLParseException;
import oracle.xml.parser.v2.XMLParser;

import com.temenos.t24browser.xml.XMLRequestManager;

// TODO: Auto-generated Javadoc
/**
 * The Class XMLValidator.
 */
public class XMLValidator
{

   /** The url. */
   private URL url;                               //the URL of the schema
   
   /** The response. */
   private String response = "";                  //the validation response
   
   /** The status. */
   private boolean status  = false;               //whether or not the xml doc has passed validation
   
   /** The xml file name. */
   private String xmlFileName = "";               //the name of the xml file



   /**
    * Instantiates a new XML validator.
    * 
    * @param xmlManager the xml manager
    */
   public XMLValidator(XMLRequestManager xmlManager){

      try{
      	
         String xsd_path = xmlManager.getRootDirectory()+xmlManager.getSchemaLocation();
         String xml = xmlManager.getXMLResponse();
         xmlFileName = xmlManager.getXMLFileName();

         XSDBuilder builder = new XSDBuilder();
         URL    url =  createURL(xsd_path);
         // Build XML Schema Object
         XMLSchema schemadoc = (XMLSchema)builder.build(url);

         process(xml, schemadoc);
      }
      catch (Exception e){
         System.out.println(e.toString());
         e.printStackTrace();
      }

   }


   //Creates a DOM parser and then validates the XML doc against it
   /**
    * Process.
    * 
    * @param XMLFile the XML file
    * @param schemadoc the schemadoc
    * 
    * @throws Exception the exception
    */
   private void process(String XMLFile, XMLSchema schemadoc)
   throws Exception
   {

      DOMParser dp  = new DOMParser();

      // Set Schema Object for Validation
      dp.setXMLSchema(schemadoc);
      dp.setValidationMode(XMLParser.SCHEMA_VALIDATION);
      dp.setPreserveWhitespace (true);

      dp.setErrorStream (System.out);

      try
      {
         setResponse("Parsing xml");
         dp.parse(new StringReader(XMLFile));
         setResponse("The input file - "+xmlFileName+", parsed without errors");
         setStatus(true);
      }
      catch (XMLParseException pe)
      {
         response = "";
         setResponse("Parser Exception: " + pe.getMessage());
      }
      catch (Exception e)
      {
         response = "";
         e.printStackTrace();
         setResponse("NonParserException: " + e.getMessage());
      }

   }

   //whether or not the xml doc has passed schema validation
   /**
    * Sets the status.
    * 
    * @param status the new status
    */
   private void setStatus(boolean status){
      this.status = status;
   }

   //returns the status of the validation
   /**
    * Gets the status.
    * 
    * @return the status
    */
   public boolean getStatus(){
      return status;
   }

   //sets the validation response
   /**
    * Sets the response.
    * 
    * @param line the new response
    */
   private void setResponse(String line){
      response = response+line+"\n";
   }

   //returns the results of the validation
   /**
    * Gets the response.
    * 
    * @return the response
    */
   public String getResponse(){
      return response;
   }




    // Helper method to create a URL from a file name
    /**
     * Gets the schema.
     * 
     * @param fileName the file name
     * 
     * @return the schema
     */
    public String getSchema(String fileName){

        String schema ="";
        try{
            FileReader fr = new FileReader("./"+fileName);
            BufferedReader br = new BufferedReader(fr);
            String s;

            while ((s = br.readLine()) != null)
                {
                    schema = schema+"\n"+s;
                }

            fr.close();
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
        return schema;
    }



   // Helper method to create a URL from a file name
   /**
    * Creates the URL.
    * 
    * @param fileName the file name
    * 
    * @return the URL
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    */
   private URL createURL(String fileName) throws IOException
   {
      url = null;
      try
      {
         url = new URL(fileName);
      }
      catch (MalformedURLException ex)
      {
         File f = new File(fileName);
         try
         {
            String path = f.getAbsolutePath();
            // This is a bunch of weird code that is required to
            // make a valid URL on the Windows platform, due
            // to inconsistencies in what getAbsolutePath returns.
            String fs = System.getProperty("file.separator");
            if (fs.length() == 1)
            {
               char sep = fs.charAt(0);
               if (sep != '/')
                  path = path.replace(sep, '/');
               if (path.charAt(0) != '/')
                  path = '/' + path;
            }
            path = "file://" + path;
            url = new URL(path);
         }
         catch (MalformedURLException e)
         {
            setResponse("Cannot create url for: " + fileName);
         }
      }
      return url;
   }

}
