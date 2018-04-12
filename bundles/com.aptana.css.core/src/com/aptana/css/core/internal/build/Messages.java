/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.build;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{

	private static final String BUNDLE_NAME = "com.aptana.css.core.internal.build.messages"; //$NON-NLS-1$

	public static String AptanaCSSInheritanceProperties_ERR_FailToLoad;
	public static String AptanaCSSStyle_ERR_CreatingNewInstance;
	public static String AptanaCSSStyle_ERR_UnableToLoadProperties;
	public static String CSSValidator_ERR_FailToLoadProfile;
	public static String CSSValidator_ERR_InvalidPath;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
