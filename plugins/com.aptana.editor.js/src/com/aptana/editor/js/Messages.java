/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

import com.aptana.core.logging.IdeLog;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "com.aptana.editor.js.messages"; //$NON-NLS-1$

	public static String openDeclaration_label;

	private static ResourceBundle fResourceBundle;

	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static ResourceBundle getResourceBundle()
	{
		if (fResourceBundle == null)
		{
			try
			{
				fResourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);
			}
			catch (MissingResourceException x)
			{
				// @formatter:off
				String message = MessageFormat.format(
					"Error retrieving the resource bundle for {0}: {1}", //$NON-NLS-1$
					Messages.class.getName(),
					x.getMessage()
				);
				// @formatter:on

				IdeLog.logError(JSPlugin.getDefault(), message);
			}
		}
		return fResourceBundle;
	}

	private Messages()
	{
	}
}
