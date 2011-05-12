/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.util.Collection;

import org.eclipse.core.resources.IResource;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;

abstract class AbstractCompareRevisionHandler extends AbstractGitHandler
{

	protected boolean calculateEnabled()
	{
		Collection<IResource> resources = getSelectedResources();
		if (resources == null || resources.isEmpty())
		{
			return false;
		}
		for (IResource blah : resources)
		{
			if (blah == null || blah.getType() != IResource.FILE)
			{
				continue;
			}
			GitRepository repo = getGitRepositoryManager().getAttached(blah.getProject());
			if (repo == null)
			{
				continue;
			}
			ChangedFile file = repo.getChangedFileForResource(blah);
			if (file == null)
			{
				continue;
			}
			if (file.hasStagedChanges() || file.hasUnstagedChanges() || file.hasUnmergedChanges())
			{
				return true;
			}
		}
		return false;
	}

}
