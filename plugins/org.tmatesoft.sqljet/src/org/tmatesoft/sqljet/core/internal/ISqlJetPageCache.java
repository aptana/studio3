/**
 * ISqlJetPageCache.java
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

import org.tmatesoft.sqljet.core.SqlJetException;

/**
 * The page cache subsystem
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetPageCache {

    /**
     * Create a new pager cache. Under memory stress, invoke xStress to try to
     * make pages clean. Only clean and unpinned pages can be reclaimed.
     * 
     * @param szPage
     *            Size of every page
     * @param szExtra
     *            Extra space associated with each page
     * @param bPurgeable
     *            True if pages are on backing store
     * @param xDestroy
     *            Called to destroy a page
     * @param xStress
     *            Call to try to make pages clean
     */
    void open(final int szPage, final boolean bPurgeable, final ISqlJetPageCallback xStress);

    /**
     * Modify the page-size after the cache has been created. Change the page
     * size for PCache object. This can only happen when the cache is empty.
     * 
     * @param pageSize
     */
    void setPageSize(final int pageSize);

    /**
     * Try to obtain a page from the cache.
     * 
     * 
     * @param pgno
     *            Page number to obtain
     * @param createFlag
     *            If true, create page if it does not exist already
     * @return
     * @throws SqlJetException
     */
    ISqlJetPage fetch(final int pageNumber, final boolean createFlag) throws SqlJetException;

    /**
     * Dereference a page. When the reference count reaches zero, move the page
     * to the LRU list if it is clean. One release per successful fetch. Page is
     * pinned until released. Reference counted.
     * 
     * @param page
     * @throws SqlJetExceptionRemove
     */
    void release(final ISqlJetPage page);

    /**
     * Remove page from cache
     * 
     * Drop a page from the cache. There must be exactly one reference to the
     * page. This function deletes that reference, so after it returns the page
     * pointed to by p is invalid.
     * 
     * @param page
     * @throws SqlJetExceptionRemove
     */
    void drop(final ISqlJetPage page);

    /**
     * Make sure the page is marked as dirty. If it isn't dirty already, make it
     * so.
     * 
     * @param page
     * @throws SqlJetExceptionRemove
     */
    void makeDirty(final ISqlJetPage page);

    /**
     * Make sure the page is marked as clean. If it isn't clean already, make it
     * so.
     * 
     * @param page
     * @throws SqlJetExceptionRemove
     */
    void makeClean(final ISqlJetPage page);

    /**
     * Mark all dirty list pages as clean Make every page in the cache clean.
     * 
     * @throws SqlJetExceptionRemove
     * 
     */
    void cleanAll();

    /**
     * Change a page number. Used by incr-vacuum.
     * 
     * Change the page number of page p to newPgno. If newPgno is 0, then the
     * page object is added to the clean-list and the PGHDR_REUSE_UNLIKELY flag
     * set.
     * 
     * @param page
     * @param pageNumber
     * @throws SqlJetExceptionRemove
     */
    void move(final ISqlJetPage page, final int pageNumber);

    /**
     * Remove all pages with page numbers more than pageNumber. Reset the cache
     * if pageNumber==0
     * 
     * Drop every cache entry whose page number is greater than "pgno".
     * 
     * @param pageNumber
     * @throws SqlJetExceptionRemove
     */
    void truncate(final int pageNumber);

    /**
     * Get a list of all dirty pages in the cache, sorted by page number
     * 
     * @return
     */
    ISqlJetPage getDirtyList();

    /**
     * Reset and close the cache object
     * 
     */
    void close();

    /**
     * Clear flags from pages of the page cache
     * 
     * @throws SqlJetExceptionRemove
     */
    void clearSyncFlags();

    /**
     * Return true if the number of dirty pages is 0 or 1
     */
    // boolean isZeroOrOneDirtyPages();
    /**
     * Discard the contents of the cache
     */
    void clear();

    /**
     * Return the total number of outstanding page references
     */
    int getRefCount();

    /**
     * Return the total number of pages stored in the cache
     */
    int getPageCount();

    /**
     * Iterate through all pages currently stored in the cache.
     * 
     * @param xIter
     * @throws SqlJetException
     */
    void iterate(final ISqlJetPageCallback xIter) throws SqlJetException;

    /**
     * Get the cache-size for the pager-cache.
     * 
     * @return
     */
    int getCachesize();

    /**
     * Set the suggested cache-size for the pager-cache.
     * 
     * If no global maximum is configured, then the system attempts to limit the
     * total number of pages cached by purgeable pager-caches to the sum of the
     * suggested cache-sizes.
     * 
     * @param cacheSize
     */
    void setCacheSize(final int cacheSize);

}
