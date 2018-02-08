/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.internal.text;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.css.internal.text.messages"; //$NON-NLS-1$

	public static String CSSModelFormatter_Example_Number;

	public static String CSSModelFormatter_ExampleSection;
	public static String CSSModelFormatter_NoDescription;
	public static String CSSModelFormatter_RemarksSection;
	public static String CSSModelFormatter_SpecificationSection;
	public static String CSSModelFormatter_SupportedPlatforms;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
