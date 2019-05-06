package com.aptana.js.debug.ui.internal.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.commands.ICommandService;

import com.aptana.js.debug.core.JSDebugPlugin;
import com.aptana.js.debug.core.model.IJSDebugTarget;

abstract class AbstractSuspendHandler extends AbstractHandler
{

	protected IJSDebugTarget getDebugTarget(IEvaluationContext context)
	{
		Object debugContext = context.getVariable(IDebugUIConstants.DEBUG_CONTEXT_SOURCE_NAME);
		if (debugContext instanceof IStructuredSelection)
		{
			IStructuredSelection structuredSelection = (IStructuredSelection) debugContext;
			Object target = structuredSelection.getFirstElement();
			if (target instanceof IJSDebugTarget)
			{
				return (IJSDebugTarget) target;
			}
		}
		return null;
	}

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

	protected boolean getDebugTargetAttributeOrPreference(IEvaluationContext context, String attributeKey,
			String prefKey)
	{
		IJSDebugTarget debugTarget = getDebugTarget(context);
		if (debugTarget != null)
		{
			String string = debugTarget.getAttribute(attributeKey);
			if (string != null)
			{
				return Boolean.valueOf(string);
			}
		}

		// no debug target, get from prefs
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(JSDebugPlugin.PLUGIN_ID);
		return prefs.getBoolean(prefKey, false);
	}

}
