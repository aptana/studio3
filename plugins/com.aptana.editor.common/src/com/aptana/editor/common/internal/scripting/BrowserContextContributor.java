package com.aptana.editor.common.internal.scripting;

import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.scripting.ScriptUtils;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.ContextContributor;

public class BrowserContextContributor implements ContextContributor
{

	@Override
	public void modifyContext(CommandElement command, CommandContext context)
	{
		if (command != null)
		{
			Ruby runtime = command.getRuntime();
			if (runtime != null)
			{
				IRubyObject rubyInstance = ScriptUtils.instantiateClass(runtime, ScriptUtils.RUBLE_MODULE, "Browser"); //$NON-NLS-1$
				context.put("browser", rubyInstance); //$NON-NLS-1$
			}
			else
			{
				context.put("browser", null); //$NON-NLS-1$
			}
		}
	}

}
