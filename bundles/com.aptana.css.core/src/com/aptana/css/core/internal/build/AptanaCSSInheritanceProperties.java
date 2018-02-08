/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.build;

import java.text.MessageFormat;

import org.w3c.css.properties.css1.CssProperty;
import org.w3c.css.util.Utf8Properties;

import com.aptana.core.logging.IdeLog;
import com.aptana.css.core.CSSCorePlugin;

/**
 * Aptana CSS inheritance properties.
 * 
 * @author Denis Denisenko
 */
public class AptanaCSSInheritanceProperties
{

	private static final String INHERITED = "inherited"; //$NON-NLS-1$
	private static final String PROPERTIES_FILE = "AptanaCSSInheritance.properties"; //$NON-NLS-1$

	private static final Utf8Properties PROPERTIES = new Utf8Properties();
	static
	{
		try
		{
			PROPERTIES.load(AptanaCSSInheritanceProperties.class.getResourceAsStream(PROPERTIES_FILE));
		}
		catch (Exception e)
		{
			IdeLog.logError(CSSCorePlugin.getDefault(),
					MessageFormat.format(Messages.AptanaCSSInheritanceProperties_ERR_FailToLoad, PROPERTIES_FILE), e);
		}
	}

	public static boolean getInheritance(CssProperty property)
	{
		String res = PROPERTIES.getProperty(property.getPropertyName() + "." + INHERITED); //$NON-NLS-1$
		return Boolean.parseBoolean(res);
	}

	private AptanaCSSInheritanceProperties()
	{
	}
}
