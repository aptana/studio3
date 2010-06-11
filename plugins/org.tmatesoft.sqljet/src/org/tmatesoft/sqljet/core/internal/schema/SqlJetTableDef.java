/**
 * SqlJetTableDef.java
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
 */
package org.tmatesoft.sqljet.core.internal.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.antlr.runtime.tree.CommonTree;
import org.tmatesoft.sqljet.core.SqlJetErrorCode;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnConstraint;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnDef;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnPrimaryKey;
import org.tmatesoft.sqljet.core.schema.ISqlJetColumnUnique;
import org.tmatesoft.sqljet.core.schema.ISqlJetTableConstraint;
import org.tmatesoft.sqljet.core.schema.ISqlJetTableDef;
import org.tmatesoft.sqljet.core.schema.ISqlJetTablePrimaryKey;
import org.tmatesoft.sqljet.core.schema.ISqlJetTableUnique;

/**
 * @author TMate Software Ltd.
 * @author Dmitry Stadnik (dtrace@seznam.cz)
 */
public class SqlJetTableDef implements ISqlJetTableDef {

    private static String AUTOINDEX = "sqlite_autoindex_%s_%d";

    private final String name;
    private final String databaseName;
    private final boolean temporary;
    private final boolean ifNotExists;
    private final List<ISqlJetColumnDef> columns;
    private final List<ISqlJetTableConstraint> constraints;

    private int page;
    private long rowId;

    private boolean rowIdPrimaryKey;
    private boolean autoincremented;
    private String primaryKeyIndexName;
    private String rowIdPrimaryKeyColumnName;
    private int rowIdPrimaryKeyColumnIndex = -1;
    private final List<String> primaryKeyColumns = new ArrayList<String>();

    // index name -> column index constraint
    private final Map<String, SqlJetColumnIndexConstraint> columnConstraintsIndexCache = new TreeMap<String, SqlJetColumnIndexConstraint>(
            String.CASE_INSENSITIVE_ORDER);

    // index name -> table index constraint
    private final Map<String, SqlJetTableIndexConstraint> tableConstrainsIndexCache = new TreeMap<String, SqlJetTableIndexConstraint>(
            String.CASE_INSENSITIVE_ORDER);

    SqlJetTableDef(String name, String databaseName, boolean temporary, boolean ifNotExists,
            List<ISqlJetColumnDef> columns, List<ISqlJetTableConstraint> constraints, int page, long rowid) {
        this.name = name;
        this.databaseName = databaseName;
        this.temporary = temporary;
        this.ifNotExists = ifNotExists;
        this.columns = Collections.unmodifiableList(columns);
        this.constraints = Collections.unmodifiableList(constraints);
        this.page = page;
        this.rowId = rowid;
    }

    public SqlJetTableDef(CommonTree ast, int page) throws SqlJetException {
        CommonTree optionsNode = (CommonTree) ast.getChild(0);
        temporary = hasOption(optionsNode, "temporary");
        ifNotExists = hasOption(optionsNode, "exists");

        CommonTree nameNode = (CommonTree) ast.getChild(1);
        name = nameNode.getText();
        databaseName = nameNode.getChildCount() > 0 ? nameNode.getChild(0).getText() : null;

        List<ISqlJetColumnDef> columns = new ArrayList<ISqlJetColumnDef>();
        List<ISqlJetTableConstraint> constraints = new ArrayList<ISqlJetTableConstraint>();
        if (ast.getChildCount() > 2) {
            CommonTree defNode = (CommonTree) ast.getChild(2);
            if ("columns".equalsIgnoreCase(defNode.getText())) {
                for (int i = 0; i < defNode.getChildCount(); i++) {
                    columns.add(new SqlJetColumnDef((CommonTree) defNode.getChild(i)));
                }
                if (ast.getChildCount() > 3) {
                    CommonTree constraintsNode = (CommonTree) ast.getChild(3);
                    assert "constraints".equalsIgnoreCase(constraintsNode.getText());
                    for (int i = 0; i < constraintsNode.getChildCount(); i++) {
                        CommonTree constraintRootNode = (CommonTree) constraintsNode.getChild(i);
                        assert "table_constraint".equalsIgnoreCase(constraintRootNode.getText());
                        CommonTree constraintNode = (CommonTree) constraintRootNode.getChild(0);
                        String constraintType = constraintNode.getText();
                        String constraintName = constraintRootNode.getChildCount() > 1 ? constraintRootNode.getChild(1)
                                .getText() : null;
                        if ("primary".equalsIgnoreCase(constraintType)) {
                            constraints.add(new SqlJetTablePrimaryKey(constraintName, constraintNode));
                        } else if ("unique".equalsIgnoreCase(constraintType)) {
                            constraints.add(new SqlJetTableUnique(constraintName, constraintNode));
                        } else if ("check".equalsIgnoreCase(constraintType)) {
                            constraints.add(new SqlJetTableCheck(constraintName, constraintNode));
                        } else if ("foreign".equalsIgnoreCase(constraintType)) {
                            constraints.add(new SqlJetTableForeignKey(constraintName, constraintNode));
                        } else {
                            assert false;
                        }
                    }
                }
            } else {
                // TODO: handle select
            }
        }
        this.columns = Collections.unmodifiableList(columns);
        this.constraints = Collections.unmodifiableList(constraints);

        this.page = page;
        resolveConstraints();
    }

