package com.temenos.arc.security.jaas;

import java.io.File;
import java.net.URL;

/**
 * Utility class that calculates paths within the file structure for use with the File class. 
 * @author jannadani
 *
 */
public class FilePathhelper {
    static final String PROJECT_ROOT_TO_TEST_PATH = "/test/com/temenos/arc/security/jaas/";
    static final String PROJECT_ROOT_TO_FILTER_TEST_PATH = "/test/com/temenos/arc/security/filter/";
    static private File projectRoot = null;    
    static String getPathToJaas(){
        return getProjectRoot().getPath() + PROJECT_ROOT_TO_TEST_PATH; 
    }
    public static String getPathToFilter(){
        return getProjectRoot().getPath() + PROJECT_ROOT_TO_FILTER_TEST_PATH; 
    }
    public static String getPathToRoot() {
        return getProjectRoot().getPath();
    }
    public static File getProjectRoot() {
        if (projectRoot == null) {
            String classFilePath = getPath("OtpFromFile.class");
            if (classFilePath == null) {
                throw new IllegalStateException("Could not find OtpFromFile.class");
            }
            projectRoot = new File(classFilePath).getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile();
            if (projectRoot == null) {
                throw new IllegalStateException("Could not initialise projectRoot");
            }
        }
        return projectRoot;
    }

    static private String getPath(String filename) {
        String path = null;
        URL url = FilePathhelper.class.getResource(filename);
        if (url != null) {
            path = url.getPath().replaceAll("%20", " "); // possibly better to use URL decoding?
            System.out.println(path);
        }
        return path;
    }    
}
