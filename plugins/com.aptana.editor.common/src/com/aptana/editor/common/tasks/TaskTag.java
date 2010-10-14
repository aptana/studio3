package com.aptana.editor.common.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;

public class TaskTag
{

	public static final String HIGH = "High"; //$NON-NLS-1$
	public static final String NORMAL = "Normal"; //$NON-NLS-1$
	public static final String LOW = "Low"; //$NON-NLS-1$

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
		if (priority.equalsIgnoreCase(HIGH))
		{
			return IMarker.PRIORITY_HIGH;
		}
		if (priority.equalsIgnoreCase(LOW))
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
		return Platform.getPreferencesService().getBoolean(CommonEditorPlugin.PLUGIN_ID,
				IPreferenceConstants.TASK_TAGS_CASE_SENSITIVE, true, contexts);
	}

	public static Collection<TaskTag> getTaskTags()
	{
		IScopeContext[] contexts = new IScopeContext[] { new InstanceScope(), new DefaultScope() };
		String rawTagNames = Platform.getPreferencesService().getString(CommonEditorPlugin.PLUGIN_ID,
				IPreferenceConstants.TASK_TAG_NAMES, null, contexts);
		String rawTagPriorities = Platform.getPreferencesService().getString(CommonEditorPlugin.PLUGIN_ID,
				IPreferenceConstants.TASK_TAG_PRIORITIES, null, contexts);
		List<TaskTag> tags = createTaskTags(rawTagNames, rawTagPriorities);
		return tags;
	}

	private static List<TaskTag> createTaskTags(String rawTagNames, String rawTagPriorities)
	{
		List<TaskTag> tags = new ArrayList<TaskTag>();
		String[] tagNames = rawTagNames.split(","); //$NON-NLS-1$
		String[] tagPriorities = rawTagPriorities.split(","); //$NON-NLS-1$
		for (int i = 0; i < tagNames.length; i++)
		{
			tags.add(new TaskTag(tagNames[i], tagPriorities[i]));
		}
		return tags;
	}
}