/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.performance;

import org.eclipse.test.internal.performance.InternalDimensions;

/**
 * Some predefined dimensions most likely supported on all platforms.
 *
 * This interface is not intended to be implemented by clients.
 * 
 * @since 3.1
 */
public interface Dimension {

    // Dimensions available on all platforms:
    
    /**
     * The amount of time that the process has executed in kernel mode.
     * It is calculated by taking the sum of the time that each of the threads of the process has executed in kernel mode.
     */
    public Dimension KERNEL_TIME= InternalDimensions.KERNEL_TIME;
    
    /**
     * The amount of CPU time used so far by this process.
     * It is calculated by adding the KERNEL_TIME and the amount of time that the process has executed in user mode.
     * The user time is calculated by taking the sum of the time that each of the threads of the process has executed in user mode.
     * It does not include any time where the process is waiting for OS resources.
     * It is the best approximation for ELAPSED_PROCESS (which is not available on all platforms).
     */
    public Dimension CPU_TIME= InternalDimensions.CPU_TIME;
    
    /**
     * WORKING_SET is the amount of memory in the working set of this process.
     * The working set is the set of memory pages touched recently by the threads in the process.
     * If free memory in the computer is above a threshold, pages are left in the working set of a process
     * even if they are not in use. When free memory falls below a threshold, pages are removed from
     * working sets.
     */
    public Dimension WORKING_SET= InternalDimensions.WORKING_SET;

    /**
     * The total elapsed time this process has been running.
     * Since it starts at 0 on process start it can be used to measure startup time.
     * On Windows it is calculated by subtracting the creation time of the process from the current system time.
     * On Linux it is calculated by subtracting the value of the system property "eclipse.startTime" from
     * System.currentTimeMillis()
     * Please note that in contrast to the CPU_TIME the elapsed time of a process is influenced by other
     * processes running in parallel.
     */
    public Dimension ELAPSED_PROCESS= InternalDimensions.ELAPSED_PROCESS;

    /**
     * The amount of memory used in the JVM.
     * It is calculated by subtracting <code>Runtime.freeMemory()</code> from <code>Runtime.totalMemory()</code>.
     */
    public Dimension USED_JAVA_HEAP= InternalDimensions.USED_JAVA_HEAP;

    // the following Dimensions not available on all platforms!
    
    /**
	 * WORKING_SET_PEAK is the maximum amount of memory in the working set of this process at any point in time.
	 * The working set is the set of memory pages touched recently by the threads in the process.
	 * If free memory in the computer is above a threshold, pages are left in the working set of a process
	 * even if they are not in use. When free memory falls below a threshold, pages are removed from working sets.
     * Currently this dimension is only available on Windows.
     */
    public Dimension WORKING_SET_PEAK= InternalDimensions.WORKING_SET_PEAK;

    /**
     * The total amount of committed memory (for the entire machine).
     * Committed memory is the size of virtual memory that has been committed (as opposed to simply reserved).
     * Committed memory must have backing (i.e., disk) storage available, or must be assured never to need disk
     * storage (because main memory is large enough to hold it.) Notice that this is an instantaneous count,
     * not an average over the time interval.
     * Currently this dimension is only available on Windows.
     */
    public Dimension COMITTED= InternalDimensions.COMITTED;
}
