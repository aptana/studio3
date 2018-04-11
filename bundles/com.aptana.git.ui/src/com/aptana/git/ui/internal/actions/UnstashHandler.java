/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import com.aptana.git.core.model.GitRepository;

public class UnstashHandler extends AbstractSimpleGitCommandHandler
{

	@Override
	protected String[] getCommand()
	{
		return new String[] { "stash", "apply" }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected void postLaunch(GitRepository repo)
	{
		// refreshAffectedProjects(repo); // Should be handled by filewatcher?
	}
	// TODO Only enable if there's a "ref/stash" ref!
}
