/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.harness;

import org.eclipse.core.runtime.IProgressMonitor;

public abstract class TestProgressMonitor implements IProgressMonitor {

	/**
	 * @see IProgressMonitor#beginTask
	 */
	public void beginTask(String name, int totalWork) {
		//do nothing
	}

	/**
	 * @see IProgressMonitor#done
	 */
	public void done() {
		//do nothing
	}

	public void internalWorked(double work) {
		//do nothing
	}

	/**
	 * @see IProgressMonitor#isCanceled
	 */
	public boolean isCanceled() {
		return false;
	}

	/**
	 * @see IProgressMonitor#setCanceled
	 */
	public void setCanceled(boolean b) {
		//do nothing
	}

	/**
	 * @see IProgressMonitor#setTaskName
	 */
	public void setTaskName(String name) {
		//do nothing
	}

	/**
	 * @see IProgressMonitor#subTask
	 */
	public void subTask(String name) {
		//do nothing
	}

	/**
	 * @see IProgressMonitor#worked
	 */
	public void worked(int work) {
		//do nothing
	}
}
