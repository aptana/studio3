/*******************************************************************************
 *  Copyright (c) 2007, 2010 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sonatype, Inc. - ongoing development
 *******************************************************************************/
package org.eclipse.equinox.p2.planner;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.p2.engine.*;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.IQueryResult;

/**
 * Planners are responsible for determining what should be done to a given 
 * profile to reshape it as requested. That is, given the current state of a 
 * profile, a description of the desired changes to that profile and metadata 
 * describing the available installable units, a planner produces a concrete plan that lists the
 * exact steps that the engine should perform.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 2.0
 */
public interface IPlanner {
	/**
	 * Service name constant for the planner service.
	 */
	public static final String SERVICE_NAME = IPlanner.class.getName();

	/**
	 * Returns a plan describing the set of changes that must be performed to
	 * satisfy the given profile change request.
	 * 
	 * @param profileChangeRequest the request to be evaluated
	 * @param context the context in which the request is processed
	 * @param monitor a monitor on which planning
	 * @return the plan representing the system that needs to be
	 */
	public IProvisioningPlan getProvisioningPlan(IProfileChangeRequest profileChangeRequest, ProvisioningContext context, IProgressMonitor monitor);

	public IProvisioningPlan getDiffPlan(IProfile currentProfile, IProfile targetProfile, IProgressMonitor monitor);

	public IProfileChangeRequest createChangeRequest(IProfile profileToChange);

	/**
	 * @noreference This method is not intended to be referenced by clients. 
	 * You may want to consider using the org.eclipse.equinox.p2.operations.UpdateOperation class instead. 
	 */
	public IQueryResult<IInstallableUnit> updatesFor(IInstallableUnit iu, ProvisioningContext context, IProgressMonitor monitor);
}
