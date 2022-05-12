////////////////////////////////////////////////////////////////////////////////
//
//  Class         :   XmlBulkResponse
//
//  Description   :   Class that deals with XML messages containing bulk 
//					  XML responses.
//					  This class takes multiple responses and merges them
//					  in to a single response for display in Browser.
//					  
//					  The input XML document will contain multiple <response> tags
//					  in the form :-
//							<responses><response><.......></response></response>
//
//					  The output XML document will put each response tag in to a
//					  <pane> tag within a standard Browser response.
//					  The duplicate window tags (e.g. window coordinates, 
//					  translations, etc) will be removed .
//					  The merged output XML document will be in the form :-
//							<ofsSessionResponse><...><pane></pane><pane></pane><...></ofsSessionResponse>
//
//  Modifications :
//
//    14/02/07   -    Initial Version.
//
////////////////////////////////////////////////////////////////////////////////

package com.temenos.t24browser.xml;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.temenos.t24.commons.logging.Logger;
import com.temenos.t24.commons.logging.LoggerFactory;
import com.temenos.t24browser.request.RequestUtils;
import com.temenos.t24browser.utils.FileManager;
import com.temenos.t24browser.utils.Utils;


// TODO: Auto-generated Javadoc
/**
 * The Class XMLBulkResponse.
 */
public class XMLBulkResponse extends XMLBulk
{
	
	/** The iv resp header xml. */
	private String ivRespHeaderXml = "";
	
	/** The iv panes. */
	private List ivPanes = new LinkedList();
	
	/** The iv errors. */
	private List ivErrors = new LinkedList();
	
	/** The iv overrides. */
	private List ivOverrides = new LinkedList();
	
	/** The iv override text. */
	private List ivOverrideText = new LinkedList();
	
	/** To Handle Duplicate errors */
	private List ivErrorText = new LinkedList();	
	
	/** The iv warnings. */
	private List ivWarnings = new LinkedList();
	
	/** The iv messages. */
	private List ivMessages = new LinkedList();
	
	/** The iv override accept action. */
	private String ivOverrideAcceptAction = "";
	
	/** The iv override unapproved action. */
	private String ivOverrideUnApproved = "";		// Used for override approvals
		
	/** The Constant COMPONENT_NAME. */
	private static final String COMPONENT_NAME = "XMLBulkResponse : ";	// Used for logging
	
	/** The Constant MAIN_PANE_TITLE. */
	private static final String MAIN_PANE_TITLE = "appreq";				// The form name of the top pane
	
	/** The Constant XML_RESPONSE. */
	private static final String XML_RESPONSE = "XML";			// XML Type of T24 response
	
