/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.portal.actionController;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.portal.ui.IDebugScopes;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;
import com.aptana.portal.ui.dispatch.actionControllers.AbstractActionController;
import com.aptana.portal.ui.dispatch.actionControllers.CommandHandlerActionController;
import com.aptana.portal.ui.dispatch.actionControllers.ControllerAction;
import com.aptana.samples.ISamplesManager;
import com.aptana.samples.SamplesPlugin;
import com.aptana.samples.model.IProjectSample;
import com.aptana.samples.model.SampleCategory;
import com.aptana.samples.ui.SamplesUIPlugin;

/**
 * An action controller for calling Studio's Samples.<br>
 * The controller allows quering for the existing Samples in the studio, and allows triggering an import-sample wizard
 * with a given sample ID.
 *
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class SamplesActionController extends AbstractActionController
{
	/**
	 * Sample-Info enum
	 */
	protected static enum SAMPLE_INFO
	{
		CATEGORY("category"), //$NON-NLS-1$
		ID("id"), //$NON-NLS-1$
		NAME("name"), //$NON-NLS-1$
		DESCRIPTION("description"), //$NON-NLS-1$
		IMAGE("image"); //$NON-NLS-1$

		private final String name;

		SAMPLE_INFO(String name)
		{
			this.name = name;
		};

		@Override
		public String toString()
		{
			return name;
		}
	}

	protected static final String IMPORT_SAMPLE_COMMAND = "com.aptana.samples.ui.commands.import"; //$NON-NLS-1$
	protected static final String IMPORT_SAMPLE_COMMAND_ID = "id"; //$NON-NLS-1$

	// ############## Actions ###############

	/**
	 * Returns a list of Sample items.<br>
	 * The returned value is a JSON representation of array of Maps. Each map contains a Sample item info that holds the
	 * following attributes:
	 * <ul>
	 * <li>category</li>
	 * <li>name</li>
	 * <li>id</li>
	 * <li>description</li>
	 * <li>image (currently empty)</li>
	 * </ul>
	 *
	 * <pre>
	 *   <b>Sample JS code:</b>
	 *   <code>result = dispatch($H({controller:'portal.samples', action:"getSamples"}).toJSON());</code>
	 * </pre>
	 *
	 * @return A samples list. Each item in the list contains a map for the sample's attributes.
	 */
	@ControllerAction
	public Object getSamples()
	{
		List<Map<String, String>> samples = new ArrayList<Map<String, String>>();
		ISamplesManager samplesManager = SamplesPlugin.getDefault().getSamplesManager();
		List<SampleCategory> categories = samplesManager.getCategories();
		for (SampleCategory category : categories)
		{
			for (IProjectSample sample : samplesManager.getSamplesForCategory(category.getId()))
			{
				Map<String, String> sampleInfo = new HashMap<String, String>();
				sampleInfo.put(SAMPLE_INFO.CATEGORY.toString(), category.getName());
				sampleInfo.put(SAMPLE_INFO.NAME.toString(), sample.getName());
				sampleInfo.put(SAMPLE_INFO.ID.toString(), sample.getId());
				sampleInfo.put(SAMPLE_INFO.DESCRIPTION.toString(), sample.getDescription());
				sampleInfo.put(SAMPLE_INFO.IMAGE.toString(), StringUtil.EMPTY); // always empty for now
				samples.add(sampleInfo);
			}
		}
		return JSON.toString(samples.toArray(new Map[samples.size()]));
	}

	/**
	 * Import a sample into the workspace<br>
	 * The attributes passed to this call should hold a sample ID that was collected through a previous call to
	 * {@link #getSamples()}.
	 *
	 * <pre>
	 *   <b>Sample JS code:</b>
	 *   <code>result = dispatch($H({controller:'portal.samples', action:"importSample", args:"sample-id"}).toJSON());</code>
	 * </pre>
	 *
	 * @param attributes
	 *            Contains the Sample ID to import.
	 * @return The import status
	 * @see #getSamples()
	 */
	@ControllerAction
	public Object importSample(Object attributes)
	{
		String sampleID = getSampleId(attributes);
		if (sampleID == null)
		{
			IdeLog.logError(SamplesUIPlugin.getDefault(),
					"The importSample ControllerAction should get a sampleID attribute", IDebugScopes.START_PAGE); //$NON-NLS-1$
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		// We actually call the CommandHandlerActionController to do the work here.
		CommandHandlerActionController commandHandler = new CommandHandlerActionController();
		Map<String, String> commandArguments = new HashMap<String, String>();
		commandArguments.put(IMPORT_SAMPLE_COMMAND_ID, sampleID);
		String callback = getCallback(attributes);
		return commandHandler.execute(new Object[] { IMPORT_SAMPLE_COMMAND, commandArguments, callback });
	}

	/**
	 * Returns the Sample-Id from the attributes. Null, if an error occurred.
	 *
	 * @param attributes
	 * @return A command Id, or null if an error occurs.
	 */
	private String getSampleId(Object attributes)
	{
		if (attributes instanceof Object[])
		{
			Object[] arr = (Object[]) attributes;
			if (arr.length > 0 && arr[0] != null)
			{
				return arr[0].toString();
			}
			else
			{
				String message = MessageFormat
						.format("Wrong argument count passed to SamplesActionController::importSample. Expected 1 and got {0}", arr.length); //$NON-NLS-1$
				IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
			}
		}
		else
		{
			String message = MessageFormat
					.format("Wrong argument type passed to SamplesActionController::importSample. Expected Object[] and got {0}", //$NON-NLS-1$
							((attributes == null) ? "null" : attributes.getClass().getName())); //$NON-NLS-1$
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
