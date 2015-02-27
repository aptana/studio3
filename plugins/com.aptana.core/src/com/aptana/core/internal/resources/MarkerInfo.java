/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.resources;

import java.util.Map;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

/**
 *
 */
@SuppressWarnings({ "restriction", "rawtypes", "unchecked" })
public class MarkerInfo extends org.eclipse.core.internal.resources.MarkerInfo
{

	/** UNDEFINED_ID */
	protected static final long UNDEFINED_ID = org.eclipse.core.internal.resources.MarkerInfo.UNDEFINED_ID;

	public void setAttributes(Map map, Boolean validate)
	{
		Class superClass = getClass().getSuperclass();
		try
		{
			try
			{
				superClass.getMethod("setAttributes", Map.class).invoke(this, map); //$NON-NLS-1$
				return;
			}
			catch (NoSuchMethodException e)
			{
			}
			superClass.getMethod("setAttributes", Map.class, boolean.class).invoke(this, map, validate); //$NON-NLS-1$
		}
		catch (Exception e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
	}

	public void setAttribute(String attributeName, Object value, Boolean validate)
	{
		Class superClass = getClass().getSuperclass();
		try
		{
			try
			{
				superClass.getMethod("setAttribute", String.class, Object.class).invoke(this, attributeName, value); //$NON-NLS-1$
				return;
			}
			catch (NoSuchMethodException e)
			{
			}
			superClass
					.getMethod("setAttribute", String.class, Object.class, boolean.class).invoke(this, attributeName, value, validate); //$NON-NLS-1$
		}
		catch (Exception e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
	}

	public void setAttributes(String[] attributeNames, Object[] values, Boolean validate)
	{
		Class superClass = getClass().getSuperclass();
		try
		{
			try
			{
				superClass
						.getMethod("setAttributes", String[].class, Object[].class).invoke(this, attributeNames, values); //$NON-NLS-1$
				return;
			}
			catch (NoSuchMethodException e)
			{
			}
			superClass
					.getMethod("setAttributes", String[].class, Object[].class, boolean.class).invoke(this, attributeNames, values, validate); //$NON-NLS-1$
		}
		catch (Exception e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
	}
}
