/**
 * ISqlJetOptions.java
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

import org.tmatesoft.sqljet.core.SqlJetEncoding;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.internal.ISqlJetLimits;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;

/**
 * Database options.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public interface ISqlJetOptions {

    /**
     * Name of system property for default encoding.
     */
    String SQLJET_DEFAULT_ENCODING_PROPERTY = "SQLJET_DEFAULT_ENCODING";

    /**
     * Default encoding.
     */
    SqlJetEncoding SQLJET_DEFAULT_ENCODING = SqlJetUtility.getEnumSysProp(SQLJET_DEFAULT_ENCODING_PROPERTY,
            SqlJetEncoding.UTF8);

    /**
     * Name of system property for legacy file format support.
     */
    String SQLJET_LEGACY_FILE_FORMAT_PROPERTY = "SQLJET_LEGACY_FILE_FORMAT";

    /**
     * Legacy file format support.
     */
    boolean SQLJET_LEGACY_FILE_FORMAT = SqlJetUtility.getBoolSysProp(SQLJET_LEGACY_FILE_FORMAT_PROPERTY, true);

    /**
     * Name of system property for default file format.
     */
    String SQLJET_DEFAULT_FILE_FORMAT_PROPERTY = "SQLJET_DEFAULT_FILE_FORMAT";

    /**
     * Default file format.
     */
    int SQLJET_DEFAULT_FILE_FORMAT = SqlJetUtility.getIntSysProp(SQLJET_DEFAULT_FILE_FORMAT_PROPERTY,
            SQLJET_LEGACY_FILE_FORMAT ? ISqlJetLimits.SQLJET_MIN_FILE_FORMAT : ISqlJetLimits.SQLJET_MAX_FILE_FORMAT);

    /**
     * File format of schema layer.
     * 
     * @return the fileFormat
     */
    int getFileFormat() throws SqlJetException;

    /**
     * Set file format. It's allowed only on new empty data base. It can't be
     * performed in active transaction.
     * 
     * @param fileFormat
     * @throws SqlJetException
     */
    void setFileFormat(int fileFormat) throws SqlJetException;

    // Pragmas to modify library operation

    /**
     * Use freelist if false. Autovacuum if true.
     * 
     * @return the autovacuum
     */
    boolean isAutovacuum() throws SqlJetException;

    /**
     * Set autovacuum flag. It's allowed only on new empty data base. It can't
     * be performed in active transaction.
     * 
     * @param autovacuum
     * @throws SqlJetException
     */
    void setAutovacuum(boolean autovacuum) throws SqlJetException;

    /**
     * Incremental-vacuum flag.
     * 
     * @return the incrementalVacuum
     */
    boolean isIncrementalVacuum() throws SqlJetException;

    /**
     * Set incremental vacuum flag. It's allowed only on new empty data base. It
     * can't be performed in active transaction.
     * 
     * @param incrementalVacuum
     * @throws SqlJetException
     */
    void setIncrementalVacuum(boolean incrementalVacuum) throws SqlJetException;

    /**
     * Size of the page cache.
     * 
     * @return the pageCacheSize
     */
    int getCacheSize() throws SqlJetException;

    /**
     * Set page cache's size. It can be performed only in active transaction.
     * 
     * @param pageCacheSize
     * @throws SqlJetException
     */
    void setCacheSize(int pageCacheSize) throws SqlJetException;

    // case_sensitive_like
    // count_changes
    // default_cache_size

    /**
     * Db text encoding.
     * 
     * @return the encoding
     */
    SqlJetEncoding getEncoding() throws SqlJetException;

    /**
     * Set encoding. It's allowed only on new empty data base. It can't be
     * performed in active transaction.
     * 
     * @param encoding
     * @throws SqlJetException
     */
    void setEncoding(SqlJetEncoding encoding) throws SqlJetException;

    // full_column_names
    // fullfsync
    // incremental_vacuum
    // journal_mode
    // journal_size_limit

    /**
     * Checks if legacy file format is used for the new databases.
     */
    boolean isLegacyFileFormat() throws SqlJetException;

    /**
     * Instructs SQLJet to use legacy file format for all new databases.
     */
    void setLegacyFileFormat(boolean flag) throws SqlJetException;

    // locking_mode
    // page_size
    // max_page_count
    // read_uncommitted
    // reverse_unordered_selects
    // short_column_names
    // synchronous
    // temp_store
    // temp_store_directory

    // Pragmas to query the database schema

    // collation_list
    // database_list
    // foreign_key_list
    // freelist_count
    // index_info
    // index_list
    // page_count
    // table_info

    // Pragmas to query/modify version values

    /**
     * Schema cookie. Changes with each schema change.
     * 
     * @return the schemaCookie
     */
    int getSchemaVersion() throws SqlJetException;

    /**
     * Set schema version. It can be performed only in active transaction.
     * 
     * @param version
     * @throws SqlJetException
     */
    void setSchemaVersion(int version) throws SqlJetException;

    /**
     * Change SchemaCookie. It can be performed only in active transaction
     */
    void changeSchemaVersion() throws SqlJetException;

    /**
     * Verify schema cookie and return true if it is unchanged by other process.
     * 
     * If throwIfStale is true then throw exception if cookie is changed by
     * other process.
     * 
     * @param throwIfStale
     * @return true of schema has not been changed
     * 
     * @throws SqlJetException
     */
    boolean verifySchemaVersion(boolean throwIfStale) throws SqlJetException;

    /**
     * The user cookie. Used by the application.
     * 
     * @return the userCookie
     */
    int getUserVersion() throws SqlJetException;

    /**
     * Set user's cookie. It can be performed only in active transaction.
     * 
     * @param userCookie
     * @throws SqlJetException
     */
    void setUserVersion(int userCookie) throws SqlJetException;

    // Pragmas to debug the library

    // integrity_check
    // quick_check
    // parser_trace
    // vdbe_trace
    // vdbe_listing
}
