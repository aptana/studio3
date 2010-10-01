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

/**
 * Null performance meter. 
 */
public class NullPerformanceMeter extends PerformanceMeter {

	/*
	 * @see org.eclipse.test.performance.PerformanceMeter#start()
	 */
	public void start() {
		// do nothing
	}

	/*
	 * @see org.eclipse.test.performance.PerformanceMeter#stop()
	 */
	public void stop() {
		// do nothing
	}

	/*
	 * @see org.eclipse.test.performance.PerformanceMeter#commit()
	 */
	public void commit() {
		// do nothing
	}

	/*
	 * @see org.eclipse.test.performance.PerformanceMeter#dispose()
	 */
	public void dispose() {
		// do nothing
	}
}
