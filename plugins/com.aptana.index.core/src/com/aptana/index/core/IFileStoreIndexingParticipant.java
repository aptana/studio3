package com.aptana.index.core;

import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IFileStoreIndexingParticipant
{
	void index(Set<IFileStore> resources, Index index, IProgressMonitor monitor) throws CoreException;
}
