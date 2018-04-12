/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.wizards;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;

public abstract class AbstractProjectWizardContributor implements IProjectWizardContributor
{
	public static final String ATTRIBUTE_NATURE_ID = "natureId"; //$NON-NLS-1$
	private String natureId = StringUtil.EMPTY;

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException
	{
		natureId = config.getAttribute(ATTRIBUTE_NATURE_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.projects.wizards.IProjectWizardContributor#hasNatureId(java.lang.String[])
	 */
	public boolean hasNatureId(String[] natureIds)
	{
		if (ArrayUtil.isEmpty(natureIds))
		{
			return false;
		}

		return CollectionsUtil.newList(natureIds).contains(natureId);
	}

	public IStatus performWizardFinish(IProject project, IProgressMonitor monitor)
	{
		return Status.OK_STATUS;
	}

	@SuppressWarnings("unchecked")
	public List<String> getArguments()
	{
		return Collections.EMPTY_LIST;
	}

	public void finalizeWizardPage(IWizardPage page)
	{
		// No-op
	}

	public void appendProjectCreationPage(Object data, IWizardPage page, Composite parent)
	{
		// No-op
	}

	public void appendSampleProjectCreationPage(Object data, IWizardPage page, Composite parent)
	{
		// No-op
	}

	public IStatus validateProjectCreationPage(Object data)
	{
		return Status.OK_STATUS;
	}
}
