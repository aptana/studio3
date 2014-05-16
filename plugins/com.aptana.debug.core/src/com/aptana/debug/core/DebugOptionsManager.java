/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable variableDeclaredInLoop

package com.aptana.debug.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.IUniformResourceMarker;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;

/**
 * @author Max Stepanov
 */
public class DebugOptionsManager implements IDebugEventSetListener
{

	/**
	 * DEBUGGER_ACTIVE_SUFFIX
	 */
	private static final String DEBUGGER_ACTIVE_SUFFIX = ".debuggerActive"; //$NON-NLS-1$

	/**
	 * PROFILER_ACTIVE_SUFFIX
	 */
	private static final String PROFILER_ACTIVE_SUFFIX = ".profilerActive"; //$NON-NLS-1$

	/**
	 * DETAIL_FORMATTER_IS_ENABLED
	 */
	public static final String DETAIL_FORMATTER_IS_ENABLED = "1"; //$NON-NLS-1$

	/**
	 * DETAIL_FORMATTER_IS_DISABLED
	 */
	public static final String DETAIL_FORMATTER_IS_DISABLED = "0"; //$NON-NLS-1$

	private final String modelIdentifier;
	private final ListenerList changeListeners = new ListenerList();
	private IPreferenceChangeListener preferenceChangeListener;

	/**
	 * Map of types to the associated formatter (code snippet). ( <code>String</code> -> <code>String</code>)
	 */
	private Map<String, DetailFormatter> fDetailFormattersMap;

	public DebugOptionsManager(String modelIdentifier)
	{
		this.modelIdentifier = modelIdentifier;
	}

	/**
	 * startup
	 */
	public void startup()
	{
		DebugPlugin.getDefault().addDebugEventListener(this);
		populateDetailFormattersMap();
		EclipseUtil.instanceScope().getNode(DebugCorePlugin.PLUGIN_ID)
				.addPreferenceChangeListener(preferenceChangeListener = new IPreferenceChangeListener()
				{
					public void preferenceChange(PreferenceChangeEvent event)
					{
						if (getDetailFormattersPrefName().equals(event.getKey()))
						{
							populateDetailFormattersMap();
							notifyChangeListeners();
						}
					}
				});
	}

	/**
	 * shutdown
	 */
	public void shutdown()
	{
		DebugPlugin.getDefault().removeDebugEventListener(this);
		EclipseUtil.instanceScope().getNode(DebugCorePlugin.PLUGIN_ID)
				.removePreferenceChangeListener(preferenceChangeListener);
	}

	public static boolean isDebuggerActive(String modelIdentifier)
	{
		return Boolean.TRUE.toString().equals(System.getProperty(modelIdentifier + DEBUGGER_ACTIVE_SUFFIX));
	}

	public static boolean isProfilerActive(String modelIdentifier)
	{
		return Boolean.TRUE.toString().equals(System.getProperty(modelIdentifier + PROFILER_ACTIVE_SUFFIX));
	}

	/**
	 * getDetailFormatters
	 * 
	 * @return Collection
	 */
	public Collection<DetailFormatter> getDetailFormatters()
	{
		return fDetailFormattersMap.values();
	}

	/**
	 * setDetailFormatters
	 * 
	 * @param formatters
	 */
	public void setDetailFormatters(Collection<DetailFormatter> formatters)
	{
		fDetailFormattersMap.clear();
		for (DetailFormatter formatter : formatters)
		{
			fDetailFormattersMap.put(formatter.getTypeName(), formatter);
		}
		savePreferences();
	}

	/**
	 * setAssociatedDetailFormatter
	 * 
	 * @param detailFormatter
	 */
	public void setAssociatedDetailFormatter(DetailFormatter detailFormatter)
	{
		fDetailFormattersMap.put(detailFormatter.getTypeName(), detailFormatter);
		savePreferences();
	}

	/**
	 * removeAssociatedDetailFormatter
	 * 
	 * @param detailFormatter
	 */
	public void removeAssociatedDetailFormatter(DetailFormatter detailFormatter)
	{
		fDetailFormattersMap.remove(detailFormatter.getTypeName());
		savePreferences();
	}

	/**
	 * hasAssociatedDetailFormatter
	 * 
	 * @param typeName
	 * @return boolean
	 */
	public boolean hasAssociatedDetailFormatter(String typeName)
	{
		return fDetailFormattersMap.containsKey(typeName);
	}

	/**
	 * getAssociatedDetailFormatter
	 * 
	 * @param typeName
	 * @return DetailFormatter
	 */
	public DetailFormatter getAssociatedDetailFormatter(String typeName)
	{
		return fDetailFormattersMap.get(typeName);
	}

	/**
	 * addChangeListener
	 * 
	 * @param listener
	 */
	public void addChangeListener(IDetailFormattersChangeListener listener)
	{
		changeListeners.add(listener);
	}

	/**
	 * removeChangeListener
	 * 
	 * @param listener
	 */
	public void removeChangeListener(IDetailFormattersChangeListener listener)
	{
		changeListeners.remove(listener);
	}

