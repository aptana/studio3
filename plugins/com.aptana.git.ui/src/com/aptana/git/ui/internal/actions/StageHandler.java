package com.aptana.git.ui.internal.actions;

import java.util.List;

import com.aptana.git.core.model.ChangedFile;
import com.aptana.git.core.model.GitRepository;

public class StageHandler extends AbstractStagingHandler
{

	@Override
	protected void doOperation(GitRepository repo, List<ChangedFile> changedFiles)
	{
		repo.index().stageFiles(changedFiles);
	}

	@Override
	protected boolean changedFileIsValid(ChangedFile correspondingChangedFile)
	{
		return correspondingChangedFile != null && correspondingChangedFile.hasUnstagedChanges();
	}

}
