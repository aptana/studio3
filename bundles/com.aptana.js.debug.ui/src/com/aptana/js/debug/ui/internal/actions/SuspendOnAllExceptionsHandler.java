package com.aptana.js.debug.ui.internal.actions;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RegistryToggleState;
import org.eclipse.ui.services.IEvaluationService;

import com.aptana.js.debug.core.JSDebugPlugin;
import com.aptana.js.debug.core.preferences.IJSDebugPreferenceNames;

public class SuspendOnAllExceptionsHandler extends AbstractSuspendHandler
{

	static final String COMMAND_ID = "com.aptana.js.debug.ui.suspendOnAllExceptions";

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Command command = event.getCommand();
		HandlerUtil.toggleCommandState(command);
		IEvaluationContext evaluationContext = (IEvaluationContext) event.getApplicationContext();
		// toggle in preferences
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(JSDebugPlugin.PLUGIN_ID);
		boolean current = prefs.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_ALL_EXCEPTIONS, false);
		prefs.putBoolean(IJSDebugPreferenceNames.SUSPEND_ON_ALL_EXCEPTIONS, !current);

		triggerReevaluationOfUncaughtExceptionCommand(evaluationContext);

		return !current;
	}

	/**
	 * Causes the "suspend on uncaught exceptions" handler's enablement to get re-evaluated (also likely triggers a lot
	 * of other commands to get re-evaluated since this is a pretty brute force way to do this)
	 * 
	 * @param evaluationContext
	 */
	private void triggerReevaluationOfUncaughtExceptionCommand(IEvaluationContext evaluationContext)
	{
		IWorkbenchSite site = (IWorkbenchSite) evaluationContext.getVariable(ISources.ACTIVE_SITE_NAME);
		IEvaluationService evalservice = site.getService(IEvaluationService.class);
		evalservice.requestEvaluation(ISources.ACTIVE_CURRENT_SELECTION_NAME);
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		boolean allExceptionsEnabled = false;
		if (evaluationContext instanceof IEvaluationContext)
		{
			IEvaluationContext context = (IEvaluationContext) evaluationContext;
			allExceptionsEnabled = getPreferenceValue(context, IJSDebugPreferenceNames.SUSPEND_ON_ALL_EXCEPTIONS);

			// set toggle value to match boolean value
			Command command = getCommand(COMMAND_ID, context);
			if (command != null)
			{
				State state = command.getState(RegistryToggleState.STATE_ID);
				state.setValue(allExceptionsEnabled);
			}
		}
		super.setEnabled(evaluationContext);
	}

}
