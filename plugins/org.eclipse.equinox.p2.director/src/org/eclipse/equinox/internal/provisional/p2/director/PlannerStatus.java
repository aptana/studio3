/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.provisional.p2.director;

import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.*;

public class PlannerStatus implements IStatus {

	private final IStatus status;
	private final RequestStatus globalRequestStatus;
	private final Map<IInstallableUnit, RequestStatus> requestChanges;
	private final Map<IInstallableUnit, RequestStatus> requestSideEffects;
	private final IQueryable<IInstallableUnit> plannedState;

	private static final IQueryable<IInstallableUnit> EMPTY_IU_QUERYABLE = new IQueryable<IInstallableUnit>() {
		public IQueryResult<IInstallableUnit> query(IQuery<IInstallableUnit> query, IProgressMonitor monitor) {
			return Collector.emptyCollector();
		}
	};

	public PlannerStatus(IStatus status, RequestStatus globalRequestStatus, Map<IInstallableUnit, RequestStatus> requestChanges, Map<IInstallableUnit, RequestStatus> requestSideEffects, IQueryable<IInstallableUnit> plannedState) {
		this.status = status;
		this.globalRequestStatus = globalRequestStatus;
		this.requestChanges = requestChanges;
		this.requestSideEffects = requestSideEffects;
		this.plannedState = (plannedState == null) ? EMPTY_IU_QUERYABLE : plannedState;
	}

	/**
	 * Returns a request status object containing additional global details on the planning of the request
	 * 
	 * @return An IStatus object with global details on the planning process
	 */
	public RequestStatus getRequestStatus() {
		return globalRequestStatus;
	}

	/**
	 * Returns a map of the problems associated with changes to the given installable unit
	 * in this plan. A status with severity {@link IStatus#OK} is returned if the unit
	 * can be provisioned successfully
	 * 
	 * @return A map of {@link IInstallableUnit} to {@link IStatus} of the requested 
	 * changes and their corresponding explanation.
	 */
	public Map<IInstallableUnit, RequestStatus> getRequestChanges() {
		return requestChanges;
	}

	/**
	 * Returns a map of side-effects that will occur as a result of the plan being executed.
	 * Side-effects of an install may include:
	 * <ul>
	 * <li>Optional software being installed that will become satisfied once the plan
	 * is executed.</li>
	 * <li>Optional software currently in the profile that will be uninstalled as a result
	 * of the plan being executed. This occurs when the optional software has dependencies
	 * that are incompatible with the software being installed.
	 * This includes additional software that will be installed as a result of the change,
	 * or optional changes and their corresponding explanation.
	 * @return A map of {@link IInstallableUnit} to {@link IStatus} of the additional side effect
	 * status, or <code>null</code> if there are no side effects.
	 */
	public Map<IInstallableUnit, RequestStatus> getRequestSideEffects() {
		return requestSideEffects;
	}

	/**
	 * Returns the set of InstallableUnits that make up the expected planned state in terms 
	 * of additions and removals to the profile based on the planning process. 
	 * 
	 * @return An IQueryable of the InstallableUnits in the planned state. 
	 */
	public IQueryable<IInstallableUnit> getPlannedState() {
		return plannedState;
	}

	// Remaining Methods Delegate to wrapped Status 
	public IStatus[] getChildren() {
		return status.getChildren();
	}

	public int getCode() {
		return status.getCode();
	}

	public Throwable getException() {
		return status.getException();
	}

	public String getMessage() {
		return status.getMessage();
	}

	public String getPlugin() {
		return status.getPlugin();
	}

	public int getSeverity() {
		return status.getSeverity();
	}

	public boolean isMultiStatus() {
		return status.isMultiStatus();
	}

	public boolean isOK() {
		return status.isOK();
	}

	public boolean matches(int severityMask) {
		return status.matches(severityMask);
	}

}
