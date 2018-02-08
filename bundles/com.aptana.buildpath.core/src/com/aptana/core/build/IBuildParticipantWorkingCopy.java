/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.build;

/**
 * Provides a wrapper to set preferences on an {@link IBuildParticipant}. The changes are batched up and then applied
 * when {@link #doSave()} is called. Also allows introspection of the queued changes so we can determine if any
 * build/reconcile actions need to take place as a result of the changes.
 * 
 * @author cwilliams
 */
public interface IBuildParticipantWorkingCopy extends IBuildParticipant
{

	/**
	 * Determines if this working copy has changes which would require a rebuild of the workspace (changes in build
	 * enablement, or changes in filters if build is enabled).
	 * 
	 * @return
	 */
	public boolean needsRebuild();

	/**
	 * Determines if this working copy has changes which would require a reconcile of open editors (changes in reconcile
	 * enablement, or changes in filters if reconcile is enabled).
	 * 
	 * @return
	 */
	public boolean needsReconcile();

	/**
	 * Saves all the changes we've made to enablement, filters and generic preferences - persisting them to the prefs.
	 * Once returned, the read-only {@link IBuildParticipant} is returned.
	 * 
	 * @return
	 */
	public IBuildParticipant doSave();

	/**
	 * Returns the {@link IBuildParticipant} that we're wrapping to make bulk changes to.
	 * 
	 * @return
	 */
	public IBuildParticipant getOriginal();

	/**
	 * Generically set a preference value for this participant. Valid value types are String, Integer, Double, Boolean,
	 * Long. All others will be stored as a string using {@link Object#toString()}
	 * 
	 * @param prefKey
	 * @param value
	 */
	public void setPreference(String prefKey, Object value);

	/**
	 * Change enablement if possible.
	 * 
	 * @param enabled
	 */
	public void setEnabled(BuildType type, boolean enabled);

	/**
	 * Sets the filtering regular expressions we use generically to match against warnings/errors messages. If there's a
	 * match, we don't display the warning.error.
	 * 
	 * @param filters
	 */
	public void setFilters(String... filters);
}
