/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.preferences;

import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.UIPlugin;

public abstract class AbstractAccountPageProvider implements IAccountPageProvider, IExecutableExtension
{

	private static final String ATTR_PRIORITY = "priority"; //$NON-NLS-1$

	private int priority = 50;
	private Set<IValidationListener> validationListeners;
	private IProgressMonitor progressMonitor;

	public AbstractAccountPageProvider()
	{
		this(new NullProgressMonitor());
	}

	public AbstractAccountPageProvider(IProgressMonitor progressMonitor)
	{
		validationListeners = new LinkedHashSet<IValidationListener>();
		setProgressMonitor(progressMonitor);
	}

	public void addValidationListener(IValidationListener listener)
	{
		validationListeners.add(listener);
	}

	public void removeValidationListener(IValidationListener listener)
	{
		validationListeners.remove(listener);
	}

	public int getPriority()
	{
		return priority;
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException
	{
		String priorityStr = config.getAttribute(ATTR_PRIORITY);
		if (!StringUtil.isEmpty(priorityStr))
		{
			try
			{
				priority = Integer.parseInt(priorityStr);
			}
			catch (NumberFormatException e)
			{
				IdeLog.logWarning(
						UIPlugin.getDefault(),
						MessageFormat
								.format("Unable to parse the priority value ({0}) for the account page provider as an integer; defaulting to 50", //$NON-NLS-1$
										priorityStr));
			}
		}
	}

	public void setProgressMonitor(IProgressMonitor progressMonitor)
	{
		this.progressMonitor = (progressMonitor == null) ? new NullProgressMonitor() : progressMonitor;
	}

	protected IProgressMonitor getProgressMonitor()
	{
		return progressMonitor;
	}

	protected void firePreValidationStartEvent()
	{
		for (IValidationListener listener : validationListeners)
		{
			listener.preValidationStart();
		}
	}

	protected void firePostValidationEndEvent()
	{
		for (IValidationListener listener : validationListeners)
		{
			listener.postValidationEnd();
		}
	}
}
