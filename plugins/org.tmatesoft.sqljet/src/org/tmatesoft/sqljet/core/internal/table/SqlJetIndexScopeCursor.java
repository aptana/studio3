/**
 * SqlJetIndexScopeCursor.java
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
package org.tmatesoft.sqljet.core.internal.table;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;
import org.tmatesoft.sqljet.core.table.ISqlJetTransaction;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetIndexScopeCursor extends SqlJetIndexOrderCursor {

    private Object[] firstKey;
    private Object[] lastKey;
    private long firstRowId;
    private long lastRowId;

    /**
     * @param table
     * @param db
     * @param indexName
     * @param firstKey
     * @param lastKey
     * @throws SqlJetException
     */
    public SqlJetIndexScopeCursor(ISqlJetBtreeDataTable table, SqlJetDb db, String indexName, Object[] firstKey,
            Object[] lastKey) throws SqlJetException {
        super(table, db, indexName);
        this.firstKey = SqlJetUtility.copyArray(firstKey);
        this.lastKey = SqlJetUtility.copyArray(lastKey);
        if (null == indexTable) {
            firstRowId = getRowIdFromKey(this.firstKey);
            lastRowId = getRowIdFromKey(this.lastKey);
        }
        first();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.SqlJetTableDataCursor#goTo(long)
     */
    @Override
    public boolean goTo(final long rowId) throws SqlJetException {
        return (Boolean) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                SqlJetIndexScopeCursor.super.goTo(rowId);
                return !eof();
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.SqlJetIndexOrderCursor#first()
     */
    @Override
    public boolean first() throws SqlJetException {
        return (Boolean) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                if (firstKey == null) {
                    return SqlJetIndexScopeCursor.super.first();
                } else if (indexTable == null) {
                    if (firstRowId == 0) {
                        return SqlJetIndexScopeCursor.super.first();
                    } else {
                        return firstRowNum(goTo(firstRowId));
                    }
                } else {
                    final long lookup = indexTable.lookupNear(false, firstKey);
                    if (lookup != 0) {
                        return firstRowNum(goTo(lookup));
                    }
                }
                return false;
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.SqlJetIndexOrderCursor#next()
     */
    @Override
    public boolean next() throws SqlJetException {
        return (Boolean) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                if (lastKey == null) {
                    return SqlJetIndexScopeCursor.super.next();
                } else if (indexTable == null) {
                    SqlJetIndexScopeCursor.super.next();
                    return !eof();
                } else {
                    if (indexTable.next() && !eof()) {
                        return nextRowNum(goTo(indexTable.getKeyRowId()));
                    }
                }
                return false;
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.SqlJetIndexOrderCursor#eof()
     */
    @Override
    public boolean eof() throws SqlJetException {
        return (Boolean) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                return SqlJetIndexScopeCursor.super.eof() || !checkScope();
            }
        });
    }

    /**
     * @return
     * @throws SqlJetException
     */
    private boolean checkScope() throws SqlJetException {
        if (indexTable == null) {
            if (getBtreeDataTable().eof()) {
                return false;
            }
            final long rowId = getRowId();
            if (firstRowId != 0) {
                if (firstRowId > rowId)
                    return false;
            }
            if (lastRowId != 0) {
                if (lastRowId < rowId)
                    return false;
            }
        } else {
            if (firstKey != null) {
                if (indexTable.compareKey(firstKey) < 0) {
                    return false;
                }
            }
            if (lastKey != null) {
                if (indexTable.compareKey(lastKey) > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.SqlJetIndexOrderCursor#last()
     */
    @Override
    public boolean last() throws SqlJetException {
        return (Boolean) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                if (lastKey == null) {
                    return SqlJetIndexScopeCursor.super.last();
                } else if (indexTable == null) {
                    if (lastRowId == 0) {
                        return SqlJetIndexScopeCursor.super.last();
                    } else {
                        return lastRowNum(goTo(lastRowId));
                    }
                } else {
                    final long lookup = indexTable.lookupLastNear(lastKey);
                    if (lookup != 0) {
                        return lastRowNum(goTo(lookup));
                    }
                }
                return false;
            }
        });
    }

    private long getRowIdFromKey(Object[] key) {
        if (key != null && key.length > 0 && key[0] instanceof Long)
            return (Long) key[0];
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.SqlJetTableDataCursor#delete()
     */
    @Override
    public void delete() throws SqlJetException {
        super.delete();
        db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                if (!checkScope())
                    next();
                return false;
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.SqlJetTableDataCursor#getRowId()
     */
    @Override
    public long getRowId() throws SqlJetException {
        if (indexTable != null && !indexTable.eof()) {
            return indexTable.getKeyRowId();
        }
        return super.getRowId();
    }

}
