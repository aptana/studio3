package com.aptana.ide.red.git.ui.internal.actions;

import java.util.List;

import com.aptana.ide.red.git.model.ChangedFile;
import com.aptana.ide.red.git.model.GitRepository;

public class StageAction extends StagingAction
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
