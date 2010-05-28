/**
 * SqlJetBtreeDataTable.java
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

import java.util.List;
import java.util.Set;

import org.tmatesoft.sqljet.core.SqlJetEncoding;
import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.internal.ISqlJetBtree;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.ISqlJetVdbeMem;
import org.tmatesoft.sqljet.core.internal.SqlJetUnpackedRecordFlags;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;
import org.tmatesoft.sqljet.core.internal.schema.SqlJetBaseIndexDef;
import org.tmatesoft.sqljet.core.internal.vdbe.SqlJetBtreeRecord;
import org.tmatesoft.sqljet.core.internal.vdbe.SqlJetUnpackedRecord;
import org.tmatesoft.sqljet.core.schema.ISqlJetIndexDef;
import org.tmatesoft.sqljet.core.schema.ISqlJetIndexedColumn;
import org.tmatesoft.sqljet.core.schema.ISqlJetSchema;
import org.tmatesoft.sqljet.core.schema.SqlJetSortingOrder;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetBtreeIndexTable extends SqlJetBtreeTable implements ISqlJetBtreeIndexTable {

    private ISqlJetIndexDef indexDef;
    private List<String> columns;

    /**
     * Open index by name
     * 
     * @throws SqlJetException
     * 
     */
    public SqlJetBtreeIndexTable(ISqlJetBtree btree, String indexName, boolean write) throws SqlJetException {
        super(btree, ((SqlJetBaseIndexDef) btree.getSchema().getIndex(indexName)).getPage(), write, true);
        indexDef = btree.getSchema().getIndex(indexName);
        adjustKeyInfo();
    }

    public SqlJetBtreeIndexTable(ISqlJetBtree btree, String indexName, List<String> columns, boolean write)
            throws SqlJetException {
        super(btree, ((SqlJetBaseIndexDef) btree.getSchema().getIndex(indexName)).getPage(), write, true);
        indexDef = btree.getSchema().getIndex(indexName);
        this.columns = columns;
        adjustKeyInfo();
    }

    /**
     * @return the indexDef
     */
    public ISqlJetIndexDef getIndexDef() {
        return indexDef;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeIndexTable#lookup
     * (boolean, java.lang.Object[])
     */
    public long lookup(boolean next, Object... values) throws SqlJetException {
        lock();
        try {
            return lookupSafe(next, false, false, values);
        } finally {
            unlock();
        }
    }

    /**
     * @param next
     * @param values
     * @return
     * @throws SqlJetException
     */
    private long lookupSafe(boolean next, boolean near, boolean last, Object... values) throws SqlJetException {
        final SqlJetEncoding encoding = btree.getDb().getOptions().getEncoding();
        ISqlJetBtreeRecord key = SqlJetBtreeRecord.getRecord(encoding, values);
        final ISqlJetMemoryPointer k = key.getRawRecord();
        if (next) {
            if (!last) {
                next();
            } else {
                previous();
            }
        } else {
            final int moved = cursorMoveTo(k, last);
            if (moved != 0) {
                if (!last) {
                    if (moved < 0) {
                        next();
                    }
                } else {
                    if (moved > 0) {
                        previous();
                    }
                }
            }
        }
        final ISqlJetBtreeRecord record = getRecord();
        if (null == record)
            return 0;
        if (!near && keyCompare(k, record.getRawRecord()) != 0)
            return 0;
        return getKeyRowId(record);
    }

    /**
     * @param k
     * @param last
     * @return
     * @throws SqlJetException
     */
    private int cursorMoveTo(final ISqlJetMemoryPointer pKey, boolean last) throws SqlJetException {
        clearRecordCache();
        final int nKey = pKey.remaining();
        if (!last) {
            return cursor.moveTo(pKey, nKey, false);
        } else {
            SqlJetUnpackedRecord pIdxKey;
            if (pKey != null) {
                assert (nKey == (long) (int) nKey);
                pIdxKey = keyInfo.recordUnpack((int) nKey, pKey);
                pIdxKey.getFlags().add(SqlJetUnpackedRecordFlags.INCRKEY);
                if (pIdxKey == null)
                    throw new SqlJetException(SqlJetErrorCode.NOMEM);
            } else {
                pIdxKey = null;
            }
            try {
                return cursor.moveToUnpacked(pIdxKey, nKey, false);
            } finally {
                if (pKey != null) {
                    SqlJetUnpackedRecord.delete(pIdxKey);
                }
            }
        }
    }

    /**
     * 
     * @param key
     * @param record
     * @return
     * 
     * @throws SqlJetException
     */
    private int keyCompare(ISqlJetMemoryPointer key, ISqlJetMemoryPointer record) throws SqlJetException {
        final SqlJetUnpackedRecord unpacked = keyInfo.recordUnpack(key.remaining(), key);
        final Set<SqlJetUnpackedRecordFlags> flags = unpacked.getFlags();
        flags.add(SqlJetUnpackedRecordFlags.IGNORE_ROWID);
        flags.add(SqlJetUnpackedRecordFlags.PREFIX_MATCH);
        return unpacked.recordCompare(record.remaining(), record);
    }

    public int compareKeys(Object[] firstKey, Object[] lastKey) throws SqlJetException {
        final SqlJetEncoding encoding = btree.getDb().getOptions().getEncoding();
        final ISqlJetMemoryPointer firstRec = SqlJetBtreeRecord.getRecord(encoding, firstKey).getRawRecord();
        final ISqlJetMemoryPointer lastRec = SqlJetBtreeRecord.getRecord(encoding, lastKey).getRawRecord();
        final SqlJetUnpackedRecord unpacked = keyInfo.recordUnpack(firstRec.remaining(), firstRec);
        unpacked.getFlags().add(SqlJetUnpackedRecordFlags.PREFIX_MATCH);
        return unpacked.recordCompare(lastRec.remaining(), lastRec);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeIndexTable#checkKey
     * (java.lang.Object[])
     */
    public boolean checkKey(Object... key) throws SqlJetException {
        if (eof())
            return false;
        final ISqlJetMemoryPointer keyRecord = SqlJetBtreeRecord.getRecord(btree.getDb().getOptions().getEncoding(),
                key).getRawRecord();
        return 0 == keyCompare(keyRecord, getRecord().getRawRecord());
    }

    /**
     * @param key
     * 
     * @throws SqlJetException
     */
    private void adjustKeyInfo() throws SqlJetException {
        if (null == keyInfo)
            throw new SqlJetException(SqlJetErrorCode.INTERNAL);
        if (null != columns) {
            keyInfo.setNField(columns.size());
        } else if (null != indexDef.getColumns()) {
            keyInfo.setNField(indexDef.getColumns().size());
            int i = 0;
            for (final ISqlJetIndexedColumn column : indexDef.getColumns()) {
                keyInfo.setSortOrder(i++, column.getSortingOrder() == SqlJetSortingOrder.DESC);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeIndexTable#insert
     * (long, boolean, java.lang.Object[])
     */
    public void insert(long rowId, boolean append, Object... key) throws SqlJetException {
        lock();
        try {
            final ISqlJetMemoryPointer zKey = SqlJetBtreeRecord.getRecord(btree.getDb().getOptions().getEncoding(),
                    SqlJetUtility.addArrays(key, new Object[] { rowId })).getRawRecord();
            cursor.insert(zKey, zKey.remaining(), SqlJetUtility.allocatePtr(0), 0, 0, append);
            clearRecordCache();
        } finally {
            unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeIndexTable#delete
     * (long, java.lang.Object[])
     */
    public boolean delete(long rowId, Object... key) throws SqlJetException {
        lock();
        try {
            final ISqlJetBtreeRecord rec = SqlJetBtreeRecord.getRecord(btree.getDb().getOptions().getEncoding(), key);
            final ISqlJetMemoryPointer k = rec.getRawRecord();
            if (cursorMoveTo(k, false) < 0) {
                next();
            }
            do {
                final ISqlJetBtreeRecord record = getRecord();
                if (null == record)
                    return false;
                if (keyCompare(k, record.getRawRecord()) != 0)
                    return false;
                if (getKeyRowId(record) == rowId) {
                    cursor.delete();
                    clearRecordCache();
                    if (cursorMoveTo(k, false) < 0) {
                        next();
                    }
                    return true;
                }
            } while (next());
            return false;
        } finally {
            unlock();
        }
    }

    private long getKeyRowId(ISqlJetBtreeRecord record) {
        if (null == record)
            return 0;
        final List<ISqlJetVdbeMem> fields = record.getFields();
        if (null == fields || 0 == fields.size())
            return 0;
        return fields.get(fields.size() - 1).intValue();
    }

    public long getKeyRowId() throws SqlJetException {
        return getKeyRowId(getRecord());
    }

    /**
     * @throws SqlJetException
     * 
     */
    public void reindex(ISqlJetSchema schema) throws SqlJetException {
        lock();
        try {
            btree.clearTable(rootPage, null);
            final SqlJetBtreeDataTable dataTable = new SqlJetBtreeDataTable(btree, indexDef.getTableName(), false);
            try {
                for (dataTable.first(); !dataTable.eof(); dataTable.next()) {
                    final Object[] key = dataTable.getKeyForIndex(dataTable.getAsNamedFields(dataTable.getValues()),
                            indexDef);
                    insert(dataTable.getRowId(), true, key);
                }
            } finally {
                dataTable.close();
            }
        } finally {
            unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeIndexTable#compareKey
     * (java.lang.Object[])
     */
    public int compareKey(Object[] key) throws SqlJetException {
        if (eof()) {
            return 1;
        }
        final ISqlJetMemoryPointer keyRecord = SqlJetBtreeRecord.getRecord(btree.getDb().getOptions().getEncoding(),
                key).getRawRecord();
        return keyCompare(keyRecord, getRecord().getRawRecord());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeIndexTable#lookupNear
     * (boolean, java.lang.Object[])
     */
    public long lookupNear(boolean next, Object[] key) throws SqlJetException {
        lock();
        try {
            return lookupSafe(next, true, false, key);
        } finally {
            unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeIndexTable#
     * lookupLastNear(java.lang.Object[])
     */
    public long lookupLastNear(Object[] key) throws SqlJetException {
        lock();
        try {
            return lookupSafe(false, true, true, key);
        } finally {
            unlock();
        }
    }

}
