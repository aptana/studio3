/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.index;

public interface ICSSIndexConstants
{
	// the content format version of the CSS index files
	// 0.1 - Initial version
	// 0.11 - Using JSON for element and property content assist model elements
	// 0.12 - Updated browser support for css3 properties
	// 0.13 - Added properties for webkit
	public static final double INDEX_VERSION = 0.13;

	// for debugging, comment the line above, and uncomment the following
	// public static final double INDEX_VERSION = new Random().nextDouble() * 1e6;

	static final String PREFIX = "css."; //$NON-NLS-1$
	// general constants
	static final String DELIMITER = "\0"; //$NON-NLS-1$
	static final String SUB_DELIMITER = ","; //$NON-NLS-1$
	static final String METADATA_INDEX_LOCATION = PREFIX + "metadata:/css"; //$NON-NLS-1$
	static final String CORE = "CSS Core"; //$NON-NLS-1$

	// index categories
	static final String ELEMENT = PREFIX + "element"; //$NON-NLS-1$
	static final String PROPERTY = PREFIX + "property"; //$NON-NLS-1$
	static final String USER_AGENT = PREFIX + "user_agent"; //$NON-NLS-1$
	static final String VALUE = PREFIX + "value"; //$NON-NLS-1$
	static final String PSUEDO_CLASS = PREFIX + "pseudo_class"; //$NON-NLS-1$
	static final String PSUEDO_ELEMENT = PREFIX + "pseudo_element"; //$NON-NLS-1$

	static final String CLASS = PREFIX + "class"; //$NON-NLS-1$
	static final String IDENTIFIER = PREFIX + "identifier"; //$NON-NLS-1$
	static final String COLOR = PREFIX + "color"; //$NON-NLS-1$
}
