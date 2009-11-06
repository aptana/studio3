package com.aptana.git.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.action.IAction;

import com.aptana.git.ui.internal.actions.GitAction;

public class StashAction extends GitAction
{

	private static final String COMMAND = "stash"; //$NON-NLS-1$

	@Override
	protected String[] getCommand()
	{
		return new String[] { COMMAND };
	}

	@Override
	protected void execute(IAction action) throws InvocationTargetException, InterruptedException
	{
		super.execute(action);

		refreshAffectedProjects();
	}

	// TODO Only enable if there are staged or unstaged files (but not untracked/new ones!)
	
}
