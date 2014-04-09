/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal;

import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.IUserAgent;
import com.aptana.core.util.StringUtil;

/**
 * @author cwilliams
 */
class LazyUserAgent implements IUserAgent
{

	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_ICON = "icon"; //$NON-NLS-1$
	private static final String ATTR_ICON_DISABLED = "icon-disabled"; //$NON-NLS-1$

	private IConfigurationElement element;

	LazyUserAgent(IConfigurationElement element)
	{
		this.element = element;
	}

	public String getID()
	{
		return element.getAttribute(ATTR_ID);
	}

	public String getContributor()
	{
		return element.getContributor().getName();
	}

	public String getName()
	{
		return element.getAttribute(ATTR_NAME);
	}

	public String getEnabledIconPath()
	{
		return element.getAttribute(ATTR_ICON);
	}

	public String getDisabledIconPath()
	{
		return element.getAttribute(ATTR_ICON_DISABLED);
	}

	public int compareTo(IUserAgent o)
	{
		String name = (o != null) ? o.getName() : StringUtil.EMPTY;
		return getName().compareToIgnoreCase(name);
	}

}
