/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.scripting;

import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.scripting.ScriptUtils;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.ContextContributor;

public class BrowserContextContributor implements ContextContributor
{

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
