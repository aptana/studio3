/**
 * Calls the Portal browser controller to refresh itself by re-loading the portal.
 * This will cause the portal to retry a connection to the remote content.
 */
 
 
function reloadPortal() {
  
  var obj = window.location.href.toQueryParams();
  var url = null;
  if(typeof(obj) !== 'undefined' && typeof(obj.url) != 'undefined') {
  	url = obj.url;
  }
  dispatchDefined = (typeof(dispatch) !== 'undefined');
  if (dispatchDefined) {
    return dispatch($H({controller:'portal.browser', action:"refreshPortal", args : [url].toJSON()}).toJSON());
  }
  
}