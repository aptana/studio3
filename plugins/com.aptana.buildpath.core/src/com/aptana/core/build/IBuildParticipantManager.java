package com.aptana.core.build;

import java.util.List;

public interface IBuildParticipantManager
{

	/**
	 * Returns an ordered list of the file indexing participants registered for the given filename's associated content
	 * types. Ordering is based on priority.
	 * 
	 * @param contentTypeId
	 * @return
	 */
	List<IBuildParticipant> getBuildParticipants(String contentTypeId);

	/**
	 * Grabs all of the build participants registered. Used to call
	 * {@link IBuildParticipant#clean(org.eclipse.core.resources.IProject, org.eclipse.core.runtime.IProgressMonitor)}
	 * 
	 * @return
	 */
	List<IBuildParticipant> getAllBuildParticipants();

}
