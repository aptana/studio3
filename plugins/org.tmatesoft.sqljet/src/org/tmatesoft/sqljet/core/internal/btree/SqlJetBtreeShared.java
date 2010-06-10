/**
 * SqlJetBtShared.java
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

import static org.tmatesoft.sqljet.core.internal.btree.SqlJetBtree.TRACE;

import java.util.LinkedList;
import java.util.List;

import org.tmatesoft.sqljet.core.ISqlJetMutex;
import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.internal.ISqlJetDbHandle;
import org.tmatesoft.sqljet.core.internal.ISqlJetFile;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.ISqlJetPage;
import org.tmatesoft.sqljet.core.internal.ISqlJetPager;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;
import org.tmatesoft.sqljet.core.internal.btree.SqlJetBtree.TransMode;
import org.tmatesoft.sqljet.core.internal.mutex.SqlJetEmptyMutex;

/**
 * An instance of this object represents a single database file.
 * 
 * A single database file can be in use as the same time by two or more database
 * connections. When two or more connections are sharing the same database file,
 * each connection has it own private Btree object for the file and each of
 * those Btrees points to this one BtShared object. BtShared.nRef is the number
 * of connections currently sharing this database file.
 * 
 * Fields in this structure are accessed under the BtShared.mutex mutex, except
 * for nRef and pNext which are accessed under the global
 * SQLITE_MUTEX_STATIC_MASTER mutex. The pPager field may not be modified once
 * it is initially set as long as nRef>0. The pSchema field may be set once
 * under BtShared.mutex and thereafter is unchanged as long as nRef>0.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetBtreeShared {

    public final static byte PTRMAP_ROOTPAGE = 1;
    public final static byte PTRMAP_FREEPAGE = 2;
    public final static byte PTRMAP_OVERFLOW1 = 3;
    public final static byte PTRMAP_OVERFLOW2 = 4;
    public final static byte PTRMAP_BTREE = 5;

    /** The page cache */
    ISqlJetPager pPager;

    /** Database connection currently using this Btree */
    ISqlJetDbHandle db;

    /** A list of all open cursors */
    SqlJetBtreeCursor pCursor;

    /** First page of the database */
    SqlJetMemPage pPage1;

    /** True if we are in a statement subtransaction */
    boolean inStmt;

    /** True if the underlying file is readonly */
    boolean readOnly;

    /** True if the page size can no longer be changed */
    boolean pageSizeFixed;

    /** True if auto-vacuum is enabled */
    boolean autoVacuum;

    /** True if incr-vacuum is enabled */
    boolean incrVacuum;

    /** Total number of bytes on a page */
    int pageSize;

    /** Number of usable bytes on each page */
    int usableSize;

    /** Maximum local payload in non-LEAFDATA tables */
    int maxLocal;

    /** Minimum local payload in non-LEAFDATA tables */
    int minLocal;

    /** Maximum local payload in a LEAFDATA table */
    int maxLeaf;

    /** Minimum local payload in a LEAFDATA table */
    int minLeaf;

    /** Transaction state */
    TransMode inTransaction = TransMode.NONE;

    /** Number of open transactions (read + write) */
    int nTransaction;

    /** Pointer to space allocated by sqlite3BtreeSchema() */
    Object pSchema;

    /** Non-recursive mutex required to access this struct */
    ISqlJetMutex mutex = new SqlJetEmptyMutex();

    /** Number of references to this structure */
    int nRef;

    /** Next on a list of sharable BtShared structs */
    SqlJetBtreeShared pNext;

    /** List of locks held on this shared-btree struct */
    List<SqlJetBtreeLock> pLock = new LinkedList<SqlJetBtreeLock>();

    /** Btree with an EXCLUSIVE lock on the whole db */
    SqlJetBtree pExclusive;

    /** BtShared.pageSize bytes of space for tmp use */
    ISqlJetMemoryPointer pTmpSpace;

    /**
     * The database page the PENDING_BYTE occupies. This page is never used.
     * TODO: This macro is very similary to PAGER_MJ_PGNO() in pager.c. They
     * should possibly be consolidated (presumably in pager.h).
     * 
     * If disk I/O is omitted (meaning that the database is stored purely in
     * memory) then there is no pending byte.
     */
    public int PENDING_BYTE_PAGE() {
        return (int) (ISqlJetFile.PENDING_BYTE / pageSize) + 1;
    }

    /**
     * The following value is the maximum cell size assuming a maximum page size
     * give above.
     */
    public int MX_CELL_SIZE() {
        return (pageSize - 8);
    }

    /**
     * The maximum number of cells on a single page of the database. This
     * assumes a minimum cell size of 6 bytes (4 bytes for the cell itself plus
     * 2 bytes for the index to the cell in the page header). Such small cells
     * will be rare, but they are possible.
     */
    public int MX_CELL() {
        return ((pageSize - 8) / 6);
    }

    /**
     * These macros define the location of the pointer-map entry for a database
     * page. The first argument to each is the number of usable bytes on each
     * page of the database (often 1024). The second is the page number to look
     * up in the pointer map.
     * 
     * PTRMAP_PAGENO returns the database page number of the pointer-map page
     * that stores the required pointer. PTRMAP_PTROFFSET returns the offset of
     * the requested map entry.
     * 
     * If the pgno argument passed to PTRMAP_PAGENO is a pointer-map page, then
     * pgno is returned. So (pgno==PTRMAP_PAGENO(pgsz, pgno)) can be used to
     * test if pgno is a pointer-map page. PTRMAP_ISPAGE implements this test.
     * 
     */
    public int PTRMAP_PAGENO(int pgno) {
        return ptrmapPageno(pgno);
    }

    boolean PTRMAP_ISPAGE(int pgno) {
        return PTRMAP_PAGENO(pgno) == pgno;
    }

    private int PTRMAP_PTROFFSET(int pgptrmap, int pgno) {
        return (5 * (pgno - pgptrmap - 1));
    }

    /**
     * Invalidate the overflow page-list cache for all cursors opened on the
     * shared btree structure pBt.
     */
    public void invalidateAllOverflowCache() {
        assert (mutex.held());
        for (SqlJetBtreeCursor p = pCursor; p != null; p = p.pNext) {
            p.aOverflow = null;
        }
    }

    /**
     * Return the size of the database file in pages. If there is any kind of
     * error, return ((unsigned int)-1).
     * 
     * @throws SqlJetException
     */
    int getPageCount() throws SqlJetException {
        assert (pPage1 != null);
        return pPager.getPageCount();
    }

    /**
     * Given a page number of a regular database page, return the page number
     * for the pointer-map page that contains the entry for the input page
     * number.
     */
    private int ptrmapPageno(int pgno) {
        int nPagesPerMapPage;
        int iPtrMap, ret;
        assert (mutex.held());
        nPagesPerMapPage = (usableSize / 5) + 1;
        iPtrMap = (pgno - 2) / nPagesPerMapPage;
        ret = (iPtrMap * nPagesPerMapPage) + 2;
        if (ret == PENDING_BYTE_PAGE()) {
            ret++;
        }
        return ret;
    }

    /**
     * Write an entry into the pointer map.
     * 
     * This routine updates the pointer map entry for page number 'key' so that
     * it maps to type 'eType' and parent page number 'pgno'. An error code is
     * returned if something goes wrong, otherwise SQLITE_OK.
     */
    public void ptrmapPut(int key, short eType, int parent) throws SqlJetException {
        ISqlJetPage pDbPage; /* The pointer map page */
        ISqlJetMemoryPointer pPtrmap; /* The pointer map data */
        int iPtrmap; /* The pointer map page number */
        int offset; /* Offset in pointer map page */

        assert (mutex.held());
        /*
         * The master-journal page number must never be used as a pointer map
         * page
         */
        assert (!PTRMAP_ISPAGE(PENDING_BYTE_PAGE()));

        assert (autoVacuum);
        if (key == 0) {
            throw new SqlJetException(SqlJetErrorCode.CORRUPT);
        }
        iPtrmap = PTRMAP_PAGENO(key);
        pDbPage = pPager.getPage(iPtrmap);
        offset = PTRMAP_PTROFFSET(iPtrmap, key);
        pPtrmap = pDbPage.getData();

        if (eType != SqlJetUtility.getUnsignedByte(pPtrmap, offset)
                || SqlJetUtility.get4byte(pPtrmap, offset + 1) != parent) {
            TRACE("PTRMAP_UPDATE: %d->(%d,%d)\n", key, eType, parent);
            pDbPage.write();
            SqlJetUtility.putUnsignedByte(pPtrmap, offset, eType);
            SqlJetUtility.put4byte(pPtrmap, offset + 1, parent);
        }
        pDbPage.unref();
    }

    /**
     * Read an entry from the pointer map.
     * 
     * This routine retrieves the pointer map entry for page 'key', writing the
     * type and parent page number to *pEType and *pPgno respectively. An error
     * code is returned if something goes wrong, otherwise SQLITE_OK.
     */
    public void ptrmapGet(int key, short[] pEType, int[] pPgno) throws SqlJetException {
        ISqlJetPage pDbPage; /* The pointer map page */
        int iPtrmap; /* Pointer map page index */
        ISqlJetMemoryPointer pPtrmap; /* Pointer map page data */
        int offset; /* Offset of entry in pointer map */

        assert (mutex.held());

        iPtrmap = PTRMAP_PAGENO(key);
        pDbPage = pPager.acquirePage(iPtrmap, true);
        pPtrmap = pDbPage.getData();

        offset = PTRMAP_PTROFFSET(iPtrmap, key);
        assert (pEType != null && pEType.length > 0);
        pEType[0] = (short) SqlJetUtility.getUnsignedByte(pPtrmap, offset);
        if (pPgno != null && pPgno.length > 0)
            pPgno[0] = SqlJetUtility.get4byte(pPtrmap, offset + 1);

        pDbPage.unref();

        if (pEType[0] < 1 || pEType[0] > 5)
            throw new SqlJetException(SqlJetErrorCode.CORRUPT);
    }

    /**
     * Convert a DbPage obtained from the pager into a MemPage used by the btree
     * layer.
     */
    private SqlJetMemPage pageFromDbPage(ISqlJetPage pDbPage, int pgno) {
        if (null == pDbPage.getExtra())
            pDbPage.setExtra(new SqlJetMemPage());
        SqlJetMemPage pPage = (SqlJetMemPage) pDbPage.getExtra();
        pPage.aData = pDbPage.getData();
        pPage.pDbPage = pDbPage;
        pPage.pBt = this;
        pPage.pgno = pgno;
        pPage.hdrOffset = (byte) (pPage.pgno == 1 ? 100 : 0);
        return pPage;
    }

    /**
     * Get a page from the pager. Initialize the MemPage.pBt and MemPage.aData
     * elements if needed.
     * 
     * If the noContent flag is set, it means that we do not care about the
     * content of the page at this time. So do not go to the disk to fetch the
     * content. Just fill in the content with zeros for now. If in the future we
     * call sqlite3PagerWrite() on this page, that means we have started to be
     * concerned about content and the disk read should occur at that point.
     * 
     * @param pgno
     *            Number of the page to fetch
     * @param noContent
     *            Do not load page content if true
     * @return
     * @throws SqlJetException
     */
    public SqlJetMemPage getPage(int pgno, boolean noContent) throws SqlJetException {
        ISqlJetPage pDbPage;
        assert (mutex.held());
        pDbPage = pPager.acquirePage(pgno, !noContent);
        return pageFromDbPage(pDbPage, pgno);
    }

    /**
     * Allocate a new page from the database file.
     * 
     * The new page is marked as dirty. (In other words, sqlite3PagerWrite() has
     * already been called on the new page.) The new page has also been
     * referenced and the calling routine is responsible for calling
     * sqlite3PagerUnref() on the new page when it is done.
     * 
     * SQLITE_OK is returned on success. Any other return value indicates an
     * error. *ppPage and *pPgno are undefined in the event of an error. Do not
     * invoke sqlite3PagerUnref() on *ppPage if an error is returned.
     * 
     * If the "nearby" parameter is not 0, then a (feeble) effort is made to
     * locate a page close to the page number "nearby". This can be used in an
     * attempt to keep related pages close to each other in the database file,
     * which in turn can make database access faster.
     * 
     * If the "exact" parameter is not 0, and the page-number nearby exists
     * anywhere on the free-list, then it is guarenteed to be returned. This is
     * only used by auto-vacuum databases when allocating a new table.
     * 
     * @throws SqlJetException
     */
    public SqlJetMemPage allocatePage(int[] pPgno, int nearby, boolean exact) throws SqlJetException {
        SqlJetMemPage ppPage = null;
        long n; /* Number of pages on the freelist */
        int k; /* Number of leaves on the trunk of the freelist */
        SqlJetMemPage pTrunk = null;
        SqlJetMemPage pPrevTrunk = null;

        assert (mutex.held());
        n = SqlJetUtility.get4byteUnsigned(pPage1.aData, 36);
        try {
            if (n > 0) {
                /* There are pages on the freelist. Reuse one of those pages. */
                int iTrunk;
                /* If the free-list must be searched for 'nearby' */
                boolean searchList = false;

                /*
                 * If the 'exact' parameter was true and a query of the
                 * pointer-map shows that the page 'nearby' is somewhere on the
                 * free-list, then the entire-list will be searched for that
                 * page.
                 */
                if (exact && nearby <= getPageCount()) {
                    short[] eType = { 0 };
                    assert (nearby > 0);
                    assert (autoVacuum);
                    ptrmapGet(nearby, eType, null);
                    if (eType[0] == PTRMAP_FREEPAGE) {
                        searchList = true;
                    }
                    pPgno[0] = nearby;
                }

                /*
                 * Decrement the free-list count by 1. Set iTrunk to the index
                 * of the first free-list trunk page. iPrevTrunk is initially 1.
                 */
                pPage1.pDbPage.write();
                SqlJetUtility.put4byteUnsigned(pPage1.aData, 36, n - 1);

                /*
                 * The code within this loop is run only once if the
                 * 'searchList' variable is not true. Otherwise, it runs once
                 * for each trunk-page on the free-list until the page 'nearby'
                 * is located.
                 */

                do {
                    pPrevTrunk = pTrunk;
                    if (pPrevTrunk != null) {
                        iTrunk = SqlJetUtility.get4byte(pPrevTrunk.aData, 0);
                    } else {
                        iTrunk = SqlJetUtility.get4byte(pPage1.aData, 32);
                    }

                    try {
                        pTrunk = getPage(iTrunk, false);
                    } catch (SqlJetException e) {
                        pTrunk = null;
                        throw e;
                    }

                    k = SqlJetUtility.get4byte(pTrunk.aData, 4);
                    if (k == 0 && !searchList) {
                        /*
                         * The trunk has no leaves and the list is not being
                         * searched. So extract the trunk page itself and use it
                         * as the newly allocated page
                         */
                        assert (pPrevTrunk == null);
                        pTrunk.pDbPage.write();
                        pPgno[0] = iTrunk;
                        SqlJetUtility.memcpy(pPage1.aData, 32, pTrunk.aData, 0, 4);
                        ppPage = pTrunk;
                        pTrunk = null;
                        TRACE("ALLOCATE: %d trunk - %d free pages left\n", pPgno[0], n - 1);
                    } else if (k > usableSize / 4 - 2) {
                        /* Value of k is out of range. Database corruption */
                        throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                    } else if (searchList && nearby == iTrunk) {
                        /*
                         * The list is being searched and this trunk page is the
                         * page to allocate, regardless of whether it has
                         * leaves.
                         */
                        assert (pPgno[0] == iTrunk);
                        ppPage = pTrunk;
                        searchList = false;
                        pTrunk.pDbPage.write();
                        if (k == 0) {
                            if (pPrevTrunk == null) {
                                SqlJetUtility.memcpy(pPage1.aData, 32, pTrunk.aData, 0, 4);
                            } else {
                                SqlJetUtility.memcpy(pPrevTrunk.aData, 0, pTrunk.aData, 0, 4);
                            }
                        } else {
                            /*
                             * The trunk page is required by the caller but it
                             * contains pointers to free-list leaves. The first
                             * leaf becomes a trunk page in this case.
                             */
                            SqlJetMemPage pNewTrunk;
                            int iNewTrunk = SqlJetUtility.get4byte(pTrunk.aData, 8);
                            pNewTrunk = getPage(iNewTrunk, false);
                            try {
                                pNewTrunk.pDbPage.write();
                            } catch (SqlJetException e) {
                                SqlJetMemPage.releasePage(pNewTrunk);
                                throw e;
                            }
                            SqlJetUtility.memcpy(pNewTrunk.aData, 0, pTrunk.aData, 0, 4);
                            SqlJetUtility.put4byte(pNewTrunk.aData, 4, k - 1);
                            SqlJetUtility.memcpy(pNewTrunk.aData, 8, pTrunk.aData, 12, (k - 1) * 4);
                            SqlJetMemPage.releasePage(pNewTrunk);
                            if (pPrevTrunk == null) {
                                SqlJetUtility.put4byte(pPage1.aData, 32, iNewTrunk);
                            } else {
                                pPrevTrunk.pDbPage.write();
                                SqlJetUtility.put4byte(pPrevTrunk.aData, 0, iNewTrunk);
                            }
                        }
                        pTrunk = null;
                        TRACE("ALLOCATE: %d trunk - %d free pages left\n", pPgno[0], n - 1);
                    } else {
                        /* Extract a leaf from the trunk */
                        int closest;
                        int iPage;
                        ISqlJetMemoryPointer aData = pTrunk.aData;
                        pTrunk.pDbPage.write();
                        if (nearby > 0) {
                            int i, dist;
                            closest = 0;
                            dist = SqlJetUtility.get4byte(aData, 8) - nearby;
                            if (dist < 0)
                                dist = -dist;
                            for (i = 1; i < k; i++) {
                                int d2 = SqlJetUtility.get4byte(aData, 8 + i * 4) - nearby;
                                if (d2 < 0)
                                    d2 = -d2;
                                if (d2 < dist) {
                                    closest = i;
                                    dist = d2;
                                }
                            }
                        } else {
                            closest = 0;
                        }

                        iPage = SqlJetUtility.get4byte(aData, 8 + closest * 4);
                        if (!searchList || iPage == nearby) {
                            int nPage;
                            pPgno[0] = iPage;
                            nPage = getPageCount();
                            if (pPgno[0] > nPage) {
                                /* Free page off the end of the file */
                                throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                            }
                            TRACE("ALLOCATE: %d was leaf %d of %d on trunk %d" + ": %d more free pages\n", pPgno[0],
                                    closest + 1, k, pTrunk.pgno, n - 1);
                            if (closest < k - 1) {
                                SqlJetUtility.memcpy(aData, 8 + closest * 4, aData, 4 + k * 4, 4);
                            }
                            SqlJetUtility.put4byte(aData, 4, k - 1);
                            ppPage = getPage(pPgno[0], true);
                            ppPage.pDbPage.dontRollback();
                            try {
                                ppPage.pDbPage.write();
                            } catch (SqlJetException e) {
                                SqlJetMemPage.releasePage(ppPage);
                            }
                            searchList = false;
                        }
                    }
                    SqlJetMemPage.releasePage(pPrevTrunk);
                    pPrevTrunk = null;
                } while (searchList);

            } else {
                /*
                 * There are no pages on the freelist, so create a new page at
                 * the end of the file
                 */
                int nPage = getPageCount();
                pPgno[0] = nPage + 1;

                if (autoVacuum && PTRMAP_ISPAGE(pPgno[0])) {
                    /*
                     * IfpPgno refers to a pointer-map page, allocate two new
                     * pages at the end of the file instead of one. The first
                     * allocated page becomes a new pointer-map page, the second
                     * is used by the caller.
                     */
                    TRACE("ALLOCATE: %d from end of file (pointer-map page)\n", pPgno[0]);
                    assert (pPgno[0] != PENDING_BYTE_PAGE());
                    pPgno[0]++;
                    if (pPgno[0] == PENDING_BYTE_PAGE()) {
                        pPgno[0]++;
                    }
                }

                assert (pPgno[0] != PENDING_BYTE_PAGE());
                ppPage = getPage(pPgno[0], false);
                try {
                    ppPage.pDbPage.write();
                } catch (SqlJetException e) {
                    SqlJetMemPage.releasePage(ppPage);
                }
                TRACE("ALLOCATE: %d from end of file\n", pPgno[0]);
            }

            assert (pPgno[0] != PENDING_BYTE_PAGE());

        } finally {
            // end_allocate_page:
            SqlJetMemPage.releasePage(pTrunk);
            SqlJetMemPage.releasePage(pPrevTrunk);
        }

        if (ppPage.pDbPage.getRefCount() > 1) {
            SqlJetMemPage.releasePage(ppPage);
            throw new SqlJetException(SqlJetErrorCode.CORRUPT);
        }
        ppPage.isInit = false;
        return ppPage;

    }

    /**
     * Move the open database page pDbPage to location iFreePage in the
     * database. The pDbPage reference remains valid.
     * 
     * @param pDbPage
     *            Open page to move
     * @param s
     *            Pointer map 'type' entry for pDbPage
     * @param iPtrPage
     *            Pointer map 'page-no' entry for pDbPage
     * @param iFreePage
     *            The location to move pDbPage to
     * @param isCommit
     * @throws SqlJetException
     */
    public void relocatePage(SqlJetMemPage pDbPage, short s, int iPtrPage, int iFreePage, boolean isCommit)
            throws SqlJetException {
        /* The page that contains a pointer to pDbPage */
        SqlJetMemPage pPtrPage;
        int iDbPage = pDbPage.pgno;

        assert (s == PTRMAP_OVERFLOW2 || s == PTRMAP_OVERFLOW1 || s == PTRMAP_BTREE || s == PTRMAP_ROOTPAGE);
        assert (mutex.held());
        assert (pDbPage.pBt == this);

        /* Move page iDbPage from its current location to page number iFreePage */

        TRACE("AUTOVACUUM: Moving %d to free page %d (ptr page %d type %d)\n", iDbPage, iFreePage, iPtrPage, s);
        pDbPage.pDbPage.move(iFreePage, isCommit);
        pDbPage.pgno = iFreePage;

        /*
         * If pDbPage was a btree-page, then it may have child pages and/or
         * cells that point to overflow pages. The pointer map entries for all
         * these pages need to be changed.
         * 
         * If pDbPage is an overflow page, then the first 4 bytes may store a
         * pointer to a subsequent overflow page. If this is the case, then the
         * pointer map needs to be updated for the subsequent overflow page.
         */
        if (s == PTRMAP_BTREE || s == PTRMAP_ROOTPAGE) {
            pDbPage.setChildPtrmaps();
        } else {
            int nextOvfl = SqlJetUtility.get4byte(pDbPage.aData);
            if (nextOvfl != 0) {
                ptrmapPut(nextOvfl, PTRMAP_OVERFLOW2, iFreePage);
            }
        }

        /*
         * Fix the database pointer on page iPtrPage that pointed at iDbPage so
         * that it points at iFreePage. Also fix the pointer map entry for
         * iPtrPage.
         */
        if (s != PTRMAP_ROOTPAGE) {
            pPtrPage = getPage(iPtrPage, false);
            try {
                pPtrPage.pDbPage.write();
            } catch (SqlJetException e) {
                SqlJetMemPage.releasePage(pPtrPage);
                throw e;
            }
            try {
                pPtrPage.modifyPagePointer(iDbPage, iFreePage, s);
            } finally {
                SqlJetMemPage.releasePage(pPtrPage);
            }
            ptrmapPut(iFreePage, s, iPtrPage);
        }
    }

    /**
     * Perform a single step of an incremental-vacuum. If successful, return
     * SQLITE_OK. If there is no work to do (and therefore no point in calling
     * this function again), return SQLITE_DONE.
     * 
     * More specificly, this function attempts to re-organize the database so
     * that the last page of the file currently in use is no longer in use.
     * 
     * If the nFin parameter is non-zero, the implementation assumes that the
     * caller will keep calling incrVacuumStep() until it returns SQLITE_DONE or
     * an error, and that nFin is the number of pages the database file will
     * contain after this process is complete.
     * 
     * @throws SqlJetException
     */
    public void incrVacuumStep(int nFin, int iLastPg) throws SqlJetException {
        int nFreeList; /* Number of pages still on the free-list */

        assert (mutex.held());

        if (!PTRMAP_ISPAGE(iLastPg) && iLastPg != PENDING_BYTE_PAGE()) {
            short[] eType = { 0 };
            int[] iPtrPage = { 0 };

            nFreeList = SqlJetUtility.get4byte(pPage1.aData, 36);
            if (nFreeList == 0 || nFin == iLastPg) {
                throw new SqlJetException(SqlJetErrorCode.DONE);
            }

            ptrmapGet(iLastPg, eType, iPtrPage);
            if (eType[0] == PTRMAP_ROOTPAGE) {
                throw new SqlJetException(SqlJetErrorCode.CORRUPT);
            }

            if (eType[0] == PTRMAP_FREEPAGE) {
                if (nFin == 0) {
                    /*
                     * Remove the page from the files free-list. This is not
                     * required if nFin is non-zero. In that case, the free-list
                     * will be truncated to zero after this function returns, so
                     * it doesn't matter if it still contains some garbage
                     * entries.
                     */
                    int[] iFreePg = new int[1];
                    SqlJetMemPage pFreePg;
                    pFreePg = allocatePage(iFreePg, iLastPg, true);
                    assert (iFreePg[0] == iLastPg);
                    SqlJetMemPage.releasePage(pFreePg);
                }
            } else {
                /* Index of free page to move pLastPg to */
                int[] iFreePg = new int[1];
                SqlJetMemPage pLastPg;

                pLastPg = getPage(iLastPg, false);

                /*
                 * If nFin is zero, this loop runs exactly once and page pLastPg
                 * is swapped with the first free page pulled off the free list.
                 * 
                 * On the other hand, if nFin is greater than zero, then keep
                 * looping until a free-page located within the first nFin pages
                 * of the file is found.
                 */
                do {
                    SqlJetMemPage pFreePg;
                    try {
                        pFreePg = allocatePage(iFreePg, 0, false);
                    } catch (SqlJetException e) {
                        SqlJetMemPage.releasePage(pLastPg);
                        throw e;
                    }
                    SqlJetMemPage.releasePage(pFreePg);
                } while (nFin != 0 && iFreePg[0] > nFin);
                assert (iFreePg[0] < iLastPg);
                pLastPg.pDbPage.write();
                try {
                    relocatePage(pLastPg, eType[0], iPtrPage[0], iFreePg[0], nFin != 0);
                } finally {
                    SqlJetMemPage.releasePage(pLastPg);
                }
            }
        }

        if (nFin == 0) {
            iLastPg--;
            while (iLastPg == PENDING_BYTE_PAGE() || PTRMAP_ISPAGE(iLastPg)) {
                iLastPg--;
            }
            pPager.truncateImage(iLastPg);
        }

    }

    /**
     * This routine is called prior to sqlite3PagerCommit when a transaction is
     * commited for an auto-vacuum database.
     * 
     * If SQLITE_OK is returned, then *pnTrunc is set to the number of pages the
     * database file should be truncated to during the commit process. i.e. the
     * database has been reorganized so that only the first *pnTrunc pages are
     * in use.
     * 
     */
    public void autoVacuumCommit() throws SqlJetException {

        int nref = pPager.getRefCount();

        assert (mutex.held());
        invalidateAllOverflowCache();
        assert (autoVacuum);
        if (!incrVacuum) {
            int nFin;
            int nFree;
            int nPtrmap;
            int iFree;
            final int pgsz = pageSize;
            int nOrig = getPageCount();

            if (PTRMAP_ISPAGE(nOrig)) {
                throw new SqlJetException(SqlJetErrorCode.CORRUPT);
            }
            if (nOrig == PENDING_BYTE_PAGE()) {
                nOrig--;
            }
            nFree = SqlJetUtility.get4byte(pPage1.aData, 36);
            nPtrmap = (nFree - nOrig + PTRMAP_PAGENO(nOrig) + pgsz / 5) / (pgsz / 5);
            nFin = nOrig - nFree - nPtrmap;
            if (nOrig > PENDING_BYTE_PAGE() && nFin <= PENDING_BYTE_PAGE()) {
                nFin--;
            }
            while (PTRMAP_ISPAGE(nFin) || nFin == PENDING_BYTE_PAGE()) {
                nFin--;
            }

            try {

                try {
                    for (iFree = nOrig; iFree > nFin; iFree--) {
                        incrVacuumStep(nFin, iFree);
                    }
                } catch (SqlJetException e) {
                    if (e.getErrorCode() != SqlJetErrorCode.DONE)
                        throw e;
                }

                if (nFree > 0) {
                    pPage1.pDbPage.write();
                    SqlJetUtility.put4byte(pPage1.aData, 32, 0);
                    SqlJetUtility.put4byte(pPage1.aData, 36, 0);
                    pPager.truncateImage(nFin);
                }

            } catch (SqlJetException e) {
                pPager.rollback();
                throw e;
            }

        }

        assert (nref == pPager.getRefCount());
    }

    /**
     * If there are no outstanding cursors and we are not in the middle of a
     * transaction but there is a read lock on the database, then this routine
     * unrefs the first page of the database file which has the effect of
     * releasing the read lock.
     * 
     * If there are any outstanding cursors, this routine is a no-op.
     * 
     * If there is a transaction in progress, this routine is a no-op.
     * 
     * @throws SqlJetException
     */
    public void unlockBtreeIfUnused() throws SqlJetException {
        assert (mutex.held());
        if (inTransaction == TransMode.NONE && pCursor == null && pPage1 != null) {
            if (pPager.getRefCount() >= 1) {
                assert (pPage1.aData != null);
                SqlJetMemPage.releasePage(pPage1);
            }
            pPage1 = null;
            inStmt = false;
        }
    }

    /**
     * Save the positions of all cursors except pExcept open on the table with
     * root-page iRoot. Usually, this is called just before cursor pExcept is
     * used to modify the table (BtreeDelete() or BtreeInsert()).
     * 
     * @param i
     * @param j
     * @throws SqlJetException
     */
    public boolean saveAllCursors(int iRoot, SqlJetBtreeCursor pExcept) throws SqlJetException {
        SqlJetBtreeCursor p;
        assert (mutex.held());
        assert (pExcept == null || pExcept.pBt == this);
        for (p = this.pCursor; p != null; p = p.pNext) {
            if (p != pExcept && (0 == iRoot || p.pgnoRoot == iRoot) && p.eState == SqlJetBtreeCursor.CursorState.VALID) {
                if (!p.saveCursorPosition())
                    return false;
            }
        }
        return true;
    }

    /**
     * Return the number of write-cursors open on this handle. This is for use
     * in assert() expressions, so it is only compiled if NDEBUG is not defined.
     * 
     * For the purposes of this routine, a write-cursor is any cursor that is
     * capable of writing to the databse. That means the cursor was originally
     * opened for writing and the cursor has not be disabled by having its state
     * changed to CURSOR_FAULT.
     * 
     * @return
     */
    public int countWriteCursors() {
        SqlJetBtreeCursor pCur;
        int r = 0;
        for (pCur = this.pCursor; pCur != null; pCur = pCur.pNext) {
            if (pCur.wrFlag && pCur.eState != SqlJetBtreeCursor.CursorState.FAULT)
                r++;
        }
        return r;
    }

    /**
     * Erase the given database page and all its children. Return the page to
     * the freelist.
     * 
     * @param pgno
     * @param freePageFlag
     *            Page number to clear
     * @param pnChange
     *            Deallocate page if true
     * 
     * @throws SqlJetException
     */
    public void clearDatabasePage(int pgno, boolean freePageFlag, int[] pnChange) throws SqlJetException {
        SqlJetMemPage pPage = null;
        ISqlJetMemoryPointer pCell;
        int i;

        assert (mutex.held());
        if (pgno > pPager.getPageCount()) {
            throw new SqlJetException(SqlJetErrorCode.CORRUPT);
        }

        try {

            pPage = getAndInitPage(pgno);
            for (i = 0; i < pPage.nCell; i++) {
                pCell = pPage.findCell(i);
                if (!pPage.leaf) {
                    clearDatabasePage(SqlJetUtility.get4byte(pCell), true, pnChange);
                }
                pPage.clearCell(pCell);
            }
            if (!pPage.leaf) {
                clearDatabasePage(SqlJetUtility.get4byte(pPage.aData, 8), true, pnChange);
            } else if (pnChange != null) {
                assert (pPage.intKey);
                pnChange[0] += pPage.nCell;
            }
            if (freePageFlag) {
                pPage.freePage();
            } else {
                pPage.pDbPage.write();
                pPage.zeroPage(SqlJetUtility.getUnsignedByte(pPage.aData, 0) | SqlJetMemPage.PTF_LEAF);
            }

        } finally {
            // cleardatabasepage_out:
            SqlJetMemPage.releasePage(pPage);
        }

    }

    /**
     * Get a page from the pager and initialize it. This routine* is just a
     * convenience wrapper around separate calls to* sqlite3BtreeGetPage() and
     * sqlite3BtreeInitPage().
     * 
     * @param pgno
     *            Number of the page to get
     * @return
     * @throws SqlJetException
     */
    SqlJetMemPage getAndInitPage(int pgno) throws SqlJetException {

        ISqlJetPage pDbPage = null;
        SqlJetMemPage pPage = null;

        assert (mutex.held());
        if (pgno == 0) {
            throw new SqlJetException(SqlJetErrorCode.CORRUPT);
        }

        /*
         * It is often the case that the page we want is already in cache.* If
         * so, get it directly. This saves us from having to call*
         * pagerPagecount() to make sure pgno is within limits, which results*
         * in a measureable performance improvements.
         */
        try {
            pDbPage = pPager.lookupPage(pgno);
            if (pDbPage != null) {
                /* Page is already in cache */
                pPage = pageFromDbPage(pDbPage, pgno);
            } else {
                /* Page not in cache. Acquire it. */
                if (pgno > pPager.getPageCount()) {
                    throw new SqlJetException(SqlJetErrorCode.CORRUPT);
                }
                pPage = getPage(pgno, false);
            }
            if (!pPage.isInit) {
                pPage.initPage();
            }
        } catch (SqlJetException e) {
            SqlJetMemPage.releasePage(pPage);
            throw e;
        }

        return pPage;

    }

    /**
     * Given the page number of an overflow page in the database (parameter
     * ovfl), this function finds the page number of the next page in the linked
     * list of overflow pages. If possible, it uses the auto-vacuum pointer-map
     * data instead of reading the content of page ovfl to do so.
     * 
     * If an error occurs an SQLite error code is returned. Otherwise:
     * 
     * Unless pPgnoNext is NULL, the page number of the next overflow page in
     * the linked list is written to *pPgnoNext. If page ovfl is the last page
     * in its linked list, *pPgnoNext is set to zero.
     * 
     * If ppPage is not NULL, *ppPage is set to the MemPage* handle for page
     * ovfl. The underlying pager page may have been requested with the
     * noContent flag set, so the page data accessable via this handle may not
     * be trusted.
     * 
     * @param ovfl
     *            Overflow page
     * @param ppPage
     *            OUT: MemPage handle
     * @param pPgnoNext
     *            OUT: Next overflow page number
     * 
     * @throws SqlJetException
     */
    public void getOverflowPage(int ovfl, SqlJetMemPage[] ppPage, int[] pPgnoNext) throws SqlJetException {
        int next = 0;

        assert (mutex.held());
        /* One of these must not be NULL. Otherwise, why call this function? */
        assert ((ppPage != null && ppPage.length != 0) || (pPgnoNext != null && pPgnoNext.length != 0));

        /*
         * If pPgnoNext is NULL, then this function is being called to obtain a
         * MemPage reference only. No page-data is required in this case.
         */
        if (pPgnoNext == null || pPgnoNext.length == 0 || pPgnoNext[0] == 0) {
            ppPage[0] = getPage(ovfl, true);
            return;
        }

        /*
         * Try to find the next page in the overflow list using the autovacuum
         * pointer-map pages. Guess that the next page in the overflow list is
         * page number (ovfl+1). If that guess turns out to be wrong, fall back
         * to loading the data of page number ovfl to determine the next page
         * number.
         */
        if (autoVacuum) {

            int[] pgno = { 0 };
            int iGuess = ovfl + 1;
            short[] eType = { 0 };

            while (PTRMAP_ISPAGE(iGuess) || iGuess == PENDING_BYTE_PAGE()) {
                iGuess++;
            }

            if (iGuess <= pPager.getPageCount()) {
                ptrmapGet(iGuess, eType, pgno);
                if (eType[0] == PTRMAP_OVERFLOW2 && pgno[0] == ovfl) {
                    next = iGuess;
                }
            }
        }

        if (next == 0 || (ppPage != null && ppPage.length != 0)) {
            SqlJetMemPage pPage = null;

            try {
                pPage = getPage(ovfl, next != 0);
            } finally {
                if (next == 0 && pPage != null) {
                    next = SqlJetUtility.get4byte(pPage.aData, 0);
                }

                if (ppPage != null && ppPage.length != 0) {
                    ppPage[0] = pPage;
                } else {
                    SqlJetMemPage.releasePage(pPage);
                }
            }

        }

        pPgnoNext[0] = next;

    }

    /**
     * Make sure pBt->pTmpSpace points to an allocation of MX_CELL_SIZE(pBt)
     * bytes.
     * 
     */
    public void allocateTempSpace() {
        if (pTmpSpace == null) {
            pTmpSpace = SqlJetUtility.allocatePtr(pageSize);
        }
    }

}
