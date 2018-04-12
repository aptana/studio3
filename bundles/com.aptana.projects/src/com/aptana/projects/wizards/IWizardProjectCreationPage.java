/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.wizards;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public interface IWizardProjectCreationPage extends IDialogPage, IWizardPage
{

	/**
	 * Creates a project resource handle for the current project name field value. The project handle is created
	 * relative to the workspace root.
	 * <p>
	 * This method does not create the project resource; this is the responsibility of <code>IProject::create</code>
	 * invoked by the new project resource wizard.
	 * </p>
	 * 
	 * @return the new project resource handle
	 */
	IProject getProjectHandle();

	/**
	 * Returns the useDefaults.
	 * 
	 * @return boolean
	 */
	boolean useDefaults();

	/**
	 * Returns the current project location URI as entered by the user, or <code>null</code> if a valid project location
	 * has not been entered.
	 * 
	 * @return the project location URI, or <code>null</code>
	 */
	URI getLocationURI();

	/**
	 * Returns the current project location path as entered by the user, or its anticipated initial value. Note that if
	 * the default has been returned the path in a project description used to create a project should not be set.
	 * 
	 * @return the project location path or its anticipated initial value.
	 */
	IPath getLocationPath();

	/**
	 * Sets whether this page is complete.
	 * <p>
	 * This information is typically used by the wizard to decide when it is okay to move on to the next page or finish
	 * up.
	 * </p>
	 * 
	 * @param complete
	 *            <code>true</code> if this page is complete, and and <code>false</code> otherwise
	 * @see #isPageComplete()
	 */
	void setPageComplete(boolean b);

	/**
	 * Returns true if the project creation page specifically defines that this project should be cloned from a GIT
	 * repository.
	 * 
	 * @return True, if this project should be cloned from a repository; False, otherwise.
	 * @see #getCloneURI()
	 */
	boolean isCloneFromGit();

	/**
	 * Returns a GIT clone URI string.<br>
	 * This method returns a valid URI when the {@link #isCloneFromGit()} is <code>true</code>.
	 * 
	 * @return A GIT URI string.
	 * @see #isCloneFromGit()
	 */
	String getCloneURI();

}
