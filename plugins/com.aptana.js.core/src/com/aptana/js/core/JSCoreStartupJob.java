/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.util.StringUtil;
import com.aptana.index.core.IndexContainerJob;
import com.aptana.js.core.preferences.IPreferenceConstants;
import com.aptana.js.internal.core.index.JSMetadataLoader;

class JSCoreStartupJob extends Job
{

	public JSCoreStartupJob()
	{
		super(Messages.JSCoreStartupJob_Name);
		setSystem(true);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		new JSMetadataLoader().schedule();

		// TODO This shouldn't be necessary once we fix up the build path infrastructure
		// index NodeJS!
		String value = Platform.getPreferencesService().getString(JSCorePlugin.PLUGIN_ID,
				IPreferenceConstants.NODEJS_SOURCE_PATH, null, null);
		if (StringUtil.isEmpty(value))
		{
			return Status.OK_STATUS;
		}
		IPath path = Path.fromOSString(value).append("lib"); //$NON-NLS-1$

		new IndexContainerJob(path.toFile().toURI()).schedule(Job.BUILD);

		// How do we tie this back to a project? We really need to have a project hold a list of classpath
		// entries/containers
		// and using that we can grab the list of indices we need to query (and their order)
		// and also what needs to be indexed.
		// We have the conecpt of build path entries, but it's not quite all the way there.

		return Status.OK_STATUS;
	}

}
