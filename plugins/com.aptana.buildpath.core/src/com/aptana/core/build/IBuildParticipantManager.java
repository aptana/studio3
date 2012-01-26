/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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

	List<IBuildParticipant> filterParticipants(List<IBuildParticipant> participants, String contentType);

}
