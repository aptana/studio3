/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.test.internal.performance;

import java.util.Map;

import org.eclipse.test.internal.performance.PerformanceMonitor;

/**
 * The PerformanceMonitor for Windows.
 */
class PerformanceMonitorWindows extends PerformanceMonitor {
    		
	
	private boolean fgNativeGetPerformanceInfoNotAvailable;

    protected void collectOperatingSystemCounters(Map scalars) {
		synchronized(this) {
			if (org.eclipse.perfmsr.core.PerformanceMonitor.isLoaded()) {
				long[] counters= new long[14];
				if (org.eclipse.perfmsr.core.PerformanceMonitor.nativeGetPerformanceCounters(counters)) {
					addScalar(scalars, InternalDimensions.WORKING_SET, counters[0]);
					addScalar(scalars, InternalDimensions.WORKING_SET_PEAK, counters[1]);
					addScalar(scalars, InternalDimensions.ELAPSED_PROCESS, counters[2]);
					//addScalar(scalars, InternalDimensions.USER_TIME, counters[3]);
					addScalar(scalars, InternalDimensions.KERNEL_TIME, counters[4]);
	                addScalar(scalars, InternalDimensions.CPU_TIME, counters[3] + counters[4]);
					addScalar(scalars, InternalDimensions.PAGE_FAULTS, counters[5]);
					if (counters[6] != -1)
						addScalar(scalars, InternalDimensions.COMITTED, counters[6]);
					addScalar(scalars, InternalDimensions.GDI_OBJECTS, counters[7]);
					//addScalar(scalars, Dimensions.USER_OBJECTS, counters[8]);
					//if (counters[9] != -1)
					//	addScalar(scalars, InternalDimensions.OPEN_HANDLES, counters[9]);
					//addScalar(scalars, InternalDimensions.READ_COUNT, counters[10]);
					//addScalar(scalars, InternalDimensions.WRITE_COUNT, counters[11]);
					//addScalar(scalars, InternalDimensions.BYTES_READ, counters[12]);
					//addScalar(scalars, InternalDimensions.BYTES_WRITTEN, counters[13]);
				}
			}
		    super.collectOperatingSystemCounters(scalars);
		}
    }

	/**
	 * Collect global performance info. This includes things like the total
	 * committed memory for the entire system.
	 * 
	 * This function depends on the GetPerformanceInfo() function being available in
	 * the Windows ivjperf.dll. This is available in XP but is usually not available
	 * in Win2000. If it is not available then this function throws an UnsupportedOperationException.
	 * @param scalars
	 */
	protected void collectGlobalPerformanceInfo(Map scalars) {
		synchronized(this) {
			if (!fgNativeGetPerformanceInfoNotAvailable && org.eclipse.perfmsr.core.PerformanceMonitor.isLoaded()) {
		    	long[] counters= new long[13];
				try {
					org.eclipse.perfmsr.core.PerformanceMonitor.nativeGetPerformanceInfo(counters);
					long pageSize= counters[9];
					addScalar(scalars, InternalDimensions.COMMIT_TOTAL, counters[0]*pageSize);
					addScalar(scalars, InternalDimensions.COMMIT_LIMIT, counters[1]*pageSize); 
					addScalar(scalars, InternalDimensions.COMMIT_PEAK, counters[2]*pageSize);
					addScalar(scalars, InternalDimensions.PHYSICAL_TOTAL, counters[3]*pageSize); 
					addScalar(scalars, InternalDimensions.PHYSICAL_AVAIL, counters[4]*pageSize); 
					addScalar(scalars, InternalDimensions.SYSTEM_CACHE, counters[5]*pageSize);
					addScalar(scalars, InternalDimensions.KERNEL_TOTAL, counters[6]*pageSize); 
					addScalar(scalars, InternalDimensions.KERNEL_PAGED, counters[7]*pageSize); 
					addScalar(scalars, InternalDimensions.KERNEL_NONPAGED, counters[8]*pageSize); 
					addScalar(scalars, InternalDimensions.PAGE_SIZE, counters[9]);
					addScalar(scalars, InternalDimensions.HANDLE_COUNT, counters[10]); 
					addScalar(scalars, InternalDimensions.PROCESS_COUNT, counters[11]); 
					addScalar(scalars, InternalDimensions.THREAD_COUNT, counters[12]);
				} catch (Exception e) {
				    PerformanceTestPlugin.logWarning("native function GetPerformanceInfo() not available on this version of Windows"); //$NON-NLS-1$
					fgNativeGetPerformanceInfoNotAvailable= true;
				}
			}
		    super.collectGlobalPerformanceInfo(scalars);
		}
	}
	
//	protected String getUUID() {
//		if (org.eclipse.perfmsr.core.PerformanceMonitor.isLoaded()) {
//			try {
//				return org.eclipse.perfmsr.core.PerformanceMonitor.nativeGetUUID();
//			} catch (Exception e) {
//			}
//		}
//		return super.getUUID();
//	}
}
