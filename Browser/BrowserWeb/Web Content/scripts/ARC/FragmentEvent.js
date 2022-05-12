/** 
 * @fileoverview This file contains the simple fragment event model.
 * There is a singleton instance of this class, through which all events are
 * funnelled.
 * Note that this implementation has been kept deliberately simple - if it gets
 * complicated, we should consider moving to a JS GUI framework.
 *
 * eventType - hierachacal description of the event
 *   e.g. Tab.Clicked
 * eventParam - 
 *   e.g. for Tab.Clicked this will be the name of the top level and second level tab clicked
 * sourceFragmentName
 *
 *
 * 
 * @author Dave Burford
 * @version 0.1
 */

/**
 * 'Singleton' object for general Fragment utilities.
 */
// TODO: This will not work across multiple windows; use top. if it ever needs to ...
var FragmentEvent = new Object();
FragmentEvent._listeners = new Array();
FragmentEvent.lastEventParam;

// The events
FragmentEvent.TAB_SELECTED="tab.selected";


/**
 * Get the parent FragmentName, if it has been set in a form's WS_parentWindow field
 */
FragmentEvent.addEventListener = function(eventType, funct) {
  var listenersForThisType = FragmentEvent._listeners[eventType];
  
  if(listenersForThisType == null) {
    listenersForThisType = new Array();
    FragmentEvent._listeners[eventType] = listenersForThisType;
  }
  
  listenersForThisType.push(funct);
};

FragmentEvent.raiseEvent = function(eventType, eventParam, sourceFragmentName) {
  // todo: At present, we only check for an absolute match on event type ...
  // this could be extended to allow one to register for Menu events and get all such events
  var listenersForThisType = FragmentEvent._listeners[eventType];
  
  if(listenersForThisType != null) {
    for(var i=0;i<listenersForThisType.length;i++) {
      listenersForThisType[i].call(this, eventType, eventParam, sourceFragmentName);
    }
  }

  FragmentEvent.lastEventParam = eventParam;
};

FragmentEvent.getLastEventParam = function() {
  return FragmentEvent.lastEventParam;
};


