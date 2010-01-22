package com.aptana.scripting;

import org.jruby.Ruby;
import org.jruby.RubyProc;
import org.jruby.javasupport.JavaEmbedUtils;
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
	 * @see com.aptana.scripting.model.ContextContributor#modifyContext(com.aptana.scripting.model.CommandElement,
	 * com.aptana.scripting.model.CommandContext)
	 */
	@Override
	public void modifyContext(CommandElement command, CommandContext context)
	{
		Ruby runtime = null;

		if (command != null)
		{
			RubyProc proc = command.getInvokeBlock();

			if (proc != null)
			{
				runtime = proc.getRuntime();
			}
		}

		if (runtime != null)
		{
			IRubyObject rubyInstance = ScriptUtils.instantiateClass(runtime, ScriptUtils.RADRAILS_MODULE,
					COMMAND_RUBY_CLASS, JavaEmbedUtils.javaToRuby(runtime, command));

			context.put(COMMAND_PROPERTY_NAME, rubyInstance);

			BundleElement bundle = command.getOwningBundle();

			if (bundle != null)
			{
				rubyInstance = ScriptUtils.instantiateClass(runtime, ScriptUtils.RADRAILS_MODULE, BUNDLE_RUBY_CLASS,
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
