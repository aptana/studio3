/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.formatter.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ui.preferences.IWorkingCopyManager;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.formatter.IDebugScopes;
import com.aptana.formatter.epl.FormatterPlugin;

public final class PreferenceKey
{

	private final String fQualifier;
	private final String fKey;

	public PreferenceKey(String qualifier, String key)
	{
		// it is here to declare the parameter precondition, not to check
		// values at runtime.
		assert (qualifier != null);
		assert (key != null);
		fQualifier = qualifier;
		fKey = key;
	}

	public String getName()
	{
		return fKey;
	}

	public void flush(IScopeContext context)
	{
		IEclipsePreferences node = context.getNode(fQualifier);
		if (node != null)
		{
			try
			{
				node.flush();
			}
			catch (BackingStoreException e)
			{
				IdeLog.logError(FormatterPlugin.getDefault(), "Error flushing a node", e, IDebugScopes.DEBUG); //$NON-NLS-1$
			}
		}
	}

	private IEclipsePreferences getNode(IScopeContext context, IWorkingCopyManager manager)
	{
		IEclipsePreferences node = context.getNode(fQualifier);
		if (manager != null)
		{
			return manager.getWorkingCopy(node);
		}
		return node;
	}

	public String getStoredValue(IScopeContext context)
	{
		return getStoredValue(context, null);
	}

	public String getStoredValue(IScopeContext context, IWorkingCopyManager manager)
	{
		return getNode(context, manager).get(fKey, null);
	}

	public String getStoredValue(IScopeContext[] lookupOrder, boolean ignoreTopScope, IWorkingCopyManager manager)
	{
		for (int i = ignoreTopScope ? 1 : 0; i < lookupOrder.length; i++)
		{
			String value = getStoredValue(lookupOrder[i], manager);
			if (value != null)
			{
				return value;
			}
		}
		return null;
	}

	public void setStoredValue(IScopeContext context, String value, IWorkingCopyManager manager)
	{
		if (value != null)
		{
			getNode(context, manager).put(fKey, value);
		}
		else
		{
			getNode(context, manager).remove(fKey);
		}
	}

	/**
	 * @param context
	 * @param value
	 */
	public void setStoredValue(IScopeContext context, String value)
	{
		setStoredValue(context, value, null);
	}

	@Override
	public int hashCode()
	{
		return fQualifier.hashCode() ^ fKey.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof PreferenceKey)
		{
			final PreferenceKey other = (PreferenceKey) obj;
			return fKey.equals(other.fKey) && fQualifier.equals(other.fQualifier);
		}
		return false;
	}

	@Override
	public String toString()
	{
		return fQualifier + '/' + fKey;
	}

	public String getQualifier()
	{
		return fQualifier;
	}
}
