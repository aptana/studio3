/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.index;

import org.eclipse.osgi.util.NLS;

/**
 * @author klindsey
 */
public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.css.core.internal.index.messages"; //$NON-NLS-1$

	public static String CSSMetadataLoader_Loading_Metadata;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
