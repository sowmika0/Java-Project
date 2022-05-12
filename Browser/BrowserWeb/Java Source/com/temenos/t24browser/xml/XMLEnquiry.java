package com.temenos.t24browser.xml;

/*
 *                      XMLEnquiry.java
 *Extends XMLTemplate.  Processes an enquiry request, passed in via an HTTP request. Over rides four methods.
 *NOTE: If a data node is passed in via the request and it does not have a value, then the id of that node is stored, and
 *that node, as well as any realted nodes, is not added to the xml doc.  
 *
 *
 * Modifications:
 * 
 * 08/07/03 - Added a new parameter to the constructor call of the Super class; now passes in
 * 				in the xml template as a string
 * 26/09/07 - New behaviour for enhanced enquiry layout, incorporating separate sort field selection
 *          - Ideas for refactoring:
 *            - Use Maps for params, or some quicker way of pruning and otherwise modifying the list before writing the XML
 *            - Maybe not do all the main work in the constructor? This is non-intuitive and could cause errors
 *              e.g. class is not initialised by the time all the work happens, only after super(...) has finished
 *            - Factor out repetitive units to worker methods
 *            - Remove stuff that is not used
 *            - Use newer / better Java features e.g. String.split
 *            - This class only handles one type of multi-node: "criteria", and multi/sub value indexes appear unused - optimize?
 */

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.w3c.dom.Node;

import com.temenos.t24browser.request.T24Request;


// TODO: Auto-generated Javadoc
/**
 * The Class XMLEnquiry.
 */
public class XMLEnquiry extends XMLTemplate {

    // WARNING: member initialisation cannot be used at present - all work flow is handled by super(...)

    /** The list. */
    protected List list;              //the entire list of params extracted from the request
    
    /** The next element. */
    private String nextElement;       //the next element in the list                          
    
    /** The new param name. */
    private String newParamName;      //the actual name of the param, as when extracted from request
    
    /** The id. */
    private int id;                   //the id of the param
    
    /** The max. */
    private int max = 0;              //set in setHigestParamId(). Signifies the higest id amongst the stored params                             
    
    /** The ids to remove. */
    private ArrayList idsToRemove;    //the vector to store the ids that need to be removed
    
    /** The stored params. */
    private ArrayList storedParams;   //the param list - correlate with the storedNodes   				
    
    /** The stored nodes. */
    private ArrayList storedNodes;    //the selection criteria nodes (multiples) that will be used to determine
                                      //which params are valid (have a value, or rely on another node having a value) - enquiry
    /** The sort fields. */
                                      private ArrayList sortFields;     // list of temporary sort fields from separate sorting options in enquiry selection
	
    /** The EN q_ FIEL d_ GROU p_ NODE. */
    private static String ENQ_FIELD_GROUP_NODE = "criteria";
    
    /** The EN q_ FIEL d_ INDE x_ DELIMETER. */
    private static String ENQ_FIELD_INDEX_DELIMETER = ":";
    
    /** The EN q_ REQUES t_ XM l_ FILE. */
    private static String ENQ_REQUEST_XML_FILE = "ofsmlEnquiryRequest.xml";
    
    /** The EN q_ NE w_ SOR t_ FIEL d_ NAME. */
    private static String ENQ_NEW_SORT_FIELD_NAME = "tempSortField";


    //pass the request and the name of the xml template to be used to the super class
    /**
     * Instantiates a new XML enquiry.
     * 
     * @param request the request
     * @param templates the templates
     */
    public XMLEnquiry(T24Request request, XMLTemplateManager templates){
        super(request, (String)templates.getProperty(ENQ_REQUEST_XML_FILE));
    }