	/**
	 * Parses the comma separated string into an array of strings
	 * 
	 * @param listString
	 * @return String[]
	 */
	public static String[] parseList(String listString)
	{
		List<String> list = new ArrayList<String>(10);
		StringTokenizer tokenizer = new StringTokenizer(listString, ","); //$NON-NLS-1$
		while (tokenizer.hasMoreTokens())
		{
			list.add(tokenizer.nextToken());
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * Serializes the array of strings into one comma separated string.
	 * 
	 * @param list
	 *            array of strings
	 * @return a single string composed of the given list
	 */
	public static String serializeList(String[] list)
	{
		if (list == null)
		{
			return StringUtil.EMPTY;
		}
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < list.length; i++)
		{
			if (i > 0)
			{
				buffer.append(',');
			}
			buffer.append(list[i]);
		}
		return buffer.toString();
	}

	private String getDetailFormattersPrefName()
	{
		return modelIdentifier + IDebugCorePreferenceNames.SUFFIX_DETAIL_FORMATTERS_LIST;
	}

	/**
	 * Populate the detail formatters map with data from preferences.
	 */
	private void populateDetailFormattersMap()
	{
		String[] detailFormattersList = DebugOptionsManager.parseList(EclipseUtil.instanceScope()
				.getNode(DebugCorePlugin.PLUGIN_ID).get(getDetailFormattersPrefName(), StringUtil.EMPTY));
		fDetailFormattersMap = new HashMap<String, DetailFormatter>(detailFormattersList.length / 3);
		for (int i = 0, length = detailFormattersList.length; i < length;)
		{
			String typeName = detailFormattersList[i++];
			String snippet = detailFormattersList[i++].replace('\u0000', ',');
			boolean enabled = !DETAIL_FORMATTER_IS_DISABLED.equals(detailFormattersList[i++]);
			fDetailFormattersMap.put(typeName, new DetailFormatter(typeName, snippet, enabled));
		}
	}

	private void savePreferences()
	{
		String[] values = new String[fDetailFormattersMap.size() * 3];
		int i = 0;
		for (DetailFormatter detailFormatter : fDetailFormattersMap.values())
		{
			values[i++] = detailFormatter.getTypeName();
			values[i++] = detailFormatter.getSnippet().replace(',', '\u0000');
			values[i++] = detailFormatter.isEnabled() ? DETAIL_FORMATTER_IS_ENABLED : DETAIL_FORMATTER_IS_DISABLED;
		}
		String value = DebugOptionsManager.serializeList(values);
		IEclipsePreferences preferences = EclipseUtil.instanceScope().getNode(DebugCorePlugin.PLUGIN_ID);
		preferences.put(getDetailFormattersPrefName(), value);
		try
		{
			preferences.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(DebugCorePlugin.getDefault(), e);
		}
	}

	private void notifyChangeListeners()
	{
		for (Object listener : changeListeners.getListeners())
		{
			((IDetailFormattersChangeListener) listener).detailFormattersChanged();
		}
	}

	/*
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events)
	{
		for (DebugEvent event : events)
		{
			if (event.getSource() instanceof IDebugTarget
					&& modelIdentifier.equals(((IDebugTarget) event.getSource()).getModelIdentifier()))
			{
				switch (event.getKind())
				{
					case DebugEvent.CREATE:
						String launchMode = ((IDebugTarget) event.getSource()).getLaunch().getLaunchMode();
						if (ILaunchManager.DEBUG_MODE.equals(launchMode))
						{
							System.setProperty(modelIdentifier + DEBUGGER_ACTIVE_SUFFIX, Boolean.TRUE.toString());
						}
						else if (ILaunchManager.PROFILE_MODE.equals(launchMode))
						{
							System.setProperty(modelIdentifier + PROFILER_ACTIVE_SUFFIX, Boolean.TRUE.toString());
						}
						break;
					case DebugEvent.TERMINATE:
						System.getProperties().remove(modelIdentifier + DEBUGGER_ACTIVE_SUFFIX);
						System.getProperties().remove(modelIdentifier + PROFILER_ACTIVE_SUFFIX);
						cleanupBreakpoints();
						break;
					default:
						break;
				}
			}
		}
	}

	private void cleanupBreakpoints()
	{
		IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		for (IBreakpoint breakpoint : breakpointManager.getBreakpoints(modelIdentifier))
		{
			try
			{
				IMarker marker = breakpoint.getMarker();
				URI uri = null;
				if (marker instanceof IUniformResourceMarker)
				{
					uri = ((IUniformResourceMarker) marker).getUniformResource().getURI();
				}
				else
				{
					IResource resource = marker.getResource();
					if (resource instanceof IWorkspaceRoot)
					{
						String bpLocation = (String) marker.getAttribute(IDebugCoreConstants.BREAKPOINT_LOCATION);
						if (bpLocation != null)
						{
							uri = URI.create(bpLocation);
						}
					}
					else
					{
						uri = resource.getLocation().makeAbsolute().toFile().toURI();
					}
				}
				if (uri != null && "dbgsource".equals(uri.getScheme())) //$NON-NLS-1$
				{
					breakpoint.delete();
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(DebugCorePlugin.getDefault(), e);
			}
		}
	}
}
