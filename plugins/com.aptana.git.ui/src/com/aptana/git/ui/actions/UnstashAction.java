package com.aptana.git.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.IAction;

import com.aptana.git.ui.internal.actions.GitAction;

public class UnstashAction extends GitAction
{

	@Override
	protected String[] getCommand()
	{
		return new String[] { "stash", "apply" }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected void execute(IAction action) throws InvocationTargetException, InterruptedException
	{
		super.execute(action);

		refreshAffectedProjects();
	}

	// TODO Only enable if there's a "ref/stash" ref!
}
