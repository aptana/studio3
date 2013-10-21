/*******************************************************************************
 *  Copyright (c) 2007, 2010 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.provisional.p2.director;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.ProvisioningContext;
import org.eclipse.equinox.p2.planner.IProfileChangeRequest;

/**
 * Directors are responsible for determining what should be done to a given 
 * profile to reshape it as requested. That is, given the current state of a 
 * profile, a description of the desired end state of that profile and metadata 
 * describing the available IUs, a director produces a list of provisioning 
 * operations (e.g., install, update or uninstall) to perform on the related IUs. 
 * Directors are also able to validate profiles and assist in the diagnosis of 
 * configuration errors. Note that directors may range in complexity from 
 * very simple (e.g., reading a list of bundles from a static file) to very complex. 
 */
public interface IDirector {

	/**
	 * Service name constant for the director service.
	 */
	public static final String SERVICE_NAME = IDirector.class.getName();

	/**
	 * performs the change request with the given context.
	 * 
	 * @param profileChangeRequest The change request
	 * @param context The provisioning context used for finding resources
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting is not desired
	 */
	public IStatus provision(IProfileChangeRequest profileChangeRequest, ProvisioningContext context, IProgressMonitor monitor);

	/**
	 * Reverts the profile to a previous state described in the target revertProfile.
	 * 
	 * @param profile The profile to revert
	 * @param revertProfile The profile snapshot state to revert to
	 * @param context The provisioning context used for finding resources
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting is not desired
	 */
	public IStatus revert(IProfile profile, IProfile revertProfile, ProvisioningContext context, IProgressMonitor monitor);
}
