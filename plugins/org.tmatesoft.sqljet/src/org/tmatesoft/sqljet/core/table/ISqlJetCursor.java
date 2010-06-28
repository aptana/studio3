/**
 * ISqlJetCursor.java
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
package org.tmatesoft.sqljet.core.table;

import java.io.InputStream;
import java.util.Map;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetValueType;
import org.tmatesoft.sqljet.core.schema.SqlJetConflictAction;

/**
 * This class represents table cursor that may be used to browse over records in
 * the table, to modify or delete existing records.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetCursor {

    /**
     * Closes the cursor.
     * 
     * @throws SqlJetException
     */
    void close() throws SqlJetException;

    // Positioning

    /**
     * Gets row Id of the current record.
     * 
     * @return row Id of the current record.
     * @throws SqlJetException
     */
    long getRowId() throws SqlJetException;

    /**
     * Goes to the record with the specified row Id.
     * 
     * @param rowId
     *            row Id
     * @return true if cursor was moved successfully.
     * @throws SqlJetException
     */
    boolean goTo(long rowId) throws SqlJetException;

    /**
     * Tests whether this cursor is positioned behind the last record.
     * 
     * @return true if the cursor is not on a record and fields can't be read.
     * @throws SqlJetException
     */
    boolean eof() throws SqlJetException;

    /**
     * Goes to the first record.
     * 
     * @return true if there is at least one record.
     * @throws SqlJetException
     */
    boolean first() throws SqlJetException;

    /**
     * Goes to the last record.
     * 
     * @return true if there is at least one record.
     * @throws SqlJetException
     */
    boolean last() throws SqlJetException;

    /**
     * Goes to the next record.
     * 
     * @return true if there is at least one record and end of cursor is not
     *         reached yet
     * @throws SqlJetException
     */
    boolean next() throws SqlJetException;

    /**
     * Goes to the previous record.
     * 
     * @return true if there is at least one record and begin of cursor is not
     *         reached yet
     * @throws SqlJetException
     */
    boolean previous() throws SqlJetException;

    // Record Access

    /**
     * Returns number of fields in the current record.
     * 
     * @throws SqlJetException
     */
    int getFieldsCount() throws SqlJetException;

    /**
     * Returns field type.
     * 
     * @param field
     *            index of the field
     * @return type of field
     * @throws SqlJetException
     */
    SqlJetValueType getFieldType(int field) throws SqlJetException;

    /**
     * Returns field type.
     * 
     * @param fieldName
     *            name of the field
     * @return type of field
     * @throws SqlJetException
     */
    SqlJetValueType getFieldType(String fieldName) throws SqlJetException;

    /**
     * Tests field value for null.
     * 
     * @param field
     *            number of field begin from zero
     * @return true if field value is null
     * @throws SqlJetException
     */
    boolean isNull(int field) throws SqlJetException;

    /**
     * Tests field value for null.
     * 
     * @return true if field value is null
     * @throws SqlJetException
     */
    boolean isNull(String fieldName) throws SqlJetException;

    /**
     * Returns specified field's value as String.
     * 
     * @param field
     *            index of the field
     * @return field's value as string
     * @throws SqlJetException
     */
    String getString(int field) throws SqlJetException;

    /**
     * Returns specified field's value as String.
     * 
     * @param fieldName
     *            name of the field
     * @return field's value as string
     * @throws SqlJetException
     */
    String getString(String fieldName) throws SqlJetException;

    /**
     * Returns specified field's value as integer.
     * 
     * @param field
     *            index of the field
     * @return field's value as integer
     * @throws SqlJetException
     */
    long getInteger(int field) throws SqlJetException;

    /**
     * Returns specified field's value as integer.
     * 
     * @param fieldName
     *            name of the field
     * @throws SqlJetException
     */
    long getInteger(String fieldName) throws SqlJetException;

    /**
     * Returns specified field's value as float.
     * 
     * @param field
     *            index of the field
     * @return field's value as real
     * @throws SqlJetException
     */
    double getFloat(int field) throws SqlJetException;

    /**
     * Returns specified field's value as float.
     * 
     * @param fieldName
     *            name of the field
     * @throws SqlJetException
     */
    double getFloat(String fieldName) throws SqlJetException;

    /**
     * Returns specified field's value as boolean.
     * 
     * @param field
     *            index of the field
     * @return field value
     * @throws SqlJetException
     */
    boolean getBoolean(int field) throws SqlJetException;

    /**
     * Returns specified field's value as boolean.
     * 
     * @param fieldName
     *            name of the field
     * @return field value
     * @throws SqlJetException
     */
    boolean getBoolean(String fieldName) throws SqlJetException;

    /**
     * Returns specified field's value as BLOB.
     * 
     * @param field
     *            index of the field
     * @return field's value as BLOB
     * @throws SqlJetException
     */
    byte[] getBlobAsArray(int field) throws SqlJetException;

    /**
     * Returns specified field's value as BLOB.
     * 
     * @param fieldName
     *            name of the field
     * @return field's value as BLOB
     * @throws SqlJetException
     */
    byte[] getBlobAsArray(String fieldName) throws SqlJetException;

    /**
     * Returns specified field's value as BLOB.
     * 
     * @param field
     *            number of field begin from zero
     * @return field's value as BLOB
     * @throws SqlJetException
     */
    InputStream getBlobAsStream(int field) throws SqlJetException;

    /**
     * Returns specified field's value as BLOB.
     * 
     * @param fieldName name of the field
     * @return field's value as BLOB
     * @throws SqlJetException
     */
    InputStream getBlobAsStream(String fieldName) throws SqlJetException;

    /**
     * Returns value of the field of the specified index in the current row.
     * 
     * @param field index of the field
     * @throws SqlJetException
     */
    Object getValue(int field) throws SqlJetException;

    /**
     * Returns value of the field with the specified name in the current row.
     * 
     * @param fieldName
     *            name of the field
     * @throws SqlJetException
     */
    Object getValue(String fieldName) throws SqlJetException;

    // Modification

    /**
     * Updates the current record.
     * 
     * @param values
     *            New record values.
     * @throws SqlJetException
     */
    void update(Object... values) throws SqlJetException;

    /**
     * Updates rowId and values in the current record.
     * 
     * @param values
     *            New record values.
     * @throws SqlJetException
     */
    long updateWithRowId(long rowId, Object... values) throws SqlJetException;

    /**
     * Updates the current record.
     * 
     * @param values
     *            New record values mapped by field names.
     * @throws SqlJetException
     */
    void updateByFieldNames(Map<String, Object> values) throws SqlJetException;

    /**
     * Updates the current record.
     * 
     * Implements ON CONFLICT clause. See {@link SqlJetConflictAction}.
     * 
     * @param onConflict
     *            {@link SqlJetConflictAction}.
     * @param values
     *            New record values.
     * @throws SqlJetException
     */
    void updateOr(SqlJetConflictAction onConflict, Object... values) throws SqlJetException;

    /**
     * Updates rowId and values in the current record.
     * 
     * Implements ON CONFLICT clause. See {@link SqlJetConflictAction}.
     * 
     * @param onConflict
     *            {@link SqlJetConflictAction}.
     * @param values
     *            New record values.
     * @throws SqlJetException
     */
    long updateWithRowIdOr(SqlJetConflictAction onConflict, long rowId, Object... values) throws SqlJetException;

    /**
     * Updates the current record.
     * 
     * Implements ON CONFLICT clause. See {@link SqlJetConflictAction}.
     * 
     * @param onConflict
     *            {@link SqlJetConflictAction}.
     * @param values
     *            New record values mapped by field names.
     * @throws SqlJetException
     */
    void updateByFieldNamesOr(SqlJetConflictAction onConflict, Map<String, Object> values) throws SqlJetException;

    /**
     * Deletes the current record.
     * 
     * @throws SqlJetException
     */
    void delete() throws SqlJetException;

    /**
     * Returns cursor with the order reversed.
     * 
     * @return cursor that will traverse the same rows as this one, but in reversed order. 
     * @throws SqlJetException
     */
    ISqlJetCursor reverse() throws SqlJetException;

    /**
     * Returns number of rows accessible with this cursor.
     * 
     * @return number of rows
     * @throws SqlJetException
     */
    long getRowCount() throws SqlJetException;

    /**
     * Returns index of the current row. Index is 1-based, first record has
     * index of one.
     * 
     * @return 1-based index of the current row.
     *  
     * @throws SqlJetException
     */
    long getRowIndex() throws SqlJetException;

    /**
     * Goes to the row with the specified index. Index is 1-based, first record
     * has index of one.
     * 
     * @param rowIndex
     * @return true if cursor has been set on the specified record.
     * @throws SqlJetException
     */
    boolean goToRow(long rowIndex) throws SqlJetException;

    /**
     * Sets limit for this cursor. Negative or zero value resets limit to
     * infinity.
     * 
     * @param limit
     *            limit to set or zero to drop the limit.
     * @throws SqlJetException
     */
    void setLimit(long limit) throws SqlJetException;

    /**
     * Returns limit of this cursor.
     * 
     * @return limit of this cursor or zero if limit has not been set.
     */
    long getLimit();

}
