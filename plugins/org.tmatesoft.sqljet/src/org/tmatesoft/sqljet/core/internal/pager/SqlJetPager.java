/**
 * Pager.java
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

import java.io.File;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetIOErrorCode;
import org.tmatesoft.sqljet.core.SqlJetIOException;
import org.tmatesoft.sqljet.core.SqlJetLogDefinitions;
import org.tmatesoft.sqljet.core.internal.ISqlJetFile;
import org.tmatesoft.sqljet.core.internal.ISqlJetFileSystem;
import org.tmatesoft.sqljet.core.internal.ISqlJetLimits;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.ISqlJetPage;
import org.tmatesoft.sqljet.core.internal.ISqlJetPageCallback;
import org.tmatesoft.sqljet.core.internal.ISqlJetPager;
import org.tmatesoft.sqljet.core.internal.SqlJetCloneable;
import org.tmatesoft.sqljet.core.internal.SqlJetDeviceCharacteristics;
import org.tmatesoft.sqljet.core.internal.SqlJetFileAccesPermission;
import org.tmatesoft.sqljet.core.internal.SqlJetFileOpenPermission;
import org.tmatesoft.sqljet.core.internal.SqlJetFileType;
import org.tmatesoft.sqljet.core.internal.SqlJetLockType;
import org.tmatesoft.sqljet.core.internal.SqlJetPageFlags;
import org.tmatesoft.sqljet.core.internal.SqlJetPagerFlags;
import org.tmatesoft.sqljet.core.internal.SqlJetPagerJournalMode;
import org.tmatesoft.sqljet.core.internal.SqlJetPagerLockingMode;
import org.tmatesoft.sqljet.core.internal.SqlJetSafetyLevel;
import org.tmatesoft.sqljet.core.internal.SqlJetSavepointOperation;
import org.tmatesoft.sqljet.core.internal.SqlJetSyncFlags;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;
import org.tmatesoft.sqljet.core.internal.fs.SqlJetFile;
import org.tmatesoft.sqljet.core.table.ISqlJetBusyHandler;

/**
 * A open page cache is an instance of the following structure.
 * 
 * Pager.errCode may be set to SQLITE_IOERR, SQLITE_CORRUPT, or or SQLITE_FULL.
 * Once one of the first three errors occurs, it persists and is returned as the
 * result of every major pager API call. The SQLITE_FULL return code is slightly
 * different. It persists only until the next successful rollback is performed
 * on the pager cache. Also, SQLITE_FULL does not affect the sqlite3PagerGet()
 * and sqlite3PagerLookup() APIs, they may still be used successfully.
 * 
 * Managing the size of the database file in pages is a little complicated. The
 * variable Pager.dbSize contains the number of pages that the database image
 * currently contains. As the database image grows or shrinks this variable is
 * updated. The variable Pager.dbFileSize contains the number of pages in the
 * database file. This may be different from Pager.dbSize if some pages have
 * been appended to the database image but not yet written out from the cache to
 * the actual file on disk. Or if the image has been truncated by an
 * incremental-vacuum operation. The Pager.dbOrigSize variable contains the
 * number of pages in the database image when the current transaction was
 * opened. The contents of all three of these variables is only guaranteed to be
 * correct if the boolean Pager.dbSizeValid is true.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetPager implements ISqlJetPager, ISqlJetLimits, ISqlJetPageCallback {

    private static Logger pagerLogger = Logger.getLogger(SqlJetLogDefinitions.SQLJET_LOG_PAGER);

    private static final boolean SQLJET_LOG_PAGER = SqlJetUtility.getBoolSysProp(SqlJetLogDefinitions.SQLJET_LOG_PAGER,
            false);

    static void PAGERTRACE(String format, Object... args) {
        if (SQLJET_LOG_PAGER) {
            SqlJetUtility.log(pagerLogger, format, args);
        }
    }

    /**
     * The following two macros are used within the PAGERTRACEX() macros above
     * to print out file-descriptors.
     * 
     * PAGERID() takes a pointer to a Pager struct as its argument. The
     * associated file-descriptor is returned. FILEHANDLEID() takes an
     * sqlite3_file struct as its argument.
     */
    String PAGERID() {
        return fileName != null ? fileName.getPath() : null;
    }

    String FILEHANDLEID() {
        return PAGERID();
    }

    /**
     * The maximum allowed sector size. 16MB. If the xSectorsize() method
     * returns a value larger than this, then MAX_SECTOR_SIZE is used instead.
     * This could conceivably cause corruption following a power failure on such
     * a system. This is currently an undocumented limit.
     */
    private static final int MAX_SECTOR_SIZE = 0x0100000;

    /**
     * An instance of the following structure is allocated for each active
     * savepoint and statement transaction in the system. All such structures
     * are stored in the Pager.aSavepoint[] array, which is allocated and
     * resized using sqlite3Realloc().
     * 
     * When a savepoint is created, the PagerSavepoint.iHdrOffset field is set
     * to 0. If a journal-header is written into the main journal while the
     * savepoint is active, then iHdrOffset is set to the byte offset
     * immediately following the last journal record written into the main
     * journal before the journal-header. This is required during savepoint
     * rollback (see pagerPlaybackSavepoint()).
     */

    private class PagerSavepoint extends SqlJetCloneable {
        /** Starting offset in main journal */
        long iOffset;
        /** See above */
        long iHdrOffset;
        /** Set of pages in this savepoint */
        BitSet pInSavepoint;
        /** Original number of pages in file */
        int nOrig;
        /** Index of first record in sub-journal */
        int iSubRec;
    };

    ISqlJetFileSystem fileSystem;
    SqlJetFileType type;
    Set<SqlJetFileOpenPermission> permissions;

    SqlJetPagerState state = SqlJetPagerState.UNLOCK;
    SqlJetPagerJournalMode journalMode = SqlJetPagerJournalMode.DELETE;
    SqlJetPagerLockingMode lockingMode = SqlJetPagerLockingMode.NORMAL;

    /** True if journal file descriptors is valid */
    boolean journalOpen;

    /** True if header of journal is synced */
    boolean journalStarted;

    /** Use a rollback journal on this file */
    boolean useJournal;

    /** Do not bother to obtain readlocks */
    boolean noReadlock;

    /** Do not sync the journal if true */
    boolean noSync;

    /** Do extra syncs of the journal for robustness */
    boolean fullSync;

    /** One of SYNC_NORMAL or SYNC_FULL */
    Set<SqlJetSyncFlags> syncFlags;

    /** fileName is a temporary file */
    boolean tempFile;

    /** True for a read-only database */
    boolean readOnly;

    /** True if an fsync() is needed on the journal */
    boolean needSync;

    /** True if cached pages have changed */
    boolean dirtyCache;

    /** True to inhibit all file I/O */
    boolean memDb;

    /** True if a m-j name has been written to jrnl */
    boolean setMaster;

    /** Boolean. While true, do not spill the cache */
    boolean doNotSync;

    /** True if there are any changes to the Db */
    boolean dbModified;

    /** Set after incrementing the change-counter */
    boolean changeCountDone;

    /** Set when dbSize is correct */
    boolean dbSizeValid;

    /** Number of pages in the file */
    int dbSize;

    /** dbSize before the current transaction */
    int dbOrigSize;

    /** Number of pages in the database file */
    int dbFileSize;

    /** Number of pages written to the journal */
    int nRec;

    /** Quasi-random value added to every checksum */
    long cksumInit;

    /** Number of records in stmt subjournal */
    int stmtNRec;

    /** Number of bytes in a page */
    int pageSize;

    /** Total number of in-memory pages */
    int nPage;

    /** Maximum number of pages to hold in cache */
    int mxPage;

    /** Maximum allowed size of the database */
    int mxPgno;

    /** One bit for each page in the database file */
    BitSet pagesInJournal;

    /** One bit for each page marked always-rollback */
    BitSet pagesAlwaysRollback;

    /** Name of the database file */
    File fileName;

    /** Name of the journal file */
    File journal;

    /** Directory hold database and journal files */
    File directory;

    /** File descriptors for database and journal */
    ISqlJetFile fd, jfd;

    /** File descriptor for the sub-journal */
    ISqlJetFile sjfd;

    /** Current byte offset in the journal file */
    long journalOff;

    /** Byte offset to previous journal header */
    long journalHdr;

    /** Assumed sector size during rollback */
    int sectorSize;

    /** Pager.pageSize bytes of space for tmp use */
    ISqlJetMemoryPointer tmpSpace;

    /** Changes whenever database file changes */
    ISqlJetMemoryPointer dbFileVers = SqlJetUtility.allocatePtr(16);

    /** Size limit for persistent journal files */
    long journalSizeLimit;

    /** Pointer to page cache object */
    SqlJetPageCache pageCache;

    SqlJetSafetyLevel safetyLevel;

    /**
     * Call this routine when reloading pages
     */
    ISqlJetPageCallback reiniter;
    ISqlJetBusyHandler busyHandler;

    /** One of several kinds of errors */
    SqlJetErrorCode errCode;

    /** Array of active savepoints */
    PagerSavepoint[] aSavepoint;

    /** Number of elements in aSavepoint[] */
    int nSavepoint;

    /**
     * The size of the header and of each page in the journal is determined by
     * the following macros.
     */
    private int JOURNAL_PG_SZ() {
        return pageSize + 8;
    }

    /**
     * The journal header size for this pager. In the future, this could be set
     * to some value read from the disk controller. The important characteristic
     * is that it is the same size as a disk sector.
     */
    private int JOURNAL_HDR_SZ() {
        return sectorSize;
    }

    /**
     * Page number PAGER_MJ_PGNO is never used in an SQLite database (it is
     * reserved for working around a windows/posix incompatibility). It is used
     * in the journal to signify that the remainder of the journal file is
     * devoted to storing a master journal name - there are no more pages to
     * roll back. See comments for function writeMasterJournal() for details.
     * 
     * @return
     */
    long PAGER_MJ_PGNO() {
        return (ISqlJetFile.PENDING_BYTE / pageSize) + 1;
    }

    int int_PAGER_MJ_PGNO() {
        return Long.valueOf(PAGER_MJ_PGNO()).intValue();
    }

    /**
     * Return true if it is necessary to write page *pPg into the sub-journal. A
     * page needs to be written into the sub-journal if there exists one or more
     * open savepoints for which:
     * 
     * * The page-number is less than or equal to PagerSavepoint.nOrig, and *
     * The bit corresponding to the page-number is not set in
     * PagerSavepoint.pInSavepoint.
     */
    static boolean subjRequiresPage(SqlJetPage pPg) {
        int pgno = pPg.pgno;
        SqlJetPager pPager = pPg.pPager;
        int i;
        for (i = 0; i < pPager.nSavepoint; i++) {
            PagerSavepoint p = pPager.aSavepoint[i];
            if (p.nOrig >= pgno && false == SqlJetUtility.bitSetTest(p.pInSavepoint, pgno)) {
                return true;
            }
        }
        return false;
    }

    /**
     ** Return true if the page is already in the journal file.
     */
    static boolean pageInJournal(SqlJetPage pPg) {
        return SqlJetUtility.bitSetTest(pPg.pPager.pagesInJournal, pPg.pgno);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetPager#open(org.tmatesoft.sqljet.core
     * .ISqlJetFileSystem, java.lang.String,
     * org.tmatesoft.sqljet.core.ISqlJetPageDestructor, int, java.util.Set,
     * org.tmatesoft.sqljet.core.SqlJetFileType, java.util.Set)
     */
    public void open(final ISqlJetFileSystem fileSystem, final File fileName, final Set<SqlJetPagerFlags> flags,
            final SqlJetFileType type, final Set<SqlJetFileOpenPermission> permissions) throws SqlJetException {

        this.fileSystem = fileSystem;
        this.type = type;
        this.permissions = EnumSet.copyOf(permissions);

        this.tempFile = false;
        this.memDb = false;
        this.readOnly = false;
        int szPageDflt = SQLJET_DEFAULT_PAGE_SIZE;
        this.useJournal = null == flags || !flags.contains(SqlJetPagerFlags.OMIT_JOURNAL);

        this.fileName = null;
        if (null != fileName) {
            if (MEMORY_DB.equals(fileName.getPath())) {
                this.memDb = true;
            } else {
                this.fileName = fileName;
            }
        }
        ;

        /* Open the pager file */
        if (null != this.fileName && !this.memDb) {

            this.directory = this.fileName.getParentFile();
            this.journal = new File(this.directory, this.fileName.getName() + JOURNAL);

            this.fd = this.fileSystem.open(this.fileName, this.type, this.permissions);
            this.readOnly = this.fd.getPermissions().contains(SqlJetFileOpenPermission.READONLY);

            /*
             * If the file was successfully opened for read/write access, choose
             * a default page size in case we have to create the database file.
             * The default page size is the maximum of:
             * 
             * + SQLITE_DEFAULT_PAGE_SIZE, + The value returned by
             * sqlite3OsSectorSize() + The largest page size that can be written
             * atomically.
             */
            if (!this.readOnly) {

                setSectorSize();
                if (szPageDflt < sectorSize) {
                    szPageDflt = sectorSize;
                }

                final Set<SqlJetDeviceCharacteristics> dcs = this.fd.deviceCharacteristics();
                if (null != dcs) {
                    for (final SqlJetDeviceCharacteristics dc : dcs) {
                        if (dc.getIoCapAtomicSize() > 0)
                            szPageDflt = dc.getIoCapAtomicSize();
                    }
                }

                if (szPageDflt > SQLJET_MAX_DEFAULT_PAGE_SIZE) {
                    szPageDflt = SQLJET_MAX_DEFAULT_PAGE_SIZE;
                }

            }

        } else {
            /*
             * If a temporary file is requested, it is not opened immediately.
             * In this case we accept the default page size and delay actually
             * opening the file until the first call to OsWrite().
             */
            this.tempFile = true;
            this.state = SqlJetPagerState.EXCLUSIVE;
        }

        this.tmpSpace = SqlJetUtility.allocatePtr(szPageDflt);

        pageCache = new SqlJetPageCache();
        pageCache.open(szPageDflt, !memDb, !memDb ? this : null);

        PAGERTRACE("OPEN %s %s\n", FILEHANDLEID(), fileName);

        this.noReadlock = null != flags && flags.contains(SqlJetPagerFlags.NO_READLOCK) && this.readOnly;

        this.dbSizeValid = this.memDb;
        this.pageSize = szPageDflt;
        this.mxPage = 100;
        this.mxPgno = SQLJET_MAX_PAGE_COUNT;
        assert (this.state == (this.tempFile ? SqlJetPagerState.EXCLUSIVE : SqlJetPagerState.UNLOCK));
        this.lockingMode = this.tempFile ? SqlJetPagerLockingMode.EXCLUSIVE : SqlJetPagerLockingMode.NORMAL;

        this.noSync = this.tempFile || !this.useJournal;
        this.fullSync = !this.noSync;
        this.syncFlags = SqlJetUtility.of(SqlJetSyncFlags.NORMAL);

        this.journalSizeLimit = SQLJET_DEFAULT_JOURNAL_SIZE_LIMIT;

        setSectorSize();
        if (memDb) {
            journalMode = SqlJetPagerJournalMode.MEMORY;
        }

    }

    /**
     * Set the sectorSize for the pager.
     * 
     * The sector size is at least as big as the sector size reported by
     * {@link SqlJetFile#sectorSize()}.
     */
    private void setSectorSize() {
        assert (null != this.fd || this.tempFile);
        if (!this.tempFile && null != this.fd) {
            /* Sector size doesn't matter for temporary files. */
            this.sectorSize = this.fd.sectorSize();
        }
        if (this.sectorSize < SQLJET_MIN_SECTOR_SIZE) {
            this.sectorSize = SQLJET_MIN_SECTOR_SIZE;
        }
        if (this.sectorSize > MAX_SECTOR_SIZE) {
            this.sectorSize = MAX_SECTOR_SIZE;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#getDirectoryName()
     */
    public File getDirectoryName() {
        return directory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#getFileName()
     */
    public File getFileName() {
        return fileName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#getFileSystem()
     */
    public ISqlJetFileSystem getFileSystem() {
        return fileSystem;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#getFile()
     */
    public ISqlJetFile getFile() {
        return fd;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#getJournalName()
     */
    public File getJournalName() {
        return journal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#isNoSync()
     */
    public boolean isNoSync() {
        return noSync;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#isReadOnly()
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#getLockingMode()
     */
    public SqlJetPagerLockingMode getLockingMode() {
        return lockingMode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetPager#setLockingMode(org.tmatesoft.sqljet
     * .core.SqlJetPagerLockingMode)
     */
    public void setLockingMode(final SqlJetPagerLockingMode lockingMode) {
        this.lockingMode = lockingMode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#getJournalMode()
     */
    public SqlJetPagerJournalMode getJournalMode() {
        return journalMode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetPager#setJournalMode(org.tmatesoft.sqljet
     * .core.SqlJetPagerJournalMode)
     */
    public void setJournalMode(final SqlJetPagerJournalMode journalMode) {
        this.journalMode = journalMode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#getJournalSizeLimit()
     */
    public long getJournalSizeLimit() {
        return journalSizeLimit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#setJournalSizeLimit(long)
     */
    public void setJournalSizeLimit(final long limit) {
        if (limit >= -1)
            journalSizeLimit = limit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#getSafetyLevel()
     */
    public SqlJetSafetyLevel getSafetyLevel() {
        return safetyLevel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetPager#setSafetyLevel(org.tmatesoft.sqljet
     * .core.SqlJetPagerSafetyLevel)
     */
    public void setSafetyLevel(final SqlJetSafetyLevel safetyLevel) {

        this.safetyLevel = safetyLevel;

        noSync = safetyLevel == SqlJetSafetyLevel.OFF || tempFile;
        fullSync = safetyLevel == SqlJetSafetyLevel.FULL && !tempFile;
        if (noSync)
            needSync = false;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#getTempSpace()
     */
    public ISqlJetMemoryPointer getTempSpace() {
        return tmpSpace;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetPager#setBusyhandler(org.tmatesoft.sqljet
     * .core.ISqlJetBusyHandler)
     */
    public void setBusyhandler(final ISqlJetBusyHandler busyHandler) {
        this.busyHandler = busyHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetPager#setReiniter(org.tmatesoft.sqljet
     * .core.ISqlJetPageDestructor)
     */
    public void setReiniter(final ISqlJetPageCallback reinitier) {
        this.reiniter = reinitier;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#setPagesize(int)
     */
    public int setPageSize(final int pageSize) throws SqlJetException {
        checkErrorCode();
        assert (pageSize >= SQLJET_MIN_PAGE_SIZE && pageSize <= SQLJET_MAX_PAGE_SIZE);
        if (pageSize != this.pageSize && (!this.memDb || this.dbSize == 0) && pageCache.getRefCount() == 0) {
            reset();
            this.pageSize = pageSize;
            if (!this.memDb)
                setSectorSize();
            this.tmpSpace = SqlJetUtility.allocatePtr(pageSize);
            pageCache.setPageSize(pageSize);
        }
        return this.pageSize;
    }

    /**
     * @throws SqlJetExceptionRemove
     */
    private void checkErrorCode() throws SqlJetException {
        if (null != errCode)
            throw new SqlJetException(errCode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#getPagesize()
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     ** Find a page in the hash table given its page number. Return a pointer to
     * the page or NULL if not found.
     * 
     * @throws SqlJetException
     */
    ISqlJetPage lookup(int pageNumber) throws SqlJetException {
        return pageCache.fetch(pageNumber, false);
    }

    /**
     * Clear the in-memory cache. This routine sets the state of the pager back
     * to what it was when it was first opened. Any outstanding pages are
     * invalidated and subsequent attempts to access those pages will likely
     * result in a coredump.
     * 
     */
    private void reset() {
        if (null != errCode)
            return;
        if (pageCache != null) {
            pageCache.clear();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#setMaxPageCount(int)
     */
    public void setMaxPageCount(int maxPageCount) throws SqlJetException {
        if (maxPageCount > 0) {
            this.mxPgno = maxPageCount;
        }
        getPageCount();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#setMaxPageCount()
     */
    public int getMaxPageCount() {
        return mxPgno;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#setCacheSize(int)
     */
    public void setCacheSize(int cacheSize) {
        pageCache.setCacheSize(cacheSize);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetPager#getCacheSize()
     */
    public int getCacheSize() {
        return pageCache.getCachesize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#readFileHeader(int, byte[])
     */
    public void readFileHeader(final int count, final ISqlJetMemoryPointer buffer) throws SqlJetIOException {
        assert (null != fd || tempFile);
        if (null != fd) {
            try {
                fd.read(buffer, count, 0);
            } catch (final SqlJetIOException e) {
                if (SqlJetIOErrorCode.IOERR_SHORT_READ != e.getIoErrorCode())
                    throw e;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#getPageCount()
     */
    public int getPageCount() throws SqlJetException {
        checkErrorCode();

        int n = 0;

        if (dbSizeValid) {
            n = dbSize;
        } else {
            assert (null != fd || tempFile);
            long l = 0;
            if (null != fd) {
                try {
                    l = fd.fileSize();
                } catch (SqlJetException e) {
                    error(e);
                    throw e;
                }
            }
            if (l > 0 && l < pageSize) {
                n = 1;
            } else {
                l /= pageSize;
                n = (new Long(l)).intValue();
            }
            if (SqlJetPagerState.UNLOCK != state) {
                dbSize = n;
                dbFileSize = n;
                dbSizeValid = true;
            }
        }
        if (n == (ISqlJetFile.PENDING_BYTE / pageSize)) {
            n++;
        }
        if (n > mxPgno) {
            mxPgno = n;
        }
        return n;
    }

    /**
     * This function should be called when an error occurs within the pager
     * code. The first argument is a pointer to the pager structure, the second
     * the error-code about to be returned by a pager API function. The value
     * returned is a copy of the second argument to this function.
     * 
     * If the second argument is SQLITE_IOERR, SQLITE_CORRUPT, or SQLITE_FULL
     * the error becomes persistent. Until the persisten error is cleared,
     * subsequent API calls on this Pager will immediately return the same error
     * code.
     * 
     * A persistent error indicates that the contents of the pager-cache cannot
     * be trusted. This state can be cleared by completely discarding the
     * contents of the pager-cache. If a transaction was active when the
     * persistent error occured, then the rollback journal may need to be
     * replayed.
     * 
     * @param e
     */
    private void error(final SqlJetException e) {
        final SqlJetErrorCode c = e.getErrorCode();
        if (SqlJetErrorCode.FULL == c || SqlJetErrorCode.IOERR == c || SqlJetErrorCode.CORRUPT == c) {
            errCode = c;
            if (SqlJetPagerState.UNLOCK == state && pageCache.getRefCount() == 0) {
                /*
                 * If the pager is already unlocked, call pager_unlock() now to
                 * clear the error state and ensure that the pager-cache is
                 * completely empty.
                 */
                unlock();
            }
        }
    }

    /**
     * Free all structures in the Pager.aSavepoint[] array and set both
     * Pager.aSavepoint and Pager.nSavepoint to zero. Close the sub-journal if
     * it is open and the pager is not in exclusive mode.
     */
    private void releaseAllSavepoint() {
        int ii;
        for (ii = 0; ii < nSavepoint; ii++) {
            aSavepoint[ii].pInSavepoint = null;
        }
        if (!exclusiveMode() && sjfd != null) {
            try {
                sjfd.close();
            } catch (SqlJetException e) {
                // TODO: handle exception
            }
        }
        aSavepoint = null;
        nSavepoint = 0;
        stmtNRec = 0;
    }

    /**
     ** Set the bit number pgno in the PagerSavepoint.pInSavepoint bitvecs of all
     * open savepoints.
     */
    void addToSavepointBitSets(int pgno) {
        int ii; /* Loop counter */
        for (ii = 0; ii < nSavepoint; ii++) {
            PagerSavepoint p = aSavepoint[ii];
            if (pgno <= p.nOrig) {
                p.pInSavepoint.set(pgno);
            }
        }
    }

    /**
     * Unlock the database file.
     * 
     * If the pager is currently in error state, discard the contents of the
     * cache and reset the Pager structure internal state. If there is an open
     * journal-file, then the next time a shared-lock is obtained on the pager
     * file (by this or any other process), it will be treated as a hot-journal
     * and rolled back.
     * 
     */
    private void unlock() {
        if (SqlJetPagerLockingMode.EXCLUSIVE != lockingMode) {

            /*
             * Always close the journal file when dropping the database lock.
             * Otherwise, another connection with journal_mode=delete might
             * delete the file out from under us.
             */
            if (journalOpen) {
                if (null != jfd)
                    try {
                        jfd.close();
                    } catch (final SqlJetException e) {
                        // e.printStackTrace();
                    }
                journalOpen = false;
                pagesInJournal = null;
                pagesAlwaysRollback = null;
            }

            try {
                if (null != fd)
                    fd.unlock(SqlJetLockType.NONE);
            } catch (final SqlJetException e) {
                errCode = e.getErrorCode();
            }
            dbSizeValid = false;
            PAGERTRACE("UNLOCK %s\n", PAGERID());

            /*
             * If Pager.errCode is set, the contents of the pager cache cannot
             * be trusted. Now that the pager file is unlocked, the contents of
             * the cache can be discarded and the error code safely cleared.
             */
            if (null != errCode) {
                errCode = null;
                reset();
                releaseAllSavepoint();
                journalOff = 0;
                journalStarted = false;
                dbOrigSize = 0;
            }

            state = SqlJetPagerState.UNLOCK;
            changeCountDone = false;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#close()
     */
    public void close() throws SqlJetException {
        errCode = null;
        lockingMode = SqlJetPagerLockingMode.NORMAL;
        reset();
        if (!memDb) {
            /*
             * Set Pager.journalHdr to -1 for the benefit of the
             * pager_playback() call which may be made from within
             * pagerUnlockAndRollback(). If it is not -1, then the unsynced
             * portion of an open journal file may be played back into the
             * database. If a power failure occurs while this is happening, the
             * database may become corrupt.
             */
            journalHdr = -1;
            unlockAndRollback();
        }
        PAGERTRACE("CLOSE %s\n", PAGERID());
        if (journalOpen) {
            if (null != jfd)
                jfd.close();
        }
        pagesInJournal = null;
        pagesAlwaysRollback = null;
        releaseAllSavepoint();
        if (null != fd)
            fd.close();

        /*
         * Temp files are automatically deleted by the OS if( pPager->tempFile
         * ){ sqlite3OsDelete(pPager->zFilename); }
         */

        tmpSpace = null;
        if (pageCache != null) {
            pageCache.close();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#acquire(int, boolean)
     */
    public ISqlJetPage acquirePage(final int pageNumber, final boolean read) throws SqlJetException {

        assert (state == SqlJetPagerState.UNLOCK || pageCache.getRefCount() > 0 || pageNumber == 1);

        if (pageNumber > PAGER_MAX_PGNO || pageNumber == 0
                || pageNumber == ((ISqlJetFile.PENDING_BYTE / (pageSize)) + 1)) {
            throw new SqlJetException(SqlJetErrorCode.CORRUPT);
        }

        /*
         * If this is the first page accessed, then get a SHARED lock on the
         * database file. pagerSharedLock() is a no-op if a database lock is
         * already held.
         */
        sharedLock();
        assert (state != SqlJetPagerState.UNLOCK);

        final ISqlJetPage page = pageCache.fetch(pageNumber, true);

        if (null == page) {
            throw new SqlJetException(SqlJetErrorCode.INTERNAL, "Page cache is overflow");
        }

        if (null == page.getPager()) {
            /*
             * The pager cache has created a new page. Its content needs to be
             * initialized.
             */
            page.setPager(this);

            int nMax;
            try {
                nMax = getPageCount();
            } catch (SqlJetException e) {
                page.unref();
                throw e;
            }

            if (nMax < pageNumber || memDb || !read) {

                if (pageNumber > mxPgno) {
                    page.unref();
                    throw new SqlJetException(SqlJetErrorCode.FULL);
                }

                SqlJetUtility.memset(page.getData(), (byte) 0, pageSize);
                if (!read) {
                    if (null == page.getFlags())
                        page.setFlags(SqlJetUtility.noneOf(SqlJetPageFlags.class));
                    page.getFlags().add(SqlJetPageFlags.NEED_READ);
                }
                PAGERTRACE("ZERO %s %d\n", PAGERID(), pageNumber);

            } else {
                try {
                    readDbPage(page, pageNumber);
                } catch (SqlJetIOException e) {
                    if (SqlJetIOErrorCode.IOERR_SHORT_READ != e.getIoErrorCode()) {
                        dropPage(page);
                        throw e;
                    }
                }
            }
            page.setHash(pageHash(page));
        } else {
            /* The requested page is in the page cache. */
            assert (pageCache.getRefCount() > 0 || 1 == pageNumber);
            if (read) {
                try {
                    getContent(page);
                } catch (SqlJetException e) {
                    page.unref();
                    throw e;
                }
            }
        }

        return page;

    }

    /**
     * Make sure we have the content for a page. If the page was previously
     * acquired with noContent==1, then the content was just initialized to
     * zeros instead of being read from disk. But now we need the real data off
     * of disk. So make sure we have it. Read it in if we do not have it
     * already.
     * 
     * @param page
     * @throws SqlJetIOException
     */
    void getContent(final ISqlJetPage page) throws SqlJetIOException {
        final Set<SqlJetPageFlags> flags = page.getFlags();
        if (null != flags && flags.contains(SqlJetPageFlags.NEED_READ)) {
            readDbPage(page, page.getPageNumber());
            flags.remove(SqlJetPageFlags.NEED_READ);
        }
    }

    /**
     * Return a 32-bit hash of the page data for pPage
     * 
     * @param page
     * @return
     */
    long pageHash(ISqlJetPage page) {
        return dataHash(pageSize, page.getData());
    }

    /**
     * @param numByte
     * @param data
     * @return
     */
    private long dataHash(int numByte, ISqlJetMemoryPointer data) {
        long hash = 0;
        /*
         * int i; for (i = 0; i < numByte; i++) { hash = (hash * 1039) +
         * SqlJetUtility.getUnsignedByte(data, i); }
         */
        return hash;
    }

    /**
     * @param page
     * @throws SqlJetException
     */
    private void dropPage(final ISqlJetPage page) throws SqlJetException {
        pageCache.drop(page);
        unlockIfUnused();
    }

    /**
     * If the reference count has reached zero, and the pager is not in the
     * middle of a write transaction or opened in exclusive mode, unlock it.
     * 
     * @throws SqlJetException
     */
    void unlockIfUnused() throws SqlJetException {
        if ((pageCache.getRefCount() == 0) && (SqlJetPagerLockingMode.EXCLUSIVE != lockingMode || journalOff > 0)) {
            unlockAndRollback();
        }
    }

    /**
     * Execute a rollback if a transaction is active and unlock the database
     * file. If the pager has already entered the error state, do not attempt
     * the rollback.
     * 
     * @throws SqlJetException
     */
    private void unlockAndRollback() throws SqlJetException {
        if (errCode == null && SqlJetPagerState.RESERVED.compareTo(state) <= 0) {
            rollback();
        }
        unlock();
    }

    /**
     * Read the content of page pPg out of the database file.
     * 
     * @param page
     * @param pageNumber
     * @throws SqlJetIOException
     */
    private void readDbPage(final ISqlJetPage page, int pageNumber) throws SqlJetIOException {
        assert (!memDb);
        assert (null != fd || tempFile);
        if (null == fd) {
            throw new SqlJetIOException(SqlJetIOErrorCode.IOERR_SHORT_READ);
        }
        long offset = (pageNumber - 1) * pageSize;
        final ISqlJetMemoryPointer data = page.getData();
        fd.read(data, pageSize, offset);
        if (1 == pageNumber) {
            SqlJetUtility.memcpy(dbFileVers, 0, data, 24, dbFileVers.remaining());
        }
        PAGERTRACE("FETCH %s page %d hash(%08x)\n", PAGERID(), page.getPageNumber(), pageHash(page));
    }

    /**
     * This function is called to obtain the shared lock required before data
     * may be read from the pager cache. If the shared lock has already been
     * obtained, this function is a no-op.
     * 
     * Immediately after obtaining the shared lock (if required), this function
     * checks for a hot-journal file. If one is found, an emergency rollback is
     * performed immediately.
     * 
     * @throws SqlJetException
     */
    private void sharedLock() throws SqlJetException {

        boolean isErrorReset = false;

        /*
         * If this database is opened for exclusive access, has no outstanding
         * page references and is in an error-state, now is the chance to clear
         * the error. Discard the contents of the pager-cache and treat any open
         * journal file as a hot-journal.
         */
        if (!memDb && lockingMode == SqlJetPagerLockingMode.EXCLUSIVE && pageCache.getRefCount() == 0
                && null != errCode) {
            if (journalOpen) {
                isErrorReset = true;
            }
            errCode = null;
            reset();
        }

        /*
         * If the pager is still in an error state, do not proceed. The error
         * state will be cleared at some point in the future when all page
         * references are dropped and the cache can be discarded.
         */
        if (null != errCode && errCode != SqlJetErrorCode.FULL) {
            throw new SqlJetException(errCode);
        }

        if (SqlJetPagerState.UNLOCK == state || isErrorReset)
            try {

                boolean isHotJournal = false;
                assert (!memDb);
                assert (pageCache.getRefCount() == 0);

                if (!noReadlock) {
                    try {
                        waitOnLock(SqlJetLockType.SHARED);
                    } catch (SqlJetException e) {
                        assert (state == SqlJetPagerState.UNLOCK);
                        error(e);
                        throw e;
                    }
                } else if (state == SqlJetPagerState.UNLOCK) {
                    state = SqlJetPagerState.SHARED;
                }
                assert (SqlJetPagerState.SHARED == state);

                /*
                 * If a journal file exists, and there is no RESERVED lock on
                 * the database file, then it either needs to be played back or
                 * deleted.
                 */
                if (!isErrorReset) {
                    isHotJournal = hasHotJournal();
                }

                if (isErrorReset || isHotJournal) {
                    /*
                     * Get an EXCLUSIVE lock on the database file. At this point
                     * it is important that a RESERVED lock is not obtained on
                     * the way to the EXCLUSIVE lock. If it were, another
                     * process might open the database file, detect the RESERVED
                     * lock, and conclude that the database is safe to read
                     * while this process is still rolling it back.
                     * 
                     * Because the intermediate RESERVED lock is not requested,
                     * the second process will get to this point in the code and
                     * fail to obtain its own EXCLUSIVE lock on the database
                     * file.
                     */
                    if (SqlJetPagerState.EXCLUSIVE.compareTo(state) > 0) {
                        try {
                            fd.lock(SqlJetLockType.EXCLUSIVE);
                        } catch (SqlJetException e) {
                            error(e);
                            throw e;
                        }
                        state = SqlJetPagerState.EXCLUSIVE;
                    }

                    /*
                     * Open the journal for read/write access. This is because
                     * in exclusive-access mode the file descriptor will be kept
                     * open and possibly used for a transaction later on. On
                     * some systems, the OsTruncate() call used in
                     * exclusive-access mode also requires a read/write file
                     * handle.
                     */
                    if (null == jfd) {

                        if (fileSystem.access(journal, SqlJetFileAccesPermission.EXISTS)) {

                            assert (!tempFile);

                            jfd = fileSystem.open(journal, SqlJetFileType.MAIN_JOURNAL, SqlJetUtility
                                    .of(SqlJetFileOpenPermission.READWRITE));
                            if (null != jfd) {
                                try {
                                    final Set<SqlJetFileOpenPermission> p = jfd.getPermissions();
                                    if (p.contains(SqlJetFileOpenPermission.READONLY))
                                        throw new SqlJetException(SqlJetErrorCode.CANTOPEN);
                                } catch (SqlJetException e) {
                                    jfd.close();
                                    throw e;
                                }
                            }

                        } else {
                            /*
                             * If the journal does not exist, it usually means
                             * that some other connection managed to get in and
                             * roll it back before this connection obtained the
                             * exclusive lock above. Or, it may mean that the
                             * pager was in the error-state when this function
                             * was called and the journal file does not exist.
                             */
                            endTransaction(false);
                        }

                        journalOpen = true;
                        journalStarted = false;
                        journalOff = 0;
                        setMaster = false;
                        journalHdr = 0;

                        /*
                         * Playback and delete the journal. Drop the database
                         * write lock and reacquire the read lock. Purge the
                         * cache before playing back the hot-journal so that we
                         * don't end up with an inconsistent cache.
                         */
                        try {
                            pageCache.clear();
                        } finally {
                            if (null != jfd) {
                                try {
                                    playback(true);
                                } catch (SqlJetException e) {
                                    error(e);
                                    throw e;
                                }
                            }
                        }
                        assert (SqlJetPagerState.SHARED == state || (SqlJetPagerLockingMode.EXCLUSIVE == lockingMode && SqlJetPagerState.SHARED
                                .compareTo(state) < 0));

                    }

                }

                if (pageCache.getPageCount() > 0) {
                    /*
                     * The shared-lock has just been acquired on the database
                     * file and there are already pages in the cache (from a
                     * previous read or write transaction). Check to see if the
                     * database has been modified. If the database has changed,
                     * flush the cache.
                     * 
                     * Database changes is detected by looking at 15 bytes
                     * beginning at offset 24 into the file. The first 4 of
                     * these 16 bytes are a 32-bit counter that is incremented
                     * with each change. The other bytes change randomly with
                     * each file change when a codec is in use.
                     * 
                     * There is a vanishingly small chance that a change will
                     * not be detected. The chance of an undetected change is so
                     * small that it can be neglected.
                     */
                    ISqlJetMemoryPointer dbFileVers = SqlJetUtility.allocatePtr(this.dbFileVers.remaining());
                    getPageCount();

                    if (null != errCode) {
                        throw new SqlJetException(errCode);
                    }

                    assert (dbSizeValid);
                    if (dbSize > 0) {
                        PAGERTRACE("CKVERS %s %d\n", PAGERID(), dbFileVers.remaining());
                        fd.read(dbFileVers, dbFileVers.remaining(), 24);
                    } else {
                        SqlJetUtility.memset(dbFileVers, (byte) 0, dbFileVers.remaining());
                    }

                    if (SqlJetUtility.memcmp(this.dbFileVers, dbFileVers, dbFileVers.remaining()) != 0) {
                        reset();
                    }
                }
                assert (SqlJetPagerLockingMode.EXCLUSIVE == lockingMode || SqlJetPagerState.SHARED == state);

            } catch (SqlJetException e) {
                unlock();
                throw e;
            }
    }

    /**
     * 
     * Playback the journal and thus restore the database file to the state it
     * was in before we started making changes.
     * 
     * The journal file format is as follows:
     * 
     * (1) 8 byte prefix. A copy of aJournalMagic[]. (2) 4 byte big-endian
     * integer which is the number of valid page records in the journal. If this
     * value is 0xffffffff, then compute the number of page records from the
     * journal size. (3) 4 byte big-endian integer which is the initial value
     * for the sanity checksum. (4) 4 byte integer which is the number of pages
     * to truncate the database to during a rollback. (5) 4 byte big-endian
     * integer which is the sector size. The header is this many bytes in size.
     * (6) 4 byte big-endian integer which is the page case. (7) 4 byte integer
     * which is the number of bytes in the master journal name. The value may be
     * zero (indicate that there is no master journal.) (8) N bytes of the
     * master journal name. The name will be nul-terminated and might be shorter
     * than the value read from (5). If the first byte of the name is \000 then
     * there is no master journal. The master journal name is stored in UTF-8.
     * (9) Zero or more pages instances, each as follows: + 4 byte page number.
     * + pPager->pageSize bytes of data. + 4 byte checksum
     * 
     * When we speak of the journal header, we mean the first 8 items above.
     * Each entry in the journal is an instance of the 9th item.
     * 
     * Call the value from the second bullet "nRec". nRec is the number of valid
     * page entries in the journal. In most cases, you can compute the value of
     * nRec from the size of the journal file. But if a power failure occurred
     * while the journal was being written, it could be the case that the size
     * of the journal file had already been increased but the extra entries had
     * not yet made it safely to disk. In such a case, the value of nRec
     * computed from the file size would be too large. For that reason, we
     * always use the nRec value in the header.
     * 
     * If the nRec value is 0xffffffff it means that nRec should be computed
     * from the file size. This value is used when the user selects the no-sync
     * option for the journal. A power failure could lead to corruption in this
     * case. But for things like temporary table (which will be deleted when the
     * power is restored) we don't care.
     * 
     * If the file opened as the journal file is not a well-formed journal file
     * then all pages up to the first corrupted page are rolled back (or no
     * pages if the journal header is corrupted). The journal file is then
     * deleted and SQLITE_OK returned, just as if no corruption had been
     * encountered.
     * 
     * If an I/O or malloc() error occurs, the journal-file is not deleted and
     * an error code is returned.
     * 
     * @param isHot
     * @throws SqlJetException
     */
    private void playback(boolean isHot) throws SqlJetException {

        SqlJetException rc = null;

        long szJ; /* Size of the journal file in bytes */
        int nRec = -1; /* Number of Records in the journal */
        int u; /* Unsigned loop counter */
        int mxPg = 0; /* Size of the original file in pages */
        boolean res = true; /* Value returned by sqlite3OsAccess() */
        String zMaster = null; /* Name of master journal file if any */

        /*
         * Figure out how many records are in the journal. Abort early if the
         * journal is empty.
         */
        assert (journalOpen);

        try {
            szJ = jfd.fileSize();
            if (szJ == 0) {
                return;
            }

            /*
             * Read the master journal name from the journal, if it is present.
             * If a master journal file name is specified, but the file is not
             * present on disk, then the journal is not hot and does not need to
             * be played back.
             */
            zMaster = readMasterJournal(jfd);
            if (null != zMaster) {
                res = fileSystem.access(new File(zMaster), SqlJetFileAccesPermission.EXISTS);
            }
            if (!res) {
                return;
            }
            journalOff = 0;

            /*
             * This loop terminates either when the readJournalHdr() call
             * returns SQLITE_DONE or an IO error occurs.
             */
            while (true) {

                /*
                 * Read the next journal header from the journal file. If there
                 * are not enough bytes left in the journal file for a complete
                 * header, or it is corrupted, then a process must of failed
                 * while writing it. This indicates nothing more needs to be
                 * rolled back.
                 */
                try {
                    final int[] readJournalHdr = readJournalHdr(szJ);
                    nRec = readJournalHdr[0];
                    mxPg = readJournalHdr[1];
                } catch (SqlJetException e) {
                    if (SqlJetErrorCode.DONE == e.getErrorCode())
                        return;
                }

                /*
                 * If nRec is 0xffffffff, then this journal was created by a
                 * process working in no-sync mode. This means that the rest of
                 * the journal file consists of pages, there are no more journal
                 * headers. Compute the value of nRec based on this assumption.
                 */
                if (nRec == 0xffffffff) {
                    assert (journalOff == JOURNAL_HDR_SZ());
                    nRec = (int) ((szJ - JOURNAL_HDR_SZ()) / JOURNAL_PG_SZ());
                }

                /*
                 * If nRec is 0 and this rollback is of a transaction created by
                 * this process and if this is the final header in the journal,
                 * then it means that this part of the journal was being filled
                 * but has not yet been synced to disk. Compute the number of
                 * pages based on the remaining size of the file.
                 * 
                 * The third term of the test was added to fix ticket #2565.
                 * When rolling back a hot journal, nRec==0 always means that
                 * the next chunk of the journal contains zero pages to be
                 * rolled back. But when doing a ROLLBACK and the nRec==0 chunk
                 * is the last chunk in the journal, it means that the journal
                 * might contain additional pages that need to be rolled back
                 * and that the number of pages should be computed based on the
                 * journal file size.
                 */
                if (nRec == 0 && !isHot && journalHdr + JOURNAL_HDR_SZ() == journalOff) {
                    nRec = (int) ((szJ - journalOff) / JOURNAL_PG_SZ());
                }

                /*
                 * If this is the first header read from the journal, truncate
                 * the database file back to its original size.
                 */
                if (journalOff == JOURNAL_HDR_SZ()) {
                    doTruncate(mxPg);
                    dbSize = mxPg;
                }

                /*
                 * Copy original pages out of the journal and back into the
                 * database file.
                 */
                for (u = 0; u < nRec; u++) {

                    try {
                        playbackOnePage(true, journalOff, false, null);
                    } catch (SqlJetException e) {
                        if (e.getErrorCode() == SqlJetErrorCode.DONE) {
                            journalOff = szJ;
                            break;
                        } else {
                            /*
                             * If we are unable to rollback, then the database
                             * is probably going to end up being corrupt. It is
                             * corrupt to us, anyhow. Perhaps the next process
                             * to come along can fix it....
                             */
                            throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                        }
                    }

                }
            }

        } catch (SqlJetException e) {
            rc = e;
        } finally {

            // end_playback:

            if (rc == null) {
                try {
                    zMaster = readMasterJournal(jfd);
                } catch (SqlJetException e) {
                    rc = e;
                }
            }

            if (rc == null) {
                try {
                    endTransaction(zMaster != null);
                } catch (SqlJetException e) {
                    rc = e;
                }
            }

            if (rc == null && zMaster != null && res) {
                /*
                 * If there was a master journal and this routine will return
                 * success, see if it is possible to delete the master journal.
                 */
                try {
                    deleteMaster(zMaster);
                } catch (SqlJetException e) {
                    rc = e;
                }
            }
        }

        /*
         * The Pager.sectorSize variable may have been updated while rolling
         * back a journal created by a process with a different sector size
         * value. Reset it to the correct value for this process.
         */
        setSectorSize();
        if (rc != null)
            throw rc;
    }

    /**
     * Playback savepoint pSavepoint. Or, if pSavepoint==NULL, then playback the
     * entire master journal file.
     * 
     * The case pSavepoint==NULL occurs when a ROLLBACK TO command is invoked on
     * a SAVEPOINT that is a transaction savepoint.
     * 
     * @throws SqlJetException
     */
    private void playbackSavepoint(PagerSavepoint pSavepoint) throws SqlJetException {
        long szJ; /* Effective size of the main journal */
        long iHdrOff; /* End of first segment of main-journal records */
        int ii; /* Loop counter */
        SqlJetException rc = null; /* Return code */
        BitSet pDone = null; /* Bitvec to ensure pages played back only once */

        /* Allocate a bitvec to use to store the set of pages rolled back */
        if (pSavepoint != null) {
            pDone = new BitSet(pSavepoint.nOrig);
        }

        /*
         * Truncate the database back to the size it was before the savepoint
         * being reverted was opened.
         */
        dbSize = pSavepoint != null ? pSavepoint.nOrig : dbOrigSize;
        assert (state.compareTo(SqlJetPagerState.SHARED) >= 0);

        /*
         * Use pPager->journalOff as the effective size of the main rollback
         * journal. The actual file might be larger than this in
         * PAGER_JOURNALMODE_TRUNCATE or PAGER_JOURNALMODE_PERSIST. But anything
         * past pPager->journalOff is off-limits to us.
         */
        szJ = journalOff;

        /*
         * Begin by rolling back records from the main journal starting at
         * PagerSavepoint.iOffset and continuing to the next journal header.
         * There might be records in the main journal that have a page number
         * greater than the current database size (pPager->dbSize) but those
         * will be skipped automatically. Pages are added to pDone as they are
         * played back.
         */
        if (pSavepoint != null) {
            iHdrOff = pSavepoint.iHdrOffset > 0 ? pSavepoint.iHdrOffset : szJ;
            journalOff = pSavepoint.iOffset;
            while (rc == null && journalOff < iHdrOff) {
                try {
                    playbackOnePage(true, journalOff, true, pDone);
                } catch (SqlJetException e) {
                    rc = e;
                    assert (e.getErrorCode() != SqlJetErrorCode.DONE);
                }
            }
        } else {
            journalOff = 0;
        }

        /*
         * Continue rolling back records out of the main journal starting at the
         * first journal header seen and continuing until the effective end of
         * the main journal file. Continue to skip out-of-range pages and
         * continue adding pages rolled back to pDone.
         */
        while (rc == null && journalOff < szJ) {
            long nJRec = 0; /* Number of Journal Records */
            try {
                final int[] b = readJournalHdr(szJ);
                nJRec = b[0];
            } catch (SqlJetException e) {
                rc = e;
                assert (e.getErrorCode() != SqlJetErrorCode.DONE);
            }

            /*
             * The
             * "pPager->journalHdr+JOURNAL_HDR_SZ(pPager)==pPager->journalOff"
             * test is related to ticket #2565. See the discussion in the
             * pager_playback() function for additional information.
             */
            assert (!(nJRec == 0 && journalHdr + JOURNAL_HDR_SZ() != journalOff && ((szJ - journalOff) / JOURNAL_PG_SZ()) > 0
            // && pagerNextJournalPageIsValid()
            ));
            if (nJRec == 0 && journalHdr + JOURNAL_HDR_SZ() == journalOff) {
                nJRec = (szJ - journalOff) / JOURNAL_PG_SZ();
            }
            for (ii = 0; rc == null && ii < nJRec && journalOff < szJ; ii++) {
                try {
                    playbackOnePage(true, journalOff, true, pDone);
                } catch (SqlJetException e) {
                    rc = e;
                    assert (e.getErrorCode() != SqlJetErrorCode.DONE);
                }
            }
        }
        assert (rc != null || journalOff == szJ);

        /*
         * Finally, rollback pages from the sub-journal. Page that were
         * previously rolled back out of the main journal (and are hence in
         * pDone) will be skipped. Out-of-range pages are also skipped.
         */
        if (pSavepoint != null) {
            long offset = pSavepoint.iSubRec * (4 + pageSize);
            for (ii = pSavepoint.iSubRec; rc == null && ii < stmtNRec; ii++) {
                assert (offset == ii * (4 + pageSize));
                try {
                    playbackOnePage(false, offset, true, pDone);
                } catch (SqlJetException e) {
                    rc = e;
                    assert (e.getErrorCode() != SqlJetErrorCode.DONE);
                }
            }
        }

        pDone = null;
        if (rc == null) {
            journalOff = szJ;
        }
        if (rc != null)
            throw rc;
    }

    /**
     * Parameter zMaster is the name of a master journal file. A single journal
     * file that referred to the master journal file has just been rolled back.
     * This routine checks if it is possible to delete the master journal file,
     * and does so if it is.
     * 
     * Argument zMaster may point to Pager.pTmpSpace. So that buffer is not
     * available for use within this function.
     * 
     * 
     * The master journal file contains the names of all child journals. To tell
     * if a master journal can be deleted, check to each of the children. If all
     * children are either missing or do not refer to a different master
     * journal, then this master journal can be deleted.
     * 
     * @param master
     * @throws SqlJetException
     */
    private void deleteMaster(String master) throws SqlJetException {

        boolean master_open = false;
        ISqlJetFile pMaster = null;
        /* Contents of master journal file */
        ISqlJetMemoryPointer zMasterJournal = null;
        /* Size of master journal file */
        int nMasterJournal;

        try {

            /*
             * Open the master journal file exclusively in case some other
             * process is running this routine also. Not that it makes too much
             * difference.
             */
            pMaster = fileSystem.open(new File(master), SqlJetFileType.MASTER_JOURNAL, SqlJetUtility
                    .of(SqlJetFileOpenPermission.READONLY));
            master_open = true;

            nMasterJournal = Long.valueOf(pMaster.fileSize()).intValue();

            if (nMasterJournal > 0) {

                /*
                 * Load the entire master journal file into space obtained from
                 * sqlite3_malloc() and pointed to by zMasterJournal.
                 */
                zMasterJournal = SqlJetUtility.allocatePtr(nMasterJournal);
                pMaster.read(zMasterJournal, nMasterJournal, 0);

                int nMasterPtr = 0;
                while (nMasterPtr < nMasterJournal) {

                    int zMasterPtr = SqlJetUtility.strlen(zMasterJournal, nMasterPtr);
                    String zJournal = SqlJetUtility.toString(SqlJetUtility.pointer(zMasterJournal, nMasterPtr));
                    final File journalPath = new File(zJournal);
                    boolean exists = fileSystem.access(journalPath, SqlJetFileAccesPermission.EXISTS);

                    if (exists) {
                        /*
                         * One of the journals pointed to by the master journal
                         * exists. Open it and check if it points at the master
                         * journal. If so, return without deleting the master
                         * journal file.
                         */
                        final ISqlJetFile pJournal = fileSystem.open(journalPath, SqlJetFileType.MAIN_JOURNAL,
                                SqlJetUtility.of(SqlJetFileOpenPermission.READONLY));
                        try {
                            final String readJournal = readMasterJournal(pJournal);
                            if (readJournal != null && readJournal.equals(master)) {
                                /*
                                 * We have a match. Do not delete the master
                                 * journal file.
                                 */
                                return;
                            }
                        } finally {
                            pJournal.close();
                        }
                    }
                    nMasterPtr += zMasterPtr + 1;
                }
            }

            fileSystem.delete(new File(master), false);

        } finally {
            // delmaster_out:
            if (master_open && pMaster != null) {
                pMaster.close();
            }
        }
    }

    /**
     * This routine ends a transaction. A transaction is ended by either a
     * COMMIT or a ROLLBACK.
     * 
     * When this routine is called, the pager has the journal file open and a
     * RESERVED or EXCLUSIVE lock on the database. This routine will release the
     * database lock and acquires a SHARED lock in its place if that is the
     * appropriate thing to do. Release locks usually is appropriate, unless we
     * are in exclusive access mode or unless this is a COMMIT AND BEGIN or
     * ROLLBACK AND BEGIN operation.
     * 
     * The journal file is either deleted or truncated.
     * 
     * TODO: Consider keeping the journal file open for temporary databases.
     * This might give a performance improvement on windows where opening a file
     * is an expensive operation.
     * 
     * @param hasMaster
     * @throws SqlJetException
     */
    private void endTransaction(boolean hasMaster) throws SqlJetException {

        SqlJetException rc = null;
        SqlJetException rc2 = null;

        if (state.compareTo(SqlJetPagerState.RESERVED) < 0) {
            return;
        }
        releaseAllSavepoint();
        if (journalOpen) {
            if (journalMode == SqlJetPagerJournalMode.MEMORY) {
                boolean isMemoryJournal = jfd.isMemJournal();
                try {
                    jfd.close();
                } catch (SqlJetException e) {
                }
                journalOpen = false;
                if (!isMemoryJournal) {
                    try {
                        fileSystem.delete(journal, false);
                    } catch (SqlJetException e) {
                        rc = e;
                    }
                }
            } else if (journalMode == SqlJetPagerJournalMode.TRUNCATE) {
                try {
                    jfd.truncate(0);
                    journalOff = 0;
                    journalStarted = false;
                } catch (SqlJetException e) {
                    rc = e;
                    try {
                        jfd.close();
                    } catch (SqlJetException e1) {
                    }
                    journalOpen = false;
                }
            } else if (exclusiveMode() || journalMode == SqlJetPagerJournalMode.PERSIST) {
                try {
                    zeroJournalHdr(hasMaster);
                } catch (SqlJetException e) {
                    rc = e;
                }
                if (rc != null)
                    error(rc);
                journalOff = 0;
                journalStarted = false;
            } else {
                assert (journalMode == SqlJetPagerJournalMode.DELETE);
                try {
                    jfd.close();
                } catch (SqlJetException e) {
                }
                journalOpen = false;
                if (rc == null && !tempFile) {
                    try {
                        if (!fileSystem.delete(journal, true)) {
                            rc = new SqlJetIOException(SqlJetIOErrorCode.IOERR_DELETE);
                        }
                    } catch (SqlJetException e) {
                        rc = e;
                    }
                }
            }
            pagesInJournal = null;
            pagesAlwaysRollback = null;
            // pCache.iterate(setPageHash)
            pageCache.cleanAll();
            dirtyCache = false;
            nRec = 0;

        } else {
            assert (null == pagesInJournal);
        }

        if (!exclusiveMode()) {
            if (null != fd) {
                try {
                    fd.unlock(SqlJetLockType.SHARED);
                } catch (SqlJetException e) {
                    rc2 = e;
                }
            }
            state = SqlJetPagerState.SHARED;
            changeCountDone = false;
        } else if (state == SqlJetPagerState.SYNCED) {
            state = SqlJetPagerState.EXCLUSIVE;
        }
        dbOrigSize = 0;
        setMaster = false;
        needSync = false;
        pageCache.truncate(dbSize);
        if (!memDb) {
            dbSizeValid = false;
        }
        dbModified = false;

        if (rc != null)
            throw rc;
        if (rc2 != null)
            throw rc2;

    }

    /**
     * Write zeros over the header of the journal file. This has the effect of
     * invalidating the journal file and committing the transaction.
     * 
     * @param doTruncate
     * @throws SqlJetException
     */
    private void zeroJournalHdr(boolean doTruncate) throws SqlJetException {

        SqlJetException rc = null;
        ISqlJetMemoryPointer zeroHdr = SqlJetUtility.allocatePtr(28);

        if (journalOff > 0) {

            long iLimit = journalSizeLimit;

            if (doTruncate || iLimit == 0) {
                try {
                    jfd.truncate(0);
                } catch (SqlJetException e) {
                    rc = e;
                }
            } else {
                try {
                    jfd.write(zeroHdr, zeroHdr.remaining(), 0);
                } catch (SqlJetException e) {
                    rc = e;
                }
            }
            if (rc == null && !noSync) {
                jfd.sync(syncFlags);
            }

            /*
             * At this point the transaction is committed but the write lock is
             * still held on the file. If there is a size limit configured for
             * the persistent journal and the journal file currently consumes
             * more space than that limit allows for, truncate it now. There is
             * no need to sync the file following this operation.
             */
            if (rc == null && iLimit > 0) {
                long sz = 0;

                try {
                    sz = jfd.fileSize();
                } catch (SqlJetException e) {
                    rc = e;
                }

                if (rc == null && sz > iLimit) {
                    try {
                        jfd.truncate(iLimit);
                    } catch (SqlJetException e) {
                        rc = e;
                    }
                }
            }
        }

        if (null != rc)
            throw rc;

    }

    /**
     * @return
     */
    private boolean exclusiveMode() {
        return lockingMode == SqlJetPagerLockingMode.EXCLUSIVE;
    }

    /**
     * Read a single page from either the journal file (if isMainJrnl==1) or
     * from the sub-journal (if isMainJrnl==0) and playback that page. The page
     * begins at offset *pOffset into the file. The *pOffset value is increased
     * to the start of the next page in the journal.
     * 
     * The isMainJrnl flag is true if this is the main rollback journal and
     * false for the statement journal. The main rollback journal uses checksums
     * - the statement journal does not.
     * 
     * If pDone is not NULL, then it is a record of pages that have already been
     * played back. If the page at *pOffset has already been played back (if the
     * corresponding pDone bit is set) then skip the playback. Make sure the
     * pDone bit corresponding to the *pOffset page is set prior to returning.
     * 
     * @param isMainJrnl
     *            true -> main journal. false -> sub-journal.
     * @param pOffset
     *            Offset of record to playback
     * @param isSavepnt
     *            True for a savepoint rollback
     * @param pDone
     *            BitSet of pages already played back
     * @throws SqlJetException
     * 
     */
    private void playbackOnePage(boolean isMainJrnl, long pOffset, boolean isSavepnt, BitSet pDone)
            throws SqlJetException {

        ISqlJetPage pPg; /* An existing page in the cache */
        int pgno; /* The page number of a page in journal */
        long cksum; /* Checksum used for sanity checking */
        ISqlJetMemoryPointer aData; /* Temporary storage for the page */
        ISqlJetFile jfd; /* The file descriptor for the journal file */

        assert (isMainJrnl || pDone != null); /*
                                               * pDone always used on
                                               * sub-journals
                                               */
        assert (isSavepnt || pDone == null); /*
                                              * pDone never used on
                                              * non-savepoint
                                              */

        aData = tmpSpace;
        assert (aData != null); /* Temp storage must have already been allocated */

        jfd = (isMainJrnl ? this.jfd : this.sjfd);

        pgno = read32bits(jfd, pOffset);
        jfd.read(aData, pageSize, pOffset + 4);
        pOffset += pageSize + 4 + (isMainJrnl ? 4 : 0);

        /*
         * Sanity checking on the page. This is more important that I originally
         * thought. If a power failure occurs while the journal is being
         * written, it could cause invalid data to be written into the journal.
         * We need to detect this invalid data (with high probability) and
         * ignore it.
         */
        if (pgno == 0 || pgno == PAGER_MJ_PGNO()) {
            throw new SqlJetException(SqlJetErrorCode.DONE);
        }
        if (pgno > dbSize || SqlJetUtility.bitSetTest(pDone, pgno)) {
            return;
        }
        if (isMainJrnl) {
            cksum = read32bitsUnsigned(jfd, pOffset - 4);
            if (!isSavepnt && cksum(aData) != cksum) {
                throw new SqlJetException(SqlJetErrorCode.DONE);
            }
        }
        if (pDone != null) {
            pDone.set(pgno);
        }

        assert (state == SqlJetPagerState.RESERVED || state.compareTo(SqlJetPagerState.EXCLUSIVE) >= 0);

        /*
         * If the pager is in RESERVED state, then there must be a copy of this
         * page in the pager cache. In this case just update the pager cache,
         * not the database file. The page is left marked dirty in this case.
         * 
         * An exception to the above rule: If the database is in no-sync mode
         * and a page is moved during an incremental vacuum then the page may
         * not be in the pager cache. Later: if a malloc() or IO error occurs
         * during a Movepage() call, then the page may not be in the cache
         * either. So the condition described in the above paragraph is not
         * assert()able.
         * 
         * If in EXCLUSIVE state, then we update the pager cache if it exists
         * and the main file. The page is then marked not dirty.
         * 
         * Ticket #1171: The statement journal might contain page content that
         * is different from the page content at the start of the transaction.
         * This occurs when a page is changed prior to the start of a statement
         * then changed again within the statement. When rolling back such a
         * statement we must not write to the original database unless we know
         * for certain that original page contents are synced into the main
         * rollback journal. Otherwise, a power loss might leave modified data
         * in the database file without an entry in the rollback journal that
         * can restore the database to its original form. Two conditions must be
         * met before writing to the database files. (1) the database must be
         * locked. (2) we know that the original page content is fully synced in
         * the main journal either because the page is not in cache or else the
         * page is marked as needSync==0.
         * 
         * 2008-04-14: When attempting to vacuum a corrupt database file, it is
         * possible to fail a statement on a database that does not yet exist.
         * Do not attempt to write if database file has never been opened.
         */
        pPg = lookup(pgno);
        PAGERTRACE("PLAYBACK %s page %d hash(%08x) %s\n", PAGERID(), pgno, dataHash(pageSize, aData),
                (isMainJrnl ? "main-journal" : "sub-journal"));
        if (state.compareTo(SqlJetPagerState.EXCLUSIVE) >= 0
                && (pPg == null || !pPg.getFlags().contains(SqlJetPageFlags.NEED_SYNC)) && null != fd) {
            long ofst = (pgno - 1) * pageSize;
            fd.write(aData, pageSize, ofst);
            if (pgno > dbFileSize) {
                dbFileSize = pgno;
            }
        } else if (!isMainJrnl && pPg == null) {
            /*
             * If this is a rollback of a savepoint and data was not written to
             * the database and the page is not in-memory, there is a potential
             * problem. When the page is next fetched by the b-tree layer, it
             * will be read from the database file, which may or may not be
             * current.
             * 
             * There are a couple of different ways this can happen. All are
             * quite obscure. When running in synchronous mode, this can only
             * happen if the page is on the free-list at the start of the
             * transaction, then populated, then moved using
             * sqlite3PagerMovepage().
             * 
             * The solution is to add an in-memory page to the cache containing
             * the data just read from the sub-journal. Mark the page as dirty
             * and if the pager requires a journal-sync, then mark the page as
             * requiring a journal-sync before it is written.
             */
            assert (isSavepnt);
            pPg = acquirePage(pgno, true);
            pPg.getFlags().remove(SqlJetPageFlags.NEED_READ);
            pageCache.makeDirty(pPg);
        }
        if (null != pPg) {
            /*
             * No page should ever be explicitly rolled back that is in use,
             * except for page 1 which is held in use in order to keep the lock
             * on the database active. However such a page may be rolled back as
             * a result of an internal error resulting in an automatic call to
             * sqlite3PagerRollback().
             */
            final ISqlJetMemoryPointer pData = pPg.getData();
            SqlJetUtility.memcpy(pData, aData, pageSize);

            if (null != reiniter) {
                reiniter.pageCallback(pPg);
            }

            if (isMainJrnl && (!isSavepnt || journalOff <= journalHdr)) {
                /*
                 * If the contents of this page were just restored from the main
                 * journal file, then its content must be as they were when the
                 * transaction was first opened. In this case we can mark the
                 * page as clean, since there will be no need to write it out to
                 * the.
                 * 
                 * There is one exception to this rule. If the page is being
                 * rolled back as part of a savepoint (or statement) rollback
                 * from an unsynced portion of the main journal file, then it is
                 * not safe to mark the page as clean. This is because marking
                 * the page as clean will clear the PGHDR_NEED_SYNC flag. Since
                 * the page is already in the journal file (recorded in
                 * Pager.pInJournal) and the PGHDR_NEED_SYNC flag is cleared, if
                 * the page is written to again within this transaction, it will
                 * be marked as dirty but the PGHDR_NEED_SYNC flag will not be
                 * set. It could then potentially be written out into the
                 * database file before its journal file segment is synced. If a
                 * crash occurs during or following this, database corruption
                 * may ensue.
                 */
                pageCache.makeClean(pPg);
            }
            pPg.setHash(pageHash(pPg));
            /*
             * If this was page 1, then restore the value of Pager.dbFileVers.
             * Do this before any decoding.
             */
            if (pgno == 1) {
                SqlJetUtility.memcpy(dbFileVers, 0, pData, 24, dbFileVers.remaining());
            }

            pageCache.release(pPg);
        }
    }

    /**
     * Compute and return a checksum for the page of data.
     * 
     * This is not a real checksum. It is really just the sum of the random
     * initial value and the page number. We experimented with a checksum of the
     * entire data, but that was found to be too slow.
     * 
     * Note that the page number is stored at the beginning of data and the
     * checksum is stored at the end. This is important. If journal corruption
     * occurs due to a power failure, the most likely scenario is that one end
     * or the other of the record will be changed. It is much less likely that
     * the two ends of the journal record will be correct and the middle be
     * corrupt. Thus, this "checksum" scheme, though fast and simple, catches
     * the mostly likely kind of corruption.
     * 
     * FIX ME: Consider adding every 200th (or so) byte of the data to the
     * checksum. That way if a single page spans 3 or more disk sectors and only
     * the middle sector is corrupt, we will still have a reasonable chance of
     * failing the checksum and thus detecting the problem.
     * 
     * @param data
     * @return
     */
    long cksum(ISqlJetMemoryPointer data) {
        long cksum = cksumInit;
        int i = pageSize - 200;
        while (i > 0) {
            cksum += SqlJetUtility.getUnsignedByte(data, i);
            i -= 200;
        }
        return cksum;
    }

    /**
     * When this is called the journal file for pager pPager must be open. The
     * master journal file name is read from the end of the file and written
     * into memory supplied by the caller.
     * 
     * zMaster must point to a buffer of at least nMaster bytes allocated by the
     * caller. This should be sqlite3_vfs.mxPathname+1 (to ensure there is
     * enough space to write the master journal name). If the master journal
     * name in the journal is longer than nMaster bytes (including a
     * nul-terminator), then this is handled as if no master journal name were
     * present in the journal.
     * 
     * If no master journal file name is present zMaster[0] is set to 0 and
     * SQLITE_OK returned.
     * 
     * @throws SqlJetException
     * 
     */
    private String readMasterJournal(final ISqlJetFile journal) throws SqlJetException {
        int len;
        long szJ;
        long cksum;
        /* Unsigned loop counter */
        int u;
        /* A buffer to hold the magic header */
        ISqlJetMemoryPointer aMagic = SqlJetUtility.allocatePtr(8);

        szJ = journal.fileSize();
        if (szJ < 16)
            return null;

        len = read32bits(journal, szJ - 16);
        cksum = read32bitsUnsigned(journal, szJ - 12);

        journal.read(aMagic, aMagic.remaining(), szJ - 8);
        if (0 != SqlJetUtility.memcmp(aMagic, aJournalMagic, aMagic.remaining()))
            return null;

        ISqlJetMemoryPointer zMaster = SqlJetUtility.allocatePtr(len);
        journal.read(zMaster, len, szJ - 16 - len);

        /* See if the checksum matches the master journal name */
        for (u = 0; u < len; u++) {
            cksum -= SqlJetUtility.getUnsignedByte(zMaster, u);
        }
        if (cksum > 0) {
            /*
             * If the checksum doesn't add up, then one or more of the disk
             * sectors containing the master journal filename is corrupted. This
             * means definitely roll back, so just return SQLITE_OK and report a
             * (nul) master-journal filename.
             */
            return null;
        }

        return SqlJetUtility.toString(zMaster);
    }

    /**
     * @param fd
     * @param offset
     * @return
     * @throws SqlJetIOException
     */
    private int read32bits(final ISqlJetFile fd, final long offset) throws SqlJetIOException {
        ISqlJetMemoryPointer ac = SqlJetUtility.allocatePtr(4);
        fd.read(ac, ac.remaining(), offset);
        return SqlJetUtility.get4byte(ac);
    }

    /**
     * @param fd
     * @param offset
     * @return
     * @throws SqlJetIOException
     */
    private long read32bitsUnsigned(final ISqlJetFile fd, final long offset) throws SqlJetIOException {
        ISqlJetMemoryPointer ac = SqlJetUtility.allocatePtr(4);
        fd.read(ac, ac.remaining(), offset);
        return SqlJetUtility.get4byteUnsigned(ac);
    }

    /**
     * The journal file must be open when this is called. A journal header file
     * (JOURNAL_HDR_SZ bytes) is read from the current location in the journal
     * file. The current location in the journal file is given by
     * pPager->journalOff. See comments above function writeJournalHdr() for a
     * description of the journal header format.
     * 
     * If the header is read successfully,nRec is set to the number of page
     * records following this header and dbSize is set to the size of the
     * database before the transaction began, in pages. Also, pPager->cksumInit
     * is set to the value read from the journal header. SQLITE_OK is returned
     * in this case.
     * 
     * If the journal header file appears to be corrupted, SQLITE_DONE is
     * returned and nRec and dbSize are undefined. If JOURNAL_HDR_SZ bytes
     * cannot be read from the journal file an error code is returned.
     */
    private int[] readJournalHdr(long journalSize) throws SqlJetException {

        int[] result = new int[2];

        /* A buffer to hold the magic header */
        ISqlJetMemoryPointer aMagic = SqlJetUtility.allocatePtr(8);
        long jrnlOff;
        int iPageSize;
        int iSectorSize;

        seekJournalHdr();
        if (journalOff + JOURNAL_HDR_SZ() > journalSize) {
            throw new SqlJetException(SqlJetErrorCode.DONE);
        }
        jrnlOff = journalOff;

        jfd.read(aMagic, aMagic.remaining(), jrnlOff);
        jrnlOff += aMagic.remaining();

        if (0 != SqlJetUtility.memcmp(aMagic, aJournalMagic, aMagic.remaining())) {
            throw new SqlJetException(SqlJetErrorCode.DONE);
        }

        int pNRec = read32bits(jfd, jrnlOff);
        cksumInit = read32bitsUnsigned(jfd, jrnlOff + 4);
        int pDbSize = read32bits(jfd, jrnlOff + 8);

        result[0] = pNRec;
        result[1] = pDbSize;

        if (journalOff == 0) {
            iPageSize = read32bits(jfd, jrnlOff + 16);

            if (iPageSize < SQLJET_MIN_PAGE_SIZE || iPageSize > SQLJET_MAX_PAGE_SIZE
                    || ((iPageSize - 1) & iPageSize) != 0) {
                /*
                 * If the page-size in the journal-header is invalid, then the
                 * process that wrote the journal-header must have crashed
                 * before the header was synced. In this case stop reading the
                 * journal file here.
                 */
                throw new SqlJetException(SqlJetErrorCode.DONE);
            } else {
                setPageSize(iPageSize);
                assert (pageSize == iPageSize);
            }

            /*
             * Update the assumed sector-size to match the value used by the
             * process that created this journal. If this journal was created by
             * a process other than this one, then this routine is being called
             * from within pager_playback(). The local value of Pager.sectorSize
             * is restored at the end of that routine.
             */

            iSectorSize = read32bits(jfd, jrnlOff + 12);

            if ((iSectorSize & (iSectorSize - 1)) != 0 || iSectorSize < SQLJET_MIN_PAGE_SIZE
                    || iSectorSize > SQLJET_MAX_PAGE_SIZE) {
                throw new SqlJetException(SqlJetErrorCode.DONE);
            }
            sectorSize = iSectorSize;
        }

        journalOff += JOURNAL_HDR_SZ();

        return result;
    }

    /**
     * Seek the journal file descriptor to the next sector boundary where a
     * journal header may be read or written. Pager.journalOff is updated with
     * the new seek offset.
     * 
     * i.e for a sector size of 512:
     * 
     * Input Offset Output Offset --------------------------------------- 0 0
     * 512 512 100 512 2000 2048
     * 
     * 
     */
    private long journalHdrOffset() {
        long offset = 0;
        long c = journalOff;
        if (c > 0) {
            offset = ((c - 1) / JOURNAL_HDR_SZ() + 1) * JOURNAL_HDR_SZ();
        }
        assert (offset % JOURNAL_HDR_SZ() == 0);
        assert (offset >= c);
        assert ((offset - c) < JOURNAL_HDR_SZ());
        return offset;
    }

    private void seekJournalHdr() {
        journalOff = journalHdrOffset();
    }

    /**
     * Return true if there is a hot journal on the given pager. A hot journal
     * is one that needs to be played back.
     * 
     * If the current size of the database file is 0 but a journal file exists,
     * that is probably an old journal left over from a prior database with the
     * same name. Just delete the journal.
     * 
     * Return false if unable to determine the status of the journal.
     * 
     * This routine does not open the journal file to examine its content.
     * Hence, the journal might contain the name of a master journal file that
     * has been deleted, and hence not be hot. Or the header of the journal
     * might be zeroed out. This routine does not discover these cases of a
     * non-hot journal - if the journal file exists and is not empty this
     * routine assumes it is hot. The pager_playback() routine will discover
     * that the journal file is not really hot and will no-op.
     * 
     * @return
     * @throws SqlJetException
     */
    private boolean hasHotJournal() throws SqlJetException {
        boolean exists = false;
        boolean locked = false;
        assert (useJournal);
        assert (null != fd);
        exists = fileSystem.access(journal, SqlJetFileAccesPermission.EXISTS);
        if (exists) {
            locked = fd.checkReservedLock();
        }
        if (exists && !locked) {
            if (0 == getPageCount()) {
                fileSystem.delete(journal, false);
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Try to obtain a lock on a file. Invoke the busy callback if the lock is
     * currently not available. Repeat until the busy callback returns false or
     * until the lock succeeds.
     * 
     * Return SQLITE_OK on success and an error code if we cannot obtain the
     * lock.
     * 
     * @param lockType
     * @throws SqlJetIOException
     */
    private void waitOnLock(final SqlJetLockType lockType) throws SqlJetException {

        /* If the file is currently unlocked then the size must be unknown */
        assert (SqlJetPagerState.SHARED.compareTo(state) <= 0 || !dbSizeValid);
        if (state.getLockType().compareTo(lockType) < 0) {
            boolean lock = false;
            int n = 0;
            do {
                lock = fd.lock(lockType);
                if (!lock && null != busyHandler) {
                    if (!busyHandler.call(n++))
                        break;
                }
            } while (lock != true);
            if (lock) {
                state = SqlJetPagerState.getPagerState(lockType);
            } else {
                throw new SqlJetException(SqlJetErrorCode.BUSY);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#get(int)
     */
    public ISqlJetPage getPage(int pageNumber) throws SqlJetException {
        return acquirePage(pageNumber, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#lookup(int)
     */
    public ISqlJetPage lookupPage(int pageNumber) throws SqlJetException {
        assert (pageNumber != 0);
        if ((state != SqlJetPagerState.UNLOCK) && (errCode == null || errCode == SqlJetErrorCode.FULL)) {
            return pageCache.fetch(pageNumber, false);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#truncate(int)
     */
    public void truncateImage(int pagesNumber) {
        assert (dbSizeValid);
        assert (dbSize >= pagesNumber);
        dbSize = pagesNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#imageSize()
     */
    public int imageSize() {
        assert (dbSizeValid);
        return dbSize;
    }

    /**
     * Truncate the main file of the given pager to the number of pages
     * indicated. Also truncate the cached representation of the file.
     * 
     * It might might be the case that the file on disk is smaller than nPage.
     * This can happen, for example, if we are in the middle of a transaction
     * which has extended the file size and the new pages are still all held in
     * cache, then an INSERT or UPDATE does a statement rollback. Some operating
     * system implementations can get confused if you try to truncate a file to
     * some size that is larger than it currently is, so detect this case and
     * write a single zero byte to the end of the new file instead.
     * 
     * @param page
     * @throws SqlJetException
     */
    private void doTruncate(int pageNumber) throws SqlJetException {
        if (state.compareTo(SqlJetPagerState.EXCLUSIVE) >= 0 && null != fd) {
            long currentSize, newSize;
            currentSize = fd.fileSize();
            newSize = pageSize * pageNumber;
            if (currentSize != newSize) {
                if (currentSize > newSize) {
                    fd.truncate(newSize);
                } else {
                    final ISqlJetMemoryPointer b = SqlJetUtility.allocatePtr(1);
                    fd.write(b, 1, newSize - 1);
                }
                dbFileSize = nPage;
            }
        }
    }

    /**
     * Sync the journal. In other words, make sure all the pages that have been
     * written to the journal have actually reached the surface of the disk. It
     * is not safe to modify the original database file until after the journal
     * has been synced. If the original database is modified before the journal
     * is synced and a power failure occurs, the unsynced journal data would be
     * lost and we would be unable to completely rollback the database changes.
     * Database corruption would occur.
     * 
     * This routine also updates the nRec field in the header of the journal.
     * (See comments on the pager_playback() routine for additional
     * information.) If the sync mode is FULL, two syncs will occur. First the
     * whole journal is synced, then the nRec field is updated, then a second
     * sync occurs.
     * 
     * For temporary databases, we do not care if we are able to rollback after
     * a power failure, so no sync occurs.
     * 
     * If the IOCAP_SEQUENTIAL flag is set for the persistent media on which the
     * database is stored, then OsSync() is never called on the journal file. In
     * this case all that is required is to update the nRec field in the journal
     * header.
     * 
     * This routine clears the needSync field of every page current held in
     * memory.
     * 
     * @throws SqlJetIOException
     */
    private void syncJournal() throws SqlJetIOException {

        /*
         * Sync the journal before modifying the main database (assuming there
         * is a journal and it needs to be synced.)
         */
        if (needSync) {
            assert (!tempFile);
            if (journalMode != SqlJetPagerJournalMode.MEMORY) {
                assert (journalOpen);

                final Set<SqlJetDeviceCharacteristics> dc = fd.deviceCharacteristics();

                if (!dc.contains(SqlJetDeviceCharacteristics.IOCAP_SAFE_APPEND)) {
                    long jrnlOff = journalHdrOffset();
                    ISqlJetMemoryPointer zMagic = SqlJetUtility.allocatePtr(8);

                    /*
                     * This block deals with an obscure problem. If the last
                     * connection that wrote to this database was operating in
                     * persistent-journal mode, then the journal file may at
                     * this point actually be larger than Pager.journalOff
                     * bytes. If the next thing in the journal file happens to
                     * be a journal-header (written as part of the previous
                     * connections transaction), and a crash or power-failure
                     * occurs after nRec is updated but before this connection
                     * writes anything else to the journal file (or
                     * commits/rolls back its transaction), then SQLite may
                     * become confused when doing the hot-journal rollback
                     * following recovery. It may roll back all of this
                     * connections data, then proceed to rolling back the old,
                     * out-of-date data that follows it. Database corruption.
                     * 
                     * To work around this, if the journal file does appear to
                     * contain a valid header following Pager.journalOff, then
                     * write a 0x00 byte to the start of it to prevent it from
                     * being recognized.
                     */
                    try {
                        jfd.read(zMagic, 8, jrnlOff);
                        if (0 == SqlJetUtility.memcmp(zMagic, aJournalMagic, 8)) {
                            ISqlJetMemoryPointer zerobyte = SqlJetUtility.allocatePtr(1);
                            jfd.write(zerobyte, 1, jrnlOff);
                        }
                    } catch (SqlJetIOException e) {
                        if (e.getIoErrorCode() != SqlJetIOErrorCode.IOERR_SHORT_READ)
                            throw e;
                    }

                    /*
                     * Write the nRec value into the journal file header. If in
                     * full-synchronous mode, sync the journal first. This
                     * ensures that all data has really hit the disk before nRec
                     * is updated to mark it as a candidate for rollback.
                     * 
                     * This is not required if the persistent media supports the
                     * SAFE_APPEND property. Because in this case it is not
                     * possible for garbage data to be appended to the file, the
                     * nRec field is populated with 0xFFFFFFFF when the journal
                     * header is written and never needs to be updated.
                     */
                    if (fullSync && !dc.contains(SqlJetDeviceCharacteristics.IOCAP_SEQUENTIAL)) {
                        PAGERTRACE("SYNC journal of %s\n", PAGERID());
                        jfd.sync(syncFlags);
                    }

                    jrnlOff = journalHdr + aJournalMagic.remaining();
                    PAGERTRACE("JHDR %s %d %d\n", PAGERID(), jrnlOff, 4);
                    write32bits(jfd, jrnlOff, nRec);
                }
                if (!dc.contains(SqlJetDeviceCharacteristics.IOCAP_SEQUENTIAL)) {
                    PAGERTRACE("SYNC journal of %s\n", PAGERID());
                    jfd.sync(syncFlags);
                }
                journalStarted = true;
            }

            needSync = false;

            /* Erase the needSync flag from every page. */
            pageCache.clearSyncFlags();

        }
    }

    /**
     ** Add the page to the sub-journal. It is the callers responsibility to use
     * subjRequiresPage() to check that it is really required before calling
     * this function.
     * 
     * @throws SqlJetIOException
     * 
     */
    void subjournalPage(SqlJetPage pPg) throws SqlJetIOException {
        ISqlJetMemoryPointer pData = pPg.getData();
        long offset = stmtNRec * (4 + pageSize);

        PAGERTRACE("STMT-JOURNAL %s page %d\n", PAGERID(), pPg.pgno);

        assert (pageInJournal(pPg) || pPg.pgno > dbOrigSize);
        write32bits(sjfd, offset, pPg.pgno);
        sjfd.write(pData, pageSize, offset + 4);
        stmtNRec++;
        assert (nSavepoint > 0);
        addToSavepointBitSets(pPg.pgno);
    }

    /**
     * Write a 32-bit integer into the given file descriptor. Return SQLITE_OK
     * on success or an error code is something goes wrong.
     * 
     * @throws SqlJetIOException
     */
    static void write32bits(ISqlJetFile fd, long offset, int val) throws SqlJetIOException {
        final ISqlJetMemoryPointer b = SqlJetUtility.put4byte(val);
        fd.write(b, b.remaining(), offset);
    }

    static void write32bitsUnsigned(ISqlJetFile fd, long offset, long val) throws SqlJetIOException {
        final ISqlJetMemoryPointer b = SqlJetUtility.put4byteUnsigned(val);
        fd.write(b, b.remaining(), offset);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#begin(boolean)
     */
    public void begin(boolean exclusive) throws SqlJetException {
        assert (state != SqlJetPagerState.UNLOCK);
        if (state == SqlJetPagerState.SHARED) {
            assert (pagesInJournal == null);
            assert (!memDb);
            if (fd.lock(SqlJetLockType.RESERVED)) {
                state = SqlJetPagerState.RESERVED;
                if (exclusive) {
                    waitOnLock(SqlJetLockType.EXCLUSIVE);
                }
                dirtyCache = false;
                PAGERTRACE("TRANSACTION %s\n", PAGERID());
                if (useJournal && !tempFile && journalMode != SqlJetPagerJournalMode.OFF) {
                    openJournal();
                }
            } else {
                throw new SqlJetException(SqlJetErrorCode.BUSY);
            }
        } else if (journalOpen && journalOff == 0) {
            /*
             * This happens when the pager was in exclusive-access mode the last
             * time a (read or write) transaction was successfully concluded by
             * this connection. Instead of deleting the journal file it was kept
             * open and either was truncated to 0 bytes or its header was
             * overwritten with zeros.
             */
            assert (nRec == 0);
            assert (dbOrigSize == 0);
            assert (pagesInJournal == null);
            getPageCount();
            pagesInJournal = new BitSet(dbSize);
            dbOrigSize = dbSize;
            writeJournalHdr();
        }
        assert (!journalOpen || journalOff > 0);
    }

    /**
     * The journal file must be open when this routine is called. A journal
     * header (JOURNAL_HDR_SZ bytes) is written into the journal file at the
     * current location.
     * 
     * The format for the journal header is as follows: - 8 bytes: Magic
     * identifying journal format. - 4 bytes: Number of records in journal, or
     * -1 no-sync mode is on. - 4 bytes: Random number used for page hash. - 4
     * bytes: Initial database page count. - 4 bytes: Sector size used by the
     * process that wrote this journal. - 4 bytes: Database page size.
     * 
     * Followed by (JOURNAL_HDR_SZ - 28) bytes of unused space.
     * 
     * @throws SqlJetException
     * 
     */
    private void writeJournalHdr() throws SqlJetException {

        SqlJetException rc = null;

        ISqlJetMemoryPointer zHeader = tmpSpace;
        int nHeader = pageSize;
        int nWrite;
        int ii;

        if (nHeader > JOURNAL_HDR_SZ()) {
            nHeader = JOURNAL_HDR_SZ();
        }

        /*
         * If there are active savepoints and any of them were created since the
         * most recent journal header was written, update the
         * PagerSavepoint.iHdrOff fields now.
         */
        for (ii = 0; ii < nSavepoint; ii++) {
            if (aSavepoint[ii].iHdrOffset == 0) {
                aSavepoint[ii].iHdrOffset = journalOff;
            }
        }

        seekJournalHdr();
        journalHdr = journalOff;

        SqlJetUtility.memcpy(zHeader, aJournalMagic, aJournalMagic.remaining());

        /*
         * Write the nRec Field - the number of page records that follow this
         * journal header. Normally, zero is written to this value at this time.
         * After the records are added to the journal (and the journal synced,
         * if in full-sync mode), the zero is overwritten with the true number
         * of records (see syncJournal()).
         * 
         * A faster alternative is to write 0xFFFFFFFF to the nRec field. When
         * reading the journal this value tells SQLite to assume that the rest
         * of the journal file contains valid page records. This assumption is
         * dangerous, as if a failure occured whilst writing to the journal file
         * it may contain some garbage data. There are two scenarios where this
         * risk can be ignored:
         * 
         * When the pager is in no-sync mode. Corruption can follow a power
         * failure in this case anyway.
         * 
         * When the SQLITE_IOCAP_SAFE_APPEND flag is set. This guarantees that
         * garbage data is never appended to the journal file.
         */
        assert (fd != null || noSync);

        if (noSync || journalMode == SqlJetPagerJournalMode.MEMORY
                || fd.deviceCharacteristics().contains(SqlJetDeviceCharacteristics.IOCAP_SAFE_APPEND)) {
            put32bits(zHeader, aJournalMagic.remaining(), 0xffffffff);
        } else {
            put32bits(zHeader, aJournalMagic.remaining(), 0);
        }

        /* The random check-hash initialiser */
        cksumInit = randomnessInt();
        put32bitsUnsigned(zHeader, aJournalMagic.remaining() + 4, cksumInit);

        /* The initial database size */
        put32bits(zHeader, aJournalMagic.remaining() + 8, dbOrigSize);

        /* The assumed sector size for this process */
        put32bits(zHeader, aJournalMagic.remaining() + 12, sectorSize);

        /*
         * Initializing the tail of the buffer is not necessary. Everything
         * works find if the following memset() is omitted. But initializing the
         * memory prevents valgrind from complaining, so we are willing to take
         * the performance hit.
         */
        SqlJetUtility.memset(zHeader, aJournalMagic.remaining() + 16, (byte) 0, nHeader
                - (aJournalMagic.remaining() + 16));

        if (journalHdr == 0) {
            /* The page size */
            put32bits(zHeader, aJournalMagic.remaining() + 16, pageSize);
        }

        for (nWrite = 0; rc == null && nWrite < JOURNAL_HDR_SZ(); nWrite += nHeader) {
            try {
                jfd.write(zHeader, nHeader, journalOff);
            } catch (SqlJetException e) {
                rc = e;
            }
            journalOff += nHeader;
        }

        if (rc != null)
            throw rc;
    }

    /**
     * @return
     */
    private long randomnessInt() {
        return SqlJetUtility.get4byteUnsigned(fileSystem.randomness(4));
    }

    /**
     * Write a 32-bit integer into a buffer in big-endian byte order.
     * 
     * @param p
     * @param pos
     * @param v
     */
    private void put32bits(ISqlJetMemoryPointer p, int pos, int v) {
        SqlJetUtility.put4byte(p, pos, v);
    }

    private void put32bits(ISqlJetMemoryPointer p, int v) {
        put32bits(p, 0, v);
    }

    private void put32bitsUnsigned(ISqlJetMemoryPointer p, int pos, long v) {
        SqlJetUtility.put4byteUnsigned(p, pos, v);
    }

    /*
     * private void put32bitsUnsigned(ByteBuffer p, long v) {
     * put32bitsUnsigned(p, 0, v); }
     */

    /**
     * If the main journal file has already been opened, ensure that the
     * sub-journal file is open too. If the main journal is not open, this
     * function is a no-op.
     * 
     * SQLITE_OK is returned if everything goes according to plan. An
     * SQLITE_IOERR_XXX error code is returned if the call to sqlite3OsOpen()
     * fails.
     */
    private void openSubJournal() throws SqlJetException {
        if (journalOpen && sjfd == null) {
            if (journalMode == SqlJetPagerJournalMode.MEMORY) {
                sjfd = fileSystem.memJournalOpen();
            } else {
                sjfd = openTemp(SqlJetFileType.SUBJOURNAL, null);
            }
        }
    }

    /**
     * Create a journal file for pPager. There should already be a RESERVED or
     * EXCLUSIVE lock on the database file when this routine is called.
     * 
     * Return SQLITE_OK if everything. Return an error code and release the
     * write lock if anything goes wrong.
     */
    void openJournal() throws SqlJetException {

        SqlJetFileType fileType = null;

        Set<SqlJetFileOpenPermission> flags = SqlJetUtility.of(SqlJetFileOpenPermission.READWRITE,
                SqlJetFileOpenPermission.EXCLUSIVE, SqlJetFileOpenPermission.CREATE);

        SqlJetException rc = null;

        assert (state.compareTo(SqlJetPagerState.RESERVED) >= 0);
        assert (useJournal);
        assert (pagesInJournal == null);

        getPageCount();
        pagesInJournal = new BitSet(dbSize);

        try {

            if (!journalOpen) {
                if (tempFile) {
                    flags.add(SqlJetFileOpenPermission.DELETEONCLOSE);
                    fileType = SqlJetFileType.TEMP_JOURNAL;
                } else {
                    fileType = SqlJetFileType.MAIN_JOURNAL;
                }
                try {
                    if (journalMode == SqlJetPagerJournalMode.MEMORY) {
                        jfd = fileSystem.memJournalOpen();
                    } else {
                        jfd = fileSystem.open(journal, fileType, flags);
                    }
                } catch (SqlJetException e) {
                    rc = e;
                }
                assert (rc != null || jfd != null);
                journalOff = 0;
                setMaster = false;
                journalHdr = 0;

                if (rc != null) {
                    fileSystem.delete(journal, false);
                    throw rc;
                }

            }
            journalOpen = true;
            journalStarted = false;
            needSync = false;
            nRec = 0;
            if (errCode != null) {
                rc = new SqlJetException(errCode);
                throw rc;
            }
            dbOrigSize = dbSize;

            try {
                writeJournalHdr();
            } catch (SqlJetException e) {
                rc = e;
            }

            if (nSavepoint > 0 && rc == null) {
                try {
                    openSubJournal();
                } catch (SqlJetException e) {
                    rc = e;
                }
            }

            if (rc != null) {
                rc = null;
                try {
                    endTransaction(false);
                } catch (SqlJetException e) {
                    rc = e;
                }
                if (rc == null) {
                    rc = new SqlJetException(SqlJetErrorCode.FULL);
                    throw rc;
                }
            }

        } finally {
            // failed_to_open_journal:
            if (rc != null) {
                pagesInJournal = null;
                throw rc;
            }

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetPager#commitPhaseOne(java.lang.String,
     * int, boolean)
     */
    public void commitPhaseOne(String master, boolean noSync) throws SqlJetException {

        if (errCode != null) {
            throw new SqlJetException(errCode);
        }

        /*
         * If no changes have been made, we can leave the transaction early.
         */
        if (!dbModified && (journalMode != SqlJetPagerJournalMode.DELETE || exclusiveMode())) {
            assert (!dirtyCache || !journalOpen);
            return;
        }

        PAGERTRACE("DATABASE SYNC: File=%s zMaster=%s nSize=%d\n", fileName, master, dbSize);

        /*
         * If this is an in-memory db, or no pages have been written to, or this
         * function has already been called, it is a no-op.
         */
        try {
            if (state != SqlJetPagerState.SYNCED && !memDb && dirtyCache) {

                /*
                 * If a master journal file name has already been written to the
                 * journal file, then no sync is required. This happens when it
                 * is written, then the process fails to upgrade from a RESERVED
                 * to an EXCLUSIVE lock. The next time the process tries to
                 * commit the transaction the m-j name will have already been
                 * written.
                 */
                if (!setMaster) {

                    incrChangeCounter();

                    if (journalMode != SqlJetPagerJournalMode.OFF) {

                        if (dbSize < dbOrigSize) {
                            /*
                             * If this transaction has made the database
                             * smaller, then all pages being discarded by the
                             * truncation must be written to the journal file.
                             */
                            int i;
                            long iSkip = PAGER_MJ_PGNO();
                            int dbSize = this.dbSize;
                            this.dbSize = this.dbOrigSize;
                            for (i = dbSize + 1; i <= this.dbOrigSize; i++) {
                                if (!SqlJetUtility.bitSetTest(pagesInJournal, i) && i != iSkip) {
                                    final ISqlJetPage pg = getPage(i);
                                    pg.write();
                                    pg.unref();
                                }
                            }
                            this.dbSize = dbSize;
                        }

                        writeMasterJournal(master);
                        syncJournal();
                    }
                }

                /* Write all dirty pages to the database file */
                final ISqlJetPage dirtyList = pageCache.getDirtyList();
                writePageList(dirtyList);
                /*
                 * The error might have left the dirty list all fouled up here,
                 * but that does not matter because if the if the dirty list did
                 * get corrupted, then the transaction will roll back and
                 * discard the dirty list. There is an assert in
                 * pager_get_all_dirty_pages() that verifies that no attempt is
                 * made to use an invalid dirty list.
                 */
                pageCache.cleanAll();

                if (dbSize != dbFileSize) {
                    assert (state.compareTo(SqlJetPagerState.EXCLUSIVE) >= 0);
                    doTruncate(dbSize - (dbSize == PAGER_MJ_PGNO() ? 1 : 0));
                }

                /* Sync the database file. */
                if (!this.noSync && !noSync) {
                    fd.sync(syncFlags);
                }

                state = SqlJetPagerState.SYNCED;

            }

        } catch (SqlJetIOException e) {
            if (e.getIoErrorCode() == SqlJetIOErrorCode.IOERR_BLOCKED) {
                /*
                 * pager_incr_changecounter() may attempt to obtain an exclusive
                 * lock to spill the cache and return IOERR_BLOCKED. But since
                 * there is no chance the cache is inconsistent, it is better to
                 * return SQLITE_BUSY.
                 */
                throw new SqlJetException(SqlJetErrorCode.BUSY);
            }
        }
    }

    /**
     * Given a list of pages (connected by the PgHdr.pDirty pointer) write every
     * one of those pages out to the database file. No calls are made to the
     * page-cache to mark the pages as clean. It is the responsibility of the
     * caller to use PcacheCleanAll() or PcacheMakeClean() to mark the pages as
     * clean.
     * 
     * @param pList
     * @throws SqlJetException
     */
    private void writePageList(ISqlJetPage pList) throws SqlJetException {

        if (pList == null)
            return;

        /*
         * At this point there may be either a RESERVED or EXCLUSIVE lock on the
         * database file. If there is already an EXCLUSIVE lock, the following
         * calls to sqlite3OsLock() are no-ops.
         * 
         * Moving the lock from RESERVED to EXCLUSIVE actually involves going
         * through an intermediate state PENDING. A PENDING lock prevents new
         * readers from attaching to the database but is unsufficient for us to
         * write. The idea of a PENDING lock is to prevent new readers from
         * coming in while we wait for existing readers to clear.
         * 
         * While the pager is in the RESERVED state, the original database file
         * is unchanged and we can rollback without having to playback the
         * journal into the original database file. Once we transition to
         * EXCLUSIVE, it means the database file has been changed and any
         * rollback will require a journal playback.
         */
        waitOnLock(SqlJetLockType.EXCLUSIVE);

        for (ISqlJetPage page = pList; page != null; page = page.getDirty()) {

            /* If the file has not yet been opened, open it now. */
            if (null == fd) {
                assert (tempFile);
                fd = openTemp(type, permissions);
            }

            /*
             * If there are dirty pages in the page cache with page numbers
             * greater than Pager.dbSize, this means sqlite3PagerTruncate() was
             * called to make the file smaller (presumably by auto-vacuum code).
             * Do not write any such pages to the file.
             */
            if (page.getPageNumber() <= dbSize && !page.getFlags().contains(SqlJetPageFlags.DONT_WRITE)) {

                long offset = (page.getPageNumber() - 1) * pageSize;

                PAGERTRACE("STORE %s page %d hash(%08x)\n", PAGERID(), pList.getPageNumber(), pageHash(pList));

                ISqlJetMemoryPointer pData = page.getData();

                fd.write(pData, pageSize, offset);
                if (page.getPageNumber() == 1) {
                    SqlJetUtility.memcpy(dbFileVers, 0, pData, 24, dbFileVers.remaining());
                }
                if (page.getPageNumber() > dbFileSize) {
                    dbFileSize = page.getPageNumber();
                }
            } else {
                PAGERTRACE("NOSTORE %s page %d\n", PAGERID(), pList.getPageNumber());
            }

            page.setHash(pageHash(page));
        }

    }

    /**
     * Open a temporary file.
     * 
     * Write the file descriptor into *fd. Return SQLITE_OK on success or some
     * other error code if we fail. The OS will automatically delete the
     * temporary file when it is closed.
     * 
     * @param fd2
     * @param type2
     * @param permissions2
     * @throws SqlJetException
     */
    private ISqlJetFile openTemp(SqlJetFileType type, Set<SqlJetFileOpenPermission> permissions) throws SqlJetException {

        Set<SqlJetFileOpenPermission> flags = null;
        if (permissions != null) {
            flags = new HashSet<SqlJetFileOpenPermission>();
            for (SqlJetFileOpenPermission sqlJetFileOpenPermission : permissions) {
                flags.add(sqlJetFileOpenPermission);
            }
        } else {
            flags = SqlJetUtility.noneOf(SqlJetFileOpenPermission.class);
        }
        flags.add(SqlJetFileOpenPermission.READWRITE);
        flags.add(SqlJetFileOpenPermission.CREATE);
        flags.add(SqlJetFileOpenPermission.EXCLUSIVE);
        flags.add(SqlJetFileOpenPermission.DELETEONCLOSE);
        return fileSystem.open(null, type, flags);

    }

    /**
     * Write the supplied master journal name into the journal file for pager
     * pPager at the current location. The master journal name must be the last
     * thing written to a journal file. If the pager is in full-sync mode, the
     * journal file descriptor is advanced to the next sector boundary before
     * anything is written. The format is:
     * 
     * + 4 bytes: PAGER_MJ_PGNO. + N bytes: length of master journal name. + 4
     * bytes: N + 4 bytes: Master journal name checksum. + 8 bytes:
     * aJournalMagic[].
     * 
     * The master journal page checksum is the sum of the bytes in the master
     * journal name.
     * 
     * If zMaster is a NULL pointer (occurs for a single database transaction),
     * this call is a no-op.
     * 
     * @param master
     * @throws SqlJetException
     */
    private void writeMasterJournal(String master) throws SqlJetException {

        int len;
        int i;
        long jrnlOff;
        long jrnlSize;
        int cksum = 0;
        ISqlJetMemoryPointer zBuf = SqlJetUtility.allocatePtr(aJournalMagic.remaining() + 2 * 4);

        if (null == master || setMaster)
            return;
        if (journalMode == SqlJetPagerJournalMode.MEMORY)
            return;

        setMaster = true;

        final ISqlJetMemoryPointer zMaster = SqlJetUtility.wrapPtr(master.getBytes());

        len = zMaster.remaining();
        for (i = 0; i < len; i++) {
            cksum += SqlJetUtility.getUnsignedByte(zMaster, i);
        }

        /*
         * If in full-sync mode, advance to the next disk sector before writing
         * the master journal name. This is in case the previous page written to
         * the journal has already been synced.
         */
        if (fullSync) {
            seekJournalHdr();
        }
        jrnlOff = journalOff;
        journalOff += (len + 20);

        write32bits(jfd, jrnlOff, int_PAGER_MJ_PGNO());
        jrnlOff += 4;

        jfd.write(zMaster, len, jrnlOff);
        jrnlOff += len;

        put32bits(zBuf, len);
        put32bits(zBuf, 4, cksum);
        SqlJetUtility.memcpy(zBuf, 8, aJournalMagic, 0, aJournalMagic.remaining());

        try {
            jfd.write(zBuf, zBuf.remaining(), jrnlOff);
        } finally {
            jrnlOff += zBuf.remaining();
            needSync = !noSync;
        }

        /*
         * If the pager is in peristent-journal mode, then the physical
         * journal-file may extend past the end of the master-journal name and 8
         * bytes of magic data just written to the file. This is dangerous
         * because the code to rollback a hot-journal file will not be able to
         * find the master-journal name to determine whether or not the journal
         * is hot.
         * 
         * Easiest thing to do in this scenario is to truncate the journal file
         * to the required size.
         */
        jrnlSize = jfd.fileSize();
        if (jrnlSize > jrnlOff) {
            jfd.truncate(jrnlOff);
        }
    }

    /**
     * This routine is called to increment the database file change-counter,
     * stored at byte 24 of the pager file.
     * 
     * @param b
     * @throws SqlJetException
     */
    private void incrChangeCounter() throws SqlJetException {

        ISqlJetPage page;
        int change_counter;

        if (!changeCountDone && dbSize > 0) {
            /* Open page 1 of the file for writing. */
            page = getPage(1);

            try {
                page.write();
            } catch (SqlJetException e) {
                page.unref();
                throw e;
            }

            /* Increment the value just read and write it back to byte 24. */
            change_counter = SqlJetUtility.get4byte(dbFileVers);
            change_counter++;
            put32bits(page.getData(), 24, change_counter);

            /* Release the page reference. */
            page.unref();
            changeCountDone = true;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#commitPhaseTwo()
     */
    public void commitPhaseTwo() throws SqlJetException {
        if (null != errCode) {
            throw new SqlJetException(errCode);
        }
        if (state.compareTo(SqlJetPagerState.RESERVED) < 0) {
            throw new SqlJetException(SqlJetErrorCode.ERROR);
        }
        if (!dbModified && (journalMode != SqlJetPagerJournalMode.DELETE || !exclusiveMode())) {
            assert (!dirtyCache || !journalOpen);
            return;
        }

        PAGERTRACE("COMMIT %s\n", PAGERID());

        assert (state == SqlJetPagerState.SYNCED || memDb || !dirtyCache);
        try {
            endTransaction(setMaster);
        } catch (SqlJetException e) {
            error(e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#rollback()
     */
    public void rollback() throws SqlJetException {
        PAGERTRACE("ROLLBACK %s\n", PAGERID());
        if (!dirtyCache || !journalOpen) {
            endTransaction(setMaster);
        } else if (null != errCode && errCode != SqlJetErrorCode.FULL) {
            if (state.compareTo(SqlJetPagerState.EXCLUSIVE) >= 0) {
                playback(false);
            }
            throw new SqlJetException(errCode);
        } else {
            try {
                if (state == SqlJetPagerState.RESERVED) {
                    try {
                        playback(false);
                    } finally {
                        endTransaction(setMaster);
                    }
                } else {
                    playback(false);
                }
            } catch (SqlJetException e) {
                if (!memDb) {
                    dbSizeValid = false;
                }
                /*
                 * If an error occurs during a ROLLBACK, we can no longer trust
                 * the pager cache. So call pager_error() on the way out to make
                 * any error persistent.
                 */
                error(e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#sync()
     */
    public void sync() throws SqlJetIOException {
        if (!memDb) {
            fd.sync(syncFlags);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPager#refCount()
     */
    public int getRefCount() {
        return pageCache.getRefCount();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetPageDestructor#pageDestructor(org.tmatesoft
     * .sqljet.core.ISqlJetPage)
     */
    public void pageCallback(final ISqlJetPage page) {

        if (!(page instanceof SqlJetPage))
            return;
        final SqlJetPage pPg = (SqlJetPage) page;

        /*
         * This function is called by the pcache layer when it has reached some
         * soft memory limit. The argument is a pointer to a purgeable Pager
         * object. This function attempts to make a single dirty page that has
         * no outstanding references (if one exists) clean so that it can be
         * recycled by the pcache layer.
         */

        if (doNotSync) {
            return;
        }

        assert (pPg.getFlags().contains(SqlJetPageFlags.DIRTY));
        if (errCode == null) {
            try {
                if (pPg.getFlags().contains(SqlJetPageFlags.NEED_SYNC)) {
                    syncJournal();
                    if (fullSync && journalMode != SqlJetPagerJournalMode.MEMORY
                            && !fd.deviceCharacteristics().contains(SqlJetDeviceCharacteristics.IOCAP_SAFE_APPEND)) {
                        nRec = 0;
                        writeJournalHdr();
                    }
                }
                pPg.pDirty = null;
                if (pPg.getPageNumber() > dbSize && subjRequiresPage(pPg)) {
                    subjournalPage(pPg);
                }
                writePageList(pPg);
            } catch (SqlJetException e) {
                error(e);
            }
        }
        PAGERTRACE("STRESS %s page %d\n", PAGERID(), pPg.pgno);
        pageCache.makeClean(pPg);
    }

    /**
     * Ensure that there are at least nSavepoint savepoints open.
     */
    public void openSavepoint(int nSavepoint) throws SqlJetException {

        if (nSavepoint > this.nSavepoint && this.useJournal) {
            int ii;
            PagerSavepoint[] aNew;

            /*
             * Either there is no active journal or the sub-journal is open or
             * the journal is always stored in memory
             */
            assert (this.nSavepoint == 0 || this.sjfd != null || journalMode == SqlJetPagerJournalMode.MEMORY);

            /*
             * Grow the Pager.aSavepoint array using realloc(). Return
             * SQLITE_NOMEM if the allocation fails. Otherwise, zero the new
             * portion in case a malloc failure occurs while populating it in
             * the for(...) loop below.
             */
            aNew = new PagerSavepoint[nSavepoint];
            SqlJetUtility.memcpy(aNew, aSavepoint, nSavepoint);
            this.aSavepoint = aNew;
            ii = this.nSavepoint;
            this.nSavepoint = nSavepoint;

            /* Populate the PagerSavepoint structures just allocated. */
            for (/* no-op */; ii < nSavepoint; ii++) {
                assert (dbSizeValid);
                aNew[ii].nOrig = dbSize;
                if (journalOpen && journalOff > 0) {
                    aNew[ii].iOffset = journalOff;
                } else {
                    aNew[ii].iOffset = JOURNAL_HDR_SZ();
                }
                aNew[ii].iSubRec = stmtNRec;
                aNew[ii].pInSavepoint = new BitSet(dbSize);
            }

            /* Open the sub-journal, if it is not already opened. */
            openSubJournal();
        }

    }

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
     */
    public void savepoint(SqlJetSavepointOperation op, int iSavepoint) throws SqlJetException {

        SqlJetException rc = null;

        assert (op == SqlJetSavepointOperation.RELEASE || op == SqlJetSavepointOperation.ROLLBACK);

        if (iSavepoint < this.nSavepoint) {
            int ii;
            int nNew = iSavepoint + (op == SqlJetSavepointOperation.ROLLBACK ? 1 : 0);
            for (ii = nNew; ii < nSavepoint; ii++) {
                aSavepoint[ii].pInSavepoint = null;
            }
            nSavepoint = nNew;

            if (op == SqlJetSavepointOperation.ROLLBACK && jfd != null) {
                PagerSavepoint pSavepoint = (nNew == 0) ? null : aSavepoint[nNew - 1];
                try {
                    playbackSavepoint(pSavepoint);
                } catch (SqlJetException e) {
                    rc = e;
                    assert (rc.getErrorCode() != SqlJetErrorCode.DONE);
                }
            }

            /*
             * If this is a release of the outermost savepoint, truncate the
             * sub-journal.
             */
            if (nNew == 0 && op == SqlJetSavepointOperation.RELEASE && sjfd != null) {
                assert (rc == null);
                try {
                    sjfd.truncate(0);
                } catch (SqlJetException e) {
                    rc = e;
                }
                stmtNRec = 0;
            }
        }
        if (rc != null)
            throw rc;
    }

}
