/**
 * SqlJetBtreeSchemaTable.java
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
import org.tmatesoft.sqljet.core.internal.ISqlJetBtree;
import org.tmatesoft.sqljet.core.internal.ISqlJetDbHandle;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.vdbe.SqlJetBtreeRecord;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetBtreeSchemaTable extends SqlJetBtreeTable implements ISqlJetBtreeSchemaTable {

    /**
     * @param btree
     * @param rootPage
     * @param write
     * @param index
     * @throws SqlJetException
     */
    public SqlJetBtreeSchemaTable(ISqlJetBtree btree, boolean write) throws SqlJetException {
        super(btree, ISqlJetDbHandle.MASTER_ROOT, write, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeSchemaTable#getTypeField
     * ()
     */
    public String getTypeField() throws SqlJetException {
        return getString(TYPE_FIELD);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeSchemaTable#getNameField
     * ()
     */
    public String getNameField() throws SqlJetException {
        return getString(NAME_FIELD);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeSchemaTable#
     * getTableField()
     */
    public String getTableField() throws SqlJetException {
        return getString(TABLE_FIELD);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeSchemaTable#getPageField
     * ()
     */
    public int getPageField() throws SqlJetException {
        return (int) getInteger(PAGE_FIELD);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeSchemaTable#getSqlField
     * ()
     */
    public String getSqlField() throws SqlJetException {
        return getString(SQL_FIELD);
    }

    private void doInsertRecord(long rowId, String typeField, String nameField, String tableField, int pageField,
            String sqlField) throws SqlJetException {
        final ISqlJetBtreeRecord record = SqlJetBtreeRecord.getRecord(getEncoding(), typeField, nameField, tableField,
                pageField, sqlField);
        final ISqlJetMemoryPointer pData = record.getRawRecord();
        insert(null, rowId, pData, pData.remaining(), 0, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeSchemaTable#insertRecord
     * (java.lang.String, java.lang.String, java.lang.String, long,
     * java.lang.String)
     */
    public long insertRecord(String typeField, String nameField, String tableField, int pageField, String sqlField)
            throws SqlJetException {
        final long rowId = newRowId();
        doInsertRecord(rowId, typeField, nameField, tableField, pageField, sqlField);
        return rowId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeSchemaTable#updateRecord
     * (long, java.lang.String, java.lang.String, java.lang.String, int,
     * java.lang.String)
     */
    public void updateRecord(long rowId, String typeField, String nameField, String tableField, int pageField,
            String sqlField) throws SqlJetException {
        doInsertRecord(rowId, typeField, nameField, tableField, pageField, sqlField);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeSchemaTable#goToRow
     * (long)
     */
    public boolean goToRow(long rowId) throws SqlJetException {
        if (getRowId() == rowId)
            return true;
        final int moveTo = cursor.moveTo(null, rowId, false);
        if (moveTo < 0) {
            next();
        }
        return getRowId() == rowId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.tmatesoft.sqljet.core.internal.table.ISqlJetBtreeSchemaTable#getRowId
     * ()
     */
    public long getRowId() throws SqlJetException {
        return cursor.getKeySize();
    }

}
