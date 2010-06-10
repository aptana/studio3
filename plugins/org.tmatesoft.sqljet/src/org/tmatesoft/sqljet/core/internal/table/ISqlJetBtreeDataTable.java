/**
 * ISqlJetBtreeDataTable.java
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

import java.util.Map;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.schema.ISqlJetIndexDef;
import org.tmatesoft.sqljet.core.schema.ISqlJetTableDef;
import org.tmatesoft.sqljet.core.schema.SqlJetConflictAction;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetBtreeDataTable extends ISqlJetBtreeTable {

    /**
     * Get table's schema definition.
     * 
     * @return
     */
    ISqlJetTableDef getDefinition();

    /**
     * Get definitions of table's indexes.
     * 
     * @return
     */
    Map<String, ISqlJetIndexDef> getIndexDefinitions();

    /**
     * Get indexes which are related with table.
     * 
     * @return
     */
    Map<String, ISqlJetBtreeIndexTable> getIndexesTables();

    /**
     * Go to record with given rowID. Return boolean to indicate success.
     * 
     * @param rowId
     * @return
     * @throws SqlJetException
     */
    boolean goToRow(long rowId) throws SqlJetException;

    /**
     * Get current rowID.
     * 
     * @return
     * @throws SqlJetException
     */
    long getRowId() throws SqlJetException;

    /**
     * Write an new entry into the table.
     * 
     * @param values
     * @throws SqlJetException
     */
    long insert(SqlJetConflictAction onConflict, Object... values) throws SqlJetException;

    /**
     * Update an entry in the table by rowId.
     * 
     * @param rowId
     * @param values
     * @throws SqlJetException
     */
    void update(SqlJetConflictAction onConflict, long rowId, Object... values) throws SqlJetException;

    /**
     * Update the current entry in the table.
     * 
     * @param values
     * @throws SqlJetException
     */
    void updateCurrent(SqlJetConflictAction onConflict, Object... values) throws SqlJetException;

    /**
     * Update the rowId and values an entry in the table by rowId.
     * 
     * @param rowId
     * @param values
     * @throws SqlJetException
     */
    long updateWithRowId(SqlJetConflictAction onConflict, long rowId, long newRowId, Object... values) throws SqlJetException;

    /**
     * Update the rowId and values in current entry in the table.
     * 
     * @param values
     * @throws SqlJetException
     */
    long updateCurrentWithRowId(SqlJetConflictAction onConflict, long newRowId, Object... values) throws SqlJetException;
    
    /**
     * Delete record by row's ID.
     * 
     * @param rowId
     * @throws SqlJetException
     */
    void delete(long rowId) throws SqlJetException;

    /**
     * Delete curent record.
     * 
     * @throws SqlJetException
     */
    void delete() throws SqlJetException;

    /**
     * Check the current record is equal to key using definition of index.
     * 
     * @param indexName
     * @param key
     * @return
     * @throws SqlJetException
     */
    boolean checkIndex(String indexName, Object[] key) throws SqlJetException;

    /**
     * Get name of index which has been auto-created for primary key.
     * 
     * @return the primaryKeyIndex
     */
    String getPrimaryKeyIndex();

    /**
     * Locate record which using index by key. Key is values for fields which
     * are defined in index. If record is found then returns true. If next is
     * true then locate record by next entry in index for key.
     * 
     * @param indexName
     * @param next
     * @param key
     * @return
     * @throws SqlJetException
     */
    public boolean locate(String indexName, boolean next, Object... key) throws SqlJetException;

    /**
     * Insert record by values by names of fields.
     * 
     * @param values
     * @return
     * @throws SqlJetException
     */
    long insert(SqlJetConflictAction onConflict, Map<String, Object> values) throws SqlJetException;

    /**
     * @param rowId
     * @param values
     * @throws SqlJetException
     */
    void update(SqlJetConflictAction onConflict, long rowId, Map<String, Object> values) throws SqlJetException;

    /**
     * @param values
     * @throws SqlJetException
     */
    void update(SqlJetConflictAction onConflict, Map<String, Object> values) throws SqlJetException;

    /**
     * @param indexName
     * @return
     */
    boolean isIndexExists(String indexName);

    /**
     * @param rowId
     * @param values
     * @return
     * @throws SqlJetException 
     */
    long insertWithRowId(SqlJetConflictAction onConflict, long rowId, Object[] values) throws SqlJetException;

    /**
     * @param indexName
     * @return
     */
    ISqlJetBtreeIndexTable getIndex(String indexName);

}