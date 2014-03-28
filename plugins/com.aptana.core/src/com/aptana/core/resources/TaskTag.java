/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.resources;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IScopeContext;

import com.aptana.core.CorePlugin;
import com.aptana.core.ICorePreferenceConstants;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;

public class TaskTag
{

	// We originally hung task tag prefs in the common editor plugin, so we look there for the prefs
	private static final String PREF_PLUGIN_ID = "com.aptana.editor.common"; //$NON-NLS-1$

	public static final String HIGH = "High"; //$NON-NLS-1$
	public static final String NORMAL = "Normal"; //$NON-NLS-1$
	public static final String LOW = "Low"; //$NON-NLS-1$

	private static final Pattern fgCommaSplitter = Pattern.compile(","); //$NON-NLS-1$

	/**
	 * Cache the values so we don't look them up in prefs every time!
	 */
	private static boolean fgCaseSensitive;
	private static IEclipsePreferences.IPreferenceChangeListener fgPrefListener;

	private static volatile List<TaskTag> fgTaskTags;

	private int fPriority;
	private String fName;

	public TaskTag(String name, String priority)
	{
		this(name, toIntegerValue(priority));
	}

	public TaskTag(String name, int priority)
	{
		this.fName = name;
		this.fPriority = priority;
	}

	private static int toIntegerValue(String priority)
	{
		if (HIGH.equalsIgnoreCase(priority))
		{
			return IMarker.PRIORITY_HIGH;
		}
		if (LOW.equalsIgnoreCase(priority))
		{
			return IMarker.PRIORITY_LOW;
		}
		return IMarker.PRIORITY_NORMAL;
	}

	public Integer getPriority()
	{
		return fPriority;
	}

	public String getName()
	{
		return fName;
	}

	public String getPriorityName()
	{
		switch (fPriority)
		{
			case IMarker.PRIORITY_HIGH:
				return HIGH;
			case IMarker.PRIORITY_LOW:
				return LOW;
			default:
				return NORMAL;
		}
	}

	public static boolean isCaseSensitive()
	{
		initializeValues();
		return fgCaseSensitive;
	}

	/**
	 * Add a pref change listener for changes to values, set up starting values. This is a performance change so we
	 * don't lookup the preference values repeatedly.
	 */
	private synchronized static void initializeValues()
	{
		if (fgPrefListener == null)
		{
			final IScopeContext[] contexts = new IScopeContext[] { EclipseUtil.instanceScope(),
					EclipseUtil.defaultScope() };
			try
			{
				fgPrefListener = new IEclipsePreferences.IPreferenceChangeListener()
				{

					public void preferenceChange(PreferenceChangeEvent event)
					{
						if (ICorePreferenceConstants.TASK_TAGS_CASE_SENSITIVE.equals(event.getKey()))
						{
							fgCaseSensitive = Platform.getPreferencesService().getBoolean(PREF_PLUGIN_ID,
									ICorePreferenceConstants.TASK_TAGS_CASE_SENSITIVE, true, contexts);
						}
						else if (ICorePreferenceConstants.TASK_TAG_PRIORITIES.equals(event.getKey())
								|| ICorePreferenceConstants.TASK_TAG_NAMES.equals(event.getKey()))
						{
							fgTaskTags = getCurrentTaskTags();
						}
					}
				};
				EclipseUtil.instanceScope().getNode(PREF_PLUGIN_ID).addPreferenceChangeListener(fgPrefListener);
			}
			catch (Exception e)
			{
				IdeLog.logError(CorePlugin.getDefault(), "Failed to attach preference listener for task tag prefs", e); //$NON-NLS-1$
				fgPrefListener = null;
			}
			fgCaseSensitive = Platform.getPreferencesService().getBoolean(PREF_PLUGIN_ID,
					ICorePreferenceConstants.TASK_TAGS_CASE_SENSITIVE, true, contexts);
		}
		if (fgTaskTags == null)
		{
			fgTaskTags = getCurrentTaskTags();
		}
	}

	/**
	 * Looks up the task tag strings an priorities from preferences.
	 * 
	 * @return
	 */
	private static List<TaskTag> getCurrentTaskTags()
	{
		try
		{
			final IScopeContext[] contexts = new IScopeContext[] { EclipseUtil.instanceScope(),
					EclipseUtil.defaultScope() };
			String rawTagNames = Platform.getPreferencesService().getString(PREF_PLUGIN_ID,
					ICorePreferenceConstants.TASK_TAG_NAMES, null, contexts);
			String rawTagPriorities = Platform.getPreferencesService().getString(PREF_PLUGIN_ID,
					ICorePreferenceConstants.TASK_TAG_PRIORITIES, null, contexts);
			return createTaskTags(rawTagNames, rawTagPriorities);
		}
		catch (Exception e)
		{
			IdeLog.logError(CorePlugin.getDefault(), "Failed to lookup task tag strings and priorities", e); //$NON-NLS-1$
			return Collections.emptyList();
		}
	}

	public static Collection<TaskTag> getTaskTags()
	{
		initializeValues();
		if (fgTaskTags == null)
		{
			return Collections.emptyList();
		}
		return fgTaskTags;
	}

	private static List<TaskTag> createTaskTags(String rawTagNames, String rawTagPriorities)
	{
		List<TaskTag> tags = new ArrayList<TaskTag>();
		String[] tagNames = fgCommaSplitter.split(rawTagNames);
		String[] tagPriorities = fgCommaSplitter.split(rawTagPriorities);
		if (tagNames.length != tagPriorities.length)
		{
			IdeLog.logWarning(
					CorePlugin.getDefault(),
					MessageFormat
							.format("Tag name and priority lists weren't the same length. Names: {0}; Priorities: {1}", rawTagNames, rawTagPriorities)); //$NON-NLS-1$
		}
		for (int i = 0; i < tagNames.length; i++)
		{
			// If array sizes don't match, assume "normal" priority.
			String priority = NORMAL;
			if (i < tagPriorities.length)
			{
				priority = tagPriorities[i];
			}
			tags.add(new TaskTag(tagNames[i], priority));
		}
		return tags;
	}
}