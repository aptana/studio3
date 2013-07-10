/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.portal.actionController;

import java.util.HashMap;
import java.util.Map;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.portal.ui.IDebugScopes;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;
import com.aptana.portal.ui.dispatch.browserNotifications.AbstractBrowserNotification;
import com.aptana.samples.ISampleListener;
import com.aptana.samples.SamplesPlugin;
import com.aptana.samples.model.IProjectSample;
import com.aptana.samples.model.SampleCategory;
import com.aptana.samples.ui.SamplesUIPlugin;

/**
 * A class that notify the portal browser when a new sample is loaded.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class SamplesNotification extends AbstractBrowserNotification
{

	private ISampleListener listener;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.portal.ui.dispatch.browserNotifications.AbstractBrowserNotification#start()
	 */
	@Override
	public synchronized void start()
	{
		isListening = true;
		SamplesPlugin.getDefault().getSamplesManager().addSampleListener(getListener());
		IdeLog.logInfo(SamplesUIPlugin.getDefault(), "Samples Portal notifier started", IDebugScopes.START_PAGE); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.portal.ui.dispatch.browserNotifications.AbstractBrowserNotification#stop()
	 */
	@Override
	public synchronized void stop()
	{
		isListening = false;
		SamplesPlugin.getDefault().getSamplesManager().removeSampleListener(getListener());
		listener = null;
		IdeLog.logInfo(SamplesUIPlugin.getDefault(), "Samples Portal notifier stopped", IDebugScopes.START_PAGE); //$NON-NLS-1$
	}

	/**
	 * Notify a sample addition
	 * 
	 * @param sample
	 */
	protected void notifyAdd(IProjectSample sample)
	{
		IdeLog.logInfo(SamplesUIPlugin.getDefault(), "Sample added. Notifying portal...", IDebugScopes.START_PAGE); //$NON-NLS-1$
		notifyTargets(IBrowserNotificationConstants.EVENT_ID_SAMPLES, IBrowserNotificationConstants.EVENT_TYPE_ADDED,
				createSampleInfo(sample), true);
	}

	/**
	 * Notify a sample removal
	 * 
	 * @param sample
	 */
	protected void notifyRemoved(IProjectSample sample)
	{
		IdeLog.logInfo(SamplesUIPlugin.getDefault(), "Sample removed. Notifying portal...", IDebugScopes.START_PAGE); //$NON-NLS-1$
		notifyTargets(IBrowserNotificationConstants.EVENT_ID_SAMPLES, IBrowserNotificationConstants.EVENT_TYPE_DELETED,
				createSampleInfo(sample), true);
	}

	/**
	 * Create a JSON sample info that will be send as the browser notification data.
	 * 
	 * @param sample
	 * @return A JSON representation of the sample that was added or removed.
	 */
	protected String createSampleInfo(IProjectSample sample)
	{
		Map<String, String> sampleInfo = new HashMap<String, String>();
		SampleCategory category = sample.getCategory();
		sampleInfo.put(SamplesActionController.SAMPLE_INFO.CATEGORY.toString(), (category != null) ? category.getName()
				: StringUtil.EMPTY);
		sampleInfo.put(SamplesActionController.SAMPLE_INFO.NAME.toString(), sample.getName());
		sampleInfo.put(SamplesActionController.SAMPLE_INFO.ID.toString(), sample.getId());
		sampleInfo.put(SamplesActionController.SAMPLE_INFO.DESCRIPTION.toString(), sample.getDescription());
		sampleInfo.put(SamplesActionController.SAMPLE_INFO.IMAGE.toString(), StringUtil.EMPTY); // always empty for now
		return JSON.toString(sampleInfo);
	}

	/**
	 * @return an {@link ISampleListener}
	 */
	protected synchronized ISampleListener getListener()
	{
		if (listener == null)
		{
			listener = new ISampleListener()
			{
				public void sampleRemoved(IProjectSample sample)
				{
					if (isListening)
					{
						notifyRemoved(sample);
					}
				}

				public void sampleAdded(IProjectSample sample)
				{
					if (isListening)
					{
						notifyAdd(sample);
					}
				}
			};
		}
		return listener;
	}
}
