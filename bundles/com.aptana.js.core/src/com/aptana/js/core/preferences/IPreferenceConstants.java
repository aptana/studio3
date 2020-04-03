/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.preferences;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public interface IPreferenceConstants
{

	/**
	 * The value is a double used to indicate the current format being used by the JS index categories.
	 */
	public static final String JS_INDEX_VERSION = "JS_INDEX_VERSION"; //$NON-NLS-1$

	/**
	 * Path to NodeJS' binary. Stored as OS string. Convert to {@link IPath} using {@link Path#fromOSString(String)}
	 */
	public static final String NODEJS_EXECUTABLE_PATH = "nodejs_path"; //$NON-NLS-1$

	/**
	 * Path to NodeJS' source directory. Stored as OS string. Convert to {@link IPath} using
	 * {@link Path#fromOSString(String)}
	 */
	public static final String NODEJS_SOURCE_PATH = "nodejs_source_path"; //$NON-NLS-1$

	/**
	 * What severity do missing semicolons use?
	 */
	public static final String PREF_MISSING_SEMICOLON_SEVERITY = "missing_semicolon_severity"; //$NON-NLS-1$

}
