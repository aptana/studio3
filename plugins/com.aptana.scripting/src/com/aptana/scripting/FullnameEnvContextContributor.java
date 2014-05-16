/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.IProcessRunner;
import com.aptana.core.util.ProcessRunner;
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
					Process p = createProcessRunner().run("/usr/bin/getent", "passwd", username); //$NON-NLS-1$ //$NON-NLS-2$
					String read = IOUtil.read(p.getInputStream(), IOUtil.UTF_8);
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

	protected IProcessRunner createProcessRunner()
	{
		return new ProcessRunner();
	}

}