    /* (non-Javadoc)
     * @see com.temenos.t24browser.xml.XMLTemplate#processParameter(java.lang.String, com.temenos.t24browser.request.T24Request)
     * 
     * Extracts each part of a field parameter, which should consist of four parts:
     *     the name, the instance, the multivalue number, and the sub value number
     */
    protected void processParameter(String newParamName,T24Request request){

        StringTokenizer tokens = new StringTokenizer(newParamName, ENQ_FIELD_INDEX_DELIMETER);

        try {
          	if (getType().equals(ENQ_FIELD_GROUP_NODE)){
              	setNewName((String)tokens.nextElement());
              	//fieldId is not used at the present moment
              	int fieldId = (Integer.parseInt((String)tokens.nextElement())-1);
              	setMultiValue((String)tokens.nextElement());
              	setSubValue((String)tokens.nextElement());
          	}
        }
        catch (Exception e){
		  	 System.out.println("Error with processing parameter: " + newParamName);
		  	 e.printStackTrace();
        }   	
    }



    /* (non-Javadoc)
     * @see com.temenos.t24browser.xml.XMLTemplate#insertData(com.temenos.t24browser.request.T24Request, java.lang.String, java.lang.String, java.util.ArrayList)
     * 
     * Gets the parent node from the parentsParentNode passed into the method.
     * Retrieves all of the child nodesfrom the parent node (done in super),
     * then runs through these nodes to see if any of them match up to the parameter being processed.
     * When found, the paramter value is then entered into the node.
     */
    protected void insertData(T24Request request,String newParamName,String paramName, ArrayList childList){
    
        int numberOfChildren = childList.size();
        for (int i=0;i<numberOfChildren;i++){
            Node node = (Node)childList.get(i);
            if(node.getNodeType()==Node.ELEMENT_NODE){
                if (node.getNodeName().equals(paramName)){
                    checkSetNodeValue(node,request.getParameter(newParamName));
    			}
    		}
    	}
    }
    
    /* (non-Javadoc)
     * @see com.temenos.t24browser.xml.XMLTemplate#processMultipleNode(com.temenos.t24browser.request.T24Request, java.util.ArrayList, java.lang.String)
     * 
     * When the node is designated to be a multiple node then this method will be caled to process it.
     * Runs through a list of nodes and processes them agaist the parms stored in the requestList.
     * The type indicates the grandparent nodes that the code is currently dealing with.
     */
    public void processMultipleNode(T24Request request, ArrayList nodes, String type) {

        setFieldInstance(0);
        setType(type);
    
        if(type.equals(ENQ_FIELD_GROUP_NODE)){
            storedNodes = nodes;
            storedNodes.trimToSize();
            addDataNodes(request);
        }
        else {
            //process standard multiple node
            super.processMultipleNode(request,nodes,type);
        }
    }

    /**
     * Deals with the processing and addition of data nodes to the xml doc (tricky).
     * 
     * @param request the request
     */
    protected void addDataNodes(T24Request request){

        setParameterList(request);               

        // Retrieve separate sorting values - later addition to enquiry screen
        getTempSortValues(request);
    
        setValidParameterList();
        setHighestParamId();
        setIdsToBeRemovedList(request);
        removeValidIdsFromRemoveList(request);
        removeInvalidParamsFromList();         	   
    
        // Apply separate sorting values - later addition to enquiry screen
        // WARNING: Relies on the params being in the right order e.g. fieldName first
        applyNewSortOrder(request);
    
        processValidParams(request); 	  
    }	

    /**
     * Create a list of all the parameters in the request.
     * 
     * @param request the request
     */
    private void setParameterList(T24Request request) {
        // initialize 'list' with parameters from the request, sorted in alphabetical order
        list = request.getSortedParameterList();
    }              

    /**
     * Extracts unique, non-empty sort parameters from the new separate temporary sort fields.
     * 
     * @param request the request
     */
    private void getTempSortValues(T24Request request) {
    
        // initialise here, as member initialisation at the top will not work, seeing as all this is called
        // from the super-class constructor!
        sortFields = new ArrayList();
    
        // Search for new temporary sort field names, add only valid (non-empty, non-dup)
        // enq fields from request and break when we run out
        for (int idx = 1; ; idx++) {
            String sortParam = ENQ_NEW_SORT_FIELD_NAME + ENQ_FIELD_INDEX_DELIMETER + idx;
         
            if (!list.contains(sortParam)) {
                break;
            }
    
            String enqFieldName = request.getParameter(sortParam);
    
            if ( (!enqFieldName.equals("")) && (!sortFields.contains(enqFieldName)) ) {
                sortFields.add(enqFieldName);
            }
        }
    }

