/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.build;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IBuildParticipant
{

	public void clean(URI uri, IProgressMonitor monitor);

	public void fullBuild(URI uri, IProgressMonitor monitor);

	public void incrementalBuild(IResourceDelta delta, IProject project, IProgressMonitor monitor);
}
