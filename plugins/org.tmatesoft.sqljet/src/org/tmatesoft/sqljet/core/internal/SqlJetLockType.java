/**
 * SqlJetLockType.java
 * Copyright (C) 2008 TMate Software Ltd
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For information on how to redistribute this software under
 * the terms of a license other than GNU General Public License
 * contact TMate Software at support@sqljet.com
 */
package org.tmatesoft.sqljet.core.internal;

/**
 * Transaction locks types.
 * 
 * PENDING lock may not be passed directly to lock(). Instead, a
 * process that requests an EXCLUSIVE lock may actually obtain a PENDING
 * lock. This can be upgraded to an EXCLUSIVE lock by a subsequent call to
 * lock().
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 *
 */
public enum SqlJetLockType {
    
    /**
     *  Not locked
     */
    NONE,
    
    /**
     * Any number of processes may hold a SHARED lock simultaneously.
     */
    SHARED,
    
    /**
     * A single process may hold a RESERVED lock on a file at
     * any time. Other processes may hold and obtain new SHARED locks.
     */
    RESERVED,
    
    /**
     * A single process may hold a PENDING lock on a file at
     * any one time. Existing SHARED locks may persist, but no new
     * SHARED locks may be obtained by other processes.
     */
    PENDING,
    
    /**
     *  An EXCLUSIVE lock precludes all other locks.
     */
    EXCLUSIVE
    
}
