package com.aptana.scripting;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.core.runtime.Platform;

import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.ContextContributor;
import com.aptana.scripting.model.EnvironmentContributor;
import com.aptana.util.ProcessUtil;

public class FullnameEnvContextContributor implements ContextContributor, EnvironmentContributor
{

	private static final String TM_FULLNAME = "TM_FULLNAME"; //$NON-NLS-1$

	@Override
	public void modifyContext(CommandElement command, CommandContext context)
	{
		context.put(System.currentTimeMillis() + "_env", this); //$NON-NLS-1$
	}

	@Override
	public Map<String, String> toEnvironment()
	{
		Map<String, String> map = new HashMap<String, String>();
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
			String username = System.getenv("USERNAME"); //$NON-NLS-1$
			if (username != null && username.trim().length() > 0)
			{
				map.put(TM_FULLNAME, username);
			}
		}
		// http://stackoverflow.com/questions/833227/whats-the-easiest-way-to-get-a-users-full-name-on-a-linux-posix-system
		// else if (Platform.getOS().equals(Platform.OS_LINUX))
		else
		{
			String username = System.getProperty("user.name"); //$NON-NLS-1$
			if (username != null && username.trim().length() > 0)
			{
				String cmdLine = "getent passwd \"" + username + "\" | cut -d ':' -f 5 | cut -d ',' -f 1"; //$NON-NLS-1$ //$NON-NLS-2$
				Map<Integer, String> result = ProcessUtil.runInBackground(cmdLine, null, new String[0]);
				if (result != null && result.keySet().iterator().next() == 0)
				{
					map.put(TM_FULLNAME, result.values().iterator().next());
				}
			}
		}

		return map;
	}

}