    private void resolveConstraints() throws SqlJetException {
        int columnIndex = 0, autoindexNumber = 0;
        for (ISqlJetColumnDef column : columns) {
            for (ISqlJetColumnConstraint constraint : column.getConstraints()) {
                if (constraint instanceof ISqlJetColumnPrimaryKey) {
                    SqlJetColumnPrimaryKey pk = (SqlJetColumnPrimaryKey) constraint;
                    primaryKeyColumns.add(column.getName());
                    if (column.hasExactlyIntegerType()) {
                        rowIdPrimaryKeyColumnName = column.getName();
                        rowIdPrimaryKeyColumnIndex = columnIndex;
                        rowIdPrimaryKey = true;
                        autoincremented = pk.isAutoincremented();
                    } else {
                        pk.setIndexName(primaryKeyIndexName = generateAutoIndexName(getName(), ++autoindexNumber));
                        columnConstraintsIndexCache.put(pk.getIndexName(), pk);
                    }
                } else if (constraint instanceof ISqlJetColumnUnique) {
                    SqlJetColumnUnique uc = (SqlJetColumnUnique) constraint;
                    uc.setIndexName(generateAutoIndexName(getName(), ++autoindexNumber));
                    columnConstraintsIndexCache.put(uc.getIndexName(), uc);
                }
            }
            columnIndex++;
        }
        for (ISqlJetTableConstraint constraint : constraints) {
            if (constraint instanceof ISqlJetTablePrimaryKey) {
                boolean b = false;
                SqlJetTablePrimaryKey pk = (SqlJetTablePrimaryKey) constraint;
                assert primaryKeyColumns.isEmpty();
                primaryKeyColumns.addAll(pk.getColumns());
                if (pk.getColumns().size() == 1) {
                    final String n = pk.getColumns().get(0);
                    final ISqlJetColumnDef c = getColumn(n);
                    if (null == c) {
                        throw new SqlJetException(SqlJetErrorCode.ERROR, "Wrong column '" + n + "' in PRIMARY KEY");
                    } else if (c.hasExactlyIntegerType()) {
                        rowIdPrimaryKeyColumnName = n;
                        rowIdPrimaryKeyColumnIndex = getColumnNumber(n);
                        rowIdPrimaryKey = true;
                        b = true;
                    }
                }
                if (!b) {
                    pk.setIndexName(primaryKeyIndexName = generateAutoIndexName(getName(), ++autoindexNumber));
                    tableConstrainsIndexCache.put(pk.getIndexName(), pk);
                }
            } else if (constraint instanceof ISqlJetTableUnique) {
                SqlJetTableUnique uc = (SqlJetTableUnique) constraint;
                uc.setIndexName(generateAutoIndexName(getName(), ++autoindexNumber));
                tableConstrainsIndexCache.put(uc.getIndexName(), uc);
            }
        }
    }

    private static String generateAutoIndexName(String tableName, int i) {
        return String.format(AUTOINDEX, tableName, i);
    }

    private boolean hasOption(CommonTree optionsNode, String name) {
        for (int i = 0; i < optionsNode.getChildCount(); i++) {
            CommonTree optionNode = (CommonTree) optionsNode.getChild(i);
            if (name.equalsIgnoreCase(optionNode.getText())) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public boolean isKeepExisting() {
        return ifNotExists;
    }

    public List<ISqlJetColumnDef> getColumns() {
        return columns;
    }

    public ISqlJetColumnDef getColumn(String name) {
        for (ISqlJetColumnDef column : getColumns()) {
            if (column.getName().equalsIgnoreCase(name)) {
                return column;
            }
        }
        return null;
    }

    public int getColumnNumber(String name) {
        int i = 0;
        for (ISqlJetColumnDef column : getColumns()) {
            if (column.getName().equalsIgnoreCase(name)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public List<ISqlJetTableConstraint> getConstraints() {
        return constraints;
    }

    public boolean isRowIdPrimaryKey() {
        return rowIdPrimaryKey;
    }

    public boolean isAutoincremented() {
        return autoincremented;
    }

    // Internal API

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    /**
     * Returns name of the primary key index.
     */
    public String getPrimaryKeyIndexName() {
        return primaryKeyIndexName;
    }

    public String getRowIdPrimaryKeyColumnName() {
        return rowIdPrimaryKeyColumnName;
    }

    public int getRowIdPrimaryKeyColumnIndex() {
        return rowIdPrimaryKeyColumnIndex;
    }

    public List<String> getPrimaryKeyColumnNames() {
        return primaryKeyColumns;
    }

    public SqlJetColumnIndexConstraint getColumnIndexConstraint(String indexName) {
        return columnConstraintsIndexCache.get(indexName);
    }

    public SqlJetTableIndexConstraint getTableIndexConstraint(String indexName) {
        return tableConstrainsIndexCache.get(indexName);
    }

    // Serialization

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getPage());
        buffer.append("/");
        buffer.append(getRowId());
        buffer.append(": ");
        buffer.append(toSQL(false));
        return buffer.toString();
    }

    public String toSQL() {
        return toSQL(true);
    }

    public String toSQL(boolean schemaStrict) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CREATE ");
        if (isTemporary()) {
            buffer.append("TEMPORARY ");
        }
        buffer.append("TABLE ");
        if (!schemaStrict) {
            if (isKeepExisting()) {
                buffer.append("IF NOT EXISTS ");
            }
            if (getDatabaseName() != null) {
                buffer.append(getDatabaseName());
                buffer.append('.');
            }
        }
        buffer.append(getName());
        buffer.append(" (");
        List<ISqlJetColumnDef> columns = getColumns();
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(columns.get(i).toString());
        }
        List<ISqlJetTableConstraint> constraints = getConstraints();
        for (int i = 0; i < constraints.size(); i++) {
            buffer.append(", ");
            buffer.append(constraints.get(i).toString());
        }
        buffer.append(')');
        return buffer.toString();
    }
}
