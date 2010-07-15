/**
 * SqlJetOptions.java
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
import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.internal.ISqlJetBtree;
import org.tmatesoft.sqljet.core.internal.ISqlJetDbHandle;
import org.tmatesoft.sqljet.core.internal.ISqlJetLimits;
import org.tmatesoft.sqljet.core.internal.SqlJetAutoVacuumMode;
import org.tmatesoft.sqljet.core.internal.pager.SqlJetPageCache;
import org.tmatesoft.sqljet.core.table.ISqlJetOptions;

/**
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetOptions implements ISqlJetOptions {

    private static final int SCHEMA_COOKIE = 1;

    private static final int FILE_FORMAT = 2;

    private static final int PAGE_CACHE_SIZE = 3;

    private static final int AUTOVACUUM = 4;

    private static final int ENCODING = 5;

    private static final int USER_COOKIE = 6;

    private static final int INCREMENTAL_VACUUM = 7;

    private final ISqlJetBtree btree;
    private final ISqlJetDbHandle dbHandle;

    /**
     * Schema cookie. Changes with each schema change.
     */
    private int schemaCookie;

    /**
     * File format of schema layer.
     */
    private int fileFormat = SQLJET_DEFAULT_FILE_FORMAT;

    /**
     * Size of the page cache.
     */
    private int pageCacheSize = SqlJetPageCache.PAGE_CACHE_SIZE_DEFAULT;

    /**
     * Use freelist if false. Autovacuum if true.
     */
    private boolean autovacuum = ISqlJetBtree.SQLJET_DEFAULT_AUTOVACUUM != SqlJetAutoVacuumMode.NONE;

    /**
     * Db text encoding.
     */
    private SqlJetEncoding encoding = SQLJET_DEFAULT_ENCODING;

    /**
     * The user cookie. Used by the application.
     */
    private int userCookie;

    /**
     * Incremental-vacuum flag.
     */
    private boolean incrementalVacuum = ISqlJetBtree.SQLJET_DEFAULT_AUTOVACUUM == SqlJetAutoVacuumMode.INCR;

    public SqlJetOptions(ISqlJetBtree btree, ISqlJetDbHandle dbHandle) throws SqlJetException {
        this.btree = btree;
        this.dbHandle = dbHandle;
        if (readSchemaCookie() == 0) {
            try{
                initMeta();
            } catch(SqlJetException e) {
                if(SqlJetErrorCode.READONLY!=e.getErrorCode()) {
                    throw e;
                }
                // we can't init meta on read-only file ...
                // but if you want, we could just don't worry.
            }
        } else {
            readMeta();
        }
    }

    /**
     * Get the database meta information.
     * 
     * Meta values are as follows:
     * 
     * <table border="1">
     * <tr>
     * <td>meta[1]</td>
     * <td>Schema cookie. Changes with each schema change.</td>
     * </tr>
     * <tr>
     * <td>meta[2]</td>
     * <td>File format of schema layer.</td>
     * </tr>
     * <tr>
     * <td>meta[3]</td>
     * <td>Size of the page cache.</td>
     * </tr>
     * <tr>
     * <td>meta[4]</td>
     * <td>Use freelist if 0. Autovacuum if greater than zero.</td>
     * </tr>
     * <tr>
     * <td>meta[5]</td>
     * <td>Db text encoding. 1:UTF-8 2:UTF-16LE 3:UTF-16BE</td>
     * </tr>
     * <tr>
     * <td>meta[6]</td>
     * <td>The user cookie. Used by the application.</td>
     * </tr>
     * <tr>
     * <td>meta[7]</td>
     * <td>Incremental-vacuum flag.</td>
     * </tr>
     * </table>
     * 
     * @throws SqlJetException
     * 
     */
    private void readMeta() throws SqlJetException {
        schemaCookie = readSchemaCookie();
        autovacuum = readAutoVacuum();
        fileFormat = readFileFormat();
        incrementalVacuum = readIncrementalVacuum();
        userCookie = readUserCookie();
        pageCacheSize = readPageCacheSize();
        encoding = readEncoding();
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ENCODING: " + encoding + "\n");
        sb.append("SCHEMA VERSION: " + schemaCookie + "\n");
        sb.append("USER VERSION: " + userCookie + "\n");
        sb.append("FILE FORMAT: " + fileFormat + "\n");
        sb.append("AUTOVACUUM: " + autovacuum + "\n");
        sb.append("CACHE SIZE: " + pageCacheSize);
        return sb.toString();
    }

    private SqlJetEncoding readEncoding() throws SqlJetException {
        switch (btree.getMeta(ENCODING)) {
        case 0:
            if (readSchemaCookie() != 0)
                throw new SqlJetException(SqlJetErrorCode.CORRUPT);
        case 1:
            return SqlJetEncoding.UTF8;
        case 2:
            return SqlJetEncoding.UTF16LE;
        case 3:
            return SqlJetEncoding.UTF16BE;
        default:
            throw new SqlJetException(SqlJetErrorCode.CORRUPT);
        }
    }

    private boolean readIncrementalVacuum() throws SqlJetException {
        return btree.getMeta(INCREMENTAL_VACUUM) != 0;
    }

    private int readUserCookie() throws SqlJetException {
        return btree.getMeta(USER_COOKIE);
    }

    private boolean readAutoVacuum() throws SqlJetException {
        return btree.getMeta(AUTOVACUUM) != 0;
    }

    private int readPageCacheSize() throws SqlJetException {
        int meta = btree.getMeta(PAGE_CACHE_SIZE);
        return meta > 0 ? meta : ISqlJetLimits.SQLJET_DEFAULT_CACHE_SIZE;
    }

    private int readFileFormat() throws SqlJetException {
        final int fileFormat = btree.getMeta(FILE_FORMAT);
        checkFileFormat(fileFormat);
        return fileFormat;
    }

    private void checkFileFormat(final int fileFormat) throws SqlJetException {
        if (fileFormat < ISqlJetLimits.SQLJET_MIN_FILE_FORMAT || fileFormat > ISqlJetLimits.SQLJET_MAX_FILE_FORMAT)
            throw new SqlJetException(SqlJetErrorCode.CORRUPT);
    }

    private int readSchemaCookie() throws SqlJetException {
        return btree.getMeta(SCHEMA_COOKIE);
    }

    public int getSchemaVersion() throws SqlJetException {
        return schemaCookie;
    }

    public int getFileFormat() throws SqlJetException {
        return fileFormat;
    }

    public int getCacheSize() throws SqlJetException {
        return pageCacheSize;
    }

    public boolean isAutovacuum() throws SqlJetException {
        return autovacuum;
    }

    public SqlJetEncoding getEncoding() throws SqlJetException {
        return encoding;
    }

    public boolean isLegacyFileFormat() throws SqlJetException {
        return fileFormat==ISqlJetLimits.SQLJET_MIN_FILE_FORMAT;
    }

    public void setLegacyFileFormat(boolean flag) throws SqlJetException {
        fileFormat=flag?ISqlJetLimits.SQLJET_MIN_FILE_FORMAT:ISqlJetLimits.SQLJET_MAX_FILE_FORMAT;
    }

    public int getUserVersion() throws SqlJetException {
        return userCookie;
    }

    public boolean isIncrementalVacuum() throws SqlJetException {
        return incrementalVacuum;
    }

    public void setSchemaVersion(int version) throws SqlJetException {
        dbHandle.getMutex().enter();
        try {
            if (!btree.isInTrans())
                throw new SqlJetException("It can be performed only in active transaction");
            verifySchemaVersion(true);
            writeSchemaCookie(this.schemaCookie = version);
        } finally {
            dbHandle.getMutex().leave();
        }
    }

    public boolean verifySchemaVersion(boolean throwIfStale) throws SqlJetException {
        dbHandle.getMutex().enter();
        try {
            final boolean stale = (schemaCookie != btree.getMeta(1));
            if (stale && throwIfStale) {
                throw new SqlJetException(SqlJetErrorCode.SCHEMA);
            }
            return !stale;
        } finally {
            dbHandle.getMutex().leave();
        }
    }

    public void changeSchemaVersion() throws SqlJetException {
        dbHandle.getMutex().enter();
        try {
            if (!btree.isInTrans())
                throw new SqlJetException("It can be performed only in active transaction");
            verifySchemaVersion(true);
            schemaCookie++;
            writeSchemaCookie(schemaCookie);
        } finally {
            dbHandle.getMutex().leave();
        }
    }

    private void initMeta() throws SqlJetException {
        btree.beginTrans(SqlJetTransactionMode.EXCLUSIVE);
        try {
            schemaCookie = 1;
            writeSchemaCookie(schemaCookie);
            writeFileFormat(fileFormat);
            writePageCacheSize(pageCacheSize);
            writeEncoding(encoding);
            final SqlJetAutoVacuumMode btreeAutoVacuum = btree.getAutoVacuum();
            autovacuum = SqlJetAutoVacuumMode.NONE != btreeAutoVacuum;
            incrementalVacuum = SqlJetAutoVacuumMode.INCR == btreeAutoVacuum;
            writeAutoVacuum(autovacuum);
            writeIncrementalVacuum(incrementalVacuum);
            btree.commit();
        } catch (SqlJetException e) {
            btree.rollback();
            throw e;
        }
    }

    private void writeSchemaCookie(int schemaCookie) throws SqlJetException {
        btree.updateMeta(SCHEMA_COOKIE, schemaCookie);
    }

    private void writeEncoding(SqlJetEncoding encoding) throws SqlJetException {
        switch (encoding) {
        case UTF8:
            btree.updateMeta(ENCODING, 1);
            break;
        case UTF16LE:
            btree.updateMeta(ENCODING, 2);
            break;
        case UTF16BE:
            btree.updateMeta(ENCODING, 3);
            break;
        default:
            throw new SqlJetException(SqlJetErrorCode.CORRUPT);
        }
    }

    private void writeIncrementalVacuum(boolean incrementalVacuum) throws SqlJetException {
        btree.updateMeta(INCREMENTAL_VACUUM, incrementalVacuum ? 1 : 0);
    }

    private void writeAutoVacuum(boolean autovacuum) throws SqlJetException {
        btree.updateMeta(AUTOVACUUM, autovacuum ? 1 : 0);
    }

    private void writePageCacheSize(int pageCacheSize) throws SqlJetException {
        checkPageCacheSize();
        btree.updateMeta(PAGE_CACHE_SIZE, pageCacheSize);
    }

    private void checkPageCacheSize() throws SqlJetException {
        if (pageCacheSize < SqlJetPageCache.PAGE_CACHE_SIZE_MINIMUM)
            pageCacheSize = SqlJetPageCache.PAGE_CACHE_SIZE_DEFAULT;
    }

    private void writeFileFormat(int fileFormat) throws SqlJetException {
        checkFileFormat(fileFormat);
        btree.updateMeta(FILE_FORMAT, fileFormat);
    }

    public void setUserVersion(int userCookie) throws SqlJetException {
        dbHandle.getMutex().enter();
        try {
            if (!btree.isInTrans())
                throw new SqlJetException("It can be performed only in active transaction");
            writeUserCookie(this.userCookie = userCookie);
        } finally {
            dbHandle.getMutex().leave();
        }
    }

    private void writeUserCookie(int userCookie) throws SqlJetException {
        btree.updateMeta(USER_COOKIE, userCookie);
    }

    private void checkSchema() throws SqlJetException {
        if (readSchemaCookie() != 1) {
            throw new SqlJetException(SqlJetErrorCode.MISUSE);
        }
    }

    public void setFileFormat(int fileFormat) throws SqlJetException {
        dbHandle.getMutex().enter();
        try {
            checkSchema();
            if (btree.isInTrans())
                throw new SqlJetException("It can't be performed in active transaction");
            btree.beginTrans(SqlJetTransactionMode.EXCLUSIVE);
            try {
                writeFileFormat(this.fileFormat = fileFormat);
                btree.commit();
            } catch (SqlJetException e) {
                btree.rollback();
                throw e;
            }
        } finally {
            dbHandle.getMutex().leave();
        }
    }

    public void setCacheSize(int pageCacheSize) throws SqlJetException {
        dbHandle.getMutex().enter();
        try {
            if (!btree.isInTrans())
                throw new SqlJetException("It can be performed only in active transaction");
            writePageCacheSize(this.pageCacheSize = pageCacheSize);
        } finally {
            dbHandle.getMutex().leave();
        }
    }

    public void setAutovacuum(boolean autovacuum) throws SqlJetException {
        dbHandle.getMutex().enter();
        try {
            checkSchema();
            if (btree.isInTrans())
                throw new SqlJetException("It can't be performed in active transaction");
            btree.beginTrans(SqlJetTransactionMode.EXCLUSIVE);
            try {
                writeAutoVacuum(this.autovacuum = autovacuum);
                btree.commit();
            } catch (SqlJetException e) {
                btree.rollback();
                throw e;
            }
        } finally {
            dbHandle.getMutex().leave();
        }
    }

    public void setEncoding(SqlJetEncoding encoding) throws SqlJetException {
        dbHandle.getMutex().enter();
        try {
            checkSchema();
            if (btree.isInTrans())
                throw new SqlJetException("It can't be performed in active transaction");
            btree.beginTrans(SqlJetTransactionMode.EXCLUSIVE);
            try {
                writeEncoding(this.encoding = encoding);
                btree.commit();
            } catch (SqlJetException e) {
                btree.rollback();
                throw e;
            }
        } finally {
            dbHandle.getMutex().leave();
        }
    }

    public void setIncrementalVacuum(boolean incrementalVacuum) throws SqlJetException {
        dbHandle.getMutex().enter();
        try {
            checkSchema();
            if (btree.isInTrans())
                throw new SqlJetException("It can't be performed in active transaction");
            btree.beginTrans(SqlJetTransactionMode.EXCLUSIVE);
            try {
                writeIncrementalVacuum(this.incrementalVacuum = incrementalVacuum);
                btree.commit();
            } catch (SqlJetException e) {
                btree.rollback();
                throw e;
            }
        } finally {
            dbHandle.getMutex().leave();
        }
    }
}
