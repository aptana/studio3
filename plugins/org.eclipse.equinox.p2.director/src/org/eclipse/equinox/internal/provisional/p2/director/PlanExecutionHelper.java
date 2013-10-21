/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.provisional.p2.director;

import java.io.IOException;
import org.eclipse.core.runtime.*;
import org.eclipse.equinox.internal.p2.core.helpers.ServiceHelper;
import org.eclipse.equinox.internal.p2.director.DirectorActivator;
import org.eclipse.equinox.internal.p2.director.Messages;
import org.eclipse.equinox.internal.provisional.configurator.Configurator;
import org.eclipse.equinox.p2.engine.*;

public class PlanExecutionHelper {
	public static IStatus executePlan(IProvisioningPlan result, IEngine engine, ProvisioningContext context, IProgressMonitor progress) {
		return executePlan(result, engine, PhaseSetFactory.createDefaultPhaseSet(), context, progress);
	}

	public static IStatus executePlan(IProvisioningPlan result, IEngine engine, IPhaseSet phaseSet, ProvisioningContext context, IProgressMonitor progress) {
		if (!result.getStatus().isOK())
			return result.getStatus();

		if (result.getInstallerPlan() != null) {
			IStatus installerPlanStatus = ((IEngine) result.getInstallerPlan().getProfile().getProvisioningAgent().getService(IEngine.SERVICE_NAME)).perform(result.getInstallerPlan(), phaseSet, progress);
			if (!installerPlanStatus.isOK())
				return installerPlanStatus;
			Configurator configChanger = (Configurator) ServiceHelper.getService(DirectorActivator.context, Configurator.class.getName());
			try {
				configChanger.applyConfiguration();
			} catch (IOException e) {
				return new Status(IStatus.ERROR, DirectorActivator.PI_DIRECTOR, Messages.Director_error_applying_configuration, e);
			}
		}
		return engine.perform(result, phaseSet, progress);
	}
}
