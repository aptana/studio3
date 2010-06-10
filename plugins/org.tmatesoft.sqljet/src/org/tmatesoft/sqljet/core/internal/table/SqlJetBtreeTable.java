/**
 * SqlJetTableWrapper.java
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
 */
package org.tmatesoft.sqljet.core.internal.table;

import java.util.List;
import java.util.Random;

import org.tmatesoft.sqljet.core.SqlJetEncoding;
import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetValueType;
import org.tmatesoft.sqljet.core.internal.ISqlJetBtree;
import org.tmatesoft.sqljet.core.internal.ISqlJetBtreeCursor;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.ISqlJetVdbeMem;
import org.tmatesoft.sqljet.core.internal.SqlJetBtreeTableCreateFlags;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;
import org.tmatesoft.sqljet.core.internal.vdbe.SqlJetBtreeRecord;
import org.tmatesoft.sqljet.core.internal.vdbe.SqlJetKeyInfo;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetBtreeTable implements ISqlJetBtreeTable {

    protected ISqlJetBtree btree;
    protected int rootPage;

    protected boolean write;
    protected boolean index;

    protected ISqlJetBtreeCursor cursor;

    protected SqlJetKeyInfo keyInfo;

    private long priorNewRowid = 0;

    private SqlJetBtreeRecord recordCache;
    private Object[] valueCache;
    private Object[] valuesCache;

    /**
     * @param db
     * @param btree
     * @param rootPage
     * @param write
     * @param index
     * @throws SqlJetException
     */
    public SqlJetBtreeTable(ISqlJetBtree btree, int rootPage, boolean write, boolean index) throws SqlJetException {

        init(btree, rootPage, write, index);

    }

    /**
     * @param db
     * @param btree
     * @param rootPage
     * @param write
     * @param index
     * @throws SqlJetException
     */
    private void init(ISqlJetBtree btree, int rootPage, boolean write, boolean index) throws SqlJetException {
        this.btree = btree;
        this.rootPage = rootPage;
        this.write = write;
        this.index = index;

        if (index) {
            this.keyInfo = new SqlJetKeyInfo();
            this.keyInfo.setEnc(btree.getDb().getOptions().getEncoding());
        }

        this.cursor = btree.getCursor(rootPage, write, index ? keyInfo : null);

        first();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.btree.ISqlJetBtreeTable#close()
     */
    public void close() throws SqlJetException {
        clearRecordCache();
        cursor.closeCursor();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeTable#unlock()
     */
    public void unlock() {
        cursor.leaveCursor();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeTable#lock()
     */
    public void lock() throws SqlJetException {
        cursor.enterCursor();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.btree.ISqlJetBtreeTable#eof()
     */
    public boolean eof() throws SqlJetException {
        hasMoved();
        return cursor.eof();
    }

    /**
     * @throws SqlJetException
     */
    public boolean hasMoved() throws SqlJetException {
        cursor.enterCursor();
        try {
            return cursor.cursorHasMoved();
        } finally {
            cursor.leaveCursor();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeTable#first()
     */
    public boolean first() throws SqlJetException {
        lock();
        try {
            clearRecordCache();
            return !cursor.first();
        } finally {
            unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeTable#last()
     */
    public boolean last() throws SqlJetException {
        lock();
        try {
            clearRecordCache();
            return !cursor.last();
        } finally {
            unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.btree.ISqlJetBtreeTable#next()
     */
    public boolean next() throws SqlJetException {
        lock();
        try {
            clearRecordCache();
            hasMoved();
            return !cursor.next();
        } finally {
            unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.ISqlJetBtreeTable#previous()
     */
    public boolean previous() throws SqlJetException {
        lock();
        try {
            clearRecordCache();
            hasMoved();
            return !cursor.previous();
        } finally {
            unlock();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.internal.btree.ISqlJetBtreeTable#getRecord
     */
    public ISqlJetBtreeRecord getRecord() throws SqlJetException {
        if (eof())
            return null;
        if (null == recordCache) {
            lock();
            try {
                recordCache = new SqlJetBtreeRecord(cursor, index, btree.getDb().getOptions().getFileFormat());
            } finally {
                unlock();
            }
            valueCache = new Object[recordCache.getFieldsCount()];
        }
        return recordCache;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeTable#lockTable(
     * boolean)
     */
    public void lockTable(boolean write) {
        btree.lockTable(rootPage, write);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeTable#getEncoding()
     */
    public SqlJetEncoding getEncoding() throws SqlJetException {
        return cursor.getCursorDb().getOptions().getEncoding();
    }

    protected static boolean checkField(ISqlJetBtreeRecord record, int field) throws SqlJetException {
        return (field >= 0 && record != null && field < record.getFieldsCount());
    }

    protected ISqlJetVdbeMem getValueMem(int field) throws SqlJetException {
        final ISqlJetBtreeRecord r = getRecord();
        if (null == r)
            return null;
        if (!checkField(r, field))
            return null;
        final List<ISqlJetVdbeMem> fields = r.getFields();
        if (null == fields)
            return null;
        return fields.get(field);
    }

    public Object getValue(int field) throws SqlJetException {
        if (valueCache != null && field < valueCache.length) {
            final Object valueCached = valueCache[field];
            if (valueCached != null)
                return valueCached;
        }
        final Object valueUncached = getValueUncached(field);
        if (valueUncached != null) {
            valueCache[field] = valueUncached;
        }
        return valueUncached;
    }

    public Object getValueUncached(int field) throws SqlJetException {
        final ISqlJetVdbeMem value = getValueMem(field);
        if (value == null || value.isNull())
            return null;
        switch (value.getType()) {
        case INTEGER:
            return value.intValue();
        case FLOAT:
            return value.realValue();
        case TEXT:
            return SqlJetUtility.toString(value.valueText(getEncoding()), getEncoding());
        case BLOB:
            return value.valueBlob();
        case NULL:
            break;
        default:
            break;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeTable#getFieldsCount
     * ()
     */
    public int getFieldsCount() throws SqlJetException {
        final ISqlJetBtreeRecord r = getRecord();
        if (null == r)
            return 0;
        return r.getFieldsCount();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeTable#isNull(int)
     */
    public boolean isNull(int field) throws SqlJetException {
        final ISqlJetVdbeMem value = getValueMem(field);
        if (null == value)
            return true;
        return value.isNull();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeTable#getString(int)
     */
    public String getString(int field) throws SqlJetException {
        final ISqlJetVdbeMem value = getValueMem(field);
        if (value == null || value.isNull())
            return null;
        return SqlJetUtility.toString(value.valueText(getEncoding()), getEncoding());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeTable#getInteger
     * (int)
     */
    public long getInteger(int field) throws SqlJetException {
        final ISqlJetVdbeMem value = getValueMem(field);
        if (value == null || value.isNull())
            return 0;
        return value.intValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeTable#getReal(int)
     */
    public double getFloat(int field) throws SqlJetException {
        final ISqlJetVdbeMem value = getValueMem(field);
        if (value == null || value.isNull())
            return 0;
        return value.realValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeTable#getFieldType
     * (int)
     */
    public SqlJetValueType getFieldType(int field) throws SqlJetException {
        final ISqlJetVdbeMem value = getValueMem(field);
        if (value == null)
            return SqlJetValueType.NULL;
        return value.getType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeTable#getBlob(int)
     */
    public ISqlJetMemoryPointer getBlob(int field) throws SqlJetException {
        final ISqlJetVdbeMem value = getValueMem(field);
        if (value == null || value.isNull())
            return null;
        return value.valueBlob();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeTable#getValues()
     */
    public Object[] getValues() throws SqlJetException {
        if (valuesCache != null) {
            return valuesCache;
        } else {
            final ISqlJetBtreeRecord record = getRecord();
            final int fieldsCount = record.getFieldsCount();
            for (int i = 0; i < fieldsCount; i++) {
                valueCache[i] = getValue(i);
            }
            valuesCache = valueCache;
            return valueCache;
        }
    }

    public long newRowId() throws SqlJetException {
        return newRowId(0);
    }

    /**
     * Get a new integer record number (a.k.a "rowid") used as the key to a
     * table. The record number is not previously used as a key in the database
     * table that cursor P1 points to. The new record number is written written
     * to register P2.
     * 
     * Prev is the largest previously generated record number. No new record
     * numbers are allowed to be less than this value. When this value reaches
     * its maximum, a SQLITE_FULL error is generated. This mechanism is used to
     * help implement the AUTOINCREMENT feature.
     * 
     * @param prev
     * @return
     * @throws SqlJetException
     */
    public long newRowId(long prev) throws SqlJetException {
        /*
         * The next rowid or record number (different terms for the same thing)
         * is obtained in a two-step algorithm. First we attempt to find the
         * largest existing rowid and add one to that. But if the largest
         * existing rowid is already the maximum positive integer, we have to
         * fall through to the second probabilistic algorithm. The second
         * algorithm is to select a rowid at random and see if it already exists
         * in the table. If it does not exist, we have succeeded. If the random
         * rowid does exist, we select a new one and try again, up to 1000
         * times.For a table with less than 2 billion entries, the probability
         * of not finding a unused rowid is about 1.0e-300. This is a non-zero
         * probability, but it is still vanishingly small and should never cause
         * a problem. You are much, much more likely to have a hardware failure
         * than for this algorithm to fail.
         * 
         * To promote locality of reference for repetitive inserts, the first
         * few attempts at choosing a random rowid pick values just a little
         * larger than the previous rowid. This has been shown experimentally to
         * double the speed of the COPY operation.
         */

        lock();
        try {
            boolean useRandomRowid = false;
            long v = 0;
            int res = 0;
            int cnt = 0;

            if ((cursor.flags() & (SqlJetBtreeTableCreateFlags.INTKEY.getValue() | SqlJetBtreeTableCreateFlags.ZERODATA
                    .getValue())) != SqlJetBtreeTableCreateFlags.INTKEY.getValue()) {
                throw new SqlJetException(SqlJetErrorCode.CORRUPT);
            }

            assert ((cursor.flags() & SqlJetBtreeTableCreateFlags.INTKEY.getValue()) != 0);
            assert ((cursor.flags() & SqlJetBtreeTableCreateFlags.ZERODATA.getValue()) == 0);

            long MAX_ROWID = 0x7fffffff;

            final boolean last = cursor.last();

            if (last) {
                v = 1;
            } else {
                v = cursor.getKeySize();
                if (v == MAX_ROWID) {
                    useRandomRowid = true;
                } else {
                    v++;
                }

                if (prev != 0) {
                    if (prev == MAX_ROWID || useRandomRowid) {
                        throw new SqlJetException(SqlJetErrorCode.FULL);
                    }
                    if (v < prev) {
                        v = prev + 1;
                    }
                }

                if (useRandomRowid) {
                    v = priorNewRowid;
                    Random random = new Random();
                    /* SQLITE_FULL must have occurred prior to this */
                    assert (prev == 0);
                    cnt = 0;
                    do {
                        if (cnt == 0 && (v & 0xffffff) == v) {
                            v++;
                        } else {
                            v = random.nextInt();
                            if (cnt < 5)
                                v &= 0xffffff;
                        }
                        if (v == 0)
                            continue;
                        res = cursor.moveToUnpacked(null, v, false);
                        cnt++;
                    } while (cnt < 100 && res == 0);
                    priorNewRowid = v;
                    if (res == 0) {
                        throw new SqlJetException(SqlJetErrorCode.FULL);
                    }
                }
            }
            return v;
        } finally {
            unlock();
        }
    }

    protected void clearRecordCache() {
        recordCache = null;
        valuesCache = null;
        valueCache = null;
    }

    public void clear() throws SqlJetException {
        btree.clearTable(rootPage, null);
    }

    public long getKeySize() throws SqlJetException {
        return cursor.getKeySize();
    }

    public int moveTo(ISqlJetMemoryPointer pKey, long nKey, boolean bias) throws SqlJetException {
        clearRecordCache();
        return cursor.moveTo(pKey, nKey, bias);
    }

    /**
     * @param object
     * @param rowId
     * @param pData
     * @param remaining
     * @param i
     * @param b
     * @throws SqlJetException
     */
    public void insert(ISqlJetMemoryPointer pKey, long nKey, ISqlJetMemoryPointer pData, int nData, int nZero,
            boolean bias) throws SqlJetException {
        clearRecordCache();
        cursor.insert(pKey, nKey, pData, nData, nZero, bias);
    }

    /**
     * @throws SqlJetException
     * 
     */
    public void delete() throws SqlJetException {
        clearRecordCache();
        cursor.delete();
    }
}
