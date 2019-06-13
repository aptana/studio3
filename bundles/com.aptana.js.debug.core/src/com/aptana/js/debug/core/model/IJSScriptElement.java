/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.model;

import java.net.URI;

import org.eclipse.debug.core.model.IDebugElement;

/**
 * @author Max Stepanov
 */
public interface IJSScriptElement extends IDebugElement
{

	/**
	 * Script name. Typically this is the relative filename from the app root, i.e. "app.js", "lib/test.js"
	 * 
	 * @return String
	 */
	String getName();

	/**
	 * <p>
	 * The url reported in-app. This can vary according to the platform implementations. Right now, Android reports
	 * values like: "/app.js" or "/lib/test.js" iOS reports values like:
	 * (Simulator)"file:///Users/cwilliams/Library/Developer/CoreSimulator/Devices/DBBD80F0-74C7-422D-8366-6FA5A343413A/data/Containers/Bundle/Application/FB1C6D78-7509-4C0D-918F-D8C739F877E9/example.app/app.js"
	 * (device) "file:///var/containers/Bundle/Application/E374D7CE-F7F7-450F-8F53-7770F9AA845C/example.app/app.js"
	 * </p>
	 * <p>
	 * Implementations should attempt to massage into app: URIs where the path looks absolute but is relative to the
	 * in-app root so that the platform differences go away, or use ti: scheme for SDK internal files (i.e.
	 * "app:/app.js", "app:/lib/test.js", "ti:/module.js")
	 * </p>
	 */
	URI getURL();

	/**
	 * The reported sourceURL. Will be null if no special //# sourceURL comment exists in the file/code. This *should be
	 * used for display purposes*. It is primarily a mechanism for naming a snippet of code that gets eval'd.
	 */
	URI getSourceURL();

	/**
	 * The reported sourceURL. May be null if no special //# sourceMappingURL comment exists in the file
	 */
	URI getSourceMapURL();

	/**
	 * Parent function
	 * 
	 * @return IJSScriptElement
	 */
	IJSScriptElement getParent();

	/**
	 * Nested functions
	 * 
	 * @return IJSScriptElement[]
	 */
	IJSScriptElement[] getChildren();

	/**
	 * The first line of the function in source code
	 * 
	 * @return int
	 */
	int getBaseLine();

	/**
	 * Length of the function in lines
	 * 
	 * @return int
	 */
	int getLineExtent();
}
