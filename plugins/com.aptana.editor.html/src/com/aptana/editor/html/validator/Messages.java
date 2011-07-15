/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.validator;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.editor.html.validator.messages"; //$NON-NLS-1$

	public static String HTMLTidyValidator_ast_errors;

	public static String HTMLTidyValidator_ERR_ParseErrors;
	public static String HTMLTidyValidator_ERR_Tidy;

	public static String HTMLTidyValidator_self_closing_syntax_on_non_void_element_error;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
