/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.portal.ui.dispatch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.portal.ui.PortalUIPlugin;

/**
 * Proxy class to the contributors of Action Controller.
 * 
 * @author pinnamuri
 */
public class ActionControllerProxy implements IActionController
{

	private IConfigurationElement element;
	private IActionController contributorClass;
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$
	private static final String ATT_CONFGURATION_PROCESSOR_ID = "configurationProcessor"; //$NON-NLS-1$

	public ActionControllerProxy(IConfigurationElement element)
	{
		this.element = element;
	}

	private synchronized void loadElement() throws CoreException
	{
		if (contributorClass == null)
		{
			contributorClass = (IActionController) element.createExecutableExtension(ATT_CLASS);
			// Set the value to the contributor immediately after it is instantiated.
			contributorClass.setConfigurationProcessorId(element.getAttribute(ATT_CONFGURATION_PROCESSOR_ID));
		}
	}

	public String[] getActions()
	{
		try
		{
			loadElement();
			return contributorClass.getActions();
		}
		catch (CoreException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
		}
		return new String[0];
	}

	public boolean hasAction(String action)
	{
		try
		{
			loadElement();
			return contributorClass.hasAction(action);
		}
		catch (CoreException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
		}
		return false;
	}

	public Object invokeAction(String action, Object args)
	{
		try
		{
			loadElement();
			return contributorClass.invokeAction(action, args);
		}
		catch (CoreException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
		}
		return null;
	}

	public void setConfigurationProcessorId(String id)
	{
		try
		{
			loadElement();
			contributorClass.setConfigurationProcessorId(id);
		}
		catch (CoreException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
		}
	}

	public String getConfigurationProcessorId()
	{
		try
		{
			loadElement();
			return contributorClass.getConfigurationProcessorId();
		}
		catch (CoreException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
		}
		return StringUtil.EMPTY;
	}
}
