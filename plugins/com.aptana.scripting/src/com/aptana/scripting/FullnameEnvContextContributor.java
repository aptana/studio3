package com.aptana.scripting;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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

	@Override
	public void modifyContext(CommandElement command, CommandContext context)
	{
		context.put(System.currentTimeMillis() + "_env", this); //$NON-NLS-1$
	}

	@Override
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
			try
			{
				String appleScript = "set myName to the long user name of (system info)\nreturn myName"; //$NON-NLS-1$
				ScriptEngineManager mgr = new ScriptEngineManager();
				ScriptEngine engine = mgr.getEngineByName("AppleScript"); //$NON-NLS-1$
				Object ret = engine.eval(appleScript);
				if (ret != null)
				{
					map.put(TM_FULLNAME, ret.toString());
				}
			}
			catch (ScriptException e)
			{
				Activator.logError(e.getMessage(), e);
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
					Process p = ProcessUtil.run("/usr/bin/getent", null, "passwd", username);
					String read = IOUtil.read(p.getInputStream(), "UTF-8");
					String raw = read.split(":")[4];
					String fullname = raw.split(",")[0];
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
