/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.browserNotifications;

import java.util.HashMap;
import java.util.Map;

import com.aptana.jetty.util.epl.ajax.JSON;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.projects.templates.IProjectTemplate;
import com.aptana.portal.ui.IDebugScopes;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;
import com.aptana.portal.ui.dispatch.actionControllers.TemplateActionController.TEMPLATE_INFO;
import com.aptana.projects.ProjectsPlugin;
import com.aptana.projects.templates.IProjectTemplateListener;

/**
 * A class that notify the portal browser when a new template is loaded.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class TemplatesNotification extends AbstractBrowserNotification
{

	private IProjectTemplateListener listener;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.portal.ui.dispatch.browserNotifications.AbstractBrowserNotification#start()
	 */
	@Override
	public synchronized void start()
	{
		isListening = true;
		ProjectsPlugin.getDefault().getTemplatesManager().addListener(getListener());
		IdeLog.logInfo(ProjectsPlugin.getDefault(), "Template Portal notifier started", IDebugScopes.START_PAGE); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.portal.ui.dispatch.browserNotifications.AbstractBrowserNotification#stop()
	 */
	@Override
	public synchronized void stop()
	{
		isListening = false;
		ProjectsPlugin.getDefault().getTemplatesManager().removeListener(getListener());
		listener = null;
		IdeLog.logInfo(ProjectsPlugin.getDefault(), "Template Portal notifier stopped", IDebugScopes.START_PAGE); //$NON-NLS-1$
	}

	/**
	 * Notify a template addition
	 * 
	 * @param template
	 */
	protected void notifyAdd(IProjectTemplate template)
	{
		IdeLog.logInfo(ProjectsPlugin.getDefault(), "Template added. Notifying portal...", IDebugScopes.START_PAGE); //$NON-NLS-1$
		notifyTargets(IBrowserNotificationConstants.EVENT_ID_TEMPLATES, IBrowserNotificationConstants.EVENT_TYPE_ADDED,
				createTemplateInfo(template), true);
	}

	/**
	 * Notify a template removal
	 * 
	 * @param template
	 */
	protected void notifyRemoved(IProjectTemplate template)
	{
		IdeLog.logInfo(ProjectsPlugin.getDefault(), "Template removed. Notifying portal...", IDebugScopes.START_PAGE); //$NON-NLS-1$
		notifyTargets(IBrowserNotificationConstants.EVENT_ID_TEMPLATES,
				IBrowserNotificationConstants.EVENT_TYPE_DELETED, createTemplateInfo(template), true);
	}

	/**
	 * Create a JSON template info that will be send as the browser notification data.
	 * 
	 * @param template
	 * @return A JSON representation of the template that was added or removed.
	 */
	protected String createTemplateInfo(IProjectTemplate template)
	{
		Map<String, String> templateInfo = new HashMap<String, String>();
		templateInfo.put(TEMPLATE_INFO.ID.toString(), template.getId());
		templateInfo.put(TEMPLATE_INFO.NAME.toString(), template.getDisplayName());
		templateInfo.put(TEMPLATE_INFO.DESCRIPTION.toString(), template.getDescription());
		templateInfo.put(TEMPLATE_INFO.TEMPLATE_TYPE.toString(), template.getType().name());
		return JSON.toString(templateInfo);
	}

	/**
	 * @return an {@link IProjectTemplateListener}
	 */
	protected synchronized IProjectTemplateListener getListener()
	{
		if (listener == null)
		{
			listener = new IProjectTemplateListener()
			{
				public void templateAdded(IProjectTemplate template)
				{
					if (isListening)
					{
						notifyAdd(template);
					}
				}

				public void templateRemoved(IProjectTemplate template)
				{
					if (isListening)
					{
						notifyRemoved(template);
					}
				}
			};
		}
		return listener;
	}
}
