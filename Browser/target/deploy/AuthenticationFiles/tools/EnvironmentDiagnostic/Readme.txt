                        ===================================================
                                About ARCIBDiagnostics.ksh Version 1.0
                        ===================================================
    INTRODUCTION :
    ==============
  
    ARCIBDiagnostics.ksh is a diagnostic tool. This tool will check the T24 server environment.
    The tool is currently only supported on the AIX operating system. It is developed and written as a Korn 
    Shell script.
          
    HOW TO INSTALL :
    ================
    Transfer the ARCIB_authentication_files.tar to the T24 Server.
    Extract the tools directory from the tar file.  This contains both the encryption tool and the environment 
    verification tool.
    
    Copy crypto.jar file to your $HOME directory and copy ARCIBDiagnostics to your T24 area.
    
    Execute the script using the below comand
        . ARCIBDiagnostics.ksh
    will execute the script 

    USER INPUTS : 
    =============

    The tool needs the following input from the user twice in its life cycle :
      
    1. TCServer installation location 
    2. 4TRESS installation information (whether 4TRESS has been deployed in JBoss or 
       Websphere) 
    
    For TCServer location , enter absolute path of the tcserver location.
    eg : /.../$USER/TestBase/TestBase.run/tcserver
    if you mention wrong path, the system will read the environment variables from .profile
    
    For 4TRESS installation details, the user has to press 1 or 2.
    Based on the option, tool will execute the respective server code.

    TOOL FUNCTIONALITY : 
    ==================== 

    ARCIBDiagnostics.ksh tool is a diagnostic tool. The functionality of the tool is to check the
    T24 server environment.
       
       Loading environment.vars or .profile variables

       Verifying the environment for CALLJ execution

       Verifying the environment for 4TRESS libraries.

       Verifying the Java security configuration

       Verifying the JCE key store setup.

       Verifying the server.config information
    
       Verifying the required JVM parameters (JBCJVMOPT1 to 5)
    


    
         
     