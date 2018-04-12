/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.build;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.content.IContentType;

/**
 * @author cwilliams
 */
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

	/**
	 * Given a list of existing participant instances, this filters them down to those that apply to the given
	 * contentTypeId
	 * 
	 * @param participants
	 * @param contentTypeId
	 * @return
	 */
	List<IBuildParticipant> filterParticipants(List<? extends IBuildParticipant> participants, String contentTypeId);

	/**
	 * Returns the set of all explicitly registered content types across all registered build participants.
	 * 
	 * @return
	 */
	Set<IContentType> getContentTypes();

}
