/**
 * SqlJetPagerState.java
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
package org.tmatesoft.sqljet.core.internal.pager;

import org.tmatesoft.sqljet.core.internal.SqlJetLockType;

/**
 * The page cache as a whole is always in one of the following states:
 * 
 * PAGER_UNLOCK The page cache is not currently reading or writing the database
 * file. There is no data held in memory. This is the initial state.
 * 
 * PAGER_SHARED The page cache is reading the database. Writing is not
 * permitted. There can be multiple readers accessing the same database file at
 * the same time.
 * 
 * PAGER_RESERVED This process has reserved the database for writing but has not
 * yet made any changes. Only one process at a time can reserve the database.
 * The original database file has not been modified so other processes may still
 * be reading the on-disk database file.
 * 
 * PAGER_EXCLUSIVE The page cache is writing the database. Access is exclusive.
 * No other processes or threads can be reading or writing while one process is
 * writing.
 * 
 * PAGER_SYNCED The pager moves to this state from PAGER_EXCLUSIVE after all
 * dirty pages have been written to the database file and the file has been
 * synced to disk. All that remains to do is to remove or truncate the journal
 * file and the transaction will be committed.
 * 
 * The page cache comes up in PAGER_UNLOCK. The first time a sqlite3PagerGet()
 * occurs, the state transitions to PAGER_SHARED. After all pages have been
 * released using sqlite_page_unref(), the state transitions back to
 * PAGER_UNLOCK. The first time that sqlite3PagerWrite() is called, the state
 * transitions to PAGER_RESERVED. (Note that sqlite3PagerWrite() can only be
 * called on an outstanding page which means that the pager must be in
 * PAGER_SHARED before it transitions to PAGER_RESERVED.) PAGER_RESERVED means
 * that there is an open rollback journal. The transition to PAGER_EXCLUSIVE
 * occurs before any changes are made to the database file, though writes to the
 * rollback journal occurs with just PAGER_RESERVED. After an
 * sqlite3PagerRollback() or sqlite3PagerCommitPhaseTwo(), the state can go back
 * to PAGER_SHARED, or it can stay at PAGER_EXCLUSIVE if we are in exclusive
 * access mode.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public enum SqlJetPagerState {

    UNLOCK(SqlJetLockType.NONE),

    /* same as SHARED_LOCK */
    SHARED(SqlJetLockType.SHARED),

    /* same as RESERVED_LOCK */
    RESERVED(SqlJetLockType.RESERVED),

    /* same as EXCLUSIVE_LOCK */
    EXCLUSIVE(SqlJetLockType.EXCLUSIVE),

    SYNCED(SqlJetLockType.EXCLUSIVE);

    private SqlJetLockType lockType;

    /**
     * @return the lock
     */
    public SqlJetLockType getLockType() {
        return lockType;
    }

    /**
     * 
     */
    private SqlJetPagerState(final SqlJetLockType lockType) {
        this.lockType = lockType;
    }

    /**
     * @param lockType
     * @return
     */
    public static SqlJetPagerState getPagerState(final SqlJetLockType lockType) {
        for (final SqlJetPagerState state : values()) {
            if (state.getLockType().equals(lockType))
                return state;
        }
        return null;
    }

}
