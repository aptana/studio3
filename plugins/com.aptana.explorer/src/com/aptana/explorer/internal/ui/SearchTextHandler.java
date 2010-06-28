package com.aptana.explorer.internal.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * This handles the Cut, Copy, Paste and SelectAll commands in the search in project text field.
 *
 * @author schitale
 */
public class SearchTextHandler extends AbstractHandler implements IExecutableExtension
{
	private String commandId;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (commandId != null)
		{
			EvaluationContext evaluationContext = (EvaluationContext) event.getApplicationContext();
			Object focusControlObject = evaluationContext.getVariable(ISources.ACTIVE_FOCUS_CONTROL_NAME);
			if (focusControlObject instanceof Text)
			{
				Text focusControl = (Text) focusControlObject;
				IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
				if (commandId.equals(ActionFactory.CUT.create(window).getActionDefinitionId()))
				{
					focusControl.cut();
				}
				else if (commandId.equals(ActionFactory.COPY.create(window).getActionDefinitionId()))
				{
					focusControl.copy();
				}
				else if (commandId.equals(ActionFactory.PASTE.create(window).getActionDefinitionId()))
				{
					focusControl.paste();
				}
				else if (commandId.equals(ActionFactory.SELECT_ALL.create(window).getActionDefinitionId()))
				{
					focusControl.selectAll();
				}
			}
		}
		return null;
	}

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException
	{
		// The data is really just a string (i.e., the commandId).
		commandId = data.toString();
	}

}
