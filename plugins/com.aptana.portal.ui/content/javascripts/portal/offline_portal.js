/**
 * Calls the Portal browser controller to refresh itself by re-loading the portal.
 * This will cause the portal to retry a connection to the remote content.
 */
function reloadPortal() {
  dispatchDefined = (typeof(dispatch) !== 'undefined');
  if (dispatchDefined) {
    return dispatch($H({controller:'portal.browser', action:"refreshPortal", args : [""].toJSON()}).toJSON());
  }
}

