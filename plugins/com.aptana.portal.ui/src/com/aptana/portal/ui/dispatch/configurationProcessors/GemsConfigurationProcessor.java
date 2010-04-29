package com.aptana.portal.ui.dispatch.configurationProcessors;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.configurations.processor.AbstractConfigurationProcessor;
import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.util.EditorUtils;
import com.aptana.util.ProcessUtil;

/**
 * A configuration processor for Ruby Gems management.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class GemsConfigurationProcessor extends AbstractConfigurationProcessor
{
	private static final String GEMS_ATTR = "gems"; //$NON-NLS-1$
	private static final String GEM_LIST = "gem list"; //$NON-NLS-1$

	@Override
	public ConfigurationStatus computeStatus(IProgressMonitor progressMonitor, Object attributes)
	{
		configurationStatus.removeAttribute(GEMS_ATTR);
		configurationStatus.setStatus(ConfigurationStatus.PROCESSING);
		// TODO - Save the status in case of an error or completion.
		String shellCommandPath = getShellPath();
		if (shellCommandPath == null)
		{
			applyErrorAttributes(Messages.GemsConfigurationProcessor_missingShellError);
		}
		else
		{
			// TODO: Shalom - This still need to be tested, but we might need to pass a -l to the command.
			// This -l will cause the output to contains some more header lines which needs to be filtered to
			// get to the real gem list.
			String gems = ProcessUtil.outputForCommand(shellCommandPath, null, new String[] { "-c", GEM_LIST }); //$NON-NLS-1$
			if (gems != null)
			{
				gems = gems.replaceAll(EditorUtils.getLineSeparatorValue(null), ";"); //$NON-NLS-1$
				configurationStatus.setAttribute(GEMS_ATTR, gems);
				configurationStatus.setStatus(ConfigurationStatus.OK);
			}
			else
			{
				applyErrorAttributes(Messages.GemsConfigurationProcessor_errorInvokingGemList);
			}
		}
		return configurationStatus;
	}

	@Override
	public ConfigurationStatus configure(IProgressMonitor progressMonitor, Object attributes)
	{
		// TODO Shalom: Parse the attributes and figure out what are the gems that the remote browser asks us to install
		return configurationStatus;
	}
}
