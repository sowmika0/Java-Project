package com.temenos.t24browser.xml;

/**
 *                      CommandProcessor.java
 * Processes a command line.
 * Returns a hashtable of parameters derived from the command line.
 */


import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;


// TODO: Auto-generated Javadoc
/**
 * The Class CommandProcessor.
 */
public class CommandProcessor
{

  /** The application. */
  private String application = "";              	// the application or enquiry indicator(ENQ)
  
  /** The function. */
  private String function = "";                 	// the function
  
  /** The key. */
  private String key = ""; 					 		// the key
  
  /** The version. */
  private String version = "";                  	// the version
  
  /** The ofs function. */
  private String ofsFunction = "";				 	// the function to be run
  
  /** The ofs operation. */
  private String ofsOperation = "";			 	 	// the operation to be run
  
  /** The transaction id. */
  private String transactionId = "";			 	// the transaction id
  
  /** The routine name. */
  private String routineName = "";				 	// the name of the routine
  
  /** The routine args. */
  private String routineArgs = "";              	// the arguments to pass down
  
  /** The request type. */
  private String requestType = "";                  // the type of request to be processed
  
  /** The enqaction. */
  private String enqaction = "";					// The action for the enquiry
  
  /** The enqname. */
  private String enqname = "";						// The enquiry name
  
  /** The valid function. */
  private boolean validFunction;                  	// Whether the function is valid or not
  
  /** The valid special function. */
  private boolean validSpecialFunction;           	// Whether the function is a special function or not
  
  /** The valid function is a key. */
  private boolean keyIsFunction;                  	// Whether the function is a key and is valid or not
  
  /** The valid special function is a key. */
  private boolean keyIsSpecialFunction;           	// Whether the function is a special key function and is valid or not
  
  /** The is real t24 command. */
  private boolean isRealT24Command;					// Whether the command type is a 'real' T24 command e.g. ENQ, TAB COS
  													// not www,http etc.
  
  /** The non t24 command. */
													  private String nonT24Command = "";				// Holds the value of a NON-T24 command
  
  /** The sign on command. */
  private boolean signOnCommand = false;			// Whether the command is a signon command

  /** The parameters. */
  private Hashtable parameters = new Hashtable();  //This will contain all of the parameters derived from the command  
  
  
  /**
   * Instantiates a new command processor.
   */
  public CommandProcessor(){
  }
  
   /**
    * passes in an xml document to extract the command from.
    * 
    * @param xml the xml
    */
  	public CommandProcessor(String xml){
  		String command = getNodeValue(xml,"command");
  		processCommand(command);
  	}
  
    
   /**
    * returns the value of a node in a given xml String.
    * 
    * @param xml the xml
    * @param nodeName the node name
    * 
    * @return the node value
    */
	public String getNodeValue(String xml, String nodeName){
			  
		int nodeLength = nodeName.length();
		int startTagPos = xml.indexOf(nodeName);
		int endTagPos = xml.lastIndexOf(nodeName);
			  
		if ( ( startTagPos == -1 ) || ( endTagPos == -1 ) ){
			return( null );
		}
		else{
			String result = xml.substring((startTagPos+nodeLength+1),(endTagPos-2));
			return result;
		}
	}  
  
