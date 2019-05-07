package com.aptana.js.debug.ui.internal.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.commands.ICommandService;

import com.aptana.js.debug.core.JSDebugPlugin;

abstract class AbstractSuspendHandler extends AbstractHandler
{

	protected ICommandService getCommandService(IEvaluationContext context)
	{
		IWorkbenchSite site = (IWorkbenchSite) context.getVariable(ISources.ACTIVE_SITE_NAME);
		if (site == null)
		{
			return null;
		}
		return (ICommandService) site.getService(ICommandService.class);
	}

	protected Command getCommand(String id, IEvaluationContext context)
	{
		ICommandService commandService = getCommandService(context);
		if (commandService == null)
		{
			return null;
		}
		return commandService.getCommand(id);
	}

	protected boolean getPreferenceValue(IEvaluationContext context, String prefKey)
	{
		// no debug target, get from prefs
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(JSDebugPlugin.PLUGIN_ID);
		return prefs.getBoolean(prefKey, false);
	}

}
