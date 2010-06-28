/**
 * SqlJetBtreeCursor.java
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

import static org.tmatesoft.sqljet.core.internal.SqlJetUtility.get2byte;
import static org.tmatesoft.sqljet.core.internal.SqlJetUtility.get4byte;
import static org.tmatesoft.sqljet.core.internal.SqlJetUtility.memcpy;
import static org.tmatesoft.sqljet.core.internal.SqlJetUtility.movePtr;
import static org.tmatesoft.sqljet.core.internal.SqlJetUtility.mutex_held;
import static org.tmatesoft.sqljet.core.internal.SqlJetUtility.pointer;
import static org.tmatesoft.sqljet.core.internal.SqlJetUtility.put4byte;
import static org.tmatesoft.sqljet.core.internal.btree.SqlJetBtree.TRACE;

import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.internal.ISqlJetBtreeCursor;
import org.tmatesoft.sqljet.core.internal.ISqlJetDbHandle;
import org.tmatesoft.sqljet.core.internal.ISqlJetKeyInfo;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.ISqlJetPage;
import org.tmatesoft.sqljet.core.internal.ISqlJetUnpackedRecord;
import org.tmatesoft.sqljet.core.internal.SqlJetCloneable;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;
import org.tmatesoft.sqljet.core.internal.btree.SqlJetBtree.TransMode;
import org.tmatesoft.sqljet.core.internal.vdbe.SqlJetUnpackedRecord;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetBtreeCursor extends SqlJetCloneable implements ISqlJetBtreeCursor {

    /*
     * The following parameters determine how many adjacent pages get involved
     * in a balancing operation. NN is the number of neighbors on either side*
     * of the page that participate in the balancing operation. NB is the* total
     * number of pages that participate, including the target page and* NN
     * neighbors on either side.** The minimum value of NN is 1 (of course).
     * Increasing NN above 1* (to 2 or 3) gives a modest improvement in SELECT
     * and DELETE performance* in exchange for a larger degradation in INSERT
     * and UPDATE performance.* The value of NN appears to give the best results
     * overall.
     */
    /** Number of neighbors on either side of pPage */
    private static final int NN = 1;
    /** Total pages involved in the balance */
    private static final int NB = (NN * 2 + 1);

    /** The Btree to which this cursor belongs */
    SqlJetBtree pBtree;

    /** The BtShared this cursor points to */
    SqlJetBtreeShared pBt;

    /** Forms a linked list of all cursors */
    SqlJetBtreeCursor pNext, pPrev;

    /** Argument passed to comparison function */
    ISqlJetKeyInfo pKeyInfo;

    /** The root page of this tree */
    int pgnoRoot;

    /** A parse of the cell we are pointing at */
    SqlJetBtreeCellInfo info = new SqlJetBtreeCellInfo();

    /** True if writable */
    boolean wrFlag;

    /** Cursor pointing to the last entry */
    boolean atLast;

    /** True if info.nKey is valid */
    boolean validNKey;

    /** One of the CURSOR_XXX constants (see below) */
    CursorState eState;

    /** Saved key that was cursor's last known position */
    ISqlJetMemoryPointer pKey;

    /** Size of pKey, or last integer key */
    long nKey;

    SqlJetErrorCode error;

    /**
     * (skip<0) -> Prev() is a no-op. (skip>0) -> Next() is
     */
    int skip;

    /** True if this cursor is an incr. io handle */
    boolean isIncrblobHandle;

    /** Cache of overflow page locations */
    int[] aOverflow;

    /** True if Btree pages are rearranged by balance() */
    boolean pagesShuffled;

    /** Index of current page in apPage */
    int iPage;

    /**
     * Pages from root to current page
     */
    SqlJetMemPage[] apPage = new SqlJetMemPage[BTCURSOR_MAX_DEPTH];

    /** Current index in apPage[i] */
    int[] aiIdx = new int[BTCURSOR_MAX_DEPTH];

    /**
     * Potential values for BtCursor.eState.
     * 
     * <ul>
     * 
     * <li>
     * CURSOR_VALID: Cursor points to a valid entry. getPayload() etc. may be
     * called.</li>
     * 
     * <li>
     * CURSOR_INVALID: Cursor does not point to a valid entry. This can happen
     * (for example) because the table is empty or because BtreeCursorFirst()
     * has not been called.</li>
     * 
     * <li>
     * CURSOR_REQUIRESEEK: The table that this cursor was opened on still
     * exists, but has been modified since the cursor was last used. The cursor
     * position is saved in variables BtCursor.pKey and BtCursor.nKey. When a
     * cursor is in this state, restoreCursorPosition() can be called to attempt
     * to seek the cursor to the saved position.</li>
     * 
     * <li>
     * CURSOR_FAULT: A unrecoverable error (an I/O error or a malloc failure)
     * has occurred on a different connection that shares the BtShared cache
     * with this cursor. The error has left the cache in an inconsistent state.
     * Do nothing else with this cursor. Any attempt to use the cursor should
     * return the error code stored in BtCursor.skip</li>
     * 
     * </ul>
     * 
     */
    static enum CursorState {
        INVALID, // 0
        VALID, // 1
        REQUIRESEEK, // 2
        FAULT
        // 3
    }

    /**
     * Create a new cursor for the BTree whose root is on the page iTable. The
     * act of acquiring a cursor gets a read lock on the database file.
     * 
     * If wrFlag==0, then the cursor can only be used for reading. If wrFlag==1,
     * then the cursor can be used for reading or for writing if other
     * conditions for writing are also met. These are the conditions that must
     * be met in order for writing to be allowed:
     * 
     * 1: The cursor must have been opened with wrFlag==1
     * 
     * 2: Other database connections that share the same pager cache but which
     * are not in the READ_UNCOMMITTED state may not have cursors open with
     * wrFlag==0 on the same table. Otherwise the changes made by this write
     * cursor would be visible to the read cursors in the other database
     * connection.
     * 
     * 3: The database must be writable (not on read-only media)
     * 
     * 4: There must be an active transaction.
     * 
     * No checking is done to make sure that page iTable really is the root page
     * of a b-tree. If it is not, then the cursor acquired will not work
     * correctly.
     * 
     * It is assumed that the sqlite3BtreeCursorSize() bytes of memory pointed
     * to by pCur have been zeroed by the caller.
     * 
     * 
     * @param sqlJetBtree
     * @param table
     * @param wrFlag2
     * @param keyInfo
     * 
     * @throws SqlJetException
     */
    public SqlJetBtreeCursor(SqlJetBtree btree, int table, boolean wrFlag, ISqlJetKeyInfo keyInfo)
            throws SqlJetException {

        int nPage;
        SqlJetBtreeShared pBt = btree.pBt;

        assert (btree.holdsMutex());

        if (wrFlag) {
            assert (!pBt.readOnly);
            if (pBt.readOnly) {
                throw new SqlJetException(SqlJetErrorCode.READONLY);
            }
            if (btree.checkReadLocks(table, null, 0)) {
                throw new SqlJetException(SqlJetErrorCode.LOCKED);
            }
        }

        if (pBt.pPage1 == null) {
            btree.lockWithRetry();
        }
        this.pgnoRoot = table;
        nPage = pBt.pPager.getPageCount();
        try {

            if (table == 1 && nPage == 0) {
                throw new SqlJetException(SqlJetErrorCode.EMPTY);
            }
            this.apPage[0] = pBt.getAndInitPage(pgnoRoot);

            /*
             * Now that no other errors can occur, finish filling in the
             * BtCursor* variables, link the cursor into the BtShared list and
             * set *ppCur (the* output argument to this function).
             */
            this.pKeyInfo = keyInfo;
            this.pBtree = btree;
            this.pBt = pBt;
            this.wrFlag = wrFlag;
            this.pNext = pBt.pCursor;
            if (this.pNext != null) {
                this.pNext.pPrev = this;
            }
            pBt.pCursor = this;
            this.eState = CursorState.INVALID;

        } catch (SqlJetException e) {
            // create_cursor_exception:
            SqlJetMemPage.releasePage(this.apPage[0]);
            pBt.unlockBtreeIfUnused();
            throw e;
        }
    }

    /**
     * @return
     */
    private boolean holdsMutex() {
        return pBt.mutex.held();
    }

    /**
     * @param cur
     * @return
     */
    private boolean cursorHoldsMutex(SqlJetBtreeCursor cur) {
        return cur.holdsMutex();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#clearCursor()
     */
    public void clearCursor() {
        assert (holdsMutex());
        pKey = null;
        eState = CursorState.INVALID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#closeCursor()
     */
    public void closeCursor() throws SqlJetException {
        if (pBtree != null) {
            int i;
            pBtree.enter();
            try {
                pBt.db = pBtree.db;
                clearCursor();
                if (pPrev != null) {
                    pPrev.pNext = pNext;
                } else {
                    pBt.pCursor = pNext;
                }
                if (pNext != null) {
                    pNext.pPrev = pPrev;
                }
                for (i = 0; i <= iPage; i++) {
                    SqlJetMemPage.releasePage(apPage[i]);
                }
                pBt.unlockBtreeIfUnused();
                invalidateOverflowCache();
                /* sqlite3_free(pCur); */
            } finally {
                pBtree.leave();
            }
        }
    }

    /**
     * Invalidate the overflow page-list cache for cursor pCur, if any.
     */
    private void invalidateOverflowCache() {
        assert (holdsMutex());
        aOverflow = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#moveTo(byte[], long,
     * boolean)
     */
    public int moveTo(ISqlJetMemoryPointer pKey, long nKey, boolean bias) throws SqlJetException {
        /* Unpacked index key */
        SqlJetUnpackedRecord pIdxKey;

        if (pKey != null) {
            assert (nKey == (long) (int) nKey);
            pIdxKey = pKeyInfo.recordUnpack((int) nKey, pKey);
            if (pIdxKey == null)
                throw new SqlJetException(SqlJetErrorCode.NOMEM);
        } else {
            pIdxKey = null;
        }

        try {
            return moveToUnpacked(pIdxKey, nKey, bias);
        } finally {
            if (pKey != null) {
                SqlJetUnpackedRecord.delete(pIdxKey);
            }
        }
    }

    /**
     * Move the cursor to the root page
     */
    private void moveToRoot() throws SqlJetException {
        SqlJetMemPage pRoot;

        assert (this.holdsMutex());
        assert (CursorState.INVALID.compareTo(CursorState.REQUIRESEEK) < 0);
        assert (CursorState.VALID.compareTo(CursorState.REQUIRESEEK) < 0);
        assert (CursorState.FAULT.compareTo(CursorState.REQUIRESEEK) > 0);
        if (this.eState.compareTo(CursorState.REQUIRESEEK) >= 0) {
            if (this.eState == CursorState.FAULT) {
                throw new SqlJetException(this.error);
            }
            this.clearCursor();
        }

        if (this.iPage >= 0) {
            int i;
            for (i = 1; i <= this.iPage; i++) {
                SqlJetMemPage.releasePage(this.apPage[i]);
            }
        } else {
            try {
                this.apPage[0] = pBt.getAndInitPage(this.pgnoRoot);
            } catch (SqlJetException e) {
                this.eState = CursorState.INVALID;
                throw e;
            }
        }

        pRoot = this.apPage[0];
        assert (pRoot.pgno == this.pgnoRoot);
        this.iPage = 0;
        this.aiIdx[0] = 0;
        this.info.nSize = 0;
        this.atLast = false;
        this.validNKey = false;

        if (pRoot.nCell == 0 && !pRoot.leaf) {
            int subpage;
            assert (pRoot.pgno == 1);
            subpage = SqlJetUtility.get4byte(pRoot.aData, pRoot.hdrOffset + 8);
            assert (subpage > 0);
            this.eState = CursorState.VALID;
            moveToChild(subpage);
        } else {
            this.eState = ((pRoot.nCell > 0) ? CursorState.VALID : CursorState.INVALID);
        }

    }

    /**
     * Move the cursor down to a new child page. The newPgno argument is the
     * page number of the child page to move to.
     */
    private void moveToChild(int newPgno) throws SqlJetException {
        int i = this.iPage;
        SqlJetMemPage pNewPage;

        assert (this.holdsMutex());
        assert (this.eState == CursorState.VALID);
        assert (this.iPage < BTCURSOR_MAX_DEPTH);
        if (this.iPage >= (BTCURSOR_MAX_DEPTH - 1)) {
            throw new SqlJetException(SqlJetErrorCode.CORRUPT);
        }
        pNewPage = pBt.getAndInitPage(newPgno);
        this.apPage[i + 1] = pNewPage;
        this.aiIdx[i + 1] = 0;
        this.iPage++;

        this.info.nSize = 0;
        this.validNKey = false;
        if (pNewPage.nCell < 1) {
            throw new SqlJetException(SqlJetErrorCode.CORRUPT);
        }
    }

    /**
     * Make sure the BtCursor has a valid BtCursor.info structure. If it is not
     * already valid, call sqlite3BtreeParseCell() to fill it in.
     * 
     * BtCursor.info is a cache of the information in the current cell. Using
     * this cache reduces the number of calls to sqlite3BtreeParseCell().
     * 
     */
    private void getCellInfo() {
        if (this.info.nSize == 0) {
            this.info = this.apPage[iPage].parseCell(this.aiIdx[iPage]);
            this.validNKey = true;
        }
    }

    /**
     * Return a pointer to payload information from the entry that the pCur
     * cursor is pointing to. The pointer is to the beginning of the key if
     * skipKey==0 and it points to the beginning of data if skipKey==1. The
     * number of bytes of available key/data is written into *pAmt. If *pAmt==0,
     * then the value returned will not be a valid pointer.
     * 
     * This routine is an optimization. It is common for the entire key and data
     * to fit on the local page and for there to be no overflow pages. When that
     * is so, this routine can be used to access the key and data without making
     * a copy. If the key and/or data spills onto overflow pages, then
     * accessPayload() must be used to reassembly the key/data and copy it into
     * a preallocated buffer.
     * 
     * The pointer returned by this routine looks directly into the cached page
     * of the database. The data might change or move the next time any btree
     * routine is called.
     * 
     * @param pAmt
     *            Write the number of available bytes here
     * @param skipKey
     *            read beginning at data if this is true
     * @return
     */
    private ISqlJetMemoryPointer fetchPayload(int[] pAmt, boolean skipKey) {
        ISqlJetMemoryPointer aPayload;
        SqlJetMemPage pPage;
        int nKey;
        int nLocal;

        assert (this.iPage >= 0 && this.apPage[this.iPage] != null);
        assert (this.eState == CursorState.VALID);
        assert (this.holdsMutex());
        pPage = this.apPage[this.iPage];
        assert (this.aiIdx[this.iPage] < pPage.nCell);
        this.getCellInfo();
        aPayload = SqlJetUtility.pointer(this.info.pCell, this.info.nHeader);
        if (pPage.intKey) {
            nKey = 0;
        } else {
            nKey = (int) this.info.nKey;
        }
        if (skipKey) {
            SqlJetUtility.movePtr(aPayload, nKey);
            nLocal = this.info.nLocal - nKey;
        } else {
            nLocal = this.info.nLocal;
            if (nLocal > nKey) {
                nLocal = nKey;
            }
        }
        pAmt[0] = nLocal;
        return aPayload;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#moveToUnpacked(org.tmatesoft
     * .sqljet.core.ISqlJetUnpackedRecord, long, boolean)
     */
    public int moveToUnpacked(ISqlJetUnpackedRecord pIdxKey, long intKey, boolean biasRight) throws SqlJetException {

        assert (holdsMutex());
        assert (pBtree.db.getMutex().held());

        /*
         * If the cursor is already positioned at the point we are trying to
         * move to, then just return without doing any work
         */
        if (this.eState == CursorState.VALID && this.validNKey && this.apPage[0].intKey) {
            if (this.info.nKey == intKey) {
                return 0;
            }
            if (this.atLast && this.info.nKey < intKey) {
                return -1;
            }
        }

        moveToRoot();

        assert (this.apPage[this.iPage] != null);
        assert (this.apPage[this.iPage].isInit);
        if (this.eState == CursorState.INVALID) {
            assert (this.apPage[this.iPage].nCell == 0);
            return -1;
        }
        assert (this.apPage[0].intKey || pIdxKey != null);
        for (;;) {
            int lwr, upr;
            int chldPg;
            SqlJetMemPage pPage = this.apPage[this.iPage];
            int c = -1; /* pRes return if table is empty must be -1 */
            lwr = 0;
            upr = pPage.nCell - 1;
            if ((!pPage.intKey && pIdxKey == null) || upr < 0) {
                throw new SqlJetException(SqlJetErrorCode.CORRUPT);
            }
            if (biasRight) {
                this.aiIdx[this.iPage] = upr;
            } else {
                this.aiIdx[this.iPage] = ((upr + lwr) / 2);
            }
            for (;;) {
                ISqlJetMemoryPointer pCellKey;
                long[] nCellKey = new long[1];
                int idx = this.aiIdx[this.iPage];
                this.info.nSize = 0;
                this.validNKey = true;
                if (pPage.intKey) {
                    ISqlJetMemoryPointer pCell;
                    pCell = SqlJetUtility.pointer(pPage.findCell(idx), pPage.childPtrSize);
                    if (pPage.hasData) {
                        int[] dummy = new int[1];
                        SqlJetUtility.movePtr(pCell, SqlJetUtility.getVarint32(pCell, dummy));
                    }
                    SqlJetUtility.getVarint(pCell, nCellKey);
                    if (nCellKey[0] == intKey) {
                        c = 0;
                    } else if (nCellKey[0] < intKey) {
                        c = -1;
                    } else {
                        assert (nCellKey[0] > intKey);
                        c = +1;
                    }
                } else {
                    int[] available = new int[1];
                    pCellKey = this.fetchPayload(available, false);
                    nCellKey[0] = this.info.nKey;
                    if (available[0] >= nCellKey[0]) {
                        c = pIdxKey.recordCompare((int) nCellKey[0], pCellKey);
                    } else {
                        pCellKey = SqlJetUtility.allocatePtr((int) nCellKey[0]);
                        try {
                            this.key(0, (int) nCellKey[0], pCellKey);
                        } finally {
                            c = pIdxKey.recordCompare((int) nCellKey[0], pCellKey);
                            // sqlite3_free(pCellKey);
                        }
                    }
                }
                if (c == 0) {
                    this.info.nKey = nCellKey[0];
                    if (pPage.intKey && !pPage.leaf) {
                        lwr = idx;
                        upr = lwr - 1;
                        break;
                    } else {
                        return 0;
                    }
                }
                if (c < 0) {
                    lwr = idx + 1;
                } else {
                    upr = idx - 1;
                }
                if (lwr > upr) {
                    this.info.nKey = nCellKey[0];
                    break;
                }
                this.aiIdx[this.iPage] = (int) ((lwr + upr) / 2);
            }
            assert (lwr == upr + 1);
            assert (pPage.isInit);
            if (pPage.leaf) {
                chldPg = 0;
            } else if (lwr >= pPage.nCell) {
                chldPg = SqlJetUtility.get4byte(pPage.aData, pPage.hdrOffset + 8);
            } else {
                chldPg = SqlJetUtility.get4byte(pPage.findCell(lwr));
            }
            if (chldPg == 0) {
                assert (this.aiIdx[this.iPage] < this.apPage[this.iPage].nCell);
                return c;
            }
            this.aiIdx[this.iPage] = (int) lwr;
            this.info.nSize = 0;
            this.validNKey = false;
            moveToChild(chldPg);
        }

    }

    /**
     * Restore the cursor to the position it was in (or as close to as possible)
     * when saveCursorPosition() was called. Note that this call deletes the
     * saved position info stored by saveCursorPosition(), so there can be at
     * most one effective restoreCursorPosition() call after each
     * saveCursorPosition().
     */
    private void restoreCursorPosition() throws SqlJetException {
        if (this.eState.compareTo(CursorState.REQUIRESEEK) < 0)
            return;
        assert (this.holdsMutex());
        if (this.eState == CursorState.FAULT) {
            throw new SqlJetException(this.error);
        }
        this.eState = CursorState.INVALID;
        this.skip = this.moveTo(this.pKey, this.nKey, false);
        this.pKey = null;
        assert (this.eState == CursorState.VALID || this.eState == CursorState.INVALID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#cursorHasMoved()
     */
    public boolean cursorHasMoved() {
        try {
            restoreCursorPosition();
        } catch (SqlJetException e) {
            return true;
        }
        if (eState != CursorState.VALID || skip != 0) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#delete()
     */
    public void delete() throws SqlJetException {

        SqlJetBtreeCursor pCur = this;

        SqlJetMemPage pPage = pCur.apPage[pCur.iPage];
        int idx;
        ISqlJetMemoryPointer pCell;
        int pgnoChild = 0;
        SqlJetBtree p = pCur.pBtree;
        SqlJetBtreeShared pBt = p.pBt;

        assert (pCur.holdsMutex());
        assert (pPage.isInit);
        assert (pBt.inTransaction == TransMode.WRITE);
        assert (!pBt.readOnly);

        if (pCur.eState == CursorState.FAULT) {
            throw new SqlJetException(pCur.error);
        }
        if (pCur.aiIdx[pCur.iPage] >= pPage.nCell) {
            /* The cursor is not pointing to anything */
            throw new SqlJetException(SqlJetErrorCode.ERROR);
        }
        assert (pCur.wrFlag);
        if (pCur.pBtree.checkReadLocks(pCur.pgnoRoot, pCur, pCur.info.nKey)) {
            /* The table pCur points to has a read lock */
            throw new SqlJetException(SqlJetErrorCode.LOCKED);
        }

        /*
         * Restore the current cursor position (a no-op if the cursor is not in*
         * CURSOR_REQUIRESEEK state) and save the positions of any other cursors
         * * open on the same table. Then call sqlite3PagerWrite() on the page*
         * that the entry will be deleted from.
         */
        pCur.restoreCursorPosition();
        pBt.saveAllCursors(pCur.pgnoRoot, pCur);
        pPage.pDbPage.write();

        /*
         * Locate the cell within its page and leave pCell pointing to the*
         * data. The clearCell() call frees any overflow pages associated with
         * the* cell. The cell itself is still intact.
         */
        idx = pCur.aiIdx[pCur.iPage];
        pCell = pPage.findCell(idx);
        if (!pPage.leaf) {
            pgnoChild = get4byte(pCell);
        }
        pPage.clearCell(pCell);

        if (!pPage.leaf) {
            /*
             * * The entry we are about to delete is not a leaf so if we do not
             * do something we will leave a hole on an internal page. We have to
             * fill the hole by moving in a cell from a leaf. The next Cell
             * after the one to be deleted is guaranteed to exist and to be a
             * leaf so we can use it.
             */
            SqlJetBtreeCursor leafCur;
            SqlJetMemPage pLeafPage = null;

            ISqlJetMemoryPointer pNext;
            @SuppressWarnings("unused")
            boolean notUsed;
            ISqlJetMemoryPointer tempCell = null;
            assert (!pPage.intKey);
            leafCur = pCur.getTempCursor();
            notUsed = leafCur.next();
            assert (leafCur.aiIdx[leafCur.iPage] == 0);
            pLeafPage = leafCur.apPage[leafCur.iPage];
            pLeafPage.pDbPage.write();

            try {

                boolean leafCursorInvalid = false;
                int szNext;
                TRACE("DELETE: table=%d delete internal from %d replace from leaf %d\n", pCur.pgnoRoot, pPage.pgno,
                        pLeafPage.pgno);
                pPage.dropCell(idx, pPage.cellSizePtr(pCell));
                pNext = pLeafPage.findCell(0);
                szNext = pLeafPage.cellSizePtr(pNext);
                assert (pBt.MX_CELL_SIZE() >= szNext + 4);
                pBt.allocateTempSpace();
                tempCell = pBt.pTmpSpace;
                pPage.insertCell(idx, pointer(pNext, -4), szNext + 4, tempCell, (byte) 0);

                /*
                 * The "if" statement in the next code block is critical. The
                 * slightest error in that statement would allow SQLite to
                 * operate* correctly most of the time but produce very rare
                 * failures. To guard against this, the following macros help to
                 * verify that the "if" statement is well tested.
                 */

                if ((pPage.nOverflow > 0 || (pPage.nFree > pBt.usableSize * 2 / 3))
                        && (pLeafPage.nFree + 2 + szNext > pBt.usableSize * 2 / 3)) {
                    /*
                     * This branch is taken if the internal node is now either
                     * overflowing or underfull and the leaf node will be
                     * underfull after the just cell copied to the internal node
                     * is deleted from it. This is a special case because the
                     * call to balance() to correct the internal node may change
                     * the tree structure and invalidate the contents of the
                     * leafCur.apPage[] and leafCur.aiIdx[] arrays, which will
                     * be used by the balance() required to correct the
                     * underfull leaf node. The formula used in the expression
                     * above are based on facets of the SQLite file-format that
                     * do not change over time.
                     */
                    leafCursorInvalid = true;
                }

                assert (pPage.pDbPage.isWriteable());
                put4byte(pPage.findOverflowCell(idx), pgnoChild);
                pCur.balance(false);

                if (leafCursorInvalid) {
                    /*
                     * The leaf-node is now underfull and so the tree needs to
                     * be* rebalanced. However, the balance() operation on the
                     * internal* node above may have modified the structure of
                     * the B-Tree and* so the current contents of
                     * leafCur.apPage[] and leafCur.aiIdx[]* may not be
                     * trusted.** It is not possible to copy the ancestry from
                     * pCur, as the same* balance() call has invalidated the
                     * pCur->apPage[] and aiIdx[]* arrays.** The call to
                     * saveCursorPosition() below internally saves the* key that
                     * leafCur is currently pointing to. Currently, there* are
                     * two copies of that key in the tree - one here on the
                     * leaf* page and one on some internal node in the tree. The
                     * copy on* the leaf node is always the next key in
                     * tree-order after the* copy on the internal node. So, the
                     * call to sqlite3BtreeNext()* calls restoreCursorPosition()
                     * to point the cursor to the copy* stored on the internal
                     * node, then advances to the next entry,* which happens to
                     * be the copy of the key on the internal node.* Net effect:
                     * leafCur is pointing back to the duplicate cell* that
                     * needs to be removed, and the leafCur.apPage[] and*
                     * leafCur.aiIdx[] arrays are correct.
                     */
                    leafCur.saveCursorPosition();
                    notUsed = leafCur.next();
                    pLeafPage = leafCur.apPage[leafCur.iPage];
                    assert (leafCur.aiIdx[leafCur.iPage] == 0);
                }

                pLeafPage.pDbPage.write();
                pLeafPage.dropCell(0, szNext);
                leafCur.balance(false);
                assert (leafCursorInvalid || !leafCur.pagesShuffled || !pCur.pagesShuffled);

            } finally {
                leafCur.releaseTempCursor();
            }

        } else {
            TRACE("DELETE: table=%d delete from leaf %d\n", pCur.pgnoRoot, pPage.pgno);
            pPage.dropCell(idx, pPage.cellSizePtr(pCell));
            pCur.balance(false);
        }
        pCur.moveToRoot();
    }

    /**
     * The page that pCur currently points to has just been modified in some
     * way. This function figures out if this modification means the tree needs
     * to be balanced, and if so calls the appropriate balancing routine.
     * 
     * Parameter isInsert is true if a new cell was just inserted into the page,
     * or false otherwise.
     * 
     * @param i
     * @throws SqlJetException
     */
    private void balance(boolean isInsert) throws SqlJetException {
        final SqlJetBtreeCursor pCur = this;
        final SqlJetMemPage pPage = pCur.apPage[pCur.iPage];
        assert (pPage.pBt.mutex.held());
        if (pCur.iPage == 0) {
            pPage.pDbPage.write();
            if (pPage.nOverflow > 0) {
                pCur.balance_deeper();
                assert (pCur.apPage[0] == pPage);
                assert (pPage.nOverflow == 0);
            }
            if (pPage.nCell == 0) {
                pCur.balance_shallower();
                assert (pCur.apPage[0] == pPage);
                assert (pPage.nOverflow == 0);
            }
        } else {
            if (pPage.nOverflow > 0 || (!isInsert && pPage.nFree > pPage.pBt.usableSize * 2 / 3)) {
                pCur.balance_nonroot();
            }
        }
    }

    /**
     * This routine redistributes Cells on pPage and up to NN*2 siblings of
     * pPage so that all pages have about the same amount of free space. Usually
     * NN siblings on either side of pPage is used in the balancing, though more
     * siblings might come from one side if pPage is the first or last child of
     * its parent. If pPage has fewer than 2*NN siblings (something which can
     * only happen if pPage is the root page or a child of root) then all
     * available siblings participate in the balancing.
     * 
     * The number of siblings of pPage might be increased or decreased by one or
     * two in an effort to keep pages nearly full but not over full. The root
     * page is special and is allowed to be nearly empty. If pPage is the root
     * page, then the depth of the tree might be increased or decreased by one,
     * as necessary, to keep the root page from being overfull or completely
     * empty.
     * 
     * Note that when this routine is called, some of the Cells on pPage might
     * not actually be stored in pPage->aData[]. This can happen if the page is
     * overfull. Part of the job of this routine is to make sure all Cells for
     * pPage once again fit in pPage->aData[].
     * 
     * In the course of balancing the siblings of pPage, the parent of pPage
     * might become overfull or underfull. If that happens, then this routine is
     * called recursively on the parent.
     * 
     * If this routine fails for any reason, it might leave the database in a
     * corrupted state. So if this routine fails, the database should be rolled
     * back.
     * 
     * @throws SqlJetException
     * 
     */
    private void balance_nonroot() throws SqlJetException {

        final SqlJetBtreeCursor pCur = this;

        SqlJetMemPage pPage; /* The over or underfull page to balance */
        SqlJetMemPage pParent; /* The parent of pPage */
        SqlJetBtreeShared pBt; /* The whole database */
        int nCell = 0; /* Number of cells in apCell[] */
        int nMaxCells = 0; /* Allocated size of apCell, szCell, aFrom. */
        int nOld = 0; /* Number of pages in apOld[] */
        int nNew = 0; /* Number of pages in apNew[] */
        int nDiv; /* Number of cells in apDiv[] */
        int i, j, k; /* Loop counters */
        int idx; /* Index of pPage in pParent->aCell[] */
        int nxDiv; /* Next divider slot in pParent->aCell[] */
        // int rc; /* The return code */
        int leafCorrection; /* 4 if pPage is a leaf. 0 if not */
        boolean leafData; /* True if pPage is a leaf of a LEAFDATA tree */
        int usableSpace; /* Bytes in pPage beyond the header */
        int pageFlags; /* Value of pPage->aData[0] */
        int subtotal; /* Subtotal of bytes in cells on one page */
        int iSpace1 = 0; /* First unused byte of aSpace1[] */
        int iSpace2 = 0; /* First unused byte of aSpace2[] */
        // int szScratch; /* Size of scratch memory requested */
        /* pPage and up to two siblings */
        SqlJetMemPage[] apOld = new SqlJetMemPage[NB];
        int[] pgnoOld = new int[NB]; /* Page numbers for each page in apOld[] */
        /* Private copies of apOld[] pages */
        SqlJetMemPage[] apCopy = new SqlJetMemPage[NB];
        /* pPage and up to NB siblings after balancing */
        SqlJetMemPage[] apNew = new SqlJetMemPage[NB + 2];
        /* Page numbers for each page in apNew[] */
        int[] pgnoNew = new int[NB + 2];
        ISqlJetMemoryPointer[] apDiv = new ISqlJetMemoryPointer[NB]; /*
                                                                      * Divider
                                                                      * cells in
                                                                      * pParent
                                                                      */
        /* Index in aCell[] of cell after i-th page */
        int[] cntNew = new int[NB + 2];
        /* Combined size of cells place on i-th page */
        int[] szNew = new int[NB + 2];
        ISqlJetMemoryPointer[] apCell = null; /* All cells begin balanced */
        int[] szCell; /* Local size of all cells in apCell[] */
        /* Space for holding data of apCopy[] */
        ISqlJetMemoryPointer[] aCopy = new ISqlJetMemoryPointer[NB];
        ISqlJetMemoryPointer aSpace1; /*
                                       * Space for copies of dividers cells
                                       * before balance
                                       */
        /* Space for overflow dividers cells after balance */
        ISqlJetMemoryPointer aSpace2 = null;
        ISqlJetMemoryPointer aFrom = null;

        pPage = pCur.apPage[pCur.iPage];
        assert (pPage.pBt.mutex.held());

        /* Find the parent page. */
        assert (pCur.iPage > 0);
        assert (pPage.isInit);
        assert (pPage.pDbPage.isWriteable() || pPage.nOverflow == 1);
        pBt = pPage.pBt;
        pParent = pCur.apPage[pCur.iPage - 1];
        assert (pParent != null);

        boolean ignore_clean = false;

        try {

            pParent.pDbPage.write();

            TRACE("BALANCE: begin page %d child of %d\n", pPage.pgno, pParent.pgno);

            /*
             * A special case: If a new entry has just been inserted into a
             * table (that is, a btree with integer keys and all data at the
             * leaves) and the new entry is the right-most entry in the tree (it
             * has the largest key) then use the special balance_quick() routine
             * for balancing. balance_quick() is much faster and results in a
             * tighter packing of data in the common case.
             */
            if (pPage.leaf && pPage.intKey && pPage.nOverflow == 1 && pPage.aOvfl[0].idx == pPage.nCell
                    && pParent.pgno != 1 && get4byte(pParent.aData, pParent.hdrOffset + 8) == pPage.pgno) {
                assert (pPage.intKey);
                ignore_clean = true;
                /*
                 * * TODO: Check the siblings to the left of pPage. It may be
                 * that* they are not full and no new page is required.
                 */
                pCur.balance_quick();
                return;
            }

            pPage.pDbPage.write();

            /*
             * * Find the cell in the parent page whose left child points back
             * to pPage. The "idx" variable is the index of that cell. If pPage
             * is the rightmost child of pParent then set idx to pParent->nCell
             */
            idx = pCur.aiIdx[pCur.iPage - 1];
            pParent.assertParentIndex(idx, pPage.pgno);

            /*
             * Find sibling pages to pPage and the cells in pParent that divide
             * the siblings. An attempt is made to find NN siblings on either
             * side of pPage. More siblings are taken from one side, however, if
             * pPage there are fewer than NN siblings on the other side. If
             * pParent has NB or fewer children then all children of pParent are
             * taken.
             */
            nxDiv = idx - NN;
            if (nxDiv + NB > pParent.nCell) {
                nxDiv = pParent.nCell - NB + 1;
            }
            if (nxDiv < 0) {
                nxDiv = 0;
            }
            nDiv = 0;
            for (i = 0, k = nxDiv; i < NB; i++, k++) {
                if (k < pParent.nCell) {
                    apDiv[i] = pParent.findCell(k);
                    nDiv++;
                    assert (!pParent.leaf);
                    pgnoOld[i] = get4byte(apDiv[i]);
                } else if (k == pParent.nCell) {
                    pgnoOld[i] = get4byte(pParent.aData, pParent.hdrOffset + 8);
                } else {
                    break;
                }
                apOld[i] = pBt.getAndInitPage(pgnoOld[i]);
                /* apOld[i]->idxParent = k; */
                apCopy[i] = null;
                assert (i == nOld);
                nOld++;
                nMaxCells += 1 + apOld[i].nCell + apOld[i].nOverflow;
            }

            /*
             * Make nMaxCells a multiple of 4 in order to preserve 8-byte*
             * alignment
             */
            // nMaxCells = (nMaxCells + 3)&~3;
            /*
             * * Allocate space for memory structures
             */

            apCell = new ISqlJetMemoryPointer[nMaxCells];
            szCell = new int[nMaxCells];
            aSpace1 = SqlJetUtility.allocatePtr(pBt.pageSize);
            if (pBt.autoVacuum) {
                aFrom = SqlJetUtility.allocatePtr(nMaxCells);
            }
            aSpace2 = SqlJetUtility.allocatePtr(pBt.pageSize);

            /*
             * Make copies of the content of pPage and its siblings into
             * aOld[].The rest of this function will use data from the copies
             * rather that the original pages since the original pages will be
             * in the process of being overwritten.
             */
            for (i = 0; i < nOld; i++) {
                SqlJetMemPage p = apCopy[i] = (SqlJetMemPage) memcpy(apOld[i]);
                p.aData = aCopy[i] = SqlJetUtility.allocatePtr(pBt.pageSize);
                memcpy(p.aData, apOld[i].aData, pBt.pageSize);
            }

            /*
             * Load pointers to all cells on sibling pages and the divider cells
             * into the local apCell[] array. Make copies of the divider cells
             * into space obtained form aSpace1[] and remove the the divider
             * Cells* from pParent.
             * 
             * If the siblings are on leaf pages, then the child pointers of the
             * divider cells are stripped from the cells before they are copied
             * into aSpace1[]. In this way, all cells in apCell[] are without
             * child pointers. If siblings are not leaves, then all cell in
             * apCell[] include child pointers. Either way, all cells in
             * apCell[] are alike.
             * 
             * leafCorrection: 4 if pPage is a leaf. 0 if pPage is not a leaf.
             * leafData: 1 if pPage holds key+data and pParent holds only keys.
             */
            nCell = 0;
            leafCorrection = (pPage.leaf ? 1 : 0) * 4;
            leafData = pPage.hasData;
            for (i = 0; i < nOld; i++) {
                SqlJetMemPage pOld = apCopy[i];
                int limit = pOld.nCell + pOld.nOverflow;
                for (j = 0; j < limit; j++) {
                    assert (nCell < nMaxCells);
                    apCell[nCell] = pOld.findOverflowCell(j);
                    szCell[nCell] = pOld.cellSizePtr(apCell[nCell]);
                    if (pBt.autoVacuum) {
                        int a;
                        SqlJetUtility.putUnsignedByte(aFrom, nCell, (byte) i);
                        assert (i >= 0 && i < 6);
                        for (a = 0; a < pOld.nOverflow; a++) {
                            if (pOld.aOvfl[a].pCell == apCell[nCell]) {
                                SqlJetUtility.putUnsignedByte(aFrom, nCell, (byte) 0xFF);
                                break;
                            }
                        }
                    }
                    nCell++;
                }
                if (i < nOld - 1) {
                    int sz = pParent.cellSizePtr(apDiv[i]);
                    if (leafData) {
                        /*
                         * With the LEAFDATA flag, pParent cells hold only
                         * INTKEYs that are duplicates of keys on the child
                         * pages. We need to remove the divider cells from
                         * pParent, but the dividers cells are not added to
                         * apCell[] because they are duplicates of child cells.
                         */
                        pParent.dropCell(nxDiv, sz);
                    } else {
                        ISqlJetMemoryPointer pTemp;
                        assert (nCell < nMaxCells);
                        szCell[nCell] = sz;
                        pTemp = pointer(aSpace1, iSpace1);
                        iSpace1 += sz;
                        assert (sz <= pBt.pageSize / 4);
                        assert (iSpace1 <= pBt.pageSize);
                        memcpy(pTemp, apDiv[i], sz);
                        apCell[nCell] = pointer(pTemp, leafCorrection);
                        if (pBt.autoVacuum) {
                            SqlJetUtility.putUnsignedByte(aFrom, nCell, (byte) 0xFF);
                        }
                        pParent.dropCell(nxDiv, sz);
                        assert (leafCorrection == 0 || leafCorrection == 4);
                        szCell[nCell] -= leafCorrection;
                        assert (get4byte(pTemp) == pgnoOld[i]);
                        if (!pOld.leaf) {
                            assert (leafCorrection == 0);
                            /*
                             * The right pointer of the child page pOld becomes
                             * the left pointer of the divider cell
                             */
                            memcpy(apCell[nCell], 0, pOld.aData, pOld.hdrOffset + 8, 4);
                        } else {
                            assert (leafCorrection == 4);
                            if (szCell[nCell] < 4) {
                                /* Do not allow any cells smaller than 4 bytes. */
                                szCell[nCell] = 4;
                            }
                        }
                        nCell++;
                    }
                }
            }

            /*
             * Figure out the number of pages needed to hold all nCell cells.
             * Store this number in "k". Also compute szNew[] which is the total
             * size of all cells on the i-th page and cntNew[] which is the
             * index in apCell[] of the cell that divides page i from page i+1.
             * cntNew[k] should equal nCell.
             * 
             * Values computed by this block: <p> k: The total number of sibling
             * pages. </p> <p> szNew[i]: Spaced used on the i-th sibling page.
             * </p> <p> cntNew[i]: Index in apCell[] and szCell[] for the first
             * cell to the right of the i-th sibling page.</p> <p> usableSpace:
             * Number of bytes of space available on each sibling.</p>
             */
            usableSpace = pBt.usableSize - 12 + leafCorrection;
            for (subtotal = k = i = 0; i < nCell; i++) {
                assert (i < nMaxCells);
                subtotal += szCell[i] + 2;
                if (subtotal > usableSpace) {
                    szNew[k] = subtotal - szCell[i];
                    cntNew[k] = i;
                    if (leafData) {
                        i--;
                    }
                    subtotal = 0;
                    k++;
                }
            }
            szNew[k] = subtotal;
            cntNew[k] = nCell;
            k++;

            /*
             * The packing computed by the previous block is biased toward the
             * siblings on the left side. The left siblings are always nearly
             * full, while the right-most sibling might be nearly empty. This
             * block of code attempts to adjust the packing of siblings to get a
             * better balance.
             * 
             * This adjustment is more than an optimization. The packing above
             * might be so out of balance as to be illegal. For example, the
             * right-most sibling might be completely empty. This adjustment is
             * not optional.
             */
            for (i = k - 1; i > 0; i--) {
                int szRight = szNew[i]; /* Size of sibling on the right */
                int szLeft = szNew[i - 1]; /* Size of sibling on the left */
                int r; /* Index of right-most cell in left sibling */
                int d; /* Index of first cell to the left of right sibling */

                r = cntNew[i - 1] - 1;
                d = r + 1 - (leafData ? 1 : 0);
                assert (d < nMaxCells);
                assert (r < nMaxCells);
                while (szRight == 0 || szRight + szCell[d] + 2 <= szLeft - (szCell[r] + 2)) {
                    szRight += szCell[d] + 2;
                    szLeft -= szCell[r] + 2;
                    cntNew[i - 1]--;
                    r = cntNew[i - 1] - 1;
                    d = r + 1 - (leafData ? 1 : 0);
                }
                szNew[i] = szRight;
                szNew[i - 1] = szLeft;
            }

            /*
             * Either we found one or more cells (cntnew[0])>0) or we are the* a
             * virtual root page. A virtual root page is when the real root*
             * page is page 1 and we are the only child of that page.
             */
            assert (cntNew[0] > 0 || (pParent.pgno == 1 && pParent.nCell == 0));

            /*
             * * Allocate k new pages. Reuse old pages where possible.
             */
            assert (pPage.pgno > 1);
            pageFlags = SqlJetUtility.getUnsignedByte(pPage.aData, 0);
            int[] ipgnoNew = new int[1];
            for (i = 0; i < k; i++) {
                SqlJetMemPage pNew;
                if (i < nOld) {
                    pNew = apNew[i] = apOld[i];
                    pgnoNew[i] = pgnoOld[i];
                    apOld[i] = null;
                    pNew.pDbPage.write();
                    nNew++;
                } else {
                    assert (i > 0);
                    ipgnoNew[0] = pgnoNew[i];
                    pNew = pBt.allocatePage(ipgnoNew, pgnoNew[i - 1], false);
                    pgnoNew[i] = ipgnoNew[0];
                    apNew[i] = pNew;
                    nNew++;
                }
            }

            /*
             * Free any old pages that were not reused as new pages.
             */
            while (i < nOld) {
                apOld[i].freePage();
                SqlJetMemPage.releasePage(apOld[i]);
                apOld[i] = null;
                i++;
            }

            /*
             * Put the new pages in accending order. This helps to keep entries
             * in the disk file in order so that a scan of the table is a linear
             * scan through the file. That in turn helps the operating system to
             * deliver pages from the disk more rapidly.
             * 
             * An O(n^2) insertion sort algorithm is used, but since n is never
             * more than NB (a small constant), that should not be a problem.
             * 
             * When NB==3, this one optimization makes the database about 25%
             * faster for large insertions and deletions.
             */
            for (i = 0; i < k - 1; i++) {
                int minV = pgnoNew[i];
                int minI = i;
                for (j = i + 1; j < k; j++) {
                    if (pgnoNew[j] < minV) {
                        minI = j;
                        minV = pgnoNew[j];
                    }
                }
                if (minI > i) {
                    int t;
                    SqlJetMemPage pT;
                    t = pgnoNew[i];
                    pT = apNew[i];
                    pgnoNew[i] = pgnoNew[minI];
                    apNew[i] = apNew[minI];
                    pgnoNew[minI] = t;
                    apNew[minI] = pT;
                }
            }
            TRACE("BALANCE: old: %d %d %d  new: %d(%d) %d(%d) %d(%d) %d(%d) %d(%d)\n", pgnoOld[0],
                    nOld >= 2 ? pgnoOld[1] : 0, nOld >= 3 ? pgnoOld[2] : 0, pgnoNew[0], szNew[0],
                    nNew >= 2 ? pgnoNew[1] : 0, nNew >= 2 ? szNew[1] : 0, nNew >= 3 ? pgnoNew[2] : 0,
                    nNew >= 3 ? szNew[2] : 0, nNew >= 4 ? pgnoNew[3] : 0, nNew >= 4 ? szNew[3] : 0,
                    nNew >= 5 ? pgnoNew[4] : 0, nNew >= 5 ? szNew[4] : 0);

            /*
             * Evenly distribute the data in apCell[] across the new pages.
             * Insert divider cells into pParent as necessary.
             */
            j = 0;
            for (i = 0; i < nNew; i++) {
                /* Assemble the new sibling page. */
                SqlJetMemPage pNew = apNew[i];
                assert (j < nMaxCells);
                assert (pNew.pgno == pgnoNew[i]);
                pNew.zeroPage(pageFlags);
                pNew.assemblePage(cntNew[i] - j, apCell, j, szCell, j);
                assert (pNew.nCell > 0 || (nNew == 1 && cntNew[0] == 0));
                assert (pNew.nOverflow == 0);

                /*
                 * If this is an auto-vacuum database, update the pointer map
                 * entries that point to the siblings that were rearranged.
                 * These can be: left children of cells, the right-child of the
                 * page, or overflow pages pointed to by cells.
                 */
                if (pBt.autoVacuum) {
                    for (k = j; k < cntNew[i]; k++) {
                        assert (k < nMaxCells);
                        if (SqlJetUtility.getUnsignedByte(aFrom, k) == 0xFF
                                || apCopy[SqlJetUtility.getUnsignedByte(aFrom, k)].pgno != pNew.pgno) {
                            pNew.ptrmapPutOvfl(k - j);
                            if (leafCorrection == 0) {
                                pBt.ptrmapPut(get4byte(apCell[k]), SqlJetBtreeShared.PTRMAP_BTREE, pNew.pgno);
                            }
                        }
                    }
                }

                j = cntNew[i];

                /*
                 * If the sibling page assembled above was not the right-most
                 * sibling, insert a divider cell into the parent page.
                 */
                if (i < nNew - 1 && j < nCell) {
                    ISqlJetMemoryPointer pCell;
                    ISqlJetMemoryPointer pTemp;
                    int sz;

                    assert (j < nMaxCells);
                    pCell = apCell[j];
                    sz = szCell[j] + leafCorrection;
                    pTemp = pointer(aSpace2, iSpace2);
                    if (!pNew.leaf) {
                        memcpy(pNew.aData, 8, pCell, 0, 4);
                        if (pBt.autoVacuum
                                && (SqlJetUtility.getUnsignedByte(aFrom, j) == 0xFF || apCopy[SqlJetUtility
                                        .getUnsignedByte(aFrom, j)].pgno != pNew.pgno)) {
                            pBt.ptrmapPut(get4byte(pCell), SqlJetBtreeShared.PTRMAP_BTREE, pNew.pgno);
                        }
                    } else if (leafData) {
                        /*
                         * If the tree is a leaf-data tree, and the siblings are
                         * leaves, then there is no divider cell in apCell[].
                         * Instead, the divider cell consists of the integer key
                         * for the right-most cell of the sibling-page assembled
                         * above only.
                         */
                        j--;
                        SqlJetBtreeCellInfo info = pNew.parseCellPtr(apCell[j]);
                        pCell = pTemp;
                        sz = pParent.fillInCell(pCell, null, info.nKey, null, 0, 0);
                        pTemp = null;
                    } else {
                        final int c = pCell.getBuffer().getSize() - pCell.getPointer();
                        ISqlJetMemoryPointer p = SqlJetUtility.allocatePtr(c + 4);
                        movePtr(p, 4);
                        SqlJetUtility.memcpy(p, pCell, c);
                        pCell = p;
                        movePtr(pCell, -4);
                        /*
                         * Obscure case for non-leaf-data trees: If the cell at
                         * pCell was previously stored on a leaf node, and its
                         * reported size was 4 bytes, then it may actually be
                         * smaller than this (see sqlite3BtreeParseCellPtr(), 4
                         * bytes is the minimum size of any cell). But it is
                         * important to pass the correct size to insertCell(),
                         * so reparse the cell now.
                         * 
                         * Note that this can never happen in an SQLite data
                         * file, as all cells are at least 4 bytes. It only
                         * happens in b-trees used to evaluate "IN (SELECT ...)"
                         * and similar clauses.
                         */
                        if (szCell[j] == 4) {
                            assert (leafCorrection == 4);
                            sz = pParent.cellSizePtr(pCell);
                        }
                    }
                    iSpace2 += sz;
                    assert (sz <= pBt.pageSize / 4);
                    assert (iSpace2 <= pBt.pageSize);
                    pParent.insertCell(nxDiv, pCell, sz, pTemp, (byte) 4);
                    assert (pParent.pDbPage.isWriteable());
                    put4byte(pParent.findOverflowCell(nxDiv), pNew.pgno);

                    /*
                     * If this is an auto-vacuum database, and not a leaf-data
                     * tree, then update the pointer map with an entry for the
                     * overflow page that the cell just inserted points to (if
                     * any).
                     */
                    if (pBt.autoVacuum && !leafData) {
                        pParent.ptrmapPutOvfl(nxDiv);
                    }
                    j++;
                    nxDiv++;
                }

                /* Set the pointer-map entry for the new sibling page. */
                if (pBt.autoVacuum) {
                    pBt.ptrmapPut(pNew.pgno, SqlJetBtreeShared.PTRMAP_BTREE, pParent.pgno);
                }
            }
            assert (j == nCell);
            assert (nOld > 0);
            assert (nNew > 0);
            if ((pageFlags & SqlJetMemPage.PTF_LEAF) == 0) {
                final ISqlJetMemoryPointer zChild = pointer(apCopy[nOld - 1].aData, 8);
                memcpy(apNew[nNew - 1].aData, 8, zChild, 0, 4);
                if (pBt.autoVacuum) {
                    pBt.ptrmapPut(get4byte(zChild), SqlJetBtreeShared.PTRMAP_BTREE, apNew[nNew - 1].pgno);
                }
            }
            assert (pParent.pDbPage.isWriteable());
            if (nxDiv == pParent.nCell + pParent.nOverflow) {
                /* Right-most sibling is the right-most child of pParent */
                put4byte(pParent.aData, pParent.hdrOffset + 8, pgnoNew[nNew - 1]);
            } else {
                /*
                 * Right-most sibling is the left child of the first entry in
                 * pParent past the right-most divider entry
                 */
                put4byte(pParent.findOverflowCell(nxDiv), pgnoNew[nNew - 1]);
            }

            /*
             * Balance the parent page. Note that the current page (pPage) might
             * have been added to the freelist so it might no longer be
             * initialized. But the parent page will always be initialized.
             */
            assert (pParent.isInit);
            // sqlite3ScratchFree(apCell);
            apCell = null;
            TRACE("BALANCE: finished with %d: old=%d new=%d cells=%d\n", pPage.pgno, nOld, nNew, nCell);
            pPage.nOverflow = 0;
            SqlJetMemPage.releasePage(pPage);
            pCur.iPage--;
            pCur.balance(false);

        } finally {

            if (ignore_clean)
                return;

            /*
             * Cleanup before returning.
             */

            // balance_cleanup:
            // sqlite3PageFree(aSpace2);
            // sqlite3ScratchFree(apCell);
            for (i = 0; i < nOld; i++) {
                SqlJetMemPage.releasePage(apOld[i]);
            }
            for (i = 0; i < nNew; i++) {
                SqlJetMemPage.releasePage(apNew[i]);
            }
            pCur.apPage[pCur.iPage].nOverflow = 0;

        }

    }

    /**
     * This version of balance() handles the common special case where a new
     * entry is being inserted on the extreme right-end of the tree, in other
     * words, when the new entry will become the largest entry in the tree.
     * 
     * Instead of trying balance the 3 right-most leaf pages, just add a new
     * page to the right-hand side and put the one new entry in that page. This
     * leaves the right side of the tree somewhat unbalanced. But odds are that
     * we will be inserting new entries at the end soon afterwards so the nearly
     * empty page will quickly fill up. On average.
     * 
     * pPage is the leaf page which is the right-most page in the tree. pParent
     * is its parent. pPage must have a single overflow entry which is also the
     * right-most entry on the page.
     * 
     * @throws SqlJetException
     */
    private void balance_quick() throws SqlJetException {
        SqlJetBtreeCursor pCur = this;

        SqlJetMemPage pNew = null;
        int[] pgnoNew = new int[1];
        ISqlJetMemoryPointer pCell;
        int szCell;
        SqlJetBtreeCellInfo info = new SqlJetBtreeCellInfo();
        SqlJetMemPage pPage = pCur.apPage[pCur.iPage];
        SqlJetMemPage pParent = pCur.apPage[pCur.iPage - 1];
        SqlJetBtreeShared pBt = pPage.pBt;
        /* pParent new divider cell index */
        int parentIdx = pParent.nCell;
        /* Size of new divider cell */
        int parentSize;
        /* Space for the new divider cell */
        ISqlJetMemoryPointer parentCell = SqlJetUtility.allocatePtr(64);

        assert (pPage.pBt.mutex.held());

        try {
            /*
             * Allocate a new page. Insert the overflow cell from pPage* into
             * it. Then remove the overflow cell from pPage.
             */
            pNew = pBt.allocatePage(pgnoNew, 0, false);

            pCell = pPage.aOvfl[0].pCell;
            szCell = pPage.cellSizePtr(pCell);
            assert (pNew.pDbPage.isWriteable());
            pNew.zeroPage(SqlJetUtility.getUnsignedByte(pPage.aData, 0));
            pNew.assemblePage(1, new ISqlJetMemoryPointer[] { pCell }, new int[] { szCell });
            pPage.nOverflow = 0;

            /*
             * pPage is currently the right-child of pParent. Change this* so
             * that the right-child is the new page allocated above and* pPage
             * is the next-to-right child.** Ignore the return value of the call
             * to fillInCell(). fillInCell()* may only return other than
             * SQLITE_OK if it is required to allocate* one or more overflow
             * pages. Since an internal table B-Tree cell* may never spill over
             * onto an overflow page (it is a maximum of* 13 bytes in size), it
             * is not neccessary to check the return code.** Similarly, the
             * insertCell() function cannot fail if the page* being inserted
             * into is already writable and the cell does not* contain an
             * overflow pointer. So ignore this return code too.
             */
            assert (pPage.nCell > 0);
            pCell = pPage.findCell(pPage.nCell - 1);
            info = pPage.parseCellPtr(pCell);
            parentSize = pParent.fillInCell(parentCell, null, info.nKey, null, 0, 0);
            assert (parentSize < 64);
            assert (pParent.pDbPage.isWriteable());
            pParent.insertCell(parentIdx, parentCell, parentSize, null, (byte) 4);
            put4byte(pParent.findOverflowCell(parentIdx), pPage.pgno);
            put4byte(pParent.aData, pParent.hdrOffset + 8, pgnoNew[0]);

            /*
             * If this is an auto-vacuum database, update the pointer map* with
             * entries for the new page, and any pointer from the* cell on the
             * page to an overflow page.
             */
            if (pBt.autoVacuum) {
                pBt.ptrmapPut(pgnoNew[0], SqlJetBtreeShared.PTRMAP_BTREE, pParent.pgno);
                pNew.ptrmapPutOvfl(0);
            }

            /* Release the reference to the new page. */
            SqlJetMemPage.releasePage(pNew);

        } finally {

            /*
             * At this point the pPage->nFree variable is not set correctly with
             * * respect to the content of the page (because it was set to 0 by*
             * insertCell). So call sqlite3BtreeInitPage() to make sure it is*
             * correct.** This has to be done even if an error will be returned.
             * Normally, if* an error occurs during tree balancing, the contents
             * of MemPage are* not important, as they will be recalculated when
             * the page is rolled* back. But here, in balance_quick(), it is
             * possible that pPage has* not yet been marked dirty or written
             * into the journal file. Therefore* it will not be rolled back and
             * so it is important to make sure that* the page data and contents
             * of MemPage are consistent.
             */
            pPage.isInit = false;
            pPage.initPage();
            assert (pPage.nOverflow == 0);

        }

        /*
         * If everything else succeeded, balance the parent page, in* case the
         * divider cell inserted caused it to become overfull.
         */
        SqlJetMemPage.releasePage(pPage);
        pCur.iPage--;
        pCur.balance(false);

    }

    /**
     * This routine is called for the root page of a btree when the root page
     * contains no cells. This is an opportunity to make the tree shallower by
     * one level.
     * 
     * @throws SqlJetException
     * 
     */
    private void balance_shallower() throws SqlJetException {

        final SqlJetBtreeCursor pCur = this;

        SqlJetMemPage pPage; /* Root page of B-Tree */
        SqlJetMemPage pChild; /* The only child page of pPage */
        int pgnoChild; /* Page number for pChild */
        SqlJetBtreeShared pBt; /* The main BTree structure */
        int mxCellPerPage; /* Maximum number of cells per page */
        ISqlJetMemoryPointer[] apCell; /* All cells from pages being balanced */
        int[] szCell; /* Local size of all cells */

        assert (pCur.iPage == 0);
        pPage = pCur.apPage[0];

        assert (pPage.nCell == 0);
        assert (pPage.pBt.mutex.held());
        pBt = pPage.pBt;
        mxCellPerPage = pBt.MX_CELL();
        apCell = new ISqlJetMemoryPointer[mxCellPerPage];
        szCell = new int[mxCellPerPage];
        if (pPage.leaf) {
            /* The table is completely empty */
            TRACE("BALANCE: empty table %d\n", pPage.pgno);
        } else {
            /*
             * The root page is empty but has one child. Transfer the*
             * information from that one child into the root page if it* will
             * fit. This reduces the depth of the tree by one.** If the root
             * page is page 1, it has less space available than* its child (due
             * to the 100 byte header that occurs at the beginning* of the
             * database fle), so it might not be able to hold all of the*
             * information currently contained in the child. If this is the*
             * case, then do not do the transfer. Leave page 1 empty except* for
             * the right-pointer to the child page. The child page becomes* the
             * virtual root of the tree.
             */
            // VVA_ONLY( pCur->pagesShuffled = 1 );
            pgnoChild = get4byte(pPage.aData, pPage.hdrOffset + 8);
            assert (pgnoChild > 0);
            assert (pgnoChild <= pPage.pBt.getPageCount());
            pChild = pPage.pBt.getPage(pgnoChild, false);
            if (pPage.pgno == 1) {
                pChild.initPage();
                assert (pChild.nOverflow == 0);
                if (pChild.nFree >= 100) {
                    /*
                     * The child information will fit on the root page, so do
                     * the* copy
                     */
                    int i;
                    pPage.zeroPage(SqlJetUtility.getUnsignedByte(pChild.aData, 0));
                    for (i = 0; i < pChild.nCell; i++) {
                        apCell[i] = pChild.findCell(i);
                        szCell[i] = pChild.cellSizePtr(apCell[i]);
                    }
                    pPage.assemblePage(pChild.nCell, apCell, szCell);
                    /* Copy the right-pointer of the child to the parent. */
                    assert (pPage.pDbPage.isWriteable());
                    put4byte(pPage.aData, pPage.hdrOffset + 8, get4byte(pChild.aData, pChild.hdrOffset + 8));
                    pChild.freePage();
                    TRACE("BALANCE: child %d transfer to page 1\n", pChild.pgno);
                } else {
                    /*
                     * The child has more information that will fit on the root.
                     * * The tree is already balanced. Do nothing.
                     */
                    TRACE("BALANCE: child %d will not fit on page 1\n", pChild.pgno);
                }
            } else {
                memcpy(pPage.aData, pChild.aData, pPage.pBt.usableSize);
                pPage.isInit = false;
                pPage.initPage();
                pChild.freePage();
                TRACE("BALANCE: transfer child %d into root %d\n", pChild.pgno, pPage.pgno);
            }
            assert (pPage.nOverflow == 0);
            if (pBt.autoVacuum) {
                pPage.setChildPtrmaps();
            }
            SqlJetMemPage.releasePage(pChild);
        }
    }

    /**
     * The root page is overfull
     * 
     * When this happens, Create a new child page and copy the contents of the
     * root into the child. Then make the root page an empty page with
     * rightChild pointing to the new child. Finally, call balance_internal() on
     * the new child to cause it to split.
     * 
     * @throws SqlJetException
     * 
     */
    private void balance_deeper() throws SqlJetException {

        final SqlJetBtreeCursor pCur = this;

        SqlJetMemPage pPage; /* Pointer to the root page */
        SqlJetMemPage pChild; /* Pointer to a new child page */
        int[] pgnoChild = new int[1]; /* Page number of the new child page */
        SqlJetBtreeShared pBt; /* The BTree */
        int usableSize; /* Total usable size of a page */
        ISqlJetMemoryPointer data; /* Content of the parent page */
        ISqlJetMemoryPointer cdata; /* Content of the child page */
        int hdr; /* Offset to page header in parent */
        int cbrk; /* Offset to content of first cell in parent */

        assert (pCur.iPage == 0);
        assert (pCur.apPage[0].nOverflow > 0);

        pPage = pCur.apPage[0];
        pBt = pPage.pBt;
        assert (pBt.mutex.held());
        assert (pPage.pDbPage.isWriteable());
        pChild = pBt.allocatePage(pgnoChild, pPage.pgno, false);
        assert (pChild.pDbPage.isWriteable());
        usableSize = pBt.usableSize;
        data = pPage.aData;
        hdr = pPage.hdrOffset;
        cbrk = get2byte(data, hdr + 5);
        cdata = pChild.aData;
        memcpy(cdata, 0, data, hdr, pPage.cellOffset + 2 * pPage.nCell - hdr);
        memcpy(cdata, cbrk, data, cbrk, usableSize - cbrk);

        try {

            assert (!pChild.isInit);
            pChild.initPage();
            int nCopy = pPage.nOverflow;
            memcpy(pChild.aOvfl, pPage.aOvfl, nCopy);
            pChild.nOverflow = pPage.nOverflow;
            if (pChild.nOverflow != 0) {
                pChild.nFree = 0;
            }
            assert (pChild.nCell == pPage.nCell);
            assert (pPage.pDbPage.isWriteable());
            pPage.zeroPage(SqlJetUtility.getUnsignedByte(pChild.aData, 0) & ~SqlJetMemPage.PTF_LEAF);
            put4byte(pPage.aData, pPage.hdrOffset + 8, pgnoChild[0]);
            TRACE("BALANCE: copy root %d into %d\n", pPage.pgno, pChild.pgno);
            if (pBt.autoVacuum) {
                pBt.ptrmapPut(pChild.pgno, SqlJetBtreeShared.PTRMAP_BTREE, pPage.pgno);
                try {
                    pChild.setChildPtrmaps();
                } catch (SqlJetException e) {
                    pChild.nOverflow = 0;
                    throw e;
                }
            }

        } catch (SqlJetException e) {
            SqlJetMemPage.releasePage(pChild);
            throw e;
        }

        pCur.iPage++;
        pCur.apPage[1] = pChild;
        pCur.aiIdx[0] = 0;
        pCur.balance_nonroot();

    }

    /**
     * Make a temporary cursor by filling in the fields of pTempCur. The
     * temporary cursor is not on the cursor list for the Btree.
     * 
     * @return
     * @throws SqlJetException
     */
    private SqlJetBtreeCursor getTempCursor() throws SqlJetException {
        SqlJetBtreeCursor pCur = this;
        assert (cursorHoldsMutex(pCur));
        SqlJetBtreeCursor pTempCur;
        try {
            pTempCur = (SqlJetBtreeCursor) this.clone();
        } catch (CloneNotSupportedException e) {
            throw new SqlJetException(SqlJetErrorCode.INTERNAL);
        }
        pTempCur.pNext = null;
        pTempCur.pPrev = null;
        for (int i = 0; i <= pTempCur.iPage; i++) {
            pTempCur.apPage[i].pDbPage.ref();
        }
        assert (pTempCur.pKey == null);
        return pTempCur;
    }

    /**
     * Delete a temporary cursor such as was made by the CreateTemporaryCursor()
     * function above.
     * 
     * @throws SqlJetException
     */
    void releaseTempCursor() throws SqlJetException {
        SqlJetBtreeCursor pCur = this;
        assert (cursorHoldsMutex(pCur));
        for (int i = 0; i <= pCur.iPage; i++) {
            pCur.apPage[i].pDbPage.unref();
        }
        pCur.pKey = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#insert(byte[], long,
     * byte[], int, int, boolean)
     */
    public void insert(ISqlJetMemoryPointer pKey, long nKey, ISqlJetMemoryPointer pData, int nData, int zero,
            boolean bias) throws SqlJetException {

        final SqlJetBtreeCursor pCur = this;

        int loc;
        int szNew;
        int idx;
        SqlJetMemPage pPage;
        SqlJetBtree p = pCur.pBtree;
        SqlJetBtreeShared pBt = p.pBt;
        ISqlJetMemoryPointer oldCell;
        ISqlJetMemoryPointer newCell = null;

        assert (cursorHoldsMutex(pCur));
        assert (pBt.inTransaction == TransMode.WRITE);
        assert (!pBt.readOnly);
        assert (pCur.wrFlag);
        if (pCur.pBtree.checkReadLocks(pCur.pgnoRoot, pCur, nKey)) {
            /* The table pCur points to has a read lock */
            throw new SqlJetException(SqlJetErrorCode.LOCKED);
        }
        if (pCur.eState == CursorState.FAULT) {
            throw new SqlJetException(pCur.error);
        }

        /*
         * Save the positions of any other cursors open on this table.
         * 
         * In some cases, the call to sqlite3BtreeMoveto() below is a no-op. For
         * example, when inserting data into a table with auto-generated integer
         * keys, the VDBE layer invokes sqlite3BtreeLast() to figure out the
         * integer key to use. It then calls this function to actually insert
         * the data into the intkey B-Tree. In this case sqlite3BtreeMoveto()
         * recognizes that the cursor is already where it needs to be and
         * returns without doing any work. To avoid thwarting these
         * optimizations, it is important not to clear the cursor here.
         */
        pBt.saveAllCursors(pCur.pgnoRoot, pCur);
        loc = pCur.moveTo(pKey, nKey, bias);
        assert (pCur.eState == CursorState.VALID || (pCur.eState == CursorState.INVALID && loc != 0));

        pPage = pCur.apPage[pCur.iPage];
        assert (pPage.intKey || nKey >= 0);
        assert (pPage.leaf || !pPage.intKey);
        TRACE("INSERT: table=%d nkey=%d ndata=%b page=%d %s\n", pCur.pgnoRoot, nKey, pData, pPage.pgno,
                loc == 0 ? "overwrite" : "new entry");
        assert (pPage.isInit);
        pBt.allocateTempSpace();
        newCell = pBt.pTmpSpace;
        szNew = pPage.fillInCell(newCell, pKey, nKey, pData, nData, zero);
        assert (szNew == pPage.cellSizePtr(newCell));
        assert (szNew <= pBt.MX_CELL_SIZE());
        idx = pCur.aiIdx[pCur.iPage];
        if (loc == 0 && CursorState.VALID == pCur.eState) {
            int szOld;
            assert (idx < pPage.nCell);
            pPage.pDbPage.write();
            oldCell = pPage.findCell(idx);
            if (!pPage.leaf) {
                memcpy(newCell, oldCell, 4);
            }
            szOld = pPage.cellSizePtr(oldCell);
            pPage.clearCell(oldCell);
            pPage.dropCell(idx, szOld);
        } else if (loc < 0 && pPage.nCell > 0) {
            assert (pPage.leaf);
            idx = ++pCur.aiIdx[pCur.iPage];
        } else {
            assert (pPage.leaf);
        }

        try {
            pPage.insertCell(idx, newCell, szNew, null, (byte) 0);

            assert (pPage.nCell > 0 || pPage.nOverflow > 0);

        } finally {
            /*
             * If no error has occured and pPage has an overflow cell, call
             * balance() to redistribute the cells within the tree. Since
             * balance() may move the cursor, zero the BtCursor.info.nSize and
             * BtCursor.validNKey variables.
             * 
             * Previous versions of SQLite called moveToRoot() to move the
             * cursor back to the root page as balance() used to invalidate the
             * contents of BtCursor.apPage[] and BtCursor.aiIdx[]. Instead of
             * doing that, set the cursor state to "invalid". This makes common
             * insert operations slightly faster.
             * 
             * There is a subtle but important optimization here too. When
             * inserting multiple records into an intkey b-tree using a single
             * cursor (as can happen while processing an
             * "INSERT INTO ... SELECT" statement), it is advantageous to leave
             * the cursor pointing to the last entry in the b-tree if possible.
             * If the cursor is left pointing to the last entry in the table,
             * and the next row inserted has an integer key larger than the
             * largest existing key, it is possible to insert the row without
             * seeking the cursor. This can be a big performance boost.
             */
            pCur.info.nSize = 0;
            pCur.validNKey = false;
        }

        if (pPage.nOverflow > 0) {

            try {
                pCur.balance(true);
            } finally {
                /*
                 * Must make sure nOverflow is reset to zero even if the
                 * balance() fails. Internal data structure corruption will
                 * result otherwise. Also, set the cursor state to invalid. This
                 * stops saveCursorPosition() from trying to save the current
                 * position of the cursor.
                 */
                pCur.apPage[pCur.iPage].nOverflow = 0;
                pCur.eState = CursorState.INVALID;
            }
        }
        assert (pCur.apPage[pCur.iPage].nOverflow == 0);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#first()
     */
    public boolean first() throws SqlJetException {
        final SqlJetBtreeCursor pCur = this;
        assert (cursorHoldsMutex(pCur));
        assert (pCur.pBtree.db.getMutex().held());
        pCur.moveToRoot();
        if (pCur.eState == CursorState.INVALID) {
            assert (pCur.apPage[pCur.iPage].nCell == 0);
            return true;
        } else {
            assert (pCur.apPage[pCur.iPage].nCell > 0);
            pCur.moveToLeftmost();
            return false;
        }
    }

    /**
     * Move the cursor down to the left-most leaf entry beneath the entry to
     * which it is currently pointing.
     * 
     * The left-most leaf is the one with the smallest key - the first in
     * ascending order.
     * 
     * @throws SqlJetException
     */
    private void moveToLeftmost() throws SqlJetException {
        final SqlJetBtreeCursor pCur = this;
        int pgno;
        SqlJetMemPage pPage;
        assert (cursorHoldsMutex(pCur));
        assert (pCur.eState == CursorState.VALID);
        while (!(pPage = pCur.apPage[pCur.iPage]).leaf) {
            assert (pCur.aiIdx[pCur.iPage] < pPage.nCell);
            pgno = get4byte(pPage.findCell(pCur.aiIdx[pCur.iPage]));
            pCur.moveToChild(pgno);
        }
    }

    /**
     * Move the cursor down to the right-most leaf entry beneath the page to
     * which it is currently pointing. Notice the difference between
     * moveToLeftmost() and moveToRightmost(). moveToLeftmost() finds the
     * left-most entry beneath the *entry* whereas moveToRightmost() finds the
     * right-most entry beneath the *page*.
     * 
     * The right-most entry is the one with the largest key - the last key in
     * ascending order.
     * 
     * @throws SqlJetException
     */
    private void moveToRightmost() throws SqlJetException {
        final SqlJetBtreeCursor pCur = this;
        int pgno;
        SqlJetMemPage pPage = null;
        assert (cursorHoldsMutex(pCur));
        assert (pCur.eState == CursorState.VALID);
        while (!(pPage = pCur.apPage[pCur.iPage]).leaf) {
            pgno = get4byte(pPage.aData, pPage.hdrOffset + 8);
            pCur.aiIdx[pCur.iPage] = pPage.nCell;
            pCur.moveToChild(pgno);
        }
        pCur.aiIdx[pCur.iPage] = pPage.nCell - 1;
        pCur.info.nSize = 0;
        pCur.validNKey = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#last()
     */
    public boolean last() throws SqlJetException {
        final SqlJetBtreeCursor pCur = this;
        assert (cursorHoldsMutex(pCur));
        assert (pCur.pBtree.db.getMutex().held());
        pCur.moveToRoot();
        if (CursorState.INVALID == pCur.eState) {
            assert (pCur.apPage[pCur.iPage].nCell == 0);
            return true;
        } else {
            assert (pCur.eState == CursorState.VALID);
            try {
                pCur.moveToRightmost();
            } catch (SqlJetException e) {
                pCur.atLast = false;
                throw e;
            } finally {
                pCur.getCellInfo();
            }
            pCur.atLast = true;
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#next()
     */
    public boolean next() throws SqlJetException {
        final SqlJetBtreeCursor pCur = this;

        int idx;
        SqlJetMemPage pPage;

        assert (cursorHoldsMutex(pCur));
        pCur.restoreCursorPosition();
        if (CursorState.INVALID == pCur.eState) {
            return true;
        }
        if (pCur.skip > 0) {
            pCur.skip = 0;
            return false;
        }
        pCur.skip = 0;

        pPage = pCur.apPage[pCur.iPage];
        idx = ++pCur.aiIdx[pCur.iPage];
        assert (pPage.isInit);
        assert (idx <= pPage.nCell);

        pCur.info.nSize = 0;
        pCur.validNKey = false;
        if (idx >= pPage.nCell) {
            if (!pPage.leaf) {
                pCur.moveToChild(get4byte(pPage.aData, pPage.hdrOffset + 8));
                pCur.moveToLeftmost();
                return false;
            }
            do {
                if (pCur.iPage == 0) {
                    pCur.eState = CursorState.INVALID;
                    return true;
                }
                pCur.moveToParent();
                pPage = pCur.apPage[pCur.iPage];
            } while (pCur.aiIdx[pCur.iPage] >= pPage.nCell);
            if (pPage.intKey) {
                return pCur.next();
            }
            return false;
        }
        if (pPage.leaf) {
            return false;
        }
        pCur.moveToLeftmost();
        return false;
    }

    /**
     * Move the cursor up to the parent page.
     * 
     * pCur->idx is set to the cell index that contains the pointer to the page
     * we are coming from. If we are coming from the right-most child page then
     * pCur->idx is set to one more than the largest cell index.
     * 
     * @throws SqlJetException
     * 
     */
    private void moveToParent() throws SqlJetException {
        final SqlJetBtreeCursor pCur = this;
        assert (cursorHoldsMutex(pCur));
        assert (pCur.eState == CursorState.VALID);
        assert (pCur.iPage > 0);
        assert (pCur.apPage[pCur.iPage] != null);
        pCur.apPage[pCur.iPage - 1].assertParentIndex(pCur.aiIdx[pCur.iPage - 1], pCur.apPage[pCur.iPage].pgno);
        SqlJetMemPage.releasePage(pCur.apPage[pCur.iPage]);
        pCur.iPage--;
        pCur.info.nSize = 0;
        pCur.validNKey = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#previous()
     */
    public boolean previous() throws SqlJetException {
        final SqlJetBtreeCursor pCur = this;

        SqlJetMemPage pPage;

        assert (cursorHoldsMutex(pCur));
        pCur.restoreCursorPosition();
        pCur.atLast = false;
        if (CursorState.INVALID == pCur.eState) {
            return true;
        }
        if (pCur.skip < 0) {
            pCur.skip = 0;
            return false;
        }
        pCur.skip = 0;

        pPage = pCur.apPage[pCur.iPage];
        assert (pPage.isInit);
        if (!pPage.leaf) {
            int idx = pCur.aiIdx[pCur.iPage];
            pCur.moveToChild(get4byte(pPage.findCell(idx)));
            pCur.moveToRightmost();
        } else {
            while (pCur.aiIdx[pCur.iPage] == 0) {
                if (pCur.iPage == 0) {
                    pCur.eState = CursorState.INVALID;
                    return true;
                }
                pCur.moveToParent();
            }
            pCur.info.nSize = 0;
            pCur.validNKey = false;

            pCur.aiIdx[pCur.iPage]--;
            pPage = pCur.apPage[pCur.iPage];
            if (pPage.intKey && !pPage.leaf) {
                return pCur.previous();
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#eof()
     */
    public boolean eof() {
        final SqlJetBtreeCursor pCur = this;
        /*
         * TODO: What if the cursor is in CURSOR_REQUIRESEEK but all table
         * entries* have been deleted? This API will need to change to return an
         * error code* as well as the boolean result value.
         */
        return (CursorState.VALID != pCur.eState);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#flags()
     */
    public short flags() throws SqlJetException {
        final SqlJetBtreeCursor pCur = this;
        /*
         * TODO: What about CURSOR_REQUIRESEEK state? Probably need to call*
         * restoreCursorPosition() here.
         */
        SqlJetMemPage pPage;
        pCur.restoreCursorPosition();
        pPage = pCur.apPage[pCur.iPage];
        assert (cursorHoldsMutex(pCur));
        assert (pPage != null);
        assert (pPage.pBt == pCur.pBt);
        return (short) SqlJetUtility.getUnsignedByte(pPage.aData, pPage.hdrOffset);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#getKeySize()
     */
    public long getKeySize() throws SqlJetException {
        final SqlJetBtreeCursor pCur = this;
        assert (cursorHoldsMutex(pCur));
        pCur.restoreCursorPosition();
        assert (pCur.eState == CursorState.INVALID || pCur.eState == CursorState.VALID);
        if (pCur.eState == CursorState.INVALID) {
            return 0;
        } else {
            pCur.getCellInfo();
            return pCur.info.nKey;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#key(int, int, byte[])
     */
    public void key(int offset, int amt, ISqlJetMemoryPointer buf) throws SqlJetException {
        final SqlJetBtreeCursor pCur = this;
        assert (cursorHoldsMutex(pCur));
        pCur.restoreCursorPosition();
        assert (pCur.eState == CursorState.VALID);
        assert (pCur.iPage >= 0 && pCur.apPage[pCur.iPage] != null);
        if (pCur.apPage[0].intKey) {
            throw new SqlJetException(SqlJetErrorCode.CORRUPT);
        }
        assert (pCur.aiIdx[pCur.iPage] < pCur.apPage[pCur.iPage].nCell);
        pCur.accessPayload(offset, amt, buf, 0, false);
    }

    /**
     * This function is used to read or overwrite payload information for the
     * entry that the pCur cursor is pointing to. If the eOp parameter is 0,
     * this is a read operation (data copied into buffer pBuf). If it is
     * non-zero, a write (data copied from buffer pBuf).
     * 
     * A total of "amt" bytes are read or written beginning at "offset". Data is
     * read to or from the buffer pBuf.
     * 
     * This routine does not make a distinction between key and data. It just
     * reads or writes bytes from the payload area. Data might appear on the
     * main page or be scattered out on multiple overflow pages.
     * 
     * If the BtCursor.isIncrblobHandle flag is set, and the current cursor
     * entry uses one or more overflow pages, this function allocates space for
     * and lazily popluates the overflow page-list cache array
     * (BtCursor.aOverflow). Subsequent calls use this cache to make seeking to
     * the supplied offset more efficient.
     * 
     * Once an overflow page-list cache has been allocated, it may be
     * invalidated if some other cursor writes to the same table, or if the
     * cursor is moved to a different row. Additionally, in auto-vacuum mode,
     * the following events may invalidate an overflow page-list cache.
     * 
     * <ul>
     * <li>An incremental vacuum</li>
     * <li>A commit in auto_vacuum="full" mode</li>
     * <li>Creating a table (may require moving an overflow page)</li>
     * </ul>
     * 
     * 
     * @param offset
     *            Begin reading this far into payload
     * @param amt
     *            Read this many bytes
     * @param buf
     *            Write the bytes into this buffer
     * @param skipKey
     *            offset begins at data if this is true
     * @param eOp
     *            false to read. true to write
     * 
     * @throws SqlJetException
     */
    private void accessPayload(int offset, int amt, ISqlJetMemoryPointer pBuf, int skipKey, boolean eOp)
            throws SqlJetException {

        pBuf = SqlJetUtility.pointer(pBuf);
        final SqlJetBtreeCursor pCur = this;

        ISqlJetMemoryPointer aPayload;
        int nKey;
        int iIdx = 0;
        /* Btree page of current entry */
        SqlJetMemPage pPage = pCur.apPage[pCur.iPage];
        /* Btree this cursor belongs to */
        SqlJetBtreeShared pBt = pCur.pBt;

        assert (pPage != null);
        assert (pCur.eState == CursorState.VALID);
        assert (pCur.aiIdx[pCur.iPage] < pPage.nCell);
        assert (cursorHoldsMutex(pCur));

        pCur.getCellInfo();
        aPayload = pointer(pCur.info.pCell, pCur.info.nHeader);
        nKey = (pPage.intKey ? 0 : (int) pCur.info.nKey);

        if (skipKey != 0) {
            offset += nKey;
        }
        if (offset + amt > nKey + pCur.info.nData
                || (aPayload.getPointer() + pCur.info.nLocal) > (pPage.aData.getPointer() + pBt.usableSize)) {
            /* Trying to read or write past the end of the data is an error */
            throw new SqlJetException(SqlJetErrorCode.CORRUPT);
        }

        /* Check if data must be read/written to/from the btree page itself. */
        if (offset < pCur.info.nLocal) {
            int a = amt;
            if (a + offset > pCur.info.nLocal) {
                a = pCur.info.nLocal - offset;
            }
            copyPayload(aPayload, offset, pBuf, 0, a, eOp, pPage.pDbPage);
            offset = 0;
            pBuf.movePointer(a);
            amt -= a;
        } else {
            offset -= pCur.info.nLocal;
        }

        if (amt > 0) {
            int ovflSize = pBt.usableSize - 4; /* Bytes content per ovfl page */
            int nextPage;

            nextPage = get4byte(aPayload, pCur.info.nLocal);

            /*
             * If the isIncrblobHandle flag is set and the BtCursor.aOverflow[]*
             * has not been allocated, allocate it now. The array is sized at*
             * one entry for each overflow page in the overflow chain. The* page
             * number of the first overflow page is stored in aOverflow[0],*
             * etc. A value of 0 in the aOverflow[] array means "not yet known"*
             * (the cache is lazily populated).
             */
            if (pCur.isIncrblobHandle && pCur.aOverflow == null) {
                int nOvfl = (pCur.info.nPayload - pCur.info.nLocal + ovflSize - 1) / ovflSize;
                pCur.aOverflow = new int[nOvfl];
            }

            /*
             * If the overflow page-list cache has been allocated and the* entry
             * for the first required overflow page is valid, skip* directly to
             * it.
             */
            if (pCur.aOverflow != null && pCur.aOverflow[offset / ovflSize] != 0) {
                iIdx = (offset / ovflSize);
                nextPage = pCur.aOverflow[iIdx];
                offset = (offset % ovflSize);
            }

            for (; amt > 0 && nextPage != 0; iIdx++) {

                /* If required, populate the overflow page-list cache. */
                if (pCur.aOverflow != null) {
                    assert (pCur.aOverflow[iIdx] == 0 || pCur.aOverflow[iIdx] == nextPage);
                    pCur.aOverflow[iIdx] = nextPage;
                }

                if (offset >= ovflSize) {
                    /*
                     * The only reason to read this page is to obtain the page*
                     * number for the next page in the overflow chain. The page*
                     * data is not required. So first try to lookup the overflow
                     * * page-list cache, if any, then fall back to the
                     * getOverflowPage()* function.
                     */
                    if (pCur.aOverflow != null && pCur.aOverflow[iIdx + 1] != 0) {
                        nextPage = pCur.aOverflow[iIdx + 1];
                    } else {
                        int[] pNextPage = { nextPage };
                        pBt.getOverflowPage(nextPage, null, pNextPage);
                        nextPage = pNextPage[0];
                    }
                    offset -= ovflSize;
                } else {
                    /*
                     * Need to read this page properly. It contains some of the*
                     * range of data that is being read (eOp==0) or written
                     * (eOp!=0).
                     */
                    ISqlJetPage pDbPage;
                    int a = amt;
                    pDbPage = pBt.pPager.getPage(nextPage);
                    aPayload = pDbPage.getData();
                    nextPage = get4byte(aPayload);
                    if (a + offset > ovflSize) {
                        a = ovflSize - offset;
                    }
                    copyPayload(aPayload, offset + 4, pBuf, 0, a, eOp, pDbPage);
                    pDbPage.unref();
                    offset = 0;
                    amt -= a;
                    pBuf.movePointer(a);
                }
            }
        }

        if (amt > 0) {
            throw new SqlJetException(SqlJetErrorCode.CORRUPT);
        }

    }

    /**
     * Copy data from a buffer to a page, or from a page to a buffer.
     * 
     * pPayload is a pointer to data stored on database page pDbPage. If
     * argument eOp is false, then nByte bytes of data are copied from pPayload
     * to the buffer pointed at by pBuf. If eOp is true, then
     * sqlite3PagerWrite() is called on pDbPage and nByte bytes of data are
     * copied from the buffer pBuf to pPayload.
     * 
     * @param pPayload
     *            Pointer to page data
     * @param pBuf
     *            Pointer to buffer
     * @param nByte
     *            Number of bytes to copy
     * @param eOp
     *            false -> copy from page, true -> copy to page
     * @param pDbPage
     *            Page containing pPayload
     * 
     * @throws SqlJetException
     */
    private void copyPayload(ISqlJetMemoryPointer pPayload, int payloadOffset, ISqlJetMemoryPointer pBuf,
            int bufOffset, int nByte, boolean eOp, ISqlJetPage pDbPage) throws SqlJetException {
        if (eOp) {
            /* Copy data from buffer to page (a write operation) */
            pDbPage.write();
            memcpy(pPayload, payloadOffset, pBuf, bufOffset, nByte);
        } else {
            /* Copy data from page to buffer (a read operation) */
            memcpy(pBuf, bufOffset, pPayload, payloadOffset, nByte);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#getCursorDb()
     */
    public ISqlJetDbHandle getCursorDb() {
        assert (mutex_held(pBtree.db.getMutex()));
        return pBtree.db;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#keyFetch(int[])
     */
    public ISqlJetMemoryPointer keyFetch(int[] amt) {
        assert (cursorHoldsMutex(this));
        if (eState == CursorState.VALID) {
            return fetchPayload(amt, false);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#dataFetch(int[])
     */
    public ISqlJetMemoryPointer dataFetch(int[] amt) {
        assert (cursorHoldsMutex(this));
        if (eState == CursorState.VALID) {
            return fetchPayload(amt, true);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#getDataSize()
     */
    public int getDataSize() throws SqlJetException {
        assert (cursorHoldsMutex(this));
        restoreCursorPosition();
        assert (eState == CursorState.INVALID || eState == CursorState.VALID);
        if (eState == CursorState.INVALID) {
            /* Not pointing at a valid entry - set *pSize to 0. */
            return 0;
        } else {
            getCellInfo();
            return info.nData;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#data(int, int, byte[])
     */
    public void data(int offset, int amt, ISqlJetMemoryPointer buf) throws SqlJetException {

        if (eState == CursorState.INVALID) {
            throw new SqlJetException(SqlJetErrorCode.ABORT);
        }

        assert (cursorHoldsMutex(this));
        restoreCursorPosition();
        assert (eState == CursorState.VALID);
        assert (iPage >= 0 && apPage[iPage] != null);
        assert (aiIdx[iPage] < apPage[iPage].nCell);
        accessPayload(offset, amt, buf, 1, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#putData(int, int,
     * byte[])
     */
    public void putData(int offset, int amt, ISqlJetMemoryPointer data) throws SqlJetException {

        assert (cursorHoldsMutex(this));
        assert (mutex_held(this.pBtree.db.getMutex()));
        assert (this.isIncrblobHandle);

        restoreCursorPosition();
        assert (this.eState != CursorState.REQUIRESEEK);
        if (this.eState != CursorState.VALID) {
            throw new SqlJetException(SqlJetErrorCode.ABORT);
        }

        /*
         * Check some preconditions:* (a) the cursor is open for writing,* (b)
         * there is no read-lock on the table being modified and* (c) the cursor
         * points at a valid row of an intKey table.
         */
        if (!this.wrFlag) {
            throw new SqlJetException(SqlJetErrorCode.READONLY);
        }
        assert (!this.pBt.readOnly && this.pBt.inTransaction == TransMode.WRITE);
        if (this.pBtree.checkReadLocks(this.pgnoRoot, this, 0)) {
            /* The table pCur points to has a read lock */
            throw new SqlJetException(SqlJetErrorCode.LOCKED);
        }
        if (this.eState == CursorState.INVALID || !this.apPage[this.iPage].intKey) {
            throw new SqlJetException(SqlJetErrorCode.ERROR);
        }

        accessPayload(offset, amt, data, 0, true);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#cacheOverflow()
     */
    public void cacheOverflow() {
        assert (cursorHoldsMutex(this));
        assert (mutex_held(pBtree.db.getMutex()));
        assert (!isIncrblobHandle);
        assert (aOverflow == null);
        isIncrblobHandle = true;
    }

    /**
     * Save the current cursor position in the variables BtCursor.nKey and
     * BtCursor.pKey. The cursor's state is set to CURSOR_REQUIRESEEK.
     * 
     */
    public boolean saveCursorPosition() throws SqlJetException {

        final SqlJetBtreeCursor pCur = this;

        assert (CursorState.VALID == pCur.eState);
        assert (null == pCur.pKey);
        assert (cursorHoldsMutex(pCur));

        try {
            pCur.nKey = pCur.getKeySize();

            /*
             * If this is an intKey table, then the above call to BtreeKeySize()
             * * stores the integer key in pCur->nKey. In this case this value
             * is* all that is required. Otherwise, if pCur is not open on an
             * intKey* table, then malloc space for and store the pCur->nKey
             * bytes of key* data.
             */
            if (!pCur.apPage[0].intKey) {
                ISqlJetMemoryPointer pKey = SqlJetUtility.allocatePtr((int) pCur.nKey);
                pCur.key(0, (int) pCur.nKey, pKey);
                pCur.pKey = pKey;
            }
            assert (!pCur.apPage[0].intKey || pCur.pKey == null);

            int i;
            for (i = 0; i <= pCur.iPage; i++) {
                SqlJetMemPage.releasePage(pCur.apPage[i]);
                pCur.apPage[i] = null;
            }
            pCur.iPage = -1;
            pCur.eState = CursorState.REQUIRESEEK;

        } finally {
            pCur.invalidateOverflowCache();
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#enterCursor()
     */
    public void enterCursor() {
        pBtree.enter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeCursor#leaveCursor()
     */
    public void leaveCursor() {
        pBtree.leave();
    }

}
