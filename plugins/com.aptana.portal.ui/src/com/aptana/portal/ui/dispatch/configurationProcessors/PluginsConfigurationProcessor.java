package com.aptana.portal.ui.dispatch.configurationProcessors;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
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
 * A configuration processor for eclipse-plugins management.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
@SuppressWarnings("restriction")
public class PluginsConfigurationProcessor extends AbstractConfigurationProcessor
{
	private static final String FEATURE_IU_SUFFIX = ".feature.group"; //$NON-NLS-1$
	private static final String P2_INSTALL = "org.eclipse.equinox.p2.ui.sdk.install"; //$NON-NLS-1$
	private static final String PLUGINS_ATTR = "plugins"; //$NON-NLS-1$

	/**
	 * Computing the status by collecting the list of all installed plugins and setting them in the plugins attribute.
	 * 
	 * @param attributes
	 *            Expecting a multi-dimensional array (Object[n][2]), that contains the plugin-ids and versions.
	 */
	@Override
	public ConfigurationStatus computeStatus(IProgressMonitor progressMonitor, Object attributes)
	{
		// Clear the previous attributes that we are no longer interested at.
		configurationStatus.removeAttribute(PLUGINS_ATTR);
		clearErrorAttributes();
		if (attributes == null || !(attributes instanceof Object[]))
		{
			applyErrorAttributes(Messages.PluginsConfigurationProcessor_missingPluginNames);
			PortalUIPlugin.logError(new Exception(Messages.PluginsConfigurationProcessor_missingPluginNames));
			return configurationStatus;
		}
		// Place the array values into a hash.
		Object[] attrArray = (Object[]) attributes;
		Map<String, String> attrPlugins = new HashMap<String, String>();
		for (Object pluginDef : attrArray)
		{
			Object[] def = null;
			if (!(pluginDef instanceof Object[]) || (def = (Object[]) pluginDef).length != 4)
			{
				applyErrorAttributes(Messages.PluginsConfigurationProcessor_wrongPluginDefinitionRequest);
				PortalUIPlugin.logError(new Exception(
						Messages.PluginsConfigurationProcessor_wrongPluginDefinitionRequest));
				return configurationStatus;
			}
			// We only use the first two arguments. The third is the update site URL.
			attrPlugins.put((String) def[0], (String) def[1]);
		}
		configurationStatus.setStatus(ConfigurationStatus.PROCESSING);
		// TODO - Save the status in case of an error or completion.
		Map<String, Map<String, String>> bundleData = new HashMap<String, Map<String, String>>();
		Bundle[] bundles = WorkbenchPlugin.getDefault().getBundles();
		for (Bundle b : bundles)
		{
			String bundleSymbolicName = b.getSymbolicName();
			if (attrPlugins.containsKey(bundleSymbolicName))
			{
				if (b.getState() != Bundle.UNINSTALLED)
				{
					Version bundleVersion = b.getVersion();
					Version requestedVersion = Version.parseVersion(attrPlugins.get(bundleSymbolicName));
					String compatibility = (bundleVersion.compareTo(requestedVersion) >= 0) ? COMPATIBILITY_OK
							: COMPATIBILITY_UPDATE;
					Map<String, String> bundleInfo = new HashMap<String, String>(4);
					bundleInfo.put(ITEM_EXISTS, YES);
					bundleInfo.put(ITEM_VERSION, bundleVersion.toString());
					bundleInfo.put(ITEM_COMPATIBILITY, compatibility);
					bundleData.put(bundleSymbolicName, bundleInfo);
					// Remove the name from the original map. Eventually, we will be left with the plugins we could not
					// locate in the system
					attrPlugins.remove(bundleSymbolicName);
				}
			}
		}
		// Traverse what we have left in the original map that was created from the attributes and mark all plug-ins as
		// 'missing'
		Set<String> missingPlugins = attrPlugins.keySet();
		for (String pluginId : missingPlugins)
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
	 *            A multi-dimensional array of size 2 which contains an optional update-site URL and an optional plugins
	 *            to check (just as passed to {@link #computeStatus(IProgressMonitor, Object)})
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
		Object[] attrArray = null;
		if (attributes == null || !(attributes instanceof Object[]) || (attrArray = (Object[]) attributes).length != 3)
		{
			applyErrorAttributes(Messages.PluginsConfigurationProcessor_wrongAttributesForConfigure);
			PortalUIPlugin.logError(new Exception(Messages.PluginsConfigurationProcessor_wrongAttributesForConfigure));
			return configurationStatus;
		}
		configurationStatus.setStatus(ConfigurationStatus.PROCESSING);
		String updateURL = (String) attrArray[0];
		String featureID = (String) attrArray[1];
		Object pluginPostCheckAttributes = attrArray[2];
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
			// At this point, check if we have a valid pluginPostCheckAttributes data. If so, call for computeStatus and
			// return its result.
			if (pluginPostCheckAttributes != null && pluginPostCheckAttributes instanceof Object[]
					&& ((Object[]) pluginPostCheckAttributes).length > 0)
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
