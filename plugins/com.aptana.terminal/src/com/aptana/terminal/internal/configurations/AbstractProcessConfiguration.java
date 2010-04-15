package com.aptana.terminal.internal.configurations;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;

import com.aptana.terminal.Activator;
import com.aptana.terminal.IProcessConfiguration;
import com.aptana.util.EclipseUtils;
import com.aptana.util.ResourceUtils;

abstract class AbstractProcessConfiguration implements IProcessConfiguration
{

	/*
	 * (non-Javadoc)
	 * @see com.aptana.terminal.IProcessConfiguration#getExecutable()
	 */
	@Override
	public File getExecutable()
	{
		URL url = FileLocator.find(Activator.getDefault().getBundle(), getExecutablePath(), null);
		return ResourceUtils.resourcePathToFile(url);
	}

	protected abstract IPath getExecutablePath();

	/*
	 * (non-Javadoc)
	 * @see com.aptana.terminal.IProcessConfiguration#getEnvironment()
	 */
	@Override
	public Map<String, String> getEnvironment()
	{
		Map<String, String> env = new HashMap<String, String>();
		env.put("TERM", "xterm-color"); //$NON-NLS-1$ //$NON-NLS-2$
		env.put("APTANA_VERSION", getVersion()); //$NON-NLS-1$
		return env;
	}

	private String getVersion()
	{
		// Grab RED RCP plugin version
		String version = EclipseUtils.getPluginVersion("com.aptana.radrails.rcp"); //$NON-NLS-1$
		if (version == null)
		{
			// Grab Product version
			version = EclipseUtils.getProductVersion();
		}
		// FIXME This doesn't work for features.
//		if (version == null)
//		{
//			// Try Red Core feature
//			version = EclipseUtils.getPluginVersion("com.aptana.red.core"); //$NON-NLS-1$
//		}
		if (version == null)
		{
			// Fallback to Terminal plugin version
			version = EclipseUtils.getPluginVersion(Activator.PLUGIN_ID);
		}
		return version;
	}
}