    /**
     * Retrieve the parameters from the list whose names start with the node names stored in storedNodes.
     */   
    private void setValidParameterList(){    

        storedParams = new ArrayList();

        //run through each of the stored nodes          
        for (int i=0;i<storedNodes.size();i++) {
      	    //run through each of the request parameters in the list
            for(int j=0;j<list.size();j++){
                nextElement = (String)list.get(j);
                //if the paramter name begins with the node name then add it to the storedParameters
                if (nextElement.startsWith(((Node)storedNodes.get(i)).getNodeName())){
                    newParamName = nextElement;
                    storedParams.add(newParamName);
                }
            }       
        }

        storedParams.trimToSize();
     }

    /*
     * find out the highest id in the parameter list (indicative of the number of node sets we are dealing with 
     * None of the nodes have been removed yet(At least none of the ones we are interested in)
     */  
    /**
     * Sets the highest param id.
     */
    private void setHighestParamId(){
    
         for (int i=0;i<storedParams.size();i++){
             StringTokenizer tokens = new StringTokenizer((String)storedParams.get(i), ENQ_FIELD_INDEX_DELIMETER);
             //the name is not required at this point 
             String name = (String)tokens.nextElement();
             //the id is
             try {
            	 id = Integer.parseInt((String)tokens.nextElement());
             } catch (NumberFormatException e) {
            	 continue;
             }
             
             if(id>max){
                 max = id;
             }
         }       
    }

    /**
     * Calculate the id's that need to be discarded (A group of sibling nodes will have the same id).
     * 
     * @param request the request
     */
    private void setIdsToBeRemovedList(T24Request request){               

        idsToRemove = new ArrayList();
    	  
        //run through each of the stored params to get at the id
        for (int j=0;j<storedParams.size();j++){ 
            StringTokenizer tokens = new StringTokenizer((String)storedParams.get(j), ENQ_FIELD_INDEX_DELIMETER);
            //don't need the name, we only extract it to get at the next token (id)
            String name = (String)tokens.nextElement();
            try {
            	id = Integer.parseInt((String)tokens.nextElement());
            } catch (NumberFormatException e) {
            	continue;
            }
            //if the stored parameter does not have a value then add it's id to the list of ids to be discarded
            if (request.getParameter((String)storedParams.get(j)).equals("")){
                if(!(idsToRemove.contains(id+""))){
                    idsToRemove.add(id+"");
                }
            }
        }

        idsToRemove.trimToSize();
    }

    /**
     * This method runs through the ID's to be removed List and checks if a sort cirteria has been set.
     * If so then this ID must be removed from the 'idsToRemove' as we need to send this to T24
     * 
     * @param request the request
     */
    private void removeValidIdsFromRemoveList(T24Request request) {
        //run through each of the stored params to get at the id
        for (int j=0; j<storedParams.size(); j++) {
            StringTokenizer tokens = new StringTokenizer((String)storedParams.get(j), ENQ_FIELD_INDEX_DELIMETER);
            //don't need the name, we only extract it to get at the next token (id)
            String name = (String)tokens.nextElement();
            try {
            	id = Integer.parseInt((String)tokens.nextElement());
            }catch(NumberFormatException e){
            	continue;
            }

            if (name.equals("sort")) {
                // check if a sort has been applied
                if (!request.getParameter((String)storedParams.get(j)).equals("none")) {
                    // sort DOES NOT equal 'none' so it has been specified by the user
                    // so we need to remove this ID from the remove list...
                    for(int i=0; i<idsToRemove.size(); i++) {
                        //if the id of the stored param equals that of the id to be removed then
                        //remove that stored param from the tempParamList
                        if (id == Integer.parseInt(((String)idsToRemove.get(i)))) {
                            idsToRemove.remove(i);
                            break;
                        }
                    }
                }
            }
        } 		 	
    }

