package com.aptana.scripting;

import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.scripting.model.BundleElement;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.ContextContributor;

public class BundleContextContributor implements ContextContributor
{
	private static final String BUNDLE_PROPERTY_NAME = "bundle";
	private static final String BUNDLE_RUBY_CLASS = "Bundle";
	private static final String COMMAND_PROPERTY_NAME = "command";
	private static final String COMMAND_RUBY_CLASS = "Command";
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.ContextContributor#modifyContext(com.aptana.scripting.model.CommandElement, com.aptana.scripting.model.CommandContext)
	 */
	@Override
	public void modifyContext(CommandElement command, CommandContext context)
	{
		if (command != null)
		{
			IRubyObject[] args = new IRubyObject[] { ScriptUtils.javaToRuby(command) };
			IRubyObject rubyInstance = ScriptUtils.instantiateClass(ScriptUtils.RADRAILS_MODULE, COMMAND_RUBY_CLASS, args);
			
			context.put(COMMAND_PROPERTY_NAME, rubyInstance);
			
			BundleElement bundle = command.getOwningBundle();
			
			if (bundle != null)
			{
				args = new IRubyObject[] { ScriptUtils.javaToRuby(bundle) };
				rubyInstance = ScriptUtils.instantiateClass(ScriptUtils.RADRAILS_MODULE, BUNDLE_RUBY_CLASS, args);
				
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
