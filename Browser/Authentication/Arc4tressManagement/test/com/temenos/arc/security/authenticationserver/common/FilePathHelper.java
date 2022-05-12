package com.temenos.arc.security.authenticationserver.common;

import java.io.File;
import java.net.URL;


/**
 * Utility class that calculates paths within the file structure for use with the File class. 
 * @author jannadani
 *
 */
public class FilePathHelper {
    static final String PROJECT_ROOT_TO_TEST_PATH = "/test/com/temenos/arc/security/";
    static private File projectRoot = null;    
    static public String getPathToSecurityDir(){
        return getProjectRoot().getPath() + PROJECT_ROOT_TO_TEST_PATH; 
    }
    static public String getPathToRoot() {
        return getProjectRoot().getPath();
    }
    static private File getProjectRoot() {
        if (projectRoot == null) {
            String classFilePath = getPath("FilePathHelper.class");
            if (classFilePath == null) {
                throw new IllegalStateException("Could not find OtpFromFile.class");
            }
            projectRoot = new File(classFilePath).getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile();
            if (projectRoot == null) {
                throw new IllegalStateException("Could not initialise projectRoot");
            }
        }
        return projectRoot;
    }

    static private String getPath(String filename) {
        String path = null;
        URL url = FilePathHelper.class.getResource(filename);
        if (url != null) {
            path = url.getPath().replaceAll("%20", " "); // possibly better to use URL decoding?
            System.out.println(path);
        }
        return path;
    }    
}
