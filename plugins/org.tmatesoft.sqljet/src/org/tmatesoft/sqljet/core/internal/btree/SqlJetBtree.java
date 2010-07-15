/**
 * SqlJetBtree.java
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

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetLogDefinitions;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.internal.ISqlJetBackend;
import org.tmatesoft.sqljet.core.internal.ISqlJetBtree;
import org.tmatesoft.sqljet.core.internal.ISqlJetBtreeCursor;
import org.tmatesoft.sqljet.core.internal.ISqlJetDbHandle;
import org.tmatesoft.sqljet.core.internal.ISqlJetFile;
import org.tmatesoft.sqljet.core.internal.ISqlJetFileSystem;
import org.tmatesoft.sqljet.core.internal.ISqlJetKeyInfo;
import org.tmatesoft.sqljet.core.internal.ISqlJetLimits;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.ISqlJetPage;
import org.tmatesoft.sqljet.core.internal.ISqlJetPageCallback;
import org.tmatesoft.sqljet.core.internal.ISqlJetPager;
import org.tmatesoft.sqljet.core.internal.SqlJetAutoVacuumMode;
import org.tmatesoft.sqljet.core.internal.SqlJetBtreeFlags;
import org.tmatesoft.sqljet.core.internal.SqlJetBtreeTableCreateFlags;
import org.tmatesoft.sqljet.core.internal.SqlJetDbFlags;
import org.tmatesoft.sqljet.core.internal.SqlJetFileOpenPermission;
import org.tmatesoft.sqljet.core.internal.SqlJetFileType;
import org.tmatesoft.sqljet.core.internal.SqlJetSafetyLevel;
import org.tmatesoft.sqljet.core.internal.SqlJetSavepointOperation;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;
import org.tmatesoft.sqljet.core.internal.btree.SqlJetBtreeCursor.CursorState;
import org.tmatesoft.sqljet.core.internal.mutex.SqlJetMutex;
import org.tmatesoft.sqljet.core.internal.pager.SqlJetPager;
import org.tmatesoft.sqljet.core.internal.schema.SqlJetSchema;
import org.tmatesoft.sqljet.core.table.ISqlJetBusyHandler;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetBtree implements ISqlJetBtree {

    private static Logger btreeLogger = Logger.getLogger(SqlJetLogDefinitions.SQLJET_LOG_BTREE);

    private static final boolean SQLJET_LOG_BTREE = SqlJetUtility.getBoolSysProp(SqlJetLogDefinitions.SQLJET_LOG_BTREE,
            false);

    static void TRACE(String format, Object... args) {
        if (SQLJET_LOG_BTREE) {
            SqlJetUtility.log(btreeLogger, format, args);
        }
    }

    private static final ISqlJetMemoryPointer PAGE1_21 = SqlJetUtility.wrapPtr(new byte[] { (byte) 0100, (byte) 040,
            (byte) 040 });

    /** The database connection holding this btree */
    ISqlJetDbHandle db;

    /** Sharable content of this btree */
    SqlJetBtreeShared pBt;

    /**
     * Btree.inTrans may take one of the following values.
     * 
     * If the shared-data extension is enabled, there may be multiple users of
     * the Btree structure. At most one of these may open a write transaction,
     * but any number may have active read transactions.
     */
    static enum TransMode {
        NONE, READ, WRITE
    }

    /** TRANS_NONE, TRANS_READ or TRANS_WRITE */
    TransMode inTrans;

    /** True if we can share pBt with another db */
    boolean sharable;

    /** True if db currently has pBt locked */
    boolean locked;

    /** Number of nested calls to sqlite3BtreeEnter() */
    int wantToLock;

    /** List of other sharable Btrees from the same db */
    SqlJetBtree pNext;

    /** Back pointer of the same list */
    SqlJetBtree pPrev;

    /**
     * A list of BtShared objects that are eligible for participation in shared
     * cache.
     */
    static List<SqlJetBtreeShared> sharedCacheList = new LinkedList<SqlJetBtreeShared>();

    /**
     * A bunch of assert() statements to check the transaction state variables
     * of handle p (type Btree*) are internally consistent.
     */
    private void integrity() {
        assert (pBt.inTransaction != TransMode.NONE || pBt.nTransaction == 0);
        assert (pBt.inTransaction.compareTo(inTrans) >= 0);
    }

    /**
     * @return the db
     */
    public ISqlJetDbHandle getDb() {
        return db;
    }

    /**
     * Invoke the busy handler for a btree.
     */
    public boolean invokeBusyHandler(int number) {
        assert (db != null);
        assert (db.getMutex().held());
        final ISqlJetBusyHandler busyHandler = db.getBusyHandler();
        if (busyHandler == null)
            return false;
        return busyHandler.call(number);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#enter()
     */
    public void enter() {
        SqlJetBtree p = this;
        SqlJetBtree pLater;

        /*
         * Some basic sanity checking on the Btree. The list of Btrees connected
         * by pNext and pPrev should be in sorted order by Btree.pBt value. All
         * elements of the list should belong to the same connection. Only
         * shared Btrees are on the list.
         */
        assert (p.pNext == null || p.pNext.pBt.hashCode() > p.pBt.hashCode());
        assert (p.pPrev == null || p.pPrev.pBt.hashCode() < p.pBt.hashCode());
        assert (p.pNext == null || p.pNext.db == p.db);
        assert (p.pPrev == null || p.pPrev.db == p.db);
        assert (p.sharable || (p.pNext == null && p.pPrev == null));

        /* Check for locking consistency */
        assert (!p.locked || p.wantToLock > 0);
        assert (p.sharable || p.wantToLock == 0);

        /* We should already hold a lock on the database connection */
        assert (p.db.getMutex().held());

        if (!p.sharable)
            return;
        p.wantToLock++;
        if (p.locked)
            return;

        /*
         * In most cases, we should be able to acquire the lock we want without
         * having to go throught the ascending lock procedure that follows. Just
         * be sure not to block.
         */
        if (p.pBt.mutex.attempt()) {
            p.locked = true;
            return;
        }

        /*
         * To avoid deadlock, first release all locks with a larger BtShared
         * address. Then acquire our lock. Then reacquire the other BtShared
         * locks that we used to hold in ascending order.
         */
        for (pLater = p.pNext; pLater != null; pLater = pLater.pNext) {
            assert (pLater.sharable);
            assert (pLater.pNext == null || pLater.pNext.pBt.hashCode() > pLater.pBt.hashCode());
            assert (!pLater.locked || pLater.wantToLock > 0);
            if (pLater.locked) {
                pLater.pBt.mutex.leave();
                pLater.locked = false;
            }
        }
        p.pBt.mutex.enter();
        p.locked = true;
        for (pLater = p.pNext; pLater != null; pLater = pLater.pNext) {
            if (pLater.wantToLock > 0) {
                pLater.pBt.mutex.enter();
                pLater.locked = true;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#leave()
     */
    public void leave() {
        SqlJetBtree p = this;
        if (p.sharable) {
            assert (p.wantToLock > 0);
            p.wantToLock--;
            if (p.wantToLock == 0) {
                assert (p.locked);
                p.pBt.mutex.leave();
                p.locked = false;
            }
        }
    }

    /**
     * Return true if the BtShared mutex is held on the btree.
     * 
     * This routine makes no determination one why or another if the database
     * connection mutex is held.
     * 
     * This routine is used only from within assert() statements.
     */
    boolean holdsMutex() {
        return !sharable || (locked && wantToLock != 0 && pBt.mutex.held());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#open(java.io.File,
     * org.tmatesoft.sqljet.core.ISqlJetDb, java.util.Set,
     * org.tmatesoft.sqljet.core.SqlJetFileType, java.util.Set)
     */
    public void open(File filename, ISqlJetDbHandle db, Set<SqlJetBtreeFlags> flags, final SqlJetFileType type,
            final Set<SqlJetFileOpenPermission> permissions) throws SqlJetException {

        ISqlJetFileSystem pVfs; /* The VFS to use for this btree */
        SqlJetBtreeShared pBt = null; /* Shared part of btree structure */
        int nReserve;
        ISqlJetMemoryPointer zDbHeader = SqlJetUtility.allocatePtr(100);

        /*
         * Set the variable isMemdb to true for an in-memory database, or false
         * for a file-based database. This symbol is only required if either of
         * the shared-data or autovacuum features are compiled into the library.
         */
        final boolean isMemdb = filename != null && ISqlJetPager.MEMORY_DB.equals(filename.getPath());

        assert (db != null);

        pVfs = db.getFileSystem();
        this.inTrans = TransMode.NONE;
        this.db = db;

        /*
         * If this Btree is a candidate for shared cache, try to find an
         * existing BtShared object that we can share with
         */
        if (!isMemdb && !db.getFlags().contains(SqlJetDbFlags.Vtab) && filename != null
                && !"".equals(filename.getPath())) {
            if (db.getConfig().isSharedCacheEnabled()) {
                this.sharable = true;
                db.getFlags().add(SqlJetDbFlags.SharedCache);
                final String fullPathname = pVfs.getFullPath(filename);
                synchronized (sharedCacheList) {
                    final Iterator<SqlJetBtreeShared> i = sharedCacheList.iterator();
                    while (i.hasNext()) {
                        pBt = i.next();
                        assert (pBt.nRef > 0);
                        final String pagerFilename = pVfs.getFullPath(pBt.pPager.getFileName());
                        if (fullPathname.equals(pagerFilename) && pVfs == pBt.pPager.getFileSystem()) {
                            this.pBt = pBt;
                            pBt.nRef++;
                            break;
                        }
                    }
                }
            }
        }

        try {
            if (this.pBt == null) {
                /*
                 * The following asserts make sure that structures used by the
                 * btree are the right size. This is to guard against size
                 * changes that result when compiling on a different
                 * architecture.
                 */
                // assert( sizeof(i64)==8 || sizeof(i64)==4 );
                // assert( sizeof(u64)==8 || sizeof(u64)==4 );
                // assert( sizeof(u32)==4 );
                // assert( sizeof(u16)==2 );
                // assert( sizeof(Pgno)==4 );
                pBt = new SqlJetBtreeShared();
                pBt.pPager = new SqlJetPager();
                pBt.pPager.open(pVfs, filename, SqlJetBtreeFlags.toPagerFlags(flags), type, permissions);
                pBt.pPager.readFileHeader(zDbHeader.remaining(), zDbHeader);
                pBt.pPager.setBusyhandler(new ISqlJetBusyHandler() {
                    public boolean call(int number) {
                        return invokeBusyHandler(number);
                    }
                });
                this.pBt = pBt;
                pBt.pPager.setReiniter(new ISqlJetPageCallback() {
                    public void pageCallback(ISqlJetPage page) throws SqlJetException {
                        pageReinit(page);
                    }
                });

                pBt.pCursor = null;
                pBt.pPage1 = null;
                pBt.readOnly = pBt.pPager.isReadOnly();
                pBt.pageSize = SqlJetUtility.get2byte(zDbHeader, 16);

                if (pBt.pageSize < ISqlJetLimits.SQLJET_MIN_PAGE_SIZE
                        || pBt.pageSize > ISqlJetLimits.SQLJET_MAX_PAGE_SIZE
                        || ((pBt.pageSize - 1) & pBt.pageSize) != 0) {
                    pBt.pageSize = ISqlJetLimits.SQLJET_DEFAULT_PAGE_SIZE;
                    pBt.pageSize = pBt.pPager.setPageSize(pBt.pageSize);
                    /*
                     * If the magic name ":memory:" will create an in-memory
                     * database, then leave the autoVacuum mode at 0 (do not
                     * auto-vacuum), even if SQLITE_DEFAULT_AUTOVACUUM is true.
                     * On the other hand, if SQLITE_OMIT_MEMORYDB has been
                     * defined, then ":memory:" is just a regular file-name. In
                     * this case the auto-vacuum applies as per normal.
                     */
                    if (null != filename && !isMemdb) {
                        pBt.autoVacuum = SQLJET_DEFAULT_AUTOVACUUM != SqlJetAutoVacuumMode.NONE;
                        pBt.incrVacuum = SQLJET_DEFAULT_AUTOVACUUM == SqlJetAutoVacuumMode.FULL;
                    }
                    nReserve = 0;
                } else {
                    nReserve = SqlJetUtility.getUnsignedByte(zDbHeader, 20);
                    pBt.pageSizeFixed = true;
                    pBt.autoVacuum = (SqlJetUtility.get4byte(zDbHeader, 36 + 4 * 4) != 0);
                    pBt.incrVacuum = (SqlJetUtility.get4byte(zDbHeader, 36 + 7 * 4) != 0);
                }
                pBt.usableSize = pBt.pageSize - nReserve;
                assert ((pBt.pageSize & 7) == 0); /*
                                                   * 8-byte alignment of
                                                   * pageSize
                                                   */
                pBt.pageSize = pBt.pPager.setPageSize(pBt.pageSize);

                /*
                 * Add the new BtShared object to the linked list sharable
                 * BtShareds.
                 */
                if (this.sharable) {
                    pBt.mutex = new SqlJetMutex();
                    pBt.nRef = 1;
                    synchronized (sharedCacheList) {
                        sharedCacheList.add(pBt);
                    }
                }
            }

            /*
             * If the new Btree uses a sharable pBtShared, then link the new
             * Btree into the list of all sharable Btrees for the same
             * connection. The list is kept in ascending order by pBt address.
             */
            if (this.sharable) {
                for (final ISqlJetBackend backend : db.getBackends()) {
                    final ISqlJetBtree btree = backend.getBtree();
                    if (btree == null || !(btree instanceof SqlJetBtree))
                        continue;
                    SqlJetBtree pSib = (SqlJetBtree) btree;
                    if (pSib.sharable) {
                        while (pSib.pPrev != null) {
                            pSib = pSib.pPrev;
                        }
                        if (this.pBt.hashCode() < pSib.pBt.hashCode()) {
                            this.pNext = pSib;
                            this.pPrev = null;
                            pSib.pPrev = this;
                        } else {
                            while (pSib.pNext != null && pSib.pNext.pBt.hashCode() < this.pBt.hashCode()) {
                                pSib = pSib.pNext;
                            }
                            this.pNext = pSib.pNext;
                            this.pPrev = pSib;
                            if (this.pNext != null) {
                                this.pNext.pPrev = this;
                            }
                            pSib.pNext = this;
                        }
                        break;
                    }
                }
            }

        } catch (SqlJetException e) {

            // btree_open_out:
            if (pBt != null && pBt.pPager != null) {
                pBt.pPager.close();
            }
            throw e;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#close()
     */
    public void close() throws SqlJetException {
        SqlJetBtree p = this;
        SqlJetBtreeCursor pCur;

        /* Close all cursors opened via this handle. */

        assert (p.db.getMutex().held());
        p.enter();
        try {
            pBt.db = db;
            pCur = pBt.pCursor;
            while (pCur != null) {
                SqlJetBtreeCursor pTmp = pCur;
                pCur = pCur.pNext;
                if (pTmp.pBtree == p) {
                    pTmp.closeCursor();
                }
            }

            /*
             * Rollback any active transaction and free the handle structure.
             * The call to sqlite3BtreeRollback() drops any table-locks held by
             * this handle.
             */
            p.rollback();
        } finally {
            p.leave();
        }

        /*
         * If there are still other outstanding references to the shared-btree
         * structure, return now. The remainder of this procedure cleans up the
         * shared-btree.
         */
        assert (p.wantToLock == 0 && !p.locked);
        if (!p.sharable || removeFromSharingList(pBt)) {
            /*
             * The pBt is no longer on the sharing list, so we can access it
             * without having to hold the mutex.
             * 
             * Clean out and delete the BtShared object.
             */
            assert (pBt.pCursor == null);
            pBt.pPager.close();
            pBt.pSchema = null;
            pBt.pTmpSpace = null;
        }
        pBt = null;

        assert (p.wantToLock == 0);
        assert (!p.locked);
        if (p.pPrev != null)
            p.pPrev.pNext = p.pNext;
        if (p.pNext != null)
            p.pNext.pPrev = p.pPrev;
    }

    /**
     * Decrement the BtShared.nRef counter. When it reaches zero, remove the
     * BtShared structure from the sharing list. Return true if the
     * BtShared.nRef counter reaches zero and return false if it is still
     * positive.
     * 
     * @param bt
     * @return
     */
    static private boolean removeFromSharingList(SqlJetBtreeShared pBt) {
        boolean removed = false;
        // assert (!pBt.mutex.held());
        synchronized (sharedCacheList) {
            pBt.mutex.enter();
            try {
                pBt.nRef--;
            } finally {
                pBt.mutex.leave();
            }
            if (pBt.nRef <= 0) {
                sharedCacheList.remove(pBt);
                removed = true;
            }
        }
        return removed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#setCacheSize(int)
     */
    public void setCacheSize(int mxPage) {
        assert (db.getMutex().held());
        enter();
        try {
            pBt.pPager.setCacheSize(mxPage);
        } finally {
            leave();
        }
    }

    public int getCacheSize() {
        assert (db.getMutex().held());
        enter();
        try {
            return pBt.pPager.getCacheSize();
        } finally {
            leave();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetBtree#setSafetyLevel(org.tmatesoft.sqljet
     * .core.SqlJetSafetyLevel)
     */
    public void setSafetyLevel(SqlJetSafetyLevel level) {
        assert (db.getMutex().held());
        enter();
        try {
            pBt.pPager.setSafetyLevel(level);
        } finally {
            leave();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#isSyncDisabled()
     */
    public boolean isSyncDisabled() {
        assert (db.getMutex().held());
        enter();
        try {
            assert (pBt != null && pBt.pPager != null);
            return pBt.pPager.isNoSync();
        } finally {
            leave();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#setPageSize(int, int)
     */
    public void setPageSize(int pageSize, int reserve) throws SqlJetException {
        assert (reserve >= -1 && reserve <= 255);
        enter();
        try {
            if (pBt.pageSizeFixed) {
                throw new SqlJetException(SqlJetErrorCode.READONLY);
            }
            if (reserve < 0) {
                reserve = pBt.pageSize - pBt.usableSize;
            }
            assert (reserve >= 0 && reserve <= 255);
            if (pageSize >= ISqlJetLimits.SQLJET_MIN_PAGE_SIZE && pageSize <= ISqlJetLimits.SQLJET_MAX_PAGE_SIZE
                    && ((pageSize - 1) & pageSize) == 0) {
                assert ((pageSize & 7) == 0);
                assert (pBt.pPage1 == null && pBt.pCursor == null);
                pBt.pageSize = pageSize;
                pBt.pTmpSpace = null;
                pBt.pageSize = pBt.pPager.setPageSize(pBt.pageSize);
            }
            pBt.usableSize = pBt.pageSize - reserve;
        } finally {
            leave();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#getPageSize()
     */
    public int getPageSize() {
        return pBt.pageSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#setMaxPageCount(int)
     */
    public void setMaxPageCount(int mxPage) throws SqlJetException {
        enter();
        try {
            pBt.pPager.setMaxPageCount(mxPage);
        } finally {
            leave();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#getReserve()
     */
    public int getReserve() {
        enter();
        try {
            return pBt.pageSize - pBt.usableSize;
        } finally {
            leave();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetBtree#setAutoVacuum(org.tmatesoft.sqljet
     * .core.SqlJetAutoVacuumMode)
     */
    public void setAutoVacuum(SqlJetAutoVacuumMode autoVacuum) throws SqlJetException {
        boolean av = autoVacuum != SqlJetAutoVacuumMode.NONE;
        enter();
        try {
            if (pBt.pageSizeFixed && av != pBt.autoVacuum) {
                throw new SqlJetException(SqlJetErrorCode.READONLY);
            } else {
                pBt.autoVacuum = av;
            }
        } finally {
            leave();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#getAutoVacuum()
     */
    public SqlJetAutoVacuumMode getAutoVacuum() {
        enter();
        try {
            return !pBt.autoVacuum ? SqlJetAutoVacuumMode.NONE : !pBt.incrVacuum ? SqlJetAutoVacuumMode.FULL
                    : SqlJetAutoVacuumMode.INCR;
        } finally {
            leave();
        }
    }

    /**
     * Get a reference to pPage1 of the database file. This will also acquire a
     * readlock on that file.
     * 
     * SQLITE_OK is returned on success. If the file is not a well-formed
     * database file, then SQLITE_CORRUPT is returned. SQLITE_BUSY is returned
     * if the database is locked. SQLITE_NOMEM is returned if we run out of
     * memory.
     */
    private void lockBtree() throws SqlJetException {

        SqlJetErrorCode rc = null;
        SqlJetMemPage pPage1;
        int nPage;

        assert (pBt.mutex.held());

        if (pBt.pPage1 != null)
            return;

        pPage1 = pBt.getPage(1, false);

        try {
            /*
             * Do some checking to help insure the file we opened really is a
             * valid database file.
             */
            nPage = pBt.pPager.getPageCount();
            if (nPage > 0) {

                int pageSize;
                int usableSize;
                ISqlJetMemoryPointer page1 = pPage1.aData;
                rc = SqlJetErrorCode.NOTADB;
                if (SqlJetUtility.memcmp(page1, zMagicHeader, 16) != 0) {
                    throw new SqlJetException(rc);
                }
                if (SqlJetUtility.getUnsignedByte(page1, 18) > 1) {
                    pBt.readOnly = true;
                }
                if (SqlJetUtility.getUnsignedByte(page1, 19) > 1) {
                    throw new SqlJetException(rc);
                }

                /*
                 * The maximum embedded fraction must be exactly 25%. And the
                 * minimum embedded fraction must be 12.5% for both leaf-data
                 * and non-leaf-data. The original design allowed these amounts
                 * to vary, but as of version 3.6.0, we require them to be
                 * fixed.
                 */
                if (SqlJetUtility.memcmp(page1, 21, PAGE1_21, 0, 3) != 0) {
                    throw new SqlJetException(rc);
                }

                pageSize = SqlJetUtility.get2byte(page1, 16);
                if (((pageSize - 1) & pageSize) != 0 || pageSize < ISqlJetLimits.SQLJET_MIN_PAGE_SIZE
                        || (ISqlJetLimits.SQLJET_MAX_PAGE_SIZE < 32768)) {
                    throw new SqlJetException(rc);
                }
                assert ((pageSize & 7) == 0);
                usableSize = pageSize - SqlJetUtility.getUnsignedByte(page1, 20);
                if (pageSize != pBt.pageSize) {
                    /*
                     * After reading the first page of the database assuming a
                     * page size of BtShared.pageSize, we have discovered that
                     * the page-size is actually pageSize. Unlock the database,
                     * leave pBt->pPage1 at zero and return SQLITE_OK. The
                     * caller will call this function again with the correct
                     * page-size.
                     */
                    SqlJetMemPage.releasePage(pPage1);
                    pBt.usableSize = usableSize;
                    pBt.pageSize = pageSize;
                    freeTempSpace(pBt);
                    pBt.pageSize = pBt.pPager.setPageSize(pBt.pageSize);
                    return;
                }
                if (usableSize < 500) {
                    throw new SqlJetException(rc);
                }
                pBt.pageSize = pageSize;
                pBt.usableSize = usableSize;
                pBt.autoVacuum = (SqlJetUtility.get4byte(page1, 36 + 4 * 4) > 0);
                pBt.incrVacuum = (SqlJetUtility.get4byte(page1, 36 + 7 * 4) > 0);
            }

            /*
             * maxLocal is the maximum amount of payload to store locally for a
             * cell. Make sure it is small enough so that at least minFanout
             * cells can will fit on one page. We assume a 10-byte page header.
             * Besides the payload, the cell must store: 2-byte pointer to the
             * cell 4-byte child pointer 9-byte nKey value 4-byte nData value
             * 4-byte overflow page pointer So a cell consists of a 2-byte
             * poiner, a header which is as much as 17 bytes long, 0 to N bytes
             * of payload, and an optional 4 byte overflow page pointer.
             */
            pBt.maxLocal = (pBt.usableSize - 12) * 64 / 255 - 23;
            pBt.minLocal = (pBt.usableSize - 12) * 32 / 255 - 23;
            pBt.maxLeaf = pBt.usableSize - 35;
            pBt.minLeaf = (pBt.usableSize - 12) * 32 / 255 - 23;
            assert (pBt.maxLeaf + 23 <= pBt.MX_CELL_SIZE());
            pBt.pPage1 = pPage1;
            return;

        } catch (SqlJetException e) {
            // page1_init_failed:
            SqlJetMemPage.releasePage(pPage1);
            pBt.pPage1 = null;
            throw e;
        }
    }

    /**
     * @param bt
     */
    private void freeTempSpace(SqlJetBtreeShared bt) {
        bt.pTmpSpace = null;
    }

    /**
     * Create a new database by initializing the first page of the file.
     */
    private void newDatabase() throws SqlJetException {
        assert (pBt.mutex.held());
        int nPage = pBt.pPager.getPageCount();
        if (nPage > 0) {
            return;
        }

        SqlJetMemPage pP1 = pBt.pPage1;
        assert (pP1 != null);
        ISqlJetMemoryPointer data = pP1.aData;
        pP1.pDbPage.write();
        SqlJetUtility.memcpy(data, zMagicHeader, zMagicHeader.remaining());
        assert (zMagicHeader.remaining() == 16);
        SqlJetUtility.put2byte(data, 16, pBt.pageSize);
        SqlJetUtility.putUnsignedByte(data, 18, (byte) 1);
        SqlJetUtility.putUnsignedByte(data, 19, (byte) 1);
        assert (pBt.usableSize <= pBt.pageSize && pBt.usableSize + 255 >= pBt.pageSize);
        SqlJetUtility.putUnsignedByte(data, 20, (byte) (pBt.pageSize - pBt.usableSize));
        SqlJetUtility.putUnsignedByte(data, 21, (byte) 64);
        SqlJetUtility.putUnsignedByte(data, 22, (byte) 32);
        SqlJetUtility.putUnsignedByte(data, 23, (byte) 32);
        SqlJetUtility.memset(data, 24, (byte) 0, 100 - 24);
        pP1.zeroPage(SqlJetMemPage.PTF_INTKEY | SqlJetMemPage.PTF_LEAF | SqlJetMemPage.PTF_LEAFDATA);
        pBt.pageSizeFixed = true;
        SqlJetUtility.put4byte(data, 36 + 4 * 4, pBt.autoVacuum ? 1 : 0);
        SqlJetUtility.put4byte(data, 36 + 7 * 4, pBt.incrVacuum ? 1 : 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetBtree#beginTrans(org.tmatesoft.sqljet
     * .core.SqlJetTransactionMode)
     */
    public void beginTrans(SqlJetTransactionMode mode) throws SqlJetException {

        SqlJetException rc = null;

        enter();

        try {

            pBt.db = db;
            integrity();

            /*
             * If the btree is already in a write-transaction, or it is already
             * in a read-transaction and a read-transaction is requested, this
             * is a no-op.
             */
            if (inTrans == TransMode.WRITE || (inTrans == TransMode.READ && mode == SqlJetTransactionMode.READ_ONLY)) {
                return;
            }

            /* Write transactions are not possible on a read-only database */
            if (pBt.readOnly && mode != SqlJetTransactionMode.READ_ONLY) {
                throw new SqlJetException(SqlJetErrorCode.READONLY);
            }

            /*
             * If another database handle has already opened a write transaction
             * on this shared-btree structure and a second write transaction is
             * requested, return SQLITE_BUSY.
             */
            if (pBt.inTransaction == TransMode.WRITE && mode != SqlJetTransactionMode.READ_ONLY) {
                throw new SqlJetException(SqlJetErrorCode.BUSY);
            }

            if (mode == SqlJetTransactionMode.EXCLUSIVE) {
                Iterator<SqlJetBtreeLock> pIter;
                for (pIter = pBt.pLock.iterator(); pIter.hasNext();) {
                    if (pIter.next().pBtree != this) {
                        throw new SqlJetException(SqlJetErrorCode.BUSY);
                    }
                }
            }

            int nBusy = 0;
            do {
                try {

                    if (pBt.pPage1 == null) {
                        do {
                            lockBtree();
                        } while (pBt.pPage1 == null);
                    }

                    if (mode != SqlJetTransactionMode.READ_ONLY) {
                        if (pBt.readOnly) {
                            throw new SqlJetException(SqlJetErrorCode.READONLY);
                        } else {
                            pBt.pPager.begin(mode == SqlJetTransactionMode.EXCLUSIVE);
                            newDatabase();
                        }
                    }

                    if (mode != SqlJetTransactionMode.READ_ONLY)
                        pBt.inStmt = false;

                } catch (SqlJetException e) {
                    rc = e;
                    pBt.unlockBtreeIfUnused();
                }

            } while (rc != null && rc.getErrorCode() == SqlJetErrorCode.BUSY && pBt.inTransaction == TransMode.NONE
                    && invokeBusyHandler(nBusy) && (nBusy++) > -1);

            if (rc == null) {
                if (inTrans == TransMode.NONE) {
                    pBt.nTransaction++;
                }
                inTrans = (mode != SqlJetTransactionMode.READ_ONLY ? TransMode.WRITE : TransMode.READ);
                if (inTrans.compareTo(pBt.inTransaction) > 0) {
                    pBt.inTransaction = inTrans;
                }
                if (mode == SqlJetTransactionMode.EXCLUSIVE) {
                    assert (pBt.pExclusive == null);
                    pBt.pExclusive = this;
                }
            }

        } catch (SqlJetException e) {
            rc = e;
        } finally {
            // trans_begun:

            if (rc == null && mode != SqlJetTransactionMode.READ_ONLY) {
                /*
                 * This call makes sure that the pager has the correct number of
                 * open savepoints. If the second parameter is greater than 0
                 * and the sub-journal is not already open, then it will be
                 * opened here.
                 */
                try {
                    pBt.pPager.openSavepoint(db.getSavepointNum());
                } catch (SqlJetException e) {
                    rc = e;
                }
            }

            integrity();
            leave();
            if (rc != null)
                throw rc;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetBtree#commitPhaseOne(java.lang.String)
     */
    public void commitPhaseOne(String master) throws SqlJetException {
        if (this.inTrans == TransMode.WRITE) {
            enter();
            try {
                pBt.db = this.db;
                if (pBt.autoVacuum) {
                    pBt.autoVacuumCommit();
                }
                pBt.pPager.commitPhaseOne(master, false);
            } finally {
                leave();
            }
        }
    }

    /**
     * Release all the table locks (locks obtained via calls to the lockTable()
     * procedure) held by Btree handle p.
     * 
     */
    private void unlockAllTables() {

        Iterator<SqlJetBtreeLock> ppIter = pBt.pLock.iterator();

        assert (holdsMutex());
        assert (sharable || !ppIter.hasNext());

        while (ppIter.hasNext()) {
            SqlJetBtreeLock pLock = ppIter.next();
            assert (pBt.pExclusive == null || pBt.pExclusive == pLock.pBtree);
            if (pLock.pBtree == this) {
                ppIter.remove();
            }
        }

        if (pBt.pExclusive == this) {
            pBt.pExclusive = null;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#commitPhaseTwo()
     */
    public void commitPhaseTwo() throws SqlJetException {

        enter();
        try {

            pBt.db = this.db;
            integrity();

            /*
             * If the handle has a write-transaction open, commit the
             * shared-btrees transaction and set the shared state to TRANS_READ.
             */
            if (this.inTrans == TransMode.WRITE) {
                assert (pBt.inTransaction == TransMode.WRITE);
                assert (pBt.nTransaction > 0);
                pBt.pPager.commitPhaseTwo();
                pBt.inTransaction = TransMode.READ;
                pBt.inStmt = false;
            }
            unlockAllTables();

            /*
             * If the handle has any kind of transaction open, decrement the
             * transaction count of the shared btree. If the transaction count
             * reaches 0, set the shared state to TRANS_NONE. The
             * unlockBtreeIfUnused() call below will unlock the pager.
             */
            if (this.inTrans != TransMode.NONE) {
                pBt.nTransaction--;
                if (0 == pBt.nTransaction) {
                    pBt.inTransaction = TransMode.NONE;
                }
            }

            /*
             * Set the handles current transaction state to TRANS_NONE and
             * unlock the pager if this call closed the only read or write
             * transaction.
             */
            this.inTrans = TransMode.NONE;
            pBt.unlockBtreeIfUnused();

            integrity();

        } finally {
            leave();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#commit()
     */
    public void commit() throws SqlJetException {
        enter();
        try {
            commitPhaseOne(null);
            commitPhaseTwo();
        } finally {
            leave();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#rollback()
     */
    public void rollback() throws SqlJetException {

        SqlJetMemPage pPage1;

        enter();

        try {

            pBt.db = this.db;
            try {
                pBt.saveAllCursors(0, null);
            } catch (SqlJetException e) {
                /*
                 * This is a horrible situation. An IO or malloc() error occured
                 * whilst trying to save cursor positions. If this is an
                 * automatic rollback (as the result of a constraint, malloc()
                 * failure or IO error) then the cache may be internally
                 * inconsistent (not contain valid trees) so we cannot simply
                 * return the error to the caller. Instead, abort all queries
                 * that may be using any of the cursors that failed to save.
                 */
                tripAllCursors(e.getErrorCode());
            }
            integrity();
            unlockAllTables();

            try {
                if (this.inTrans == TransMode.WRITE) {
                    assert (TransMode.WRITE == pBt.inTransaction);
                    try {
                        pBt.pPager.rollback();
                    } finally {
                        /*
                         * The rollback may have destroyed the pPage1->aData
                         * value. So call sqlite3BtreeGetPage() on page 1 again
                         * to make sure pPage1->aData is set correctly.
                         */
                        try {
                            pPage1 = pBt.getPage(1, false);
                            SqlJetMemPage.releasePage(pPage1);
                        } finally {
                            // assert (pBt.countWriteCursors() == 0);
                            pBt.inTransaction = TransMode.READ;
                        }
                    }
                }
            } finally {

                if (this.inTrans != TransMode.NONE) {
                    assert (pBt.nTransaction > 0);
                    pBt.nTransaction--;
                    if (0 == pBt.nTransaction) {
                        pBt.inTransaction = TransMode.NONE;
                    }
                }

                this.inTrans = TransMode.NONE;
                pBt.inStmt = false;
                pBt.unlockBtreeIfUnused();

                integrity();

            }

        } finally {
            leave();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#beginStmt()
     */
    public void beginStmt() throws SqlJetException {
        enter();
        try {
            pBt.db = this.db;
            assert (this.inTrans == TransMode.WRITE);
            assert (!pBt.inStmt);
            assert (!pBt.readOnly);
            if (this.inTrans != TransMode.WRITE || pBt.inStmt || pBt.readOnly) {
                throw new SqlJetException(SqlJetErrorCode.INTERNAL);
            } else {
                assert (pBt.inTransaction == TransMode.WRITE);
                /*
                 * At the pager level, a statement transaction is a savepoint
                 * with an index greater than all savepoints created explicitly
                 * using SQL statements. It is illegal to open, release or
                 * rollback any such savepoints while the statement transaction
                 * savepoint is active.
                 */
                try {
                    pBt.pPager.openSavepoint(this.db.getSavepointNum() + 1);
                } finally {
                    pBt.inStmt = true;
                }
            }
        } finally {
            leave();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#commitStmt()
     */
    public void commitStmt() throws SqlJetException {
        enter();
        try {
            pBt.db = this.db;
            assert (!pBt.readOnly);
            if (pBt.inStmt)
                try {
                    int iStmtpoint = this.db.getSavepointNum();
                    pBt.pPager.savepoint(SqlJetSavepointOperation.RELEASE, iStmtpoint);
                } finally {
                    pBt.inStmt = false;
                }
        } finally {
            leave();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#rollbackStmt()
     */
    public void rollbackStmt() throws SqlJetException {
        enter();
        try {
            pBt.db = this.db;
            assert (!pBt.readOnly);
            if (pBt.inStmt)
                try {
                    int iStmtpoint = this.db.getSavepointNum();
                    pBt.pPager.savepoint(SqlJetSavepointOperation.ROLLBACK, iStmtpoint);
                    pBt.pPager.savepoint(SqlJetSavepointOperation.RELEASE, iStmtpoint);
                } finally {
                    pBt.inStmt = false;
                }
        } finally {
            leave();
        }
    }

    /**
     * During a rollback, when the pager reloads information into the cache so
     * that the cache is restored to its original state at the start of the
     * transaction, for each page restored this routine is called.
     * 
     * This routine needs to reset the extra data section at the end of the page
     * to agree with the restored data.
     * 
     * @param page
     * @throws SqlJetException
     */
    protected void pageReinit(ISqlJetPage page) throws SqlJetException {
        final SqlJetMemPage pPage = (SqlJetMemPage) page.getExtra();
        if (pPage != null && pPage.isInit) {
            assert (pPage.pBt.mutex.held());
            pPage.isInit = false;
            if (page.getRefCount() > 0) {
                pPage.initPage();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#createTable(java.util.Set)
     */
    public int createTable(Set<SqlJetBtreeTableCreateFlags> flags) throws SqlJetException {
        enter();
        try {
            pBt.db = db;
            return doCreateTable(flags);
        } finally {
            leave();
        }
    }

    /**
     * @param flags
     * @return
     * @throws SqlJetException
     */
    private int doCreateTable(Set<SqlJetBtreeTableCreateFlags> flags) throws SqlJetException {
        SqlJetMemPage pRoot;
        int pgnoRoot;

        assert (holdsMutex());
        assert (pBt.inTransaction == TransMode.WRITE);
        assert (!pBt.readOnly);

        if (pBt.autoVacuum) {
            /* Move a page here to make room for the root-page */
            int[] pgnoMove = new int[1];
            SqlJetMemPage pPageMove; /* The page to move to. */

            /*
             * Creating a new table may probably require moving an existing
             * database* to make room for the new tables root page. In case this
             * page turns* out to be an overflow page, delete all overflow
             * page-map caches* held by open cursors.
             */
            pBt.invalidateAllOverflowCache();

            /*
             * Read the value of meta[3] from the database to determine where
             * the* root page of the new table should go. meta[3] is the largest
             * root-page* created so far, so the new root-page is (meta[3]+1).
             */
            pgnoRoot = getMeta(4);
            pgnoRoot++;

            /*
             * The new root-page may not be allocated on a pointer-map page, or
             * the* PENDING_BYTE page.
             */
            while (pgnoRoot == pBt.PTRMAP_PAGENO(pgnoRoot) || pgnoRoot == pBt.PENDING_BYTE_PAGE()) {
                pgnoRoot++;
            }
            assert (pgnoRoot >= 3);

            /*
             * Allocate a page. The page that currently resides at pgnoRoot will
             * * be moved to the allocated page (unless the allocated page
             * happens* to reside at pgnoRoot).
             */
            pPageMove = pBt.allocatePage(pgnoMove, pgnoRoot, true);

            if (pgnoMove[0] != pgnoRoot) {
                /*
                 * pgnoRoot is the page that will be used for the root-page of*
                 * the new table (assuming an error did not occur). But we were*
                 * allocated pgnoMove. If required (i.e. if it was not allocated
                 * * by extending the file), the current page at position
                 * pgnoMove* is already journaled.
                 */
                short[] eType = { 0 };
                int[] iPtrPage = { 0 };

                SqlJetMemPage.releasePage(pPageMove);

                /* Move the page currently at pgnoRoot to pgnoMove. */
                pRoot = pBt.getPage(pgnoRoot, false);
                try {
                    pBt.ptrmapGet(pgnoRoot, eType, iPtrPage);
                    if (eType[0] == SqlJetBtreeShared.PTRMAP_ROOTPAGE || eType[0] == SqlJetBtreeShared.PTRMAP_FREEPAGE) {
                        throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                    }
                } catch (SqlJetException e) {
                    SqlJetMemPage.releasePage(pRoot);
                    throw e;
                }
                assert (eType[0] != SqlJetBtreeShared.PTRMAP_ROOTPAGE);
                assert (eType[0] != SqlJetBtreeShared.PTRMAP_FREEPAGE);
                try {
                    pRoot.pDbPage.write();
                } catch (SqlJetException e) {
                    SqlJetMemPage.releasePage(pRoot);
                    throw e;
                }
                try {
                    pBt.relocatePage(pRoot, eType[0], iPtrPage[0], pgnoMove[0], false);
                } finally {
                    SqlJetMemPage.releasePage(pRoot);
                }

                /* Obtain the page at pgnoRoot */
                pRoot = pBt.getPage(pgnoRoot, false);
                try {
                    pRoot.pDbPage.write();
                } catch (SqlJetException e) {
                    SqlJetMemPage.releasePage(pRoot);
                    throw e;
                }
            } else {
                pRoot = pPageMove;
            }

            /*
             * Update the pointer-map and meta-data with the new root-page
             * number.
             */
            try {
                pBt.ptrmapPut(pgnoRoot, SqlJetBtreeShared.PTRMAP_ROOTPAGE, 0);
            } catch (SqlJetException e) {
                SqlJetMemPage.releasePage(pRoot);
                throw e;
            }
            try {
                updateMeta(4, pgnoRoot);
            } catch (SqlJetException e) {
                SqlJetMemPage.releasePage(pRoot);
                throw e;
            }

        } else {
            int[] a = new int[1];
            pRoot = pBt.allocatePage(a, 1, false);
            pgnoRoot = a[0];
        }

        // assert( sqlite3PagerIswriteable(pRoot->pDbPage) );
        try {
            pRoot.zeroPage(SqlJetBtreeTableCreateFlags.toByte(flags) | SqlJetMemPage.PTF_LEAF);
        } finally {
            pRoot.pDbPage.unref();
        }

        return pgnoRoot;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#isInTrans()
     */
    public boolean isInTrans() {
        assert (db.getMutex().held());
        return inTrans == TransMode.WRITE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#isInStmt()
     */
    public boolean isInStmt() {
        assert (holdsMutex());
        return pBt != null && pBt.inStmt;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#isInReadTrans()
     */
    public boolean isInReadTrans() {
        assert (db.getMutex().held());
        return inTrans == TransMode.NONE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#getSchema()
     */
    public SqlJetSchema getSchema() {
        return (SqlJetSchema) pBt.pSchema;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#setSchema(java.lang.Object)
     */
    public void setSchema(SqlJetSchema schema) {
        pBt.pSchema = schema;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#isSchemaLocked()
     */
    public boolean isSchemaLocked() {
        assert (db.getMutex().held());
        enter();
        try {
            return queryTableLock(ISqlJetDbHandle.MASTER_ROOT, SqlJetBtreeLockMode.READ);
        } finally {
            leave();
        }
    }

    /**
     * Query to see if btree handle p may obtain a lock of type eLock (READ_LOCK
     * or WRITE_LOCK) on the table with root-page iTab. Return SQLITE_OK if the
     * lock may be obtained (by calling lockTable()), or SQLITE_LOCKED if not.
     * 
     * @param iTab
     * @param eLock
     * 
     * @throws SqlJetException
     */
    private boolean queryTableLock(int iTab, SqlJetBtreeLockMode eLock) {

        assert (holdsMutex());
        assert (eLock != null);
        assert (db != null);

        /* This is a no-op if the shared-cache is not enabled */
        if (!sharable) {
            return true;
        }

        /*
         * If some other connection is holding an exclusive lock, the* requested
         * lock may not be obtained.
         */
        if (pBt.pExclusive != null && pBt.pExclusive != this) {
            return false;
        }

        /*
         * This (along with lockTable()) is where the ReadUncommitted flag is*
         * dealt with. If the caller is querying for a read-lock and the flag is
         * * set, it is unconditionally granted - even if there are write-locks*
         * on the table. If a write-lock is requested, the ReadUncommitted flag*
         * is not considered.** In function lockTable(), if a read-lock is
         * demanded and the* ReadUncommitted flag is set, no entry is added to
         * the locks list* (BtShared.pLock).** To summarize: If the
         * ReadUncommitted flag is set, then read cursors do* not create or
         * respect table locks. The locking procedure for a* write-cursor does
         * not change.
         */
        if (!db.getFlags().contains(SqlJetDbFlags.ReadUncommitted) || eLock == SqlJetBtreeLockMode.WRITE
                || iTab == ISqlJetDbHandle.MASTER_ROOT) {
            for (SqlJetBtreeLock pIter : pBt.pLock) {
                if (pIter.pBtree != this && pIter.iTable == iTab
                        && (pIter.eLock != eLock || eLock != SqlJetBtreeLockMode.READ)) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#lockTable(int, boolean)
     */
    public void lockTable(int table, boolean isWriteLock) {
        if (sharable) {
            enter();
            try {
                final SqlJetBtreeLockMode lockType = isWriteLock ? SqlJetBtreeLockMode.WRITE : SqlJetBtreeLockMode.READ;
                if (queryTableLock(table, lockType)) {
                    lockTable(table, lockType);
                }
            } finally {
                leave();
            }
        }
    }

    /**
     * Add a lock on the table with root-page iTable to the shared-btree used by
     * Btree handle p. Parameter eLock must be either READ_LOCK or WRITE_LOCK.
     * 
     * SQLITE_OK is returned if the lock is added successfully. SQLITE_BUSY and
     * SQLITE_NOMEM may also be returned.
     * 
     * @param table
     * @param lockType
     */
    private void lockTable(int iTable, SqlJetBtreeLockMode eLock) {

        assert (holdsMutex());
        assert (eLock != null);
        assert (db != null);

        /* This is a no-op if the shared-cache is not enabled */
        if (!sharable) {
            return;
        }

        assert (queryTableLock(iTable, eLock));

        /*
         * If the read-uncommitted flag is set and a read-lock is requested,
         * return early without adding an entry to the BtShared.pLock list. See
         * comment in function queryTableLock() for more info on handling the
         * ReadUncommitted flag.
         */
        if (db.getFlags().contains(SqlJetDbFlags.ReadUncommitted) && (eLock == SqlJetBtreeLockMode.READ)
                && iTable != ISqlJetDbHandle.MASTER_ROOT) {
            return;
        }

        SqlJetBtreeLock pLock = null;

        /* First search the list for an existing lock on this table. */
        for (SqlJetBtreeLock pIter : pBt.pLock) {
            if (pIter.iTable == iTable && pIter.pBtree == this) {
                pLock = pIter;
                break;
            }
        }

        /*
         * If the above search did not find a BtLock struct associating Btree p
         * with table iTable, allocate one and link it into the list.
         */
        if (null == pLock) {
            pLock = new SqlJetBtreeLock();
            pLock.iTable = iTable;
            pLock.pBtree = this;
            pBt.pLock.add(pLock);
            pLock.eLock = eLock;
        }

        /*
         * Set the BtLock.eLock variable to the maximum of the current lock and
         * the requested lock. This means if a write-lock was already held and a
         * read-lock requested, we don't incorrectly downgrade the lock.
         */
        if (eLock == SqlJetBtreeLockMode.WRITE && pLock.eLock == SqlJetBtreeLockMode.READ) {
            pLock.eLock = eLock;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetBtree#savepoint(org.tmatesoft.sqljet
     * .core.SqlJetSavepointOperation, int)
     */
    public void savepoint(SqlJetSavepointOperation op, int savepoint) throws SqlJetException {
        if (this.inTrans == TransMode.WRITE) {
            assert (pBt.inStmt == false);
            assert (op == SqlJetSavepointOperation.RELEASE || op == SqlJetSavepointOperation.ROLLBACK);
            assert (savepoint >= 0 || (savepoint == -1 && op == SqlJetSavepointOperation.ROLLBACK));
            enter();
            try {
                pBt.db = this.db;
                pBt.pPager.savepoint(op, savepoint);
                newDatabase();
            } finally {
                leave();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#getFilename()
     */
    public File getFilename() {
        assert (pBt.pPager != null);
        return pBt.pPager.getFileName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#getDirname()
     */
    public File getDirname() {
        assert (pBt.pPager != null);
        return pBt.pPager.getDirectoryName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#getJournalname()
     */
    public File getJournalname() {
        assert (pBt.pPager != null);
        return pBt.pPager.getJournalName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetBtree#copyFile(org.tmatesoft.sqljet.
     * core.ISqlJetBtree)
     */
    public void copyFile(ISqlJetBtree from) throws SqlJetException {
        enter();
        try {
            final SqlJetBtree pFrom = (SqlJetBtree) from;
            pFrom.enter();
            try {
                doCopyFile(pFrom);
            } finally {
                leave();
            }
        } finally {
            leave();
        }
    }

    /**
     * Copy the complete content of pBtFrom into pBtTo. A transaction must be
     * active for both files.
     * 
     * The size of file pTo may be reduced by this operation. If anything goes
     * wrong, the transaction on pTo is rolled back.
     * 
     * If successful, CommitPhaseOne() may be called on pTo before returning.
     * The caller should finish committing the transaction on pTo by calling
     * sqlite3BtreeCommit().
     * 
     * @param from
     * @throws SqlJetException
     */
    private void doCopyFile(SqlJetBtree pFrom) throws SqlJetException {

        SqlJetBtree pTo = this;

        int i;

        int nFromPage; /* Number of pages in pFrom */
        int nToPage; /* Number of pages in pTo */
        int nNewPage; /* Number of pages in pTo after the copy */

        int iSkip; /* Pending byte page in pTo */
        int nToPageSize; /* Page size of pTo in bytes */
        int nFromPageSize; /* Page size of pFrom in bytes */

        SqlJetBtreeShared pBtTo = pTo.pBt;
        SqlJetBtreeShared pBtFrom = pFrom.pBt;
        pBtTo.db = pTo.db;
        pBtFrom.db = pFrom.db;

        nToPageSize = pBtTo.pageSize;
        nFromPageSize = pBtFrom.pageSize;

        assert (pTo.inTrans == TransMode.WRITE);
        assert (pFrom.inTrans == TransMode.WRITE);
        if (pBtTo.pCursor != null) {
            throw new SqlJetException(SqlJetErrorCode.BUSY);
        }

        nToPage = pBtTo.pPager.getPageCount();
        nFromPage = pBtFrom.pPager.getPageCount();
        iSkip = pBtTo.PENDING_BYTE_PAGE();

        /*
         * Variable nNewPage is the number of pages required to store the*
         * contents of pFrom using the current page-size of pTo.
         */
        nNewPage = (int) (((long) nFromPage * (long) nFromPageSize + (long) nToPageSize - 1) / (long) nToPageSize);

        for (i = 1; (i <= nToPage || i <= nNewPage); i++) {

            /*
             * Journal the original page.** iSkip is the page number of the
             * locking page (PENDING_BYTE_PAGE)* in database *pTo (before the
             * copy). This page is never written* into the journal file. Unless
             * i==iSkip or the page was not* present in pTo before the copy
             * operation, journal page i from pTo.
             */
            if (i != iSkip && i <= nToPage) {
                ISqlJetPage pDbPage = null;
                pDbPage = pBtTo.pPager.getPage(i);
                try {
                    pDbPage.write();
                    if (i > nFromPage) {
                        /*
                         * Yeah. It seems wierd to call DontWrite() right after
                         * Write(). But* that is because the names of those
                         * procedures do not exactly* represent what they do.
                         * Write() really means "put this page in the* rollback
                         * journal and mark it as dirty so that it will be
                         * written* to the database file later." DontWrite()
                         * undoes the second part of* that and prevents the page
                         * from being written to the database. The* page is
                         * still on the rollback journal, though. And that is
                         * the* whole point of this block: to put pages on the
                         * rollback journal.
                         */
                        pDbPage.dontWrite();
                    }
                } finally {
                    pDbPage.unref();
                }
            }

            /* Overwrite the data in page i of the target database */
            if (i != iSkip && i <= nNewPage) {

                ISqlJetPage pToPage = null;
                long iOff;

                pToPage = pBtTo.pPager.getPage(i);
                pToPage.write();

                for (iOff = (i - 1) * nToPageSize; iOff < i * nToPageSize; iOff += nFromPageSize) {
                    ISqlJetPage pFromPage = null;
                    int iFrom = (int) (iOff / nFromPageSize) + 1;

                    if (iFrom == pBtFrom.PENDING_BYTE_PAGE()) {
                        continue;
                    }

                    pFromPage = pBtFrom.pPager.getPage(iFrom);

                    ISqlJetMemoryPointer zTo = pToPage.getData();
                    ISqlJetMemoryPointer zFrom = pFromPage.getData();
                    int nCopy;

                    int nFrom = 0;
                    int nTo = 0;

                    if (nFromPageSize >= nToPageSize) {
                        nFrom += ((i - 1) * nToPageSize - ((iFrom - 1) * nFromPageSize));
                        nCopy = nToPageSize;
                    } else {
                        nTo += (((iFrom - 1) * nFromPageSize) - (i - 1) * nToPageSize);
                        nCopy = nFromPageSize;
                    }
                    SqlJetUtility.memcpy(zTo, nTo, zFrom, nFrom, nCopy);

                    pFromPage.unref();
                }

                if (pToPage != null) {
                    SqlJetMemPage p = (SqlJetMemPage) pToPage.getExtra();
                    p.isInit = false;
                    pToPage.unref();
                }
            }
        }

        /*
         * If things have worked so far, the database file may need to be*
         * truncated. The complex part is that it may need to be truncated to* a
         * size that is not an integer multiple of nToPageSize - the current*
         * page size used by the pager associated with B-Tree pTo.** For
         * example, say the page-size of pTo is 2048 bytes and the original*
         * number of pages is 5 (10 KB file). If pFrom has a page size of 1024*
         * bytes and 9 pages, then the file needs to be truncated to 9KB.
         */

        ISqlJetFile pFile = pBtTo.pPager.getFile();
        long iSize = (long) nFromPageSize * (long) nFromPage;
        long iNow = (long) ((nToPage > nNewPage) ? nToPage : nNewPage) * (long) nToPageSize;
        long iPending = ((long) pBtTo.PENDING_BYTE_PAGE() - 1) * (long) nToPageSize;

        assert (iSize <= iNow);

        /*
         * Commit phase one syncs the journal file associated with pTo*
         * containing the original data. It does not sync the database file*
         * itself. After doing this it is safe to use OsTruncate() and other*
         * file APIs on the database file directly.
         */
        pBtTo.db = pTo.db;
        pBtTo.pPager.commitPhaseOne(null, true);
        if (iSize < iNow) {
            pFile.truncate(iSize);
        }

        /*
         * The loop that copied data from database pFrom to pTo did not*
         * populate the locking page of database pTo. If the page-size of* pFrom
         * is smaller than that of pTo, this means some data will* not have been
         * copied.** This block copies the missing data from database pFrom to
         * pTo* using file APIs. This is safe because at this point we know that
         * * all of the original data from pTo has been synced into the* journal
         * file. At this point it would be safe to do anything at* all to the
         * database file except truncate it to zero bytes.
         */
        if (nFromPageSize < nToPageSize && iSize > iPending) {
            long iOff;
            for (iOff = iPending; iOff < (iPending + nToPageSize); iOff += nFromPageSize) {
                ISqlJetPage pFromPage = null;
                int iFrom = (int) (iOff / nFromPageSize) + 1;

                if (iFrom == pBtFrom.PENDING_BYTE_PAGE() || iFrom > nFromPage) {
                    continue;
                }

                pFromPage = pBtFrom.pPager.getPage(iFrom);
                ISqlJetMemoryPointer zFrom = pFromPage.getData();
                pFile.write(zFrom, nFromPageSize, iOff);
                pFromPage.unref();
            }
        }

        /* Sync the database file */
        try {
            pBtTo.pPager.sync();
            pBtTo.pageSizeFixed = false;
        } catch (SqlJetException e) {
            pTo.rollback();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#incrVacuum()
     */
    public void incrVacuum() throws SqlJetException {
        enter();
        try {
            pBt.db = this.db;
            assert (pBt.inTransaction == TransMode.WRITE && this.inTrans == TransMode.WRITE);
            if (!pBt.autoVacuum) {
                throw new SqlJetException(SqlJetErrorCode.DONE);
            } else {
                pBt.invalidateAllOverflowCache();
                pBt.incrVacuumStep(0, pBt.pPager.imageSize());
            }
        } finally {
            leave();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#dropTable(int)
     */
    public int dropTable(int table) throws SqlJetException {
        enter();
        try {
            pBt.db = this.db;
            return doDropTable(table);
        } finally {
            leave();
        }
    }

    /**
     * Erase all information in a table and add the root of the table to the
     * freelist. Except, the root of the principle table (the one on page 1) is
     * never added to the freelist.
     * 
     * This routine will fail with SQLITE_LOCKED if there are any open cursors
     * on the table.
     * 
     * If AUTOVACUUM is enabled and the page at iTable is not the last root page
     * in the database file, then the last root page in the database file is
     * moved into the slot formerly occupied by iTable and that last slot
     * formerly occupied by the last root page is added to the freelist instead
     * of iTable. In this say, all root pages are kept at the beginning of the
     * database file, which is necessary for AUTOVACUUM to work right. *piMoved
     * is set to the page number that used to be the last root page in the file
     * before the move. If no page gets moved, *piMoved is set to 0. The last
     * root page is recorded in meta[3] and the value of meta[3] is updated by
     * this procedure.
     */
    private int doDropTable(int iTable) throws SqlJetException {

        SqlJetMemPage pPage = null;
        int piMoved;

        assert (holdsMutex());
        assert (this.inTrans == TransMode.WRITE);

        /*
         * It is illegal to drop a table if any cursors are open on the*
         * database. This is because in auto-vacuum mode the backend may* need
         * to move another root-page to fill a gap left by the deleted* root
         * page. If an open cursor was using this page a problem would* occur.
         */
        if (pBt.pCursor != null) {
            throw new SqlJetException(SqlJetErrorCode.LOCKED);
        }

        pPage = pBt.getPage(iTable, false);
        try {
            clearTable(iTable, null);
        } catch (SqlJetException e) {
            SqlJetMemPage.releasePage(pPage);
            throw e;
        }

        piMoved = 0;

        if (iTable > 1) {
            if (pBt.autoVacuum) {
                int maxRootPgno;
                try {
                    maxRootPgno = getMeta(4);
                } catch (SqlJetException e) {
                    SqlJetMemPage.releasePage(pPage);
                    throw e;
                }

                if (iTable == maxRootPgno) {
                    /*
                     * If the table being dropped is the table with the largest
                     * root-page* number in the database, put the root page on
                     * the free list.
                     */
                    try {
                        pPage.freePage();
                    } finally {
                        SqlJetMemPage.releasePage(pPage);
                    }
                } else {
                    /*
                     * The table being dropped does not have the largest
                     * root-page* number in the database. So move the page that
                     * does into the* gap left by the deleted root-page.
                     */
                    SqlJetMemPage pMove;
                    SqlJetMemPage.releasePage(pPage);
                    pMove = pBt.getPage(maxRootPgno, false);
                    try {
                        pBt.relocatePage(pMove, SqlJetBtreeShared.PTRMAP_ROOTPAGE, 0, iTable, false);
                    } finally {
                        SqlJetMemPage.releasePage(pMove);
                    }
                    pMove = pBt.getPage(maxRootPgno, false);
                    try {
                        pMove.freePage();
                    } finally {
                        SqlJetMemPage.releasePage(pMove);
                    }
                    piMoved = maxRootPgno;
                }

                /*
                 * Set the new 'max-root-page' value in the database header.
                 * This* is the old value less one, less one more if that
                 * happens to* be a root-page number, less one again if that is
                 * the* PENDING_BYTE_PAGE.
                 */
                maxRootPgno--;
                if (maxRootPgno == pBt.PENDING_BYTE_PAGE()) {
                    maxRootPgno--;
                }
                if (maxRootPgno == pBt.PTRMAP_PAGENO(maxRootPgno)) {
                    maxRootPgno--;
                }
                assert (maxRootPgno != pBt.PENDING_BYTE_PAGE());

                updateMeta(4, maxRootPgno);
            } else {
                try {
                    pPage.freePage();
                } finally {
                    SqlJetMemPage.releasePage(pPage);
                }
            }
        } else {
            /* If sqlite3BtreeDropTable was called on page 1. */
            try {
                pPage.zeroPage(SqlJetMemPage.PTF_INTKEY | SqlJetMemPage.PTF_LEAF);
            } finally {
                SqlJetMemPage.releasePage(pPage);
            }
        }

        return piMoved;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#clearTable(int, int[])
     */
    public void clearTable(int table, int[] change) throws SqlJetException {
        enter();
        try {
            pBt.db = db;
            assert (inTrans == TransMode.WRITE);
            if (checkReadLocks(table, null, 1)) {
                /* nothing to do */
            } else if (!pBt.saveAllCursors(table, null)) {
                /* nothing to do */
            } else {
                pBt.clearDatabasePage(table, false, change);
            }
        } finally {
            leave();
        }
    }

    /**
     * This routine checks all cursors that point to table pgnoRoot. If any of
     * those cursors were opened with wrFlag==0 in a different database
     * connection (a database connection that shares the pager cache with the
     * current connection) and that other connection is not in the
     * ReadUncommmitted state, then this routine returns SQLITE_LOCKED.
     * 
     * As well as cursors with wrFlag==0, cursors with wrFlag==1 and
     * isIncrblobHandle==1 are also considered 'read' cursors. Incremental blob
     * cursors are used for both reading and writing.
     * 
     * When pgnoRoot is the root page of an intkey table, this function is also
     * responsible for invalidating incremental blob cursors when the table row
     * on which they are opened is deleted or modified. Cursors are invalidated
     * according to the following rules:
     * 
     * <ol>
     * 
     * <li>When BtreeClearTable() is called to completely delete the contents of
     * a B-Tree table, pExclude is set to zero and parameter iRow is set to
     * non-zero. In this case all incremental blob cursors open on the table
     * rooted at pgnoRoot are invalidated.</li>
     * 
     * <li>When BtreeInsert(), BtreeDelete() or BtreePutData() is called to
     * modify a table row via an SQL statement, pExclude is set to the write
     * cursor used to do the modification and parameter iRow is set to the
     * integer row id of the B-Tree entry being modified. Unless pExclude is
     * itself an incremental blob cursor, then all incremental blob cursors open
     * on row iRow of the B-Tree are invalidated.</li>
     * 
     * <li>If both pExclude and iRow are set to zero, no incremental blob
     * cursors are invalidated.</li>
     * 
     * </ol>
     */
    boolean checkReadLocks(int pgnoRoot, SqlJetBtreeCursor pExclude, long iRow) {
        SqlJetBtreeCursor p;
        assert (holdsMutex());
        for (p = pBt.pCursor; p != null; p = p.pNext) {
            if (p == pExclude)
                continue;
            if (p.pgnoRoot != pgnoRoot)
                continue;
            if (p.isIncrblobHandle
                    && ((pExclude == null && iRow != 0) || (pExclude != null && !pExclude.isIncrblobHandle && p.info.nKey == iRow))) {
                p.eState = CursorState.INVALID;
            }
            if (p.eState != CursorState.VALID)
                continue;
            if (!p.wrFlag || p.isIncrblobHandle) {
                ISqlJetDbHandle dbOther = p.pBtree.db;
                if (dbOther == null || (dbOther != db && !dbOther.getFlags().contains(SqlJetDbFlags.ReadUncommitted))) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#getMeta(int)
     */
    public int getMeta(int idx) throws SqlJetException {

        enter();

        try {

            ISqlJetPage pDbPage = null;
            ISqlJetMemoryPointer pP1;

            pBt.db = this.db;

            /*
             * Reading a meta-data value requires a read-lock on page 1 (and
             * hence the sqlite_master table. We grab this lock regardless of
             * whether or not the SQLITE_ReadUncommitted flag is set (the table
             * rooted at page 1 is treated as a special case by queryTableLock()
             * and lockTable()).
             */
            queryTableLock(1, SqlJetBtreeLockMode.READ);

            assert (idx >= 0 && idx <= 15);
            if (pBt.pPage1 != null) {
                /*
                 * The b-tree is already holding a reference to page 1 of the
                 * database file. In this case the required meta-data value can
                 * be read directly from the page data of this reference. This
                 * is slightly faster than* requesting a new reference from the
                 * pager layer.
                 */
                pP1 = pBt.pPage1.aData;
            } else {
                /*
                 * The b-tree does not have a reference to page 1 of the
                 * database file. Obtain one from the pager layer.
                 */
                pDbPage = pBt.pPager.acquirePage(1, true);
                pP1 = pDbPage.getData();
            }

            int pMeta = SqlJetUtility.get4byte(pP1, 36 + idx * 4);

            /*
             * If the b-tree is not holding a reference to page 1, then one was
             * requested from the pager layer in the above block. Release it
             * now.
             */
            if (pBt.pPage1 == null) {
                pDbPage.unref();
            }

            /* Grab the read-lock on page 1. */
            lockTable(1, SqlJetBtreeLockMode.READ);

            return pMeta;

        } finally {
            leave();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#updateMeta(int, int[])
     */
    public void updateMeta(int idx, int value) throws SqlJetException {
        assert (idx >= 1 && idx <= 15);
        enter();
        try {
            pBt.db = this.db;
            assert (this.inTrans == TransMode.WRITE);
            assert (pBt.pPage1 != null);
            ISqlJetMemoryPointer pP1 = pBt.pPage1.aData;
            pBt.pPage1.pDbPage.write();
            SqlJetUtility.put4byte(pP1, 36 + idx * 4, value);
            if (idx == 7) {
                assert (pBt.autoVacuum || value == 0);
                assert (value == 0 || value == 1);
                pBt.incrVacuum = value != 0;
            }
        } finally {
            leave();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetBtree#tripAllCursors(org.tmatesoft.sqljet
     * .core.SqlJetErrorCode)
     */
    public void tripAllCursors(SqlJetErrorCode errCode) throws SqlJetException {
        SqlJetBtreeCursor p;
        enter();
        try {
            for (p = pBt.pCursor; p != null; p = p.pNext) {
                int i;
                p.clearCursor();
                p.eState = CursorState.FAULT;
                p.error = errCode;
                p.skip = errCode != null ? 1 : 0;
                for (i = 0; i <= p.iPage; i++) {
                    SqlJetMemPage.releasePage(p.apPage[i]);
                    p.apPage[i] = null;
                }
            }
        } finally {
            leave();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#getPager()
     */
    public ISqlJetPager getPager() {
        return pBt.pPager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#getCursor(int, boolean,
     * org.tmatesoft.sqljet.core.ISqlJetKeyInfo)
     */
    public ISqlJetBtreeCursor getCursor(int table, boolean wrFlag, ISqlJetKeyInfo keyInfo) throws SqlJetException {
        enter();
        try {
            pBt.db = db;
            return new SqlJetBtreeCursor(this, table, wrFlag, keyInfo);
        } finally {
            leave();
        }
    }

    /**
     * This routine works like lockBtree() except that it also invokes the busy
     * callback if there is lock contention.
     * 
     * @throws SqlJetException
     */
    void lockWithRetry() throws SqlJetException {
        assert (holdsMutex());
        if (inTrans == TransMode.NONE) {
            TransMode inTransaction = pBt.inTransaction;
            integrity();
            try {
                beginTrans(SqlJetTransactionMode.READ_ONLY);
            } finally {
                pBt.inTransaction = inTransaction;
                inTrans = TransMode.NONE;
            }
            pBt.nTransaction--;
            integrity();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.ISqlJetBtree#closeAllCursors()
     */
    public void closeAllCursors() throws SqlJetException {
        SqlJetBtreeCursor p;
        enter();
        try {
            for (p = pBt.pCursor; p != null; p = p.pNext) {
                p.closeCursor();
            }
        } finally {
            leave();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtree#integrityCheck(int[], int,
     * int, int[])
     */
    public String integrityCheck(int[] root, int root2, int mxErr, int[] err) {
        // TODO Auto-generated method stub
        return null;
    }

}
