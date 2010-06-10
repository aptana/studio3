/**
 * ISqlJetTable.java
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

import java.util.Map;
import java.util.Set;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.schema.ISqlJetIndexDef;
import org.tmatesoft.sqljet.core.schema.ISqlJetTableDef;
import org.tmatesoft.sqljet.core.schema.SqlJetConflictAction;

/**
 * Interface which represents database table.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetTable {

    /**
     * Get database connection.
     * 
     * @return the database connection.
     */
    SqlJetDb getDataBase();

    /**
     * Returns name of primary key index. For tables with INTEGER PRIMARY KEY
     * and tables without primary key it returns null. This method could be used
     * in {@link #lookup(String, Object...)},
     * {@link #scope(String, Object[], Object[])} or {@link #order(String)}
     * methods at first parameter.
     * 
     * @return the primaryKeyIndex name of index or null if table haven't
     *         primary key or have INTEGER PRIMARY KEY.
     */
    String getPrimaryKeyIndexName() throws SqlJetException;

    /**
     * Get table's schema definition.
     */
    ISqlJetTableDef getDefinition() throws SqlJetException;

    /**
     * Returns definitions of indices used in this table.
     * 
     * @return definitions of indices used in this table.
     * @throws SqlJetException
     */
    Set<ISqlJetIndexDef> getIndexesDefs() throws SqlJetException;

    /**
     * Get table indexes names.
     * 
     * @return names of table indexes.
     * @throws SqlJetException
     */
    Set<String> getIndexesNames() throws SqlJetException;

    /**
     * Get definition of index by name.
     * 
     * @param name
     *            name of index.
     * @return definition of index.
     * @throws SqlJetException
     */
    ISqlJetIndexDef getIndexDef(String name) throws SqlJetException;

    /**
     * <p>
     * Open cursor for all table records.
     * </p>
     * 
     * <p>
     * Cursors can be opened only within active transaction. When transaction
     * ends all cursors will be closed.
     * </p>
     * 
     * @return cursor for all table records.
     * @throws SqlJetException
     */
    ISqlJetCursor open() throws SqlJetException;

    /**
     * <p>
     * Open cursor for records which have found by key on index.
     * </p>
     * 
     * <p>
     * If indexName is NULL then primary key will be used.
     * </p>
     * 
     * <p>
     * Cursors can be opened only within active transaction. When transaction
     * ends all cursors will be closed.
     * </p>
     * 
     * @param indexName
     *            Name of the searched index. If null then primary key will be
     *            used.
     * @param key
     *            Key for the index lookup.
     * @return cursor for records which have found by key on index.
     * @throws SqlJetException
     */
    ISqlJetCursor lookup(String indexName, Object... key) throws SqlJetException;

    /**
     * <p>
     * Open cursors which sorts table by index.
     * </p>
     * 
     * <p>
     * If indexName is NULL then primary key will be used.
     * </p>
     * 
     * <p>
     * Cursors can be opened only within active transaction. When transaction
     * ends all cursors will be closed.
     * </p>
     * 
     * @param indexName name of index which defines ordering.If null then primary key
     *                  will be used.
     *                  
     * @return cursor sorted by index.
     * @throws SqlJetException
     */
    ISqlJetCursor order(String indexName) throws SqlJetException;

    /**
     * <p>
     * Open cursor which restricts table to some scope of index values.
     * </p>
     * 
     * <p>
     * Scope is specified as pair of index keys. First key means start of scope
     * and last key means end of scope. One of these keys (or even both) could
     * be NULL. In this case scope is open from one side (or both sides). If
     * first key is less of last key then cursor will be in reversed order. If
     * indexName is NULL then primary key will be used.
     * </p>
     * 
     * <p>
     * Cursors can be opened only within active transaction. When transaction
     * ends all cursors will be closed.
     * </p>
     * 
     * @param indexName
     *            Name of the searched index. If null then primary key will be
     *            used.
     * @param firstKey
     *            first key of scope. Could be NULL.
     * @param lastKey
     *            first key of scope. Could be NULL.
     * @return cursor which have defined scope of rows.
     * @throws SqlJetException
     */
    ISqlJetCursor scope(String indexName, Object[] firstKey, Object[] lastKey) throws SqlJetException;

    /**
     * <p>
     * Add new record to the table with specified values.
     * </p>
     * 
     * <p>
     * Values must be specified by position in table structure. If table have
     * INTEGER PRIMARY KEY column then this column could be null and in this
     * case it value will be defined automatically.
     * <p/>
     * 
     * <p>
     * If field has DEFAULT value then it could be passed as null. If fields
     * have DEFAULT value and are last in table structure then they could be not
     * specified.
     * <p/>
     * 
     * <p>
     * All relevant indexes are updated automatically.
     * 
     * <p>
     * Returns ROWID of inserted record.
     * </p>
     * 
     * <p>
     * Can be used without of active transaction, in this case method begins and
     * ends own internal transaction.
     * </p>
     * 
     * @param values
     *            Values for the new record.
     * @return ROWID of inserted record.
     */
    long insert(Object... values) throws SqlJetException;

    /**
     * <p>
     * Insert record by values by names of fields.
     * </p>
     * 
     * <p>
     * If table have INTEGER PRIMARY KEY column then this column could be null
     * or even not specified and in this case it value will be defined
     * automatically.
     * </p>
     * 
     * <p>
     * The ROWID of record could be passed by any of this names: ROWID, _ROWID_,
     * OID. ROWID could be specified even if table haven't INTEGER PRIMARY KEY
     * column.
     * </p>
     * 
     * <p>
     * All relevant indexes are updated automatically.
     * </p>
     * 
     * <p>
     * Returns ROWID of inserted record.
     * <p/>
     * 
     * <p>
     * Can be used without of active transaction, in this case method begins and
     * ends own internal transaction.
     * </p>
     * 
     * @param values
     *            map of field names with values.
     * @return ROWID of inserted record.
     * @throws SqlJetException
     */
    long insertByFieldNames(Map<String, Object> values) throws SqlJetException;

    /**
     * <p>
     * Inserts record at specified rowId. If rowId is 0 then it generates new
     * rowId.
     * </p>
     * 
     * <p>
     * If table has INTEGER PRIMARY KEY column and rowId isn't 0 then value for
     * this field will be ignored and could be specified just as null. If table
     * has INTEGER PRIMARY KEY column and rowId is 0 then value for this field
     * used as rowId.
     * </p>
     * 
     * <p>
     * If field has DEFAULT value then it could be passed as null. If fields
     * have DEFAULT value and are last in table structure then they could be not
     * specified.
     * </p>
     * 
     * <p>
     * All relevant indexes are updated automatically.
     * </p>
     * 
     * <p>
     * Returns ROWID of inserted record.
     * </p>
     * 
     * <p>
     * Can be used without of active transaction, in this case method begins and
     * ends own internal transaction.
     * </p>
     * 
     * @param rowId
     *            ROWID of record.
     * @param values
     *            Values for the new record.
     * @throws SqlJetException
     */
    long insertWithRowId(long rowId, Object... values) throws SqlJetException;

    /**
     * <p>
     * Add new record to the table with specified values.
     * </p>
     * 
     * <p>
     * Values must be specified by position in table structure.
     * </p>
     * 
     * <p>
     * If table have INTEGER PRIMARY KEY column then this column could be null
     * and in this case it value will be defined automatically.
     * </p>
     * 
     * <p>
     * If field has DEFAULT value then it could be passed as null. If fields
     * have DEFAULT value and are last in table structure then they could be not
     * specified.
     * </p>
     * 
     * <p>
     * All relevant indexes are updated automatically.
     * </p>
     * 
     * <p>
     * Returns ROWID of inserted record.
     * </p>
     * 
     * <p>
     * Can be used without of active transaction, in this case method begins and
     * ends own internal transaction.
     * </p>
     * 
     * <p>
     * Implements ON CONFLICT clause. See {@link SqlJetConflictAction}.
     * </p>
     * 
     * @param onConflict
     *            {@link SqlJetConflictAction}.
     * @param values
     *            Values for the new record.
     * @return ROWID of inserted record.
     * @throws SqlJetException
     * 
     */
    long insertOr(SqlJetConflictAction onConflict, Object... values) throws SqlJetException;

    /**
     * <p>
     * Insert record by values by names of fields.
     * </p>
     * 
     * <p>
     * If table have INTEGER PRIMARY KEY column then this column could be null
     * or even not specified and in this case it value will be defined
     * automatically.
     * </p>
     * 
     * <p>
     * The ROWID of record could be passed by any of this names: ROWID, _ROWID_,
     * OID. ROWID could be specified even if table haven't INTEGER PRIMARY KEY
     * column.
     * </p>
     * 
     * <p>
     * All relevant indexes are updated automatically.
     * </p>
     * 
     * <p>
     * Returns ROWID of inserted record.
     * </p>
     * 
     * <p>
     * Can be used without of active transaction, in this case method begins and
     * ends own internal transaction.
     * </p>
     * 
     * <p>
     * Implements ON CONFLICT clause. See {@link SqlJetConflictAction}.
     * </p>
     * 
     * @param onConflict
     *            {@link SqlJetConflictAction}.
     * @param values
     *            Values for the new record.
     * @return ROWID of inserted record.
     * @throws SqlJetException
     */
    long insertByFieldNamesOr(SqlJetConflictAction onConflict, Map<String, Object> values) throws SqlJetException;

    /**
     * <p>
     * Inserts record at specified rowId.
     * </p>
     * 
     * <p>
     * If rowId is 0 then it generates new rowId.
     * </p>
     * 
     * <p>
     * If table has INTEGER PRIMARY KEY column and rowId isn't 0 then value for
     * this field will be ignored and could be specified just as null. If table
     * has INTEGER PRIMARY KEY column and rowId is 0 then value for this field
     * used as rowId.
     * </p>
     * 
     * <p>
     * If field has DEFAULT value then it could be passed as null. If fields
     * have DEFAULT value and are last in table structure then they could be not
     * specified.
     * </p>
     * 
     * <p>
     * All relevant indexes are updated automatically.
     * </p>
     * 
     * <p>
     * Returns ROWID of inserted record.
     * </p>
     * 
     * <p>
     * Can be used without of active transaction, in this case method begins and
     * ends own internal transaction.
     * </p>
     * 
     * <p>
     * Implements ON CONFLICT clause. See {@link SqlJetConflictAction}.
     * </p>
     * 
     * @param onConflict
     *            {@link SqlJetConflictAction}.
     * @param rowId
     *            ROWID of record.
     * @param values
     *            Values for the new record.
     * @return ROWID of inserted record.
     * @throws SqlJetException
     */
    long insertWithRowIdOr(SqlJetConflictAction onConflict, long rowId, Object... values) throws SqlJetException;

    /**
     * Clear table. It fast delete of all rows in table.
     * 
     * @throws SqlJetException
     */
    void clear() throws SqlJetException;
}
