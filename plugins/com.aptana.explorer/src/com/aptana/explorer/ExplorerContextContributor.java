package com.aptana.explorer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;

import com.aptana.scripting.model.CommandContext;
import com.aptana.scripting.model.CommandElement;

public class ExplorerContextContributor implements
		com.aptana.scripting.model.ContextContributor {

	public ExplorerContextContributor() {
	}

	@Override
	public void modifyContext(CommandElement command, CommandContext context)
	{
		String activeProjectName = Platform.getPreferencesService().getString(ExplorerPlugin.PLUGIN_ID,
				IPreferenceConstants.ACTIVE_PROJECT, null, null);
		
		IProject project = null;
		if (activeProjectName != null)
		{
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(activeProjectName);
		}

		if (project == null)
		{
			return;
		}
		
		if (command.isShellCommand())
		{
			context.putEnvironment(CommandContext.ACTIVE_PROJECT_NAME, activeProjectName);
			context.putEnvironment(CommandContext.ACTIVE_PROJECT_FOLDER, project.getLocation().toOSString());
		}
		else
		{
			context.put(CommandContext.ACTIVE_PROJECT_NAME.toLowerCase(), activeProjectName);
			context.put(CommandContext.ACTIVE_PROJECT_FOLDER.toLowerCase(), project.getLocation().toOSString());
		}
	}

}
