/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     IBM Corporation - initial API and implementation
 *     Sonatype, Inc. - ongoing development
 ******************************************************************************/
package org.eclipse.equinox.internal.p2.director;

import java.util.Collection;
import org.eclipse.core.runtime.*;
import org.eclipse.equinox.internal.provisional.p2.director.IDirector;
import org.eclipse.equinox.internal.provisional.p2.director.PlanExecutionHelper;
import org.eclipse.equinox.p2.engine.*;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.planner.IPlanner;
import org.eclipse.equinox.p2.planner.IProfileChangeRequest;
import org.eclipse.osgi.util.NLS;

public class SimpleDirector implements IDirector {
	static final int PlanWork = 10;
	static final int EngineWork = 100;
	private IEngine engine;
	private IPlanner planner;

	public SimpleDirector(IEngine engine, IPlanner planner) {
		if (engine == null)
			throw new IllegalStateException("Provisioning engine is not registered"); //$NON-NLS-1$
		this.engine = engine;
		if (planner == null)
			throw new IllegalStateException("Unable to find provisioning planner"); //$NON-NLS-1$
		this.planner = planner;
	}

	public IStatus revert(IProfile currentProfile, IProfile revertProfile, ProvisioningContext context, IProgressMonitor monitor) {
		SubMonitor sub = SubMonitor.convert(monitor, Messages.Director_Task_Updating, PlanWork + EngineWork);
		try {
			IProvisioningPlan plan = planner.getDiffPlan(currentProfile, revertProfile, sub.newChild(PlanWork));
			return PlanExecutionHelper.executePlan(plan, engine, context, sub.newChild(EngineWork));
		} finally {
			sub.done();
		}
	}

	public IStatus provision(IProfileChangeRequest request, ProvisioningContext context, IProgressMonitor monitor) {
		String taskName = NLS.bind(Messages.Director_Task_Installing, ((ProfileChangeRequest) request).getProfile().getProperty(IProfile.PROP_INSTALL_FOLDER));
		SubMonitor sub = SubMonitor.convert(monitor, taskName, PlanWork + EngineWork);
		try {
			Collection<IInstallableUnit> installRoots = request.getAdditions();
			// mark the roots as such
			for (IInstallableUnit root : installRoots) {
				request.setInstallableUnitProfileProperty(root, IProfile.PROP_PROFILE_ROOT_IU, Boolean.toString(true));
			}
			IProvisioningPlan plan = planner.getProvisioningPlan(request, context, sub.newChild(PlanWork));
			return PlanExecutionHelper.executePlan(plan, engine, context, sub.newChild(EngineWork));
		} finally {
			sub.done();
		}
	}
}