	  /**
  	 * takes in a command line and separates it out into the required arguments.
  	 * 
  	 * @param command the command
  	 */
	  public void processCommand(String command)
	  {
		  setIsRealT24Command(true);
		  command = command.trim();
		
		  String prefix = "";
		  String queryPrefix = "";
		  String menuPrefix = "";
		  String startChar = "";
		  String pwPrefix = "";		  
		  int iCommandLength = command.length();
		  
		  startChar = command.substring(0,1);
		  
		  if (iCommandLength > 3)
		  {
			  prefix = command.substring(0,4);
		  }
	  
		  if (iCommandLength > 5)
		  {
			  queryPrefix = command.substring(0,6);
		  }
		  
		  if (iCommandLength > 4)
		  {
			  menuPrefix = command.substring(0,5);
		  }
		  
		  if (iCommandLength > 2)
		  {
			  pwPrefix = command.substring(0, 3);
		  }
		  
		  // Check what type of cammand this is
		  if ( prefix.equals("TAB ") )
		  {
			  // Process the TAB
			  doTab(command);
		  }
		  else if ( prefix.equals("COS ") )
		  {
			  // Process the Composite Screen
			  doCompositeScreen(command);
		  }
		  else if (( menuPrefix.equals("MENU ") ) || ( startChar.equals("?") ))
		  {
			// Process the Menu
			  doMenu(command);
		  }
		  else if (queryPrefix.equals("QUERY ") )
		  {
			  // Run the Enquiry with no Selection Criteria
			  doEnqRun(command);
		  }
		  else if ( menuPrefix.equalsIgnoreCase("https") )
		  {
			  setIsRealT24Command(false);
			  setNonT24Command(command);
		  }
		  else if ( prefix.equalsIgnoreCase("http") )
		  {
			  setIsRealT24Command(false);
			  setNonT24Command(command);
		  }
		  else if ( menuPrefix.equalsIgnoreCase("file:") )
		  {
			  setIsRealT24Command(false);
			  setNonT24Command(command);
		  }
		  else if ( prefix.equalsIgnoreCase("www.") )
		  {
			  setIsRealT24Command(false);
			  command = "http://" + command;
			  setNonT24Command(command);
		  }
		  else if ( prefix.equalsIgnoreCase("url ") )
		  {
			  // Remove the URL prefix
			  String sUrl = command.substring(4, command.length());
			  // Call this function again
			  processCommand(sUrl);
		  }
		  else if ( pwPrefix.equals("PW "))
		  {
			  doPwProcess(command);
		  }
		  else if ( prefix.equals("ENQ "))
		  {
			  // Process the enquiry
			  doEnq(command, "");
		  }
		  else
		  {	
			  // Set the 3 command parameters in the form variables and get the number of arguments
			  int argsNumber = processCommandString( command );
			  
			  setApplicationAndVersion();
			
			  // Check if the supplied function is valid.
			  validFunction = functionCheck();
			  validSpecialFunction = specialFunctionCheck();
			  
			  // Check to see if the supplied key is a function
			  keyIsFunction = keyFunctionCheck();
			  keyIsSpecialFunction = keySpecialFunctionCheck();
			  
			  if ((argsNumber > 2 ) && (keyIsFunction || keyIsSpecialFunction))
			  {
				  argsNumber = 2;
				  String[] splitCommand = command.split( " " );
				  //take the last argument..
				  command = splitCommand[0] + " " + splitCommand[splitCommand.length-1];
					if  (!key.equals("L"))
					{
						// set the function here if listing
						function = key;
						key = "";
					}
			  }
			
			  // Check for a sign off command or abbreviation
			  if ( checkSignOffCommand( application ) )
			  {
				  doSignOff();
			  }
			  else if ( checkSignOnCommand(application))
			  {
				  setIsRealT24Command(false);
				  doSignOn();
			  }
			  else if ( function.equals("E") )
			  {
				  // Display the Exception list
				  if ( key.equals("E") )
				  {
					  // Get enquiry selection form
					  doEnq( "ENQ %" + application + "$NAU", "" );
				  }
				  else
				  {
					  // Get enquiry results using the last selection criteria
					  doEnq( "%" + application + "$NAU", "BACK" );
				  }
			
				  //**THE CALL TO BRING UP A SECOND WINDOW HAS BEEN REMOVED - WE ONLY WANT THE APPLICATION LIST (AS ABOVE)
			  }
			  else if ( function.equals("L") )
			  {
				  // Display the Live list
				  if ( key.equals("L") )
				  {
					  // Get enquiry selection form
					  doEnq( "ENQ %" + application, "" );
				  }
				  else
				  {
					  // Get enquiry results using the last selection criteria
					  doEnq("%" + application, "BACK");
				  }
			  }
			  else if ( ( argsNumber == 2 ) && ( F3Check(command) == false ) && ( !validFunction )  && ( !validSpecialFunction ) )
			  {
				  // Application with key
				  ofsFunction = "I";	// Default to Input for existing record
				  transactionId = function;	// Second line arg was the key, not a function
				  ofsOperation= "BUILD";
				  routineArgs=command;
				  requestType= "OFS.APPLICATION";
			  }
			  else if ( ( argsNumber == 3 ) && ( F3Check(command) == false ) && ( validFunction ) )
			  {
				  // Application with function and key
				  ofsOperation = "BUILD";
				  routineArgs=command;
				  requestType = "OFS.APPLICATION";	
			  }
			  else
			  {
				  // Application or application and function
				  routineName="OS.NEW.DEAL";
				  routineArgs=command;
				  requestType = "UTILITY.ROUTINE";
			  }
		  }

		  // If the command is a 'Real' T24 Command
		  if (getIsRealT24Command())
		  {
			  generateParameterList();
		  }
	  }
	  
