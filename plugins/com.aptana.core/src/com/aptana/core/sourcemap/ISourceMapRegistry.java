/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.sourcemap;

import org.eclipse.core.resources.IProject;

/**
 * A source map registry that can be used to retrieve registered {@link ISourceMap} implementations.
 * 
 * @author sgibly@appcelerator.com
 */
public interface ISourceMapRegistry
{
	static final String MAP_LOCATION_PREFIX_ATTR = "mapLocationPrefix"; //$NON-NLS-1$
	static final String ORIGINAL_LOCATION_PREFIX_ATTR = "originalLocationPrefix"; //$NON-NLS-1$
	static final String GENERATED_LOCATION_PREFIX_ATTR = "generatedLocationPrefix"; //$NON-NLS-1$

	/**
	 * Returns a source map instance by a primary nature ID of the given project.
	 * 
	 * @param project
	 *            A project that needs a sourcemap.
	 * @return An {@link ISourceMap}. <code>null</code> in case none can be located.
	 */
	ISourceMap getSourceMap(IProject project, String platform);

	/**
	 * Returns a source map instance that was registered for the requested project nature ID.
	 * 
	 * @param projectNatureId
	 *            A project nature identifier.
	 * @return An {@link ISourceMap}. <code>null</code> in case none can be located.
	 */
	ISourceMap getSourceMap(IProject project, String projectNatureId, String platform);
}
