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

import org.eclipse.test.internal.performance.data.Dim;
import org.eclipse.test.internal.performance.data.Unit;

/**
 * Some hard coded measurement id's.
 */
public interface InternalDimensions {
    
//  Common:
//		OS Counters:
    		Dim
			SYSTEM_TIME= new Dim(2, Unit.SECOND, 1000), 		// System.currentTimeMillis. "System Time"
			USED_JAVA_HEAP= new Dim(3, Unit.BYTE), 			// Runtime.totalMemory() - Runtime.freeMemory()
    			WORKING_SET= new Dim(4, Unit.BYTE), 				// the working set (or on Linux the resident set). "Working Set"	
//    		USER_TIME= new Dim(10, Unit.SECOND, 1000),			// the amount of elapsed user time. "User time"
    			KERNEL_TIME= new Dim(11, Unit.SECOND, 1000),		// the amount of elapsed kernel time. "Kernel time"
    			CPU_TIME= new Dim(20, Unit.SECOND, 1000), 			// the amount of CPU time we have used so far. "CPU Time"
    			INVOCATION_COUNT= new Dim(52, Unit.INVOCATION, 1);	// the number of method invocations. "Invocation Count"

//  	OS Info:
    		Dim
    			PHYSICAL_TOTAL= new Dim(24, Unit.BYTE),			// the amount of physical memory in bytes. "Physical Memory"
			SYSTEM_CACHE= new Dim(26, Unit.BYTE);				// the amount of system cache memory in bytes. "System Cache"

//	Windows:
//		OS Counters:
    		Dim
			COMITTED= new Dim(7, Unit.BYTE),					// "Committed"
    			WORKING_SET_PEAK= new Dim(8, Unit.BYTE),			// "Working Set Peak"
    			ELAPSED_PROCESS= new Dim(9, Unit.SECOND, 1000),	// "Elapsed Process"
    			PAGE_FAULTS= new Dim(19), 						// "Page Faults"
    			GDI_OBJECTS= new Dim(34), 						// "GDI Objects"
    			USER_OBJECTS= new Dim(35), 						// "USER Objects"
    			OPEN_HANDLES= new Dim(36), 						// "Open Handles"
    			READ_COUNT= new Dim(38, Unit.BYTE), 				// "Read Count"
    			WRITE_COUNT= new Dim(39, Unit.BYTE),				// "Write Count"
    			BYTES_READ= new Dim(40, Unit.BYTE), 				// "Bytes Read"
    			BYTES_WRITTEN= new Dim(41, Unit.BYTE); 			// "Bytes Written"
    			
//    	OS Info:
    		Dim
    			COMMIT_LIMIT= new Dim(22),				// "Commit Limit"
    			COMMIT_PEAK= new Dim(23),					// "Commit Peak"
    			PHYSICAL_AVAIL= new Dim(25, Unit.BYTE), 	// "Physical Available"
    			KERNEL_TOTAL= new Dim(27),				// "Kernel Total"
    			KERNEL_PAGED= new Dim(28),				// "Kernel Paged"
    			KERNEL_NONPAGED= new Dim(29), 			// "Kernel Nonpaged"
    			PAGE_SIZE= new Dim(30, Unit.BYTE),		// "Page Size"
    			HANDLE_COUNT= new Dim(31),				// "Handle Count"
    			PROCESS_COUNT= new Dim(32),				// "Process Count"
    			THREAD_COUNT= new Dim(33),				// "Thread Count"
			COMMIT_TOTAL= new Dim(37);				// "Commit Total"
    		
// Linux:
//    	OS Counters:
    		Dim
			HARD_PAGE_FAULTS= new Dim(42),	// the number of hard page faults. A page had to be fetched from disk. "Hard Page Faults"
    			SOFT_PAGE_FAULTS= new Dim(43),	// the number of soft page faults. A page was not fetched from disk. "Soft Page Faults"		
    			TRS= new Dim(44, Unit.BYTE),		// the amount of memory in bytes occupied by text (i.e. code). "Text Size"	
    			DRS= new Dim(45, Unit.BYTE),		// the amount of memory in bytes occupied by data or stack. "Data Size"
    			LRS= new Dim(46, Unit.BYTE);		// the amount of memory in bytes occupied by shared code. "Library Size"
    			
//  	OS Info:
    		Dim
    			USED_LINUX_MEM= new Dim(48, Unit.BYTE),	// the amount of memory that Linux reports is used. From /proc/meminfo. "Used Memory"
    			FREE_LINUX_MEM= new Dim(49, Unit.BYTE),	// the amount of memory that Linux reports is free. From /proc/meminfo. "Free Memory"
    			BUFFERS_LINUX= new Dim(50, Unit.BYTE);	// the amount of memory that Linux reports is used by buffers. From /proc/meminfo. "Buffers Memory"
    	
// Mac:
//		OS Counters:
//		OS Info:
}
