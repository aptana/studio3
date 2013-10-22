/*******************************************************************************
 * Copyright (c) 2009-2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sonatype, Inc. - ongoing development
 *******************************************************************************/
package org.eclipse.equinox.internal.p2.director;

import org.eclipse.equinox.p2.planner.IPlanner;

import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.spi.IAgentServiceFactory;
import org.eclipse.equinox.p2.engine.IEngine;

public class DirectorComponent implements IAgentServiceFactory {

	public Object createService(IProvisioningAgent agent) {
		IEngine engine = (IEngine) agent.getService(IEngine.SERVICE_NAME);
		IPlanner planner = (IPlanner) agent.getService(IPlanner.SERVICE_NAME);
		return new SimpleDirector(engine, planner);
	}

}
