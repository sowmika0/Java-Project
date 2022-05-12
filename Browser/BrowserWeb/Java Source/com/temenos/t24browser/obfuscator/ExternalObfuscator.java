package com.temenos.t24browser.obfuscator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import com.temenos.t24browser.utils.FileManager;


// TODO: Auto-generated Javadoc
/**
 * Class which obfuscates javascript files by using JavaScript compress program
 * and obfuscator Packer. Uses library Rhino to execute Packer.
 * 
 * @author mludvik
 */
public class ExternalObfuscator {
		
		/** The Constant T24_CONSTS. */
		private static final String T24_CONSTS = "T24_constants.js";

		/** JavaScript files need to be processed in the right order. */
		private static final String[] FILES = {
			"yahoo.js",
			"event.js",
			"customEvents.js",
			"general.js",
			"commandline.js",
			"Deal.js",
			"dropdown.js",
			"dynamicHtml.js",
			"enquiry.js",
			"explorer.js",
			"fastpath.js",
			"help.js",
			"jsp.js",
			"menu.js",
			"request.js",
			"tabs.js",
			"validation.js",
			"compositescreen.js",
			"captcha.js",
			"ARC/tabbedMenu.js",
	        "ARC/FragmentUtil.js",
			"ARC/FragmentEvent.js",
			"ARC/Fragment.js",
        	"ARC/T24_constants.js",
        	"ARC/History.js",
        	"ARC/Print.js",
        	"charting/DataSet.js",
	        "charting/DataSeries.js",
	        "charting/main.js",
	        "charting/Area.js",
	        "charting/Axis.js",
	        "charting/Point2D.js",
	        "charting/Segment.js",
	        "charting/OrdinalLineGraph.js",
	        "charting/OrdinalBarGraph.js",
	        "charting/Legend.js",
	        "charting/Bar.js",
	        "workflow.js",
	        "fieldcontext.js", // used only in context enquiry context.jsp
            "FieldGroup.js",
            "frequency.js",
            "recurrence.js",
            "charting/MultiPieChart.js",
            "charting/OrdinalBarLine.js",
            "t24Updates.js"
	    };

		/**
		 * Instantiates a new external obfuscator.
		 */
		private ExternalObfuscator(){}

		/**
		 * Obfuscate input JavaScript files to one output JavaScript files.
		 * 
		 * @param packerFile Packer JavaScript.
		 * @param inputFiles JavaScript files which are to be obfuscated.
		 * @param outputFile Output JavaScript file.
		 * 
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static void obfuscate(String packerFile, String[] inputFiles /*String path*/, String outputFile)
			throws IOException{

			ByteArrayOutputStream origJS = new ByteArrayOutputStream(1024);

			for(int i = 0; i < inputFiles.length; i++){
				File f = new File(inputFiles[i]);
				if(f.exists()) {
					InputStream in = new BufferedInputStream(new FileInputStream(f));
					FileManager.copyFile(in, origJS);
				}
			}

			Context cx = Context.enter();

			// Initialize the standard objects (Object, Function, etc.)
			// This must be done before scripts can be executed. Returns
			// a scope object that we use in later calls.
			Scriptable scope = cx.initStandardObjects();

			// Collect the arguments into a single string.

			ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
			InputStream is = new BufferedInputStream(new FileInputStream(packerFile));
			FileManager.copyFile(is, os);
			String packer = os.toString();

			// Compile packer.js
			cx.evaluateString(scope, packer, "packer.js", 0, null);

			// Define function for obfuscation
			String jsFce = "function packString(arg){return pack(arg, 62, true, false )}; ";

			// Compile auxiliary function
			Function fce = cx.compileFunction(scope, jsFce, "auxiliary function", 1, null);

			// Prepare arguments
			Object obj[]  = {origJS.toString()};

			// Call fucntion with prepared arguments
			Object result = fce.call(cx, scope, scope, obj);

			// Now evaluate the string we've colected.

			OutputStream output = new BufferedOutputStream(
					new FileOutputStream(new File(outputFile)));
			output.write(Context.toString(result).getBytes());
			output.close();
		}

		/**
		 * Executes external obfuscation. Expects three arguments, packer file,
		 * directory which contains script files and JavaScript output file.
		 * 
		 * @param args the args
		 * 
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static void main(String[] args) throws IOException {
			if(args.length != 3) {
				throw new IllegalArgumentException("Expected three arguments, " +
						"packer file, directory which contains script files " +
						"and output file.");
			}
			String packer = args[0];
			String path = args[1];
			String outputFile = args[2];
			String[] files = new String[FILES.length];
			for(int i = 0; i < files.length; i++) {
				files[i] =  path + File.separator + FILES[i];
			}
			obfuscate(packer, files /*path*/, outputFile);
		}
}
