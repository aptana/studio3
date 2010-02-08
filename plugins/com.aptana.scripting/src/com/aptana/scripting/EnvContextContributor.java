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

public class EnvContextContributor implements ContextContributor, EnvironmentContributor
{

	// FIXME There was a class that held common env vars before...
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
		// TODO Is there an equivalent for Linux/Windows?
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

		return map;
	}

}
