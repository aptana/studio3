/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.core.io;

import java.util.HashMap;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("serial")
public class ConnectionContext extends HashMap<String, Object> {

	public static final String NO_PASSWORD_PROMPT = "no_password_prompt"; //$NON-NLS-1$
	public static final String QUICK_CONNECT = "quick_connect"; //$NON-NLS-1$
	public static final String DETECT_TIMEZONE = "detect_timezone"; //$NON-NLS-1$
	public static final String SERVER_TIMEZONE = "server_timezone"; //$NON-NLS-1$
	public static final String COMMAND_LOG = "command_log"; //$NON-NLS-1$
	public static final String USE_TEMPORARY_ON_UPLOAD = "use_temporary_on_upload"; //$NON-NLS-1$
	
	public boolean getBoolean(String key) {
		Object value = get(key);
		if (value != null) {
			if (value instanceof Boolean) {
				return ((Boolean) value).booleanValue();
			} else if (value instanceof String) {
				return Boolean.valueOf((String) value);
			}
		}
		return false;
	}
	
	public void setBoolean(String key, boolean value) {
		put(key, Boolean.valueOf(value));
	}
}
