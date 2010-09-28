/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;

import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.preferences.IPreferenceConstants.DeployType;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.ResourceSynchronizationUtils;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.ui.actions.SynchronizeProjectAction;
import com.aptana.terminal.views.TerminalView;

public class DeployHandler extends AbstractHandler
{

	private IProject selectedProject;

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		
		DeployType type = DeployPreferenceUtil.getDeployType(selectedProject);
		
		if (type == null)
		{
			if (isCapistranoProject(selectedProject))
			{
				deployWithCapistrano();
			}
			else if (selectedProject != null && isFTPProject(selectedProject))
			{
				deployWithFTP();
			}
			else if (selectedProject != null && isHerokuProject(selectedProject))
			{
				deployWithHeroku();
			}
			else if (selectedProject != null && isEYProject(selectedProject))
			{
				deployWithEngineYard();
			}
		}
		else if (type == DeployType.HEROKU)
		{
			deployWithHeroku();
		}
		else if (type == DeployType.FTP)
		{
			deployWithFTP();
		}
		else if (type == DeployType.CAPISTRANO)
		{
			deployWithCapistrano();
		}
		else if (isEYProject(selectedProject))
		{
			deployWithEngineYard();
		}
		return null;
	}

	@Override
	public boolean isEnabled()
	{
		return selectedProject != null && selectedProject.isAccessible();
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		selectedProject = null;
		if (evaluationContext instanceof EvaluationContext)
		{
			Object value = ((EvaluationContext) evaluationContext).getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
			if (value instanceof ISelection)
			{
				ISelection selections = (ISelection) value;
				if (!selections.isEmpty() && selections instanceof IStructuredSelection)
				{
					Object selection = ((IStructuredSelection) selections).getFirstElement();
					IResource resource = null;
					if (selection instanceof IResource)
					{
						resource = (IResource) selection;
					}
					else if (selection instanceof IAdaptable)
					{
						resource = (IResource) ((IAdaptable) selection).getAdapter(IResource.class);
					}
					if (resource != null)
					{
						selectedProject = resource.getProject();
					}
				}
			}
		}
	}

	private void deployWithCapistrano(){
		TerminalView terminal = TerminalView.openView(selectedProject.getName(), selectedProject.getName(),
				selectedProject.getLocation());
		terminal.sendInput("cap deploy\n");
	}
	
	private void deployWithEngineYard(){
		TerminalView terminal = TerminalView.openView(selectedProject.getName(), selectedProject.getName(),
				selectedProject.getLocation());
		terminal.sendInput("ey deploy\n");
	}
	private void deployWithHeroku(){
		TerminalView terminal = TerminalView.openView(selectedProject.getName(), selectedProject.getName(),
				selectedProject.getLocation());
		terminal.sendInput("git push heroku master\n");
	}
	
	private void deployWithFTP(){
		SynchronizeProjectAction action = new SynchronizeProjectAction();
		action.setActivePart(null, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActivePart());
		action.setSelection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
				.getSelection());
		ISiteConnection[] sites = SiteConnectionUtils.findSitesForSource(selectedProject, true);
		if (sites.length > 1)
		{
			String lastConnection = ResourceSynchronizationUtils.getLastSyncConnection(selectedProject);
			if (lastConnection == null)
			{
				lastConnection = DeployPreferenceUtil.getDeployEndpoint(selectedProject);
			}
			if (lastConnection != null)
			{
				action.setSelectedSite(SiteConnectionUtils.getSiteWithDestination(lastConnection, sites));
			}
		}
		action.run(null);
	}
	
	private boolean isEYProject(IProject selectedProject)
	{

		DeployType type = DeployPreferenceUtil.getDeployType(selectedProject);
		
		// Engine Yard gem does not work in Windows
		if(!Platform.getOS().equals(Platform.OS_WIN32))
		{
			if (type.equals(DeployType.ENGINEYARD))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isCapistranoProject(IProject selectedProject)
	{
		return selectedProject.getFile("Capfile").exists(); //$NON-NLS-1$
	}

	private boolean isFTPProject(IProject selectedProject)
	{
		ISiteConnection[] siteConnections = SiteConnectionUtils.findSitesForSource(selectedProject, true);
		return siteConnections.length > 0;
	}

	private boolean isHerokuProject(IProject selectedProject)
	{
		GitRepository repo = GitPlugin.getDefault().getGitRepositoryManager().getAttached(selectedProject);
		if (repo != null)
		{
			for (String remote : repo.remotes())
			{
				if (remote.indexOf("heroku") != -1) //$NON-NLS-1$
				{
					return true;
				}
			}
			for (String remoteURL : repo.remoteURLs())
			{
				if (remoteURL.indexOf("heroku.com") != -1) //$NON-NLS-1$
				{
					return true;
				}
			}
		}
		return false;
	}
}
