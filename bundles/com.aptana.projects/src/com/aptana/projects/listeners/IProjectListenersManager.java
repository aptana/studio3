/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.listeners;

/**
 * @author Chris Williams <cwilliams@appcelerator.com>
 */
public interface IProjectListenersManager
{

	/**
	 * Adds a project listener for a given project nature. Priority controls order it gets called. Project nature may be
	 * set to null for a "global" listener.
	 * 
	 * @param listener
	 * @param priority
	 * @param projectNature
	 * @return
	 */
	public boolean addListener(IStudioProjectListener listener, int priority, String projectNature);

	/**
	 * Returns an array of project listeners ordered based on priority
	 * 
	 * @param projectNature
	 * @return
	 */
	public IStudioProjectListener[] getProjectListeners(String... projectNatures);

	/**
	 * Removes a project listener. Must use the same project nature used to register it. Returns boolean indicating if
	 * listener was found and removed.
	 * 
	 * @param listener
	 * @param projectNature
	 * @return
	 */
	public boolean removeListener(IStudioProjectListener listener, String projectNature);

}