	  /**
  	 * Do tab.
  	 * 
  	 * @param command the command
  	 */
  	private void doTab(String command)
	  {
		  if (command.length() < 5)
		  {
			  // No ID so get out
			  return;
		  }
		  else
		  {
			  routineName="OS.GET.TAB.FRAMES";
			  routineArgs=command;
			  requestType = "UTILITY.ROUTINE";			  
		  }
	  }
	  
	  /**
  	 * Do composite screen.
  	 * 
  	 * @param command the command
  	 */
  	private void doCompositeScreen(String command)
	  {
		  if (command.length() < 5)
		  {
			  // No ID so get out
			  return;
		  }
		  else
		  {
			  routineName="OS.GET.COMPOSITE.SCREEN.XML";
			  routineArgs=command;
			  requestType = "UTILITY.ROUTINE";			  
		  }		  
		  
	  }
	  
	  /**
  	 * Do menu.
  	 * 
  	 * @param command the command
  	 */
  	private void doMenu(String command)
	  {
			String startChar = command.substring(0,1);
			
			if ( startChar.equals("?"))
			{
				// Replace the "?"
				String sMenuId = command.substring(1, command.length());
				command = "MENU " + sMenuId;
			}
			
			routineName="OS.GET.MENU.XML";
			routineArgs=command;
			requestType = "UTILITY.ROUTINE";
	  }
	  
	  /**
  	 * Do enq run.
  	 * 
  	 * @param command the command
  	 */
  	private void doEnqRun(String command)
	  {
		  // Runs the enquiry with no selection screen
		  if (command.length() < 6)
		  {
			  return;
		  }
		  else
		  {
			  // Extract the enquiry name (remove the 'QUERY ' command)
			  String enqName = command.substring(6, command.length());
			  // the enquiry should be run with last known selection criteria.
			  command = "ENQ "+enqName+" LAST";
			  doEnq(command, "SELECTION");
		  }
	  }
	  
	  /**
  	 * Do pw process.
  	 * 
  	 * @param command the command
  	 */
  	private void doPwProcess(String command)
	  {
		  routineName="OS.RUN.PW.PROCESS";
		  routineArgs=command;
		  requestType = "UTILITY.ROUTINE";		  
	  }

	  /**
  	 * Sets the is real t24 command.
  	 * 
  	 * @param value the new is real t24 command
  	 */
  	private void setIsRealT24Command(boolean value)
	  {
		  isRealT24Command = value;
	  }
	  
	  /**
  	 * Gets the is real t24 command.
  	 * 
  	 * @return the is real t24 command
  	 */
  	public boolean getIsRealT24Command()
	  {
		  return isRealT24Command;
	  }
	  
	  /**
  	 * Sets the non t24 command.
  	 * 
  	 * @param value the new non t24 command
  	 */
  	private void setNonT24Command(String value)
	  {
		  nonT24Command = value;
	  }
	  
	  /**
  	 * Gets the non t24 command.
  	 * 
  	 * @return the non t24 command
  	 */
  	public String getNonT24Command()
	  {
		  return nonT24Command;
	  }
  
	  /**
  	 * Sets the is sign on command.
  	 * 
  	 * @param value the new is sign on command
  	 */
  	private void setIsSignOnCommand(boolean value)
	  {
		  signOnCommand = value;
	  }
	  
	  /**
  	 * Gets the is sign on command.
  	 * 
  	 * @return the is sign on command
  	 */
  	public boolean getIsSignOnCommand()
	  {
		  return signOnCommand;
	  }
  	
