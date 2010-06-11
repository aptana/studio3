/**
 * ISqlJetBtreeSchemaTable.java
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

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetBtreeSchemaTable extends ISqlJetBtreeTable {

    int TYPE_FIELD = 0;
    int NAME_FIELD = 1;
    int TABLE_FIELD = 2;
    int PAGE_FIELD = 3;
    int SQL_FIELD = 4;

    String getTypeField() throws SqlJetException;

    String getNameField() throws SqlJetException;

    String getTableField() throws SqlJetException;

    int getPageField() throws SqlJetException;

    String getSqlField() throws SqlJetException;

    long insertRecord(String typeField, String nameField, String tableField, int pageField, String sqlField)
            throws SqlJetException;

    boolean goToRow(long rowId) throws SqlJetException;

    long getRowId() throws SqlJetException;

    /**
     * @param rowId
     * @param typeField
     * @param nameField
     * @param tableField
     * @param pageField
     * @param sqlField
     * @throws SqlJetException
     */
    void updateRecord(long rowId, String typeField, String nameField, String tableField, int pageField, String sqlField)
            throws SqlJetException;

}