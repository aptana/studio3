/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import com.aptana.git.core.model.GitRepository;

public class PullHandler extends AbstractSimpleGitCommandHandler
{
	// FIXME Use GitRepository.pull!

	@Override
	protected String[] getCommand()
	{
		return new String[] { "pull" }; //$NON-NLS-1$
	}

	@Override
	protected void postLaunch(GitRepository repo)
	{
		repo.firePullEvent();
	}

	@Override
	protected boolean calculateEnabled()
	{
		for (GitRepository repo : getSelectedRepositories())
		{
			if (repo == null)
			{
				continue;
			}
			// TODO Explicitly check if there's any remote tracking branches?
			// Just check if we have any remotes to pull from
			if (!repo.remotes().isEmpty())
			{
				return true;
			}
		}
		return false;
	}

	protected boolean supportsMultipleRepoOperation()
	{
		return true;
	}

}