	  /**
	   * breaks up the command line into its individual elements and assigns them to
	   * relevant variables. Also returns the number of elements in the command line.
	   * 
	   * @param command the command
	   * 
	   * @return the int
	   */
  	private int processCommandString(String command){
  		
  		StringTokenizer tokens = new StringTokenizer(command," ");
  		int tokenCount = tokens.countTokens();
  		
  		try{
  		
  			//get the application or enquiry indicator
  			if(tokens.hasMoreTokens()){
  				application = tokens.nextToken();
  				//get the function
  				if(tokens.hasMoreTokens()){
  					function = tokens.nextToken();
  					ofsFunction = function;
  					//get the key
  					if(tokens.hasMoreTokens()){
  						key = tokens.nextToken();
  						transactionId = key;
  					}
  				}
  			}	
  		}
  		catch(NoSuchElementException e){
  			e.printStackTrace();
  		}
  		
  		return tokenCount;  			
  		
  	}
  	
    /**
     * breaks up the application name into application and verion - if needed.
     */
  	private void setApplicationAndVersion(){
  		
  		StringTokenizer tokens = new StringTokenizer(application,",");
  		int tokenCount = tokens.countTokens();
  		
  		//we either want to split application into application and version, or just
  		//leave the application - by default - the way it is.
  		try{
  			if (tokenCount==2){
  				application = (String)tokens.nextToken();
  				version = (String)tokens.nextToken();
  			}
  		}
  		catch(NoSuchElementException e){
  			e.printStackTrace();
  		}
  	}
  	
  	
  	
 	/**
	  * Checks to see if the function is a valid function.
	  * Returns true if this is so.
	  * Find below the list of all possible functions.
	  * most of these are special though, and will be processed later
	  * in specialFunctionCheck.
	  * {"A","C","D","E","H","I","L","P","Q","R","S","V"}
	  * 
	  * @return true, if function check
	  */ 	
  	private boolean functionCheck(){
  
  		//The following list consists of the only functions required at this moment in time.
  		String[] functionArray = {"E","H","I","L"};
  		int arraySize = functionArray.length;
  	
  		//run through each of the functions in the list and check them against the function
  		for (int i=0;i<arraySize;i++){
  			if (function.equals(functionArray[i]))
				return( true );
  		}	
  		
		return false;
  	}
 
 
  	
  	/**
	   * checks whether the function is a special function.
	   * 
	   * @return true, if special function check
	   */
  	private boolean specialFunctionCheck(){
  		
		
		//The following list consists of the special functions.
  		String[] functionArray = {"A","C","D","P","Q","R","S","V"};
  		int arraySize = functionArray.length;
  	
  		//run through each of the functions in the list and check them against the function
  		for (int i=0;i<arraySize;i++){
  			if (function.equals(functionArray[i]))
				return( true );
  		}	
  		
		return false;
  	}	
  	
  	/**
	  * Checks to see if the key is a valid function.
	  * Returns true if this is so.
	  * Find below the list of all possible functions.
	  * most of these are special though, and will be processed later
	  * in specialFunctionCheck.
	  * {"A","C","D","E","H","I","L","P","Q","R","S","V"}
	  * 
	  * @return true, if function check
	  */ 	
 	private boolean keyFunctionCheck(){
 
 		//The following list consists of the only functions required at this moment in time.
 		String[] functionArray = {"E","H","I","L"};
 		int arraySize = functionArray.length;
 	
 		//run through each of the functions in the list and check them against the function
 		for (int i=0;i<arraySize;i++){
 			if (key.equals(functionArray[i]))
				return( true );
 		}	
 		
		return false;
 	}


 	
 	/**
	   * checks whether the key is a special function.
	   * 
	   * @return true, if special function check
	   */
 	private boolean keySpecialFunctionCheck(){
 		
		
		//The following list consists of the special functions.
 		String[] functionArray = {"A","C","D","P","Q","R","S","V"};
 		int arraySize = functionArray.length;
 	
 		//run through each of the functions in the list and check them against the function
 		for (int i=0;i<arraySize;i++){
 			if (key.equals(functionArray[i]))
				return( true );
 		}	
 		
		return false;
 	}	
  	
  	/**
	   * Checks if the supplied command is a SIGN.OFF command, or abbreviation.
	   * 
	   * @param command the command
	   * 
	   * @return true, if check sign off command
	   */
  	private boolean checkSignOffCommand(String command){
  		
		
		//The following list consists of the possible sign off commands.
  		String[] commandArray = {"SIGN.OFF","SO","LO"};
  		int arraySize = commandArray.length;
  	
  		//run through each of the elements in the list and check them against the command
  		for (int i=0;i<arraySize;i++){
  			if (command.equals(commandArray[i]))
				return( true );
  		}	
  		
		return false;
  	}	
  	
