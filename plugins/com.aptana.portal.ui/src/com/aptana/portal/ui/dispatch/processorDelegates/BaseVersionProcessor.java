/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.processorDelegates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Version;

import com.aptana.configurations.processor.AbstractProcessorDelegate;
import com.aptana.portal.ui.PortalUIPlugin;

/**
 * A base application version retrieval delegate for applications that use '--version' in order to get their version.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public abstract class BaseVersionProcessor extends AbstractProcessorDelegate
{
	// Match x.y and x.y.z
	private static final String VERSION_PATTERN = "(\\d+)\\.(\\d+)(\\.(\\d+))?"; //$NON-NLS-1$
	private static final String VERSION_COMMAND_SYNTAX = "--version"; //$NON-NLS-1$

	/**
	 * Constructs a new BaseVersionProcessor
	 */
	public BaseVersionProcessor()
	{
		// Make sure we add the supported commands
		supportedCommands.put(VERSION_COMMAND, VERSION_COMMAND_SYNTAX);
	}

	/**
	 * Parse the raw output and return a {@link Version} instance out of it.
	 * 
	 * @param rawOutput
	 * @return A {@link Version} instance. Null if the output did not contain a parsable version number.
	 */
	public static Version parseVersion(String rawOutput)
	{
		Pattern pattern = Pattern.compile(VERSION_PATTERN);
		Matcher matcher = pattern.matcher(rawOutput);
		if (matcher.find())
		{
			String version = matcher.group();
			try
			{
				return Version.parseVersion(version);
			}
			catch (IllegalArgumentException iae)
			{
				// Should never happen, since the matcher found it. But just in case.
				PortalUIPlugin.logError("Error parsing the version string - " + version, iae); //$NON-NLS-1$
			}
		}
		return null;
	}
}
