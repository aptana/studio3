package com.aptana.git.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.IAction;

import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.actions.GitAction;
import com.aptana.git.ui.internal.actions.Messages;

public class PullAction extends GitAction
{

	@Override
	protected String[] getCommand()
	{
		return new String[] { "pull" }; //$NON-NLS-1$
	}

	protected void execute(IAction action) throws InvocationTargetException, InterruptedException
	{
		super.execute(action);
		refreshAffectedProjects();
	}
}