  	/**
	   * Checks if the supplied command is a SIGN.ON command, or abbreviation.
	   * 
	   * @param command the command
	   * 
	   * @return true, if check sign on command
	   */
  	private boolean checkSignOnCommand(String command){
  		
		
		//The following list consists of the possible sign off commands.
  		String[] commandArray = {"SIGN.ON","SON"};
  		int arraySize = commandArray.length;
  	
  		//run through each of the elements in the list and check them against the command
  		for (int i=0;i<arraySize;i++){
  			if (command.equals(commandArray[i]))
				return( true );
  		}	
  		
		return false;
  	}	
  	
  	
  	 /**
 	   * Sets up all the variables for a sign off.
 	   */
  	private void doSignOff(){

		// Set SIGN.OFF command parameters
		routineName = "OS.NEW.DEAL";
		routineArgs = "SIGN.OFF";
		requestType = "UTILITY.ROUTINE";

	}
  	
  	/**
	   * Do sign on.
	   */
	  private void doSignOn()
  	{
  		setIsSignOnCommand(true);
  	}
  	
  	
  	 /**
 	   * Sets up all the variables for an enquiry.
 	   * 
 	   * @param command the command
 	   * @param action the action
 	   */
  	private void doEnq(String command, String action)
  	{
		// Set-up parameters for a Globus enquiry and submit it
		
  		//routineName = "OS.ENQUIRY.SELECTION";
		//routineArgs = command;
		//requestType = "UTILITY.ROUTINE";
		
  		requestType = "OFS.ENQUIRY";
  		
  		if (action.equals(""))
  		{
  			enqaction = "SELECTION";
  		}
  		else
  		{
  			enqaction = action.toUpperCase();
  		}
  		
  		String sPrefix = "";
  		String sEnquiryName = command;
  		// Remove the 'ENQ ' from the command
		if (command.length() > 3)
		{
			sPrefix = command.substring(0,4);
			
			if (sPrefix.equals("ENQ "))
			{
				sEnquiryName = command.substring(4, command.length());
			}
		}
  		enqname = sEnquiryName;

  		// check for any arguments by counting number of spaces.
		StringTokenizer tokens = new StringTokenizer(command," ");
  		int tokenCount = tokens.countTokens();
  		
  		if (tokenCount > 2)
  		{
  			//set the correct enquiry name
  			enqname = sEnquiryName.substring(0,sEnquiryName.indexOf(" "));
  			
  			//Set the arguments by getting all values after the enquiry name
  			int argumentStartPos = sEnquiryName.indexOf(" ");
  			this.routineArgs = sEnquiryName.substring(argumentStartPos+1,sEnquiryName.length());
  		}
	}
  
	/**
	 * check to see if the last section is equal to F3. If
	 * it is then return true
	 * 
	 * @param command the command
	 * 
	 * @return true, if f3 check
	 */	
	private boolean F3Check(String command){		
		boolean found = false;
		int index = command.lastIndexOf(" ");
		String operation = command.substring(index+1,command.length());
	
		if (operation.equals("F3")){
			found = true;
		}
		else{
			found = false;
		}
	
		return found;
	}
		
	
	/**
	 * Builds up the list of all the parameters to be returned, in a Hashtable.
	 */
	public void generateParameterList(){
		
		parameters.put("application",application);
		parameters.put("function",function);
		parameters.put("key",key);
		parameters.put("version",version);
		parameters.put("ofsFunction",ofsFunction);
		parameters.put("ofsOperation",ofsOperation);
		parameters.put("transactionId",transactionId);
		parameters.put("routineName",routineName);
		parameters.put("routineArgs",routineArgs);
		parameters.put("requestType",requestType);
		
		parameters.put("enqaction",enqaction);
		parameters.put("enqname",enqname);
	}	
	
	
	/**
	 * returns the requestType for the command.
	 * 
	 * @return the request type
	 */
	public String getRequestType(){
		return requestType;
	}	
	
	/**
	 * returns the parameters collected for this command.
	 * 
	 * @return the parameters
	 */
	public Hashtable getParameters(){
		return parameters;
	}
	

	
  
}