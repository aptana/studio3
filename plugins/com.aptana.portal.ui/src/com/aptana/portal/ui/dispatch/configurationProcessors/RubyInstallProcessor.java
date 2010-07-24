package com.aptana.portal.ui.dispatch.configurationProcessors;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.configurations.processor.AbstractConfigurationProcessor;
import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.portal.ui.PortalUIPlugin;

/**
 * A Ruby install processor.<br>
 * This class is in charge of downloading and installing Ruby and DevKit for Windows operating systems.<br>
 * Note: In case we decide to support something similar for MacOSX and Linux, this processor would probably need
 * delegators set up.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class RubyInstallProcessor extends AbstractConfigurationProcessor
{

	private String[] downloadedPaths;
	/**
	 * Constructs a new RubyInstallProcessor
	 */
	public RubyInstallProcessor()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * Configure Ruby on the machine.<br>
	 * The configuration will grab the installer and the DevKit from the given attributes. We expects the first
	 * attribute to contain the Ruby installer link, and the second attribute to contain the DevKit 7z package.
	 * 
	 * @param attributes
	 *            An array of strings holding the rubyinstaller and the devkit URLs.
	 * @see com.aptana.configurations.processor.AbstractConfigurationProcessor#configure(org.eclipse.core.runtime.IProgressMonitor,
	 *      java.lang.Object)
	 */
	@Override
	public ConfigurationStatus configure(IProgressMonitor progressMonitor, Object attributes)
	{
		configurationStatus.removeAttribute(CONFIG_ATTR);
		clearErrorAttributes();
		if (attributes == null || !(attributes instanceof Object[]))
		{
			applyErrorAttributes(Messages.RubyInstallProcessor_missingRubyInstallURLs);
			PortalUIPlugin.logError(new Exception(Messages.RubyInstallProcessor_missingRubyInstallURLs));
			return configurationStatus;
		}
		Object[] attrArray = (Object[]) attributes;
		if (attrArray.length != 2)
		{
			// structure error
			applyErrorAttributes(Messages.RubyInstallProcessor_wrongNumberOfRubyInstallLinks + attrArray.length);
			PortalUIPlugin.logError(new Exception(
					Messages.RubyInstallProcessor_wrongNumberOfRubyInstallLinks + attrArray.length));
			return configurationStatus;
		}
		
		// Start the installation...
		configurationStatus.setStatus(ConfigurationStatus.PROCESSING);
		download();
		install();
		configurationStatus.setStatus(ConfigurationStatus.OK);
		return configurationStatus;
	}

	/**
	 * Download the remote content and store it the temp directory.
	 */
	private void download()
	{
		downloadedPaths = new String[2];
		// TODO
	}

	/**
	 * Install Ruby and DevKit.
	 */
	private void install()
	{
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.configurations.processor.AbstractConfigurationProcessor#computeStatus(org.eclipse.core.runtime.
	 * IProgressMonitor, java.lang.Object)
	 */
	@Override
	public ConfigurationStatus computeStatus(IProgressMonitor progressMonitor, Object attributes)
	{
		// This one does nothing. We compute the Ruby status in the generic VersionsConfigurationProcessor
		return configurationStatus;
	}
}
