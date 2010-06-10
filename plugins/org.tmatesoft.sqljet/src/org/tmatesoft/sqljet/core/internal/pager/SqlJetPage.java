/**
 * Page.java
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

import java.util.BitSet;
import java.util.Set;

import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.ISqlJetPage;
import org.tmatesoft.sqljet.core.internal.ISqlJetPager;
import org.tmatesoft.sqljet.core.internal.SqlJetMemoryBufferType;
import org.tmatesoft.sqljet.core.internal.SqlJetPageFlags;
import org.tmatesoft.sqljet.core.internal.SqlJetPagerJournalMode;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetPage implements ISqlJetPage {

    /**
     * 
     */
    public static final SqlJetMemoryBufferType BUFFER_TYPE = SqlJetUtility.getEnumSysProp(
            "SqlJetPage.BUFFER_TYPE", SqlJetMemoryBufferType.ARRAY);

    /** Content of this page */
    ISqlJetMemoryPointer pData;

    /** Extra content */
    Object pExtra;

    /** Transient list of dirty pages */
    SqlJetPage pDirty;

    /** Page number for this page */
    int pgno;

    /** The pager this page is part of */
    SqlJetPager pPager;

    /** Hash of page content */
    long pageHash;

    Set<SqlJetPageFlags> flags = SqlJetUtility.noneOf(SqlJetPageFlags.class);

    /*
     * Elements above are public. All that follows is private to pcache.c and
     * should not be accessed by other modules.
     */

    /** Number of users of this page */
    int nRef;

    /** Cache that owns this page */
    SqlJetPageCache pCache;

    /** Next element in list of dirty pages */
    SqlJetPage pDirtyNext;

    /** Previous element in list of dirty pages */
    SqlJetPage pDirtyPrev;

    /**
     * 
     */
    public SqlJetPage() {
    }

    /**
     * 
     */
    SqlJetPage(int szPage) {
        pData = SqlJetUtility.allocatePtr(szPage, BUFFER_TYPE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#dontRollback()
     */
    public void dontRollback() {

        assert (pPager.state.compareTo(SqlJetPagerState.RESERVED) >= 0);

        /*
         * If the journal file is not open, or DontWrite() has been called on
         * this page (DontWrite() sets the Pager.pAlwaysRollback bit), then this
         * function is a no-op.
         */
        if (!pPager.journalOpen || SqlJetUtility.bitSetTest(pPager.pagesAlwaysRollback, pgno)
                || pgno > pPager.dbOrigSize) {
            return;
        }

        if (SqlJetUtility.bitSetTest(pPager.pagesInJournal, pgno) || pgno > pPager.dbOrigSize) {
            return;
        }

        /*
         * If SECURE_DELETE is disabled, then there is no way that this routine
         * can be called on a page for which sqlite3PagerDontWrite() has not
         * been previously called during the same transaction. And if
         * DontWrite() has previously been called, the following conditions must
         * be met.
         * 
         * (Later:) Not true. If the database is corrupted by having duplicate
         * pages on the freelist (ex: corrupt9.test) then the following is not
         * necessarily true:
         */

        assert (pPager.pagesInJournal != null);
        flags.remove(SqlJetPageFlags.NEED_READ);

        /*
         * Failure to set the bits in the InJournal bit-vectors is benign. It
         * merely means that we might do some extra work to journal a page that
         * does not need to be journaled. Nevertheless, be sure to test the case
         * where a malloc error occurs while trying to set a bit in a bit
         * vector.
         */
        pPager.pagesInJournal.set(pgno);
        pPager.addToSavepointBitSets(pgno);

        SqlJetPager.PAGERTRACE("DONT_ROLLBACK page %d of %s\n", pgno, pPager.PAGERID());
        // IOTRACE(("GARBAGE %p %d\n", pPager, pPg->pgno))

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#dontWrite()
     */
    public void dontWrite() {

        if (pgno > pPager.dbOrigSize) {
            return;
        }

        if (pPager.pagesAlwaysRollback == null) {
            assert (pPager.pagesInJournal != null);
            pPager.pagesAlwaysRollback = new BitSet(pPager.dbOrigSize);
        }
        pPager.pagesAlwaysRollback.set(pgno);
        if (flags.contains(SqlJetPageFlags.DIRTY) && pPager.nSavepoint == 0) {
            assert (pPager.state.compareTo(SqlJetPagerState.SHARED) >= 0);
            if (pPager.dbSize == pgno && pPager.dbOrigSize < pPager.dbSize) {
                /*
                 * If this pages is the last page in the file and the file has
                 * grown during the current transaction, then do NOT mark the
                 * page as clean. When the database file grows, we must make
                 * sure that the last page gets written at least once so that
                 * the disk file will be the correct size. If you do not write
                 * this page and the size of the file on the disk ends up being
                 * too small, that can lead to database corruption during the
                 * next transaction.
                 */
            } else {
                SqlJetPager.PAGERTRACE("DONT_WRITE page %d of %s\n", pgno, pPager.PAGERID());
                // IOTRACE(("CLEAN %p %d\n", pPager, pPg->pgno))
                flags.add(SqlJetPageFlags.DONT_WRITE);
                pageHash = pPager.pageHash(this);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#getData()
     */
    public ISqlJetMemoryPointer getData() {
        // assertion( nRef>0 || pPager.memDb );
        return pData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#getExtra()
     */
    public Object getExtra() {
        return (pPager != null ? pExtra : null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#setExtra(java.lang.Object)
     */
    public void setExtra(Object extra) {
        this.pExtra = extra;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#move(int, boolean)
     */
    public void move(int pageNumber, boolean isCommit) throws SqlJetException {

        SqlJetPage pPgOld; /* The page being overwritten. */
        int needSyncPgno = 0;

        assert (nRef > 0);

        /*
         * If the page being moved is dirty and has not been saved by the latest
         * savepoint, then save the current contents of the page into the
         * sub-journal now. This is required to handle the following scenario:
         * 
         * BEGIN; <journal page X, then modify it in memory> SAVEPOINT one;
         * <Move page X to location Y> ROLLBACK TO one;
         * 
         * If page X were not written to the sub-journal here, it would not be
         * possible to restore its contents when the "ROLLBACK TO one" statement
         * were processed.
         */
        if (flags.contains(SqlJetPageFlags.DIRTY) && SqlJetPager.subjRequiresPage(this)) {
            pPager.subjournalPage(this);
        }

        SqlJetPager.PAGERTRACE("MOVE %s page %d (needSync=%b) moves to %d\n", pPager.PAGERID(), this.pgno, flags
                .contains(SqlJetPageFlags.NEED_SYNC), pageNumber);
        // IOTRACE(("MOVE %p %d %d\n", pPager, pPg->pgno, pgno))

        pPager.getContent(this);

        /*
         * If the journal needs to be sync()ed before page pPg->pgno can be
         * written to, store pPg->pgno in local variable needSyncPgno.
         * 
         * If the isCommit flag is set, there is no need to remember that the
         * journal needs to be sync()ed before database page pPg->pgno can be
         * written to. The caller has already promised not to write to it.
         */
        if (flags.contains(SqlJetPageFlags.NEED_SYNC) && !isCommit) {
            needSyncPgno = pgno;
            assert (SqlJetPager.pageInJournal(this) || pgno > pPager.dbOrigSize);
            assert (flags.contains(SqlJetPageFlags.DIRTY));
            assert (pPager.needSync);
        }

        /*
         * If the cache contains a page with page-number pgno, remove it from
         * its hash chain. Also, if the PgHdr.needSync was set for page pgno
         * before the 'move' operation, it needs to be retained for the page
         * moved there.
         */
        flags.remove(SqlJetPageFlags.NEED_SYNC);
        pPgOld = (SqlJetPage) pPager.lookup(pageNumber);
        assert (pPgOld == null || pPgOld.nRef >= 1);
        if (pPgOld != null) {
            if (pPgOld.flags.contains(SqlJetPageFlags.NEED_SYNC))
                flags.add(SqlJetPageFlags.NEED_SYNC);
        }

        if (pPgOld != null) {
            pPager.pageCache.drop(pPgOld);
        }

        pPager.pageCache.move(this, pageNumber);
        
        pPager.pageCache.makeDirty(this);
        pPager.dirtyCache = true;
        pPager.dbModified = true;

        if (needSyncPgno != 0) {
            /*
             * If needSyncPgno is non-zero, then the journal file needs to be
             * sync()ed before any data is written to database file page
             * needSyncPgno. Currently, no such page exists in the page-cache
             * and the "is journaled" bitvec flag has been set. This needs to be
             * remedied by loading the page into the pager-cache and setting the
             * PgHdr.needSync flag.
             * 
             * If the attempt to load the page into the page-cache fails, (due
             * to a malloc() or IO failure), clear the bit in the pInJournal[]
             * array. Otherwise, if the page is loaded and written again in this
             * transaction, it may be written to the database file before it is
             * synced into the journal file. This way, it may end up in the
             * journal file twice, but that is not a problem.
             * 
             * The sqlite3PagerGet() call may cause the journal to sync. So make
             * sure the Pager.needSync flag is set too.
             */
            SqlJetPage pPgHdr;
            assert (pPager.needSync);
            try {
                pPgHdr = (SqlJetPage) pPager.getPage(needSyncPgno);
            } catch (SqlJetException e) {
                if (pPager.pagesInJournal != null && needSyncPgno <= pPager.dbOrigSize) {
                    pPager.pagesInJournal.clear(needSyncPgno);
                }
                throw e;
            }

            pPager.needSync = true;
            assert (!pPager.noSync && !pPager.memDb);
            pPgHdr.flags.add(SqlJetPageFlags.NEED_SYNC);
            pPager.pageCache.makeDirty(pPgHdr);
            pPgHdr.unref();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#ref()
     */
    public void ref() {
        assert (nRef > 0);
        nRef++;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#unref()
     */
    public void unref() throws SqlJetException {
        try {
            pPager.pageCache.release(this);
        } finally {
            pPager.unlockIfUnused();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#write()
     */
    public void write() throws SqlJetException {

        int nPagePerSector = (pPager.sectorSize / pPager.pageSize);

        if (nPagePerSector > 1) {

            int nPageCount; /* Total number of pages in database file */
            int pg1; /* First page of the sector pPg is located on. */
            int nPage; /* Number of pages starting at pg1 to journal */
            int ii;
            boolean needSync = false;

            /*
             * Set the doNotSync flag to 1. This is because we cannot allow a
             * journal header to be written between the pages journaled by this
             * function.
             */
            assert (!pPager.memDb);
            assert (!pPager.doNotSync);
            pPager.doNotSync = true;

            /*
             * This trick assumes that both the page-size and sector-size are an
             * integer power of 2. It sets variable pg1 to the identifier of the
             * first page of the sector pPg is located on.
             */
            pg1 = ((pgno - 1) & ~(nPagePerSector - 1)) + 1;

            nPageCount = pPager.getPageCount();
            if (pgno > nPageCount) {
                nPage = (pgno - pg1) + 1;
            } else if ((pg1 + nPagePerSector - 1) > nPageCount) {
                nPage = nPageCount + 1 - pg1;
            } else {
                nPage = nPagePerSector;
            }
            assert (nPage > 0);
            assert (pg1 <= pgno);
            assert ((pg1 + nPage) > pgno);

            for (ii = 0; ii < nPage; ii++) {
                int pg = pg1 + ii;
                SqlJetPage pPage;
                if (pg == pgno || !SqlJetUtility.bitSetTest(pPager.pagesInJournal, pg)) {
                    if (pg != pPager.PAGER_MJ_PGNO()) {
                        pPage = (SqlJetPage) pPager.getPage(pg);
                        pPage.doWrite();
                        if (pPage.flags.contains(SqlJetPageFlags.NEED_SYNC)) {
                            needSync = true;
                        }
                        pPage.unref();
                    }
                } else if ((pPage = (SqlJetPage) pPager.lookup(pg)) != null) {
                    if (pPage.flags.contains(SqlJetPageFlags.NEED_SYNC)) {
                        needSync = true;
                        assert (pPager.needSync);
                    }
                    pPage.unref();
                }
            }

            /*
             * If the PGHDR_NEED_SYNC flag is set for any of the nPage pages
             * starting at pg1, then it needs to be set for all of them. Because
             * writing to any of these nPage pages may damage the others, the
             * journal file must contain sync()ed copies of all of them before
             * any of them can be written out to the database file.
             */
            if (needSync) {
                assert (!pPager.memDb && !pPager.noSync);
                for (ii = 0; ii < nPage && needSync; ii++) {
                    SqlJetPage pPage = (SqlJetPage) pPager.lookup(pg1 + ii);
                    if (pPage != null) {
                        pPage.flags.add(SqlJetPageFlags.NEED_SYNC);
                        pPage.unref();
                    }
                }
                assert (pPager.needSync);
            }

            assert (pPager.doNotSync);
            pPager.doNotSync = false;

        } else {
            doWrite();
        }
    }

    /**
     * Mark a data page as writeable. The page is written into the journal if it
     * is not there already. This routine must be called before making changes
     * to a page.
     * 
     * The first time this routine is called, the pager creates a new journal
     * and acquires a RESERVED lock on the database. If the RESERVED lock could
     * not be acquired, this routine returns SQLITE_BUSY. The calling routine
     * must check for that return value and be careful not to change any page
     * data until this routine returns SQLITE_OK.
     * 
     * If the journal file could not be written because the disk is full, then
     * this routine returns SQLITE_FULL and does an immediate rollback. All
     * subsequent write attempts also return SQLITE_FULL until there is a call
     * to sqlite3PagerCommit() or sqlite3PagerRollback() to reset.
     * 
     * @throws SqlJetException
     * 
     */
    private void doWrite() throws SqlJetException {

        /*
         * Check for errors
         */
        if (pPager.errCode != null) {
            throw new SqlJetException(pPager.errCode);
        }
        if (pPager.readOnly) {
            throw new SqlJetException(SqlJetErrorCode.PERM);
        }

        assert (!pPager.setMaster);

        /*
         * If this page was previously acquired with noContent==1, that means we
         * didn't really read in the content of the page. This can happen (for
         * example) when the page is being moved to the freelist. But now we are
         * (perhaps) moving the page off of the freelist for reuse and we need
         * to know its original content so that content can be stored in the
         * rollback journal. So do the read at this time.
         */
        pPager.getContent(this);

        /*
         * Mark the page as dirty. If the page has already been written to the
         * journal then we can return right away.
         */
        pCache.makeDirty(this);
        if (SqlJetPager.pageInJournal(this) && !SqlJetPager.subjRequiresPage(this)) {
            pPager.dirtyCache = true;
            pPager.dbModified = true;
        } else {
            /*
             * If we get this far, it means that the page needs to be written to
             * the transaction journal or the ckeckpoint journal or both.
             * 
             * First check to see that the transaction journal exists and create
             * it if it does not.
             */
            assert (pPager.state != SqlJetPagerState.UNLOCK);
            pPager.begin(false);
            assert (pPager.state.compareTo(SqlJetPagerState.RESERVED) >= 0);
            if (!pPager.journalOpen && pPager.useJournal && pPager.journalMode != SqlJetPagerJournalMode.OFF) {
                pPager.openJournal();
            }
            pPager.dirtyCache = true;
            pPager.dbModified = true;

            /*
             * The transaction journal now exists and we have a RESERVED or an
             * EXCLUSIVE lock on the main database file. Write the current page
             * to the transaction journal if it is not there already.
             */
            if (!SqlJetPager.pageInJournal(this) && pPager.journalOpen) {
                if (pgno <= pPager.dbOrigSize) {
                    /*
                     * We should never write to the journal file the page that
                     * contains the database locks. The following assert
                     * verifies that we do not.
                     */
                    assert (pgno != pPager.PAGER_MJ_PGNO());

                    try {
                        long cksum = pPager.cksum(pData);
                        SqlJetPager.write32bits(pPager.jfd, pPager.journalOff, pgno);
                        try {
                            pPager.jfd.write(pData, pPager.pageSize, pPager.journalOff + 4);
                        } finally {
                            pPager.journalOff += pPager.pageSize + 4;
                        }
                        try {
                            SqlJetPager.write32bitsUnsigned( pPager.jfd, pPager.journalOff, cksum );
                        } finally {
                            pPager.journalOff += 4;
                        }

                    } finally {
                        // IOTRACE(("JOUT %p %d %lld %d\n", pPager, pPg->pgno,
                        // pPager->journalOff, pPager->pageSize));
                        // PAGER_INCR(sqlite3_pager_writej_count);
                        SqlJetPager.PAGERTRACE("JOURNAL %s page %d needSync=%b hash(%08x)\n", pPager.PAGERID(), pgno, flags
                                .contains(SqlJetPageFlags.NEED_SYNC), pPager.pageHash(this));

                        /*
                         * Even if an IO or diskfull error occurred while
                         * journalling the page in the block above, set the
                         * need-sync flag for the page. Otherwise, when the
                         * transaction is rolled back, the logic in
                         * playback_one_page() will think that the page needs to
                         * be restored in the database file. And if an IO error
                         * occurs while doing so, then corruption may follow.
                         */
                        if (!pPager.noSync) {
                            flags.add(SqlJetPageFlags.NEED_SYNC);
                            pPager.needSync = true;
                        }

                        /*
                         * An error has occured writing to the journal file. The
                         * transaction will be rolled back by the layer above.
                         */

                    }

                    pPager.nRec++;
                    assert (pPager.pagesInJournal != null);
                    pPager.pagesInJournal.set(pgno);
                    pPager.addToSavepointBitSets(pgno);
                } else {
                    if (!pPager.journalStarted && !pPager.noSync) {
                        flags.add(SqlJetPageFlags.NEED_SYNC);
                        pPager.needSync = true;
                    }
                    SqlJetPager.PAGERTRACE("APPEND %s page %d needSync=%b\n", pPager.PAGERID(), pgno, flags
                            .contains(SqlJetPageFlags.NEED_SYNC));
                }
            }

            /*
             * If the statement journal is open and the page is not in it, then
             * write the current page to the statement journal. Note that the
             * statement journal format differs from the standard journal format
             * in that it omits the checksums and the header.
             */
            if (SqlJetPager.subjRequiresPage(this)) {
                pPager.subjournalPage(this);
            }
        }

        /*
         * Update the database size and return.
         */
        assert (pPager.state.compareTo(SqlJetPagerState.SHARED) >= 0);
        if (pPager.dbSize < pgno) {
            pPager.dbSize = pgno;
            if (pPager.dbSize == (pPager.PAGER_MJ_PGNO() - 1)) {
                pPager.dbSize++;
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#getFlags()
     */
    public Set<SqlJetPageFlags> getFlags() {
        return flags;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#getHash()
     */
    public long getHash() {
        return pageHash;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#getPager()
     */
    public ISqlJetPager getPager() {
        return pPager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#setFlags(java.util.Set)
     */
    public void setFlags(Set<SqlJetPageFlags> flags) {
        this.flags = flags;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#setHash(long)
     */
    public void setHash(long hash) {
        pageHash = hash;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetPage#setPager(org.tmatesoft.sqljet.core
     * .ISqlJetPager)
     */
    public void setPager(ISqlJetPager pager) {
        pPager = (SqlJetPager) pager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#getPageNumber()
     */
    public int getPageNumber() {
        return pgno;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#setPageNumber(long)
     */
    public void setPageNumber(int pageNumber) {
        pgno = pageNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#getNext()
     */
    public ISqlJetPage getNext() {
        return pDirtyNext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#getPrev()
     */
    public ISqlJetPage getPrev() {
        return pDirtyPrev;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#getRefCount()
     */
    public int getRefCount() {
        return nRef;
    }
    
    /* (non-Javadoc)
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#isWriteable()
     */
    public boolean isWriteable() {
        return flags.contains( SqlJetPageFlags.DIRTY );
    }

    /* (non-Javadoc)
     * @see org.tmatesoft.sqljet.core.ISqlJetPage#getDirty()
     */
    public ISqlJetPage getDirty() {
        return pDirty;
    }
}
