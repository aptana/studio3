/**
 * IPager.java
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

import java.io.File;
import java.util.Set;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetIOException;
import org.tmatesoft.sqljet.core.table.ISqlJetBusyHandler;

/**
 * The pages cache subsystem reads and writes a file a page at a time and
 * provides a journal for rollback.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetPager {

    /**
     * The minimum sector size is 512
     */
    int SQLJET_MIN_SECTOR_SIZE = SqlJetUtility.getIntSysProp("SQLJET_MIN_SECTOR_SIZE", 512);

    String JOURNAL = "-journal";

    /**
     * Journal files begin with the following magic string. The data was
     * obtained from /dev/random. It is used only as a sanity check.
     * 
     * Since version 2.8.0, the journal format contains additional sanity
     * checking information. If the power fails while the journal is being
     * written, semi-random garbage data might appear in the journal file after
     * power is restored. If an attempt is then made to roll the journal back,
     * the database could be corrupted. The additional sanity checking data is
     * an attempt to discover the garbage in the journal and ignore it.
     * 
     * The sanity checking information for the new journal format consists of a
     * 32-bit checksum on each page of data. The checksum covers both the page
     * number and the pPager->pageSize bytes of data for the page. This cksum is
     * initialized to a 32-bit random value that appears in the journal file
     * right after the header. The random initializer is important, because
     * garbage data that appears at the end of a journal is likely data that was
     * once in other files that have now been deleted. If the garbage data came
     * from an obsolete journal file, the checksums might be correct. But by
     * initializing the checksum to random value which is different for every
     * journal, we minimize that risk.
     */
    ISqlJetMemoryPointer aJournalMagic = SqlJetUtility.wrapPtr(new byte[] { (byte) 0xd9, (byte) 0xd5, (byte) 0x05,
            (byte) 0xf9, (byte) 0x20, (byte) 0xa1, (byte) 0x63, (byte) 0xd7 });

    /**
     * The maximum legal page number is (2^31 - 1).
     */
    int PAGER_MAX_PGNO = 2147483647;

    /**
     * If defined as non-zero, auto-vacuum is enabled by default. Otherwise it
     * must be turned on for each database using "PRAGMA auto_vacuum = 1".
     */
    // int SQLJET_DEFAULT_JOURNAL_SIZE_LIMIT = -1;
    int SQLJET_DEFAULT_JOURNAL_SIZE_LIMIT = SqlJetUtility.getIntSysProp("SQLJET_DEFAULT_JOURNAL_SIZE_LIMIT", -1);

    /**
     * In-memory database's "file-name".
     */
    String MEMORY_DB = ":memory:";

    /**
     * Open a new page cache.
     * 
     * The file to be cached need not exist. The file is not locked until the
     * first call to {@link #getPage(int)} and is only held open until the last
     * page is released using {@link #unref(ISqlJetPage)}.
     * 
     * If fileName is null then a randomly-named temporary file is created and
     * used as the file to be cached. The file will be deleted automatically
     * when it is closed.
     * 
     * If fileName is {@link #MEMORY_DB} then all information is held in cache.
     * It is never written to disk. This can be used to implement an in-memory
     * database.
     * 
     * @param fs
     *            The file system to use
     * @param fileName
     *            Name of the database file to open
     * @param flags
     *            flags controlling this file
     * @param type
     *            file type passed through to
     *            {@link ISqlJetFileSystem#open(java.io.File, SqlJetFileType, Set)}
     * @param permissions
     *            permissions passed through to
     *            {@link ISqlJetFileSystem#open(java.io.File, SqlJetFileType, Set)}
     * @throws SqlJetException
     */
    void open(final ISqlJetFileSystem fs, final File fileName, final Set<SqlJetPagerFlags> flags,
            final SqlJetFileType type, final Set<SqlJetFileOpenPermission> permissions) throws SqlJetException;

    /**
     * Return the path of the database file.
     * 
     * @return
     */
    File getFileName();

    /**
     * Return the directory of the database file.
     * 
     * @return
     */
    File getDirectoryName();

    /**
     * Return the path of the journal file.
     * 
     * @return
     */
    File getJournalName();

    /**
     * Return the file system for the pager.
     * 
     * @return
     */
    ISqlJetFileSystem getFileSystem();

    /**
     * Return the file handle for the database file associated with the pager.
     * This might return NULL if the file has not yet been opened.
     * 
     * 
     * @return
     */
    ISqlJetFile getFile();

    /**
     * Return TRUE if the database file is opened read-only. Return FALSE if the
     * database is (in theory) writable.
     * 
     * @return
     */
    boolean isReadOnly();

    /**
     * Return true if fsync() calls are disabled for this pager. Return FALSE if
     * fsync()s are executed normally.
     * 
     * @return
     */
    boolean isNoSync();

    /**
     * Get the locking-mode for this pager.
     * 
     * @return
     */
    SqlJetPagerLockingMode getLockingMode();

    /**
     * Set the locking-mode for this pager.
     * 
     * @param lockingMode
     */
    void setLockingMode(final SqlJetPagerLockingMode lockingMode);

    /**
     * Get the journal-mode for this pager.
     * 
     * @param journalMode
     * @return
     */
    SqlJetPagerJournalMode getJournalMode();

    /**
     * Set the journal-mode for this pager.
     * 
     * @param journalMode
     * @return
     */
    void setJournalMode(final SqlJetPagerJournalMode journalMode);

    /**
     * Get the size-limit used for persistent journal files.
     * 
     * @return
     */
    long getJournalSizeLimit();

    /**
     * Set the size-limit used for persistent journal files.
     * 
     * @param limit
     */
    void setJournalSizeLimit(final long limit);

    /**
     * Set safety level
     * 
     * @param safetyLevel
     */
    void setSafetyLevel(final SqlJetSafetyLevel safetyLevel);

    /**
     * Get safety level
     * 
     * @return
     */
    SqlJetSafetyLevel getSafetyLevel();

    /**
     * Return a pointer to the "temporary page" buffer held internally by the
     * pager. This is a buffer that is big enough to hold the entire content of
     * a database page. This buffer is used internally during rollback and will
     * be overwritten whenever a rollback occurs. But other modules are free to
     * use it too, as long as no rollbacks are happening.
     * 
     * @return
     */
    ISqlJetMemoryPointer getTempSpace();

    /**
     * Set the busy handler function.
     * 
     * @param busyHandler
     */
    void setBusyhandler(final ISqlJetBusyHandler busyHandler);

    /**
     * Set the reinitializer for this pager. If not NULL, the reinitializer is
     * called when the content of a page in cache is restored to its original
     * value as a result of a rollback. The callback gives higher-level code an
     * opportunity to restore the EXTRA section to agree with the restored page
     * data.
     * 
     * @param reinitier
     */
    void setReiniter(final ISqlJetPageCallback reinitier);

    /**
     * 
     * Set the page size to pageSize. If the suggest new page size is
     * inappropriate, then an alternative page size is set to that value before
     * returning.
     * 
     * @param pageSize
     * @return
     * @throws SqlJetException
     */
    int setPageSize(final int pageSize) throws SqlJetException;

    /**
     * Get the page size.
     * 
     * @return
     */
    int getPageSize();

    /**
     * Attempt to set the maximum database page count if mxPage is positive.
     * Make no changes if mxPage is zero or negative. And never reduce the
     * maximum page count below the current size of the database.
     * 
     * @param maxPageCount
     */
    void setMaxPageCount(final int maxPageCount) throws SqlJetException;

    /**
     * Return the current maximum page count.
     * 
     * @return
     */
    int getMaxPageCount();

    /**
     * Change the maximum number of in-memory pages that are allowed.
     * 
     * @param cacheSize
     */
    void setCacheSize(final int cacheSize);

    /**
     * Read the first N bytes from the beginning of the file into memory that
     * buffer points to.
     * 
     * No error checking is done. The rational for this is that this function
     * may be called even if the file does not exist or contain a header. In
     * these cases sqlite3OsRead() will return an error, to which the correct
     * response is to zero the memory at pDest and continue. A real IO error
     * will presumably recur and be picked up later (Todo: Think about this).
     * 
     * @param count
     * @param buffer
     * @throws SqlJetIOException
     */
    void readFileHeader(final int count, final ISqlJetMemoryPointer buffer) throws SqlJetIOException;

    /**
     * Return the total number of pages in the disk file associated with pager.
     * 
     * If the PENDING_BYTE lies on the page directly after the end of the file,
     * then consider this page part of the file too. For example, if
     * PENDING_BYTE is byte 4096 (the first byte of page 5) and the size of the
     * file is 4096 bytes, 5 is returned instead of 4.
     * 
     * @return pages count
     * @throws SqlJetException
     *             if pager is in error state.
     */
    int getPageCount() throws SqlJetException;

    /**
     * Shutdown the page cache. Free all memory and close all files.
     * 
     * If a transaction was in progress when this routine is called, that
     * transaction is rolled back. All outstanding pages are invalidated and
     * their memory is freed. Any attempt to use a page associated with this
     * page cache after this function returns will likely result in a coredump.
     * 
     * This function always succeeds. If a transaction is active an attempt is
     * made to roll it back. If an error occurs during the rollback a hot
     * journal may be left in the filesystem but no error is returned to the
     * caller.
     * 
     * @throws SqlJetException
     */
    void close() throws SqlJetException;

    /**
     * Acquire a page.
     * 
     * A read lock on the disk file is obtained when the first page is acquired.
     * This read lock is dropped when the last page is released.
     * 
     * This routine works for any page number greater than 0. If the database
     * file is smaller than the requested page, then no actual disk read occurs
     * and the memory image of the page is initialized to all zeros. The extra
     * data appended to a page is always initialized to zeros the first time a
     * page is loaded into memory.
     * 
     * The acquisition might fail for several reasons. In all cases, an
     * appropriate error code is returned and *ppPage is set to NULL.
     * 
     * See also {@link #lookupPage(int)}. Both this routine and
     * {@link #lookupPage(int)} attempt to find a page in the in-memory cache
     * first. If the page is not already in memory, this routine goes to disk to
     * read it in whereas {@link #lookupPage(int)} just returns 0. This routine
     * acquires a read-lock the first time it has to go to disk, and could also
     * playback an old journal if necessary. Since {@link #lookupPage(int)}
     * never goes to disk, it never has to deal with locks or journal files.
     * 
     * If noContent is false, the page contents are actually read from disk. If
     * noContent is true, it means that we do not care about the contents of the
     * page at this time, so do not do a disk read. Just fill in the page
     * content with zeros. But mark the fact that we have not read the content
     * by setting the PgHdr.needRead flag. Later on, if sqlite3PagerWrite() is
     * called on this page or if this routine is called again with noContent==0,
     * that means that the content is needed and the disk read should occur at
     * that point.
     * 
     * @param pageNumber
     *            Page number to fetch
     * @param read
     *            Do not bother reading content from disk if false
     * 
     * @return
     * @throws SqlJetException
     */
    ISqlJetPage acquirePage(final int pageNumber, final boolean read) throws SqlJetException;

    /**
     * Just call acquire( pageNumber, true);
     * 
     * @param pageNumber
     *            Page number to fetch
     * @return
     * @throws SqlJetException
     */
    ISqlJetPage getPage(final int pageNumber) throws SqlJetException;

    /**
     * Acquire a page if it is already in the in-memory cache. Do not read the
     * page from disk. Return a pointer to the page, or null if the page is not
     * in cache.
     * 
     * See also {@link #getPage(int)}. The difference between this routine and
     * {@link #getPage(int)} is that {@link #getPage(int)} will go to the disk
     * and read in the page if the page is not already in cache. This routine
     * returns null if the page is not in cache or if a disk I/O error has ever
     * happened.
     * 
     * @param pageNumber
     *            Page number to lookup
     * @return
     * @throws SqlJetException
     */
    ISqlJetPage lookupPage(final int pageNumber) throws SqlJetException;

    /**
     * Acquire a write-lock on the database. The lock is removed when the any of
     * the following happen:
     * 
     * <ul>
     * <li>commitPhaseTwo() is called.</li>
     * <li>rollback() is called.</li>
     * <li>close() is called.</li>
     * <li>unref() is called to on every outstanding page.</li>
     * </ul>
     * 
     * The parameter indicates how much space in bytes to reserve for a master
     * journal file-name at the start of the journal when it is created.
     * 
     * A journal file is opened if this is not a temporary file. For temporary
     * files, the opening of the journal file is deferred until there is an
     * actual need to write to the journal.
     * 
     * If the database is already reserved for writing, this routine is a no-op.
     * 
     * If exclusive is true, go ahead and get an EXCLUSIVE lock on the file
     * immediately instead of waiting until we try to flush the cache. The
     * exclusive is ignored if a transaction is already active.
     * 
     * @param exclusive
     * @throws SqlJetException
     */
    void begin(boolean exclusive) throws SqlJetException;

    /**
     * Sync the database file for the pager pPager. zMaster points to the name
     * of a master journal file that should be written into the individual
     * journal file. zMaster may be NULL, which is interpreted as no master
     * journal (a single database transaction).
     * 
     * This routine ensures that the journal is synced, all dirty pages written
     * to the database file and the database file synced. The only thing that
     * remains to commit the transaction is to delete the journal file (or
     * master journal file if specified).
     * 
     * Note that if zMaster==NULL, this does not overwrite a previous value
     * passed to an sqlite3PagerCommitPhaseOne() call.
     * 
     * If the final parameter - noSync - is true, then the database file itself
     * is not synced. The caller must call sqlite3PagerSync() directly to sync
     * the database file before calling CommitPhaseTwo() to delete the journal
     * file in this case.
     * 
     * 
     * @param master
     * @param noSync
     * @throws SqlJetException
     */
    void commitPhaseOne(final String master, boolean noSync) throws SqlJetException;

    /**
     * Commit all changes to the database and release the write lock.
     * 
     * If the commit fails for any reason, a rollback attempt is made and an
     * error code is returned. If the commit worked, SQLITE_OK is returned.
     * 
     * @throws SqlJetException
     * 
     */
    void commitPhaseTwo() throws SqlJetException;

    /**
     * Rollback all changes. The database falls back to PAGER_SHARED mode. All
     * in-memory cache pages revert to their original data contents. The journal
     * is deleted.
     * 
     * This routine cannot fail unless some other process is not following the
     * correct locking protocol or unless some other process is writing trash
     * into the journal file (SQLITE_CORRUPT) or unless a prior malloc() failed
     * (SQLITE_NOMEM). Appropriate error codes are returned for all these
     * occasions. Otherwise, SQLITE_OK is returned.
     * 
     * @throws SqlJetException
     */
    void rollback() throws SqlJetException;

    /**
     * Return the number of references to the pager.
     * 
     * @return
     */
    int getRefCount();

    /**
     * Sync the pager file to disk.
     * 
     * @throws SqlJetIOException
     * 
     */
    void sync() throws SqlJetIOException;

    /**
     * Ensure that there are at least nSavepoint savepoints open.
     * 
     * @throws SqlJetException
     */
    void openSavepoint(int nSavepoint) throws SqlJetException;

    /**
     * Parameter op is always either SAVEPOINT_ROLLBACK or SAVEPOINT_RELEASE. If
     * it is SAVEPOINT_RELEASE, then release and destroy the savepoint with
     * index iSavepoint. If it is SAVEPOINT_ROLLBACK, then rollback all changes
     * that have occured since savepoint iSavepoint was created.
     * 
     * In either case, all savepoints with an index greater than iSavepoint are
     * destroyed.
     * 
     * If there are less than (iSavepoint+1) active savepoints when this
     * function is called it is a no-op.
     * 
     * @throws SqlJetException
     */
    void savepoint(SqlJetSavepointOperation op, int iSavepoint) throws SqlJetException;

    /**
     * Truncate the in-memory database file image to nPage pages. This function
     * does not actually modify the database file on disk. It just sets the
     * internal state of the pager object so that the truncation will be done
     * when the current transaction is committed.
     * 
     * @param pageNumber
     */
    void truncateImage(final int pagesNumber);

    /**
     * Return the current size of the database file image in pages. This
     * function differs from sqlite3PagerPagecount() in two ways:
     * 
     * a) It may only be called when at least one reference to a database page
     * is held. This guarantees that the database size is already known and a
     * call to sqlite3OsFileSize() is not required.
     * 
     * b) The return value is not adjusted for the locking page.
     * 
     */
    int imageSize();

    /**
     * @return
     */
    int getCacheSize();

}
