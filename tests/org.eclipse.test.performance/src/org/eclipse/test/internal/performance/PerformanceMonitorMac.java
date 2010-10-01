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

/**
 * The Mac OS X version of the performance monitor.
 * (Uses default implementation for now).
 */
class PerformanceMonitorMac extends PerformanceMonitor {

	private static boolean fgHasElapsedTime= true;
	private static long fgStartupTime;

	/** 
	 * name of the library that implements the native methods.
	 */
	private static final String NATIVE_LIBRARY_NAME= "perf_3_1_0"; //$NON-NLS-1$
	
	/**
	 * Is the native library loaded? 0-don't know, 1-no, 2-yes
	 */
	private static int fgIsLoaded= 0;
	
	/**
	 * Answer true if the native library for this class has been successfully
	 * loaded. If the load has not been attempted yet, try to load it.
	 * @return returns true if native library has been successfullz loaded
	 */
	public static boolean isLoaded() {
		if (fgIsLoaded == 0) {
			try {
				System.loadLibrary(NATIVE_LIBRARY_NAME);
				fgIsLoaded= 2;
			} catch (Throwable e) {
				//e.printStackTrace();
			    //PerformanceTestPlugin.log(e);
			    fgIsLoaded= 1;
			}
		}
		return fgIsLoaded == 2;
	}
	
    
	/**
	 * Write out operating system counters for Mac OS X.
	 * @param scalars where to collect the data
	 */
	protected void collectOperatingSystemCounters(Map scalars) {
		synchronized(this) {
		    if (isLoaded()) {
				int[] counters= new int[18];
				if (getrusage(0, counters) == 0) {
				    
				    int user_time= counters[0]*1000 + counters[1]/1000;
				    int kernel_time= counters[2]*1000 + counters[3]/1000;
				    
					addScalar(scalars, InternalDimensions.KERNEL_TIME, kernel_time);
					addScalar(scalars, InternalDimensions.CPU_TIME, user_time + kernel_time);
				}
			}
			
			long currentTime= System.currentTimeMillis();
			if (!PerformanceTestPlugin.isOldDB())
				addScalar(scalars, InternalDimensions.SYSTEM_TIME, currentTime);
			
			if (fgHasElapsedTime) {
				if (fgStartupTime == 0) {
					String t= System.getProperty("eclipse.startTime"); //$NON-NLS-1$
					if (t != null) {
						try {
							fgStartupTime= Long.parseLong(t);
						} catch (NumberFormatException e) {
							fgHasElapsedTime= false;
						}
					} else
						fgHasElapsedTime= false;
				}
				if (fgHasElapsedTime)
					addScalar(scalars, InternalDimensions.ELAPSED_PROCESS, currentTime-fgStartupTime);
			}
			
		    super.collectOperatingSystemCounters(scalars);
		}
	}

//		struct rusage {
//	0,1		struct timeval ru_utime; /* user time used */
//	2,3		struct timeval ru_stime; /* system time used */
//	4		long ru_maxrss;          /* integral max resident set size */
//	5		long ru_ixrss;           /* integral shared text memory size */
//	6		long ru_idrss;           /* integral unshared data size */
//	7		long ru_isrss;           /* integral unshared stack size */
//	8		long ru_minflt;          /* page reclaims */
//	9		long ru_majflt;          /* page faults */
//	10		long ru_nswap;           /* swaps */
//	11		long ru_inblock;         /* block input operations */
//	12		long ru_oublock;         /* block output operations */
//	13		long ru_msgsnd;          /* messages sent */
//	14		long ru_msgrcv;          /* messages received */
//	15		long ru_nsignals;        /* signals received */
//	16		long ru_nvcsw;           /* voluntary context switches */
//	17		long ru_nivcsw;          /* involuntary context switches */
//		};
	private static native int getrusage(int who, int[] counters);
}
