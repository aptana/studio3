/**
 * SqlJetReverseOrderCursor.java
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

import java.io.InputStream;
import java.util.Map;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetValueType;
import org.tmatesoft.sqljet.core.schema.SqlJetConflictAction;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetReverseOrderCursor implements ISqlJetCursor {

    private ISqlJetCursor cursor;
    private boolean eof;

    public SqlJetReverseOrderCursor(ISqlJetCursor cursor) throws SqlJetException {
        this.cursor = cursor;
        first();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#close()
     */
    public void close() throws SqlJetException {
        cursor.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#delete()
     */
    public void delete() throws SqlJetException {
        if (!eof) {
            cursor.delete();
            eof = cursor.eof();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#eof()
     */
    public boolean eof() throws SqlJetException {
        return eof;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#first()
     */
    public boolean first() throws SqlJetException {
        return !(eof = !cursor.last());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#getBlobAsArray(int)
     */
    public byte[] getBlobAsArray(int field) throws SqlJetException {
        return cursor.getBlobAsArray(field);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetCursor#getBlobAsArray(java.lang
     * .String)
     */
    public byte[] getBlobAsArray(String fieldName) throws SqlJetException {
        return cursor.getBlobAsArray(fieldName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#getBlobAsStream(int)
     */
    public InputStream getBlobAsStream(int field) throws SqlJetException {
        return cursor.getBlobAsStream(field);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetCursor#getBlobAsStream(java.lang
     * .String)
     */
    public InputStream getBlobAsStream(String fieldName) throws SqlJetException {
        return cursor.getBlobAsStream(fieldName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#getBoolean(int)
     */
    public boolean getBoolean(int field) throws SqlJetException {
        return cursor.getBoolean(field);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetCursor#getBoolean(java.lang.String
     * )
     */
    public boolean getBoolean(String fieldName) throws SqlJetException {
        return cursor.getBoolean(fieldName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#getFieldType(int)
     */
    public SqlJetValueType getFieldType(int field) throws SqlJetException {
        return cursor.getFieldType(field);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetCursor#getFieldType(java.lang.
     * String)
     */
    public SqlJetValueType getFieldType(String fieldName) throws SqlJetException {
        return cursor.getFieldType(fieldName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#getFieldsCount()
     */
    public int getFieldsCount() throws SqlJetException {
        return cursor.getFieldsCount();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#getFloat(int)
     */
    public double getFloat(int field) throws SqlJetException {
        return cursor.getFloat(field);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetCursor#getFloat(java.lang.String)
     */
    public double getFloat(String fieldName) throws SqlJetException {
        return cursor.getFloat(fieldName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#getInteger(int)
     */
    public long getInteger(int field) throws SqlJetException {
        return cursor.getInteger(field);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetCursor#getInteger(java.lang.String
     * )
     */
    public long getInteger(String fieldName) throws SqlJetException {
        return cursor.getInteger(fieldName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#getRowId()
     */
    public long getRowId() throws SqlJetException {
        return cursor.getRowId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#getString(int)
     */
    public String getString(int field) throws SqlJetException {
        return cursor.getString(field);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetCursor#getString(java.lang.String)
     */
    public String getString(String fieldName) throws SqlJetException {
        return cursor.getString(fieldName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#getValue(int)
     */
    public Object getValue(int field) throws SqlJetException {
        return cursor.getValue(field);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetCursor#getValue(java.lang.String)
     */
    public Object getValue(String fieldName) throws SqlJetException {
        return cursor.getValue(fieldName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#goTo(long)
     */
    public boolean goTo(long rowId) throws SqlJetException {
        return cursor.goTo(rowId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#isNull(int)
     */
    public boolean isNull(int field) throws SqlJetException {
        return cursor.isNull(field);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetCursor#isNull(java.lang.String)
     */
    public boolean isNull(String fieldName) throws SqlJetException {
        return cursor.isNull(fieldName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#last()
     */
    public boolean last() throws SqlJetException {
        return !(eof = !cursor.first());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#next()
     */
    public boolean next() throws SqlJetException {
        return !(eof = !cursor.previous());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#previous()
     */
    public boolean previous() throws SqlJetException {
        return !(eof = !cursor.next());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetCursor#update(java.lang.Object[])
     */
    public void update(Object... values) throws SqlJetException {
        cursor.update(values);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetCursor#update(org.tmatesoft.sqljet
     * .core.schema.SqlJetConflictAction, java.lang.Object[])
     */
    public void updateOr(SqlJetConflictAction onConflict, Object... values) throws SqlJetException {
        cursor.updateOr(onConflict, values);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetCursor#updateByFieldNames(java
     * .util.Map)
     */
    public void updateByFieldNames(Map<String, Object> values) throws SqlJetException {
        cursor.updateByFieldNames(values);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetCursor#updateByFieldNames(org.
     * tmatesoft.sqljet.core.schema.SqlJetConflictAction, java.util.Map)
     */
    public void updateByFieldNamesOr(SqlJetConflictAction onConflict, Map<String, Object> values) throws SqlJetException {
        cursor.updateByFieldNamesOr(onConflict, values);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#updateWithRowId(long,
     * java.lang.Object[])
     */
    public long updateWithRowId(long rowId, Object... values) throws SqlJetException {
        return cursor.updateWithRowId(rowId, values);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.table.ISqlJetCursor#updateWithRowId(org.tmatesoft
     * .sqljet.core.schema.SqlJetConflictAction, long, java.lang.Object[])
     */
    public long updateWithRowIdOr(SqlJetConflictAction onConflict, long rowId, Object... values) throws SqlJetException {
        return cursor.updateWithRowIdOr(onConflict, rowId, values);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#reverse()
     */

    public ISqlJetCursor reverse() throws SqlJetException {
        return new SqlJetReverseOrderCursor(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#getCurrentRow()
     */
    public long getRowIndex() throws SqlJetException {
        return cursor.getRowIndex();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#getRowCount()
     */
    public long getRowCount() throws SqlJetException {
        return cursor.getRowCount();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#goToRow(long)
     */
    public boolean goToRow(long rowIndex) throws SqlJetException {
        return cursor.goToRow(rowIndex);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#getLimit()
     */
    public long getLimit() {
        return cursor.getLimit();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tmatesoft.sqljet.core.table.ISqlJetCursor#setLimit(int)
     */
    public void setLimit(long limit) throws SqlJetException {
        cursor.setLimit(limit);
    }
}