    /**
     * remove the params from the storedParams list that have an id that is to be removed.
     */  
    private void removeInvalidParamsFromList(){                         

      //clone the paramList - need to do this to get around refference problem
      ArrayList tempParamList = (ArrayList)storedParams.clone();
      tempParamList.trimToSize();

      int paramSize = storedParams.size();

      //run through each of the stored params
      for (int i=0; i<paramSize; i++) {
          String nextParam = (String)storedParams.get(i);
          StringTokenizer tokens = new StringTokenizer(nextParam, ENQ_FIELD_INDEX_DELIMETER);
          String name = (String)tokens.nextElement();
          //get the id of the param
          try {
        	  id = Integer.parseInt((String)tokens.nextElement());
          }catch( NumberFormatException e){
        	  continue;
          }
          //run through each the list of id's that are to be removed 
          for(int j=0;j<idsToRemove.size();j++){
              //if the id of the stored param equals that of the id to be removed then 
              //remove that stored param from the tempParamList
              if(id == Integer.parseInt(((String)idsToRemove.get(j)))){
                  if (tempParamList.contains(nextParam)){
                      tempParamList.remove(nextParam);
                      break;
                  }
              }
          }
      }   
      //replace the list with the valid params
      storedParams = tempParamList;
      storedParams.trimToSize();
    }
 		  	            
    /**
     * if new (separate) sorting box entries are present ...
     * .. re-order the param list according to order of the selected sort fields
     * .. relies on sort value (A/D) substituted by Javascript into hidden old-style sort param at submit
     * 
     * @param request the request
     */
    private void applyNewSortOrder(T24Request request) {

        //  
        // Big loops to check and adjust the parameter list - only way to do it now without refactoring
        //
        // Loop 1: For each (temp sort field)
        //  Loop 2: For each node/field type (e.g. fieldName, operand ..)
        //   Loop 3: For each param in (sorted) request param list
        // 

        int firstFieldCounter, firstFieldIndex, secondFieldIndex, sortFieldIndex, thisFieldIndex;
        int firstParamIndex, secondParamIndex, thisParamIndex;
        boolean alreadySorted;
             
        // Declare separate list to iterate over (to avoid conc. mod. ex.)
        List tempParams;

        // For the X sort fields, we need to put the appropriate selection fields into the correct sort order
        // by swapping any out-of-order fields with the corresponding field in position X in the param list
        // Note: we have to swap all fields in the 'set' for each index e.g. fieldName:N:1:1, operand:N:1:1, etc.
        //       due to the beahviour of the XML data insertion methods later .. 
        //       The main param (fieldName) yields the field indexes to be swapped for all params in field 'set'
        //
        // e.g. Enq sort by Name (field index 6), then Account Num (field index 3)
        //      Initial List: [ ..., fieldName:3:1:1, fieldName:6:1:1, ..., operand:3:1:1, operand:6:1:1, ... ]
        //        Final List: [ ..., fieldName:6:1:1, fieldName:3:1:1, ..., operand:6:1:1, operand:3:1:1, ... ]
        //
        for (ListIterator fieldIter = sortFields.listIterator(); fieldIter.hasNext(); ) {
            String enqSortField = (String) fieldIter.next();  // corresponds to user-facing enquiry field name
            tempParams = new ArrayList(storedParams); // reset for each sort field, for valid field searches
            
            // used to stop at the correct first swap field when iterating through fieldName params
            sortFieldIndex = fieldIter.previousIndex();
            firstFieldCounter = 0;

            // 1st and 2nd embedded field indexes of params to swap,
            // determined by sort position (1st), and checking fieldName values against sort field value (2nd)
            firstFieldIndex = -1;
            secondFieldIndex = -1;

            // short circuit test flag, in case a particular field 'set' is already in the correct position
            alreadySorted = false;
                 
            for (ListIterator nodeIter = storedNodes.listIterator(); (! alreadySorted) && nodeIter.hasNext(); ) {
                String nodeType = ((Node)nodeIter.next()).getNodeName(); // == field prefix
                firstParamIndex = -1;
                secondParamIndex = -1;

                for (ListIterator paramIter = tempParams.listIterator(); paramIter.hasNext(); ) {
                    String paramName = (String) paramIter.next();

                    // Get embedded field index, and check it is the right param prefix too
                    thisFieldIndex = getParamFieldIndex(paramName, nodeType);
                    if (thisFieldIndex < 0) {
                        continue;  // param does not match node type
                    }

                    // now in the right field range .. get list position for current param
                    thisParamIndex = paramIter.previousIndex();

                    // List sorted, so get the index of the 1st, 2nd .. placed main field
                    // (depending on current sort field index)
                    // This sets the sort order, as set by the user in the sort box
                    if (firstFieldIndex == -1) {
                        if (firstFieldCounter++ < sortFieldIndex) {
                            continue;  // waiting for the right field to swap with sort field
                        }
                        firstFieldIndex = thisFieldIndex;
                        firstParamIndex = thisParamIndex;
                    }

                    // cycle round until we get the index of the main field
                    // whose value matches the current sort field
                    if (secondFieldIndex == -1) {
                        if (! request.getParameter(paramName).equals(enqSortField)) {
                            continue;
                        }
                        // if still on first field => must be already in correct position
                        if (thisFieldIndex == firstFieldIndex) {
                            alreadySorted = true;
                            break;  // secondParamIndex not set so no swap
                        }
                        else {
                            // got second field - ready to swap main params
                            secondFieldIndex = thisFieldIndex;
                            secondParamIndex = thisParamIndex;
                            break;
                        }
                    }

                    // Got both field indexes now, and main fields swapped
                    // Swap associated params in 'set' ..
                    // Check if we have hit the second param (determined by field index)
                    if (thisFieldIndex == secondFieldIndex) {
                        secondParamIndex = thisParamIndex;
                        break;                                    
                    }

                    // Check for first param to swap (again, determined by field index)
                    if (thisFieldIndex == firstFieldIndex) {
                        firstParamIndex = thisParamIndex;
                    }
                }

                // Swap fields in the main parameter list, if we have both indexes
                if ((firstParamIndex > -1) && (secondParamIndex > -1)) {
                    swapListItems(storedParams, firstParamIndex, secondParamIndex);
                }
            }
        }
    }

