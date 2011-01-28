/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting;

import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.ContextContributor;

public class BundleContextContributor implements ContextContributor
{
	private static final String BUNDLE_PROPERTY_NAME = "bundle"; //$NON-NLS-1$
	private static final String BUNDLE_RUBY_CLASS = "Bundle"; //$NON-NLS-1$
	private static final String COMMAND_PROPERTY_NAME = "command"; //$NON-NLS-1$
	private static final String COMMAND_RUBY_CLASS = "Command"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.ContextContributor#modifyContext(com.aptana.scripting.model.CommandElement,
	 * com.aptana.scripting.model.CommandContext)
	 */
	public void modifyContext(CommandElement command, CommandContext context)
	{
		if (command != null)
		{
			Ruby runtime = command.getRuntime();
			
			if (runtime != null)
			{
				IRubyObject rubyInstance = ScriptUtils.instantiateClass(runtime, ScriptUtils.RUBLE_MODULE,
						COMMAND_RUBY_CLASS, JavaEmbedUtils.javaToRuby(runtime, command));
				
				context.put(COMMAND_PROPERTY_NAME, rubyInstance);
				
				BundleElement bundle = command.getOwningBundle();
				
				if (bundle != null)
				{
					rubyInstance = ScriptUtils.instantiateClass(runtime, ScriptUtils.RUBLE_MODULE, BUNDLE_RUBY_CLASS,
							JavaEmbedUtils.javaToRuby(runtime, bundle));
					
					context.put(BUNDLE_PROPERTY_NAME, rubyInstance);
				}
				else
				{
					context.put(BUNDLE_PROPERTY_NAME, null);
				}
			}
			else
			{
				context.put(COMMAND_PROPERTY_NAME, null);
				context.put(BUNDLE_PROPERTY_NAME, null);
			}
		}
	}
}
