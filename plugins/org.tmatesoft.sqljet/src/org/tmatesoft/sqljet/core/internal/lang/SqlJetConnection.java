/**
 * SqlJetConnection.java
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
package org.tmatesoft.sqljet.core.internal.lang;

import java.io.File;

import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.schema.ISqlJetSchema;
import org.tmatesoft.sqljet.core.table.ISqlJetRunnableWithLock;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetConnection {

    private SqlJetDb db;

    protected SqlJetConnection(String fileName) throws SqlJetException {
        db = SqlJetDb.open(new File(fileName), false);
    }

    public static SqlJetConnection open(String fileName) throws SqlJetException {
        return new SqlJetConnection(fileName);
    }

    public SqlJetPreparedStatement prepare(String sql) throws SqlJetException {
        if (sql == null || sql.trim().length() == 0) {
            throw new SqlJetException(SqlJetErrorCode.ERROR, "SQL statement is empty");
        }
        return new SqlJetPreparedStatement(db, sql);
    }

    public void exec(final String sql, final SqlJetExecCallback callback) throws SqlJetException {
        db.runWithLock(new ISqlJetRunnableWithLock() {

            public Object runWithLock(SqlJetDb db) throws SqlJetException {
                SqlJetPreparedStatement stmt = prepare(sql);
                try {
                    while (stmt.step()) {
                        if (callback != null) {
                            callback.processRow(stmt);
                        }
                    }
                } finally {
                    stmt.close();
                }
                return null;
            }
        });
    }

    public void close() throws SqlJetException {
        db.close();
    }

    public ISqlJetSchema getSchema(String databaseName) throws SqlJetException {
        return db.getSchema();
    }
}
