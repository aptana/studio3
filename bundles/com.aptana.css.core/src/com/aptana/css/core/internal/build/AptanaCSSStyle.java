/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.build;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.w3c.css.properties.css1.CssProperty;
import org.w3c.css.properties.css3.Css3Style;
import org.w3c.css.util.Utf8Properties;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.css.core.CSSCorePlugin;

/**
 * Aptana CSS Style.
 * 
 * @author Denis Denisenko
 */
public class AptanaCSSStyle extends Css3Style
{

	private static final String PROPERITES_FILE_NAME = "AptanaCSSProperties.properties"; //$NON-NLS-1$

	private static final Utf8Properties CSS_PROPERTIES = new Utf8Properties();
	static
	{
		InputStream configStream = AptanaCSSStyle.class.getResourceAsStream(PROPERITES_FILE_NAME);
		try
		{
			CSS_PROPERTIES.load(configStream);
		}
		catch (IOException e)
		{
			IdeLog.logError(CSSCorePlugin.getDefault(), Messages.AptanaCSSStyle_ERR_UnableToLoadProperties, e);
		}
	}

	/**
	 * Properties map.
	 */
	private final Map<String, CssProperty> properties = new HashMap<String, CssProperty>();

	public AptanaCSSStyle()
	{
	}

	/**
	 * Gets property by name.
	 * 
	 * @param propertyName
	 *            the property name
	 * @return the property, or null if not found
	 */
	public CssProperty getProperty(String propertyName)
	{
		return properties.get(propertyName);
	}

	/**
	 * Gets property by name. Is aware of CSS cascading.
	 * 
	 * @param propertyName
	 *            - property name.
	 * @return property by name.
	 */
	public CssProperty getPropertyCascadingOrder(String propertyName)
	{
		CssProperty toReturn = properties.get(propertyName);
		return (toReturn == null) ? style.CascadingOrder(createNewDefaultInstance(propertyName), style, selector)
				: toReturn;
	}

	/**
	 * Sets property value.
	 * 
	 * @param propertyName
	 *            the property name
	 * @param propertyValue
	 *            the property value
	 */
	public void setProperty(String propertyName, CssProperty propertyValue)
	{
		properties.put(propertyName, propertyValue);
	}

	/**
	 * Creates new default property instance.
	 * 
	 * @param propertyName
	 *            - property name.
	 * @return new property instance.
	 */
	private CssProperty createNewDefaultInstance(String propertyName)
	{
		if (StringUtil.isEmpty(propertyName))
		{
			return null;
		}
		try
		{
			String nameToSearch = propertyName;
			if (propertyName.charAt(0) == '-')
			{
				nameToSearch = propertyName.substring(1);
			}

			String propertyClassName = CSS_PROPERTIES.getProperty(nameToSearch);
			if (propertyClassName == null)
			{
				return null;
			}
			return (CssProperty) Class.forName(propertyClassName).newInstance();
		}
		catch (InstantiationException e)
		{
			throw new RuntimeException(MessageFormat.format(Messages.AptanaCSSStyle_ERR_CreatingNewInstance, getClass()
					.getName()), e);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(MessageFormat.format(Messages.AptanaCSSStyle_ERR_CreatingNewInstance, getClass()
					.getName()), e);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuntimeException(MessageFormat.format(Messages.AptanaCSSStyle_ERR_CreatingNewInstance, getClass()
					.getName()), e);
		}
	}
}
