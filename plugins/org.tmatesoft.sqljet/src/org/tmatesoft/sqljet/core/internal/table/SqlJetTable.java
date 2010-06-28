/**
 * SqlJetDataTableCursor.java
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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.internal.ISqlJetBtree;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;
import org.tmatesoft.sqljet.core.schema.ISqlJetIndexDef;
import org.tmatesoft.sqljet.core.schema.ISqlJetTableDef;
import org.tmatesoft.sqljet.core.schema.SqlJetConflictAction;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetRunnableWithLock;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.ISqlJetTransaction;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 * Implementation of {@link ISqlJetTable}.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetTable implements ISqlJetTable {

    private interface ISqlJetTableRun {
        public Object run(final ISqlJetBtreeDataTable table) throws SqlJetException;
    }

    private final SqlJetDb db;
    private ISqlJetBtree btree;
    private String tableName;
    private boolean write;

    public SqlJetTable(SqlJetDb db, ISqlJetBtree btree, String tableName, boolean write) throws SqlJetException {
        this.db = db;
        this.btree = btree;
        this.tableName = tableName;
        this.write = write;
        if (null == getDefinition())
            throw new SqlJetException(SqlJetErrorCode.ERROR, "Table not found: " + tableName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetTable#getDataBase()
     */
    public SqlJetDb getDataBase() {
        return db;
    }

    public String getPrimaryKeyIndexName() throws SqlJetException {
        final ISqlJetTableDef definition = getDefinition();
        return definition.isRowIdPrimaryKey() ? null : definition.getPrimaryKeyIndexName();
    }

    public ISqlJetTableDef getDefinition() throws SqlJetException {
        return btree.getSchema().getTable(tableName);
    };

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetTable#getIndexes(java.lang.String)
     */
    public Set<ISqlJetIndexDef> getIndexesDefs() throws SqlJetException {
        return btree.getSchema().getIndexes(tableName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetTable#getIndexNames()
     */
    public Set<String> getIndexesNames() throws SqlJetException {
        final Set<String> result = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        final Set<ISqlJetIndexDef> indexesDefs = getIndexesDefs();
        if (null != indexesDefs) {
            for (final ISqlJetIndexDef indexDef : indexesDefs) {
                if (null != indexDef) {
                    result.add(indexDef.getName());
                }
            }
        }
        return Collections.unmodifiableSet(result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetTable#getIndex(java.lang.String)
     */
    public ISqlJetIndexDef getIndexDef(String name) throws SqlJetException {
        if (null == name) {
            name = getPrimaryKeyIndexName();
            if (null == name) {
                return null;
            }
        }
        final Set<ISqlJetIndexDef> indexesDefs = getIndexesDefs();
        if (null != indexesDefs) {
            for (final ISqlJetIndexDef indexDef : indexesDefs) {
                if (null != indexDef && name.equalsIgnoreCase(indexDef.getName())) {
                    return indexDef;
                }
            }
        }
        return null;
    }

    public ISqlJetCursor open() throws SqlJetException {
        return (ISqlJetCursor) db.runWithLock(new ISqlJetRunnableWithLock() {
            public Object runWithLock(SqlJetDb db) throws SqlJetException {
                return new SqlJetTableDataCursor(new SqlJetBtreeDataTable(btree, tableName, write), db);
            }
        });
    }

    public ISqlJetCursor lookup(final String indexName, final Object... key) throws SqlJetException {
        final Object[] k = SqlJetUtility.adjustNumberTypes(key);
        return (ISqlJetCursor) db.runWithLock(new ISqlJetRunnableWithLock() {
            public Object runWithLock(SqlJetDb db) throws SqlJetException {
                final SqlJetBtreeDataTable table = new SqlJetBtreeDataTable(btree, tableName, write);
                checkIndexName(indexName, table);
                return new SqlJetIndexScopeCursor(table, db, indexName, k, k);
            }
        });
    }

    private Object runWriteTransaction(final ISqlJetTableRun op) throws SqlJetException {
        return db.runWriteTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                final ISqlJetBtreeDataTable table = new SqlJetBtreeDataTable(btree, tableName, write);
                try {
                    return op.run(table);
                } finally {
                    table.close();
                }
            }
        });
    }

    public long insert(final Object... values) throws SqlJetException {
        return insertOr(null, values);
    }

    public long insertByFieldNames(final Map<String, Object> values) throws SqlJetException {
        return insertByFieldNamesOr(null, values);
    }

    public long insertWithRowId(final long rowId, final Object... values) throws SqlJetException {
        return insertWithRowIdOr(null, rowId, values);
    }

    public long insertOr(final SqlJetConflictAction onConflict, final Object... values) throws SqlJetException {
        return (Long) runWriteTransaction(new ISqlJetTableRun() {
            public Object run(ISqlJetBtreeDataTable table) throws SqlJetException {
                return table.insert(onConflict, values);
            }
        });
    }

    public long insertByFieldNamesOr(final SqlJetConflictAction onConflict, final Map<String, Object> values)
            throws SqlJetException {
        return (Long) runWriteTransaction(new ISqlJetTableRun() {
            public Object run(ISqlJetBtreeDataTable table) throws SqlJetException {
                return table.insert(onConflict, values);
            }
        });
    }

    public long insertWithRowIdOr(final SqlJetConflictAction onConflict, final long rowId, final Object... values)
            throws SqlJetException {
        return (Long) runWriteTransaction(new ISqlJetTableRun() {
            public Object run(ISqlJetBtreeDataTable table) throws SqlJetException {
                return table.insertWithRowId(onConflict, rowId, values);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetTable#order(java.lang.String)
     */
    public ISqlJetCursor order(final String indexName) throws SqlJetException {
        return (ISqlJetCursor) db.runWithLock(new ISqlJetRunnableWithLock() {
            public Object runWithLock(SqlJetDb db) throws SqlJetException {
                final SqlJetBtreeDataTable table = new SqlJetBtreeDataTable(btree, tableName, write);
                checkIndexName(indexName, table);
                return new SqlJetIndexOrderCursor(table, db, indexName);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetTable#scope(java.lang.String,
     * java.lang.Object[], java.lang.Object[])
     */
    public ISqlJetCursor scope(final String indexName, final Object[] firstKey, final Object[] lastKey)
            throws SqlJetException {
        final Object[] first = SqlJetUtility.adjustNumberTypes(firstKey);
        final Object[] last = SqlJetUtility.adjustNumberTypes(lastKey);
        return (ISqlJetCursor) db.runWithLock(new ISqlJetRunnableWithLock() {
            public Object runWithLock(SqlJetDb db) throws SqlJetException {
                final SqlJetBtreeDataTable table = new SqlJetBtreeDataTable(btree, tableName, write);
                checkIndexName(indexName, table);
                if (isNeedReverse(getIndexTable(indexName, table), first, last)) {
                    return new SqlJetReverseOrderCursor(new SqlJetIndexScopeCursor(table, db, indexName, last, first));
                } else {
                    return new SqlJetIndexScopeCursor(table, db, indexName, first, last);
                }
            }
        });
    }

    public void clear() throws SqlJetException {
        runWriteTransaction(new ISqlJetTableRun() {
            public Object run(ISqlJetBtreeDataTable table) throws SqlJetException {
                table.clear();
                return null;
            }
        });
    }

    /**
     * @param indexName
     * @param firstKey
     * @param lastKey
     * @param reverse
     * @param table
     * @return
     * @throws SqlJetException
     */
    private boolean isNeedReverse(final ISqlJetBtreeIndexTable indexTable, final Object[] firstKey,
            final Object[] lastKey) throws SqlJetException {
        if (firstKey != null && lastKey != null && firstKey.length > 0 && lastKey.length > 0) {
            if (indexTable != null) {
                return indexTable.compareKeys(firstKey, lastKey) < 0;
            } else if (firstKey.length == 1 && lastKey.length == 1 && firstKey[0] instanceof Long
                    && lastKey[0] instanceof Long) {
                return ((Long) firstKey[0]).compareTo((Long) lastKey[0]) > 0;
            }
        }
        return false;
    }

    private ISqlJetBtreeIndexTable getIndexTable(final String indexName, final SqlJetBtreeDataTable table) {
        final String index = indexName == null ? table.getPrimaryKeyIndex() : indexName;
        return index != null ? table.getIndex(index) : null;
    }

    private void checkIndexName(final String indexName, final SqlJetBtreeDataTable table) throws SqlJetException {
        if (!isIndexNameValid(indexName, table)) {
            throw new SqlJetException(SqlJetErrorCode.MISUSE, String.format("Index not exists: %s", indexName));
        }
    }

    private boolean isIndexNameValid(final String indexName, final SqlJetBtreeDataTable table) {
        if (indexName != null) {
            return getIndexTable(indexName, table) != null;
        } else {
            if (table.getDefinition().isRowIdPrimaryKey()) {
                return true;
            } else {
                return table.getPrimaryKeyIndex() != null;
            }
        }
    }

}
