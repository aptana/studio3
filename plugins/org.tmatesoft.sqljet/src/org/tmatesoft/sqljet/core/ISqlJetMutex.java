/**
 * ISqlJetMutex.java
 * Copyright (C) 2009-2010 TMate Software Ltd
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
package org.tmatesoft.sqljet.core;

/**
 * Mutex interface. SQLJet may have different implementations of mutexes.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetMutex {

    /**
     * Locks mutex. If mutex is locked then this method waits while it will
     * unlock.
     */
    void enter();

    /**
     * Locks mutex if it is unlocked and return true. Otherwise just return
     * false. This method doesn't wait.
     * 
     * @return true if this method locked mutex or false if mutex was already
     *         locked by other thread.
     */
    boolean attempt();

    /**
     * Unlocks mutex.
     */
    void leave();

    /**
     * Check mutex locking status.
     * 
     * @return true if mutex is locked or false if mutex is unlocked.
     */
    boolean held();
}
