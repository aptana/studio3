package com.aptana.js.debug.ui.internal.actions;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RegistryToggleState;

import com.aptana.js.debug.core.ILaunchConfigurationConstants;
import com.aptana.js.debug.core.JSDebugPlugin;
import com.aptana.js.debug.core.model.IJSDebugTarget;
import com.aptana.js.debug.core.preferences.IJSDebugPreferenceNames;

public class SuspendOnUncaughtExceptionsHandler extends AbstractSuspendHandler
{

	static final String COMMAND_ID = "com.aptana.js.debug.ui.suspendOnUncaughtExceptions";

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Command command = event.getCommand();
		HandlerUtil.toggleCommandState(command);
		IEvaluationContext evaluationContext = (IEvaluationContext) event.getApplicationContext();
		IJSDebugTarget jsDebugTarget = getDebugTarget(evaluationContext);
		// Try toggling on current debug target....
		if (jsDebugTarget != null)
		{
			boolean current = Boolean.valueOf(jsDebugTarget
					.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_UNCAUGHT_EXCEPTIONS));
			jsDebugTarget.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_UNCAUGHT_EXCEPTIONS,
					Boolean.toString(!current));
			return !current;
		}
		// If we couldn't, toggle in preferences
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(JSDebugPlugin.PLUGIN_ID);
		boolean current = prefs.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_UNCAUGHT_EXCEPTIONS, false);
		prefs.putBoolean(IJSDebugPreferenceNames.SUSPEND_ON_UNCAUGHT_EXCEPTIONS, !current);

		return !current;
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		boolean allExceptionsEnabled = false;
		boolean uncaughtExceptionsEnabled = false;
		// disable if suspend on all exceptions is true!
		if (evaluationContext instanceof IEvaluationContext)
		{
			IEvaluationContext context = (IEvaluationContext) evaluationContext;
			allExceptionsEnabled = getDebugTargetAttributeOrPreference(context,
					ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_ALL_EXCEPTIONS,
					IJSDebugPreferenceNames.SUSPEND_ON_ALL_EXCEPTIONS);
			uncaughtExceptionsEnabled = getDebugTargetAttributeOrPreference(context,
					ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_UNCAUGHT_EXCEPTIONS,
					IJSDebugPreferenceNames.SUSPEND_ON_UNCAUGHT_EXCEPTIONS);

			// set toggle value to match boolean value
			Command command = getCommand(COMMAND_ID, context);
			State state = command.getState(RegistryToggleState.STATE_ID);
			state.setValue(uncaughtExceptionsEnabled);
		}

		// disable this if all exceptions enabled
		setBaseEnabled(!allExceptionsEnabled);
	}

}
