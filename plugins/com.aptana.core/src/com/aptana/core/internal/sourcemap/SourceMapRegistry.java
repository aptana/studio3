/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal.sourcemap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.sourcemap.ISourceMap;
import com.aptana.core.sourcemap.ISourceMapRegistry;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.StringUtil;

/**
 * A source map extension point registry. Use an instance of this class by calling
 * {@link CorePlugin#getSourceMapRegistry()}.
 * 
 * @author sgibly@appcelerator.com
 */
public class SourceMapRegistry implements ISourceMapRegistry
{
	private static final String EXTENSION_POINT_ID = "sourceMaps"; //$NON-NLS-1$
	private static final String ELEMENT_TYPE = "sourceMap"; //$NON-NLS-1$
	private static final String CLASS_ATTR = "class"; //$NON-NLS-1$
	private static final String NATURE_ID_ATTR = "projectNatureId"; //$NON-NLS-1$

	protected Map<String, IConfigurationElement> sourceMappers;

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.sourcemap.ISourceMapRegistry#getSourceMap(org.eclipse.core.resources.IProject)
	 */
	public ISourceMap getSourceMap(IProject project, String platform)
	{
		if (project == null || !project.isAccessible())
		{
			IdeLog.logError(CorePlugin.getDefault(),
					"Could not get the sourcemap. The given project was null or not accessible"); //$NON-NLS-1$
			return null;
		}
		lazyLoad();
		String[] natureIds = null;
		try
		{
			natureIds = project.getDescription().getNatureIds();
		}
		catch (CoreException ce)
		{
			IdeLog.logError(CorePlugin.getDefault(), ce);
			return null;
		}

		if (ArrayUtil.isEmpty(natureIds))
		{
			return null;
		}
		return getSourceMap(project, natureIds[0], platform);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.sourcemap.ISourceMapRegistry#getSourceMap(java.lang.String)
	 */
	public ISourceMap getSourceMap(IProject project, String projectNatureId, String platform)
	{
		if (StringUtil.isEmpty(projectNatureId))
		{
			return null;
		}
		lazyLoad();
		// Use the primary nature ID to load the registered ISourceMap that deals with it.
		IConfigurationElement element = sourceMappers.get(projectNatureId);
		if (element == null)
		{
			// No source map for this project.
			return null;
		}
		try
		{
			ISourceMap sourceMap = (ISourceMap) element.createExecutableExtension(CLASS_ATTR);
			sourceMap.setInitializationData(element, platform, project);
			return sourceMap;
		}
		catch (CoreException e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
		return null;
	}

	/**
	 * load the source maps contributors.
	 */
	protected synchronized void lazyLoad()
	{
		if (sourceMappers == null)
		{
			sourceMappers = new HashMap<String, IConfigurationElement>();

			EclipseUtil.processConfigurationElements(CorePlugin.PLUGIN_ID, EXTENSION_POINT_ID,
					new IConfigurationElementProcessor()
					{

						public void processElement(IConfigurationElement element)
						{
							sourceMappers.put(element.getAttribute(NATURE_ID_ATTR), element);
						}

						public Set<String> getSupportElementNames()
						{
							return CollectionsUtil.newSet(ELEMENT_TYPE);
						}
					});
		}
	}
}
