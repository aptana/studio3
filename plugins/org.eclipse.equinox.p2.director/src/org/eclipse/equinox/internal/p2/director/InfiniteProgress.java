/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.p2.director;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ProgressMonitorWrapper;

/**
 * This class provides a simulation of progress. This is useful
 * for situations where computing the amount of work to do in advance
 * is too costly.  The monitor will accept any number of calls to
 * {@link #worked(int)}, and will scale the actual reported work appropriately
 * so that the progress never quite completes.
 */
class InfiniteProgress extends ProgressMonitorWrapper {
	/*
	 * Fields for progress monitoring algorithm.
	 * Initially, give progress for every 4 resources, double
	 * this value at halfway point, then reset halfway point
	 * to be half of remaining work.  (this gives an infinite
	 * series that converges at total work after an infinite
	 * number of resources).
	 */
	private int totalWork;
	private int currentIncrement = 4;
	private int halfWay;
	private int nextProgress = currentIncrement;
	private int worked = 0;

	protected InfiniteProgress(IProgressMonitor monitor) {
		super(monitor);
	}

	public void beginTask(String name, int work) {
		super.beginTask(name, work);
		this.totalWork = work;
		this.halfWay = totalWork / 2;
	}

	public void worked(int work) {
		if (--nextProgress <= 0) {
			//we have exhausted the current increment, so report progress
			super.worked(1);
			worked++;
			if (worked >= halfWay) {
				//we have passed the current halfway point, so double the
				//increment and reset the halfway point.
				currentIncrement *= 2;
				halfWay += (totalWork - halfWay) / 2;
			}
			//reset the progress counter to another full increment
			nextProgress = currentIncrement;
		}
	}

}
