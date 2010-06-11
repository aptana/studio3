/**
 * ISqlJetFile.java
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

import java.util.Set;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetIOException;

/**
 * OS Interface Open File Handle.
 *
 * An ISqlJetFile object represents an open file in the OS
 * interface layer.  Individual OS interface implementations will
 * want to subclass this object by appending additional fields
 * for their own use.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetFile {

    /*
    ** File Locking Notes:
    **
    ** A SHARED_LOCK is obtained by locking a single randomly-chosen 
    ** byte out of a specific range of bytes. The lock byte is obtained at 
    ** random so two separate readers can probably access the file at the 
    ** same time, unless they are unlucky and choose the same lock byte.
    ** An EXCLUSIVE_LOCK is obtained by locking all bytes in the range.
    ** There can only be one writer.  A RESERVED_LOCK is obtained by locking
    ** a single byte of the file that is designated as the reserved lock byte.
    ** A PENDING_LOCK is obtained by locking a designated byte different from
    ** the RESERVED_LOCK byte.
    **
    ** The following defines specify the range of bytes used for locking.
    ** SHARED_SIZE is the number of bytes available in the pool from which
    ** a random byte is selected for a shared lock.  The pool of bytes for
    ** shared locks begins at SHARED_FIRST. 
    **
    ** Locking in windows is manditory.  For this reason, we cannot store
    ** actual data in the bytes used for locking.  The pager never allocates
    ** the pages involved in locking therefore.  SHARED_SIZE is selected so
    ** that all locks will fit on a single page even at the minimum page size.
    ** PENDING_BYTE defines the beginning of the locks.  By default PENDING_BYTE
    ** is set high so that we don't have to allocate an unused page except
    ** for very large databases.  But one should test the page skipping logic 
    ** by setting PENDING_BYTE low and running the entire regression suite.
    **
    ** Changing the value of PENDING_BYTE results in a subtly incompatible
    ** file format.  Depending on how it is changed, you might not notice
    ** the incompatibility right away, even running a full regression test.
    ** The default location of PENDING_BYTE is the first byte past the
    ** 1GB boundary.
    **
    */

    long PENDING_BYTE  =    0x40000000;  /* First byte past the 1GB boundary */
    long RESERVED_BYTE =    (PENDING_BYTE+1);
    long SHARED_FIRST  =    (PENDING_BYTE+2);
    long SHARED_SIZE   =    510;
    
    /**
     * Returns file type.
     * 
     * @return {@link SqlJetFileType}
     */
    SqlJetFileType getFileType();
    
    /**
     * Returns assiciated permissions.
     * 
     * @return {@link SqlJetFileOpenPermission}
     */
    Set<SqlJetFileOpenPermission> getPermissions();

    /**
     * Close a file.
     * 
     * @throws SqlJetException 
     */
    void close() throws SqlJetException;

    /**
     * Read data from a file into a buffer.
     * 
     * @param buffer
     * @param amount
     * @param offset
     * @return
     * @throws SqlJetIOException 
     */
    int read(final ISqlJetMemoryPointer buffer, final int amount, final long offset) throws SqlJetIOException;

    /**
     * Write data from a buffer into a file. 
     * 
     * @param buffer
     * @param amount
     * @param offset
     * @return
     * @throws SqlJetIOException 
     */
    void write(final ISqlJetMemoryPointer buffer, final int amount, final long offset) throws SqlJetIOException;

    /**
     * Truncate an open file to a specified size
     * 
     * @param size
     * @throws SqlJetIOException 
     */
    void truncate(final long size) throws SqlJetIOException;

    /**
     * Make sure all writes to a particular file are committed to disk.
     *
     * If dataOnly==false then both the file itself and its metadata (file
     * size, access time, etc) are synced.  If dataOnly==true then only the
     * file data is synced.
     *
     * Also make sure that the directory entry for the file
     * has been created by fsync-ing the directory that contains the file.
     * If we do not do this and we encounter a power failure, the directory
     * entry for the journal might not exist after we reboot.  The next
     * SqlJet to access the file will not know that the journal exists (because
     * the directory entry for the journal was never created) and the transaction
     * will not roll back - possibly leading to database corruption.
     *
     * @param dataOnly
     * @param full
     * @throws SqlJetIOException 
     */
    void sync(Set<SqlJetSyncFlags> syncFlags) throws SqlJetIOException;

    /**
     * Determine the current size of a file in bytes
     * 
     * @return
     * @throws SqlJetException 
     */
    long fileSize() throws SqlJetException;

    /**
     * Lock the file with the lock specified by parameter locktype - one
     * of the following:
     *
     *     (1) SHARED
     *     (2) RESERVED
     *     (3) PENDING
     *     (4) EXCLUSIVE
     *
     * Sometimes when requesting one lock state, additional lock states
     * are inserted in between.  The locking might fail on one of the later
     * transitions leaving the lock state different from what it started but
     * still short of its goal.  The following chart shows the allowed
     * transitions and the inserted intermediate states:
     *
     *    UNLOCKED -> SHARED
     *    SHARED -> RESERVED
     *    SHARED -> (PENDING) -> EXCLUSIVE
     *    RESERVED -> (PENDING) -> EXCLUSIVE
     *    PENDING -> EXCLUSIVE
     *
     * This routine will only increase a lock. 
     * 
     * 
     * @param lockType
     * @return
     * @throws SqlJetIOException 
     */
    boolean lock(final SqlJetLockType lockType) throws SqlJetIOException;

    /**
     * Lower the locking level on file descriptor pFile to locktype.  locktype
     * must be either NONE or SHARED.
     *
     * If the locking level of the file descriptor is already at or below
     * the requested locking level, this routine is a no-op.
     * 
     * @param lockType
     * @return
     * @throws SqlJetIOException 
     */
    boolean unlock(final SqlJetLockType lockType) throws SqlJetIOException;

    /**
     * This routine checks if there is a RESERVED lock held on the specified
     * file by this or any other process. The return value is set 
     * unless an I/O error occurs during lock checking.
     * 
     * @return
     */
    boolean checkReservedLock();

    /**
     * Returns lock type of file.
     * 
     * @return
     */
    SqlJetLockType getLockType();

    /**
     * Return the sector size in bytes of the underlying block device for
     * the specified file. This is almost always 512 bytes, but may be
     * larger for some devices.
     *
     * SqlJet code assumes this function cannot fail. It also assumes that
     * if two files are created in the same file-system directory (i.e.
     * a database and its journal file) that the sector size will be the
     * same for both.
     * 
     * @return
     */
    int sectorSize();

    /**
     * Return the device characteristics for the file.
     * 
     * @return
     */
    Set<SqlJetDeviceCharacteristics> deviceCharacteristics();

    /**
    * Return true if the file-handle passed as an argument is 
    * an in-memory journal 
    */
    boolean isMemJournal();
    
}
