package com.aptana.git.ui.internal.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.IAction;

import com.aptana.git.ui.internal.GitLightweightDecorator;

public class PushAction extends GitAction
{

	private static final String COMMAND = "push";

	@Override
	protected String getCommand()
	{
		return COMMAND;
	}

	@Override
	protected void execute(IAction action) throws InvocationTargetException, InterruptedException
	{
		super.execute(action);

		// TODO It'd be nice if we could just tell it to update the labels of the projects attached to the repo (and only the project, not it's children)!
		GitLightweightDecorator.refresh();
	}

	// TODO Only enable if there are commits in local repo?
}
