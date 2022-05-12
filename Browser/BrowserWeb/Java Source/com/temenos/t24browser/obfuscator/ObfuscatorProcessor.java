package com.temenos.t24browser.obfuscator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.TransformerException;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.temenos.t24browser.utils.FileManager;

// TODO: Auto-generated Javadoc
/**
 * Creates obfuscated JavaScript files from original and, creates properties
 * file with common T24 obfuscation mapping and obfuscates T24 constants
 * XSL file.
 * 
 * @author mludvik
 */
public class ObfuscatorProcessor {
	
	/** The Constant OBFUSCATED_DIR. */
	private static final String OBFUSCATED_DIR = "obfuscated"; 
	
	/** The Constant T24_CONSTANTS_JS_NAME. */
	private static final String T24_CONSTANTS_JS_NAME = "T24_constants.js";
	//private static final String COMMON_TRANSLATION_TABLE = "commonCommands.prop";
	
	/** The obf. */
	private AllTokensObfuscator obf;
	
	/** The scripts path. */
	private String scriptsPath;
	
	/** The xsl t24 consts path. */
	private String xslT24ConstsPath;
	
	/** The obfuscated scripts path. */
	private String obfuscatedScriptsPath;
	
	/** The xsl t24 consts file. */
	private File xslT24ConstsFile;
	
	/** The scripts dir. */
	private File scriptsDir;
	
	/** The common commands file. */
	private File commonCommandsFile;
	
