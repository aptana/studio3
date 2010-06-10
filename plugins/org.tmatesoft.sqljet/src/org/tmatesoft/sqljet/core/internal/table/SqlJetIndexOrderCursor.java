/**
 * SqlJetIndexOrderCursor.java
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
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTransaction;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetIndexOrderCursor extends SqlJetTableDataCursor implements ISqlJetCursor {

    protected String indexName;
    protected ISqlJetBtreeIndexTable indexTable;

    /**
     * @param table
     * @param db
     * @throws SqlJetException
     */
    public SqlJetIndexOrderCursor(ISqlJetBtreeDataTable table, SqlJetDb db, String indexName) throws SqlJetException {
        super(table, db);
        this.indexName = indexName != null ? indexName : table.getPrimaryKeyIndex();
        if (this.indexName != null) {
            this.indexTable = table.getIndexesTables().get(this.indexName);
        }
        first();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.table.SqlJetCursor#first()
     */
    @Override
    public boolean first() throws SqlJetException {
        return (Boolean) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                if (indexTable == null) {
                    return SqlJetIndexOrderCursor.super.first();
                } else {
                    if (indexTable.first()) {
                        return firstRowNum(goTo(indexTable.getKeyRowId()));
                    }
                }
                return false;
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.table.SqlJetCursor#next()
     */
    @Override
    public boolean next() throws SqlJetException {
        return (Boolean) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                if (indexTable == null) {
                    return SqlJetIndexOrderCursor.super.next();
                } else {
                    if (indexTable.next()) {
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
     * @see org.tmatesoft.sqljet.core.internal.table.SqlJetCursor#eof()
     */
    @Override
    public boolean eof() throws SqlJetException {
        return (Boolean) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                if (indexTable == null) {
                    return SqlJetIndexOrderCursor.super.eof();
                } else {
                    return indexTable.eof();
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.table.SqlJetCursor#last()
     */
    @Override
    public boolean last() throws SqlJetException {
        return (Boolean) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                if (indexTable == null) {
                    return SqlJetIndexOrderCursor.super.last();
                } else {
                    if (indexTable.last()) {
                        return lastRowNum(goTo(indexTable.getKeyRowId()));
                    }
                }
                return false;
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.table.SqlJetCursor#previous()
     */
    @Override
    public boolean previous() throws SqlJetException {
        return (Boolean) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                if (indexTable == null) {
                    return SqlJetIndexOrderCursor.super.previous();
                } else {
                    if (indexTable.previous()) {
                        return previousRowNum(goTo(indexTable.getKeyRowId()));
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
     * org.tmatesoft.sqljet.core.internal.table.SqlJetTableDataCursor#delete()
     */
    @Override
    public void delete() throws SqlJetException {
        if (indexTable != null) {
            goTo(indexTable.getKeyRowId());
        }
        super.delete();
        if (indexTable != null) {
            goTo(indexTable.getKeyRowId());
        }
    }

}
