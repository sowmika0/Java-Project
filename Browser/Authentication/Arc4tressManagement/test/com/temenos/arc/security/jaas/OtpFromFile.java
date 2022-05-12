package com.temenos.arc.security.jaas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.temenos.arc.security.authenticationserver.common.FilePathHelper;


/**
 * Class used by unit tests to pop an OTP from the OTP file.  
 * @author jannadani
 *
 */
public class OtpFromFile {
    static final String FILENAME = "testotps.txt";
    private File otpFile = null;
    
    public OtpFromFile() {        
        initOtpFile();        
    }
   
    private File getOtpFile() {
        return otpFile;
    }

    private void initOtpFile() {
        String otpFilePath = FilePathHelper.getPathToSecurityDir() + "jaas/" + FILENAME;                 
        otpFile = new File(otpFilePath);        
        if (otpFile.exists()) {
            return;
        }
        
        // doesn't exist, so copy it over
        String sourcePath = FilePathHelper.getPathToRoot() + "/test/data/" + FILENAME;
        File source = new File(sourcePath);
        if (!source.exists()) {
            throw new IllegalStateException("couldn't find: " + sourcePath);
        }        
        // copy contents
        copyText(source, otpFile);
        
        if (!otpFile.exists()) {
            throw new RuntimeException("Failed to find or copy otp file: " + otpFile.getPath());
        }
        if (!otpFile.canWrite()) {
            throw new RuntimeException("Cannot write to otp file: " + otpFile.getPath());
        }
    }
    
    public String getOtp() {
        String otp = consumeFromOtpFile();      
        System.out.println("Got OTP: " + otp);
        return otp;
    }
    
    private String consumeFromOtpFile() {
        List lines = OtpFromFile.readFile(getOtpFile());
        String line = (String) lines.remove(0);
        File tempFile = new File(getOtpFile().getAbsolutePath() + ".tmp");
        try {
            if (tempFile.exists()) {
                if (!tempFile.canWrite()) {
                    throw new RuntimeException("Cannot write to temp file: " + tempFile.getPath());
                }
                if (!tempFile.delete()) {
                    throw new RuntimeException("Cannot delete temp file: " + tempFile.getPath());
                }
            }            
            tempFile.createNewFile();            
            FileWriter writer = new FileWriter(tempFile);
            for (int i=0; i < lines.size(); ++i) {
                writer.write((String)lines.get(i));
                writer.write('\n');
            }
            writer.close();
            
            // now copy it over the original
            getOtpFile().delete();
            tempFile.renameTo(getOtpFile());                        
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("could not write to file: " + tempFile);
        }
        return line;
    }
    
    static List readFile(File source) {
        BufferedReader buffReader = null;
        try {
            FileReader reader = new FileReader(source);
            buffReader = new BufferedReader(reader);            
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("could not find file: " + source.getPath());
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
            throw new RuntimeException("couldn't read text from file: " + source.getPath());
        }        
        return lines;
    }
    
   static public void copyText(final File source, File target) {
       if (!source.exists()) {
           throw new RuntimeException("Source file does not exist: " + source);
       }
       List lines = readFile(source);
       try{
           target.createNewFile();            
           FileWriter writer = new FileWriter(target);
           for (int i=0; i < lines.size(); ++i) {
               writer.write((String)lines.get(i));
               writer.write('\n');
           }
           writer.close();
       } catch(IOException e) {
           e.printStackTrace();
           System.out.println("Cannot write to file: " + target.getPath());
       }
   }    
}