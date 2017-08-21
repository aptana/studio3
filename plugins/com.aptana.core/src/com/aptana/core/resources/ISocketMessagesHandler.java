/**
 * Aptana Studio
 * Copyright (c) 2015 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.resources;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The interface to implement the handlers for addressing the requests/messages from the underlying CLI and return back
 * with the responses.
 *
 * @author pinnamuri
 */
public interface ISocketMessagesHandler extends ISocketMessagesHandlerNotifier
{

	/**
	 * Addresses the request either by prompting a UI to the user, or just computes a JSON object from existing answers,
	 * and will return back a response.
	 *
	 * @param request
	 * @return
	 * @throws RequestCancelledException
	 */
	public JsonNode handleRequest(JsonNode request) throws RequestCancelledException;

	/**
	 * Checks and returns workbench launch status.
	 * 
	 * @return <code>true</code> if workbench has been launched, <code>false</code> otherwise.
	 */
	public boolean isWorkbenchLaunched();

}