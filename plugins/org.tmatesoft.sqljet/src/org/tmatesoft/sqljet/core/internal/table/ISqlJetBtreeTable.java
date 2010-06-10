/**
 * ISqlJetBtreeTable.java
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

import org.tmatesoft.sqljet.core.SqlJetEncoding;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetValueType;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;

/**
 * Interface of table over B-Tree.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetBtreeTable {

    /**
     * Close B-Tree table cursor.
     * 
     * @throws SqlJetException
     */
    void close() throws SqlJetException;

    /**
     * Lock B-Tree table cursor's mutex.
     * 
     * @throws SqlJetException
     */
    void lock() throws SqlJetException;

    /**
     * Unlock B-Tree table cursror's mutex.
     */
    void unlock();

    /**
     * Check of end of B-Tree table cursor.
     * 
     * @return true if there is not more records.
     * @throws SqlJetException
     */
    boolean eof() throws SqlJetException;

    /**
     * Go to first record.
     * 
     * @return true if there is at least one record.
     * @throws SqlJetException
     */
    boolean first() throws SqlJetException;

    /**
     * Go to last record.
     * 
     * @return true if there is at least one record.
     * @throws SqlJetException
     */
    boolean last() throws SqlJetException;

    /**
     * Go to next record.
     * 
     * @return true if there is at least one record and end of cursor is not
     *         reached yet
     * @throws SqlJetException
     */
    boolean next() throws SqlJetException;

    /**
     * Go to previous record.
     * 
     * @return true if there is at least one record and begin of cursor is not
     *         reached yet
     * @throws SqlJetException
     */
    boolean previous() throws SqlJetException;

    /**
     * @return
     * @throws SqlJetException
     */
    ISqlJetBtreeRecord getRecord() throws SqlJetException;

    /**
     * @param write
     */
    void lockTable(boolean write);

    /**
     * @return
     * @throws SqlJetException
     */
    SqlJetEncoding getEncoding() throws SqlJetException;

    /**
     * Get count of fields in current record.
     * 
     * @return count of fields
     * @throws SqlJetException
     */
    int getFieldsCount() throws SqlJetException;

    /**
     * Get type of field.
     * 
     * @param field
     *            number of field begin from zero
     * @return type of field
     * @throws SqlJetException
     */
    SqlJetValueType getFieldType(int field) throws SqlJetException;

    /**
     * Check to field has null value.
     * 
     * @param field
     *            number of field begin from zero
     * @return true if field value is null
     * @throws SqlJetException
     */
    boolean isNull(int field) throws SqlJetException;

    /**
     * Get field's value as string.
     * 
     * @param field
     *            number of field begin from zero
     * @return field's value as string
     * @throws SqlJetException
     */
    String getString(int field) throws SqlJetException;

    /**
     * Get field's value as integer.
     * 
     * @param field
     *            number of field begin from zero
     * @return field's value as integer
     * @throws SqlJetException
     */
    long getInteger(int field) throws SqlJetException;

    /**
     * Get field's value as real (float).
     * 
     * @param field
     *            number of field begin from zero
     * @return field's value as real
     * @throws SqlJetException
     */
    double getFloat(int field) throws SqlJetException;

    /**
     * Get field's value as BLOB.
     * 
     * @param field
     *            number of field begin from zero
     * @return field's value as BLOB
     * @throws SqlJetException
     */
    ISqlJetMemoryPointer getBlob(int field) throws SqlJetException;

    /**
     * Get values as objects.
     * 
     * @return
     * @throws SqlJetException
     */
    Object[] getValues() throws SqlJetException;

    /**
     * Get value as object.
     * 
     * @param field
     * @return
     * @throws SqlJetException
     */
    Object getValue(int field) throws SqlJetException;

    /**
     * Returns true if cursor has been moved. Side effect of this is restore
     * position of cursor.
     * 
     * @return
     * @throws SqlJetException
     */
    boolean hasMoved() throws SqlJetException;

    /**
     * @throws SqlJetException
     */
    void clear() throws SqlJetException;

    /**
     * @throws SqlJetException
     */
    void delete() throws SqlJetException;

}
