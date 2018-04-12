/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.actions;

import java.util.List;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;

public class UnstageHandler extends AbstractStagingHandler
{

	@Override
	protected void doOperation(GitRepository repo, List<ChangedFile> changedFiles)
	{
		repo.index().unstageFiles(changedFiles);
	}

	@Override
	protected boolean changedFileIsValid(ChangedFile correspondingChangedFile)
	{
		return correspondingChangedFile != null && correspondingChangedFile.hasStagedChanges();
	}

}
