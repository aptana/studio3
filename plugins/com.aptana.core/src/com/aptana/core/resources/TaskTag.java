package com.aptana.core.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.aptana.core.ICorePreferenceConstants;

public class TaskTag
{

	// We originally hung task tag prefs in the common editor plugin, so we look there for the prefs
	private static final String PREF_PLUGIN_ID = "com.aptana.editor.common"; //$NON-NLS-1$

	public static final String HIGH = "High"; //$NON-NLS-1$
	public static final String NORMAL = "Normal"; //$NON-NLS-1$
	public static final String LOW = "Low"; //$NON-NLS-1$

	private static final Pattern fgCommaSplitter = Pattern.compile(","); //$NON-NLS-1$

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
		IScopeContext[] contexts = new IScopeContext[] { new InstanceScope(), new DefaultScope() };
		return Platform.getPreferencesService().getBoolean(PREF_PLUGIN_ID,
				ICorePreferenceConstants.TASK_TAGS_CASE_SENSITIVE, true, contexts);
	}

	public static Collection<TaskTag> getTaskTags()
	{
		IScopeContext[] contexts = new IScopeContext[] { new InstanceScope(), new DefaultScope() };
		String rawTagNames = Platform.getPreferencesService().getString(PREF_PLUGIN_ID,
				ICorePreferenceConstants.TASK_TAG_NAMES, null, contexts);
		String rawTagPriorities = Platform.getPreferencesService().getString(PREF_PLUGIN_ID,
				ICorePreferenceConstants.TASK_TAG_PRIORITIES, null, contexts);
		return createTaskTags(rawTagNames, rawTagPriorities);
	}

	private static List<TaskTag> createTaskTags(String rawTagNames, String rawTagPriorities)
	{
		List<TaskTag> tags = new ArrayList<TaskTag>();
		String[] tagNames = fgCommaSplitter.split(rawTagNames);
		String[] tagPriorities = fgCommaSplitter.split(rawTagPriorities);
		// TODO make sure arrays are same size!
		for (int i = 0; i < tagNames.length; i++)
		{
			tags.add(new TaskTag(tagNames[i], tagPriorities[i]));
		}
		return tags;
	}
}