package com.aptana.git.ui.internal.actions;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;

import com.aptana.git.ui.actions.AddRemoteAction;
import com.aptana.git.ui.actions.DeleteBranchAction;
import com.aptana.git.ui.actions.GithubNetworkAction;
import com.aptana.git.ui.actions.MergeBranchAction;
import com.aptana.ui.UIUtils;

public class GitActionHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IAction action = getAction(event);
		if (action == null)
		{
			return null;
		}
		setupAction(event, action);
		Object trigger = event.getTrigger();
		if (trigger instanceof Event)
		{
			action.runWithEvent((Event) trigger);
		}
		else
		{
			action.run();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	protected void setupAction(ExecutionEvent event, IAction action)
	{
		if (action instanceof IActionDelegate)
		{
			IActionDelegate d = (IActionDelegate) action;

			Object context = event.getApplicationContext();
			if (context instanceof IEvaluationContext)
			{
				IEvaluationContext duh = (IEvaluationContext) context;
				Object var = duh.getDefaultVariable();
				StructuredSelection selection = null;
				if (var instanceof List)
				{
					selection = new StructuredSelection((List) var);
				}
				else
				{
					selection = new StructuredSelection(var);
				}
				d.selectionChanged(action, selection);
			}
		}
		if (action instanceof IObjectActionDelegate)
		{
			IObjectActionDelegate del = (IObjectActionDelegate) action;
			del.setActivePart(action, UIUtils.getActivePart());
		}
	}

	protected IAction getAction(ExecutionEvent event)
	{
		Command command = event.getCommand();
		String commandId = command.getId();
		IAction action = null;
		if (commandId.startsWith("com.aptana.git.ui.command."))
		{
			String[] parts = commandId.split("\\.");
			String lastPart = parts[parts.length - 1];
			if (lastPart.equals("blame"))
			{
				return new BlameAction();
			}
			if (lastPart.equals("compare_with_HEAD"))
			{
				return new CompareWithHEADAction();
			}
			if (lastPart.equals("compare_with_revision"))
			{
				return new CompareWithRevisionAction();
			}
			if (lastPart.equals("merge_conflicts"))
			{
				return new MergeConflictsAction();
			}
			if (lastPart.equals("add_remote"))
			{
				return new AddRemoteAction();
			}
			if (lastPart.equals("create_branch"))
			{
				return new CreateBranchAction();
			}
			if (lastPart.equals("switch_branch"))
			{
				return new SwitchBranchAction();
			}
			if (lastPart.equals("merge_branch"))
			{
				return new MergeBranchAction();
			}
			if (lastPart.equals("delete_branch"))
			{
				return new DeleteBranchAction();
			}
			if (commandId.equals("com.aptana.git.ui.command.github.network"))
			{
				return new GithubNetworkAction();
			}

			lastPart = Character.toUpperCase(lastPart.charAt(0)) + lastPart.substring(1);
			try
			{
				Class klazz = Class.forName("com.aptana.git.ui.actions." + lastPart + "Action");
				return (IAction) klazz.newInstance();
			}
			catch (Exception e)
			{
				// ignore
			}
		}
		return action;
	}

}
