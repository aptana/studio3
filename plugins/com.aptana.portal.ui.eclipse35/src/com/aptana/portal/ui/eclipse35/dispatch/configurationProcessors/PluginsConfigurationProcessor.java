/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.eclipse35.dispatch.configurationProcessors;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.provisional.p2.artifact.repository.IArtifactRepository;
import org.eclipse.equinox.internal.provisional.p2.artifact.repository.IArtifactRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
import org.eclipse.equinox.internal.provisional.p2.core.VersionRange;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfile;
import org.eclipse.equinox.internal.provisional.p2.engine.IProfileRegistry;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.metadata.query.InstallableUnitQuery;
import org.eclipse.equinox.internal.provisional.p2.metadata.repository.IMetadataRepository;
import org.eclipse.equinox.internal.provisional.p2.metadata.repository.IMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.equinox.internal.provisional.p2.repository.IRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui.IProvHelpContextIds;
import org.eclipse.equinox.internal.provisional.p2.ui.QueryableMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.InstallWizard;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.ProvisioningWizardDialog;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.ProvisioningUtil;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.Policy;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.mortbay.util.ajax.JSON;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.aptana.configurations.processor.AbstractConfigurationProcessor;
import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.portal.ui.PortalUIPlugin;

/**
 * A configuration processor for eclipse-plugins management.<br>
 * This PluginsConfigurationProcessor is compatible with Eclipse 3.5 only.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
@SuppressWarnings("restriction")
public class PluginsConfigurationProcessor extends AbstractConfigurationProcessor
{
	protected static final String PLUGIN_VERSION_ATTR = "plugin_version"; //$NON-NLS-1$
	protected static final String PLUGIN_ID_ATTR = "plugin_id"; //$NON-NLS-1$
	protected static final String FEATURE_ID_ATTR = "feature_id"; //$NON-NLS-1$
	protected static final String PLUGIN_POST_CHECK_ATTR = "plugin_post_check"; //$NON-NLS-1$
	private static final String FEATURE_IU_SUFFIX = ".feature.group"; //$NON-NLS-1$
	private static final String P2_INSTALL = "org.eclipse.equinox.p2.ui.sdk.install"; //$NON-NLS-1$
	private static final String PLUGINS_ATTR = "plugins"; //$NON-NLS-1$

	/**
	 * Computing the installation status of a give plugin.<br>
	 * We expects that the attributes will contain the plugin-URL and a hash that contains the plugin-in, plugin-version
	 * and feature-id.
	 * 
	 * @param attributes
	 *            A string array of size 1, which contains the URL for the plugin update site. Second - map of
	 *            additional attributes.
	 */
	@Override
	public ConfigurationStatus computeStatus(IProgressMonitor progressMonitor, Object attributes)
	{
		// Clear the previous attributes that we are no longer interested at.
		configurationStatus.removeAttribute(PLUGINS_ATTR);
		clearErrorAttributes();
		// Load the plugin's attributes
		IStatus loadingStatus = loadAttributes(attributes);
		if (!loadingStatus.isOK())
		{
			applyErrorAttributes(loadingStatus.getMessage());
			PortalUIPlugin.log(loadingStatus);
			return configurationStatus;
		}

		// Check that we got the plugin-id and version in the attributes.
		if (!attributesMap.containsKey(PLUGIN_ID_ATTR) || !attributesMap.containsKey(PLUGIN_VERSION_ATTR))
		{
			applyErrorAttributes(Messages.PluginsConfigurationProcessor_wrongPluginDefinitionRequest);
			PortalUIPlugin.logError(new Exception(Messages.PluginsConfigurationProcessor_wrongPluginDefinitionRequest));
			return configurationStatus;
		}
		// Start the check
		configurationStatus.setStatus(ConfigurationStatus.PROCESSING);
		// TODO - Save the status in case of an error or completion.
		String pluginId = attributesMap.get(PLUGIN_ID_ATTR);
		Version pluginVersion = Version.parseVersion(attributesMap.get(PLUGIN_VERSION_ATTR));
		Map<String, Map<String, String>> bundleData = new HashMap<String, Map<String, String>>();
		Bundle[] bundles = WorkbenchPlugin.getDefault().getBundles();
		boolean found = false;
		for (Bundle b : bundles)
		{
			String bundleSymbolicName = b.getSymbolicName();
			if (pluginId.equals(bundleSymbolicName))
			{
				if (b.getState() != Bundle.UNINSTALLED)
				{
					Version bundleVersion = b.getVersion();
					String compatibility = (bundleVersion.compareTo(pluginVersion) >= 0) ? COMPATIBILITY_OK
							: COMPATIBILITY_UPDATE;
					Map<String, String> bundleInfo = new HashMap<String, String>(4);
					bundleInfo.put(ITEM_EXISTS, YES);
					bundleInfo.put(ITEM_VERSION, bundleVersion.toString());
					bundleInfo.put(ITEM_COMPATIBILITY, compatibility);
					bundleData.put(bundleSymbolicName, bundleInfo);
					found = true;
					break;
				}
			}
		}
		// if we did not find it, mark the plugin as missing
		if (!found)
		{
			Map<String, String> bundleInfo = new HashMap<String, String>(4);
			bundleInfo.put(ITEM_EXISTS, NO);
			bundleData.put(pluginId, bundleInfo);
		}

		// Finally, set the bundle data status into the configuration attribute
		configurationStatus.setAttribute(PLUGINS_ATTR, JSON.toString(bundleData));
		configurationStatus.setStatus(ConfigurationStatus.OK);
		return configurationStatus;
	}

	/**
	 * This configure can either open the 'New Software' dialog and point directly to a given update site for a plug-in
	 * installation, or it can open the dialog on its root, and allow any plugin installation. A post-check for a given
	 * plugins attributes can be done after the dialog is closed, in case post-check attributes are attached.
	 * 
	 * @param attributes
	 *            Contains a String array of size one which holds the update-site URL, and a Map which should hold the
	 *            feature_id and optional plugin_post_check that will indicate (with a 'true' or 'false' value) that a
	 *            computeStatus call is needed after the installation.
	 */
	@Override
	public ConfigurationStatus configure(IProgressMonitor progressMonitor, Object attributes)
	{
		IWorkbench workbench = PortalUIPlugin.getDefault().getWorkbench();
		if (workbench.isClosing())
		{
			return configurationStatus;
		}
		clearErrorAttributes();

		// Load the plugin's attributes
		IStatus loadingStatus = loadAttributes(attributes);
		if (!loadingStatus.isOK())
		{
			applyErrorAttributes(loadingStatus.getMessage());
			PortalUIPlugin.logError(new Exception(loadingStatus.getMessage()));
			return configurationStatus;
		}

		// Check that we got the plugin-id and version in the attributes.
		if (!attributesMap.containsKey(FEATURE_ID_ATTR))
		{
			applyErrorAttributes(Messages.PluginsConfigurationProcessor_wrongPluginDefinitionRequest);
			PortalUIPlugin.logError(new Exception(Messages.PluginsConfigurationProcessor_wrongPluginDefinitionRequest));
			return configurationStatus;
		}

		// Start the plugin installation
		configurationStatus.setStatus(ConfigurationStatus.PROCESSING);
		String updateURL = urls[0];
		String featureID = attributesMap.get(FEATURE_ID_ATTR);
		String pluginPostCheckAttributes = attributesMap.get(PLUGIN_POST_CHECK_ATTR);
		IHandlerService handlerService = (IHandlerService) workbench.getService(IHandlerService.class);
		try
		{
			if (updateURL == null || updateURL.trim().length() == 0)
			{
				handlerService.executeCommand(P2_INSTALL, null);
			}
			else
			{
				openInstallDialog(updateURL, featureID, progressMonitor);
			}
			// At this point, check if the plugin_post_check attribute is set to 'true' . If so, call for computeStatus
			// and return its result.
			if (pluginPostCheckAttributes != null && Boolean.valueOf(pluginPostCheckAttributes).booleanValue())
			{
				return computeStatus(progressMonitor, pluginPostCheckAttributes);
			}
			else
			{
				configurationStatus.setStatus(ConfigurationStatus.OK);
			}
		}
		catch (Exception e)
		{
			PortalUIPlugin.logError("Error while trying to install a new plugin", e); //$NON-NLS-1$
			applyErrorAttributes(e.getMessage());
		}
		return configurationStatus;
	}

	/**
	 * Open the plugin installation dialog for the given feature on the given update site.
	 * 
	 * @param updateSite
	 * @param featureID
	 * @param monitor
	 * @throws InvocationTargetException
	 */
	public void openInstallDialog(final String updateSite, final String featureID, final IProgressMonitor monitor)
			throws InvocationTargetException
	{
		String profileId = IProfileRegistry.SELF;
		IInstallableUnit[] toInstall = getInstallationUnits(updateSite, featureID, profileId);
		if (toInstall.length <= 0)
		{
			throw new IllegalStateException(Messages.PluginsConfigurationProcessor_cannotFindInstallationUnits);
		}

		if (monitor.isCanceled())
		{
			return;
		}

		QueryableMetadataRepositoryManager queryableManager = new QueryableMetadataRepositoryManager(Policy
				.getDefault().getQueryContext(), false)
		{
			@Override
			protected URI[] getRepoLocations(IRepositoryManager manager)
			{
				URI[] result = new URI[1];
				try
				{
					result[0] = new URI(updateSite);
				}
				catch (URISyntaxException e)
				{
					PortalUIPlugin.logError(e);
				}
				return result;
			}
		};

		InstallWizard wizard = new InstallWizard(generateNonManipulatingRepoPolicy(), profileId, toInstall, null,
				queryableManager);
		WizardDialog dialog = new ProvisioningWizardDialog(Display.getDefault().getActiveShell(), wizard);
		dialog.create();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(), IProvHelpContextIds.INSTALL_WIZARD);
		dialog.open();
	}

	private IInstallableUnit[] getInstallationUnits(final String updateSite, final String featureID,
			final String profileId) throws InvocationTargetException
	{
		final List<IInstallableUnit> units = new ArrayList<IInstallableUnit>();

		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{

			@SuppressWarnings("unchecked")
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
			{
				SubMonitor sub = SubMonitor.convert(monitor, 1);
				sub.setTaskName(Messages.PluginsConfigurationProcessor_locatingFeatures);
				try
				{
					URI siteURL = new URI(updateSite);
					IMetadataRepositoryManager manager = (IMetadataRepositoryManager) ServiceHelper.getService(
							PortalUIPlugin.getContext(), IMetadataRepositoryManager.class.getName());
					IMetadataRepository repo = manager.loadRepository(siteURL, new NullProgressMonitor());
					if (repo == null)
					{
						throw new ProvisionException(Messages.PluginsConfigurationProcessor_metadataRepoNotFound
								+ siteURL);
					}
					if (!manager.isEnabled(siteURL))
					{
						manager.setEnabled(siteURL, true);
					}
					sub.worked(1);

					IArtifactRepositoryManager artifactManager = (IArtifactRepositoryManager) ServiceHelper.getService(
							PortalUIPlugin.getContext(), IArtifactRepositoryManager.class.getName());
					IArtifactRepository artifactRepo = artifactManager.loadRepository(siteURL,
							new NullProgressMonitor());
					if (artifactRepo == null)
					{
						throw new ProvisionException(Messages.PluginsConfigurationProcessor_artifactRepoNotFound
								+ siteURL);
					}
					if (!artifactManager.isEnabled(siteURL))
					{
						artifactManager.setEnabled(siteURL, true);
					}
					sub.worked(1);

					InstallableUnitQuery query = new InstallableUnitQuery(featureID + FEATURE_IU_SUFFIX,
							VersionRange.emptyRange);
					Collector roots = repo.query(query, new Collector(), monitor);

					if (roots.size() <= 0)
					{
						if (monitor.isCanceled())
						{
							return;
						}

						IProfile profile = ProvisioningUtil.getProfile(profileId);
						if (profile != null)
						{
							roots = profile.query(query, roots, monitor);
						}
						else
						{
							// Log this
							PortalUIPlugin.logError(
									"Error while retrieving the profile for '" + updateSite + "' update site", //$NON-NLS-1$  //$NON-NLS-2$
									new RuntimeException("The profile for '" + profileId + "' was null")); //$NON-NLS-1$  //$NON-NLS-2$
						}
					}
					units.addAll(roots.toCollection());
					sub.worked(2);
				}
				catch (Exception e)
				{
					throw new InvocationTargetException(e);
				}
				finally
				{
					sub.done();
				}
			}
		};
		try
		{
			new ProgressMonitorDialog(Display.getDefault().getActiveShell()).run(true, true, runnable);
		}
		catch (InterruptedException e)
		{
			// don't report thread interruption
		}
		return units.toArray(new IInstallableUnit[units.size()]);
	}

	/**
	 * Generates a copy of the default policy but removes the ability to manipulate repos, so that that portion of
	 * Install Wizard UI doesn't show and mess up our installation process!
	 * 
	 * @return
	 */
	private Policy generateNonManipulatingRepoPolicy()
	{
		Policy newPolicy = new Policy();
		newPolicy.setLicenseManager(Policy.getDefault().getLicenseManager());
		newPolicy.setPlanValidator(Policy.getDefault().getPlanValidator());
		newPolicy.setProfileChooser(Policy.getDefault().getProfileChooser());
		newPolicy.setQueryContext(Policy.getDefault().getQueryContext());
		newPolicy.setQueryProvider(Policy.getDefault().getQueryProvider());
		newPolicy.setRepositoryManipulator(null);
		return newPolicy;
	}

}
