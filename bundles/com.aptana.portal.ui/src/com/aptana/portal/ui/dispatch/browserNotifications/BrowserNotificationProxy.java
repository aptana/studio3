/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.portal.ui.dispatch.browserNotifications;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.logging.IdeLog;
import com.aptana.portal.ui.PortalUIPlugin;

/**
 * Proxy class to the contributors of Browser notifications.
 * 
 * @author pinnamuri
 */
public class BrowserNotificationProxy extends AbstractBrowserNotification
{

	private IConfigurationElement element;
	private AbstractBrowserNotification contributorClass;
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$
	private static final String ATT_NOTIFICATION_TARGET = "notificationTarget"; //$NON-NLS-1$

	public BrowserNotificationProxy(IConfigurationElement element)
	{
		this.element = element;
	}

	@Override
	public void start()
	{
		try
		{
			loadElement();
			contributorClass.start();

		}
		catch (CoreException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
		}
	}

	@Override
	public List<String> getNotificationTargets()
	{
		try
		{
			loadElement();
			return contributorClass.getNotificationTargets();

		}
		catch (CoreException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
		}
		return Collections.emptyList();
	}

	@Override
	public void setNotificationTargets(String targets)
	{
		try
		{
			loadElement();
			contributorClass.setNotificationTargets(targets);
		}
		catch (CoreException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
		}

	}

	private synchronized void loadElement() throws CoreException
	{
		if (contributorClass == null)
		{
			contributorClass = (AbstractBrowserNotification) element.createExecutableExtension(ATT_CLASS);
			contributorClass.setNotificationTargets(element.getAttribute(ATT_NOTIFICATION_TARGET));
		}
	}

	@Override
	protected void notifyTargets(String eventId, String eventType, String data)
	{
		try
		{
			loadElement();
			contributorClass.notifyTargets(eventId, eventType, data);
		}
		catch (CoreException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
		}
	}

	@Override
	protected void notifyTargets(String eventId, String eventType, String data, boolean notifyInUIThread)
	{
		try
		{
			loadElement();
			contributorClass.notifyTargets(eventId, eventType, data, notifyInUIThread);
		}
		catch (CoreException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
		}

	}

	@Override
	public void stop()
	{
		try
		{
			loadElement();
			contributorClass.stop();

		}
		catch (CoreException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
		}
	}

}
