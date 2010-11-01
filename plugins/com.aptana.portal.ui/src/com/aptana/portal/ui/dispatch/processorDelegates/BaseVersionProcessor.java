/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
