/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.spelling;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Max Stepanov
 *
 */
public final class ScopeDefinitions {

	public static final Map<String, String> DEFINITIONS = new LinkedHashMap<String, String>();

	static {
		DEFINITIONS.put("comment.block.documentation", Messages.ScopeDefinitions_Documentation); //$NON-NLS-1$
		DEFINITIONS.put("comment.block", Messages.ScopeDefinitions_BlockComment); //$NON-NLS-1$
		DEFINITIONS.put("comment.line", Messages.ScopeDefinitions_LineComment); //$NON-NLS-1$
		DEFINITIONS.put("string.quoted.single", Messages.ScopeDefinitions_SingleQiotedString); //$NON-NLS-1$
		DEFINITIONS.put("string.quoted.double", Messages.ScopeDefinitions_DoubleQuotedString); //$NON-NLS-1$
		DEFINITIONS.put("string.unquoted", Messages.ScopeDefinitions_UnquotedStringHeredoc); //$NON-NLS-1$
	}

	/**
	 *
	 */
	private ScopeDefinitions() {
	}

}
