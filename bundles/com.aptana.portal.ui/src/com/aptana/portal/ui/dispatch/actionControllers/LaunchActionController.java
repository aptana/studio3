/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.contextlaunching.LaunchingResourceManager;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchShortcutExtension;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.progress.UIJob;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.portal.ui.IPortalPreferences;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * A action controller for launch related actions.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
@SuppressWarnings({ "unchecked", "restriction" })
public class LaunchActionController extends AbstractActionController
{
	private static final String ATTR_PROJECT = "project"; //$NON-NLS-1$
	private static final String ATTR_MODE = "mode"; //$NON-NLS-1$
	private static final String ATTR_TYPE = "type"; //$NON-NLS-1$

	// ############## Actions ###############
	/**
	 * Returns the available launch type IDs for a given project and mode.
	 * 
	 * @param attributes
	 *            Contains the project name and mode of launching.
	 * @return A JSON Map representation for the launch types allowed (ID and Label pairs).
	 */
	@ControllerAction
	public Object getLaunchTypes(Object attributes)
	{
		if (!containsMap(attributes))
		{
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		Map<String, String> attrMap = (Map<String, String>) ((Object[]) attributes)[0];

		// We expect a project name and a launch mode.
		IProject project = getProject(attrMap.get(ATTR_PROJECT));
		if (project == null)
		{
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		String mode = getMode(attrMap.get(ATTR_MODE));
		if (mode == null)
		{
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		// List<LaunchShortcutExtension> shortcuts = DebugUIPlugin.getDefault().getLaunchConfigurationManager()
		// .getLaunchShortcuts();
		// List availableShortcuts = new ArrayList();
		LaunchingResourceManager lrm = DebugUIPlugin.getDefault().getLaunchingResourceManager();
		List<LaunchShortcutExtension> shortcutsForSelection = lrm.getShortcutsForSelection(new StructuredSelection(
				project), mode);
		Map<String, String> result = new HashMap<String, String>();
		if (CollectionsUtil.isEmpty(shortcutsForSelection))
		{
			return JSON.toString(result);
		}
		// Prepare a Map representation for the result (ID to Label map)
		for (LaunchShortcutExtension shortcut : shortcutsForSelection)
		{
			result.put(shortcut.getId(), shortcut.getLabel());
		}
		return JSON.toString(result);
	}

	/**
	 * Launch a project in a given mode with the specified type.
	 * 
	 * @param attributes
	 *            Contains the project name, mode of launching, and launcher ID retrieved from the
	 *            {@link #getLaunchTypes(Object)} call.
	 */
	@ControllerAction
	public Object launch(Object attributes)
	{
		if (!containsMap(attributes))
		{
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		Map<String, String> attrMap = (Map<String, String>) ((Object[]) attributes)[0];

		// We expect a project name and a launch mode.
		IProject project = getProject(attrMap.get(ATTR_PROJECT));
		if (project == null)
		{
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		String mode = getMode(attrMap.get(ATTR_MODE));
		if (mode == null)
		{
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		String type = attrMap.get(ATTR_TYPE);
		if (StringUtil.isEmpty(type))
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), new Exception("LaunchActionController - missing type")); //$NON-NLS-1$
		}
		LaunchingResourceManager lrm = DebugUIPlugin.getDefault().getLaunchingResourceManager();
		IStructuredSelection projectSelection = new StructuredSelection(project);
		List<LaunchShortcutExtension> shortcutsForSelection = lrm.getShortcutsForSelection(projectSelection, mode);
		// Prepare a Map representation for the result (ID to Label map)
		for (LaunchShortcutExtension shortcut : shortcutsForSelection)
		{
			if (type.equals(shortcut.getId()))
			{
				innerLaunch(shortcut, projectSelection, mode);
				// done!
				return IBrowserNotificationConstants.JSON_OK;
			}
		}
		return IBrowserNotificationConstants.JSON_ERROR;
	}

	/**
	 * Launch internally using a Job.
	 * 
	 * @param shortcut
	 * @param mode
	 */
	private void innerLaunch(final LaunchShortcutExtension shortcut, final ISelection projectSelection,
			final String mode)
	{
		Job launchJob = new UIJob(Messages.LaunchActionController_launchingJob)
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				shortcut.launch(projectSelection, mode);
				return Status.OK_STATUS;
			}
		};
		launchJob.schedule();
	}

	/**
	 * Validate and return a launch mode.
	 * 
	 * @param mode
	 *            The given mode attribute
	 * @param A
	 *            matching launch mode ('run', 'debug', 'profile'); <code>null</code> if the mode attribute did not
	 *            match any valid mode.
	 */
	private String getMode(String mode)
	{
		if (StringUtil.isEmpty(mode))
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), new Exception("LaunchActionController - Missing launch mode")); //$NON-NLS-1$
			return null;
		}
		mode = mode.toLowerCase().trim();
		if (ILaunchManager.RUN_MODE.equals(mode) || ILaunchManager.DEBUG_MODE.equals(mode)
				|| ILaunchManager.PROFILE_MODE.equals(mode))
		{
			return mode;
		}
		IdeLog.logError(PortalUIPlugin.getDefault(), new Exception("LaunchActionController - Unknown mode: " + mode)); //$NON-NLS-1$
		return null;

	}

	/**
	 * Validate and return an {@link IProject} that corresponds the given project name.
	 * 
	 * @param projectName
	 * @return An {@link IProject}; <code>null</code> in case the project could not be located, or it's not accessible.
	 */
	private IProject getProject(String projectName)
	{
		if (StringUtil.isEmpty(projectName))
		{
			// Launch previously created project.
			projectName = InstanceScope.INSTANCE.getNode(PortalUIPlugin.PLUGIN_ID).get(
					IPortalPreferences.RECENTLY_CREATED_PROJECT, null);
			if (StringUtil.isEmpty(projectName))
			{
				IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(
						"LaunchActionController - Missing project name")); //$NON-NLS-1$
				return null;
			}
		}
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(projectName);
		if (resource == null)
		{
			IdeLog.logError(
					PortalUIPlugin.getDefault(),
					new Exception(
							MessageFormat
									.format("LaunchActionController - Could not locate a project named ''{0}'' in the workspace", projectName))); //$NON-NLS-1$
			return null;
		}
		IProject project = resource.getProject();
		if (!project.isAccessible())
		{
			IdeLog.logError(
					PortalUIPlugin.getDefault(),
					new Exception(MessageFormat.format(
							"LaunchActionController - The project ''{0}'' is not accessible", project.getName()))); //$NON-NLS-1$
			return null;
		}
		return project;
	}

	/**
	 * Returns true if the attributes contain a map. Otherwise, return false and log any errors.
	 */
	private boolean containsMap(Object attributes)
	{
		if (attributes instanceof Object[])
		{
			Object[] arr = (Object[]) attributes;
			if (arr.length == 1)
			{
				if (arr[0] instanceof Map)
				{
					return true;
				}
				IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(
						"Wrong argument type passed to LaunchActionController. Expected a Map.")); //$NON-NLS-1$
				return false;
			}
			else
			{
				String message = MessageFormat.format(
						"Wrong argument count passed to LaunchActionController. Expected 1 and got {0}", arr.length); //$NON-NLS-1$
				IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
			}
		}
		else
		{
			String message = MessageFormat.format(
					"Wrong argument type passed to LaunchActionController. Expected Object[] and got {0}", //$NON-NLS-1$
					((attributes == null) ? "null" : attributes.getClass().getName())); //$NON-NLS-1$
			IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.IConfigurationProcessorListener#configurationStateChanged(com.aptana.
	 * configurations.processor.ConfigurationStatus, java.util.Set)
	 */
	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
		// Nothing to do here
	}
}
