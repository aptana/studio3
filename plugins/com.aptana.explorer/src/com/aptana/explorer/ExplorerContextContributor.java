package com.aptana.explorer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

import com.aptana.scripting.ScriptUtils;
import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;
import com.aptana.scripting.model.ContextContributor;

public class ExplorerContextContributor implements ContextContributor
{
	private static final String PROJECT_PROPERTY_NAME = "project";
	private static final String PROJECT_RUBY_CLASS = "Project";

	/**
	 * ExplorerContextContributor
	 */
	public ExplorerContextContributor()
	{
	}

	/**
	 * getActiveProject
	 * 
	 * @return
	 */
	private IProject getActiveProject()
	{
		IPreferencesService preferencesService = Platform.getPreferencesService();
		String activeProjectName = preferencesService.getString(ExplorerPlugin.PLUGIN_ID,
				IPreferenceConstants.ACTIVE_PROJECT, null, null);
		IProject result = null;

		if (activeProjectName != null)
		{
			result = ResourcesPlugin.getWorkspace().getRoot().getProject(activeProjectName);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.ContextContributor#modifyContext(com.aptana.scripting.model.CommandElement,
	 * com.aptana.scripting.model.CommandContext)
	 */
	@Override
	public void modifyContext(CommandElement command, CommandContext context)
	{
		IProject project = this.getActiveProject();

		if (project != null && command != null)
		{
			Ruby runtime = command.getRuntime();

			if (runtime != null)
			{
				IRubyObject rubyInstance = ScriptUtils.instantiateClass(runtime, ScriptUtils.RADRAILS_MODULE,
						PROJECT_RUBY_CLASS, JavaEmbedUtils.javaToRuby(runtime, project));

				context.put(PROJECT_PROPERTY_NAME, rubyInstance);
			}
			else
			{
				context.put(PROJECT_PROPERTY_NAME, null);
			}
		}
	}
}
