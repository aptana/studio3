/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core;

import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;

class IndexProjectJob extends IndexContainerJob
{

	protected IndexProjectJob(IProject project)
	{
		super(MessageFormat.format(Messages.IndexProjectJob_Name, project.getName()), project.getLocationURI());
	}
}