	/**
	 * The Constructor.
	 * 
	 * @param obf Obfuscator used for obfuscation
	 * @param scriptsPath Directory with original JavaScript files.
	 * @param xslT24ConstsPath XSL File with T24 commands mapping
	 * @param commonCommandsFilePath File with common obfuscated T24 commands
	 * mapping.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private ObfuscatorProcessor(AllTokensObfuscator obf, String scriptsPath, String xslT24ConstsPath, String commonCommandsFilePath) throws IOException {
		this.obf = obf;
		xslT24ConstsFile= new File(xslT24ConstsPath);
		this.xslT24ConstsPath = xslT24ConstsFile.getAbsolutePath();
		scriptsDir = new File(scriptsPath);
		this.scriptsPath = scriptsDir.getAbsolutePath();
		obfuscatedScriptsPath = this.scriptsPath + File.separator + OBFUSCATED_DIR;
		commonCommandsFile = new File(commonCommandsFilePath);
	}
	
	/**
	 * Creates obfuscated JavaScript files from original and creates properties
	 * file from obfuscation table.
	 * 
	 * @param args First argument is directory with original JavaScript files,
	 * second argument is XSL file with T24 commands mapping and third argument
	 * is file with common obfuscated T24 commands mapping. (both files are created)
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		if(args.length < 3)
			throw new IllegalArgumentException("Argumetns expected: First " +
					"argument is directory with original JavaScript files, " +
					"second argument is file XSL file with T24 commands " +
					"mapping and third argument is file with common obfuscated T24 " +
					"commands mapping. (both files are created)");
		ObfuscatorProcessor proc = new ObfuscatorProcessor(
				new AllTokensObfuscator(new CharMappingObfuscationAlgorithm()), 
				args[0], args[1], args[2]);
		proc.obfuscate();
	}
	
	/**
	 * Obfuscates JavaScript files from original and, creates properties
	 * file with common T24 obfuscation mapping and obfuscates T24 constants
	 * XSL file.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void obfuscate() throws IOException {
		File obfuscatedScriptsDir = new File(obfuscatedScriptsPath);
		obfuscatedScriptsDir.mkdir();
		obfuscateDir(scriptsDir);
		enquiryJavascriptFileFix();
		obfuscateXSLFile(xslT24ConstsFile);
		Properties prop = obf.getTranslationTable();
		OutputStream commonCommandsOutStr = new BufferedOutputStream(new FileOutputStream(
				commonCommandsFile));
		prop.store(commonCommandsOutStr, 
				"Translation table for common T24 commands.");
		commonCommandsOutStr.close();

	}
	
	/**
	 * Recursively obfuscates directory with all subdirectories.
	 * 
	 * @param dir the dir
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void obfuscateDir(File dir) throws IOException {
		File[] childs = dir.listFiles();
		for(int i = 0; i < childs.length; i++) {
			File child = childs[i];
			if(child.isDirectory()) {
				if(!child.getName().equals(OBFUSCATED_DIR)) {
					// recursive processing
					obfuscateDir(child);
				}
			} else {
				if(!child.getName().equals(T24_CONSTANTS_JS_NAME) && child.getName().endsWith(".js")) {
					obfuscateJSFile(child);
				}
			}
		}
	}
	
	/**
	 * Obfuscates given JavaScript file.
	 * 
	 * @param origFile the orig file
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void obfuscateJSFile(File origFile) throws IOException{
		String relPath = origFile.getAbsolutePath().substring(
				scriptsPath.length());
		File newFile = new File(obfuscatedScriptsPath + relPath);
		newFile.getParentFile().mkdirs();
    	InputStream in = new BufferedInputStream(new FileInputStream(origFile));
    	InputStream obfuscatedIn = new ObfuscatedInputStream(in, obf);
    	OutputStream out = new BufferedOutputStream(new FileOutputStream(newFile));
    	for(int ch = obfuscatedIn.read(); ch != -1; ch = obfuscatedIn.read()) {
    		out.write(ch);
    	}
    	obfuscatedIn.close();
    	out.close();
	}
	
	/**
	 * Enquiry javascript file fix.
	 */
	private void enquiryJavascriptFileFix() {
		File f = new File(obfuscatedScriptsPath + File.separator + "enquiry.js");
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(f));
			StringBuffer jsFile = FileManager.readFileFromInputStream(in);
			Pattern p = Pattern.compile("_display_|_Display_|_print_|_Print_|_save_|_Save_"); 
			Matcher m = p.matcher(jsFile);
			while(m.find()) {
				String transformed = "\"" + obf.transform(m.group().substring(1, m.group().length() -1)) + "\"";
				jsFile.replace(m.start(), m.end(), transformed);
			}
			in.close();
			FileOutputStream out = new FileOutputStream(f);
			out.write(jsFile.toString().getBytes());
			out.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Obfuscates given XSL file.
	 * 
	 * @param origXSL the orig XSL
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void obfuscateXSLFile(File origXSL) throws IOException {
		DOMParser parser = new DOMParser();
		try {
			parser.parse(new InputSource(new FileInputStream(origXSL)));
		} catch(SAXException e) {
			e.printStackTrace();
		}
		Document document = parser.getDocument();
		try {
			// select all variables from XSL file 
			NodeList variables = XPathAPI.selectNodeList(document, "/xsl:stylesheet/xsl:variable");
			// obfuscate selected variables
			for(int i = 0; i < variables.getLength(); i++) {
				Node variable = variables.item(i);
				Node data = variable.getFirstChild();
				data.setNodeValue(obf.transform(data.getNodeValue()));
			}
		} catch(TransformerException e) {
			e.printStackTrace();
		}
		File originalXSLCopy = new File(xslT24ConstsPath + 
				ObfuscationManager.ORIG_COMMANDS_POSTFIX);
		if(!originalXSLCopy.exists())
			FileManager.copyFile(xslT24ConstsFile, originalXSLCopy);
		File obfXSL = new File(xslT24ConstsPath + 
				ObfuscationManager.OBF_COMMANDS_POSTFIX);
		OutputStream obfStream = new BufferedOutputStream(new FileOutputStream(obfXSL));
		OutputFormat format = new OutputFormat(document);
		// serialize XSL (DOM tree) to the file
		XMLSerializer serializer = new XMLSerializer(obfStream, format);
		serializer.asDOMSerializer();
		serializer.serialize(document);
		obfStream.close();
	}
}
