/**
 * SqlJetBtreeLock.java
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
package org.tmatesoft.sqljet.core.internal.btree;

/**
 * A linked list of the following structures is stored at BtShared.pLock. Locks
 * are added (or upgraded from READ_LOCK to WRITE_LOCK) when a cursor is opened
 * on the table with root page BtShared.iTable. Locks are removed from this list
 * when a transaction is committed or rolled back, or when a btree handle is
 * closed.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetBtreeLock {

    /** Btree handle holding this lock */
    SqlJetBtree pBtree;

    /** Root page of table */
    int iTable;

    /** READ_LOCK or WRITE_LOCK */
    SqlJetBtreeLockMode eLock;

    /** Next in BtShared.pLock list */
    //SqlJetBtreeLock pNext;

}
