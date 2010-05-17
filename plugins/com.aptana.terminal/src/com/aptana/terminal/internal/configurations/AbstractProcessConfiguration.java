package com.aptana.terminal.internal.configurations;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.terminal.Activator;
import com.aptana.terminal.IProcessConfiguration;

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
		return ResourceUtil.resourcePathToFile(url);
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
		env.put("APTANA_VERSION", getVersion()); //$NON-NLS-1$
		return env;
	}

	private String getVersion()
	{
		// Grab RED RCP plugin version
		String version = EclipseUtil.getPluginVersion("com.aptana.radrails.rcp"); //$NON-NLS-1$
		if (version == null)
		{
			// Grab Product version
			version = EclipseUtil.getProductVersion();
		}
		// FIXME This doesn't work for features.
//		if (version == null)
//		{
//			// Try Red Core feature
//			version = EclipseUtil.getPluginVersion("com.aptana.red.core"); //$NON-NLS-1$
//		}
		if (version == null)
		{
			// Fallback to Terminal plugin version
			version = EclipseUtil.getPluginVersion(Activator.PLUGIN_ID);
		}
		return version;
	}
}
