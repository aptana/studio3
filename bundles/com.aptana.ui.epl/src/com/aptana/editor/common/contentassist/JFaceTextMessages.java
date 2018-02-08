/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Helper class to get NLSed messages.
 */
class JFaceTextMessages
{

	private static final String RESOURCE_BUNDLE = JFaceTextMessages.class.getName();

	private static ResourceBundle fgResourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);

	private JFaceTextMessages()
	{
	}

	/**
	 * Gets a string from the resource bundle.
	 * 
	 * @param key
	 *            the string used to get the bundle value, must not be <code>null</code>
	 * @return the string from the resource bundle
	 */
	public static String getString(String key)
	{
		try
		{
			return fgResourceBundle.getString(key);
		}
		catch (MissingResourceException e)
		{
			return "!" + key + "!";//$NON-NLS-2$ //$NON-NLS-1$
		}
	}
}
