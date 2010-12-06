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
package com.aptana.scripting;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ProcessUtil;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.ContextContributor;
import com.aptana.scripting.model.EnvironmentContributor;

public class FullnameEnvContextContributor implements ContextContributor, EnvironmentContributor
{

	private static final String TM_FULLNAME = "TM_FULLNAME"; //$NON-NLS-1$
	/**
	 * A static cache for the environment map by user.name, so that we don't have to run processes on *nix or Mac every
	 * time we populate the command context (since if the user.name is the same the user probably hasn't changed their
	 * full name).
	 */
	private Map<String, Map<String, String>> fgCache = new HashMap<String, Map<String, String>>(3);

	public void modifyContext(CommandElement command, CommandContext context)
	{
		context.put(System.currentTimeMillis() + "_env", this); //$NON-NLS-1$
	}

	public Map<String, String> toEnvironment()
	{
		String username = System.getProperty("user.name"); //$NON-NLS-1$
		if (username == null)
			username = ""; //$NON-NLS-1$
		Map<String, String> map = fgCache.get(username);
		if (map != null)
			return map;

		map = new HashMap<String, String>();
		// If we're on Mac, grab the full user name via applescript and stuff it in the TM_FULLNAME env var
		if (Platform.getOS().equals(Platform.OS_MACOSX))
		{
			String appleScript = "do shell script \"echo \" & the long user name of (system info)"; //$NON-NLS-1$
			String output = ProcessUtil.outputForCommand("osascript", null, "-e", appleScript); //$NON-NLS-1$ //$NON-NLS-2$
			if (output != null)
			{
				String[] lines = output.split("\r|\n|\r\n"); //$NON-NLS-1$
				map.put(TM_FULLNAME, lines[lines.length - 1]);
			}
		}
		// Seems like %USERNAME% typically holds the full name of the user now on Windows
		else if (Platform.getOS().equals(Platform.OS_WIN32))
		{
			String fullusername = System.getenv("USERNAME"); //$NON-NLS-1$
			if (fullusername != null && fullusername.trim().length() > 0)
			{
				map.put(TM_FULLNAME, fullusername);
			}
		}
		// http://stackoverflow.com/questions/833227/whats-the-easiest-way-to-get-a-users-full-name-on-a-linux-posix-system
		// else if (Platform.getOS().equals(Platform.OS_LINUX))
		else
		{
			if (username.trim().length() > 0)
			{
				try
				{
					Process p = ProcessUtil.run("/usr/bin/getent", null, "passwd", username); //$NON-NLS-1$ //$NON-NLS-2$
					String read = IOUtil.read(p.getInputStream(), "UTF-8"); //$NON-NLS-1$
					String raw = read.split(":")[4]; //$NON-NLS-1$
					String fullname = raw.split(",")[0]; //$NON-NLS-1$
					map.put(TM_FULLNAME, fullname);
				}
				catch (Throwable e)
				{
					// ignore
				}
			}
		}
		fgCache.put(username, map);
		return map;
	}

}
