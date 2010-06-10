/**
 * SqlJetMemJournal.java
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
package org.tmatesoft.sqljet.core.internal.fs;

import java.util.Set;

import org.tmatesoft.sqljet.core.internal.ISqlJetFile;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.SqlJetDeviceCharacteristics;
import org.tmatesoft.sqljet.core.internal.SqlJetFileOpenPermission;
import org.tmatesoft.sqljet.core.internal.SqlJetFileType;
import org.tmatesoft.sqljet.core.internal.SqlJetLockType;
import org.tmatesoft.sqljet.core.internal.SqlJetSyncFlags;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;

/**
 * This subclass is a subclass of sqlite3_file. Each open memory-journal is an
 * instance of this class.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetMemJournal implements ISqlJetFile {

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#isMemJournal()
     */
    public boolean isMemJournal() {
        return true;
    }

    /*
     * Space to hold the rollback journal is allocated in increments of* this
     * many bytes.
     */
    private static final int JOURNAL_CHUNKSIZE = 1024;

    /*
     * Macro to find the minimum of two numeric values.
     */
    private static int MIN(int x, int y) {
        return ((x) < (y) ? (x) : (y));
    }

    /**
     * The rollback journal is composed of a linked list of these structures.
     */
    private static class FileChunk {
        /** Next chunk in the journal */
        FileChunk pNext;
        /** Content of this chunk */
        ISqlJetMemoryPointer zChunk = SqlJetUtility.allocatePtr(JOURNAL_CHUNKSIZE);
    };

    /*
     * * An instance of this object serves as a cursor into the rollback
     * journal.* The cursor can be either for reading or writing.
     */
    private static class FilePoint {
        long iOffset; /* Offset from the beginning of the file */
        FileChunk pChunk; /* Specific chunk into which cursor points */
    };

    FileChunk pFirst; /* Head of in-memory chunk-list */
    FilePoint endpoint = new FilePoint(); /* Pointer to the end of the file */
    FilePoint readpoint = new FilePoint(); /*
                                            * Pointer to the end of the last
                                            * xRead()
                                            */

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#read(byte[], int, long)
     */
    public int read(ISqlJetMemoryPointer buffer, int amount, long offset) {

        SqlJetMemJournal p = this;

        int iAmt = amount;
        long iOfst = offset;

        int zOut = 0;
        int nRead = iAmt;
        int iChunkOffset;
        FileChunk pChunk;

        assert (iOfst + iAmt <= p.endpoint.iOffset);

        if (p.readpoint.iOffset != iOfst || iOfst == 0) {
            long iOff = 0;
            for (pChunk = p.pFirst; pChunk != null && (iOff + JOURNAL_CHUNKSIZE) <= iOfst; pChunk = pChunk.pNext) {
                iOff += JOURNAL_CHUNKSIZE;
            }
        } else {
            pChunk = p.readpoint.pChunk;
        }

        iChunkOffset = (int) (iOfst % JOURNAL_CHUNKSIZE);
        do {
            int iSpace = JOURNAL_CHUNKSIZE - iChunkOffset;
            int nCopy = MIN(nRead, (JOURNAL_CHUNKSIZE - iChunkOffset));
            SqlJetUtility.memcpy(buffer, zOut, pChunk.zChunk, iChunkOffset, nCopy);
            zOut += nCopy;
            nRead -= iSpace;
            iChunkOffset = 0;
        } while (nRead >= 0 && (pChunk = pChunk.pNext) != null && nRead > 0);
        p.readpoint.iOffset = iOfst + iAmt;
        p.readpoint.pChunk = pChunk;

        return iAmt - nRead;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#write(byte[], int, long)
     */
    public void write(ISqlJetMemoryPointer buffer, int amount, long offset) {

        SqlJetMemJournal p = this;

        int iAmt = amount;
        long iOfst = offset;

        int nWrite = iAmt;
        int zWrite = 0;

        /*
         * An in-memory journal file should only ever be appended to. Random*
         * access writes are not required by sqlite.
         */
        assert (iOfst == p.endpoint.iOffset);

        while (nWrite > 0) {
            FileChunk pChunk = p.endpoint.pChunk;
            int iChunkOffset = (int) (p.endpoint.iOffset % JOURNAL_CHUNKSIZE);
            int iSpace = MIN(nWrite, JOURNAL_CHUNKSIZE - iChunkOffset);

            if (iChunkOffset == 0) {
                /* New chunk is required to extend the file. */
                FileChunk pNew = new FileChunk();
                pNew.pNext = null;
                if (pChunk != null) {
                    assert (p.pFirst != null);
                    pChunk.pNext = pNew;
                } else {
                    assert (p.pFirst == null);
                    p.pFirst = pNew;
                }
                p.endpoint.pChunk = pNew;
            }

            SqlJetUtility.memcpy(p.endpoint.pChunk.zChunk, iChunkOffset, buffer, zWrite, iSpace);
            zWrite += iSpace;
            nWrite -= iSpace;
            p.endpoint.iOffset += iSpace;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#truncate(long)
     */
    public void truncate(long size) {
        SqlJetMemJournal p = this;

        FileChunk pChunk;
        assert (size == 0);
        pChunk = p.pFirst;
        while (pChunk != null) {
            pChunk = pChunk.pNext;
        }

        // sqlite3MemJournalOpen(pJfd);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#close()
     */
    public void close() {
        truncate(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#sync(java.util.Set)
     */
    public void sync(Set<SqlJetSyncFlags> syncFlags) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#fileSize()
     */
    public long fileSize() {
        SqlJetMemJournal p = this;
        return p.endpoint.iOffset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#checkReservedLock()
     */
    public boolean checkReservedLock() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#deviceCharacteristics()
     */
    public Set<SqlJetDeviceCharacteristics> deviceCharacteristics() {
        return empty;
    }

    private final static Set<SqlJetDeviceCharacteristics> empty = SqlJetUtility
            .noneOf(SqlJetDeviceCharacteristics.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#getFileType()
     */
    public SqlJetFileType getFileType() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#getLockType()
     */
    public SqlJetLockType getLockType() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#getPermissions()
     */
    public Set<SqlJetFileOpenPermission> getPermissions() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetFile#lock(org.tmatesoft.sqljet.core.
     * SqlJetLockType)
     */
    public boolean lock(SqlJetLockType lockType) {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetFile#sectorSize()
     */
    public int sectorSize() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetFile#unlock(org.tmatesoft.sqljet.core
     * .SqlJetLockType)
     */
    public boolean unlock(SqlJetLockType lockType) {
        return false;
    }

}
