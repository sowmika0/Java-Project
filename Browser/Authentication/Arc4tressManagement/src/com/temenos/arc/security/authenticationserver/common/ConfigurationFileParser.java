package com.temenos.arc.security.authenticationserver.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/* TODO Could do with some bullet-proofing, as it's currently a bit sensitive to
 changes in the layout of the file being parsed */

/**   
 * This class reads config params from JAAS style config files.
 * @author jannadani
 */
public class ConfigurationFileParser {
    public final static String FILE_PATH_KEY = "ARC_CONFIG_PATH";
    public final static String FILE_APP_NAME_KEY = "ARC_CONFIG_APP_NAME";
    
    private File configFile;    
    private String appName;
    
    private List indicesOfSeparators = new ArrayList();

    /**
     * Overload used when reading from a known path & appname. 
     * @param filePath
     * @param appName
     */
    public ConfigurationFileParser(String filePath, String appName) {
        initFile(filePath);
        this.appName = appName;
        if (appName == null || appName.length() == 0) {
        	appName="ARC";
//            throw new ArcAuthenticationServerException("No app name specified in config file: " + filePath);
        }        
    }

    /**
     * Overload used when the VM parameters specified by FILE_PATH_KEY and FILE_APP_NAME_KEY are set. 
     */
    public ConfigurationFileParser() {
    	String appName = System.getProperty(FILE_APP_NAME_KEY);
    	if (null == appName || appName.length() == 0) {
    		appName = "ARC";
    	}
    	initFile(System.getProperty(FILE_PATH_KEY));
        this.appName=appName;
    }

    /** Retrieves properties parsed from the <code>appName</code> section of the config file pointed to by <code>filePath</code>. 
     * @return map of config params  
     */
    public Map[] parse() {       
        List lines = getLinesFromFile();
        List relevantLines = filterLinesForAppName(lines);

        int numMaps = getNumMaps(relevantLines);
        if (numMaps == 0) {
            Properties props = new Properties();
            Map[] result = new Properties[1];
            result[0] = props;
            return result;
        }
        
        Map[] result = new Properties[numMaps];        
        for (int i=0; i < numMaps; ++i) {                
            Properties props = new Properties();
            String string = stringFromLines(getLinesFromSection(i, relevantLines));
            try {
                InputStream is = new ByteArrayInputStream(string.getBytes("UTF-8"));
                props.load(is);
                result[i] = props;
            } catch (IOException e) {
                throw new ArcAuthenticationServerException(e.getMessage());
            }
        }
        return result;
    }
    
    private List getLinesFromSection(int sectionIndex, List lines) {
        if (sectionIndex >= indicesOfSeparators.size()) {
            throw new IllegalStateException("section parsing out of synch!");
        }
         
        int fromIndex = ((Integer)indicesOfSeparators.get(sectionIndex)).intValue();
        int toIndex  = ((Integer)indicesOfSeparators.get(sectionIndex + 1)).intValue(); 
        return lines.subList(fromIndex, toIndex);        
    }
    
    private int getNumMaps(List lines) {
        if (lines == null) {
            return 0;
        }
        
        int i = -1;
        indicesOfSeparators.add(new Integer(0));
        do {
            i = indexOfSeparatorLine(lines, i + 1);
            if (i != -1) {
                indicesOfSeparators.add(new Integer(i));
            }
        } while ( i != -1 );
        indicesOfSeparators.add(new Integer(lines.size()));
        return indicesOfSeparators.size() - 1;
    }
    
    private int indexOfSeparatorLine(List lines, int startIndex) {
        for (int i = startIndex; i < lines.size(); ++i) {
            String line = ((String) lines.get(i)).trim();
            // look for ; as first character
            int indexOfSemi = line.indexOf(';');
            if ( indexOfSemi == 0) {
                return i;
            }
        } 
        return -1;
    }
    
    private List filterLinesForAppName(List lines) {        
        int startIndex = -1, endIndex = -1;
        for (int i = 0; i < lines.size(); ++i) {
            String line = ((String) lines.get(i)).trim();
            // look for opening brace
            int indexOfBrace = line.indexOf('{');
            if ( indexOfBrace != -1) {
                if (isCorrectAppName(line, indexOfBrace)) {
                    startIndex = i + 1;
                } 
                continue;
            }
            // stop at first } after start
            if (startIndex != -1) {
                int indexOfClosingBrace = line.indexOf('}');
                if (indexOfClosingBrace != -1) {
                    endIndex = i;
                    break;
                }
            }
        }
        if (startIndex != -1) {
            return lines.subList(startIndex, endIndex);
        }
        return null;
    }

    private String stringFromLines(List lines) {
        if (lines == null) {
            return "";
        }
        // process the relevant lines into a String to return
        StringBuffer result = new StringBuffer(); 
        for (int i = 0; i < lines.size(); ++i) {
            String processedLine = process((String) lines.get(i));
            result.append(processedLine);
            result.append('\n');
        }
        return result.toString();
    }
    
    private boolean isCorrectAppName(String line, int indexOfBrace) {
        int indexOfAppName = line.indexOf(appName);
        
        // appname not present at all
        if (indexOfAppName == -1) {
            return false;
        }
        // appname has non whitespace chars in front 
        if (indexOfAppName > 0 
          && !Character.isWhitespace(line.charAt(indexOfAppName - 1)) ) {
            return false;
        }
        // appname has non whitespace chars after appname and before brace
        int indexEndOfAppName = indexOfAppName + appName.length();
        for (int i = indexEndOfAppName + 1; i < indexOfBrace; ++i) {
            if (!Character.isWhitespace(line.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    private String process(String str) {
        String temp = str.trim();
        return temp.replaceAll("\"", "");        
    }
    
    private List getLinesFromFile() {
        BufferedReader buffReader = null;
        try {
            FileReader reader = new FileReader(configFile);
            buffReader = new BufferedReader(reader);            
        } catch(FileNotFoundException e) {
            throw new ArcAuthenticationServerException("could not find config file: " + configFile.getPath());
        }
        List lines = new ArrayList();
        try {
            String line = null; 
            while ((line = buffReader.readLine()) != null) {
                lines.add(line);
            }
            buffReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ArcAuthenticationServerException("could nt read text from file: " + configFile.getPath());
        }        
        return lines;
    }
        
    private void initFile(String filePath) {
        if (filePath == null || filePath.length() == 0) {
            throw new ArcAuthenticationServerException("no config file path specified");
        }
        configFile = new File(filePath);
        if (!configFile.exists()) {
            throw new ArcAuthenticationServerException("Cannot find config file: " + filePath);
        }
    }
    
}
