package com.aptana.portal.ui.dispatch.configurationProcessors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.osgi.framework.Bundle;

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
	private static final String P2_INSTALL = "org.eclipse.equinox.p2.ui.sdk.install"; //$NON-NLS-1$
	private static final String PLUGINS_ATTR = "plugins"; //$NON-NLS-1$

	/**
	 * Computing the status by collecting the list of all installed plugins and setting them in the plugins attribute.
	 */
	@Override
	public ConfigurationStatus computeStatus(IProgressMonitor progressMonitor, Object attributes)
	{
		configurationStatus.removeAttribute(PLUGINS_ATTR);
		configurationStatus.setStatus(ConfigurationStatus.PROCESSING);
		// TODO - Save the status in case of an error or completion.
		Bundle[] bundles = WorkbenchPlugin.getDefault().getBundles();
		for (Bundle b : bundles) {
			
		}
		//gems = gems.replaceAll(EditorUtils.getLineSeparatorValue(null), ";"); //$NON-NLS-1$
		//configurationStatus.setAttribute(GEMS_ATTR, gems);
		configurationStatus.setStatus(ConfigurationStatus.OK);
		// applyErrorAttributes(configurationStatus, Messages.GemsConfigurationProcessor_errorInvokingGemList);
		return configurationStatus;
	}

	/*
	 * (non-Javadoc)
	 * @seecom.aptana.configurations.processor.AbstractConfigurationProcessor#configure(org.eclipse.core.runtime.
	 * IProgressMonitor, java.lang.Object)
	 */
	@Override
	public ConfigurationStatus configure(IProgressMonitor progressMonitor, Object attributes)
	{
		configurationStatus.removeAttribute(PLUGINS_ATTR);
		configurationStatus.setStatus(ConfigurationStatus.PROCESSING);
		IHandlerService handlerService = (IHandlerService) PortalUIPlugin.getDefault().getWorkbench().getService(
				IHandlerService.class);
		try
		{
			// TODO: Shalom - Look at the given attributes to get the details for the plugin/feature to install.
			Object value = handlerService.executeCommand(P2_INSTALL, null);
			// TODO - Look at the return value?
			configurationStatus.setStatus(ConfigurationStatus.OK);
		}
		catch (Exception e)
		{
			PortalUIPlugin.logError("Error while trying to install a new plugin", e); //$NON-NLS-1$
			applyErrorAttributes(configurationStatus, e.getMessage());
		}
		return configurationStatus;
	}

}
