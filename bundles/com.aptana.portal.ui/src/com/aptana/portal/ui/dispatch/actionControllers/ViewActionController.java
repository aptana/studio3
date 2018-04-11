/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import java.util.Set;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.core.logging.IdeLog;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * An action controller for opening Studio's Views.<br>
 * Executing example (for JS with Prototype):
 * <code>result = dispatch($H({controller:'portal.views', action:"openView", args : [viewId].toJSON()}).toJSON());</code>
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class ViewActionController extends AbstractActionController
{
	// ############## Actions ###############

	/**
	 * Opens a Studio view.<br>
	 * 
	 * @param attributes
	 *            We expect for an array that contains a single string Id for the view.
	 */
	@ControllerAction
	public Object openView(Object attributes)
	{
		String viewId = getViewId(attributes);
		if (viewId == null)
		{
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		try
		{
			PortalUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewId);
		}
		catch (Exception e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		return IBrowserNotificationConstants.JSON_OK;
	}

	/**
	 * Returns the view-Id from the attributes. Null, if an error occurred.
	 * 
	 * @param attributes
	 * @return A view Id, or null if an error occurs.
	 */
	private String getViewId(Object attributes)
	{
		if (attributes instanceof Object[])
		{
			Object[] arr = (Object[]) attributes;
			if (arr.length == 1 && arr[0] != null)
			{
				return arr[0].toString();
			}
			else
			{
				String message = "Wrong argument count passed to ViewActionController::openView. Expected 1 and got " + arr.length; //$NON-NLS-1$
				IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
			}
		}
		else
		{
			String message = "Wrong argument type passed to ViewActionController::openView. Expected Object[] and got " //$NON-NLS-1$
					+ ((attributes == null) ? "null" : attributes.getClass().getName()); //$NON-NLS-1$
			IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @seecom.aptana.configurations.processor.IConfigurationProcessorListener#configurationStateChanged(com.aptana.
	 * configurations.processor.ConfigurationStatus, java.util.Set)
	 */
	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
		// Nothing to do here...
	}

}
