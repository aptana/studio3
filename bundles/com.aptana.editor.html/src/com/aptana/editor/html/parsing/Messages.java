/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.html.parsing.messages"; //$NON-NLS-1$

	public static String HTMLParser_missing_end_tag_error;
	public static String HTMLParser_self_closing_syntax_on_non_void_element_error;
	public static String HTMLParser_unexpected_error;
	public static String HTMLParser_ERR_TagMissingEnd;

	public static String HTMLParser_OpenTagIntendedAsClosed;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
