package com.aptana.editor.common.tasks;

import org.eclipse.core.resources.IMarker;

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
}