/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;

import com.aptana.deploy.DeployPlugin;
import com.aptana.deploy.IDeployProvider;

public class DeployHandler extends AbstractHandler
{

	/**
	 * Pref key used to store the associated deploy provider for a project
	 */
	private static final String DEPLOY_PROVIDER_ID_PREF_KEY = "deploy_provider_id"; //$NON-NLS-1$

	/**
	 * unique id of the provider.
	 */
	private static final String PROVIDER_ID_ATTRIBUTE = "id"; //$NON-NLS-1$

	/**
	 * Element name to register a deploy provider.
	 */
	private static final String PROVIDER_ELEMENT_NAME = "provider"; //$NON-NLS-1$

	/**
	 * Extension point name/id.
	 */
	private static final String DEPLOY_PROVIDERS_EXP_PT = "deployProviders"; //$NON-NLS-1$

	private IProject selectedProject;

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IDeployProvider provider = getConfiguredProvider(selectedProject);
		if (provider == null)
		{
			// Grab providers from ext pt!
			List<IDeployProvider> providers = getAllProviders();
			// Now go through the providers and find one that handles this project
			for (IDeployProvider aProvider : providers)
			{
				if (aProvider.handles(selectedProject))
				{
					provider = aProvider;
					break;
				}
			}
		}

		// TODO What if provider is still null? Prompt to choose explicitly? Run wizard?
		if (provider != null)
		{
			// TODO Run in a job?
			provider.deploy(selectedProject, new NullProgressMonitor());
		}
		return null;
	}

	private List<IDeployProvider> getAllProviders()
	{
		List<IDeployProvider> providers = new ArrayList<IDeployProvider>();
		try
		{
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IConfigurationElement[] elements = registry.getConfigurationElementsFor(DeployPlugin.getPluginIdentifier(),
					DEPLOY_PROVIDERS_EXP_PT);
			for (IConfigurationElement element : elements)
			{
				if (PROVIDER_ELEMENT_NAME.equals(element.getName()))
				{
					providers.add(createProvider(element));
				}
			}
		}
		catch (InvalidRegistryObjectException e)
		{
			DeployPlugin.logError(e);
		}
		catch (CoreException e)
		{
			DeployPlugin.logError(e);
		}
		return providers;
	}

	private IDeployProvider getConfiguredProvider(IProject project)
	{
		// check what deploy provider id is stored for project, then get provider from ext pt matching that id.
		IEclipsePreferences prefs = new ProjectScope(project).getNode(DeployPlugin.getPluginIdentifier());
		String id = prefs.get(DEPLOY_PROVIDER_ID_PREF_KEY, null);
		if (id == null)
		{
			return null;
		}
		// Now go through registered deploy providers and find one with matching id!
		try
		{
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IConfigurationElement[] elements = registry.getConfigurationElementsFor(DeployPlugin.getPluginIdentifier(),
					DEPLOY_PROVIDERS_EXP_PT);
			for (IConfigurationElement element : elements)
			{
				if (PROVIDER_ELEMENT_NAME.equals(element.getName()))
				{
					String providerId = element.getAttribute(PROVIDER_ID_ATTRIBUTE);
					if (id.equals(providerId))
					{
						return createProvider(element);
					}
				}
			}
		}
		catch (InvalidRegistryObjectException e)
		{
			DeployPlugin.logError(e);
		}
		catch (CoreException e)
		{
			DeployPlugin.logError(e);
		}

		return null;
	}

	private IDeployProvider createProvider(IConfigurationElement element) throws CoreException
	{
		return (IDeployProvider) element.createExecutableExtension("class"); //$NON-NLS-1$
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
}
