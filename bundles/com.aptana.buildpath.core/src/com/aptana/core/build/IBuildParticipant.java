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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;

import com.aptana.index.core.build.BuildContext;

/**
 * @author cwilliams
 */
public interface IBuildParticipant
{

	enum BuildType
	{
		BUILD, RECONCILE;
	}

	/**
	 * Clean is called on a whole project. This method is called on every build participant regardless of the content
	 * type bindings. As a result, implementations are expected to introspect on the {@link IProject} to determine if
	 * any operations need to be performed. (i.e. traverse the folders and find any files they need to operate on)
	 * 
	 * @param project
	 * @param monitor
	 */
	public void clean(IProject project, IProgressMonitor monitor);

	/**
	 * Called before we run any build, so we can do any sort of pre-init/batch work.
	 * 
	 * @param kind
	 * @param monitor
	 */
	public void buildStarting(IProject project, int kind, IProgressMonitor monitor);

	/**
	 * Called after we finish doing the per-file processing in the build.
	 * 
	 * @param monitor
	 */
	public void buildEnding(IProgressMonitor monitor);

	/**
	 * Grab the priority of the build participant. Used to order how they get called during build process.
	 * 
	 * @return
	 */
	public int getPriority();

	/**
	 * Called on an individual file. For incremental builds we traverse the diff and call this for every updated/added
	 * file. For full builds we traverse the project to collect the files and call this once per file.
	 * 
	 * @param context
	 * @param monitor
	 */
	public void buildFile(BuildContext context, IProgressMonitor monitor);

	/**
	 * Called on an individual file. For incremental builds we traverse the diff and call this for every updated/added
	 * file.
	 * 
	 * @param context
	 * @param monitor
	 */
	public void deleteFile(BuildContext context, IProgressMonitor monitor);

	/**
	 * Returns the set of {@link IContentType}s that this participant is registered for.
	 * 
	 * @return
	 */
	public Set<IContentType> getContentTypes();

	/**
	 * Returns the display name.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Returns the unique id string used to identify this participant. This is not unique per instance of this type.
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Is this participant enabled?
	 * 
	 * @return
	 */
	public boolean isEnabled(BuildType type);

	/**
	 * Tells the participant to restore it's default enablement state and possibly reset any participant specified
	 * settings (like filters for warnings).
	 */
	public void restoreDefaults();

	/**
	 * Some participants are required - meaning they cannot be disabled.
	 * 
	 * @return
	 */
	public boolean isRequired();

	/**
	 * Returns the list of filters.
	 * 
	 * @return
	 */
	public List<String> getFilters();

	/**
	 * Determine if this participant is valid for the given project. Subclasses should use behavior from
	 * {@link AbstractBuildParticipant}, which checks against the project natures this participant is bound to.
	 * 
	 * @param project
	 * @return
	 */
	public boolean isEnabled(IProject project);

	/**
	 * Gets a working copy so we can modify preferences/attributes and apply them all at once.
	 * 
	 * @return
	 */
	public IBuildParticipantWorkingCopy getWorkingCopy();

	/**
	 * @param prefKey
	 * @return
	 */
	public String getPreferenceString(String prefKey);

	/**
	 * @param prefKey
	 * @return
	 */
	public boolean getPreferenceBoolean(String prefKey);

	/**
	 * @param prefKey
	 * @param defaultValue
	 * @return
	 */
	public int getPreferenceInt(String prefKey, int defaultValue);
}
