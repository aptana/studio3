/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist.index;

public interface JSIndexConstants
{
	// the content format version of the JS index files
	// 0.1 - Initial version
	// 0.11 - Use UUIDs for foreign keys
	// 0.12 - FunctionElements have types and returnTypes now
	// 0.13 - Fix UserAgent foreign keys
	// 0.14 - Fix to StringUtil.join to not include null values in final string
	// 0.15 - Write user agent list from UserAgentManager to index as well
	// 0.16 - Change field order when writing properties and functions (search optimization)
	// 0.17 - Modified static properties in DOM files, changing them to instance where appropriate.
	// 0.18 - Window now inherits from Global
	// 0.19 - Fix Document.forms type in dom_2.xml
	// 0.20 - Added Error class as a property of Window
	// 0.21 - Using JSON for property and function content assist model elements
	// 0.22 - Add events, examples, remarks, and a deprecated flag to type elements
	public static final double INDEX_VERSION = 0.22;

	// for debugging, comment the line above, and uncomment the following
	// public static final double INDEX_VERSION = new Random().nextDouble() * 1e6;

	// general constants
	static final String PREFIX = "js."; //$NON-NLS-1$
	static final String METADATA_FILE_LOCATION = PREFIX + "metadata"; //$NON-NLS-1$
	static final String METADATA_INDEX_LOCATION = "metadata:/js"; //$NON-NLS-1$
	static final String DELIMITER = "\0"; //$NON-NLS-1$
	static final String SUB_DELIMITER = ","; //$NON-NLS-1$
	static final String CORE = "JS Core"; //$NON-NLS-1$

	// index categories
	static final String TYPE = PREFIX + "type"; //$NON-NLS-1$
	static final String FUNCTION = PREFIX + "function"; //$NON-NLS-1$
	static final String PROPERTY = PREFIX + "property"; //$NON-NLS-1$

	static final String[] ALL_CATEGORIES = new String[] { //
	TYPE, //
		FUNCTION, //
		PROPERTY //
	};

	// special values
	static final String NO_ENTRY = "-1"; //$NON-NLS-1$
}
