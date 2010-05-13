package com.aptana.index.core;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IFileIndexingParticipant
{
	void index(Set<IFile> files, Index index, IProgressMonitor monitor);
}
