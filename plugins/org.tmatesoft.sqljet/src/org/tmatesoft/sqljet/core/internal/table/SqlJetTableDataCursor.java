/**
 * SqlJetTableDataCursor.java
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetValueType;
import org.tmatesoft.sqljet.core.internal.ISqlJetMemoryPointer;
import org.tmatesoft.sqljet.core.internal.SqlJetUtility;
import org.tmatesoft.sqljet.core.schema.SqlJetConflictAction;
import org.tmatesoft.sqljet.core.table.ISqlJetTransaction;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

/**
 * Implementation of cursor which allow access to all table's rows.
 * 
 * @author TMate Software Ltd.
 * @author Sergey Scherbina (sergey.scherbina@gmail.com)
 * 
 */
public class SqlJetTableDataCursor extends SqlJetRowNumCursor {

    public SqlJetTableDataCursor(ISqlJetBtreeDataTable table, SqlJetDb db) throws SqlJetException {
        super(table, db);
        super.first();
    }

    protected ISqlJetBtreeDataTable getBtreeDataTable() {
        return (ISqlJetBtreeDataTable) btreeTable;
    }

    public long getRowId() throws SqlJetException {
        return (Long) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                final ISqlJetBtreeDataTable table = getBtreeDataTable();
                if (table.eof()) {
                    throw new SqlJetException(SqlJetErrorCode.MISUSE,
                            "Table is empty or the current record doesn't point to a data row");
                }
                return table.getRowId();
            }
        });
    }

    public boolean goTo(final long rowId) throws SqlJetException {
        return (Boolean) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                final ISqlJetBtreeDataTable table = getBtreeDataTable();
                return table.goToRow(rowId);
            }
        });
    }

    private int getFieldSafe(String fieldName) throws SqlJetException {
        final ISqlJetBtreeDataTable table = getBtreeDataTable();
        if (table.eof()) {
            throw new SqlJetException(SqlJetErrorCode.MISUSE,
                    "Table is empty or the current record doesn't point to a data row");
        }
        if (fieldName == null) {
            throw new SqlJetException(SqlJetErrorCode.MISUSE, "Field name is null");
        }
        final int field = table.getDefinition().getColumnNumber(fieldName);
        if (field < 0) {
            throw new SqlJetException(SqlJetErrorCode.MISUSE, "Field not found: " + fieldName);
        }
        return field;
    }

    public SqlJetValueType getFieldType(final String fieldName) throws SqlJetException {
        return (SqlJetValueType) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                return getBtreeDataTable().getFieldType(getFieldSafe(fieldName));
            }
        });
    }

    public boolean isNull(final String fieldName) throws SqlJetException {
        return (Boolean) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                return getBtreeDataTable().isNull(getFieldSafe(fieldName));
            }
        });
    }

    public String getString(final String fieldName) throws SqlJetException {
        return (String) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                return getBtreeDataTable().getString(getFieldSafe(fieldName));
            }
        });
    }

    public long getInteger(final String fieldName) throws SqlJetException {
        return (Long) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                if (SqlJetBtreeDataTable.isFieldNameRowId(fieldName)) {
                    return getBtreeDataTable().getRowId();
                } else {
                    return getBtreeDataTable().getInteger(getFieldSafe(fieldName));
                }
            }
        });
    }

    public double getFloat(final String fieldName) throws SqlJetException {
        return (Double) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                return getBtreeDataTable().getFloat(getFieldSafe(fieldName));
            }
        });
    }

    public byte[] getBlobAsArray(final String fieldName) throws SqlJetException {
        return (byte[]) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                ISqlJetMemoryPointer buffer = getBtreeDataTable().getBlob(getFieldSafe(fieldName));
                return buffer != null ? SqlJetUtility.readByteBuffer(buffer) : null;
            }
        });
    }

    public InputStream getBlobAsStream(final String fieldName) throws SqlJetException {
        return (InputStream) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                ISqlJetMemoryPointer buffer = getBtreeDataTable().getBlob(getFieldSafe(fieldName));
                return buffer != null ? new ByteArrayInputStream(SqlJetUtility.readByteBuffer(buffer)) : null;
            }
        });
    }

    public Object getValue(final String fieldName) throws SqlJetException {
        return db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                if (SqlJetBtreeDataTable.isFieldNameRowId(fieldName)) {
                    return getBtreeDataTable().getRowId();
                } else {
                    return getBtreeDataTable().getValue(getFieldSafe(fieldName));
                }
            }
        });
    }

    public boolean getBoolean(final String fieldName) throws SqlJetException {
        return (Boolean) db.runReadTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                return getBoolean(getFieldSafe(fieldName));
            }
        });
    }

    public void update(final Object... values) throws SqlJetException {
        updateOr(null, values);
    }

    public void updateOr(final SqlJetConflictAction onConflict, final Object... values) throws SqlJetException {
        db.runWriteTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                final ISqlJetBtreeDataTable table = getBtreeDataTable();
                if (table.eof()) {
                    throw new SqlJetException(SqlJetErrorCode.MISUSE,
                            "Table is empty or current record doesn't't point to data row");
                }
                table.updateCurrent(onConflict, values);
                return null;
            }
        });
    }

    public long updateWithRowId(final long rowId, final Object... values) throws SqlJetException {
        return updateWithRowIdOr(null, rowId, values);
    }

    public long updateWithRowIdOr(final SqlJetConflictAction onConflict, final long rowId, final Object... values)
            throws SqlJetException {
        return (Long) db.runWriteTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                final ISqlJetBtreeDataTable table = getBtreeDataTable();
                if (table.eof()) {
                    throw new SqlJetException(SqlJetErrorCode.MISUSE,
                            "Table is empty or current record doesn't't point to data row");
                }
                return table.updateCurrentWithRowId(onConflict, rowId, values);
            }
        });
    }

    public void updateByFieldNames(final Map<String, Object> values) throws SqlJetException {
        updateByFieldNamesOr(null, values);
    }

    public void updateByFieldNamesOr(final SqlJetConflictAction onConflict, final Map<String, Object> values)
            throws SqlJetException {
        db.runWriteTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                final ISqlJetBtreeDataTable table = getBtreeDataTable();
                if (table.eof()) {
                    throw new SqlJetException(SqlJetErrorCode.MISUSE,
                            "Table is empty or current record doesn't point to data row");
                }
                table.update(onConflict, values);
                return null;
            }
        });
    }

    public void delete() throws SqlJetException {
        db.runWriteTransaction(new ISqlJetTransaction() {
            public Object run(SqlJetDb db) throws SqlJetException {
                final ISqlJetBtreeDataTable table = getBtreeDataTable();
                if (table.eof()) {
                    throw new SqlJetException(SqlJetErrorCode.MISUSE,
                            "Table is empty or current record doesn't point to data row");
                }
                table.delete();
                return null;
            }
        });
        super.delete();
    }

}