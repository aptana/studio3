/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
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