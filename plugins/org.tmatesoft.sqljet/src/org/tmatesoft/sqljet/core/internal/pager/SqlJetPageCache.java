/**
 * PCache.java
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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.internal.ISqlJetPage;
import org.tmatesoft.sqljet.core.internal.ISqlJetPageCache;
import org.tmatesoft.sqljet.core.internal.ISqlJetPageCallback;
import org.tmatesoft.sqljet.core.internal.SqlJetPageFlags;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;

/**
 * A complete page cache is an instance of this structure.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetPageCache implements ISqlJetPageCache {

    /**
     * System property name for cache size configuration.
     */
    public static final String SQLJET_PAGE_CACHE_SIZE = "SQLJET.PAGE_CACHE_SIZE";
    public static final int PAGE_CACHE_SIZE_DEFAULT = 2000;
    public static final int PAGE_CACHE_SIZE_MINIMUM = 10;

    private static final int N_SORT_BUCKET = 25;

    /** List of dirty pages in LRU order */
    SqlJetPage pDirty, pDirtyTail;
    /** Last synced page in dirty page list */
    SqlJetPage pSynced;
    /** Number of pinned pages */
    int nRef;
    /** Configured cache size */
    int nMax = PAGE_CACHE_SIZE_DEFAULT;
    /** Configured minimum cache size */
    int nMin = PAGE_CACHE_SIZE_MINIMUM;
    /** Size of every page in this cache */
    int szPage;
    /** True if pages are on backing store */
    boolean bPurgeable;
    /** Call to try make a page clean */
    ISqlJetPageCallback xStress;
    PCache pCache = new PCache();
    ISqlJetPage pPage1;

    SqlJetPageCache() {
        final int cacheSize = SqlJetUtility.getIntSysProp(SQLJET_PAGE_CACHE_SIZE, nMax);
        if (cacheSize >= nMin)
            nMax = cacheSize;
    }

    /*
     * Remove page pPage from the list of dirty pages.
     */
    static void removeFromDirtyList(SqlJetPage pPage) {

        SqlJetPageCache p = pPage.pCache;

        assert (pPage.pDirtyNext != null || pPage == p.pDirtyTail);
        assert (pPage.pDirtyPrev != null || pPage == p.pDirty);

        /* Update the PCache1.pSynced variable if necessary. */
        if (p.pSynced == pPage) {
            SqlJetPage pSynced = pPage.pDirtyPrev;
            while (pSynced != null && pSynced.flags.contains(SqlJetPageFlags.NEED_SYNC)) {
                pSynced = pSynced.pDirtyPrev;
            }
            p.pSynced = pSynced;
        }

        if (pPage.pDirtyNext != null) {
            pPage.pDirtyNext.pDirtyPrev = pPage.pDirtyPrev;
        } else {
            assert (pPage == p.pDirtyTail);
            p.pDirtyTail = pPage.pDirtyPrev;
        }
        if (pPage.pDirtyPrev != null) {
            pPage.pDirtyPrev.pDirtyNext = pPage.pDirtyNext;
        } else {
            assert (pPage == p.pDirty);
            p.pDirty = pPage.pDirtyNext;
        }
        pPage.pDirtyNext = null;
        pPage.pDirtyPrev = null;

    }

    /*
     * Add page pPage to the head of the dirty list (PCache1.pDirty is set to
     * pPage).
     */
    static void addToDirtyList(SqlJetPage pPage) {

        SqlJetPageCache p = pPage.pCache;

        assert (pPage.pDirtyNext == null && pPage.pDirtyPrev == null && p.pDirty != pPage);

        pPage.pDirtyNext = p.pDirty;
        if (pPage.pDirtyNext != null) {
            assert (pPage.pDirtyNext.pDirtyPrev == null);
            pPage.pDirtyNext.pDirtyPrev = pPage;
        }
        p.pDirty = pPage;
        if (p.pDirtyTail == null) {
            p.pDirtyTail = pPage;
        }
        if (p.pSynced == null && !pPage.flags.contains(SqlJetPageFlags.NEED_SYNC)) {
            p.pSynced = pPage;
        }

    }

    /*
     * Wrapper around the pluggable caches xUnpin method. If the cache is being
     * used for an in-memory database, this function is a no-op.
     */
    static void unpin(SqlJetPage p) {
        SqlJetPageCache pCache = p.pCache;
        if (pCache.bPurgeable) {
            if (p.pgno == 1) {
                pCache.pPage1 = null;
            }
            if (pCache.pCache != null) {
                pCache.pCache.unpin(p, false);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPageCache#open(int, int, boolean,
     * org.tmatesoft.sqljet.core.ISqlJetPageCallback)
     */
    public void open(int szPage, boolean purgeable, ISqlJetPageCallback stress) {
        this.szPage = szPage;
        this.bPurgeable = purgeable;
        this.xStress = stress;
        // this.nMax = 100;
        // this.nMin = 10;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPageCache#setPageSize(int)
     */
    public void setPageSize(int pageSize) {
        assert (this.nRef == 0 && this.pDirty == null);
        if (pCache != null) {
            pCache.destroy();
            pCache = null;
        }
        this.szPage = pageSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPageCache#fetch(int, boolean)
     */
    public ISqlJetPage fetch(int pgno, boolean createFlag) throws SqlJetException {

        SqlJetPage pPage = null;

        assert (pgno > 0);

        /*
         * If the pluggable cache (sqlite3_pcache) has not been allocated,
         * allocate it now.
         */
        if (pCache == null && createFlag) {
            pCache = new PCache();
        }

        if (pCache != null) {
            pPage = pCache.fetch(pgno, createFlag);
        }

        if (pPage == null && createFlag) {
            SqlJetPage pPg;

            /*
             * Find a dirty page to write-out and recycle. First try to find a
             * page that does not require a journal-sync (one with
             * PGHDR_NEED_SYNC cleared), but if that is not possible settle for
             * any other unreferenced dirty page.
             */
            for (pPg = pSynced; pPg != null && (pPg.nRef > 0 || pPg.flags.contains(SqlJetPageFlags.NEED_SYNC)); pPg = pPg.pDirtyPrev)
                ;
            if (pPg == null) {
                for (pPg = pDirtyTail; pPg != null && pPg.nRef > 0; pPg = pPg.pDirtyPrev)
                    ;
            }
            if (pPg != null) {
                xStress.pageCallback(pPg);
            }
            pCache.cleanUnpinned();

            pPage = pCache.fetch(pgno, true);
        }

        if (pPage != null) {
            if (0 == pPage.nRef) {
                nRef++;
            }
            pPage.nRef++;
            if (null == pPage.pData)
                pPage.pData = SqlJetUtility.allocatePtr(szPage, SqlJetPage.BUFFER_TYPE);
            pPage.pCache = this;
            pPage.pgno = pgno;
            if (pgno == 1) {
                pPage1 = pPage;
            }
        }
        return pPage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetPageCache#release(org.tmatesoft.sqljet
     * .core.ISqlJetPage)
     */
    public void release(ISqlJetPage page) {
        SqlJetPage p = (SqlJetPage) page;
        assert (p.nRef > 0);
        p.nRef--;
        if (p.nRef == 0) {
            SqlJetPageCache pCache = p.pCache;
            pCache.nRef--;
            if (!p.flags.contains(SqlJetPageFlags.DIRTY)) {
                unpin(p);
            } else {
                /* Move the page to the head of the dirty list. */
                removeFromDirtyList(p);
                addToDirtyList(p);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetPageCache#drop(org.tmatesoft.sqljet.
     * core.ISqlJetPage)
     */
    public void drop(ISqlJetPage page) {
        SqlJetPage p = (SqlJetPage) page;
        assert (p.nRef >= 1);
        if (p.flags.contains(SqlJetPageFlags.DIRTY)) {
            removeFromDirtyList(p);
        }
        nRef--;
        if (p.pgno == 1) {
            pPage1 = null;
        }
        pCache.unpin(p, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetPageCache#makeDirty(org.tmatesoft.sqljet
     * .core.ISqlJetPage)
     */
    public void makeDirty(ISqlJetPage page) {
        SqlJetPage p = (SqlJetPage) page;
        p.flags.remove(SqlJetPageFlags.DONT_WRITE);
        assert (p.nRef > 0);
        if (!p.flags.contains(SqlJetPageFlags.DIRTY)) {
            p.flags.add(SqlJetPageFlags.DIRTY);
            addToDirtyList(p);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetPageCache#makeClean(org.tmatesoft.sqljet
     * .core.ISqlJetPage)
     */
    public void makeClean(ISqlJetPage page) {
        SqlJetPage p = (SqlJetPage) page;
        if (p.flags.contains(SqlJetPageFlags.DIRTY)) {
            removeFromDirtyList(p);
            p.flags.remove(SqlJetPageFlags.DIRTY);
            p.flags.remove(SqlJetPageFlags.NEED_SYNC);
            if (p.nRef == 0) {
                unpin(p);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPageCache#cleanAll()
     */
    public void cleanAll() {
        ISqlJetPage p;
        while ((p = pDirty) != null) {
            makeClean(p);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPageCache#clearSyncFlags()
     */
    public void clearSyncFlags() {
        SqlJetPage p;
        for (p = pDirty; p != null; p = p.pDirtyNext) {
            p.flags.remove(SqlJetPageFlags.NEED_SYNC);
        }
        pSynced = pDirtyTail;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetPageCache#move(org.tmatesoft.sqljet.
     * core.ISqlJetPage, int)
     */
    public void move(ISqlJetPage page, int newPgno) {
        SqlJetPage p = (SqlJetPage) page;
        assert (p.nRef > 0);
        assert (newPgno > 0);
        pCache.rekey(p, p.pgno, newPgno);
        p.pgno = newPgno;
        if (p.flags.contains(SqlJetPageFlags.DIRTY) && p.flags.contains(SqlJetPageFlags.NEED_SYNC)) {
            removeFromDirtyList(p);
            addToDirtyList(p);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPageCache#truncate(int)
     */
    public void truncate(int pgno) {
        if (pCache != null) {
            SqlJetPage p;
            SqlJetPage pNext;
            for (p = pDirty; p != null; p = pNext) {
                pNext = p.pDirtyNext;
                if (p.pgno > pgno) {
                    assert (p.flags.contains(SqlJetPageFlags.DIRTY));
                    makeClean(p);
                }
            }
            if (pgno == 0 && pPage1 != null) {
                SqlJetUtility.memset(pPage1.getData(), (byte) 0, szPage);
                pgno = 1;
            }
            pCache.truncate(pgno + 1);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPageCache#close()
     */
    public void close() {
        if (pCache != null) {
            pCache.destroy();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPageCache#clear()
     */
    public void clear() {
        truncate(0);
    }

    /*
     * Merge two lists of pages connected by pDirty and in pgno order. Do not
     * both fixing the pDirtyPrev pointers.
     */
    static SqlJetPage mergeDirtyList(SqlJetPage pA, SqlJetPage pB) {
        SqlJetPage result = new SqlJetPage();
        SqlJetPage pTail = result;
        while (pA != null && pB != null) {
            if (pA.pgno < pB.pgno) {
                pTail.pDirty = pA;
                pTail = pA;
                pA = pA.pDirty;
            } else {
                pTail.pDirty = pB;
                pTail = pB;
                pB = pB.pDirty;
            }
        }
        if (pA != null) {
            pTail.pDirty = pA;
        } else if (pB != null) {
            pTail.pDirty = pB;
        } else {
            pTail.pDirty = null;
        }
        return result.pDirty;
    }

    /*
     * Sort the list of pages in accending order by pgno. Pages are connected by
     * pDirty pointers. The pDirtyPrev pointers are corrupted by this sort.
     */
    static SqlJetPage sortDirtyList(SqlJetPage pIn) {
        SqlJetPage[] a = new SqlJetPage[N_SORT_BUCKET];
        SqlJetPage p;
        int i;
        while (pIn != null) {
            p = pIn;
            pIn = p.pDirty;
            p.pDirty = null;
            for (i = 0; i < N_SORT_BUCKET - 1; i++) {
                if (a[i] == null) {
                    a[i] = p;
                    break;
                } else {
                    p = mergeDirtyList(a[i], p);
                    a[i] = null;
                }
            }
            if (i == N_SORT_BUCKET - 1) {
                /*
                 * Coverage: To get here, there need to be 2^(N_SORT_BUCKET)
                 * elements in the input list. This is possible, but
                 * impractical. Testing this line is the point of global
                 * variable sqlite3_pager_n_sort_bucket.
                 */
                a[i] = mergeDirtyList(a[i], p);
            }
        }
        p = a[0];
        for (i = 1; i < N_SORT_BUCKET; i++) {
            p = mergeDirtyList(p, a[i]);
        }
        return p;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPageCache#getDirtyList()
     */
    public ISqlJetPage getDirtyList() {
        SqlJetPage p;
        for (p = pDirty; p != null; p = p.pDirtyNext) {
            p.pDirty = p.pDirtyNext;
        }
        return sortDirtyList(pDirty);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPageCache#getRefCount()
     */
    public int getRefCount() {
        return nRef;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPageCache#getPageCount()
     */
    public int getPageCount() {
        int nPage = 0;
        if (pCache != null) {
            nPage = pCache.getPageCount();
        }
        return nPage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPageCache#getCachesize()
     */
    public int getCachesize() {
        return nMax;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetPageCache#setCacheSize(int)
     */
    public void setCacheSize(int mxPage) {
        nMax = mxPage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.ISqlJetPageCache#iterate(org.tmatesoft.sqljet
     * .core.ISqlJetPageCallback)
     */
    public void iterate(ISqlJetPageCallback iter) throws SqlJetException {
        SqlJetPage pDirty;
        for (pDirty = this.pDirty; pDirty != null; pDirty = pDirty.pDirtyNext) {
            iter.pageCallback(pDirty);
        }
    }

    class PCache {

        /** Hash table for fast lookup by key */
        private Map<Integer, SqlJetPage> apHash = new LinkedHashMap<Integer, SqlJetPage>();

        private Set<Integer> unpinned = new LinkedHashSet<Integer>();

        /** Largest key seen since xTruncate() */
        private int iMaxKey;

        public synchronized int getPageCount() {
            return apHash.size();
        }

        /**
         * Fetch a page by key value.
         * 
         * Whether or not a new page may be allocated by this function depends
         * on the value of the createFlag argument.
         * 
         * There are three different approaches to obtaining space for a page,
         * depending on the value of parameter createFlag (which may be 0, 1 or
         * 2).
         * 
         * 1. Regardless of the value of createFlag, the cache is searched for a
         * copy of the requested page. If one is found, it is returned.
         * 
         * 2. If createFlag==0 and the page is not already in the cache, NULL is
         * returned.
         * 
         * 3. If createFlag is 1, the cache is marked as purgeable and the page
         * is not already in the cache, and if either of the following are true,
         * return NULL:
         * 
         * (a) the number of pages pinned by the cache is greater than
         * PCache1.nMax, or (b) the number of pages pinned by the cache is
         * greater than the sum of nMax for all purgeable caches, less the sum
         * of nMin for all other purgeable caches.
         * 
         * 4. If none of the first three conditions apply and the cache is
         * marked as purgeable, and if one of the following is true:
         * 
         * (a) The number of pages allocated for the cache is already
         * PCache1.nMax, or
         * 
         * (b) The number of pages allocated for all purgeable caches is already
         * equal to or greater than the sum of nMax for all purgeable caches,
         * 
         * then attempt to recycle a page from the LRU list. If it is the right
         * size, return the recycled buffer. Otherwise, free the buffer and
         * proceed to step 5.
         * 
         * 5. Otherwise, allocate and return a new page buffer.
         */
        public synchronized SqlJetPage fetch(final int key, final boolean createFlag) {

            class FetchOut {
                SqlJetPage go_to(SqlJetPage pPage) {
                    if (pPage != null && key > iMaxKey) {
                        iMaxKey = key;
                    }
                    return pPage;
                }
            }

            FetchOut fetch_out = new FetchOut();

            SqlJetPage pPage = null;

            /* Search the hash table for an existing entry. */
            if (apHash.size() > 0) {
                pPage = apHash.get(key);
            }

            if (pPage != null || !createFlag) {
                return fetch_out.go_to(pPage);
            }

            /* Step 3 of header comment. */
            if (bPurgeable && getPageCount() == nMax) {
                return null;
            }

            /*
             * If a usable page buffer has still not been found, attempt to
             * allocate a new one.
             */
            if (pPage == null) {
                pPage = new SqlJetPage(szPage);
            }

            if (pPage != null) {
                pPage.pgno = key;
                pPage.pCache = SqlJetPageCache.this;
                apHash.put(key, pPage);
            }

            return fetch_out.go_to(pPage);
        }

        /**
         * Mark a page as unpinned (eligible for asynchronous recycling).
         * 
         * xUnpin() is called by SQLite with a pointer to a currently pinned
         * page as its second argument. If the third parameter, discard, is
         * non-zero, then the page should be evicted from the cache. In this
         * case SQLite assumes that the next time the page is retrieved from the
         * cache using the xFetch() method, it will be zeroed. If the discard
         * parameter is zero, then the page is considered to be unpinned. The
         * cache implementation may choose to reclaim (free or recycle) unpinned
         * pages at any time. SQLite assumes that next time the page is
         * retrieved from the cache it will either be zeroed, or contain the
         * same data that it did when it was unpinned.
         * 
         * The cache is not required to perform any reference counting. A single
         * call to xUnpin() unpins the page regardless of the number of prior
         * calls to xFetch().
         * 
         */
        public synchronized void unpin(ISqlJetPage page, boolean discard) {
            final int pageNumber = page.getPageNumber();
            if (discard || (bPurgeable && getPageCount() == nMax)) {
                apHash.remove(pageNumber);
            } else if (!unpinned.contains(pageNumber)) {
                unpinned.add(pageNumber);
            }
        }

        /**
         * The xRekey() method is used to change the key value associated with
         * the page passed as the second argument from oldKey to newKey. If the
         * cache previously contains an entry associated with newKey, it should
         * be discarded. Any prior cache entry associated with newKey is
         * guaranteed not to be pinned.
         * 
         */
        public synchronized void rekey(ISqlJetPage page, int oldKey, int newKey) {

            SqlJetPage pPage = (SqlJetPage) page;

            assert (pPage.pgno == oldKey);

            apHash.remove(oldKey);
            apHash.put(newKey, pPage);
            pPage.pgno = newKey;

            if (newKey > iMaxKey) {
                iMaxKey = newKey;
            }

        }

        /**
         * When SQLite calls the xTruncate() method, the cache must discard all
         * existing cache entries with page numbers (keys) greater than or equal
         * to the value of the iLimit parameter passed to xTruncate(). If any of
         * these pages are pinned, they are implicitly unpinned, meaning that
         * they can be safely discarded.
         * 
         */
        public synchronized void truncate(int iLimit) {
            if (iLimit <= iMaxKey) {
                List<Integer> l = new LinkedList<Integer>();
                for (Integer i : apHash.keySet()) {
                    if (i >= iLimit) {
                        l.add(i);
                    }
                }
                for (Integer i : l)
                    apHash.remove(i);
                iMaxKey = iLimit - 1;
            }
        }

        /**
         * The xDestroy() method is used to delete a cache allocated by
         * xCreate(). All resources associated with the specified cache should
         * be freed. After calling the xDestroy() method, SQLite considers the
         * [sqlite3_pcache*] handle invalid, and will not use it with any other
         * sqlite3_pcache_methods functions.
         */
        public synchronized void destroy() {
            apHash.clear();
            unpinned.clear();
        }

        /**
         * 
         */
        public void cleanUnpinned() {
            final Iterator<Integer> i = unpinned.iterator();
            while (i.hasNext()) {
                final Integer next = i.next();
                if (next == null) {
                    i.remove();
                    continue;
                }
                final SqlJetPage p = apHash.get(next);
                if (p == null) {
                    i.remove();
                    continue;
                }
                if(p.getRefCount()>0){
                    i.remove();
                    continue;                    
                }
                final Set<SqlJetPageFlags> flags = p.getFlags();
                if (flags.contains(SqlJetPageFlags.DIRTY) || flags.contains(SqlJetPageFlags.NEED_SYNC)) {
                    continue;
                }
                apHash.remove(next);
                i.remove();
                return;
            }
        }

    }

}
