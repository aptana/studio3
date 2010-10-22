package com.aptana.git.ui.internal.actions;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.team.core.RepositoryProvider;

public class DisconnectHandler extends AbstractGitHandler
{

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		Set<IProject> projects = new HashSet<IProject>();
		for (IResource resource : getSelectedResources())
		{
			IProject project = resource.getProject();
			projects.add(project);
		}

		for (final IProject project : projects)
		{
			Job job = new WorkspaceJob(MessageFormat.format("Disconnecting {0}", project.getName()))
			{

				@Override
				public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
				{
					SubMonitor sub = SubMonitor.convert(monitor, 100);

					// Set back all the team privates to false!
					project.accept(new IResourceProxyVisitor()
					{

						public boolean visit(IResourceProxy proxy) throws CoreException
						{
							if (proxy.isTeamPrivateMember())
							{
								proxy.requestResource().setTeamPrivateMember(false);
							}
							return true;
						}
					}, IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS);
					sub.worked(20);

					RepositoryProvider.unmap(project);
					sub.worked(10);

					getGitRepositoryManager().removeRepository(project);
					sub.worked(10);

					project.refreshLocal(IResource.DEPTH_INFINITE, sub.newChild(60));

					sub.done();

					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
		return null;
	}
}
