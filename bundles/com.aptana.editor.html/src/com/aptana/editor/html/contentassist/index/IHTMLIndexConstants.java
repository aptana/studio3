/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist.index;

public interface IHTMLIndexConstants
{
	// the content format version of the JS index files
	// 0.1 - Initial version
	// 0.11 - Using JSON for element and property content assist model elements
	// 0.12 - Updated the browser support for html5 tags
	// 0.13 - Fixed some misformatted examples
	// 0.14 - Added '*' value to both 'rel' and 'rev attribute, fixed a typo
	public static final double INDEX_VERSION = 0.14;

	// for debugging, comment the line above, and uncomment the following
	// public static final double INDEX_VERSION = new Random().nextDouble() * 1e6;

	// general constants
	static final String PREFIX = "html."; //$NON-NLS-1$
	static final String DELIMITER = "\0"; //$NON-NLS-1$
	static final String SUB_DELIMITER = ","; //$NON-NLS-1$
	static final String METADATA_INDEX_LOCATION = "metadata:/html"; //$NON-NLS-1$
	static final String CORE = "HTML Core"; //$NON-NLS-1$

	// index categories
	static final String ELEMENT = PREFIX + "element"; //$NON-NLS-1$
	static final String ATTRIBUTE = PREFIX + "attribute"; //$NON-NLS-1$
	static final String EVENT = PREFIX + "event"; //$NON-NLS-1$
	static final String USER_AGENT = PREFIX + "user_agent"; //$NON-NLS-1$
	static final String ENTITY = PREFIX + "entity"; //$NON-NLS-1$

	static final String RESOURCE_CSS = PREFIX + "resource.css"; //$NON-NLS-1$
	static final String RESOURCE_JS = PREFIX + "resource.js"; //$NON-NLS-1$
}
