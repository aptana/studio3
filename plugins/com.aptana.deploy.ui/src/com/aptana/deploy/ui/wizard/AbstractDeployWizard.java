/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.ui.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

import com.aptana.deploy.ui.DeployUIPlugin;

public abstract class AbstractDeployWizard extends Wizard implements IDeployWizard
{

	private static final ImageDescriptor fgDefaultImage = DeployUIPlugin.imageDescriptorFromPlugin(
			DeployUIPlugin.PLUGIN_ID, "icons/blank.png"); //$NON-NLS-1$

	private IProject project;

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		Object element = selection.getFirstElement();
		IResource resource = null;
		if (element instanceof IResource)
		{
			resource = (IResource) element;
		}
		else if (element instanceof IAdaptable)
		{
			resource = (IResource) ((IAdaptable) element).getAdapter(IResource.class);
		}
		if (resource != null)
		{
			project = resource.getProject();
		}

		setDefaultPageImageDescriptor(fgDefaultImage);
	}

	public IProject getProject()
	{
		return project;
	}
}
