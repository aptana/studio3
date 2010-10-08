/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.test.internal.performance;

import org.eclipse.test.performance.PerformanceMeter;

public class SystemTimePerformanceMeterFactory extends PerformanceMeterFactory {

	protected PerformanceMeter doCreatePerformanceMeter(String scenario) {
		return new SystemTimePerformanceMeter(scenario);
	}
}
