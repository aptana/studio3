/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.ui.portal.actionController;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Path;

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
import com.aptana.samples.model.SamplesReference;
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
		PATH("location"), //$NON-NLS-1$
		NATURES("natures"), //$NON-NLS-1$
		IMAGE("image"); //$NON-NLS-1$

		private String name;

		SAMPLE_INFO(String name)
		{
			this.name = name;
		};

		public String toString()
		{
			return name;
		}
	}

	protected static final String IMPORT_SAMPLE_COMMAND = "com.aptana.samples.ui.commands.import"; //$NON-NLS-1$
	protected static final String IMPORT_SAMPLE_COMMAND_ID = "id"; //$NON-NLS-1$
	protected static final String WEB_NATURE = "com.aptana.projects.webnature"; //$NON-NLS-1$

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
	 * Adds a new sample from the local file system or a remote URI into the Samples Manager<br>
	 * The attributes passed to this call should hold the following required attributes of the sample to be added.
	 * <ul>
	 * <li>category</li>
	 * <li>name</li>
	 * <li>id</li>
	 * <li>description</li>
	 * <li>location</li>
	 * </ul>
	 *
	 * <pre>
	 *   <b>Sample JS code:</b>
	 *   <code>result = dispatch($H({controller:'portal.samples', action:"addSample", 
	 *   args:"{"category":"com.appcelerator.titanium.mobile.samples.category",
	 *                 "description":"This is a dynamically imported sample",
	 *                 "name":"dynamic_sample",
	 *                 "location":"git://github.com/appcelerator-developer-relations/Sample.Mapping.git",
	 *                 "id":"dyn",
	 *                 "image":"http://preview.appcelerator.com/dashboard/img/icons/icon_geo.png",
	 *                 "natures":"[com.appcelerator.titanium.mobile.nature, com.aptana.projects.webnature]"
	 *                }"}).toJSON());</code>
	 * </pre>
	 *
	 * @param attributes
	 *            Contains the Sample data in JSON format.
	 * @return The status
	 * @see #getSamples()
	 */
	@ControllerAction
	public Object addSample(Object attributes)
	{
		ISamplesManager samplesManager = SamplesPlugin.getDefault().getSamplesManager();
		// We have got the sample data in a JSON format. Now lets try and parse it
		// and create an IProjectSample object which we will add to the Samples Manager
		Object[] arr = (Object[]) attributes;

		if (arr == null || arr.length < 1 || arr[0] == null || !(arr[0] instanceof HashMap))
		{
			IdeLog.logError(SamplesUIPlugin.getDefault(),
					"The addSample ControllerAction should get an attribute in JSON format", IDebugScopes.START_PAGE); //$NON-NLS-1$
			return IBrowserNotificationConstants.JSON_ERROR;
		}

		@SuppressWarnings({ "rawtypes" })
		HashMap sampleData = (HashMap) arr[0];

		// Id for the sample
		String id = (String) sampleData.get(SAMPLE_INFO.ID.toString());
		if (StringUtil.isEmpty(id))
		{
			IdeLog.logError(SamplesUIPlugin.getDefault(),
					"Sample Missing required attribute id", IDebugScopes.START_PAGE); //$NON-NLS-1$
			return IBrowserNotificationConstants.JSON_ERROR;
		}

		// Name for the sample
		String name = (String) sampleData.get(SAMPLE_INFO.NAME.toString());
		if (StringUtil.isEmpty(name))
		{
			IdeLog.logError(SamplesUIPlugin.getDefault(),
					"Sample Missing required attribute name", IDebugScopes.START_PAGE); //$NON-NLS-1$
			return IBrowserNotificationConstants.JSON_ERROR;
		}

		// Description for the sample
		String description = (String) sampleData.get(SAMPLE_INFO.DESCRIPTION.toString());
		if (StringUtil.isEmpty(description))
		{
			IdeLog.logError(SamplesUIPlugin.getDefault(),
					"Sample Missing required attribute description", IDebugScopes.START_PAGE); //$NON-NLS-1$
			return IBrowserNotificationConstants.JSON_ERROR;
		}

		// Location for the sample
		String location = (String) sampleData.get(SAMPLE_INFO.PATH.toString());
		if (StringUtil.isEmpty(location))
		{
			IdeLog.logError(SamplesUIPlugin.getDefault(),
					"Sample Missing required attribute path", IDebugScopes.START_PAGE); //$NON-NLS-1$
			return IBrowserNotificationConstants.JSON_ERROR;
		}

		// If file does not exist it is remote.
		boolean isRemote = !(new File(location).exists()); //$NON-NLS-1$

		// Category of the sample
		String category = (String) sampleData.get(SAMPLE_INFO.CATEGORY.toString());
		if (StringUtil.isEmpty(category))
		{
			IdeLog.logError(SamplesUIPlugin.getDefault(),
					"Sample Missing required attribute category", IDebugScopes.START_PAGE); //$NON-NLS-1$
			return IBrowserNotificationConstants.JSON_ERROR;
		}

		SampleCategory sampleCategory = samplesManager.getCategory(category);
		// If we could not find the category use the default category
		if (sampleCategory == null)
		{
			sampleCategory = new SampleCategory(category, "Others", null);//$NON-NLS-1$
		}

		// Icons for the sample
		URL iconUrl = null;
		String iconPath = null;
		try
		{
			iconPath = (String) sampleData.get(SAMPLE_INFO.IMAGE.toString());
			if (StringUtil.isEmpty(iconPath))
			{
				IdeLog.logError(SamplesPlugin.getDefault(),
						MessageFormat.format("Unable to retrieve the icon at {0} for sample {1}", iconPath, name)); //$NON-NLS-1$

			}
			else
			{
				iconUrl = new URL(iconPath);
			}

		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(SamplesPlugin.getDefault(), MessageFormat.format("malformed icon URL at {0}", iconPath)); //$NON-NLS-1$

		}
		Map<String, URL> iconUrls = new HashMap<String, URL>();
		iconUrls.put(SamplesReference.DEFAULT_ICON_KEY, iconUrl);

		// Create the sample and add it to the samples manager
		SamplesReference sample = new SamplesReference(sampleCategory, id, name, location, isRemote, description,
				iconUrls, new Path("app"), null);

		// Project natures for the sample
		String[] natures = (String[]) sampleData.get(SAMPLE_INFO.NATURES.toString());
		if (natures == null || natures.length < 1)
		{
			natures = new String[1];
			natures[0] = WEB_NATURE;
		}

		sample.setNatures(natures);
		samplesManager.addSample(sample);
		return IBrowserNotificationConstants.JSON_OK;
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
		return commandHandler.execute(new Object[] { IMPORT_SAMPLE_COMMAND, commandArguments });
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
			if (arr.length == 1 && arr[0] != null)
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