	/** The Constant OFS_RESPONSE. */
	private static final String OFS_RESPONSE = "OFS";			// OFS Type of T24 response

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLBulkResponse.class);
	
		
	/**
	 * Instantiates a new XML bulk response.
	 * 
	 * @param sXmlResponse the s xml response
	 */
	public XMLBulkResponse( String sXmlResponse )
	{
		// If the XML document has multiple responses, then merge them
		// in to one XML document
		if ( RequestUtils.bulkResponse( sXmlResponse ) )
		{
			ivXml = mergeResponses( sXmlResponse );
			LOGGER.debug("Merged XML Response: " + ivXml);
		}
		else
		{
			ivXml = sXmlResponse;
		}
	}
	
		
	/**
	 * Returns the merged responses in a single XML document.
	 * 
	 * @param sResponse The bulk response in XML format
	 * 
	 * @return String
	 */
	private String mergeResponses( String sResponse )
	{
		String sMergedXml = "";

		// Get the header information from the first response as we need
		// this for the header of the merged result document.
		// Match with a pattern of "<response>...</response>" using 'reluctant' qualifier ('?')
		Pattern respPattern = Pattern.compile( "<response>(.*?)</response>", Pattern.DOTALL );
		Matcher respMatcher = respPattern.matcher( sResponse );

		// Loop round each response extracting the <pane> tag within each <response> tag.
		// If a response is in OFS format then extract any errors and overrides and add them to the main application.
		// Ensure that the window events (e.g. onLoad), and window header information are moved
		// in to the pane node as we will want each pane to have it's own details.
		// Move all the overrides, warnings, errors, and messages to the first (main) pane.
		
		// If we have overrides then get the accept action text for the "Accept Overrides" button 
		// as we need it later when we add overrides to the main pane
		ivOverrideAcceptAction = Utils.getNodeFromString( sResponse, XMLConstants.XML_ACCEPT_ACTION_TAG );
		ivOverrideUnApproved = Utils.getNodeFromString( sResponse, XMLConstants.XML_OVERRIDE_UNAPPROVED_TAG );
		
		boolean moreResponses = false;
		int respNo = 0;
		
		while ( moreResponses =  respMatcher.find() )
		{
			respNo++;
			String response = respMatcher.group(1);
			String responseType = getResponseType ( response );
			
			if ( responseType.equals( XML_RESPONSE ) )
			{
				// Browser XML response
				String sPaneXml = getPaneXml( respNo, response );
				String sTitle = Utils.getNodeFromString( sPaneXml, XMLConstants.XML_TITLE_TAG );
							
				if ( respNo == 1 )
				{
					// This is the main pane, so get the header information (e.g. window coordinates, etc)
					ivRespHeaderXml = response.split( XMLConstants.XML_PANES_TAGGED )[0];
					ivRespHeaderXml += XMLConstants.XML_PANES_TAGGED;
					sTitle = MAIN_PANE_TITLE;
				}
	
				sPaneXml = processErrors( sPaneXml, respNo, sTitle );
				sPaneXml = processOverrides( sPaneXml, respNo );
				sPaneXml = processWarnings( sPaneXml, respNo );
				sPaneXml = processMessages( sPaneXml, respNo );
				
				ivPanes.add( sPaneXml );
			}
			else
			{
				// OFS string format - process the errors and overrides, but don't display the OFS response
				processOfsResponse( response );				
			}
		}
		
		// Build the merged response
		sMergedXml = buildResponse();
		
		return( sMergedXml );
	}

	
	// Returns the type of response - Browser XML or OFS
	/**
	 * Gets the response type.
	 * 
	 * @param response the response
	 * 
	 * @return the response type
	 */
	private String getResponseType( String response )
	{
		if ( response.startsWith("<") && !response.startsWith( XMLConstants.XML_OFS_TAGGED ) )
		{
			return( XML_RESPONSE );
		}
		else
		{
			return( OFS_RESPONSE );
		}
	}
	
	// Returns a tagged pane
	/**
	 * Gets the pane xml.
	 * 
	 * @param paneNo the pane no
	 * @param response the response
	 * 
	 * @return the pane xml
	 */
	private String getPaneXml( int paneNo, String response )
	{
		String sPaneXml = "";
		String pane = Utils.getNodeFromString( response, XMLConstants.XML_PANE_TAG );			
		String sSubPane = "";

		// Add the header, by getting all the tags up to the <panes> tag
		if ( paneNo > 1 )
		{
			// This is a sub-pane, remove the toolbars from the pane
			sSubPane = XMLConstants.XML_SUB_PANE_TAGGED + "Y" + XMLConstants.XML_SUB_PANE_TAGGED_C;
			pane = Utils.removeNodeFromString( pane, XMLConstants.XML_TOOLBARS_TAG );
		}

		sPaneXml += XMLConstants.XML_PANE_TAGGED;
		sPaneXml += sSubPane;
		sPaneXml += getPaneHeaderXml( response );
		sPaneXml += pane;
		sPaneXml += XMLConstants.XML_PANE_TAGGED_C;
		
		return( sPaneXml );		
	}
	
	
	// Returns the tagged footer information for a bulk response - i.e. closing tags
	/**
	 * Gets the footer xml.
	 * 
	 * @return the footer xml
	 */
	private String getFooterXml()
	{
		// Get the footer information for this response - the closing tags
		String sFooterXml = "";
		sFooterXml += XMLConstants.XML_PANES_TAGGED_C;
		sFooterXml += XMLConstants.XML_WINDOW_TAGGED_C;
		sFooterXml += XMLConstants.XML_RESPONSE_DETAILS_TAGGED_C;
		sFooterXml += XMLConstants.XML_RESPONSE_DATA_TAGGED_C;
		sFooterXml += XMLConstants.XML_OFS_SESSION_RESPONSE_TAGGED_C;
		
		return( sFooterXml );
	}
	
	
	// Processes any errors in a pane 
	/**
	 * Process errors.
	 * 
	 * @param sPane the s pane
	 * @param paneNo the pane no
	 * @param sTitle the s title
	 * 
	 * @return the string
	 */
	private String processErrors( String sPane, int paneNo, String sTitle )
	{
		// Extract all of the errors in the pane so that they can be displayed
		// against the main pane only.
		// Change the hyperlink to the error so that it sets the current form
		// form that the error is actually on.
		String sErrors = Utils.getNodeFromString( sPane, XMLConstants.XML_ERRORS_TAG );
		String sApp = Utils.getNodeFromString( sPane, XMLConstants.XML_APPLICATION_TAG );
		String sVersion = Utils.getNodeFromString( sPane, XMLConstants.XML_VERSION_TAG );
		String sKey = Utils.getNodeFromString( sPane, XMLConstants.XML_KEY_TAG );
		
		String sPaneForm = "";
		if ( paneNo == 1 )
		{
			sPaneForm = "appreq";
		}
		else
		{
			sPaneForm = sApp + sVersion + sKey;
			String sErrorApp = XMLConstants.XML_ERROR_TAGGED + XMLConstants.XML_ERROR_APP_TAGGED + sTitle + XMLConstants.XML_ERROR_APP_TAGGED_C;
			sErrors = Utils.replaceAll( sErrors, XMLConstants.XML_ERROR_TAGGED, sErrorApp );
		}
		
		String sOldErrorLink = "<errorhref>javascript:linkError";
		String sNewErrorLink = "<errorhref>javascript:setCurrentForm('" + sPaneForm + "');linkError";
		sErrors = Utils.replaceAll( sErrors, sOldErrorLink, sNewErrorLink );
		
		// Add each error to the errors list, ensuring there are no duplicates
		addNodesToList( ivErrors, sErrors, XMLConstants.XML_ERROR_TAG, false );
		
		// Remove the errors from the pane
		sPane = Utils.removeNodeFromString( sPane, XMLConstants.XML_ERRORS_TAG );
		
		return( sPane );
	}
	
	// Processes any overrides in a pane 
	/**
	 * Process overrides.
	 * 
	 * @param sPane the s pane
	 * @param paneNo the pane no
	 * 
	 * @return the string
	 */
	private String processOverrides( String sPane, int paneNo )
	{
		// Extract all of the overrides in the pane so that they can be displayed
		// against the main pane.
		String sOverrides = Utils.getNodeFromString( sPane, XMLConstants.XML_OVERRIDES_TAG );
		
		// Add each error to the errors list, ensuring there are no duplicates
		addNodesToList( ivOverrides, sOverrides, XMLConstants.XML_OVERRIDE_TAG, false );
		
		// Remove the overrides from the pane as we are going to add the full set at the end
		sPane = removeOverridesMainTopic( sPane );
		
		return( sPane );
	}
	
	// Processes any warnings in a pane 
	/**
	 * Process warnings.
	 * 
	 * @param sPane the s pane
	 * @param paneNo the pane no
	 * 
	 * @return the string
	 */
	private String processWarnings( String sPane, int paneNo )
	{
		// Extract all of the warnings in the pane so that they can be displayed
		// against the main pane.
		String sWarnings = Utils.getNodeFromString( sPane, XMLConstants.XML_WARNINGS_TAG );
		
		// Add each warning to the warnings list, ensuring there are no duplicates
		addNodesToList( ivWarnings, sWarnings, XMLConstants.XML_WARNING_TAG, false );
		
		// Remove the warnings from the pane as we are going to add the full set at the end
		sPane = removeWarningsMainTopic( sPane );
		
		return( sPane );
	}
	
	// Processes any messages in a pane 
	/**
	 * Process messages.
	 * 
	 * @param sPane the s pane
	 * @param paneNo the pane no
	 * 
	 * @return the string
	 */
	private String processMessages( String sPane, int paneNo )
	{
		// Extract all of the messages in the pane so that they can be displayed
		// against the main pane only.
		String sMessages = Utils.getNodeFromString( sPane, XMLConstants.XML_BROWSER_MESSAGES_TAG );
		
		// Add each error to the errors list, ensuring there are no duplicates
		addNodesToList( ivMessages, sMessages, XMLConstants.XML_BROWSER_MESSAGE_TAG, false );
		
		// Remove the messages from the pane
		sPane = Utils.removeNodeFromString( sPane, XMLConstants.XML_BROWSER_MESSAGES_TAG );
		
		return( sPane );
	}
	
	/**
	 * Process ofs response.
	 * 
	 * @param response the response
	 */
	private void processOfsResponse( String response )
	{
		processOfsErrors( response );
		processOverrides( response, -1 );
		processWarnings( response, -1 );	
	}
	
	// Process any errors in the OFS response to display in the main pane
	/**
	 * Process ofs errors.
	 * 
	 * @param ofsResponse the ofs response
	 */
	private void processOfsErrors( String ofsResponse )
	{
		// Add the ofs error tags
		String errors = ofsResponse;
		errors = Utils.replaceAll( errors, XMLConstants.XML_ERROR_TAGGED, XMLConstants.XML_ERROR_TAGGED + XMLConstants.XML_OFS_ERROR_TAGGED + XMLConstants.XML_OFS_ERROR_TAGGED_C );
		processErrors( errors, -1, "" );
	}
	
	// Split a XML document in to nodes and add each one to a list
	/**
	 * Adds the nodes to list.
	 * 
	 * @param list the list
	 * @param nodes the nodes
	 * @param nodeName the node name
	 * @param allowDuplicates the allow duplicates
	 */
	private void addNodesToList( List list, String nodes, String nodeName, boolean allowDuplicates )
	{
		if ( ( nodes != null ) && ( ! nodes.equals("") ) )
		{
			Pattern nodePattern = Pattern.compile( "<" + nodeName + ">(.*?)</" + nodeName + ">", Pattern.DOTALL );
			Matcher nodeMatcher = nodePattern.matcher( nodes );
			
			// Loop round each response extracting the <pane> tag within each <response> tag.
			// Ensure that the window events (e.g. onLoad), and window header information are moved
			// in to the pane node as we will want each pane to have it's own details.
			// Move all the overrides, warnings, errors, and messages to the first (main) pane.
			
			boolean moreNodes = false;
			
			while ( moreNodes =  nodeMatcher.find() )
			{
				String node = nodeMatcher.group();
				if (nodeName.equals(XMLConstants.XML_OVERRIDE_TAG))
				{
					Pattern nodePatternOvrText = Pattern.compile(XMLConstants.XML_OVERRIDE_TEXT_TAGGED + "(.*?)" + XMLConstants.XML_OVERRIDE_TEXT_TAGGED_C, Pattern.DOTALL );
					Matcher nodeMatcherOvrText = nodePatternOvrText.matcher(node);
					while (nodeMatcherOvrText.find())
					{	
						String nodeOvrText = nodeMatcherOvrText.group();
						if ( ! ivOverrideText.contains( nodeOvrText ) )
						{
						ivOverrideText.add(nodeOvrText);
						addStringToList( list, node, allowDuplicates );
						}
					}
				}else if (nodeName.equals(XMLConstants.XML_ERROR_TAG))
				{
					Pattern nodePatternFieldNameText = Pattern.compile(XMLConstants.XML_FIELD_NAME_TAGGED + "(.*?)" + XMLConstants.XML_FIELD_NAME_TAGGED_C, Pattern.DOTALL );
					Matcher nodeMatcherFieldNameText = nodePatternFieldNameText.matcher(node);
					Pattern nodePatternErrText = Pattern.compile(XMLConstants.XML_ERROR_MESSAGE_TAGGED + "(.*?)" + XMLConstants.XML_ERROR_MESSAGE_TAGGED_C, Pattern.DOTALL );
					Matcher nodeMatcherErrText = nodePatternErrText.matcher(node);	
					//if <fn> tag contains value, add all the field errors 
					if(nodeMatcherFieldNameText.find() && nodeMatcherFieldNameText.group()!= null && !nodeMatcherFieldNameText.group().equals(""))
					{						
						allowDuplicates =true;
						while (nodeMatcherErrText.find())
						{	
							String nodeErrText = nodeMatcherErrText.group();
							ivErrorText.add(nodeErrText);
							addStringToList( list, node, allowDuplicates );							
						}			
					}					
					else
					{	
						//if <fn> tag does not contain values, check for duplicate error messages and then add the error
						while (nodeMatcherErrText.find())
						{	
							String nodeErrText = nodeMatcherErrText.group();
							if ( ! ivErrorText.contains( nodeErrText ) )
							{
								ivErrorText.add(nodeErrText);
								addStringToList( list, node, allowDuplicates );
							}
						}
					}	
				}else
				{
					addStringToList( list, node, allowDuplicates );
				}				
			}
		}
	}
	
	// Add a string to a list object at the end
	/**
	 * Adds the string to list.
	 * 
	 * @param list the list
	 * @param item the item
	 * @param allowDuplicates the allow duplicates
	 */
	private void addStringToList( List list, String item, boolean allowDuplicates )
	{
		boolean bAddItem = true;
		
		if ( allowDuplicates == false )
		{
			// Check if the item exists or not
			if ( ! list.contains( item ) )
			{
				list.add( item );
			}
		}
		else
		{
			list.add( item );
		}
	}
	
	// Build the response containing all of the panes, etc
	/**
	 * Builds the response.
	 * 
	 * @return the string
	 */
	private String buildResponse()
	{
		String sResponse = "";
		
		sResponse += ivRespHeaderXml;
		
		for ( int paneNo = 0; paneNo < ivPanes.size(); paneNo++ )
		{
			String pane = (String) ivPanes.get( paneNo );
			
			if ( paneNo == 0 )
			{
				// This is the main pane so add and errors, overrides, warnings and messages.
				// But if there are any errors, then do not display any overrides or warnings.
				String sTopics = "";
				sTopics += getErrorsMainTopic();
				
				if ( ivErrors.size() == 0 )
				{
					sTopics += getOverridesMainTopic();
					sTopics += getWarningsMainTopic();
				}
				
				sTopics += getMessagesMainTopic();
				
				// Insert the topics XML in to the document after the TopicList node
				pane = Utils.replaceAll( pane, XMLConstants.XML_TOPIC_LIST_TAGGED, XMLConstants.XML_TOPIC_LIST_TAGGED + sTopics );
			}
			// when group tag present group the similar group tags and place the contract tag in group tag inside
			// an grouppantag
            //	response should look like this	<pane><grouptag>value</grouptag><expanablepanetag><contract>value</contract></expanablepanetag><expanablepanetag><contract>value</contract></expanablepanetag></pane>
			String sGroup = Utils.getNodeFromString(pane,XMLConstants.XML_GROUP_TAG );
			
			if (sGroup != null && ! (sGroup.equals("")) )
			{
				int expandpos = 1;
				int matchfound = 0;
				Pattern groupPattern = Pattern.compile( "<groupTab>(.*?)</groupTab>", Pattern.DOTALL );
				Matcher groupMatcher = groupPattern.matcher( sResponse );
				boolean Group = false;
				int panecontentstartpos = pane.indexOf("<subPane>");
				int panecontentendpos = pane.indexOf("</pane>");
				String panecontent =  pane.substring(panecontentstartpos,panecontentendpos);
				panecontent = Utils.removeNodeFromString(panecontent, XMLConstants.XML_GROUP_TAG);
				while ( Group =  groupMatcher.find(expandpos) )
				{
													
					String rGroup = groupMatcher.group(1);
					expandpos = groupMatcher.end();
					if (rGroup.equals(sGroup))
					{
					   matchfound = 1;	
					   String beforeexpand = sResponse.substring(0,expandpos);
					   String afterexpand = sResponse.substring(expandpos,sResponse.length());
					   int grouppos = afterexpand.lastIndexOf(XMLConstants.XML_GROUP_PANE_TAGGED_C);//indexOf(XMLConstants.XML_GROUP_PANE_TAGGED_C) ;
					   String beforeinsert = afterexpand.substring(0,(grouppos+XMLConstants.XML_GROUP_PANE_TAGGED_C.length()));
					   String afterinsert = afterexpand.substring(grouppos+XMLConstants.XML_GROUP_PANE_TAGGED_C.length(),afterexpand.length());
					   sResponse = beforeexpand+beforeinsert+ XMLConstants.XML_GROUP_PANE_TAGGED +  panecontent + XMLConstants.XML_GROUP_PANE_TAGGED_C + afterinsert;
					}
		
				}	
				if (matchfound == 0)
				{
	                  	int headerendpos = pane.indexOf(XMLConstants.XML_GROUP_TAGGED_C);
	                  	String headercontent = pane.substring(panecontentstartpos,headerendpos+XMLConstants.XML_GROUP_TAGGED_C.length());
						pane = XMLConstants.XML_PANE_TAGGED+headercontent+XMLConstants.XML_GROUP_PANE_TAGGED;
						pane += panecontent + XMLConstants.XML_GROUP_PANE_TAGGED_C+XMLConstants.XML_PANE_TAGGED_C;
						sResponse += pane;
				}
													
			}
			else
			{
			sResponse += pane;
		}
		}
		
		sResponse += getFooterXml();
	
		return( sResponse );
	}
	
	// Removes the overrides main topic from a pane
	/**
	 * Removes the overrides main topic.
	 * 
	 * @param sPane the s pane
	 * 
	 * @return the string
	 */
	private String removeOverridesMainTopic( String sPane )
	{
		String sResult = sPane;
		
		// Find the <overrides> node and then remove the MainTopic node around it
		int overridesPos = sPane.indexOf( XMLConstants.XML_OVERRIDES_TAGGED );
		int mainTopicPos = sPane.indexOf( XMLConstants.XML_MAIN_TOPIC_TAGGED );
		
		if ( ( overridesPos != -1 ) && ( mainTopicPos  != -1 ) )
		{
			// Find the MainTopic preceding node position
			String beforeOverrides = sPane.substring( 0, overridesPos );
			int startTopicsPos = beforeOverrides.lastIndexOf( XMLConstants.XML_MAIN_TOPIC_TAGGED );
			int endTopicsPos = sPane.indexOf( XMLConstants.XML_MAIN_TOPIC_TAGGED_C, startTopicsPos );
				
			sResult = "";
			sResult += sPane.substring( 0, startTopicsPos );
			sResult += sPane.substring( endTopicsPos + (XMLConstants.XML_MAIN_TOPIC_TAGGED_C).length(), sPane.length() );
		}
		
		return( sResult );
	}
	
	// Removes the warnings main topic from a pane
	/**
	 * Removes the warnings main topic.
	 * 
	 * @param sPane the s pane
	 * 
	 * @return the string
	 */
	private String removeWarningsMainTopic( String sPane )
	{
		String sResult = sPane;
		
		// Find the <warnings> node and then remove the MainTopic node around it
		int warningsPos = sPane.indexOf( XMLConstants.XML_WARNINGS_TAGGED );
		int mainTopicPos = sPane.indexOf( XMLConstants.XML_MAIN_TOPIC_TAGGED );
		
		if ( ( warningsPos != -1 ) && ( mainTopicPos  != -1 ) )
		{
			// Find the MainTopic preceding node position
			String beforeWarnings = sPane.substring( 0, warningsPos );
			int startTopicsPos = beforeWarnings.lastIndexOf( XMLConstants.XML_MAIN_TOPIC_TAGGED );
			int endTopicsPos = sPane.indexOf( XMLConstants.XML_MAIN_TOPIC_TAGGED_C, startTopicsPos );
				
			sResult = "";
			sResult += sPane.substring( 0, startTopicsPos );
			sResult += sPane.substring( endTopicsPos + (XMLConstants.XML_MAIN_TOPIC_TAGGED_C).length(), sPane.length() );
		}
		
		return( sResult );
	}

	// Get a contract screen MainTopic XML tag for the errors 
	/**
	 * Gets the errors main topic.
	 * 
	 * @return the errors main topic
	 */
	private String getErrorsMainTopic()
	{
		return( getMainTopic( "Errors", "errors", "ERRORS", XMLConstants.XML_ERRORS_TAG, "", ivErrors ) );
	}
	
	// Get a contract screen MainTopic XML tag for the overrides 
	/**
	 * Gets the overrides main topic.
	 * 
	 * @return the overrides main topic
	 */
	private String getOverridesMainTopic()
	{
		String extraNodes = XMLConstants.XML_ACCEPT_ACTION_TAGGED + ivOverrideAcceptAction + XMLConstants.XML_ACCEPT_ACTION_TAGGED_C;
		extraNodes += XMLConstants.XML_OVERRIDE_UNAPPROVED_TAGGED + ivOverrideUnApproved + XMLConstants.XML_OVERRIDE_UNAPPROVED_TAGGED_C;
		return( getMainTopic( "Overrides", "", "ERRORS", XMLConstants.XML_OVERRIDES_TAG, extraNodes, ivOverrides ) );
	}
	
	// Get a contract screen MainTopic XML tag for the warnings 
	/**
	 * Gets the warnings main topic.
	 * 
	 * @return the warnings main topic
	 */
	private String getWarningsMainTopic()
	{
		return( getMainTopic( "Warnings", "warnings", "WARNINGS", XMLConstants.XML_WARNINGS_TAG, "", ivWarnings ) );
	}
	
	// Get a contract screen MainTopic XML tag for the messages 
	/**
	 * Gets the messages main topic.
	 * 
	 * @return the messages main topic
	 */
	private String getMessagesMainTopic()
	{
		return( getMainTopic( "Messages", "messages", "MESSAGES", XMLConstants.XML_BROWSER_MESSAGES_TAG, "", ivMessages ) );
	}
	
	// Get a contract screen MainTopic XML tag for the errors 
	/**
	 * Gets the main topic.
	 * 
	 * @param caption the caption
	 * @param id the id
	 * @param type the type
	 * @param mainTag the main tag
	 * @param mainExtraNodes the main extra nodes
	 * @param list the list
	 * 
	 * @return the main topic
	 */
	private String getMainTopic( String caption, String id, String type, String mainTag, String mainExtraNodes, List list )
	{
		String sTopic = "";
		
		if ( list.size() > 0 )
		{
			sTopic += XMLConstants.XML_MAIN_TOPIC_TAGGED + XMLConstants.XML_TOPICS_TAGGED;
			sTopic += XMLConstants.XML_ID_TAGGED + id + XMLConstants.XML_ID_TAGGED_C;
			sTopic += "<" + mainTag + ">";
			sTopic += mainExtraNodes;						// Add any extra nodes inside the main tag passed
			
			for ( int i = 0; i < list.size(); i++ )
			{
				sTopic += list.get(i);
			}
			
			sTopic += "</" + mainTag + ">";
			sTopic += XMLConstants.XML_TOPIC_TAGGED;
			sTopic += XMLConstants.XML_CAPTION_TAGGED + caption + XMLConstants.XML_CAPTION_TAGGED_C;
			sTopic += XMLConstants.XML_TYPE_TAGGED + type + XMLConstants.XML_TYPE_TAGGED_C;
			sTopic += XMLConstants.XML_TOPIC_TAGGED_C;
			sTopic += XMLConstants.XML_TOPICS_TAGGED_C + XMLConstants.XML_MAIN_TOPIC_TAGGED_C;
		}
		
		return( sTopic );
	}
}
