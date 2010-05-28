/**
 * ISqlJetBusyHandler.java
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
package org.tmatesoft.sqljet.core.table;

import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;

/**
 * <p>
 * Busy handler interface.
 * </p>
 * 
 * <p>
 * Busy handler are used to implement some behavior on database locking if
 * database is locked already by other thread or process. To method call() is
 * passed number of retry to obtain database lock. If call() returns true then
 * retries to locking still continue. If call() returns false then will be
 * thrown {@link SqlJetException} with {@link SqlJetErrorCode#BUSY}.
 * </p>
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetBusyHandler {

    /**
     * <p>
     * Callback which implements busy handler.
     * </p>
     * 
     * <p>
     * To method call() is passed number of retry to obtain database lock. If
     * call() returns true then retries to locking still continue. If call()
     * returns false then will be thrown {@link SqlJetException} with
     * {@link SqlJetErrorCode#BUSY}.
     * </p>
     * 
     * @param number
     *            number of retry to obtain lock on database.
     * @return true if retries will continue or false if retries will stop.
     */
    boolean call(int number);

}