    /**
     * Get a field index embedded in a parameter name, such as that posted in an enquiry selection - fieldName:1:1:1.
     * 
     * @param param full name of request parameter
     * @param paramPrefix parameter value before first ':'
     * 
     * @return index value embedded in param name, or -1 if not found
     */
    private int getParamFieldIndex(String param, String paramPrefix) {
        int retval = -1;
        try {
            String[] tokens = param.split(ENQ_FIELD_INDEX_DELIMETER);
            if ( (paramPrefix == null) || (tokens[0].equals(paramPrefix)) ) {
                retval = Integer.parseInt(tokens[1]);
            }
        }
        catch (Exception e) {
            // normally very bad practice, but this degrades simply to returning invalid index: -1    
        }
        return retval;
    }

    /**
     * Simple list object swap - no bounds checking - could throw ArrayIndexOutOfBounds.
     * 
     * @param target list to modify, care needed if calling this in loop over list
     * @param idx1 first element index
     * @param idx2 second element index
     */
    private void swapListItems(List target, int idx1, int idx2) {
        String temp = (String) target.get(idx1);
        target.set(idx1, target.get(idx2));
        target.set(idx2, temp);
    }

    /**
     * Add the stored params that are left in the storedParams list to the xml document.
     * 
     * @param request the request
     */
    private void processValidParams(T24Request request) {

        //run through each of the nodes that we are looking for        
        for(int j=0; j<storedNodes.size(); j++) {
            // run through each of the storedParams

            // Reset the field instance for each node type
            // As the nodes list of params has been sorted then in theory
            // each of the params should follow in the same order.
            setFieldInstance(0);  

            for(int i=0; i<storedParams.size(); i++) {

                nextElement = (String)storedParams.get(i);

                //if the storedParam name starts with the name of the node
                if (nextElement.startsWith(((Node)storedNodes.get(j)).getNodeName())) {
                    newParamName = nextElement;

                    processField((Node)storedNodes.get(j), ((Node)storedNodes.get(j)).getNodeName(),newParamName,request);
                    setFieldInstance(getFieldInstance()+1);
                }
            }
        }
    }

}