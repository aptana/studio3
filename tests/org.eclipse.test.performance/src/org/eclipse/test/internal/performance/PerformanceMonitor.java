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

import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.data.Scalar;


class PerformanceMonitor {
    
    private static PerformanceMonitor fgPerformanceMonitor;

    public static PerformanceMonitor getPerformanceMonitor() {
		if (fgPerformanceMonitor == null) {
		    String os= System.getProperty("os.name"); //$NON-NLS-1$
		    if (os.startsWith("Windows")) //$NON-NLS-1$
		        fgPerformanceMonitor= new PerformanceMonitorWindows();
		    else if (os.startsWith("Mac OS X")) //$NON-NLS-1$
                fgPerformanceMonitor= new PerformanceMonitorMac();
            else
                fgPerformanceMonitor= new PerformanceMonitorLinux();
		}
		return fgPerformanceMonitor;
    }

    protected void collectOperatingSystemCounters(Map scalars) {
        if (PerformanceTestPlugin.isOldDB()) {
            addScalar(scalars, InternalDimensions.SYSTEM_TIME, System.currentTimeMillis());
        } else {
            Runtime runtime= Runtime.getRuntime();
            //runtime.gc();
            addScalar(scalars, InternalDimensions.USED_JAVA_HEAP, runtime.totalMemory() - runtime.freeMemory());
        }
    }

	protected void collectGlobalPerformanceInfo(Map scalars) {
		// no default implementation
	}
	
    void addScalar(Map scalars, Dim dimension, long value) {
        scalars.put(dimension, new Scalar(dimension, value));
    }
}
