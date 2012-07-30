/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.build;

import java.util.Map;

import org.eclipse.core.resources.IMarker;
import com.aptana.core.Messages;

/**
 * @author Ingo Muschenetz
 * @author Chris Williams
 */
public interface IProblem
{

	enum Severity
	{
		// @formatter:off
		IGNORE(-1, Messages.IProblem_Ignore),
		INFO(IMarker.SEVERITY_INFO, Messages.IProblem_Info),
		WARNING(IMarker.SEVERITY_WARNING, Messages.IProblem_Warning),
		ERROR(IMarker.SEVERITY_ERROR, Messages.IProblem_Error);
		// @formatter:on

		private int num;
		private String label;

		Severity(int num, String label)
		{
			this.num = num;
			this.label = label;
		}

		public int intValue()
		{
			return this.num;
		}

		public static Severity create(int value)
		{
			for (Severity s : values())
			{
				if (s.num == value)
				{
					return s;
				}
			}
			return Severity.WARNING;
		}

		public static Severity create(String value)
		{
			for (Severity s : values())
			{
				if (s.label.equals(value))
				{
					return s;
				}
			}
			return Severity.WARNING;
		}

		public String label()
		{
			return label;
		}
	}

	/**
	 * A unique id used to identify the exact problem type. Used to associate specific problems with quick fixes.
	 */
	public static final String ID = "id"; //$NON-NLS-1$

	/**
	 */
	public static final String FLAGS = "flags"; //$NON-NLS-1$

	/**
	 */
	public static final String ARGUMENTS = "arguments"; //$NON-NLS-1$

	/**
	 * Gets the text offset where the error starts.
	 * 
	 * @return the offset
	 */
	int getOffset();

	/**
	 * Gets the length of the errored text.
	 * 
	 * @return the length
	 */
	int getLength();

	/**
	 * Gets the line number of the errored text.
	 * 
	 * @return the line number
	 */
	int getLineNumber();

	/**
	 * Gets the error message.
	 * 
	 * @return the error message
	 */
	String getMessage();

	/**
	 * Gets the severity of the error.
	 * 
	 * @return the severity
	 */
	Severity getSeverity();

	/**
	 * Gets the priority of the task (typically unused for errors/warnings).
	 * 
	 * @return the priority
	 */
	int getPriority();

	/**
	 * Gets the path of the source.
	 * 
	 * @return the source path
	 */
	String getSourcePath();

	/**
	 * Creates a map of marker attributes.
	 * 
	 * @return the attributes in a map
	 */
	Map<String, Object> createMarkerAttributes();

	/**
	 * Return the mapping of custom attributes.
	 * 
	 * @return the attributes in a map
	 */
	Map<String, Object> getAttributes();

	/**
	 * Sets a custom attribute.
	 * 
	 * @param attrName
	 * @param value
	 */
	public void setAttribute(String attrName, Object value);

	/**
	 * Does this problem represent a warning?
	 * 
	 * @return
	 */
	boolean isWarning();

	/**
	 * Does this problem represent an error?
	 * 
	 * @return
	 */
	boolean isError();

	/**
	 * Does this problem represent a task?
	 * 
	 * @return
	 */
	boolean isTask();
}
